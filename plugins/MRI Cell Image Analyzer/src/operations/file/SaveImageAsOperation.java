package operations.file;
import ij.ImagePlus;
import ij.io.FileSaver;
import gui.options.ChoiceOption;


public class SaveImageAsOperation extends SaveImageOperation {
	private static final long serialVersionUID = 1176194264453451253L;
	protected ChoiceOption format;
	
	protected void initialize() throws ClassNotFoundException {	
		super.initialize();
		String[] newOptionsNames = new String[optionsNames.length+1];
		for (int i=0;i<optionsNames.length;i++) {
			newOptionsNames[i] = optionsNames[i];
		}
		newOptionsNames[optionsNames.length] = "format";
		optionsNames = newOptionsNames;
	}
	
	protected void setupOptions() {
		super.setupOptions();
		format = new ChoiceOption(this.getFormats());
		format.setValue("jpg");
		format.setName(optionsNames[optionsNames.length-1]);
		format.setShortHelpText("Select the output format.");
		this.options.add(format);
	}

	private String[] getFormats() {
		String[] formats = {"bmp", "fits", "gif", "jpg", "lut", "pgm", "png", "raw", "tif", "txt", "zip"};
		return formats;
	}
	
	public void connectOptions() {
		super.connectOptions();
		this.format = (ChoiceOption) this.options.getOptions().get(4);
	}
	
	public void doIt() {
		ImagePlus inputImage = this.inputImage;
		if (application==null && inputImage==null) inputImage = this.getInputImage(); 
		if (inputImage==null) return;
		inputImage = this.getInputImage();
		FileSaver saver = new FileSaver(inputImage);
		path = this.getPath();
		this.addPathAdditions();
		this.setExtension();
		if (inputImage.getStackSize()==1){
			if (this.getFormat().equals("bmp")) this.setSuccess(new Boolean(saver.saveAsBmp(path)));
			if (this.getFormat().equals("fits")) this.setSuccess(new Boolean(saver.saveAsFits(path)));
			if (this.getFormat().equals("gif")) this.setSuccess(new Boolean(saver.saveAsGif(path)));
			if (this.getFormat().equals("jpg")) this.setSuccess(new Boolean(saver.saveAsJpeg(path)));
			if (this.getFormat().equals("lut")) this.setSuccess(new Boolean(saver.saveAsLut(path)));
			if (this.getFormat().equals("pgm")) this.setSuccess(new Boolean(saver.saveAsPgm(path)));
			if (this.getFormat().equals("png")) this.setSuccess(new Boolean(saver.saveAsPng(path)));
			if (this.getFormat().equals("raw")) this.setSuccess(new Boolean(saver.saveAsRaw(path)));
			if (this.getFormat().equals("tif")) this.setSuccess(new Boolean(saver.saveAsTiff(path)));
			if (this.getFormat().equals("txt")) this.setSuccess(new Boolean(saver.saveAsText(path)));
			if (this.getFormat().equals("zip")) this.setSuccess(new Boolean(saver.saveAsZip(path)));
		}
		else {
			int extIndex = path.lastIndexOf(".");
			String newPath = path + ".tif";
			if (extIndex!=-1) newPath = path.substring(0, extIndex) + ".tif";
			this.setSuccess(new Boolean(saver.saveAsTiffStack(newPath)));
		}
	}

	private void setExtension() {
		String filePath = path.substring(0,path.lastIndexOf("."));
		path = filePath + "." + this.getFormat();
	}

	public String getFormat() {
		return format.getValue();
	}

	public void setFormat(String format) {
		this.format.setValue(format);
	}
}
