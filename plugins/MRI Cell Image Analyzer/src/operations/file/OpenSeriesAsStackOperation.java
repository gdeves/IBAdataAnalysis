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
package operations.file;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileInfo;
import ij.io.Opener;
import ij.measure.Calibration;
import ij.plugin.DICOM_Sorter;
import ij.plugin.FolderOpener;
import ij.process.ImageProcessor;

import java.awt.image.ColorModel;
import java.io.File;

/**
 * Open a series of images as a stack. If the path is not set the series is defined by the file 
 * selected by the user. All images with the same basename are loaded into the stack. The order 
 * is determinated by the trailing numbers of the filenames. The numerical order is respected 
 * (e.g. 1<9<10<99<100, etc.).   
 * 
 * @author Volker Baecker (modification of the FolderOpener code from ImageJ by Wayne Rasband)
 */
public class OpenSeriesAsStackOperation extends OpenImageOperation {
	private static final long serialVersionUID = -8882570959009289125L;
	private FileInfo fi;
	
	public void doIt() {
		String thePath = this.getPath();
		if (path==null) {
			this.stopApplication();
			return;
		}
		int index = thePath.lastIndexOf(File.separator);
		String dir = thePath.substring(0, index+1);
		String filename = thePath.substring(index+1, thePath.length());
		result = this.loadSeriesAsStack(dir, filename);
	}
	
	public ImagePlus loadSeriesAsStack(String dir, String filename) {
		String directory = dir;
		String name = filename;
		String basename = this.getBasename(filename);
		String filter = basename;
		boolean convertToRGB = false;
		boolean convertToGrayscale = false;
		float scale = 100.0f;
		int increment = 1;
		String info1=null;
		if (name==null)
			return null;
		String[] list = (new File(directory)).list();
		if (list==null)
			return null;
		String title = directory;
		if (title.endsWith(File.separator))
			title = title.substring(0, title.length()-1);
		int index = title.lastIndexOf(File.separatorChar);
		if (index!=-1) title = title.substring(index + 1);
		if (title.endsWith(":"))
			title = title.substring(0, title.length()-1);
		list = sortFileList(list);
		if (IJ.debugMode) IJ.log("FolderOpener: "+directory+" ("+list.length+" files)");
		int width=0,height=0,bitDepth=0;
		ImageStack stack = null;
		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;
		Calibration cal = null;
		boolean allSameCalibration = true;
		IJ.resetEscape();		
		try {
			for (int i=0; i<list.length; i++) {
				if (list[i].endsWith(".txt"))
					continue;
				IJ.redirectErrorMessages();
				ImagePlus imp = (new Opener()).openImage(directory, list[i]);
				if (imp!=null) {
					width = imp.getWidth();
					height = imp.getHeight();
					bitDepth = imp.getBitDepth();
					fi = imp.getOriginalFileInfo();
					break;
				}
			}
			if (width==0) {
				IJ.error("Import Sequence", "This folder does not appear to contain any TIFF,\n"
				+ "JPEG, BMP, DICOM, GIF, FITS or PGM files.");
				return null;
			}

			if (filter!=null && (filter.equals("") || filter.equals("*")))
				filter = null;
			if (filter!=null) {
				int filteredImages = 0;
  				for (int i=0; i<list.length; i++) {
 					if (list[i].indexOf(filter)>=0)
 						filteredImages++;
 					else
 						list[i] = null;
 				}
  				if (filteredImages==0) {
  					IJ.error("None of the "+list.length+" files contain\n the string '"+filter+"' in their name.");
  					return null;
  				}
  				String[] list2 = new String[filteredImages];
  				int j = 0;
  				for (int i=0; i<list.length; i++) {
 					if (list[i]!=null)
 						list2[j++] = list[i];
 				}
  				list = list2;
  			}

			int n = list.length;
			int start = 1;
			if (start+n-1>list.length)
				n = list.length-start+1;
			int count = 0;
			int counter = 0;
			for (int i=start-1; i<list.length; i++) {
				if (list[i].endsWith(".txt"))
					continue;
				if ((counter++%increment)!=0)
					continue;
				Opener opener = new Opener();
				opener.setSilentMode(true);
				IJ.redirectErrorMessages();
				ImagePlus imp = opener.openImage(directory, list[i]);
				if (imp!=null && stack==null) {
					width = imp.getWidth();
					height = imp.getHeight();
					bitDepth = imp.getBitDepth();
					cal = imp.getCalibration();
					if (convertToRGB) bitDepth = 24;
					if (convertToGrayscale) bitDepth = 8;
					ColorModel cm = imp.getProcessor().getColorModel();
					if (scale<100.0)						
						stack = new ImageStack((int)(width*scale/100.0), (int)(height*scale/100.0), cm);
					else
						stack = new ImageStack(width, height, cm);
					info1 = (String)imp.getProperty("Info");
				}
				if (imp==null) {
					if (!list[i].startsWith("."))
						IJ.log(list[i] + ": unable to open");
					continue;
				}
				ImageProcessor ip = imp.getProcessor();
				int bitDepth2 = imp.getBitDepth();
				if (convertToRGB) {
					ip = ip.convertToRGB();
					bitDepth2 = 24;
				} else if(convertToGrayscale) {
					ip = ip.convertToByte(true);
					bitDepth2 = 8;
				}
				if (bitDepth2!=bitDepth) {
					if (bitDepth==8) {
						ip = ip.convertToByte(true);
						bitDepth2 = 8;
					} else if (bitDepth==24) {
						ip = ip.convertToRGB();
						bitDepth2 = 24;
					}
				}
				if (imp.getWidth()!=width || imp.getHeight()!=height)
					IJ.log(list[i] + ": wrong size; "+width+"x"+height+" expected, "+imp.getWidth()+"x"+imp.getHeight()+" found");
				else if (bitDepth2!=bitDepth) {
					IJ.log(list[i] + ": wrong bit depth; "+bitDepth+" expected, "+bitDepth2+" found");
				} else {
					count = stack.getSize()+1;
					IJ.showStatus(count+"/"+n);
					IJ.showProgress(count, n);
					if (scale<100.0)
						ip = ip.resize((int)(width*scale/100.0), (int)(height*scale/100.0));
					if (ip.getMin()<min) min = ip.getMin();
					if (ip.getMax()>max) max = ip.getMax();
					String label = imp.getTitle();
					if (imp.getCalibration().pixelWidth!=cal.pixelWidth)
						allSameCalibration = false;
					String info = (String)imp.getProperty("Info");
					if (info!=null)
						label += "\n" + info;
					stack.addSlice(label, ip);
				}
				if (count>=n)
					break;
				if (IJ.escapePressed())
					{IJ.beep(); break;}
				//System.gc();
			}
		} catch(OutOfMemoryError e) {
			IJ.outOfMemory("FolderOpener");
			if (stack!=null) stack.trim();
		}
		ImagePlus imp2 = null;
		if (stack!=null && stack.getSize()>0) {
			if (info1!=null && info1.lastIndexOf("7FE0,0010")>0)
				stack = (new DICOM_Sorter()).sort(stack);
			imp2 = new ImagePlus(title, stack);
			if (imp2.getType()==ImagePlus.GRAY16 || imp2.getType()==ImagePlus.GRAY32)
				imp2.getProcessor().setMinAndMax(min, max);
			imp2.setFileInfo(fi); // saves FileInfo of the first image
			if (allSameCalibration)
				imp2.setCalibration(cal); // use calibration from first image
			if (imp2.getStackSize()==1 && info1!=null)
				imp2.setProperty("Info", info1);
		}
		IJ.showProgress(1.0);
		return imp2;
	}
	
	protected String getBasename(String filename) {
		String result = "";
		int index = filename.lastIndexOf(".");
		if (index==-1) index = filename.length() - 1;
		for (int i=index-1; i>0; i--) {
			char in = filename.charAt(i);
			if (!Character.isDigit(in)) {
				index = i;
				break;
			}
		}
		result = result + filename.substring(0, index);
		return result;
	}

	String[] sortFileList(String[] list) {
		FolderOpener fo = new FolderOpener();
		String[] result= fo.sortFileList(list);
		return result; 
	}
}
