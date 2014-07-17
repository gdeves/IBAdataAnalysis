/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2008 Volker B�cker
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
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.IndexColorModel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import javax.swing.SpinnerNumberModel;
import tools.grid.GridOptionsView;
import tools.grid.GridPoint;
import tools.grid.GridPositionComparator;
import tools.grid.LabelComparator;
import utils.Broadcaster;
import ij.IJ;
import ij.ImagePlus;
import ij.LookUpTable;
import ij.Prefs;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.gui.NewImage;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import ij.plugin.Duplicator;
import ij.process.ImageProcessor;
import ij.util.Java2;

/**
 * The grid allows to get measurements with the cells of the grid as coordinates.
 * 
 * @author Volker B�cker
 */
public class Grid extends ImageCanvas implements FilenameFilter {
	public static int SORT_BY_COORDINATES = 0;
	public static int SORT_BY_LABEL = 1;
	public static int SORT_BY_GRID_POSITION = 2;
	
	private static final long serialVersionUID = -3691364033021763153L;
	protected SpinnerNumberModel columns;
	protected SpinnerNumberModel rows;
	protected GridOptionsView view;
	protected int[] xPositions;
	protected int[] yPositions;
	protected String[][] names;
	private int draggingXIndex;
	private int draggingYIndex;
	protected Broadcaster broadcaster;
	protected int sortBy;
	protected boolean fillInEmptyPositions = true;
	protected boolean showAllROIs;
	protected Vector<String> gridList;
	protected ArrayList<GridPoint> emptyPositions;
	protected String loadedGridName;
	protected Calibration calibration;
	protected Color gridColor = Color.yellow;
	protected boolean showOutlines = false;
	private ResultsTable tableWithLabels;
	protected static Grid current;
	
	public Grid(ImagePlus imp) {
		super(imp);
		sortBy = Grid.SORT_BY_LABEL;
		broadcaster = new Broadcaster();
		createGridList();
		setupGrid();
		setCurrent(this);
	}

	private void createGridList() {
		gridList = new Vector<String>();
		File folder = new File("." + File.separator + Grid.baseFolder() + File.separator);
		String[] grids = folder.list(this);
		gridList = new Vector<String>(Arrays.asList(grids));
	}

	public void changed(String anAspect) {
		broadcaster.changed(anAspect, "");
	}
	
	public static String baseFolder() {
		return "_grids";
	}

	public void showOptions() {
		this.getView().setVisible(true);
	}

	private Component getView() {
		if (view==null) view = new GridOptionsView(this);
		return view;
	}

	public SpinnerNumberModel getColumns() {
		if (columns==null) columns = new SpinnerNumberModel(23, 1, Integer.MAX_VALUE, 1);
		return columns;
	}


	public SpinnerNumberModel getRows() {
		if (rows==null) rows = new SpinnerNumberModel(8, 1, Integer.MAX_VALUE, 1);
		return rows;
	}
	
	public static void openNew() {
		ImagePlus image = WindowManager.getCurrentImage();
		if (image==null)  image = NewImage.createByteImage("grid", 300, 200, 1, NewImage.FILL_BLACK); 
		(new Grid(image)).showOptions();
	}

	void updateImage(ImagePlus imp) {
		this.imp = imp;
		int width = imp.getWidth();
		int height = imp.getHeight();
		imageWidth = width;
		imageHeight = height;
		srcRect = new Rectangle(0, 0, imageWidth, imageHeight);
		setDrawingSize(imageWidth, (int)imageHeight);
		magnification = 1.0;
		calibration = imp.getCalibration();
		fitToWindow();
	}

    public void paint(Graphics g) {
    	Roi roi = imp.getRoi();
		if (roi!=null || showAllROIs || getOverlay()!=null) {
			if (roi!=null) roi.updatePaste();
			if (!IJ.isMacOSX() && imageWidth!=0) {
				paintDoubleBuffered(g);
				return;
			}
		}
		try {
			if (imageUpdated) {
				imageUpdated = false;
				imp.updateImage();
			}
			Java2.setBilinearInterpolation(g, Prefs.interpolateScaledImages);
			Image img = imp.getImage();
			if (img!=null)
 				g.drawImage(img, 0, 0, (int)(srcRect.width*magnification), (int)(srcRect.height*magnification),
				srcRect.x, srcRect.y, srcRect.x+srcRect.width, srcRect.y+srcRect.height, null);
			if (getOverlay()!=null) drawOverlay(g);
			if (showAllROIs) drawAllROIs(g);
			if (roi != null) roi.draw(g);
			drawGrid(g);
			if (srcRect.width<imageWidth || srcRect.height<imageHeight)
				drawZoomIndicator(g);
			if (IJ.debugMode) showFrameRate(g);
		}
		catch(OutOfMemoryError e) {IJ.outOfMemory("Paint");}
    }
    
	public void drawGrid(Graphics g) {
		Color oldColor = g.getColor();
		g.setColor(gridColor);;
		for (int i=0; i<yPositions.length; i++) {
			int pos = yPositions[i];
			g.drawLine(screenX(0), screenY(pos), screenX(imp.getWidth()-1), screenY(pos));
		}
		for (int i=0; i<xPositions.length; i++) {
			int pos = xPositions[i];
			g.drawLine(screenX(pos), screenY(0), screenX(pos), screenY(imp.getHeight()));
		}
		g.setColor(oldColor);
	}
	
	public void setupGrid() {
		xPositions = new int[getColumns().getNumber().intValue()];
		yPositions = new int[getRows().getNumber().intValue()];
		names = new String[getColumns().getNumber().intValue()][getRows().getNumber().intValue()];
		double xStep =  imp.getWidth() / (getColumns().getNumber().intValue() + 1.0);
		double yStep = imp.getHeight() / (getRows().getNumber().intValue() + 1.0);
		for (int i=0; i<xPositions.length; i++) {
			xPositions[i] = (int)Math.round((i+1)*xStep); 
		}
		for (int i=0; i<yPositions.length; i++) {
			yPositions[i] = (int)Math.round((i+1)*yStep); 
		}
		for (int i=0; i<xPositions.length; i++) {
			for (int j=0; j<yPositions.length; j++) {
				names[i][j] = (i + 1) +", " + (j + 1); 
			}
		}
	}
	
	public void setImage(ImagePlus image) {
		ImageWindow win = image.getWindow();
		if (win!=null) {
			win.setVisible(false);
		}
		if (image.getNSlices()>1) 
			win = new StackWindow(image, this);
		else
			win = new ImageWindow(image, this);
		updateImage(image);
	}
	
	/** Sets the cursor based on the current tool and cursor location. */
	public void setCursor(int sx, int sy, int ox, int oy) {
		super.setCursor(sx, sy, ox, oy);
		if (xGridPositionIndex(ox, oy)!=-1) {
			setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
			return;
		}
		if (yGridPositionIndex(ox, oy)!=-1) {
			setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
			return;
		}
		super.setCursor(sx, sy, ox, oy);
	}
	
	private int xGridPositionIndex(int ox, int oy) {
		int result = -1;
		for (int i=0; i<xPositions.length; i++) {
			if (xPositions[i]>ox-3 && xPositions[i]<ox+3) return i;
		}
		return result;
	}
	
	private int yGridPositionIndex(int ox, int oy) {
		int result = -1;
		for (int i=0; i<yPositions.length; i++) {
			if (yPositions[i]>oy-3 && yPositions[i]<oy+3) return i;
		}
		return result;
	}

	public void mouseMoved(MouseEvent e) {
		super.mouseMoved(e);
		if (ij==null) return;
		int sx = e.getX();
		int sy = e.getY();
		int ox = offScreenX(sx);
		int oy = offScreenY(sy);
		flags = e.getModifiers();
		setCursor(sx, sy, ox, oy);
	}
	
	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		xMouse = offScreenX(x);
		yMouse = offScreenY(y);
		flags = e.getModifiers();
		this.draggingXIndex = xGridPositionIndex(xMouse, yMouse);
		this.draggingYIndex = yGridPositionIndex(xMouse, yMouse);
		super.mousePressed(e);
	}
	
	public void mouseReleased(MouseEvent e) {
		if (draggingXIndex!=-1 || draggingYIndex!=-1) {
			draggingXIndex = -1;
			draggingYIndex = -1;
		}
		else super.mouseReleased(e);
	}
	
	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		xMouse = offScreenX(x);
		yMouse = offScreenY(y);
		if (draggingXIndex==-1 && draggingYIndex==-1) {
			super.mouseDragged(e);
			return;
		}
		if (e.isControlDown()) {
			moveAllColumnsOrRows();
			return;
		}
		moveColumnOrRow();
	}

	private void moveColumnOrRow() {
		int index = draggingXIndex;
		int mouse = xMouse;
		int[] positions = xPositions;
		if (draggingYIndex!=-1){
			index = draggingYIndex;
			mouse = yMouse;
			positions = yPositions;
		}
		int newPos = mouse;
		if (index>0) {
			int leftNeighborPos = positions[index-1];
			if (newPos<=leftNeighborPos) newPos = leftNeighborPos+1;
		}
		if (index<positions.length-1) {
			int rightNeighborPos = positions[index+1];
			if (newPos>=rightNeighborPos) newPos = rightNeighborPos-1;
		}
		positions[index] = newPos;
		imp.repaintWindow();
	}

	private void moveAllColumnsOrRows() {
		int [] positions=null; 
		int delta=0;
		if (draggingXIndex!=-1) {
			positions = xPositions;
			delta = xPositions[draggingXIndex] - xMouse;
		}
		if (draggingYIndex!=-1) {
			positions = yPositions;
			delta = yPositions[draggingYIndex] - yMouse;
		}
		for (int i=0; i<positions.length; i++) {
			positions[i] = positions[i]-delta;
		}
		imp.repaintWindow();
	}

	public ImagePlus getImage() {
		return imp;
	}

	public int[] getXPositions() {
		return xPositions;
	}

	public void setXPositions(int[] positions) {
		xPositions = positions;
	}

	public int[] getYPositions() {
		return yPositions;
	}

	public void setYPositions(int[] positions) {
		yPositions = positions;
	}

	public void saveAs(String path) {
		FileWriter outFile = null;
		PrintWriter out = null;
		 try {
			outFile = new FileWriter(path);
			out = new PrintWriter(outFile);
			out.println(xPositions.length);
			out.println(yPositions.length);
			for (int i=0; i<xPositions.length; i++) {
				out.println(xPositions[i]);
			}
			for (int i=0; i<yPositions.length; i++) {
				out.println(yPositions[i]);
			}
			for (int i=0; i<xPositions.length; i++) {
				for (int j=0; j<yPositions.length; j++) {
					out.println(names[i][j]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out!=null) out.close();
		}
		this.createGridList();
		this.changed("gridlist");
	}

	public void load(String path) {
		 BufferedReader input = null;
		 try {
			input = new BufferedReader( new FileReader(new File(path)) );
			this.columns.setValue(new Integer(Integer.parseInt(input.readLine())));
			this.rows.setValue(new Integer(Integer.parseInt(input.readLine())));
			this.setupGrid();
			for (int i=0; i<xPositions.length; i++) {
				xPositions[i] = Integer.parseInt(input.readLine());
			}
			for (int i=0; i<yPositions.length; i++) {
				yPositions[i] = Integer.parseInt(input.readLine());
			}
			for (int i=0; i<xPositions.length; i++) {
				for (int j=0; j<yPositions.length; j++) {
					names[i][j]=input.readLine();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.changed("grid");
	}
	
	public String[][] getNames() {
		return names;
	}

	public Vector<String> getGridList() {
		return gridList;
	}

	public boolean accept(File folder, String filename) {
		boolean result = false;
		String[] components = filename.split("\\.");
		if (components.length<2) return false;
		if (components[1].toLowerCase().equals("grid")) result = true;
		return result;
	}

	public Broadcaster getBroadcaster() {
		if (broadcaster==null) broadcaster = new Broadcaster();
		return broadcaster;
	}

	public void loadGrid(String gridName) {
		loadedGridName = gridName;
		String filename = "." + File.separator + Grid.baseFolder() + File.separator + gridName;
		this.load(filename);
	}

	public void setNames(String[][] names) {
		this.names = names;
	}

	public ResultsTable applyGridTo(ResultsTable table) {
		emptyPositions = new ArrayList<GridPoint>();
		GridPoint[] gridPoints = getGridPoints();
		for (int i=0;i<gridPoints.length;i++) {
			if (!names[gridPoints[i].i][gridPoints[i].j].equals("")) emptyPositions.add(gridPoints[i]);
		}
		ResultsTable newTable = (ResultsTable) table.clone();
		newTable.addLabel("Cell", "");
		for (int i=0; i<table.getCounter(); i++) {
			double xCoord = table.getValue("X", i);
			double yCoord = table.getValue("Y", i);
			GridPoint closestGridPoint = getClosestGridPoint(gridPoints, new Point2D.Double(xCoord, yCoord)); 
			emptyPositions.remove(closestGridPoint);
			newTable.setLabel(names[closestGridPoint.i][closestGridPoint.j], i);
		}
		if (newTable.getCounter()>0 && isFillInEmptyPositions()) newTable = fillInEmptyPositions(newTable);
		if (sortBy==Grid.SORT_BY_GRID_POSITION) newTable = this.sortByGridPosition(newTable);
		if (sortBy==Grid.SORT_BY_LABEL) newTable = this.sortByLabel(newTable);
		tableWithLabels = newTable;
		return newTable;
	}
	
	private ResultsTable sortByLabel(ResultsTable table) {
		String[] elements = new String[table.getCounter()];
		for (int i=0; i<table.getCounter(); i++) {
			elements[i] = table.getLabel(i) + "`"  + i;
		}
		Arrays.sort(elements, new LabelComparator());
		ResultsTable newTable = new ResultsTable();
		for (int i=0; i<elements.length; i++) {
			int index = Integer.parseInt(elements[i].split("`")[1]);
			newTable.incrementCounter();
			newTable.addLabel("Cell", table.getLabel(index));
			for (int j=0; j<=table.getLastColumn(); j++) {
				newTable.setValue(table.getColumnHeading(j), newTable.getCounter()-1, table.getValueAsDouble(j, index));
			}
		}
		return newTable;
	}
	
	private ResultsTable sortByGridPosition(ResultsTable table) {
		String[] elements = new String[table.getCounter()];
		for (int i=0; i<table.getCounter(); i++) {
			String label = table.getLabel(i);
			Point2D pos = this.getGridPositionOfLabel(label);
			elements[i] = pos.getX() + "x" + pos.getY() + "x" + i;
		}
		Arrays.sort(elements, new GridPositionComparator());
		ResultsTable newTable = new ResultsTable();
		for (int i=0; i<elements.length; i++) {
			int index = Integer.parseInt(elements[i].split("x")[2]);
			newTable.incrementCounter();
			newTable.addLabel("Cell", table.getLabel(index));
			for (int j=0; j<=table.getLastColumn(); j++) {
				newTable.setValue(table.getColumnHeading(j), newTable.getCounter()-1, table.getValueAsDouble(j, index));
			}
		}
		return newTable;
	}

	private Point2D getGridPositionOfLabel(String label) {
		Point2D pos = new Point2D.Float(-1,-1);
		for (int i=0; i<this.xPositions.length; i++) {
			for (int j=0; j<this.yPositions.length; j++) {
				if (names[i][j].equals(label)) {
					pos.setLocation(i, j);
				}
			}
		}
		return pos;
	}
	
	private ResultsTable fillInEmptyPositions(ResultsTable table) {
		ResultsTable newTable = new ResultsTable();
		int emptyPositionIndex = 0;
		for (int i=0; i<table.getCounter(); i++) {
			newTable.incrementCounter();
			double xCoord = table.getValue("X", i);
			double yCoord = table.getValue("Y", i);
			if (emptyPositionIndex<emptyPositions.size()){
				GridPoint emptyPosition = emptyPositions.get(emptyPositionIndex);
				boolean insertEmpty = false;
				if (emptyPosition.point.getY()<yCoord) insertEmpty=true;
				if (emptyPosition.point.getY()==yCoord && emptyPosition.point.getX()<xCoord) insertEmpty=true;
				if (insertEmpty) {
					newTable.addLabel("Cell", names[emptyPosition.i][emptyPosition.j]);
					emptyPositionIndex++;
					newTable.incrementCounter();
				}
			}
			for (int j=0; j<=table.getLastColumn(); j++) {
				newTable.setValue(table.getColumnHeading(j), newTable.getCounter()-1, table.getValueAsDouble(j, i));
				newTable.setLabel(table.getLabel(i), newTable.getCounter()-1);
			}
		}
		while (emptyPositionIndex<emptyPositions.size()) {
			newTable.incrementCounter();
			GridPoint emptyPosition = emptyPositions.get(emptyPositionIndex);
			newTable.addLabel(names[emptyPosition.i][emptyPosition.j]);
			emptyPositionIndex++;
		}
		return newTable;
	}

	private GridPoint getClosestGridPoint(GridPoint[] gridPoints, Point2D aPoint) {
		double minDistSoFar = Float.MAX_VALUE;
		int bestIndex = -1;
		for (int i=0; i<gridPoints.length; i++) {
			GridPoint current = gridPoints[i];
			double currentDistance = current.distance(aPoint);
			if (this.getNames()[current.i][current.j].equals("")) currentDistance = Float.MAX_VALUE;
			if (currentDistance<minDistSoFar) {
				minDistSoFar = currentDistance;
				bestIndex = i;
			}
		}
		return gridPoints[bestIndex];
	}

	private GridPoint[] getGridPoints() {
		calibration = imp.getCalibration();
		GridPoint[] result = new GridPoint[xPositions.length*yPositions.length];
		for (int i=0; i<xPositions.length; i++) {
			for (int j=0; j<yPositions.length; j++) {
				result[j*xPositions.length+i]=new GridPoint(calibration.getX(xPositions[i]), calibration.getY(yPositions[j]), i, j);
			}
		}
		return result;
	}

	public boolean isFillInEmptyPositions() {
		return fillInEmptyPositions;
	}

	public void setFillInEmptyPositions(boolean fillInEmptyPositions) {
		this.fillInEmptyPositions = fillInEmptyPositions;
	}

	public int getSortBy() {
		return sortBy;
	}

	public void setSortBy(int sortBy) {
		this.sortBy = sortBy;
	}

	public String getLoadedGridName() {
		return loadedGridName;
	}

	public void setLoadedGridName(String name) {
		loadedGridName = name;
	}

	public Color getGridColor() {
		return gridColor;
	}

	public void setGridColor(Color gridColor) {
		this.gridColor = gridColor;
		this.repaint();
	}

	public void deleteGrids(Object[] selectedValues) {
		for (int i=0; i<selectedValues.length;i++) {
			String name = (String) selectedValues[i];
			String filename = "." + File.separator + Grid.baseFolder() + File.separator + name;
			(new File(filename)).delete();
		}
		this.createGridList();
		this.changed("gridlist");
	}

	public boolean isShowOutlines() {
		return showOutlines; 
	}

	public void setShowOutlines(boolean showOutlines) {
		this.showOutlines = showOutlines;
	}

	public ImagePlus getOutlinesImage() {
		ImagePlus outlines = null;
		Duplicator copyAction = new Duplicator();
		outlines = copyAction.run(imp);
		outlines.setTitle(imp.getTitle() + " outlines");
		ImageProcessor ip = outlines.getProcessor();
		ip.setColorModel(getCustomLut());
		ip.setFont(new Font("SansSerif", Font.PLAIN, 9));
		ip.setValue(1.0);
		for (int i=0; i<tableWithLabels.getCounter(); i++) {
			String s = tableWithLabels.getLabel(i);
			double x = tableWithLabels.getValue("X", i);
			double y = tableWithLabels.getValue("Y", i);
			ip.moveTo((int)x,(int)y);
			ip.drawString(s);
		}
		return outlines;
	}
	
	protected IndexColorModel getCustomLut() {
		IndexColorModel cm = (IndexColorModel)LookUpTable.createGrayscaleColorModel(false);
		byte[] reds = new byte[256];
		byte[] greens = new byte[256];
		byte[] blues = new byte[256];
		cm.getReds(reds);
		cm.getGreens(greens);
		cm.getBlues(blues);
		reds[1] =(byte) 255;
		greens[1] = (byte)0;
		blues[1] = (byte)0;
		IndexColorModel customLut = new IndexColorModel(8, 256, reds, greens, blues);
		return customLut;
	}

	public static Grid getCurrent() {
		return current;
	}

	public static void setCurrent(Grid current) {
		Grid.current = current;
	}

	public String getResultsTableTitle() {
		return "grid measurements";
	}
}
