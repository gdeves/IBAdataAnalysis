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
package operations.analysis;

import java.io.File;

import javax.swing.JFrame;

import ij.ImagePlus;
import ij.text.TextPanel;
import gui.Options;
import gui.options.BooleanOption;
import gui.options.Option;
import operations.Operation;
import particleTracking.MRIMTrack2_;

/**
 * Track moving particles in a binary image and measure the distance traveled and the
 * length of the path for each particle. The operation calls the MTrack2 plugin from 
 * Nico Stuurman (http://valelab.ucsf.edu/~nico/IJplugins/MTrack2.html)
 * 
 * @author Volker
 */
public class TrackParticlesOperation extends Operation {
	private static final long serialVersionUID = 6214834452229289276L;
	protected Option minSize;
	protected Option maxSize;
	protected Option minTrackLength;
	protected Option maxVelocity;
	protected Option resultFilename;
	protected BooleanOption showLabels;
	protected BooleanOption showPositions;
	protected BooleanOption showPaths;
	protected BooleanOption showPathLengths;
	protected Option maxColumns;
	protected MRIMTrack2_ mTrack2Plugin;
	protected ImagePlus pathsImage;
	protected TextPanel trackMeasurements;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[10];
		optionsNames[0] = "min size";
		optionsNames[1] = "max size";
		optionsNames[2] = "min track length";
		optionsNames[3] = "max velocity";
		optionsNames[4] = "result filename";
		optionsNames[5] = "show labels";
		optionsNames[6] = "show positions";
		optionsNames[7] = "show paths";
		optionsNames[8] = "show path lengths";
		optionsNames[9] = "max columns";
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		resultTypes = new Class[3];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultTypes[1] = Class.forName("ij.ImagePlus");
		resultTypes[2] = Class.forName("ij.text.TextPanel");
		resultNames = new String[3];
		resultNames[0] = "Result";
		resultNames[1] = "PathsImage";
		resultNames[2] = "TrackMeasurements";
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		this.mTrack2Plugin = null;
		result = inputImage;
		MRIMTrack2_ plugin = this.getMTrack2Plugin();
		plugin.run(result.getProcessor());
		this.setResult(plugin.getLablesImage());
		this.setPathsImage(plugin.getPathsImage());
		if (this.getShowPaths()) this.getPathsImage().show();
		this.setTrackMeasurements(plugin.getResultText());
	}

	private MRIMTrack2_ getMTrack2Plugin() {
		if (mTrack2Plugin==null) {
			mTrack2Plugin = new MRIMTrack2_();
			MRIMTrack2_.setMinSize(this.getMinSize());
			MRIMTrack2_.setMaxSize(this.getMaxSize());
			MRIMTrack2_.setMinTrackLength(this.getMinTrackLength());
			if ( this.getResultFilename()!=null && this.getResultFilename().trim().length()!=0) {
				MRIMTrack2_.setSaveResultsFile(true);
				mTrack2Plugin.setFilename(this.getResultFilename().substring(
								this.getResultFilename().lastIndexOf(File.separator), this.getResultFilename().length()));
				mTrack2Plugin.setDirectory(this.getResultFilename().substring(
						0, this.getResultFilename().lastIndexOf(File.separator)));
			} else {
				MRIMTrack2_.setSaveResultsFile(false);
			}
			MRIMTrack2_.setShowLabels(this.getShowLabels());
			MRIMTrack2_.setShowPositions(this.getShowPositions());
			MRIMTrack2_.setShowPaths(this.getShowPaths());
			MRIMTrack2_.setShowPathLengths(this.getShowPathLengths());
			MRIMTrack2_.setMaxVelocity(this.getMaxVelocity());
			MRIMTrack2_.setMaxColumns(this.getMaxColumns());
			mTrack2Plugin.setup("", result);
		}
		return mTrack2Plugin;
	}

	public int getMaxColumns() {
		return maxColumns.getIntegerValue();
	}

	public void setMaxColumns(int maxColumns) {
		this.maxColumns.setValue(Integer.toString(maxColumns));
	}

	public int getMaxSize() {
		return maxSize.getIntegerValue();
	}

	public void setMaxSize(int maxSize) {
		this.maxSize.setValue(Integer.toString(maxSize));
	}

	public int getMaxVelocity() {
		return maxVelocity.getIntegerValue();
	}

	public void setMaxVelocity(int maxVelocity) {
		this.maxVelocity.setValue(Integer.toString(maxVelocity));
	}

	public int getMinSize() {
		return minSize.getIntegerValue();
	}

	public void setMinSize(int minSize) {
		this.minSize.setValue(Integer.toString(minSize));
	}

	public int getMinTrackLength() {
		return minTrackLength.getIntegerValue();
	}

	public void setMinTrackLength(int minTrackLength) {
		this.minTrackLength.setValue(Integer.toString(minTrackLength));
	}

	public String getResultFilename() {
		return resultFilename.getValue();
	}

	public void setResultFilename(String resultFilename) {
		this.resultFilename.setValue(resultFilename);
	}

	public boolean getShowLabels() {
		return showLabels.getBooleanValue();
	}

	public void setShowLabels(boolean showLabels) {
		this.showLabels.setValue(Boolean.toString(showLabels));
	}

	public boolean getShowPathLengths() {
		return showPathLengths.getBooleanValue();
	}

	public void setShowPathLengths(boolean showPathLengths) {
		this.showPathLengths.setValue(Boolean.toString(showPathLengths));
	}

	public boolean getShowPaths() {
		return showPaths.getBooleanValue();
	}

	public void setShowPaths(boolean showPaths) {
		this.showPaths.setValue(Boolean.toString(showPaths));
	}

	public boolean getShowPositions() {
		return showPositions.getBooleanValue();
	}

	public void setShowPositions(boolean showPositions) {
		this.showPositions.setValue(Boolean.toString(showPositions));
	}
	
	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		
		this.minSize = new Option();
		this.setMinSize(1);
		this.minSize.setName(optionsNames[0]);
		options.add(minSize);
		
		this.maxSize = new Option();
		this.setMaxSize(999999);
		this.maxSize.setName(optionsNames[1]);
		options.add(maxSize);
		
		this.minTrackLength = new Option();
		this.setMinTrackLength(2);
		this.minTrackLength.setName(optionsNames[2]);
		options.add(minTrackLength);
		
		this.maxVelocity = new Option();
		this.setMaxVelocity(10);
		this.maxVelocity.setName(optionsNames[3]);
		options.add(maxVelocity);
		
		this.resultFilename = new Option();
		resultFilename.beForFilename();
		this.resultFilename.setName(optionsNames[4]);
		options.add(resultFilename);
		
		this.showLabels = new BooleanOption();
		this.setShowLabels(true);
		this.showLabels.setName(optionsNames[5]);
		options.add(showLabels);
		
		this.showPositions = new BooleanOption();
		this.setShowPositions(false);
		this.showPositions.setName(optionsNames[6]);
		options.add(showPositions);
		
		this.showPaths = new BooleanOption();
		this.setShowPaths(true);
		this.showPaths.setName(optionsNames[7]);
		options.add(showPaths);
		
		this.showPathLengths = new BooleanOption();
		this.setShowPathLengths(true);
		this.showPathLengths.setName(optionsNames[8]);
		options.add(showPathLengths);
		
		this.maxColumns = new Option();
		this.setMaxColumns(75);
		this.maxColumns.setName(optionsNames[9]);
		options.add(maxColumns);
	}
	
	public void connectOptions() {
		minSize  = (Option)options.getOptions().get(0);
		maxSize  = (Option)options.getOptions().get(1);
		minTrackLength  = (Option)options.getOptions().get(2);
		maxVelocity  = (Option)options.getOptions().get(3);
		resultFilename  = (Option)options.getOptions().get(4);
		showLabels  = (BooleanOption)options.getOptions().get(5);
		showPositions  = (BooleanOption)options.getOptions().get(6);
		showPaths  = (BooleanOption)options.getOptions().get(7); 
		showPathLengths = (BooleanOption)options.getOptions().get(8);
		maxColumns  = (Option)options.getOptions().get(9);
	}

	public ImagePlus getPathsImage() {
		return pathsImage;
	}

	public void setPathsImage(ImagePlus pathsImage) {
		this.pathsImage = pathsImage;
	}

	public TextPanel getTrackMeasurements() {
		return trackMeasurements;
	}

	public void setTrackMeasurements(TextPanel trackMeasurements) {
		this.trackMeasurements = trackMeasurements;
	}
	
	public void showResult() {
		if (this.getResult() != null) this.getResult().show();
		if (this.getTrackMeasurements()==null) return;
		JFrame window = new JFrame();
		window.setSize(300, 200);
		window.add(this.getTrackMeasurements());
		window.invalidate();
		window.setVisible(true);
	}
}
