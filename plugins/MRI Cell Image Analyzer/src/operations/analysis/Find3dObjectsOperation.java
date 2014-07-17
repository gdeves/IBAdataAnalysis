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
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import ij.text.TextWindow;
import gui.Options;
import gui.options.BooleanOption;
import gui.options.Option;
import operations.FilterOperation;

/**
 * Counts the number of 3D objects in a stack and displays the volume, the surface, 
 * the centre of mass and the centre of intensity for each object. This operation calls
 * the 3D objects counter plugin from Fabrice Cordelières and Jonathan Jackson 
 * (see http://rsb.info.nih.gov/ij/plugins/track/objects.html).
 * 
 * @author	Volker Baecker
 **/
public class Find3dObjectsOperation extends FilterOperation {
	private static final long serialVersionUID = -6430665132043143941L;
	protected Option minSize;
	protected Option maxSize;
	protected BooleanOption displayLabels;
	protected BooleanOption computeOutlines;
	protected Option threshold;
	ResultsTable measurements;
	ImagePlus mask;
	ImagePlus outlines;
	private Thread workingThread;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		optionsNames = new String[5];
		optionsNames[0] = "min size";
		optionsNames[1] = "max size";
		optionsNames[2] = "display labels";
		optionsNames[3] = "compute outlines";
		optionsNames[4] = "threshold";
		resultTypes = new Class[3];
		resultTypes[0] = Class.forName("ij.measure.ResultsTable");
		resultTypes[1] = Class.forName("ij.ImagePlus");
		resultTypes[2] = Class.forName("ij.ImagePlus");
		resultNames = new String[3];
		resultNames[0] = "Measurements";
		resultNames[1] = "Mask";
		resultNames[2] = "Outlines";
	}
	
	public ImagePlus getMask() {
		return mask;
	}
	
	public void setMask(ImagePlus mask) {
		if (mask==null && this.mask!=null) {
			this.mask.close();
			WindowManager.setTempCurrentImage(workingThread, null);
		}
		this.mask = mask;
	}
	
	public int getMaxSize() {
		return maxSize.getIntegerValue();
	}

	public void setMaxSize(int maxSize) {
		this.maxSize.setValue(Integer.toString(maxSize));
	}

	public ResultsTable getMeasurements() {
		if (measurements==null) {
			measurements = new ResultsTable();
		}
		return measurements;
	}
	
	public void setMeasurements(ResultsTable measurements) {
		this.measurements = measurements;
	}
	
	public int getMinSize() {
		return minSize.getIntegerValue();
	}

	public void setMinSize(int minSize) {
		this.minSize.setValue(Integer.toString(minSize));
	}
	
	public ImagePlus getOutlines() {
		return outlines;
	}

	public void setOutlines(ImagePlus outlines) {
		if (outlines==null && this.outlines!=null) {
			this.outlines.close();
			WindowManager.setTempCurrentImage(workingThread, null);
		}
		this.outlines = outlines;
	}
	
	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		// min 
		this.minSize = new Option();
		this.setMinSize(10);
		minSize.setName(optionsNames[0]);
		options.add(this.minSize);
		// max
		this.maxSize = new Option();
		maxSize.setName(optionsNames[1]);
		this.setMaxSize(999999);
		options.add(this.maxSize);
		// display labels
		this.displayLabels = new BooleanOption();
		this.setDisplayLabels(false);
		displayLabels.setName(optionsNames[2]);
		options.add(this.displayLabels);
		// compute outlines
		this.computeOutlines = new BooleanOption();
		this.setComputeOutlines(false);
		computeOutlines.setName(optionsNames[3]);
		options.add(this.computeOutlines);
		// threshold
		this.threshold = new Option();
		threshold.setName(optionsNames[4]);
		this.setThreshold(128);
		options.add(this.threshold);
	}

	public void connectOptions() {
		this.minSize = (Option) this.options.getOptions().get(0);
		this.maxSize = (Option) this.options.getOptions().get(1);
		this.displayLabels = (BooleanOption) this.options.getOptions().get(2);
		this.computeOutlines = (BooleanOption) this.options.getOptions().get(3);
		this.threshold = (Option) this.options.getOptions().get(4);
	}
	
	public boolean getDisplayLabels() {
		return displayLabels.getBooleanValue();
	}
	
	public void setDisplayLabels(boolean showNumbers) {
		this.displayLabels.setValue(Boolean.toString(showNumbers));
	}
	
	public boolean getComputeOutlines() {
		return computeOutlines.getBooleanValue();
	}
	
	public void setComputeOutlines(boolean computeOutlines) {
		this.computeOutlines.setValue(Boolean.toString(computeOutlines));
	}
	
	protected void showResult() {
		this.getMeasurements().show("Results from " + inputImage.getTitle());
		this.getMask().show();
		if (this.getComputeOutlines()) this.getOutlines().show();
	}

	public void runFilter() {
		String options = "threshold=" + this.getThreshold() + " slice=1 min=" + this.getMinSize() + " max=" + this.getMaxSize() +  " particles ";
		if (this.getComputeOutlines()) options = options + "edges ";
		if (this.getDisplayLabels()) options = options +  "dot=3 numbers font=14";
		IJ.run("Object Counter3D", options);
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		result = this.getCopyOfOrReferenceTo(inputImage, inputImage.getTitle());
		ResultsTable tmp = null;
		if (WindowManager.getFrame("Results")!=null) {
			tmp = copySystemResultsTable();
			Analyzer.getResultsTable().reset();
			((TextWindow)WindowManager.getFrame("Results")).close();
		}
		this.getInterpreter().setBatchMode(true);
		WindowManager.setTempCurrentImage(result);
		this.runFilter();
		outlines = WindowManager.getImage("Edges " + inputImage.getTitle());
		if (this.getComputeOutlines()) this.getOutlines().hide();
		mask = WindowManager.getImage("Particles " + inputImage.getTitle());
		mask.hide();
		this.setMeasurements(copySystemResultsTable());
		((TextWindow)WindowManager.getFrame("Results")).close();
		if (tmp!=null) {
			copyResultsTable(tmp, Analyzer.getResultsTable());
			Analyzer.getResultsTable().show("Results");
		}
		WindowManager.setTempCurrentImage(inputImage);
		workingThread = Thread.currentThread();
		this.getInterpreter().setBatchMode(false);
	}

	public int getThreshold() {
		return threshold.getIntegerValue();
	}

	public void setThreshold(int threshold) {
		this.threshold.setIntegerValue(threshold);
	}
}
