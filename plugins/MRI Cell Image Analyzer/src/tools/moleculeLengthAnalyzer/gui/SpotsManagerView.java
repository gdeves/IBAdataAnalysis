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
import ij.gui.GenericDialog;
import ij.gui.PointRoi;
import ij.gui.Roi;
import ij.io.OpenDialog;
import ij.plugin.frame.RoiManager;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Toolkit;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import java.awt.GridBagConstraints;
import javax.swing.JScrollPane;
import javax.swing.JList;
import tools.moleculeLengthAnalyzer.excel.ExcelFilenameFilter;
import tools.moleculeLengthAnalyzer.excel.MoleculeLengthAnalyzerProperties;
import tools.moleculeLengthAnalyzer.gui.SpotsManager.Aspect;
import tools.moleculeLengthAnalyzer.model.ImageInfo;
import tools.moleculeLengthAnalyzer.model.Spot;
import tools.moleculeLengthAnalyzer.model.Spots;
import java.awt.FlowLayout;
import javax.swing.JProgressBar;
import javax.vecmath.Point3d;

/**
 * A component that allows to read in a list of points from excel and 
 * to filter them using a minimum distance to the nearest neighbour.
 * A list of point rois can be created for the spots.  
 * 
 * @author baecker
 *
 */
public class SpotsManagerView extends JPanel implements Observer {
	private static final long serialVersionUID = 1L;
	private SpotsManager model;
	private JPanel browsePanel = null;
	private JTextField excelFileTextField = null;
	private JButton browseButton = null;
	private JPanel topPanel = null;
	private JPanel filterPanel = null;
	private JLabel minDistLabel = null;
	private JTextField minDistTextField = null;
	private JButton jButton = null;
	private JPanel middlePanel = null;
	private JScrollPane spotsScrollPane = null;
	private JList spotsList = null;
	private JPanel bottomPanel = null;
	private JButton resetSpotsButton = null;
	private JButton createRoiButton = null;
	private JTextField unitTextField = null;
	private JPanel bottomActionPanel = null;
	private JPanel spotsInfoPanel = null;
	private JTextField numberOfSpotsTextField = null;
	private JLabel jLabel = null;
	private JProgressBar jProgressBar = null;
	private GenericDialog imageParameterDialog;
	
	@Override
	public void update(Observable sender, Object aspect) {
		Aspect anAspect = (Aspect) aspect; 
		switch (anAspect) {
		case EXCEL_FILE: 
			this.handleExcelFileChanged();
			break;
		case UNIT: 
			this.handleUnitChanged();
			break;
		case SPOTS: 
			this.handleSpotsChanged();
			break;
		case PROGRESS: 
			this.handleProgressChanged();
			break;	
		case MAX_PROGRESS: 
			this.handleMaxProgressChanged();
			break;	
		case ERROR_OCCURED: 
			this.handleErrorOccured();
			break;	
		default:
			break;
		}
	}

	private void handleErrorOccured() {
		this.getJProgressBar().setIndeterminate(false);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		Toolkit.getDefaultToolkit().beep();
	}

	private void handleMaxProgressChanged() {
		JProgressBar progressBar = this.getJProgressBar();
		progressBar.setMinimum(1);
		progressBar.setMaximum(model.getMaxProgress());
		this.getJProgressBar().setIndeterminate(false);
	}

	private void handleProgressChanged() {
		JProgressBar progressBar = this.getJProgressBar();
		progressBar.setValue(model.getProgress());
		if (model.getProgress()>=model.getMaxProgress()) {
			progressBar.setValue(0);
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	private void handleSpotsChanged() {
		this.getSpotsList().setModel(model.getSpots());
		this.getNumberOfSpotsTextField().setText(Integer.toString(model.getNumberOfSpots()));
	}

	private void handleUnitChanged() {
		String unit = model.getUnit();
		this.getUnitTextField().setText(unit);
	}

	private void handleExcelFileChanged() {
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		String filename = model.getExcelFile();
		this.getExcelFileTextField().setText(filename);
		model.readSpotsFromExcel();
	}

	/**
	 * This is the default constructor
	 */
	public SpotsManagerView() {
		super();
		this.model = new SpotsManager("spots");
		model.addObserver(this);
		initialize();
	}

	public SpotsManagerView(SpotsManager model) {
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
		this.setSize(300, 250);
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), model.getName(), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		this.add(getTopPanel(), BorderLayout.NORTH);
		this.add(getMiddlePanel(), BorderLayout.CENTER);
		this.add(getBottomPanel(), BorderLayout.SOUTH);
	}

	/**
	 * This method initializes browsePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getBrowsePanel() {
		if (browsePanel == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints.weightx = 1.0;
			browsePanel = new JPanel();
			browsePanel.setLayout(new BoxLayout(getBrowsePanel(), BoxLayout.X_AXIS));
			browsePanel.add(getExcelFileTextField(), null);
			browsePanel.add(getBrowseButton(), null);
		}
		return browsePanel;
	}

	/**
	 * This method initializes excelFileTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getExcelFileTextField() {
		if (excelFileTextField == null) {
			excelFileTextField = new JTextField();
			excelFileTextField.setEnabled(false);
			excelFileTextField.setEditable(false);
		}
		return excelFileTextField;
	}

	/**
	 * This method initializes browseButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBrowseButton() {
		if (browseButton == null) {
			browseButton = new JButton();
			browseButton.setText("browse");
			browseButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					FileDialog fd = new FileDialog((Frame)null); 
					fd.setMode(FileDialog.LOAD);
					fd.setFilenameFilter(new ExcelFilenameFilter());
					String lastDir = OpenDialog.getLastDirectory(); 
					if (lastDir==null) lastDir = OpenDialog.getDefaultDirectory();
					fd.setDirectory(lastDir);
					fd.setVisible(true);
					String file = fd.getFile();
					if (file == null) return;
					String dir = fd.getDirectory();
					OpenDialog.setLastDirectory(dir);
					String path = dir + file;
					getJProgressBar().setIndeterminate(true);
					model.setExcelFile(path);
				}
			});
		}
		return browseButton;
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
			topPanel.add(getBrowsePanel(), null);
			topPanel.add(getFilterPanel(), null);
		}
		return topPanel;
	}

	/**
	 * This method initializes filterPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getFilterPanel() {
		if (filterPanel == null) {
			minDistLabel = new JLabel();
			minDistLabel.setText("min. dist.: ");
			filterPanel = new JPanel();
			filterPanel.setLayout(new BoxLayout(getFilterPanel(), BoxLayout.X_AXIS));
			filterPanel.add(minDistLabel, null);
			filterPanel.add(getMinDistTextField(), null);
			filterPanel.add(getUnitTextField(), null);
			filterPanel.add(getJButton(), null);
		}
		return filterPanel;
	}

	/**
	 * This method initializes minDistTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getMinDistTextField() {
		if (minDistTextField == null) {
			minDistTextField = new JTextField();
			minDistTextField.setText(Double.toString(model.getMinimumDistance()));
			minDistTextField.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					updateMinDistance();
				}
			});
			minDistTextField.addFocusListener(new java.awt.event.FocusAdapter() {
				public void focusLost(java.awt.event.FocusEvent e) {
					updateMinDistance();
				}
			});
		}
		return minDistTextField;
	}

	protected void updateMinDistance() {
		String minDistString = this.getMinDistTextField().getText();
		try {
			double minDist = Double.parseDouble(minDistString);
			model.setMinimumDistance(minDist);
		} catch (NumberFormatException e) {
			this.getMinDistTextField().setText(Double.toString(model.getMinimumDistance()));
		}
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("filter");
			jButton.setPreferredSize(new Dimension(80, 25));
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					model.filterSpots();
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			});
		}
		return jButton;
	}

	/**
	 * This method initializes middlePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getMiddlePanel() {
		if (middlePanel == null) {
			middlePanel = new JPanel();
			middlePanel.setLayout(new BorderLayout());
			middlePanel.add(getSpotsScrollPane(), BorderLayout.CENTER);
		}
		return middlePanel;
	}

	/**
	 * This method initializes spotsScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getSpotsScrollPane() {
		if (spotsScrollPane == null) {
			spotsScrollPane = new JScrollPane();
			spotsScrollPane.setViewportView(getSpotsList());
		}
		return spotsScrollPane;
	}

	/**
	 * This method initializes spotsList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getSpotsList() {
		if (spotsList == null) {
			spotsList = new JList();
		}
		return spotsList;
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
			bottomPanel.setMinimumSize(new Dimension(1, 1));
			bottomPanel.setPreferredSize(new Dimension(1, 80));
			bottomPanel.add(getJProgressBar(), null);
			bottomPanel.add(getSpotsInfoPanel(), null);
			bottomPanel.add(getBottomActionPanel(), null);
		}
		return bottomPanel;
	}

	/**
	 * This method initializes resetSpotsButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getResetSpotsButton() {
		if (resetSpotsButton == null) {
			resetSpotsButton = new JButton();
			resetSpotsButton.setText("reset");
			resetSpotsButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.resetSpots();
				}
			});
		}
		return resetSpotsButton;
	}

	/**
	 * This method initializes createRoiButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCreateRoiButton() {
		if (createRoiButton == null) {
			createRoiButton = new JButton();
			createRoiButton.setText("create roi");
			createRoiButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ImagePlus image = WindowManager.getCurrentImage();
					ImageInfo info = null;
					if (image==null) {
						info = getImageInfoFromUser();
						image = model.getImageFor(info);
						image.show();
					} else 
						info = model.getImageInfoFromImage(image);
					if (info==null) return;
					Spots spots = model.getSpots();
					RoiManager manager = RoiManager.getInstance();
					if (manager==null) manager = new RoiManager();
					for (Spot spot : spots) {
						Point3d point = spot.getPointInPixelCoords(info);
						Roi roi = new PointRoi((int)point.x, (int)point.y);
						image.setSlice((int)point.z);
						manager.add(image, roi, spot.getId());	
					}
				}
			});
		}
		return createRoiButton;
	}
		
	public ImageInfo getImageInfoFromUser() {
		imageParameterDialog = getImageParameterDialog();
		imageParameterDialog.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/tools/moleculeLengthAnalyzer/resources/mla.gif")));
		imageParameterDialog.showDialog();
		if (imageParameterDialog.wasCanceled()) return null;
		ImageInfo info = new ImageInfo();
		info.setWidth((int) imageParameterDialog.getNextNumber());
		info.setHeight ((int) imageParameterDialog.getNextNumber());
		info.setSlices((int) imageParameterDialog.getNextNumber());
		info.setXSize(imageParameterDialog.getNextNumber());
		info.setYSize(imageParameterDialog.getNextNumber());
		info.setZSize(imageParameterDialog.getNextNumber());
		return info;
	}

	private GenericDialog getImageParameterDialog() {
		 MoleculeLengthAnalyzerProperties props = MoleculeLengthAnalyzerProperties.getInstance();
		imageParameterDialog = new GenericDialog("image size and scale");
		imageParameterDialog.addNumericField("image_width (pixel)", Double.parseDouble(props.getImageWidth()), 0);
		imageParameterDialog.addNumericField("image_height (pixel)", Double.parseDouble(props.getImageHeight()), 0);
		imageParameterDialog.addNumericField("number_of_slices", Double.parseDouble(props.getNumberOfSlices()), 0);
		imageParameterDialog.addNumericField("voxel_size_x", Double.parseDouble(props.getVoxelSizeX()), 5);
		imageParameterDialog.addNumericField("voxel_size_y", Double.parseDouble(props.getVoxelSizeY()), 5);
		imageParameterDialog.addNumericField("voxel_size_z", Double.parseDouble(props.getVoxelSizeZ()), 5);
		return imageParameterDialog;
	}
	
	/**
	 * This method initializes unitTextField	
	 * 	
	 * @return javax.swing.JTextField	image
	 */
	private JTextField getUnitTextField() {
		if (unitTextField == null) {
			unitTextField = new JTextField();
			unitTextField.setEditable(false);
			unitTextField.setEnabled(false);
		}
		return unitTextField;
	}

	/**
	 * This method initializes bottomActionPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getBottomActionPanel() {
		if (bottomActionPanel == null) {
			bottomActionPanel = new JPanel();
			bottomActionPanel.setLayout(new FlowLayout());
			bottomActionPanel.add(getResetSpotsButton(), null);
			bottomActionPanel.add(getCreateRoiButton(), null);
		}
		return bottomActionPanel;
	}

	/**
	 * This method initializes spotsInfoPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getSpotsInfoPanel() {
		if (spotsInfoPanel == null) {
			jLabel = new JLabel();
			jLabel.setText("nr. of spots:  ");
			spotsInfoPanel = new JPanel();
			spotsInfoPanel.setLayout(new FlowLayout());
			spotsInfoPanel.add(jLabel, null);
			spotsInfoPanel.add(getNumberOfSpotsTextField(), null);
		}
		return spotsInfoPanel;
	}

	/**
	 * This method initializes numberOfSpotsTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getNumberOfSpotsTextField() {
		if (numberOfSpotsTextField == null) {
			numberOfSpotsTextField = new JTextField();
			numberOfSpotsTextField.setPreferredSize(new Dimension(60, 19));
			numberOfSpotsTextField.setEnabled(false);
			numberOfSpotsTextField.setEditable(false);
			numberOfSpotsTextField.setText("0");
		}
		return numberOfSpotsTextField;
	}

	/**
	 * This method initializes jProgressBar	
	 * 	
	 * @return javax.swing.JProgressBar	
	 */
	private JProgressBar getJProgressBar() {
		if (jProgressBar == null) {
			jProgressBar = new JProgressBar();
			jProgressBar.setOrientation(JProgressBar.HORIZONTAL);
		}
		return jProgressBar;
	}

}
