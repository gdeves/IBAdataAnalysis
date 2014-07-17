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
package tools.channels;

import ij.ImagePlus;
import ij.WindowManager;
import ij.process.ColorProcessor;

/**
 * Hide the red, the green or the blue channel of the current image.
 * 
 * @author	Volker Baecker
 **/
public class ChannelHider {
	protected ChannelHiderView view;
	protected boolean showRed;
	protected boolean showGreen;
	protected boolean showBlue;
	
	public ChannelHider() {
		super();
		this.initialize();
	}
	
	private void initialize() {
		this.setShowRed(true);
		this.setShowBlue(true);
		this.setShowGreen(true);
	}

	public void show() {
		this.getView().setVisible(true);
	}

	protected ChannelHiderView getView() {
		if (view==null) view = new ChannelHiderView(this);
		return view;
	}
	
	public boolean isShowBlue() {
		return showBlue;
	}

	public void setShowBlue(boolean showBlue) {
		ImagePlus image = WindowManager.getCurrentImage(); 
		if (image==null) return;
		if (image.getType()!=ImagePlus.COLOR_RGB) return;
		this.showBlue = showBlue;
		ColorProcessor processor = (ColorProcessor)image.getProcessor();
		if (showBlue) {
			processor.reset();
		} else {
			showGreen = true;
			showRed = true;
			processor.reset();
			processor.snapshot();
			processor.setMinAndMax(255,255,1);
		}
		image.updateAndDraw();
	}

	public boolean isShowGreen() {
		return showGreen;
	}

	public void setShowGreen(boolean showGreen) {
		ImagePlus image = WindowManager.getCurrentImage(); 
		if (image==null) return;
		if (image.getType()!=ImagePlus.COLOR_RGB) return;
		this.showGreen = showGreen;
		ColorProcessor processor = (ColorProcessor)image.getProcessor();
		if (showGreen) {
			processor.reset();
		} else {
			showRed = true;
			showBlue = true;
			processor.reset();
			processor.snapshot();
			processor.setMinAndMax(255,255,2);
		}
		image.updateAndDraw();
	}

	public boolean isShowRed() {
		return showRed;
	}

	public void setShowRed(boolean showRed) {
		ImagePlus image = WindowManager.getCurrentImage(); 
		if (image==null) return;
		if (image.getType()!=ImagePlus.COLOR_RGB) return;
		this.showRed = showRed;
		ColorProcessor processor = (ColorProcessor)image.getProcessor();
		if (showRed) {
			processor.reset();
		} else {
			showGreen = true;
			showBlue = true;
			processor.reset();
			processor.snapshot();
			processor.setMinAndMax(255,255,4);
		}
		image.updateAndDraw();
	}
}
