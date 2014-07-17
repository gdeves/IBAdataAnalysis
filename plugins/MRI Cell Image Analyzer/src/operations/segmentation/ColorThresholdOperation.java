package operations.segmentation;
import gui.Options;
import gui.options.BooleanOption;
import gui.options.ChoiceOption;
import gui.options.Option;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import operations.FilterOperation;

// Operation uses code from:
//Colour Thresholding v1.8-------
//G Landini 5/Jan/2007.

public class ColorThresholdOperation extends FilterOperation {
	protected static final long serialVersionUID = 509997195631918771L;
	protected Option colorModel; 
	protected Option minChannelR;
	protected Option minChannelG;
	protected Option minChannelB;
	protected Option maxChannelR;
	protected Option maxChannelG;
	protected Option maxChannelB;
	protected Option filterChannelRIsStop;
	protected Option filterChannelGIsStop;
	protected Option filterChannelBIsStop;

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[10];
		optionsNames[0] = "color model";
		optionsNames[1] = "min channel 1";
		optionsNames[2] = "max channel 1";
		optionsNames[3] = "min channel 2";
		optionsNames[4] = "max channel 2";
		optionsNames[5] = "min channel 3";
		optionsNames[6] = "max channel 3";
		optionsNames[7] = "stop channel 1";
		optionsNames[8] = "stop channel 2";
		optionsNames[9] = "stop channel 3";
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		result = this.getCopyOfOrReferenceTo(inputImage, inputImage.getTitle());
		ImageStack stack = result.getStack();
		if (stack!=null) {
			result.setSlice(inputImage.getCurrentSlice());
		}
		WindowManager.setTempCurrentImage(result);
		this.runFilter();
		WindowManager.setTempCurrentImage(null);
	}
	
	public void runFilter() {
		String firstChannel = this.nameOfFirstChannel();
		String secondChannel = this.nameOfSecondChannel();
		String thirdChannel = this.nameOfThirdChannel();
		IJ.run("Colors...", "foreground=white background=black selection=yellow");
		IJ.run("Options...", "iterations=1 black count=1");

		this.convertColorModel();
		IJ.run("Convert Stack to Images");
		
		IJ.selectWindow(firstChannel);
		IJ.setThreshold(this.getMinChannelR(), this.getMaxChannelR());
		IJ.run("Make Binary", "thresholded remaining");
		if (getFilterChannelRIsStop()) IJ.run("Invert");
		
		IJ.selectWindow(secondChannel);
		IJ.setThreshold(this.getMinChannelG(), this.getMaxChannelG());
		IJ.run("Make Binary", "thresholded remaining");
		if (getFilterChannelGIsStop()) IJ.run("Invert");
		
		IJ.selectWindow(thirdChannel);
		IJ.setThreshold(this.getMinChannelB(), this.getMaxChannelB());
		IJ.run("Make Binary", "thresholded remaining");
		if (getFilterChannelBIsStop()) IJ.run("Invert");
		
		IJ.run("Image Calculator...", "image1=" + firstChannel + " operation=AND image2="+secondChannel+" create");
		IJ.run("Image Calculator...", "image1=[Result of "+firstChannel+"] operation=AND image2="+thirdChannel+" create");
		
		WindowManager.getImage(firstChannel).close();
		WindowManager.getImage(secondChannel).close();
		WindowManager.getImage(thirdChannel).close();
		WindowManager.getImage("Result of " + firstChannel).close();
		IJ.selectWindow("Result of Result of " + firstChannel);
		result = IJ.getImage();
		result.hide();
		result.setTitle(inputImage.getTitle() + " color threshold");
	}


	private String nameOfThirdChannel() {
		String result = "Blue";
		if (this.getColorModel().equals("hsb")) {
			result = "Brightness";
		}
		return result;
	}

	private String nameOfSecondChannel() {
		String result = "Green";
		if (this.getColorModel().equals("hsb")) {
			result = "Saturation";
		}
		return result;
	}

	private String nameOfFirstChannel() {
		String result = "Red";
		if (this.getColorModel().equals("hsb")) {
			result = "Hue";
		}
		return result;
	}

	protected void convertColorModel() {
		if (this.getColorModel().equals("rgb")) {
			IJ.run("RGB Stack");
			return;
		}
		if (this.getColorModel().equals("hsb")) {
			IJ.run("HSB Stack");
			return;
		}
		if (this.getColorModel().equals("cie lab")) {
			IJ.run("RGBtoLab ");
			IJ.run("RGB Stack");
			return;
		}
		if (this.getColorModel().equals("yuv")) {
			IJ.run("RGBtoYUV ");
			IJ.run("RGB Stack");
			return;
		}
	}

	public double getMaxChannelB() {
		return maxChannelB.getDoubleValue();
	}


	public void setMaxChannelB(double maxChannelB) {
		this.maxChannelB.setDoubleValue(maxChannelB);
	}


	public double getMaxChannelG() {
		return maxChannelG.getDoubleValue();
	}


	public void setMaxChannelG(double maxChannelG) {
		this.maxChannelG.setDoubleValue(maxChannelG);
	}


	public double getMaxChannelR() {
		return maxChannelR.getDoubleValue();
	}


	public void setMaxChannelR(double maxChannelR) {
		this.maxChannelR.setDoubleValue(maxChannelR);
	}


	public double getMinChannelB() {
		return minChannelB.getDoubleValue();
	}


	public void setMinChannelB(double minChannelB) {
		this.minChannelB.setDoubleValue(minChannelB);
	}


	public double getMinChannelG() {
		return minChannelG.getDoubleValue();
	}


	public void setMinChannelG(double minChannelG) {
		this.minChannelG.setDoubleValue(minChannelG);
	}


	public double getMinChannelR() {
		return minChannelR.getDoubleValue();
	}


	public void setMinChannelR(double minChannelR) {
		this.minChannelR.setDoubleValue(minChannelR);
	}


	public boolean getFilterChannelRIsStop() {
		return filterChannelRIsStop.getBooleanValue();
	}


	public void setFilterChannelRIsStop(boolean filterChannelRIsStop) {
		this.filterChannelRIsStop.setBooleanValue(filterChannelRIsStop);
	}


	public boolean getFilterChannelBIsStop() {
		return filterChannelBIsStop.getBooleanValue();
	}


	public void setFilterChannelBIsStop(boolean filterChannelBIsStop) {
		this.filterChannelBIsStop.setBooleanValue(filterChannelBIsStop);
	}


	public boolean getFilterChannelGIsStop() {
		return filterChannelGIsStop.getBooleanValue();
	}


	public void setFilterChannelGIsStop(boolean filterChannelGIsStop) {
		this.filterChannelGIsStop.setBooleanValue(filterChannelGIsStop);
	}


	public String getColorModel() {
		return colorModel.getValue();
	}


	public void setColorModel(String colorModel) {
		this.colorModel.setValue(colorModel);
	}

	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		
		this.colorModel = new ChoiceOption(this.choices());
		this.colorModel.setValue(this.choices()[2]);
		colorModel.setName(optionsNames[0]);
		colorModel.setShortHelpText("Choose the color model.");
		options.add(colorModel);
		
		this.minChannelR = new Option();
		this.minChannelR.setName(optionsNames[1]);
		this.setMinChannelR(99);
		this.minChannelR.setMin(0);
		this.minChannelR.setMax(255);
		this.minChannelR.setShortHelpText("Enter the minimum value for the first channel.");
		options.add(minChannelR);
		
		this.maxChannelR = new Option();
		this.maxChannelR.setName(optionsNames[2]);
		this.setMaxChannelR(255);
		this.maxChannelR.setMin(0);
		this.maxChannelR.setMax(255);
		this.maxChannelR.setShortHelpText("Enter the maximum value for the first channel.");
		options.add(maxChannelR);
		
		this.minChannelG = new Option();
		this.minChannelG.setName(optionsNames[3]);
		this.setMinChannelG(0);
		this.minChannelG.setMin(0);
		this.minChannelG.setMax(255);
		this.minChannelG.setShortHelpText("Enter the minimum value for the second channel.");
		options.add(minChannelG);
		
		this.maxChannelG = new Option();
		this.maxChannelG.setName(optionsNames[4]);
		this.setMaxChannelG(118);
		this.maxChannelG.setMin(0);
		this.maxChannelG.setMax(255);
		this.maxChannelG.setShortHelpText("Enter the maximum value for the second channel.");
		options.add(maxChannelG);
		
		this.minChannelB = new Option();
		this.minChannelB.setName(optionsNames[5]);
		this.setMinChannelB(110);
		this.minChannelB.setMin(0);
		this.minChannelB.setMax(255);
		this.minChannelB.setShortHelpText("Enter the minimum value for the third channel.");
		options.add(minChannelB);
		
		this.maxChannelB = new Option();
		this.maxChannelB.setName(optionsNames[6]);
		this.setMaxChannelB(255);
		this.maxChannelB.setMin(0);
		this.maxChannelB.setMax(255);
		this.maxChannelB.setShortHelpText("Enter the maximum value for the third channel.");
		options.add(maxChannelB);
		
		this.filterChannelRIsStop = new BooleanOption();
		this.filterChannelRIsStop.setName(optionsNames[7]);
		this.filterChannelRIsStop.setShortHelpText("stop selected range instead of pass");
		options.add(filterChannelRIsStop);
		
		this.filterChannelGIsStop = new BooleanOption();
		this.filterChannelGIsStop.setName(optionsNames[8]);
		this.filterChannelGIsStop.setShortHelpText("stop selected range instead of pass");
		options.add(filterChannelGIsStop);
		
		this.filterChannelBIsStop = new BooleanOption();
		this.filterChannelBIsStop.setName(optionsNames[9]);
		this.filterChannelBIsStop.setShortHelpText("stop selected range instead of pass");
		options.add(filterChannelBIsStop);
	}

	protected String[] choices() {
		String[] choices = {"hsb", "rgb", "cie lab", "yuv"};
		return choices;
	}

	public void connectOptions() {
		this.colorModel = (ChoiceOption) this.options.getOptions().get(0);
		this.minChannelR = (Option) this.options.getOptions().get(1);
		this.maxChannelR = (Option) this.options.getOptions().get(2);
		this.minChannelG = (Option) this.options.getOptions().get(3);
		this.maxChannelG = (Option) this.options.getOptions().get(4);
		this.minChannelB = (Option) this.options.getOptions().get(5);
		this.maxChannelB = (Option) this.options.getOptions().get(6);
		this.filterChannelRIsStop = (BooleanOption)this.options.getOptions().get(7);
		this.filterChannelGIsStop = (BooleanOption)this.options.getOptions().get(8);
		this.filterChannelBIsStop = (BooleanOption)this.options.getOptions().get(9);
	}
}
