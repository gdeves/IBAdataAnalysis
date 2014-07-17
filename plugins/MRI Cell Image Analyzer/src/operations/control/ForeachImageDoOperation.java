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
package operations.control;

import gui.ListEditor;
import gui.Options;
import gui.options.BooleanOption;
import gui.options.ListOption;
import gui.options.Option;

import ij.IJ;

import java.io.File;
import java.util.Vector;

import operations.Operation;

/**
 * The user sets up a list of image filenames. "foreach image do" and "foreach image end" build a loop. 
 * In each cycle the result of "foreach image do" is the current filename. When "foreach image end" is 
 * reached the current filename is set to the next in the list and the execution continues after "foreach image do". 
 * When the last filename in the list is reached execution continues after "foreach image end".
 * 
 * @author Volker Baecker
 */
public class ForeachImageDoOperation extends Operation {
	private static final long serialVersionUID = -1848902732538247334L;
	protected Option imageList;
	protected Option useSequenceOpener;
	protected String currentFilename;
	protected int currentIndex = 0;
	protected Boolean atEnd = new Boolean(false);
	protected boolean imageListWasEmpty;
	
	public ForeachImageDoOperation() {
		super();
		imageListWasEmpty = false;
	}

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[0];
		parameterNames = new String[0];
		optionsNames = new String[2];
		optionsNames[0] = "image list";
		optionsNames[1] = "use sequence opener";
		resultTypes = new Class[3];
		resultTypes[0] = Class.forName("java.lang.String");
		resultTypes[1] = Class.forName("java.lang.Boolean");
		resultTypes[2] = Class.forName("operations.control.ForeachImageDoOperation");
		resultNames = new String[3];
		resultNames[0] = "CurrentFilename";
		resultNames[1] = "AtEnd";
		resultNames[2] = "DoOperation";
	}

	public ForeachImageDoOperation getDoOperation() {
		return this;
	}

	public void setDoOperation(ForeachImageDoOperation op) {
	}

	public String getCurrentFilename() {
		return currentFilename;
	}

	public void setCurrentFilename(String currentFilename) {
		this.currentFilename = currentFilename;
	}

	public Vector<File> getImageList() {
		return imageList.getListValue();
	}

	protected void setupOptions() {
		options = new Options();
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		this.imageList = new ListOption();
		imageList.setName(optionsNames[0]);
		imageList.setShortHelpText("press edit to select images or a folder");
		options.add(this.imageList);
		this.useSequenceOpener = new BooleanOption();
		useSequenceOpener.setName(optionsNames[1]);
		useSequenceOpener.setShortHelpText("check to use the sequence opener instead of the file dialog.");
		options.add(useSequenceOpener);
	}

	public void doIt() {
		if (this.getApplication()!=null) this.getApplication().cleanupResultsAfter(this);
		if (this.getImageList().size()==0) {
			ListEditor editor = ((ListOption)this.imageList).getEditor();
			if (this.getUseSequenceOpener()) {
				editor.setUseSequenceOpener(true);
			}
			editor.view().getAddButton().doClick();
			imageListWasEmpty = true;
		}
		if (this.getImageList().size()==0) {
			this.setAtEnd(new Boolean(true));
			this.stopApplication();
			return;
		}
		String message = "Iteration " + (currentIndex+1) + "/" + this.getImageList().size();
		IJ.showStatus(message);
		this.setCurrentFilename(((File)this.getImageList().get(this.currentIndex)).getAbsolutePath());
		if (currentIndex==this.getImageList().size()-1) {
			this.setAtEnd(new Boolean(true));
		}
	}

	public boolean next() {
		if (this.getAtEnd().booleanValue()) return false;
		currentIndex++;
		return true;
	}
	
	public void connectOptions() {
		this.imageList = (ListOption) this.options.getOptions().get(0);
		this.useSequenceOpener = (BooleanOption) this.options.getOptions().get(1);
	}

	public void reset() {
		this.setAtEnd(new Boolean(false));
		this.currentIndex = 0;
		if (imageListWasEmpty) {
			((ListOption)this.imageList).getEditor().getList().removeAllElements();
		}
	}

	public Boolean getAtEnd() {
		if (atEnd==null) {
			atEnd = new Boolean(false);
		}
		return atEnd;
	}

	public void setAtEnd(Boolean atEnd) {
		this.atEnd = atEnd;
	}
	
	public boolean isLoop() {
		return true;
	}

	public boolean isImageListWasEmpty() {
		return imageListWasEmpty;
	}

	public void setImageListWasEmpty(boolean imageListWasEmpty) {
		this.imageListWasEmpty = imageListWasEmpty;
	}
	
	public void resetPathOptions() {
		if (this.isInteractive()) imageListWasEmpty = true;
		this.reset();
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
		editor.view().getAddButton().doClick();
		imageListWasEmpty = false;
		if (this.getImageList().size()==0) {
			this.stopApplication();
		}
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public boolean getUseSequenceOpener() {
		return useSequenceOpener.getBooleanValue();
	}

	public void setUseSequenceOpener(boolean value) {
		this.useSequenceOpener.setValue(Boolean.toString(value));
	}
	
	protected void showResult() {
		ListEditor editor = ((ListOption)imageList).getEditor();
		editor.setModal(false);
		editor.show();
	}
}