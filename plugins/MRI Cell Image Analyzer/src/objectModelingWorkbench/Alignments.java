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
package objectModelingWorkbench;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.Observer;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import applications.Application;

/**
 * The alignments corresponding to an image series. Basically manages a list of alignment 
 * slices and maps the fields of the AlignmentSlices to a table model.
 * 
 * @author Volker B�cker
 */
public class Alignments extends AbstractTableModel implements TableModel {
	private static final long serialVersionUID = -2690300221461261456L;
	protected ArrayList<AlignmentSlice> alignments;
	protected Application application;
	
	public Alignments() {
		super();
	}

	public Alignments(ArrayList<AlignmentSlice> theAlignments) {
		super();
		this.alignments = theAlignments;
		this.fireTableRowsInserted(0, theAlignments.size()-1);
		this.fireTableDataChanged();
	}
	
	public int getRowCount() {
		return this.getAlignments().size();
	}

	public int getColumnCount() {
		return 5;
	}

	public String getColumnName(int index) {
		String[] columnNames = {"nr.", "x", "y", "angle", "score"};
		return columnNames[index];
	}

	public Class<?> getColumnClass(int index) {
		Class<?>[] classes = {Integer.class, Integer.class, Integer.class, Integer.class, Double.class};
		return classes[index];
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == 0) return false; 
		return true;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Object result = null;
		AlignmentSlice row = (AlignmentSlice) this.getAlignments().get(rowIndex);
		if (columnIndex == 0) return new Integer(rowIndex+1);
		if (columnIndex == 1) return new Integer(row.getXOffset());
		if (columnIndex == 2) return new Integer(row.getYOffset());
		if (columnIndex == 3) return new Integer(row.getAngle());
		if (columnIndex == 4) return new Double(row.getScore());
		return result;
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (columnIndex==0) return;
		if (columnIndex==1) ((AlignmentSlice)(this.getAlignments().get(rowIndex))).setXOffset((Integer)aValue);
		if (columnIndex==2) ((AlignmentSlice)(this.getAlignments().get(rowIndex))).setYOffset((Integer)aValue);
		if (columnIndex==3) ((AlignmentSlice)(this.getAlignments().get(rowIndex))).setAngle((Integer)aValue);
		if (columnIndex==4) ((AlignmentSlice)(this.getAlignments().get(rowIndex))).setScore((Double)aValue);
		fireTableCellUpdated(rowIndex, columnIndex);
	}

	public ArrayList<AlignmentSlice> getAlignments() {
		if (alignments==null) {
			alignments = new ArrayList<AlignmentSlice>();
		}
		return alignments;
	}

	public void setAlignments(ArrayList<AlignmentSlice> alignments) {
		this.alignments = alignments;
		this.fireTableDataChanged();
	}

	public Rectangle calculateImageSize() {
		if (this.getAlignments().isEmpty()) return null;
		int minX=999999, maxX=-999999, minY=999999, maxY = -999999;
		ArrayList<AlignmentSlice> slices = this.getAlignments();
		for(AlignmentSlice currentSlice : slices) {
			Rectangle currentBounds = currentSlice.bounds();
			int leftX = currentBounds.x;
			int rightX = leftX + currentBounds.width - 1;
			int upY = currentBounds.y;
			int downY = currentBounds.y + currentBounds.height - 1;
			if (leftX < minX) minX = leftX;
			if (rightX > maxX) maxX = rightX;
			if (upY < minY) minY = upY;
			if (downY > maxY) maxY = downY;
		}
		Rectangle result = new Rectangle(minX, minY, (maxX-minX)+1, (maxY-minY)+1);
		return result;
	}
	
	public void saveWithSize(File targetFile, Rectangle zeroAndimageSize, boolean applyTranslations) {
		TreatAndSaveSlices saver = new TreatAndSaveSlices(this.getAlignments(), targetFile, zeroAndimageSize, this.getApplication(), applyTranslations);
		saver.setShowProgress(true);
		saver.execute();
	}

	public void scale(File targetFile, Observer observer) {
		ScaleSlices scale = new ScaleSlices(this.getAlignments(), targetFile, this.getApplication());
		scale.setShowProgress(true);
		if (observer!=null) scale.addObserver(observer);
		scale.execute();
	}
	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}
}
