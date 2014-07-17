package regionGrowing;

import ij.ImagePlus;

public class MagicWandFloat extends MagicWand {

	public MagicWandFloat(ImagePlus img) {
		super(img);
	}
	
	public float getPixel(int x, int y) {
		int pos = y*width+x;
		return (((float[])(this.data))[pos]);
	}

}
