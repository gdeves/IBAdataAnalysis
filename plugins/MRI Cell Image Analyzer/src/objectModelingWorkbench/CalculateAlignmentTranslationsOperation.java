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
package objectModelingWorkbench;

import gui.options.Option;
import ij.IJ;
import ij.ImagePlus;
import ij.measure.ResultsTable;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;

import operations.Operation;
import operations.file.OpenImageOperation;

/**
 * For two consecutive images, find the translation that minimizes the difference between the images in the 
 * overlapping part. Write all translations to a text file.
 * 
 * @author Volker Baecker
 */
public class CalculateAlignmentTranslationsOperation extends Operation {
	private static final long serialVersionUID = 779477588840854607L;
	protected ArrayList<File> imageList;
	protected ResultsTable translations;
	protected String folder;
	protected BufferedWriter out;
	protected Option resultFilename;
	protected Option stepWidth;
	
	public CalculateAlignmentTranslationsOperation() {
		super();
	}
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("java.util.Vector");
		parameterNames = new String[1];
		parameterNames[0] = "ImageList";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.measure.ResultsTable");
		resultNames = new String[1];
		resultNames[0] = "Translations";
		optionsNames = new String[2];
		optionsNames[0] = "result filename";
		optionsNames[1] = "step width";
	}

	public ArrayList<File> getImageList() {
		return imageList;
	}

	public void setImageList(ArrayList<File> imageList) {
		this.imageList = imageList;
	}

	
	@SuppressWarnings("unchecked")
	public void setImageList(Vector imageList) {
		this.imageList = new ArrayList<File>(imageList);
	}
	
	public ResultsTable getTranslations() {
		return translations;
	}

	public void setTranslations(ResultsTable translations) {
		this.translations = translations;
	}
	
	public void doIt() {
		boolean isFastAlignment = false;
		if (this.getStepWidth()>0) isFastAlignment = true;
		int stepWidth = this.getStepWidth();
		writeTimestamp();
		int counter = 0;
		OpenImageOperation open = new OpenImageOperation();
		String firstImageFileName = ((File)(this.getImageList().get(counter))).getAbsolutePath();
		open.setAbsoluteFilename(firstImageFileName);
		open.setShowResult(false);
		open.run();
		if (this.resultFilename.getValue()==null || this.resultFilename.getValue().equals("null") || this.resultFilename.getValue().trim().equals("")) {
			folder = firstImageFileName.substring(0, firstImageFileName.lastIndexOf(File.separator)) + File.separator;
			this.resultFilename.setValue(folder + "translations.txt");
		}
		try {
			out = new BufferedWriter(new FileWriter(this.resultFilename.getValue()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		ImagePlus firstImage = open.getResult();
		AlignmentSlice firstSlice = new AlignmentSlice(firstImage);
		this.translations = new ResultsTable();
		this.translations.reset();
		translations.incrementCounter();
		translations.addValue(ResultsTable.X_CENTROID, 0);
		translations.addValue(ResultsTable.Y_CENTROID, 0);
		translations.addValue(ResultsTable.INTEGRATED_DENSITY, 0);
		counter++;
		this.writeToFile(0,0,0);
		while (counter<this.getImageList().size()) {
			System.gc();
			String secondImageFileName = ((File)(this.getImageList().get(counter))).getAbsolutePath();
			open.setAbsoluteFilename(secondImageFileName);
			open.run();
			ImagePlus secondImage = open.getResult();
			AlignmentSlice secondSlice = new AlignmentSlice(secondImage);
			double score;
			if (isFastAlignment) {
				score = firstSlice.fastAlignSlice(secondSlice, stepWidth);
			} else {
				score = firstSlice.alignSlice(secondSlice);
			}
			translations.incrementCounter();
			translations.addValue(ResultsTable.X_CENTROID, secondSlice.getXOffset());
			translations.addValue(ResultsTable.Y_CENTROID, secondSlice.getYOffset());
			translations.addValue(ResultsTable.INTEGRATED_DENSITY, score);
			this.writeToFile(secondSlice.getXOffset(),secondSlice.getYOffset(),score);
			// prepare next loop cycle
			firstSlice = secondSlice;
			IJ.showProgress(counter, this.getImageList().size()-1);
			counter++;
			writeTimestamp();
		}
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		writeTimestamp();
	}

	private void writeTimestamp() {
		GregorianCalendar cal = new GregorianCalendar();
		System.out.println(cal.get(Calendar.HOUR_OF_DAY) + ":" + 
							cal.get(Calendar.MINUTE) + ":" + 
							cal.get(Calendar.SECOND) + "." + 
							cal.get(Calendar.MILLISECOND));
	}

	protected void writeToFile(int shiftX, int shiftY, double score) {
		try {
	        out.write(shiftX + "\t" + shiftY + "\t" + score + "\n");
	        out.flush();
	    } catch (IOException e) {
	    }
		
	}

	protected void setupOptions() {
		super.setupOptions();
		this.resultFilename.beForFilename();
		this.resultFilename.setShortHelpText("choose a result file for the translations");
		this.setStepWidth(0);
		this.stepWidth.setMin(0);
		this.stepWidth.setShortHelpText("if step width is >0 fast alignment is used");
		
	}
	
	public void connectOptions() {
		this.resultFilename = (Option) this.options.getOptions().get(0);
		this.stepWidth = (Option) this.options.getOptions().get(1);
	}

	public String getResultFilename() {
		return resultFilename.getValue();
	}

	public void setResultFilename(String filename) {
		this.resultFilename.setValue(filename);
	}

	public int getStepWidth() {
		return stepWidth.getIntegerValue();
	}

	public void setStepWidth(int stepWidth) {
		this.stepWidth.setValue(Integer.toString(stepWidth));
	}
}
