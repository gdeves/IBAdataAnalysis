/*
 * Created on 21.11.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package operations.processing;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import gui.Options;
import gui.options.BooleanOption;
import gui.options.Option;
import operations.Operation;

/**
 * @author Volker
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SubtractBaselineOperation extends Operation {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1357189031294423554L;
	Option rank;
	BooleanOption useOnlyDifferentValues;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		// Set parameterTypes, parameterNames and resultType
		optionsNames = new String[2];
		optionsNames[0] = "rank";
		optionsNames[1] = "different only";
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultNames = new String[1];
		resultNames[0] = "Result";
	}
	
	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		this.rank = new Option();
		this.setRank(15);
		this.rank.setShortHelpText("enter the rank of the minimum value to be subtracted");
		this.rank.setName(optionsNames[0]);
		options.add(rank);
		this.useOnlyDifferentValues = new BooleanOption();
		this.setUseOnlyDifferentValues(true);
		this.useOnlyDifferentValues.setShortHelpText("if selected only different values are taken for the ranking");
		this.useOnlyDifferentValues.setName(optionsNames[1]);
		options.add(useOnlyDifferentValues);
	}

	public void connectOptions() {
		this.rank = (Option) this.options.getOptions().get(0);
		this.useOnlyDifferentValues = (BooleanOption) this.options.getOptions().get(1);
	}
	/**
	 * @return Returns the rank.
	 */
	public int getRank() {
		return rank.getIntegerValue();
	}
	/**
	 * @param rank The rank to set.
	 */
	public void setRank(int rank) {
		this.rank.setValue(Integer.toString(rank));
	}
	/**
	 * @return Returns the useOnlyDifferentValues.
	 */
	public boolean getUseOnlyDifferentValues() {
		return useOnlyDifferentValues.getBooleanValue();
	}
	/**
	 * @param useOnlyDifferentValues The useOnlyDifferentValues to set.
	 */
	public void setUseOnlyDifferentValues(boolean useOnlyDifferentValues) {
		this.useOnlyDifferentValues.setValue(Boolean.toString(useOnlyDifferentValues));
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		result = this.getCopyOfOrReferenceTo(inputImage, inputImage.getTitle());
		int[] minValues = new int[this.getRank()];
		for (int i=0; i<this.getRank(); i++) {
			minValues[i] = 999999;
		}
		this.getMinValues(minValues);
		int baseline = this.getRankValue(minValues);
		result.getProcessor().add(-baseline);
	}

	/**
	 * @param minValues
	 * @return
	 */
	private int getRankValue(int[] minValues) {
		for (int i=this.getRank()-1; i>=0; i--) {
			if (minValues[i]!=999999) return minValues[i];
		}
		return 0;
	}

	/**
	 * 
	 */
	private void getMinValues(int[] minValues) {
		ImageProcessor ip = result.getProcessor();
		for (int x=0; x<result.getWidth(); x++) {
			for (int y=0; y<result.getHeight(); y++) {
				this.sortInto(minValues, 
							  ip.getPixel(x,y));				
			}	
		}
		
	}

	/**
	 * @param minValues
	 * @param pixel
	 */
	private void sortInto(int[] minValues, int pixel) {
		if (pixel>minValues[this.getRank()-1]) return;
		for (int i=0; i<this.getRank(); i++) {
			if (this.getUseOnlyDifferentValues() && pixel==minValues[i]) return;
			if (pixel<minValues[i]) {
				this.pushUp(minValues, i); 
				minValues[i] = pixel;
				return;
			}
		}
	}

	/**
	 * @param minValues
	 * @param i
	 */
	private void pushUp(int[] minValues, int start) {
		for (int i=this.getRank()-1 ; i>=start+1; i--) {
			minValues[i] = minValues[i-1];
		}
	}
}
