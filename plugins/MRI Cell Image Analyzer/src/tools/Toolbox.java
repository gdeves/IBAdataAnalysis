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
package tools;

import utils.RoiEditor;
import help.HelpSystem;
import ij.IJ;
import ij.WindowManager;
import ij.gui.Toolbar;

/**
 * The toolbox makes the most important tools available in one place.
 * 
 * @author	Volker Baecker
 **/
public class Toolbox {
	
	protected ToolboxView view;

	/**
	 * Display the toolbox.
	 */
	public void show() {
		if (view == null) {
			view = new ToolboxView(this);
		}
		view.setVisible(true);
	}

	/**
	 * Activate the rectangular selection tool.
	 */
	public void setRectangularSelectionTool() {
		IJ.setTool(Toolbar.RECTANGLE);
	}

	/**
	 * Activate the oval selection tool.
	 */
	public void setOvalSelectionTool() {
		IJ.setTool(Toolbar.OVAL);
	}

	/**
	 * Activate the polygon selection tool.
	 */
	public void setPolygonSelectionTool() {
		IJ.setTool(Toolbar.POLYGON);
	}

	/**
	 * Activate the freehand selection tool.
	 */
	public void setFreehandSelectionTool() {
		IJ.setTool(Toolbar.FREEROI);
	}

	/**
	 * Activate the line selection tool.
	 */
	public void setStraightLineSelectionTool() {
		IJ.setTool(Toolbar.LINE);
	}

	/**
	 * Activate the segmented line selection tool.
	 */
	public void setSegmentedLineSelectionTool() {
		IJ.setTool(Toolbar.POLYLINE);
	}

	/**
	 * Activate the freehand line selection tool.
	 */
	public void setFreehandLineSelectionTool() {
		IJ.setTool(Toolbar.FREELINE);
	}

	/**
	 * Activate the angle selection tool.
	 */
	public void setAngleSelectionTool() {
		IJ.setTool(Toolbar.ANGLE);
	}

	/**
	 * Activate the point selection tool.
	 */
	public void setPointSelectionTool() {
		IJ.setTool(Toolbar.POINT);
	}

	/**
	 * Activate the magic wand tool.
	 */
	public void setMagicWandSelectionTool() {
		IJ.setTool(Toolbar.WAND);
	}

	/**
	 * Run the particle analyzer. This will open the particle analyzer dialog.
	 */
	public void analyzeParticles() {
		IJ.doCommand("Analyze Particles...");
	}

	/**
	 * Measure the image or the current selection.
	 */
	public void measure() {
		IJ.doCommand("Measure");
	}

	/**
	 * Activate the magnify tool.
	 */
	public void setMagnifyTool() {
		IJ.setTool(Toolbar.MAGNIFIER);
	}

	/**
	 * Activate the scroll tool.
	 */
	public void setScrollTool() {
		IJ.setTool(Toolbar.HAND);
	}

	/**
	 * Activate the color picker tool.
	 */
	public void setColorPickerTool() {
		IJ.setTool(Toolbar.DROPPER);
	}

	/**
	 * Activate the text tool.
	 */
	public void setTextTool() {
		IJ.setTool(Toolbar.TEXT);
	}

	/**
	 * Select the whole image.
	 */
	public void selectAll() {
		IJ.doCommand("Select All");
	}

	/**
	 * Remove the selection from the image.
	 */
	public void selectNone() {
		IJ.doCommand("Select None");
	}

	/**
	 * Inverse the selection.
	 */
	public void inverseSelection() {
		IJ.doCommand("Make Inverse");
	}

	/**
	 * Restore the last active selection.
	 */
	public void restoreSelection() {
		IJ.doCommand("Restore Selection");
	}

	/**
	 * Create a mask from the selection.
	 */
	public void createMaskFromSelection() {
		IJ.doCommand("Create Mask");
	}

	/**
	 * Replace the selection by its convex hull.  
	 * Works for polygon and point selections.
	 */
	public void convexHullOfSelection() {
		IJ.doCommand("Convex Hull");
	}

	/**
	 * Replace the selection by a fitting ellipse.
	 */
	public void makeSelectionOval() {
		IJ.doCommand("Fit Ellipse");
	}

	/**
	 * Replace the selection by a spline selection.
	 */
	public void makeSpline() {
		IJ.doCommand("Fit Spline");
	}

	/**
	 * Duplicate the image or selection. 
	 */
	public void duplicate() {
		IJ.doCommand("Duplicate...");
	}

	/**
	 * Crop image to the current selection.
	 */
	public void crop() {
		IJ.doCommand("Crop");
	}

	/**
	 * Copy the image or the selection into the imagej clipboard.
	 */
	public void copy() {
		IJ.doCommand("Copy");
	}

	/**
	 * Paste the content of the imagej clipboard into the image.
	 */
	public void paste() {
		IJ.doCommand("Paste");
	}

	/**
	 * Set the scale. Distances will be measured in units of the scale if a scale is set.
	 */
	public void setScale() {
		IJ.doCommand("Set Scale...");
	}

	/**
	 * Paste a scale bar into the image.
	 */
	public void pasteScaleBar() {
		IJ.doCommand("Scale Bar...");
	}

	/**
	 * Delete the last element of a polygon selection. This is not macro recordable.
	 */
	public void cutLastPolygonElement() {
		RoiEditor editor = new RoiEditor(WindowManager.getCurrentImage());
		editor.deleteLastPolygonSegment();
	}

	/**
	 * Configure the measurements that are taken by the measure command.
	 */
	public void setMeasureOptions() {
		IJ.doCommand("Set Measurements...");
	}

	/**
	 * Change the line width of selections.
	 */
	public void adjustLineWidth() {
		IJ.runPlugIn("ij.plugin.frame.LineWidthAdjuster", "");
	}

	/**
	 * Change the display size of selection points. Doesn't seem to work. 
	 * Activate/Deactivate auto-measure and auto-next slice.
	 */
	public void adjustPointSize() {
		IJ.doCommand("Point Tool...");
	}

	/**
	 * Choose font and size for the text tool. 
	 */
	public void setTextOptions() {
		IJ.doCommand("Fonts...");
	}

	/**
	 * Opens a dialog in which the user can select the paste mode (copy, blend, add, ...).
	 */
	public void setPasteOptions() {
		IJ.doCommand("Paste Control...");
	}

	/**
	 * Select the foreground and background color. These are for example used by the fill and clear command
	 * from the images' context menu. 
	 */
	public void selectColors() {
		IJ.doCommand("Color Picker...");
	}

	/**
	 * Open the help page of the toolbox in an external browser.
	 */
	public void openHelp() {
		HelpSystem help = HelpSystem.getCurrent();
		help.openHelpFor(this.name());
	}

	/**
	 * @return the name of this component
	 */
	protected String name() {
		return "Toolbox";
	}
}
