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
package gui;

import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.measure.Calibration;
import ij.plugin.DragAndDrop;
import ij.plugin.MacroInstaller;
import ij.IJ;
import imagejProxies.MRIJava2;
import java.awt.dnd.DropTarget;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import neuronJNewGUI.NeuronJ;
import neuronJgui.NJ;
import applications.Application;
import operations.Operation;
import operations.channel.SplitChannelsOperation;
import operations.file.OpenImageOperation;
import operations.file.SaveImageOperation;
import operations.image.InvertImageOperation;
import operations.processing.DilateOperation;
import operations.processing.ErodeOperation;
import operations.processing.SubtractMeanOperation;
import operations.segmentation.AutoThresholdOperation;
import operations.segmentation.EntropyThresholdOperation;
import operations.segmentation.MeanThresholdOperation;
import operations.segmentation.OtsuThresholdOperation;
import movie.MRI_QT_Stack_Writer_fast;

/**
 * This is the launcher window of the Cell Image Analyzer. It is still there for backward compatibility. 
 * New components should better be made available using the standard ImageJ mechanisms (toolsets, plugins, etc)
 * 
 * @author	Volker Baecker
 **/
public class CellImageAnalyzer extends Observable {

	protected CellImageAnalyzerView view;
	Map<String, String> applicationFiles = new HashMap<String, String>();
	Map<String, String> operationFiles = new HashMap<String, String>();
	
	static protected boolean calledFromImageJ;
	
	static protected CellImageAnalyzer current = null;
	
	public CellImageAnalyzer() {
		current = this;
		this.initialize();
		resetScale();
	}

	private void resetScale() {
		ImagePlus image = new ImagePlus();
		Calibration calibration = image.getCalibration();
		calibration.setUnit("pixel");
		image.setGlobalCalibration(calibration);
	}

	/**
	 * Initialize the application. Create the view and register myself as model. 
	 */
	private void initialize() {
		try {
			if (IJ.getInstance()==null) {
				new ImageJ();
				IJ.getInstance().setVisible(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		MRIJava2.setLookAndFeelSet(true);
		view = new CellImageAnalyzerView(this);
		view.setModel(this);
		view.setDropTarget(null);
		new DropTarget(view, new DragAndDrop());
	}
	
	/**
	 * Create a new instance and run it.
	 * @param args	not used
	 */
	public static void main(String[] args) {
		CellImageAnalyzer.getCurrent().run();
	}
	
	/**
	 * Show my view.
	 */
	private void run() {
		view.setVisible(true);
	}
	
	/**
	 * Notify observers that an aspect of the client has changed.
	 */
	protected void changed(Object anObject) {
		this.setChanged();//required by notifyObservers()
		notifyObservers(anObject);
	}
	
	/**
	 * The open image menu action. Ask the user to select an image file. Open the
	 * image and show it.
	 */
	public void openImage() {
		OpenImageOperation openImageAction = new OpenImageOperation();
		openImageAction.execute();
		ImagePlus image = openImageAction.getResult();
		if (image==null) return;
		image.show();
	}

	public void splitChannels() {
		SplitChannelsOperation splitChannelsAction = new SplitChannelsOperation();
		ImagePlus inputImage = WindowManager.getCurrentImage();
		if (inputImage == null) return;
		if (inputImage.getType()!=ImagePlus.COLOR_256 && inputImage.getType()!=ImagePlus.COLOR_RGB) {
			return;
		}
		splitChannelsAction.setInputImage(inputImage);
		splitChannelsAction.execute();
	}
	
	public void autoThreshold() {
		ImagePlus inputImage = WindowManager.getCurrentImage();
		AutoThresholdOperation action = new AutoThresholdOperation();
		action.setInputImage(inputImage);
		action.execute();
	}

	public void entropyThreshold() {
		ImagePlus inputImage = WindowManager.getCurrentImage();
		EntropyThresholdOperation action = new EntropyThresholdOperation();
		action.setInputImage(inputImage);
		action.execute();
	}
	
	public void meanThreshold() {
		ImagePlus inputImage = WindowManager.getCurrentImage();
		MeanThresholdOperation action = new MeanThresholdOperation();
		action.setInputImage(inputImage);
		action.execute();
	}
	
	public void otsuThreshold() {
		ImagePlus inputImage = WindowManager.getCurrentImage();
		OtsuThresholdOperation action = new OtsuThresholdOperation();
		action.setInputImage(inputImage);
		action.execute();
	}

	public void invertImage() {
		ImagePlus inputImage = WindowManager.getCurrentImage();
		InvertImageOperation action = new InvertImageOperation();
		action.setInputImage(inputImage);
		action.execute();
	}
	
	/**
	 * Close the application. Close all image windows before exiting.
	 */
	public void exit() {
		if (CellImageAnalyzer.calledFromImageJ && IJ.getInstance().isVisible()) {
			this.getView().setVisible(false);
			return;
		}
		WindowManager.closeAllWindows();
		IJ.getInstance().quit();
		System.exit(0);
	}

	public void saveAs() {
		ImagePlus inputImage = WindowManager.getCurrentImage();
		SaveImageOperation save = new SaveImageOperation();
		save.setInputImage(inputImage);
		save.execute();
	}

	public void showOperations(String category) {
		OperationsBox box = getOperationsFor(category);
		box.show();
	}

	public OperationsBox getOperationsFor(String category) {
		OperationsBox box = new OperationsBox();
		box.setTitle(category + " operations");
		ArrayList<Class<?>> classes = Operation.operationClasses();
		for (Class<?> aClass : classes) {
			try {
				Constructor<?> ct = aClass.getConstructor((Class[]) null);
				Operation operation = (Operation) ct.newInstance((Object[]) null);
				if (category.equals("all")) {
					box.add(operation);
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return box;
	}
	
	public void fillApplicationsMenu(JMenu theMenu) {
		File startFolder = new File("." + File.separator + Application.baseFolder() + File.separator);
		this.fillMenu(startFolder, Application.fileExtension(), theMenu);
	}

	void fillMenu(File startFolder, String fileExtension, JMenu theMenu) {
		File[] files = startFolder.listFiles();
		for (File aFile : files) {
			String filename = aFile.getName();
			if (filename.equals("CVS")) continue;
			if (filename.equals(".svn")) continue;
			if (aFile.isDirectory()) {
				JMenu aMenu = new JMenu(aFile.getName());
				theMenu.add(aMenu);
				this.fillMenu(aFile, fileExtension, aMenu);
				continue;
			}
			String shortFilename = filename.substring(0, filename.length()-4);
			String extension = filename.substring(filename.length()-4, filename.length());
			if (extension.equals(fileExtension)) {
				JMenuItem aMenuItem = new JMenuItem(shortFilename);
				if (fileExtension.equals(Application.fileExtension())) {
					applicationFiles.put(shortFilename, aFile.getAbsolutePath());
					this.addAddApplicationMenuActionListener(aMenuItem);
				} 
				if (fileExtension.equals(OperationsBox.fileExtension())) {
					operationFiles.put(shortFilename, aFile.getAbsolutePath());
					this.addAddOperationsMenuActionListener(aMenuItem);
				}
				theMenu.add(aMenuItem);
			}
		}
		
	}

	private void addAddOperationsMenuActionListener(JMenuItem menuItem) {
		menuItem.addActionListener(new java.awt.event.ActionListener() { 
			public void actionPerformed(java.awt.event.ActionEvent e) {    
				String aFilename = e.getActionCommand();
				String longName = operationFiles.get(aFilename);
				OperationsBox newBox = OperationsBox.load(longName);
				newBox.show();
			}
		});
		
	}

	private void addAddApplicationMenuActionListener(JMenuItem menuItem) {
		menuItem.addActionListener(new java.awt.event.ActionListener() { 
			public void actionPerformed(java.awt.event.ActionEvent e) {    
				String aFilename = e.getActionCommand();
				String longName = applicationFiles.get(aFilename);
				Application newApp = Application.load(longName);
				newApp.show();
			}
		});
	}

	public void fillOperationsMenu(JMenu theMenu) {
		File startFolder = new File("." + File.separator + OperationsBox.baseFolder() + File.separator);
		this.fillMenu(startFolder, OperationsBox.fileExtension(), theMenu);
	}
	/**
	 * @return Returns the current instance of the cell image analyzer.
	 */
	public static CellImageAnalyzer getCurrent() {
		if (current==null) current = new CellImageAnalyzer();
		return current;
	}

	public static void setCurrent(CellImageAnalyzer anApp) {
		current = anApp;
	}

	public void rebuildOperationMenu() {
		view.rebuildOperationsMenu();
	}

	public void rebuildApplicationMenu() {
		view.rebuildApplicationMenu();	
	}

	public void toggleImageJ() {
		if (NJ.ntb!=null) return;
		if (IJ.getInstance().isShowing()) {
			IJ.getInstance().setVisible(false);
		} else {
			IJ.getInstance().setVisible(true);
		}
		this.resetScale();
	}

	public void startNeuronJ() {
		NeuronJ.createAndShow();
	}

	public void substractMean() {
		ImagePlus inputImage = WindowManager.getCurrentImage();
		SubtractMeanOperation action = new SubtractMeanOperation();
		action.setInputImage(inputImage);
		action.execute();
	}

	public void dilate() {
		ImagePlus inputImage = WindowManager.getCurrentImage();
		DilateOperation action = new DilateOperation();
		action.setInputImage(inputImage);
		action.execute();
	}

	public void erode() {
		ImagePlus inputImage = WindowManager.getCurrentImage();
		ErodeOperation action = new ErodeOperation();
		action.setInputImage(inputImage);
		action.execute();
	}

	public void start3DObjectCounter() {
		IJ.run("Object Counter3D");
	}

	public void startExportAsQuicktime() {
		MRI_QT_Stack_Writer_fast writer = new MRI_QT_Stack_Writer_fast();
		gui.Executer executer = new gui.Executer(writer, "");
		Thread thread = new Thread(executer);
		thread.setPriority(Math.max(thread.getPriority()-2, Thread.MIN_PRIORITY));
		thread.start();
	}

	public void loadOperationCollections(File[] files) {
		for(File file : files) {
			OperationsBox opBox = OperationsBox.load(file.getAbsolutePath());
			opBox.show();
		}
	}

	public void loadApplications(File[] files) {
		for(File file : files) {
			Application app = Application.load(file.getAbsolutePath());
			app.show();
		}
	}

	public CellImageAnalyzerView getView() {
		return view;
	}
	
    public void startInVivo() {
        IJ.doCommand("StkImage ");
    }

	public Map<String, String> getApplicationFiles() {
		return applicationFiles;
	}

	static public void setCalledFromImageJ() {
		calledFromImageJ = true;
	}
	
	static public boolean hasBeenCalledFromImageJ() {
		return calledFromImageJ;
	}
	
	static public void switchToMRIToolset() {
		String path = IJ.getDirectory("macros")+"toolsets/"+"Montpellier RIO Imaging"+".txt";
		new MacroInstaller().run(path);
	}
}
