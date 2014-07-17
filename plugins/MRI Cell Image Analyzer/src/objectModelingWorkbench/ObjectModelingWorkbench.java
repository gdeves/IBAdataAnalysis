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

import gui.ListEditor;
import help.HelpSystem;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.NewImage;
import ij.gui.Roi;
import ij.io.RoiDecoder;
import ij.io.RoiEncoder;
import imagejProxies.MRIInterpreter;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import operations.Operation;
import operations.image.ScaleImageOperation;
import operations.roi.ClearOperation;
import operations.roi.FillOperation;
import operations.roi.InverseSelectionOperation;
import operations.stack.AutoAlignImagesOperation;
import applications.Application;
import sun.awt.shell.ShellFolder;

/**
 * A tool for the alignment of the slices in large image series and the extraction of data
 * for 3d modeling.
 * 
 * @author Volker Baecker
 */
public class ObjectModelingWorkbench extends Observable implements Observer {
	protected Alignments alignments;
	protected ImagePlus firstImage;
	protected ImagePlus secondImage;
	protected int firstImageOffsetX;
	protected int firstImageOffsetY;
	protected int firstImageAngle;
	protected int secondImageOffsetX;
	protected int secondImageOffsetY;
	protected int secondImageAngle;
	protected ImageDisplayOptions firstImageOptions;
	protected ImageDisplayOptions secondImageOptions;
	protected AlignmentSlice firstImageSlice = null;
	protected AlignmentSlice secondImageSlice = null;
	protected Object3DManager objectManager = null;
	protected ObjectModelingWorkbenchView view;

	public void changed(String string) {
		this.setChanged();
		this.notifyObservers(string);
		this.clearChanged();
	}
	
	/**
	 * Show my gui.
	 */
	public void show() {
		view = this.getView();
		view.setVisible(true);
	}
	
	/**
	 * Open alignments from a tab seperated text file. 
	 * Creates an alignment slice for each line in the file.
	 * The format of a line in the file is:
	 *  x-offset[tab]y-offset[tab]score
	 * The origin is in the center of the image.
	 * 
	 * @param String path
	 */
	public void openAlignments(String path) {
		ArrayList<AlignmentSlice> alignments = new ArrayList<AlignmentSlice>();
		File aFile = new File(path);
		BufferedReader input = null;
		try {
			input = new BufferedReader(new FileReader(aFile));
			String line = null; 
			int counter = 0;
			while ((line = input.readLine()) != null) {
				String[] items = line.split("\t");
				AlignmentSlice currentTranslation = new AlignmentSlice();
				if (this.getAlignments().getAlignments().size()>counter) {
					currentTranslation = (AlignmentSlice) this.getAlignments().getAlignments().get(counter);
				}
				if (items.length==3) { 
					currentTranslation.setXOffset(Integer.parseInt(items[0]));
					currentTranslation.setYOffset(Integer.parseInt(items[1]));
					currentTranslation.setScore(Double.parseDouble(items[2]));
				} else {
					currentTranslation.setXOffset(Integer.parseInt(items[0]));
					currentTranslation.setYOffset(Integer.parseInt(items[1]));
					currentTranslation.setAngle(Integer.parseInt(items[2]));
					currentTranslation.setScore(Double.parseDouble(items[3]));
				}
				alignments.add(currentTranslation);
				counter++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.alignments.setAlignments(alignments);
		this.changed("translations");
	}

	public Alignments getAlignments() {
		if (alignments==null) {
			alignments = new Alignments(new ArrayList<AlignmentSlice>());
		}
		return alignments;
	}

	/**
	 * Open a list editor and load the images selected by the user.
	 */
	public void openSeries() {
		ListEditor editor = new ListEditor();
		editor.show();
		if (editor.getList().isEmpty()) {
			return;
		}
		this.openSeries(editor.getList());
		this.changed("translations");
		this.changed("display");
	}
	
	/**
	 * Open a series of tif-images from the file list parameter, create an alignment slice for each image.  
	 * @param list : Vector of File
	 */
	public void openSeries(Vector<File> list) {
		int counter = 0;
		String firstPath = ((ShellFolder)list.elementAt(0)).getAbsolutePath();
		ArrayList<AlignmentSlice> alignments = this.getAlignments().getAlignments();
		if (alignments.isEmpty()) {
			alignments.add(new AlignmentSlice());
		}
		AlignmentSlice firstTranslation = alignments.get(0);
		Point dims = firstTranslation.readDimensions(firstPath);
		for (File file : list) {
			String path = file.getAbsolutePath();
			if (counter>=alignments.size()) {
				alignments.add(new AlignmentSlice());
			}
			AlignmentSlice alignment = alignments.get(counter);
			alignment.setPath(path);
			alignment.setWidth(dims.x);
			alignment.setHeight(dims.y);
			counter++;
		}
	}

	public void loadImagesForSelection(Vector<Integer> selection) {
		if (selection.size() > 0) {
			if (firstImage!=null && firstImageOptions != null) firstImageOptions.setColorModel(firstImage.getProcessor().getColorModel());
			if (secondImageOptions != null && secondImage!=null && secondImage.getProcessor()!=null) secondImageOptions.setColorModel(secondImage.getProcessor().getColorModel());
			int index = selection.elementAt(0).intValue();
			firstImageSlice = (AlignmentSlice)alignments.getAlignments().get(index);
			firstImageOffsetX = firstImageSlice.getXOffset();
			firstImageOffsetY = firstImageSlice.getYOffset();
			firstImageAngle = firstImageSlice.getAngle();
			firstImageOptions = firstImageSlice.getDisplayOptions();
			this.removeListeners(firstImage);
			firstImageSlice.setImage(firstImageSlice.loadImage());
			this.firstImage = firstImageSlice.getImage();
			if (firstImage==null) return;
			if (firstImageOptions.getColorModel() != null) firstImage.getProcessor().setColorModel(firstImageOptions.getColorModel());
			firstImage.setRoi(firstImageSlice.getRoi());
			this.changed("image");
		} else {
			firstImage = null;
			secondImage = null;
		}
		if (selection.size() > 1) {
			if (secondImageOptions != null && secondImage!=null && secondImage.getProcessor()!=null) secondImageOptions.setColorModel(secondImage.getProcessor().getColorModel());
			int index = selection.elementAt(selection.size()-1).intValue();
			secondImageSlice = (AlignmentSlice)alignments.getAlignments().get(index);
			secondImageOffsetX = secondImageSlice.getXOffset();
			secondImageOffsetY = secondImageSlice.getYOffset();
			secondImageAngle = secondImageSlice.getAngle();
			secondImageOptions = secondImageSlice.getDisplayOptions();
			this.removeListeners(secondImage);
			secondImageSlice.setImage(secondImageSlice.loadImage());
			this.secondImage = secondImageSlice.getImage();
			if (secondImage==null) return;
			if (secondImageOptions.getColorModel() != null) secondImage.getProcessor().setColorModel(secondImageOptions.getColorModel());
			secondImage.setRoi(secondImageSlice.getRoi());
			this.changed("secondImage");
		} else this.secondImage = null;
	}

	private void removeListeners(ImagePlus image) {
		if (image==null || image.getWindow()==null) return;
		this.removeMouseListeners(image);
		this.removeMouseMotionListeners(image);
	}

	protected void removeMouseMotionListeners(ImagePlus image) {
		ImageCanvas canvas =  image.getWindow().getCanvas();
		MouseMotionListener[] listeners = image.getWindow().getCanvas().getMouseMotionListeners();
		for (int i=0; i<listeners.length; i++) canvas.removeMouseMotionListener(listeners[i]);
	}

	protected void removeMouseListeners(ImagePlus image) {
		ImageCanvas canvas =  image.getWindow().getCanvas();
		MouseListener[] listeners = image.getWindow().getCanvas().getMouseListeners();
		for (int i=0; i<listeners.length; i++) canvas.removeMouseListener(listeners[i]);
	}

	public ImagePlus getFirstImage() {
		return firstImage;
	}

	public int getFirstImageOffsetX() {
		return firstImageOffsetX;
	}

	public void setFirstImageOffsetX(int firstImageOffsetX) {
		this.firstImageOffsetX = firstImageOffsetX;
	}

	public int getFirstImageOffsetY() {
		return firstImageOffsetY;
	}

	public void setFirstImageOffsetY(int firstImageOffsetY) {
		this.firstImageOffsetY = firstImageOffsetY;
	}
	
	public ImagePlus getSecondImage() {
		return secondImage;
	}

	public void setSecondImage(ImagePlus secondImage) {
		this.secondImage = secondImage;
	}

	public int getSecondImageOffsetX() {
		return secondImageOffsetX;
	}

	public void setSecondImageOffsetX(int secondImageOffsetX) {
		this.secondImageOffsetX = secondImageOffsetX;
	}

	public int getSecondImageOffsetY() {
		return secondImageOffsetY;
	}

	public void setSecondImageOffsetY(int secondImageOffsetY) {
		this.secondImageOffsetY = secondImageOffsetY;
	}

	public void applyTranslationsAndSaveSeries(File targetFile, boolean applyTranslations) {
		Rectangle zeroAndImageSize = null;
		if (applyTranslations) {
			zeroAndImageSize = this.getAlignments().calculateImageSize();
		}
		this.getAlignments().saveWithSize(targetFile, zeroAndImageSize, applyTranslations);
	}
	
	public ImageDisplayOptions getFirstImageOptions() {
		return firstImageOptions;
	}
	
	public ImageDisplayOptions getSecondImageOptions() {
		return secondImageOptions;
	}

	public void setAlignment(int index, int x, int y, int angle) {
		AlignmentSlice slice = (AlignmentSlice) this.getAlignments().getAlignments().get(index);
		slice.setXOffset(x);
		slice.setYOffset(y);
		slice.setAngle(angle);
		this.changed("translations");
		this.changed("display");
	}

	public void translateFollowingBy(int index, int x, int y, int angle) {
		int size = this.getAlignments().getAlignments().size();
		for (int i=index; i<size; i++) {
			AlignmentSlice slice = (AlignmentSlice) this.getAlignments().getAlignments().get(i);
			slice.setXOffset(slice.getXOffset() + x);
			slice.setYOffset(slice.getYOffset() + y);
			slice.setAngle((slice.getAngle() + angle) % 360);
		}
		this.changed("translations");
		this.changed("display");
	}

	public double calculateScore(int index1, int index2, int offsetX0, int offsetY0, int offsetX1, int offsetY1) {
		AlignmentSlice slice1 = (AlignmentSlice) this.getAlignments().getAlignments().get(index1);
		AlignmentSlice slice2 = (AlignmentSlice) this.getAlignments().getAlignments().get(index2);
		int x0 = Math.min(offsetX0, offsetX1);
		int y0 = Math.min(offsetY0, offsetY1);
		int x1 = Math.max(offsetX0 + slice1.getWidth(), offsetX1 + slice2.getWidth());
		int y1 = Math.max(offsetY0 + slice1.getHeight(), offsetY1 + slice2.getHeight());
		ImagePlus image1 = this.getFirstImage();
		ImagePlus image2 = this.getSecondImage();
		ImagePlus newImage1 = NewImage.createImage(image1.getTitle()+"-tmp", x1-x0, y1-y0, image1.getNSlices(), 
														image1.getBitDepth(), NewImage.FILL_BLACK);
		ImagePlus newImage2 = NewImage.createImage(image2.getTitle()+"-tmp", x1-x0, y1-y0, image2.getNSlices(), 
				image2.getBitDepth(), NewImage.FILL_BLACK);
		MRIInterpreter interpreter = new MRIInterpreter();
		interpreter.setBatchMode(true);
		
		WindowManager.setTempCurrentImage(image1);
		IJ.run("Select All");
		IJ.run("Copy");
		WindowManager.setTempCurrentImage(newImage1);
		IJ.makeRectangle(offsetX0-x0, offsetY0-y0, image1.getWidth(), image1.getHeight());
		IJ.run("Paste");
		IJ.run("Select None");
		WindowManager.setTempCurrentImage(null);
		
		WindowManager.setTempCurrentImage(image2);
		IJ.run("Select All");
		IJ.run("Copy");
		WindowManager.setTempCurrentImage(newImage2);
		IJ.makeRectangle(offsetX1-x0, offsetY1-y0, image2.getWidth(), image2.getHeight());
		IJ.run("Paste");
		IJ.run("Select None");		
		WindowManager.setTempCurrentImage(null);
		
		double score = AutoAlignImagesOperation.computeDifference(newImage1, newImage2);
		
		interpreter.setBatchMode(false);
		return score;
	}

	public void showScore(int index1, int index2, int offsetX0, int offsetY0, int offsetX1, int offsetY1) {
		double score = this.calculateScore(index1, index2, offsetX0, offsetY0, offsetX1, offsetY1);
		IJ.showStatus(offsetX1+", " + offsetY1 + ", " + score);
	}
	public void recalculateAndSetScore(int index) {
		AlignmentSlice slice1 = (AlignmentSlice) this.getAlignments().getAlignments().get(index-1);
		AlignmentSlice slice2 = (AlignmentSlice) this.getAlignments().getAlignments().get(index);
		double score = this.calculateScore(index-1, index, slice1.getXOffset(), 
													slice1.getYOffset(), slice2.getXOffset(), slice2.getYOffset());
		slice2.setScore(score);
		this.changed("translations");
	}

	public void saveTranslationsAs(File targetFile) {
		FileWriter fileWriter;
		BufferedWriter out = null;
			try {
				fileWriter = new FileWriter(targetFile);
				out = new BufferedWriter(fileWriter);
				ArrayList<AlignmentSlice> slices = this.getAlignments().getAlignments();
				for(AlignmentSlice currentSlice : slices) {
					currentSlice.printOn(out);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (out!=null) {
					try {
						out.close();
					} catch (IOException e1) {
						// ignore
					}
				}
			}
	}

	public void saveSelectionsAs(File targetFile) {
		DataOutputStream out = null;
		try {
			ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(targetFile));
			out = new DataOutputStream(new BufferedOutputStream(zos));
			RoiEncoder re = new RoiEncoder(out);
			ArrayList<AlignmentSlice> slices = this.getAlignments().getAlignments();
			int counter = 1;
			for (AlignmentSlice slice : slices) {
				String roiName = this.getRoiName(counter);
				Roi roi = slice.getRoi();
				if (roi==null){
					counter++;
					continue;
				}
				zos.putNextEntry(new ZipEntry(roiName));
				re.write(roi);
				out.flush();
				counter++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out!=null) {
				try {
					out.close();
				} catch (IOException e1) {
					// ignore
				}
			}
		}
	}

	protected String getRoiName(int counter) {
		int digits = Integer.toString(this.getAlignments().getAlignments().size()).length();
		int counterDigits = Integer.toString(counter).length();
		String result = Integer.toString(counter);
		for (int i=0; i<digits-counterDigits; i++) {
			result = "0" + result;
		}
		result = result + ".roi";
		return result;
	}

	public void openSelections(File aFile) {
		ZipInputStream zis = null;
		ByteArrayOutputStream in;
		try {
			zis = new ZipInputStream(new FileInputStream(aFile));
			byte[] buf = new byte[1024];
			int len;
			while (true) {
				ZipEntry entry = zis.getNextEntry();
				if (entry==null)
					{zis.close(); return;}
				String name = entry.getName();
				in = new ByteArrayOutputStream();
				while ((len = zis.read(buf)) > 0)
					in.write(buf, 0, len);
				in.close();
				byte[] bytes = in.toByteArray();
				RoiDecoder rd = new RoiDecoder(bytes, name);
				Roi roi = rd.getRoi();
				if (roi!=null) {
					String shortName = name.substring(0, name.lastIndexOf("."));
					int counter = Integer.parseInt(shortName) - 1;
					AlignmentSlice slice = (AlignmentSlice) this.getAlignments().getAlignments().get(counter);
					slice.setRoi(roi);
					if (roi!= null && firstImage!=null && slice==firstImageSlice) {
						roi.setImage(firstImage);
						firstImage.setRoi(roi);
					}
					if (roi != null && secondImage!=null && slice==secondImageSlice) {
						roi.setImage(secondImage);
						secondImage.setRoi(roi);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void clearOutsideAndSaveSeries(File targetFile) {
		Application app = this.getClearOutsideApplication();
		this.getAlignments().setApplication(app);
		this.applyTranslationsAndSaveSeries(targetFile, true);
	}

	protected Application getClearOutsideApplication() {
		Application app = new Application();
		Operation first = new InverseSelectionOperation();
		first.setKeepSource(false);
		app.addOperationAfterIndex(first, -1);
		Operation second = new ClearOperation();
		app.addOperationAfterIndex(second, 0);
		second.setInputForParameterTo(0, 0, 0);
		return app;
	}

	public void clearOutsideFillInsideAndSaveSeries(File targetFile) {
		Application app = this.getClearOutsideFillInsideApplication();
		this.getAlignments().setApplication(app);
		this.applyTranslationsAndSaveSeries(targetFile, true);
	}

	protected Application getClearOutsideFillInsideApplication() {
		Application app = new Application();
		Operation first = new FillOperation();
		app.addOperationAfterIndex(first, -1);
		Operation second = new InverseSelectionOperation();
		second.setKeepSource(false);
		app.addOperationAfterIndex(second, 0);
		second.setInputForParameterTo(0, 0, 0);
		Operation third = new ClearOperation();
		app.addOperationAfterIndex(third, 1);
		third.setInputForParameterTo(0, 1, 0);
		return app;
	}

	public void applyOperationToSeries(Operation op, File targetFile, boolean applyTranslations) {
		Application app = new Application();
		app.addOperationAfterIndex(op, -1);
		op.setKeepSource(false);
		this.getAlignments().setApplication(app);
		this.applyTranslationsAndSaveSeries(targetFile, applyTranslations);
	}

	public Object3DManager getObjectManager() {
		if (objectManager == null) objectManager = new Object3DManager(this);
		return objectManager;
	}

	public ObjectModelingWorkbenchView getView() {
		if (view==null) view = new ObjectModelingWorkbenchView(this);
		return view;
	}

	public void calculateSeriesRegistration(File targetFile) {
		ArrayList<File> files = this.getFileList();
		CalculateAlignmentTranslationsOperation op = new CalculateAlignmentTranslationsOperation();
		op.setShowResult(false);
		op.setResultFilename(targetFile.getAbsolutePath());
		op.setStepWidth(0);
		op.setImageList(files);
		op.execute();
	}

	public void fastCalculateSeriesRegistration(File targetFile, int stepWidth) {
		ArrayList<File> files = this.getFileList();
		CalculateAlignmentTranslationsOperation op = new CalculateAlignmentTranslationsOperation();
		op.setShowResult(false);
		op.setResultFilename(targetFile.getAbsolutePath());
		op.setStepWidth(stepWidth);
		op.setImageList(files);
		op.execute();
	}
	
	public ArrayList<File> getFileList() {
		ArrayList<File> result = new ArrayList<File>();
		ArrayList<AlignmentSlice> slices = this.getAlignments().getAlignments();
		for (AlignmentSlice slice : slices) {
			result.add(new File(slice.getPath()));
		}
		return result;
	}

	public void scaleSeries(ScaleImageOperation op, File targetFile) {
		Application app = new Application();
		app.addOperationAfterIndex(op, -1);
		op.setKeepSource(false);
		this.getAlignments().setApplication(app);
		this.getAlignments().scale(targetFile, this);
	}

	public void update(Observable sender, Object aspect) {
		if (aspect.equals("running")) {
			this.changed("translations");
			firstImage = null;
			secondImage = null;
			this.loadImagesForSelection(view.getSelectionIndices());
		}
	}

	private void loadImagesForSelection(int[] selectionIndices) {
		Vector<Integer> selection = new Vector<Integer>();
		for(int i=0; i<selectionIndices.length; i++) {
			selection.add(new Integer(selectionIndices[i]));
		}
		this.loadImagesForSelection(selection);
	}

	public void openHelp() {
		HelpSystem help = HelpSystem.getCurrent();
		help.openHelpFor(this.name());
	}
	
	public String name() {
		return "MRI Object Modeling Workbench";
	}

	public void setSelectionOnAllSlices() {
		ImagePlus image = firstImage;
		if (secondImage!=null) image = secondImage;
		Roi roi = image.getRoi();
		ArrayList<AlignmentSlice> slices = this.getAlignments().getAlignments();
		for (AlignmentSlice slice : slices) {
			slice.setRoi(roi);
		}
	}

	public int getFirstImageAngle() {
		return firstImageAngle;
	}

	public void setFirstImageAngle(int firstImageAngle) {
		this.firstImageAngle = firstImageAngle;
	}

	public int getSecondImageAngle() {
		return secondImageAngle;
	}

	public void setSecondImageAngle(int secondImageAngle) {
		this.secondImageAngle = secondImageAngle;
	}
}
