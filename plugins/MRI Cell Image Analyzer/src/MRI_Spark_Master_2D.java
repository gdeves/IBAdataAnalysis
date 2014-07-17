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
import tools.sparkMaster2D.SparkMaster2D;
import ij.plugin.PlugIn;
import imagejProxies.MRIJava2;

/**
 * This plugin opens the MRISparkMaster2D, a tool
 * for the detection of calcium sparks in confocal time series data.
 * Implementation of the method described in
 * "Bányász, Tamás,  Chen-Izu, Ye,  Balke, C W,  Izu, Leighton T,
 *  A New Approach to the Detection and Statistical Classification of Ca2+ Sparks
 *  Biophysical Journal,  Jun 15, 2007" 
 * 
 * @author	Volker Baecker
 */
public class MRI_Spark_Master_2D implements PlugIn {
	public void run(String arg) {
		MRIJava2.setLookAndFeelSet(true);
		SparkMaster2D tool = new SparkMaster2D();
		tool.show();
	}

}
