package tools.multipleImageWindow;
import ij.IJ;
import ij.ImageListener;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import java.awt.Toolkit;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JLabel;
import java.awt.event.KeyEvent;

public class FourImageWindowView extends JFrame implements Observer, MouseListener, MouseMotionListener, ImageListener, WindowListener, WindowStateListener {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JSplitPane jSplitPane = null;

	private JSplitPane jSplitPane1 = null;

	private JSplitPane jSplitPane2 = null;

	private JScrollPane image1ScrollPane = null;

	private JScrollPane image3ScrollPane = null;

	private JScrollPane image2ScrollPane = null;

	private JScrollPane image4ScrollPane = null;

	private FourImageWindow model;

	private ImageCanvas upperLeftImageCanvas;
	
	private ImageCanvas upperRightImageCanvas;
	
	private ImageCanvas lowerLeftImageCanvas;
	
	private ImageCanvas lowerRightImageCanvas;

	protected boolean jSplitPaneMoved;

	private boolean isValidating;  //  @jve:decl-index=0:

	private JMenuBar mainMenuBar = null;

	private JToolBar toolBar = null;

	private JMenu fileMenu = null;

	private JMenuItem openImageMenuItem = null;

	private JMenuItem getCurrentImageMenuItem = null;

	private JButton openImageButton = null;

	private JButton getCurrentImageButton = null;

	private JMenu OptionsMenu = null;

	private JRadioButtonMenuItem lockUnlockRadioButtonMenuItem = null;

	private JMenuItem setImageListMenuItem = null;

	private JButton setImageListButton = null;

	private JButton gotoFirstImageButton = null;

	private JButton gotoPreviousImageButton = null;

	private JButton gotoNextImageButton = null;

	private JButton gotoLastImageButton = null;

	private JLabel spaceLabel = null;

	private JButton reloadCurrentImageButton = null;

	private JMenu goMenu = null;

	private JMenuItem reloadCurrentImageMenuItem = null;

	private JMenuItem gotoFirstImageMenuItem = null;

	private JMenuItem gotoPreviousImageMenuItem = null;

	private JMenuItem gotoNextImageMenuItem = null;

	private JMenuItem gotoLastImageMenuItem = null;

	private JMenu applicationsMenu = null;

	private JMenuItem FluoDistributionInCellMenuItem = null;

	/**
	 * This is the default constructor
	 */
	public FourImageWindowView() {
		super();
		initialize();
	}

	public FourImageWindowView(FourImageWindow model) {
		super();
		this.model=model;
		model.getBroadcaster().addObserver(this);
		this.addWindowStateListener(this);
		this.addWindowListener(this);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		javax.swing.JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		this.setSize(600, 600);
		this.setJMenuBar(getMainMenuBar());
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/icon.gif")));
		this.setContentPane(getJContentPane());
		this.setTitle("MRI Multiple Images Window");
		ImagePlus.addImageListener(this);
		activateImageOfComonent(model.getActiveComponent());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJSplitPane(), BorderLayout.CENTER);
			jContentPane.add(getToolBar(), BorderLayout.NORTH);
		}
		return jContentPane;
	}

	public void setVerticalDividerPosition(int pos) {
		getJSplitPane().setDividerLocation(pos);
	}
	
	public void setHorizontalDividerPosition(int pos) {
		getJSplitPane1().setDividerLocation(pos);
		getJSplitPane2().setDividerLocation(pos);
	}
	/**
	 * This method initializes jSplitPane	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setLeftComponent(getJSplitPane1());
			jSplitPane.setRightComponent(getJSplitPane2());
			jSplitPane.setDividerLocation(this.getWidth()/3);
			jSplitPane.addPropertyChangeListener("lastDividerLocation",
					new java.beans.PropertyChangeListener() {
						public void propertyChange(java.beans.PropertyChangeEvent e) {
							if (model.getSlidersLocked()) {
								getJSplitPane1().setDividerLocation(getJSplitPane().getDividerLocation());
								getJSplitPane2().setDividerLocation(getJSplitPane().getDividerLocation());
							}
							resizeImages();
						}
					});
		}
		return jSplitPane;
	}

	/**
	 * This method initializes jSplitPane1	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getJSplitPane1() {
		if (jSplitPane1 == null) {
			jSplitPane1 = new JSplitPane();
			jSplitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
			jSplitPane1.setBottomComponent(getImage3ScrollPane());
			jSplitPane1.setTopComponent(getImage1ScrollPane());
			jSplitPane1.setDividerLocation(this.getHeight()/3);
			jSplitPane1.addPropertyChangeListener("lastDividerLocation",
					new java.beans.PropertyChangeListener() {
						public void propertyChange(java.beans.PropertyChangeEvent e) {
							getJSplitPane2().setDividerLocation(getJSplitPane1().getDividerLocation());
							if (model.getSlidersLocked()) {
								getJSplitPane().setDividerLocation(getJSplitPane1().getDividerLocation());
							}
							resizeImages();
						}
					});
		}
		return jSplitPane1;
	}

	/**
	 * This method initializes jSplitPane2	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getJSplitPane2() {
		if (jSplitPane2 == null) {
			jSplitPane2 = new JSplitPane();
			jSplitPane2.setOrientation(JSplitPane.VERTICAL_SPLIT);
			jSplitPane2.setBottomComponent(getImage4ScrollPane());
			jSplitPane2.setTopComponent(getImage2ScrollPane());
			jSplitPane2.setDividerLocation(this.getHeight()/3);
			jSplitPane2.addPropertyChangeListener("lastDividerLocation",
					new java.beans.PropertyChangeListener() {
						public void propertyChange(java.beans.PropertyChangeEvent e) {
							getJSplitPane1().setDividerLocation(getJSplitPane2().getDividerLocation());
							resizeImages();
						}
					});
		}
		return jSplitPane2;
	}

	/**
	 * This method initializes image1ScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getImage1ScrollPane() {
		if (image1ScrollPane == null) {
			image1ScrollPane = new JScrollPane(this.getUpperLeftImageCanvas());
		}
		return image1ScrollPane;
	}

	/**
	 * This method initializes image3ScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getImage3ScrollPane() {
		if (image3ScrollPane == null) {
			image3ScrollPane = new JScrollPane(this.getLowerLeftImageCanvas());
		}
		return image3ScrollPane;
	}

	/**
	 * This method initializes image2ScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getImage2ScrollPane() {
		if (image2ScrollPane == null) {
			image2ScrollPane = new JScrollPane(this.getUpperRightImageCanvas());
		}
		return image2ScrollPane;
	}

	/**
	 * This method initializes image4ScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getImage4ScrollPane() {
		if (image4ScrollPane == null) {
			image4ScrollPane = new JScrollPane(this.getLowerRightImageCanvas());
		}
		return image4ScrollPane;
	}
	
	protected ImageCanvas getUpperLeftImageCanvas() {
		if (upperLeftImageCanvas == null) {
			upperLeftImageCanvas = new ImageCanvasPlus(new ImagePlus(), this, FourImageWindow.UPPERLEFT);
			upperLeftImageCanvas.addMouseListener(this);
			upperLeftImageCanvas.addMouseMotionListener(this);
		}
		return upperLeftImageCanvas;
	}

	protected ImageCanvas getUpperRightImageCanvas() {
		if (upperRightImageCanvas == null) {
			upperRightImageCanvas = new ImageCanvasPlus(new ImagePlus(), this, FourImageWindow.UPPERRIGHT);
			upperRightImageCanvas.addMouseListener(this);
			upperRightImageCanvas.addMouseMotionListener(this);
		}
		return upperRightImageCanvas;
	}
	
	protected ImageCanvas getLowerLeftImageCanvas() {
		if (lowerLeftImageCanvas == null) {
			lowerLeftImageCanvas = new ImageCanvasPlus(new ImagePlus(), this, FourImageWindow.LOWERLEFT);
			lowerLeftImageCanvas.addMouseListener(this);
			lowerLeftImageCanvas.addMouseMotionListener(this);
		}
		return lowerLeftImageCanvas;
	}
	
	protected ImageCanvas getLowerRightImageCanvas() {
		if (lowerRightImageCanvas == null) {
			lowerRightImageCanvas = new ImageCanvasPlus(new ImagePlus(), this, FourImageWindow.LOWERRIGHT);
			lowerRightImageCanvas.addMouseListener(this);
			lowerRightImageCanvas.addMouseMotionListener(this);
		}
		return lowerRightImageCanvas;
	}

	public void update(Observable sender, Object aspect) {
		String[] components = ((String)aspect).split(":");
		String realSender = components[0];
		String realAspect = components[1];
		if (realSender.equals("FourImageWindow")) this.handleFourImageWindowChanged(realAspect);
		
	}

	protected void handleFourImageWindowChanged(Object aspect) {
		if (aspect.equals("upperLeftImage")) {
			ImagePlus image = model.getUpperLeftImage();
			if (image==null) return;
			ImageCanvas oldCanvas = upperLeftImageCanvas;
			upperLeftImageCanvas = new ImageCanvasPlus(image, this, FourImageWindow.UPPERLEFT);
			handleImageChanged(image, getImage1ScrollPane(), oldCanvas, upperLeftImageCanvas);
		}
		if (aspect.equals("upperRightImage")) {
			ImagePlus image = model.getUpperRightImage();
			if (image==null) return;
			ImageCanvas oldCanvas = upperRightImageCanvas;
			upperRightImageCanvas = new ImageCanvasPlus(image, this, FourImageWindow.UPPERRIGHT);
			handleImageChanged(image, getImage2ScrollPane(), oldCanvas, upperRightImageCanvas);
		}
		if (aspect.equals("lowerLeftImage")) {
			ImagePlus image = model.getLowerLeftImage();
			if (image==null) return;
			ImageCanvas oldCanvas = lowerLeftImageCanvas;
			lowerLeftImageCanvas = new ImageCanvasPlus(image, this, FourImageWindow.LOWERLEFT);
			handleImageChanged(image, getImage3ScrollPane(), oldCanvas, lowerLeftImageCanvas);
		}
		if (aspect.equals("lowerRightImage")) {
			ImagePlus image = model.getLowerRightImage();
			if (image==null) return;
			ImageCanvas oldCanvas = lowerRightImageCanvas;
			lowerRightImageCanvas = new ImageCanvasPlus(image, this, FourImageWindow.LOWERRIGHT);
			handleImageChanged(image, getImage4ScrollPane(), oldCanvas, lowerRightImageCanvas);
		}
		if (aspect.equals("activeComponent")) {
			this.activateImageOfComonent(model.activeComponent);
		}
		activateImageOfComonent(model.getActiveComponent());
		this.recalculateTitle();
	}

	private void recalculateTitle() {
		this.setTitle(model.getTitle());
	}

	protected void handleImageChanged(ImagePlus image, JScrollPane pane, ImageCanvas oldCanvas, ImageCanvas newCanvas) {
		oldCanvas.removeMouseListener(this);
		oldCanvas.removeMouseMotionListener(this);
		newCanvas.addMouseListener(this);
		newCanvas.addMouseMotionListener(this);
		pane.setViewportView(newCanvas);
		resizeImages();
		validate();
	}
	
	public int getContentWidth() {
		return getJContentPane().getWidth();
	}
	
	public int getContentHeight() {
		return getJContentPane().getHeight();
	}
	
	private void resizeImages() {
		ImagePlus upperLeftImage = model.getUpperLeftImage();
		int widthWin = getContentWidth();
		int heightWin = getContentHeight();
		int splitXPosition = getJSplitPane().getDividerLocation();
		int splitYPosition = getJSplitPane1().getDividerLocation();
		if (upperLeftImage!=null) {
			resizeImage(upperLeftImage, getUpperLeftImageCanvas(), splitXPosition, splitYPosition);
		}
		ImagePlus upperRightImage = model.getUpperRightImage();
		if (upperRightImage!=null) {
			resizeImage(upperRightImage, getUpperRightImageCanvas(), widthWin-splitXPosition, splitYPosition);
		}
		ImagePlus lowerLeftImage = model.getLowerLeftImage();
		if (lowerLeftImage!=null) {
			resizeImage(lowerLeftImage, getLowerLeftImageCanvas(), splitXPosition, heightWin-splitYPosition);
		}
		ImagePlus lowerRightImage = model.getLowerRightImage();
		if (lowerRightImage!=null) {
			resizeImage(lowerRightImage, getLowerRightImageCanvas(), widthWin-splitXPosition, heightWin-splitYPosition);
		}
		validate();
	}

	private void resizeImage(ImagePlus anImage, ImageCanvas canvas, int width, int height) {
		if (anImage==null) return;
		ImageWindow win = anImage.getWindow();
		if (win==null) return;
		canvas.setDrawingSize(width, height);
		canvas.setMagnification(Math.min(width / (anImage.getWidth()*1.0d),height / (anImage.getHeight()*1.0d)));
		anImage.getWindow().getCanvas().setDrawingSize(width, height);
		anImage.getWindow().getCanvas().setMagnification(Math.min(width / (anImage.getWidth()*1.0d),height / (anImage.getHeight()*1.0d)));
	}

	public void validate() {
		if (isValidating) return;
		isValidating =true;
		super.validate();
		isValidating =false;
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	private int getPositionOfComponent(Component component) {
		int result = -1;
		if (component==this.getUpperLeftImageCanvas()) {
			result = FourImageWindow.UPPERLEFT;
		}
		if (component==this.getUpperRightImageCanvas()) { 
			result = FourImageWindow.UPPERRIGHT;
		}
		if (component==this.getLowerLeftImageCanvas()) { 
			result = FourImageWindow.LOWERLEFT;
		}
		if (component==this.getLowerRightImageCanvas()) { 
			result = FourImageWindow.LOWERRIGHT;
		}
		return result;
	}
	
	private ImageCanvas getCanvasOfPosition(int position) {
		ImageCanvas result = null;
		if (position==FourImageWindow.UPPERLEFT) {
			result = this.getUpperLeftImageCanvas();
		}
		if (position==FourImageWindow.UPPERRIGHT) { 
			result = this.getUpperRightImageCanvas();
		}
		if (position==FourImageWindow.LOWERLEFT) { 
			result = this.getLowerLeftImageCanvas();
		}
		if (position==FourImageWindow.LOWERRIGHT) { 
			result = this.getLowerRightImageCanvas();
		}
		return result;
	}

	private void activateImageOfComonent(int position) {
		ImagePlus image = model.images[position];
		if (image!=null) WindowManager.setCurrentWindow(image.getWindow());
		getUpperLeftImageCanvas().setBackground(null);
		getUpperRightImageCanvas().setBackground(null);
		getLowerLeftImageCanvas().setBackground(null);
		getLowerRightImageCanvas().setBackground(null);
		getCanvasOfPosition(position).setBackground(Color.LIGHT_GRAY);
	}
	
	public void mouseClicked(MouseEvent arg0) {
		Component component = arg0.getComponent();
		component.repaint();
	}
	
	public void mousePressed(MouseEvent arg0) {
		Component component = arg0.getComponent();
		component.repaint();
	}

	public void mouseReleased(MouseEvent arg0) {
		Component component = arg0.getComponent();
		component.repaint();
		int newActivePosition = this.getPositionOfComponent(component);
		if (newActivePosition!=model.getActiveComponent()) model.setActiveComponent(newActivePosition);
	}

	public void mouseDragged(MouseEvent arg0) {
		Component component = arg0.getComponent();
		component.repaint();
	}

	public void mouseMoved(MouseEvent arg0) {
	}

	public void imageClosed(ImagePlus imp) {
	}

	public void imageOpened(ImagePlus imp) {
	}

	public void imageUpdated(ImagePlus imp) {
		System.out.println("image updated");
		ImageCanvas canvas = null;
		if (imp==model.getUpperLeftImage()) canvas = getUpperLeftImageCanvas();
		if (imp==model.getUpperRightImage()) canvas = getUpperRightImageCanvas();
		if (imp==model.getLowerLeftImage())	canvas = getLowerLeftImageCanvas();
		if (imp==model.getLowerRightImage()) canvas = getLowerRightImageCanvas();
		if (canvas!=null) {
			canvas.setImageUpdated();
			canvas.repaint();
		}
	}

	/**
	 * This method initializes mainMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuBar getMainMenuBar() {
		if (mainMenuBar == null) {
			mainMenuBar = new JMenuBar();
			mainMenuBar.add(getFileMenu());
			mainMenuBar.add(getGoMenu());
			mainMenuBar.add(getApplicationsMenu());
			mainMenuBar.add(getOptionsMenu());
		}
		return mainMenuBar;
	}

	/**
	 * This method initializes toolBar	
	 * 	
	 * @return javax.swing.JToolBar	
	 */
	private JToolBar getToolBar() {
		if (toolBar == null) {
			spaceLabel = new JLabel();
			spaceLabel.setText("        ");
			toolBar = new JToolBar();
			toolBar.add(getGetCurrentImageButton());
			toolBar.add(getOpenImageButton());
			toolBar.add(getSetImageListButton());
			toolBar.add(spaceLabel);
			toolBar.add(getReloadCurrentImageButton());
			toolBar.add(getGotoFirstImageButton());
			toolBar.add(getGotoPreviousImageButton());
			toolBar.add(getGotoNextImageButton());
			toolBar.add(getGotoLastImageButton());
		}
		return toolBar;
	}

	/**
	 * This method initializes FileMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("File");
			fileMenu.add(getGetCurrentImageMenuItem());
			fileMenu.add(getOpenImageMenuItem());
			fileMenu.add(getSetImageListMenuItem());
		}
		return fileMenu;
	}

	/**
	 * This method initializes openImageMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getOpenImageMenuItem() {
		if (openImageMenuItem == null) {
			openImageMenuItem = new JMenuItem();
			openImageMenuItem.setText("Open Image...");
			openImageMenuItem.setIcon(new ImageIcon(getClass().getResource("/resources/images/document-open.png")));
			openImageMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getOpenImageButton().doClick();
				}
			});
		}
		return openImageMenuItem;
	}

	/**
	 * This method initializes getCurrentImageMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getGetCurrentImageMenuItem() {
		if (getCurrentImageMenuItem == null) {
			getCurrentImageMenuItem = new JMenuItem();
			getCurrentImageMenuItem.setText("Get Current Image");
			getCurrentImageMenuItem.setIcon(new ImageIcon(getClass().getResource("/resources/images/list-add.png")));
			getCurrentImageMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getGetCurrentImageButton().doClick();
				}
			});
		}
		return getCurrentImageMenuItem;
	}

	/**
	 * This method initializes openImageButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getOpenImageButton() {
		if (openImageButton == null) {
			openImageButton = new JButton();
			openImageButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/document-open.png")));
			openImageButton.setToolTipText("Open Image...");
			openImageButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.openImage();
				}
			});
		}
		return openImageButton;
	}

	/**
	 * This method initializes getCurrentImageButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getGetCurrentImageButton() {
		if (getCurrentImageButton == null) {
			getCurrentImageButton = new JButton();
			getCurrentImageButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/list-add.png")));
			getCurrentImageButton.setToolTipText("Get Current Image");
			getCurrentImageButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ImagePlus image = IJ.getImage();
					model.addImage(image);
				}
			});
		}
		return getCurrentImageButton;
	}

	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowClosing(WindowEvent arg0) {
		if (arg0.getWindow()==this) {
			model.closeAllImages();
			this.removeWindowListener(this);
			this.removeWindowStateListener(this);
			model.view=null;
		}
	}

	public void windowDeactivated(WindowEvent arg0) {

	}

	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowStateChanged(WindowEvent arg0) {
		System.out.println("window state changed");
	}

	/**
	 * This method initializes OptionsMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getOptionsMenu() {
		if (OptionsMenu == null) {
			OptionsMenu = new JMenu();
			OptionsMenu.setText("Options");
			OptionsMenu.setMnemonic(KeyEvent.VK_UNDEFINED);
			OptionsMenu.add(getLockUnlockRadioButtonMenuItem());
		}
		return OptionsMenu;
	}

	/**
	 * This method initializes lockUnlockRadioButtonMenuItem	
	 * 	
	 * @return javax.swing.JRadioButtonMenuItem	
	 */
	private JRadioButtonMenuItem getLockUnlockRadioButtonMenuItem() {
		if (lockUnlockRadioButtonMenuItem == null) {
			lockUnlockRadioButtonMenuItem = new JRadioButtonMenuItem();
			lockUnlockRadioButtonMenuItem.setText("lock horizontal/vertical slider");
			lockUnlockRadioButtonMenuItem.setIcon(new ImageIcon(getClass().getResource("/resources/images/lock_closed.png")));
			lockUnlockRadioButtonMenuItem.setDisabledIcon(new ImageIcon(getClass().getResource("/resources/images/lock_open.png")));
			lockUnlockRadioButtonMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							model.setSlidersLocked(!model.getSlidersLocked());
						}
					});
		}
		return lockUnlockRadioButtonMenuItem;
	}

	/**
	 * This method initializes setImageListMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getSetImageListMenuItem() {
		if (setImageListMenuItem == null) {
			setImageListMenuItem = new JMenuItem();
			setImageListMenuItem.setText("Set Image List...");
			setImageListMenuItem.setIcon(new ImageIcon(getClass().getResource("/resources/images/another_folder_icon_01.png")));
			setImageListMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getSetImageListButton().doClick();
				}
			});
		}
		return setImageListMenuItem;
	}

	/**
	 * This method initializes setImageListButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSetImageListButton() {
		if (setImageListButton == null) {
			setImageListButton = new JButton();
			setImageListButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/another_folder_icon_01.png")));
			setImageListButton.setToolTipText("Set Image List...");
			setImageListButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.getListEditor().show();
					model.getSlideShowControl().setFileList(model.getListEditor().getList());
					model.gotoFirstImage();
				}
			});
		}
		return setImageListButton;
	}

	/**
	 * This method initializes gotoFirstImageButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getGotoFirstImageButton() {
		if (gotoFirstImageButton == null) {
			gotoFirstImageButton = new JButton();
			gotoFirstImageButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/go-first.png")));
			gotoFirstImageButton.setToolTipText("Goto First Image");
			gotoFirstImageButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.gotoFirstImage();
				}
			});
		}
		return gotoFirstImageButton;
	}

	/**
	 * This method initializes gotoPreviousImageButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getGotoPreviousImageButton() {
		if (gotoPreviousImageButton == null) {
			gotoPreviousImageButton = new JButton();
			gotoPreviousImageButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/go-previous.png")));
			gotoPreviousImageButton.setToolTipText("Goto Previous Image");
			gotoPreviousImageButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.gotoPreviousImage();
				}
			});
		}
		return gotoPreviousImageButton;
	}

	/**
	 * This method initializes gotoNextImageButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getGotoNextImageButton() {
		if (gotoNextImageButton == null) {
			gotoNextImageButton = new JButton();
			gotoNextImageButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/go-next.png")));
			gotoNextImageButton.setToolTipText("Goto Next Image");
			gotoNextImageButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.gotoNextImage();
				}
			});
		}
		return gotoNextImageButton;
	}

	/**
	 * This method initializes gotoLastImageButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getGotoLastImageButton() {
		if (gotoLastImageButton == null) {
			gotoLastImageButton = new JButton();
			gotoLastImageButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/go-last.png")));
			gotoLastImageButton.setToolTipText("Goto Last Image");
			gotoLastImageButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.gotoLastImage();
				}
			});
		}
		return gotoLastImageButton;
	}

	/**
	 * This method initializes reloadCurrentImageButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getReloadCurrentImageButton() {
		if (reloadCurrentImageButton == null) {
			reloadCurrentImageButton = new JButton();
			reloadCurrentImageButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/edit-undo.png")));
			reloadCurrentImageButton.setToolTipText("Revert Current Image");
			reloadCurrentImageButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.revertCurrentImage();
				}
			});
		}
		return reloadCurrentImageButton;
	}

	/**
	 * This method initializes goMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getGoMenu() {
		if (goMenu == null) {
			goMenu = new JMenu();
			goMenu.setText("Go");
			goMenu.add(getReloadCurrentImageMenuItem());
			goMenu.add(getGotoFirstImageMenuItem());
			goMenu.add(getGotoPreviousImageMenuItem());
			goMenu.add(getGotoNextImageMenuItem());
			goMenu.add(getGotoLastImageMenuItem());
		}
		return goMenu;
	}

	/**
	 * This method initializes reloadCurrentImageMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getReloadCurrentImageMenuItem() {
		if (reloadCurrentImageMenuItem == null) {
			reloadCurrentImageMenuItem = new JMenuItem();
			reloadCurrentImageMenuItem.setText("Revert Current Image");
			reloadCurrentImageMenuItem.setIcon(new ImageIcon(getClass().getResource("/resources/images/edit-undo.png")));
			reloadCurrentImageMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							getReloadCurrentImageButton().doClick();
						}
					});
		}
		return reloadCurrentImageMenuItem;
	}

	/**
	 * This method initializes gotoFirstImageMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getGotoFirstImageMenuItem() {
		if (gotoFirstImageMenuItem == null) {
			gotoFirstImageMenuItem = new JMenuItem();
			gotoFirstImageMenuItem.setIcon(new ImageIcon(getClass().getResource("/resources/images/go-first.png")));
			gotoFirstImageMenuItem.setText("Goto First Image");
			gotoFirstImageMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getGotoFirstImageButton().doClick();
				}
			});
		}
		return gotoFirstImageMenuItem;
	}

	/**
	 * This method initializes gotoPreviousImageMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getGotoPreviousImageMenuItem() {
		if (gotoPreviousImageMenuItem == null) {
			gotoPreviousImageMenuItem = new JMenuItem();
			gotoPreviousImageMenuItem.setIcon(new ImageIcon(getClass().getResource("/resources/images/go-previous.png")));
			gotoPreviousImageMenuItem.setText("Goto Previous Image");
			gotoPreviousImageMenuItem.setMnemonic(KeyEvent.VK_LEFT);
			gotoPreviousImageMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							getGotoPreviousImageButton().doClick();
						}
					});
		}
		return gotoPreviousImageMenuItem;
	}

	/**
	 * This method initializes gotoNextImageMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getGotoNextImageMenuItem() {
		if (gotoNextImageMenuItem == null) {
			gotoNextImageMenuItem = new JMenuItem();
			gotoNextImageMenuItem.setText("Goto Next Image");
			gotoNextImageMenuItem.setIcon(new ImageIcon(getClass().getResource("/resources/images/go-next.png")));
			gotoNextImageMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getGotoNextImageButton().doClick();
				}
			});
		}
		return gotoNextImageMenuItem;
	}

	/**
	 * This method initializes gotoLastImageMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getGotoLastImageMenuItem() {
		if (gotoLastImageMenuItem == null) {
			gotoLastImageMenuItem = new JMenuItem();
			gotoLastImageMenuItem.setText("Goto Last Image");
			gotoLastImageMenuItem.setIcon(new ImageIcon(getClass().getResource("/resources/images/go-last.png")));
			gotoLastImageMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getGotoLastImageButton().doClick();
				}
			});
		}
		return gotoLastImageMenuItem;
	}

	/**
	 * This method initializes applicationsMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getApplicationsMenu() {
		if (applicationsMenu == null) {
			applicationsMenu = new JMenu();
			applicationsMenu.setText("Applications");
			applicationsMenu.add(getFluoDistributionInCellMenuItem());
		}
		return applicationsMenu;
	}

	/**
	 * This method initializes FluoDistributionInCellMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getFluoDistributionInCellMenuItem() {
		if (FluoDistributionInCellMenuItem == null) {
			FluoDistributionInCellMenuItem = new JMenuItem();
			FluoDistributionInCellMenuItem.setText("Analyze Fluo Distribution in Cell");
			FluoDistributionInCellMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							FluoDistributionInCellAnalyzer app = new FluoDistributionInCellAnalyzer();
							app.getView().setVisible(true);
							model.addActiveApplication(app);
						}
					});
		}
		return FluoDistributionInCellMenuItem;
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
