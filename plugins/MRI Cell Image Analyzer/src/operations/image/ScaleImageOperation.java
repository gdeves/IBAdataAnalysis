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
package operations.image;

import ij.IJ;
import gui.Options;
import gui.options.BooleanOption;
import gui.options.Option;
import operations.FilterOperation;

/**
 * Scales the image in x and y-direction by a given factor.
 * 
 * @author Volker Bäcker
 */
public class ScaleImageOperation extends FilterOperation {
	private static final long serialVersionUID = 1L;
	protected Option scaleFactorX;
	protected Option scaleFactorY;
	protected BooleanOption interpolate;
	protected BooleanOption fillWithBackgroundColor;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[4];
		optionsNames[0] = "scale factor x";
		optionsNames[1] = "scale factor y";
		optionsNames[2] = "interpolate";
		optionsNames[3] = "fill";
	}
	
	public void runFilter() {
		String options = "x=" + this.getScaleFactorX() + " ";
		options += "y=" + this.getScaleFactorY();
		String interpolateString = "";
		if (this.getInterpolate()) interpolateString = " interpolate";
		options += interpolateString;
		String fillString = "";
		if (this.getFillWithBackgroundColor()) fillString = " fill";
		options += fillString;
		String createString = "";
		if (this.isKeepSource()) createString = " create";
		options += createString;
		String titleString = this.getInputImage().getTitle() + "-scaled";
		options += " title=" + titleString;
		IJ.run("Scale...", options);
	}

	public boolean getFillWithBackgroundColor() {
		return fillWithBackgroundColor.getBooleanValue();
	}

	public void setFillWithBackgroundColor(boolean fillWithBackgroundColor) {
		this.fillWithBackgroundColor.setValue(Boolean.toString(fillWithBackgroundColor));
	}

	public boolean getInterpolate() {
		return interpolate.getBooleanValue();
	}

	public void setInterpolate(boolean interpolate) {
		this.interpolate.setValue(Boolean.toString(interpolate));
	}

	public double getScaleFactorX() {
		return scaleFactorX.getDoubleValue();
	}

	public void setScaleFactorX(double scaleFactorX) {
		this.scaleFactorX.setValue(Double.toString(scaleFactorX));
	}

	public double getScaleFactorY() {
		return scaleFactorY.getDoubleValue();
	}

	public void setScaleFactorY(double scaleFactorY) {
		this.scaleFactorY.setValue(Double.toString(scaleFactorY));
	}

	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		this.scaleFactorX = new Option();
		this.setScaleFactorX(0.5);
		this.scaleFactorX.setMin(0.05);
		this.scaleFactorX.setMax(25);
		this.scaleFactorX.setName(optionsNames[0]);
		this.scaleFactorX.setShortHelpText("The scale factor in x-direction.");
		options.add(this.scaleFactorX);
		this.scaleFactorY = new Option();
		this.setScaleFactorY(0.5);
		this.scaleFactorY.setMin(0.05);
		this.scaleFactorY.setMax(25);
		this.scaleFactorY.setName(optionsNames[1]);
		this.scaleFactorY.setShortHelpText("The scale factor in y-direction.");
		options.add(this.scaleFactorY);
		this.interpolate = new BooleanOption();
		this.setInterpolate(true);
		this.interpolate.setName(optionsNames[2]);
		this.interpolate.setShortHelpText("Check interpolate to use bilinear interpolation.");
		options.add(this.interpolate);
		this.fillWithBackgroundColor = new BooleanOption();
		this.setFillWithBackgroundColor(true);
		this.fillWithBackgroundColor.setName(optionsNames[3]);
		this.fillWithBackgroundColor.setShortHelpText("This is only used when the input image is changed.");
		options.add(this.fillWithBackgroundColor);
	}

	public void connectOptions() {
		this.scaleFactorX = (Option) this.options.getOptions().get(0);
		this.scaleFactorY = (Option) this.options.getOptions().get(1);
		this.interpolate = (BooleanOption) this.options.getOptions().get(2);
		this.fillWithBackgroundColor = (BooleanOption) this.options.getOptions().get(3);
	}	
}
