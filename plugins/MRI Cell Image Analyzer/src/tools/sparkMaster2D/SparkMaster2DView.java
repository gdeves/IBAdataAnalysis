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
package tools.sparkMaster2D;

import gui.ListEditor;
import ij.ImagePlus;
import ij.gui.Roi;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Toolkit;
import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import java.awt.Rectangle;
import javax.swing.ButtonGroup;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.BorderFactory;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JList;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JProgressBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Detect and measure Ca2+ sparks in confocal time series images. Implementation of the method described in
 * "B�ny�sz, Tam�s,  Chen-Izu, Ye,  Balke, C W,  Izu, Leighton T,
 *  A New Approach to the Detection and Statistical Classification of Ca2+ Sparks
 *  Biophysical Journal,  Jun 15, 2007"
 *  
 * @author	Volker Baecker
 **/
public class SparkMaster2DView extends JFrame implements Observer {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JScrollPane imagesScrollPane = null;

	private JPanel imagesPanel = null;

	private JPanel optionsPanel = null;

	private JButton setNormalizationFrameButton = null;

	private JRadioButton normalizationRadioButton = null;

	private JRadioButton normalizationWithProjectionRadioButton = null;

	private JPanel normalizationPanel = null;

	private JPanel roiPanel = null;

	private JList regionOfInterestList = null;

	private JButton regionOfInterestButton = null;

	private JList excludeRegionsList = null;

	private JLabel excludeRegionsLabel = null;

	private JButton addExcludeRegionsButton = null;

	private JLabel fromFrameLabel = null;

	private JSpinner startFrameSpinner;  //  @jve:decl-index=0:

	private JLabel endFrameLabel = null;

	private JSpinner endFrameSpinner;  //  @jve:decl-index=0:

	private JPanel actionPanel = null;

	private JButton runButton = null;

	private JMenuBar mainMenuBar = null;

	private JMenu fileMenu = null;

	private JProgressBar progressBar = null;

	private JButton applyButton = null;

	private JButton stopButton = null;

	private JPanel runPanel = null;

	private JPanel infoPanel = null;

	private JTextField infoTextField = null;

	private JMenuItem addSeriesMenuItem = null;

	private JScrollPane excludeRegionsScrollPane = null;

	private JMenu optionsMenu = null;

	private JMenuItem editOptionsMenuItem = null;
	
	private SparkMaster2D model;

	private JSpinner normalizationFrameSpinner;  //  @jve:decl-index=0:

	private JList imagesList = null;

	private JPopupMenu imageListPopupMenu = null;

	private JMenuItem removeSelectedMenuItem = null;

	private ButtonGroup normalizeRadioButtonGroup;  //  @jve:decl-index=0:

	private JPopupMenu excludeRegionsListPopupMenu = null;

	private JMenuItem removeSelectedRegionsMenuItem = null;

	private JLabel backgroundLabel = null;

	private JList backgroundRegionList = null;

	private JButton setBackgroundRegionButton = null;

	/**
	 * This is the default constructor
	 */
	public SparkMaster2DView() {
		super();
		initialize();
	}

	public SparkMaster2DView(SparkMaster2D model) {
		super();
		this.model = model;
		model.addObserver(this);
		initialize();
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(346, 632);
		this.setJMenuBar(getMainMenuBar());
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/icon.gif")));
		this.setContentPane(getJContentPane());
		this.setTitle("MRI  Spark Master 2D");
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
			jContentPane.setPreferredSize(new Dimension(148, 400));
			jContentPane.add(getImagesPanel(), BorderLayout.CENTER);
			jContentPane.add(getOptionsPanel(), BorderLayout.SOUTH);
			jContentPane.add(getInfoPanel(), BorderLayout.NORTH);
		}
		return jContentPane;
	}

	/**
	 * This method initializes imagesScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getImagesScrollPane() {
		if (imagesScrollPane == null) {
			imagesScrollPane = new JScrollPane();
			imagesScrollPane.setViewportView(getImagesList());
		}
		return imagesScrollPane;
	}

	/**
	 * This method initializes imagesPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getImagesPanel() {
		if (imagesPanel == null) {
			imagesPanel = new JPanel();
			imagesPanel.setLayout(new BorderLayout());
			imagesPanel.add(getImagesScrollPane(), BorderLayout.CENTER);
		}
		return imagesPanel;
	}

	/**
	 * This method initializes optionsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getOptionsPanel() {
		if (optionsPanel == null) {
			optionsPanel = new JPanel();
			optionsPanel.setLayout(null);
			optionsPanel.setPreferredSize(new Dimension(1, 350));
			optionsPanel.add(getNormalizationPanel(), null);
			optionsPanel.add(getRoiPanel(), null);
			optionsPanel.add(getActionPanel(), null);
		}
		return optionsPanel;
	}

	/**
	 * This method initializes setNormalizationFrameButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSetNormalizationFrameButton() {
		if (setNormalizationFrameButton == null) {
			setNormalizationFrameButton = new JButton();
			setNormalizationFrameButton.setText("set frame");
			setNormalizationFrameButton.setBounds(new Rectangle(211, 23, 97, 20));
			setNormalizationFrameButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							ImagePlus image = model.currentImage;
							if (image==null) return;
							model.normalizationFrame.setValue(image.getCurrentSlice());
							model.setUseProjection(false);
						}
					});
		}
		return setNormalizationFrameButton;
	}

	/**
	 * This method initializes normalizationRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getNormalizationRadioButton() {
		if (normalizationRadioButton == null) {
			normalizationRadioButton = new JRadioButton();
			normalizationRadioButton.setText("normalization frame:");
			normalizationRadioButton.setBounds(new Rectangle(6, 21, 142, 24));
			normalizationRadioButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.setUseProjection(false);
				}
			});
		}
		return normalizationRadioButton;
	}

	/**
	 * This method initializes normalizationWithProjectionRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getNormalizationWithProjectionRadioButton() {
		if (normalizationWithProjectionRadioButton == null) {
			normalizationWithProjectionRadioButton = new JRadioButton();
			normalizationWithProjectionRadioButton.setText("use average projection");
			normalizationWithProjectionRadioButton.setBounds(new Rectangle(6, 42, 155, 24));
			normalizationWithProjectionRadioButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							model.setUseProjection(true);
						}
					});
		}
		return normalizationWithProjectionRadioButton;
	}

	/**
	 * This method initializes normalizationPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getNormalizationPanel() {
		if (normalizationPanel == null) {
			backgroundLabel = new JLabel();
			backgroundLabel.setBounds(new Rectangle(9, 67, 148, 16));
			backgroundLabel.setText("background region:");
			normalizationPanel = new JPanel();
			normalizationPanel.setLayout(null);
			normalizationPanel.setBounds(new Rectangle(1, 0, 335, 115));
			normalizationPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "normalization", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
			normalizationPanel.add(getNormalizationFrameSpinner(), null);
			normalizationPanel.add(getSetNormalizationFrameButton(), null);
			normalizationPanel.add(getNormalizationRadioButton(), null);
			normalizationPanel.add(getNormalizationWithProjectionRadioButton(), null);
			normalizationPanel.add(backgroundLabel, null);
			normalizationPanel.add(getBackgroundRegionList(), null);
			normalizationPanel.add(getSetBackgroundRegionButton(), null);
			normalizeRadioButtonGroup = new ButtonGroup();
			normalizeRadioButtonGroup.add(normalizationRadioButton);
			normalizeRadioButtonGroup.add(normalizationWithProjectionRadioButton);
			if (model.useProjection) normalizationWithProjectionRadioButton.setSelected(true);
			else normalizationRadioButton.setSelected(true);
		}
		return normalizationPanel;
	}

	/**
	 * This method initializes roiPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getRoiPanel() {
		if (roiPanel == null) {
			endFrameLabel = new JLabel();
			endFrameLabel.setBounds(new Rectangle(164, 170, 67, 16));
			endFrameLabel.setText("end frame:");
			fromFrameLabel = new JLabel();
			fromFrameLabel.setBounds(new Rectangle(10, 170, 70, 16));
			fromFrameLabel.setText("start frame:");
			excludeRegionsLabel = new JLabel();
			excludeRegionsLabel.setBounds(new Rectangle(10, 49, 115, 16));
			excludeRegionsLabel.setText("exclude regions:");
			roiPanel = new JPanel();
			roiPanel.setLayout(null);
			roiPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "region of interest", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			roiPanel.setLocation(new Point(1, 114));
			roiPanel.setSize(new Dimension(335, 154));
			roiPanel.add(getRegionOfInterestList(), null);
			roiPanel.add(getRegionOfInterestButton(), null);
			roiPanel.add(excludeRegionsLabel, null);
			roiPanel.add(getAddExcludeRegionsButton(), null);
			roiPanel.add(fromFrameLabel, null);
			roiPanel.add(getStartFrameSpinner());
			roiPanel.add(endFrameLabel, null);
			roiPanel.add(getEndFrameSpinner());
			roiPanel.add(getExcludeRegionsScrollPane(), null);
		}
		return roiPanel;
	}

	/**
	 * This method initializes regionOfInterestList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getRegionOfInterestList() {
		if (regionOfInterestList == null) {
			regionOfInterestList = new JList(model.roi);
			regionOfInterestList.setLocation(new Point(10, 21));
			regionOfInterestList.setSize(new Dimension(192, 20));
			regionOfInterestList
					.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
						public void valueChanged(javax.swing.event.ListSelectionEvent e) {
							if (regionOfInterestList.getSelectedIndex()!=0) return;
							ImagePlus image = model.currentImage;
							image.setRoi(model.getRoi());
						}
					});
		}
		return regionOfInterestList;
	}

	/**
	 * This method initializes regionOfInterestButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getRegionOfInterestButton() {
		if (regionOfInterestButton == null) {
			regionOfInterestButton = new JButton();
			regionOfInterestButton.setLocation(new Point(211, 21));
			regionOfInterestButton.setText("set roi");
			regionOfInterestButton.setSize(new Dimension(97, 20));
			regionOfInterestButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ImagePlus image = model.currentImage;
					if (image==null) return;
					model.roi.removeAllElements();
					if (image.getRoi()!=null) model.roi.addElement(image.getRoi()); 
				}
			});
		}
		return regionOfInterestButton;
	}

	/**
	 * This method initializes excludeRegoinsList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getExcludeRegionsList() {
		if (excludeRegionsList == null) {
			excludeRegionsList = new JList(model.excludedRegions);
			excludeRegionsList
					.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
						public void valueChanged(javax.swing.event.ListSelectionEvent e) {
							ImagePlus image = model.currentImage;
							if (image==null) return;
							if (excludeRegionsList.getSelectedIndices().length>0)
								image.setRoi((Roi)excludeRegionsList.getSelectedValue());
						}
					});
			excludeRegionsList.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {
					if (e.getButton()==MouseEvent.BUTTON3) {
						JPopupMenu menu = getExcludeRegionsListPopupMenu();
						Point origin = getImagesList().getLocationOnScreen();
						menu.setLocation(e.getX() + origin.x, e.getY() + origin.y);
						menu.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			});
		}
		return excludeRegionsList;
	}

	/**
	 * This method initializes addExcludeRegionsButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAddExcludeRegionsButton() {
		if (addExcludeRegionsButton == null) {
			addExcludeRegionsButton = new JButton();
			addExcludeRegionsButton.setBounds(new Rectangle(211, 100, 97, 20));
			addExcludeRegionsButton.setText("add");
			addExcludeRegionsButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ImagePlus image = model.currentImage;
					if (image==null) return;
					if (image.getRoi()==null) return;
					model.excludedRegions.addElement(image.getRoi());
				}
			});
		}
		return addExcludeRegionsButton;
	}
	
	private JSpinner getStartFrameSpinner() {
		if (startFrameSpinner == null) {
			startFrameSpinner = new JSpinner(model.startFrame);
			startFrameSpinner.setPreferredSize(new java.awt.Dimension(102,22));
			startFrameSpinner.setName("startDateSpinner");
			startFrameSpinner.setBounds(84, 169, 56, 20);
		}
		return startFrameSpinner;
	}
	
	private JSpinner getEndFrameSpinner() {
		if (endFrameSpinner == null) {
			endFrameSpinner = new JSpinner(model.endFrame);
			endFrameSpinner.setPreferredSize(new java.awt.Dimension(102,22));
			endFrameSpinner.setName("startDateSpinner");
			endFrameSpinner.setBounds(237, 169, 56, 20);
		}
		return endFrameSpinner;
	}
	
	private JSpinner getNormalizationFrameSpinner() {
		if (normalizationFrameSpinner == null) {
			normalizationFrameSpinner = new JSpinner(model.normalizationFrame);
			normalizationFrameSpinner.setPreferredSize(new java.awt.Dimension(102,22));
			normalizationFrameSpinner.setName("startDateSpinner");
			normalizationFrameSpinner.setBounds(148, 23, 56, 20);
		}
		return normalizationFrameSpinner;
	}

	/**
	 * This method initializes actionPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getActionPanel() {
		if (actionPanel == null) {
			actionPanel = new JPanel();
			actionPanel.setLayout(null);
			actionPanel.setBounds(new Rectangle(1, 269, 335, 81));
			actionPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
			actionPanel.add(getStopButton(), null);
			actionPanel.add(getRunPanel(), null);
			actionPanel.add(getRunButton(), null);
		}
		return actionPanel;
	}

	/**
	 * This method initializes runButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getRunButton() {
		if (runButton == null) {
			runButton = new JButton();
			runButton.setText("run");
			runButton.setBounds(new Rectangle(137, 28, 66, 26));
			runButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.run();
				}
			});
		}
		return runButton;
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
			mainMenuBar.add(getOptionsMenu());
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
			fileMenu.add(getAddSeriesMenuItem());
		}
		return fileMenu;
	}

	/**
	 * This method initializes progressBar	
	 * 	
	 * @return javax.swing.JProgressBar	
	 */
	private JProgressBar getProgressBar() {
		if (progressBar == null) {
			progressBar = new JProgressBar();
		}
		return progressBar;
	}

	/**
	 * This method initializes applyButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getApplyButton() {
		if (applyButton == null) {
			applyButton = new JButton();
			applyButton.setText("apply");
			applyButton.setBounds(new Rectangle(22, 23, 64, 26));
			applyButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Object selectedValue = getImagesList().getSelectedValue();
					if (selectedValue==null) return;
					model.applyParametersTo(selectedValue.toString());
				}
			});
		}
		return applyButton;
	}

	/**
	 * This method initializes stopButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getStopButton() {
		if (stopButton == null) {
			stopButton = new JButton();
			stopButton.setBounds(new Rectangle(236, 29, 66, 24));
			stopButton.setText("stop");
			stopButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.stop();
				}
			});
		}
		return stopButton;
	}

	/**
	 * This method initializes runPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getRunPanel() {
		if (runPanel == null) {
			runPanel = new JPanel();
			runPanel.setLayout(null);
			runPanel.setBounds(new Rectangle(0, 4, 107, 74));
			runPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
			runPanel.add(getApplyButton(), null);
		}
		return runPanel;
	}

	/**
	 * This method initializes infoPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getInfoPanel() {
		if (infoPanel == null) {
			infoPanel = new JPanel();
			infoPanel.setLayout(new BorderLayout());
			infoPanel.add(getProgressBar(), BorderLayout.NORTH);
			infoPanel.add(getInfoTextField(), BorderLayout.SOUTH);
		}
		return infoPanel;
	}

	/**
	 * This method initializes infoTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getInfoTextField() {
		if (infoTextField == null) {
			infoTextField = new JTextField();
			infoTextField.setEditable(false);
		}
		return infoTextField;
	}

	/**
	 * This method initializes addSeriesMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getAddSeriesMenuItem() {
		if (addSeriesMenuItem == null) {
			addSeriesMenuItem = new JMenuItem();
			addSeriesMenuItem.setText("add series");
			addSeriesMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ListEditor listEditor = new ListEditor();
					listEditor.setModal(true);
					listEditor.show();
					model.addFiles(listEditor.getList());
				}
			});
		}
		return addSeriesMenuItem;
	}

	/**
	 * This method initializes excludeRegionsScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getExcludeRegionsScrollPane() {
		if (excludeRegionsScrollPane == null) {
			excludeRegionsScrollPane = new JScrollPane();
			excludeRegionsScrollPane.setBounds(new Rectangle(10, 65, 192, 79));
			excludeRegionsScrollPane.setViewportView(getExcludeRegionsList());
		}
		return excludeRegionsScrollPane;
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
			optionsMenu.add(getEditOptionsMenuItem());
		}
		return optionsMenu;
	}

	/**
	 * This method initializes editOptionsMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getEditOptionsMenuItem() {
		if (editOptionsMenuItem == null) {
			editOptionsMenuItem = new JMenuItem();
			editOptionsMenuItem.setText("edit options");
			editOptionsMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.showOptions();
				}
			});
		}
		return editOptionsMenuItem;
	}

	public void update(Observable sender, Object aspect) {
		if (aspect.equals("use projection")) this.handleUseProjectionChanged();
		if (aspect.equals("progress min")) this.handleProgressMinChanged();
		if (aspect.equals("progress max")) this.handleProgressMaxChanged();
		if (aspect.equals("progress")) this.handleProgressChanged();
		if (aspect.equals("message")) this.handleMessageChanged();
	}

	private void handleMessageChanged() {
		getInfoTextField().setText(model.message);
	}

	private void handleProgressChanged() {
		getProgressBar().setValue(model.progress);
	}

	private void handleProgressMaxChanged() {
		getProgressBar().setMaximum(model.progressMax);
	}

	private void handleProgressMinChanged() {
		getProgressBar().setMinimum(model.progressMin);
	}

	private void handleUseProjectionChanged() {
		if (model.useProjection) normalizationWithProjectionRadioButton.setSelected(true);
		else normalizationRadioButton.setSelected(true);
	}

	/**
	 * This method initializes imagesList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getImagesList() {
		if (imagesList == null) {
			imagesList = new JList(model.getImages());
			imagesList.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {
					if (e.getButton()==MouseEvent.BUTTON3) {
						JPopupMenu menu = getImageListPopupMenu();
						Point origin = getImagesList().getLocationOnScreen();
						menu.setLocation(e.getX() + origin.x, e.getY() + origin.y);
						menu.show(e.getComponent(), e.getX(), e.getY());
					}
					if (e.getButton()==MouseEvent.BUTTON1 && e.getClickCount()==2) {
						model.openImage((String)getImagesList().getSelectedValue());
					}
				}
			});
		}
		return imagesList;
	}

	/**
	 * This method initializes imageListPopupMenu	
	 * 	
	 * @return javax.swing.JPopupMenu	
	 */
	private JPopupMenu getImageListPopupMenu() {
		if (imageListPopupMenu == null) {
			imageListPopupMenu = new JPopupMenu();
			imageListPopupMenu.add(getRemoveSelectedMenuItem());
		}
		return imageListPopupMenu;
	}

	/**
	 * This method initializes removeSelectedMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getRemoveSelectedMenuItem() {
		if (removeSelectedMenuItem == null) {
			removeSelectedMenuItem = new JMenuItem();
			removeSelectedMenuItem.setText("remove selected");
			removeSelectedMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.removeImages(getImagesList().getSelectedValues());
				}
			});
		}
		return removeSelectedMenuItem;
	}

	/**
	 * This method initializes excludeRegionsListPopupMenu	
	 * 	
	 * @return javax.swing.JPopupMenu	
	 */
	private JPopupMenu getExcludeRegionsListPopupMenu() {
		if (excludeRegionsListPopupMenu == null) {
			excludeRegionsListPopupMenu = new JPopupMenu();
			excludeRegionsListPopupMenu.add(getRemoveSelectedRegionsMenuItem());
		}
		return excludeRegionsListPopupMenu;
	}

	/**
	 * This method initializes removeSelectedRegionsMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getRemoveSelectedRegionsMenuItem() {
		if (removeSelectedRegionsMenuItem == null) {
			removeSelectedRegionsMenuItem = new JMenuItem();
			removeSelectedRegionsMenuItem.setText("remove selected");
			removeSelectedRegionsMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							Object[] regions = excludeRegionsList.getSelectedValues();
							for (int i=0; i<regions.length;i++) {
								model.excludedRegions.removeElement(regions[i]);
							}
						}
					});
		}
		return removeSelectedRegionsMenuItem;
	}

	/**
	 * This method initializes backgroundRegionList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getBackgroundRegionList() {
		if (backgroundRegionList == null) {
			backgroundRegionList = new JList(model.background);
			backgroundRegionList.setLocation(new Point(10, 89));
			backgroundRegionList.setSize(new Dimension(192, 20));
			backgroundRegionList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
				public void valueChanged(javax.swing.event.ListSelectionEvent e) {
					if (backgroundRegionList.getSelectedIndex()!=0) return;
					ImagePlus image = model.currentImage;
					image.setRoi(model.getBackground());
				}
			});
		}
		return backgroundRegionList;
	}

	/**
	 * This method initializes setBackgroundRegionButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSetBackgroundRegionButton() {
		if (setBackgroundRegionButton == null) {
			setBackgroundRegionButton = new JButton();
			setBackgroundRegionButton.setBounds(new Rectangle(211, 89, 97, 20));
			setBackgroundRegionButton.setText("set");
			setBackgroundRegionButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							ImagePlus image = model.currentImage;
							if (image==null) return;
							model.background.removeAllElements();
							if (image.getRoi()!=null) model.background.addElement(image.getRoi()); 
						}
					});
		}
		return setBackgroundRegionButton;
	}

}  //  @jve:decl-index=0:visual-constraint="8,-34"
