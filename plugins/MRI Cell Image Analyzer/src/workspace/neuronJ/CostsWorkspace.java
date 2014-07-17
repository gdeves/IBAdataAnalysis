/*
 * Created on 11.08.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package workspace.neuronJ;

import operations.channel.SplitChannelsOperation;
import operations.file.OpenImageOperation;
import operations.file.SaveImageOperation;
import nj.Costs;
import ij.ImagePlus;
import ij.gui.NewImage;
import ij.process.ByteProcessor;
/**
 * @author Volker
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CostsWorkspace {

	public static void main(String[] args) {
		OpenImageOperation openImageAction = new OpenImageOperation();
		openImageAction.setShowResult(false);
		openImageAction.setPath("C:\\Dokumente und Einstellungen\\All Users\\Dokumente\\Eigene Dateien\\bioinformatik\\projekte\\cell  image analysis\\projects\\schwob\\combing\\Bt2_1_01.tif");
		openImageAction.execute();
		ImagePlus inputImage = openImageAction.getResult();
		inputImage.show();
		SplitChannelsOperation splitAction = new SplitChannelsOperation();
		splitAction.setShowResult(false);
		splitAction.setInputImage(inputImage);
		splitAction.setKeepRedChannel(false);
		splitAction.setKeepGreenChannel(true);
		splitAction.setKeepBlueChannel(false);
		splitAction.execute();
		ImagePlus redChannelImage = splitAction.getResultGreen();
		Costs costsImage = new Costs();
		float[][][] costs = costsImage.run((ByteProcessor)redChannelImage.getProcessor(), 2.0f);
		ImagePlus result = NewImage.createByteImage("costs", costs[0][0].length, costs[0].length, 1, NewImage.GRAY8);
		for (int xIndex = 0; xIndex < costs[0][0].length; xIndex++) {
			for (int yIndex = 0; yIndex < costs[0].length; yIndex++) {
				result.getProcessor().putPixel(xIndex, yIndex, (int)Math.round(costs[0][yIndex][xIndex]));
			}
		}
		SaveImageOperation saveOperation = new SaveImageOperation();
		saveOperation.setPath("C:\\tmp\\out_green.tiff");
		saveOperation.setInputImage(result);
		saveOperation.execute();
		System.out.println("saved");
		result.show();
		ImagePlus potential = NewImage.createByteImage("costs", costs[1][0].length, costs[1].length, 1, NewImage.GRAY8);
		int raster = 20;
		for (int xIndex = 0; xIndex < costs[1][0].length; xIndex+=raster) {
			for (int yIndex = 0; yIndex < costs[1].length; yIndex+=raster) {
				float xValue =(costs[2][yIndex][xIndex]);
				float yValue = (costs[1][yIndex][xIndex]);
				float length = 128-(costs[0][yIndex][xIndex]);
				potential.getProcessor().drawLine(xIndex, yIndex, Math.round(xIndex+xValue*(length / 2.0f)), Math.round(yIndex+yValue*(length/2.0f)));
			}
		}
		potential.show();
	}
}
