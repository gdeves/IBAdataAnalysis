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
package gui;

import java.awt.Dialog.ModalityType;
import java.io.File;
import javax.swing.filechooser.FileFilter;

import neuronJNewGUI.ConfigurableFileFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Observable;
import java.util.Vector;

/**
 * The list editor allows to select a list of image files. Files can be added either from a file-open dialog
 * or using the sequence opener. Images can be selected manually or by substrings contained in the path. Selected
 * images can be deleted from the list.
 * 
 * @author	Volker Baecker
 **/
public class ListEditor extends Observable {
	protected ListEditorView view;
	protected Vector<File> list;
	protected boolean showPath = true;
	protected boolean useSequenceOpener = false;
	protected boolean modal = true;
	
	public void show() {
		if (!isModal()) this.view().setModalityType(ModalityType.MODELESS);
		this.view().setVisible(true);
		this.changed("list");
	}
	
	public ListEditorView view() {
		if (this.view == null) {
			this.view = new ListEditorView(this);
		}
		return this.view;
	}

	public static ConfigurableFileFilter getTiffFileFilter() {
		String[] extensions = {"tif", "tiff", "TIF", "TIFF"};
		ConfigurableFileFilter filter = new ConfigurableFileFilter(extensions, "tif images");
		return filter;
	}
	
	public static ConfigurableFileFilter getImageFileFilter() {
		String[] extensions = {"tiff", "tif", "TIF", "TIFF", "gif", "GIF", "jpg", "JPG", "bmp", "BMP", "pgm", "PGM", "stk", "STK"};
		ConfigurableFileFilter filter = new ConfigurableFileFilter(extensions, "all images");
		return filter;
	}

	public void addToList(File[] selectedFiles, FileFilter filter) {
		for(int i=0; i<selectedFiles.length;i++) {
			File aFile = selectedFiles[i];
			if (!filter.accept(aFile)) {
				selectedFiles[i] = null;
				continue;
			}
			if (aFile.isDirectory()) {
				this.addToList(aFile.listFiles(), (FileFilter)filter);
				selectedFiles[i] = null;
			}
		}
		ArrayList<File> fileList = new ArrayList<File>();
		fileList.addAll(Arrays.asList(selectedFiles));
		while (fileList.remove(null));
		this.getList().addAll(fileList);
		this.changed("list");
	}

	public void addToList(File[] selectedFiles) {
		Vector<File> fileList = new Vector<File>();
		fileList.addAll(Arrays.asList(selectedFiles));
		while (fileList.remove(null));
		this.getList().addAll(fileList);
		this.changed("list");
	}

	protected void changed(String anAspect) {
		this.setChanged();
		this.notifyObservers(anAspect);
	}

	public Vector<File> getList() {
		if (list==null) {
			list = new Vector<File>();
		}
		return list;
	}

	public void removeElementsFromList(Object[] toBeRemoved) {
		for(int i=0; i<toBeRemoved.length; i++) {
			this.getList().remove(toBeRemoved[i]);
		}
		this.changed("list");
	}

	public void setList(Vector<File> list) {
		this.list = list;
	}

	public static ConfigurableFileFilter getExcelFileFilter() {
		String[] extensions = {"xls"};
		ConfigurableFileFilter filter = new ConfigurableFileFilter(extensions, "excel");
		return filter;
	}

	public void setUseSequenceOpener(boolean value) {
		this.useSequenceOpener = value;
	}

	public int[] getItemsMatching(String text) {
		Vector<Integer> indices = new Vector<Integer>();
		Iterator<File> it = list.iterator();
		while (it.hasNext()) {
			File current = it.next();
			if (current.getPath().contains(text)) indices.add(new Integer(list.indexOf(current)));
		}
		int[] result = new int[indices.size()];
		int index = 0;
		Iterator<Integer> indexIterator = indices.iterator();
		while (indexIterator.hasNext()) {
			Integer current = indexIterator.next();
			result[index] = current.intValue();
			index++;
		}
		return result;
	}

	public boolean isModal() {
		return modal;
	}

	public void setModal(boolean modal) {
		this.modal = modal;
	}
}
