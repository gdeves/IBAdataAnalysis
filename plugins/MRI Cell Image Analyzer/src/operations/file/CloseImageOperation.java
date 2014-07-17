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
package operations.file;

import operations.Operation;

/**
 * Closes the window of the input image.
 * 
 * @author Volker Baecker
 */
public class CloseImageOperation extends Operation {
	private static final long serialVersionUID = -5146433453697652925L;

	protected void initialize() throws ClassNotFoundException {	
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		resultTypes = new Class[0];
		resultNames = new String[0];
	}
	
	public void doIt() {
		inputImage = this.getInputImage();
		if (inputImage==null) {
			System.out.println("Couldn't close window");
			return;
		}
		if (inputImage.getWindow()==null) {
			inputImage.show();
		}
		inputImage.getWindow().close();
	}
}
