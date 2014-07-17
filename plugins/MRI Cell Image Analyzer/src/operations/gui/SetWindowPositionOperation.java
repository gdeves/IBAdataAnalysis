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
package operations.gui;

import java.awt.Rectangle;

import ij.ImagePlus;
import gui.Options;
import gui.options.Option;
import operations.Operation;

/**
 * Move the upper left corner of the window of the input image to the position
 * specified in the options of the operation. 
 * 
 * @author Volker Baecker
 */
public class SetWindowPositionOperation extends Operation {
	private static final long serialVersionUID = -6497899346286815843L;
	protected Option x;
	protected Option y;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		optionsNames = new String[2];
		optionsNames[0] = "x";
		optionsNames[1] = "y";
		resultTypes = new Class[0];
		resultNames = new String[0];
	}
	
	public int getX() {
		return x.getIntegerValue();
	}

	public void setX(int x) {
		this.x.setValue(Integer.toString(x));
	}

	public int getY() {
		return y.getIntegerValue();
	}

	public void setY(int y) {
		this.y.setValue(Integer.toString(y));
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		if (application==null && inputImage==null) return;
		Rectangle bounds = inputImage.getWindow().getBounds();
		bounds.x = this.getX();
		bounds.y = this.getY();
		inputImage.getWindow().setBounds(bounds);
	}
	
	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		this.x = new Option();
		this.x.setName(optionsNames[0]);
		this.setX(100);
		x.setMin(0);
		x.setShortHelpText("enter the x coordinate of the position");
		options.add(x);
		this.y = new Option();
		this.y.setName(optionsNames[1]);
		this.setY(100);
		y.setMin(0);
		y.setShortHelpText("enter the y coordinate of the position");
		options.add(y);
	}
	
	public void connectOptions() {
		this.x = (Option) this.options.getOptions().get(0);
		this.y = (Option) this.options.getOptions().get(1);
	}
}
