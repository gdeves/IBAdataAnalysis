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
package tools.frap;

import gui.ListEditor;

import java.io.File;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.filechooser.FileFilter;

/**
 *  Adaptation of the ListEditor that allows to add folders only and
 *  not the files within the folders.
 *  
 * @author	Volker Baecker
 **/
public class FolderListEditor extends ListEditor {

	public void addToList(File[] selectedFiles, FileFilter filter) {
		for(int i=0; i<selectedFiles.length;i++) {
			File aFile = selectedFiles[i];
			if (!aFile.isDirectory()) {
				selectedFiles[i] = null;
				continue;
			}
		}
		Vector<File> fileList = new Vector<File>();
		fileList.addAll(Arrays.asList(selectedFiles));
		while (fileList.remove(null));
		this.getList().addAll(fileList);
		this.changed("list");
	}
}
