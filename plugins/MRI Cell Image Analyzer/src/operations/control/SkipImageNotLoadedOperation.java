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

import gui.options.Option;
import ij.ImagePlus;
import ij.text.TextWindow;
import operations.Operation;

/** 
 * Skip a configurable number of operations if the input image is null.
 *
 * @author Volker Baecker
 */
public class SkipImageNotLoadedOperation extends Operation {
	private static final long serialVersionUID = -1015304173474479332L;
	protected String filename;
	protected TextWindow textWindow;	
	protected Option numberOfOperations;

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[2];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterTypes[1] = Class.forName("java.lang.String");
		parameterNames = new String[2];
		parameterNames[0] = "InputImage";
		parameterNames[1] = "Filename";
		optionsNames = new String[1];
		optionsNames[0] = "number of operations";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultNames = new String[1];
		resultNames[0] = "Result";
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public void doIt() {
		ImagePlus inputImage = this.inputImage;
		if (application==null) inputImage = this.getInputImage();
		result = inputImage;
		if (result == null || result.getProcessor()==null) {
			this.getTextWindow().append(this.getFilename() + "\tcould not be loaded. Skipped the image." + "\n");
			this.getTextWindow().setVisible(true);
			if (application==null) return;
			application.setProgramCounter(application.getProgramCounter()+this.getNumberOfOperations());
		}
	}

	private TextWindow getTextWindow() {
		if (textWindow==null) {
			textWindow = new TextWindow("skipped files", "image\tmessage", "", 300, 200);
		}
		return textWindow;
	}

	protected void setupOptions() {
		super.setupOptions();
		this.setNumberOfOperations(0);
		numberOfOperations.setMin(0);
		numberOfOperations.setShortHelpText("the number of operations to skip");
	}
	
	public void connectOptions() {
		this.numberOfOperations = (Option) this.options.getOptions().get(0);
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
