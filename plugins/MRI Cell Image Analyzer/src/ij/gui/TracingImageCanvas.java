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
package ij.gui;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import tracing.Tracing;
import tracing.Tree;
import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.util.Java2;

/**
 * A special image canvas that displays tracings and trees. Used by the TreeTracer.
 *  
 * @author	Volker Baecker
 **/
public class TracingImageCanvas extends ImageCanvas {	
	private static final long serialVersionUID = -9153893715693277243L;
	protected ArrayList<Tracing> tracings;
	protected ArrayList<Tree> trees;

	public TracingImageCanvas(ImagePlus imp) {
		super(imp);
		tracings = new ArrayList<Tracing>();
		trees = new ArrayList<Tree>();
	}

	public void setTracings(ArrayList<Tracing> tracings) {
		this.tracings = tracings;
	}
	
	 public void paint(Graphics g) {
			Roi roi = imp.getRoi();
			if (roi != null) {
				roi.updatePaste();
				if (Prefs.doubleBuffer && !IJ.isMacOSX())
					{paintDoubleBuffered(g); return;}
			}
			try {
				if (imageUpdated) {
					imageUpdated = false;
					imp.updateImage();
				}
				if (IJ.isJava2())
					Java2.setBilinearInterpolation(g, Prefs.interpolateScaledImages);
				Image img = imp.getImage();
				if (img!=null)
	 				g.drawImage(img, 0, 0, (int)(srcRect.width*magnification), (int)(srcRect.height*magnification),
					srcRect.x, srcRect.y, srcRect.x+srcRect.width, srcRect.y+srcRect.height, null);
				if (getShowAllROIs()) drawAllROIs(g);
				for(Tracing tracing : tracings) tracing.draw(g, imp);
				for(Tree tree : trees) tree.draw(g, imp);
				if (roi != null) roi.draw(g);
				if (srcRect.width<imageWidth ||srcRect.height<imageHeight)
					drawZoomIndicator(g);
				if (IJ.debugMode) showFrameRate(g);
			}
			catch(OutOfMemoryError e) {IJ.outOfMemory("Paint");}
	    }

	public void setTrees(ArrayList<Tree> trees) {
		this.trees = trees;
	}

}
