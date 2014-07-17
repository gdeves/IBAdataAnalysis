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

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Toolkit;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.BoxLayout;

import rgbRegistration.Align_RGB_planes;
import javax.swing.JButton;
import java.awt.GridLayout;
import javax.swing.JCheckBox;

/**
 * A tool to manually align the slices of a stack. The view has a file menu to
 * open a stack or to grab the active stack. It shows the filename and the quality
 * of the current alignment. When the update checkbox is checked the quality is
 * updated when the alignment changes. 
 * The pad button grows the image so that it can be rotated withhout loosing
 * part of the image. Accept makes the alignment permanent in the stack. Revert
 * sets the current alignment back to the last permanent. The compute quality
 * button can be used to manually update the quality.
 * 
 * @author	Volker Baecker
 **/
public class StackRegistratorView extends JFrame implements Observer {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private StackRegistrator model;
	private JMenuBar jJMenuBar = null;
	private JMenu fileMenu = null;
	private JMenuItem openStackMenuItem = null;
	private JToolBar jToolBar = null;
	private JPanel infoPanel = null;
	private JPanel mainPanel = null;
	private JLabel imageLabel = null;
	private JTextField imageNameTextField = null;
	private JPanel actionPanel = null;
	private JButton padImageButton = null;
	private JButton acceptButton = null;
	private JButton revertButton = null;
	private JMenuItem getActiveStackMenuItem = null;
	private JButton qualityButton = null;
	private JPanel topPanel = null;
	private JPanel qualityPanel = null;
	private JLabel qualityLabel = null;
	private JTextField qualityTextField = null;
	private JCheckBox qualityCheckBox = null;

	public StackRegistratorView() {
		super();
		initialize();
	}

	public StackRegistratorView(StackRegistrator model) {
		super();
		this.model = model;
		initialize();
		model.addObserver(this);
	}

	private void initialize() {
		this.setSize(300, 200);
		this.setJMenuBar(getJJMenuBar());
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/icon.gif")));
		this.setContentPane(getJContentPane());
		this.setTitle("Stack Registrator");
		this.getQualityCheckBox().setSelected(true);
		model.setUpdateQuality(true);
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
			jContentPane.add(getJToolBar(), java.awt.BorderLayout.NORTH);
			jContentPane.add(getMainPanel(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}

	public void update(Observable arg0, Object arg1) {
		if (arg1.equals("inputImage")) this.updateInputImage();
		if (arg1.equals("currentSlice")) this.updateCurrentSlice();
		if (arg1.equals("rgbImage")) this.updateRGBImage();
		if (arg1.equals("slicePosition")) this.updateSlicePosition();
	}

	protected void updateSlicePosition() {
		double quality = model.computeCurrentQuality();
		this.getQualityTextField().setText(Double.toString(quality));
	}

	protected void updateRGBImage() {
		model.getRgbImage().show();
		if (model.getAligner()!=null) {
			((Dialog)model.getAligner()).dispose();
		}
		model.setAligner(new Align_RGB_planes()); 
	}

	protected void updateCurrentSlice() {
		// TODO Auto-generated method stub
	}

	protected void updateInputImage() {
		this.getImageNameTextField().setText(model.getInputImagePath());
	}

	/**
	 * This method initializes jJMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getFileMenu());
		}
		return jJMenuBar;
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
			fileMenu.add(getOpenStackMenuItem());
			fileMenu.add(getGetActiveStackMenuItem());
		}
		return fileMenu;
	}

	/**
	 * This method initializes openStackMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getOpenStackMenuItem() {
		if (openStackMenuItem == null) {
			openStackMenuItem = new JMenuItem();
			openStackMenuItem.setText("open stack");
			openStackMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.openStack();
				}
			});
		}
		return openStackMenuItem;
	}

	/**
	 * This method initializes jToolBar	
	 * 	
	 * @return javax.swing.JToolBar	
	 */
	private JToolBar getJToolBar() {
		if (jToolBar == null) {
			jToolBar = new JToolBar();
		}
		return jToolBar;
	}

	/**
	 * This method initializes infoPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getInfoPanel() {
		if (infoPanel == null) {
			imageLabel = new JLabel();
			imageLabel.setText("image:");
			infoPanel = new JPanel();
			infoPanel.setLayout(new BoxLayout(getInfoPanel(), BoxLayout.X_AXIS));
			infoPanel.setPreferredSize(new java.awt.Dimension(23,23));
			infoPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));
			infoPanel.add(imageLabel, null);
			infoPanel.add(getImageNameTextField(), null);
		}
		return infoPanel;
	}

	/**
	 * This method initializes mainPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(new BorderLayout());
			mainPanel.add(getActionPanel(), java.awt.BorderLayout.CENTER);
			mainPanel.add(getTopPanel(), java.awt.BorderLayout.NORTH);
		}
		return mainPanel;
	}

	/**
	 * This method initializes imageNameTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getImageNameTextField() {
		if (imageNameTextField == null) {
			imageNameTextField = new JTextField();
			imageNameTextField.setPreferredSize(new java.awt.Dimension(200,20));
			imageNameTextField.setEditable(false);
		}
		return imageNameTextField;
	}

	/**
	 * This method initializes actionPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getActionPanel() {
		if (actionPanel == null) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(4);
			actionPanel = new JPanel();
			actionPanel.setName("2");
			actionPanel.setLayout(gridLayout);
			actionPanel.add(getPadImageButton(), null);
			actionPanel.add(getAcceptButton(), null);
			actionPanel.add(getRevertButton(), null);
			actionPanel.add(getQualityButton(), null);
		}
		return actionPanel;
	}

	/**
	 * This method initializes padImageButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getPadImageButton() {
		if (padImageButton == null) {
			padImageButton = new JButton();
			padImageButton.setText("pad image");
			padImageButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.padImage();
				}
			});
		}
		return padImageButton;
	}

	/**
	 * This method initializes acceptButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAcceptButton() {
		if (acceptButton == null) {
			acceptButton = new JButton();
			acceptButton.setText("accept");
			acceptButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.acceptCurrentRegistration();
				}
			});
		}
		return acceptButton;
	}

	/**
	 * This method initializes revertButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getRevertButton() {
		if (revertButton == null) {
			revertButton = new JButton();
			revertButton.setText("revert");
			revertButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.revertCurrentSlice();
				}
			});
		}
		return revertButton;
	}

	/**
	 * This method initializes getActiveStackMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getGetActiveStackMenuItem() {
		if (getActiveStackMenuItem == null) {
			getActiveStackMenuItem = new JMenuItem();
			getActiveStackMenuItem.setText("get active stack");
			getActiveStackMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.getActiveStack();
				}
			});
		}
		return getActiveStackMenuItem;
	}

	/**
	 * This method initializes qualityButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getQualityButton() {
		if (qualityButton == null) {
			qualityButton = new JButton();
			qualityButton.setText("compute quality");
			qualityButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					double quality = model.computeCurrentQuality();
					IJ.log(Double.toString(quality));
				}
			});
		}
		return qualityButton;
	}

	/**
	 * This method initializes topPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getTopPanel() {
		if (topPanel == null) {
			topPanel = new JPanel();
			topPanel.setLayout(new BoxLayout(getTopPanel(), BoxLayout.Y_AXIS));
			topPanel.add(getInfoPanel(), null);
			topPanel.add(getQualityPanel(), null);
		}
		return topPanel;
	}

	/**
	 * This method initializes qualityPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getQualityPanel() {
		if (qualityPanel == null) {
			qualityLabel = new JLabel();
			qualityLabel.setText("quality:");
			qualityPanel = new JPanel();
			qualityPanel.setLayout(new BoxLayout(getQualityPanel(), BoxLayout.X_AXIS));
			qualityPanel.add(qualityLabel, null);
			qualityPanel.add(getQualityTextField(), null);
			qualityPanel.add(getQualityCheckBox(), null);
		}
		return qualityPanel;
	}

	/**
	 * This method initializes qualityTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getQualityTextField() {
		if (qualityTextField == null) {
			qualityTextField = new JTextField();
			qualityTextField.setEditable(false);
		}
		return qualityTextField;
	}

	/**
	 * This method initializes qualityCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getQualityCheckBox() {
		if (qualityCheckBox == null) {
			qualityCheckBox = new JCheckBox();
			qualityCheckBox.setText("update");
			qualityCheckBox.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.setUpdateQuality(getQualityCheckBox().isSelected());
				}
			});
		}
		return qualityCheckBox;
	}

}
