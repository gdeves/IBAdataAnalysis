/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2007 Volker Bäcker
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
package operations.processing;

import gui.Options;
import gui.options.BooleanOption;
import gui.options.Option;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.macro.Interpreter;
import operations.FilterOperation;

/**
* Calls the plugin LoG 3D. A spot detector based on 3D Laplacian of Gaussian or Mexican Hat
* 
* Daniel Sage
* http://bigwww.epfl.ch/sage/soft/LoG3D/index.html
*/  
public class EnhanceSpotsOperation extends FilterOperation {
	protected Option sigmaX;
	protected Option sigmaY;
	protected Option sigmaZ;
	protected Option volume;

	private static final long serialVersionUID = 4238604458936674065L;

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[4];
		optionsNames[0] = "sigma x";
		optionsNames[1] = "sigma y";
		optionsNames[2] = "sigma z";
		optionsNames[3] = "volume";
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		result = inputImage;
		if (result.getType()==ImagePlus.COLOR_RGB) {
			IJ.error("Enhance spots operation can't handle RGB images.");
			return;
		}
		WindowManager.setTempCurrentImage(result);
		String oldTitle = result.getTitle();
		result.setTitle(oldTitle + "...tmp");
		this.runFilter();
		inputImage.setTitle(oldTitle);
		result.setTitle("LoG of " + oldTitle);
		result.hide();
	}
	
	public void runFilter() {
		int volume = 0;
		if (this.getVolume()) volume = 1;
		String parameter = "sigmax=" + this.getSigmaX() + 
						  " sigmay=" + this.getSigmaY() + 
						  " sigmaz=" + this.getSigmaZ() +
						  " displaykernel=0" +
						  " volume=" + volume;
		boolean isBatchMode = Interpreter.isBatchMode();
		this.getInterpreter().setBatchMode(true);
		IJ.run("LoG 3D", parameter);
	    int active = Thread.activeCount();
	    System.out.println("currently active threads: " + active);
	    Thread all[] = new Thread[active];
	    Thread.enumerate(all);
		ImagePlus tmp = null;
		while (tmp==null) {
			tmp = WindowManager.getImage("LoG of " + inputImage.getTitle());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Thread.yield();
		}
		result = tmp;
		for (Thread workingThread : all) {
			WindowManager.setTempCurrentImage(workingThread, null);
		}
		this.getInterpreter().setBatchMode(isBatchMode);
	}
	
	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		this.sigmaX = new Option();
		this.setSigmaX(3);
		this.sigmaX.setName(optionsNames[0]);
		this.sigmaX.setShortHelpText("The filter size in x-direction.");
		options.add(this.sigmaX);
		this.sigmaY = new Option();
		this.setSigmaY(3);
		this.sigmaY.setName(optionsNames[1]);
		this.sigmaY.setShortHelpText("The filter size in y-direction.");
		options.add(this.sigmaY);
		this.sigmaZ = new Option();
		this.setSigmaZ(3);
		this.sigmaZ.setName(optionsNames[2]);
		this.sigmaZ.setShortHelpText("The filter size in z-direction.");
		options.add(this.sigmaZ);
		this.volume = new BooleanOption();
		this.setVolume(false);
		this.volume.setName(optionsNames[3]);
		this.volume.setShortHelpText("Select volume processing or slice-by-slice processing.");
		options.add(this.volume);
	}

	public void connectOptions() {
		this.sigmaX = (Option) this.options.getOptions().get(0);
		this.sigmaY = (Option) this.options.getOptions().get(1);
		this.sigmaZ = (Option) this.options.getOptions().get(2);
		this.volume = (BooleanOption) this.options.getOptions().get(3);
	}
	
	public float getSigmaX() {
		return sigmaX.getFloatValue();
	}

	public void setSigmaX(float sigmaX) {
		this.sigmaX.setValue(Float.toString(sigmaX));
	}

	public float getSigmaY() {
		return sigmaY.getFloatValue();
	}

	public void setSigmaY(float sigmaY) {
		this.sigmaY.setValue(Float.toString(sigmaY));
	}
	
	public float getSigmaZ() {
		return sigmaZ.getFloatValue();
	}

	public void setSigmaZ(float sigmaZ) {
		this.sigmaZ.setValue(Float.toString(sigmaZ));
	}
	
	public boolean getVolume() {
		return volume.getBooleanValue();
	}

	public void setVolume(boolean value) {
		this.volume.setValue(Boolean.toString(value));
	}
}
