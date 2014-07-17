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
package operations.image;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.NewImage;
import ij.gui.Roi;
import gui.Options;
import gui.options.ChoiceOption;
import operations.Operation;
import operations.control.WaitForUserOperation;

/**
 * Pastes the first image into the second image using the selected
 * paste-mode.
 * 
 * @author Volker Baecker
 */
public class PasteImageOperation extends Operation {
	private static final long serialVersionUID = -7636076721265449044L;
	protected ChoiceOption mode;
	protected ImagePlus destinationImage;

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[1];
		optionsNames[0] = "mode";
		parameterTypes = new Class[2];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterTypes[1] = Class.forName("ij.ImagePlus");
		parameterNames = new String[2];
		parameterNames[0] = "InputImage";
		parameterNames[1] = "DestinationImage";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultNames = new String[1];
		resultNames[0] = "Result";
	}

	public void doIt() {
		int oldPasteMode = Roi.getCurrentPasteMode();
		ImagePlus inputImage = this.inputImage;
		if (this.getApplication()==null) inputImage = this.getInputImage();
		ImagePlus destinationImage = this.getDestinationImage();
		result = this.getCopyOfOrReferenceTo(destinationImage, destinationImage.getTitle());
		IJ.setPasteMode(this.getMode());
		if (inputImage==null) inputImage = 
			NewImage.createImage("empty", destinationImage.getWidth(), 
										  destinationImage.getHeight(), 
										  destinationImage.getNSlices(), 
										  destinationImage.getBitDepth(),
										  NewImage.FILL_WHITE);
		result.getProcessor().copyBits(this.getInputImage().getProcessor(), 0, 0, Roi.getCurrentPasteMode());
		Roi.setPasteMode(oldPasteMode);
	}
	
	public ImagePlus getDestinationImage() {
		if (this.getApplication()==null) {
			WaitForUserOperation op = new WaitForUserOperation();
			op.run();
			destinationImage = WindowManager.getCurrentImage();
		}
		return destinationImage;
	}

	public void setDestinationImage(ImagePlus destinationImage) {
		this.destinationImage = destinationImage;
	}

	public String getMode() {
		return mode.getValue();
	}

	public void setMode(String mode) {
		this.mode.setValue(mode);
	}
	
	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		this.mode = new ChoiceOption(this.availableModes());
		this.mode.setValue(this.availableModes()[0]);
		this.mode.setName(optionsNames[0]);
		this.mode.setShortHelpText("select a paste mode");
		options.add(this.mode);
	}
	
	private String[] availableModes() {
		String[] modes = {"Copy", "Blend", "Difference", "Transparent", "AND", "OR", "XOR", "Add", "Subtract", "Multiply", "Divide", "Min", "Max"};
		return modes;
	}

	public void connectOptions() {
		this.mode = (ChoiceOption) this.options.getOptions().get(0);
	}
}
