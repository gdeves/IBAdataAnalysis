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
 */package tools.moleculeLengthAnalyzer.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import tools.moleculeLengthAnalyzer.model.ImageInfo;

/**
 * Properties for the molecular length analyzer.
 * 
 * @author baecker
 *
 */
public class MoleculeLengthAnalyzerProperties extends Properties {
	private static final long serialVersionUID = 6306267917177440146L;
	private static MoleculeLengthAnalyzerProperties properties;
	
	public MoleculeLengthAnalyzerProperties() {
		super();
	}
	
	public MoleculeLengthAnalyzerProperties(MoleculeLengthAnalyzerProperties defaultProperties) {
		super(defaultProperties);
	}

	public static MoleculeLengthAnalyzerProperties getInstance() {
		if (properties==null) properties = new MoleculeLengthAnalyzerProperties(defaultProperties());
		return properties;
	}

	public static MoleculeLengthAnalyzerProperties defaultProperties() {
		MoleculeLengthAnalyzerProperties props = new MoleculeLengthAnalyzerProperties();
		props.setProperty("worksheet", "Position");
		props.setProperty("id_column", "ID");
		props.setProperty("x_column", "Position X");
		props.setProperty("y_column", "Position Y");
		props.setProperty("z_column", "Position Z");
		props.setProperty("unit_column", "Unit");
		props.setProperty("min_dist", "1");
		props.setProperty("image_width", "1024");
		props.setProperty("image_height", "768");
		props.setProperty("number_of_slices", "70");
		props.setProperty("voxel_size_x", "1");
		props.setProperty("voxel_size_y", "1");
		props.setProperty("voxel_size_z", "1");
		return props;
	}

	public String getWorkSheet() {
		return this.getProperty("worksheet");
	}

	public String getXColumn() {
		return this.getProperty("x_column");
	}
	
	public String getYColumn() {
		return this.getProperty("y_column");
	}
	
	public String getZColumn() {
		return this.getProperty("z_column");
	}
	
	public String getIdColumn() {
		return this.getProperty("id_column");
	}

	public String getUnitColumn() {
		return this.getProperty("unit_column");
	}

	public String getImageWidth() {
		return this.getProperty("image_width");
	}

	public String getImageHeight() {
		return this.getProperty("image_height");
	}

	public String getNumberOfSlices() {
		return this.getProperty("number_of_slices");
	}

	public String getVoxelSizeX() {
		return this.getProperty("voxel_size_x");
	}
	
	public String getVoxelSizeY() {
		return this.getProperty("voxel_size_y");
	}
	
	public String getVoxelSizeZ() {
		return this.getProperty("voxel_size_z");
	}

	public void setImageInfo(ImageInfo info) {
		this.setProperty("image_width", Integer.toString(info.getWidth()));
		this.setProperty("image_height", Integer.toString(info.getHeight()));
		this.setProperty("number_of_slices", Integer.toString(info.getSlices()));
		this.setProperty("voxel_size_x", Double.toString(info.getXSize()));
		this.setProperty("voxel_size_y", Double.toString(info.getYSize()));
		this.setProperty("voxel_size_z", Double.toString(info.getZSize()));
	}

	public void save() {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(getFilename());
			this.store(out, "Properties for the MRI Molecule Length Analyzer");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace(); 
		} finally {
			if (out!=null)
				try {
					if (out != null) out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	public static String getFilename() {
		return "MoleculeLengthAnalyzer.props";
	}

	public void load() {
		FileInputStream in = null;
		try {
			if (!(new File(getFilename())).exists()) return;
			in = new FileInputStream(getFilename());
			this.load(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in!=null) in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
