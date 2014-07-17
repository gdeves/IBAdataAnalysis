/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2008 cnrs, inserm
 * MRI Cell Image Analyzer has been created at Montpellier RIO Imaging
 * by Volker B�cker
 * 
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

import java.util.Collections;
import ij.ImagePlus;
import ij.gui.ProfilePlot;
import ij.measure.ResultsTable;
import gui.Options;
import gui.options.BooleanOption;
import gui.options.Option;
import operations.Operation;
import utils.CurveMaximaFinder;

public class HeightOfProfileMaximumOperation extends Operation {
	private static final long serialVersionUID = -8546539251247116012L;
	private Option horizontalBars;
	private ResultsTable height;

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.measure.ResultsTable");
		resultNames = new String[1];
		resultNames[0] = "Height";
		optionsNames = new String[1];
		optionsNames[0] = "horizontal bars";
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		ProfilePlot profile = new ProfilePlot(inputImage, this.getHorizontalBars());
		CurveMaximaFinder maxFinder = new CurveMaximaFinder(profile.getProfile());
		maxFinder.setMinHeight(0.1);
		maxFinder.setMinDistance(1);
		double max = Collections.max(maxFinder.getHeights());
		maxFinder.getCurveAndMaximaImage().show();
		this.height = new ResultsTable();
		this.height.reset();
		this.height.incrementCounter();
		this.height.addValue("maximum height", max);
	}
	
	@Override 
	protected void showResult() {
		this.getHeight().show("result of height of profile maximum operation");
	}
	
	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
	
		this.horizontalBars = new BooleanOption();
		this.setHorizontalBars(true);
		this.horizontalBars.setName(optionsNames[0]);
		this.horizontalBars.setShortHelpText("Select if the bars are horizontal.");
		options.add(horizontalBars);
	}

	public void connectOptions() {
		this.horizontalBars = (Option) this.options.getOptions().get(0);
	}
	
	public void setHorizontalBars(boolean value) {
		horizontalBars.setBooleanValue(value);
	}
	
	public boolean getHorizontalBars() {
		return horizontalBars.getBooleanValue();
	}
	
	public ResultsTable getHeight() {
		return height;
	}

	public void setHeight(ResultsTable height) {
		this.height = height;
	}
}
