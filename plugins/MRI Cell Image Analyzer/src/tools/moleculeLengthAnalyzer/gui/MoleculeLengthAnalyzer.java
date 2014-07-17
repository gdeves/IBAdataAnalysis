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

import java.io.File;
import java.util.Observable;

import tools.moleculeLengthAnalyzer.excel.ExcelMoleculeWriter;
import tools.moleculeLengthAnalyzer.model.ImageInfo;
import tools.moleculeLengthAnalyzer.model.Molecules;
import tools.moleculeLengthAnalyzer.model.Spots;
import tools.moleculeLengthAnalyzer.progress.ProgressIndicator;

/**
 * A tool that allows to read in to lists of points from excel files.
 * The points can be filtered in a way that only points with a minimum 
 * distance to the nearest neighbour are kept. For each green point the
 * nearest red point is found. The two points form a molecule. The molecules
 * can be drawn into an image and reported to excel. The length of the molecules
 * is measured. 
 * 
 * @author baecker
 *
 */
public class MoleculeLengthAnalyzer extends Observable implements ProgressIndicator {
	protected enum Aspect {MOLECULES, ERROR_OCCURED};  
	private MoleculeLengthAnalyzerView view;
	private SpotsManager greenSpotsManager;
	private SpotsManager redSpotsManager;
	private int progress;
	private int maxProgress;
	private Molecules molecules;
	private String resultFilename;

	public MoleculeLengthAnalyzer() {
		greenSpotsManager = new SpotsManager("green spots");
		redSpotsManager = new SpotsManager("red spots");
		molecules = new Molecules();
	}
	
	public void changed(Aspect anAspect) {
		this.setChanged();
		this.notifyObservers(anAspect);
		this.clearChanged();
	}
	
	public void show() {
		this.getView().setVisible(true);
	}

	public MoleculeLengthAnalyzerView getView() {
		if (this.view==null) this.view = new MoleculeLengthAnalyzerView(this);
		return view;
	}
	
	public SpotsManager getGreenSpotsManager() {
		return greenSpotsManager;
	}
	
	public SpotsManager getRedSpotsManager() {
		return redSpotsManager;
	}
	
	@Override
	public int getMaxProgress() {
		return maxProgress;
	}
	
	@Override
	public int getProgress() {
		return progress;
	}
	
	@Override
	public void setMaxProgress(int max) {
		this.maxProgress = max;
	}
	
	@Override
	public void setProgress(int progress) {
		this.progress = progress;
	}
	
	public void findMolecules() {
		Spots greenSpots = greenSpotsManager.getSpots();
		if (greenSpots==null) return;
		molecules = greenSpots.findMolecules(redSpotsManager.getSpots());	
		this.changed(Aspect.MOLECULES);
	}
	
	public Molecules getMolecules() {
		return molecules;
	}

	public String getResultFilename() {
		if (resultFilename==null) resultFilename = this.getDefaultResultFilename();
		return resultFilename;
	}

	private String getDefaultResultFilename() {
		File greenFile = new File(this.getGreenSpotsManager().getExcelFile());
		String greenFilename = greenFile.getName().split("\\.")[0];
		String[] components = greenFilename.split("_");
		String result = components[0] + "_molecules" + ".xls";
		return result;
	}

	public void setResultFilename(String file) {
		this.resultFilename = file;
	}

	public void writeMoleculesToExcel(String path) {
		Molecules molecules = this.getMolecules();
		if (molecules == null) return;
		ExcelMoleculeWriter writer = new ExcelMoleculeWriter(path, molecules);
		writer.writeFile();
	}
	
	public static void main(String[] args) {
		(new MoleculeLengthAnalyzer()).show();
	}

	@Override
	public void stopWithError() {
		this.changed(Aspect.ERROR_OCCURED);
	}

	public ImagePlus getImageFor(ImageInfo info) {
		return this.getGreenSpotsManager().getImageFor(info);
	}

	public ImageInfo getImageInfoFromImage(ImagePlus image) {
		return this.getGreenSpotsManager().getImageInfoFromImage(image);
	}

	public void drawMolecules(ImagePlus image, ImageInfo info) {
		this.getMolecules().drawOn(image, info);
	}
}
