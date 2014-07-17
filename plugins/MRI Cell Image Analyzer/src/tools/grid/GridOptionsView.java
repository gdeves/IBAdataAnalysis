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
package tools.grid;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ColorChooser;
import ij.gui.Grid;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.gui.NewImage;
import ij.io.OpenDialog;
import ij.io.SaveDialog;
import ij.measure.ResultsTable;
import ij.plugin.BrowserLauncher;
import ij.plugin.filter.Analyzer;
import ij.plugin.frame.Recorder;

import java.awt.BorderLayout;
import java.awt.Point;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import java.awt.Toolkit;
import javax.swing.JScrollPane;
import javax.swing.JList;
import java.awt.Dimension;
import javax.swing.JLabel;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import broadcaster.WindowManagerBroadcaster;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;

/**
 * This is the user interface of the grid tool. The user can 
 * 	- create a new grid with a given number of rows and columns and put it onto an image
 *  - load and save grids  
 *  - transfer a grid from one image to another 
 *  - remove a grid from an image 
 *  - edit the naming scheme of the grid positions
 *  - apply the grid to a results table
 *  - modify the options
 *  Grids saved in the _grids subfolder will appear in the list and can be loaded by double-clicking.
 *  	 
 * @author	Volker Baecker
 **/
public class GridOptionsView extends JFrame implements Observer {

	protected WindowManagerBroadcaster windowManagerBroadcaster;
	
	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private Grid model;

	private JScrollPane gridsScrollPane = null;

	private JList gridsList = null;

	private JPanel gridOptionsPanel = null;

	private JLabel gridLabel = null;
	
	private JSpinner rowSpinner = null; 
	
	private JSpinner columnSpinner = null;

	private JButton setButton = null;

	private JButton newButton = null;

	private JMenuBar mainMenuBar = null;

	private JMenu fileMenu = null;

	private JMenuItem saveAsMenuItem = null;

	private JButton removeButton = null;

	private JButton editNamesButton = null;

	private JButton applyButton = null;

	private JLabel sortByLabel = null;

	private JRadioButton sortByNoneRadioButton = null;

	private JRadioButton sortByGroupsRadioButton = null;

	private JRadioButton sortByGridPositionRadioButton = null;

	private JCheckBox addEmptyPositionsCheckBox = null;
	
	private ButtonGroup sortByGroup = null;  //  @jve:decl-index=0:

	private GridNameCellRenderer gridNameCellRenderer;

	private JButton gridColorButton = null;

	private JMenuItem saveMenuItem;  //  @jve:decl-index=0:

	private JMenuItem openMenuItem = null;

	private JPopupMenu contextMenu = null;

	private JMenuItem deleteMenuItem = null;

	private JMenu helpMenu = null;

	private JMenuItem manualMenuItem = null;

	private JCheckBox showOutlinesCheckBox = null;

	/**
	 * This is the default constructor
	 */
	public GridOptionsView() {
		super();
		model = new Grid(NewImage.createByteImage("grid", 400, 300, 1, NewImage.FILL_BLACK));
		model.getBroadcaster().addObserver(this);
		windowManagerBroadcaster = new WindowManagerBroadcaster();
		windowManagerBroadcaster.addObserver(this);
		initialize();
	}

	public GridOptionsView(Grid grid) {
		super();
		this.model = grid;
		model.getBroadcaster().addObserver(this);
		windowManagerBroadcaster = new WindowManagerBroadcaster();
		windowManagerBroadcaster.addObserver(this);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(284, 421);
		this.setJMenuBar(getMainMenuBar());
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/icon.gif")));
		this.setContentPane(getJContentPane());
		this.setTitle("MRI Grid");
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
			jContentPane.add(getGridsScrollPane(), BorderLayout.CENTER);
			jContentPane.add(getGridOptionsPanel(), BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	/**
	 * This method initializes gridsScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getGridsScrollPane() {
		if (gridsScrollPane == null) {
			gridsScrollPane = new JScrollPane();
			gridsScrollPane.setViewportView(getGridsList());
		}
		return gridsScrollPane;
	}

	/**
	 * This method initializes gridsList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getGridsList() {
		if (gridsList == null) {
			gridsList = new JList(model.getGridList());
			gridNameCellRenderer = new GridNameCellRenderer(model);
			gridsList.setCellRenderer(gridNameCellRenderer);
			gridsList.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {
					if (e.getButton()==MouseEvent.BUTTON3) {
						e.consume();
						JPopupMenu menu = getContextMenu();
						Point origin = getGridsList().getLocationOnScreen();
						menu.setLocation(e.getX() + origin.x, e.getY() + origin.y);
						menu.show(e.getComponent(), e.getX(), e.getY());
						return;
					}
					if (e.getClickCount()==2) {
						model.loadGrid((String)getGridsList().getSelectedValue());
						getGridsList().repaint();
						e.consume();
					}
				}
			});
		}
		return gridsList;
	}

	/**
	 * This method initializes gridOptionsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getGridOptionsPanel() {
		if (gridOptionsPanel == null) {
			sortByGroup = new ButtonGroup();
			sortByLabel = new JLabel();
			sortByLabel.setBounds(new Rectangle(151, 56, 105, 16));
			sortByLabel.setText("sort by:");
			gridLabel = new JLabel();
			gridLabel.setBounds(new Rectangle(30, 15, 52, 16));
			gridLabel.setText("grid:");
			gridOptionsPanel = new JPanel();
			gridOptionsPanel.setLayout(null);
			gridOptionsPanel.setPreferredSize(new Dimension(1, 250));
			gridOptionsPanel.add(gridLabel, null);
			gridOptionsPanel.add(getRowSpinner(), null);
			gridOptionsPanel.add(getColumnSpinner(), null);
			gridOptionsPanel.add(getSetButton(), null);
			gridOptionsPanel.add(getNewButton(), null);
			gridOptionsPanel.add(getRemoveButton(), null);
			gridOptionsPanel.add(getEditNamesButton(), null);
			gridOptionsPanel.add(getApplyButton(), null);
			gridOptionsPanel.add(sortByLabel, null);
			gridOptionsPanel.add(getSortByNoneRadioButton(), null);
			gridOptionsPanel.add(getSortByGroupsRadioButton(), null);
			gridOptionsPanel.add(getSortByGridPositionRadioButton(), null);
			gridOptionsPanel.add(getAddEmptyPositionsCheckBox(), null);
			gridOptionsPanel.add(getGridColorButton(), null);
			gridOptionsPanel.add(getShowOutlinesCheckBox(), null);
			sortByGroup.add(getSortByNoneRadioButton());
			sortByGroup.add(getSortByGroupsRadioButton());
			sortByGroup.add(getSortByGridPositionRadioButton());
		}
		return gridOptionsPanel;
	}

	public JSpinner getColumnSpinner() {
		if (columnSpinner==null) {
			columnSpinner = new JSpinner(model.getColumns());
			columnSpinner.setBounds(new Rectangle(122, 15, 48, 16));
			columnSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					model.setupGrid();
				}
			});
		}
		return columnSpinner;
	}

	public JSpinner getRowSpinner() {
		if (rowSpinner==null) {
			rowSpinner = new JSpinner(model.getRows());
			rowSpinner.setBounds(new Rectangle(74, 15, 48, 16));
			rowSpinner.setPreferredSize(new Dimension(30, 20));
			rowSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					model.setupGrid();
				}
			});
		}
		return rowSpinner;
	}

	/**
	 * This method initializes setButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSetButton() {
		if (setButton == null) {
			setButton = new JButton();
			setButton.setBounds(new Rectangle(6, 89, 130, 23));
			setButton.setText("set");
			setButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ImagePlus image = WindowManager.getCurrentImage();
					Grid newModel = new Grid(image);
					newModel.setXPositions(model.getXPositions());
					newModel.setYPositions(model.getYPositions());
					newModel.setImage(WindowManager.getCurrentImage());
					newModel.setNames(model.getNames());
					gridNameCellRenderer.setModel(newModel);
					getGridsList().repaint();
					model = newModel;
				}
			});
		}
		return setButton;
	}

	/**
	 * This method initializes newButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getNewButton() {
		if (newButton == null) {
			newButton = new JButton();
			newButton.setBounds(new Rectangle(6, 56, 130, 23));
			newButton.setText("new");
			newButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ImagePlus image = WindowManager.getCurrentImage();
					if (model.getImage()==image && model.getImage().getWindow().getCanvas()==model) return;
					model = new Grid(image);
					model.getRows().setValue(getRowSpinner().getValue());
					model.getColumns().setValue(getColumnSpinner().getValue());
					model.setupGrid();
					model.setImage(WindowManager.getCurrentImage());
				}
			});
		}
		return newButton;
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
			mainMenuBar.add(getHelpMenu());
		}
		return mainMenuBar;
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
			fileMenu.add(getOpenMenuItem());
			fileMenu.add(getSaveMenuItem());
			fileMenu.add(getSaveAsMenuItem());
		}
		return fileMenu;
	}

	/**
	 * This method initializes saveMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getSaveMenuItem() {
		if (saveMenuItem == null) {
			saveMenuItem = new JMenuItem();
			saveMenuItem.setText("Save...");
			saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ImagePlus image  = model.getImage();
					String name = "";
					if (image != null) name = image.getTitle();
					name = IJ.getString("name:", name);
					if (name==null)
						return;
					String directory = IJ.getDirectory("startup") + "_grids" + File.separator;
					if (!name.endsWith(".grid")) name = name + ".grid";
					model.saveAs(directory + name);
				}
			});
		}
		return saveMenuItem;
	}
	
	/**
	 * This method initializes saveMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getSaveAsMenuItem() {
		if (saveAsMenuItem == null) {
			saveAsMenuItem = new JMenuItem();
			saveAsMenuItem.setText("Save As...");
			saveAsMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ImagePlus image  = model.getImage();
					String name = "";
					if (image != null) name = image.getTitle();
					SaveDialog sd = new SaveDialog("Save as ", name, ".grid");
					name = sd.getFileName();
					if (name==null)
						return;
					String directory = sd.getDirectory();
					if (!name.endsWith(".grid")) name = name + ".grid";
					model.saveAs(directory + name);
				}
			});
		}
		return saveAsMenuItem;
	}

	public void update(Observable sender, Object aspect) {
		if (aspect.equals("currentImage")) handleCurrentImageChanged();
		if (aspect.equals("gridlist:")) handleGridListChanged();
		if (aspect.equals("grid:")) handleGridChanged();
	}

	private void handleGridChanged() {
		reset();
		getGridsList().repaint();
	}

	private void handleGridListChanged() {
		this.getGridsList().setListData(model.getGridList());
	}

	private void handleCurrentImageChanged() {
		ImagePlus image = WindowManager.getCurrentImage();
		if (image==model.getImage()) return;
		if (image.getCanvas()!=null && image.getCanvas().getClass()==model.getClass()) {
			this.model = (Grid)image.getCanvas();
			model.setLoadedGridName(null);
			gridNameCellRenderer.setModel(model);
			reset();
			getGridsList().repaint();
		}
	}

	private void reset() {
		rowSpinner.setValue(model.getRows().getValue());
		columnSpinner.setValue(model.getColumns().getValue());
	}

	/**
	 * This method initializes removeButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getRemoveButton() {
		if (removeButton == null) {
			removeButton = new JButton();
			removeButton.setBounds(new Rectangle(6, 123, 130, 23));
			removeButton.setText("remove");
			removeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ImagePlus image = WindowManager.getCurrentImage();
					if (image==null) return;
					if (image.getWindow().getCanvas()!=model) return;
					ImageWindow win = image.getWindow();
					if (win!=null) {
						win.setVisible(false);
					}
					win = new ImageWindow(image, new ImageCanvas(image));
					win.updateImage(image);
				}
			});
		}
		return removeButton;
	}

	/**
	 * This method initializes editNamesButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getEditNamesButton() {
		if (editNamesButton == null) {
			editNamesButton = new JButton();
			editNamesButton.setBounds(new Rectangle(6, 158, 130, 23));
			editNamesButton.setText("edit names");
			editNamesButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JFrame tableWindow = new JFrame();
					tableWindow.setTitle("MRI Grid Names");
					tableWindow.setSize(700, 190);
					JTable table = new JTable(new GridNamesTableModel(model.getNames()));
					tableWindow.getContentPane().add(table);
					tableWindow.setVisible(true);
				}
			});
		}
		return editNamesButton;
	}

	/**
	 * This method initializes applyButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getApplyButton() {
		if (applyButton == null) {
			applyButton = new JButton();
			applyButton.setBounds(new Rectangle(21, 214, 101, 23));
			applyButton.setText("apply");
			applyButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ResultsTable table = Analyzer.getResultsTable();
					ResultsTable newTable = model.applyGridTo(table);
					newTable.show(model.getResultsTableTitle());
					Recorder.recordString("call(\"tools.grid.RunGrid.applyGrid\");\n");
					if (!model.isShowOutlines()) return;
					model.getOutlinesImage().show();
				}
			});
		}
		return applyButton;
	}

	/**
	 * This method initializes sortByNoneRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getSortByNoneRadioButton() {
		if (sortByNoneRadioButton == null) {
			sortByNoneRadioButton = new JRadioButton();
			sortByNoneRadioButton.setBounds(new Rectangle(151, 76, 128, 21));
			sortByNoneRadioButton.setText("coordinates");
			if (model.getSortBy()==Grid.SORT_BY_COORDINATES) 
				sortByNoneRadioButton.setSelected(true); 
			else 
				sortByNoneRadioButton.setSelected(false); 
			sortByNoneRadioButton
			.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.setSortBy(Grid.SORT_BY_COORDINATES);
				}
			});
		}
		return sortByNoneRadioButton;
	}

	/**
	 * This method initializes sortByGroupsRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getSortByGroupsRadioButton() {
		if (sortByGroupsRadioButton == null) {
			sortByGroupsRadioButton = new JRadioButton();
			sortByGroupsRadioButton.setBounds(new Rectangle(151, 97, 97, 21));
			sortByGroupsRadioButton.setText("label");
			if (model.getSortBy()==Grid.SORT_BY_LABEL) 
				sortByGroupsRadioButton.setSelected(true); 
			else 
				sortByGroupsRadioButton.setSelected(false); 
			sortByGroupsRadioButton
			.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.setSortBy(Grid.SORT_BY_LABEL);
				}
			});
		}
		return sortByGroupsRadioButton;
	}

	/**
	 * This method initializes sortByGridPositionRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getSortByGridPositionRadioButton() {
		if (sortByGridPositionRadioButton == null) {
			sortByGridPositionRadioButton = new JRadioButton();
			sortByGridPositionRadioButton.setBounds(new Rectangle(151, 118, 121, 21));
			sortByGridPositionRadioButton.setText("grid position");
			if (model.getSortBy()==Grid.SORT_BY_GRID_POSITION) 
				sortByGridPositionRadioButton.setSelected(true);
			else 
				sortByGridPositionRadioButton.setSelected(false); 
			sortByGridPositionRadioButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							model.setSortBy(Grid.SORT_BY_GRID_POSITION);
						}
					});
		}
		return sortByGridPositionRadioButton;
	}

	/**
	 * This method initializes addEmptyPositionsCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getAddEmptyPositionsCheckBox() {
		if (addEmptyPositionsCheckBox == null) {
			addEmptyPositionsCheckBox = new JCheckBox();
			addEmptyPositionsCheckBox.setBounds(new Rectangle(151, 148, 111, 21));
			addEmptyPositionsCheckBox.setText("add empty");
			addEmptyPositionsCheckBox.setSelected(model.isFillInEmptyPositions());
			addEmptyPositionsCheckBox
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							model.setFillInEmptyPositions(!model.isFillInEmptyPositions());
						}
					});
		}
		return addEmptyPositionsCheckBox;
	}

	/**
	 * This method initializes gridColorButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getGridColorButton() {
		if (gridColorButton == null) {
			gridColorButton = new JButton();
			gridColorButton.setBounds(new Rectangle(154, 214, 86, 23));
			gridColorButton.setText("color");
			gridColorButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ColorChooser chooser = new ColorChooser("grid color", model.getGridColor(), false);
					model.setGridColor(chooser.getColor());
				}
			});
		}
		return gridColorButton;
	}

	/**
	 * This method initializes openMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getOpenMenuItem() {
		if (openMenuItem == null) {
			openMenuItem = new JMenuItem();
			openMenuItem.setText("Open...");
			openMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					OpenDialog dialog = new OpenDialog("open grid", null);
					String directory = dialog.getDirectory();
					String filename = dialog.getFileName();
					model.setLoadedGridName("");
					model.load(directory+File.separator+filename);
				}
			});
		}
		return openMenuItem;
	}

	/**
	 * This method initializes contextMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JPopupMenu getContextMenu() {
		if (contextMenu == null) {
			contextMenu = new JPopupMenu();
			contextMenu.add(getDeleteMenuItem());
		}
		return contextMenu;
	}

	/**
	 * This method initializes deleteMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getDeleteMenuItem() {
		if (deleteMenuItem == null) {
			deleteMenuItem = new JMenuItem();
			deleteMenuItem.setText("delete selected");
			deleteMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.deleteGrids(getGridsList().getSelectedValues());
				}
			});
		}
		return deleteMenuItem;
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
			helpMenu.add(getManualMenuItem());
		}
		return helpMenu;
	}

	/**
	 * This method initializes manualMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getManualMenuItem() {
		if (manualMenuItem == null) {
			manualMenuItem = new JMenuItem();
			manualMenuItem.setText("open manual");
			manualMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						BrowserLauncher.openURL("file:./_help/mri grid.pdf");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
		}
		return manualMenuItem;
	}

	/**
	 * This method initializes showOutlinesCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getShowOutlinesCheckBox() {
		if (showOutlinesCheckBox == null) {
			showOutlinesCheckBox = new JCheckBox();
			showOutlinesCheckBox.setSelected(model.isShowOutlines());
			showOutlinesCheckBox.setBounds(new Rectangle(151, 175, 145, 21));
			showOutlinesCheckBox.setText("show outlines");
			showOutlinesCheckBox.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.setShowOutlines(!model.isShowOutlines());
				}
			});
		}
		return showOutlinesCheckBox;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
