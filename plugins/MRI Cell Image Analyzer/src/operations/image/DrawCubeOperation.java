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
package operations.image;

import ij.ImagePlus;
import ij.gui.NewImage;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import ij.process.ImageProcessor;
import operations.Operation;

/**
 * Draw a solid cube with the given radius at x,y,z in the result stack.
 * The operation can be used to show the result of the MeasureMaxCubeOperation. 
 * 
 * @author Volker Baecker
 */
public class DrawCubeOperation extends Operation {
	private static final long serialVersionUID = 8808007166894484608L;
	
	protected ResultsTable cube;

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[2];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterTypes[1] = Class.forName("ij.measure.ResultsTable");
		parameterNames = new String[2];
		parameterNames[0] = "InputImage";
		parameterNames[1] = "Cube";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultNames = new String[1];
		resultNames[0] = "Result";
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		result = NewImage.createByteImage(inputImage.getTitle() + " - best cube", 
										  inputImage.getWidth(),
										  inputImage.getHeight(),
										  inputImage.getNSlices(),
										  NewImage.FILL_BLACK);
		int x = (int)this.getCube().getValue("x", 0);
		int y = (int)this.getCube().getValue("y", 0);
		int z = (int)this.getCube().getValue("z", 0);
		int radius = (int)this.getCube().getValue("radius", 0);
		int value = 255;
		for(int k=z-radius; k<=z+radius; k++) {
			if (k<1 || k>result.getNSlices()) continue;
			ImageProcessor ip = result.getStack().getProcessor(k);
			for (int i=x-radius; i<=x+radius; i++) {
				for (int j=y-radius; j<=y+radius; j++) {
					ip.putPixel(i,j,value);
				}
			}
		}
		result.updateAndDraw();
	}

	public ResultsTable getCube() {
		if (cube==null && this.getApplication()==null) {
			cube = Analyzer.getResultsTable();
		}
		return cube;
	}

	public void setCube(ResultsTable cube) {
		this.cube = cube;
	}
}
