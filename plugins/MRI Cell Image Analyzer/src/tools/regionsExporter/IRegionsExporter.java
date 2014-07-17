/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2009 INSERM
 * MRI Cell Image Analyzer has been created at Montpellier RIO Imaging by Volker Baecker
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
package tools.regionsExporter;

import java.io.IOException;
import java.util.List;

import ij.ImagePlus;
import ij.gui.Roi;
import ij.measure.Calibration;

/**
 * Allows to define a number of regions on a series of images and to export these regions
 * as new images.
 * 
 * @author baecker
 */
public interface IRegionsExporter {
	
	/**
	 * Answer the complete path of the current image.
	 * 
	 * @return the path of the current image
	 */
	public String getImageID();
	public String getImageID(int i);
	public void setImageID(int i, String path);
	/**
	 * Answer a list of the image ids, i.e. the complete paths of the images.
	 * 
	 * @return a list of image ids
	 */
	public List<String> getImageIDs();
	public int getNumberOfImages();
	
	/**
	 * Answer the current image. The image will be loaded if necessary.
	 * 
	 * @return the current image
	 */
	public ImagePlus getImage();
	public ImagePlus getImage(int i);
	
	public boolean next();
	public boolean previous();
	public void first();
	public void last();
	/**
	 * Make image i the current image.
	 * 
	 * @param i
	 */
	public void set(int i);
	
	public List<Roi> getRegions(int i);
	public void addRegion(int i, Roi region);
	public void removeRegion(int i, Roi region);
	public List<Roi> getRegions();

	/**
	 * Add a region to the regions of the current image.
	 */
	public void addRegion(Roi region);
	public void removeRegion(Roi region);
	
	public Calibration getCalibration(int i);
	public void setCalibration(int i, Calibration calibration);
	
	/**
	 * Get the calibration of the current image.
	 * @return the calibration of the current image
	 */
	public Calibration getCalibration();
	public void setCalibration(Calibration calibration);
	/**
	 * Get the index of the current image.
	 * 
	 * @return  	the index of the current image
	 */
	public int getIndex();
	public String getTargetFolder(int i);
	public String getTargetFolder();
	public void setTargetFolder(int i, String path);
	public void setTargetFolder(String path);
	
	public boolean export() throws IOException;

	/**
	 * Export all regions of all images as separate images.
	 *  
	 * @return true if all regions have been successfully exported
	 * @throws IOException 
	 */
	public boolean exportAll() throws IOException;
}

