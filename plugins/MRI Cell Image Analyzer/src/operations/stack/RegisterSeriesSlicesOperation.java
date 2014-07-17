package operations.stack;

import ij.ImagePlus;
import ij.ImageStack;
import ij.io.Opener;
import ij.io.SaveDialog;
import ij.process.ImageProcessor;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import gui.ListEditor;
import gui.Options;
import gui.options.BooleanOption;
import gui.options.ListOption;
import gui.options.Option;
import operations.Operation;

public class RegisterSeriesSlicesOperation extends Operation {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7442772402704671048L;

	protected Option imagesAtOnce;

	protected Option imageList;

	protected Option outputFolder;

	protected Option useSequenceOpener;

	protected Option referenceSlice;

	protected boolean imageListWasEmpty;

	protected String path;

	public RegisterSeriesSlicesOperation() {
		super();
		imageListWasEmpty = false;
	}

	/**
	 * @throws ClassNotFoundException
	 * 
	 */
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		// Set parameterTypes, parameterNames and resultType
		parameterTypes = new Class[0];
		parameterNames = new String[0];
		optionsNames = new String[5];
		optionsNames[0] = "images at once";
		optionsNames[1] = "image list";
		optionsNames[2] = "output folder";
		optionsNames[3] = "use sequence opener";
		optionsNames[4] = "reference slice";
		resultTypes = new Class[0];
		resultNames = new String[0];
	}

	
	public Vector<File> getImageList() {
		return imageList.getListValue();
	}

	public int getImagesAtOnce() {
		return imagesAtOnce.getIntegerValue();
	}

	public void setImagesAtOnce(int imagesAtOnce) {
		this.imagesAtOnce.setValue(Integer.toString(imagesAtOnce));
	}

	public String getOutputFolder() {
		return outputFolder.getValue();
	}

	public void setOutputFolder(String outputFolder) {
		this.outputFolder.setValue(outputFolder);
	}

	public boolean getUseSequenceOpener() {
		return useSequenceOpener.getBooleanValue();
	}

	public void setUseSequenceOpener(boolean useSequenceOpener) {
		this.useSequenceOpener.setValue(Boolean.toString(useSequenceOpener));
	}

	/**
	 * @return Returns the imageListWasEmpty.
	 */
	public boolean isImageListWasEmpty() {
		return imageListWasEmpty;
	}

	/**
	 * @param imageListWasEmpty
	 *            The imageListWasEmpty to set.
	 */
	public void setImageListWasEmpty(boolean imageListWasEmpty) {
		this.imageListWasEmpty = imageListWasEmpty;
	}

	public void resetPathOptions() {
		imageListWasEmpty = true;
		this.reset();
	}

	public void setPathOptionsFromUser() {
		ListEditor editor = ((ListOption) this.imageList).getEditor();
		if (this.getUseSequenceOpener()) {
			editor.setUseSequenceOpener(true);
		}
		editor.view().getAddButton().doClick();
		imageListWasEmpty = false;
		if (this.getImageList().size() == 0) {
			this.stopApplication();
		}
	}

	public void connectOptions() {
		this.imagesAtOnce = (Option) this.options.getOptions().get(0);
		this.imageList = (ListOption) this.options.getOptions().get(1);
		this.outputFolder = (Option) this.options.getOptions().get(2);
		this.useSequenceOpener = (BooleanOption) this.options.getOptions()
				.get(3);
	}

	protected void setupOptions() {
		options = new Options();
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();

		this.imagesAtOnce = new Option();
		imagesAtOnce.setName(optionsNames[0]);
		imagesAtOnce
				.setShortHelpText("enter the number of images loaded at once");
		this.setImagesAtOnce(5);
		options.add(this.imagesAtOnce);

		this.imageList = new ListOption();
		imageList.setName(optionsNames[1]);
		imageList.setShortHelpText("press edit to select images or a folder");
		options.add(this.imageList);

		this.outputFolder = new Option();
		outputFolder.setName(optionsNames[2]);
		outputFolder.setShortHelpText("the folder in which results are saved");
		options.add(this.outputFolder);

		this.useSequenceOpener = new BooleanOption();
		useSequenceOpener.setName(optionsNames[3]);
		useSequenceOpener
				.setShortHelpText("check to use the sequence opener instead of the file dialog.");
		options.add(useSequenceOpener);

		this.referenceSlice = new Option();
		referenceSlice.setName(optionsNames[4]);
		referenceSlice
				.setShortHelpText("number of the slice to align the other slices to");
		this.setReferenceSlice(1);
		options.add(this.referenceSlice);
	}

	public int getReferenceSlice() {
		return referenceSlice.getIntegerValue();
	}

	public void setReferenceSlice(int referenceSlice) {
		this.referenceSlice.setValue(Integer.toString(referenceSlice));
	}

	public void doIt() {
		if (this.getImageList().size()==0) {
			ListEditor editor = ((ListOption)this.imageList).getEditor();
			if (this.getUseSequenceOpener()) {
				editor.setUseSequenceOpener(true);
			}
			editor.view().getAddButton().doClick();
			imageListWasEmpty = true;
		}
		if (this.getImageList().size()==0) {
			return;
		}
		
		int currentSlice = this.getReferenceSlice() - 1;
		this.saveReferenceImage();
		while (currentSlice>0) {
			this.registerBackwards(currentSlice);
			currentSlice = currentSlice - this.getImagesAtOnce() + 1;
		}
		while (currentSlice<this.getImageList().size()) {
			this.registerForwards(currentSlice);
			currentSlice = currentSlice + this.getImagesAtOnce() - 1;
		}
	}

	protected void saveReferenceImage() {
		this.getImageList().get(this.getReferenceSlice());
		
	}

	protected void registerForwards(int currentSlice) {
		int startIndex = currentSlice;
		int endIndex = currentSlice + this.getImagesAtOnce() - 1;
		ArrayList<ImagePlus> images = new ArrayList<ImagePlus>();
		for (int i=startIndex; i<endIndex; i++) {
			String filename = ((File) this.getImageList().get(i)).getAbsolutePath();
			images.add(this.openImage(filename));
		}
		
	}

	protected ImagePlus openImage(String thePath) {
		Opener opener = new Opener();
		int index = thePath.lastIndexOf(File.separator);
		String dir = thePath.substring(0, index+1);
		String filename = thePath.substring(index+1, thePath.length());
		result = opener.openImage(dir, filename);
		return result;
	}

	protected void registerBackwards(int currentSlice) {
	}

	protected ImagePlus createStack(ArrayList<ImagePlus> imageList) {
		ImagePlus firstImage = imageList.get(0);
		int maxWidth = firstImage.getWidth();
		int maxHeight = firstImage.getHeight();
		ImageStack newStack= new ImageStack(maxWidth, maxHeight);
		ImagePlus imp;
		ImageProcessor ip;
		boolean rgbStack = (firstImage.getType()==ImagePlus.COLOR_RGB );
        for(int i=0; i<imageList.size(); i++){
            imp=(ImagePlus) imageList.get(i);
            ip=imp.getProcessor();
            if (rgbStack)
                ip = ip.convertToRGB();
            else 
                ip = ip.convertToByte(true);
            //Rectangle r = ip.getRoi();
            //IJ.write(i+" "+r.width+" "+r.height);
            newStack.addSlice(imp.getTitle(),ip);
        }
        ImagePlus newImage = new ImagePlus("the stack", newStack);
        return newImage;
	}
	/**
	 * @return Returns the path.
	 */
	public String getPath() {
		if (path == null) {
			this.getPathFromUser();
			return path;
		}
		if (this.getOutputFolder() == "") {
			return path;
		}

		String result = this.getOutputFolder();
		result = result
				+ path.substring(path.lastIndexOf(File.separator), path
						.length());
		path = result;
		return path;

	}

	/**
	 * 
	 */
	public void getPathFromUser() {
		// FileSaver fileSaver = new FileSaver(inputImage);
		// path = fileSaver.getPath("TIFF", ".tif");
		name = inputImage.getTitle();
		SaveDialog sd = new SaveDialog("Save as " + "TIFF", name, ".tif");
		name = sd.getFileName();
		if (name == null)
			return;
		String directory = sd.getDirectory();
		inputImage.startTiming();
		path = directory + name;
	}
}
