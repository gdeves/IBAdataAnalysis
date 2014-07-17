package operations.channel;

import operations.control.WaitForUserOperation;
import gui.options.ChoiceOption;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.RGBStackMerge;

public class CreateCompositeOperation extends MergeChannelsOperation {

	private static final long serialVersionUID = 1380541445647427779L;

	protected ImagePlus inputImageGray;
	protected ChoiceOption grayChannel;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[4];
		optionsNames[0] = "red channel";
		optionsNames[1] = "green channel";
		optionsNames[2] = "blue channel";
		optionsNames[3] = "gray channel";
		parameterTypes = new Class[4];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterTypes[1] = Class.forName("ij.ImagePlus");
		parameterTypes[2] = Class.forName("ij.ImagePlus");
		parameterTypes[3] = Class.forName("ij.ImagePlus");
		parameterNames = new String[4];
		parameterNames[0] = "InputImageRed";
		parameterNames[1] = "InputImageGreen";
		parameterNames[2] = "InputImageBlue";
		parameterNames[3] = "InputImageGray";
	}
	
	public void doIt() {
		ImagePlus[] channels = new ImagePlus[4];;
		this.setupChannels(channels);
		RGBStackMerge merger = new RGBStackMerge();
		ImageStack redStack = null;
		ImageStack greenStack = null;
		ImageStack blueStack = null;
		ImageStack grayStack = null;
		int width = 0; 
		int height = 0;
		int stackSize = 0;
		if (channels[0]!=null) {
			redStack = channels[0].getStack();
			width = channels[0].getWidth();
			height = channels[0].getHeight();
			stackSize = channels[0].getStackSize();
		}
		if (channels[1]!=null) {
			greenStack = channels[1].getStack();
			width = channels[1].getWidth();
			height = channels[1].getHeight();
			stackSize = channels[1].getStackSize();
		}
		if (channels[2]!=null) {
			blueStack = channels[2].getStack();
			width = channels[2].getWidth();
			height = channels[2].getHeight();
			stackSize = channels[2].getStackSize();
		}
		if (channels[3]!=null) {
			grayStack = channels[3].getStack();
			width = channels[3].getWidth();
			height = channels[3].getHeight();
			stackSize = channels[3].getStackSize();
		}
		ImageStack[] stacks = new ImageStack[4];
		stacks[0] = redStack;
		stacks[1] = greenStack;
		stacks[2] = blueStack;
		stacks[3] = grayStack;
		ImagePlus mergedStacks = merger.createComposite(width,
												     height,
													 stackSize,
													 stacks,
													 this.keepSource);
		result = mergedStacks;
	}
	
	protected void setupChannels(ImagePlus[] channels) {
		channels[0] = null;
		channels[1] = null;
		channels[2] = null;
		channels[3] = null;
		if (this.getRedChannel().equals(this.choices()[0])) {
			channels[0] = this.getInputImageRed();
		}
		if (this.getRedChannel().equals(this.choices()[1])) {
			channels[0] = this.getInputImageGreen();
		}
		if (this.getRedChannel().equals(this.choices()[2])) {
			channels[0] = this.getInputImageBlue();
		}
		if (this.getRedChannel().equals(this.choices()[3])) {
			channels[0] = this.getInputImageGray();
		}
		if (!getRedChannel().equals("none") && channels[0]==null) channels[0] = IJ.getImage();
		if (this.getGreenChannel().equals(this.choices()[0])) {
			channels[1] = this.getInputImageRed();
		}
		if (this.getGreenChannel().equals(this.choices()[1])) {
			channels[1] = this.getInputImageGreen();
		}
		if (this.getGreenChannel().equals(this.choices()[2])) {
			channels[1] = this.getInputImageBlue();
		}
		if (this.getGreenChannel().equals(this.choices()[3])) {
			channels[1] = this.getInputImageGray();
		}
		if (!getGreenChannel().equals("none") && channels[1]==null) {
			if (channels[0]==null) channels[1] = IJ.getImage(); 
			else {
				WaitForUserOperation op = new WaitForUserOperation();
				op.run();
				channels[1] = IJ.getImage();
			}
		}
		if (this.getBlueChannel().equals(this.choices()[0])) {
			channels[2] = this.getInputImageRed();
		}
		if (this.getBlueChannel().equals(this.choices()[1])) {
			channels[2] = this.getInputImageGreen();
		}
		if (this.getBlueChannel().equals(this.choices()[2])) {
			channels[2] = this.getInputImageBlue();
		}
		if (this.getBlueChannel().equals(this.choices()[3])) {
			channels[2] = this.getInputImageGray();
		}
		if (!getBlueChannel().equals("none") && channels[2]==null) {
			if (channels[0]==null && channels[1]==null) channels[2] = IJ.getImage();
			else {
				WaitForUserOperation op = new WaitForUserOperation();
				op.run();
				channels[2] = IJ.getImage();
			}
		}
		if (this.getGrayChannel().equals(this.choices()[0])) {
			channels[3] = this.getInputImageRed();
		}
		if (this.getGrayChannel().equals(this.choices()[1])) {
			channels[3] = this.getInputImageGreen();
		}
		if (this.getGrayChannel().equals(this.choices()[2])) {
			channels[3] = this.getInputImageBlue();
		}
		if (this.getBlueChannel().equals(this.choices()[3])) {
			channels[3] = this.getInputImageGray();
		}
		if (!getGrayChannel().equals("none") && channels[3]==null) {
			if (channels[0]==null && channels[1]==null && channels[2]==null) channels[3] = IJ.getImage();
			else {
				WaitForUserOperation op = new WaitForUserOperation();
				op.run();
				channels[3] = IJ.getImage();
			}
		}
	}

	public ImagePlus getInputImageGray() {
		return inputImageGray;
	}

	public void setInputImageGray(ImagePlus inputImageGray) {
		this.inputImageGray = inputImageGray;
	}

	public String getGrayChannel() {
		return grayChannel.getValue();
	}

	public void setGrayChannel(String grayChannel) {
		this.grayChannel.setValue(grayChannel);
	}
	
	public String[] choices() {
		String[] choices = {"red input", "green input", "blue input", "gray input", "none"};
		return choices;
	}
	
	protected void setupOptions() {
		super.setupOptions();
		this.grayChannel = new ChoiceOption(this.choices());
		this.grayChannel.setValue(this.choices()[3]);
		this.grayChannel.setName(optionsNames[3]);
		options.add(this.grayChannel);
	}
	
	public void connectOptions() {
		super.connectOptions();
		grayChannel = (ChoiceOption) options.getOptions().get(3);
	}
	
	protected void cleanUpInput() {
		super.cleanUpInput();
		this.setInputImageGray(null);
	}

}
