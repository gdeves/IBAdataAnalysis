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
package operations.analysis;

import operations.Operation;

/**
 * The operation takes the eigenvalues from the operation hessian 
 * and computes an image from them.
 * 
 * @author	Volker Baecker
 **/
public class GetImageFromHessianOperation extends Operation {
	private static final long serialVersionUID = 6359117923984293626L;
	protected HessianImage hessianImage;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("operations.analysis.HessianImage");
		parameterNames = new String[1];
		parameterNames[0] = "HessianImage";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultNames = new String[1];
		resultNames[0] = "Result";
	}
	
	public void doIt() {
		result = hessianImage.getImage();
	}
	
	public HessianImage getHessianImage() {
		return hessianImage;
	}

	public void setHessianImage(HessianImage hessianImage) {
		this.hessianImage = hessianImage;
	}

	protected void cleanUpInput() {
		this.setHessianImage(null);
	}
}
