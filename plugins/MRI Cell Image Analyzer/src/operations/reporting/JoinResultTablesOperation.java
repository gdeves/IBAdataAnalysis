/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2008 INSERM and CNRS
 * MRI Cell Image Analyzer has been created at Montpellier RIO Imaging, 
 * by Volker B�cker
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
package operations.reporting;

import ij.measure.ResultsTable;
import operations.Operation;

/**
 * Create a new results table with the columns of the first input table followed by the columns of the second
 * input table.
 * 
 * @author Volker Baecker
 */
public class JoinResultTablesOperation extends Operation {

	private static final long serialVersionUID = 2755661289973006787L;
	
	protected ResultsTable firstInputTable;
	protected ResultsTable secondInputTable;
	protected ResultsTable joinedTable;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[2];
		parameterTypes[0] = Class.forName("ij.measure.ResultsTable");
		parameterTypes[1] = Class.forName("ij.measure.ResultsTable");
		parameterNames = new String[2];
		parameterNames[0] = "FirstInputTable";
		parameterNames[1] = "SecondInputTable";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.measure.ResultsTable");
		resultNames = new String[1];
		resultNames[0] = "JoinedTable";
	}
	
	public void doIt() {
		ResultsTable firstTable = this.getFirstInputTable();
		ResultsTable secondTable = this.getSecondInputTable();
		joinedTable = new ResultsTable();
		String[] firstTableHeadings = firstTable.getColumnHeadings().split("\t");
		String[] secondTableHeadings = secondTable.getColumnHeadings().split("\t");
		String[] headings = (firstTable.getColumnHeadings()  + secondTable.getColumnHeadings()).split("\t");
		int counter = 0;
		for (int j=0; j<headings.length; j++) {
			if (j>0 && headings[j].trim().equals("")) continue;
			joinedTable.setHeading(counter, headings[j].trim());
			counter++;
		}
		for (int i=0; i<firstTable.getCounter(); i++) {
			joinedTable.incrementCounter();
			int j,k;
			for (j=1; j<firstTableHeadings.length; j++) {
				joinedTable.setValue(firstTableHeadings[j].trim(), i, firstTable.getValue(firstTableHeadings[j].trim(), i));
			}
			for (k=1; k<secondTableHeadings.length; k++) {
				joinedTable.setValue(secondTableHeadings[k].trim(), i, secondTable.getValue(secondTableHeadings[k].trim(), i));
			}
		}
	}
	
	public ResultsTable getFirstInputTable() {
		return firstInputTable;
	}
	public void setFirstInputTable(ResultsTable firstInputTable) {
		this.firstInputTable = firstInputTable;
	}
	public ResultsTable getJoinedTable() {
		return joinedTable;
	}
	public void setJoinedTable(ResultsTable joinedTable) {
		this.joinedTable = joinedTable;
	}
	public ResultsTable getSecondInputTable() {
		return secondInputTable;
	}
	public void setSecondInputTable(ResultsTable secondInputTable) {
		this.secondInputTable = secondInputTable;
	}
	
	protected void showResult() {
		this.getJoinedTable().show("joined results");
	}
	
	protected void cleanUpInput() {
		this.setFirstInputTable(null);
		this.setSecondInputTable(null);
	}
}
