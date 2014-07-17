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
import gui.ListEditor;
import gui.options.ListOption;
import ij.IJ;
import java.io.File;

/** 
 * The user sets up a list of image filenames. "foreach pair of images in list do" and "foreach image end" build a loop. 
 * In each cycle the results of "foreach pair of images in list do" are the filenames of the current image and the next
 * image. When "foreach image end" is reached the current filename is set to the next in the list and the execution 
 * continues after "foreach pair of images do". When the last two filenames in the list are reached execution continues
 * after "foreach image end". The user sets up the file list using the list editor.
 *  
 * @author Volker Bäcker
 */
public class ForeachPairOfImagesInListDoOperation extends ForeachImageInListDoOperation {
	private static final long serialVersionUID = 8437123371314601838L;
	
	protected String currentFilenameSecondImage;

	protected Boolean isFirstInFolder = new Boolean(true);
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[0];
		parameterNames = new String[0];
		optionsNames = new String[2];
		optionsNames[0] = "image list";
		optionsNames[1] = "use sequence opener";
		resultTypes = new Class[5];
		resultTypes[0] = Class.forName("java.lang.String");
		resultTypes[1] = Class.forName("java.lang.Boolean");
		resultTypes[2] = Class.forName("operations.control.ForeachImageDoOperation");
		resultTypes[3] = Class.forName("java.lang.String");
		resultTypes[4] = Class.forName("java.lang.Boolean");
		resultNames = new String[5];
		resultNames[0] = "CurrentFilename";
		resultNames[1] = "AtEnd";
		resultNames[2] = "DoOperation";
		resultNames[3] = "CurrentFilenameSecondImage";
		resultNames[4] = "IsFirstInFolder";
	}

	public String getCurrentFilenameSecondImage() {
		return currentFilenameSecondImage;
	}

	public void setCurrentFilenameSecondImage(String currentFilenameSecondImage) {
		this.currentFilenameSecondImage = currentFilenameSecondImage;
	}
	
	public void doIt() {
		if (this.getApplication()!=null) this.getApplication().cleanupResultsAfter(this);
		if (this.getImageList().size()==0) {
			ListEditor editor = ((ListOption)this.imageList).getEditor();
			if (this.getUseSequenceOpener()) {
				editor.setUseSequenceOpener(true);
			}
			if (this.getApplication()==null) editor.setModal(false);
			editor.show();
			imageListWasEmpty = true;
			isFirstInFolder = new Boolean(true);
		}
		if (this.getImageList().size()<2) {
			this.setAtEnd(new Boolean(true));
			this.stopApplication();
			return;
		}
		File file1 = (File)this.getImageList().elementAt(this.currentIndex);
		File file2 = (File)this.getImageList().elementAt(this.currentIndex + 1);
		if (!file1.getParentFile().getAbsolutePath().equals(file2.getParentFile().getAbsolutePath())) {
			currentIndex++;
			isFirstInFolder = new Boolean(true);
			if (currentIndex>this.getImageList().size()-2) {
				this.setAtEnd(new Boolean(true));
				return;
			}
			file1 = (File)this.getImageList().elementAt(this.currentIndex);
			file2 = (File)this.getImageList().elementAt(this.currentIndex + 1);
		} else {
			if (currentIndex!=0) isFirstInFolder = new Boolean(false);
		}
			
		String message = "Iteration " + (currentIndex+1) + "/" + (this.getImageList().size()-1);
		IJ.showStatus(message);
		this.setCurrentFilename(file1.getAbsolutePath());
		this.setCurrentFilenameSecondImage(file2.getAbsolutePath());
		if (currentIndex>=this.getImageList().size()-2) {
			this.setAtEnd(new Boolean(true));
		}
	}

	public Boolean getIsFirstInFolder() {
		return isFirstInFolder;
	}

	public void setIsFirstInFolder(Boolean isFirstInFolder) {
		//this.isFirstInFolder = isFirstInFolder;
	}
}
