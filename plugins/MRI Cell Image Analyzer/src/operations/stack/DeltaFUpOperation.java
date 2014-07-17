package operations.stack;

import operations.Operation;
import regionGrowing.ImageTypeDoesntExist;
import utils.ImageAccessor;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;

public class DeltaFUpOperation extends Operation {

	private static final long serialVersionUID = 7399750771927529637L;

	protected void initialize() throws ClassNotFoundException {	
		super.initialize();
		parameterTypes = new Class[0];
		parameterNames = new String[0];
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		if (inputImage.getNSlices()<2) {
			result = getCopyOfOrReferenceTo(inputImage, "delta F up");
			return;
		}
		ImageStack stack = new ImageStack(inputImage.getWidth(), inputImage.getHeight());
		ImageStack inputStack = inputImage.getStack();
		for (int i=2; i<=inputImage.getNSlices(); i++) {
			float[] pixels = subtract(inputStack.getProcessor(i), inputStack.getProcessor(i-1));
			stack.addSlice("delta F up " + i, pixels);
		}
		result = new ImagePlus("delta F up", stack);
	}

	private float[] subtract(ImageProcessor processor, ImageProcessor processor2) {
		float[] result = null;
		int width = processor.getWidth();
		int height = processor.getHeight();
		try {
			ImageAccessor image1 = ImageAccessor.newFor(processor);
			ImageAccessor image2 = ImageAccessor.newFor(processor2);
			result = new float[width * height];
			for (int x=0;x<width;x++) {
				for (int y=0; y<height; y++) {
					result[y*width+x] = image1.getPixel(x, y) - image2.getPixel(x, y);
				}
			}
		} catch (ImageTypeDoesntExist e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
