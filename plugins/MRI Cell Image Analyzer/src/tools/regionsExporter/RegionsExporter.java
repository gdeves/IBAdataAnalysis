package tools.regionsExporter;

import ij.ImagePlus;
import ij.gui.Roi;
import ij.measure.Calibration;
import java.util.ArrayList;
import java.util.List;

public class RegionsExporter extends AbstractRegionsExporter {
	private int index;
	private List<String> targetFolders;
	private List<List<Roi>> regions;
	private List<String> ids;
	private int loadedImageIndex;
	private ImagePlus loadedImage;
	private List<Calibration> calibrations;
	
	public RegionsExporter(List<String> imageIDs) {
		super(imageIDs);
	}
	
	@Override
	public List<String> getImageIDs() {
		if (this.ids==null) this.ids = new ArrayList<String>();
		return ids;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	protected List<String> getTargetFolders() {
		if (this.targetFolders==null) this.targetFolders = new ArrayList<String>();
		return targetFolders;
	}

	@Override
	public void set(int i) {
		int max = this.getImageIDs().size()-1;
		if (i<0 || i>max) {
			throw new IndexOutOfBoundsException("i=" + i + " not in [0," + max + "]");
		}
		this.index = i;
	}

	@Override
	protected List<List<Roi>> getAllRegions() {
		if (this.regions==null) this.regions = new ArrayList<List<Roi>>(); 
		return regions;
	}

	@Override
	protected ImagePlus getLoadedImage() {
		return this.loadedImage;
	}

	@Override
	protected int getLoadedImageIndex() {
		return this.loadedImageIndex;
	}

	@Override
	protected void setLoadedImage(ImagePlus image) {
		this.loadedImage = image;
	}

	@Override
	protected void setLoadedImageIndex(int i) {
		this.loadedImageIndex = i;
	}

	@Override
	protected void setImageIDs(List<String> ids) {
		this.ids = ids;
		this.regions = new ArrayList<List<Roi>>();
		this.calibrations = new ArrayList<Calibration>();
		this.targetFolders = new ArrayList<String>();
		for (int i=0; i<ids.size(); i++) {
			this.calibrations.add(new Calibration());
			this.regions.add(new ArrayList<Roi>());
			this.targetFolders.add(new String());
		}
		this.index = 0;
		this.loadedImage = null;
		this.loadedImageIndex = -1;
	}	
	
	@Override
	protected List<Calibration> getCalibrations() {
		if (this.calibrations==null) this.calibrations = new ArrayList<Calibration>();
		return calibrations;
	}
}
