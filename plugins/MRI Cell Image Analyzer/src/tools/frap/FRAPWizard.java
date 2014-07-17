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
package tools.frap;

import gui.options.ListOption;
import ij.ImagePlus;
import ij.gui.Roi;
import java.awt.Rectangle;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Vector;
import operations.control.ForeachImageInListDoOperation;
import operations.file.OpenSeriesAsStackOperation;
import applications.Application;
import utils.Zoom;

/**
 * The frap wizard measures the brightest cube within a region, in a 
 * time-series of volume images.  
 * 
 * @author	Volker Baecker
 **/
public class FRAPWizard extends Observable implements FilenameFilter {
	protected FRAPWizardView view;
	protected FRAPTableModel data;
	protected ImagePlus image;
	protected boolean closeSession;
	protected int lastSelectionIndex = -1;
	protected Thread thread;
	protected int timePoint = 0;
	protected boolean showProjections = true;
	
	public FRAPWizardView getView() {
		if (view == null) {
			view = new FRAPWizardView(this);
		}
		return view;
	}
	
	public void show() {
		this.getView().setVisible(true);
	}

	protected void changed(String anAspect) {
		this.setChanged();
		this.notifyObservers(anAspect);
	}
	
	public void addFolders(Vector<File> list) {
		Vector<File> notEmptyFolders = new Vector<File>();
		for(File aFile : list) {
			String[] images = aFile.list(this);
			if (images.length>0) {
				notEmptyFolders.add(aFile);
			}
		}
		data.constructFrom(notEmptyFolders);
		this.changed("folderList");
	}

	public FRAPTableModel getData() {
		if (data==null) {
			data = new FRAPTableModel();
		}
		return data;
	}

	public void showImageNumber(int index) {
		if (lastSelectionIndex!=-1) {
			this.saveSelection(lastSelectionIndex);
		}
		Rectangle bounds = null;
		double zoom = 1;
		if (image!=null && image.getWindow()!=null) {
			bounds = image.getWindow().getBounds();
			zoom = image.getCanvas().getMagnification();
			image.close();
		}
		Roi roi = data.getSelectionForIndex(index);
		File folder = data.getFolderForIndex(index);
		
		String[] images = folder.list(this);
		Arrays.sort(images, new FileComparator());
		if (timePoint>images.length-1) timePoint = images.length-1;
		this.loadImage(folder.getAbsolutePath() + File.separator + images[timePoint]);
		image.setRoi(roi);
		image.show();
		if (bounds!=null) {
			image.getWindow().setBounds(bounds);
			Zoom.zoomTo(image.getWindow(), zoom);
		}
		lastSelectionIndex = index;
	}

	public void showProjection(int index) {
		if (lastSelectionIndex!=-1) {
			this.saveSelection(lastSelectionIndex);
		}
		Rectangle bounds = null;
		double zoom = 1;
		if (image!=null && image.getWindow()!=null) {
			bounds = image.getWindow().getBounds();
			zoom = image.getCanvas().getMagnification();
			image.close();
		}
		Roi roi = data.getSelectionForIndex(index);
		File folder = data.getFolderForIndex(index);
		
		File[] images = folder.listFiles(this);
		String[] imageNames = folder.list(this);
		Arrays.sort(imageNames, new FileComparator());
		if (!this.existsProjection(folder)) {
			this.createProjections(images);
		}
		OpenSeriesAsStackOperation open = new OpenSeriesAsStackOperation();
		open.setInteractive(false);
		open.setPath(folder.getAbsolutePath() + File.separator + "projections" + File.separator + imageNames[0]);
		open.run();
		this.image = open.getResult();
		image.setRoi(roi);
		image.show();
		if (bounds!=null) {
			image.getWindow().setBounds(bounds);
			Zoom.zoomTo(image.getWindow(), zoom);
		}
		lastSelectionIndex = index;
	}
	
	protected boolean existsProjection(File folder) {
		boolean result = false;
		String[] images = folder.list(this);
		String lastImage = images[images.length-1];
		String projectionsFolderName = folder.getAbsolutePath()  
									+ File.separator + "projections" + File.separator;
		String projectionShortName = lastImage.split("\\.")[0];
		File projectionsFolder = new File(projectionsFolderName);
		List<String> projectionFilenames = Arrays.asList(projectionsFolder.list());
		Iterator<String> it = projectionFilenames.iterator();
		while(it.hasNext()) {
			String projectionFileName = it.next();
			if (projectionFileName.contains(projectionShortName)) result = true;
		}
		return result;
	}

	private void createProjections(File[] images) {
		Application app = Application.load("./_applications/stacks/z projection for frap batch.cia");
		ForeachImageInListDoOperation doOp = (ForeachImageInListDoOperation) app.getOperations().get(0);
		ListOption option = (ListOption) doOp.getOptions().getOptions().get(0);
		option.getEditor().setList(new Vector<File>(Arrays.asList(images)));
		doOp.setImageListWasEmpty(false);
		doOp.setInteractive(false);
		app.doIt();
	}

	public void loadImage(String path) {
		this.image = data.loadImage(path);
	}

	public boolean accept(File dir, String name) {
		boolean result = false;
		if (name.contains(".tif") || 
			name.contains(".TIF") || 
			name.contains(".stk") || 
			name.contains(".STK")) result = true;
		return result;
	}

	public void saveSelection(int index) {
		if (image.getRoi()==null) return;
		data.setSelectionForIndexTo(index, image.getRoi());
	}

	public void setCloseSession(boolean value) {
		closeSession = value;
	}

	public void run() {
		MeasureMaxCubesTask task = new MeasureMaxCubesTask(data, closeSession);
		task.execute();
	}

	public void remove(int[] indices) {
		if (indices.length<1) return;
		data.remove(indices);
		this.changed("folderList");
	}

	public void incrementTimePoint() {
		if (this.isShowProjections()) return;
		if (lastSelectionIndex==-1) return;
		File folder = data.getFolderForIndex(lastSelectionIndex);
		String[] images = folder.list(this);
		if (timePoint<images.length-1) timePoint++; 
		showImageNumber(lastSelectionIndex);
	}

	public void decrementTimePoint() {
		if (this.isShowProjections()) return;
		if (lastSelectionIndex==-1) return;
		if (timePoint>0) timePoint--; 
		showImageNumber(lastSelectionIndex);
	}
	
	static public void createAndShow() {
		(new FRAPWizard()).show();
	}

	public boolean isShowProjections() {
		return showProjections;
	}

	public void setShowProjections(boolean showProjections) {
		this.showProjections = showProjections;
	}
}
