/*
 * org.openmicroscopy.shoola.agents.hiviewer.clipboard.ClipBoard
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

package org.openmicroscopy.shoola.agents.hiviewer.clipboard;

//Java imports
import java.util.Map;
import java.util.Set;
import javax.swing.JComponent;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.hiviewer.AnnotationEditor;
import org.openmicroscopy.shoola.agents.hiviewer.browser.ImageDisplay;
import org.openmicroscopy.shoola.agents.hiviewer.util.LoadingWin;
import org.openmicroscopy.shoola.util.ui.component.ObservableComponent;
import pojos.AnnotationData;

/** 
 * Defines the interface provided by clip board component.
 * The clip board provides a UI component hosting the <code>ImageDisplay</code>
 * manipulation e.g. <code>Search</code> panel, <code>Annotation</code> panel.
 * Use the {@link ClipBoardFactory} to create an object implementing this 
 * interface.
 * 
 * @author  Barry Anderson &nbsp;&nbsp;&nbsp;&nbsp;
 *              <a href="mailto:banderson@computing.dundee.ac.uk">
 *              banderson@comnputing.dundee.ac.uk
 *              </a>
 * @version 2.2
 * <small>
 * (<b>Internal version:</b> $Revision$ $Date$)
 * </small>
 * @since OME2.2
 */
public interface ClipBoard
    extends ObservableComponent
{
 
    /** 
     * Bound property name to bring on screen the selected {@link ImageDisplay}. 
     */
    public static final String LOCALIZE_IMAGE_DISPLAY = "localize";
    
    /** Identifies the index of the search panel. */
    public static final int     SEARCH_PANEL = 0;
    
    /** Identifies the index of the annotation panel. */
    public static final int     ANNOTATION_PANEL = 1;
    
    /** Identifies the <i>Loading annotations</i> state. */
    public static final int     LOADING_ANNOTATIONS = 200;
    
    /** Identifies the <i>Edit annotations</i> state. */
    public static final int     EDIT_ANNOTATIONS = 201;
    
    /** Identifies the <i>Ready</i> state. */
    public static final int     ANNOTATIONS_READY = 202;
    
    /** Identifies the <i>Discarded annotations</i> state. */
    public static final int     DISCARDED_ANNOTATIONS = 203;
    
    /** Indicates to retrieve the image annotations. */
    public static final int     IMAGE_ANNOTATIONS = 300;
    
    /** Indicates to retrieve the dataset annotations. */
    public static final int     DATASET_ANNOTATIONS = 301;
    
    /**
     * Returns the {@link LoadingWin}.
     * 
     * @return See above.
     */
    LoadingWin getLoadingWin();
    
    /** Any ongoing data loading is cancelled. */
    public void discard();
    
    /**
     * Returns the UI representation of this component. 
     * 
     * @return See above.
     */
    public JComponent getUI();
    
    /** 
     * Sets the results of the search.
     * 
     * @param foundNodes The set of found nodes.
     */
    public void setSearchResults(Set foundNodes);
    
    /**
     * Sets the annotations retrieved.
     * 
     * @param map The annotations.
     */
    public void setAnnotations(Map map);
    
    /**
     * Retrieves the annotations for the specified object.
     * 
     * @param objectID          The ID of the data object.
     * @param annotationIndex   The annotation index.
     *                          One of the following constants:
     *                          {@link #DATASET_ANNOTATIONS}, 
     *                          {@link #IMAGE_ANNOTATIONS}.
     */
    public void retrieveAnnotations(long objectID, int annotationIndex);

    /** 
     * Creates a new annotation.
     * 
     * @param text The text of the annotation.
     */
    public void createAnnotation(String text);
    
    /**
     * Edits an existing annotation.
     * 
     * @param data The annotation data object.
     */
    public void updateAnnotation(AnnotationData data);
    
    /**
     * Deletes the specified annotation.
     * 
     * @param data The annotation data object.
     */
    public void deleteAnnotation(AnnotationData data);
    
    /**
     * Transitions the viewer to the {@link #DISCARDED_ANNOTATIONS} state.
     * Any ongoing data loading is cancelled.
     */
    public void discardAnnotation();
    
    /**
     * Sets the result of the creation, update or deletion of an annotation.
     * 
     * @param b Flag to indicate if the operation was successful.
     * 			Right now it is always true b/c of OME-Java
     */
    public void manageAnnotationEditing(boolean b);

    /**
     * Sets the selected panel.
     * 
     * @param index The index of the panel
     * @param node  Pass <code>null</code> to modify the diplay.
     */
    public void setPaneIndex(int index, ImageDisplay node);

    /**
     * Queries the current state.
     * 
     * @return One of the state flags defined by this interface.
     */
    public int getState();
    
}

