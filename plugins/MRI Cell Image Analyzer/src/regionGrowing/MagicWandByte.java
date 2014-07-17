package regionGrowing;

import ij.ImagePlus;

public class MagicWandByte extends MagicWand {

	public MagicWandByte(ImagePlus img) {
		super(img);
	}

	public float getPixel(int x, int y) {
		int pos = y*width+x;
		return (((byte[])(this.data))[pos])&0xff;
	}
}
