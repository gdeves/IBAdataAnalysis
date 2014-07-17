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
package operations.analysis;

import java.awt.Rectangle;

import analysis.GLCMTextureAnalyzer;
import ij.ImagePlus;
import ij.gui.NewImage;
import ij.process.ImageProcessor;
import gui.Options;
import gui.options.ChoiceOption;
import gui.options.Option;
import operations.Operation;

/**
 * Calculates a stack with one slice for each of the glcm texture analysis features.
 * 
 * @author Volker Baecker
 */
public class TextureAnalysisOperation extends Operation {
	private static final long serialVersionUID = -4476501133184727805L;
	
	protected Option radius;
	protected Option step;
	protected Option angle;

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[0];
		parameterNames = new String[0];
		optionsNames = new String[3];
		optionsNames[0] = "radius";
		optionsNames[1] = "step";
		optionsNames[2] = "angle";
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		result =  NewImage.createFloatImage(inputImage.getTitle() + " texture values", inputImage.getWidth(), 
											inputImage.getHeight(), 5, NewImage.FILL_BLACK);
		int radius = this.getRadius();
		Rectangle zone = new Rectangle(0, 0, 2*radius+1, 2*radius+1);
		GLCMTextureAnalyzer analyzer = new GLCMTextureAnalyzer(inputImage.getProcessor());
		analyzer.setRectangle(zone);
		analyzer.setStep(this.getStep());
		if (this.getAngle().equals(this.getAngleChoices()[0])) analyzer.be0Degrees();
		if (this.getAngle().equals(this.getAngleChoices()[1])) analyzer.be90Degrees();
		if (this.getAngle().equals(this.getAngleChoices()[2])) analyzer.be180Degrees();
		if (this.getAngle().equals(this.getAngleChoices()[3])) analyzer.be270Degrees();
		ImageProcessor processor1 = result.getStack().getProcessor(1);
		ImageProcessor processor2 = result.getStack().getProcessor(2);
		ImageProcessor processor3 = result.getStack().getProcessor(3);
		ImageProcessor processor4 = result.getStack().getProcessor(4);
		ImageProcessor processor5 = result.getStack().getProcessor(5);
		for (int x=0; x<inputImage.getWidth(); x++) {
			for (int y=0; y<inputImage.getHeight(); y++) {
				zone.setLocation(x-radius, y-radius);
				analyzer.run();
				processor1.putPixelValue(x, y, analyzer.angularSecondMoment);
				processor2.putPixelValue(x, y, analyzer.contrast);
				processor3.putPixelValue(x, y, analyzer.correlation);
				processor4.putPixelValue(x, y, analyzer.inverseDifferenceMoment);
				processor5.putPixelValue(x, y, analyzer.entropy);
			}
		}
	}
	
	protected String[] getAngleChoices() {
		String[] choices = {"0 degrees", "90 degrees", "180 degrees", "270 degrees"};
		return choices;
	}

	public String getAngle() {
		return angle.getValue();
	}
	public void setAngle(String angle) {
		this.angle.setValue(angle);
	}
	public int getRadius() {
		return radius.getIntegerValue();
	}
	public void setRadius(int radius) {
		this.radius.setIntegerValue(radius);
	}
	public int getStep() {
		return step.getIntegerValue();
	}
	public void setStep(int step) {
		this.step.setIntegerValue(step);
	}
	
	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		
		this.radius = new Option();
		radius.setName(optionsNames[0]);
		this.setRadius(25);
		radius.setMin(1);
		radius.setShortHelpText("the radius of the neighborhood in which the texture is analyzed");
		options.add(this.radius);
		
		this.step = new Option();
		step.setName(optionsNames[1]);
		this.setStep(1);
		step.setMin(1);
		step.setShortHelpText("the step size for the texture analysis");
		options.add(this.step);
		
		this.angle = new ChoiceOption(this.getAngleChoices());
		angle.setName(optionsNames[2]);
		this.setAngle(getAngleChoices()[1]);
		angle.setShortHelpText("select the direction for the texture analysis");
		options.add(this.angle);
	}
	
	public void connectOptions() {
		this.radius = (Option) this.options.getOptions().get(0);
		this.step = (Option) this.options.getOptions().get(1);
		this.angle = (ChoiceOption) this.options.getOptions().get(2);
	}
}
