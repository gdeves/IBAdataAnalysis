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

import ij.ImagePlus;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.io.RoiDecoder;
import ij.io.RoiEncoder;
import ij.measure.ResultsTable;
import ij.process.ImageProcessor;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
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
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import operations.Operation;
import operations.analysis.MeasureOperation;
import utils.NumberUtil;

/**
 * A 3D object in the Object3DManager. The object has a name, a color, an opacity and a collection of selections,
 * each associated with a number of a slice. The object can create a stack with the object drawn on it. The object
 * can be saved to disk and read it again.  	
 * 
 * @author Volker Baecker
 */
public class Object3D implements Comparable<Object3D> {
	protected Hashtable<Integer, Roi> selections;
	protected Color color;
	protected String name;
	protected float alpha;
	protected boolean isVisible = true;

	public Object3D(String name) {
		super();
		this.setName(name);
	}

	public void addSelection(int slice, Roi roi) {
		this.getSelections().put(new Integer(slice), roi);
	}
	
	public void removeSelection(int slice) {
		this.getSelections().remove(new Integer(slice));
	}
	
	public Hashtable<Integer, Roi> getSelections() {
		if (selections==null) selections = new Hashtable<Integer, Roi>();
		return selections;
	}

	public Color getColor() {
		if (color == null) color = this.getDefaultColor();
		return color;
	}

	public Color getDefaultColor() {
		return Color.BLUE;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean equals(Object obj) {
		if (!(obj.getClass()==this.getClass())) return false;
		return ((Object3D)(obj)).getName().equals(this.getName());
	}

	public Vector<String> getSelectionNames() {
		Vector<String> result =  new Vector<String>();
		Enumeration<Integer> keys = this.getSelections().keys();
		while(keys.hasMoreElements()) {
			Integer key = keys.nextElement();
			String nameIndex = NumberUtil.intToStringWithNDigits(key.intValue()+1, 5);
			String name = nameIndex + ": " + this.getSelections().get(key).toString();
			result.add(0, name);
		}
		Collections.sort(result);
		return result;
	}

	public void deleteSelections(int[] selectionIndices) {
		for (int i=0; i<selectionIndices.length; i++) {
			this.removeSelection(selectionIndices[i]);
		}
		
	}

	public float getAlpha() {
		if (alpha==0) alpha = 0.5f;
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public void subtract(Object3D anObject, int[] slices) {
		for (int i=0; i<slices.length; i++) {
			Integer slice = new Integer(slices[i]);
			if (!this.getSelections().containsKey(slice)) continue;
			if (!anObject.getSelections().containsKey(slice)) continue;
			Roi roi1 = (Roi) this.getSelections().get(slice);
			Roi roi2 = (Roi) anObject.getSelections().get(slice);
			ShapeRoi shape1 = new ShapeRoi(roi1);
			ShapeRoi shape2 = new ShapeRoi(roi2);
			ShapeRoi shape3 = shape1.not(shape2);
			Roi newRoi = shape3.getRois()[0];
			this.getSelections().put(slice, newRoi);
		}
	}

	public void add(Object3D anObject, int[] slices) {
		for (int i=0; i<slices.length; i++) {
			Integer slice = new Integer(slices[i]);
		//	if (!this.getSelections().containsKey(slice)) continue;
			if (!anObject.getSelections().containsKey(slice)) continue;
			Roi roi1 = (Roi) this.getSelections().get(slice);
			Roi roi2 = (Roi) anObject.getSelections().get(slice);
			if (roi1==null) {
				Roi newRoi = (Roi) roi2.clone();
				this.getSelections().put(slice, newRoi);
				return;
			}
			ShapeRoi shape1 = new ShapeRoi(roi1);
			ShapeRoi shape2 = new ShapeRoi(roi2);
			ShapeRoi shape3 = shape1.or(shape2);
			Roi newRoi = shape3.getRois()[0];
			this.getSelections().put(slice, newRoi);
		}
	}

	public void save(File folder) {
		this.saveObjectProperties(folder);
		this.saveSelections(folder);
	}

	public void load(File folder) {
		this.loadObjectProperties(folder);
		this.loadSelections(folder);
	}
		
	protected void saveSelections(File folder) {
		DataOutputStream out = null;
		File targetFile = new File(folder.getAbsoluteFile() + File.separator + this.getName() + ".zip");
		try {
			ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(targetFile));
			out = new DataOutputStream(new BufferedOutputStream(zos));
			RoiEncoder re = new RoiEncoder(out);
			Enumeration<Integer> keys = this.getSelections().keys();
			int counter = 0;
			while (keys.hasMoreElements()) {
				Integer key = keys.nextElement();
				String roiName = "slice-" + key.toString();
				Roi roi = (Roi) this.getSelections().get(key);
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
	

	protected void loadSelections(File folder) {
		ZipInputStream zis = null;
		ByteArrayOutputStream in;
		this.selections = null;
		File aFile = new File(folder.getAbsoluteFile() + File.separator + this.getName() + ".zip");
		try {
			zis = new ZipInputStream(new FileInputStream(aFile));
			byte[] buf = new byte[1024];
			int len;
			while (true) {
				ZipEntry entry = zis.getNextEntry();
				if (entry==null)
					{zis.close(); return;}
				String name = entry.getName();
				Integer slice = Integer.decode(name.split("-")[1]);
				in = new ByteArrayOutputStream();
				while ((len = zis.read(buf)) > 0)
					in.write(buf, 0, len);
				in.close();
				byte[] bytes = in.toByteArray();
				RoiDecoder rd = new RoiDecoder(bytes, name);
				Roi roi = rd.getRoi();
				if (roi!=null) {
					this.getSelections().put(slice, roi);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void loadObjectProperties(File folder) {
		FileReader fileReader;
		BufferedReader in = null;
		File aFile = new File(folder.getAbsoluteFile() + File.separator + this.getName() + ".3dp");
		try {
			fileReader = new FileReader(aFile);
			in = new BufferedReader(fileReader);
			this.readPropertiesFrom(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void saveObjectProperties(File folder) {
		FileWriter fileWriter;
		BufferedWriter out = null;
		File aFile = new File(folder.getAbsoluteFile() + File.separator + this.getName() + ".3dp");
		try {
			fileWriter = new FileWriter(aFile);
			out = new BufferedWriter(fileWriter);
			this.printPropertiesOn(out);
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

	protected void printPropertiesOn(BufferedWriter out) throws IOException {
		Color color = this.getColor();
		out.write("Color:" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + "\n");
		out.write("Alpha:" + Float.toString(this.getAlpha()) + "\n");
	}
	

	protected void readPropertiesFrom(BufferedReader in) throws IOException {
		String line;
		while((line=in.readLine()) != null) {
			String[] property = line.split(":");
			String[] values = property[1].split(",");
			this.setProperty(property[0], values);
		}
	}

	protected void setProperty(String property, String[] values) {
		if (property.equals("Color")) this.setColor( new Color(Integer.parseInt(values[0]),
													 	       Integer.parseInt(values[1]),
													           Integer.parseInt(values[2])));
		if (property.equals("Alpha")) this.setAlpha(Float.parseFloat(values[0]));
	}

	public void paintSelectedSlicesOn(ImagePlus newImage, int[] selectedSlices, ArrayList<AlignmentSlice> slices, Rectangle zeroAndSize) {
		int stackSlice = 1;
		for (int i=0; i<selectedSlices.length; i++) {
			int slice = selectedSlices[i];
			if (!this.getSelections().keySet().contains(new Integer(slice))) {stackSlice++; continue;}
			AlignmentSlice alignment = (AlignmentSlice) slices.get(i);
			ImageProcessor ip = newImage.getImageStack().getProcessor(stackSlice);
			ip.setColor(this.getColor());
			this.paintSliceOn(slice, ip, alignment.getXOffset(), alignment.getYOffset(), alignment.getAngle(), alignment.getWidth(), alignment.getHeight(), zeroAndSize);
			newImage.setProcessor(null, ip);
			newImage.setStack(null, newImage.getStack());
			newImage.updateImage();
			newImage.updateAndDraw();
			stackSlice++;
		}
		
	}

	public int countObjectSlicesAmong(int[] selectedSlices) {
		int counter = 0;
		for (int i=0; i<selectedSlices.length; i++) {
			int slice = selectedSlices[i];
			if (this.getSelections().keySet().contains(new Integer(slice))) counter++;
		}
		return counter;
	}
	
	protected void paintSliceOn(int slice, ImageProcessor ip, int offsetX, int offsetY, int angle, int width, int height, Rectangle zeroAndSize) {
		double angleInRadians = -angle * (Math.PI / 180.0);
		Polygon form = getTransformatedForm(slice, offsetX, offsetY, width, height, zeroAndSize, angleInRadians);
		ip.fillPolygon(form);
	}

	public Polygon getTransformatedForm(int slice, int offsetX, int offsetY, int width, int height, Rectangle zeroAndSize, double angleInRadians) {
		Polygon form = ((Roi)this.getSelections().get(new Integer(slice))).getPolygon();	
		float[] points = new float[form.npoints*2];
		float[] rotatedPoints = new float[form.npoints*2];
		for (int i=0; i<form.npoints; i++) {
			points[2*i] = form.xpoints[i];
			points[2*i+1] = form.ypoints[i];
		}
		AffineTransform op = AffineTransform.getRotateInstance(angleInRadians, width/2 , height/2);
		op.transform(points, 0, rotatedPoints, 0, form.npoints);
		for (int i=0; i<form.npoints; i++) {
			form.xpoints[i] = Math.round(rotatedPoints[2*i]);  
			form.ypoints[i] = Math.round(rotatedPoints[2*i+1]); 
		}
		// form.translate(zeroAndSize.width-width+offsetX, zeroAndSize.height-height+offsetY);
		form.translate(offsetX - zeroAndSize.x  /*  -(zeroAndSize.width/2) */ -  (width /2) , 
				       offsetY - zeroAndSize.y  /* - (zeroAndSize.height/2) */ - (height / 2)) ; // test
		return form;
	}

	public void paintSliceOnImage(AlignmentSlice slice, int currentSlice, ImagePlus image, Rectangle zeroAndSize) {
		ImageProcessor ip = image.getProcessor();
		ip.setColor(this.getColor());
		this.paintSliceOn(currentSlice, ip, slice.getXOffset(), slice.getYOffset(), slice.getAngle(), slice.getWidth(), slice.getHeight(), zeroAndSize);
	}

	public void setIsVisible(boolean b) {
		this.isVisible = b;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public ResultsTable measureSliceOnImage(int sliceNumber, ImagePlus image, Operation operation) {
		Roi roi = (Roi) this.getSelections().get(new Integer(sliceNumber));
		image.setRoi(roi);
		MeasureOperation measure = (MeasureOperation)operation;
		measure.setInputImage(image);
		measure.setShowResult(false);
		measure.run();
		ResultsTable result = measure.getMeasurements();
		return result;
	}

	public int compareTo(Object3D object3D) {
		return -1 * object3D.getName().compareTo(this.getName());
	}

	public void paintSliceOn8BitImage(AlignmentSlice slice, int currentSlice, ImagePlus image, Rectangle zeroAndSize, int greyvalue) {
		ImageProcessor ip = image.getProcessor();
		ip.setColor(new Color(greyvalue,greyvalue,greyvalue));
		this.paintSliceOn(currentSlice, ip, slice.getXOffset(), slice.getYOffset(), slice.getAngle(), slice.getWidth(), slice.getHeight(), zeroAndSize);
	}
}
