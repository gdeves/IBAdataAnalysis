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

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import operations.Operation;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import java.awt.Toolkit;

/**
 * An empty frame into which operations can be droped to create 
 * a collection of operations. The collection can be saved from
 * the context menu of the box. If it is saved in the _operations
 * folder it will appear in the operations menu of the visual scripting 
 * launcher.
 * 
 * @author	Volker Baecker
 **/
public class OperationsBoxView extends javax.swing.JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private javax.swing.JPanel ivjJFrameContentPane = null;
	private JScrollPane jScrollPane = null;
	private JPanel jPanel = null;

	protected OperationsBox model;  //  @jve:decl-index=0:
	
	private JPopupMenu jPopupMenu = null;  //  @jve:decl-index=0:visual-constraint="314,71"
	private JMenuItem jMenuItem = null;  //  @jve:decl-index=0:visual-constraint="341,87"
	private JMenuItem jMenuItem1 = null;
	public OperationsBoxView() {
		super();
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
			ivjJFrameContentPane.setLayout(new BoxLayout(ivjJFrameContentPane, BoxLayout.Y_AXIS));
			ivjJFrameContentPane.add(getJScrollPane(), null);
		}
		return ivjJFrameContentPane;
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {

		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/icon.gif")));
		this.setName("JFrame1");
		this
				.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		this.setContentPane(getJFrameContentPane());
		this.setBounds(45, 25, 165, 273);
		this.setTitle("Operations");
		this.addWindowListener(new java.awt.event.WindowAdapter() {   
			public void windowActivated(java.awt.event.WindowEvent e) {    
				model.moveToFront((OperationsBoxView)getJPanel().getTopLevelAncestor());
			} 
			public void windowClosing(java.awt.event.WindowEvent e) {    
				model.operationsBoxViewClosed((OperationsBoxView)getJPanel().getTopLevelAncestor());
				dispose();
			}
		});
		this.setContentPane(getJFrameContentPane());

	}

	/**
	 * @param box
	 */
	public void setModel(OperationsBox box) {
		model = box;
	}

	/**
	 * @param model
	 */
	public void handleMouseDragged(Operation model, Point point, MouseEvent e) {
		Operation newOperation = (Operation) model.clone();
		newOperation.view().setLocation(point);
		newOperation.show();
		MouseListener mouseListener = newOperation.view().getJPanel().getMouseListeners()[0];
		mouseListener.mousePressed(e);
	}
	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */    
	public JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJPanel());
		}
		return jScrollPane;
	}
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	public JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
			jPanel.addMouseListener(new java.awt.event.MouseAdapter() { 
				public void mouseClicked(java.awt.event.MouseEvent e) {    
					if (e.getButton()==MouseEvent.BUTTON3) {
						getJPopupMenu().show(e.getComponent(), e.getX(), e.getY());
					}
				}
			});
		}
		return jPanel;
	}

	/**
	 * @param operation
	 * @param localPoint
	 */
	public void operationDropedAt(Operation operation, Point localPoint) {
		ArrayList<Operation> operations = model.getOperations();
		int addAfterIndex = -1;
		for (int i=0; i<operations.size(); i++) {
			Operation op = operations.get(i);
			Point opOrigin = new Point(op.view().getJPanel().getX(), op.view().getJPanel().getY());
			if (localPoint.getY()>opOrigin.getY()) {
				addAfterIndex = i;
			}
		}
		if (operation.view().isInApplicationBox()) return;
		if (operations.contains(operation)) return;
		model.addOperationAfterIndex(operation, addAfterIndex);
	}
	/**
	 * This method initializes jPopupMenu	
	 * 	
	 * @return javax.swing.JPopupMenu	
	 */    
	private JPopupMenu getJPopupMenu() {
		if (jPopupMenu == null) {
			jPopupMenu = new JPopupMenu();
			jPopupMenu.add(getJMenuItem1());
			jPopupMenu.add(getJMenuItem());
		}
		return jPopupMenu;
	}
	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem() {
		if (jMenuItem == null) {
			jMenuItem = new JMenuItem();
			jMenuItem.setText("save as...");
			jMenuItem.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					JFileChooser fileDialog = new JFileChooser();
					fileDialog.setDialogTitle("save collection as");
					fileDialog.setDialogType(JFileChooser.SAVE_DIALOG);
					fileDialog.setSelectedFile(new File(model.getPath()));
					int answer = fileDialog.showSaveDialog(model.getView());
					File file = fileDialog.getSelectedFile();
					if (answer != JFileChooser.APPROVE_OPTION) {
						return;
					}
					String path = file.getAbsolutePath();
					try {
						if (path.substring(0, path.length()-4).equals(OperationsBox.fileExtension())) {
							path = path + OperationsBox.fileExtension();
						}
						File outFile = new File(path);
						if (outFile.exists()) {
							int cont = JOptionPane.showConfirmDialog(null, "The file " + outFile.getName() + " already exists\nDo you want to overwrite it?", "overwrite file?",  JOptionPane.YES_NO_OPTION);
							if (cont!=JOptionPane.YES_OPTION) {
								return;
							}
						}
						((OperationsBox)model).save(path);
					} catch (IOException e1) {
						e1.printStackTrace();
						JOptionPane.showConfirmDialog(null, "An error occured while saving the collection\n" + e1.getMessage(), "error saving collection",  JOptionPane.CLOSED_OPTION);
					}
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
					OperationsBox box = (OperationsBox)model;
					if (box.getPath()==null) {
						jMenuItem.doClick();
					} else {
						try {
							box.save(box.getPath());
						} catch (IOException e1) {
							e1.printStackTrace();
							JOptionPane.showConfirmDialog(null,
									"An error occured while saving the application\n" + e1.getMessage(),  "error saving application", JOptionPane.CLOSED_OPTION);
						}
					}
				}
			});
		}
		return jMenuItem1;
	}

	/**
	 * @param model2
	 */
	public void removeOperation(Operation operation) {
		jPanel.remove(operation.view().getJPanel());
		model.removeOperation(operation);
		this.validate();
		
	}
    }  //  @jve:decl-index=0:visual-constraint="10,10"
