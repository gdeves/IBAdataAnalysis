/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2007 Volker B�cker
 * MRI Cell Image Analyzer has been created at Montpellier RIO Imaging
 * www.mri.cnrs.fr
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 */
package tools.multipleImageWindow;

import java.awt.Window;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import neuronJNewGUI.SlideShowControl;
import operations.channel.MergeChannelsOperation;
import operations.channel.SplitChannelsOperation;
import operations.file.OpenImageOperation;
import operations.image.ApplyLutOperation;
import utils.Broadcaster;
import gui.ListEditor;
import ij.ImagePlus;
import ij.gui.ImageWindow;
import ij.plugin.Duplicator;
import imagejProxies.MRIInterpreter;

/**
 * A window separated into four areas. Each area can contain an image. The size of the areas can be changed
 * using a horizontal and a vertical split pane.
 * 
 * @author Volker B�cker
 */
public class FourImageWindow extends ImageWindow {
	private static final long serialVersionUID = 7188455238655473633L;
	public static final int UPPERLEFT = 0;
	public static final int UPPERRIGHT = 1;
	public static final int LOWERLEFT = 2;
	public static final int LOWERRIGHT = 3;
	protected ImagePlusPlus[] images = new ImagePlusPlus[4];
	protected FourImageWindowView view;
	protected Broadcaster broadcaster = new Broadcaster();
	protected int activeComponent;
	protected boolean slidersLocked = false;
	private ListEditor listEditor;
	protected SlideShowControl slideShowControl;
	protected ArrayList<PlugableImageAnalysisApplication> activeApplications = new ArrayList<PlugableImageAnalysisApplication>();

	public FourImageWindow() {
		super("");
		setActiveComponent(UPPERLEFT);
		this.getView();
	}

	public String getTitle() {
		String result = "empty";
		if (images[0]!=null) result = "1:"+images[0].getTitle();
		if (images[1]!=null) result+= " 2:"+images[1].getTitle();
		if (images[2]!=null) result+= " 3:"+images[2].getTitle();
		if (images[3]!=null) result+= " 4:"+images[3].getTitle();
		return result;
	}
	
	public void setVisible(boolean value) {
		this.getView().setVisible(value);
		this.changed("upperLeftImage");
	}

	protected Window getView() {
		if (this.view == null) this.view = new FourImageWindowView(this);
		return view;
	}
	
	public boolean isVisible() {
		return this.getView().isVisible();
	}

	public void setImage(int position, ImagePlus anImage) {
		if (images[position]!=null) images[position].close();
		images[position] = new ImagePlusPlus(anImage.getTitle(), anImage.getProcessor());
		prepareImage(images[position]);
		this.changeImagesInApplications();
		this.changed(this.getImageChangedMessages()[position]);
	}
	
	private String[] getImageChangedMessages() {
		String[] result = {"upperLeftImage", "upperRightImage", "lowerLeftImage", "lowerRightImage"};
		return result;
	}

	private void prepareImage(ImagePlus anImage) {
		anImage.setImage(anImage.getProcessor().createImage());
		MRIInterpreter interpreter = new MRIInterpreter();
		interpreter.setBatchMode(true);
		ImageWindow win = new ImageWindow(anImage);
		anImage.setWindow(win);
		interpreter.setBatchMode(false);
	}

	private void changed(String string) {
		broadcaster.changed("FourImageWindow", string);
	}

	public Broadcaster getBroadcaster() {
		return broadcaster;
	}

	public ImagePlus getUpperLeftImage() {
		return images[0];
	}

	public ImagePlus getUpperRightImage() {
		return images[1];
	}

	public ImagePlus getLowerLeftImage() {
		return images[2];
	}

	public ImagePlus getLowerRightImage() {
		return images[3];
	}

	public void addImage(ImagePlus image) {
		if (image.getType()==ImagePlus.COLOR_RGB) {
			this.addRGBImage(image);
			return;
		}
		this.addGreyScaleImage(image);
	}

	private void changeImagesInApplications() {
		Iterator<PlugableImageAnalysisApplication> it = this.getActiveApplications().iterator();
		while(it.hasNext()) {
			PlugableImageAnalysisApplication current = it.next();
			current.setRedImage(this.getUpperLeftImage());
			current.setGreenImage(this.getUpperRightImage());
			current.setBlueImage(this.getLowerLeftImage());
		}
	}

	public void addGreyScaleImage(ImagePlus image) {
		Duplicator copyAction = new Duplicator();
		ImagePlus copy = copyAction.run(image);
		copy.setTitle(image.getTitle() + "+");
		this.setImage(this.getActiveComponent(), copy);
	}

	public int getActiveComponent() {
		return activeComponent;
	}

	public void addRGBImage(ImagePlus image) {
		Duplicator copyAction = new Duplicator();
		ImagePlus copy = copyAction.run(image);
		copy.setTitle(image.getTitle() + "+");
		SplitChannelsOperation split = new SplitChannelsOperation();
		split.setInputImage(copy);
		split.setShowResult(false);
		split.doIt();
		ApplyLutOperation applyLUT = new ApplyLutOperation();
		applyLUT.setKeepSource(false);
		applyLUT.setInputImage(split.getResultRed());
		applyLUT.setLookupTable("red");
		applyLUT.doIt();
		applyLUT.setInputImage(split.getResultGreen());
		applyLUT.setLookupTable("green");
		applyLUT.doIt();
		applyLUT.setInputImage(split.getResultBlue());
		applyLUT.setLookupTable("blue");
		applyLUT.doIt();
		this.setImage(UPPERLEFT, split.getResultRed());
		this.setImage(UPPERRIGHT, split.getResultGreen());
		this.setImage(LOWERLEFT, split.getResultBlue());
		this.setImage(LOWERRIGHT, copy);
	}

	public void setActiveComponent(int component) {
		this.activeComponent=component;
		this.changed("activeComponent");
	}

	public void openImage() {
		OpenImageOperation open = new OpenImageOperation();
		open.setShowResult(false);
		String path = open.getPath();
		if (path==null) return;
		File imageFile = new File(path);
		this.openImage(imageFile);
	}
	
	public void openImage(File aFile) {
		if (isMultiChannel(aFile)) {
			this.loadMultiChannelImage(aFile);
			return;
		}
		OpenImageOperation open = new OpenImageOperation();
		open.setShowResult(false);
		open.setPath(aFile.getAbsolutePath());
		open.doIt();
		this.addImage(open.getResult());
	}

	public void loadMultiChannelImage(File imageFile) {
		File redFile = getChannel1File(imageFile, "red", "green", "blue");
		File greenFile = getChannel1File(imageFile, "green", "red", "blue");
		File blueFile = getChannel1File(imageFile, "blue", "red", "green");
		OpenImageOperation open = new OpenImageOperation();
		ImagePlus redImage = null;
		if (redFile!=null) {
			open.setShowResult(false);
			open.setPath(redFile.getAbsolutePath());
			open.doIt();
			redImage = open.getResult();
		}
		ImagePlus greenImage = null;
		if (greenFile!=null) {
			open.setPath(greenFile.getAbsolutePath());
			open.doIt();
			greenImage = open.getResult();	
		}
		ImagePlus blueImage = null;
		if (blueFile!=null) {
			open.setPath(blueFile.getAbsolutePath());
			open.doIt();
			blueImage = open.getResult();
		}
		MergeChannelsOperation merge = new MergeChannelsOperation();
		merge.setShowResult(false);
		merge.setInputImageRed(redImage);
		merge.setInputImageGreen(greenImage);
		merge.setInputImageBlue(blueImage);
		if (redFile==null) merge.setRedChannel("none");
		if (greenFile==null) merge.setGreenChannel("none");
		if (blueFile==null) merge.setBlueChannel("none");
		merge.doIt();
		String basename = getBasename(redFile, greenFile, blueFile);
		merge.getResult().setTitle(basename);
		this.addRGBImage(merge.getResult());		
		if (images[0]!=null && redFile!=null) images[0].setTitle(redFile.getName());
		if (images[1]!=null && greenFile!=null) images[1].setTitle(greenFile.getName());
		if (images[2]!=null && blueFile!=null) images[2].setTitle(blueFile.getName());
		this.changed(this.getImageChangedMessages()[0]);
	}

	public String getBasename(File redFile, File greenFile, File blueFile) {
		ArrayList<String> filenames = new ArrayList<String>();
		if (redFile!=null) filenames.add(redFile.getName());
		if (greenFile!=null) filenames.add(greenFile.getName());
		if (blueFile!=null) filenames.add(blueFile.getName());
		String name1 = filenames.get(0);
		String name2 = filenames.get(1);
		if (name1.length()>name2.length()) {
			name1 = filenames.get(1);
			name2 = filenames.get(0);
		}
		int start = -1;
		for (int i=0; i<name1.length(); i++) {
			if (name1.charAt(i)!=name2.charAt(i)) {
				start=i;
				break;
			}
		}
		int end = -1;
		for (int i=0; i<name1.length(); i++) {
			if (name1.charAt(name1.length()-1-i)!=name2.charAt(name2.length()-1-i)) {
				end=name1.length()-1-i;
				break;
			}
		}
		String result = name1.substring(0, start) + name1.substring(end+1, name1.length());
		return result;
	}

	private File getChannel1File(File file, String channel1, String channel2, String channel3) {
		String filename = file.getName();
		if (filename.toLowerCase().contains(channel1)) return file;
		String directory = file.getParent();
		File folder = new File(directory);
		File[] files = folder.listFiles();
		String basename = filename.toLowerCase().replace(channel2, "").replace(channel3, "");
		for (int i=0; i<files.length; i++) {
			File aFile = files[i];
			if (aFile.getName().toLowerCase().replace(channel1, "").equals(basename)) return aFile;
		}
		return null;
	}

	public boolean isMultiChannel(File imageFile) {
		String reducedName = imageFile.getName().toLowerCase().replace("red", "").replace("green", "").replace("blue", "");
		String directory = imageFile.getParent();
		File folder = new File(directory);
		File[] files = folder.listFiles();
		int counter = 0;
		for (int i=0; i<files.length; i++) {
			if (files[i].getName().toLowerCase().replace("red", "").replace("green", "").replace("blue", "")
				.equals(reducedName)) counter++;
		}
		return counter>1;
	}
	
	static public void showNew() {
		(new FourImageWindow()).setVisible(true);
	}

	public boolean getSlidersLocked() {
		return slidersLocked;
	}

	public void setSlidersLocked(boolean slidersLocked) {
		this.slidersLocked = slidersLocked;
	}

	public void closeAllImages() {
		for (int i=0; i<this.images.length; i++) {
			if (images[i]!=null) images[i].close();
		}	
	}

	public ListEditor getListEditor() {
		if (listEditor==null) listEditor = new ListEditor();
		return listEditor;
	}

	public SlideShowControl getSlideShowControl() {
		if (slideShowControl==null) {
			slideShowControl = new SlideShowControl();
			slideShowControl.setCloseLastImage(false);
			slideShowControl.setCloseAllImages(false);
		}
		return slideShowControl;
	}

	public void gotoFirstImage() {
		SlideShowControl control = getSlideShowControl();
		if (!control.gotoFirstImage()) return;
		this.openImage(control.getCurrentFile());
	}

	public void gotoPreviousImage() {
		SlideShowControl control = getSlideShowControl();
		if (!control.gotoPreviousImage()) return;
		this.openImage(control.getCurrentFile());
	}

	public void gotoNextImage() {
		SlideShowControl control = getSlideShowControl();
		if (!control.gotoNextImage()) return;
		this.openImage(control.getCurrentFile());
	}

	public void gotoLastImage() {
		SlideShowControl control = getSlideShowControl();
		if (!control.gotoLastImage()) return;
		this.openImage(control.getCurrentFile());
	}

	public void revertCurrentImage() {
		SlideShowControl control = getSlideShowControl();
		if (control.getCurrentFile()==null) return;
		this.openImage(control.getCurrentFile());
	}
	
	public ArrayList<PlugableImageAnalysisApplication> getActiveApplications() {
		ArrayList<Integer> indices = new ArrayList<Integer>();
		Iterator<PlugableImageAnalysisApplication> it = activeApplications.iterator();
		int index=0;
		while(it.hasNext()) {
			PlugableImageAnalysisApplication current = it.next();
			if (!current.getComponent().isShowing()) indices.add(index);
			index++;
		}
		Iterator<Integer> indexIterator = indices.iterator();
		while (indexIterator.hasNext()) {
			index = indexIterator.next().intValue();
			activeApplications.remove(index);
		}
		return activeApplications;
	}
	
	public void addActiveApplication(PlugableImageAnalysisApplication application) {
		this.getActiveApplications().add(application);
		changeImagesInApplications();
	}
}

