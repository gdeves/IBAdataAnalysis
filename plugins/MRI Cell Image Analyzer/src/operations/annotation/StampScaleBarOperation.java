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
package operations.annotation;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.Roi;
import gui.Options;
import gui.options.BooleanOption;
import gui.options.ChoiceOption;
import gui.options.Option;
import operations.FilterOperation;

/**
 * Stamps a scale bar, indicating the spacial scale, into an image. If the image is a stack the scale bar can be 
 * stamped to the current slice or to all slices. This operation calls the "Scale Bar..." command from ImageJ.
 * 
 * @author Volker Baecker
 */
public class StampScaleBarOperation extends FilterOperation {
	private static final long serialVersionUID = -8529209923378545881L;
	protected Option width;
	protected Option height;
	protected Option fontSize;
	protected Option color;
	protected Option background;
	protected Option location;
	protected Option bold;
	protected Option serif;
	protected Option hideText;
	protected BooleanOption labelAllSlices;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[10];
		optionsNames[0] = "width";
		optionsNames[1] = "height";
		optionsNames[2] = "font size";
		optionsNames[3] = "color";
		optionsNames[4] = "background";
		optionsNames[5] = "location";
		optionsNames[6] = "bold";
		optionsNames[7] = "serif";
		optionsNames[8] = "hide text";
		optionsNames[9] = "label all slices";
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		Roi roi = inputImage.getRoi();
		inputImage.killRoi();
		result = this.getCopyOfOrReferenceTo(inputImage, inputImage.getTitle() + " scale bar");
		inputImage.setRoi(roi);
		result.setRoi(roi);
		ImageStack stack = result.getStack();
		if (stack!=null) {
			result.setSlice(inputImage.getCurrentSlice());
		}
		WindowManager.setTempCurrentImage(result);
		this.runFilter();
		result = IJ.getImage();
		WindowManager.setTempCurrentImage(null);
	}
	public void runFilter() {
		String style = "";
		if (this.getBold()) style+= "bold";
		if (this.getHideText()) style+= " hide";
		if (this.getSerif()) style+= " serif";
		String options = "width="+this.getWidth()+" height="+this.getHeight()+" font="+this.getFontSize()+
		" color="+this.getColor()+ " background="+this.getBackground()+" location=["+this.getLocation()+"] " +
		style;
		if (this.getLabelAllSlices()) options = options + " label";
		IJ.run("Scale Bar...", options);
		result.killRoi();
	}

	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
	
		this.width = new Option();
		this.setWidth(1);
		this.width.setName(optionsNames[0]);
		this.width.setMin(0);
		this.width.setShortHelpText("the width of the scale bar");
		options.add(width);
		
		this.height = new Option();
		this.setHeight(12);
		this.height.setName(optionsNames[1]);
		this.height.setMin(0);
		this.height.setShortHelpText("the height of the scale bar");
		options.add(height);
		
		this.fontSize = new Option();
		this.setFontSize(42);
		this.fontSize.setName(optionsNames[2]);
		this.fontSize.setMin(0);
		this.fontSize.setShortHelpText("the size of the text");
		options.add(fontSize);
		
		this.color = new ChoiceOption(this.colorChoices());
		this.setColor(this.colorChoices()[0]);
		this.color.setName(optionsNames[3]);
		this.color.setShortHelpText("the color of the scale bar");
		options.add(color);
		
		this.background = new ChoiceOption(this.backgroundChoices());
		this.setBackground(this.backgroundChoices()[0]);
		this.background.setName(optionsNames[4]);
		this.background.setShortHelpText("the background of the scale bar");
		options.add(background);
		
		this.location = new ChoiceOption(this.locationChoices());
		this.setLocation(this.locationChoices()[1]);
		this.location.setName(optionsNames[5]);
		this.location.setShortHelpText("the position of the scale bar");
		options.add(location);
		
		this.bold = new BooleanOption();
		this.setBold(true);
		this.bold.setName(optionsNames[6]);
		this.bold.setShortHelpText("check to use bold font");
		options.add(bold);
		
		this.serif = new BooleanOption();
		this.setSerif(false);
		this.serif.setName(optionsNames[7]);
		this.serif.setShortHelpText("check to use serif font");
		options.add(serif);
		
		this.hideText = new BooleanOption();
		this.setHideText(false);
		this.hideText.setName(optionsNames[8]);
		this.hideText.setShortHelpText("check to hide the text");
		options.add(hideText);
		
		this.labelAllSlices = new BooleanOption();
		this.setLabelAllSlices(false);
		this.labelAllSlices.setName(optionsNames[9]);
		this.labelAllSlices.setShortHelpText("apply to all slices or to the current slice only");
		options.add(labelAllSlices);
	}
	
	public void connectOptions() {
		this.width = (Option) this.options.getOptions().get(0);
		this.height = (Option) this.options.getOptions().get(1);
		this.fontSize = (Option) this.options.getOptions().get(2);
		this.color = (ChoiceOption) this.options.getOptions().get(3);
		this.background = (ChoiceOption) this.options.getOptions().get(4);
		this.location = (ChoiceOption) this.options.getOptions().get(5);
		this.bold = (BooleanOption) this.options.getOptions().get(6);
		this.serif = (BooleanOption) this.options.getOptions().get(7);
		this.hideText = (BooleanOption) this.options.getOptions().get(8);
		this.labelAllSlices = (BooleanOption) this.options.getOptions().get(9);
	}
	
	protected String[] locationChoices() {
		String[] result = {"Upper Right", "Lower Right", "Lower Left", "Upper Left", "At Selection"};
		return result;
	}

	protected String[] backgroundChoices() {
		String[] result = {"None","Black","White","Dark Gray","Gray","Light Gray","Yellow","Blue","Green","Red"};
		return result;
	}

	protected String[] colorChoices() {
		String[] result = {"White","Black","Light Gray","Gray","Dark Gray","Red","Green","Blue","Yellow"};
		return result;
	}

	public String getBackground() {
		return background.getValue();
	}

	public void setBackground(String background) {
		this.background.setValue(background);
	}

	public boolean getBold() {
		return bold.getBooleanValue();
	}

	public void setBold(boolean bold) {
		this.bold.setBooleanValue(bold);
	}

	public String getColor() {
		return color.getValue();
	}

	public void setColor(String color) {
		this.color.setValue(color);
	}

	public int getFontSize() {
		return fontSize.getIntegerValue();
	}

	public void setFontSize(int fontSize) {
		this.fontSize.setIntegerValue(fontSize);
	}

	public int getHeight() {
		return height.getIntegerValue();
	}

	public void setHeight(int height) {
		this.height.setIntegerValue(height);
	}

	public boolean getHideText() {
		return hideText.getBooleanValue();
	}

	public void setHideText(boolean hideText) {
		this.hideText.setBooleanValue(hideText);
	}

	public String getLocation() {
		return location.getValue();
	}

	public void setLocation(String location) {
		this.location.setValue(location);
	}

	public boolean getSerif() {
		return serif.getBooleanValue();
	}

	public void setSerif(boolean serif) {
		this.serif.setBooleanValue(serif);
	}

	public int getWidth() {
		return width.getIntegerValue();
	}

	public void setWidth(int width) {
		this.width.setIntegerValue(width);
	}

	public boolean getLabelAllSlices() {
		return labelAllSlices.getBooleanValue();
	}

	public void setLabelAllSlices(boolean labelAllSlices) {
		this.labelAllSlices.setBooleanValue(labelAllSlices);
	}
}
