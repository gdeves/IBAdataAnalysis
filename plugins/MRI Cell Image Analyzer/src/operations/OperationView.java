/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2007 Volker Bäcker
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
package operations;

import gui.OperationsBox;
import gui.OperationsBoxView;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;

import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

import applications.Application;
import applications.ApplicationView;
import javax.swing.JProgressBar;
import javax.swing.JCheckBoxMenuItem;
/**
 * The tile representation of an operation has an empty top area,
 * in which the operation can be grabed for drag and drop. A right-
 * click in this area opens the operation's context menu. The keep 
 * source option in the context menu means that the operation  
 * 
 * @author	Volker Baecker
 **/
public class OperationView extends javax.swing.JFrame implements Observer {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1698013547790766548L;

	protected Operation model;

	protected Point origin = new Point();

	protected String lastMouseAction = "none";

	private JPanel jPanel = null;

	private JButton jButton = null;

	private JButton runOperationButton = null;

	private JButton optionsButton = null;

	private JButton parameterButton = null;

	private JPanel jPanel1 = null;

	private JPanel jPanel2 = null;

	private JButton jButton4 = null;

	private JPopupMenu jPopupMenu = null;  //  @jve:decl-index=0:visual-constraint="311,67"
	private JMenuItem jMenuItem = null;
	private JMenuItem jMenuItem1 = null;  //  @jve:decl-index=0:visual-constraint="341,86"
	private JMenuItem jMenuItem2 = null;
	private JProgressBar jProgressBar = null;  //  @jve:decl-index=0:visual-constraint="335,158"
	
	private JMenuItem jMenuItem3 = null;
	private JMenuItem jMenuItem4 = null;

	private JCheckBoxMenuItem keepSourceCheckBoxMenuItem = null;
	public OperationView(Operation model) {
		super();
		this.model = model;
		model.addObserver(this);
		initialize();
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {

		this.setContentPane(getJPanel());
		this.setName("JFrame1");
		int width = (int) (this.getHelpButton().getPreferredSize().getWidth()
				+ this.getRunOperationButton().getPreferredSize().getWidth()
				+ this.getOptionsButton().getPreferredSize().getWidth()
				+ this.getParameterButton().getPreferredSize().getWidth() + 10);
		this.setBounds(0, 25, width, 50);
		this.setUndecorated(true);
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	public JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new BorderLayout());
			jPanel
					.setBorder(javax.swing.BorderFactory
							.createCompoundBorder(
									javax.swing.BorderFactory
											.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED),
									javax.swing.BorderFactory
											.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED)));
			jPanel.setAlignmentY(0.5F);
			jPanel.setMaximumSize(new java.awt.Dimension(400,50));
			jPanel.add(getJPanel2(), java.awt.BorderLayout.NORTH);
			jPanel.add(getJPanel1(), java.awt.BorderLayout.SOUTH);
			jPanel.addMouseListener(new java.awt.event.MouseAdapter() {   
				public void mouseClicked(java.awt.event.MouseEvent e) {  
					// if (model.isApplication()) {
						if (e.getButton()==MouseEvent.BUTTON3) {
							getJPopupMenu().show(e.getComponent(), e.getX(), e.getY());
					//	}
					}
				}
				public void mouseReleased(java.awt.event.MouseEvent e) {
					lastMouseAction = "released";
					Application.checkForDropAction(model, e);
					OperationsBox.checkForDropAction(model, e);
				}

				public void mousePressed(java.awt.event.MouseEvent e) {
					lastMouseAction = "pressed";
					origin.x = e.getX();
					origin.y = e.getY();
				}
			});
			jPanel
					.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
						public void mouseDragged(java.awt.event.MouseEvent e) {
							if (!lastMouseAction.equals("dragged")) {
								Container app = jPanel.getTopLevelAncestor();
								if (app.getClass() == OperationsBoxView.class) {
									OperationsBoxView realApp = (OperationsBoxView) app;
									Point point = e.getPoint();
									SwingUtilities.convertPointToScreen(point,
											jPanel);
									MouseListener mouseListener = jPanel
											.getMouseListeners()[0];
									mouseListener.mouseReleased(e);
									realApp.handleMouseDragged(model, point, e);
									lastMouseAction = "dragged";
									return;
								}
							}
							lastMouseAction = "dragged";
							Point p = getLocation();
							setLocation(p.x + e.getX() - origin.x, p.y
									+ e.getY() - origin.y);
						}
					});
		}
		return jPanel;
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getHelpButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("?");
			jButton.setFont(new java.awt.Font("Courier New", java.awt.Font.PLAIN, 11));
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.openHelp();
				}
			});
		}
		return jButton;
	}

	/**
	 * This method initializes jButton1
	 * 
	 * @return javax.swing.JButton
	 */
	public JButton getRunOperationButton() {
		if (runOperationButton == null) {
			runOperationButton = new JButton();
			if (model.isMacroOperation()) runOperationButton.setText(model.macroName());
			else
				runOperationButton.setText(model.name());
				runOperationButton.setFont(new java.awt.Font("Courier New", java.awt.Font.PLAIN, 11));
				runOperationButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.execute();
				}
			});
		}
		return runOperationButton;
	}

	/**
	 * This method initializes jButton2
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getOptionsButton() {
		if (optionsButton == null) {
			optionsButton = new JButton();
			optionsButton.setText("O");
			optionsButton.setFont(new java.awt.Font("Courier New", java.awt.Font.PLAIN, 11));
			if (model.getOptionsNames().length<1) {
				optionsButton.setEnabled(false);
			} else {
				optionsButton.setEnabled(true);
			}
			optionsButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.getOptions().setOperation(model);
					model.getOptions().view().setVisible(true);
				}
			});
		}
		return optionsButton;
	}

	/**
	 * This method initializes jButton3
	 * 
	 * @return javax.swing.JButton
	 */
	public JButton getParameterButton() {
		if (parameterButton == null) {
			parameterButton = new JButton();
			parameterButton.setText("P");
			parameterButton.setFont(new java.awt.Font("Courier New", java.awt.Font.PLAIN, 11));
			if (!this.isInApplicationBox() || (model.getParameterNames()==null || model.getParameterNames().length<1)) {
				parameterButton.setEnabled(false);
			}
			parameterButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					ApplicationView topView = (ApplicationView)getJPanel().getTopLevelAncestor();
					topView.getModel().showParameterViewForOperation(model);
				}
			});
		}
		return parameterButton;
	}

	public boolean isInApplicationBox() {
		if (getJPanel().getTopLevelAncestor()==null) return false;
		String name = getJPanel().getTopLevelAncestor().getClass().getSimpleName();
		return (name.equals(ApplicationView.class.getSimpleName()));
	}
	
	public boolean isInOperationsBox() {
		if (getJPanel().getTopLevelAncestor()==null) return false;
		String name = getJPanel().getTopLevelAncestor().getClass().getSimpleName();
		return (name.equals(OperationsBoxView.class.getSimpleName()));
	}
	
	/**
	 * This method initializes jPanel1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.setLayout(new BoxLayout(jPanel1, BoxLayout.X_AXIS));
			jPanel1.add(getHelpButton(), null);
			jPanel1.add(getRunOperationButton(), null);
			jPanel1.add(getOptionsButton(), null);
			jPanel1.add(getParameterButton(), null);
		}
		return jPanel1;
	}

	/**
	 * This method initializes jPanel2
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			jPanel2 = new JPanel();
			jPanel2.setLayout(new BorderLayout());
			jPanel2.setPreferredSize(new java.awt.Dimension(12,12));
			jPanel2.setMinimumSize(new java.awt.Dimension(11,19));
			jPanel2.add(getJButton4(), java.awt.BorderLayout.EAST);
		}
		return jPanel2;
	}

	/**
	 * This method initializes jButton4
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton4() {
		if (jButton4 == null) {
			jButton4 = new JButton();
			jButton4.setText("X");
			jButton4.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
			jButton4.setToolTipText("close the operation's view");
			jButton4.setMargin(new java.awt.Insets(1,1,1,1));
			jButton4.setMinimumSize(new java.awt.Dimension(10,10));
			jButton4.setBackground(new java.awt.Color(213,12,12));
			jButton4.setDefaultCapable(false);
			jButton4.setFocusPainted(false);
			jButton4.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					try {
						finalize();
					} catch (Throwable e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					dispose();
					Container app = jPanel.getTopLevelAncestor();
					if (app.getClass() == ApplicationView.class) {
						((ApplicationView)app).removeOperation(model);
					}
					if (app.getClass() == OperationsBoxView.class) {
						((OperationsBoxView)app).removeOperation(model);
					}
				}
			});
			jButton4.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
			jButton4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			jButton4.setForeground(java.awt.Color.white);
			jButton4.setPreferredSize(new java.awt.Dimension(20,16));
			jButton4
					.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 10));
		}
		return jButton4;
	}
	/**
	 * This method initializes jPopupMenu	
	 * 	
	 * @return javax.swing.JPopupMenu	
	 */    
	private JPopupMenu getJPopupMenu() {
		if (jPopupMenu == null) {
			jPopupMenu = new JPopupMenu();
			if (model.isApplication()) {
				jPopupMenu.add(getJMenuItem());
				jPopupMenu.add(getJMenuItem2());
				jPopupMenu.add(getJMenuItem1());
				jPopupMenu.addSeparator();
				jPopupMenu.add(getJMenuItem3());
				jPopupMenu.add(getJMenuItem4());
			} else {
				jPopupMenu.add(getKeepSourceCheckBoxMenuItem());
			}
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
			jMenuItem.setText("open");
			jMenuItem.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.showApplicationView();
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
			jMenuItem1.setText("save as...");
			jMenuItem1.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) { 
					JFileChooser fileDialog = new JFileChooser();
					fileDialog.setDialogTitle("save application as");
					fileDialog.setDialogType(JFileChooser.SAVE_DIALOG);
					fileDialog.setSelectedFile(new File(((Application)model).getPath()));
					int answer = fileDialog.showSaveDialog(model.view());
					File file = fileDialog.getSelectedFile();
					if (answer != JFileChooser.APPROVE_OPTION) {
						return;
					}
					String path = file.getAbsolutePath();
					try {
						if (path.substring(0, path.length()-4).equals(Application.fileExtension())) {
							path = path + OperationsBox.fileExtension();
						}
						File outFile = new File(path);
						if (outFile.exists()) {
							int cont = JOptionPane.showConfirmDialog(null, "The file " + outFile.getName() + " already exists\nDo you want to overwrite it?", "overwrite file?",  JOptionPane.YES_NO_OPTION);
							if (cont!=JOptionPane.YES_OPTION) {
								return;
							}
						}
						((Application)model).save(file.getAbsolutePath());
					} catch (IOException e1) {
						e1.printStackTrace();
						JOptionPane.showConfirmDialog(null,
									"An error occured while saving the application\n" + e1.getMessage(), "error saving application", JOptionPane.CLOSED_OPTION);
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
			jMenuItem2.setText("save");
			jMenuItem2.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Application app = (Application)model;
					if (app.getPath()==null) {
						jMenuItem1.doClick();
					} else {
						try {
							app.save(app.getPath());
						} catch (IOException e1) {
							e1.printStackTrace();
							JOptionPane.showConfirmDialog(null, 
										"An error occured while saving the application\n" + e1.getMessage(), "error saving application", JOptionPane.CLOSED_OPTION);
						}
					}
				}
			});
		}
		return jMenuItem2;
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable model, Object aspect) {
		if (aspect.equals("input map")) {
			this.handleChangedInputMap();
		}
		if (aspect.equals("progressMax")) {
			this.handleChangedProgressMax();
		}
		if (aspect.equals("progress")) {
			this.handleChangedProgress();
		}
		if (aspect.equals("isRunning")) {
			this.handleChangedIsRunning();
		}
		if (aspect.equals("name")) {
			this.handleChangedName();
		}
	}

	public void handleChangedName() {
		this.getRunOperationButton().setText(model.macroName());
	}

	/**
	 * 
	 */
	private void handleChangedIsRunning() {
		if (model.isRunning) {
			this.getJPanel2().add(this.getJProgressBar(),  java.awt.BorderLayout.CENTER);
			this.update(this.getGraphics());
			if (!model.isApplication()) {
				this.getJProgressBar().setIndeterminate(true);
			}
		} else {
			if (!model.isApplication()) {
				this.getJProgressBar().setIndeterminate(false);
			}
			this.getJPanel2().remove(this.getJProgressBar());
			this.repaint();
			if (isInOperationsBox()) {
				OperationsBoxView box = (OperationsBoxView) (getJPanel().getTopLevelAncestor());
				if (box.isVisible()) {
					box.setVisible(true);
				}
			}
			getJPanel().getTopLevelAncestor().repaint();
		}
	}

	/**
	 * 
	 */
	private void handleChangedProgress() {
		JProgressBar progressBar = this.getJProgressBar();
		progressBar.setValue(model.getProgress());
		this.update(this.getGraphics());
	}

	/**
	 * 
	 */
	private void handleChangedProgressMax() {
		JProgressBar progressBar = this.getJProgressBar();
		progressBar.setMaximum(model.getProgressMax());
	}

	/**
	 * 
	 */
	private void handleChangedInputMap() {
		JButton parameterButton = this.getParameterButton();
		if (model.isConnected()) {
			parameterButton.setBackground(this.getHelpButton().getBackground());
		} else {
			parameterButton.setBackground(Color.magenta);
		}
		
	}
	/**
	 * This method initializes jProgressBar	
	 * 	
	 * @return javax.swing.JProgressBar	
	 */    
	private JProgressBar getJProgressBar() {
		if (jProgressBar == null) {
			jProgressBar = new JProgressBar();
			jProgressBar.setSize(86, 10);
		}
		return jProgressBar;
	}
	/**
	 * This method initializes jMenuItem3	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem3() {
		if (jMenuItem3 == null) {
			jMenuItem3 = new JMenuItem();
			jMenuItem3.setText("run");
			jMenuItem3.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					if (!model.isRunning) {
						getRunOperationButton().doClick();
					}
				}
			});
		}
		return jMenuItem3;
	}
	/**
	 * This method initializes jMenuItem4	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */    
	private JMenuItem getJMenuItem4() {
		if (jMenuItem4 == null) {
			jMenuItem4 = new JMenuItem();
			jMenuItem4.setText("stop");
			jMenuItem4.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					if (model.isRunning) {
						((Application)model).stop();
					}
				}
			});
		}
		return jMenuItem4;
	}

	/**
	 * This method initializes keepSourceCheckBoxMenuItem	
	 * 	
	 * @return javax.swing.JCheckBoxMenuItem	
	 */
	private JCheckBoxMenuItem getKeepSourceCheckBoxMenuItem() {
		if (keepSourceCheckBoxMenuItem == null) {
			keepSourceCheckBoxMenuItem = new JCheckBoxMenuItem();
			keepSourceCheckBoxMenuItem.setSelected(model.isKeepSource());
			keepSourceCheckBoxMenuItem.setText("keep source");
			keepSourceCheckBoxMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							model.setKeepSource(!model.isKeepSource());
						}
					});
		}
		return keepSourceCheckBoxMenuItem;
	}
       } //  @jve:decl-index=0:visual-constraint="57,37"
