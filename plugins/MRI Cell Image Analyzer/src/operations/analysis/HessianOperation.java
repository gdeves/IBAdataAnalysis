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
import ij.ImagePlus;
import ij.process.ByteProcessor;
import nj.Costs;
import operations.Operation;

 /**
 * Creates a cost image and a vector field computed from the eigenvalues and eigenvectors 
 * of the Hessian of the input image. The Hessian derivative can be used to discriminate 
 * locally between plate-like, line-like, and blob-like image structures. The Hessian 
 * matrix of a scalar function of an n-dimensional vector is the symetric nxn matrix of 
 * second partial derivatives. The operation uses the cost computation from NeuronJ by
 * Erik Meijering.
 * 
 * @author	Volker Baecker 
 */

public class HessianOperation extends Operation {
	private static final long serialVersionUID = 6675567290826774434L;
	protected HessianImage hessian;
	protected Option scale;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		optionsNames = new String[1];
		optionsNames[0] = "scale";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("operations.analysis.HessianImage");
		resultNames = new String[1];
		resultNames[0] = "Hessian";
	}

	public HessianImage getHessian() {
		return hessian;
	}

	public void setHessian(HessianImage hessian) {
		this.hessian = hessian;
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		result = this.getCopyOfOrReferenceTo(inputImage, inputImage.getTitle());
		Costs costsCalculator = new Costs();
		float[][][] costs = costsCalculator.run((ByteProcessor)result.getProcessor(), this.getScale());
		hessian = new HessianImage();
		hessian.setHessian(costs);
	}
	
	protected void setupOptions() {
		super.setupOptions();
		this.setScale(2.0f);
		this.scale.setMin(0);
		this.scale.setShortHelpText("the derivative smoothing scale");
	}
	
	public void connectOptions() {
		this.scale = (Option) this.options.getOptions().get(0);
	}

	public float getScale() {
		return scale.getFloatValue();
	}

	public void setScale(float f) {
		this.scale.setValue(new Float(f).toString());
	}
	
	protected void showResult() {
		this.getHessian().getImage().show();
	}
}
