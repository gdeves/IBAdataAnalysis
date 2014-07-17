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

import ij.ImagePlus;
import ij.text.TextWindow;
import gui.options.Option;
import operations.Operation;

/** 
 * If more than a configurable percentage of pixels in the input image is saturated (has the maximal intensity), a 
 * configurable number of operations is skipped. 
 *
 * @author Volker Baecker
 */
public class SkipSaturatedOperation extends Operation {
	private static final long serialVersionUID = 6007243096948820332L;
	protected String filename;
	protected TextWindow textWindow;	
	protected Option maxPercentSaturated;
	protected Option numberOfOperations;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[2];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterTypes[1] = Class.forName("java.lang.String");
		parameterNames = new String[2];
		parameterNames[0] = "InputImage";
		parameterNames[1] = "Filename";
		optionsNames = new String[2];
		optionsNames[0] = "max. % saturated";
		optionsNames[1] = "number of operations";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultNames = new String[1];
		resultNames[0] = "Result";
	}

	public double getMaxPercentSaturated() {
		return maxPercentSaturated.getDoubleValue();
	}

	public void setMaxPercentSaturated(double maxPercentSaturated) {
		this.maxPercentSaturated.setValue(Double.toString(maxPercentSaturated));
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		if (application==null && inputImage==null) return;
		if (application==null) filename = inputImage.getTitle();
		result = inputImage;
		int maxValue = 255;
		if (result.getType()==ImagePlus.GRAY16) {
			maxValue = 65535;
		}
		int totalNumberOfPixel = result.getWidth() * result.getHeight();
		int numberOfSaturatedPixel = this.getNumberOfSaturatedPixel(maxValue);
		double percent = (numberOfSaturatedPixel * 100) / (totalNumberOfPixel * 1.0);
		if (percent<=this.getMaxPercentSaturated()) {
			return;
		}
		this.getTextWindow().append(this.getFilename() + "\t" + percent + "\n");
		this.getTextWindow().setVisible(true);
		if (application==null) return;
		application.setProgramCounter(application.getProgramCounter()+this.getNumberOfOperations());
	}

	private TextWindow getTextWindow() {
		if (textWindow==null) {
			textWindow = new TextWindow("skipped files", "image\tpercent", "", 300, 200);
		}
		return textWindow;
	}

	private int getNumberOfSaturatedPixel(int max) {
		int[] histogram = result.getProcessor().getHistogram();
		int result = histogram[max];
		return result;
	}

	protected void setupOptions() {
		super.setupOptions();
		this.setMaxPercentSaturated(0.5);
		maxPercentSaturated.setMin(0);
		maxPercentSaturated.setMax(100);
		maxPercentSaturated.setShortHelpText("enter the maximal allowed percent of saturated pixel");
		this.setNumberOfOperations(0);
		numberOfOperations.setMin(0);
		numberOfOperations.setShortHelpText("the number of operations to skip");
	}
	
	public void connectOptions() {
		this.maxPercentSaturated = (Option) this.options.getOptions().get(0);
		this.numberOfOperations = (Option) this.options.getOptions().get(1);
	}

	public int getNumberOfOperations() {
		return numberOfOperations.getIntegerValue();
	}

	public void setNumberOfOperations(int numberOfOperations) {
		this.numberOfOperations.setValue(Integer.toString(numberOfOperations));
	}

	public void resetPathOptions() {
		this.textWindow = null;
		
	}

	public void setPathOptionsFromUser() {
		// noop
	}
}
