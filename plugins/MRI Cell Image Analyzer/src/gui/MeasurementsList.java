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
package gui;

import java.awt.event.KeyEvent;
import java.util.Observable;
import java.util.Vector;
import ij.IJ;
import ij.ImagePlus;
import ij.measure.ResultsTable;

/**
 * Manages a result table of measurements. If the table contains centroids the objects
 * can be selected in a mask image.
 * 
 * @author	Volker Baecker
 **/
public class MeasurementsList  extends Observable {
	protected ResultsTable measurements;
	private MeasurementsListView view;

	public MeasurementsList(ResultsTable measurements) {
		this.measurements = measurements;
	}
	public ResultsTable getMeasurements() {
		return measurements;
	}

	public void setMeasurements(ResultsTable measurements) {
		this.measurements = measurements;
		this.changed("measurements");
	} 
	
	public void changed(String string) {
		this.setChanged();
		this.notifyObservers(string);
		this.clearChanged();
	}
	
	public MeasurementsListView getView() {
		if (view==null) {
			view = new MeasurementsListView(this);
		}
		return view;
	}
	
	public void show() {
		this.getView().setVisible(true);
	}
	
	public void selectObjects(Vector<Integer> selection) {
		if (!measurements.columnExists(measurements.getColumnIndex("X"))) return;
		ImagePlus image = IJ.getImage();
		image.killRoi();
		IJ.setKeyDown(KeyEvent.VK_SHIFT);
		for(int index : selection) {
			double x = measurements.getValue("X", index);
			double y = measurements.getValue("Y", index);
			IJ.doWand((int)Math.round(x), (int)Math.round(y));
		}
		IJ.setKeyUp(KeyEvent.VK_SHIFT);
	}
}
