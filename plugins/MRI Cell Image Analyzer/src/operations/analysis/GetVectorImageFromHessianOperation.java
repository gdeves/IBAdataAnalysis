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

import gui.options.Option;

/**
 * Draws the eigenvectors from the hessian operations.
 * 
 * @author	Volker Baecker 
 */
public class GetVectorImageFromHessianOperation extends GetImageFromHessianOperation {
	private static final long serialVersionUID = 3170330325192863559L;
	protected Option raster;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[1];
		optionsNames[0] = "raster";
	}
	
	protected void setupOptions() {
		super.setupOptions();
		this.setRaster(10);
		raster.setMin(2);
		raster.setShortHelpText("only vectors on raster points are drawn");
	}
	
	public void connectOptions() {
		this.raster = (Option) this.options.getOptions().get(0);
	}
	
	public int getRaster() {
		return raster.getIntegerValue();
	}

	public void setRaster(int raster) {
		this.raster.setValue(new Integer(raster).toString());
	}
	
	public void doIt() {
		result = hessianImage.getVectorImage(this.getRaster());
	}
}
