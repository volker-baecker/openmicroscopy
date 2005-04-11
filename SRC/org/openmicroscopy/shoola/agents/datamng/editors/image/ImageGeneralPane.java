/*
 * org.openmicroscopy.shoola.agents.datamng.editors.image.ImageGeneralPane
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

package org.openmicroscopy.shoola.agents.datamng.editors.image;

//Java imports
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.datamng.DataManagerUIF;
import org.openmicroscopy.shoola.env.data.model.ImageData;
import org.openmicroscopy.shoola.util.ui.MultilineLabel;
import org.openmicroscopy.shoola.util.ui.table.TableComponent;
import org.openmicroscopy.shoola.util.ui.table.TableComponentCellEditor;
import org.openmicroscopy.shoola.util.ui.table.TableComponentCellRenderer;

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
class ImageGeneralPane
	extends JPanel
{

    private static final String     MSG = "No thumbnail available";
    
	private ImageEditorManager     manager;
	
	MultilineLabel                 descriptionArea, nameField;
	
	ImageGeneralPane(ImageEditorManager manager, Image thumbnail)
	{
		this.manager = manager;
		buildGUI(thumbnail);
	}
	
	/** Build and lay out the GUI. */
	private void buildGUI(Image thumbnail)
	{
		setLayout(new GridLayout(2, 1));
		add(buildSummaryPanel());
        add(buildThumbnailPanel(thumbnail));
		Border b = BorderFactory.createEmptyBorder(0, 0, 10, 10);
		setBorder(b);
	}
	
	/** Build a panel with table. */
	private JPanel buildSummaryPanel() 
	{	
		JPanel  p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.add(buildTable());
		//make panel transparent
		p.setOpaque(false);
		return p;
	}
	
    /** Display the image Thumbnail in a JPanel. */
    private JPanel buildThumbnailPanel(Image thumbnail)
    {
        JPanel  p = new JPanel();
        p.setLayout(new BorderLayout()); 
        p.setBorder(new BevelBorder(BevelBorder.LOWERED)); 
        JLabel l;
        if (thumbnail != null) l = new JLabel(new ImageIcon(thumbnail));    
        else l = new JLabel(MSG);
        p.add(l, BorderLayout.CENTER);
        return p;
    }
    
	/** 
	 * A <code>3x2</code> table model to view image summary.
	 * The first column contains the property names (id, name, description)
	 * and the second column holds the corresponding values. 
	 * <code>name</code> and <code>description</code> values
	 * are marked as editable. 
	 */
	private TableComponent buildTable()
	{
		TableComponent table = new TableComponent(2, 2);
		setTableLayout(table);
		
		// Labels
		table.setValueAt(new JLabel(" Name"), 0, 0);
		table.setValueAt(new JLabel(" Description"), 1, 0);

		ImageData pd = manager.getImageData();
		
		//textfields
		nameField = new MultilineLabel(pd.getName());
		nameField.setForeground(DataManagerUIF.STEELBLUE);
		nameField.setEditable(true);
		
		JScrollPane scrollPaneName  = new JScrollPane(nameField);
		scrollPaneName.setPreferredSize(DataManagerUIF.DIM_SCROLL_NAME);
		table.setValueAt(scrollPaneName, 0, 1); 

		descriptionArea = new MultilineLabel(pd.getDescription());
		descriptionArea.setForeground(DataManagerUIF.STEELBLUE);
		descriptionArea.setEditable(true);
		JScrollPane scrollPane = new JScrollPane(descriptionArea);
		scrollPane.setPreferredSize(DataManagerUIF.DIM_SCROLL_TABLE);
		table.setValueAt(scrollPane, 1, 1);					
		return table;
	}

	/** Set the table layout. */
	private void setTableLayout(TableComponent table)
	{
		table.setTableHeader(null);
		table.setRowHeight(1, DataManagerUIF.ROW_TABLE_HEIGHT);
		table.setRowHeight(0, DataManagerUIF.ROW_NAME_FIELD);
		table.setDefaultRenderer(JComponent.class, 
								new TableComponentCellRenderer());
		table.setDefaultEditor(JComponent.class, 
								new TableComponentCellEditor());
	}
	
}
