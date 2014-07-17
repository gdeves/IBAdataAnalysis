/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2008 Volker Bäcker
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

import javax.swing.table.AbstractTableModel;

/**
 * The GridNamesTableModel manages the labels associated with the grid-positions.
 * 
 * @author	Volker Baecker
 **/
public class GridNamesTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -465791202667565628L;
	private String[][] names;

	public GridNamesTableModel(String[][] names) {
		this.names = names;
	}
	
	public String[][] getNames() {
		return names;
	}
	
	public int getColumnCount() {
		return names.length;
	}

	public int getRowCount() {
		return names[0].length;
	}

	public Object getValueAt(int row, int column) {
		return names[column][row];
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}
	
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		names[columnIndex][rowIndex] = (String)aValue;
	}
}
