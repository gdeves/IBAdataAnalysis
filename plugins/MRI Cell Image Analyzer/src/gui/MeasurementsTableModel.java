/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2007 Volker Bäcker
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
package gui;

import ij.measure.ResultsTable;

import javax.swing.table.AbstractTableModel;

/**
 * A TableModel to display a ResultsTable in a JTable.
 * 
 * @author	Volker Baecker
 **/
public class MeasurementsTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -6826857055852063163L;
	private ResultsTable measurements;
	protected String[] headings; 

	public MeasurementsTableModel(ResultsTable measurements) {
		this.measurements = measurements;
		headings = measurements.getColumnHeadings().split("\t");
	}
	public int getColumnCount() {
		int result = 0;
		if (measurements.getColumnHeadings()!="") result = headings.length;
		return result;
	}

	public int getRowCount() {
		int result = measurements.getCounter();
		return result;
	}

	public Object getValueAt(int row, int column) {
		if (column==0) return new Integer(row+1);
		double result = measurements.getValue(headings[column],row);
		return new Double(result);
	}

	public String getColumnName(int column) {
		if (column==0) return "Nr.";
		return headings[column];
	}
}
