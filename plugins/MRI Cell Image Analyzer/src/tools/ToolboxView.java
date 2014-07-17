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
package tools;

import ij.IJ;
import ij.plugin.MacroInstaller;

import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.ImageIcon;
import java.awt.FlowLayout;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.Toolkit;

/**
 * The toolbox view has an options and a color menu and a number of buttons
 * that either run a tool or select the active tool.
 * 
 * @author	Volker Baecker
 **/
public class ToolboxView extends javax.swing.JFrame {

	private static final long serialVersionUID = -9040679787662579458L;
	private javax.swing.JPanel ivjJFrameContentPane = null;
	protected Toolbox model;
	private JButton rectangularSelectionButton = null;
	private JButton ovalSelectionButton = null;
	private JButton polygonSelectionButton = null;
	private JToolBar areaSelectionToolBar = null;
	private JButton freehandSelectionButton = null;
	private JToolBar lineSelectionToolBar = null;
	private JButton straightLineSelectionButton = null;
	private JButton freehandLineSelectionButton = null;
	private JButton segmentedLineSelectionButton = null;
	private JButton angleSelectionButton = null;
	private JToolBar measureToolBar = null;
	private JButton pointSelectionButton = null;
	private JButton magicWandButton = null;
	private JButton textButton = null;
	private JButton measureButton = null;
	private JToolBar magnifyToolBar = null;
	private JButton magnifyButton = null;
	private JButton scrollButton = null;
	private JButton colorPickerButton = null;
	private JButton jButton15 = null;
	private JPanel lineSelectionPanel = null;
	private JPanel areaSelectionPanel = null;
	private JPanel measurePanel = null;
	private JPanel magniftPanel = null;
	private JMenuBar jJMenuBar = null;
	private JMenu optionsMenu = null;
	private JMenuItem lineMenuItem = null;
	private JMenuItem pointMenuItem = null;
	private JMenuItem measurementsMenuItem = null;
	private JMenuItem textMenuItem = null;
	private JMenu colorMenu = null;
	private JMenuItem selectColorsMenuItem = null;
	private JPanel manipulateSelectionPanel = null;
	private JToolBar manipulateSelectionToolBar = null;
	private JButton selectAllButton = null;
	private JButton selectNoneButton = null;
	private JButton inverseSelectionButton = null;
	private JButton restoreSelectionButton = null;
	private JPanel modifySelectionPanel = null;
	private JToolBar modifySelectionToolBar = null;
	private JButton createMaskFromSelectionButton = null;
	private JButton convexHullButton = null;
	private JButton makeSelectionOvalButton = null;
	private JButton makeSplineButton = null;
	private JPanel copyCutPastePanel = null;
	private JToolBar copyCutPasteToolBar = null;
	private JButton duplicateButton = null;
	private JButton cropButton = null;
	private JButton analyzeParticlesButton = null;
	private JButton copyButton = null;
	private JToolBar setScaleToolBar = null;
	private JPanel setScalePanel = null;
	private JButton setScaleButton = null;
	private JButton pasteScaleButton = null;
	private JButton cutLastPolygonElementButton = null;
	private JButton pasteButton = null;
	private JMenuItem pasteMenuItem = null;

	private JPanel magicSelectionPanel = null;

	private JToolBar magicSelectionToolBar = null;

	private JButton liveWire1dButton = null;

	private JButton liveWire2dButton = null;

	private JButton distributionButton = null;

	private JButton labelButton = null;

	private JPanel lutPanel = null;

	private JToolBar lutToolBar = null;

	private JButton redButton = null;

	private JButton greenButton = null;

	private JButton blueButton = null;

	private JButton hiloButton = null;
	
	public ToolboxView(Toolbox toolbox) {
		super();
		this.model = toolbox;
		initialize();
	}

	/**
	 * Return the JFrameContentPane property value.
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJFrameContentPane() {
		if (ivjJFrameContentPane == null) {
			FlowLayout flowLayout2 = new FlowLayout();
			ivjJFrameContentPane = new javax.swing.JPanel();
			ivjJFrameContentPane.setLayout(flowLayout2);
			ivjJFrameContentPane.setName("JFrameContentPane");
			ivjJFrameContentPane.setMinimumSize(new java.awt.Dimension(3,3));
			flowLayout2.setAlignment(java.awt.FlowLayout.LEFT);
			flowLayout2.setHgap(1);
			flowLayout2.setVgap(1);
			ivjJFrameContentPane.add(getAreaSelectionPanel(), null);
			ivjJFrameContentPane.add(getLineSelectionPanel(), null);
			ivjJFrameContentPane.add(getMagicSelectionPanel(), null);
			ivjJFrameContentPane.add(getMeasurePanel(), null);
			ivjJFrameContentPane.add(getMagniftPanel(), null);
			ivjJFrameContentPane.add(getManipulateSelectionPanel(), null);
			ivjJFrameContentPane.add(getModifySelectionPanel(), null);
			ivjJFrameContentPane.add(getCopyCutPastePanel(), null);
			ivjJFrameContentPane.add(getSetScalePanel(), null);
			ivjJFrameContentPane.add(getLutPanel(), null);
		}
		return ivjJFrameContentPane;
	}

	private void initialize() {
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/icon.gif")));
		this.setJMenuBar(getJJMenuBar());
		this.setMinimumSize(new java.awt.Dimension(3,3));
		this.setName("JFrame1");
		this
				.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		this.setBounds(45, 25, 199, 364);
		this.setTitle("Tools");
		this.setContentPane(getJFrameContentPane());

	}
	
	/**
	 * Enable all buttons except for pressed button.	
	 */   
	protected void enableButtons(JButton pressedButton) {
		rectangularSelectionButton.setEnabled(true);
		ovalSelectionButton.setEnabled(true);
		polygonSelectionButton.setEnabled(true);
		freehandSelectionButton.setEnabled(true);
		straightLineSelectionButton.setEnabled(true);
		freehandLineSelectionButton.setEnabled(true);
		segmentedLineSelectionButton.setEnabled(true);
		angleSelectionButton.setEnabled(true);
		pointSelectionButton.setEnabled(true);
		magicWandButton.setEnabled(true);
		textButton.setEnabled(true);
		magnifyButton.setEnabled(true);
		scrollButton.setEnabled(true);
		colorPickerButton.setEnabled(true);
		liveWire1dButton.setEnabled(true);
		liveWire2dButton.setEnabled(true);
		pressedButton.setEnabled(false);
	}
	
	/**
	 * This method initializes rectangularSelectionButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getRectangularSelectionButton() {
		if (rectangularSelectionButton == null) {
			rectangularSelectionButton = new JButton();
			rectangularSelectionButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/roi-rect.gif")));
			rectangularSelectionButton.setMaximumSize(new java.awt.Dimension(33,33));
			rectangularSelectionButton.setMinimumSize(new java.awt.Dimension(33,33));
			rectangularSelectionButton.setPreferredSize(new java.awt.Dimension(33,33));
			rectangularSelectionButton.setToolTipText("Rectangular selections");
			rectangularSelectionButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.setRectangularSelectionTool();
					enableButtons(rectangularSelectionButton);
				}
			});
		}
		return rectangularSelectionButton;
	}
	
	/**
	 * This method initializes ovalSelectionButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getOvalSelectionButton() {
		if (ovalSelectionButton == null) {
			ovalSelectionButton = new JButton();
			ovalSelectionButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/roi-oval.gif")));
			ovalSelectionButton.setToolTipText("Oval selections");
			ovalSelectionButton.setPreferredSize(new java.awt.Dimension(33,33));
			ovalSelectionButton.setMinimumSize(new java.awt.Dimension(33,33));
			ovalSelectionButton.setMaximumSize(new java.awt.Dimension(33,33));
			ovalSelectionButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.setOvalSelectionTool();
					enableButtons(ovalSelectionButton);
				}
			});
		}
		return ovalSelectionButton;
	}
	
	/**
	 * This method initializes polygonSelectionButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getPolygonSelectionButton() {
		if (polygonSelectionButton == null) {
			polygonSelectionButton = new JButton();
			polygonSelectionButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/roi-polygon.gif")));
			polygonSelectionButton.setToolTipText("Polygon selections");
			polygonSelectionButton.setMaximumSize(new java.awt.Dimension(33,33));
			polygonSelectionButton.setMinimumSize(new java.awt.Dimension(33,33));
			polygonSelectionButton.setPreferredSize(new java.awt.Dimension(33,33));
			polygonSelectionButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.setPolygonSelectionTool();
					enableButtons(polygonSelectionButton);
				}
			});
		}
		return polygonSelectionButton;
	}
	
	/**
	 * This method initializes freehandSelectionButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getFreehandSelectionButton() {
		if (freehandSelectionButton == null) {
			freehandSelectionButton = new JButton();
			freehandSelectionButton.setPreferredSize(new java.awt.Dimension(33,33));
			freehandSelectionButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/roi-free.gif")));
			freehandSelectionButton.setMaximumSize(new java.awt.Dimension(33,33));
			freehandSelectionButton.setMinimumSize(new java.awt.Dimension(33,33));
			freehandSelectionButton.setToolTipText("Freehand selections");
			freehandSelectionButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.setFreehandSelectionTool();
					enableButtons(freehandSelectionButton);
				}
			});
		}
		return freehandSelectionButton;
	}
	
	/**
	 * This method initializes areaSelectionToolBar	
	 * 	
	 * @return javax.swing.JToolBar	
	 */    
	private JToolBar getAreaSelectionToolBar() {
		if (areaSelectionToolBar == null) {
			areaSelectionToolBar = new JToolBar();
			areaSelectionToolBar.setOrientation(javax.swing.JToolBar.VERTICAL);
			areaSelectionToolBar.add(getRectangularSelectionButton());
			areaSelectionToolBar.add(getOvalSelectionButton());
			areaSelectionToolBar.add(getPolygonSelectionButton());
			areaSelectionToolBar.add(getFreehandSelectionButton());
		}
		return areaSelectionToolBar;
	}
	
	/**
	 * This method initializes lineSelectionToolBar	
	 * 	
	 * @return javax.swing.JToolBar	
	 */    
	private JToolBar getLineSelectionToolBar() {
		if (lineSelectionToolBar == null) {
			lineSelectionToolBar = new JToolBar();
			lineSelectionToolBar.setOrientation(javax.swing.JToolBar.VERTICAL);
			lineSelectionToolBar.add(getStraightLineSelectionButton());
			lineSelectionToolBar.add(getSegmentedLineSelectionButton());
			lineSelectionToolBar.add(getFreehandLineSelectionButton());
			lineSelectionToolBar.add(getAngleSelectionButton());
		}
		return lineSelectionToolBar;
	}
	/**
	 * This method initializes straightLineSelectionButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getStraightLineSelectionButton() {
		if (straightLineSelectionButton == null) {
			straightLineSelectionButton = new JButton();
			straightLineSelectionButton.setPreferredSize(new java.awt.Dimension(33,33));
			straightLineSelectionButton.setMaximumSize(new java.awt.Dimension(33,33));
			straightLineSelectionButton.setMinimumSize(new java.awt.Dimension(33,33));
			straightLineSelectionButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/roi-line.gif")));
			straightLineSelectionButton.setVerticalAlignment(javax.swing.SwingConstants.TOP);
			straightLineSelectionButton.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
			straightLineSelectionButton.setToolTipText("Straight line selections");
			straightLineSelectionButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.setStraightLineSelectionTool();
					enableButtons(straightLineSelectionButton);
				}
			});
		}
		return straightLineSelectionButton;
	}
	/**
	 * This method initializes freehandLineSelectionButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getFreehandLineSelectionButton() {
		if (freehandLineSelectionButton == null) {
			freehandLineSelectionButton = new JButton();
			freehandLineSelectionButton.setMaximumSize(new java.awt.Dimension(33,33));
			freehandLineSelectionButton.setMinimumSize(new java.awt.Dimension(33,33));
			freehandLineSelectionButton.setPreferredSize(new java.awt.Dimension(33,33));
			freehandLineSelectionButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/roi-free-line.gif")));
			freehandLineSelectionButton.setToolTipText("Freehand line selections");
			freehandLineSelectionButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.setFreehandLineSelectionTool();
					enableButtons(freehandLineSelectionButton);
				}
			});
		}
		return freehandLineSelectionButton;
	}
	/**
	 * This method initializes segmentedLineSelectionButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getSegmentedLineSelectionButton() {
		if (segmentedLineSelectionButton == null) {
			segmentedLineSelectionButton = new JButton();
			segmentedLineSelectionButton.setMaximumSize(new java.awt.Dimension(33,33));
			segmentedLineSelectionButton.setMinimumSize(new java.awt.Dimension(33,33));
			segmentedLineSelectionButton.setPreferredSize(new java.awt.Dimension(33,33));
			segmentedLineSelectionButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/roi-polygon-line.gif")));
			segmentedLineSelectionButton.setToolTipText("Segmented line selections");
			segmentedLineSelectionButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.setSegmentedLineSelectionTool();
					enableButtons(segmentedLineSelectionButton);
				}
			});
		}
		return segmentedLineSelectionButton;
	}
	/**
	 * This method initializes angleSelectionButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getAngleSelectionButton() {
		if (angleSelectionButton == null) {
			angleSelectionButton = new JButton();
			angleSelectionButton.setMaximumSize(new java.awt.Dimension(33,33));
			angleSelectionButton.setMinimumSize(new java.awt.Dimension(33,33));
			angleSelectionButton.setPreferredSize(new java.awt.Dimension(33,33));
			angleSelectionButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/roi-angle.gif")));
			angleSelectionButton.setToolTipText("Angle tool");
			angleSelectionButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.setAngleSelectionTool();
					enableButtons(angleSelectionButton);
				}
			});
		}
		return angleSelectionButton;
	}
	/**
	 * This method initializes measureToolBar	
	 * 	
	 * @return javax.swing.JToolBar	
	 */    
	private JToolBar getMeasureToolBar() {
		if (measureToolBar == null) {
			measureToolBar = new JToolBar();
			measureToolBar.setAlignmentX(0.0F);
			measureToolBar.setAlignmentY(0.5F);
			measureToolBar.setOrientation(javax.swing.JToolBar.VERTICAL);
			measureToolBar.add(getAnalyzeParticlesButton());
			measureToolBar.add(getDistributionButton());
			measureToolBar.add(getLabelButton());
			measureToolBar.add(getMeasureButton());
		}
		return measureToolBar;
	}
	/**
	 * This method initializes pointSelectionButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getPointSelectionButton() {
		if (pointSelectionButton == null) {
			pointSelectionButton = new JButton();
			pointSelectionButton.setMaximumSize(new java.awt.Dimension(33,33));
			pointSelectionButton.setMinimumSize(new java.awt.Dimension(33,33));
			pointSelectionButton.setPreferredSize(new java.awt.Dimension(33,33));
			pointSelectionButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/roi-point.gif")));
			pointSelectionButton.setToolTipText("Point selections");
			pointSelectionButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.setPointSelectionTool();
					enableButtons(pointSelectionButton);
				}
			});
		}
		return pointSelectionButton;
	}
	/**
	 * This method initializes magicWandButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getMagicWandButton() {
		if (magicWandButton == null) {
			magicWandButton = new JButton();
			magicWandButton.setMaximumSize(new java.awt.Dimension(33,33));
			magicWandButton.setMinimumSize(new java.awt.Dimension(33,33));
			magicWandButton.setPreferredSize(new java.awt.Dimension(33,33));
			magicWandButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/roi-wand.gif")));
			magicWandButton.setToolTipText("Wand (tracing) tool");
			magicWandButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.setMagicWandSelectionTool();
					enableButtons(magicWandButton);
				}
			});
		}
		return magicWandButton;
	}
	/**
	 * This method initializes textButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getTextButton() {
		if (textButton == null) {
			textButton = new JButton();
			textButton.setMaximumSize(new java.awt.Dimension(33,33));
			textButton.setMinimumSize(new java.awt.Dimension(33,33));
			textButton.setPreferredSize(new java.awt.Dimension(33,33));
			textButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/text-tool.gif")));
			textButton.setToolTipText("Text tool");
			textButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.setTextTool();
					enableButtons(textButton);
				}
			});
		}
		return textButton;
	}
	/**
	 * This method initializes measureButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getMeasureButton() {
		if (measureButton == null) {
			measureButton = new JButton();
			measureButton.setMaximumSize(new java.awt.Dimension(33,33));
			measureButton.setMinimumSize(new java.awt.Dimension(33,33));
			measureButton.setPreferredSize(new java.awt.Dimension(33,33));
			measureButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/measure-tool.gif")));
			measureButton.setToolTipText("Measure");
			measureButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.measure();
				}
			});
		}
		return measureButton;
	}
	/**
	 * This method initializes magnifyToolBar	
	 * 	
	 * @return javax.swing.JToolBar	
	 */    
	private JToolBar getMagnifyToolBar() {
		if (magnifyToolBar == null) {
			magnifyToolBar = new JToolBar();
			magnifyToolBar.setOrientation(javax.swing.JToolBar.VERTICAL);
			magnifyToolBar.setAlignmentY(0.5F);
			magnifyToolBar.add(getMagnifyButton());
			magnifyToolBar.add(getScrollButton());
			magnifyToolBar.add(getColorPickerButton());
			magnifyToolBar.add(getTextButton());
		}
		return magnifyToolBar;
	}
	/**
	 * This method initializes magnifyButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getMagnifyButton() {
		if (magnifyButton == null) {
			magnifyButton = new JButton();
			magnifyButton.setPreferredSize(new java.awt.Dimension(33,33));
			magnifyButton.setMaximumSize(new java.awt.Dimension(33,33));
			magnifyButton.setMinimumSize(new java.awt.Dimension(33,33));
			magnifyButton.setToolTipText("Magnifying glass");
			magnifyButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/magnify-tool.gif")));
			magnifyButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.setMagnifyTool();
					enableButtons(magnifyButton);
				}
			});
		}
		return magnifyButton;
	}
	/**
	 * This method initializes scrollButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getScrollButton() {
		if (scrollButton == null) {
			scrollButton = new JButton();
			scrollButton.setMaximumSize(new java.awt.Dimension(33,33));
			scrollButton.setMinimumSize(new java.awt.Dimension(33,33));
			scrollButton.setPreferredSize(new java.awt.Dimension(33,33));
			scrollButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/scroll-tool.gif")));
			scrollButton.setToolTipText("Scrolling tool");
			scrollButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.setScrollTool();
					enableButtons(scrollButton);
				}
			});
		}
		return scrollButton;
	}
	/**
	 * This method initializes colorPickerButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getColorPickerButton() {
		if (colorPickerButton == null) {
			colorPickerButton = new JButton();
			colorPickerButton.setMaximumSize(new java.awt.Dimension(33,33));
			colorPickerButton.setMinimumSize(new java.awt.Dimension(33,33));
			colorPickerButton.setPreferredSize(new java.awt.Dimension(33,33));
			colorPickerButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/color-picker-tool.gif")));
			colorPickerButton.setToolTipText("Color picker");
			colorPickerButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.setColorPickerTool();
					enableButtons(colorPickerButton);
				}
			});
		}
		return colorPickerButton;
	}
	/**
	 * This method initializes jButton15	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton15() {
		if (jButton15 == null) {
			jButton15 = new JButton();
			jButton15.setMaximumSize(new java.awt.Dimension(33,33));
			jButton15.setMinimumSize(new java.awt.Dimension(33,33));
			jButton15.setPreferredSize(new java.awt.Dimension(33,33));
			jButton15.setText("?");
			jButton15.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 14));
			jButton15.setToolTipText("Help");
			jButton15.addActionListener(new java.awt.event.ActionListener() {   
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.openHelp();
				} 
			
			});
		}
		return jButton15;
	}
	/**
	 * This method initializes lineSelectionPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getLineSelectionPanel() {
		if (lineSelectionPanel == null) {
			lineSelectionPanel = new JPanel();
			lineSelectionPanel.setLayout(new BorderLayout());
			lineSelectionPanel.add(getLineSelectionToolBar(), java.awt.BorderLayout.WEST);
		}
		return lineSelectionPanel;
	}
	/**
	 * This method initializes areaSelectionPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getAreaSelectionPanel() {
		if (areaSelectionPanel == null) {
			areaSelectionPanel = new JPanel();
			areaSelectionPanel.setLayout(new BorderLayout());
			areaSelectionPanel.add(getAreaSelectionToolBar(), java.awt.BorderLayout.WEST);
		}
		return areaSelectionPanel;
	}
	/**
	 * This method initializes measurePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getMeasurePanel() {
		if (measurePanel == null) {
			measurePanel = new JPanel();
			measurePanel.setLayout(new BorderLayout());
			measurePanel.add(getMeasureToolBar(), java.awt.BorderLayout.WEST);
		}
		return measurePanel;
	}
	/**
	 * This method initializes magniftPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getMagniftPanel() {
		if (magniftPanel == null) {
			magniftPanel = new JPanel();
			magniftPanel.setLayout(new BorderLayout());
			magniftPanel.add(getMagnifyToolBar(), java.awt.BorderLayout.WEST);
		}
		return magniftPanel;
	}
	/**
	 * This method initializes jJMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */    
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getOptionsMenu());
			jJMenuBar.add(getColorMenu());
		}
		return jJMenuBar;
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
			optionsMenu.addSeparator();
			optionsMenu.add(getLineMenuItem());
			optionsMenu.add(getPointMenuItem());
			optionsMenu.add(getTextMenuItem());
			optionsMenu.add(getPasteMenuItem());
		}
		return optionsMenu;
	}
	/**
	 * This method initializes lineMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getLineMenuItem() {
		if (lineMenuItem == null) {
			lineMenuItem = new JMenuItem();
			lineMenuItem.setText("Line");
			lineMenuItem.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.adjustLineWidth();
				}
			});
		}
		return lineMenuItem;
	}
	/**
	 * This method initializes pointMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getPointMenuItem() {
		if (pointMenuItem == null) {
			pointMenuItem = new JMenuItem();
			pointMenuItem.setText("Point");
			pointMenuItem.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.adjustPointSize();
				}
			});
		}
		return pointMenuItem;
	}
	/**
	 * This method initializes measurementsMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getMeasurementsMenuItem() {
		if (measurementsMenuItem == null) {
			measurementsMenuItem = new JMenuItem();
			measurementsMenuItem.setText("Measurements");
			measurementsMenuItem.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.setMeasureOptions();
				}
			});
		}
		return measurementsMenuItem;
	}
	/**
	 * This method initializes textMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getTextMenuItem() {
		if (textMenuItem == null) {
			textMenuItem = new JMenuItem();
			textMenuItem.setText("Text");
			textMenuItem.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.setTextOptions();
				}
			});
		}
		return textMenuItem;
	}
	/**
	 * This method initializes colorMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	private JMenu getColorMenu() {
		if (colorMenu == null) {
			colorMenu = new JMenu();
			colorMenu.setText("Color");
			colorMenu.add(getSelectColorsMenuItem());
		}
		return colorMenu;
	}
	/**
	 * This method initializes selectColorsMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getSelectColorsMenuItem() {
		if (selectColorsMenuItem == null) {
			selectColorsMenuItem = new JMenuItem();
			selectColorsMenuItem.setText("Select foreground / background color");
			selectColorsMenuItem.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.selectColors();
				}
			});
		}
		return selectColorsMenuItem;
	}
	/**
	 * This method initializes manipulateSelectionPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getManipulateSelectionPanel() {
		if (manipulateSelectionPanel == null) {
			manipulateSelectionPanel = new JPanel();
			manipulateSelectionPanel.setLayout(new BorderLayout());
			manipulateSelectionPanel.add(getManipulateSelectionToolBar(), java.awt.BorderLayout.NORTH);
		}
		return manipulateSelectionPanel;
	}
	/**
	 * This method initializes manipulateSelectionToolBar	
	 * 	
	 * @return javax.swing.JToolBar	
	 */    
	private JToolBar getManipulateSelectionToolBar() {
		if (manipulateSelectionToolBar == null) {
			manipulateSelectionToolBar = new JToolBar();
			manipulateSelectionToolBar.setOrientation(javax.swing.JToolBar.VERTICAL);
			manipulateSelectionToolBar.add(getSelectAllButton());
			manipulateSelectionToolBar.add(getSelectNoneButton());
			manipulateSelectionToolBar.add(getInverseSelectionButton());
			manipulateSelectionToolBar.add(getRestoreSelectionButton());
		}
		return manipulateSelectionToolBar;
	}
	/**
	 * This method initializes selectAllButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getSelectAllButton() {
		if (selectAllButton == null) {
			selectAllButton = new JButton();
			selectAllButton.setPreferredSize(new java.awt.Dimension(33,33));
			selectAllButton.setMaximumSize(new java.awt.Dimension(33,33));
			selectAllButton.setMinimumSize(new java.awt.Dimension(33,33));
			selectAllButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/roi-all.gif")));
			selectAllButton.setToolTipText("Select all");
			selectAllButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.selectAll();
				}
			});
		}
		return selectAllButton;
	}
	/**
	 * This method initializes selectNoneButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getSelectNoneButton() {
		if (selectNoneButton == null) {
			selectNoneButton = new JButton();
			selectNoneButton.setPreferredSize(new java.awt.Dimension(33,33));
			selectNoneButton.setMaximumSize(new java.awt.Dimension(33,33));
			selectNoneButton.setMinimumSize(new java.awt.Dimension(33,33));
			selectNoneButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/roi-none.gif")));
			selectNoneButton.setToolTipText("Select none");
			selectNoneButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.selectNone();
				}
			});
		}
		return selectNoneButton;
	}
	/**
	 * This method initializes inverseSelectionButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getInverseSelectionButton() {
		if (inverseSelectionButton == null) {
			inverseSelectionButton = new JButton();
			inverseSelectionButton.setPreferredSize(new java.awt.Dimension(33,33));
			inverseSelectionButton.setMaximumSize(new java.awt.Dimension(33,33));
			inverseSelectionButton.setMinimumSize(new java.awt.Dimension(33,33));
			inverseSelectionButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/roi-inverse.gif")));
			inverseSelectionButton.setToolTipText("Inverse selection");
			inverseSelectionButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.inverseSelection();
				}
			});
		}
		return inverseSelectionButton;
	}
	/**
	 * This method initializes restoreSelectionButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getRestoreSelectionButton() {
		if (restoreSelectionButton == null) {
			restoreSelectionButton = new JButton();
			restoreSelectionButton.setPreferredSize(new java.awt.Dimension(33,33));
			restoreSelectionButton.setMaximumSize(new java.awt.Dimension(33,33));
			restoreSelectionButton.setMinimumSize(new java.awt.Dimension(33,33));
			restoreSelectionButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/roi-restore.gif")));
			restoreSelectionButton.setToolTipText("Restore selection or copy it to another image");
			restoreSelectionButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.restoreSelection();
				}
			});
		}
		return restoreSelectionButton;
	}
	/**
	 * This method initializes modifySelectionPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getModifySelectionPanel() {
		if (modifySelectionPanel == null) {
			modifySelectionPanel = new JPanel();
			modifySelectionPanel.setLayout(new BorderLayout());
			modifySelectionPanel.add(getModifySelectionToolBar(), java.awt.BorderLayout.NORTH);
		}
		return modifySelectionPanel;
	}
	/**
	 * This method initializes modifySelectionToolBar	
	 * 	
	 * @return javax.swing.JToolBar	
	 */    
	private JToolBar getModifySelectionToolBar() {
		if (modifySelectionToolBar == null) {
			modifySelectionToolBar = new JToolBar();
			modifySelectionToolBar.setOrientation(javax.swing.JToolBar.VERTICAL);
			modifySelectionToolBar.add(getCreateMaskFromSelectionButton());
			modifySelectionToolBar.add(getConvexHullButton());
			modifySelectionToolBar.add(getMakeSelectionOvalButton());
			modifySelectionToolBar.add(getMakeSplineButton());
		}
		return modifySelectionToolBar;
	}
	/**
	 * This method initializes createMaskFromSelectionButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getCreateMaskFromSelectionButton() {
		if (createMaskFromSelectionButton == null) {
			createMaskFromSelectionButton = new JButton();
			createMaskFromSelectionButton.setPreferredSize(new java.awt.Dimension(33,33));
			createMaskFromSelectionButton.setMaximumSize(new java.awt.Dimension(33,33));
			createMaskFromSelectionButton.setMinimumSize(new java.awt.Dimension(33,33));
			createMaskFromSelectionButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/roi-toMask.gif")));
			createMaskFromSelectionButton.setToolTipText("Create mask from selection");
			createMaskFromSelectionButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.createMaskFromSelection();
				}
			});
		}
		return createMaskFromSelectionButton;
	}
	/**
	 * This method initializes convexHullButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getConvexHullButton() {
		if (convexHullButton == null) {
			convexHullButton = new JButton();
			convexHullButton.setMaximumSize(new java.awt.Dimension(33,33));
			convexHullButton.setMinimumSize(new java.awt.Dimension(33,33));
			convexHullButton.setPreferredSize(new java.awt.Dimension(33,33));
			convexHullButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/roi-hull.gif")));
			convexHullButton.setToolTipText("Convex hull of selection");
			convexHullButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.convexHullOfSelection();
				}
			});
		}
		return convexHullButton;
	}
	/**
	 * This method initializes makeSelectionOvalButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getMakeSelectionOvalButton() {
		if (makeSelectionOvalButton == null) {
			makeSelectionOvalButton = new JButton();
			makeSelectionOvalButton.setMaximumSize(new java.awt.Dimension(33,33));
			makeSelectionOvalButton.setMinimumSize(new java.awt.Dimension(33,33));
			makeSelectionOvalButton.setPreferredSize(new java.awt.Dimension(33,33));
			makeSelectionOvalButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/roi-toOval.gif")));
			makeSelectionOvalButton.setToolTipText("Make selection oval");
			makeSelectionOvalButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.makeSelectionOval();
				}
			});
		}
		return makeSelectionOvalButton;
	}
	/**
	 * This method initializes makeSplineButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getMakeSplineButton() {
		if (makeSplineButton == null) {
			makeSplineButton = new JButton();
			makeSplineButton.setMaximumSize(new java.awt.Dimension(33,33));
			makeSplineButton.setMinimumSize(new java.awt.Dimension(33,33));
			makeSplineButton.setPreferredSize(new java.awt.Dimension(33,33));
			makeSplineButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/roi-toSpline.gif")));
			makeSplineButton.setToolTipText("Make selection a spline");
			makeSplineButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.makeSpline();
				}
			});
		}
		return makeSplineButton;
	}
	/**
	 * This method initializes copyCutPastePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getCopyCutPastePanel() {
		if (copyCutPastePanel == null) {
			copyCutPastePanel = new JPanel();
			copyCutPastePanel.setLayout(new BorderLayout());
			copyCutPastePanel.add(getCopyCutPasteToolBar(), java.awt.BorderLayout.WEST);
		}
		return copyCutPastePanel;
	}
	/**
	 * This method initializes copyCutPasteToolBar	
	 * 	
	 * @return javax.swing.JToolBar	
	 */    
	private JToolBar getCopyCutPasteToolBar() {
		if (copyCutPasteToolBar == null) {
			copyCutPasteToolBar = new JToolBar();
			copyCutPasteToolBar.setOrientation(javax.swing.JToolBar.VERTICAL);
			copyCutPasteToolBar.add(getDuplicateButton());
			copyCutPasteToolBar.add(getCropButton());
			copyCutPasteToolBar.add(getCopyButton());
			copyCutPasteToolBar.add(getPasteButton());
		}
		return copyCutPasteToolBar;
	}
	/**
	 * This method initializes duplicateButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getDuplicateButton() {
		if (duplicateButton == null) {
			duplicateButton = new JButton();
			duplicateButton.setMaximumSize(new java.awt.Dimension(33,33));
			duplicateButton.setMinimumSize(new java.awt.Dimension(33,33));
			duplicateButton.setPreferredSize(new java.awt.Dimension(33,33));
			duplicateButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/copy.gif")));
			duplicateButton.setToolTipText("Duplicate image / selection");
			duplicateButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {  
					model.duplicate();
				}
			});
		}
		return duplicateButton;
	}
	/**
	 * This method initializes cropButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getCropButton() {
		if (cropButton == null) {
			cropButton = new JButton();
			cropButton.setMaximumSize(new java.awt.Dimension(33,33));
			cropButton.setMinimumSize(new java.awt.Dimension(33,33));
			cropButton.setPreferredSize(new java.awt.Dimension(33,33));
			cropButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/crop.gif")));
			cropButton.setToolTipText("Crop selection");
			cropButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.crop();
				}
			});
		}
		return cropButton;
	}
	/**
	 * This method initializes analyzeParticlesButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getAnalyzeParticlesButton() {
		if (analyzeParticlesButton == null) {
			analyzeParticlesButton = new JButton();
			analyzeParticlesButton.setMaximumSize(new java.awt.Dimension(33,33));
			analyzeParticlesButton.setMinimumSize(new java.awt.Dimension(33,33));
			analyzeParticlesButton.setPreferredSize(new java.awt.Dimension(33,33));
			analyzeParticlesButton.setToolTipText("Analyze particles");
			analyzeParticlesButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/analyze-particles.gif")));
			analyzeParticlesButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {   
					model.analyzeParticles();
				}
			});
		}
		return analyzeParticlesButton;
	}
	/**
	 * This method initializes copyButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getCopyButton() {
		if (copyButton == null) {
			copyButton = new JButton();
			copyButton.setMaximumSize(new java.awt.Dimension(33,33));
			copyButton.setMinimumSize(new java.awt.Dimension(33,33));
			copyButton.setPreferredSize(new java.awt.Dimension(33,33));
			copyButton.setToolTipText("Copy image / selection");
			copyButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/copy-c.gif")));
			copyButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.copy();
				}
			});
		}
		return copyButton;
	}
	/**
	 * This method initializes setScaleToolBar	
	 * 	
	 * @return javax.swing.JToolBar	
	 */    
	private JToolBar getSetScaleToolBar() {
		if (setScaleToolBar == null) {
			setScaleToolBar = new JToolBar();
			setScaleToolBar.setOrientation(javax.swing.JToolBar.VERTICAL);
			setScaleToolBar.add(getSetScaleButton());
			setScaleToolBar.add(getPasteScaleButton());
			setScaleToolBar.add(getCutLastPolygonElementButton());
			setScaleToolBar.add(getJButton15());
		}
		return setScaleToolBar;
	}
	/**
	 * This method initializes setScalePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getSetScalePanel() {
		if (setScalePanel == null) {
			setScalePanel = new JPanel();
			setScalePanel.setLayout(new BorderLayout());
			setScalePanel.add(getSetScaleToolBar(), java.awt.BorderLayout.WEST);
		}
		return setScalePanel;
	}
	/**
	 * This method initializes setScaleButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getSetScaleButton() {
		if (setScaleButton == null) {
			setScaleButton = new JButton();
			setScaleButton.setMaximumSize(new java.awt.Dimension(33,33));
			setScaleButton.setMinimumSize(new java.awt.Dimension(33,33));
			setScaleButton.setPreferredSize(new java.awt.Dimension(33,33));
			setScaleButton.setToolTipText("Set scale");
			setScaleButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/set-scale.gif")));
			setScaleButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.setScale();
				}
			});
		}
		return setScaleButton;
	}
	/**
	 * This method initializes pasteScaleButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getPasteScaleButton() {
		if (pasteScaleButton == null) {
			pasteScaleButton = new JButton();
			pasteScaleButton.setMaximumSize(new java.awt.Dimension(33,33));
			pasteScaleButton.setMinimumSize(new java.awt.Dimension(33,33));
			pasteScaleButton.setPreferredSize(new java.awt.Dimension(33,33));
			pasteScaleButton.setToolTipText("Paste scale-bar");
			pasteScaleButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/paste-scale-bar2.gif")));
			pasteScaleButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.pasteScaleBar();
				}
			});
		}
		return pasteScaleButton;
	}
	/**
	 * This method initializes cutLastPolygonElementButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getCutLastPolygonElementButton() {
		if (cutLastPolygonElementButton == null) {
			cutLastPolygonElementButton = new JButton();
			cutLastPolygonElementButton.setMaximumSize(new java.awt.Dimension(33,33));
			cutLastPolygonElementButton.setMinimumSize(new java.awt.Dimension(33,33));
			cutLastPolygonElementButton.setPreferredSize(new java.awt.Dimension(33,33));
			cutLastPolygonElementButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/cut-segment.gif")));
			cutLastPolygonElementButton.setToolTipText("cut last polygon segment");
			cutLastPolygonElementButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.cutLastPolygonElement();
				}
			});
		}
		return cutLastPolygonElementButton;
	}
	/**
	 * This method initializes pasteButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getPasteButton() {
		if (pasteButton == null) {
			pasteButton = new JButton();
			pasteButton.setMaximumSize(new java.awt.Dimension(33,33));
			pasteButton.setMinimumSize(new java.awt.Dimension(33,33));
			pasteButton.setPreferredSize(new java.awt.Dimension(33,33));
			pasteButton.setToolTipText("Paste");
			pasteButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/paste.gif")));
			pasteButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.paste();
				}
			});
		}
		return pasteButton;
	}
	/**
	 * This method initializes pasteMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getPasteMenuItem() {
		if (pasteMenuItem == null) {
			pasteMenuItem = new JMenuItem();
			pasteMenuItem.setText("Paste");
			pasteMenuItem.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.setPasteOptions();
				}
			});
		}
		return pasteMenuItem;
	}

	/**
	 * This method initializes magicSelectionPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getMagicSelectionPanel() {
		if (magicSelectionPanel == null) {
			magicSelectionPanel = new JPanel();
			magicSelectionPanel.setLayout(new BorderLayout());
			magicSelectionPanel.setPreferredSize(new java.awt.Dimension(37,151));
			magicSelectionPanel.add(getMagicSelectionToolBar(), java.awt.BorderLayout.WEST);
		}
		return magicSelectionPanel;
	}

	/**
	 * This method initializes magicSelectionToolBar	
	 * 	
	 * @return javax.swing.JToolBar	
	 */
	private JToolBar getMagicSelectionToolBar() {
		if (magicSelectionToolBar == null) {
			magicSelectionToolBar = new JToolBar();
			magicSelectionToolBar.setOrientation(javax.swing.JToolBar.VERTICAL);
			magicSelectionToolBar.setPreferredSize(new java.awt.Dimension(37,151));
			magicSelectionToolBar.add(getPointSelectionButton());
			magicSelectionToolBar.add(getLiveWire1dButton());
			magicSelectionToolBar.add(getLiveWire2dButton());
			magicSelectionToolBar.add(getMagicWandButton());
		}
		return magicSelectionToolBar;
	}

	/**
	 * This method initializes liveWire1dButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getLiveWire1dButton() {
		if (liveWire1dButton == null) {
			liveWire1dButton = new JButton();
			liveWire1dButton.setPreferredSize(new java.awt.Dimension(33,33));
			liveWire1dButton.setToolTipText("livewire 1d selection");
			liveWire1dButton.setMaximumSize(new java.awt.Dimension(33,33));
			liveWire1dButton.setMinimumSize(new java.awt.Dimension(33,33));
			liveWire1dButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/livewire1d.gif")));
			liveWire1dButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String path = IJ.getDirectory("macros")+"toolsets/Tracing.txt";
					MacroInstaller installer = new MacroInstaller();
					installer.run(path);
					IJ.setTool(17);
					enableButtons(liveWire1dButton);
				}
			});
		}
		return liveWire1dButton;
	}

	/**
	 * This method initializes liveWire2dButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getLiveWire2dButton() {
		if (liveWire2dButton == null) {
			liveWire2dButton = new JButton();
			liveWire2dButton.setPreferredSize(new java.awt.Dimension(33,33));
			liveWire2dButton.setToolTipText("livewire 2d selection");
			liveWire2dButton.setMaximumSize(new java.awt.Dimension(33,33));
			liveWire2dButton.setMinimumSize(new java.awt.Dimension(33,33));
			liveWire2dButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/livewire2d.gif")));
			liveWire2dButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String path = IJ.getDirectory("macros")+"toolsets/Tracing.txt";
					MacroInstaller installer = new MacroInstaller();
					installer.run(path);
					IJ.setTool(18);
					enableButtons(liveWire2dButton);
				}
			});
		}
		return liveWire2dButton;
	}

	/**
	 * This method initializes distributionButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getDistributionButton() {
		if (distributionButton == null) {
			distributionButton = new JButton();
			distributionButton.setPreferredSize(new java.awt.Dimension(33,33));
			distributionButton.setToolTipText("distribution from results table");
			distributionButton.setMaximumSize(new java.awt.Dimension(33,33));
			distributionButton.setMinimumSize(new java.awt.Dimension(33,33));
			distributionButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/distribution.gif")));
			distributionButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					IJ.run("Distribution...");
				}
			});
		}
		return distributionButton;
	}

	/**
	 * This method initializes labelButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getLabelButton() {
		if (labelButton == null) {
			labelButton = new JButton();
			labelButton.setPreferredSize(new java.awt.Dimension(33,33));
			labelButton.setToolTipText("label objects in the image");
			labelButton.setMaximumSize(new java.awt.Dimension(33,33));
			labelButton.setMinimumSize(new java.awt.Dimension(33,33));
			labelButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/label.gif")));
			labelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					IJ.run("Label");
				}
			});
		}
		return labelButton;
	}

	/**
	 * This method initializes lutPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getLutPanel() {
		if (lutPanel == null) {
			lutPanel = new JPanel();
			lutPanel.setLayout(new BorderLayout());
			lutPanel.setPreferredSize(new java.awt.Dimension(37,151));
			lutPanel.add(getLutToolBar(), java.awt.BorderLayout.WEST);
		}
		return lutPanel;
	}

	/**
	 * This method initializes lutToolBar	
	 * 	
	 * @return javax.swing.JToolBar	
	 */
	private JToolBar getLutToolBar() {
		if (lutToolBar == null) {
			lutToolBar = new JToolBar();
			lutToolBar.setOrientation(javax.swing.JToolBar.VERTICAL);
			lutToolBar.setPreferredSize(new java.awt.Dimension(37,151));
			lutToolBar.add(getRedButton());
			lutToolBar.add(getGreenButton());
			lutToolBar.add(getBlueButton());
			lutToolBar.add(getHiloButton());
		}
		return lutToolBar;
	}

	/**
	 * This method initializes redButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getRedButton() {
		if (redButton == null) {
			redButton = new JButton();
			redButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/red.gif")));
			redButton.setToolTipText("apply red lut");
			redButton.setMaximumSize(new java.awt.Dimension(33,33));
			redButton.setMinimumSize(new java.awt.Dimension(33,33));
			redButton.setPreferredSize(new java.awt.Dimension(33,33));
			redButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					IJ.run("Red");
				}
			});
		}
		return redButton;
	}

	/**
	 * This method initializes greenButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getGreenButton() {
		if (greenButton == null) {
			greenButton = new JButton();
			greenButton.setPreferredSize(new java.awt.Dimension(33,33));
			greenButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/green.gif")));
			greenButton.setMaximumSize(new java.awt.Dimension(33,33));
			greenButton.setMinimumSize(new java.awt.Dimension(33,33));
			greenButton.setToolTipText("apply green lut");
			greenButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					IJ.run("Green");
				}
			});
		}
		return greenButton;
	}

	/**
	 * This method initializes blueButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBlueButton() {
		if (blueButton == null) {
			blueButton = new JButton();
			blueButton.setPreferredSize(new java.awt.Dimension(33,33));
			blueButton.setToolTipText("apply blue lut");
			blueButton.setMaximumSize(new java.awt.Dimension(33,33));
			blueButton.setMinimumSize(new java.awt.Dimension(33,33));
			blueButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/blue.gif")));
			blueButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					IJ.run("Blue");
				}
			});
		}
		return blueButton;
	}

	/**
	 * This method initializes hiloButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getHiloButton() {
		if (hiloButton == null) {
			hiloButton = new JButton();
			hiloButton.setPreferredSize(new java.awt.Dimension(33,33));
			hiloButton.setToolTipText("apply hilo lut");
			hiloButton.setMaximumSize(new java.awt.Dimension(33,33));
			hiloButton.setMinimumSize(new java.awt.Dimension(33,33));
			hiloButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/hilo.gif")));
			hiloButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					IJ.run("HiLo");
				}
			});
		}
		return hiloButton;
	}
                   }  //  @jve:decl-index=0:visual-constraint="60,12"
