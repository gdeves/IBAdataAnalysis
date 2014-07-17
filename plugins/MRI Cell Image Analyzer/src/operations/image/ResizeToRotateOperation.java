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
package operations.image;

import ij.IJ;
import gui.Options;
import gui.options.BooleanOption;
import gui.options.Option;
import operations.FilterOperation;

/**
 * Enlarges the image canvas in a way that the image can be rotated at all pixels will still be within the canvas.
 * 
 * @author Volker Baecker
 */
public class ResizeToRotateOperation extends FilterOperation {
	protected Option fillBlack;
	private static final long serialVersionUID = -8152233029043810901L;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[1];
		optionsNames[0] = "fill black";
	}
	
	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		this.fillBlack = new BooleanOption();
		this.fillBlack.setValue("true");
		this.fillBlack.setName(optionsNames[0]);
		this.fillBlack.setShortHelpText("if selected the enlarged area will be black, otherwise it will be white");
		options.add(this.fillBlack);
	}
	
	public void connectOptions() {
		fillBlack = (BooleanOption) options.getOptions().get(0);
	}

	public boolean getFillBlack() {
		return fillBlack.getBooleanValue();
	}

	public void setFillBlack(boolean fillBlack) {
		this.fillBlack.setValue(Boolean.toString(fillBlack));
	}
	
	public void runFilter() {
		int x = result.getWidth();
		int y = result.getHeight();
		double d = 2+Math.sqrt(x*x+y*y);
		if (this.getFillBlack()) {
			IJ.run("Canvas Size...", "width="+d + " height="+d+ " position=Center zero");
		} else {
			IJ.run("Canvas Size...", "width="+d + " height="+d+ " position=Center");
		}
	}
}
