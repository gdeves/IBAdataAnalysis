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
import ij.ImagePlus;
import ij.gui.NewImage;
import java.util.ArrayList;
import java.awt.geom.Line2D;
import operations.Operation;

/**
 * Sweeps the image with lines of inclinations between -angle to angle and counts the pixel above threshold. If
 * the count is above count threshold a line is detected. 
 * 
 * @author Volker Bäcker
 */
public class ScanForStraightLinesOperation extends Operation {
	private static final long serialVersionUID = -2223855221596281921L;
	Option threshold;
	Option maxAngle;
	Option countThreshold;
	ArrayList<Line2D> resultLines;

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		optionsNames = new String[3];
		optionsNames[0] = "threshold";
		optionsNames[1] = "max angle";
		optionsNames[2] = "count threshold";
		resultTypes = new Class[2];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultTypes[1] = Class.forName("java.util.ArrayList");
		resultNames = new String[2];
		resultNames[0] = "Result";
		resultNames[1] = "ResultLines";
		
	}
	
	public void doIt() {
		resultLines = new ArrayList<Line2D>();
		ImagePlus inputImage = this.getInputImage();
		result = NewImage.createByteImage(inputImage.getTitle(), inputImage.getWidth(), inputImage.getHeight(), 
				inputImage.getStackSize(), NewImage.GRAY8);
		int startX = 0;
		int endX = inputImage.getWidth();
		for (int startY=0; startY<inputImage.getHeight();startY++) {
			for (int angle = -maxAngle.getIntegerValue(); angle<= maxAngle.getIntegerValue(); angle++) {
				int endY = (int)Math.round((Math.sin(angle*(Math.PI / 180.0))*(endX-startX)) + startY); 
				double[] pixelValues = inputImage.getProcessor().getLine(startX, startY, endX, endY);
				double pixelPercentage = this.getCount(pixelValues);
				if (pixelPercentage>countThreshold.getDoubleValue()) {
					Line2D aLine = new Line2D.Float(startX,startY,endX,endY);
					if (resultLines.size()>0 ) {
						Line2D lastLine = resultLines.get(resultLines.size()-1);
						if (lastLine.intersectsLine(aLine)) {
							break;
						}
					}
					resultLines.add(aLine);
				}
			}
		}
		this.drawLines();
	}
	
	public double getThreshold() {
		return threshold.getDoubleValue();
	}

	public void setTreshold(double threshold) {
		this.threshold.setValue(new Double(threshold).toString());
	}
	
	public ScanForStraightLinesOperation() {
		super();
		resultLines = new ArrayList<Line2D>();
	}
	
	public void setMaxAngle(int angle) {
		this.maxAngle.setValue(new Integer(angle).toString());
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

	private double getCount(double[] pixelValues) {
		double result = 0;
		for (int i=0; i<pixelValues.length; i++) {
			if(pixelValues[i]>threshold.getDoubleValue()) result++;
		}
		result = result / pixelValues.length;
		return result;
	}

	public double getCountThreshold() {
		return countThreshold.getDoubleValue();
	}

	public void setCountThreshold(double countThreshold) {
		this.countThreshold.setValue(new Double(countThreshold).toString());
	}
	
	protected void setupOptions() {
		super.setupOptions();
		this.connectOptions();
		this.setTreshold(30);
		threshold.setShortHelpText("Pixels with intensities above threshold are counted.");
		this.setMaxAngle(7);
		maxAngle.setShortHelpText("Detects lines with an inclination between -angle and angle.");
		this.setCountThreshold(0.33);
		countThreshold.setShortHelpText("A line is detected when it has a support of at least count threshold pixels.");
	}

	public void connectOptions() {
		this.threshold = (Option) this.options.getOptions().get(0);
		this.maxAngle = (Option) this.options.getOptions().get(1);
		this.countThreshold = (Option) this.options.getOptions().get(2);
	}

	public int getMaxAngle() {
		return maxAngle.getIntegerValue();
	}

	public ArrayList<Line2D> getResultLines() {
		return resultLines;
	}

	public void setResultLines(ArrayList<Line2D> resultLines) {
		this.resultLines = resultLines;
	}
}
