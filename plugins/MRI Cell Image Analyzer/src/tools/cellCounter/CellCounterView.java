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
package tools.cellCounter;

import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.WindowManager;
import ij.gui.PointRoi;
import ij.io.FileInfo;
import java.awt.BorderLayout;

import javax.swing.JColorChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JButton;

import java.awt.Color;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import java.awt.Toolkit;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * The cell counter view has a list of object classes per image. When a row is selected
 * the selection is set to the current image. The user can add and delete classes. He
 * can set the selection to the selection on the current image or add the selection of the
 * current image to a selection. He can run a command to automatically detect all cells or
 * to detect cells similar to the ones currently selected.
 * 
 * The cell counter is currently unused and its further development frozen. 
 * 
 * @author	Volker Baecker
 **/
public class CellCounterView extends javax.swing.JFrame implements Observer {

	private static final long serialVersionUID = -3875589004711572218L;

	private javax.swing.JPanel ivjJFrameContentPane = null;

	private JScrollPane jScrollPane = null;
	private JList jList = null;
	private JPanel jPanel = null;
	private JButton jButton = null;
	private JPanel jPanel1 = null;
	private JButton jButton1 = null;
	private JButton jButton2 = null;
	private JButton jButton3 = null;
	private JButton jButton4 = null;

	protected CellCounter model;
	private JPanel jPanel2 = null;
	private JLabel jLabel = null;
	private JTextField jTextField = null;
	private JMenuBar jJMenuBar = null;
	private JMenu jMenu = null;
	private JMenuItem jMenuItem = null;
	private JMenuItem jMenuItem1 = null;
	private JMenuItem jMenuItem2 = null;
	private JButton jButton5 = null;
	private JButton jButton6 = null;
	private JButton jButton7 = null;
	private JButton jButton8 = null;

	private JButton addButton = null;

	private JButton measureButton = null;
	public CellCounterView() {
		super();
		initialize();
	}

	public CellCounterView(CellCounter model) {
		super();
		this.model = model;
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
			ivjJFrameContentPane.add(getJPanel(), java.awt.BorderLayout.EAST);
			ivjJFrameContentPane.add(getJPanel1(), java.awt.BorderLayout.SOUTH);
			ivjJFrameContentPane.add(getJPanel2(), java.awt.BorderLayout.NORTH);
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
		this.setBounds(45, 25, 225, 309);
		this.setTitle("MRI Cell Counter");
		this.setContentPane(getJFrameContentPane());

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
	 * This method initializes jList	
	 * 	
	 * @return javax.swing.JList	
	 */    
	private JList getJList() {
		if (jList == null) {
			jList = new JList();
			jList.addListSelectionListener(new javax.swing.event.ListSelectionListener() { 
				public void valueChanged(javax.swing.event.ListSelectionEvent e) { 
					updateSelections();
				}
			});
		}
		return jList;
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
			jPanel.setPreferredSize(new java.awt.Dimension(120,23));
			jPanel.setMinimumSize(new java.awt.Dimension(120,23));
			jPanel.setMaximumSize(new java.awt.Dimension(120,23));
			jPanel.add(getJButton(), null);
			jPanel.add(getJButton4(), null);
			jPanel.add(getJButton3(), null);
			jPanel.add(getJButton2(), null);
			jPanel.add(getJButton5(), null);
			jPanel.add(getJButton6(), null);
			jPanel.add(getJButton7(), null);
			jPanel.add(getJButton8(), null);
		}
		return jPanel;
	}
	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("new class");
			jButton.setPreferredSize(new java.awt.Dimension(120,23));
			jButton.setMaximumSize(new java.awt.Dimension(120,23));
			jButton.setMinimumSize(new java.awt.Dimension(130,23));
			jButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					String name = JOptionPane.showInputDialog("Enter the name of the class!");
					if (!model.existsObjectClass(name)) {
						model.addObjectClass(name);
						getJList().setSelectedValue(name, true);
					}
				}
			});
		}
		return jButton;
	}
	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.add(getMeasureButton(), null);
			jPanel1.add(getJButton1(), null);
			jPanel1.add(getAddButton(), null);
		}
		return jPanel1;
	}
	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setText("set");
			jButton1.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ImagePlus image = IJ.getImage();
					if (image==null) return;
					PointRoi aRoi = (PointRoi)image.getRoi();
					FileInfo imageFile = image.getOriginalFileInfo();
					if (imageFile==null) imageFile = image.getFileInfo();
					Object[] selectedValues = getJList().getSelectedValues();
					int[] selectedIndices = getJList().getSelectedIndices();
					model.addSelectionsTo(aRoi, imageFile, selectedValues);
					getJList().setSelectedIndices(selectedIndices);
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
			jButton2.setText("delete class");
			jButton2.setMaximumSize(new java.awt.Dimension(120,23));
			jButton2.setPreferredSize(new java.awt.Dimension(120,23));
			jButton2.setMinimumSize(new java.awt.Dimension(120,23));
			jButton2.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					Object[] selectedValues = getJList().getSelectedValues();
					if (selectedValues==null || selectedValues.length==0) return;
					String text = "Do you really want to delete " + selectedValues[0]; 
					if (selectedValues.length > 1) {
						text = "Do you really want to delete " + selectedValues.length + " object classes?";
					}
					int answer = JOptionPane.showConfirmDialog(null, text);
					if (answer!=JOptionPane.YES_OPTION) return;
					model.deleteObjectClasses(selectedValues);
				}
			});
		}
		return jButton2;
	}
	/**
	 * This method initializes jButton3	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton3() {
		if (jButton3 == null) {
			jButton3 = new JButton();
			jButton3.setText(" ");
			jButton3.setMaximumSize(new java.awt.Dimension(120,23));
			jButton3.setMinimumSize(new java.awt.Dimension(120,23));
			jButton3.setRolloverEnabled(false);
			jButton3.setEnabled(false);
			jButton3.setPreferredSize(new java.awt.Dimension(120,23));
			jButton3.setVisible(true);
		}
		return jButton3;
	}
	/**
	 * This method initializes jButton4	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton4() {
		if (jButton4 == null) {
			jButton4 = new JButton();
			jButton4.setText("change color");
			jButton4.setPreferredSize(new java.awt.Dimension(120,23));
			jButton4.setMinimumSize(new java.awt.Dimension(120,23));
			jButton4.setMaximumSize(new java.awt.Dimension(120,23));
			jButton4.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {   
					Color color = JColorChooser.showDialog(
		                     null,
		                     "Choose a color",
		                     Prefs.getColor(Prefs.ROICOLOR,Color.yellow));
					if (color==null) return;
					model.setColor(getJList().getSelectedValues(), color);
					updateSelections();
				}
			});
		}
		return jButton4;
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable sender, Object aspect) {
		if (aspect.equals("objectClasses")) {
			this.updateObjectClasses();
		}
		if (aspect.equals("selections")) {
			this.updateSelections();
		}
	}

	/**
	 * 
	 */
	private void updateSelections() {
		this.getJTextField().setText(("" + model.getCount(this.getJList().getSelectedValues())));
		ImagePlus image = WindowManager.getCurrentImage();
		if (image==null) return;
		image.setRoi(model.getCurrentSelection(getJList().getSelectedValues()));
	}

	private void updateObjectClasses() {
		this.getJList().setListData(model.getObjectClassNames());
	}
	/**
	 * This method initializes jPanel2	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			jLabel = new JLabel();
			jPanel2 = new JPanel();
			jLabel.setText("count:");
			jPanel2.add(jLabel, null);
			jPanel2.add(getJTextField(), null);
		}
		return jPanel2;
	}
	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setPreferredSize(new java.awt.Dimension(150,19));
			jTextField.setEditable(false);
		}
		return jTextField;
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
			jMenu.setText("file");
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
			jMenuItem.setText("load...");
			jMenuItem.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.load();
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
					if (model.getPath()!=null) {
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
	/**
	 * This method initializes jButton5	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton5() {
		if (jButton5 == null) {
			jButton5 = new JButton();
			jButton5.setMaximumSize(new java.awt.Dimension(120,23));
			jButton5.setMinimumSize(new java.awt.Dimension(120,23));
			jButton5.setEnabled(false);
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
			jButton6.setMaximumSize(new java.awt.Dimension(120,23));
			jButton6.setMinimumSize(new java.awt.Dimension(120,23));
			jButton6.setPreferredSize(new java.awt.Dimension(120,23));
			jButton6.setText("select all cells");
			jButton6.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.selectAllCells();
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
			jButton7.setText("select similar");
			jButton7.setPreferredSize(new java.awt.Dimension(120,23));
			jButton7.setMaximumSize(new java.awt.Dimension(120,23));
			jButton7.setMinimumSize(new java.awt.Dimension(120,23));
			jButton7.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) { 
					String input = JOptionPane.showInputDialog("max. distance:", this);
					String newName = JOptionPane.showInputDialog("name of new class:", this);
					if (newName.trim().equals("") || model.existsObjectClass(newName)) return;
					double distance = 0d;
					try {
						distance = Double.parseDouble(input);
					} catch (Exception exc) {
						return;
					}
					model.selectSimilar(getJList().getSelectedValues(), distance, newName);
				}
			});
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
			jButton8.setText("select similar tool");
			jButton8.setMaximumSize(new java.awt.Dimension(120,23));
			jButton8.setMinimumSize(new java.awt.Dimension(120,23));
			jButton8.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.openSelectSimilarTool(getJList().getSelectedValue());
				}
			});
		}
		return jButton8;
	}

	/**
	 * This method initializes addButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAddButton() {
		if (addButton == null) {
			addButton = new JButton();
			addButton.setText("add");
			addButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ImagePlus image = IJ.getImage();
					if (image==null) return;
					PointRoi aRoi = (PointRoi)image.getRoi();
					FileInfo imageFile = image.getOriginalFileInfo();
					if (imageFile==null) imageFile = image.getFileInfo();
					
					model.addSelection(aRoi, imageFile);
				}
			});
		}
		return addButton;
	}

	/**
	 * This method initializes measureButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getMeasureButton() {
		if (measureButton == null) {
			measureButton = new JButton();
			measureButton.setText("report all");
			measureButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.reportAll();
				}
			});
		}
		return measureButton;
	}
                    }  //  @jve:decl-index=0:visual-constraint="123,14"
