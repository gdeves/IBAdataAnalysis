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
import ij.ImagePlus;
import ij.gui.NewImage;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import operations.Operation;
import utils.LineProcessor;

/**
 * Trace a line in a possibly noisy image, starting from a point on the line. The line 
 * can have gaps. Only lines with a given maximum angle against the horizontal are taken 
 * into account. Only lines with minimal length and a minimum distance from the image
 * borders are taken into account.
 * 
 * @author Volker
 */
public class TraceLinesOperation extends Operation {
	private static final long serialVersionUID = -3104046059535469398L;
	protected ResultsTable startPoints;
	protected Option minLength;
	protected Option maxAngle;
	protected Option minDistanceBorder;
	protected Option minSegmentLength;
	protected ArrayList<Line2D> resultLines;
		
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[2];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterTypes[1] = Class.forName("ij.measure.ResultsTable");
		parameterNames = new String[2];
		parameterNames[0] = "InputImage";
		parameterNames[1] = "StartPoints";
		optionsNames = new String[4];
		optionsNames[0] = "min stick length";
		optionsNames[1] = "max angle";
		optionsNames[2] = "distance border";
		optionsNames[3] = "min segment length";
		resultTypes = new Class[2];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultTypes[1] = Class.forName("java.util.ArrayList");
		resultNames = new String[2];
		resultNames[0] = "Result";
		resultNames[1] = "ResultLines";
	}

	public float getMaxAngle() {
		return maxAngle.getFloatValue();
	}

	public void setMaxAngle(float maxAngle) {
		this.maxAngle.setValue(Float.valueOf(maxAngle).toString());
	}

	public float getMinLength() {
		return minLength.getFloatValue();
	}

	public void setMinLength(float minLength) {
		this.minLength.setValue(Float.valueOf(minLength).toString());
	}

	public ResultsTable getStartPoints() {
		if (startPoints==null) startPoints = Analyzer.getResultsTable();
		return startPoints;
	}

	public void setStartPoints(ResultsTable startPoints) {
		this.startPoints = startPoints;
	}
	
	protected void setupOptions() {
		super.setupOptions();
		this.setMinLength(60);
		this.setMaxAngle(7);
		this.setMinDistanceBorder(60);
		this.setMinSegmentLength(600);
	}
	
	public void connectOptions() {
		this.minLength = (Option) this.options.getOptions().get(0);
		this.maxAngle = (Option) this.options.getOptions().get(1);
		this.minDistanceBorder = (Option) this.options.getOptions().get(2);
		this.minSegmentLength = (Option) this.options.getOptions().get(3);
	}
	
	protected void cleanUpInput() {
		super.cleanUpInput();
		this.setStartPoints(null);
	}

	public ArrayList<Line2D> getResultLines() {
		return resultLines;
	}

	public void setResultLines(ArrayList<Line2D> resultLines) {
		this.resultLines = resultLines;
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		ResultsTable startPoints = this.getStartPoints();
		result = NewImage.createByteImage(inputImage.getTitle(), inputImage.getWidth(), inputImage.getHeight(), 
				1, NewImage.GRAY8);
		resultLines = new ArrayList<Line2D>();
		for (int i=0; i<startPoints.getCounter(); i++) {
			double x = startPoints.getValueAsDouble(ResultsTable.X_CENTROID, i);
			double y = startPoints.getValueAsDouble(ResultsTable.Y_CENTROID, i);
			Point2D center = new Point2D.Double(x,y);
			Point2D left = this.traceLeft(center);
			Point2D right = this.traceRight(center);
			Line2D aSegment = new Line2D.Float(left,right);
			if (this.alreadyFound(aSegment)) continue;
			if (aSegment.getP1().distance(aSegment.getP2())<this.getMinSegmentLength()) continue;
			if (this.nearBorder(aSegment, result)) continue;
			resultLines.add(aSegment);
		}
		drawLines(resultLines, result.getProcessor());
	}

	private boolean nearBorder(Line2D segment, ImagePlus result) {
		Point2D point = new Point2D.Double(segment.getX2(), segment.getY2());
		LineProcessor.movePointIntoDirectionOfBy(point, segment,this.getMinDistanceBorder());
		if (point.getX()>=result.getWidth() ||
			point.getY()>=result.getHeight() ||
			point.getX() < 0 ||
			point.getY() <0) return true;
		point = new Point2D.Double(segment.getX1(), segment.getY1());
		Line2D inversedLine = LineProcessor.inverseLine(segment);
		LineProcessor.movePointIntoDirectionOfBy(point, inversedLine,this.getMinDistanceBorder());
		if (point.getX()>=result.getWidth() ||
				point.getY()>=result.getHeight() ||
				point.getX() < 0 ||
				point.getY() <0) return true;
		return false;
	}

	private boolean alreadyFound(Line2D segment) {
		if (resultLines.size()==0) return false;
		if (resultLines.contains(segment)) return true;
		Line2D lastLine = resultLines.get(resultLines.size()-1);
		if (lastLine.ptLineDist(segment.getP1())<this.minLineDistance() || 
				lastLine.ptLineDist(segment.getP2())<this.minLineDistance()) return true;
		return false;
	}

	public int minLineDistance() {
		return 10;
	}

	private Point2D traceRight(Point2D center) {
		return this.trace(center, true);
	}

	private Point2D traceLeft(Point2D center) {
		return this.trace(center, false);
	}

	private Point2D trace(Point2D center, boolean right) {
		double currentX = center.getX();
		double currentY = center.getY();
		Point2D currentPoint = new Point2D.Double(center.getX(), center.getY());
		double l = this.getMinLength();
		double pi = Math.PI / 180.0;
		while (currentX<inputImage.getWidth() && currentY<inputImage.getHeight() &&
				currentX>0 && currentY>0) {
			double bestAverage = 0;
			double bestEndX = currentX + l;
			if (!right) bestEndX = currentX - l;
			double bestEndY = currentY;
			float turn = 0;
			if (!right) turn = 180; 
			for (float angle = (-this.getMaxAngle() + turn); angle <= (this.getMaxAngle() + turn); angle++) {
				double endX = currentX + (l * Math.cos(angle * (pi)));
				double endY = currentY + (l * Math.sin(angle * (pi)));
				double[] pixelValues = inputImage.getProcessor().getLine(
						currentX, currentY, endX, endY);
				double average = this.getAverage(pixelValues);
				if (average > bestAverage) {
					bestAverage = average;
					bestEndX = endX;
					bestEndY = endY;
				}
			}
			Line2D currentSegment = new Line2D.Double(currentX, currentY,
					bestEndX, bestEndY);
			Line2D perpendicularLine = LineProcessor.perpendicularLine(
					currentSegment, l);
			double[] pixelValues = inputImage.getProcessor().getLine(
					perpendicularLine.getX1(), perpendicularLine.getY1(),
					perpendicularLine.getX2(), perpendicularLine.getY2());
			double threshold = this.getAverage(pixelValues);
			if (bestAverage<=threshold) break;
			LineProcessor.movePointIntoDirectionOf(currentPoint,currentSegment);
			currentX = currentPoint.getX();
			currentY = currentPoint.getY();
		}
		return currentPoint;
	}

	private double getAverage(double[] pixelValues) {
		double result = 0;
		for (int i=0; i<pixelValues.length; i++) {
			result = result + pixelValues[i];
		}
		result = result / pixelValues.length;
		return result;
	}

	public int getMinDistanceBorder() {
		return minDistanceBorder.getIntegerValue();
	}

	public void setMinDistanceBorder(int minDistanceBorder) {
		this.minDistanceBorder.setValue(Integer.valueOf(minDistanceBorder).toString());
	}

	public float getMinSegmentLength() {
		return minSegmentLength.getFloatValue();
	}

	public void setMinSegmentLength(float minSegmentLength) {
		this.minSegmentLength.setValue(Float.valueOf(minSegmentLength).toString());
	}
}
