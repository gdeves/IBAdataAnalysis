/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2009 INSERM 
 * MRI Cell Image Analyzer has been created at Montpellier RIO Imaging
 * (www.mri.cnrs.fr) by Volker Bäcker
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
package tools.moleculeLengthAnalyzer.gui;

import ij.ImagePlus;
import ij.gui.NewImage;
import ij.measure.Calibration;
import java.util.Observable;
import tools.moleculeLengthAnalyzer.excel.ExcelSpotReader;
import tools.moleculeLengthAnalyzer.excel.MoleculeLengthAnalyzerProperties;
import tools.moleculeLengthAnalyzer.model.ImageInfo;
import tools.moleculeLengthAnalyzer.model.Spots;
import tools.moleculeLengthAnalyzer.progress.ProgressIndicator;

/**
 * A component that allows to read in a list of points from excel and 
 * to filter them using a minimum distance to the nearest neighbour.
 * A list of point rois can be created for the spots.  
 * 
 * @author baecker
 *
 */
public class SpotsManager extends Observable implements ProgressIndicator {
	protected enum Aspect {EXCEL_FILE, UNIT, SPOTS, PROGRESS, MAX_PROGRESS, ERROR_OCCURED};
	private String name;
	private SpotsManagerView view;
	private String excelFile;
	private ExcelSpotReader reader;
	private Double minimumDistance;
	private MoleculeLengthAnalyzerProperties props; 
	private Spots filteredSpots;
	private int progress;
	private int maxProgress;
	private Thread readerThread;

	public String getExcelFile() {
		if (excelFile==null) excelFile = new String();
		return excelFile;
	}

	public void setExcelFile(String excelFile) {
		this.excelFile = excelFile;
		this.changed(Aspect.EXCEL_FILE);
	}

	public void changed(Aspect anAspect) {
		this.setChanged();
		this.notifyObservers(anAspect);
		this.clearChanged();
	}
	
	public SpotsManager(String name) {
		this.setName(name);
		this.props = MoleculeLengthAnalyzerProperties.getInstance();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		if (name==null) name ="spots";
		return name;
	}

	public SpotsManagerView getView() {
		if (view==null) view = new SpotsManagerView(this);
		return view;
	}

	public void readSpotsFromExcel() {
		reader = new ExcelSpotReader(this.getExcelFile());
		this.filteredSpots = null;
		reader.setProgressIndicator(this);
		readerThread = new Thread(reader);
		readerThread.start();
	}
	
	public Spots getSpots() {
		if (reader==null) return new Spots();
		if (filteredSpots!=null) return filteredSpots;
		return this.reader.getSpots();
	}
	
	public String getUnit() {
		if (reader==null) return null;
		return this.reader.getUnit();
	}

	public void setMinimumDistance(double minDist) {
		this.minimumDistance = minDist; 
		props.setProperty("min_dist", Double.toString(minDist));
	}
	
	public double getMinimumDistance() {
		if (minimumDistance==null) {
			String minimumDistanceString = props.getProperty("min_dist");
			if (minimumDistanceString!=null) minimumDistance = Double.parseDouble(minimumDistanceString);
		}
		return minimumDistance;
	}

	public void filterSpots() {
		if (reader==null) return;
		filteredSpots = reader.getSpots().getSpotsWithMinDistance(this.getMinimumDistance());
		this.changed(Aspect.SPOTS);
	}

	public void resetSpots() {
		filteredSpots = null;
		this.changed(Aspect.SPOTS);
	}

	public int getNumberOfSpots() {
		return this.getSpots().getSize();
	}

	@Override
	public void setProgress(int progress) {
		this.progress = progress;
		this.changed(Aspect.PROGRESS);
		if (progress >= maxProgress) {
			this.changed(Aspect.UNIT);
			this.changed(Aspect.SPOTS);
		}
	}

	@Override
	public int getProgress() {
		return progress;
	}

	@Override
	public void setMaxProgress(int max) {
		this.maxProgress = max;
		this.changed(Aspect.MAX_PROGRESS);
	}
	
	@Override
	public int getMaxProgress() {
		return maxProgress;
	}

	@Override
	public void stopWithError() {
		this.changed(Aspect.ERROR_OCCURED);
	}

	public ImagePlus getImageFor(ImageInfo info) {
		ImagePlus image = NewImage.createByteImage(this.getName(), info.getWidth(), info.getHeight(), info.getSlices(), NewImage.FILL_BLACK);
		Calibration cal = new Calibration();
		cal.pixelWidth = info.getXSize();
		cal.pixelHeight = info.getYSize();
		cal.pixelDepth = info.getZSize();
		if (this.getUnit()!=null) cal.setUnit(this.getUnit());
		image.setCalibration(cal);
		return image;
	}

	public ImageInfo getImageInfoFromImage(ImagePlus image) {
		Calibration calibration = image.getCalibration();
		ImageInfo info = new ImageInfo();
		info.setWidth(image.getWidth());
		info.setHeight(image.getHeight());
		info.setSlices(image.getNSlices());
		info.setXSize(calibration.pixelWidth);
		info.setYSize(calibration.pixelHeight);
		info.setZSize(calibration.pixelDepth);
		return info;
	}
}
