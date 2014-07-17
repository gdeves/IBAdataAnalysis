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
package operations.processing;

import java.util.ArrayList;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;
import gui.options.Option;
import operations.Operation;

/**
 * 
 * 
 * @author Volker Bäcker
 */
public class GridMaximaOperation extends Operation {
	private static final long serialVersionUID = 5490831546934375930L;
	protected Option gridWidth;
	protected Option gridHeight;
	
	protected ArrayList<Integer> xMaxima;
	protected ArrayList<Integer> yMaxima;
	protected ArrayList<Integer> iMaxima;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[2];
		optionsNames[0] = "grid width";
		optionsNames[1] = "grid height";
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultNames = new String[1];
		resultNames[0] = "Result";
	}
	
	public void connectOptions() {
		this.gridWidth = (Option) this.options.getOptions().get(0);
		this.gridHeight = (Option) this.options.getOptions().get(1);
	}
	
	protected void setupOptions() {
		super.setupOptions();
		this.setGridWidth(100);
		this.setGridHeight(130);
	}

	public int getGridHeight() {
		return gridHeight.getIntegerValue();
	}

	public void setGridHeight(int gridHeight) {
		this.gridHeight.setValue(Integer.toString(gridHeight));
	}

	public int getGridWidth() {
		return gridWidth.getIntegerValue();
	}

	public void setGridWidth(int gridWidth) {
		this.gridWidth.setValue(Integer.toString(gridWidth));
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		result = this.getCopyOfOrReferenceTo(inputImage, inputImage.getTitle());
		this.processSlices();
	}
	
	protected void doItForSlice(int sliceNumber, ImageStack stack) {
		ImageProcessor ip = stack.getProcessor(sliceNumber);
		int gridWidth = this.getGridWidth();
		int gridHeight = this.getGridHeight();
		int xSteps = (ip.getWidth() / gridWidth) + 1;
		int ySteps = (ip.getHeight() / gridHeight) + 1;
		for (int x=0; x<xSteps; x++) {
			for (int y=0; y<ySteps; y++) {
				 this.findMaxima(x*gridWidth, y*gridHeight, sliceNumber, gridWidth, gridHeight, stack);
				 this.zeroNoneMaximaInRegion(x*gridWidth,y*gridHeight, sliceNumber, gridWidth, gridHeight, stack);
			}
		}
	}

	protected void zeroNoneMaximaInRegion(int startX, int startY, int z, int width, int height, ImageStack stack) {
		ImageProcessor ip = stack.getProcessor(z);
		for (int x=startX; x<startX+width; x++) {
			for (int y=startY; y<startY+height; y++) {
				ip.putPixel(x,y, 0);
			}
		}
		for (int i=0; i<xMaxima.size(); i++) {
				int x = xMaxima.get(i).intValue();
				int y = yMaxima.get(i).intValue();
				int intensity = iMaxima.get(i).intValue();
				ip.putPixel(x,y, intensity);
		}
	}

	protected void findMaxima(int startX, int startY, int z, int width, int height, ImageStack stack) {
		ImageProcessor ip = stack.getProcessor(z);
		xMaxima = new ArrayList<Integer>();
		yMaxima = new ArrayList<Integer>();
		iMaxima = new ArrayList<Integer>();
		int maximumIntensity = this.getLocalMaximumIntensity(startX, startY, width, height, ip);
		for (int x=startX; x<startX+width; x++) {
			for (int y=startY; y<startY+height; y++) {
				int intensity = ip.getPixel(x,y);
				if (intensity != maximumIntensity) continue;
				xMaxima.add(new Integer(x));
				yMaxima.add(new Integer(y));
				iMaxima.add(new Integer(intensity));
			}	
		}
	}

	private int getLocalMaximumIntensity(int startX, int startY, int width, int height, ImageProcessor ip) {
		double mean = result.getStatistics().mean;
		double threshold = mean / 4.0;
		int maximum = 0;
		for (int x=startX; x<startX+width; x++) {
			for (int y=startY; y<startY+height; y++) {
				int intensity = ip.getPixel(x,y);
				if (intensity<=threshold) continue;
				if (intensity>maximum) maximum = intensity;
			}	
		}
		return maximum;
	}
}
