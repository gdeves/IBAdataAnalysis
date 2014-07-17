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

import ij.ImagePlus;
import ij.macro.Interpreter;
import imagejProxies.ExtendedLutLoader;
import java.util.Vector;
import neuronJNewGUI.LookupTableSelector;
import gui.Options;
import gui.options.ChoiceOption;
import operations.Operation;

/**
 * Apply a lookup table to a greyscale image. A lookup table maps intensity values to colors. An image
 * with a lookup-table is displayed with the colors defined by the lookup-table.
 * 
 * @author Volker Baecker
 */
public class ApplyLutOperation extends Operation {
	private static final long serialVersionUID = -1293315312188604087L;
	protected ChoiceOption lookupTable;
	private LookupTableSelector lutSelector;

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[1];
		optionsNames[0] = "lookup tables";
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultNames = new String[1];
		resultNames[0] = "Result";
		lutSelector = new LookupTableSelector();
	}

	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		result = this.getCopyOfOrReferenceTo(inputImage, inputImage.getTitle());
		boolean batchMode = Interpreter.isBatchMode();
		this.getInterpreter().setBatchMode(true);
		boolean noWin = result.getWindow()==null;
		if (noWin) result.show();
		ExtendedLutLoader loader = new ExtendedLutLoader();
		String lutName = lutSelector.getFullLutNameFor(this.getLookupTable());
		loader.applyLut(lutName);
		if (noWin) result.hide();
		this.getInterpreter().setBatchMode(batchMode);
	}

	public void connectOptions() {
		this.lookupTable = (ChoiceOption) this.options.getOptions()
				.get(0);
	}

	protected void setupOptions() {
		options = new Options();
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		Vector<String> choices = lutSelector.getAllLutNames();
		this.lookupTable = new ChoiceOption(choices);
		lookupTable.setName(optionsNames[0]);
		lookupTable.setShortHelpText("select the lookup table");
		lookupTable.setValue((String) choices.elementAt(0));
		options.add(this.lookupTable);
	}

	public String getLookupTable() {
		return lookupTable.getValue();
	}

	public void setLookupTable(String lookupTable) {
		this.lookupTable.setValue(lookupTable);
	}
}