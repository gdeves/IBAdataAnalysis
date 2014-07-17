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
package tools.cellCounter;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.PointRoi;
import ij.gui.Roi;
import ij.io.FileInfo;
import ij.io.OpenDialog;
import ij.io.SaveDialog;
import imagejProxies.ColorPointRoi;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Observable;
import java.util.Vector;

import operations.analysis.MeasureOperation;
import operations.file.OpenImageOperation;
import operations.reporting.ReportMeasurementsOperation;


import applications.Application;

/**
 * The cell counter is a component that allows to start different cell counting algorithms. 
 * An algorithm creates a point-selection that can be corrected by the user before the result
 * is saved.
 * 
 * The cell counter is currently unused and its further development frozen. 
 * 
 * @author	Volker Baecker
 **/
public class CellCounter extends Observable {
	Vector<ObjectClass> objectClasses = new Vector<ObjectClass>();
	private String path;
	/**
	 * @param name
	 * @return
	 */
	public boolean existsObjectClass(String name) {
		return getObjectClassNames().contains(name);
	}

	/**
	 * 
	 */
	public void addObjectClass(String name) {
		objectClasses.add(new ObjectClass(name));
		this.changed("objectClasses");
	}

	/**
	 * @param string
	 */
	private void changed(String string) {
		this.setChanged();
		this.notifyObservers(string);
		this.clearChanged();
	}
	/**
	 * @return Returns the names.
	 */
	public Vector<String> getObjectClassNames() {
		Vector<String> names = new Vector<String>();
		Iterator<ObjectClass> it = this.objectClasses.iterator();
		while (it.hasNext()) {
			ObjectClass objectClass = it.next();
			names.add(objectClass.getName());
		}
		return names;
	}
	
	public void show() {
		CellCounterView view = new CellCounterView(this);
		view.setVisible(true);
	}

	/**
	 * @param roi
	 * @param imageFile
	 * @param selectedValues
	 */
	public void addSelectionsTo(PointRoi roi, FileInfo imageFile, Object[] selectedValues) {
		for (int i=0; i<selectedValues.length; i++) {
			String name = (String) selectedValues[i];
			ObjectClass objectClass = this.getObjectClass(name);
			if (objectClass.isEmpty() && imageFile.fileName!=null) {
				String fileName = imageFile.fileName;
				objectClass.setName(objectClass.getName() + " - " + fileName);
			}
			String fileName = imageFile.directory + File.separator + imageFile.fileName;
			objectClass.setSelection(roi, fileName);
		}
		this.changed("selections");
	}

	/**
	 * @param name
	 * @return
	 */
	private ObjectClass getObjectClass(String name) {
		ObjectClass result = null;
		Iterator<ObjectClass> it = this.objectClasses.iterator();
		while (it.hasNext()) {
			ObjectClass objectClass = it.next();
			if (objectClass.getName().equals(name)) result = objectClass;
		}
		return result;
	}

	/**
	 * @param name
	 * @return
	 */
	public Roi getCurrentSelection(Object[] names) {
		ColorPointRoi result = null;
		for (int i=0; i<names.length; i++) {
			String name = (String) names[i];
			ObjectClass objectClass = this.getObjectClass((String) name);
			if (result==null) {
				result = new ColorPointRoi(objectClass.getSelection().getPolygon().xpoints,
						objectClass.getSelection().getPolygon().ypoints,
						objectClass.getSelection().getPolygon().npoints,
						objectClass.getColor());
			} else {
				for (int j=0; j<objectClass.getSelection().getPolygon().npoints; j++) {
					result = result.addPoint(objectClass.getSelection().getPolygon().xpoints[j],
							objectClass.getSelection().getPolygon().ypoints[j], objectClass.getColor());
				}
			}
			
		}
		return result;
	}

	/**
	 * @param selectedValues
	 */
	public void setColor(Object[] names, Color color) {
		for (int i=0; i<names.length; i++) {
			String name = (String) names[i];
			this.getObjectClass(name).setColor(color);
		}
		
	}

	/**
	 * @param selectedValues
	 */
	public void deleteObjectClasses(Object[] selectedValues) {
		for (int i=0; i<selectedValues.length; i++) {
			ObjectClass objectClass = this.getObjectClass((String) selectedValues[i]); 
			objectClasses.remove(objectClass);
		}
		this.changed("objectClasses");
	}
	
	public int getCount(Object[] selectedValues) {
		int result = 0;
		for (int i=0; i<selectedValues.length; i++) {
			ObjectClass objectClass = this.getObjectClass((String) selectedValues[i]); 
			result = result + objectClass.getSelection().getPolygon().npoints;
		}
		return result;
	}

	/**
	 * 
	 */
	public void saveAs() {
		String defaultDir = OpenDialog.getDefaultDirectory();
		SaveDialog saveDialog = new SaveDialog("Save data", "object classes", ".cnt");
		String filename = saveDialog.getFileName();
		if (filename==null) {
			return;
		}
		String folder = saveDialog.getDirectory();
		path = folder + filename;
		OpenDialog.setDefaultDirectory(defaultDir);
		this.save(path);
	}

	/**
	 * @param path2
	 */
	public void save(String aPath) {
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

	/**
	 * @param output
	 * @throws IOException
	 */
	private void putOn(Writer output) throws IOException {
		Iterator<ObjectClass> it = this.objectClasses.iterator();
		while (it.hasNext()) {
			ObjectClass objectClass = it.next();
			objectClass.putOn(output);
		}
		
	}

	/**
	 * @return
	 */
	public String getPath() {
		return path;
	}

	/**
	 * 
	 */
	public void load() {
		OpenDialog openDialog = new OpenDialog("Open data", "object classes", ".cnt");
		String filename = openDialog.getFileName();
		if (filename==null) {
			return;
		}
		String folder = openDialog.getDirectory();
		path = folder + filename;
		OpenDialog.setDefaultDirectory(folder);
		this.open(path);
		this.changed("objectClasses");
	}

	/**
	 * @param path2
	 */
	private void open(String aPath) {
		BufferedReader input = null;
	    try {
	      //use buffering
	      input = new BufferedReader( new FileReader(new File(aPath)) );
	      this.readFrom(input);
	      this.path = aPath;
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

	/**
	 * @param input
	 */
	private void readFrom(BufferedReader input) {
		try {
			objectClasses = new Vector<ObjectClass>();
			while (true) {
				String line = input.readLine();
				if (line == null || line.trim().equals("")) return;
				ObjectClass current = new ObjectClass(line);
				objectClasses.add(current);
				line = input.readLine();
				int number = Integer.parseInt(line);
				String[] comp = input.readLine().split(",");
				float r = Float.parseFloat(comp[0]);
				float g = Float.parseFloat(comp[1]);
				float b = Float.parseFloat(comp[2]);
				Color color = new Color(r,g,b); 
				current.setColor(color);
				if (number==0) {
					input.readLine();
					continue;
				}
				line = input.readLine();
				current.setFileName(line);
				int[] x = new int[number];
				int[] y = new int[number];
				for (int i=0; i<number; i++) {
					line = input.readLine();
					String[] point = line.split(",");
					x[i] = Integer.parseInt(point[0]);
					y[i] = Integer.parseInt(point[1]);
				}
				PointRoi roi = new PointRoi(x, y, number);
				current.setSelection(roi, current.getFileName());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * @param selectedValues
	 */
	public void calculateFeatures(Object[] selectedValues) {
		for (int i=0; i<selectedValues.length; i++) {
			String objectClassName = (String) selectedValues[i];
			this.getObjectClass(objectClassName).calculateFeatures();
		}
	}

	/**
	 * @param selectedValues
	 * @param distance
	 */
	public void selectSimilar(Object[] selectedValues, double distance, String newObjectClassName) {
		ImagePlus image = IJ.getImage();
		if (image==null) return;
		this.calculateFeatures(selectedValues);
		this.selectAllCells();
		ObjectClass allCells = new ObjectClass("all cells");
		PointRoi aRoi = (PointRoi)image.getRoi();
		FileInfo imageFile = image.getOriginalFileInfo();
		String fileName = imageFile.directory + File.separator + imageFile.fileName;
		allCells.setSelection(aRoi, fileName);
		allCells.calculateFeatures();
		Vector<ObjectClass> comparisonObjectClasses = this.getObjectClasses(selectedValues);
		ObjectClass similar = allCells.newClassWithCellsSimilarTo(comparisonObjectClasses, distance, newObjectClassName);
		this.objectClasses.add(similar);
		this.changed("objectClasses");
	}

	/**
	 * @param selectedValues
	 * @return
	 */
	private Vector<ObjectClass> getObjectClasses(Object[] selectedValues) {
		Vector<ObjectClass> result = new Vector<ObjectClass>();
		for (int i=0; i<selectedValues.length; i++) {
			String objectClassName = (String) selectedValues[i];
			result.add(this.getObjectClass(objectClassName));
		}
		return result;
	}

	/**
	 * 
	 */
	public void selectAllCells() {
		Application newApp = Application.load("./_applications/particle counting/select all cells.cia");
		newApp.show();
		newApp.run();
		newApp.view().dispose();	
	}

	/**
	 * @param object
	 * 
	 */
	public void openSelectSimilarTool(Object object) {
		// todo ObjectClass objectClass = this.getObjectClass((String) object);
		// todo SelectSimilarObjectsTool tool = new SelectSimilarObjectsTool(objectClass, objectClasses);
		
	}

	public void addSelection(PointRoi roi, FileInfo imageFile) {
		String fileName = imageFile.directory + File.separator + imageFile.fileName;
		this.addObjectClass(fileName);
		ObjectClass objectClass = this.getObjectClass(fileName);
		objectClass.setSelection(roi, "");
		this.changed("selections");
	}

	public void reportAll() {
		Iterator<ObjectClass> it = objectClasses.iterator();
		OpenImageOperation openImage = new OpenImageOperation();
		MeasureOperation measure = new MeasureOperation();
		ReportMeasurementsOperation report = new ReportMeasurementsOperation();
		report.getFilenameFromUser();
		while(it.hasNext()) {
			ObjectClass objectClass = it.next();
			String path = objectClass.getFileName();
			Roi roi = objectClass.getSelection();
			if (objectClass.getFileName().equals("")) path = objectClass.getName();
			openImage.setPath(path);
			openImage.doIt();
			ImagePlus currentImage = openImage.getResult();
			currentImage.setRoi(roi);
			measure.setInputImage(currentImage);
			measure.doIt();
			currentImage = measure.getResult();
			report.setImageName(path);
			report.setInputImage(currentImage);
			report.setMeasurements(measure.getMeasurements());
			report.doIt();
		}
		
	}
}
