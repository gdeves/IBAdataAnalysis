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

import ij.io.OpenDialog;
import imagejProxies.MRIFolderOpener;
import java.awt.BorderLayout;
import java.io.File;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JTextField;

import operations.file.OpenImageOperation;

import java.awt.FlowLayout;

/**
 * The list editor allows to select a list of image files. The interface contains a
 * list of images. Images can be added to the list with the add button that opens
 * either a file-open-dialog or a sequence opener. The select button selects all images 
 * that contain the string in the input field in their name. "remove selected" removes
 * the selected images from the list. the "close" button closes the list editor.
 * 
 * @author	Volker Baecker
 **/
public class ListEditorView extends javax.swing.JDialog implements Observer {

	private static final long serialVersionUID = 1L;
	private javax.swing.JPanel ivjJFrameContentPane = null;
	private JPanel jPanel = null;
	private JPanel jPanel1 = null;
	private JScrollPane jScrollPane = null;
	private JList theList = null;
	private JButton addButton = null;
	private JButton removeSelectedButton = null;
	private JButton closeButton = null;
	protected ListEditor model;
	private JPanel jPanel2 = null;
	private JPanel jPanel3 = null;
	private JPanel filterPanel = null;
	private JButton selectFilteredButton = null;
	private JTextField filterTextField = null;

	public ListEditorView(ListEditor model) {
		super();
		this.model = model;
		model.addObserver(this);
		initialize();
	}
	
	/**
	 * Return the JFrameContentPane property value.
	 * @return javax.swing.JPanel
	 */
	public javax.swing.JPanel getJFrameContentPane() {
		if (ivjJFrameContentPane == null) {
			ivjJFrameContentPane = new javax.swing.JPanel();
			ivjJFrameContentPane.setName("JFrameContentPane");
			ivjJFrameContentPane.setLayout(new BorderLayout());
			ivjJFrameContentPane.add(getJPanel(), java.awt.BorderLayout.CENTER);
			ivjJFrameContentPane.add(getJPanel1(), java.awt.BorderLayout.SOUTH);
		}
		return ivjJFrameContentPane;
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {

		// this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/icon.gif")));
		this.setName("JFrame1");
		this.setModal(true);
		this
				.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		this.setBounds(45, 25, 426, 273);
		this.setTitle("list editor");
		this.setContentPane(getJFrameContentPane());

	}
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new BorderLayout());
			jPanel.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
		}
		return jPanel;
	}
	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.setLayout(new BorderLayout());
			jPanel1.setPreferredSize(new java.awt.Dimension(100,60));
			jPanel1.add(getJPanel2(), java.awt.BorderLayout.WEST);
			jPanel1.add(getJPanel3(), java.awt.BorderLayout.EAST);
			jPanel1.add(getFilterPanel(), java.awt.BorderLayout.SOUTH);
		}
		return jPanel1;
	}
	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */    
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getTheList());
		}
		return jScrollPane;
	}
	/**
	 * This method initializes theList	
	 * 	
	 * @return javax.swing.JList	
	 */    
	private JList getTheList() {
		if (theList == null) {
			theList = new JList();
			theList.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {
					if (e.getClickCount() == 2) {
			             int index = theList.locationToIndex(e.getPoint());
			             File theFile = (File) theList.getModel().getElementAt(index);			     
			             OpenImageOperation open = new OpenImageOperation();
			             open.setAbsoluteFilename(theFile.getAbsolutePath());
			             open.setShowResult(true);
			             open.run();
			          }
				}
			});
		}
		return theList;
	}
	/**
	 * This method initializes addButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	public JButton getAddButton() {
		if (addButton == null) {
			addButton = new JButton();
			addButton.setText("add...");
			addButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
			addButton.setBounds(5, 5, 66, 19);
			addButton.setPreferredSize(new java.awt.Dimension(45,19));
			addButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					if (model.useSequenceOpener) {
						getFilesWithSequenceOpener();
					} else {
						getFilesWithFileChooser();
					}
				}
			});
		}
		return addButton;
	}
	/**
	 * 
	 */
	protected void getFilesWithSequenceOpener() {
		MRIFolderOpener opener = new MRIFolderOpener();
		opener.run(null);
		File[] list = opener.getFileList();
		model.addToList(list);
	}

	/**
	 * This method initializes removeSelectedButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getRemoveSelectedButton() {
		if (removeSelectedButton == null) {
			removeSelectedButton = new JButton();
			removeSelectedButton.setText("remove selected");
			removeSelectedButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
			removeSelectedButton.setBounds(85, 5, 107, 19);
			removeSelectedButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					Object[] toBeRemoved = getTheList().getSelectedValues();
					model.removeElementsFromList(toBeRemoved);
				}
			});
		}
		return removeSelectedButton;
	}
	/**
	 * This method initializes closeButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getCloseButton() {
		if (closeButton == null) {
			closeButton = new JButton();
			closeButton.setText("close");
			closeButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
			closeButton.setPreferredSize(new java.awt.Dimension(45,19));
			closeButton.setBounds(130, 5, 65, 19);
			closeButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					dispose();
				}
			});
		}
		return closeButton;
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable aModel, Object anAspect) {
		if (anAspect.equals("list")) this.handleListChanged();
		
	}

	private void handleListChanged() {
		this.getTheList().setListData(model.getList());
		
	}
	/**
	 * This method initializes jPanel2	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			jPanel2 = new JPanel();
			jPanel2.setLayout(null);
			jPanel2.setPreferredSize(new java.awt.Dimension(200,30));
			jPanel2.add(getAddButton(), null);
			jPanel2.add(getRemoveSelectedButton(), null);
		}
		return jPanel2;
	}
	/**
	 * This method initializes jPanel3	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	public JPanel getJPanel3() {
		if (jPanel3 == null) {
			jPanel3 = new JPanel();
			jPanel3.setLayout(null);
			jPanel3.setPreferredSize(new java.awt.Dimension(200,29));
			jPanel3.add(getCloseButton(), null);
		}
		return jPanel3;
	}

	protected void getFilesWithFileChooser() {
		JFileChooser fileChooser = new JFileChooser();
		if (OpenDialog.getDefaultDirectory()!=null) {
			fileChooser.setCurrentDirectory(new File(OpenDialog.getDefaultDirectory()));
		}
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setMultiSelectionEnabled(true);
		fileChooser.addChoosableFileFilter(ListEditor.getImageFileFilter());
		fileChooser.addChoosableFileFilter(ListEditor.getTiffFileFilter());
		int returnVal = fileChooser.showOpenDialog(model.view());
		if (returnVal != JFileChooser.APPROVE_OPTION) return;
		model.addToList(fileChooser.getSelectedFiles(), fileChooser.getFileFilter());
		File aFile = fileChooser.getSelectedFile();
		String currentFolder = fileChooser.getSelectedFile().getAbsolutePath();
		if (!aFile.isDirectory() || fileChooser.getSelectedFiles().length>1) {
			currentFolder = fileChooser.getSelectedFile().getParent();
		}
		OpenDialog.setDefaultDirectory(currentFolder);
	}

	/**
	 * This method initializes filterPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getFilterPanel() {
		if (filterPanel == null) {
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(java.awt.FlowLayout.LEFT);
			filterPanel = new JPanel();
			filterPanel.setLayout(flowLayout);
			filterPanel.setPreferredSize(new java.awt.Dimension(10,30));
			filterPanel.add(getFilterTextField(), null);
			filterPanel.add(getSelectFilteredButton(), null);
		}
		return filterPanel;
	}

	/**
	 * This method initializes selectFilteredButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSelectFilteredButton() {
		if (selectFilteredButton == null) {
			selectFilteredButton = new JButton();
			selectFilteredButton.setText("select");
			selectFilteredButton.setPreferredSize(new java.awt.Dimension(99,20));
			selectFilteredButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.view().selectContaining(getFilterTextField().getText());
				}
			});
		}
		return selectFilteredButton;
	}

	protected void selectContaining(String text) {
		int[] matching = model.getItemsMatching(text);
		this.getTheList().setSelectedIndices(matching);
	}

	/**
	 * This method initializes filterTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getFilterTextField() {
		if (filterTextField == null) {
			filterTextField = new JTextField();
			filterTextField.setPreferredSize(new java.awt.Dimension(210,20));
		}
		return filterTextField;
	}
         }  //  @jve:decl-index=0:visual-constraint="10,10"
