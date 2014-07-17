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

import ij.WindowManager;
import operations.Operation;

/**
 * Get a reference to the active image.
 * 
 * @author Volker Baecker
 */
public class GetCurrentImageOperation extends Operation {
	private static final long serialVersionUID = 3197899600429300576L;

	protected void initialize() throws ClassNotFoundException {	
		super.initialize();
		parameterTypes = new Class[0];
		parameterNames = new String[0]; 
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultNames = new String[1];
		resultNames[0] = "Result";
	}
	
	public void doIt() {
		this.inputImage = WindowManager.getCurrentImage();
		result = inputImage;
	}

	protected boolean isBatchOperation() {
		return false;
	}
}
