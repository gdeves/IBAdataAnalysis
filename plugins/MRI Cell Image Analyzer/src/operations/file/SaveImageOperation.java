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
package operations.file;

import java.io.File;
import java.util.ArrayList;
import operations.Operation;
import gui.Options;
import gui.options.BooleanOption;
import gui.options.Option;
import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.io.SaveDialog;

/**
 * Save the input image in a configurable location. Uses the FileSaver from ImageJ.
 * 
 * @author Volker Baecker
 */
public class SaveImageOperation extends Operation {
	private static final long serialVersionUID = 1973545690357695121L;
	protected Boolean success;
	protected String path;
	protected BooleanOption useIndex;
	protected Option nameAddition;
	protected Option outputFolder;
	protected BooleanOption createInSourceFolder;
	
	public SaveImageOperation() {
		super();
		success = new Boolean(true);
	}
	
	protected void initialize() throws ClassNotFoundException {	
		super.initialize();
		parameterTypes = new Class[2];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterTypes[1] = Class.forName("java.lang.String");
		parameterNames = new String[2];
		parameterNames[0] = "InputImage";
		parameterNames[1] = "Path";
		optionsNames = new String[4];
		optionsNames[0] = "output folder";
		optionsNames[1] = "create in source folder";
		optionsNames[2] = "name addition";
		optionsNames[3] = "add loop index to name";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("java.lang.Boolean");
		resultNames = new String[1];
		resultNames[0] = "Success";
	}
	
	public void doIt() {
		ImagePlus inputImage = this.inputImage;
		if (application==null && inputImage==null) inputImage = this.getInputImage(); 
		if (inputImage==null) return;
		inputImage = this.getInputImage();
		FileSaver saver = new FileSaver(inputImage);
		path = this.getPath();
		this.addPathAdditions();
		this.setTifExtension();
		if (inputImage.getStackSize()==1)
			this.setSuccess(new Boolean(saver.saveAsTiff(path)));
		else {
			int extIndex = path.lastIndexOf(".");
			String newPath = path + ".tif";
			if (extIndex!=-1) newPath = path.substring(0, extIndex) + ".tif";
			this.setSuccess(new Boolean(saver.saveAsTiffStack(newPath)));
		}
	}

	private void setTifExtension() {
		String ext = path.substring(path.lastIndexOf(".")+1, path.length());
		if (ext.toLowerCase().equals("tif")) return;
		String filePath = path.substring(0,path.lastIndexOf("."));
		path = filePath + "." + "tif";
	}

	public void getPathFromUser() {
		String name = inputImage.getTitle();
		SaveDialog sd = new SaveDialog("Save as "+"TIFF", name, ".tif");
		name = sd.getFileName();
		if (name==null)
			return;
		String directory = sd.getDirectory();
		inputImage.startTiming();
		path = directory+name;		
	}

	public String getPath() {
		if (path == null) {
			this.getPathFromUser();
			return path;
		}
		if (this.getOutputFolder()=="") {
			return path;
		}
		if (this.getOutputFolder()!="" && !this.getCreateInSourceFolder()) {
			String result = this.getOutputFolder();
			result = result + path.substring(path.lastIndexOf(File.separator), path.length());
			path = result;
			return path;
		}
		if (this.getCreateInSourceFolder()) {
			String result = path.substring(0, path.lastIndexOf(File.separator)+1);
			result = result + this.getOutputFolder().substring(this.getOutputFolder().lastIndexOf(File.separator) + 1, this.getOutputFolder().length());
			File aFile = new File(result);
			if (!aFile.exists()) aFile.mkdir();
			result = result + path.substring(path.lastIndexOf(File.separator), path.length());
			path = result;
			return path;
		} 
		return path;
	}

	protected void addPathAdditions() {
		if (this.getNameAddition()!="") {
			String ext = path.substring(path.lastIndexOf(".")+1, path.length());
			String filePath = path.substring(0,path.lastIndexOf("."));
			path = filePath + this.getNameAddition() + "." + ext;
		}
		if (this.getUseIndex()) {
			String ext = path.substring(path.lastIndexOf(".")+1, path.length());
			String filePath = path.substring(0,path.lastIndexOf("."));
			path = filePath + this.getIndex() + "." + ext;
		}
	}

	protected String getIndex() {
		int result = this.application.getCurrentIndexInLoopFor(this) + 1;
		return Integer.toString(result);
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isSuccess() {
		return success.booleanValue();
	}
	
	public ArrayList<String> categories() {
		ArrayList<String> categories = new ArrayList<String>();
		categories.add("Input / Output");
		return categories;
	}
	
	protected void cleanUpInput() {
		super.cleanUpInput();
		this.setPath(null);
	}

	protected void showResult() {
		String message = "saved " + path + " successfully";
		if (!this.isSuccess()) message = "couldn't save " + path;
		IJ.showStatus(message);
	}
	
	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		this.outputFolder = new Option();
		this.setOutputFolder("");
		this.outputFolder.setName(optionsNames[0]);
		this.outputFolder.beForFilename();
		options.add(this.outputFolder);
		this.createInSourceFolder = new BooleanOption();
		createInSourceFolder.setValue("false");
		createInSourceFolder.setName(optionsNames[1]);
		options.add(createInSourceFolder);
		nameAddition = new Option();
		nameAddition.setValue("");
		nameAddition.setName(optionsNames[2]);
		options.add(nameAddition);
		useIndex = new BooleanOption();
		useIndex.setValue(Boolean.toString(false));
		useIndex.setName(optionsNames[3]);
		options.add(useIndex);
	}
	
	public void setOutputFolder(String path) {
		this.outputFolder.setValue(path);
	}

	public void connectOptions() {
		this.outputFolder = (Option) this.options.getOptions().get(0);
		this.outputFolder.beForFilename();
		this.createInSourceFolder = (BooleanOption) this.options.getOptions().get(1);
		this.nameAddition = (Option) this.options.getOptions().get(2);
		this.useIndex = (BooleanOption) this.options.getOptions().get(3);
	}

	public boolean getCreateInSourceFolder() {
		return createInSourceFolder.getBooleanValue();
	}

	public void setCreateInSourceFolder(boolean value) {
		this.createInSourceFolder.setValue(new Boolean(value).toString());
	}

	public String getOutputFolder() {
		return outputFolder.getValue();
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getNameAddition() {
		return nameAddition.getValue();
	}

	public void setNameAddition(String nameAddition) {
		this.nameAddition.setValue(nameAddition);
	}

	public boolean getUseIndex() {
		return useIndex.getBooleanValue();
	}

	public void setUseIndex(boolean useIndex) {
		this.useIndex.setValue(Boolean.toString(useIndex));
	}
}
