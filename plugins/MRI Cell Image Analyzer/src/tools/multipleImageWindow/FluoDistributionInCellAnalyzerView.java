package tools.multipleImageWindow;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.Toolkit;
import java.awt.Dimension;
import javax.swing.JButton;
import java.awt.Rectangle;
import java.awt.ComponentOrientation;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class FluoDistributionInCellAnalyzerView extends JFrame {

	private static final long serialVersionUID = 7598799855291209629L;

	private JPanel jContentPane = null;

	private JScrollPane jScrollPane = null;

	private JTable jTable = null;

	private FluoDistributionInCellAnalyzer model;

	private JPanel commandsPanel = null;

	private JButton addCellButton = null;

	private JButton setCytoplasmAndMembraneButton = null;

	private JButton setNucleusButton = null;

	private JButton setCytoplasmButton = null;

	private JButton setMembraneButton = null;

	private JButton measureButton = null;

	private JPanel lowerPanel = null;

	private JMenuBar jJMenuBar = null;

	private JMenu optionsMenu = null;

	private JMenuItem editOptionsMenuItem = null;

	/**
	 * This is the default constructor
	 */
	public FluoDistributionInCellAnalyzerView() {
		super();
		initialize();
	}

	public FluoDistributionInCellAnalyzerView(FluoDistributionInCellAnalyzer analyzer) {
		super();
		this.model = analyzer;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(487, 290);
		this.setJMenuBar(getJJMenuBar());
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/icon.gif")));
		this.setContentPane(getJContentPane());
		this.setTitle("MRI Fluo Distribution Analyzer");
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
			jContentPane.add(getJScrollPane(), BorderLayout.CENTER);
			jContentPane.add(getLowerPanel(), BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJTable());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getJTable() {
		if (jTable == null) {
			jTable = new JTable(model);
			jTable.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {
					ImagePlus image = IJ.getImage();
					if (image==null) return;
					int row = getJTable().getSelectedRow();
					int column = getJTable().getSelectedColumn();
					if (column>0) {
						Roi aRoi = (Roi)model.getValueAt(row, column);
						if (aRoi==null) return;
						Roi newRoi = (Roi)aRoi.clone();
						image.setRoi(newRoi);
					}
				}
			});
		}
		return jTable;
	}

	public JComponent getMainComponent() {
		return getJContentPane();
	}

	/**
	 * This method initializes commandsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getCommandsPanel() {
		if (commandsPanel == null) {
			commandsPanel = new JPanel();
			commandsPanel.setLayout(null);
			commandsPanel.setPreferredSize(new Dimension(100, 100));
			commandsPanel.add(getAddCellButton(), null);
			commandsPanel.add(getSetCytoplasmAndMembraneButton(), null);
			commandsPanel.add(getSetNucleusButton(), null);
			commandsPanel.add(getSetCytoplasmButton(), null);
			commandsPanel.add(getSetMembraneButton(), null);
			commandsPanel.add(getMeasureButton(), null);
		}
		return commandsPanel;
	}

	/**
	 * This method initializes addCellButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAddCellButton() {
		if (addCellButton == null) {
			addCellButton = new JButton();
			addCellButton.setText("add cell");
			addCellButton.setBounds(new Rectangle(10, 7, 78, 26));
			addCellButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int index = model.numberOfCells()+1;
					String name = Integer.toString(index);
					if (name.length()==1) name = "0" + name;
					while (model.getCells().contains(name)) {
						index++;
						name = Integer.toString(index);
						if (name.length()==1) name = "0" + name;
					}
					model.addCell(name);
					getJTable().setRowSelectionInterval(model.getRowCount()-1, model.getRowCount()-1);
				}
			});
		}
		return addCellButton;
	}

	/**
	 * This method initializes setCytoplasmAndMembraneButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSetCytoplasmAndMembraneButton() {
		if (setCytoplasmAndMembraneButton == null) {
			setCytoplasmAndMembraneButton = new JButton();
			setCytoplasmAndMembraneButton.setText("set cytoplasm and membrane");
			setCytoplasmAndMembraneButton.setBounds(new Rectangle(111, 7, 237, 26));
			setCytoplasmAndMembraneButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							Roi aRoi = null;
							ImagePlus image = IJ.getImage(); 
							if (image!=null) {
								aRoi = image.getRoi();
							}
							if (aRoi==null) return;
							int selectedRow = getJTable().getSelectedRow();
							if (selectedRow == -1) return;
							model.setCytoplasmAndMembrane(selectedRow, image);
							getJTable().setRowSelectionInterval(selectedRow, selectedRow);
						}
					});
		}
		return setCytoplasmAndMembraneButton;
	}

	/**
	 * This method initializes setNucleusButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSetNucleusButton() {
		if (setNucleusButton == null) {
			setNucleusButton = new JButton();
			setNucleusButton.setText("set nucleus");
			setNucleusButton.setBounds(new Rectangle(371, 7, 100, 26));
			setNucleusButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Roi aRoi = null;
					ImagePlus image = IJ.getImage(); 
					if (image!=null) {
						aRoi = image.getRoi();
					}
					if (aRoi==null) return;
					int selectedRow = getJTable().getSelectedRow();
					if (selectedRow == -1) return;
					model.setNucleus(selectedRow, image);
					getJTable().setRowSelectionInterval(selectedRow, selectedRow);
				}
			});
		}
		return setNucleusButton;
	}

	/**
	 * This method initializes setCytoplasmButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSetCytoplasmButton() {
		if (setCytoplasmButton == null) {
			setCytoplasmButton = new JButton();
			setCytoplasmButton.setText("set cytoplasm");
			setCytoplasmButton.setBounds(new Rectangle(111, 35, 114, 26));
			setCytoplasmButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Roi aRoi = null;
					ImagePlus image = IJ.getImage(); 
					if (image!=null) {
						aRoi = image.getRoi();
					}
					if (aRoi==null) return;
					int selectedRow = getJTable().getSelectedRow();
					if (selectedRow == -1) return;
					model.setCytoplasm(selectedRow, image);
					getJTable().setRowSelectionInterval(selectedRow, selectedRow);
				}
			});
		}
		return setCytoplasmButton;
	}

	/**
	 * This method initializes setMembraneButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSetMembraneButton() {
		if (setMembraneButton == null) {
			setMembraneButton = new JButton();
			setMembraneButton.setText("set membrane");
			setMembraneButton.setBounds(new Rectangle(231, 35, 117, 26));
			setMembraneButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Roi aRoi = null;
					ImagePlus image = IJ.getImage(); 
					if (image!=null) {
						aRoi = image.getRoi();
					}
					if (aRoi==null) return;
					int selectedRow = getJTable().getSelectedRow();
					if (selectedRow == -1) return;
					model.setMembrane(selectedRow, image);
					getJTable().setRowSelectionInterval(selectedRow, selectedRow);
				}
			});
		}
		return setMembraneButton;
	}

	/**
	 * This method initializes measureButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getMeasureButton() {
		if (measureButton == null) {
			measureButton = new JButton();
			measureButton.setBounds(new Rectangle(111, 71, 237, 26));
			measureButton.setText("measure");
			measureButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (model.getBlueImage()==null) {
						ImagePlus anImage = IJ.getImage();
						if (anImage==null) return;
						model.setRGBImage(anImage);
						model.setImageExisted(false);
					}
					if (model.getBlueImage()==null) return;
					model.measure();
					if (!model.imageExisted()) {
						model.setBlueImage(null);
					}
				}
			});
		}
		return measureButton;
	}

	/**
	 * This method initializes lowerPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getLowerPanel() {
		if (lowerPanel == null) {
			lowerPanel = new JPanel();
			lowerPanel.setLayout(new BorderLayout());
			lowerPanel.setPreferredSize(new Dimension(100, 100));
			lowerPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			lowerPanel.add(getCommandsPanel(), BorderLayout.SOUTH);
		}
		return lowerPanel;
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
			editOptionsMenuItem.setText("Edit...");
			editOptionsMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.getOptionsView().setVisible(true);
				}
			});
		}
		return editOptionsMenuItem;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
