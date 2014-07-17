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
import gui.options.Option;
import ij.io.OpenDialog;
import ij.io.Opener;

/**
 * If path is null the operation opens a file dialog and loads the image selected by the
 * user. Otherwise the image in path is loaded. With help of the options part of the filename
 * can be replaced. That allows to automatically open images belonging together in an application. 
 * 
 * @author Volker Baecker
 */
public class OpenImageOperation extends Operation {
	private static final long serialVersionUID = 5173632257684493168L;
	protected Option stringToBeReplaced;
	protected Option stringToReplaceWith;
	/** The folder in which the image file is. */
	protected String folder;
	/** The filename of the image file without path or folder. */
	protected String filename;
	protected String path;
	protected boolean resetPath = false;

	protected void initialize() throws ClassNotFoundException {	
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("java.lang.String");
		parameterNames = new String[1];
		parameterNames[0] = "Path";
		optionsNames = new String[2];
		optionsNames[0] = "replace string";
		optionsNames[1] = "replace with";
		resultTypes = new Class[2];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultTypes[1] = Class.forName("java.lang.String");
		resultNames = new String[2];
		resultNames[0] = "Result";
		resultNames[1] = "AbsoluteFilename";
	}
	
	/**
	 * Answer the folder in which the image file is.
	 * @return Returns the folder.
	 */
	public String getFolder() {
		return folder;
	}
	
	/**
	 * Set the folder in which the image file is.
	 * @param folder The folder to set.
	 */
	public void setFolder(String folder) {
		this.folder = folder;
	}
	
	public void getFilenameFromUser() {
		OpenDialog openDialog = new OpenDialog("Open Image", "");
		filename = openDialog.getFileName();
		if (filename == null) {
			path = null;
			return;
		}
		folder = openDialog.getDirectory();
		OpenDialog.setDefaultDirectory(folder);
		path = folder + filename;
		resetPath = true;
	}

	public void doIt() {
		String thePath = this.getPath();
		if (path==null) {
			this.stopApplication();
			return;
		}
		Opener opener = new Opener();
		int index = thePath.lastIndexOf(File.separator);
		String dir = thePath.substring(0, index+1);
		String filename = thePath.substring(index+1, thePath.length());
		result = opener.openImage(dir, filename);
	}

	public String getAbsoluteFilename() {
		return path;
	}
	
	public void setAbsoluteFilename(String path) {
		this.path = path;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getPath() {
		if (path==null) {
			this.getFilenameFromUser();
		}
		if (!this.getStringToBeReplaced().equals("")) {
			System.out.println(this.substitutedPath());
			return this.substitutedPath();
		}
		return path;
	}

	private String substitutedPath() {
		return this.path.replaceAll(this.getStringToBeReplaced(), this.getStringToReplaceWith());
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public ArrayList<String> categories() {
		ArrayList<String> categories = new ArrayList<String>();
		categories.add("Input / Output");
		return categories;
	}

	protected void cleanUpInput() {
		if (resetPath) this.path = null;
		resetPath = false;
	}
	
	public void resetPathOptions() {
		path = null;
	}
	
	public void setPathOptionsFromUser() {
		if (!this.isConnected()) {
			this.getFilenameFromUser();
			resetPath = false;
			if (path == null) {
				this.stopApplication();
				return;
			}
		}
	}

	public String getStringToBeReplaced() {
		return stringToBeReplaced.getValue();
	}

	public void setStringToBeReplaced(String stringToBeReplaced) {
		this.stringToBeReplaced.setValue(stringToBeReplaced);
	}

	public String getStringToReplaceWith() {
		return stringToReplaceWith.getValue();
	}

	public void setStringToReplaceWith(String stringToReplaceWith) {
		this.stringToReplaceWith.setValue(stringToReplaceWith);
	}
	
	protected void setupOptions() {
		super.setupOptions();
		this.setStringToBeReplaced("");
		this.stringToBeReplaced.setShortHelpText("enter the part of the filename to be replaced");
		this.stringToReplaceWith.setShortHelpText("enter the substitution for the part to be replaced");
	}
	
	public void connectOptions() {
		this.stringToBeReplaced = (Option) this.options.getOptions().get(0);
		this.stringToReplaceWith = (Option) this.options.getOptions().get(1);
	}
}
