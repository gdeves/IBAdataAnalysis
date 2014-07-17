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
package operations.lines;
import gui.options.Option;
import ij.gui.NewImage;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import operations.Operation;
import utils.LineProcessor;

/**
 * Scan the image along the given lines. Stop when no point above threshold is found within a range of 6 pixel 
 * perpendicular to the line. Segments along a line are merged if the gap size is smaller than 
 * minConnected*gapFactor and if the length of the segment is at least minConnected.  
 * 
 * @author Volker Bäcker
 */
public class CutLinesToSizeOperation extends Operation {
	private static final long serialVersionUID = 3955988323282636540L;
	ArrayList<Line2D> lines;
	Option minConnected;
	Option gapFactor;
	Option threshold;
	ArrayList<Line2D> resultLines;

	public CutLinesToSizeOperation() {
		super();
		resultLines = new ArrayList<Line2D>();
	}
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[2];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterTypes[1] = Class.forName("java.util.ArrayList");
		parameterNames = new String[2];
		parameterNames[0] = "InputImage";
		parameterNames[1] = "Lines";
		optionsNames = new String[3];
		optionsNames[0] = "min connected";
		optionsNames[1] = "gap factor";
		optionsNames[2] = "threshold";
		resultTypes = new Class[2];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultTypes[1] = Class.forName("java.util.ArrayList");
		resultNames = new String[2];
		resultNames[0] = "Result";
		resultNames[1] = "ResultLines";
		
	}

	public ArrayList<Line2D> getResultLines() {
		return resultLines;
	}

	public void setResultLines(ArrayList<Line2D> resultLines) {
		this.resultLines = resultLines;
	}

	public double getGapFactor() {
		return gapFactor.getDoubleValue();
	}

	public void setGapFactor(double gapFactor) {
		this.gapFactor.setValue(new Double(gapFactor).toString());
	}

	public double getThreshold() {
		return threshold.getDoubleValue();
	}

	public void setThreshold(double threshold) {
		this.threshold.setValue(new Double(threshold).toString());
	}

	public int getMinConnected() {
		return minConnected.getIntegerValue();
	}

	public void setMinConnected(int minConnected) {
		this.minConnected.setValue(new Integer(minConnected).toString());
	}

	public ArrayList<Line2D> getLines() {
		return lines;
	}

	public void setLines(ArrayList<Line2D> lines) {
		this.lines = lines;
	}
	
	protected void setupOptions() {
		super.setupOptions();
		this.setMinConnected(3);
		minConnected.setShortHelpText("The minimal number of connected pixel that are counted as a segment.");
		this.setGapFactor(1.5);
		gapFactor.setShortHelpText("minConnected*gapFactor gives the maximal size of gaps that are ignored.");
		this.setThreshold(13);
		threshold.setShortHelpText("If no pixel above threshold is found next to the last position the tracing stops.");
	}
	
	public void connectOptions() {
		this.minConnected = (Option) this.options.getOptions().get(0);
		this.gapFactor = (Option) this.options.getOptions().get(1);
		this.threshold = (Option) this.options.getOptions().get(2);
	}
	
	public void doIt() {
		result = NewImage.createByteImage(inputImage.getTitle(), inputImage.getWidth(), inputImage.getHeight(), 
				inputImage.getStackSize(), NewImage.GRAY8);
		resultLines = new ArrayList<Line2D>();
		for (int i=0; i<this.lines.size(); i++) {
			Line2D aLine = (Line2D) lines.get(i);
			this.cutLineToSize(aLine);
		}
		this.drawLines();
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

	private void cutLineToSize(Line2D line) {
		ArrayList<Line2D> whiteSegments = new ArrayList<Line2D>();
		Point2D currentPoint = line.getP1();
		boolean lastWasWhite = false;
		Point2D start = null;
		if (this.isAboveThreshold(currentPoint, line)) {
			lastWasWhite = true;
			start = new Point2D.Double(currentPoint.getX(),currentPoint.getY());
		}
		Point2D lastPoint = new Point2D.Double(currentPoint.getX(), currentPoint.getY());
		boolean finishedSegment = true;
		while(LineProcessor.movePointAlongLine(currentPoint, line)) {
			if (this.isAboveThreshold(currentPoint, line)){
				if (!lastWasWhite) {
					start = new Point2D.Double(currentPoint.getX(),currentPoint.getY());
					finishedSegment = false;
				}
				lastWasWhite = true;
			} else {
				if (lastWasWhite) {
					whiteSegments.add(new Line2D.Float(start, lastPoint));
					finishedSegment = true;
				}
				lastWasWhite = false;
			}
			lastPoint = new Point2D.Double(currentPoint.getX(), currentPoint.getY());
		}
		if (!finishedSegment) {
			whiteSegments.add(new Line2D.Float(start, lastPoint));
		}
		if (whiteSegments.size()==1) {
			resultLines.add(whiteSegments.get(0));
			return;
		}
		ArrayList<Line2D> resultSegments = whiteSegments;
		for (int i=0; i<1; i++) {
			if (resultSegments.size()<=1) break;
			resultSegments = mergeAllConnected(resultSegments);
		}
			if (resultSegments.size()>0) {
			resultLines.add(new Line2D.Float(resultSegments.get(0).getP1(), 
											 resultSegments.get(resultSegments.size()-1).getP2()));
			}
	}

	private ArrayList<Line2D> mergeAllConnected(ArrayList<Line2D> whiteSegments) {
		boolean[] connected = new boolean[whiteSegments.size()-1];
		for (int i=0; i<whiteSegments.size()-1; i++) {
			Line2D aSegment = whiteSegments.get(i);
			Line2D nextSegment = whiteSegments.get(i+1);
			double length = aSegment.getP1().distance(aSegment.getP2());
			double gapLength = aSegment.getP2().distance(nextSegment.getP1());
			if ((length >= minConnected.getDoubleValue()) && (gapLength<=(gapFactor.getDoubleValue()*length))) {
				connected[i] = true;
			} else {
				connected[i] = false;
			}
		}
		ArrayList<Line2D> resultSegments = this.mergeConnected(whiteSegments, connected);
		return resultSegments;
	}

	private ArrayList<Line2D> mergeConnected(ArrayList<Line2D> whiteSegments, boolean[] connected) {
		ArrayList<Line2D> result = new ArrayList<Line2D>();
		Point2D start = (whiteSegments.get(0)).getP1();
		Point2D end = (whiteSegments.get(0)).getP2();
		for (int i=0; i<connected.length;i++) {
			if (connected[i]) {
				end = (whiteSegments.get(i+1)).getP2();
				if (i==connected.length-1) {
					result.add(new Line2D.Float(start,end));
				}
			} else {
				if (!(i==0 || !connected[i-1])) { 
				 result.add(new Line2D.Float(start,end));
				}
				start = (whiteSegments.get(i+1)).getP1();
				end = (whiteSegments.get(i+1)).getP2();
			}
		}
		return result;
	}

	private boolean isAboveThreshold(Point2D currentPoint, Line2D line) {
		Line2D perpendicularSegment = LineProcessor.perpendicularLine(new Line2D.Float(currentPoint, line.getP2()), 6);
		double[] pixelValues = inputImage.getProcessor().getLine(perpendicularSegment.getX1(), 
																 perpendicularSegment.getY1(), 
																 perpendicularSegment.getX2(), 
																 perpendicularSegment.getY2());
		for (int i=0; i<pixelValues.length; i++) {
			if(pixelValues[i]>threshold.getDoubleValue()) return true;
		}
		return false;
	}
	
	protected void cleanUpInput() {
		super.cleanUpInput();
		this.setLines(null);
	}
}
