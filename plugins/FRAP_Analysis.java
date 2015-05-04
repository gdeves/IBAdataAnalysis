import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

import ij.*;
import ij.process.*;
import ij.gui.*;
import ij.plugin.*;
import ij.plugin.filter.Analyzer;
import ij.plugin.frame.PlugInFrame;
import ij.measure.*;
import ij.util.*;

/*  This plugin is for the analysis of FRAP experiments.
 *  The plugin lets you choose the starting slice corresponding to the slice where the cell
 *  has been hitted and defines automatically a ROI corresponding to the zone hitted by the
 *  laser and plots the corresponding intensity profile.
 *
 *  Given that the plugin is looking for the barycenter of the hitted spot, it can only be
 *  applied for FRAP experiments with a single spot.
 *  It able also to determine the intensity of the rest of the cell in order to normalize
 *  the FRAP measurements.
 *
 *  Last updated: 04-13-2009
 *  author : Philippe Carl (phcarl@free.fr)
*/

public final class FRAP_Analysis extends PlugInFrame implements ActionListener, ChangeListener, Measurements
{
	ImagePlus imp, imp1, imp2, imp3;
	Roi roi;
	ImageStack stack;
	ImageProcessor ip, ip3;

	JTextField startSlice;
	JButton startButton, goButton, normalizeButton, doneButton;
	JSlider maxLevelSlider;
	JLabel maxLevelLabel;

	static final boolean DEBUG = false;

	String title;
	int xcen, ycen;
	double min_level;
	double max_level;
	double image_min;
	double image_max;

	public FRAP_Analysis()
	{
		super("FRAP_Analysis");

		if (DEBUG)
			IJ.log("FRAP_Analysis.<init>(): initialization happening");

		FRAP_initialize();
		FRAP_makeWindow();
	}

        @Override
	public void windowActivated(WindowEvent e)
	{
		if (DEBUG)
			IJ.log("FRAP_Analysis.windowActivated(" + e.toString() + ")");

		super.windowActivated(e);

		/* re-initialize if the current image has changed */
		if (WindowManager.getCurrentImage() != imp)
		{
		    FRAP_initialize();
		    FRAP_remakeWindow();
		}
	}

	public void run(ImageProcessor ip)
	{
		if ( DEBUG ) IJ.log("FRAP_Analysis.run(ImageProcessor)...");

		FRAP_initialize();

		FRAP_makeWindow();
	}

	public void FRAP_initialize()
	{
		if ( DEBUG ) IJ.log("Cell_Outliner.initialize...");

		imp = WindowManager.getCurrentImage();
		stack = imp.getStack();
		if (stack == null)
		{
			IJ.error("This plugin only works on image stacks, not single frames.");
			return;
		}
		roi = imp.getRoi();
		if (roi == null)
		{ // ensure there is always some ROI.
			roi = new Roi(0, 0, imp.getWidth(), imp.getHeight(), imp);
			imp.setRoi(roi);
		}

		int measurements = Analyzer.getMeasurements();
		// defined in Set Measurements dialog
		measurements |= CENTROID; // make sure centroid is included
		measurements |= MIN_MAX;  // make sure min_max is included
		Analyzer.setMeasurements(measurements);

		ImageStatistics stats = imp.getStatistics(measurements);

		min_level = stats.min;
		max_level = stats.max;

		imp.killRoi();
		stats = imp.getStatistics(measurements);
		image_min = stats.min;
		image_max = stats.max;
//		IJ.log("min max = " + image_min + " " + image_max );
	}


	protected void FRAP_makeWindow()
	{
		if ( DEBUG ) IJ.log("FRAP_Analysis.makeWindow...");

		goButton = new JButton ( "find ROI" );

		// Create a label for the max level slider:
		maxLevelLabel = new JLabel( "Max Level: " + (int)max_level, JLabel.LEFT );
		maxLevelLabel.setAlignmentX( Component.LEFT_ALIGNMENT );

		// Create the max level slider:
		maxLevelSlider = new JSlider( JSlider.HORIZONTAL, (int) image_min, (int) image_max, (int) max_level );
		maxLevelSlider.setMajorTickSpacing( (int) (image_max - image_min) / 4 );
		maxLevelSlider.setMinorTickSpacing( (int) (image_max - image_min) / 16 );
		maxLevelSlider.setPaintTicks( true );
	        maxLevelSlider.setPaintLabels( true );
        	maxLevelSlider.setBorder( BorderFactory.createEmptyBorder( 0, 0, 10, 0 ) );
	        maxLevelSlider.addChangeListener( this );

		normalizeButton = new JButton ( "Normalize" );
		doneButton = new JButton( "Done" );

		// For stacks:
		int ns = stack.getSize();
		if ( DEBUG ) IJ.log( "getSize = " + ns );

		/* Set a too-long label here, so that when things are packed,
		   they will be sufficiently big. Then, we re-set the label text
		   to say what we actually want to say. */

		startButton = new JButton("Starting Slice:");
		startButton.setActionCommand("starting slice");
		startButton.addActionListener( this );

		startSlice = new JTextField("1", 4);

		// Position everything in the content pane:
		JPanel contentPane = new JPanel();
		contentPane.setLayout( new BoxLayout( contentPane, BoxLayout.Y_AXIS ) );

		JPanel slicerPanel = new JPanel();
		slicerPanel.add(startButton);
		slicerPanel.add(startSlice);

		contentPane.add(slicerPanel);
		addAButton( goButton, contentPane );
		contentPane.add(Box.createVerticalStrut(20));
		contentPane.add( maxLevelLabel );
		contentPane.add( maxLevelSlider );

		addAButton( normalizeButton, contentPane );
		contentPane.add(Box.createVerticalStrut(20));
		addAButton( doneButton, contentPane );
		add(contentPane);
		pack();
		setVisible( true );
	}


	void addAButton(JButton button, Container container)
	{
		button.addActionListener( this );
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		container.add(button);
	}


	protected void FRAP_remakeWindow()
	{
		maxLevelLabel.setText("Max Level: " + (int)max_level);
		maxLevelSlider.setMinimum((int)image_min);
		maxLevelSlider.setMaximum((int)image_max);
		maxLevelSlider.setValue((int)max_level);
		maxLevelSlider.setMajorTickSpacing((int)(image_max-image_min) / 4);
		maxLevelSlider.setMinorTickSpacing((int)(image_max-image_min) / 16);
		maxLevelSlider.repaint(); // shouldn't be needed...

		maxLevelLabel.setText("Max Level: " + (int)max_level);
		maxLevelSlider.setMinimum((int)image_min);
		maxLevelSlider.setMaximum((int)image_max);
		maxLevelSlider.setValue((int)max_level);
		maxLevelSlider.setMajorTickSpacing((int)(image_max-image_min) / 4);
		maxLevelSlider.setMinorTickSpacing((int)(image_max-image_min) / 16);
		maxLevelSlider.repaint(); // shouldn't be needed...

//		firstSlice.setText("1");

//		updateSliceLabel();
	}

	public void stateChanged(ChangeEvent e)
	{
		if(e.getSource() == maxLevelSlider)
		{
			maxLevelLabel.setText( "Max Level: " + maxLevelSlider.getValue() );
			max_level = (double)maxLevelSlider.getValue();

			imp.killRoi();
			Wand wand = new Wand(ip);
			wand.npoints = 0;
			wand.autoOutline( xcen, ycen, (int)min_level, (int)max_level );
			roi = new PolygonRoi( wand.xpoints, wand.ypoints, wand.npoints, imp, Roi.TRACED_ROI);
			imp.setRoi( roi );
		}
	}


	public void actionPerformed(ActionEvent e)
	{
		Object b = e.getSource();

		if (b == startButton)
		{
			startSlice.setText(Integer.toString(imp.getCurrentSlice()));
		}
		if (b == goButton)
		{
			if ( DEBUG )
			IJ.log( "Button pressed: " + e.getActionCommand() );
			testStack();

			title = stack.getShortSliceLabel( Integer.parseInt(startSlice.getText()) - 1 );
			ip = stack.getProcessor( Integer.parseInt(startSlice.getText()) - 1);
			imp1 = new ImagePlus(title, ip);

			title = stack.getShortSliceLabel( Integer.parseInt(startSlice.getText()) );
			ip = stack.getProcessor( Integer.parseInt(startSlice.getText()) );
			imp2 = new ImagePlus(title, ip);

			ImageCalculator ic = new ImageCalculator();
			ic.calculate("create, sub", imp1, imp2);

			imp3 = WindowManager.getCurrentImage();
			ip3 = imp3.getProcessor();
			ip3.autoThreshold();
			imp3.updateAndDraw();

			findRoi();
			imp.restoreRoi();

			double[] y = getZAxisProfile();
			if (y!=null)
			{
				double[] x = new double[y.length];
				for (int i = 0; i != x.length; i++)
//					x[i] = i + Integer.parseInt(startSlice.getText());
					x[i] = i + 1;
				PlotWindow pw = new PlotWindow("FRAP Analysis: "+ imp.getTitle(), "Slice", "Mean", x, y);
				double [] a = Tools.getMinMax(x);
				double xmin=a[0], xmax=a[1];
				a = Tools.getMinMax(y);
				double ymin=a[0], ymax=a[1];
				pw.setLimits(xmin, xmax, ymin, ymax);
				pw.draw();
			}
		}
		if (b == normalizeButton)
		{
			double[] y = getZAxisProfile();
			if (y!=null)
			{
				double[] x = new double[y.length];
				for (int i = 0; i != x.length; i++)
//					x[i] = i + Integer.parseInt(startSlice.getText());
					x[i] = i + 1;
				PlotWindow pw = new PlotWindow("FRAP Analysis: "+ imp.getTitle(), "Slice", "Mean", x, y);
				double [] a = Tools.getMinMax(x);
				double xmin=a[0], xmax=a[1];
				a = Tools.getMinMax(y);
				double ymin=a[0], ymax=a[1];
				pw.setLimits(xmin, xmax, ymin, ymax);
				pw.draw();
			}
		}
		if (b == doneButton)
		{
			setVisible(false);
			dispose();
		}
	}


	private void findRoi()
	{
		int i, j;
		int nb = 0;
		xcen = 0;
		ycen = 0;

		ImageConverter imageConvert = new ImageConverter(imp3);
		imageConvert.convertToGray8();

		int background = 0;
		int foreground = 255;
		fill(ip3, foreground, background);

		for(j = 0; j != imp3.getHeight(); j++)
		{
			for(i = 0; i != imp3.getWidth(); i++)
			{
				if(ip3.getPixelValue(i, j) != 0)
				{
					xcen += i;
					ycen += j;
					nb++;
				}
			}
		}
		xcen /= nb;
		ycen /= nb;

		imp3.killRoi();
		Wand wand = new Wand(ip3);
//		wand.npoints = 0;
		wand.autoOutline(xcen, ycen);
		roi = new PolygonRoi( wand.xpoints, wand.ypoints, wand.npoints, imp3, Roi.TRACED_ROI);
		imp3.setRoi(roi);
	}


	private void fill(ImageProcessor ip, int foreground, int background)
	{
		int i, j;
		FloodFiller ff = new FloodFiller(ip);
		ip.setColor(127);

		for (j = 0; j != ip.getHeight(); j++) 
		{
			if (ip.getPixel(0,                 j) == background) ff.fill(0,                 j);
			if (ip.getPixel(ip.getWidth() - 1, j) == background) ff.fill(ip.getWidth() - 1, j);
		}

		for (i = 0; i != ip.getWidth(); i++)
		{
			if (ip.getPixel(i, 0)                  == background) ff.fill(i, 0);
			if (ip.getPixel(i, ip.getHeight() - 1) == background) ff.fill(i, ip.getHeight() - 1);
		}

		for (j = 0; j != ip.getHeight(); j++)
		{
			for (i = 0; i != ip.getWidth(); i++)
			{
				if (ip.getPixel(i, j) == 127)
					ip.putPixel(i, j, background);
				else
					ip.putPixel(i, j, foreground);
			}
		}
	}


	double[] getZAxisProfile()
	{
//		int size = stack.getSize() - Integer.parseInt(startSlice.getText()) + 1;
		int size = stack.getSize();
		double[] values = new double[size];

		Calibration cal = imp.getCalibration();
		Analyzer analyzer = new Analyzer(imp);
		int measurements = analyzer.getMeasurements();
		boolean showResults = measurements!=0 && measurements!=LIMIT;
		measurements |= MEAN;
		if (showResults)
		{
			if (!analyzer.resetCounter())
				return null;
		}

		for (int i = 0; i != size; i++)
		{
//			ip = stack.getProcessor(i + Integer.parseInt(startSlice.getText()));
			ip = stack.getProcessor(i + 1);
			ip.setRoi(roi);
			ImageStatistics stats = ImageStatistics.getStatistics(ip, measurements, cal);
			analyzer.saveResults(stats, roi);
//			if (showResults)
//				analyzer.displayResults();
			values[i] = (double)stats.mean;
		}
		return values;
	}


	public void testStack() throws IllegalArgumentException
	{
		int first;

		try
		{
			first = Integer.parseInt(startSlice.getText());
		}
		catch (NumberFormatException ex)
		{
			IJ.error("Invalid slice number: please use an integer!");
			throw new IllegalArgumentException();
		}

		if (first < 1 || first >= imp.getStackSize())
		{
			IJ.error("Invalid slice range: please stick within the bounds of the current image stack.");
			throw new IllegalArgumentException();
		}
	}
}
