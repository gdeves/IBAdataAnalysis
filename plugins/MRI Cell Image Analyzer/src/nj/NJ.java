/* Copyright � Erik Meijering. All rights reserved.
 *
 * Permission to copy and use the software and accompanying documentation provided 
 * on these webpages for educational, research, and not-for-profit purposes, without 
 * fee and without a signed licensing agreement, is hereby granted, provided that the 
 * above copyright notice, this paragraph and the following two paragraphs appear in all copies. 
 * The copyright holder is free to make upgraded or improved versions of the software available for a 
 * fee or commercially only.
 *
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, 
 * INCIDENTAL, OR CONSEQUENTIAL DAMAGES, OF ANY KIND WHATSOEVER, ARISING OUT OF THE USE OF THIS 
 * SOFTWARE AND ITS DOCUMENTATION, EVEN IF HE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * THE COPYRIGHT HOLDER SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE AND 
 * ACCOMPANYING DOCUMENTATION IS PROVIDED "AS IS". THE COPYRIGHT HOLDER HAS NO OBLIGATION TO PROVIDE 
 * MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 * 
 */
package nj;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.MessageDialog;
import ij.text.TextWindow;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class NJ {
    // NeuronJ version number:
    static final float VERSION = 1.01f;

    // NeuronJ copyright statement:
    static final String COPYRIGHT = "NeuronJ "+VERSION+" (C) Erik Meijering";

    // Flag for hidden keys:
    static final boolean hkeys = true;

    // Handles for shared objects:

    static TextWindow srw = null;
    static TextWindow trw = null;

    // Range for cursor-tracing 'nearby' determination
    static final float NEARBYRANGE = 3.0f;
    
    // Standard colors:
    static final Color ACTIVECOLOR = Color.red;
    static final Color HIGHLIGHTCOLOR = Color.white;
    
    // Regarding images:
    static private ImagePlus imageplus = null;
    static boolean image = false;
    static String imagename = "";
    static String workdir = "";
    static String[] workimages = null;
    static int workimagenr = 0;

    static void image(final ImagePlus imp) {
	imageplus = imp;
	if (imageplus == null) {
	    image = false;
	    imagename = "";
	} else {
	    image = true;
	    final String title = imageplus.getTitle();
	    final int dotIndex = title.lastIndexOf(".");
	    if (dotIndex >= 0) imagename = title.substring(0,dotIndex);
	    else imagename = title;
	}
    }

    // Method for showing no-image error message:
    static void noImage() {
	notify("Please load an image first using NeuronJ.");
	IJ.showStatus(COPYRIGHT);
    }

    // Colors supported by NeuronJ:
    static final Color[] colors = { Color.black, Color.blue, Color.cyan, Color.green, Color.magenta,
				    Color.orange, Color.pink, Color.red, Color.yellow };

    static final String[] colornames = { "Black", "Blue", "Cyan", "Green", "Magenta",
					 "Orange", "Pink", "Red", "Yellow" };

    static int colorIndex(final Color color) {
	final int nrcolors = colors.length;
	for (int i=0; i<nrcolors; ++i) if (color == colors[i]) return i;
	return -1;
    }
	
    // Tracing types, type colors, clusters:
    static String[] types = { "Default", "Axon", "Dendrite", "Primary", "Secondary", "Tertiary",
			      "Type 06", "Type 07", "Type 08", "Type 09", "Type 10" };

    static Color[] typecolors = { Color.magenta, Color.red, Color.blue, Color.red, Color.blue, Color.yellow,
				  Color.magenta, Color.magenta, Color.magenta, Color.magenta, Color.magenta};

    static String[] clusters = { "Default", "Cluster 01", "Cluster 02", "Cluster 03", "Cluster 04", "Cluster 05",
				 "Cluster 06", "Cluster 07", "Cluster 08", "Cluster 09", "Cluster 10" };

    // Switch for enabling or disabling (automatic) saving of tracings:
    static boolean autosave = false;
    static boolean save = false;
    
    // Switch for enabling or disabling debug messages:
    static boolean debug = false;
    
    // Scale at which eigenvalues are computed:
    static float scale = 2.0f;
    
    // Cost component weight factor:
    static float gamma = 0.7f;
    
    // Half-window size for snapping cursor to locally lowest cost:
    static int snaprange = 4;
    
    // Window size for shortest-path searching. Must be less than about
    // 2900 in order to avoid cumulative costs exceeding the range
    // spanned by the integers.
    static int dijkrange = 800;
    
    // For smoothing and subsampling tracing segments:
    static int halfsmoothrange = 5;
    static int subsamplefactor = 5;

    // Dimensions and unit of pixels:
    static float xsize = 1.0f;
    static float ysize = 1.0f;
    static String units = "micron";
    
    // Central method for writing messages:
    static void write(final String message) { if (debug) IJ.log(message); }

    // Central method for error messages:
    static void error(final String message) { new MessageDialog(IJ.getInstance(), "NeuronJ: Error", message); }

    // Central method for notifications:
    static void notify(final String message) { new MessageDialog(IJ.getInstance(), "NeuronJ: Note", message); }

    // Central method for out-of-memory notifications:
    static void outOfMemory() {
	write("Not enough memory for the computations");
	error("Not enough memory to process the current image.\nAbout " +
	      (imageplus.getWidth()*imageplus.getHeight()*25)/1000000 +
	      "MB is needed for the computations.");
	IJ.showProgress(1.0);
	IJ.showStatus(COPYRIGHT);
    }

    // Method for saving the parameters:
    static void saveParams() {

	write("Saving parameters to disk...");

	try {
	    final DataOutputStream dos =
		new DataOutputStream(new FileOutputStream("NJOptions.dat"));
	    dos.writeFloat(VERSION);
	    dos.writeFloat(scale);
	    dos.writeFloat(gamma);
	    dos.writeInt(snaprange);
	    dos.writeInt(dijkrange);
	    dos.writeInt(halfsmoothrange);
	    dos.writeInt(subsamplefactor);
	    dos.writeFloat(xsize);
	    dos.writeFloat(ysize);
	    final int nrunitschars = units.length();
	    dos.writeInt(nrunitschars);
	    dos.writeChars(units);
	    dos.writeBoolean(autosave);
	    dos.writeBoolean(debug);
	    dos.close();
	    write("Done");

	} catch(IOException e) {
	    write("Unable to write to file");
	}
    }

    // Method for loading the parameters:
    static float loadParams() {

	float disversion;

	try {
	    final DataInputStream dis =
		new DataInputStream(new FileInputStream("NJOptions.dat"));

	    disversion = dis.readFloat();

	    float disscale = scale;
	    float disgamma = gamma;
	    int dissnaprange = snaprange;
	    int disdijkrange = dijkrange;
	    int dishalfsmoothrange = halfsmoothrange;
	    int dissubsamplefactor = subsamplefactor;
	    float disxsize = xsize;
	    float disysize = ysize;
	    String disunits = units;
	    boolean disautosave = autosave;
	    boolean disdebug = debug;

	    if (disversion <= 1.01f) {
		disscale = dis.readFloat();
		disgamma = dis.readFloat();
		dissnaprange = dis.readInt();
		disdijkrange = dis.readInt();
		dishalfsmoothrange = dis.readInt();
		dissubsamplefactor = dis.readInt();
		disxsize = dis.readFloat();
		disysize = dis.readFloat();
		final int nrunitschars = dis.readInt();
		final char[] unitschars = new char[nrunitschars];
		for (int n=0; n<nrunitschars; ++n) unitschars[n] = dis.readChar();
		disunits = new String(unitschars);
		disautosave = dis.readBoolean();
		disdebug = dis.readBoolean();
	    }

	    dis.close();

	    scale = disscale;
	    gamma = disgamma;
	    snaprange = dissnaprange;
	    dijkrange = disdijkrange;
	    halfsmoothrange = dishalfsmoothrange;
	    subsamplefactor = dissubsamplefactor;
	    xsize = disxsize;
	    ysize = disysize;
	    units = disunits;
	    autosave = disautosave;
	    debug = disdebug;

	} catch(IOException e) {
	    disversion = -1.0f;
	}

	return disversion;
    }

   

    static void quit() {

	if (srw != null) { srw.setVisible(false); srw.dispose(); srw = null; }
	if (trw != null) { trw.setVisible(false); trw.dispose(); trw = null; }

	if (image) {
	    // Close image but first restore listeners to avoid
	    // a call to ntb.windowClosed():
	    imageplus.hide();
	}

		image(null);

	IJ.showStatus("");
	IJ.showProgress(1.0);
	write("Quitting NeuronJ");
    }

}
