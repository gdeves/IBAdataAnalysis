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

import java.awt.geom.Point2D;

import gui.MeasurementsList;
import ij.IJ;
import ij.measure.ResultsTable;
import operations.Operation;

/** 
 *  Loop through a results table. In each iteration the centroid of the current row is
 *  accessible as currentPoint.
 *  
 * @author Volker Baecker
 */
 
public class ForeachObjectDoOperation extends Operation {
	private static final long serialVersionUID = 2522273368281610832L;
	protected ResultsTable objects;
	protected int currentIndex = 0;
	protected Boolean atEnd = new Boolean(false);
	protected Point2D currentPoint;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.measure.ResultsTable");
		parameterNames = new String[1];
		parameterNames[0] = "Objects";
		resultTypes = new Class[4];
		resultTypes[0] = Class.forName("java.awt.geom.Point2D");
		resultTypes[1] = Class.forName("java.lang.Boolean");
		resultTypes[2] = Class.forName("operations.control.ForeachObjectDoOperation");
		resultTypes[3] = Class.forName("java.lang.Integer");
		resultNames = new String[4];
		resultNames[0] = "CurrentPoint";
		resultNames[1] = "AtEnd";
		resultNames[2] = "DoOperation";
		resultNames[3] = "Index";
	}
	
	public Integer getIndex() {
		return new Integer(currentIndex);
	}
	
	public void setIndex(Integer anIndex) {
		// noop
	}
	
	public ForeachObjectDoOperation getDoOperation() {
		return this;
	}

	public void setDoOperation(ForeachObjectDoOperation op) {
	}

	public Boolean getAtEnd() {
		return atEnd;
	}

	public void setAtEnd(Boolean atEnd) {
		this.atEnd = atEnd;
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}

	public Point2D getCurrentPoint() {
		return currentPoint;
	}

	public void setCurrentPoint(Point2D currentPoint) {
		this.currentPoint = currentPoint;
	}

	public ResultsTable getObjects() {
		if (application==null && objects==null) {
			objects = Operation.copySystemResultsTable();
		}
		return objects;
	}

	public void setObjects(ResultsTable objects) {
		this.objects = objects;
	}
	
	public void doIt() {
		if (application==null) {
			objects = null;
			return;
		}
		if (this.getObjects().getCounter()==0) {
			this.setAtEnd(new Boolean(true));
			return;
		}
		String message = "Iteration " + (currentIndex+1) + "/" + this.getObjects().getCounter();
		IJ.showStatus(message);
		float x = this.getObjects().getColumn(ResultsTable.X_CENTROID)[currentIndex];
		float y = this.getObjects().getColumn(ResultsTable.Y_CENTROID)[currentIndex];
		this.setCurrentPoint(new Point2D.Float(x,y));
		this.setAtEnd(new Boolean(false));
		if (currentIndex==this.getObjects().getCounter()-1) {
			this.setAtEnd(new Boolean(true));
		}
	}
	
	public boolean next() {
		if (this.getAtEnd().booleanValue()) return false;
		currentIndex++;
		return true;
	}
	
	public void reset() {
		this.setAtEnd(new Boolean(false));
		this.currentIndex = 0;
	}
	
	public void showResult() {
		(new MeasurementsList(this.getObjects())).show();
	}
	
	public boolean isLoop() {
		return true;
	}
}
