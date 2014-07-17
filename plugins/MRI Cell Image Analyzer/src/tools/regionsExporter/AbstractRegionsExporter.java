package tools.regionsExporter;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.io.Opener;
import ij.measure.Calibration;
import java.io.File;
import java.io.IOException;
import java.util.List;
import utils.ImageDuplicator;

public abstract class AbstractRegionsExporter implements IRegionsExporter {

	public AbstractRegionsExporter(List<String> imageIDs) {
		this.setImageIDs(imageIDs);
	}
	
	@Override
	public void addRegion(int i, Roi region) {
		this.getRegions(i).add(region);
	}

	@Override
	public void addRegion(Roi region) {
		this.addRegion(this.getIndex(), region);
	}

	@Override
	public boolean export() throws IOException {
		boolean result = true;
		ImagePlus image = this.getImage();
		int counter = 0;
		for (Roi roi : this.getRegions()) {
			image.setRoi(roi);
			ImagePlus regionImage = ImageDuplicator.copyImage(image, roi.getName());
			String path = this.getTargetPath(counter);
			File folder = new File(path).getParentFile();
			if (!folder.exists()) folder.mkdir();
			IJ.save(regionImage, path);
			counter++;
		}
		return result;
	}

	private String getTargetPath(int region) {
		String path = this.getTargetFolder();
		path += "/" + this.getImageName() +  "-r" +
						this.paddedNumberString(region, this.getRegions().size()) + 
						this.getExtension();
		return path;
	}

	private String getExtension() {
		String result = "";
		String[] components = getPathComponents();
		if (components.length<2) return result;
		result = "." + components[1];
		return result;
	}

	public String getImageName() {
		String[] components = getPathComponents();
		String result = components[0];
		return result;
	}
	
	private String[] getPathComponents() {
		String nameWithExt = new File(this.getImageID()).getName();
		String[] components = nameWithExt.split("\\.");
		return components;
	}
	
	private String paddedNumberString(int counter, int max) {
		String result = "";
		String maxString = Integer.toString(max);
		String counterString = Integer.toString(counter);
		int diff = maxString.length() - counterString.length();
		for (int i=0; i<diff; i++) result += "0";
		result += counterString;
		return result;
	}

	@Override
	public boolean exportAll() throws IOException {
		boolean result = true;
		this.first();
		while (this.next()) result &= export();
		return result;
	}

	@Override
	public void first() {
		this.set(0);
	}

	@Override
	public Calibration getCalibration(int i) {
		return this.getCalibrations().get(i);
	}

	@Override
	public Calibration getCalibration() {
		Calibration calibration = this.getCalibration(this.getIndex());
		return calibration;
	}

	public ImagePlus getImage() {
		ImagePlus image = this.getImage(this.getIndex());
		return image;
	}

	@Override
	public ImagePlus getImage(int i) {
		if (this.getLoadedImageIndex()==i && this.getLoadedImage() != null) return this.getLoadedImage();
		Opener opener = new Opener();
		this.setLoadedImage(opener.openImage(this.getImageID(i)));
		this.setLoadedImageIndex(i);
		return this.getLoadedImage();
	}
	
	@Override
	public String getImageID() {
		String id = this.getImageID(this.getIndex());
		return id;
	}

	@Override
	public String getImageID(int i) {
		return this.getImageIDs().get(i);
	}

	@Override
	abstract public List<String> getImageIDs();

	@Override
	abstract public int getIndex();
	
	@Override
	public int getNumberOfImages() {
		return this.getImageIDs().size();
	}
	
	@Override
	public List<Roi> getRegions(int i) {
		return this.getAllRegions().get(i);
	}

	@Override
	public List<Roi> getRegions() {
		List<Roi> regions = this.getRegions(this.getIndex());
		return regions;
	}

	@Override
	public String getTargetFolder(int i) {
		String folder = this.getTargetFolders().get(i);
		if (folder.isEmpty()) folder = new File(this.getImageID(i)).getParent() + "/regions";
		return folder;
	}

	@Override
	public String getTargetFolder() {
		String targetFolder = this.getTargetFolder(this.getIndex());
		return targetFolder;
	}

	@Override
	public void last() {
		this.set(this.getNumberOfImages()-1);

	}

	@Override
	public boolean next() {
		int index = this.getIndex();
		if (index>=this.getNumberOfImages()-1) return false;
		this.set(++index);
		return true;
	}

	@Override
	public boolean previous() {
		int index = this.getIndex();
		if (index<=0) return false;
		this.set(--index);
		return true;
	}


	@Override
	public void removeRegion(Roi region) {
		this.removeRegion(this.getIndex(), region);
	}
	
	@Override
	public void removeRegion(int i, Roi region) {
		this.getRegions(i).remove(region);
	}

	@Override
	abstract public void set(int i);

	@Override
	public void setCalibration(int i, Calibration calibration) {
		this.getCalibrations().set(i, calibration);
	}

	@Override
	public void setCalibration(Calibration calibration) {
		this.setCalibration(this.getIndex(), calibration);
	}

	@Override
	public void setImageID(int i, String path) {
		this.getImageIDs().set(i, path);
	}

	@Override
	public void setTargetFolder(int i, String path) {
		this.getTargetFolders().set(i, path);
	}

	@Override
	public void setTargetFolder(String path) {
		this.setTargetFolder(this.getIndex(), path);
	}

	abstract protected List<String> getTargetFolders();
	abstract protected List<Calibration> getCalibrations();
	abstract protected List<List<Roi>> getAllRegions();
	abstract protected int getLoadedImageIndex();
	abstract protected ImagePlus getLoadedImage();
	abstract protected void setLoadedImage(ImagePlus openImage);
	abstract protected void setLoadedImageIndex(int i);
	abstract protected void setImageIDs(List<String> ids);
}
