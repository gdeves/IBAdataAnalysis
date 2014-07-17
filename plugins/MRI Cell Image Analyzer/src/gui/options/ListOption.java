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
package gui.options;

import gui.ListEditor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

/**
 * An option of the visual scripting framework . The value of the option is a list
 * of values. The list is edited using the ListEditor.
 * 
 * @author	Volker Baecker
 **/
public class ListOption extends Option {

	private static final long serialVersionUID = -5194832365859277225L;
	protected  ListEditor editor = new ListEditor();
	
	public boolean isList() {
		return true;
	}
	
	public Vector<File> getListValue() {
		return editor.getList();
	}
	
	public String getValue() {
		String result = "";
		Vector<File> listValue = this.getListValue();
		for(File aFile : listValue) {
			result = result + aFile.getAbsolutePath();
			if (listValue.lastElement()!=aFile) result = result + ";";
		}
		return result;
	}
	
	public void setupFrom(BufferedReader in) throws IOException {
		super.setupFrom(in);
		Vector<File> files = new Vector<File>();
		String[] filenames = this.value.split(";");
		if (!filenames[0].equals("")) { 
			for (int i=0; i<filenames.length; i++) {
				files.add(new File(filenames[i]));
			}
		}
		this.editor.setList(files);
	}

	public ListEditor openEditor() {
		editor.show();
		return editor;
	}
	
	public ListEditor getEditor() {
		return editor;
	}
}
