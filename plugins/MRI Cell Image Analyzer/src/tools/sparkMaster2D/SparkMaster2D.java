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
package tools.sparkMaster2D;
import gui.OperationsBox;
import ij.ImagePlus;
import ij.gui.Roi;
import java.awt.Window;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.SpinnerNumberModel;
import operations.Operation;
import operations.file.OpenSeriesAsStackOperation;
import operations.image.BinaryLiveOrDieOperation;

/**
 * Detect and measure Ca2+ sparks in confocal time series images. Implementation of the method described in
 * "B�ny�sz, Tam�s,  Chen-Izu, Ye,  Balke, C W,  Izu, Leighton T,
 *  A New Approach to the Detection and Statistical Classification of Ca2+ Sparks
 *  Biophysical Journal,  Jun 15, 2007"
 *  
 * @author	Volker Baecker
 **/
public class SparkMaster2D extends Observable {
	// input values
	protected ImageListModel images = new ImageListModel();
	protected SparkMaster2DOptionsView optionsView;
	private SparkMaster2DView view;
	// parameter per image
	protected SpinnerNumberModel normalizationFrame = new SpinnerNumberModel();
	protected boolean useProjection = false;
	protected DefaultListModel background = new DefaultListModel();;
	protected DefaultListModel roi = new DefaultListModel();
	protected DefaultListModel excludedRegions = new DefaultListModel();
	protected SpinnerNumberModel startFrame = new SpinnerNumberModel();
	protected SpinnerNumberModel endFrame = new SpinnerNumberModel();
	// options
	boolean maskCell = true;
	DefaultComboBoxModel thresholdOperations = null;
	DefaultComboBoxModel smoothingOperations = null;
	float sparksThreshold = 0.075f;
	Operation liveOrDieOperation = new BinaryLiveOrDieOperation();
	int statisticalFilterRadius = 1;
	double alphaConfidenceThreshold = 0.01;
	double betaConfidenceThreshold = 0.025;
	private Thread task;
	boolean stopped = false;
	int progressMin = 0;
	int progressMax = 0;
	int progress = 0;
	String message = "";
	boolean batchMode = false;
	protected ImagePlus currentImage;
	public boolean showSparkCandidates = true;
	private boolean useBatchMode = true;
	private boolean closeImages = true;
	private boolean stopAfterEachStep = false;
	protected boolean smoothImage = true;
	protected boolean doOnlyNormalization = false;
	protected int normalizationRadius = 1;
	
	public SparkMaster2D() {
		((BinaryLiveOrDieOperation) liveOrDieOperation).setIterations(2);
		((BinaryLiveOrDieOperation) liveOrDieOperation).setCountThreshold(20);
		((BinaryLiveOrDieOperation) liveOrDieOperation).setRadius(3);
		normalizationFrame.setMinimum(1);
		normalizationFrame.setValue(1);
		startFrame.setMinimum(1);
		startFrame.setValue(1);
		endFrame.setMinimum(1);
		endFrame.setValue(1);
		OperationsBox segmentationOperationsBox = OperationsBox.load("_operations/basic/segmentation.cio");
		thresholdOperations = new DefaultComboBoxModel(new Vector<Operation>(segmentationOperationsBox.getOperations()));
		OperationsBox smoothingOperationsBox = OperationsBox.load("_operations/basic/smoothing.cio");
		smoothingOperations = new DefaultComboBoxModel(new Vector<Operation>(smoothingOperationsBox.getOperations()));
		loadOptions();
	}
	
	public void showOptions() {
		this.getOptionsView().setVisible(true);
	}

	private Window getOptionsView() {
		if (optionsView==null) optionsView = new SparkMaster2DOptionsView(this);
		return optionsView;
	}

	public void show() {
		this.getView().setVisible(true);
	}

	public SparkMaster2DView getView() {
		if (view==null) view = new SparkMaster2DView(this);
		return view;
	}

	public void addFiles(Vector<File> vector) {
		images.addAll(vector);
	}

	private void changed(String string) {
		this.setChanged();
		this.notifyObservers(string);
		this.clearChanged();
	}

	public ImageListModel getImages() {
		return images;
	}

	public void removeImages(Object[] selectedValues) {
		images.removeImages(selectedValues);
	}

	public void openImage(String selectedValue) {
		OpenSeriesAsStackOperation op = new OpenSeriesAsStackOperation();
		op.setPath((String)selectedValue);
		if (isBatchMode()) {
			op.setShowResult(false);
		}
		op.run();
		currentImage = op.getResult();
		if (currentImage==null) return;
		this.endFrame.setValue(currentImage.getNSlices());
		this.loadImageParameters(selectedValue);
	}

	public int getNormalizationFrame() {
		return ((Integer)normalizationFrame.getValue()).intValue();
	}

	public void setUseProjection(boolean b) {
		this.useProjection = b;
		this.changed("use projection");
	}

	public Roi getRoi() {
		if (roi.isEmpty()) return null;
		return (Roi)roi.getElementAt(0);
	}
	
	public Roi getBackground() {
		if (background.isEmpty()) return null;
		return (Roi)background.getElementAt(0);
	}

	public ArrayList<Object> getExcludedRegions() {
		ArrayList<Object> regions = new ArrayList<Object>();
		regions.addAll(Arrays.asList(excludedRegions.toArray()));
		return regions;
	}

	public int getEndFrame() {
		return ((Integer)endFrame.getValue()).intValue();
	}

	public int getStartFrame() {
		return ((Integer)startFrame.getValue()).intValue();
	}

	public boolean getMaskCell() {
		return maskCell;
	}

	public void setMaskCell(boolean value) {
		maskCell = value;
		this.changed("mask cell");
	}

	public void setSparksThreshold(float f) {
		sparksThreshold = f;
		this.changed("sparks threshold");
	}

	public void setStatisticalFilterRadius(int value) {
		statisticalFilterRadius = value;
		this.changed("statistical filter radius");
	}

	public void setAlphaConfidenceThreshold(double d) {
		alphaConfidenceThreshold = d;
		this.changed("alpha confidence threshold");
	}

	public void setBetaConfidenceThreshold(double d) {
		betaConfidenceThreshold = d;
		this.changed("beta confidence threshold");
	}
	
	public void saveOptions() throws IOException {
		FileWriter fileWriter;
		BufferedWriter out = null;
		try {
			fileWriter = new FileWriter(getOptionsFilename());
			out = new BufferedWriter(fileWriter);
			this.printOptionsOn(out);
		} catch (IOException e) {
			if (out!=null) {
				try {
					out.close();
				} catch (IOException e1) {
					// ignore
				}
			}
			throw e;
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
	
	public void loadOptions() {
		File optionsFile = new File(this.getOptionsFilename());
		if (!optionsFile.exists()) return;
		FileReader fileReader;
		BufferedReader in = null;
		try {
			fileReader = new FileReader(this.getOptionsFilename());
			in = new BufferedReader(fileReader);
			String[] line =(in.readLine()).split("=");
			this.setMaskCell(Boolean.parseBoolean(line[1].trim()));
			Operation op = Operation.readFrom(in);
			this.replaceOperation(thresholdOperations, op);
			line =(in.readLine()).split("=");
			this.setSmoothImage(Boolean.parseBoolean(line[1].trim()));
			Operation op2 = Operation.readFrom(in);
			this.replaceOperation(smoothingOperations, op2);
			line =(in.readLine()).split("=");
			this.setSparksThreshold(Float.parseFloat(line[1].trim()));
			liveOrDieOperation = Operation.readFrom(in);
			line =(in.readLine()).split("=");
			this.setStatisticalFilterRadius(Integer.parseInt(line[1].trim()));
			line =(in.readLine()).split("=");
			this.setAlphaConfidenceThreshold(Double.parseDouble(line[1].trim()));
			line =(in.readLine()).split("=");
			this.setBetaConfidenceThreshold(Double.parseDouble(line[1].trim()));
			line =(in.readLine()).split("=");
			this.setDoOnlyNormalization(Boolean.parseBoolean(line[1].trim()));
			line =(in.readLine()).split("=");
			this.setNormalizationRadius(Integer.parseInt(line[1].trim()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in!=null) {
				try {
					in.close();
				} catch (IOException e1) {
					// ignore
				}
			}
		}
	}
	
	private void replaceOperation(DefaultComboBoxModel operations, Operation anOperation) {
		for (int i=0; i<operations.getSize(); i++) {
			Operation current = (Operation) operations.getElementAt(i);
			if (current.name().equals(anOperation.name())) {
				operations.removeElement(current);
				operations.insertElementAt(anOperation, i);
			}
		}
		operations.setSelectedItem(anOperation);
	}

	private void printOptionsOn(BufferedWriter out) throws IOException {
		out.write("mask_cell = " + Boolean.toString(this.maskCell)+"\n");
		Operation op = (Operation) this.thresholdOperations.getSelectedItem();
		op.printOn(out);
		out.write("smooth_image = " + Boolean.toString(this.isSmoothImage())+"\n");
		op = (Operation) this.smoothingOperations.getSelectedItem();
		op.printOn(out);
		out.write("sparks_threshold = " + Float.toString(this.sparksThreshold)+"\n");
		op = (Operation) this.liveOrDieOperation;
		op.printOn(out);
		out.write("statistical_filter_radius = " + Integer.toString(this.statisticalFilterRadius)+"\n");
		out.write("alpha_confidence_threshold = " + Double.toString(this.alphaConfidenceThreshold)+"\n");
		out.write("beta_confidence_threshold = " + Double.toString(this.betaConfidenceThreshold)+"\n");
		out.write("do_only_normalization = " + Boolean.toString(this.isDoOnlyNormalization())+"\n");
		out.write("normalization_radius = " + Integer.toString(this.getNormalizationRadius())+"\n");
	}

	public String getOptionsFilename() {
		return "spark_master_2d_config.txt";
	}

	public void applyParametersTo(String selectedValue) {
		String path = parameterFilenameFor(selectedValue);
		try {
			saveImageParameters(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String parameterFilenameFor(String selectedValue) {
		String[] components = selectedValue.split("\\" + File.separator);
		String fileName = components[components.length-1];
		int index = selectedValue.lastIndexOf(fileName);
		String path = selectedValue.substring(0, index);
		path += "." + fileName;
		path += ".sm2";
		return path;
	}

	protected void saveImageParameters(String absolutePath) throws IOException {
		FileWriter fileWriter;
		BufferedWriter out = null;
		try {
			fileWriter = new FileWriter(absolutePath);
			out = new BufferedWriter(fileWriter);
			this.printParametersOn(out);
		} catch (IOException e) {
			if (out!=null) {
				try {
					out.close();
				} catch (IOException e1) {
					// ignore
				}
			}
			throw e;
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

	private void printParametersOn(BufferedWriter out) throws IOException {
		out.write("normalization_frame = "+ this.getNormalizationFrame() + "\n");
		out.write("use_projection = " + this.useProjection + "\n");
		out.write("background = " + this.getBackground() + "\n");
		out.write("roi = " + this.getRoi() + "\n");
		out.write("[exclude_regions start]"+ "\n");
		Enumeration<?> regions = excludedRegions.elements();
		while (regions.hasMoreElements()) {
			Roi region = (Roi)regions.nextElement();
			out.write(region+"\n");
		}
		out.write("[exclude_regions end]"+ "\n");
		out.write("start frame = " + this.getStartFrame() + "\n");
		out.write("end frame = " + this.getEndFrame() + "\n");
	}
	
	private void loadImageParameters(String selectedValue) {
		String path = parameterFilenameFor(selectedValue);
		File parameterFile = new File(path);
		if (!parameterFile.exists()) return;
		FileReader fileReader;
		BufferedReader in = null;
		try {
			fileReader = new FileReader(path);
			in = new BufferedReader(fileReader);
			String[] lineComponents = in.readLine().split("=");
			normalizationFrame.setValue(Integer.parseInt(lineComponents[1].trim()));
			lineComponents = in.readLine().split("=");
			setUseProjection(Boolean.parseBoolean(lineComponents[1].trim()));
			String line = in.readLine();
			background.removeAllElements();
			background.addElement(this.createRectangleRoiFrom(line));
			line = in.readLine();
			roi.removeAllElements();
			roi.addElement(this.createRectangleRoiFrom(line));
			line = in.readLine();
			excludedRegions.removeAllElements();
			while ((line=in.readLine()).contains("Roi")) {
				excludedRegions.addElement(this.createRectangleRoiFrom(line));
			}
			lineComponents = in.readLine().split("=");
			startFrame.setValue(Integer.parseInt(lineComponents[1].trim()));
			lineComponents = in.readLine().split("=");
			endFrame.setValue(Integer.parseInt(lineComponents[1].trim()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in!=null) {
				try {
					in.close();
				} catch (IOException e1) {
					// ignore
				}
			}
		}
		
	}

	protected Roi createRectangleRoiFrom(String line) {
		int startIndex = line.indexOf("[");
		int endIndex = line.indexOf("]"); 
		String parameter = line.substring(startIndex+1, endIndex);
		String[] components = parameter.split(",");
		int x = Integer.parseInt(components[1].split("=")[1].trim()); 
		int y = Integer.parseInt(components[2].split("=")[1].trim());
		int width = Integer.parseInt(components[3].split("=")[1].trim());
		int height = Integer.parseInt(components[4].split("=")[1].trim());
		Roi roi = new Roi(x,y,width,height);
		return roi;
	}

	public void run() {
		stopped = false;
		if (this.isDoOnlyNormalization()) 
			task = new Thread(new SparkMaster2DBaecker(this));
		else
			task = new Thread(new SparkMaster2DBanyasz(this));
			// task = new Thread(new SparkMaster2DAptel(this));
		task.start();
	}

	public void stop() {
		stopped = true;
	}

	public void setProgressMin(int i) {
		progressMin = i;
		this.changed("progress min");
	}

	public void setProgressMax(int i) {
		progressMax = i;
		this.changed("progress max");
	}

	public void setProgress(int i) {
		progress = i;
		this.changed("progress");
	}

	public void setMessage(String message) {
		this.message = message;
		this.changed("message");
	}

	public boolean isBatchMode() {
		return batchMode;
	}

	public void setBatchMode(boolean batchMode) {
		this.batchMode = batchMode;
		liveOrDieOperation.getInterpreter().setBatchMode(batchMode);
	}

	public boolean useBatchMode() {
		return this.useBatchMode ;
	}

	public void setUseBatchMode(boolean useBatchMode) {
		this.useBatchMode = useBatchMode;
	}

	public boolean closeImages() {
		return this.closeImages ;
	}

	public void setCloseImages(boolean closeImages) {
		this.closeImages = closeImages;
	}

	public boolean stopAfterEachStep() {
		return stopAfterEachStep;
	}

	public void setStopAfterEachStep(boolean stopAfterEachStep) {
		this.stopAfterEachStep = stopAfterEachStep;
	}

	public boolean isSmoothImage() {
		return smoothImage;
	}

	public void setSmoothImage(boolean smoothImage) {
		this.smoothImage = smoothImage;
	}

	public boolean isDoOnlyNormalization() {
		return doOnlyNormalization;
	}

	public void setDoOnlyNormalization(boolean doOnlyNormalization) {
		this.doOnlyNormalization = doOnlyNormalization;
	}

	public int getNormalizationRadius() {
		return normalizationRadius;
	}

	public void setNormalizationRadius(int normalizationRadius) {
		this.normalizationRadius = normalizationRadius;
	}

}
