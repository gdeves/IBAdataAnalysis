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
package operations.processing;

import ij.ImagePlus;
import imagejProxies.SilentContrastEnhancer;
import gui.Options;
import gui.options.BooleanOption;
import gui.options.Option;
import operations.Operation;

/**
 * Enhances image contrast by using either histogram stretching or histogram equalization.
 * 
 * @author Volker Bäcker
 */
public class EnhanceContrastOperation extends Operation {
	private static final long serialVersionUID = -6351286066408777162L;
	protected SilentContrastEnhancer contrastEnhancer;	
	protected Option percentSaturated;
	protected BooleanOption equalize;
	protected BooleanOption normalize;
	protected BooleanOption useStackHistogram;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[4];
		optionsNames[0] = "percent saturated";
		optionsNames[1] = "equalize";
		optionsNames[2] = "normalize";
		optionsNames[3] = "use stack histogram";
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultNames = new String[1];
		resultNames[0] = "Result";
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		this.contrastEnhancer = null;
		result = this.getCopyOfOrReferenceTo(inputImage, inputImage.getTitle());
		this.getContrastEnhancer().run("");
	}
	
	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		
		this.percentSaturated = new Option();
		this.setPercentSaturated(0.5f);
		this.percentSaturated.setName(optionsNames[0]);
		options.add(percentSaturated);
		
		this.equalize = new BooleanOption();
		this.setEqualize(false);
		this.equalize.setName(optionsNames[1]);
		options.add(equalize);
		
		this.normalize = new BooleanOption();
		this.setNormalize(true);
		this.normalize.setName(optionsNames[2]);
		options.add(normalize);
		
		this.useStackHistogram = new BooleanOption();
		this.setUseStackHistogram(false);
		this.useStackHistogram.setName(optionsNames[3]);
		options.add(useStackHistogram);
	}
	
	public void connectOptions() {
		percentSaturated = (Option)options.getOptions().get(0);
		equalize = (BooleanOption)options.getOptions().get(1);
		normalize = (BooleanOption)options.getOptions().get(2);
		useStackHistogram = (BooleanOption)options.getOptions().get(3);
	}
	
	public boolean getEqualize() {
		return equalize.getBooleanValue();
	}

	public void setEqualize(boolean equalize) {
		this.equalize.setValue(Boolean.toString(equalize));
	}

	public boolean getNormalize() {
		return normalize.getBooleanValue();
	}

	public void setNormalize(boolean normalize) {
		this.normalize.setValue(Boolean.toString(normalize));
	}

	public float getPercentSaturated() {
		return percentSaturated.getFloatValue();
	}

	public void setPercentSaturated(float percentSaturated) {
		this.percentSaturated.setValue(Float.toString(percentSaturated));
	}

	public boolean getUseStackHistogram() {
		return useStackHistogram.getBooleanValue();
	}

	public void setUseStackHistogram(boolean useStackHistogram) {
		this.useStackHistogram.setValue(Boolean.toString(useStackHistogram));
	}

	public SilentContrastEnhancer getContrastEnhancer() {
		contrastEnhancer = new SilentContrastEnhancer(this.result);
		contrastEnhancer.setProcessStack(true);
		contrastEnhancer.setPercentSaturated(this.getPercentSaturated());
		contrastEnhancer.setEqualize(this.getEqualize());
		contrastEnhancer.setNormalize(this.getNormalize());
		contrastEnhancer.useStackHistogram(this.getUseStackHistogram());
		return contrastEnhancer;
	}
}
