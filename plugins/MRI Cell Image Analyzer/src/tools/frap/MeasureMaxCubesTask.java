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

import gui.ProgressBar;
import gui.options.ListOption;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageWindow;
import ij.gui.Roi;
import imagejProxies.MRIInterpreter;
import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Vector;
import operations.control.CloseSessionOperation;
import operations.control.ForeachImageInListDoOperation;
import operations.reporting.ReportMeasurementsOperation;
import applications.Application;

/**
 * Runs the visual scripting application "measure best cube batch auto_3pix"
 * in a separated thread on the data passed in.
 * 
 * @author	Volker Baecker
 **/
public class MeasureMaxCubesTask implements Runnable {
	protected FRAPTableModel data;
	protected ProgressBar progress;
	protected Thread task;
	protected boolean closeSession;
	
	public MeasureMaxCubesTask(FRAPTableModel data, boolean closeSession) {
		super();
		this.data = data;
		this.closeSession = closeSession;
	}

	public void run() {
		int index = -1;
		for(Roi aRoi : data.getSelections()) {
			index++;
			if (aRoi==null) continue;
			
			File aFolder = data.getFolderForIndex(index);
			File[] tImages = aFolder.listFiles(new FRAPWizard());
			Arrays.sort(tImages, new FileComparator());
			String[] filenames = this.getFilenames(tImages);
			MRIInterpreter interpreter = new MRIInterpreter();
			interpreter.setBatchMode(true);
			
			Application app = Application.load("./_applications/stacks/measure best cube batch auto_3pix.cia");
			
			ForeachImageInListDoOperation operation = (ForeachImageInListDoOperation) app.getOperations().get(1);
			ListOption option = (ListOption) operation.getOptions().getOptions().get(0);
			option.getEditor().setList(new Vector<File>(Arrays.asList(tImages)));
			operation.setImageListWasEmpty(false);
			operation.setInteractive(false);
			
			ReportMeasurementsOperation reportOperation = (ReportMeasurementsOperation) app.getOperations().get(9);
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			SimpleDateFormat df = new SimpleDateFormat();
			df.applyPattern("yyyyMMddHHmmss");
			String timestampString = df.format(timestamp);
			reportOperation.setOutputPath(aFolder + File.separator + "mbc-report-" + timestampString + ".xls");
			reportOperation.setInteractive(false);
			
			ImagePlus image = data.loadImage(filenames[0]);
			image.setRoi(aRoi);
			ImageWindow win = new ImageWindow(image);
			WindowManager.setCurrentWindow(win);
			
			app.doIt();
			
			interpreter.setBatchMode(false);
			progress.setProgress(progress.getProgress() + 1);
		}
		if (closeSession) {
			CloseSessionOperation op = new CloseSessionOperation();
			op.setDeactivate(false);
			op.run();
		}
	}
	
	protected String[] getFilenames(File[] images) {
		String[] result = new String[images.length];
		for (int i=0; i<images.length; i++) {
			result[i] = images[i].getAbsolutePath();
		}
		return result;
	}

	public void execute() {
		progress = new ProgressBar(0, data.getNumberOfSelections(), "measure best cubes...");
		progress.setCloseWhenFinished(true);
		progress.getView().setVisible(true);
		
		this.task = new Thread(this);
		task.start();
	}

}
