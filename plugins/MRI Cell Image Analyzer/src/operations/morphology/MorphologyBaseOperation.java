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
package operations.morphology;

import ij.IJ;
import ij.ImagePlus;
import operations.FilterOperation;
import gui.Options;
import gui.options.ChoiceOption;
import gui.options.MatrixOption;
import gui.options.Option;

/**
 * Base class of the morphological operations that use the Grayscale Morphology plugins by Dimiter Prodanov.
 * 
 * @author Volker B�cker
 */
abstract public class MorphologyBaseOperation extends FilterOperation {
	
	private static final long serialVersionUID = -4254136092329863050L;

	abstract public String operator();
	
	protected Option morphologyType;
	protected Option radius; 
	protected Option structuringElementType;
	protected Option structuringElement;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[4];
		optionsNames[0] = "morphology type";
		optionsNames[1] = "radius";
		optionsNames[2] = "structuring element type";
		optionsNames[3] = "structuring element";
	}	
	
	public void runFilter() {
		if (this.getMorphologyType()=="8-bit" && result.getType() != ImagePlus.GRAY8 && result.getType() != ImagePlus.COLOR_256) IJ.run("8-bit");
		if (this.getMorphologyType()=="32-bit" && result.getType() != ImagePlus.GRAY32 && result.getType()!=ImagePlus.COLOR_RGB) IJ.run("32-bit");
		String type = getMorphologyTypeString();
		String command = type + "Morphology ";
		String options = "radius=" + this.getRadius() + " type=" + this.getStructuringElementType() + " operator=" + this.operator();
		IJ.run(command, options);	
	}

	protected String getMorphologyTypeString() {
		String type = "Gray";
		if (this.getMorphologyType()=="32-bit") type = "Float";
		return type;
	}

	public boolean isFloatMorphology() {
		return this.getMorphologyTypeString().equals("Float");
	}
	
	public String getMorphologyType() {
		return morphologyType.getValue();
	}

	public void setMorphologyType(String morphologyType) {
		this.morphologyType.setValue(morphologyType);
	}

	public int getRadius() {
		return radius.getIntegerValue();
	}

	public void setRadius(int radius) {
		this.radius.setValue(Integer.toString(radius));
	}

	public String getStructuringElementType() {
		return structuringElementType.getValue();
	}

	public void setStructuringElementType(String structuringElementType) {
		this.structuringElementType.setValue(structuringElementType);
	}
	

	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		this.morphologyType = new ChoiceOption(this.getMorphologyTypeChoices());
		this.setMorphologyType(this.getMorphologyTypeChoices()[0]);
		this.morphologyType.setName(optionsNames[0]);
		this.morphologyType.setShortHelpText("Shall the algorithm work internally with 8-bit or 32-bit data?");
		options.add(this.morphologyType);
		this.radius = new Option();
		this.setRadius(4);
		this.radius.setName(optionsNames[1]);
		this.radius.setShortHelpText("The radius of the structuring element.");
		options.add(this.radius);
		this.structuringElementType = new ChoiceOption(this.getStructuringElementTypeChoices());
		this.setStructuringElementType(this.getStructuringElementTypeChoices()[0]);
		this.structuringElementType.setName(optionsNames[2]);
		this.structuringElementType.setShortHelpText("The type of the structuring element.");
		options.add(this.structuringElementType);
		this.structuringElement = new MatrixOption();
		this.setStructuringElement(" 0, 0, 0, 0, 0; 0, 0, 255, 0, 0; 0, 255, 255, 255, 0; 0, 0, 255, 0, 0; 0, 0, 0, 0, 0");
		this.structuringElement.setName(optionsNames[3]);
		this.structuringElement.setShortHelpText("Enter the structuring element if you selected free form as type.");
		options.add(this.structuringElement);
	}

	protected String[] getStructuringElementTypeChoices() {
		String[] choices = {"circle", "diamond", "square", "[hor line]", "[ver line]", "[2p h]", "[2p v]", "[free form]"};
		return choices;
	}

	protected String[] getMorphologyTypeChoices() {
		String[] choices = {"8-bit", "32-bit"};
		return choices;
	}
	
	public void connectOptions() {
		this.morphologyType = (Option) this.options.getOptions().get(0);
		this.radius = (Option) this.options.getOptions().get(1);
		this.structuringElementType = (Option) this.options.getOptions().get(2);
		this.structuringElement = (MatrixOption) this.options.getOptions().get(3);
	}

	public String getStructuringElement() {
		return structuringElement.getValue();
	}

	public void setStructuringElement(String structuringElement) {
		this.structuringElement.setValue(structuringElement);
	}
}
