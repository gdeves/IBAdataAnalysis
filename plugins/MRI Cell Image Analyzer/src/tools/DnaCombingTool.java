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

import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.io.OpenDialog;
import ij.io.SaveDialog;
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Vector;

import operations.reporting.ReportSegmentsOperation;


/**
 * Manually measure replication sites in combed dna molecules. The dna-molecules are
 * stained in red and sites on the molecules at which replication takes place are
 * stained in green. The tool allows to measure the total length of the molecule and
 * the lengthes and distances of the green fragments. 
 * 
 * @author	Volker Baecker
 **/
public class DnaCombingTool extends Observable {
	protected DNACombingToolView view;
	protected ArrayList<Roi> selections;
	protected ArrayList<String> imagePaths;
	protected ArrayList<Boolean> firstSegmentIsGreen;
	protected ArrayList<Integer> indexInImage;
	protected Vector<String> selectionStrings;
	private ReportSegmentsOperation reportOperation;
	private String path;
	
	public ArrayList<Integer> getIndexInImage() {
		if (indexInImage == null) {
			indexInImage = new ArrayList<Integer>();
		}
		return indexInImage;
	}

	public ArrayList<Boolean> getFirstSegmentIsGreen() {
		if (firstSegmentIsGreen==null) {
			firstSegmentIsGreen = new ArrayList<Boolean>();
		}
		return firstSegmentIsGreen;
 	}

	public ArrayList<Roi> getSelections() {
		if (selections == null) {
			selections = new ArrayList<Roi>();
		}
		return selections;
	}
	
	private void changed(String anAspect) {
		this.setChanged();
		this.notifyObservers(anAspect);
	}
	
	public void show() {
		this.view().setVisible(true);
		this.changed("list");
	}

	public DNACombingToolView view() {
		if (this.view == null) {
			this.view = new DNACombingToolView(this);
		}
		return this.view;
	}
	
	public void addSelection(Boolean firstSegmentIsGreen, String imagePath, Roi selection) {
		if (selection==null) return;
		int pos = this.getSelections().size();
		if (this.getImagePaths().contains(imagePath)) {
			pos = this.getImagePaths().lastIndexOf(imagePath) + 1;
		}
		Integer index = this.getNextIndexFor(imagePath);
		this.getFirstSegmentIsGreen().add(pos, firstSegmentIsGreen);
		this.getImagePaths().add(pos, imagePath);
		this.getSelections().add(pos, selection);
		this.getIndexInImage().add(pos, index);
		this.addSelectionStringFor(firstSegmentIsGreen, imagePath, selection, index, pos);
		this.changed("selections");
	}
	
	private void addSelectionStringFor(Boolean firstSegmentIsGreen2, String imagePath, Roi selection, Integer index, int pos) {
		String result = "";
		int startIndex = imagePath.lastIndexOf(File.separator)+1;
		int endIndex = imagePath.lastIndexOf(".");
		String imageName = imagePath.substring(startIndex, endIndex);
		result = result + imageName + " - ";
		result = result + index.toString();
		this.getSelectionStrings().add(pos, result);
	}
	
	private Integer getNextIndexFor(String imagePath) {
		int index = 0;
		Iterator<String> it = this.getImagePaths().iterator();
		int counter = 0;
		while(it.hasNext()) {
			String path = it.next();
			if (path.equals(imagePath)) {
				int existingIndex = this.getIndexInImage().get(counter).intValue();
				index = Math.max(index, existingIndex);
			}
			counter++;
		}
		return new Integer(index+1);
	}

	public ArrayList<String> getImagePaths() {
		if (imagePaths==null) {
			imagePaths = new ArrayList<String>();
		}
		return imagePaths;
	}

	public Vector<String> getSelectionStrings() {
		if (this.selectionStrings==null) {
			this.selectionStrings = new Vector<String>();
		}
		return selectionStrings;
	}

	public void showSelections(int[] selectedIndices) {
		ImagePlus inputImage = WindowManager.getCurrentImage();
		if (inputImage==null) return;
		for (int i=0; i<selectedIndices.length; i++) {
			int index = selectedIndices[i];
			Roi aRoi = this.getSelections().get(index);
			inputImage.setRoi(aRoi);
		}
		
	}

	public void removeSelections(int[] selectedIndices) {
		for (int i=0; i<selectedIndices.length; i++) {
			int index = selectedIndices[i];
			this.getFirstSegmentIsGreen().remove(index);
			this.getImagePaths().remove(index);
			this.getSelections().remove(index);
			this.getIndexInImage().remove(index);
			this.getSelectionStrings().remove(index);
		}
		this.changed("selections");
	}

	public void measure() {
		reportOperation = new ReportSegmentsOperation();
		Vector<String> images = new Vector<String>();
		Iterator<String> it = this.getImagePaths().iterator();
		while (it.hasNext()) {
			String pathName = it.next();
			if (!images.contains(pathName)) {
				images.add(pathName);
			}
		}
		it = images.iterator();
		while (it.hasNext()) {
			String path = it.next();
			this.measureImage(path);
		}
	}

	private void measureImage(String path) {
		ArrayList<Line2D> baseSegments = this.getBaseSegments(path);
		ArrayList<Line2D> subsegments = this.getSubsegments(path);
		reportOperation.setBaseSegments(baseSegments);
		reportOperation.setSegments(subsegments);
		reportOperation.setImageName(path);
		reportOperation.run();
		reportOperation.setOutputPath(reportOperation.getOutputPath());
	}

	private ArrayList<Line2D> getSubsegments(String path) {
		ArrayList<Line2D> result = new ArrayList<Line2D>();
		Iterator<String> it = this.getImagePaths().iterator();
		int index = 0;
		while (it.hasNext()) {
			String currentPath = it.next();
			if (!currentPath.equals(path)){index++; continue;}
			result.addAll(this.getSubsegmentsForIndex(index));
			index++;
		}
		return result;
	}

	private ArrayList<Line2D> getSubsegmentsForIndex(int index) {
		ArrayList<Line2D> result = new ArrayList<Line2D>();
		Polygon currentPolygon = (this.getSelections().get(index)).getPolygon();
		int pointCounter = 0;
		if (!this.getFirstSegmentIsGreen().get(index).booleanValue()) {
			pointCounter = 1;
		}
		while(pointCounter<currentPolygon.npoints-1) {
			Line2D line = new Line2D.Float(currentPolygon.xpoints[pointCounter], 
					   currentPolygon.ypoints[pointCounter],								
					   currentPolygon.xpoints[pointCounter+1],
					   currentPolygon.ypoints[pointCounter+1]
					   );
			result.add(line);
			pointCounter = pointCounter + 2;
		}
		return result;
	}

	private ArrayList<Line2D> getBaseSegments(String path) {
		ArrayList<Line2D> result = new ArrayList<Line2D>();
		Iterator<String> it = this.getImagePaths().iterator();
		int index = 0;
		while (it.hasNext()) {
			String currentPath = it.next();
			if (!currentPath.equals(path)){index++; continue;}
			Polygon currentPolygon = (this.getSelections().get(index)).getPolygon();
			Line2D line = new Line2D.Float(currentPolygon.xpoints[0], 
										   currentPolygon.ypoints[0],								
										   currentPolygon.xpoints[currentPolygon.npoints-1],
										   currentPolygon.ypoints[currentPolygon.npoints-1]
										   );
			result.add(line);
			index++;
		}
		return result;
	}

	public void saveAs() {
		String defaultDir = OpenDialog.getDefaultDirectory();
		SaveDialog saveDialog = new SaveDialog("Save data", "selection", ".sel");
		String filename = saveDialog.getFileName();
		if (filename==null) {
			return;
		}
		String folder = saveDialog.getDirectory();
		path = folder + filename;
		OpenDialog.setDefaultDirectory(defaultDir);
		this.save(path);
	}

	public void save(String path) {
		   Writer output = null;
		    try {
		      //use buffering
		      output = new BufferedWriter( new FileWriter(new File(path)) );
		      this.putOn(output);
		    } catch (IOException e) {
				e.printStackTrace();
			}
		    finally {
		      //flush and close both "output" and its underlying FileWriter
		      if (output != null)
				try {
					output.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		    }
	}

	private void putOn(Writer output) {
		try {
			output.write(path + "\n");
			output.write(this.getSelections().size()+"\n");
			for (int i=0; i<this.getSelections().size(); i++) {
				String name = this.getSelectionStrings().get(i);
				output.write(name + "\n");
				
				PolygonRoi aRoi = (PolygonRoi)this.getSelections().get(i);
				this.putSelectionOn(aRoi, output);
				
				String imagePath = this.getImagePaths().get(i);
				output.write(imagePath + "\n");
				
				Boolean isGreen = this.getFirstSegmentIsGreen().get(i);
				output.write(isGreen + "\n");
				
				Integer index = this.getIndexInImage().get(i);
				output.write(index + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param roi
	 * @param output
	 */
	private void putSelectionOn(PolygonRoi roi, Writer output) {
		try {
			output.write(roi.getPolygon().npoints + "\n");
			for (int i=0; i<roi.getPolygon().npoints; i++) {
				output.write(roi.getPolygon().xpoints[i] + "," + roi.getPolygon().ypoints[i] + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void open() {
		String defaultDir = OpenDialog.getDefaultDirectory();
		OpenDialog openDialog = new OpenDialog("Open data", "selection", ".sel");
		String filename = openDialog.getFileName();
		if (filename==null) {
			return;
		}
		String folder = openDialog.getDirectory();
		path = folder + filename;
		OpenDialog.setDefaultDirectory(defaultDir);
		this.open(path);
		this.changed("selections");
	}

	private void open(String aPath) {
		BufferedReader input = null;
	    try {
	      //use buffering
	    	input = new BufferedReader( new FileReader(new File(path)) );
	      this.readFrom(input);
	    } catch (IOException e) {
			e.printStackTrace();
		}
	    finally {
	      //flush and close both "output" and its underlying FileWriter
	      if (input != null)
			try {
				input.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	    }
		
	}

	private void readFrom(BufferedReader input) {
		try {
			input.readLine();
			int numberOfSelections = Integer.parseInt(input.readLine());
			this.reset();
			for (int i=0; i<numberOfSelections; i++) {
				this.getSelectionStrings().add(input.readLine());
				Roi aRoi = this.readSelection(input);
				this.getSelections().add(aRoi);
				this.getImagePaths().add(input.readLine());
				this.getFirstSegmentIsGreen().add(new Boolean(Boolean.parseBoolean(input.readLine())));
				this.getIndexInImage().add(new Integer(Integer.parseInt(input.readLine())));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private Roi readSelection(BufferedReader input) {
		int numberOfPoints = 0;
		int[] x = new int[0];
		int[] y = new int[0];
		try {
			numberOfPoints = Integer.parseInt(input.readLine());
			x = new int[numberOfPoints];
			y = new int[numberOfPoints];
			for (int i = 0; i < numberOfPoints; i++) {
				String line;
				line = input.readLine();
				String[] point = line.split(",");
				x[i] = Integer.parseInt(point[0]);
				y[i] = Integer.parseInt(point[1]);
			}
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		PolygonRoi aRoi = new PolygonRoi(x, y, numberOfPoints, Roi.POLYLINE);
		return aRoi;
	}

	private void reset() {
		selections = null;
		imagePaths = null;
		firstSegmentIsGreen = null;
		indexInImage = null;
		selectionStrings = null;
	}

	public String getPath() {
		return path;
	}
	
	static public void createAndShow() {
		DnaCombingTool app = new DnaCombingTool();
		app.show();
	}
}
