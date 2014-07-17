/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2007 Volker B�cker
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
package operations.file;

import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import operations.Operation;

/**
 * Opens a window and displays the input results table.
 * 
 * @author Volker Baecker
 */
public class ShowResultsTableOperation extends Operation {
	private static final long serialVersionUID = -7634763256865700833L;
	protected ResultsTable inputResultsTable;
	public static int counter = 1;

	protected void initialize() throws ClassNotFoundException {	
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.measure.ResultsTable");
		parameterNames = new String[1];
		parameterNames[0] = "InputResultsTable";
		resultTypes = new Class[0];
		resultNames = new String[0];
	}
	
	public ResultsTable getInputResultsTable() {
		return this.inputResultsTable;
	}
	
	public void setInputResultsTable(ResultsTable inputResultsTable) {
		this.inputResultsTable = inputResultsTable;
	}
	
	public void doIt() {
		String title = this.name + " " + counter;
		if (application==null && inputResultsTable==null) {
			inputResultsTable = Analyzer.getResultsTable();
			title = "Results";
		}
		inputResultsTable.show(title);
		counter = (counter + 1) % Integer.MAX_VALUE;
	}

	protected void cleanUpInput() {
		this.setInputResultsTable(null);
	}

	protected void showResult() {
		// do nothing
	}
	
	protected boolean isBatchOperation() {
		return false;
	}
}
