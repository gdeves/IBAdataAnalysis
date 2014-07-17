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
 * The user sets up a list of image filenames. "foreach image in list do" and "foreach image end" build a loop. 
 * In each cycle the result of "foreach image do" is the current filename. When "foreach image end" is reached 
 * the current filename is set to the next in the list and the execution continues after "foreach image do". 
 * When the last filename in the list is reached execution continues after "foreach image end". The user 
 * sets up the file list using the list editor.
 *  
 * @author Volker Bäcker
 */
public class ForeachImageInListDoOperation extends ForeachImageDoOperation {
	private static final long serialVersionUID = 711957037748122577L;

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
		}
		if (this.getImageList().size()==0) {
			this.setAtEnd(new Boolean(true));
			this.stopApplication();
			return;
		}
		String message = "Iteration " + (currentIndex+1) + "/" + this.getImageList().size();
		IJ.showStatus(message);
		this.setCurrentFilename(((File)this.getImageList().elementAt(this.currentIndex)).getAbsolutePath());
		if (currentIndex==this.getImageList().size()-1) {
			this.setAtEnd(new Boolean(true));
		}
	}
	
	public void setPathOptionsFromUser() {
		if (!this.isInteractive()) {
			imageListWasEmpty = false;
			return;
		}
		ListEditor editor = ((ListOption)this.imageList).getEditor();
		if (this.getUseSequenceOpener()) {
			editor.setUseSequenceOpener(true);
		}
		editor.show();
		while (editor.view().isVisible()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Thread.yield();
		}
		imageListWasEmpty = false;
		if (this.getImageList().size()==0) {
			this.stopApplication();
		}
	}
}
