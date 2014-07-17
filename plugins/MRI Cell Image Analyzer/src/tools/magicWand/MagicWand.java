package tools.magicWand;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.Roi;
import ij.gui.Toolbar;
import ij.measure.ResultsTable;
import ij.text.TextWindow;
import imagejProxies.MRIInterpreter;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import utils.ImageDuplicator;

import broadcaster.WindowManagerBroadcaster;

public class MagicWand extends Observable implements Observer, MouseListener, KeyListener, WindowListener {
	protected static MagicWand instance;
	protected ImagePlus image;
	protected ImagePlus backstore;
	protected MagicWandView view;
	protected boolean isActive = false; 
	protected WindowManagerBroadcaster wmObserver;
	protected MagicWandBackstoreImageCreator backstoreImageCreator; 
	protected Vector<MagicWandBackstoreImageCreator> backstoreCreationMethods;
	protected int currentSlice;
	protected ImagePlus lastImage;
	protected int lastSlice;
	protected int y;
	protected int x;
	
	public MagicWand() {
		super();
		this.setupBackstoreCreationMethods();
		wmObserver = WindowManagerBroadcaster.getInstance();
		wmObserver.addObserver(this);
		ImagePlus anImage = WindowManager.getCurrentImage();
		this.setImage(anImage);
	}
	
	protected void setupBackstoreCreationMethods() {
		backstoreCreationMethods = new Vector<MagicWandBackstoreImageCreator>();
		backstoreCreationMethods.add(new CalculateMorphologicDams(this));
		backstoreCreationMethods.add(new Watershed(this));
		backstoreImageCreator = backstoreCreationMethods.firstElement();
	}

	public Vector<String> getBackstoreCreationMethodNames() {
		Vector<String> names = new Vector<String>();
		Iterator<MagicWandBackstoreImageCreator> it = backstoreCreationMethods.iterator();
		while(it.hasNext()) {
			MagicWandBackstoreImageCreator method = it.next();
			names.add(method.name());
		}
		return names;
	}
	
	public void show() {
		this.getView().setVisible(true);
	}
	
	static public MagicWand getInstance() {
		if (instance==null) instance=new MagicWand();
		return instance;
	}
	
	public MagicWandView getView() {
		if (view==null) view = new MagicWandView(this);
		return view;
	}
	protected void changed(String string) {
		this.setChanged();
		this.notifyObservers(string);
		this.clearChanged();
	}

	public ImagePlus getBackstore() {
		if (this.backstore==null) this.createBackstore();
		return backstore;
	}
	
	protected void createBackstore() {
		wmObserver.deleteObserver(this);
		this.changed("progress");
		this.getImage().killRoi();
		Thread thread = new Thread(this.getBackstoreImageCreator());
		ImagePlus image = ImageDuplicator.copyCurrentSlice(this.getImage(), "magic wand backstore image");
		WindowManager.setTempCurrentImage(thread, image);
		this.backstore = image;
		backstore.setImage(backstore.getProcessor().createImage());
		
		thread.start();
	}

	public void setBackstore(ImagePlus backstore) {
		this.backstore = backstore;
	}
	
	public ImagePlus getImage() {
		return image;
	}
	
	public void setImage(ImagePlus anImage) {
		if (anImage==this.getImage()) return;
		if (image!=null && image.getWindow() != null) {
			image.getWindow().getCanvas().removeMouseListener(this);
			image.getWindow().getCanvas().removeKeyListener(this);
		}
		this.image = anImage;
		if (image!=null && image.getWindow() != null) {
			image.getWindow().getCanvas().addMouseListener(this);
			image.getWindow().getCanvas().addKeyListener(this);
		}
		this.setBackstore(null);
		this.changed("image");
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getImageName() {
		String result = "none";
		if (this.getImage()!=null) result = this.getImage().getTitle();
		return result;
	}

	public void update(Observable sender, Object aspect) {
		if (aspect.equals("currentImage")) this.handleCurrentImageChanged();
		if (aspect.equals(":stop calculating")) this.handleBackgroundImageCalculated();
	}

	protected void handleBackgroundImageCalculated() {
		this.backstore = WindowManager.getTempCurrentImage();
		this.backstore.setTitle("magic wand backstore image");
		this.changed("stop progress");
		wmObserver.addObserver(this);
		this.doMagicWandUsingBackgroundImage(this.getImage(), this.x, this.y);
	}

	protected void handleCurrentImageChanged() {
		ImagePlus theImage = WindowManager.getCurrentImage();
		if (theImage==null) return;
		if (theImage.getTitle().contains("magic wand backstore image")) return;
		this.setImage(WindowManager.getCurrentImage());
	}

	public void mouseClicked(MouseEvent e) {
		ImageCanvas canvas = (ImageCanvas) this.getImage().getWindow().getCanvas();
		if (canvas.offScreenX(e.getX())<0 || canvas.offScreenY(e.getY())<0) return;
		if (Toolbar.getToolId()!=Toolbar.WAND) return;
		if (!this.isActive) return;
		this.doMagicWandAt(this.getImage(), canvas.offScreenX(e.getX()), canvas.offScreenY(e.getY()));
	}

	public void doMagicWandAt(ImagePlus anImage, int x, int y) {
		currentSlice = anImage.getCurrentSlice()-1;
		if ((lastImage==anImage) && (lastSlice!=currentSlice)) this.setBackstore(null);
		if (this.backstore==null) {
			this.createBackstore();
			this.x = x;
			this.y = y;
			return;
		}
		doMagicWandUsingBackgroundImage(anImage, x, y);
	}

	private void doMagicWandUsingBackgroundImage(ImagePlus anImage, int x, int y) {
		ImagePlus backstoreImage = this.getBackstore();
		WindowManager.setTempCurrentImage(backstoreImage);
		if (!IJ.shiftKeyDown()) backstoreImage.killRoi(); // test 
		IJ.doWand(x,y);
		Roi aRoi = (Roi) backstoreImage.getRoi().clone(); // test
		WindowManager.setTempCurrentImage(null);
		anImage.setRoi(aRoi);
		WindowManager.setTempCurrentImage(this.getImage());
		lastSlice = currentSlice;
		lastImage = anImage;
	}

	public void mousePressed(MouseEvent arg0) {
		// do nothing		
	}

	public void mouseReleased(MouseEvent arg0) {
		this.getImage().setRoi(this.getImage().getRoi());
	}

	public void mouseEntered(MouseEvent arg0) {
		// do nothing
	}

	public void mouseExited(MouseEvent arg0) {
		// do nothing
	}

	public void keyTyped(KeyEvent arg0) {
		// do nothing
	}

	public void keyPressed(KeyEvent arg0) {
		// do nothing
	}

	public void keyReleased(KeyEvent arg0) {
		// do nothing
	}

	public void windowOpened(WindowEvent arg0) {
		// do nothing
	}

	public void windowClosing(WindowEvent arg0) {
		this.setImage(null);
		this.changed("image");
	}

	public void windowClosed(WindowEvent arg0) {
		// do nothing
	}

	public void windowIconified(WindowEvent arg0) {
		// do nothing
	}

	public void windowDeiconified(WindowEvent arg0) {
		// do nothing
	}

	public void windowActivated(WindowEvent arg0) {
		this.setImage(WindowManager.getCurrentImage());
	}

	public void windowDeactivated(WindowEvent arg0) {
		// do nothing
	}

	public MagicWandBackstoreImageCreator getBackstoreImageCreator() {
		return backstoreImageCreator;
	}

	public void setBackstoreImageCreator(MagicWandBackstoreImageCreator backstoreImageCreator) {
		this.backstoreImageCreator = backstoreImageCreator;
	}

	public Vector<MagicWandBackstoreImageCreator> getBackstoreCreationMethods() {
		return backstoreCreationMethods;
	}

	public void closeSelection() {
		Roi roi = image.getRoi();
		if (roi==null) return;
		Point point = this.getPointInsidePolygon(roi.getPolygon());
		MRIInterpreter interpreter = new MRIInterpreter();
		interpreter.setBatchMode(true);
		IJ.run("Create Mask");
		IJ.run("Dilate");
		IJ.run("Erode");
		IJ.doWand(point.x, point.y);
		Roi newRoi = WindowManager.getTempCurrentImage().getRoi();
		interpreter.setBatchMode(false);
		image.setRoi(newRoi);
		WindowManager.setTempCurrentImage(null);
	}

	public Point getPointInsidePolygon(Polygon polygon) {
		int x = polygon.xpoints[0];
		int y = polygon.ypoints[0];
		Rectangle bounds = polygon.getBounds();
		int x0 = bounds.x + bounds.width / 2;
		int y0 = bounds.y + bounds.height / 2;
		if (polygon.contains(x0,y0)) return new Point(x0, y0);
		for(int i=-1; i<1; i++) {
			for(int j=-1; j<1; j++) {
				x0 = x + i;
				y0 = y + j;
				if (polygon.contains(x0,y0)) break;
			}
		}
		return new Point(x0, y0);
	}

	public void split() {
		Roi roi = image.getRoi();
		if (roi==null) return;
		MRIInterpreter interpreter = new MRIInterpreter();
		interpreter.setBatchMode(true);
		IJ.run("Create Mask");
		int counter = 0;
		int lastNrOfObjects = 0;
		int nrOfObjects = 0;
		ImagePlus lastMask = null;
		while (nrOfObjects>=lastNrOfObjects) {
			lastMask = ImageDuplicator.copyImage(WindowManager.getTempCurrentImage(), "lastMask");
			IJ.run("Erode");
			counter++;
			IJ.run("Analyze Particles...", "minimum=1 maximum=999999 bins=256 show=Nothing display clear");
			lastNrOfObjects = nrOfObjects;
			nrOfObjects = ResultsTable.getResultsTable().getCounter();
		}
		WindowManager.setTempCurrentImage(lastMask);
		for (int i=0; i<counter; i++) {
			IJ.run("Dilate");
		}
		IJ.run("Analyze Particles...", "minimum=1 maximum=999999 bins=256 show=Nothing clear record");
		ResultsTable resultsTable = ResultsTable.getResultsTable();
		ImagePlus mask =  WindowManager.getTempCurrentImage();
		for (int i=0; i<resultsTable.getCounter(); i++) {
			int x = (int)Math.round(resultsTable.getValueAsDouble(resultsTable.getColumnIndex("XStart"), i));
			int y = (int)Math.round(resultsTable.getValueAsDouble(resultsTable.getColumnIndex("YStart"), i));
			IJ.doWand(x,y);
			Roi aRoi = mask.getRoi();
			backstore.setRoi(aRoi);
			WindowManager.setTempCurrentImage(backstore);
			IJ.run("Draw");
			WindowManager.setTempCurrentImage(mask);
		}
		int x = (int)Math.round(resultsTable.getValueAsDouble(resultsTable.getColumnIndex("XStart"), 0));
		int y = (int)Math.round(resultsTable.getValueAsDouble(resultsTable.getColumnIndex("YStart"), 0));
		WindowManager.setTempCurrentImage(backstore);
		IJ.doWand(x,y);
		Roi aRoi = backstore.getRoi();
		image.setRoi(aRoi);
		WindowManager.setTempCurrentImage(image);
		interpreter.setBatchMode(false);
		TextWindow textWindow = ((TextWindow)IJ.getTextPanel().getParent());
		textWindow.close();
	}
}
