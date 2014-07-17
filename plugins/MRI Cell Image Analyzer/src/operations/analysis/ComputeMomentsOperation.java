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
package operations.analysis;

import ij.measure.ResultsTable;
import operations.Operation;
import patternMatching.CentralMomentCalculator;

/**
 * Computes the central moments up to the third order and 7 features build from 
 * the central moments that are invariant against translation and rotation. Computes
 * either the moments for the whole image or within the rectangular selection.
 * 
 * @author	Volker Baecker
 **/
public class ComputeMomentsOperation extends Operation {
	private static final long serialVersionUID = -6794489744685353555L;
	ResultsTable moments;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.measure.ResultsTable");
		resultNames = new String[1];
		resultNames[0] = "Moments";
	}

	public ResultsTable getMoments() {
		if (moments==null) {
			moments = new ResultsTable();
		}
		return moments;
	}

	public void setMoments(ResultsTable moments) {
		this.moments = moments;
	}
	
	public void doIt() {
		CentralMomentCalculator calc = new CentralMomentCalculator();
		calc.setImage(this.getInputImage());
		calc.calculate();
		writeValues(calc);
	}

	private void writeValues(CentralMomentCalculator calc) {
		ResultsTable moments = this.getMoments();
		moments.incrementCounter();
		moments.addValue("m00", calc.cm00);
		
		moments.addValue("m10", calc.cm10);
		moments.addValue("m01", calc.cm01);
		
		moments.addValue("m20", calc.cm20);
		moments.addValue("m02", calc.cm02);
		moments.addValue("m11", calc.cm11);
		
		moments.addValue("m30", calc.cm30);
		moments.addValue("m03", calc.cm03);
		moments.addValue("m21", calc.cm21);
		moments.addValue("m12", calc.cm12);
		
		moments.addValue("f1", calc.feature[0]);
		moments.addValue("f2", calc.feature[1]);
		moments.addValue("f3", calc.feature[2]);
		moments.addValue("f4", calc.feature[3]);
		moments.addValue("f5", calc.feature[4]);
		moments.addValue("f6", calc.feature[5]);
		moments.addValue("f7", calc.feature[6]);
	}
	
	protected void showResult() {
		getMoments().show("result from compute moments");
	}
}
