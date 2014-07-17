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

import ij.IJ;
import ij.ImagePlus;
import ij.gui.ColorChooser;
import ij3d.Image3DUniverse;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import java.awt.GridLayout;
import java.io.File;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.JSlider;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import operations.Operation;

import utils.FileFromUser;
import java.awt.Toolkit;

/**
 * The user interface of the object 3D manager.  Allows to create and manage objects, to add selections
 * to objects and to create a 3d scene of the objects or to export the objects as image stacks.
 * 
 * @author Volker B�cker
 */
public class Object3DManagerView extends JFrame implements Observer {
	private static final long serialVersionUID = 2739837514575410554L;
	private JPanel jContentPane = null;
	private JPanel topPanel = null;
	private JScrollPane objectsScrollPane = null;
	private JScrollPane selectionsScrollPane = null;
	private JList objectsList = null;
	private JList selectionsList = null;
	private JPanel objectsPanel = null;
	private JPanel selectionsPanel = null;
	private JPanel objectActionsPanel = null;
	private JPanel objectDisplayPanel = null;
	private JCheckBox showObjectCheckBox = null;
	private JButton changeColorButton = null;
	private JPanel manageObjectsPanel = null;
	private JButton addObjectButton = null;
	private JButton renameObjectButton = null;
	private JButton deleteObjectsButton = null;
	private JPanel selectionsActionPanel = null;
	private JSlider opacitySlider = null;
	private JLabel opacityLabel = null;
	private JButton addSelectionsButton = null;
	private JMenuBar jJMenuBar = null;
	private JMenu modelMenu = null;
	private JMenuItem createStackMenuItem = null;
	private JMenuItem saveSeriesMenuItem = null;
	private JPanel displaySelectionsPanel = null;
	private JPanel manageSelectionsPanel = null;
	private JButton setSelectionToActiveImageButton = null;
	private JButton deleteSelectionButton = null;
	protected Object3DManager model;
	private JMenu fileMenu = null;
	private JMenu editMenu = null;
	private JMenuItem objectArithmeticMenuItem = null;
	private JMenuItem openMenuItem = null;
	private JMenuItem saveMenuItem = null;
	private JButton showButton = null;
	private JButton hideButton = null;
	private JButton findAndCreateObjectsButton = null;
	private JMenuItem saveOneSeriesPerObjectMenuItem = null;
	private JMenuItem measureObjectsMenuItem = null;
	private JMenuItem measureObjects3DMenuItem = null;
	private JMenu optionsMenu = null;
	private JMenuItem measurementsMenuItem = null;
	private JMenuItem create3DSurfaceSceneMenuItem = null;
	private JMenuItem saveSeries8BitMenuItem = null;

	public Object3DManagerView() {
		super();
		initialize();
	}

	public Object3DManagerView(Object3DManager model) {
		super();
		this.model = model;
		model.addObserver(this);
		initialize();
	}

	private void initialize() {
		this.setSize(609, 665);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/icon.gif")));
		this.setJMenuBar(getJJMenuBar());
		this.setContentPane(getJContentPane());
		this.setTitle("MRI OMW - 3d object manager");
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
			jContentPane.add(getListPanel(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes llistPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getListPanel() {
		if (topPanel == null) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(1);
			topPanel = new JPanel();
			topPanel.setLayout(gridLayout);
			topPanel.add(getObjectsPanel(), null);
			topPanel.add(getSelectionsPanel(), null);
		}
		return topPanel;
	}

	/**
	 * This method initializes objectsScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getObjectsScrollPane() {
		if (objectsScrollPane == null) {
			objectsScrollPane = new JScrollPane();
			objectsScrollPane.setPreferredSize(new java.awt.Dimension(259,200));
			objectsScrollPane.setViewportView(getObjectsList());
		}
		return objectsScrollPane;
	}

	/**
	 * This method initializes selectionsScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getSelectionsScrollPane() {
		if (selectionsScrollPane == null) {
			selectionsScrollPane = new JScrollPane();
			selectionsScrollPane.setPreferredSize(new java.awt.Dimension(259,200));
			selectionsScrollPane.setViewportView(getSelectionsList());
		}
		return selectionsScrollPane;
	}

	/**
	 * This method initializes objectsList	
	 * 	
	 * @return javax.swing.JList	
	 */
	public JList getObjectsList() {
		if (objectsList == null) {
			objectsList = new JList(model.getObjectNamesWithIndications());
			objectsList
					.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
						public void valueChanged(javax.swing.event.ListSelectionEvent e) {
							changedSelections();
						}
					});
		}
		return objectsList;
	}

	/**
	 * This method initializes selectionsList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getSelectionsList() {
		if (selectionsList == null) {
			selectionsList = new JList();
			selectionsList.setSize(new java.awt.Dimension(140,128));
		}
		return selectionsList;
	}

	/**
	 * This method initializes objectsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getObjectsPanel() {
		if (objectsPanel == null) {
			objectsPanel = new JPanel();
			objectsPanel.setLayout(new BorderLayout());
			objectsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "objects", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			objectsPanel.setPreferredSize(new java.awt.Dimension(269,600));
			objectsPanel.add(getObjectsScrollPane(), java.awt.BorderLayout.CENTER);
			objectsPanel.add(getObjectActionsPanel(), java.awt.BorderLayout.SOUTH);
		}
		return objectsPanel;
	}

	/**
	 * This method initializes selectionsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getSelectionsPanel() {
		if (selectionsPanel == null) {
			selectionsPanel = new JPanel();
			selectionsPanel.setLayout(new BorderLayout());
			selectionsPanel.setPreferredSize(new java.awt.Dimension(259,131));
			selectionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "selections", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			selectionsPanel.add(getSelectionsScrollPane(), java.awt.BorderLayout.CENTER);
			selectionsPanel.add(getSelectionsActionPanel(), java.awt.BorderLayout.SOUTH);
		}
		return selectionsPanel;
	}

	/**
	 * This method initializes objectActionsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getObjectActionsPanel() {
		if (objectActionsPanel == null) {
			objectActionsPanel = new JPanel();
			objectActionsPanel.setLayout(new BoxLayout(getObjectActionsPanel(), BoxLayout.Y_AXIS));
			objectActionsPanel.setPreferredSize(new java.awt.Dimension(0,250));
			objectActionsPanel.add(getObjectDisplayPanel(), null);
			objectActionsPanel.add(getManageObjectsPanel(), null);
		}
		return objectActionsPanel;
	}

	/**
	 * This method initializes objectDisplayPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getObjectDisplayPanel() {
		if (objectDisplayPanel == null) {
			opacityLabel = new JLabel();
			opacityLabel.setBounds(new java.awt.Rectangle(14,52,67,23));
			opacityLabel.setText("opacity:");
			objectDisplayPanel = new JPanel();
			objectDisplayPanel.setLayout(null);
			objectDisplayPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "display objects", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			objectDisplayPanel.add(getShowObjectCheckBox(), null);
			objectDisplayPanel.add(getChangeColorButton(), null);
			objectDisplayPanel.add(getOpacitySlider(), null);
			objectDisplayPanel.add(opacityLabel, null);
			objectDisplayPanel.add(getShowButton(), null);
			objectDisplayPanel.add(getHideButton(), null);
		}
		return objectDisplayPanel;
	}

	/**
	 * This method initializes showObjectCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getShowObjectCheckBox() {
		if (showObjectCheckBox == null) {
			showObjectCheckBox = new JCheckBox();
			showObjectCheckBox.setBounds(new java.awt.Rectangle(20,24,58,21));
			showObjectCheckBox.setText("show");
			showObjectCheckBox.setSelected(model.isShowObjects());
			showObjectCheckBox.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					model.setShowObjects(!model.isShowObjects());
					showObjectCheckBox.setSelected(model.isShowObjects);
				}
			});
		}
		return showObjectCheckBox;
	}

	/**
	 * This method initializes changeColorButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getChangeColorButton() {
		if (changeColorButton == null) {
			changeColorButton = new JButton();
			changeColorButton.setBounds(new java.awt.Rectangle(88,25,122,21));
			changeColorButton.setText("change color...");
			changeColorButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int[] objectIndices = getObjectsList().getSelectedIndices();
					if (objectIndices.length==0) return;
					Object3D object = (Object3D) model.getObjects().get(objectIndices[0]);
					Color oldColor = new Color(object.getColor().getRed(),
											   object.getColor().getGreen(),
											   object.getColor().getBlue());
					ColorChooser colorChooser = new ColorChooser("object color", oldColor, false);
					Color selectedColor = colorChooser.getColor();
					if (selectedColor==null) return;
					Color objectColor = new Color(selectedColor.getRed(),
												  selectedColor.getGreen(),
							                      selectedColor.getBlue());
					model.setObjectColor(objectIndices, objectColor);
				}
			});
		}
		return changeColorButton;
	}

	/**
	 * This method initializes manageObjectsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getManageObjectsPanel() {
		if (manageObjectsPanel == null) {
			manageObjectsPanel = new JPanel();
			manageObjectsPanel.setLayout(null);
			manageObjectsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "manage objects", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			manageObjectsPanel.add(getAddObjectButton(), null);
			manageObjectsPanel.add(getRenameObjectButton(), null);
			manageObjectsPanel.add(getDeleteObjectsButton(), null);
			manageObjectsPanel.add(getAddSelectionsButton(), null);
			manageObjectsPanel.add(getFindAndCreateObjectsButton(), null);
		}
		return manageObjectsPanel;
	}

	/**
	 * This method initializes addObjectButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAddObjectButton() {
		if (addObjectButton == null) {
			addObjectButton = new JButton();
			addObjectButton.setText("add object...");
			addObjectButton.setSize(new java.awt.Dimension(131,21));
			addObjectButton.setLocation(new java.awt.Point(10,23));
			addObjectButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String name = JOptionPane.showInputDialog("name:");
					if (name==null || name.trim().length()==0) return;
					model.addObject(name);
					getObjectsList().setSelectedValue(name, true);
				}
			});
		}
		return addObjectButton;
	}

	/**
	 * This method initializes renameObjectButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getRenameObjectButton() {
		if (renameObjectButton == null) {
			renameObjectButton = new JButton();
			renameObjectButton.setBounds(new java.awt.Rectangle(10,49,131,21));
			renameObjectButton.setText("rename object...");
			renameObjectButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int index = getObjectsList().getSelectedIndex();
					String name = JOptionPane.showInputDialog("new name:");
					if (name==null || name.trim().length()==0) return;
					model.renameObject(index, name);
					getObjectsList().setSelectedValue(name, true);
				}
			});
		}
		return renameObjectButton;
	}

	/**
	 * This method initializes deleteObjectsButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getDeleteObjectsButton() {
		if (deleteObjectsButton == null) {
			deleteObjectsButton = new JButton();
			deleteObjectsButton.setBounds(new java.awt.Rectangle(154,36,131,21));
			deleteObjectsButton.setText("delete objects");
			deleteObjectsButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.deleteObjects(getObjectsList().getSelectedIndices());
				}
			});
		}
		return deleteObjectsButton;
	}

	/**
	 * This method initializes selectionsActionPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getSelectionsActionPanel() {
		if (selectionsActionPanel == null) {
			selectionsActionPanel = new JPanel();
			selectionsActionPanel.setLayout(new BoxLayout(getSelectionsActionPanel(), BoxLayout.Y_AXIS));
			selectionsActionPanel.setPreferredSize(new java.awt.Dimension(0,250));
			selectionsActionPanel.add(getDisplaySelectionsPanel(), null);
			selectionsActionPanel.add(getManageSelectionsPanel(), null);
		}
		return selectionsActionPanel;
	}

	/**
	 * This method initializes opacitySlider	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	private JSlider getOpacitySlider() {
		if (opacitySlider == null) {
			opacitySlider = new JSlider();
			opacitySlider.setBounds(new java.awt.Rectangle(3,72,275,16));
			opacitySlider.setMinimum(0);
			opacitySlider.setMaximum(100);
			opacitySlider.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					int[] indices = getObjectsList().getSelectedIndices();
					for (int i=0; i<indices.length; i++) {
						Object3D object = (Object3D) model.getObjects().get(indices[i]);
						object.setAlpha(opacitySlider.getValue() / 100f);
					}
					model.model.changed("showObjects");
					model.model.changed("display");
				}
			});
		}
		return opacitySlider;
	}

	/**
	 * This method initializes addSelectionsButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAddSelectionsButton() {
		if (addSelectionsButton == null) {
			addSelectionsButton = new JButton();
			addSelectionsButton.setBounds(new java.awt.Rectangle(32,74,222,21));
			addSelectionsButton.setText("add selections");
			addSelectionsButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int[] indices = getObjectsList().getSelectedIndices();
					model.addSelectionsToObjects(indices);
				}
			});
		}
		return addSelectionsButton;
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
			jJMenuBar.add(getEditMenu());
			jJMenuBar.add(getModelMenu());
			jJMenuBar.add(getOptionsMenu());
		}
		return jJMenuBar;
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
			modelMenu.add(getCreateStackMenuItem());
			modelMenu.add(getCreate3DSurfaceSceneMenuItem());
			modelMenu.addSeparator();
			modelMenu.add(getSaveOneSeriesPerObjectMenuItem());
			modelMenu.add(getSaveSeriesMenuItem());
			modelMenu.add(getSaveSeries8BitMenuItem());
			modelMenu.addSeparator();
			modelMenu.add(getMeasureObjectsMenuItem());
			modelMenu.add(getJMenuItem());
		}
		return modelMenu;
	}

	/**
	 * This method initializes createStackMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getCreateStackMenuItem() {
		if (createStackMenuItem == null) {
			createStackMenuItem = new JMenuItem();
			createStackMenuItem.setText("create stack");
			createStackMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ImagePlus stack = model.createStack(getObjectsList().getSelectedIndices(), model.model.getView().getSelectionIndices());
					stack.show();
				}
			});
		}
		return createStackMenuItem;
	}

	/**
	 * This method initializes saveSeriesMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getSaveSeriesMenuItem() {
		if (saveSeriesMenuItem == null) {
			saveSeriesMenuItem = new JMenuItem();
			saveSeriesMenuItem.setText("save series...");
			saveSeriesMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					File targetFile = FileFromUser.getSaveFile("save series", "tif", ".tif");
					if (targetFile==null) return;
					model.saveObjectsToSeries(targetFile, getObjectsList().getSelectedIndices(), model.model.getView().getSelectionIndices());
				}
			});
		}
		return saveSeriesMenuItem;
	}

	/**
	 * This method initializes displaySelectionsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getDisplaySelectionsPanel() {
		if (displaySelectionsPanel == null) {
			displaySelectionsPanel = new JPanel();
			displaySelectionsPanel.setLayout(null);
			displaySelectionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "display selections", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			displaySelectionsPanel.setPreferredSize(new java.awt.Dimension(20,0));
			displaySelectionsPanel.add(getJButton(), null);
		}
		return displaySelectionsPanel;
	}

	/**
	 * This method initializes manageSelectionsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getManageSelectionsPanel() {
		if (manageSelectionsPanel == null) {
			manageSelectionsPanel = new JPanel();
			manageSelectionsPanel.setLayout(null);
			manageSelectionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "manage selections", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			manageSelectionsPanel.setPreferredSize(new java.awt.Dimension(20,0));
			manageSelectionsPanel.add(getDeleteSelectionButton(), null);
		}
		return manageSelectionsPanel;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (setSelectionToActiveImageButton == null) {
			setSelectionToActiveImageButton = new JButton();
			setSelectionToActiveImageButton.setLocation(new java.awt.Point(60,25));
			setSelectionToActiveImageButton.setText("set to active image");
			setSelectionToActiveImageButton.setSize(new java.awt.Dimension(170,21));
			setSelectionToActiveImageButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							// int selectionIndex = getSelectionsList().getSelectedIndex();
							String sel = (String)getSelectionsList().getSelectedValue();
							if (sel==null) return;
							int selectionIndex = getSliceIndexFor(sel);
							int objectIndex = getObjectsList().getSelectedIndex();
							model.setSelectionToActiveImage(objectIndex, selectionIndex);
						}
					});
		}
		return setSelectionToActiveImageButton;
	}

	/**
	 * This method initializes deleteSelectionButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getDeleteSelectionButton() {
		if (deleteSelectionButton == null) {
			deleteSelectionButton = new JButton();
			deleteSelectionButton.setText("delete selections");
			deleteSelectionButton.setSize(new java.awt.Dimension(131,21));
			deleteSelectionButton.setLocation(new java.awt.Point(77,36));
			deleteSelectionButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Object[] selectionValues = getSelectionsList().getSelectedValues();
					int[] indices = new int[selectionValues.length];
					for (int i=0; i<indices.length; i++) {
						indices[i] = getSliceIndexFor((String)selectionValues[i]);
					}
					model.deleteSelections(getObjectsList().getSelectedIndex(), indices);
				}
			});
		}
		return deleteSelectionButton;
	}

	public void update(Observable sender, Object aspect) {
		if (aspect.equals("objects")) this.changedObjects();
		if (aspect.equals("selections")) this.changedSelections();
		if (aspect.equals("selectedObject")) this.handleSelectedObjectChanged();
	}

	protected void handleSelectedObjectChanged() {
		this.getObjectsList().setSelectedIndex(model.selectObjectIndex);
		this.getObjectsList().ensureIndexIsVisible(model.selectObjectIndex);
	}

	public void changedSelections() {
		if (this.getObjectsList().getSelectedIndex()==-1) {
			this.getSelectionsList().setListData(new Vector<String>());
			return;
		}
		Object3D object = (Object3D) model.getObjects().get(this.getObjectsList().getSelectedIndex());
		this.getSelectionsList().setListData(object.getSelectionNames());
		this.getOpacitySlider().setValue(Math.round(100*object.getAlpha()));
	}

	protected void changedObjects() {
		this.getObjectsList().setListData(model.getObjectNamesWithIndications());
	}

	private int getSliceIndexFor(String sel) {
		return Integer.parseInt(sel.split(":")[0]) - 1;
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
		}
		return fileMenu;
	}

	/**
	 * This method initializes objectArithmeticMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getEditMenu() {
		if (editMenu == null) {
			editMenu = new JMenu();
			editMenu.setText("Edit");
			editMenu.add(getObjectArithmeticMenuItem());
		}
		return editMenu;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getObjectArithmeticMenuItem() {
		if (objectArithmeticMenuItem == null) {
			objectArithmeticMenuItem = new JMenuItem();
			objectArithmeticMenuItem.setText("object arithmetic");
			objectArithmeticMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ObjectArithmetic objectCalculator = new ObjectArithmetic();
					objectCalculator.setModel(model);
					objectCalculator.show();
				}
			});
		}
		return objectArithmeticMenuItem;
	}

	/**
	 * This method initializes openMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getOpenMenuItem() {
		if (openMenuItem == null) {
			openMenuItem = new JMenuItem();
			openMenuItem.setText("open...");
			openMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					File aFile = FileFromUser.getOpenFile("open objects", "", ".3do");
					if (aFile==null) return;
					model.open(aFile);
				}
			});
		}
		return openMenuItem;
	}

	/**
	 * This method initializes saveMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getSaveMenuItem() {
		if (saveMenuItem == null) {
			saveMenuItem = new JMenuItem();
			saveMenuItem.setText("save...");
			saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					File aFile = FileFromUser.getSaveFile("save objects", "", ".3do");
					if (aFile==null) return;
					model.save(aFile);
				}
			});
		}
		return saveMenuItem;
	}

	/**
	 * This method initializes showButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getShowButton() {
		if (showButton == null) {
			showButton = new JButton();
			showButton.setBounds(new java.awt.Rectangle(11,91,122,21));
			showButton.setText("show objects");
			showButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int[] selection = getObjectsList().getSelectedIndices();
					model.showObjects(selection);
					getObjectsList().setSelectedIndices(selection);
				}
			});
		}
		return showButton;
	}

	/**
	 * This method initializes hideButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getHideButton() {
		if (hideButton == null) {
			hideButton = new JButton();
			hideButton.setBounds(new java.awt.Rectangle(147,91,122,21));
			hideButton.setText("hide objects");
			hideButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int[] selection = getObjectsList().getSelectedIndices();
					model.hideObjects(selection);
					getObjectsList().setSelectedIndices(selection);
				}
			});
		}
		return hideButton;
	}

	/**
	 * This method initializes autoAddSelectionsButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getFindAndCreateObjectsButton() {
		if (findAndCreateObjectsButton == null) {
			findAndCreateObjectsButton = new JButton();
			findAndCreateObjectsButton.setBounds(new java.awt.Rectangle(32,100,222,21));
			findAndCreateObjectsButton.setText("find and create objects");
			findAndCreateObjectsButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.model.getView().updateRoi(); // test
					String name = JOptionPane.showInputDialog("base name:");
					if (name==null || name.trim().length()==0) return;
					model.findAndCreateObjects(name);
				}
			});
		}
		return findAndCreateObjectsButton;
	}

	/**
	 * This method initializes saveOneSeriesPerObjectMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getSaveOneSeriesPerObjectMenuItem() {
		if (saveOneSeriesPerObjectMenuItem == null) {
			saveOneSeriesPerObjectMenuItem = new JMenuItem();
			saveOneSeriesPerObjectMenuItem.setText("save one series per object");
			saveOneSeriesPerObjectMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							File targetFile = FileFromUser.getSaveFile("save series", "tif", ".tif");
							if (targetFile==null) return;
							int[] selectedSlices =  model.model.getView().getSelectionIndices();
							model.saveOneSeriesPerObject(targetFile, getObjectsList().getSelectedIndices(), selectedSlices);
						}
					});
		}
		return saveOneSeriesPerObjectMenuItem;
	}

	/**
	 * This method initializes measureObjectsMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getMeasureObjectsMenuItem() {
		if (measureObjectsMenuItem == null) {
			measureObjectsMenuItem = new JMenuItem();
			measureObjectsMenuItem.setText("measure objects");
			measureObjectsMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.measureObjects(getObjectsList().getSelectedIndices(), model.model.getView().getSelectionIndices());
				}
			});
		}
		return measureObjectsMenuItem;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItem() {
		if (measureObjects3DMenuItem == null) {
			measureObjects3DMenuItem = new JMenuItem();
			measureObjects3DMenuItem.setText("measure objects 3D");
			measureObjects3DMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.measureObjects3D(getObjectsList().getSelectedIndices(), model.model.getView().getSelectionIndices());
				}
			});
		}
		return measureObjects3DMenuItem;
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
			optionsMenu.add(getMeasurementsMenuItem());
		}
		return optionsMenu;
	}

	/**
	 * This method initializes measurementsMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getMeasurementsMenuItem() {
		if (measurementsMenuItem == null) {
			measurementsMenuItem = new JMenuItem();
			measurementsMenuItem.setText("measurements");
			measurementsMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Operation measure = model.getMeasureOperation();
					measure.getOptions().view().setVisible(true);
				}
			});
		}
		return measurementsMenuItem;
	}

	/**
	 * This method initializes create3DSurfaceSceneMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getCreate3DSurfaceSceneMenuItem() {
		if (create3DSurfaceSceneMenuItem == null) {
			create3DSurfaceSceneMenuItem = new JMenuItem();
			create3DSurfaceSceneMenuItem.setText("create 3D surface scene");
			create3DSurfaceSceneMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							int resamplingFactor= (int)IJ.getNumber("resampling factor: ", 4);
							Image3DUniverse scene = model.create3DSurfaceScene(getObjectsList().getSelectedIndices(), model.model.getView().getSelectionIndices(), resamplingFactor);
							scene.show();
						}
					});
		}
		return create3DSurfaceSceneMenuItem;
	}

	/**
	 * This method initializes saveSeries8BitMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getSaveSeries8BitMenuItem() {
		if (saveSeries8BitMenuItem == null) {
			saveSeries8BitMenuItem = new JMenuItem();
			saveSeries8BitMenuItem.setText("save series 8-bit");
			saveSeries8BitMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					File targetFile = FileFromUser.getSaveFile("save series", "tif", ".tif");
					if (targetFile==null) return;
					model.saveObjectsTo8BitSeries(targetFile, getObjectsList().getSelectedIndices(), model.model.getView().getSelectionIndices());
				}
			});
		}
		return saveSeries8BitMenuItem;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
