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
package operations.analysis;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import gui.options.Option;
import ij.gui.NewImage;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import ij.process.ImageProcessor;
import operations.Operation;

/**
 * Find the end points of more or less horizontal lines. The line can have small gaps.
 * The inputs are an image stack and a results table with points. The point i in the 
 * table must be a point on a line in the i-th slice. The operation is used in the 
 * dna tracing application. The results are an ArrayList of line segments and an image
 * on which the line segments are drawn. 
 * 
 * @author	Volker Baecker
 **/
public class FindEndsOperation extends Operation {
	private static final long serialVersionUID = -4446934063805091395L;
	protected ResultsTable centerPoints;
	protected Option maxGapSize;
	protected Option threshold;
	protected ArrayList<Line2D> resultLines;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[2];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterTypes[1] = Class.forName("ij.measure.ResultsTable");
		parameterNames = new String[2];
		parameterNames[0] = "InputImage";
		parameterNames[1] = "CenterPoints";
		optionsNames = new String[2];
		optionsNames[0] = "max. gap size";
		optionsNames[1] = "threshold";
		resultTypes = new Class[2];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultTypes[1] = Class.forName("java.util.ArrayList");
		resultNames = new String[2];
		resultNames[0] = "Result";
		resultNames[1] = "ResultLines";
	}
	
	public ResultsTable getCenterPoints() {
		if (centerPoints==null) centerPoints = Analyzer.getResultsTable();
		return centerPoints;
	}

	public void setCenterPoints(ResultsTable centerPoints) {
		this.centerPoints = centerPoints;
	}

	public float getMaxGapSize() {
		return maxGapSize.getFloatValue();
	}

	public void setMaxGapSize(float maxGapSize) {
		this.maxGapSize.setValue(Float.valueOf(maxGapSize).toString());
	}

	public ArrayList<Line2D> getResultLines() {
		return resultLines;
	}

	public void setResultLines(ArrayList<Line2D> resultLines) {
		this.resultLines = resultLines;
	}

	public float getThreshold() {
		return threshold.getFloatValue();
	}

	public void setThreshold(float threshold) {
		this.threshold.setValue(Float.valueOf(threshold).toString());
	}
	
	protected void setupOptions() {
		super.setupOptions();
		this.setThreshold(0);
		this.setMaxGapSize(5);
	}
	
	public void connectOptions() {
		this.maxGapSize = (Option) this.options.getOptions().get(0);
		this.threshold = (Option) this.options.getOptions().get(1);
	}
	
	protected void cleanUpInput() {
		super.cleanUpInput();
		this.setCenterPoints(null);
	}
	
	public void doIt() {
		result = NewImage.createByteImage(getInputImage().getTitle() + " find ends", getInputImage().getWidth(), 
				getInputImage().getHeight(), 1, NewImage.GRAY8);
		resultLines = new ArrayList<Line2D>();
		ResultsTable centerPoints = this.getCenterPoints();
		for (int i=0; i<centerPoints.getCounter(); i++) {
			double x = centerPoints.getValueAsDouble(ResultsTable.X_CENTROID, i);
			double y = centerPoints.getValueAsDouble(ResultsTable.Y_CENTROID, i);
			Point2D center = new Point2D.Double(x,y);
			Point2D left = this.findLeftEnd(center, i+1);
			Point2D right = this.findRightEnd(center, i+1);
			Line2D aSegment = new Line2D.Float(left,right);
			boolean skip = false;
			for (int j=0; j<resultLines.size(); j++) {
				Line2D currentSegment = resultLines.get(j);
				if (currentSegment.intersectsLine(aSegment)) skip = true;
			}
			if (skip) continue;
			resultLines.add(aSegment);
		}
		this.drawLines();
	}

	public void showResult() {
		result.show();
	}
	
	private void drawLines() {
		for (int i=0; i<resultLines.size();i++) {
			Line2D aLine = resultLines.get(i);
			result.getProcessor().drawLine((int)Math.round(aLine.getX1()), 
										   (int)Math.round(aLine.getY1()), 
										   (int)Math.round(aLine.getX2()), 
										   (int)Math.round(aLine.getY2()));
		}
		
	}

	private Point2D findEnd(Point2D center, int slice, int sign) {
		ImageProcessor ip = inputImage.getStack().getProcessor(slice);
		int x = (int)Math.round(center.getX());
		int y = (int)Math.round(center.getY());
		int lastDir = -1;
		while(x>=0 && y>=0 && x<ip.getWidth() && y<ip.getHeight()) {
			float left = ip.getPixelValue(x+sign,y);
			float up = ip.getPixelValue(x,y-1);
			float down = ip.getPixelValue(x,y+1);
			int dir = 9;
			float value = left;
			if (lastDir != 6 && up>left && up>down) {dir = 0; value = up;}
			if (lastDir != 0 && down>left && down>up) {dir = 6; value = down;}
			if (value>this.getThreshold()) {
				if (dir==9) x = x + sign;
				if (dir==0) y--;
				if (dir==6) y++;
				lastDir = dir;
				continue;
			} 
			for (int i=2; i<this.getMaxGapSize(); i++) {
				value = ip.getPixelValue(x+(sign*i),y);
				if (value>this.getThreshold()) {
					x = x + (sign*i);
					break;
				}
			}
			if (value<=this.getThreshold()) break;
		}
		Point2D result = new Point2D.Float(x,y);
		return result;
	}

	private Point2D findLeftEnd(Point2D center, int slice) {
		return findEnd(center, slice, -1);
	}
	
	private Point2D findRightEnd(Point2D center, int slice) {
		return findEnd(center, slice, 1);
	}
}
