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
import java.util.Arrays;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import imagejProxies.SilentImageCalculator;
import operations.Operation;
import operations.control.WaitForUserOperation;
import gui.Options;
import gui.options.BooleanOption;
import gui.options.ChoiceOption;

/**
 * Apply one of the operations "Add","Subtract","Multiply","Divide", "AND", 
 * "OR", "XOR", "Min", "Max", "Average", "Difference" or "Copy" on the two input images, pixel by pixel.
 * 
 * @author Volker Baecker
 */
public class ImageCalculationOperation extends Operation {
	private static final long serialVersionUID = -8122064832127165532L;
	protected ChoiceOption operator;
	protected BooleanOption floatResult;
	
	protected ImagePlus secondInputImage;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[2];
		optionsNames[0] = "operator";
		optionsNames[1] = "float result";
		parameterTypes = new Class[2];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterTypes[1] = Class.forName("ij.ImagePlus");
		parameterNames = new String[2];
		parameterNames[0] = "InputImage";
		parameterNames[1] = "SecondInputImage";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultNames = new String[1];
		resultNames[0] = "Result";
	}

	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		this.operator = new ChoiceOption(this.operators());
		this.operator.setValue(this.operators()[0]);
		this.operator.setName(optionsNames[0]);
		this.operator.setShortHelpText("select an operation");
		options.add(this.operator);
		this.floatResult = new BooleanOption();
		this.setFloatResult(false);
		floatResult.setName(optionsNames[1]);
		floatResult.setShortHelpText("create a float image as result");
		options.add(this.floatResult);
	}

	private String[] operators() {
		String[] operators = {"Add","Subtract","Multiply","Divide", "AND", "OR", "XOR", "Min", "Max", "Average", "Difference", "Copy"};
		return operators;
	}

	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		result = this.getCopyOfOrReferenceTo(inputImage, inputImage.getTitle());
		SilentImageCalculator calculator = new SilentImageCalculator();
		calculator.setFloatResult(this.getFloatResult());
		calculator.setOperator(Arrays.asList(this.operators()).indexOf(this.getOperator()));
		calculator.setCreateWindow(false);
		calculator.run(result, this.getSecondInputImage());
		if (this.getFloatResult()) result = WindowManager.getCurrentImage();
	}
	
	public void connectOptions() {
		this.operator = (ChoiceOption) this.options.getOptions().get(0);
		this.floatResult = (BooleanOption) this.options.getOptions().get(1);
	}

	public boolean getFloatResult() {
		return floatResult.getBooleanValue();
	}

	public void setFloatResult(boolean floatResult) {
		this.floatResult.setValue(Boolean.toString(floatResult));
	}

	public String getOperator() {
		return operator.getValue();
	}

	public void setOperator(String operator) {
		this.operator.setValue(operator);
	}

	public ImagePlus getSecondInputImage() {
		if (getApplication()==null && secondInputImage==null) {
			WaitForUserOperation op = new WaitForUserOperation();
			op.run();
			secondInputImage = IJ.getImage();
		}
		return secondInputImage;
	}

	public void setSecondInputImage(ImagePlus secondInputImage) {
		this.secondInputImage = secondInputImage;
	}
	
	protected void cleanUpInput() {
		super.cleanUpInput();
		this.setSecondInputImage(null);	
	}
}
