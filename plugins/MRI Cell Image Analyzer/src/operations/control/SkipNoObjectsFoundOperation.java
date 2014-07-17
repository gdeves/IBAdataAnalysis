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
package operations.control;

import gui.options.Option;
import ij.measure.ResultsTable;
import operations.Operation;

/** 
 * If the results table doesn't contain at least one object a configurable number of
 * opertations is skipped.
 *
 * @author Volker Baecker
 */
public class SkipNoObjectsFoundOperation extends Operation {
	private static final long serialVersionUID = 6753387711732802792L;
	protected ResultsTable objects;
	protected Option numberOfOperations;

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.measure.ResultsTable");
		parameterNames = new String[1];
		parameterNames[0] = "Objects";
		optionsNames = new String[1];
		optionsNames[0] = "number of operations";
		resultTypes = new Class[0];
		resultNames = new String[0];
	}

	public ResultsTable getObjects() {
		return objects;
	}

	public void setObjects(ResultsTable objects) {
		this.objects = objects;
	}
	
	public void doIt() {
		if (application==null) return;
		if (this.getObjects().getCounter()<1) {
			application.setProgramCounter(application.getProgramCounter()+this.getNumberOfOperations());
		}
	}

	public int getNumberOfOperations() {
		return numberOfOperations.getIntegerValue();
	}

	public void setNumberOfOperations(int numberOfOperations) {
		this.numberOfOperations.setValue(Integer.toString(numberOfOperations));
	}
	
	protected void setupOptions() {
		super.setupOptions();
		this.setNumberOfOperations(0);
		numberOfOperations.setMin(0);
		numberOfOperations.setShortHelpText("the number of operations to skip");
	}
	
	public void connectOptions() {
		this.numberOfOperations = (Option) this.options.getOptions().get(0);
	}
	
	protected void cleanUpInput() {
		super.cleanUpInput();
		this.setObjects(null);
	}
}
