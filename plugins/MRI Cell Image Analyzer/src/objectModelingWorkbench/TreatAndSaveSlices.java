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
package objectModelingWorkbench;

import gui.ProgressBar;
import ij.IJ;
import ij.gui.Toolbar;
import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import applications.Application;

/**
 * Run the application on each slice and save the result image. Applies the alignment if applyAlignments is true. 
 * 
 * @author Volker B�cker
 */
public class TreatAndSaveSlices implements Runnable {

	protected Rectangle zeroAndImageSize;
	protected File targetFile;
	protected ArrayList<AlignmentSlice> alignments;
	protected Thread task;
	protected Application application;
	protected boolean showProgress;
	protected ProgressBar progress;
	protected boolean applyAlignments;

	public TreatAndSaveSlices(ArrayList<AlignmentSlice> alignments, File targetFile, Rectangle zeroAndImageSize, Application app, boolean applyAlignments) {
		this.alignments = alignments;
		this.targetFile = targetFile;
		this.zeroAndImageSize = zeroAndImageSize;
		this.application = app;
		this.applyAlignments = applyAlignments;
	}

	public void run() {
		Color backgroundColor  = Toolbar.getBackgroundColor();
		IJ.setBackgroundColor(0, 0, 0);
		for(AlignmentSlice currentAlignment : alignments) {
			currentAlignment.setApplication(this.application);
			currentAlignment.saveWithSize(targetFile, zeroAndImageSize, applyAlignments);
			if (isShowProgress()) progress.setProgress(progress.getProgress() + 1);
			Thread.yield();
		}
		Toolbar.setBackgroundColor(backgroundColor);
	}
			
	public void execute() { 
		if (this.isShowProgress()) {
			progress = new ProgressBar(0, alignments.size()-1, "treating slices");
			progress.setCloseWhenFinished(true);
			progress.getView().setVisible(true);
		}
		this.task = new Thread(this);
		task.start();
	}

	public boolean isShowProgress() {
		return showProgress;
	}

	public void setShowProgress(boolean showProgress) {
		this.showProgress = showProgress;
	}
}
