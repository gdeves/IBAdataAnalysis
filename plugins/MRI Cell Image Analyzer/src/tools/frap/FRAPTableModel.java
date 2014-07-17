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
package tools.frap;

import ij.ImagePlus;
import ij.gui.Roi;
import ij.io.RoiDecoder;
import ij.io.RoiEncoder;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import operations.file.OpenImageOperation;

/**
 * The table model for the frap wizard. The table has the three columns
 * nr, folder and selection. When a row is selected the according 
 * time-series of projections is loaded. If a selection of the region
 * in which to search the max. cube has been made it is loaded as well. 
 * 
 * @author	Volker Baecker
 **/
public class FRAPTableModel extends AbstractTableModel implements TableModel {

	private static final long serialVersionUID = 8189869595698584985L;
	
	protected Vector<Integer> number;
	protected Vector<File> folderList;
	protected Vector<Roi> selectionList;
	protected String[] columnNames;

	public FRAPTableModel() {
		number = new Vector<Integer>();
		folderList = new Vector<File>();
		selectionList = new Vector<Roi>();
		this.fireTableRowsInserted(0, folderList.size()-1);
		this.fireTableDataChanged();
	}
	
	public int getColumnCount() {
		return 3;
	}

	public int getRowCount() {
		return folderList.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex==0) return number.elementAt(rowIndex);
		if (columnIndex==1) return folderList.elementAt(rowIndex);
		if (columnIndex==2) return selectionList.elementAt(rowIndex);
		return null;
	}

	public void constructFrom(Vector<File> folderList) {
		FileComparator comparator = new FileComparator();
		Collections.sort(folderList, comparator);
		this.number = new Vector<Integer>();
		this.folderList = folderList;
		this.selectionList = new Vector<Roi>();
		Iterator<File> it = this.folderList.iterator();
		int counter = 1;
		while(it.hasNext()) {
			File folder = it.next();
			Roi selection = this.readSelection(folder);
			selectionList.add(selection);
			number.add(new Integer(counter));
			counter++;
		}
		this.fireTableDataChanged();
	}
	
	protected Roi readSelection(File folder) {
		Roi result = null;
		String roiFileName = folder.getAbsolutePath() + File.separator + folder.getName() + ".sel";
		File roiFile = new File(roiFileName);
		if (roiFile.exists()) {
			RoiDecoder decoder = new RoiDecoder(roiFile.getAbsolutePath());
			try {
				result = decoder.getRoi();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public String getColumnName(int index) {
		return this.columnNames()[index];
	}

	public Class<?> getColumnClass(int index) {
		Class<?>[] classes = {Integer.class, File.class, Roi.class};
		return classes[index];
	}
	
	public String[] columnNames() {
		if (this.columnNames==null) {
			this.columnNames = new String[3];
			columnNames[0] = "nr";
			columnNames[1] = "folder";
			columnNames[2] = "selection";
		}
 		return columnNames;
	}
	
	public File getFolderForIndex(int index) {
		return (File)(this.getValueAt(index, 1));
	}
	
	public void setSelectionForIndexTo(int index, Roi selection) {
		this.selectionList.setElementAt(selection, index);
		File folder = this.getFolderForIndex(index);
		String filename = folder.getAbsolutePath() + File.separator + folder.getName() + ".sel";
		RoiEncoder encoder = new RoiEncoder(filename);
		try {
			encoder.write(selection);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.fireTableDataChanged();
	}
	
	public Roi getSelectionForIndex(int index) {
		return (Roi)(this.getValueAt(index, 2));
	}
	
	public int getNumberOfSelections() {
		Iterator<Roi> it = this.selectionList.iterator();
		int counter = 0;
		while(it.hasNext()) {
			Roi aRoi = it.next();
			if (aRoi!=null) counter++;
		}
		return counter;
	}

	public Vector<Roi> getSelections() {
		return selectionList;
	}
	
	public ImagePlus loadImage(String path) {
		OpenImageOperation open = new OpenImageOperation();
		open.setShowResult(false);
		open.setPath(path);
		open.run();
		ImagePlus image = open.getResult();
		return image;
	}

	public void remove(int[] indices) {
		for (int i=0; i<indices.length; i++) {
			int index = indices[i];
			number.remove(index-i);
			folderList.remove(index-i);
			selectionList.remove(index-i);
		}
	}
}
