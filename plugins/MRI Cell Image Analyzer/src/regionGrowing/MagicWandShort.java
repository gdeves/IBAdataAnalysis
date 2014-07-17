package regionGrowing;

import ij.ImagePlus;

public class MagicWandShort extends MagicWand {

	public MagicWandShort(ImagePlus img) {
		super(img);
	}
	
	public float getPixel(int x, int y) {
		int pos = y*width+x;
		return (((short[])(this.data))[pos])&0xffff;
	}
}
