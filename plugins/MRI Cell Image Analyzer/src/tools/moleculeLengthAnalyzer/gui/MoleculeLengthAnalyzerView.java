/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2009 INSERM 
 * MRI Cell Image Analyzer has been created at Montpellier RIO Imaging
 * (www.mri.cnrs.fr) by Volker Bäcker
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
package tools.moleculeLengthAnalyzer.gui;

import ij.ImagePlus;
import ij.WindowManager;
import ij.io.OpenDialog;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JFrame;
import java.awt.Toolkit;
import javax.swing.JTabbedPane;
import javax.swing.BoxLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import java.awt.GridBagConstraints;
import javax.swing.BorderFactory;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.JScrollPane;
import javax.swing.JList;
import java.awt.Insets;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import javax.swing.JTextField;
import tools.moleculeLengthAnalyzer.excel.ExcelFilenameFilter;
import tools.moleculeLengthAnalyzer.excel.MoleculeLengthAnalyzerProperties;
import tools.moleculeLengthAnalyzer.gui.MoleculeLengthAnalyzer.Aspect;
import tools.moleculeLengthAnalyzer.model.ImageInfo;

/**
 * A tool that allows to read in to lists of points from excel files.
 * The points can be filtered in a way that only points with a minimum 
 * distance to the nearest neighbour are kept. For each green point the
 * nearest red point is found. The two points form a molecule. The molecules
 * can be drawn into an image and reported to excel. The length of the molecules
 * is measured. 
 * 
 * @author baecker
 *
 */
public class MoleculeLengthAnalyzerView extends JFrame implements Observer, WindowListener {
	private static final long serialVersionUID = -93671124411275365L;
	private JPanel jContentPane = null;
	private MoleculeLengthAnalyzer model;
	private JTabbedPane jTabbedPane = null;
	private JPanel mlaPanel = null;
	private JPanel spotsPanel = null;
	private JPanel greenSpotsPanel = null;
	private JPanel redSpotsPanel = null;
	private JPanel moleculesPanel = null;
	private JPanel topPanel = null;
	private JButton findButton = null;
	private JScrollPane moleculesScrollPane = null;
	private JList moleculesList = null;
	private JPanel bottomPanel = null;
	private JPanel progressPanel = null;
	private JProgressBar jProgressBar = null;
	private JPanel numberOfMoleculesPanel = null;
	private JPanel bottomActionPanel = null;
	private JButton createRoiButton = null;
	private JButton writeToExcelButton = null;
	private JLabel jLabel = null;
	private JTextField numberOfMoleculesTextField = null;
	
	@Override
	public void update(Observable sender, Object aspect) {
		Aspect anAspect = (Aspect) aspect;
		switch (anAspect) {
		case MOLECULES:
			this.handleMoleculesChanged();
			break;
		case ERROR_OCCURED:
			this.handleErrorOccured();
			break;
		default:
			break;
		}
	}

	private void handleErrorOccured() {
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	private void handleMoleculesChanged() {
		this.getMoleculesList().setModel(model.getMolecules());
		this.getNumberOfMoleculesTextField().setText(Integer.toString(model.getMolecules().getSize()));
	}

	/**
	 * This is the default constructor
	 */
	public MoleculeLengthAnalyzerView() {
		super();
		this.model = new MoleculeLengthAnalyzer();
		model.addObserver(this);
		initialize();
	}

	public MoleculeLengthAnalyzerView(MoleculeLengthAnalyzer model) {
		super();
		this.model = model;
		model.addObserver(this);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		MoleculeLengthAnalyzerProperties.getInstance().load();
		this.addWindowListener(this);
		this.setSize(638, 553);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/tools/moleculeLengthAnalyzer/resources/mla.gif")));
		this.setContentPane(getJContentPane());
		this.setTitle("MRI Molecule Length Analyzer");
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
			jContentPane.add(getJTabbedPane(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jTabbedPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			jTabbedPane.addTab("run", null, getMlaPanel(), null);
		}
		return jTabbedPane;
	}

	/**
	 * This method initializes mlaPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getMlaPanel() {
		if (mlaPanel == null) {
			mlaPanel = new JPanel();
			mlaPanel.setLayout(new BorderLayout());
			mlaPanel.add(getSpotsPanel(), BorderLayout.NORTH);
			mlaPanel.add(getMoleculesPanel(), BorderLayout.CENTER);
		}
		return mlaPanel;
	}

	/**
	 * This method initializes spotsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getSpotsPanel() {
		if (spotsPanel == null) {
			spotsPanel = new JPanel();
			spotsPanel.setLayout(new BoxLayout(getSpotsPanel(), BoxLayout.X_AXIS));
			spotsPanel.setPreferredSize(new Dimension(0, 250));
			spotsPanel.add(getGreenSpotsPanel(), null);
			spotsPanel.add(getRedSpotsPanel(), null);
		}
		return spotsPanel;
	}

	/**
	 * This method initializes greenSpotsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getGreenSpotsPanel() {
		if (greenSpotsPanel == null) {
			greenSpotsPanel = model.getGreenSpotsManager().getView();
		}
		return greenSpotsPanel;
	}

	/**
	 * This method initializes redSpotsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getRedSpotsPanel() {
		if (redSpotsPanel == null) {
			redSpotsPanel = model.getRedSpotsManager().getView();
		}
		return redSpotsPanel;
	}

	/**
	 * This method initializes moleculesPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getMoleculesPanel() {
		if (moleculesPanel == null) {
			moleculesPanel = new JPanel();
			moleculesPanel.setLayout(new BorderLayout());
			moleculesPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "molecules", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
			moleculesPanel.add(getTopPanel(), BorderLayout.NORTH);
			moleculesPanel.add(getMoleculesScrollPane(), BorderLayout.CENTER);
			moleculesPanel.add(getBottomPanel(), BorderLayout.SOUTH);
		}
		return moleculesPanel;
	}

	/**
	 * This method initializes topPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getTopPanel() {
		if (topPanel == null) {
			topPanel = new JPanel();
			topPanel.setLayout(new GridBagLayout());
			topPanel.add(getFindButton(), new GridBagConstraints());
		}
		return topPanel;
	}

	/**
	 * This method initializes findButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getFindButton() {
		if (findButton == null) {
			findButton = new JButton();
			findButton.setText("find");
			findButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					model.findMolecules();
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			});
		}
		return findButton;
	}

	/**
	 * This method initializes moleculesScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getMoleculesScrollPane() {
		if (moleculesScrollPane == null) {
			moleculesScrollPane = new JScrollPane();
			moleculesScrollPane.setViewportView(getMoleculesList());
		}
		return moleculesScrollPane;
	}

	/**
	 * This method initializes moleculesList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getMoleculesList() {
		if (moleculesList == null) {
			moleculesList = new JList();
		}
		return moleculesList;
	}

	/**
	 * This method initializes bottomPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getBottomPanel() {
		if (bottomPanel == null) {
			bottomPanel = new JPanel();
			bottomPanel.setLayout(new BoxLayout(getBottomPanel(), BoxLayout.Y_AXIS));
			bottomPanel.add(getProgressPanel(), null);
			bottomPanel.add(getNumberOfMoleculesPanel(), null);
			bottomPanel.add(getBottomActionPanel(), null);
		}
		return bottomPanel;
	}

	/**
	 * This method initializes progressPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getProgressPanel() {
		if (progressPanel == null) {
			progressPanel = new JPanel();
			progressPanel.setLayout(new BorderLayout());
			progressPanel.add(getJProgressBar(), BorderLayout.NORTH);
		}
		return progressPanel;
	}

	/**
	 * This method initializes jProgressBar	
	 * 	
	 * @return javax.swing.JProgressBar	
	 */
	private JProgressBar getJProgressBar() {
		if (jProgressBar == null) {
			jProgressBar = new JProgressBar();
		}
		return jProgressBar;
	}

	/**
	 * This method initializes numberOfMoleculesPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getNumberOfMoleculesPanel() {
		if (numberOfMoleculesPanel == null) {
			jLabel = new JLabel();
			jLabel.setText("nr. of molecules: ");
			numberOfMoleculesPanel = new JPanel();
			numberOfMoleculesPanel.setLayout(new FlowLayout());
			numberOfMoleculesPanel.add(jLabel, null);
			numberOfMoleculesPanel.add(getNumberOfMoleculesTextField(), null);
		}
		return numberOfMoleculesPanel;
	}

	/**
	 * This method initializes bottomActionPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getBottomActionPanel() {
		if (bottomActionPanel == null) {
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.insets = new Insets(0, 10, 0, 0);
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.insets = new Insets(0, 0, 0, 10);
			bottomActionPanel = new JPanel();
			bottomActionPanel.setLayout(new GridBagLayout());
			bottomActionPanel.add(getCreateRoiButton(), gridBagConstraints2);
			bottomActionPanel.add(getWriteToExcelButton(), gridBagConstraints11);
		}
		return bottomActionPanel;
	}

	/**
	 * This method initializes createRoiButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCreateRoiButton() {
		if (createRoiButton == null) {
			createRoiButton = new JButton();
			createRoiButton.setText("draw molecules");
			createRoiButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ImagePlus image = WindowManager.getCurrentImage();
					ImageInfo info = null;
					if (image==null) {
						info = model.getGreenSpotsManager().getView().getImageInfoFromUser();
						image = model.getImageFor(info);
						image.show();
					} else 
						info = model.getImageInfoFromImage(image);
					if (info==null) return;
					MoleculeLengthAnalyzerProperties.getInstance().setImageInfo(info);
					model.drawMolecules(image, info);		
				}
			});
		}
		return createRoiButton;
	}

	/**
	 * This method initializes writeToExcelButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getWriteToExcelButton() {
		if (writeToExcelButton == null) {
			writeToExcelButton = new JButton();
			writeToExcelButton.setText("write to excel");
			writeToExcelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					FileDialog fd = new FileDialog((Frame)null); 
					fd.setMode(FileDialog.SAVE);
					fd.setFilenameFilter(new ExcelFilenameFilter());
					String lastDir = OpenDialog.getLastDirectory(); 
					if (lastDir==null) lastDir = OpenDialog.getDefaultDirectory();
					fd.setDirectory(lastDir);
					fd.setFile(model.getResultFilename());
					fd.setVisible(true);
					String file = fd.getFile();
					if (file==null) return;
					OpenDialog.setLastDirectory(fd.getDirectory());
					model.setResultFilename(file);
					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					model.writeMoleculesToExcel(fd.getDirectory() + file);
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			});
		}
		return writeToExcelButton;
	}

	/**
	 * This method initializes numberOfMoleculesTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getNumberOfMoleculesTextField() {
		if (numberOfMoleculesTextField == null) {
			numberOfMoleculesTextField = new JTextField();
			numberOfMoleculesTextField.setPreferredSize(new Dimension(60, 19));
			numberOfMoleculesTextField.setEnabled(false);
			numberOfMoleculesTextField.setEditable(false);
			numberOfMoleculesTextField.setText("0");
		}
		return numberOfMoleculesTextField;
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		MoleculeLengthAnalyzerProperties.getInstance().save();
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
