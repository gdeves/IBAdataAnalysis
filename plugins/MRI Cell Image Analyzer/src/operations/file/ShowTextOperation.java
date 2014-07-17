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
package operations.file;

import ij.measure.ResultsTable;
import ij.text.TextPanel;
import ij.text.TextWindow;
import operations.Operation;

/**
 * Opens a window and displays the input text panel.
 * 
 * @author Volker
 */
public class ShowTextOperation extends Operation {
	private static final long serialVersionUID = -2215176471181462350L;
	protected TextPanel inputTextPanel;

	protected void initialize() throws ClassNotFoundException {	
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.text.TextPanel");
		parameterNames = new String[1];
		parameterNames[0] = "InputTextPanel";
		resultTypes = new Class[0];
		resultNames = new String[0];
	}
	
	public TextPanel getInputTextPanel() {
		return this.inputTextPanel;
	}

	public void setInputTextPanel(TextPanel inputTextPanel) {
		this.inputTextPanel = inputTextPanel;
	}
	
	public void doIt() {
		if (application==null && inputTextPanel==null) {
			ResultsTable table = copySystemResultsTable();
			inputTextPanel = new TextPanel();
			inputTextPanel.setColumnHeadings(table.getColumnHeadings());
			for (int i=0; i<table.getCounter(); i++) 
				inputTextPanel.append(table.getRowAsString(i));
		}
		TextWindow resultsWindow = new TextWindow("output", inputTextPanel.getColumnHeadings(), 
													inputTextPanel.getText(), 300, 200);
		resultsWindow.setVisible(true);
	}

	protected void cleanUpInput() {
		this.setInputTextPanel(null);
	}

	protected void showResult() {
		// do nothing
	}
	
	protected boolean isBatchOperation() {
		return false;
	}
}
