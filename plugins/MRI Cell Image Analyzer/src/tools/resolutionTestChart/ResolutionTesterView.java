package tools.resolutionTestChart;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.Rectangle;
import javax.swing.JButton;
import java.awt.FlowLayout;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.awt.Toolkit;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import javax.swing.JList;
import java.awt.Point;

public class ResolutionTesterView extends JFrame implements Observer {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JPanel calibratePanel = null;

	private JPanel firstGroupPanel = null;

	private JPanel northPanel = null;

	private JPanel horizontalMeasurementsPanel = null;

	private JScrollPane horizontalScrollPane = null;

	private JScrollPane verticalScrollPane = null;

	private JPanel middlePanel = null;

	private JPanel verticalMeasurementsPanel = null;

	private JPanel horizontalActionPanel = null;

	private JButton addHorizontalMeasurementButton = null;

	private JPanel verticalActionPanel = null;

	private JButton addVerticalMeasurementButton = null;

	private JLabel distanceLabel = null;

	private JComboBox distancesComboBox = null;

	private JLabel pixelLabel = null;

	private JTextField pixelTextField = null;

	private JButton setPixelButton = null;

	private JComboBox firstGroupComboBox = null;

	private JButton setFirstGroupButton = null;

	private ResolutionTester model;

	private JList horizontalMeasurementsList = null;

	private JList verticalMeasurementsList = null;

	private JButton plotButton = null;

	private JPanel horizontalPlotPanel = null;

	private JPanel verticalPlotPanel = null;

	private JButton verticalPlotButton = null;

	private JPanel intensityNormalizationPanel = null;

	private JLabel blackAreaIntensityLabel = null;

	private JLabel whiteAreaIntensityLabel = null;

	private JTextField blackAreaIntensityTextField = null;

	private JButton blackAreaIntensityButton = null;

	private JTextField whiteAreaIntensityTextField = null;

	private JButton whiteAreaIntensityButton = null;

	/**
	 * This is the default constructor
	 */
	public ResolutionTesterView() {
		super();
		model = new ResolutionTester();
		model.addObserver(this);
		initialize();
	}

	public ResolutionTesterView(ResolutionTester tester) {
		super();
		this.model = tester;
		model.addObserver(this);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(388, 427);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/icon.gif")));
		this.setContentPane(getJContentPane());
		this.setTitle("Resolution Tester");
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
			jContentPane.add(getNorthPanel(), BorderLayout.NORTH);
			jContentPane.add(getMiddlePanel(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes calibratePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getCalibratePanel() {
		if (calibratePanel == null) {
			pixelLabel = new JLabel();
			pixelLabel.setBounds(new Rectangle(28, 54, 38, 16));
			pixelLabel.setText("pixel:");
			distanceLabel = new JLabel();
			distanceLabel.setBounds(new Rectangle(28, 22, 71, 16));
			distanceLabel.setText("distance:");
			calibratePanel = new JPanel();
			calibratePanel.setLayout(null);
			calibratePanel.setPreferredSize(new Dimension(0, 80));
			calibratePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "Spatial Calibration", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			calibratePanel.add(distanceLabel, null);
			calibratePanel.add(getDistancesComboBox(), null);
			calibratePanel.add(pixelLabel, null);
			calibratePanel.add(getPixelTextField(), null);
			calibratePanel.add(getSetPixelButton(), null);
		}
		return calibratePanel;
	}

	/**
	 * This method initializes firstGroupPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getFirstGroupPanel() {
		if (firstGroupPanel == null) {
			firstGroupPanel = new JPanel();
			firstGroupPanel.setLayout(null);
			firstGroupPanel.setPreferredSize(new Dimension(0, 60));
			firstGroupPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "First Group", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
			firstGroupPanel.add(getFirstGroupComboBox(), null);
			firstGroupPanel.add(getSetFirstGroupButton(), null);
		}
		return firstGroupPanel;
	}

	/**
	 * This method initializes northPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getNorthPanel() {
		if (northPanel == null) {
			northPanel = new JPanel();
			northPanel.setLayout(new BoxLayout(getNorthPanel(), BoxLayout.Y_AXIS));
			northPanel.add(getCalibratePanel(), null);
			northPanel.add(getIntensityNormalizationPanel(), null);
			northPanel.add(getFirstGroupPanel(), null);
		}
		return northPanel;
	}

	/**
	 * This method initializes horizontalMeasurementsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getHorizontalMeasurementsPanel() {
		if (horizontalMeasurementsPanel == null) {
			horizontalMeasurementsPanel = new JPanel();
			horizontalMeasurementsPanel.setLayout(new BorderLayout());
			horizontalMeasurementsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "horizontal", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			horizontalMeasurementsPanel.add(getHorizontalScrollPane(), BorderLayout.CENTER);
			horizontalMeasurementsPanel.add(getHorizontalActionPanel(), BorderLayout.NORTH);
			horizontalMeasurementsPanel.add(getHorizontalPlotPanel(), BorderLayout.SOUTH);
		}
		return horizontalMeasurementsPanel;
	}

	/**
	 * This method initializes horizontalScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getHorizontalScrollPane() {
		if (horizontalScrollPane == null) {
			horizontalScrollPane = new JScrollPane();
			horizontalScrollPane.setViewportView(getHorizontalMeasurementsList());
		}
		return horizontalScrollPane;
	}

	/**
	 * This method initializes verticalScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getVerticalScrollPane() {
		if (verticalScrollPane == null) {
			verticalScrollPane = new JScrollPane();
			verticalScrollPane.setViewportView(getVerticalMeasurementsList());
		}
		return verticalScrollPane;
	}

	/**
	 * This method initializes middlePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getMiddlePanel() {
		if (middlePanel == null) {
			middlePanel = new JPanel();
			middlePanel.setLayout(new BoxLayout(getMiddlePanel(), BoxLayout.X_AXIS));
			middlePanel.add(getHorizontalMeasurementsPanel(), null);
			middlePanel.add(getVerticalMeasurementsPanel(), null);
		}
		return middlePanel;
	}

	/**
	 * This method initializes verticalMeasurementsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getVerticalMeasurementsPanel() {
		if (verticalMeasurementsPanel == null) {
			verticalMeasurementsPanel = new JPanel();
			verticalMeasurementsPanel.setLayout(new BorderLayout());
			verticalMeasurementsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "vertical", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			verticalMeasurementsPanel.add(getVerticalScrollPane(), BorderLayout.CENTER);
			verticalMeasurementsPanel.add(getVerticalActionPanel(), BorderLayout.NORTH);
			verticalMeasurementsPanel.add(getVerticalPlotPanel(), BorderLayout.SOUTH);
		}
		return verticalMeasurementsPanel;
	}

	/**
	 * This method initializes horizontalActionPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getHorizontalActionPanel() {
		if (horizontalActionPanel == null) {
			horizontalActionPanel = new JPanel();
			horizontalActionPanel.setLayout(new FlowLayout());
			horizontalActionPanel.setPreferredSize(new Dimension(44, 40));
			horizontalActionPanel.add(getAddHorizontalMeasurementButton(), null);
		}
		return horizontalActionPanel;
	}

	/**
	 * This method initializes addHorizontalMeasurementButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAddHorizontalMeasurementButton() {
		if (addHorizontalMeasurementButton == null) {
			addHorizontalMeasurementButton = new JButton();
			addHorizontalMeasurementButton.setPreferredSize(new Dimension(140, 30));
			addHorizontalMeasurementButton.setName("addHorizontalMeasurementButton");
			addHorizontalMeasurementButton.setText("add measurement");
			addHorizontalMeasurementButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							model.addHorizontalMeasurement();
							int index = model.getHorizontalMeasurements().size()-1;
							JList list = getHorizontalMeasurementsList();
							list.setSelectedIndex(index);
							list.ensureIndexIsVisible(index);
						}
					});
		}
		return addHorizontalMeasurementButton;
	}

	/**
	 * This method initializes verticalActionPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getVerticalActionPanel() {
		if (verticalActionPanel == null) {
			verticalActionPanel = new JPanel();
			verticalActionPanel.setLayout(new FlowLayout());
			verticalActionPanel.setPreferredSize(new Dimension(44, 40));
			verticalActionPanel.add(getAddVerticalMeasurementButton(), null);
		}
		return verticalActionPanel;
	}

	/**
	 * This method initializes addVerticalMeasurementButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAddVerticalMeasurementButton() {
		if (addVerticalMeasurementButton == null) {
			addVerticalMeasurementButton = new JButton();
			addVerticalMeasurementButton.setPreferredSize(new Dimension(140, 30));
			addVerticalMeasurementButton.setText("add measurement");
			addVerticalMeasurementButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							model.addVerticalMeasurement();
							int index = model.getVerticalMeasurements().size()-1;
							JList list = getVerticalMeasurementsList();
							list.setSelectedIndex(index);
							list.ensureIndexIsVisible(index);
						}
					});
		}
		return addVerticalMeasurementButton;
	}

	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getDistancesComboBox() {
		if (distancesComboBox == null) {
			distancesComboBox = new JComboBox();
			distancesComboBox.setBounds(new Rectangle(110, 18, 217, 25));
			distancesComboBox.setEditable(true);
			this.handleDistancesChanged();
			distancesComboBox.setSelectedIndex(3);
			Double dist = (Double)getDistancesComboBox().getSelectedItem();
			model.setCalibrationDistance(dist);
			distancesComboBox.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					Double dist = (Double)getDistancesComboBox().getSelectedItem();
					model.setCalibrationDistance(dist.doubleValue());
				}
			});
		}
		return distancesComboBox;
	}

	/**
	 * This method initializes pixelTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getPixelTextField() {
		if (pixelTextField == null) {
			pixelTextField = new JTextField();
			pixelTextField.setText(Integer.toString(model.getCalibrationPixel()));
			pixelTextField.setBounds(new Rectangle(111, 50, 104, 25));
			pixelTextField.addCaretListener(new javax.swing.event.CaretListener() {
				public void caretUpdate(javax.swing.event.CaretEvent e) {
					try {
						int pixel = Integer.parseInt(getPixelTextField().getText());
						model.setCalibrationPixel(pixel);
					} catch (NumberFormatException exc) {
						// noop
					}
				}
			});
		}
		return pixelTextField;
	}

	/**
	 * This method initializes setPixelButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSetPixelButton() {
		if (setPixelButton == null) {
			setPixelButton = new JButton();
			setPixelButton.setBounds(new Rectangle(224, 50, 104, 25));
			setPixelButton.setText("set");
			setPixelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.setCalibrationPixel();
					getPixelTextField().setText(Integer.toString(model.getCalibrationPixel()));
				}
			});
		}
		return setPixelButton;
	}

	/**
	 * This method initializes firstGroupComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getFirstGroupComboBox() {
		if (firstGroupComboBox == null) {
			firstGroupComboBox = new JComboBox(model.getGroupsOfBars());
			firstGroupComboBox.setSelectedIndex(model.getSelectedGroupOfBarsIndex());
			firstGroupComboBox.setPreferredSize(new Dimension(104, 25));
			firstGroupComboBox.setEditable(true);
			firstGroupComboBox.setBounds(new Rectangle(110, 18, 104, 25));
			firstGroupComboBox.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					model.setSelectedGroupOfBarsIndex(getFirstGroupComboBox().getSelectedIndex());
				}
			});
		}
		return firstGroupComboBox;
	}

	/**
	 * This method initializes setFirstGroupButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSetFirstGroupButton() {
		if (setFirstGroupButton == null) {
			setFirstGroupButton = new JButton();
			setFirstGroupButton.setBounds(new Rectangle(224, 18, 104, 25));
			setFirstGroupButton.setText("set");
		}
		return setFirstGroupButton;
	}

	public void update(Observable sender, Object aspect) {
		if (aspect.equals("distances")) this.handleDistancesChanged();
		if (aspect.equals("horizontalMeasurements")) this.handleHorizontalMeasurementsChanged();
		if (aspect.equals("verticalMeasurements")) this.handleVerticalMeasurementsChanged();
		if (aspect.equals("blackAreaIntensity")) this.handleBlackAreaIntensityChanged();
		if (aspect.equals("whiteAreaIntensity")) this.handleWhiteAreaIntensityChanged();
		
	}

	private void handleWhiteAreaIntensityChanged() {
		whiteAreaIntensityTextField.setText(Double.toString(model.getMaxWhiteAreaIntensity()));
	}

	private void handleBlackAreaIntensityChanged() {
		blackAreaIntensityTextField.setText(Double.toString(model.getMinBlackAreaIntensity()));
	}

	private void handleVerticalMeasurementsChanged() {
		JList list = this.getVerticalMeasurementsList();
		list.setListData(new Vector<ResolutionMeasurement>(model.getVerticalMeasurements()));
	}

	private void handleHorizontalMeasurementsChanged() {
		JList list = this.getHorizontalMeasurementsList();
		list.setListData(new Vector<ResolutionMeasurement>(model.getHorizontalMeasurements()));
	}

	private void handleDistancesChanged() {
		getDistancesComboBox().removeAllItems();
		for (double val : model.getDistances()) {
			getDistancesComboBox().addItem(val);
		}
	}

	/**
	 * This method initializes horizontalMeasurementsList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getHorizontalMeasurementsList() {
		if (horizontalMeasurementsList == null) {
			horizontalMeasurementsList = new JList();
		}
		return horizontalMeasurementsList;
	}

	/**
	 * This method initializes verticalMeasurementsList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getVerticalMeasurementsList() {
		if (verticalMeasurementsList == null) {
			verticalMeasurementsList = new JList();
		}
		return verticalMeasurementsList;
	}

	/**
	 * This method initializes plotButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getPlotButton() {
		if (plotButton == null) {
			plotButton = new JButton();
			plotButton.setPreferredSize(new Dimension(70, 30));
			plotButton.setText("plot");
			plotButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.getHorizontalPlot().show();
				}
			});
		}
		return plotButton;
	}

	/**
	 * This method initializes horizontalPlotPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getHorizontalPlotPanel() {
		if (horizontalPlotPanel == null) {
			horizontalPlotPanel = new JPanel();
			horizontalPlotPanel.setLayout(new FlowLayout());
			horizontalPlotPanel.setPreferredSize(new Dimension(1, 40));
			horizontalPlotPanel.add(getPlotButton(), null);
		}
		return horizontalPlotPanel;
	}

	/**
	 * This method initializes verticalPlotPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getVerticalPlotPanel() {
		if (verticalPlotPanel == null) {
			verticalPlotPanel = new JPanel();
			verticalPlotPanel.setLayout(new FlowLayout());
			verticalPlotPanel.setPreferredSize(new Dimension(10, 40));
			verticalPlotPanel.add(getVerticalPlotButton(), null);
		}
		return verticalPlotPanel;
	}

	/**
	 * This method initializes verticalPlotButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getVerticalPlotButton() {
		if (verticalPlotButton == null) {
			verticalPlotButton = new JButton();
			verticalPlotButton.setPreferredSize(new Dimension(70, 30));
			verticalPlotButton.setText("plot");
			verticalPlotButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.getVerticalPlot().show();
				}
			});
		}
		return verticalPlotButton;
	}

	/**
	 * This method initializes intensityNormalizationPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getIntensityNormalizationPanel() {
		if (intensityNormalizationPanel == null) {
			whiteAreaIntensityLabel = new JLabel();
			whiteAreaIntensityLabel.setBounds(new Rectangle(207, 23, 156, 16));
			whiteAreaIntensityLabel.setText("max. white area intensity");
			blackAreaIntensityLabel = new JLabel();
			blackAreaIntensityLabel.setBounds(new Rectangle(7, 23, 158, 16));
			blackAreaIntensityLabel.setText("min.  black area intensity");
			intensityNormalizationPanel = new JPanel();
			intensityNormalizationPanel.setLayout(null);
			intensityNormalizationPanel.setPreferredSize(new Dimension(0, 80));
			intensityNormalizationPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "Intensity Normalization", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
			intensityNormalizationPanel.add(blackAreaIntensityLabel, null);
			intensityNormalizationPanel.add(whiteAreaIntensityLabel, null);
			intensityNormalizationPanel.add(getBlackAreaIntensityTextField(), null);
			intensityNormalizationPanel.add(getBlackAreaIntensityButton(), null);
			intensityNormalizationPanel.add(getWhiteAreaIntensityTextField(), null);
			intensityNormalizationPanel.add(getWhiteAreaIntensityButton(), null);
		}
		return intensityNormalizationPanel;
	}

	/**
	 * This method initializes blackAreaIntensityTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getBlackAreaIntensityTextField() {
		if (blackAreaIntensityTextField == null) {
			blackAreaIntensityTextField = new JTextField();
			blackAreaIntensityTextField.setBounds(new Rectangle(7, 44, 77, 24));
			blackAreaIntensityTextField.setEditable(false);
			blackAreaIntensityTextField.setText(Double.toString(model.getMinBlackAreaIntensity()));
		}
		return blackAreaIntensityTextField;
	}

	/**
	 * This method initializes blackAreaIntensityButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBlackAreaIntensityButton() {
		if (blackAreaIntensityButton == null) {
			blackAreaIntensityButton = new JButton();
			blackAreaIntensityButton.setText("set");
			blackAreaIntensityButton.setLocation(new Point(101, 44));
			blackAreaIntensityButton.setSize(new Dimension(64, 26));
			blackAreaIntensityButton.setPreferredSize(new Dimension(52, 26));
			blackAreaIntensityButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.updateBlackAreaIntensity();
				}
			});
		}
		return blackAreaIntensityButton;
	}

	/**
	 * This method initializes whiteAreaIntensityTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getWhiteAreaIntensityTextField() {
		if (whiteAreaIntensityTextField == null) {
			whiteAreaIntensityTextField = new JTextField();
			whiteAreaIntensityTextField.setBounds(new Rectangle(207, 44, 77, 24));
			whiteAreaIntensityTextField.setEditable(false);
			whiteAreaIntensityTextField.setText(Double.toString(model.getMaxWhiteAreaIntensity()));
		}
		return whiteAreaIntensityTextField;
	}

	/**
	 * This method initializes whiteAreaIntensityButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getWhiteAreaIntensityButton() {
		if (whiteAreaIntensityButton == null) {
			whiteAreaIntensityButton = new JButton();
			whiteAreaIntensityButton.setBounds(new Rectangle(299, 44, 64, 26));
			whiteAreaIntensityButton.setText("set");
			whiteAreaIntensityButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.updateWhiteAreaIntensity();
				}
			});
		}
		return whiteAreaIntensityButton;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
