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

import ij.ImagePlus;
import imagejProxies.MRIConverter;
import gui.Options;
import gui.options.ChoiceOption;
import operations.Operation;

/**
 * Convert the image type of the input image to the type selected in the options.
 * Uses the class converter from ImageJ.
 * 
 * @author Volker Baecker
 */
public class ConvertImageTypeOperation extends Operation {
	private static final long serialVersionUID = -1884637314382451366L;
	protected ChoiceOption outputType;

	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		result = this.getCopyOfOrReferenceTo(inputImage, inputImage.getTitle());
		MRIConverter converter = new MRIConverter(result);
		converter.run(this.getOutputType());
	}
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[1];
		optionsNames[0] = "output type";
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultNames = new String[1];
		resultNames[0] = "Result";
	}
	
	public String getOutputType() {
		return outputType.getValue();
	}

	public void setOutputType(String outputType) {
		this.outputType.setValue(outputType);
	}
	
	public void convertTo8Bit() {
		this.setOutputType("8-bit");
	}
	
	public void convertTo16Bit() {
		this.setOutputType("16-bit");
	}
	
	public void convertTo32Bit() {
		this.setOutputType("32-bit");
	}
	public void convertTo8BitColor() {
		this.setOutputType("8-bit Color");
	}
	
	public void convertToRGBColor() {
		this.setOutputType("RGB Color");
	}
	
	public void convertToRGBStack() {
		this.setOutputType("RGB Stack");
	}
	
	public void convertToHSBStack() {
		this.setOutputType("HSB Stack");
	}
	
	public String[] outputTypes() {
		String[] types = {"8-bit", "16-bit", "32-bit", "8-bit Color", "RGB Color", "RGB Stack", "HSB Stack"};
		return types;
	}
	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		this.outputType = new ChoiceOption(this.outputTypes());
		this.outputType.setValue(this.outputTypes()[0]);
		this.outputType.setName(optionsNames[0]);
		this.outputType.setShortHelpText("choose the type of the result image");
		options.add(this.outputType);
	}
	
	public void connectOptions() {
		outputType = (ChoiceOption) options.getOptions().get(0);
	}
}
