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
package objectModelingWorkbench;

import gui.Options;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageWindow;
import ij.gui.Roi;
import ij.io.OpenDialog;
import imagejProxies.MRIInterpreter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.JViewport;
import javax.swing.JScrollBar;

import operations.image.ScaleImageOperation;


import tools.magicWand.MagicWand;
import utils.FileFromUser;
import java.awt.Toolkit;

/**
 * The user interface of the ObjectModelingWorkbench. On the left hand are a list of images and
 * the current translation, rotation and score. On the right hand is a canvas in which one or two
 * images at a time can be displayed.
 * 
 * @author Volker B�cker
 */
public class ObjectModelingWorkbenchView extends JFrame implements Observer, KeyListener {
	private static final long serialVersionUID = -8439606539966828029L;
	private JPanel jContentPane = null;
	private JMenuBar jJMenuBar = null;
	private JMenu fileMenu = null;
	private JMenuItem openTranslationsMenuItem = null;
	private JMenuItem openStackMenuItem = null;
	private JMenuItem saveSeriesMenuItem = null;
	protected ObjectModelingWorkbench model;
	private JScrollPane translationsTableScrollPane = null;
	private JTable translationsTable = null;
	private JViewport jViewport = null;
	private JPanel jPanel = null;
	private ImageCanvas imageCanvas = null;
	private JPanel scrollImagePanel = null;
	private JScrollBar vScrollBar = null;
	private JScrollBar hScrollBar = null;
	private JPopupMenu tableContextMenu;
	private JMenuItem saveTranslationsMenuItem = null;
	private JMenu optionsMenu = null;
	private JMenuItem displayOptionsMenuItem = null;
	private JMenuItem openSelectionsMenuItem = null;
	private JMenuItem saveSelectionsMenuItem = null;
	private JMenu selectionsMenu = null;
	private JMenuItem clearOutsideMenuItem = null;
	private JMenuItem clearOutsideAndFillInsideMenuItem = null;
	private JMenu processMenu = null;
	private JMenuItem operationMenuItem = null;
	private JMenu modelMenu = null;
	private JMenuItem objectManagerMenuItem = null;
	private JMenuItem calculateAlignmentsMenuItem = null;
	private JMenuItem fastCalculateTranslationsMenuItem = null;
	private JMenuItem scaleSeriesMenuItem = null;
	private JMenu helpMenu = null;
	private JMenuItem helpMenuItem = null;
	private JMenuItem setSelectionToAllSlicesMenuItem = null;
	/**
	 * This is the default constructor
	 */
	public ObjectModelingWorkbenchView() {
		super();
		initialize();
	}


	public ObjectModelingWorkbenchView(ObjectModelingWorkbench model) {
		super();
		this.model = model;
		initialize();
		model.addObserver(this);
	}
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(619, 401);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/icon.gif")));
		this.setJMenuBar(getJJMenuBar());
		this.setContentPane(getJContentPane());
		this.setTitle("MRI Object Modeling Workbench");
		this.addWindowListener(new java.awt.event.WindowAdapter() {   
			public void windowActivated(java.awt.event.WindowEvent e) {    
				if (getImageCanvas().image!=null && getImageCanvas().image.getWindow() != null) WindowManager.setCurrentWindow(getImageCanvas().image.getWindow());
				if (getImageCanvas().secondImage!=null && getImageCanvas().secondImage.getWindow() != null) WindowManager.setCurrentWindow(getImageCanvas().secondImage.getWindow());
				
			}
			public void windowClosing(java.awt.event.WindowEvent e) {
				if (getImageCanvas().image!=null && getImageCanvas().image.getWindow() != null) getImageCanvas().image.getWindow().close();
				if (getImageCanvas().secondImage!=null && getImageCanvas().secondImage.getWindow() != null) getImageCanvas().secondImage.getWindow().close();
			}
		});
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
			jContentPane.add(getJPanel(), java.awt.BorderLayout.WEST);
			jContentPane.add(getScrollImagePanel(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}

	protected ImageCanvas getImageCanvas() {
		if (imageCanvas==null) {
			imageCanvas = new ImageCanvas();
			imageCanvas.setImage(new ImagePlus());
			imageCanvas.setPreferredSize(new Dimension(2000,2000));
			imageCanvas.getBroadcaster().addObserver(this);
		}
		return imageCanvas;
	}


	/**
	 * This method initializes jJMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getFileMenu());
			jJMenuBar.add(getSelectionsMenu());
			jJMenuBar.add(getProcessMenu());
			jJMenuBar.add(getModelMenu());
			jJMenuBar.add(getOptionsMenu());
			jJMenuBar.add(getHelpMenu());
		}
		return jJMenuBar;
	}

	/**
	 * This method initializes fileMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("File");
			fileMenu.add(getOpenTranslationsMenuItem());
			fileMenu.add(getOpenStackMenuItem());
			fileMenu.addSeparator();
			fileMenu.add(getSaveTranslationsMenuItem());
			fileMenu.add(getSaveSeriesMenuItem());
			fileMenu.addSeparator();
			fileMenu.add(getOpenSelectionsMenuItem());
			fileMenu.add(getSaveSelectionsMenuItem());
		}
		return fileMenu;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getOpenTranslationsMenuItem() {
		if (openTranslationsMenuItem == null) {
			openTranslationsMenuItem = new JMenuItem();
			openTranslationsMenuItem.setText("open translations...");
			openTranslationsMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					OpenDialog.getDefaultDirectory();
					OpenDialog openDialog = new OpenDialog("Open translations", "translations", ".txt");
					String filename = openDialog.getFileName();
					if (filename==null) {
						return;
					}
					String folder = openDialog.getDirectory();
					String path = folder + filename;
					OpenDialog.setDefaultDirectory(folder);
					model.openAlignments(path);
				}
			});
		}
		return openTranslationsMenuItem;
	}

	/**
	 * This method initializes openStackMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getOpenStackMenuItem() {
		if (openStackMenuItem == null) {
			openStackMenuItem = new JMenuItem();
			openStackMenuItem.setText("open series...");
			openStackMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.openSeries();
				}
			});
		}
		return openStackMenuItem;
	}

	private JMenuItem getSaveSeriesMenuItem() {
		if (saveSeriesMenuItem == null) {
			saveSeriesMenuItem = new JMenuItem();
			saveSeriesMenuItem.setText("apply and save series...");
			saveSeriesMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					File targetFile = FileFromUser.getSaveFile("save series", "tif", ".tif");
					if (targetFile==null) return;
					model.applyTranslationsAndSaveSeries(targetFile, true);
				}
			});
		}
		return saveSeriesMenuItem;
	}
	
	public void update(Observable sender, Object aspect) {
		if (aspect.equals("translations")) {
			this.updateTranslations();
		}
		if (aspect.equals("image")) {
			this.updateImage();
		}
		if (aspect.equals("secondImage")) {
			this.updateSecondImage();
		}
		if (aspect.equals("display")) {
			this.getImageCanvas().validate();
			this.getImageCanvas().validateWindow();
		}
		if (aspect.equals("showObjects")) {
			this.getImageCanvas().setObjectManager(model.getObjectManager());
			this.getImageCanvas().repaint();
		}
		if (aspect.equals(":roi")) {
			this.updateRoi();
		}
	}

	protected void updateRoi() {
		if (model.firstImageSlice!=null && model.firstImage!=null) model.firstImageSlice.setRoi(model.firstImage.getRoi());
		if (model.secondImageSlice!=null && model.secondImage!=null) model.secondImageSlice.setRoi(model.secondImage.getRoi());
	}


	protected void updateSecondImage() {
		if (this.getImageCanvas().secondImage!=null) {
			if (this.getImageCanvas().secondImage.getWindow()!=null) {
				this.getImageCanvas().secondImage.getWindow().close();
			}
		}
		ImagePlus image = model.getSecondImage();
		Roi roi = null;
		if (image!=null) roi = image.getRoi();
		image.setImage(image.getProcessor().createImage());
		MRIInterpreter interpreter = new MRIInterpreter();
		interpreter.setBatchMode(true);
		ImageWindow win = new ImageWindow(image);
		image.setWindow(win);
		interpreter.setBatchMode(false);
		image.setRoi(roi);
		this.getImageCanvas().setSecondImage(image);
		this.getImageCanvas().removeKeyListener(this);
		this.getImageCanvas().removeKeyListener(model.getObjectManager());
		this.getImageCanvas().addKeyListener(this);
		this.getImageCanvas().addKeyListener(model.getObjectManager());
		this.getImageCanvas().setSecondImageOffsetX(model.getSecondImageOffsetX());
		this.getImageCanvas().setSecondImageOffsetY(model.getSecondImageOffsetY());
		this.getImageCanvas().setSecondImageAngle(model.getSecondImageAngle());
		this.getImageCanvas().setSecondImageOptions(model.getSecondImageOptions());
		this.getImageCanvas().validateWindow();
		MagicWand wand =  MagicWand.getInstance();
		if (wand.isActive()) {
			wand.setImage(image);
		}
	}


	protected void updateImage() {
		if (this.getImageCanvas().secondImage!=null) {
			if (this.getImageCanvas().secondImage.getWindow()!=null) {
				this.getImageCanvas().secondImage.getWindow().close();
			}
		}
		this.getImageCanvas().setSecondImage(null);
		if (this.getImageCanvas().image!=null) {
			if (this.getImageCanvas().image.getWindow()!=null) {
				this.getImageCanvas().image.getWindow().close();
			}
		}
		ImagePlus image = model.getFirstImage();
		if (image==null) return;
		Roi roi = image.getRoi();
		image.setImage(image.getProcessor().createImage());
		MRIInterpreter interpreter = new MRIInterpreter();
		interpreter.setBatchMode(true);
		ImageWindow win = new ImageWindow(image);
		image.setWindow(win);
		interpreter.setBatchMode(false);
		image.setRoi(roi);
		this.getImageCanvas().setImage(image);
		this.getImageCanvas().removeKeyListener(model.getObjectManager());
		this.getImageCanvas().addKeyListener(model.getObjectManager());
		this.getImageCanvas().setXOffset(model.getFirstImageOffsetX());
		this.getImageCanvas().setYOffset(model.getFirstImageOffsetY());
		this.getImageCanvas().setAngle(model.getFirstImageAngle());
		this.getImageCanvas().setImageOptions(model.getFirstImageOptions());
		this.getImageCanvas().validateWindow();
		MagicWand wand =  MagicWand.getInstance();
		if (wand.isActive()) {
			wand.setImage(image);
		}
	}


	protected void updateTranslations() {
		this.getTranslationsTable().invalidate();
		this.getTranslationsTable().repaint();
	}


	/**
	 * This method initializes translationsTableScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getTranslationsTableScrollPane() {
		if (translationsTableScrollPane == null) {
			translationsTableScrollPane = new JScrollPane();
			translationsTableScrollPane.setAlignmentX(0.0F);
			translationsTableScrollPane.setAlignmentY(0.0F);
			translationsTableScrollPane.setColumnHeader(getJViewport());
			translationsTableScrollPane.setViewportView(getTranslationsTable());
		}
		return translationsTableScrollPane;
	}


	public int[] getSelectionIndices() {
		int[] result = this.getTranslationsTable().getSelectedRows();
		return result;
	}
	/**
	 * This method initializes translationsTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getTranslationsTable() {
		if (translationsTable == null) {
			TableModel tmodel = model.getAlignments();
			translationsTable = new JTable(tmodel);
			TableColumnModel tableColumnModel = translationsTable.getColumnModel();
			translationsTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
			translationsTable.setPreferredScrollableViewportSize(new Dimension(320, 70));
			tableColumnModel.getColumn(0).setWidth(20);
			tableColumnModel.getColumn(1).setWidth(25);
			tableColumnModel.getColumn(2).setWidth(30);
			tableColumnModel.getColumn(3).setWidth(40);
			ListSelectionModel rowSM = translationsTable.getSelectionModel();
			translationsTable.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mousePressed(java.awt.event.MouseEvent e) {
					if (e.getButton()!=MouseEvent.BUTTON3) return;
					JPopupMenu menu = getTableContextMenu();
					Point origin = getTranslationsTable().getLocationOnScreen();
					menu.setLocation(e.getX() + origin.x, e.getY() + origin.y);
					menu.show(e.getComponent(), e.getX(), e.getY());
				}
			});
			rowSM.addListSelectionListener(new ListSelectionListener() {
			    public void valueChanged(ListSelectionEvent e) {
			        //Ignore extra messages.
			        if (e.getValueIsAdjusting()) return;

			        ListSelectionModel lsm =
			            (ListSelectionModel)e.getSource();
			        if (model.firstImageSlice!=null && model.firstImage!=null) model.firstImageSlice.setRoi(model.firstImage.getRoi());
		    		if (model.secondImageSlice!=null && model.secondImage!=null) model.secondImageSlice.setRoi(model.secondImage.getRoi());
			        if (lsm.isSelectionEmpty()) {
			            
			        } else {
			        	Vector<Integer> selection = new Vector<Integer>();
			        	for (int i=lsm.getMinSelectionIndex(); i<=lsm.getMaxSelectionIndex(); i++) {
			        		if (lsm.isSelectedIndex(i)) {
			        			selection.add(new Integer(i));
			        		}
			        	}
			        	model.loadImagesForSelection(selection);
			        }
			    }
			});
		}
		return translationsTable;
	}

	protected JPopupMenu getTableContextMenu() {
		if (this.tableContextMenu==null) {
			tableContextMenu = new JPopupMenu();
			tableContextMenu.setLabel("actions");
			JMenuItem menuItem = new JMenuItem("display options");
			menuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					tableContextMenu.setVisible(false);
					int[] selections = getTranslationsTable().getSelectedRows();
					if (selections.length==0) return;
					if (selections.length==1) model.getFirstImageOptions().show();
					else model.getSecondImageOptions().show();
				}
			});
			tableContextMenu.add(menuItem);
		}
		return this.tableContextMenu;
	}


	/**
	 * This method initializes jViewport	
	 * 	
	 * @return javax.swing.JViewport	
	 */
	private JViewport getJViewport() {
		if (jViewport == null) {
			jViewport = new JViewport();
			jViewport.setPreferredSize(new java.awt.Dimension(15,20));
		}
		return jViewport;
	}


	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new BorderLayout());
			jPanel.add(getTranslationsTableScrollPane(), java.awt.BorderLayout.CENTER);
		}
		return jPanel;
	}


	/**
	 * This method initializes scrollImagePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getScrollImagePanel() {
		if (scrollImagePanel == null) {
			scrollImagePanel = new JPanel();
			scrollImagePanel.setLayout(new BorderLayout());
			scrollImagePanel.add(getImageCanvas(), java.awt.BorderLayout.CENTER);
			scrollImagePanel.add(getVScrollBar(), java.awt.BorderLayout.EAST);
			scrollImagePanel.add(getHScrollBar(), java.awt.BorderLayout.SOUTH);
		}
		return scrollImagePanel;
	}


	/**
	 * This method initializes vScrollBar	
	 * 	
	 * @return javax.swing.JScrollBar	
	 */
	private JScrollBar getVScrollBar() {
		if (vScrollBar == null) {
			vScrollBar = new JScrollBar();
			int height = this.getImageCanvas().getHeight();
			vScrollBar.setMinimum(-height/2);
			vScrollBar.setMaximum(height/2);
			vScrollBar.setValue(0);
			vScrollBar.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
				public void adjustmentValueChanged(java.awt.event.AdjustmentEvent e) {
					vScrollBar.setMinimum(-getImageCanvas().getHeight()/2);
					vScrollBar.setMaximum(getImageCanvas().getHeight()/2);
					getImageCanvas().setScrollY(e.getValue());
					getImageCanvas().repaint();
					getImageCanvas().validateWindow();
				}
			});
		}
		return vScrollBar;
	}


	/**
	 * This method initializes hScrollBar	
	 * 	
	 * @return javax.swing.JScrollBar	
	 */
	private JScrollBar getHScrollBar() {
		if (hScrollBar == null) {
			hScrollBar = new JScrollBar();
			hScrollBar.setOrientation(javax.swing.JScrollBar.HORIZONTAL);
			int width = this.getImageCanvas().getWidth();
			hScrollBar.setMinimum(-width/2);
			hScrollBar.setMaximum(width/2);
			hScrollBar.setValue(0);
			hScrollBar.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
				public void adjustmentValueChanged(java.awt.event.AdjustmentEvent e) {
					hScrollBar.setMinimum(-getImageCanvas().getWidth()/2);
					hScrollBar.setMaximum(getImageCanvas().getWidth()/2);
					getImageCanvas().setScrollX(e.getValue());
					getImageCanvas().repaint();
					getImageCanvas().validateWindow();
				}
			});
		}
		return hScrollBar;
	}


	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		if (e.isControlDown() && e.getKeyCode()==KeyEvent.VK_RIGHT) {
			imageCanvas.tmpSecondImageAngle -= imageCanvas.deltaAngle;
			if (imageCanvas.tmpSecondImageAngle < 0) imageCanvas.tmpSecondImageAngle = (360 + imageCanvas.tmpSecondImageAngle); 
		}
		if (e.isControlDown() && e.getKeyCode()==KeyEvent.VK_LEFT) imageCanvas.tmpSecondImageAngle = (imageCanvas.tmpSecondImageAngle + imageCanvas.deltaAngle) % 360;
		if (!e.isControlDown() && e.getKeyCode()==KeyEvent.VK_RIGHT) imageCanvas.tmpSecondImageOffsetX += imageCanvas.distance;
		if (!e.isControlDown() && e.getKeyCode()==KeyEvent.VK_LEFT) imageCanvas.tmpSecondImageOffsetX -= imageCanvas.distance;
		if (e.getKeyCode()==KeyEvent.VK_UP) imageCanvas.tmpSecondImageOffsetY -= imageCanvas.distance;
		if (e.getKeyCode()==KeyEvent.VK_DOWN) imageCanvas.tmpSecondImageOffsetY += imageCanvas.distance;
		if (e.getKeyCode()==KeyEvent.VK_ENTER && e.isControlDown()) {
			this.acceptAlignmentSlicesChained();
			return;
		}
		if (e.getKeyCode()==KeyEvent.VK_ENTER) {
			this.acceptAlignment();
			return;
		}
		if (e.isControlDown() && e.getKeyCode()==KeyEvent.VK_RIGHT || e.isControlDown() && e.getKeyCode()==KeyEvent.VK_LEFT)
			imageCanvas.deltaAngle = (imageCanvas.deltaAngle+1)%360;
		if ((!e.isControlDown() && e.getKeyCode()==KeyEvent.VK_RIGHT) || ((e.isControlDown() && e.getKeyCode()==KeyEvent.VK_LEFT)))
			imageCanvas.distance++;
		imageCanvas.repaint();
	}

	public void keyReleased(KeyEvent arg0) {
		imageCanvas.distance = 1;
		imageCanvas.deltaAngle = 1;
		int[] selectedRows = this.getTranslationsTable().getSelectedRows();
		if (selectedRows.length<2) return;
		int index1 = selectedRows[0];
		int index2 = selectedRows[selectedRows.length - 1];
		model.showScore(index1, index2, 
								imageCanvas.offsetX,
								imageCanvas.offsetY, 
								imageCanvas.secondImageOffsetX+imageCanvas.tmpSecondImageOffsetX,
								imageCanvas.secondImageOffsetY+imageCanvas.tmpSecondImageOffsetY
								);
	}
	
	protected void acceptAlignmentSlicesChained() {
		int answer = JOptionPane.showConfirmDialog(
				this,
			    "do you want to accept the alignment \nfor all following slices?",
			    "apply changes?",
			    JOptionPane.YES_NO_OPTION,
			    JOptionPane.QUESTION_MESSAGE);
		if (answer!=0) return;
		int index = this.getTranslationsTable().getSelectedRows()[this.getTranslationsTable().getSelectedRows().length-1];
		int x = imageCanvas.tmpSecondImageOffsetX;
		int y = imageCanvas.tmpSecondImageOffsetY;
		int angle = imageCanvas.tmpSecondImageAngle % 360;
		model.translateFollowingBy(index, x, y, angle);
		imageCanvas.tmpSecondImageOffsetX = 0;
		imageCanvas.tmpSecondImageOffsetY = 0;
		imageCanvas.tmpSecondImageAngle = 0;
		model.recalculateAndSetScore(index);
	}

	protected void acceptAlignment() {
		int answer = JOptionPane.showConfirmDialog(
				this,
			    "do you want to accept the alignment \nfor the current slice?",
			    "apply changes?",
			    JOptionPane.YES_NO_OPTION,
			    JOptionPane.QUESTION_MESSAGE);
		if (answer!=0) return;
		int index = this.getTranslationsTable().getSelectedRows()[this.getTranslationsTable().getSelectedRows().length-1];
		int x = imageCanvas.getSecondImageOffsetX() + imageCanvas.tmpSecondImageOffsetX;
		int y = imageCanvas.getSecondImageOffsetY() + imageCanvas.tmpSecondImageOffsetY;
		int angle = (imageCanvas.getSecondImageAngle() + imageCanvas.tmpSecondImageAngle) % 360;
		model.setAlignment(index, x, y, angle);
		imageCanvas.tmpSecondImageOffsetX = 0;
		imageCanvas.tmpSecondImageOffsetY = 0;
		imageCanvas.tmpSecondImageAngle = 0;
		model.recalculateAndSetScore(index);
	}


	/**
	 * This method initializes saveTranslationsMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getSaveTranslationsMenuItem() {
		if (saveTranslationsMenuItem == null) {
			saveTranslationsMenuItem = new JMenuItem();
			saveTranslationsMenuItem.setText("save translations as...");
			saveTranslationsMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					File targetFile = FileFromUser.getSaveFile("save translations as", "translations.txt", ".txt");
					if (targetFile==null) return;
					model.saveTranslationsAs(targetFile);
				}
			});
		}
		return saveTranslationsMenuItem;
	}


	/**
	 * This method initializes optionsMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getOptionsMenu() {
		if (optionsMenu == null) {
			optionsMenu = new JMenu();
			optionsMenu.setText("Options");
			optionsMenu.add(getDisplayOptionsMenuItem());
		}
		return optionsMenu;
	}


	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getDisplayOptionsMenuItem() {
		if (displayOptionsMenuItem == null) {
			displayOptionsMenuItem = new JMenuItem();
			displayOptionsMenuItem.setText("display options");
			displayOptionsMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					new ObjectModelingWorkbenchOptionsView(getImageCanvas()).setVisible(true);
				}
			});
		}
		return displayOptionsMenuItem;
	}


	/**
	 * This method initializes openSelectionsMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getOpenSelectionsMenuItem() {
		if (openSelectionsMenuItem == null) {
			openSelectionsMenuItem = new JMenuItem();
			openSelectionsMenuItem.setText("open selections...");
			openSelectionsMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					File targetFile = FileFromUser.getOpenFile("open selections", "selections.sel", ".sel");
					if (targetFile==null) return;
					model.openSelections(targetFile);
				}
			});
		}
		return openSelectionsMenuItem;
	}


	/**
	 * This method initializes saveSelectionsMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getSaveSelectionsMenuItem() {
		if (saveSelectionsMenuItem == null) {
			saveSelectionsMenuItem = new JMenuItem();
			saveSelectionsMenuItem.setText("save selections...");
			saveSelectionsMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					File targetFile = FileFromUser.getSaveFile("save selections as", "selections.zip", ".zip");
					if (targetFile==null) return;
					model.saveSelectionsAs(targetFile);
				}
			});
		}
		return saveSelectionsMenuItem;
	}


	/**
	 * This method initializes selectionsMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getSelectionsMenu() {
		if (selectionsMenu == null) {
			selectionsMenu = new JMenu();
			selectionsMenu.setText("Selection");
			selectionsMenu.add(getClearOutsideMenuItem());
			selectionsMenu.add(getSetSelectionOnAllSlicesMenuItem());
			selectionsMenu.add(getClearOutsideAndFillInsideMenuItem());
		}
		return selectionsMenu;
	}


	/**
	 * This method initializes clearOutsideMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getClearOutsideMenuItem() {
		if (clearOutsideMenuItem == null) {
			clearOutsideMenuItem = new JMenuItem();
			clearOutsideMenuItem.setText("clear outside...");
			clearOutsideMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					File targetFile = FileFromUser.getSaveFile("save series", "tif", ".tif");
					if (targetFile==null) return;
					model.clearOutsideAndSaveSeries(targetFile);
				}
			});
		}
		return clearOutsideMenuItem;
	}


	/**
	 * This method initializes clearOutsideAndFillInsideMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getClearOutsideAndFillInsideMenuItem() {
		if (clearOutsideAndFillInsideMenuItem == null) {
			clearOutsideAndFillInsideMenuItem = new JMenuItem();
			clearOutsideAndFillInsideMenuItem.setText("clear outside and fill inside...");
			clearOutsideAndFillInsideMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							File targetFile = FileFromUser.getSaveFile("save series", "tif", ".tif");
							if (targetFile==null) return;
							model.clearOutsideFillInsideAndSaveSeries(targetFile);
						}
					});
		}
		return clearOutsideAndFillInsideMenuItem;
	}


	/**
	 * This method initializes processMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getProcessMenu() {
		if (processMenu == null) {
			processMenu = new JMenu();
			processMenu.setText("Process");
			processMenu.add(getOperationMenuItem());
			processMenu.add(getScaleSeriesMenuItem());
			processMenu.add(getFastCalculateTranslationsMenuItem());
			processMenu.add(getCalculateAlignmentsMenuItem());
		}
		return processMenu;
	}


	/**
	 * This method initializes operationMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getOperationMenuItem() {
		if (operationMenuItem == null) {
			operationMenuItem = new JMenuItem();
			operationMenuItem.setText("apply operation...");
			operationMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					(new ApplyOperationToSeries(model)).show();
				}
			});
		}
		return operationMenuItem;
	}


	/**
	 * This method initializes modelMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getModelMenu() {
		if (modelMenu == null) {
			modelMenu = new JMenu();
			modelMenu.setText("Model");
			modelMenu.add(getObjectManagerMenuItem());
		}
		return modelMenu;
	}


	/**
	 * This method initializes objectManagerMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getObjectManagerMenuItem() {
		if (objectManagerMenuItem == null) {
			objectManagerMenuItem = new JMenuItem();
			objectManagerMenuItem.setText("open 3D object manager...");
			objectManagerMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.getObjectManager().show();
				}
			});
		}
		return objectManagerMenuItem;
	}


	/**
	 * This method initializes calculateAlignmentsMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getCalculateAlignmentsMenuItem() {
		if (calculateAlignmentsMenuItem == null) {
			calculateAlignmentsMenuItem = new JMenuItem();
			calculateAlignmentsMenuItem.setText("calculate translations");
			calculateAlignmentsMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							File targetFile = FileFromUser.getSaveFile("save translations", "translations.txt", ".txt" );
							if (targetFile==null) return;
							model.calculateSeriesRegistration(targetFile);
						}
					});
		}
		return calculateAlignmentsMenuItem;
	}


	/**
	 * This method initializes fastCalculateTranslationsMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getFastCalculateTranslationsMenuItem() {
		if (fastCalculateTranslationsMenuItem == null) {
			fastCalculateTranslationsMenuItem = new JMenuItem();
			fastCalculateTranslationsMenuItem.setText("calculate translations quickly");
			fastCalculateTranslationsMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							String answer = null;
							int stepWidth = -1;
							while (stepWidth<0) {
								answer = JOptionPane.showInputDialog(model.view, "step width:", "10");
								try {
										stepWidth = Integer.parseInt(answer);
									} catch (NumberFormatException exc) {
										stepWidth = -1;
									}
							}
							File targetFile = FileFromUser.getSaveFile("save translations", "translations.txt", ".txt" );
							if (targetFile==null) return;
							model.fastCalculateSeriesRegistration(targetFile, stepWidth);
						}
					});
		}
		return fastCalculateTranslationsMenuItem;
	}


	/**
	 * This method initializes scaleSeriesMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getScaleSeriesMenuItem() {
		if (scaleSeriesMenuItem == null) {
			scaleSeriesMenuItem = new JMenuItem();
			scaleSeriesMenuItem.setText("scale series...");
			scaleSeriesMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ScaleImageOperation op = new ScaleImageOperation();
					Options options = op.getOptions();
					options.dialogView().setVisible(true);
					File targetFile = FileFromUser.getSaveFile("save scales series", "", ".tif" );
					if (targetFile==null) return;
					int[] selectedRows = getTranslationsTable().getSelectedRows();
					Vector<Integer[]> selection = new Vector<Integer[]>();
					for (int i=0; i<selectedRows.length; i++) {
						selection.add(new Integer[selectedRows[i]]);
					}
					model.scaleSeries(op, targetFile);
				}
			});
		}
		return scaleSeriesMenuItem;
	}


	/**
	 * This method initializes helpMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu();
			helpMenu.setText("Help");
			helpMenu.add(getHelpMenuItem());
		}
		return helpMenu;
	}


	/**
	 * This method initializes helpMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getHelpMenuItem() {
		if (helpMenuItem == null) {
			helpMenuItem = new JMenuItem();
			helpMenuItem.setText("help");
			helpMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.openHelp();
				}
			});
		}
		return helpMenuItem;
	}


	/**
	 * This method initializes setSelectionToAllSlicesMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getSetSelectionOnAllSlicesMenuItem() {
		if (setSelectionToAllSlicesMenuItem == null) {
			setSelectionToAllSlicesMenuItem = new JMenuItem();
			setSelectionToAllSlicesMenuItem.setText("set on all slices");
			setSelectionToAllSlicesMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							model.setSelectionOnAllSlices();
						}
					});
		}
		return setSelectionToAllSlicesMenuItem;
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
