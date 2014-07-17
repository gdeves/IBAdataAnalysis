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
package gui;

import ij.Executer;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.io.OpenDialog;
import ij.plugin.ImageCalculator;
import ij.plugin.RGBStackMerge;
import ij.plugin.ZProjector;
import ij.plugin.filter.Projector;
import ij.plugin.frame.ThresholdAdjuster;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Label;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.ImageIcon;

import neuronJNewGUI.SlideShowControl;
import neuronJNewGUI.LookupTableSelector;
import neuronJgui.NJ;
import applications.Application;
import broadcaster.ObservableContrastAdjuster;

import java.awt.Toolkit;
import java.io.File;

import javax.swing.BoxLayout;

import objectModelingWorkbench.ObjectModelingWorkbench;
import operations.Operation;
import operations.image.ConvertImageTypeOperation;
import operations.image.GammaAdjustOperation;
import operations.processing.ConvolveOperation;
import operations.processing.EnhanceContrastOperation;
import operations.processing.GaussianBlurOperation;
import operations.processing.MaximumFilterOperation;
import operations.processing.MeanFilterOperation;
import operations.processing.MedianFilterOperation;
import operations.processing.MinimumFilterOperation;
import operations.processing.SubtractBackgroundOperation;
import operations.processing.VarianceFilterOperation;
import operations.segmentation.ThresholdOperation;
import tools.DnaCombingTool;
import tools.PixelSpy;
import tools.StackRegistrator;
import tools.Toolbox;
import tools.channels.ChannelHider;
import tools.channels.ChannelMixer;
import tools.frap.FRAPWizard;
import tools.magicWand.MagicWand;
import volume.Volume_Viewer;
import volume.OrtView.OrtView_;

/**
 * This is the launcher window of the Cell Image Analyzer. It is still there for backward compatibility.
 * The operations menu and the applications menu are build from the folders _operations and _applications. 
 * New components should better be made available using the standard ImageJ mechanisms (toolsets, plugins, etc)
 * 
 * @author	Volker Baecker
 **/
public class CellImageAnalyzerView extends javax.swing.JFrame implements Observer {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8277998123054156103L;
	private javax.swing.JPanel ivjJFrameContentPane = null;
	private JPanel jPanel = null;
	private JMenuBar jJMenuBar = null;
	private JMenu jMenu = null;
	private JMenuItem jMenuItem = null;
	
	private CellImageAnalyzer model;
	private String mouseOverComponent;
	
	private JMenu jMenu1 = null;
	private JMenuItem jMenuItem1 = null;
	private JToolBar jToolBar = null;
	private JButton jButton = null;
	private JMenu jMenu2 = null;
	private JMenu jMenu3 = null;
	private JMenuItem jMenuItem2 = null;
	private JMenu jMenu4 = null;
	private JMenuItem jMenuItem3 = null;
	private JMenuItem jMenuItem6 = null;
	private JMenu operationsMenu = null;
	private JMenuItem jMenuItem7 = null;
	private JMenu jMenu7 = null;
	private JMenuItem jMenuItem11 = null;
	
	private JMenuItem jMenuItem12 = null;
	private JButton jButton1 = null;
	private JMenu jMenu6 = null;
	private JMenuItem jMenuItem8 = null;
	private JButton jButton3 = null;
	private JButton jButton2 = null;
	private JMenuItem jMenuItem4 = null;
	private JMenuItem jMenuItem5 = null;
	private JButton jButton4 = null;
	private JMenuItem jMenuItem10 = null;
	private JMenu jMenu5 = null;
	private JMenuItem jMenuItem13 = null;
	private JMenu jMenu8 = null;
	private JMenuItem jMenuItem14 = null;
	private JMenuItem jMenuItem15 = null;
	private JMenuItem jMenuItem16 = null;
	private JMenuItem jMenuItem17 = null;
	private JToolBar jToolBar1 = null;

	private Label jLabel;
	private JMenuItem jMenuItem18 = null;
	private JMenuItem jMenuItem19 = null;
	private JMenuItem jMenuItem20 = null;
	private JMenuItem jMenuItem21 = null;
	private JMenu jMenu9 = null;
	private JMenuItem jMenuItem22 = null;
	private JMenuItem jMenuItem23 = null;
	private JButton jButton5 = null;
	private JButton jButton6 = null;
	private JButton jButton7 = null;
	private JButton jButton8 = null;
	private JButton jButton9 = null;
	private JButton jButton10 = null;
	private JMenuItem jMenuItem24 = null;
	private JMenuItem jMenuItem25 = null;
	private JMenuItem jMenuItem26 = null;
	private JMenuItem jMenuItem27 = null;
	private JMenuItem jMenuItem28 = null;
	private JMenuItem jMenuItem29 = null;
	private JMenuItem jMenuItem30 = null;
	private JMenuItem jMenuItem31 = null;
	private JMenuItem jMenuItem32 = null;
	private JMenu jMenu10 = null;
	private JMenuItem jMenuItem33 = null;
	private JMenuItem jMenuItem34 = null;
	private JMenuItem jMenuItem35 = null;
	private JButton jButton11 = null;
	private JMenuItem jMenuItem36 = null;
	private JMenuItem jMenuItem37 = null;
	private JMenuItem jMenuItem9 = null;
	private JMenuItem jMenuItem38 = null;
	private JMenuItem jMenuItem39 = null;
	private JMenuItem jMenuItem40 = null;
	private JMenuItem jMenuItem41 = null;
	private JButton jButton12 = null;
	private JMenuItem jMenuItem42 = null;
	private JMenuItem jMenuItem43 = null;
	private JMenuItem jMenuItem44 = null;
	private JMenu jMenu11 = null;
	private JMenuItem jMenuItem45 = null;
	private JMenuItem jMenuItem46 = null;
	private JMenu jMenu12 = null;
	private JMenu jMenu13 = null;
	private JMenuItem jMenuItem47 = null;
	private JMenuItem jMenuItem48 = null;
	private JMenu jMenu14 = null;
	private JMenuItem jMenuItem49 = null;
	private JMenuItem jMenuItem50 = null;
	private JMenuItem jMenuItem51 = null;
	private JMenuItem jMenuItem52 = null;
	private JMenu jMenu15 = null;
	private JMenu jMenu16 = null;
	private JMenuItem jMenuItem53 = null;
	private JMenu jMenu17 = null;
	private JMenuItem jMenuItem54 = null;
	private JMenuItem jMenuItem55 = null;
	private JMenuItem jMenuItem56 = null;
	private JMenuItem jMenuItem57 = null;
	private JMenuItem jMenuItem58 = null;
	private JButton jButton13 = null;
	private JMenuItem jMenuItem59 = null;
	private JMenu jMenu18 = null;
	private JMenuItem jMenuItem60 = null;
	private JMenu jMenu19 = null;
	private JMenuItem jMenuItem61 = null;
	private JMenuItem jMenuItem62 = null;
	private JMenuItem jMenuItem63 = null;  //  @jve:decl-index=0:visual-constraint="598,42"
	private JMenuItem jMenuItem64 = null;
	private JMenuItem jMenuItem65 = null;  //  @jve:decl-index=0:visual-constraint="646,178"
	private JMenuItem jMenuItem66 = null;  //  @jve:decl-index=0:visual-constraint="621,213"
	private JMenu aboutMenu = null;
	private JMenuItem jMenuItem67 = null;
	private JMenuItem showStackRegistratorMenuItem = null;
	private JMenuItem openObjectModelingWorkbenchMenuItem = null;
	private JMenuItem jMenuItem68 = null;
	private JButton objectModelingWorkbenchButton = null;
	private JButton magicWandButton = null;
	private JMenuItem frapWizardMenuItem = null;
	public CellImageAnalyzerView() {
		super();
		initialize();
	}

	public CellImageAnalyzerView(CellImageAnalyzer analyzer) {
		super();
		model = analyzer;
		initialize();
	}

	/**
	 * Return the JFrameContentPane property value.
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJFrameContentPane() {
		if (ivjJFrameContentPane == null) {
			ivjJFrameContentPane = new javax.swing.JPanel();
			ivjJFrameContentPane.setName("JFrameContentPane");
			ivjJFrameContentPane.setLayout(new BorderLayout());
			ivjJFrameContentPane.add(getJPanel(), java.awt.BorderLayout.NORTH);
		}
		return ivjJFrameContentPane;
	}

	/**
	 * Initialize the class.
	 */	private void initialize() {

		this.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 10));
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/icon.gif")));
		this.setJMenuBar(getJJMenuBar());
		this.setName("JFrame1");
		this
				.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		this.setBounds(45, 25, 515, 107);
		this.setTitle("MRI Cell Image Analyzer");
		this.setContentPane(getJFrameContentPane());
		this.addKeyListener(new java.awt.event.KeyAdapter() { 
			public void keyPressed(java.awt.event.KeyEvent e) {    
				System.out.println(mouseOverComponent);
			}
		});
		this.addWindowListener(new java.awt.event.WindowAdapter() { 
			public void windowClosing(java.awt.event.WindowEvent e) {    
				model.exit();
			}
		});
		
	}
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
			jPanel.setMinimumSize(new java.awt.Dimension(400,31));
			jPanel.setMaximumSize(new java.awt.Dimension(800,31));
			jPanel.add(getJToolBar(), null);
			jPanel.add(getJToolBar1(), null);
		}
		return jPanel;
	}
	/**
	 * This method initializes jJMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */    
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getJMenu());
			jJMenuBar.add(getJMenu6());
			jJMenuBar.add(getJMenu4());
			jJMenuBar.add(getJMenu5());
			jJMenuBar.add(getJMenu1());
			jJMenuBar.add(getJMenu2());
			jJMenuBar.add(getJMenu11());
			jJMenuBar.add(getOperationsMenu());
			jJMenuBar.add(getApplicationsMenu());
			jJMenuBar.add(getAboutMenu());
		}
		return jJMenuBar;
	}
	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	private JMenu getJMenu() {
		if (jMenu == null) {
			jMenu = new JMenu();
			jMenu.setText("File");
			jMenu.setToolTipText("operation concerning the file system");
			jMenu.setMnemonic(java.awt.event.KeyEvent.VK_F);
			jMenu.add(getJMenuItem());
			jMenu.add(getJMenuItem6());
			jMenu.add(getJMenuItem18());
			jMenu.addMouseListener(new java.awt.event.MouseAdapter() {   
				public void mouseExited(java.awt.event.MouseEvent e) {    
					mouseOverComponent = null;
				} 
				public void mouseEntered(java.awt.event.MouseEvent e) {    
					mouseOverComponent = jMenu.getText();
				}
			});
		}
		return jMenu;
	}
	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem() {
		if (jMenuItem == null) {
			jMenuItem = new JMenuItem();
			jMenuItem.setText("Open...");
			jMenuItem.setToolTipText("open an image from a file");
			jMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.Event.CTRL_MASK, false));
			jMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_O);
			jMenuItem.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.openImage();
				}
			});
		}
		return jMenuItem;
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable arg0, Object arg1) {
		// at the moment there are no messages from the model
	}

	/**
	 * @param analyzer
	 */
	public void setModel(CellImageAnalyzer analyzer) {
		model = analyzer;
	}
	
	/**
	 * This method initializes jMenu1	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	private JMenu getJMenu1() {
		if (jMenu1 == null) {
			jMenu1 = new JMenu();
			jMenu1.setText("Channels");
			jMenu1.setMnemonic(java.awt.event.KeyEvent.VK_C);
			jMenu1.add(getJMenuItem1());
			jMenu1.add(getJMenuItem20());
			jMenu1.addSeparator();
			jMenu1.add(getJMenuItem56());
			jMenu1.add(getJMenuItem57());
		}
		return jMenu1;
	}
	/**
	 * This method initializes jMenuItem1	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem1() {
		if (jMenuItem1 == null) {
			jMenuItem1 = new JMenuItem();
			jMenuItem1.setText("Split");
			jMenuItem1.setMnemonic(java.awt.event.KeyEvent.VK_S);
			jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.Event.CTRL_MASK, false));
			jMenuItem1.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.splitChannels();
				}
			});
		}
		return jMenuItem1;
	}
	/**
	 * This method initializes jToolBar	
	 * 	
	 * @return javax.swing.JToolBar	
	 */    
	private JToolBar getJToolBar() {
		if (jToolBar == null) {
			jToolBar = new JToolBar();
			jToolBar.setPreferredSize(new java.awt.Dimension(510,25));
			jToolBar.setAlignmentX(0.0F);
			jToolBar.setDoubleBuffered(false);
			jToolBar.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
			jToolBar.setAutoscrolls(false);
			jToolBar.setMaximumSize(new java.awt.Dimension(1000,29));
			jToolBar.add(getJButton());
			jToolBar.add(getJButton7());
			jToolBar.add(getJButton1());
			jToolBar.add(getJButton13());
			jToolBar.add(getJButton5());
			jToolBar.add(getJButton6());
			jToolBar.add(getJButton4());
			jToolBar.add(getJButton9());
			jToolBar.add(getJButton8());
			jToolBar.add(getJButton3());
			jToolBar.add(getJButton2());
			jToolBar.add(getJButton10());
			jToolBar.add(getJButton12());
			jToolBar.add(getJButton11());
			jToolBar.add(getObjectModelingWorkbenchButton());
			jToolBar.add(getMagicWandButton());
			
		}
		return jToolBar;
	}
	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setPreferredSize(new java.awt.Dimension(20,20));
			jButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/microscope.gif")));
			jButton.setToolTipText("ImageJ");
			jButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) { 
					model.toggleImageJ();
				}
			});
		}
		return jButton;
	}
	/**
	 * This method initializes jMenu2	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	private JMenu getJMenu2() {
		if (jMenu2 == null) {
			jMenu2 = new JMenu();
			jMenu2.setText("Processing");
			jMenu2.setMnemonic(java.awt.event.KeyEvent.VK_P);
			jMenu2.add(getJMenu19());
			jMenu2.add(getJMenu9());
			jMenu2.add(getJMenu10());
			jMenu2.add(getJMenu3());
			jMenu2.addSeparator();
		}
		return jMenu2;
	}
	/**
	 * This method initializes jMenu3	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	private JMenu getJMenu3() {
		if (jMenu3 == null) {
			jMenu3 = new JMenu();
			jMenu3.setText("Threshold Methods");
			jMenu3.setMnemonic(java.awt.event.KeyEvent.VK_T);
			jMenu3.add(getJMenuItem21());
			jMenu3.add(getJMenuItem2());
			jMenu3.add(getJMenuItem22());
			jMenu3.add(getJMenuItem23());
			jMenu3.addSeparator();
			jMenu3.add(getJMenuItem31());
		}
		return jMenu3;
	}
	/**
	 * This method initializes jMenuItem2	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem2() {
		if (jMenuItem2 == null) {
			jMenuItem2 = new JMenuItem();
			jMenuItem2.setText("Entropy Threshold");
			jMenuItem2.setMnemonic(java.awt.event.KeyEvent.VK_E);
			jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.Event.CTRL_MASK, false));
			jMenuItem2.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.entropyThreshold();
				}
			});
		}
		return jMenuItem2;
	}
	/**
	 * This method initializes jMenu4	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	private JMenu getJMenu4() {
		if (jMenu4 == null) {
			jMenu4 = new JMenu();
			jMenu4.setText("Image");
			jMenu4.setMnemonic(java.awt.event.KeyEvent.VK_I);
			jMenu4.add(getJMenuItem34());
			jMenu4.add(getJMenuItem35());
			jMenu4.add(getJMenuItem3());
		}
		return jMenu4;
	}
	/**
	 * This method initializes jMenuItem3	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem3() {
		if (jMenuItem3 == null) {
			jMenuItem3 = new JMenuItem();
			jMenuItem3.setText("Invert");
			jMenuItem3.setMnemonic(java.awt.event.KeyEvent.VK_I);
			jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.Event.CTRL_MASK, false));
			jMenuItem3.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.invertImage();
				}
			});
		}
		return jMenuItem3;
	}
	/**
	 * This method initializes jMenuItem6	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem6() {
		if (jMenuItem6 == null) {
			jMenuItem6 = new JMenuItem();
			jMenuItem6.setText("Save As...");
			jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.Event.CTRL_MASK, false));
			jMenuItem6.setMnemonic(java.awt.event.KeyEvent.VK_S);
			jMenuItem6.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.saveAs();
				}
			});
		}
		return jMenuItem6;
	}
	/**
	 * This method initializes operationsMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	public JMenu getOperationsMenu() {
		if (operationsMenu == null) {
			operationsMenu = new JMenu();
			operationsMenu.setText("Operations");
			operationsMenu.setMnemonic(java.awt.event.KeyEvent.VK_O);
			rebuildOperationsMenu();
		}
		return operationsMenu;
	}

	public void rebuildOperationsMenu() {
		operationsMenu.removeAll();
		operationsMenu.add(getJMenuItem12());
		operationsMenu.add(getJMenuItem63());
		operationsMenu.add(getJMenuItem64());
		operationsMenu.addSeparator();
		operationsMenu.add(getJMenuItem7());
		operationsMenu.addSeparator();
		model.fillOperationsMenu(operationsMenu);
	}

	/**
	 * This method initializes jMenuItem7	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem7() {
		if (jMenuItem7 == null) {
			jMenuItem7 = new JMenuItem();
			jMenuItem7.setText("All");
			jMenuItem7.setMnemonic(java.awt.event.KeyEvent.VK_A);
			jMenuItem7.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.showOperations("all");
				}
			});
		}
		return jMenuItem7;
	}
	/**
	 * This method initializes jMenu7	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	public JMenu getApplicationsMenu() {
		if (jMenu7 == null) {
			jMenu7 = new JMenu();
			jMenu7.setText("Applications");
			jMenu7.setMnemonic(java.awt.event.KeyEvent.VK_A);
			this.rebuildApplicationMenu();
		}
		return jMenu7;
	}
	/**
	 * This method initializes jMenuItem11	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem11() {
		if (jMenuItem11 == null) {
			jMenuItem11 = new JMenuItem();
			jMenuItem11.setText("new...");
			jMenuItem11.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					String name = JOptionPane.showInputDialog("Enter the name for the application!");
					if (name==null) return;
					Application newApplication = new Application();
					newApplication.setName(name);
					newApplication.showApplicationView();
					newApplication.show();
				}
			});
		}
		return jMenuItem11;
	}
	/**
	 * This method initializes jMenuItem12	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem12() {
		if (jMenuItem12 == null) {
			jMenuItem12 = new JMenuItem();
			jMenuItem12.setText("new collection...");
			jMenuItem12.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {  
					String name = JOptionPane.showInputDialog("Enter the name for the collection!");
					if (name==null) return;
					OperationsBox newCollection = new OperationsBox();
					newCollection.setTitle(name);
					newCollection.show();
					newCollection.getView().setSize(300,400);
					newCollection.getView().validate();
				}
			});
		}
		return jMenuItem12;
	}

	public void rebuildApplicationMenu() {
		jMenu7.removeAll();
		jMenu7.add(getJMenuItem11());
		jMenu7.add(getJMenuItem65());
		jMenu7.add(getJMenuItem66());
		jMenu7.addSeparator();
		JMenu aJMenu = new JMenu("applications"); 
		model.fillApplicationsMenu(aJMenu);
		jMenu7.add(aJMenu);
	}
	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setText("");
			jButton1.setIcon(new ImageIcon(getClass().getResource("/resources/images/neuronJIcon.gif")));
			jButton1.setToolTipText("NeuronJ");
			jButton1.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.startNeuronJ();
				}
			});
		}
		return jButton1;
	}
	/**
	 * This method initializes jMenu6	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	private JMenu getJMenu6() {
		if (jMenu6 == null) {
			jMenu6 = new JMenu();
			jMenu6.setText("Show");
			jMenu6.setMnemonic(java.awt.event.KeyEvent.VK_S);
			jMenu6.add(getJMenuItem36());
			jMenu6.add(getJMenuItem8());
			jMenu6.add(getJMenuItem17());
			jMenu6.add(getJMenuItem37());
			jMenu6.add(getJMenuItem41());
			jMenu6.addSeparator();
			jMenu6.add(getJMenuItem4());
			jMenu6.add(getJMenuItem5());
			jMenu6.addSeparator();
			jMenu6.add(getJMenuItem59());
			jMenu6.add(getJMenuItem9());
			jMenu6.add(getJMenuItem38());
			jMenu6.add(getJMenuItem10());
			jMenu6.add(getJMenuItem40());
			jMenu6.add(getJMenuItem39());
			jMenu6.add(getShowStackRegistratorMenuItem());
			jMenu6.add(getJMenuItem62());
			jMenu6.add(getFrapWizardMenuItem());
			jMenu6.addSeparator();
	//		jMenu6.add(getNewCellCounterMenuItem());
			jMenu6.add(getOpenObjectModelingWorkbenchMenuItem());
			jMenu6.add(getJMenuItem68());
		}
		return jMenu6;
	}
	

	/**
	 * This method initializes jMenuItem8	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem8() {
		if (jMenuItem8 == null) {
			jMenuItem8 = new JMenuItem();
			jMenuItem8.setText("slide show control");
			jMenuItem8.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					SlideShowControl slideShowControl = new SlideShowControl();
					slideShowControl.show();
				}
			});
		}
		return jMenuItem8;
	}
	/**
	 * This method initializes jButton3	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton3() {
		if (jButton3 == null) {
			jButton3 = new JButton();
			jButton3.setPreferredSize(new java.awt.Dimension(27,27));
			jButton3.setText("8bit");
			jButton3.setMargin(new java.awt.Insets(2,3,2,3));
			jButton3.setToolTipText("convert images/folder to 8bit");
			jButton3.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					Application newApp = Application.load("./_applications/converting/converter batch.cia");
					newApp.show();
				}
			});
		}
		return jButton3;
	}
	/**
	 * This method initializes jButton2	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton2() {
		if (jButton2 == null) {
			jButton2 = new JButton();
			jButton2.setPreferredSize(new java.awt.Dimension(27,27));
			jButton2.setMargin(new java.awt.Insets(2,3,2,3));
			jButton2.setText("|>");
			jButton2.setToolTipText("open slide show control");
			jButton2.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					SlideShowControl slideShowControl = new SlideShowControl();
					slideShowControl.show();
				}
			});
		}
		return jButton2;
	}
	/**
	 * This method initializes jMenuItem4	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem4() {
		if (jMenuItem4 == null) {
			jMenuItem4 = new JMenuItem();
			jMenuItem4.setText("ImageJ");
			jMenuItem4.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					getJButton().doClick();
				}
			});
		}
		return jMenuItem4;
	}
	/**
	 * This method initializes jMenuItem5	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem5() {
		if (jMenuItem5 == null) {
			jMenuItem5 = new JMenuItem();
			jMenuItem5.setText("NeuronJ");
			jMenuItem5.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					getJButton1().doClick();
				}
			});
		}
		return jMenuItem5;
	}
	/**
	 * This method initializes jButton4	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton4() {
		if (jButton4 == null) {
			jButton4 = new JButton();
			jButton4.setPreferredSize(new java.awt.Dimension(27,27));
			jButton4.setMargin(new java.awt.Insets(2,3,2,3));
			jButton4.setIcon(new ImageIcon(getClass().getResource("/resources/images/dnac-icon.gif")));
			jButton4.setToolTipText("dna combing");
			jButton4.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					Application newApp = Application.load("./_applications/dna combing/dna tracing batch.cia");
					newApp.show();
				}
			});
		}
		return jButton4;
	}
	/**
	 * This method initializes jMenuItem10	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem10() {
		if (jMenuItem10 == null) {
			jMenuItem10 = new JMenuItem();
			jMenuItem10.setText("dna combing batch");
			jMenuItem10.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					getJButton4().doClick();
				}
			});
		}
		return jMenuItem10;
	}
	/**
	 * This method initializes jMenu5	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	private JMenu getJMenu5() {
		if (jMenu5 == null) {
			jMenu5 = new JMenu();
			jMenu5.setText("View");
			jMenu5.setMnemonic(java.awt.event.KeyEvent.VK_V);
			jMenu5.add(getJMenu8());
			jMenu5.add(getJMenuItem16());
			jMenu5.addSeparator();
		}
		return jMenu5;
	}
	/**
	 * This method initializes jMenuItem13	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem13() {
		if (jMenuItem13 == null) {
			jMenuItem13 = new JMenuItem();
			jMenuItem13.setText("brightness / contrast");
			jMenuItem13.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.Event.CTRL_MASK | java.awt.Event.SHIFT_MASK, false));
			jMenuItem13.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					ObservableContrastAdjuster adjuster = new ObservableContrastAdjuster();
					if (NJ.ntb!=null) {
						adjuster.getBroadcaster().addObserver(NJ.ntb);
					}
					adjuster.run("");
				}
			});
		}
		return jMenuItem13;
	}
	/**
	 * This method initializes jMenu8	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	private JMenu getJMenu8() {
		if (jMenu8 == null) {
			jMenu8 = new JMenu();
			jMenu8.setText("adjust");
			jMenu8.add(getJMenuItem13());
			jMenu8.add(getJMenuItem14());
			jMenu8.add(getJMenuItem15());
			jMenu8.addSeparator();
			jMenu8.add(getJMenuItem19());
		}
		return jMenu8;
	}
	/**
	 * This method initializes jMenuItem14	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem14() {
		if (jMenuItem14 == null) {
			jMenuItem14 = new JMenuItem();
			jMenuItem14.setText("window / level");
			jMenuItem14.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					ObservableContrastAdjuster adjuster = new ObservableContrastAdjuster();
					if (NJ.ntb!=null) {
						adjuster.getBroadcaster().addObserver(NJ.ntb);
					}
					adjuster.run("wl");
				}
			});
		}
		return jMenuItem14;
	}
	/**
	 * This method initializes jMenuItem15	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem15() {
		if (jMenuItem15 == null) {
			jMenuItem15 = new JMenuItem();
			jMenuItem15.setText("color balance");
			jMenuItem15.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					ObservableContrastAdjuster adjuster = new ObservableContrastAdjuster();
					if (NJ.ntb!=null) {
						adjuster.getBroadcaster().addObserver(NJ.ntb);
					}
					adjuster.run("balance");
				}
			});
		}
		return jMenuItem15;
	}
	/**
	 * This method initializes jMenuItem16	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem16() {
		if (jMenuItem16 == null) {
			jMenuItem16 = new JMenuItem();
			jMenuItem16.setText("look up tables");
			jMenuItem16.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					LookupTableSelector lookupTableSelector = new LookupTableSelector();
					lookupTableSelector.show();
				}
			});
		}
		return jMenuItem16;
	}
	/**
	 * This method initializes jMenuItem17	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem17() {
		if (jMenuItem17 == null) {
			jMenuItem17 = new JMenuItem();
			jMenuItem17.setText("toolbox");
			jMenuItem17.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					Toolbox toolbox = new Toolbox();
					toolbox.show();
				}
			});
		}
		return jMenuItem17;
	}
	/**
	 * This method initializes jToolBar1	
	 * 	
	 * @return javax.swing.JToolBar	
	 */    
	private JToolBar getJToolBar1() {
		if (jToolBar1 == null) {
			jToolBar1 = new JToolBar();
			jToolBar1.setAlignmentX(0.0F);
			jToolBar1.setMaximumSize(new java.awt.Dimension(1000,29));
			jToolBar1.setMinimumSize(new java.awt.Dimension(600,29));
			jToolBar1.setPreferredSize(new java.awt.Dimension(300,29));
			jLabel = new Label(""); 
			jToolBar1.add(jLabel);
		}
		return jToolBar1;
	}
	/**
	 * This method initializes jMenuItem18	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem18() {
		if (jMenuItem18 == null) {
			jMenuItem18 = new JMenuItem();
			jMenuItem18.setText("Quit");
			jMenuItem18.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.exit();
				}
			});
		}
		return jMenuItem18;
	}
	/**
	 * This method initializes jMenuItem19	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem19() {
		if (jMenuItem19 == null) {
			jMenuItem19 = new JMenuItem();
			jMenuItem19.setText("threshold");
			jMenuItem19.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					new ThresholdAdjuster();
				}
			});
		}
		return jMenuItem19;
	}
	/**
	 * This method initializes jMenuItem20	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem20() {
		if (jMenuItem20 == null) {
			jMenuItem20 = new JMenuItem();
			jMenuItem20.setText("Merge");
			jMenuItem20.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					RGBStackMerge merger = new RGBStackMerge();
					merger.run("");
				}
			});
		}
		return jMenuItem20;
	}
	/**
	 * This method initializes jMenuItem21	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem21() {
		if (jMenuItem21 == null) {
			jMenuItem21 = new JMenuItem();
			jMenuItem21.setText("Auto Threshold");
			jMenuItem21.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.autoThreshold();
				}
			});
		}
		return jMenuItem21;
	}
	/**
	 * This method initializes jMenu9	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	private JMenu getJMenu9() {
		if (jMenu9 == null) {
			jMenu9 = new JMenu();
			jMenu9.setText("Filters");
			jMenu9.add(getJMenuItem30());
			jMenu9.add(getJMenuItem43());
			jMenu9.add(getJMenuItem44());
			jMenu9.add(getJMenuItem24());
			jMenu9.add(getJMenuItem25());
			jMenu9.add(getJMenuItem26());
			jMenu9.add(getJMenuItem27());
			jMenu9.add(getJMenuItem28());
			jMenu9.add(getJMenuItem29());
		}
		return jMenu9;
	}
	/**
	 * This method initializes jMenuItem22	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem22() {
		if (jMenuItem22 == null) {
			jMenuItem22 = new JMenuItem();
			jMenuItem22.setText("Mean Threshold");
			jMenuItem22.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.meanThreshold();
				}
			});
		}
		return jMenuItem22;
	}
	/**
	 * This method initializes jMenuItem23	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem23() {
		if (jMenuItem23 == null) {
			jMenuItem23 = new JMenuItem();
			jMenuItem23.setText("Otsu Threshold");
			jMenuItem23.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.otsuThreshold();
				}
			});
		}
		return jMenuItem23;
	}
	/**
	 * This method initializes jButton5	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton5() {
		if (jButton5 == null) {
			jButton5 = new JButton();
			jButton5.setIcon(new ImageIcon(getClass().getResource("/resources/images/nucleiAndSpots20x20-gui.gif")));
			jButton5.setPreferredSize(new java.awt.Dimension(27,27));
			jButton5.setMaximumSize(new java.awt.Dimension(27,27));
			jButton5.setMinimumSize(new java.awt.Dimension(27,27));
			jButton5.setToolTipText("find nuclei and spots");
			jButton5.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					Application newApp = Application.load("./_applications/measure spots/baldin/spots on nuclei.cia");
					newApp.show();
				}
			});
		}
		return jButton5;
	}
	/**
	 * This method initializes jButton6	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton6() {
		if (jButton6 == null) {
			jButton6 = new JButton();
			jButton6.setMaximumSize(new java.awt.Dimension(32,32));
			jButton6.setMinimumSize(new java.awt.Dimension(32,32));
			jButton6.setIcon(new ImageIcon(getClass().getResource("/resources/images/ory-spots-gui.gif")));
			jButton6.setToolTipText("measure spots batch");
			jButton6.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					Application newApp = Application.load("./_applications/measure spots/ory/measure spots batch.cia");
					newApp.show();
				}
			});
		}
		return jButton6;
	}
	/**
	 * This method initializes jButton7	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton7() {
		if (jButton7 == null) {
			jButton7 = new JButton();
			jButton7.setMaximumSize(new java.awt.Dimension(16,16));
			jButton7.setMinimumSize(new java.awt.Dimension(16,16));
			jButton7.setPreferredSize(new java.awt.Dimension(16,16));
			jButton7.setOpaque(false);
			jButton7.setEnabled(false);
			jButton7.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,0,0,0));
		}
		return jButton7;
	}
	/**
	 * This method initializes jButton8	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton8() {
		if (jButton8 == null) {
			jButton8 = new JButton();
			jButton8.setMaximumSize(new java.awt.Dimension(16,16));
			jButton8.setMinimumSize(new java.awt.Dimension(16,16));
			jButton8.setPreferredSize(new java.awt.Dimension(16,16));
			jButton8.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,0,0,0));
			jButton8.setEnabled(false);
		}
		return jButton8;
	}
	/**
	 * This method initializes jButton9	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton9() {
		if (jButton9 == null) {
			jButton9 = new JButton();
			jButton9.setMaximumSize(new java.awt.Dimension(32,32));
			jButton9.setMinimumSize(new java.awt.Dimension(32,32));
			jButton9.setPreferredSize(new java.awt.Dimension(32,32));
			jButton9.setIcon(new ImageIcon(getClass().getResource("/resources/images/spots-contrast-gui.gif")));
			jButton9.setToolTipText("find spots contrast batch");
			jButton9.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					Application newApp = Application.load("./_applications/contrast/find spots contrast batch.cia");
					newApp.show();
				}
			});
		}
		return jButton9;
	}
	/**
	 * This method initializes jButton10	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton10() {
		if (jButton10 == null) {
			jButton10 = new JButton();
			jButton10.setMaximumSize(new java.awt.Dimension(32,32));
			jButton10.setMinimumSize(new java.awt.Dimension(32,32));
			jButton10.setPreferredSize(new java.awt.Dimension(32,32));
			jButton10.setIcon(new ImageIcon(getClass().getResource("/resources/images/toolbox-gui.gif")));
			jButton10.setToolTipText("open toolbox");
			jButton10.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					Toolbox toolbox = new Toolbox();
					toolbox.show();
				}
			});
		}
		return jButton10;
	}
	/**
	 * This method initializes jMenuItem24	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem24() {
		if (jMenuItem24 == null) {
			jMenuItem24 = new JMenuItem();
			jMenuItem24.setText("gaussian blur");
			jMenuItem24.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					Operation op = new GaussianBlurOperation();
					op.show();
				}
			});
		}
		return jMenuItem24;
	}
	/**
	 * This method initializes jMenuItem25	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem25() {
		if (jMenuItem25 == null) {
			jMenuItem25 = new JMenuItem();
			jMenuItem25.setText("maximum");
			jMenuItem25.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					Operation op = new MaximumFilterOperation();
					op.show();
				}
			});
		}
		return jMenuItem25;
	}
	/**
	 * This method initializes jMenuItem26	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem26() {
		if (jMenuItem26 == null) {
			jMenuItem26 = new JMenuItem();
			jMenuItem26.setText("minimum");
			jMenuItem26.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					Operation op = new MinimumFilterOperation();
					op.show();
				}
			});
		}
		return jMenuItem26;
	}
	/**
	 * This method initializes jMenuItem27	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem27() {
		if (jMenuItem27 == null) {
			jMenuItem27 = new JMenuItem();
			jMenuItem27.setText("mean");
			jMenuItem27.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					Operation op = new MeanFilterOperation();
					op.show();
				}
			});
		}
		return jMenuItem27;
	}
	/**
	 * This method initializes jMenuItem28	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem28() {
		if (jMenuItem28 == null) {
			jMenuItem28 = new JMenuItem();
			jMenuItem28.setText("median");
			jMenuItem28.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					Operation op = new MedianFilterOperation();
					op.show();
				}
			});
		}
		return jMenuItem28;
	}
	/**
	 * This method initializes jMenuItem29	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem29() {
		if (jMenuItem29 == null) {
			jMenuItem29 = new JMenuItem();
			jMenuItem29.setText("variance");
			jMenuItem29.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					Operation op = new VarianceFilterOperation();
					op.show();
				}
			});
		}
		return jMenuItem29;
	}
	/**
	 * This method initializes jMenuItem30	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem30() {
		if (jMenuItem30 == null) {
			jMenuItem30 = new JMenuItem();
			jMenuItem30.setText("convolve");
			jMenuItem30.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					Operation op = new ConvolveOperation();
					op.show();
				}
			});
		}
		return jMenuItem30;
	}
	/**
	 * This method initializes jMenuItem31	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem31() {
		if (jMenuItem31 == null) {
			jMenuItem31 = new JMenuItem();
			jMenuItem31.setText("Treshold");
			jMenuItem31.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					Operation op = new ThresholdOperation();
					op.show();
				}
			});
		}
		return jMenuItem31;
	}
	/**
	 * This method initializes jMenuItem32	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem32() {
		if (jMenuItem32 == null) {
			jMenuItem32 = new JMenuItem();
			jMenuItem32.setText("background");
			jMenuItem32.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					Operation op = new SubtractBackgroundOperation();
					op.show();
				}
			});
		}
		return jMenuItem32;
	}
	/**
	 * This method initializes jMenu10	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	private JMenu getJMenu10() {
		if (jMenu10 == null) {
			jMenu10 = new JMenu();
			jMenu10.setText("Substract");
			jMenu10.add(getJMenuItem32());
			jMenu10.add(getJMenuItem33());
		}
		return jMenu10;
	}
	/**
	 * This method initializes jMenuItem33	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem33() {
		if (jMenuItem33 == null) {
			jMenuItem33 = new JMenuItem();
			jMenuItem33.setText("mean");
			jMenuItem33.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.substractMean();
				}
			});
		}
		return jMenuItem33;
	}
	/**
	 * This method initializes jMenuItem34	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem34() {
		if (jMenuItem34 == null) {
			jMenuItem34 = new JMenuItem();
			jMenuItem34.setText("Convert image type");
			jMenuItem34.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					Operation op = new ConvertImageTypeOperation();
					op.show();
				}
			});
		}
		return jMenuItem34;
	}
	/**
	 * This method initializes jMenuItem35	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem35() {
		if (jMenuItem35 == null) {
			jMenuItem35 = new JMenuItem();
			jMenuItem35.setText("Image Calculator");
			jMenuItem35.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					ImageCalculator app = new ImageCalculator();
					app.run("");
				}
			});
		}
		return jMenuItem35;
	}
	/**
	 * This method initializes jButton11	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton11() {
		if (jButton11 == null) {
			jButton11 = new JButton();
			jButton11.setPreferredSize(new java.awt.Dimension(32,32));
			jButton11.setMaximumSize(new java.awt.Dimension(32,32));
			jButton11.setMinimumSize(new java.awt.Dimension(32,32));
			jButton11.setText("LUT");
			jButton11.setFont(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 10));
			jButton11.setToolTipText("open lookup tables");
			jButton11.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					LookupTableSelector lookupTableSelector = new LookupTableSelector();
					lookupTableSelector.show();
				}
			});
		}
		return jButton11;
	}
	/**
	 * This method initializes jMenuItem36	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem36() {
		if (jMenuItem36 == null) {
			jMenuItem36 = new JMenuItem();
			jMenuItem36.setText("converter batch (8bit...)");
			jMenuItem36.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					getJButton3().doClick();
				}
			});
		}
		return jMenuItem36;
	}
	/**
	 * This method initializes jMenuItem37	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem37() {
		if (jMenuItem37 == null) {
			jMenuItem37 = new JMenuItem();
			jMenuItem37.setText("lookup table tool");
			jMenuItem37.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					getJButton11().doClick();
				}
			});
		}
		return jMenuItem37;
	}
	/**
	 * This method initializes jMenuItem9	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem9() {
		if (jMenuItem9 == null) {
			jMenuItem9 = new JMenuItem();
			jMenuItem9.setText("find nuclei and spots");
			jMenuItem9.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					getJButton5().doClick();
				}
			});
		}
		return jMenuItem9;
	}
	/**
	 * This method initializes jMenuItem38	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem38() {
		if (jMenuItem38 == null) {
			jMenuItem38 = new JMenuItem();
			jMenuItem38.setText("measure spots batch");
			jMenuItem38.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					getJButton6().doClick();
				}
			});
		}
		return jMenuItem38;
	}
	/**
	 * This method initializes jMenuItem39	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem39() {
		if (jMenuItem39 == null) {
			jMenuItem39 = new JMenuItem();
			jMenuItem39.setText("find spots contrast batch");
			jMenuItem39.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					getJButton9().doClick();
				}
			});
		}
		return jMenuItem39;
	}
	/**
	 * This method initializes jMenuItem40	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem40() {
		if (jMenuItem40 == null) {
			jMenuItem40 = new JMenuItem();
			jMenuItem40.setText("dna combing tool");
			jMenuItem40.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					DnaCombingTool.createAndShow();
				}
			});
		}
		return jMenuItem40;
	}
	/**
	 * This method initializes jMenuItem41	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem41() {
		if (jMenuItem41 == null) {
			jMenuItem41 = new JMenuItem();
			jMenuItem41.setText("pixel spy");
			jMenuItem41.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ImagePlus image = WindowManager.getCurrentImage();
					PixelSpy app = PixelSpy.getCurrent(image);
					app.setVisible(true);
				}
			});
		}
		return jMenuItem41;
	}
	/**
	 * This method initializes jButton12	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton12() {
		if (jButton12 == null) {
			jButton12 = new JButton();
			jButton12.setText("");
			jButton12.setIcon(new ImageIcon(getClass().getResource("/resources/images/spy3.gif")));
			jButton12.setPreferredSize(new java.awt.Dimension(32,32));
			jButton12.setMaximumSize(new java.awt.Dimension(32,32));
			jButton12.setToolTipText("open pixel spy");
			jButton12.setMinimumSize(new java.awt.Dimension(32,32));
			jButton12.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					ImagePlus image = WindowManager.getCurrentImage();
					PixelSpy app = PixelSpy.getCurrent(image);
					app.setVisible(true);
				}
			});
		}
		return jButton12;
	}
	/**
	 * This method initializes jMenuItem42	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem42() {
		if (jMenuItem42 == null) {
			jMenuItem42 = new JMenuItem();
			jMenuItem42.setText("enhance contrast");
			jMenuItem42.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					Operation op = new EnhanceContrastOperation();
					op.show();
				}
			});
		}
		return jMenuItem42;
	}
	/**
	 * This method initializes jMenuItem43	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem43() {
		if (jMenuItem43 == null) {
			jMenuItem43 = new JMenuItem();
			jMenuItem43.setText("dilate");
			jMenuItem43.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.dilate();
				}
			});
		}
		return jMenuItem43;
	}
	/**
	 * This method initializes jMenuItem44	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem44() {
		if (jMenuItem44 == null) {
			jMenuItem44 = new JMenuItem();
			jMenuItem44.setText("erode");
			jMenuItem44.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.erode();
				}
			});
		}
		return jMenuItem44;
	}
	/**
	 * This method initializes jMenu11	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	private JMenu getJMenu11() {
		if (jMenu11 == null) {
			jMenu11 = new JMenu();
			jMenu11.setText("Stack");
			jMenu11.setMnemonic(java.awt.event.KeyEvent.VK_T);
			jMenu11.add(getJMenu16());
			jMenu11.add(getJMenu18());
			jMenu11.add(getJMenu17());
			jMenu11.addSeparator();
			jMenu11.add(getJMenu15());
			jMenu11.add(getJMenu14());
			jMenu11.add(getJMenu13());
			jMenu11.add(getJMenu12());
		}
		return jMenu11;
	}
	/**
	 * This method initializes jMenuItem45	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem45() {
		if (jMenuItem45 == null) {
			jMenuItem45 = new JMenuItem();
			jMenuItem45.setText("volume viewer");
			jMenuItem45.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					Volume_Viewer viewer = new Volume_Viewer();
					viewer.run("");
				}
			});
		}
		return jMenuItem45;
	}
	/**
	 * This method initializes jMenuItem46	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem46() {
		if (jMenuItem46 == null) {
			jMenuItem46 = new JMenuItem();
			jMenuItem46.setText("ort view");
			jMenuItem46.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ImagePlus image = WindowManager.getCurrentImage();
					OrtView_ view = new OrtView_();
					view.setup("", image);
					view.run(image.getProcessor());
				}
			});
		}
		return jMenuItem46;
	}
	/**
	 * This method initializes jMenu12	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	private JMenu getJMenu12() {
		if (jMenu12 == null) {
			jMenu12 = new JMenu();
			jMenu12.setText("view");
			jMenu12.add(getJMenuItem46());
			jMenu12.add(getJMenuItem45());
		}
		return jMenu12;
	}
	/**
	 * This method initializes jMenu13	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	private JMenu getJMenu13() {
		if (jMenu13 == null) {
			jMenu13 = new JMenu();
			jMenu13.setText("projection");
			jMenu13.add(getJMenuItem48());
			jMenu13.add(getJMenuItem47());
			jMenu13.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
				
				}
			});
		}
		return jMenu13;
	}
	/**
	 * This method initializes jMenuItem47	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem47() {
		if (jMenuItem47 == null) {
			jMenuItem47 = new JMenuItem();
			jMenuItem47.setText("z projection");
			jMenuItem47.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					ZProjector projector = new ZProjector();
					projector.run("");
				}
			});
		}
		return jMenuItem47;
	}
	/**
	 * This method initializes jMenuItem48	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem48() {
		if (jMenuItem48 == null) {
			jMenuItem48 = new JMenuItem();
			jMenuItem48.setText("3d projection");
			jMenuItem48.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) { 
					ImagePlus image = WindowManager.getCurrentImage();
					Projector projector = new Projector();
					projector.setup("", image);
					projector.run(image.getProcessor());
				}
			});
		}
		return jMenuItem48;
	}
	/**
	 * This method initializes jMenu14	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	private JMenu getJMenu14() {
		if (jMenu14 == null) {
			jMenu14 = new JMenu();
			jMenu14.setText("animate");
			jMenu14.add(getJMenuItem49());
			jMenu14.add(getJMenuItem50());
			jMenu14.add(getJMenuItem51());
		}
		return jMenu14;
	}
	/**
	 * This method initializes jMenuItem49	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem49() {
		if (jMenuItem49 == null) {
			jMenuItem49 = new JMenuItem();
			jMenuItem49.setText("start");
			jMenuItem49.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ImagePlus image = WindowManager.getCurrentImage();
					new Executer("Start Animation", image);
				}
			});
		}
		return jMenuItem49;
	}
	/**
	 * This method initializes jMenuItem50	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem50() {
		if (jMenuItem50 == null) {
			jMenuItem50 = new JMenuItem();
			jMenuItem50.setText("stop");
			jMenuItem50.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					ImagePlus image = WindowManager.getCurrentImage();
					new Executer("Stop Animation", image);
				}
			});
		}
		return jMenuItem50;
	}
	/**
	 * This method initializes jMenuItem51	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem51() {
		if (jMenuItem51 == null) {
			jMenuItem51 = new JMenuItem();
			jMenuItem51.setText("options");
			jMenuItem51.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ImagePlus image = WindowManager.getCurrentImage();
					new Executer("Animation Options...", image);
				}
			});
		}
		return jMenuItem51;
	}
	/**
	 * This method initializes jMenuItem52	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem52() {
		if (jMenuItem52 == null) {
			jMenuItem52 = new JMenuItem();
			jMenuItem52.setText("3d object counter");
			jMenuItem52.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.start3DObjectCounter();
				}
			});
		}
		return jMenuItem52;
	}
	/**
	 * This method initializes jMenu15	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	private JMenu getJMenu15() {
		if (jMenu15 == null) {
			jMenu15 = new JMenu();
			jMenu15.setText("analyze");
			jMenu15.add(getJMenuItem52());
		}
		return jMenu15;
	}
	/**
	 * This method initializes jMenu16	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	private JMenu getJMenu16() {
		if (jMenu16 == null) {
			jMenu16 = new JMenu();
			jMenu16.setText("open");
			jMenu16.add(getJMenuItem58());
			jMenu16.add(getJMenuItem53());
		}
		return jMenu16;
	}
	/**
	 * This method initializes jMenuItem53	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem53() {
		if (jMenuItem53 == null) {
			jMenuItem53 = new JMenuItem();
			jMenuItem53.setText("virtual stack");
			jMenuItem53.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					IJ.run("  Virtual Stack...");
				}
			});
		}
		return jMenuItem53;
	}
	/**
	 * This method initializes jMenu17	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	private JMenu getJMenu17() {
		if (jMenu17 == null) {
			jMenu17 = new JMenu();
			jMenu17.setText("export as movie");
			jMenu17.add(getJMenuItem54());
			jMenu17.add(getJMenuItem55());
		}
		return jMenu17;
	}
	/**
	 * This method initializes jMenuItem54	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem54() {
		if (jMenuItem54 == null) {
			jMenuItem54 = new JMenuItem();
			jMenuItem54.setText("avi");
			jMenuItem54.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ImagePlus image = WindowManager.getCurrentImage();
					new Executer("AVI... ", image);
				}
			});
		}
		return jMenuItem54;
	}
	/**
	 * This method initializes jMenuItem55	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem55() {
		if (jMenuItem55 == null) {
			jMenuItem55 = new JMenuItem();
			jMenuItem55.setText("quicktime");
			jMenuItem55.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.startExportAsQuicktime();
				}
			});
		}
		return jMenuItem55;
	}
	/**
	 * This method initializes jMenuItem56	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem56() {
		if (jMenuItem56 == null) {
			jMenuItem56 = new JMenuItem();
			jMenuItem56.setText("channel chooser");
			jMenuItem56.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					ChannelHider chooser = new ChannelHider();
					chooser.show();
				}
			});
		}
		return jMenuItem56;
	}
	/**
	 * This method initializes jMenuItem57	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem57() {
		if (jMenuItem57 == null) {
			jMenuItem57 = new JMenuItem();
			jMenuItem57.setText("channel mixer");
			jMenuItem57.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						ChannelMixer mixer = new ChannelMixer(WindowManager.getCurrentImage());
						mixer.show();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});
		}
		return jMenuItem57;
	}
	/**
	 * This method initializes jMenuItem58	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem58() {
		if (jMenuItem58 == null) {
			jMenuItem58 = new JMenuItem();
			jMenuItem58.setText("image sequence");
			jMenuItem58.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					IJ.run("Image Sequence...");
				}
			});
		}
		return jMenuItem58;
	}
	/**
	 * This method initializes jButton13	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton13() {
		if (jButton13 == null) {
			jButton13 = new JButton();
			jButton13.setPreferredSize(new java.awt.Dimension(27,27));
			jButton13.setMaximumSize(new java.awt.Dimension(27,27));
			jButton13.setMinimumSize(new java.awt.Dimension(27,27));
			jButton13.setIcon(new ImageIcon(getClass().getResource("/resources/images/cellCounter2.gif")));
			jButton13.setToolTipText("cell counter batch");
			jButton13.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					Application newApp = Application.load("./_applications/particle counting/work in progress/count cells batch.cia");
					newApp.show();
				}
			});
		}
		return jButton13;
	}
	/**
	 * This method initializes jMenuItem59	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem59() {
		if (jMenuItem59 == null) {
			jMenuItem59 = new JMenuItem();
			jMenuItem59.setText("count cells batch");
			jMenuItem59.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					getJButton13().doClick();
				}
			});
		}
		return jMenuItem59;
	}
	/**
	 * This method initializes jMenu18	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	private JMenu getJMenu18() {
		if (jMenu18 == null) {
			jMenu18 = new JMenu();
			jMenu18.setText("save as...");
			jMenu18.add(getJMenuItem60());
		}
		return jMenu18;
	}
	/**
	 * This method initializes jMenuItem60	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem60() {
		if (jMenuItem60 == null) {
			jMenuItem60 = new JMenuItem();
			jMenuItem60.setText("tiff series");
			jMenuItem60.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					IJ.run("Image Sequence... ");
				}
			});
		}
		return jMenuItem60;
	}
	/**
	 * This method initializes jMenu19	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	private JMenu getJMenu19() {
		if (jMenu19 == null) {
			jMenu19 = new JMenu();
			jMenu19.setText("Contrast");
			jMenu19.add(getJMenuItem42());
			jMenu19.add(getJMenuItem61());
		}
		return jMenu19;
	}
	/**
	 * This method initializes jMenuItem61	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem61() {
		if (jMenuItem61 == null) {
			jMenuItem61 = new JMenuItem();
			jMenuItem61.setText("gamma correction");
			jMenuItem61.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						GammaAdjustOperation op = new GammaAdjustOperation();
						op.show();
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					}
				}
			});
		}
		return jMenuItem61;
	}
	/**
	 * This method initializes jMenuItem62	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem62() {
		if (jMenuItem62 == null) {
			jMenuItem62 = new JMenuItem();
			jMenuItem62.setText("cell counter");
			jMenuItem62.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					IJ.run("Cell Counter");
				}
			});
		}
		return jMenuItem62;
	}
	/**
	 * This method initializes jMenuItem63	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem63() {
		if (jMenuItem63 == null) {
			jMenuItem63 = new JMenuItem();
			jMenuItem63.setText("load collection...");
			jMenuItem63.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JFileChooser fileChooser = new JFileChooser();
					if (OpenDialog.getDefaultDirectory()!=null) {
						fileChooser.setCurrentDirectory(new File(OpenDialog.getDefaultDirectory()));
					}
					fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					fileChooser.setMultiSelectionEnabled(true);
					int returnVal = fileChooser.showOpenDialog(model.view);
					if (returnVal != JFileChooser.APPROVE_OPTION) return;
					File[] files = fileChooser.getSelectedFiles();
					if (files == null || files.length==0) return;
					model.loadOperationCollections(files);
				}
			});
		}
		return jMenuItem63;
	}
	/**
	 * This method initializes jMenuItem64	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem64() {
		if (jMenuItem64 == null) {
			jMenuItem64 = new JMenuItem();
			jMenuItem64.setText("install menu...");
			jMenuItem64.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					JFileChooser fileChooser = new JFileChooser();
					if (OpenDialog.getDefaultDirectory()!=null) {
						fileChooser.setCurrentDirectory(new File(OpenDialog.getDefaultDirectory()));
					}
					fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					fileChooser.setMultiSelectionEnabled(false);
					int returnVal = fileChooser.showOpenDialog(model.view);
					if (returnVal != JFileChooser.APPROVE_OPTION) return;
					File file = fileChooser.getSelectedFile();
					if (file == null || !file.exists()) return;
					
					model.fillMenu(file, OperationsBox.fileExtension(), getOperationsMenu());
				}
			});
		}
		return jMenuItem64;
	}
	/**
	 * This method initializes jMenuItem65	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem65() {
		if (jMenuItem65 == null) {
			jMenuItem65 = new JMenuItem();
			jMenuItem65.setText("load...");
			jMenuItem65.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					JFileChooser fileChooser = new JFileChooser();
					if (OpenDialog.getDefaultDirectory()!=null) {
						fileChooser.setCurrentDirectory(new File(OpenDialog.getDefaultDirectory()));
					}
					fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					fileChooser.setMultiSelectionEnabled(true);
					int returnVal = fileChooser.showOpenDialog(model.view);
					if (returnVal != JFileChooser.APPROVE_OPTION) return;
					File[] files = fileChooser.getSelectedFiles();
					if (files == null || files.length==0) return;
					model.loadApplications(files);
				}
			});
		}
		return jMenuItem65;
	}
	/**
	 * This method initializes jMenuItem66	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem66() {
		if (jMenuItem66 == null) {
			jMenuItem66 = new JMenuItem();
			jMenuItem66.setText("install menu...");
			jMenuItem66.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					JFileChooser fileChooser = new JFileChooser();
					if (OpenDialog.getDefaultDirectory()!=null) {
						fileChooser.setCurrentDirectory(new File(OpenDialog.getDefaultDirectory()));
					}
					fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					fileChooser.setMultiSelectionEnabled(false);
					int returnVal = fileChooser.showOpenDialog(model.view);
					if (returnVal != JFileChooser.APPROVE_OPTION) return;
					File file = fileChooser.getSelectedFile();
					if (file == null || !file.exists()) return;
					model.fillMenu(file, Application.fileExtension(), getApplicationsMenu());
				}
			});
		}
		return jMenuItem66;
	}
	/**
	 * This method initializes aboutMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */    
	public JMenu getAboutMenu() {
		if (aboutMenu == null) {
			aboutMenu = new JMenu();
			aboutMenu.setText("About");
			aboutMenu.setMnemonic(java.awt.event.KeyEvent.VK_B);
			aboutMenu.add(getJMenuItem67());
		}
		return aboutMenu;
	}
	/**
	 * This method initializes jMenuItem67	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem67() {
		if (jMenuItem67 == null) {
			jMenuItem67 = new JMenuItem();
			jMenuItem67.setText("MRI Cell Image Analyzer");
			jMenuItem67.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					AboutView aboutView = new AboutView();
					aboutView.setVisible(true);
				}
			});
		}
		return jMenuItem67;
	}

	/**
	 * This method initializes showStackRegistratorMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getShowStackRegistratorMenuItem() {
		if (showStackRegistratorMenuItem == null) {
			showStackRegistratorMenuItem = new JMenuItem();
			showStackRegistratorMenuItem.setText("stack registrator");
			showStackRegistratorMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							(new StackRegistrator()).show();
						}
					});
		}
		return showStackRegistratorMenuItem;
	}

	public Label getJLabel() {
		return jLabel;
	}

	/**
	 * This method initializes openObjectModelingWorkbenchMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getOpenObjectModelingWorkbenchMenuItem() {
		if (openObjectModelingWorkbenchMenuItem == null) {
			openObjectModelingWorkbenchMenuItem = new JMenuItem();
			openObjectModelingWorkbenchMenuItem.setText("object modeling workbench");
			openObjectModelingWorkbenchMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					(new ObjectModelingWorkbench()).show();
				}
			});
		}
		return openObjectModelingWorkbenchMenuItem;
	}

	/**
	 * This method initializes jMenuItem68	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItem68() {
		if (jMenuItem68 == null) {
			jMenuItem68 = new JMenuItem();
			jMenuItem68.setText("magic wand");
			jMenuItem68.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					MagicWand.getInstance().show();
				}
			});
		}
		return jMenuItem68;
	}

	/**
	 * This method initializes objectModelingWorkbenchButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getObjectModelingWorkbenchButton() {
		if (objectModelingWorkbenchButton == null) {
			objectModelingWorkbenchButton = new JButton();
			objectModelingWorkbenchButton.setBounds(new java.awt.Rectangle(413,5,32,21));
			objectModelingWorkbenchButton.setFont(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 10));
			objectModelingWorkbenchButton.setToolTipText("open object modeling workbench");
			objectModelingWorkbenchButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							(new ObjectModelingWorkbench()).show();
						}
					});
			objectModelingWorkbenchButton.setText("OMW");
		}
		return objectModelingWorkbenchButton;
	}

	/**
	 * This method initializes magicWandButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getMagicWandButton() {
		if (magicWandButton == null) {
			magicWandButton = new JButton();
			magicWandButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/magic-wand.gif")));
			magicWandButton.setMaximumSize(new java.awt.Dimension(38,25));
			magicWandButton.setMinimumSize(new java.awt.Dimension(38,25));
			magicWandButton.setToolTipText("magic wand");
			magicWandButton.setPreferredSize(new java.awt.Dimension(38,25));
			magicWandButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					MagicWand.getInstance().show();
				}
			});
		}
		return magicWandButton;
	}

	/**
	 * This method initializes frapWizardMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getFrapWizardMenuItem() {
		if (frapWizardMenuItem == null) {
			frapWizardMenuItem = new JMenuItem();
			frapWizardMenuItem.setText("frap wizard");
			frapWizardMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					new FRAPWizard().show();
				}
			});
		}
		return frapWizardMenuItem;
	}
       }  //  @jve:decl-index=0:visual-constraint="12,20"
