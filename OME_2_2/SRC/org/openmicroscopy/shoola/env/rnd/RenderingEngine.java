/*
 * org.openmicroscopy.shoola.env.rnd.RenderingEngine
 *
 *------------------------------------------------------------------------------
 *
 *  Copyright (C) 2004 Open Microscopy Environment
 *      Massachusetts Institute of Technology,
 *      National Institutes of Health,
 *      University of Dundee
 *
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *------------------------------------------------------------------------------
 */

package org.openmicroscopy.shoola.env.rnd;

//Java imports
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.ds.st.Pixels;
import org.openmicroscopy.shoola.env.Container;
import org.openmicroscopy.shoola.env.LookupNames;
import org.openmicroscopy.shoola.env.config.Registry;
import org.openmicroscopy.shoola.env.event.AgentEvent;
import org.openmicroscopy.shoola.env.event.AgentEventListener;
import org.openmicroscopy.shoola.env.event.EventBus;
import org.openmicroscopy.shoola.env.log.LogMessage;
import org.openmicroscopy.shoola.env.rnd.data.DataSink;
import org.openmicroscopy.shoola.env.rnd.data.DataSourceException;
import org.openmicroscopy.shoola.env.rnd.defs.PlaneDef;
import org.openmicroscopy.shoola.env.rnd.defs.RenderingDef;
import org.openmicroscopy.shoola.env.rnd.events.Image3DRendered;
import org.openmicroscopy.shoola.env.rnd.events.ImageLoaded;
import org.openmicroscopy.shoola.env.rnd.events.ImageRendered;
import org.openmicroscopy.shoola.env.rnd.events.LoadImage;
import org.openmicroscopy.shoola.env.rnd.events.RenderImage;
import org.openmicroscopy.shoola.env.rnd.events.RenderImage3D;
import org.openmicroscopy.shoola.env.rnd.events.RenderingPropChange;
import org.openmicroscopy.shoola.env.rnd.events.ResetPlaneDef;
import org.openmicroscopy.shoola.env.rnd.metadata.MetadataSource;
import org.openmicroscopy.shoola.env.rnd.metadata.MetadataSourceException;
import org.openmicroscopy.shoola.env.rnd.metadata.PixelsDimensions;
import org.openmicroscopy.shoola.env.rnd.quantum.QuantizationException;
import org.openmicroscopy.shoola.util.concur.AsyncByteBuffer;
import org.openmicroscopy.shoola.util.concur.tasks.AsyncProcessor;
import org.openmicroscopy.shoola.util.concur.tasks.CmdProcessor;

/** 
 * 
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author  <br>Andrea Falconi &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:a.falconi@dundee.ac.uk">
 * 					a.falconi@dundee.ac.uk</a>
 * @version 2.2
 * <small>
 * (<b>Internal version:</b> $Revision$ $Date$)
 * </small>
 * @since OME2.2
 */
public class RenderingEngine
	implements AgentEventListener
{

	private static RenderingEngine		singleton;
	private static Registry				registry;

	
	//NB: this can't be called outside of container b/c agents have no refs
	//to the singleton container. So we can be sure this method is going to
	//create services just once.
	public static RenderingEngine getInstance(Container c)
	{
		if (c == null)
			throw new NullPointerException();  //An agent called this method?
		registry = c.getRegistry();
		if (singleton == null)	singleton = new RenderingEngine();
		return singleton;
	}

	private Map				renderers;
    
    private RenderingManager curRenderingManager;
	
	//Shared by all instances of DataSink.
	private AsyncByteBuffer	stackBuffer;
    
    //Used by DataSink and AsyncByteBuffer for async operation.
    private CmdProcessor        cmdProcessor;
	
	private RenderingEngine()
	{
		renderers = new HashMap();
		cmdProcessor = new AsyncProcessor();
        Integer size = (Integer) registry.lookup(LookupNames.RE_STACK_BUF_SZ), 
			blockSize = (Integer) registry.lookup(
												LookupNames.RE_STACK_BLOCK_SZ);
		int sz = size.intValue(), block = blockSize.intValue();
		block = (0 < block ? block : 4096);
		if (0 < sz) 
			stackBuffer = new AsyncByteBuffer(sz*1024*1024, block, 
                                                    cmdProcessor);
		else 
			stackBuffer = new AsyncByteBuffer(1, 1);  
            //Won't be used b/c we first check if we can cache a stack.
	}
	
	public static Registry getRegistry() { return registry; }
	
	private void hanldeException(String message, Exception cause)
	{
		LogMessage msg = new LogMessage();
		msg.print("Rendering Engine Exception: ");
		msg.println(message);
		msg.print(cause);
		registry.getLogger().error(this, msg);
		registry.getUserNotifier().notifyError("Rendering Engine Exception", 
												message, msg.toString());
	}
	
	private void handleLoadImage(LoadImage request)
	{
		Renderer rnd = new Renderer(request.getImageID(), request.getPixelsID(),
																		this);
		try {
			rnd.initialize();
			renderers.put(new Integer(request.getPixelsID()), rnd);
			//TODO: how do we figure when to remove rnd?
            curRenderingManager = new RenderingManager(rnd, cmdProcessor);
          
			RenderingDef original = rnd.getRenderingDef(), copy;
			copy = original.copy();
			EventBus eventBus = registry.getEventBus();
			RenderingControlImpl facade = new RenderingControlImpl(rnd);
			RenderingControlProxy proxy =
							new RenderingControlProxy(facade, copy, eventBus);
			ImageLoaded response = new ImageLoaded(request, proxy);
			eventBus.post(response);  //TODO: this has to be run w/in Swing thread.
		} catch (MetadataSourceException mse) {
			hanldeException("Can't load image metadata. Image id: "+
													request.getImageID(), mse);
        }
	}
	
	private void handleRenderImage(RenderImage request)
	{
		Renderer rnd = (Renderer) renderers.get(
											new Integer(request.getPixelsID()));
		//TODO: if null, log?
		if (rnd != null) {
			try {
				PlaneDef pd = request.getPlaneDef();
				BufferedImage img;
				img = curRenderingManager.renderXYPlane(pd);
                //if (pd == null)	img = rnd.render();
				//else	img = rnd.render(pd);
				ImageRendered response = new ImageRendered(request, img);
				EventBus eventBus = registry.getEventBus();
				eventBus.post(response);  //TODO: this has to be run w/in Swing thread.
			} catch (DataSourceException dse) {
				hanldeException("Can't load pixels data. Pixels id: "+
													request.getPixelsID(), dse);
			} catch (QuantizationException qee) {
				//TODO: need to post an event to update the GUI.
				hanldeException("Can't map the wavelength "
								+qee.getWavelength(), qee);
			}
		}
	}
	
	private void handleRenderImage3D(RenderImage3D request)
	{
		Renderer rnd = (Renderer) renderers.get(
											new Integer(request.getPixelsID()));
		//TODO: if null, log?
		if (rnd != null) {
			try {
				PlaneDef xyPD = request.getXYPlaneDef(), 
							xzPD = request.getXZPlaneDef(),
							zyPD = request.getZYPlaneDef();
				BufferedImage xyPlane = null, xzPlane, zyPlane;
				if (xyPD != null) xyPlane = rnd.render(xyPD);
				xzPlane = rnd.render(xzPD);
				zyPlane = rnd.render(zyPD);
				Image3DRendered response = new Image3DRendered(request, 
													xyPlane, xzPlane, zyPlane);
				EventBus eventBus = registry.getEventBus();
				eventBus.post(response);  //TODO: this has to be run w/in Swing thread.
			} catch (DataSourceException dse) {
				hanldeException("Can't load pixels data. Pixels id: "+
													request.getPixelsID(), dse);
			} catch (QuantizationException qee) {
				//TODO: need to post an event to update the GUI.
				hanldeException("Can't map the wavelength "
								+qee.getWavelength(), qee);
			}
		}
	}
	
	private void handleResetPlaneDef(ResetPlaneDef request)
	{
		Renderer rnd = (Renderer) renderers.get(
											new Integer(request.getPixelsID()));
		//TODO: if null, log?
		//if (rnd != null) rnd.resetPlaneDef(request.getPlaneDef());
		if (rnd != null) {
            rnd.resetPlaneDef(request.getPlaneDef());
            curRenderingManager.onRenderingPropChange();
        }
	}
	
	private void handleRenderingPropChange(RenderingPropChange event)
	{
		event.doUpdate();
        curRenderingManager.onRenderingPropChange();
	}
	
	MetadataSource getMetadataSource(int imageID, int pixelsID)
	{
		return new MetadataSource(imageID, pixelsID, registry);
	}
	
	DataSink getDataSink(Pixels pixelsID, int pixelType, PixelsDimensions dims) 
	{
		//TODO: TMP (and wrong if multiple pixels), do it properly when
		//PixelsService supports int pixelsID instead of Pixels.
		return new DataSink(pixelsID, pixelType, dims, 
							registry.getPixelsService(), 
                            stackBuffer, cmdProcessor);
	}
	
	public void activate()
	{
		EventBus eventBus = registry.getEventBus();
		eventBus.register(this, LoadImage.class);
		eventBus.register(this, RenderImage.class);
		eventBus.register(this, RenderingPropChange.class);
		eventBus.register(this, RenderImage3D.class);
		eventBus.register(this, ResetPlaneDef.class);
		//TODO: start event loop in its own thread.
	}
	
	public void terminate()
	{
		//TODO: implement.
	}

	public void eventFired(AgentEvent e) 
	{
		//TODO: put event on the queue and remove the following.
		if (e instanceof LoadImage)	handleLoadImage((LoadImage) e);
		else if	(e instanceof RenderImage)	handleRenderImage((RenderImage) e);
		else if (e instanceof RenderingPropChange)
			handleRenderingPropChange((RenderingPropChange) e);
		else if (e instanceof RenderImage3D)
			handleRenderImage3D((RenderImage3D) e);
		else if (e instanceof ResetPlaneDef)
			handleResetPlaneDef((ResetPlaneDef) e);
	}

}
