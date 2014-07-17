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
package objectModelingWorkbench;

import ij.ImagePlus;
import ij.gui.Roi;
import ij.io.FileInfo;
import ij.io.TiffDecoder;

import java.awt.Point;
import java.io.File;
import java.io.IOException;

import operations.file.OpenImageOperation;

/**
 * A slice of an image stack. The slice knows the dimensions of the image, the path to the image and can 
 * opens the image. It can store a ROI and the display options for the image.
 * 
 * @author Volker Bäcker
 */
public class DisplaySlice {
	protected ImagePlus image;
	protected int width;
	protected int height;
	protected String path;
	protected ImageDisplayOptions displayOptions;
	protected Roi roi;

	public DisplaySlice() {
		super();
	}

	public DisplaySlice(ImagePlus anImage) {
		image = anImage;
		image.setImage(image.getProcessor().createImage());
		width = anImage.getWidth();
		height = anImage.getHeight();
		this.path = anImage.getOriginalFileInfo().directory + File.separator + anImage.getOriginalFileInfo().fileName;
	}
	
	public DisplaySlice(String path) {
		setPath(path);
		Point dims = readDimensions(path);
		this.width = dims.x;
		this.height = dims.y;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	public Point readDimensions(String path) {
		int width = 0;
		int height = 0;
		String directory = path.substring(0, path.lastIndexOf(File.separator));
		String filename = path.substring(path.lastIndexOf(File.separator), path.length());
		TiffDecoder decoder = new TiffDecoder(directory, filename);
		try {
			FileInfo[] tiffInfo = decoder.getTiffInfo();
			if (tiffInfo!=null) {
				FileInfo info = tiffInfo[0];
				width = info.width;
				height = info.height;
			} else {
				OpenImageOperation open = new OpenImageOperation();
				open.setPath(path);
				open.setShowResult(false);
				open.run();
				ImagePlus image = open.getResult();
				width = image.getWidth();
				height = image.getHeight();
			}

		} catch (IOException e) {
		}
		return new Point(width, height);
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public ImagePlus getImage() {
		return image;
	}

	public void setImage(ImagePlus image) {
		this.image = image;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public String getPath() {
		return path;
	}

	public ImagePlus loadImage() {
		OpenImageOperation open = new OpenImageOperation();
		open.setShowResult(false);
		open.setPath(this.getPath());
		open.run();
		open.getResult().setImage(open.getResult().getProcessor().createImage());
		return open.getResult();
	}

	public ImageDisplayOptions getDisplayOptions() {
		if (this.displayOptions==null) {
			this.displayOptions = new ImageDisplayOptions();
		}
		return displayOptions;
	}

	public void setDisplayOptions(ImageDisplayOptions displayOptions) {
		this.displayOptions = displayOptions;
	}

	public Roi getRoi() {
		return roi;
	}

	public void setRoi(Roi roi) {
		this.roi = roi;
	}
}
