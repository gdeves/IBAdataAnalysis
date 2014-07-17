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
import java.io.File;
import java.util.ArrayList;
import java.util.Observable;
import applications.Application;

/**
 * Scales the slices in the ObjectModelingWorkbench and changes
 * the translations accordingly.
 * 
 * @author Volker B�cker
 */
public class ScaleSlices extends Observable implements Runnable {
	protected File targetFile;
	protected ArrayList<AlignmentSlice> translations;
	protected Thread task;
	protected Application application;
	protected boolean showProgress;
	protected ProgressBar progress;
	
	public ScaleSlices(ArrayList<AlignmentSlice> translations, File targetFile, Application application) {
		this.translations = translations;
		this.targetFile = targetFile;
		this.application = application;
	}

	public boolean isShowProgress() {
		return showProgress;
	}

	public void setShowProgress(boolean showProgress) {
		this.showProgress = showProgress;
	}
	
	public void execute() { 
		if (this.isShowProgress()) {
			progress = new ProgressBar(0, translations.size()-1, "treating slices");
			progress.setCloseWhenFinished(true);
			progress.getView().setVisible(true);
		}
		this.task = new Thread(this);
		task.start();
	}

	public void run() {
		for(AlignmentSlice currentTranslation : this.translations) {
			currentTranslation.setApplication(this.application);
			currentTranslation.scale(targetFile);
			if (isShowProgress()) progress.setProgress(progress.getProgress() + 1);
			Thread.yield();
		}
		this.changed("running");
		this.deleteObservers();
	}

	public Thread getTask() {
		return task;
	}

	public void setTask(Thread task) {
		this.task = task;
	}
	
	public void changed(String string) {
		this.setChanged();
		this.notifyObservers(string);
		this.clearChanged();
	}
}
