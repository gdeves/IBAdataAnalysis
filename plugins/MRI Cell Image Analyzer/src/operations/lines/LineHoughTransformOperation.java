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
package operations.lines;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import operations.Operation;
import transforms.LineHoughTransform;
import gui.Options;
import gui.options.BooleanOption;
import gui.options.Option;
import ij.ImagePlus;

/**
 * Calculates the line hough transform and answers lines with a support bigger than
 * a relative threshold value. The input image is supposed to be a greyscale line image.
 * 
 * @author Volker Bäcker
 */

public class LineHoughTransformOperation extends Operation {
	private static final long serialVersionUID = -3289575958884878665L;
	protected ImagePlus houghTransform;
	protected ArrayList<Line2D> resultLines;
	protected Option houghThreshold;
	protected BooleanOption showTransform;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";			
		optionsNames = new String[2];
		optionsNames[0] = "hough threshold";
		optionsNames[1] = "show transform";
		resultTypes = new Class[3];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultTypes[1] = Class.forName("java.util.ArrayList");
		resultTypes[2] = Class.forName("ij.ImagePlus");
		resultNames = new String[3];
		resultNames[0] = "Result";
		resultNames[1] = "ResultLines";
		resultNames[2] = "houghTransform";
	}

	public ImagePlus getHoughTransform() {
		return houghTransform;
	}
	
	public void doIt() {
		LineHoughTransform hough = new LineHoughTransform();
		hough.setInputImage(this.getInputImage());
		hough.run();
		this.houghTransform = hough.getAccumulatorImage();
		hough.computeLines(this.getHoughThreshold());
		result = hough.getLinesImage();
	}
	
	public double getHoughThreshold() {
		return houghThreshold.getDoubleValue();
	}
	
	public void setHoughThreshold(double threshold) {
		houghThreshold.setValue((new Double(threshold).toString()));
	}
	
	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		this.houghThreshold = new Option();
		this.setHoughThreshold(0.5);
		this.houghThreshold.setName(optionsNames[0]);
		houghThreshold.setShortHelpText("Lines with a support smaller than the relative threshold are ignored");
		options.add(this.houghThreshold);
		this.showTransform = new BooleanOption();
		showTransform.setName(optionsNames[1]);
		showTransform.setShortHelpText("If selected the image in hough space is shown");
		this.setShowTransform(false);
		options.add(showTransform);
	}
	
	public void connectOptions() {
		this.houghThreshold = (Option) this.options.getOptions().get(0);
		this.showTransform = (BooleanOption) this.options.getOptions().get(1);
	}

	public void setShowTransform(boolean b) {
		showTransform.setValue((new Boolean(b)).toString());
	}

	public boolean getShowTransform() {
		return showTransform.getBooleanValue();
	}
	
	protected void showResult() {
		if (this.getShowResult()) {
			this.getResult().show();
		}
		if (this.getShowTransform()) {
			this.getHoughTransform().show();
		}
	}
	
	public ArrayList<Line2D> getResultLines() {
		return resultLines;
	}
}
