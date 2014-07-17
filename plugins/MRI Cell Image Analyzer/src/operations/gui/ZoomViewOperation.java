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
package operations.gui;

import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;

import java.awt.Point;

import gui.Options;
import gui.options.BooleanOption;
import gui.options.Option;
import operations.Operation;

/**
 * Zoom n-times into or out of the image. 
 * 
 * @author Volker Baecker
 */
public class ZoomViewOperation extends Operation {
	private static final long serialVersionUID = 7150080905726066665L;
	protected BooleanOption zoomIn;
	protected Option times;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		optionsNames = new String[2];
		optionsNames[0] = "zoom in";
		optionsNames[1] = "times";
		resultTypes = new Class[0];
		resultNames = new String[0];
	}
	
	public void doIt() {
		inputImage = this.getInputImage();
		for (int i=0; i<this.getTimes(); i++) {
			ImageWindow win = inputImage.getWindow();
			if (win==null) return;
			ImageCanvas ic = win.getCanvas();
			Point loc = ic.getCursorLoc();
			int x = ic.screenX(loc.x);
			int y = ic.screenY(loc.y);
			if (this.getZoomIn()) {
				ic.zoomIn(x, y);
				if (ic.getMagnification()<=1.0) inputImage.repaintWindow();
			} else {
				ic.zoomOut(x, y);
				if (ic.getMagnification()<1.0) inputImage.repaintWindow();
			}
		}
	}
	
	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		this.zoomIn = new BooleanOption();
		this.zoomIn.setName(optionsNames[0]);
		this.setZoomIn(false);
		zoomIn.setShortHelpText("select to zoom in and deselect to zoom out");
		options.add(zoomIn);
		this.times = new Option();
		this.times.setName(optionsNames[1]);
		this.setTimes(1);
		times.setShortHelpText("enter the number of times to zoom in or out");
		options.add(times);
	}
	
	public void connectOptions() {
		this.zoomIn = (BooleanOption) this.options.getOptions().get(0);
		this.times = (Option) this.options.getOptions().get(1);
	}

	public int getTimes() {
		return times.getIntegerValue();
	}

	public void setTimes(int times) {
		this.times.setValue(Integer.toString(times));
	}

	public boolean getZoomIn() {
		return zoomIn.getBooleanValue();
	}

	public void setZoomIn(boolean zoomIn) {
		this.zoomIn.setValue(Boolean.toString(zoomIn));
	}
}
