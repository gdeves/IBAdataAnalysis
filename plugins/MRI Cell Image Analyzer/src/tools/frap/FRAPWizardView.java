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
package tools.frap;

import java.awt.BorderLayout;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.ListSelectionModel;

import java.awt.Dimension;
import javax.swing.JButton;
import java.awt.Rectangle;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JCheckBox;
import java.awt.Toolkit;
import javax.swing.ImageIcon;

/**
 * The frap wizard view has a button to add folders . Each folder represents one cell and contains 
 * a time series of volume images in stk format. Each cell is shown in one row of the table. The 
 * selected cells can be removed with the "remove selected" button. When a row in the table is selected
 * the projections of the volume images of the cell are loaded. The user can make a selection to restrain 
 * the area in which to search for the max. cube. If a selection has been made previously it is
 * automatically loaded with the image. Instead of projection the user can decide to load the whole stacks.
 * With the run button the batch application is started. It creates a spreadsheet file with the
 * measured results and control images showing where the best cube has been found for each cell.
 * 
 * @author	Volker Baecker
 **/
public class FRAPWizardView extends JFrame implements Observer {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JPanel commandPanel = null;

	private JButton addFoldersButton = null;

	private JScrollPane jScrollPane = null;

	private JTable jTable = null;

	private JButton removeSelectedButton = null;

	private JButton previousStackButton = null;

	private JButton nextStackButton = null;

	private JButton previousSeriesButton = null;

	private JButton nextSeriesButton = null;

	private JPanel runPanel = null;

	private JButton runButton = null;

	private JButton saveSelectionButton = null;

	private JButton closeButton = null;

	protected FRAPWizard model;

	private JCheckBox closeSessionCheckBox = null;

	private JCheckBox showProjectionsBox = null;

	FRAPWizardView() {
		super();
		initialize();
	}

	public FRAPWizardView(FRAPWizard wizard) {
		super();
		model = wizard;
		initialize();
		model.addObserver(this);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	private void initialize() {
		this.setSize(379, 457);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/icon.gif")));
		this.setContentPane(getJContentPane());
		this.setTitle("MRI FRAP Wizard");
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
			jContentPane.add(getCommandPanel(), BorderLayout.NORTH);
			jContentPane.add(getJScrollPane(), BorderLayout.CENTER);
			jContentPane.add(getRunPanel(), BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	public void update(Observable arg0, Object arg1) {
		if (arg1=="folderList") this.updateFolderList();
	}

	protected void updateFolderList() {
		this.getJTable().invalidate();
		this.getJTable().repaint();
	}

	/**
	 * This method initializes commandPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getCommandPanel() {
		if (commandPanel == null) {
			commandPanel = new JPanel();
			commandPanel.setLayout(null);
			commandPanel.setPreferredSize(new Dimension(1, 130));
			commandPanel.add(getAddFoldersButton(), null);
			commandPanel.add(getRemoveSelectedButton(), null);
			commandPanel.add(getPreviousStackButton(), null);
			commandPanel.add(getNextStackButton(), null);
			commandPanel.add(getPreviousSeriesButton(), null);
			commandPanel.add(getNextSeriesButton(), null);
			commandPanel.add(getShowProjectionsBox(), null);
		}
		return commandPanel;
	}

	/**
	 * This method initializes addFoldersButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAddFoldersButton() {
		if (addFoldersButton == null) {
			addFoldersButton = new JButton();
			addFoldersButton.setBounds(new Rectangle(9, 16, 150, 32));
			addFoldersButton.setText("add folders");
			addFoldersButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					FolderListEditor editor = new FolderListEditor();
					editor.show();
					model.addFolders(editor.getList());
				}
			});
		}
		return addFoldersButton;
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
			jTable = new JTable(model.getData());
			ListSelectionModel rowSM = jTable.getSelectionModel();
			rowSM.addListSelectionListener(new ListSelectionListener() {
			    public void valueChanged(ListSelectionEvent e) { 
			        //Ignore extra messages.
			        if (e.getValueIsAdjusting()) return;
			        ListSelectionModel lsm =
			            (ListSelectionModel)e.getSource();
			        if (lsm.isSelectionEmpty()) return;
			        int index = lsm.getMinSelectionIndex();
			        if (index==model.lastSelectionIndex) return;
			        if (model.isShowProjections()) model.showProjection(index); 
			        else model.showImageNumber(index);
			        lsm.setSelectionInterval(index, index);
			    }
			});
		}
		return jTable;
	}

	/**
	 * This method initializes removeSelectedButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getRemoveSelectedButton() {
		if (removeSelectedButton == null) {
			removeSelectedButton = new JButton();
			removeSelectedButton.setBounds(new Rectangle(9, 62, 150, 32));
			removeSelectedButton.setText("remove selected");
			removeSelectedButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int[] indices = getJTable().getSelectedRows();
					model.remove(indices);
				}
			});
		}
		return removeSelectedButton;
	}

	/**
	 * This method initializes previousStackButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getPreviousStackButton() {
		if (previousStackButton == null) {
			previousStackButton = new JButton();
			previousStackButton.setBounds(new Rectangle(214, 41, 41, 35));
			previousStackButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/go-previous.gif")));
			previousStackButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int index = getJTable().getSelectedRow();
					model.decrementTimePoint();
					getJTable().getSelectionModel().setSelectionInterval(index, index);
				}
			});
		}
		return previousStackButton;
	}

	/**
	 * This method initializes nextStackButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getNextStackButton() {
		if (nextStackButton == null) {
			nextStackButton = new JButton();
			nextStackButton.setBounds(new Rectangle(295, 41, 41, 35));
			nextStackButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/go-next.gif")));
			nextStackButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    int index = getJTable().getSelectedRow();
					model.incrementTimePoint();
					getJTable().getSelectionModel().setSelectionInterval(index, index);
				}
			});
		}
		return nextStackButton;
	}

	/**
	 * This method initializes previousSeriesButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getPreviousSeriesButton() {
		if (previousSeriesButton == null) {
			previousSeriesButton = new JButton();
			previousSeriesButton.setBounds(new Rectangle(254, 5, 41, 35));
			previousSeriesButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/go-up.gif")));
			previousSeriesButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (getJTable().getRowCount()<=0) return;
					int newRow = 0;
					int row = getJTable().getSelectedRow();
					if (row>0) newRow = row - 1;
					getJTable().getSelectionModel().setSelectionInterval(newRow, newRow);
				}
			});
		}
		return previousSeriesButton;
	}

	/**
	 * This method initializes nextSeriesButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getNextSeriesButton() {
		if (nextSeriesButton == null) {
			nextSeriesButton = new JButton();
			nextSeriesButton.setBounds(new Rectangle(254, 77, 41, 35));
			nextSeriesButton.setIcon(new ImageIcon(getClass().getResource("/resources/images/go-down.gif")));
			nextSeriesButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int newRow = getJTable().getRowCount()-1;
					int row = getJTable().getSelectedRow();
					if (row<getJTable().getRowCount()-1) newRow = row + 1;
					getJTable().getSelectionModel().setSelectionInterval(newRow, newRow);
				}
			});
		}
		return nextSeriesButton;
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
			runPanel.setPreferredSize(new Dimension(1, 100));
			runPanel.add(getRunButton(), null);
			runPanel.add(getSaveSelectionButton(), null);
			runPanel.add(getCloseButton(), null);
			runPanel.add(getCloseSessionCheckBox(), null);
		}
		return runPanel;
	}

	/**
	 * This method initializes runButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getRunButton() {
		if (runButton == null) {
			runButton = new JButton();
			runButton.setBounds(new Rectangle(152, 37, 84, 24));
			runButton.setText("run");
			runButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.run();
				}
			});
		}
		return runButton;
	}

	/**
	 * This method initializes saveSelectionButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSaveSelectionButton() {
		if (saveSelectionButton == null) {
			saveSelectionButton = new JButton();
			saveSelectionButton.setBounds(new Rectangle(15, 37, 122, 24));
			saveSelectionButton.setText("save selection");
			saveSelectionButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.saveSelection(getJTable().getSelectedRow());
				}
			});
		}
		return saveSelectionButton;
	}

	/**
	 * This method initializes closeButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCloseButton() {
		if (closeButton == null) {
			closeButton = new JButton();
			closeButton.setBounds(new Rectangle(279, 37, 84, 24));
			closeButton.setText("close");
			closeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.getView().dispose();
				}
			});
		}
		return closeButton;
	}

	/**
	 * This method initializes closeSessionCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getCloseSessionCheckBox() {
		if (closeSessionCheckBox == null) {
			closeSessionCheckBox = new JCheckBox();
			closeSessionCheckBox.setBounds(new Rectangle(14, 73, 267, 21));
			closeSessionCheckBox.setText("close session when finished");
			closeSessionCheckBox.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.setCloseSession(closeSessionCheckBox.isSelected());
				}
			});
		}
		return closeSessionCheckBox;
	}

	/**
	 * This method initializes showProjectionsBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getShowProjectionsBox() {
		if (showProjectionsBox == null) {
			showProjectionsBox = new JCheckBox();
			showProjectionsBox.setBounds(new Rectangle(9, 104, 162, 21));
			showProjectionsBox.setText("show projections");
			showProjectionsBox.setSelected(model.isShowProjections());
			showProjectionsBox.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.setShowProjections(!model.isShowProjections());
				}
			});
		}
		return showProjectionsBox;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
