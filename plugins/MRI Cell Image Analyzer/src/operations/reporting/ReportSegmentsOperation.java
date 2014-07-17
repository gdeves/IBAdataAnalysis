/*
 * Created on 22.08.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package operations.reporting;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import reporting.ExcelIO;
import reporting.ExcelWriter;
import utils.LineProcessor;

import ij.ImagePlus;
import ij.measure.Calibration;

/**
 * @author Volker
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ReportSegmentsOperation extends ReportingOperation {
	/**
	 * 
	 */
	private static final long serialVersionUID = -381821746932551237L;

	protected ArrayList<Line2D> baseSegments;

	protected ArrayList<Line2D> segments;
	
	protected double calibration_factor = 1.0;

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[3];
		parameterTypes[0] = Class.forName("java.util.ArrayList");
		parameterTypes[1] = Class.forName("java.util.ArrayList");
		parameterTypes[2] = Class.forName("java.lang.String");
		parameterNames = new String[3];
		parameterNames[0] = "BaseSegments";
		parameterNames[1] = "Segments";
		parameterNames[2] = "ImageName";
	}
	
	/**
	 * @return Returns the baseSegments.
	 */
	public ArrayList<Line2D> getBaseSegments() {
		return baseSegments;
	}
	/**
	 * @param baseSegments The baseSegments to set.
	 */
	public void setBaseSegments(ArrayList<Line2D> baseSegments) {
		this.baseSegments = baseSegments;
	}
	/**
	 * @return Returns the segments.
	 */
	public ArrayList<Line2D> getSegments() {
		return this.segments;
	}
	/**
	 * @param segments The segments to set.
	 */
	public void setSegments(ArrayList<Line2D> segments) {
		this.segments = segments;
	}
	
	protected void cleanUpInput() {
		super.cleanUpInput();
		this.setBaseSegments(null);
		this.setSegments(null);
	}
	
	public void doIt() {
		Calibration calibration = (new ImagePlus()).getCalibration();
		if (calibration==null) 
			calibration_factor = 1.0; 
		else 
			calibration_factor = calibration.pixelWidth;
	    setupOutputPath();
		ExcelIO excelIO = this.loadOrCreateWorkbook(3);
		ExcelWriter excelProxy = new ExcelWriter();
		excelProxy.setWb(excelIO.wb);
		excelProxy.setSheet(excelIO.s);
		excelProxy.setSheet(excelIO.wb.getSheetAt(0));
		this.createHeadingsFirstSheet(excelProxy, excelIO);
		this.createHeadingsSecondSheet(excelProxy, excelIO);
		this.createHeadingsThirdSheet(excelProxy, excelIO);
		this.createHeadingsFourthSheet(excelProxy, excelIO);
		 if (this.getBaseSegments().size()==0) {
		 	this.reportImageTreated(excelIO, excelProxy);
		 }
		for (int i=0; i<this.getBaseSegments().size(); i++) {
			Line2D base = (Line2D) this.getBaseSegments().get(i);
			Vector<Line2D> containedSegments = this.getContainedSegments(i);
			this.reportBaseSegment(excelIO, excelProxy, base, i);
			this.reportContainedSegments(excelIO, excelProxy, base, containedSegments);
			this.reportSubsegmentDistances(excelIO, excelProxy, containedSegments, base);
		}
		this.saveWorkbook(excelIO.wb);
	}

	/**
	 * @param excelIO
	 * @param excelProxy
	 */
	private void reportImageTreated(ExcelIO excelIO, ExcelWriter excelProxy) {
		excelProxy.setSheet(excelIO.wb.getSheetAt(0));
		String row = this.getImageName() + "\t";
		row = row + "0" + "\t";
		row = row + " \t";
		row = row + " \t";
		row = row + " \t";
		row = row + " \t";
		row = row + " \t";
		row = row + " \t";
		row = row + " \t";
		row = row + " \t";
		row = row + " \t";
		row = row + " \t";
		row = row + " \t";
		row = row + this.getImageFolder();
		excelProxy.writeTabSeparatedDataAsRow(row);
		excelProxy.setSheet(excelIO.wb.getSheetAt(1));
		row = "\t";
		excelProxy.writeTabSeparatedDataAsRow(row);
		excelProxy.setSheet(excelIO.wb.getSheetAt(2));
		row = "\t";
		excelProxy.writeTabSeparatedDataAsRow(row);
		excelProxy.setSheet(excelIO.wb.getSheetAt(3));
		row = "\t";
		excelProxy.writeTabSeparatedDataAsRow(row);
	}
	
	/**
	 * @return
	 */
	protected String getSheetName(int i) {
		String[] names = {"DNA combing", "green segments", "lengths", "distances"};
		return names[i];
	}

	/**
	 * @param excelProxy
	 * @param excelIO
	 */
	private void createHeadingsFourthSheet(ExcelWriter excelProxy, ExcelIO excelIO) {
		excelProxy.setSheet(excelIO.wb.getSheetAt(3));
		excelIO.wb.setSheetName(3, this.getSheetName(3));
		if (continueExistingFile) return;
		String line = "";
		for (int i=1; i<13; i++) {
			line = line + i + "-" + (i+1) + "\t";
		}
		excelProxy.writeTabSeparatedColumnHeadings(line);
	}


	/**
	 * 
	 */
	private void reportSubsegmentDistances( ExcelIO excelIO, ExcelWriter excelProxy, Vector<Line2D> containedSegments, Line2D base) {
		excelProxy.setSheet(excelIO.wb.getSheetAt(3));
		String line = "";
		if (containedSegments.size()<2) {
			excelProxy.writeTabSeparatedDataAsRow(line);	
			return;
		}
		for (int i=0; i<containedSegments.size()-1; i++) {
			Line2D segment1 = containedSegments.elementAt(i);
			Line2D segment2 = containedSegments.elementAt(i+1);
			if (this.isStartSegment(segment1, base) || this.isEndSegment(segment2, base)) {
				line = line + "-\t";
				continue;
			}
			Point2D middle1 = LineProcessor.middlePointOf(segment1);
			Point2D middle2 = LineProcessor.middlePointOf(segment2);
			float distance = (float) middle1.distance(middle2);
			line = line + distance * calibration_factor + "\t";
		}
		excelProxy.writeTabSeparatedDataAsRow(line);	
	}


	/**
	 * @param segment1
	 * @return
	 */
	private boolean isStartSegment(Line2D segment1, Line2D base) {
		boolean result = segment1.getX1() <= base.getX1();
		return result;
	}

	/**
	 * @param segment2
	 * @return
	 */
	private boolean isEndSegment(Line2D segment2, Line2D base) {
		boolean result = segment2.getX2() >= base.getX2();
		return result;
	}

	/**
	 * @return
	 */
	protected String headingsSecondSheet() {
		String line = "";
		for (int i=1; i<13; i++) {
			line = line + i + "\t";
		}
		return line;
	}

	/**
	 * 
	 */
	private void createHeadingsThirdSheet(ExcelWriter excelProxy, ExcelIO excelIO) {
		excelProxy.setSheet(excelIO.wb.getSheetAt(2));
		excelIO.wb.setSheetName(2, this.getSheetName(2));
		if (continueExistingFile) return;
		String line = this.headingsLengthsSheet();
		excelProxy.writeTabSeparatedColumnHeadings(line);
	}
	
	/**
	 * @param excelIO
	 * @param excelProxy
	 * @param base
	 * @param containedSegments
	 */
	private void reportContainedSegments(ExcelIO excelIO, ExcelWriter excelProxy, Line2D base, Vector<Line2D> containedSegments) {
		excelProxy.setSheet(excelIO.wb.getSheetAt(1));
		String line = "";
		for (int i=0; i<containedSegments.size(); i++) {
			Line2D segment =  containedSegments.elementAt(i);
			line = line + segment.getX1() + ", " + segment.getY1() + ", " + segment.getX2() + ", " + segment.getY2() + "\t";	
		}
		excelProxy.writeTabSeparatedDataAsRow(line);
		excelProxy.setSheet(excelIO.wb.getSheetAt(2));
		Vector<Line2D> allSegments = this.computeAllSegments(containedSegments, base);
		line = "";
		if (!this.startsWithRed(containedSegments, base)) {
			line = line + "-\t";
		}
		for (int i=0; i<allSegments.size(); i++) {
			Line2D segment =  allSegments.elementAt(i);
			line = line + segment.getP2().distance(segment.getP1()) * calibration_factor + "\t";	
		}
		excelProxy.writeTabSeparatedDataAsRow(line);		
	}


	/**
	 * @return
	 */
	private String headingsLengthsSheet() {
		String headings = "red\tgreen\tred\tgreen\tred\tgreen\tred\tgreen\tred\tgreen\tred\tgreen\tred\tgreen\tred\tgreen\tred\tgreen\tred\tgreen\tred\tgreen\tred\tgreen";
		return headings;
	}


	/**
	 * @param containedSegments
	 * @param base
	 * @return
	 */
	private boolean startsWithRed(Vector<Line2D> containedSegments, Line2D base) {
		if (containedSegments.size()==0) return true;
		Line2D subsegment = containedSegments.firstElement();
		return base.getX1() < subsegment.getX1();
	}

	/**
	 * @param line2D
	 * @param base
	 * @return
	 */
	private boolean endsWithRed(Vector<Line2D> containedSegments, Line2D base) {
		if (containedSegments.size()==0) return true;
		Line2D subsegment = containedSegments.lastElement();
		return base.getX2() > subsegment.getX2();
	}
	
	/**
	 * @param containedSegments
	 * @param base
	 * @return
	 */
	private Vector<Line2D> computeAllSegments(Vector<Line2D> containedSegments, Line2D base) {
		Vector<Line2D> results = new Vector<Line2D>();
		if (containedSegments.size()==0) {
			results.add(base);
			return results;
		}
		Line2D segment = containedSegments.firstElement();
		if (this.startsWithRed(containedSegments, base)) {
			Line2D first = new Line2D.Float(base.getP1(), segment.getP1());
			results.add(first);
		}
		for (int i=0; i<containedSegments.size(); i++) {
			segment = containedSegments.elementAt(i);
			results.add(segment);
			if (i<(containedSegments.size()-1)) {
				Line2D nextSegment = containedSegments.elementAt(i+1);
				segment = new Line2D.Float(segment.getP2(), nextSegment.getP1());
				results.add(segment);
			}
		}
		if (this.endsWithRed(containedSegments, base)) {
			 segment = containedSegments.lastElement();
			Line2D last = new Line2D.Float(segment.getP2(), base.getP2());
			results.add(last);
		}
		return results;
	}


	/**
	 * @param excelIO
	 * @param excelProxy
	 * @param i
	 */
	private void reportBaseSegment(ExcelIO excelIO, ExcelWriter excelProxy, Line2D base, int i) {
		excelProxy.setSheet(excelIO.wb.getSheetAt(0));
		Vector<Line2D> containedSegments = this.getContainedSegments(i);
		double totalLength = base.getP1().distance(base.getP2());
		double totalLengthGreen = this.getTotalLengthGreen(containedSegments);
		double percentGreen = (totalLengthGreen * 100.0) / (totalLength * 1.0);
		double averageLengthGreen = this.getAverageLengthGreen(containedSegments, base);
		double medianLengthGreen = this.getMedianLengthGreen(containedSegments, base);
		double averageDistanceGreen = this.getAverageDistanceGreen(containedSegments, base);
		double medianDistanceGreen = this.getMedianDistanceGreen(containedSegments, base);
		String row = this.getImageName() + "\t";
		row = row + (i+1) + "\t";
		row = row + base.getX1() + "\t";
		row = row + base.getY1() + "\t";
		row = row + base.getX2() + "\t";
		row = row + base.getY2() + "\t";
		row = row + totalLength * calibration_factor + "\t";
		row = row + totalLengthGreen * calibration_factor + "\t";
		row = row + percentGreen + "\t";
		row = row + averageLengthGreen * calibration_factor + "\t";
		row = row + medianLengthGreen * calibration_factor + "\t";
		row = row + averageDistanceGreen * calibration_factor + "\t";
		row = row + medianDistanceGreen * calibration_factor + "\t";
		row = row + this.getImageFolder();
		excelProxy.writeTabSeparatedDataAsRow(row);
		excelProxy.setSheet(excelIO.wb.getSheetAt(1));
	}


	/**
	 * @param containedSegments
	 * @param base
	 * @return
	 */
	private double getMedianDistanceGreen(Vector<Line2D> containedSegments, Line2D base) {
		double result = 0.0;
		if (containedSegments.size()<2) {	
			return result;
		}
		Vector<Double> distances = new Vector<Double>();
		for (int i=0; i<containedSegments.size()-1; i++) {
			Line2D segment1 = containedSegments.elementAt(i);
			Line2D segment2 = containedSegments.elementAt(i+1);
			if (this.isStartSegment(segment1, base) || this.isEndSegment(segment2, base)) {
				continue;
			}
			Point2D middle1 = LineProcessor.middlePointOf(segment1);
			Point2D middle2 = LineProcessor.middlePointOf(segment2);
			double distance = (double) middle1.distance(middle2);
			distances.add(Double.valueOf(distance));
		}	
		if (distances.size()==0) return result;
		result = this.getMedian(distances);
		return result;
	}

	/**
	 * @param containedSegments
	 * @param base
	 * @return
	 */
	private double getAverageDistanceGreen(Vector<Line2D> containedSegments, Line2D base) {
		double result = 0;
		double count = 0.0;
		if (containedSegments.size()<2) {	
			return result;
		}
		for (int i=0; i<containedSegments.size()-1; i++) {
			Line2D segment1 = containedSegments.elementAt(i);
			Line2D segment2 = containedSegments.elementAt(i+1);
			if (this.isStartSegment(segment1, base) || this.isEndSegment(segment2, base)) {
				continue;
			}
			Point2D middle1 = LineProcessor.middlePointOf(segment1);
			Point2D middle2 = LineProcessor.middlePointOf(segment2);
			double distance = (double) middle1.distance(middle2);
			result += distance;
			count++;
		}	
		if (count==0) return 0;
		result = result / (count * 1.0);
		return result;
	}

	/**
	 * @param containedSegments
	 * @param base
	 * @return
	 */
	private double getMedianLengthGreen(Vector<Line2D> containedSegments, Line2D base) {
		double result = 0;
		Vector<Double> lengths = new Vector<Double>();
		Iterator<Line2D> it = containedSegments.iterator();
		if (containedSegments.size()==0) return result;
		while (it.hasNext()) {
			Line2D segment = it.next();
			if (this.isStartSegment(segment, base) || this.isEndSegment(segment, base)) continue;
			Double size = Double.valueOf(segment.getP2().distance(segment.getP1()));
			lengths.add(size);
		}
		if (lengths.size()==0) return result;
		result = getMedian(lengths);
		return result;
	}

	/**
	 * @param lengths
	 * @return
	 */
	private double getMedian(Vector<Double> values) {
		double result;
		Collections.sort(values);
		if (values.size()%2==1) {
			result = values.elementAt((values.size()/2)).doubleValue();
		} else {
			int index = values.size()/2;
			double first = values.elementAt(index).doubleValue();
			double second = values.elementAt(index - 1).doubleValue();
			result = (first + second) / 2.0;
		}
		return result;
	}

	/**
	 * @param containedSegments
	 * @return
	 */
	private double getAverageLengthGreen(Vector<Line2D> containedSegments, Line2D base) {
		double result = 0;
		double count = 0;
		Iterator<Line2D> it = containedSegments.iterator();
		while (it.hasNext()) {
			Line2D segment = it.next();
			if (this.isStartSegment(segment, base) || this.isEndSegment(segment, base)) continue;
			result += segment.getP2().distance(segment.getP1());
			count++;
		}
		if (count==0) return 0;
		result = result / count;
		return result;
	}

	/**
	 * @param containedSegments
	 * @return
	 */
	private double getTotalLengthGreen(Vector<Line2D> containedSegments) {
		double result = 0;
		Iterator<Line2D> it = containedSegments.iterator();
		while(it.hasNext()) {
			Line2D segment = it.next();
			result += segment.getP2().distance(segment.getP1());
		}
		return result;
	}

	/**
	 * @return
	 */
	protected String headingsFirstSheet() {
		String headings = "image\tdna brin nr.\tstart x\tstart y\tend x\tend y\ttotal length\ttotal length green\t% green\tav. length green\tmedian length green\tav. dist. green\tmedian dist. green\tfolder";
		return headings;
	}


	/**
	 * @return
	 */
	private Vector<Line2D> getContainedSegments(int index) {
		Line2D base = (Line2D) this.getBaseSegments().get(index);
		Vector<Line2D> result = new Vector<Line2D>();
		for (int i=0; i<this.getSegments().size(); i++) {
			Line2D line = (Line2D) this.getSegments().get(i);
			if (base.ptLineDist(line.getP1())<10 || base.ptLineDist(line.getP2())<10 ) {
				result.add(line);
			}
		}
		LineComparator comparator = new LineComparator();
		Collections.sort(result, comparator);
		return result;
	}
}
