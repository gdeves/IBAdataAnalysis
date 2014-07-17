package segmentation;

import java.awt.BorderLayout;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Dimension;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JButton;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import java.awt.Font;
import java.awt.Color;
import javax.swing.BoxLayout;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;
import javax.swing.JRadioButton;

public class ImageObjectExplorerOptionsView extends JFrame implements Observer {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private ImageObjectExplorerOptions model;

	private JTabbedPane optionsTabbedPane = null;

	private JPanel sortOptionsPanel = null;

	private JPanel availableMeasurementsPanel = null;

	private JScrollPane availableMeasurementsScrollPane = null;

	private JList availableMeasurementsList = null;

	private JButton addMeasurementButton = null;

	private JPanel addRemoveButtonPanel = null;

	private JButton removeButton = null;

	private JPanel measuremenstPanel = null;

	private JScrollPane measurementsScrollPane = null;

	private JList measurementsList = null;

	private JPanel middlePanel = null;

	private JPanel rightPanel = null;

	private JPanel emptyPanel = null;

	private JPanel addRemoveButtonPanel2 = null;

	private JPanel spacePanel = null;

	private JButton moveUpButton = null;

	private JButton moveDownButton = null;

	private JPanel emptyPanel2 = null;

	private JPanel moveUpDownPanel = null;

	private JPanel moveUpDownPanel2 = null;

	private JPanel spacePanel2 = null;

	private JRadioButton descendingRadioButton = null;

	private JPanel spacePanel3 = null;

	private JRadioButton ascendingRadioButton = null;

	private ButtonGroup ascDescGroup;  //  @jve:decl-index=0:

	/**
	 * This is the default constructor
	 */
	public ImageObjectExplorerOptionsView() {
		super();
		this.model = new ImageObjectExplorerOptions();
		model.setView(this);
		model.addObserver(this);
		initialize();
	}

	public ImageObjectExplorerOptionsView(ImageObjectExplorerOptions options) {
		super();
		this.model = options;
		model.addObserver(this);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(732, 305);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/extract-foreground-objects.png")));
		this.setContentPane(getJContentPane());
		this.setTitle("Options (MRI Image-Object Explorer) ");
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
			jContentPane.add(getOptionsTabbedPane(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	public void update(Observable sender, Object aspect) {
		if (aspect.equals("sortByMeasurements")) this.handleMeasurementsListChanged();
	}

	private void handleMeasurementsListChanged() {
		getMeasurementsList().setListData(model.getSortByMeasurements().toArray());
	}

	/**
	 * This method initializes optionsTabbedPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	private JTabbedPane getOptionsTabbedPane() {
		if (optionsTabbedPane == null) {
			optionsTabbedPane = new JTabbedPane();
			optionsTabbedPane.setName("");
			optionsTabbedPane.addTab("sort", new ImageIcon(getClass().getResource("/resources/images/sort.gif")), getSortOptionsPanel(), "define by which measurements to sort");
		}
		return optionsTabbedPane;
	}

	/**
	 * This method initializes sortOptionsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getSortOptionsPanel() {
		if (sortOptionsPanel == null) {
			sortOptionsPanel = new JPanel();
			sortOptionsPanel.setLayout(new BoxLayout(getSortOptionsPanel(), BoxLayout.X_AXIS));
			sortOptionsPanel.setName("");
			sortOptionsPanel.setPreferredSize(new Dimension(608, 400));
			sortOptionsPanel.add(getAvailableMeasurementsPanel(), null);
			sortOptionsPanel.add(getMiddlePanel(), null);
			sortOptionsPanel.add(getMeasuremenstPanel(), null);
			sortOptionsPanel.add(getRightPanel(), null);
		}
		return sortOptionsPanel;
	}

	/**
	 * This method initializes availableMeasurementsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getAvailableMeasurementsPanel() {
		if (availableMeasurementsPanel == null) {
			availableMeasurementsPanel = new JPanel();
			availableMeasurementsPanel.setLayout(new BorderLayout());
			availableMeasurementsPanel.setName("availableMeasurementsPanel");
			availableMeasurementsPanel.setBorder(BorderFactory.createTitledBorder(null, "available measurements", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			availableMeasurementsPanel.setPreferredSize(new Dimension(269, 200));
			availableMeasurementsPanel.add(getAvailableMeasurementsScrollPane(), BorderLayout.CENTER);
		}
		return availableMeasurementsPanel;
	}

	/**
	 * This method initializes availableMeasurementsScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getAvailableMeasurementsScrollPane() {
		if (availableMeasurementsScrollPane == null) {
			availableMeasurementsScrollPane = new JScrollPane();
			availableMeasurementsScrollPane.setViewportView(getAvailableMeasurementsList());
		}
		return availableMeasurementsScrollPane;
	}

	/**
	 * This method initializes availableMeasurementsList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getAvailableMeasurementsList() {
		if (availableMeasurementsList == null) {
			availableMeasurementsList = new JList();
			availableMeasurementsList.setListData(model.getAvailableMeasurements());
			availableMeasurementsList.setSize(new Dimension(256, 169));
			availableMeasurementsList.setVisibleRowCount(8);
		}
		return availableMeasurementsList;
	}

	/**
	 * This method initializes addMeasurementButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAddMeasurementButton() {
		if (addMeasurementButton == null) {
			addMeasurementButton = new JButton();
			addMeasurementButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.addMeasurements(getAvailableMeasurementsList().getSelectedValues());
					getAvailableMeasurementsList().setSelectedIndices(new int[0]);
				}
			});
			addMeasurementButton.setName("addMeasurementButton");
			addMeasurementButton.setText(">");
		}
		return addMeasurementButton;
	}

	/**
	 * This method initializes addRemoveButtonPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getAddRemoveButtonPanel() {
		if (addRemoveButtonPanel == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = -1;
			gridBagConstraints1.gridy = -1;
			addRemoveButtonPanel = new JPanel();
			addRemoveButtonPanel.setLayout(new FlowLayout());
			addRemoveButtonPanel.setName("addRemoveButtonPanel");
			addRemoveButtonPanel.setPreferredSize(new Dimension(50, 100));
			addRemoveButtonPanel.setAlignmentX(0.5F);
			addRemoveButtonPanel.add(getAddRemoveButtonPanel2(), null);
		}
		return addRemoveButtonPanel;
	}

	/**
	 * This method initializes removeButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getRemoveButton() {
		if (removeButton == null) {
			removeButton = new JButton();
			removeButton.setText("<");
			removeButton.setName("removeButton");
			removeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.removeMeasurements(getMeasurementsList().getSelectedValues());
				}
			});
		}
		return removeButton;
	}

	/**
	 * This method initializes measuremenstPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getMeasuremenstPanel() {
		if (measuremenstPanel == null) {
			measuremenstPanel = new JPanel();
			measuremenstPanel.setLayout(new BorderLayout());
			measuremenstPanel.setBorder(BorderFactory.createTitledBorder(null, "sort by", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			measuremenstPanel.setPreferredSize(new Dimension(269, 200));
			measuremenstPanel.add(getMeasurementsScrollPane(), BorderLayout.CENTER);
		}
		return measuremenstPanel;
	}

	/**
	 * This method initializes measurementsScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getMeasurementsScrollPane() {
		if (measurementsScrollPane == null) {
			measurementsScrollPane = new JScrollPane();
			measurementsScrollPane.setViewportView(getMeasurementsList());
		}
		return measurementsScrollPane;
	}

	/**
	 * This method initializes measurementsList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getMeasurementsList() {
		if (measurementsList == null) {
			measurementsList = new JList();
		}
		return measurementsList;
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
			middlePanel.add(getEmptyPanel(), BorderLayout.NORTH);
			middlePanel.add(getAddRemoveButtonPanel(), BorderLayout.CENTER);
		}
		return middlePanel;
	}

	/**
	 * This method initializes rightPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getRightPanel() {
		if (rightPanel == null) {
			rightPanel = new JPanel();
			rightPanel.setLayout(new BorderLayout());
			rightPanel.add(getEmptyPanel2(), BorderLayout.NORTH);
			rightPanel.add(getMoveUpDownPanel(), BorderLayout.CENTER);
		}
		return rightPanel;
	}

	/**
	 * This method initializes emptyPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getEmptyPanel() {
		if (emptyPanel == null) {
			emptyPanel = new JPanel();
			emptyPanel.setLayout(null);
			emptyPanel.setPreferredSize(new Dimension(1, 50));
		}
		return emptyPanel;
	}

	/**
	 * This method initializes addRemoveButtonPanel2	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getAddRemoveButtonPanel2() {
		if (addRemoveButtonPanel2 == null) {
			addRemoveButtonPanel2 = new JPanel();
			addRemoveButtonPanel2.setLayout(new BoxLayout(getAddRemoveButtonPanel2(), BoxLayout.Y_AXIS));
			addRemoveButtonPanel2.add(getAddMeasurementButton(), null);
			addRemoveButtonPanel2.add(getSpacePanel(), null);
			addRemoveButtonPanel2.add(getRemoveButton(), null);
		}
		return addRemoveButtonPanel2;
	}

	/**
	 * This method initializes spacePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getSpacePanel() {
		if (spacePanel == null) {
			spacePanel = new JPanel();
			spacePanel.setLayout(new GridBagLayout());
			spacePanel.setPreferredSize(new Dimension(0, 10));
		}
		return spacePanel;
	}

	/**
	 * This method initializes moveUpButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getMoveUpButton() {
		if (moveUpButton == null) {
			moveUpButton = new JButton();
			moveUpButton.setName("addMeasurementButton");
			moveUpButton.setMaximumSize(new Dimension(65, 26));
			moveUpButton.setMinimumSize(new Dimension(65, 26));
			moveUpButton.setMnemonic(KeyEvent.VK_UNDEFINED);
			moveUpButton.setText("up");
			moveUpButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Object[] selectedValues = getMeasurementsList().getSelectedValues();
					int[] indices = model.moveMeasurementsUpByOne(selectedValues);
					getMeasurementsList().setSelectedIndices(indices);
				}
			});
		}
		return moveUpButton;
	}

	/**
	 * This method initializes moveDownButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getMoveDownButton() {
		if (moveDownButton == null) {
			moveDownButton = new JButton();
			moveDownButton.setName("addMeasurementButton");
			moveDownButton.setText("down");
			moveDownButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Object[] selectedValues = getMeasurementsList().getSelectedValues();
					int[] indices = model.moveMeasurementsDownByOne(selectedValues);
					getMeasurementsList().setSelectedIndices(indices);
				}
			});
		}
		return moveDownButton;
	}

	/**
	 * This method initializes emptyPanel2	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getEmptyPanel2() {
		if (emptyPanel2 == null) {
			emptyPanel2 = new JPanel();
			emptyPanel2.setLayout(null);
			emptyPanel2.setPreferredSize(new Dimension(1, 50));
		}
		return emptyPanel2;
	}

	/**
	 * This method initializes moveUpDownPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getMoveUpDownPanel() {
		if (moveUpDownPanel == null) {
			moveUpDownPanel = new JPanel();
			moveUpDownPanel.setLayout(new FlowLayout());
			moveUpDownPanel.add(getMoveUpDownPanel2(), null);
		}
		return moveUpDownPanel;
	}

	/**
	 * This method initializes moveUpDownPanel2	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getMoveUpDownPanel2() {
		if (moveUpDownPanel2 == null) {
			moveUpDownPanel2 = new JPanel();
			moveUpDownPanel2.setLayout(new BoxLayout(getMoveUpDownPanel2(), BoxLayout.Y_AXIS));
			moveUpDownPanel2.add(getMoveUpButton(), null);
			moveUpDownPanel2.add(getSpacePanel2(), null);
			moveUpDownPanel2.add(getMoveDownButton(), null);
			moveUpDownPanel2.add(getSpacePanel3(), null);
			moveUpDownPanel2.add(getDescendingRadioButton(), null);
			moveUpDownPanel2.add(getAscendingRadioButton(), null);
			ascDescGroup = new ButtonGroup();
			ascDescGroup.add(getDescendingRadioButton());
			ascDescGroup.add(getAscendingRadioButton());
		}
		return moveUpDownPanel2;
	}

	/**
	 * This method initializes spacePanel2	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getSpacePanel2() {
		if (spacePanel2 == null) {
			spacePanel2 = new JPanel();
			spacePanel2.setLayout(new GridBagLayout());
			spacePanel2.setPreferredSize(new Dimension(0, 10));
		}
		return spacePanel2;
	}

	/**
	 * This method initializes descendingRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getDescendingRadioButton() {
		if (descendingRadioButton == null) {
			descendingRadioButton = new JRadioButton();
			descendingRadioButton.setText("descending");
			descendingRadioButton.setSelected(model.isSortDescending());
			descendingRadioButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.setSortDescending(true);
				}
			});
		}
		return descendingRadioButton;
	}

	/**
	 * This method initializes spacePanel3	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getSpacePanel3() {
		if (spacePanel3 == null) {
			spacePanel3 = new JPanel();
			spacePanel3.setLayout(new GridBagLayout());
			spacePanel3.setPreferredSize(new Dimension(0, 25));
		}
		return spacePanel3;
	}

	/**
	 * This method initializes ascendingRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getAscendingRadioButton() {
		if (ascendingRadioButton == null) {
			ascendingRadioButton = new JRadioButton();
			ascendingRadioButton.setText("ascending");
			ascendingRadioButton.setSelected(!model.isSortDescending());
			ascendingRadioButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.setSortDescending(false);
				}
			});
		}
		return ascendingRadioButton;
	}

}  //  @jve:decl-index=0:visual-constraint="38,70"
