package operations.roi;

import java.io.IOException;

import operations.file.SaveImageOperation;

import ij.io.RoiEncoder;

public class SaveSelectionOperation extends SaveImageOperation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doIt() {
		inputImage = this.getInputImage();
		if (inputImage==null) return;
		if (inputImage.getRoi()==null) return;
		path = this.getPath();
		this.addPathAdditions();
		String filePath = path.substring(0,path.lastIndexOf("."));
		path = filePath + ".roi";
		RoiEncoder encoder = new RoiEncoder(path);
		try {
			encoder.write(inputImage.getRoi());
		} catch (IOException e) {
			this.success = new Boolean(false);
			e.printStackTrace();
		}
	}
	
}
