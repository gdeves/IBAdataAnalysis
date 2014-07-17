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
package operations.clustering;

import analysis.clustering.FuzzyCMeansClustering;
import ij.ImagePlus;
import ij.process.ByteProcessor;
import gui.options.Option;
import operations.Operation;

/**
 * Segments an image into n-classes using fuzzy-c-means clustering.
 * 
 * @author Volker Baecker
 */
public class FuzzyCMeansClusteringOperation extends Operation {

	private static final long serialVersionUID = 1485693572446761122L;
	protected Option numberOfClusters;
	protected Option maxIterations;
	protected Option fuzziness;
	protected Option minQuality;
	protected Option qualityChangeThreshold;

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultNames = new String[1];
		resultNames[0] = "Result";
		optionsNames = new String[5];
		optionsNames[0] = "number of clusters";
		optionsNames[1] = "max. iterations";
		optionsNames[2] = "fuzziness";
		optionsNames[3] = "min quality";
		optionsNames[4] = "quality change threshold";
	}

	public float getFuzziness() {
		return fuzziness.getFloatValue();
	}

	public void setFuzziness(float fuzziness) {
		this.fuzziness.setValue(Float.toString(fuzziness));
	}

	public int getMaxIterations() {
		return maxIterations.getIntegerValue();
	}

	public void setMaxIterations(int maxIterations) {
		this.maxIterations.setValue(Integer.toString(maxIterations));
	}

	public int getNumberOfClusters() {
		return numberOfClusters.getIntegerValue();
	}

	public void setNumberOfClusters(int numberOfClusters) {
		this.numberOfClusters.setValue(Integer.toString(numberOfClusters));
	}
	
	protected void setupOptions() {
		super.setupOptions();
		this.setNumberOfClusters(3);
		numberOfClusters.setMin(2);
		numberOfClusters.setShortHelpText("Enter the number of clusters.");
		this.setMaxIterations(200);
		maxIterations.setMin(1);
		maxIterations.setShortHelpText("Enter the maximum number of iterations.");
		this.setFuzziness(2);
		fuzziness.setMin(1.01);
		fuzziness.setShortHelpText("Enter the fuzziness.");
		this.setMinQuality(1);
		this.minQuality.setMin(0);
		minQuality.setShortHelpText("The minimum quality befor quality threshold applies.");
		this.setQualityChangeThreshold(0.001f);
		qualityChangeThreshold.setMin(0);
		minQuality.setShortHelpText("Stop if quality doesn't change more than threshold");
	}
	
	public void connectOptions() {
		this.numberOfClusters = (Option) this.options.getOptions().get(0);
		this.maxIterations = (Option) this.options.getOptions().get(1);
		this.fuzziness = (Option) this.options.getOptions().get(2);
		this.minQuality = (Option) this.options.getOptions().get(3);
		this.qualityChangeThreshold = (Option) this.options.getOptions().get(4);
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		result = new ImagePlus();
		result.setProcessor(inputImage.getTitle() + " fcm clusters", new ByteProcessor(inputImage.getWidth(), inputImage.getHeight()));
		FuzzyCMeansClustering fcm = FuzzyCMeansClustering.newFor(inputImage, this.getNumberOfClusters(), this.getMaxIterations(), this.getFuzziness(), this.getMinQuality(), this.getQualityChangeThreshold());
		fcm.run();
		Object fcmResult = fcm.getResult();
		result.getProcessor().setPixels(fcmResult);
	}

	public float getMinQuality() {
		return minQuality.getFloatValue();
	}

	public void setMinQuality(float minQuality) {
		this.minQuality.setFloatValue(minQuality);
	}

	public float getQualityChangeThreshold() {
		return qualityChangeThreshold.getFloatValue();
	}

	public void setQualityChangeThreshold(float qualityChangeThreshold) {
		this.qualityChangeThreshold.setFloatValue(qualityChangeThreshold);
	}
}
