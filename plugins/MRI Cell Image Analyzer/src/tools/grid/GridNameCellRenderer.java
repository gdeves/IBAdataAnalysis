/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2008 Volker B�cker
 * MRI Cell Image Analyzer has been created at Montpellier RIO Imaging
 * www.mri.cnrs.fr
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 */
package tools.grid;
import ij.gui.Grid;
import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * The Renderer allows to display the loaded grid name in green until
 * the grid is modified.  
 * 
 * @author	Volker Baecker
 **/
public class GridNameCellRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = -2154073261902572491L;
	protected Grid model;
	
	public GridNameCellRenderer(Grid model) {
		super();
		this.model = model;
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		 JLabel label =
		      (JLabel)super.getListCellRendererComponent(list,
		                                                 value,
		                                                 index,
		                                                 isSelected,
		                                                 cellHasFocus);
		 String loadedGridName = model.getLoadedGridName();
		 if (loadedGridName!=null && loadedGridName.equals(value)) {
			 label.setForeground(Color.green);
		 }
		 return label;
    }

	public void setModel(Grid newModel) {
		model = newModel;		
	}
}
