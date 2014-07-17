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
package operations;

import java.io.File;

import gui.options.Option;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.measure.ResultsTable;

/**
 * The operation runs a macro file. The input must be an image.
 * The result is the image that is active after the macro finished
 * and the system results table.
 *  
 * @author	Volker Baecker
 **/
public class MacroOperation extends Operation {
	private static final long serialVersionUID = -3535262582995593558L;
	
	protected Option macroFile;
	protected ResultsTable measurements;

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		resultTypes = new Class[2];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultTypes[1] = Class.forName("ij.measure.ResultsTable");
		resultNames = new String[2];
		resultNames[0] = "Result";
		resultNames[1] = "Measurements";
		optionsNames = new String[1];
		optionsNames[0] = "macro file";
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		result = this.getCopyOfOrReferenceTo(inputImage, inputImage.getTitle());
		WindowManager.setTempCurrentImage(result);
		this.runMacro();
		result = IJ.getImage();
		measurements = ResultsTable.getResultsTable();
		WindowManager.setTempCurrentImage(null);
	}

	public void runMacro() {
		if (noMacroFileSet()) return;
		IJ.runMacroFile(this.getMacroFile());
	}

	private boolean noMacroFileSet() {
		return this.getMacroFile()==null || this.getMacroFile().equals("null") || this.getMacroFile().equals("");
	}

	public String getMacroFile() {
		return macroFile.getValue();
	}

	public void setMacroFile(String macroFile) {
		this.macroFile.setValue(macroFile);
	}

	public ResultsTable getMeasurements() {
		return measurements;
	}

	public void setMeasurements(ResultsTable measurements) {
		this.measurements = measurements;
	}

	protected void setupOptions() {
		super.setupOptions();
		this.setMacroFile("");
		this.macroFile.beForFilename();
		this.macroFile.setShortHelpText("select the macro file");
	}
	
	public void connectOptions() {
		this.macroFile = (Option) this.options.getOptions().get(0);
	}
	
	public boolean isMacroOperation() {
		return true;
	}
	
	public String macroName() {
		String result="macro operation";
		if (!noMacroFileSet()) {
			File aFile = new File(this.getMacroFile());
			result = aFile.getName();     
		}
		return result;
	}
	
	public int browseFileForOption(Option anOption) {
		int result = super.browseFileForOption(anOption);
		this.view().update(this, "name");
		return result;
	}
}
