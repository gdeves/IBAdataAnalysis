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

import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.Roi;
import ij.io.FileInfo;

import java.awt.Toolkit;
import javax.swing.JList;
import java.awt.BorderLayout;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * Manually measure replication sites in combed dna molecules. The user selects the
 * molecule with the segmented line tool. The add button adds the selection to the list.
 * If the first segment is green a checkbox has be checked. When a line is selected in
 * the list the selection is set to the active image. The selections can be saved and
 * loaded. A report of the measurements can be generated as a spreadsheet file.
 * 
 * @author	Volker Baecker
 **/
public class DNACombingToolView extends javax.swing.JFrame implements Observer {
	private static final long serialVersionUID = 1L;
	private javax.swing.JPanel ivjJFrameContentPane = null;
	private JList jList = null;
	private JScrollPane jScrollPane = null;
	private JCheckBox jCheckBox = null;
	private JButton jButton = null;
	private DnaCombingTool model;
	private JButton jButton1 = null;
	private JButton jButton2 = null;
	private JPanel jPanel = null;
	private JMenuBar jJMenuBar = null;
	private JMenu jMenu = null;
	private JMenuItem jMenuItem = null;
	private JMenuItem jMenuItem1 = null;
	private JMenuItem jMenuItem2 = null;
	
	public DNACombingToolView() {
		super();
		initialize();
	}
	
	public DNACombingToolView(DnaCombingTool tool) {
		super();
		this.model = tool;
		model.addObserver(this);
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
			ivjJFrameContentPane.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
			ivjJFrameContentPane.add(getJPanel(), java.awt.BorderLayout.SOUTH);
		}
		return ivjJFrameContentPane;
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {

		this.setJMenuBar(getJJMenuBar());
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/icon.gif")));
		this.setName("JFrame1");
		this
				.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		this.setBounds(45, 25, 213, 324);
		this.setTitle("DNA Combing Tool");
		this.setContentPane(getJFrameContentPane());

	}
	/**
	 * This method initializes jList	
	 * 	
	 * @return javax.swing.JList	
	 */    
	private JList getJList() {
		if (jList == null) {
			jList = new JList();
			jList.addListSelectionListener(new javax.swing.event.ListSelectionListener() { 
				public void valueChanged(javax.swing.event.ListSelectionEvent e) {    
					model.showSelections(getJList().getSelectedIndices());
				}
			});
			jList.setListData(model.getSelectionStrings());
		}
		return jList;
	}
	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */    
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJList());
		}
		return jScrollPane;
	}
	/**
	 * This method initializes jCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */    
	private JCheckBox getJCheckBox() {
		if (jCheckBox == null) {
			jCheckBox = new JCheckBox();
			jCheckBox.setText("first segment is green");
			jCheckBox.setBounds(5, 5, 191, 23);
			jCheckBox.setToolTipText("check if the first segment is green");
		}
		return jCheckBox;
	}
	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("add");
			jButton.setPreferredSize(new java.awt.Dimension(71,23));
			jButton.setBounds(5, 30, 88, 23);
			jButton.setToolTipText("add a brin to the manager");
			jButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ImagePlus image = WindowManager.getCurrentImage();
					Boolean firstSegmentIsGreen = new Boolean(getJCheckBox().isSelected());
					FileInfo fileInfo = image.getOriginalFileInfo();
					String filePath = fileInfo.directory + fileInfo.fileName;
					Roi roi = image.getRoi();
					model.addSelection(firstSegmentIsGreen, filePath, roi);
				}
			});
		}
		return jButton;
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable aModel, Object anAspect) {
		if (anAspect.equals("selections")) this.handleSelectionsChanged();
	}

	private void handleSelectionsChanged() {
		getJList().setListData(model.getSelectionStrings());
		
	}
	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setText("remove");
			jButton1.setBounds(109, 30, 88, 23);
			jButton1.setToolTipText("remove a brin from the manager");
			jButton1.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.removeSelections(getJList().getSelectedIndices());
				}
			});
		}
		return jButton1;
	}
	/**
	 * This method initializes jButton2	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton2() {
		if (jButton2 == null) {
			jButton2 = new JButton();
			jButton2.setText("measure");
			jButton2.setBounds(5, 57, 88, 23);
			jButton2.setToolTipText("measure the selected brins");
			jButton2.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.measure();
				}
			});
		}
		return jButton2;
	}
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(null);
			jPanel.setPreferredSize(new java.awt.Dimension(10,90));
			jPanel.add(getJCheckBox(), null);
			jPanel.add(getJButton(), null);
			jPanel.add(getJButton1(), null);
			jPanel.add(getJButton2(), null);
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
			jMenu.setMnemonic(java.awt.event.KeyEvent.VK_F);
			jMenu.add(getJMenuItem());
			jMenu.add(getJMenuItem1());
			jMenu.add(getJMenuItem2());
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
			jMenuItem.setText("open...");
			jMenuItem.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.open();
				}
			});
		}
		return jMenuItem;
	}
	/**
	 * This method initializes jMenuItem1	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem1() {
		if (jMenuItem1 == null) {
			jMenuItem1 = new JMenuItem();
			jMenuItem1.setText("save");
			jMenuItem1.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					if (model.getPath()!=null && (new File(model.getPath())).exists()) {
						model.save(model.getPath());
					} else {
						model.saveAs();
					}
				}
			});
		}
		return jMenuItem1;
	}
	/**
	 * This method initializes jMenuItem2	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem2() {
		if (jMenuItem2 == null) {
			jMenuItem2 = new JMenuItem();
			jMenuItem2.setText("save as...");
			jMenuItem2.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.saveAs();
				}
			});
		}
		return jMenuItem2;
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
