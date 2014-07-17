/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2007 Volker B�cker
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

import operations.control.WaitForUserOperation;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import imagejProxies.SilentParticleAnalyzer;

/**
 * Count and measure features of objects. Features that measure pixel values are
 * measured on the redirect image, not on the mask. This operation calls the particle
 * analyzer from ImageJ.
 * 
 * @author	Volker Baecker
 **/
public class FindObjectsRedirectingOperation extends FindObjectsOperation {
	private static final long serialVersionUID = -528219103340383733L;
	ImagePlus redirectToImage;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[2];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterTypes[1] = Class.forName("ij.ImagePlus");
		parameterNames = new String[2];
		parameterNames[0] = "InputImage";
		parameterNames[1] = "RedirectToImage";
	}

	public ImagePlus getRedirectToImage() {
		if (redirectToImage==null) {
			WaitForUserOperation op = new WaitForUserOperation();
			op.run();
			redirectToImage = IJ.getImage();
		}
		return redirectToImage;
	}

	public void setRedirectToImage(ImagePlus redirectToImage) {
		if (redirectToImage==null && this.redirectToImage!=null) {
			// this.redirectToImage.close();
			WindowManager.setTempCurrentImage(workingThread, null);
		}
		this.redirectToImage = redirectToImage;
	}
	
	public void doIt() {
		int measurementsWord = this.getMeasurementsWord();
		int oldMeasurements = Analyzer.getMeasurements();
		Analyzer.setMeasurements(measurementsWord);
		ImagePlus inputImage =  this.getInputImage();
		ImagePlus redirectToImage = this.getRedirectToImage();
		ResultsTable measurements = this.getMeasurements();
		measurements.reset();
		SilentParticleAnalyzer analyzer = new SilentParticleAnalyzer(this.getOptionsWord(), 
				measurementsWord, 
				measurements,
				this.getMinSize(),
				this.getMaxSize());
		int oldIndex = analyzer.getRedirectTarget();
		String oldTitle = analyzer.getRedirectTitle();
		WindowManager.setTempCurrentImage(redirectToImage);
		int id = redirectToImage.getID();
		analyzer.setRedirectTarget(id);
		analyzer.setRedirectTitle(redirectToImage.getTitle());
		int previousShowChoice = SilentParticleAnalyzer.getStaticShowChoice();
		SilentParticleAnalyzer.setStaticShowChoice(2);
		analyzer.analyze(inputImage, inputImage.getProcessor());
		IJ.showStatus(measurements.getCounter() + " objects found");
		mask = analyzer.getOutlineImage();
		SilentParticleAnalyzer.setStaticShowChoice(previousShowChoice);
		this.computeOutlines();
		analyzer.setRedirectTarget(oldIndex);
		analyzer.setRedirectTitle(oldTitle);
		Analyzer.setMeasurements(oldMeasurements);
		workingThread = Thread.currentThread();
	}
	
	public void computeOutlines() {
		int oldMeasurements = Analyzer.getMeasurements();
		ResultsTable measurements = new ResultsTable();
		measurements.reset();
		SilentParticleAnalyzer analyzer = new SilentParticleAnalyzer(this.getOptionsWord(), 
				this.getMeasurementsWord(), 
				measurements,
				this.getMinSize(),
				this.getMaxSize());
		int oldIndex = analyzer.getRedirectTarget();
		String oldTitle = analyzer.getRedirectTitle();
		WindowManager.setTempCurrentImage(redirectToImage);
		int id = redirectToImage.getID();
		analyzer.setRedirectTarget(id);
		analyzer.setRedirectTitle(redirectToImage.getTitle());
		int previousShowChoice = SilentParticleAnalyzer.getStaticShowChoice();
		SilentParticleAnalyzer.setStaticShowChoice(1);
		analyzer.analyze(this.getInputImage(), this.getInputImage().getProcessor());
		outlines = analyzer.getOutlineImage();
		SilentParticleAnalyzer.setStaticShowChoice(previousShowChoice);
		analyzer.setRedirectTarget(oldIndex);
		analyzer.setRedirectTitle(oldTitle);
		Analyzer.setMeasurements(oldMeasurements);
	}

	protected void cleanUpInput() {
		super.cleanUpInput();
		this.setRedirectToImage(null);	
	}
}
