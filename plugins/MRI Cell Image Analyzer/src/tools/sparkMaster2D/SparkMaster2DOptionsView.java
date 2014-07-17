package tools.sparkMaster2D;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Toolkit;
import javax.swing.JTabbedPane;
import javax.swing.JCheckBox;
import java.awt.Rectangle;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Dimension;
import java.awt.Point;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JTextField;
import operations.Operation;

public class SparkMaster2DOptionsView extends JFrame implements Observer {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JTabbedPane optionsTabbedPane = null;

	private JCheckBox maskCellCheckBox = null;

	private JPanel maskCellPanel = null;

	private JPanel preProcessingPanel = null;

	private JPanel statisticalFilteringPanel = null;

	private SparkMaster2D model;

	private JComboBox thresholdOperationComboBox = null;

	private JButton thresholdOperationOptionsButton = null;

	private JLabel thresholdLabel = null;

	private JLabel thresholdSparksLabel = null;

	private JLabel smoothingOperationLabel = null;

	private JComboBox smoothingOperationComboBox = null;

	private JButton smoothingOptionsButton = null;

	private JTextField thresholdSparksTextField = null;

	private JButton liveOrDieOptionsButton = null;

	private JLabel radiusLabel = null;

	private JTextField radiusTextField = null;

	private JLabel alphaLabel = null;

	private JTextField alphaTextField = null;

	private JLabel betaLabel = null;

	private JTextField betaTextField = null;  //  @jve:decl-index=0:

	private JPanel debugPanel = null;

	private JCheckBox batchModeCheckBox = null;

	private JCheckBox closeImagesCheckBox = null;

	private JCheckBox stopAfterEachStepCheckBox = null;

	private JCheckBox smoothCheckBox = null;

	private JPanel normalizationPanel = null;

	private JCheckBox doOnlyNormalizationCheckBox = null;

	private JLabel normalizationRadiusLabel = null;

	private JTextField normalizationRadiusTextField = null;

	/**
	 * This is the default constructor
	 */
	public SparkMaster2DOptionsView() {
		super();
		initialize();
	}

	public SparkMaster2DOptionsView(SparkMaster2D master2D) {
		super();
		model = master2D;
		model.addObserver(this);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(336, 200);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/icon.gif")));
		this.setContentPane(getJContentPane());
		this.setTitle("MRI Spark Master 2D Options");
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				try {
					model.saveOptions();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
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

	/**
	 * This method initializes optionsTabbedPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	private JTabbedPane getOptionsTabbedPane() {
		if (optionsTabbedPane == null) {
			optionsTabbedPane = new JTabbedPane();
			optionsTabbedPane.addTab("mask cell", null, getMaskCellPanel(), null);
			optionsTabbedPane.addTab("pre-processing", null, getPreProcessingPanel(), null);
			optionsTabbedPane.addTab("statistical filtering", null, getStatisticalFilteringPanel(), null);
			optionsTabbedPane.addTab("debug", null, getDebugPanel(), null);
			optionsTabbedPane.addTab("normalization", null, getNormalizationPanel(), null);
		}
		return optionsTabbedPane;
	}

	/**
	 * This method initializes maskCellCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getMaskCellCheckBox() {
		if (maskCellCheckBox == null) {
			maskCellCheckBox = new JCheckBox();
			maskCellCheckBox.setSelected(model.maskCell);
			maskCellCheckBox.setBounds(new Rectangle(5, 5, 84, 21));
			maskCellCheckBox.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.setMaskCell(!model.getMaskCell());
				}
			});
			maskCellCheckBox.setText("mask cell");
		}
		return maskCellCheckBox;
	}

	/**
	 * This method initializes maskCellPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getMaskCellPanel() {
		if (maskCellPanel == null) {
			thresholdLabel = new JLabel();
			thresholdLabel.setBounds(new Rectangle(5, 38, 148, 16));
			thresholdLabel.setText("threshold operation:");
			maskCellPanel = new JPanel();
			maskCellPanel.setLayout(null);
			maskCellPanel.add(getMaskCellCheckBox(), null);
			maskCellPanel.add(getThresholdOperationComboBox(), null);
			maskCellPanel.add(getThresholdOperationOptionsButton(), null);
			maskCellPanel.add(thresholdLabel, null);
		}
		return maskCellPanel;
	}

	/**
	 * This method initializes preProcessingPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getPreProcessingPanel() {
		if (preProcessingPanel == null) {
			smoothingOperationLabel = new JLabel();
			smoothingOperationLabel.setText("smoothing operation");
			smoothingOperationLabel.setLocation(new Point(5, 5));
			smoothingOperationLabel.setSize(new Dimension(196, 16));
			thresholdSparksLabel = new JLabel();
			thresholdSparksLabel.setText("threshold:");
			thresholdSparksLabel.setLocation(new Point(5, 58));
			thresholdSparksLabel.setSize(new Dimension(59, 16));
			preProcessingPanel = new JPanel();
			preProcessingPanel.setLayout(null);
			preProcessingPanel.add(thresholdSparksLabel, null);
			preProcessingPanel.add(smoothingOperationLabel, null);
			preProcessingPanel.add(getSmoothingOperationComboBox(), null);
			preProcessingPanel.add(getSmoothingOptionsButton(), null);
			preProcessingPanel.add(getThresholdSparksTextField(), null);
			preProcessingPanel.add(getLiveOrDieOptionsButton(), null);
			preProcessingPanel.add(getSmoothCheckBox(), null);
		}
		return preProcessingPanel;
	}

	/**
	 * This method initializes statisticalFilteringPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getStatisticalFilteringPanel() {
		if (statisticalFilteringPanel == null) {
			betaLabel = new JLabel();
			betaLabel.setBounds(new Rectangle(5, 48, 162, 16));
			betaLabel.setText("beta confidence threshold:");
			alphaLabel = new JLabel();
			alphaLabel.setBounds(new Rectangle(5, 29, 162, 16));
			alphaLabel.setText("alpha confidence threshold:");
			radiusLabel = new JLabel();
			radiusLabel.setText("radius:");
			radiusLabel.setLocation(new Point(5, 5));
			radiusLabel.setSize(new Dimension(48, 16));
			statisticalFilteringPanel = new JPanel();
			statisticalFilteringPanel.setLayout(null);
			statisticalFilteringPanel.add(radiusLabel, null);
			statisticalFilteringPanel.add(getRadiusTextField(), null);
			statisticalFilteringPanel.add(alphaLabel, null);
			statisticalFilteringPanel.add(getAlphaTextField(), null);
			statisticalFilteringPanel.add(betaLabel, null);
			statisticalFilteringPanel.add(getBetaTextField(), null);
		}
		return statisticalFilteringPanel;
	}

	/**
	 * This method initializes thresholdOperationComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getThresholdOperationComboBox() {
		if (thresholdOperationComboBox == null) {
			thresholdOperationComboBox = new JComboBox(model.thresholdOperations);
			thresholdOperationComboBox.setBounds(new Rectangle(5, 56, 176, 25));
		}
		return thresholdOperationComboBox;
	}

	/**
	 * This method initializes thresholdOperationOptionsButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getThresholdOperationOptionsButton() {
		if (thresholdOperationOptionsButton == null) {
			thresholdOperationOptionsButton = new JButton();
			thresholdOperationOptionsButton.setBounds(new Rectangle(196, 56, 105, 25));
			thresholdOperationOptionsButton.setText("options");
			thresholdOperationOptionsButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							Operation op =(Operation)thresholdOperationComboBox.getSelectedItem();
							op.getOptions().view().setVisible(true);
						}
					});
		}
		return thresholdOperationOptionsButton;
	}

	/**
	 * This method initializes smoothingOperationComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getSmoothingOperationComboBox() {
		if (smoothingOperationComboBox == null) {
			smoothingOperationComboBox = new JComboBox(model.smoothingOperations);
			smoothingOperationComboBox.setBounds(new Rectangle(30, 24, 168, 25));
		}
		return smoothingOperationComboBox;
	}

	/**
	 * This method initializes smoothingOptionsButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSmoothingOptionsButton() {
		if (smoothingOptionsButton == null) {
			smoothingOptionsButton = new JButton();
			smoothingOptionsButton.setBounds(new Rectangle(208, 24, 108, 25));
			smoothingOptionsButton.setText("options");
			smoothingOptionsButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Operation op = (Operation) smoothingOperationComboBox.getSelectedItem();
					op.getOptions().view().setVisible(true);
				}
			});
		}
		return smoothingOptionsButton;
	}

	/**
	 * This method initializes thresholdSparksTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getThresholdSparksTextField() {
		if (thresholdSparksTextField == null) {
			thresholdSparksTextField = new JTextField(Float.toString(model.sparksThreshold));
			thresholdSparksTextField.setBounds(new Rectangle(66, 56, 135, 20));
			thresholdSparksTextField.addFocusListener(new java.awt.event.FocusAdapter() {
				public void focusLost(java.awt.event.FocusEvent e) {
					try {
						model.setSparksThreshold(Float.parseFloat(thresholdSparksTextField.getText()));
					} catch (NumberFormatException exc) {
						model.setSparksThreshold(model.sparksThreshold);
					}
				}
			});
			thresholdSparksTextField.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						model.setSparksThreshold(Float.parseFloat(thresholdSparksTextField.getText()));
					} catch (NumberFormatException exc) {
						model.setSparksThreshold(model.sparksThreshold);
					}
				}
			});
		}
		return thresholdSparksTextField;
	}

	/**
	 * This method initializes liveOrDieOptionsButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getLiveOrDieOptionsButton() {
		if (liveOrDieOptionsButton == null) {
			liveOrDieOptionsButton = new JButton();
			liveOrDieOptionsButton.setBounds(new Rectangle(5, 84, 196, 25));
			liveOrDieOptionsButton.setText("live or die operation options");
			liveOrDieOptionsButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.liveOrDieOperation.getOptions().view().setVisible(true);
				}
			});
		}
		return liveOrDieOptionsButton;
	}

	/**
	 * This method initializes radiusTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getRadiusTextField() {
		if (radiusTextField == null) {
			radiusTextField = new JTextField();
			radiusTextField.setBounds(new Rectangle(55, 3, 48, 20));
			radiusTextField.setText(Integer.toString(model.statisticalFilterRadius));
			radiusTextField.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						model.setStatisticalFilterRadius(Integer.parseInt(thresholdSparksTextField.getText()));
					} catch (NumberFormatException exc) {
						model.setStatisticalFilterRadius(model.statisticalFilterRadius);
					}
				}
			});
			radiusTextField.addFocusListener(new java.awt.event.FocusAdapter() {
				public void focusLost(java.awt.event.FocusEvent e) {
					try {
						model.setStatisticalFilterRadius(Integer.parseInt(thresholdSparksTextField.getText()));
					} catch (NumberFormatException exc) {
						model.setStatisticalFilterRadius(model.statisticalFilterRadius);
					}
				}
			});
		}
		return radiusTextField;
	}

	/**
	 * This method initializes alphaTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getAlphaTextField() {
		if (alphaTextField == null) {
			alphaTextField = new JTextField();
			alphaTextField.setText(Double.toString(model.alphaConfidenceThreshold));
			alphaTextField.setBounds(new Rectangle(170, 27, 71, 20));
			alphaTextField.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						model.setAlphaConfidenceThreshold(Double.parseDouble(alphaTextField.getText()));
					} catch (NumberFormatException exc) {
						model.setAlphaConfidenceThreshold(model.alphaConfidenceThreshold);
					}
				}
			});
			alphaTextField.addFocusListener(new java.awt.event.FocusAdapter() {
				public void focusLost(java.awt.event.FocusEvent e) {
					try {
						model.setAlphaConfidenceThreshold(Double.parseDouble(alphaTextField.getText()));
					} catch (NumberFormatException exc) {
						model.setAlphaConfidenceThreshold(model.alphaConfidenceThreshold);
					}
				}
			});
		}
		return alphaTextField;
	}

	/**
	 * This method initializes betaTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getBetaTextField() {
		if (betaTextField == null) {
			betaTextField = new JTextField();
			betaTextField.setBounds(new Rectangle(170, 46, 71, 20));
			betaTextField.setText(Double.toString(model.betaConfidenceThreshold));
			betaTextField.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						model.setBetaConfidenceThreshold(Double.parseDouble(betaTextField.getText()));
					} catch (NumberFormatException exc) {
						model.setBetaConfidenceThreshold(model.betaConfidenceThreshold);
					}
				}
			});
			betaTextField.addFocusListener(new java.awt.event.FocusAdapter() {
				public void focusLost(java.awt.event.FocusEvent e) {
					try {
						model.setBetaConfidenceThreshold(Double.parseDouble(betaTextField.getText()));
					} catch (NumberFormatException exc) {
						model.setBetaConfidenceThreshold(model.betaConfidenceThreshold);
					}
				}
			});
		}
		return betaTextField;
	}

	public void update(Observable sender, Object aspect) {
		if (aspect.equals("mask cell")) this.handleMaskCellChanged();
		if (aspect.equals("sparks threshold")) this.handleSparksThresholdChanged();
		if (aspect.equals("statistical filter radius")) this.handleStatisticalFilterRadiusChanged(); 
		if (aspect.equals("alpha confidence threshold")) this.handleAlphaConfidenceThresholdChanged();
		if (aspect.equals("beta confidence threshold")) this.handleBetaConfidenceThresholdChanged();
	}

	private void handleBetaConfidenceThresholdChanged() {
		getBetaTextField().setText(Double.toString(model.betaConfidenceThreshold));
	}

	private void handleAlphaConfidenceThresholdChanged() {
		getAlphaTextField().setText(Double.toString(model.alphaConfidenceThreshold));
	}

	private void handleStatisticalFilterRadiusChanged() {
		getRadiusTextField().setText(Integer.toString(model.statisticalFilterRadius));
	}

	private void handleSparksThresholdChanged() {
		getThresholdSparksTextField().setText(Float.toString(model.sparksThreshold));
	}

	private void handleMaskCellChanged() {
		getMaskCellCheckBox().setSelected(model.getMaskCell());
	}

	/**
	 * This method initializes debugPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getDebugPanel() {
		if (debugPanel == null) {
			debugPanel = new JPanel();
			debugPanel.setLayout(null);
			debugPanel.add(getBatchModeCheckBox(), null);
			debugPanel.add(getCloseImagesCheckBox(), null);
			debugPanel.add(getStopAfterEachStepCheckBox(), null);
		}
		return debugPanel;
	}

	/**
	 * This method initializes batchModeCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getBatchModeCheckBox() {
		if (batchModeCheckBox == null) {
			batchModeCheckBox = new JCheckBox();
			batchModeCheckBox.setSelected(model.useBatchMode());
			batchModeCheckBox.setBounds(new Rectangle(16, 15, 124, 21));
			batchModeCheckBox.setText("batch mode");
			batchModeCheckBox.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.setUseBatchMode(!model.useBatchMode());
				}
			});
		}
		return batchModeCheckBox;
	}

	/**
	 * This method initializes closeImagesCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getCloseImagesCheckBox() {
		if (closeImagesCheckBox == null) {
			closeImagesCheckBox = new JCheckBox();
			closeImagesCheckBox.setBounds(new Rectangle(16, 41, 131, 21));
			closeImagesCheckBox.setText("close images");
			closeImagesCheckBox.setSelected(model.closeImages());
			closeImagesCheckBox.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.setCloseImages(!model.closeImages());
				}
			});
		}
		return closeImagesCheckBox;
	}

	/**
	 * This method initializes stopAfterEachStepCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getStopAfterEachStepCheckBox() {
		if (stopAfterEachStepCheckBox == null) {
			stopAfterEachStepCheckBox = new JCheckBox();
			stopAfterEachStepCheckBox.setBounds(new Rectangle(150, 15, 148, 21));
			stopAfterEachStepCheckBox.setText("stop after each step");
			stopAfterEachStepCheckBox.setSelected(model.stopAfterEachStep());
			stopAfterEachStepCheckBox
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							model.setStopAfterEachStep(!model.stopAfterEachStep());
						}
					});
		}
		return stopAfterEachStepCheckBox;
	}

	/**
	 * This method initializes smoothCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getSmoothCheckBox() {
		if (smoothCheckBox == null) {
			smoothCheckBox = new JCheckBox();
			smoothCheckBox.setBounds(new Rectangle(5, 24, 21, 21));
			smoothCheckBox.setSelected(model.isSmoothImage());
			smoothCheckBox.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.setSmoothImage(!model.isSmoothImage());
				}
			});
		}
		return smoothCheckBox;
	}

	/**
	 * This method initializes normalizationPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getNormalizationPanel() {
		if (normalizationPanel == null) {
			normalizationRadiusLabel = new JLabel();
			normalizationRadiusLabel.setBounds(new Rectangle(25, 34, 51, 16));
			normalizationRadiusLabel.setText("radius:");
			normalizationPanel = new JPanel();
			normalizationPanel.setLayout(null);
			normalizationPanel.add(getDoOnlyNormalizationCheckBox(), null);
			normalizationPanel.add(normalizationRadiusLabel, null);
			normalizationPanel.add(getNormalizationRadiusTextField(), null);
		}
		return normalizationPanel;
	}

	/**
	 * This method initializes doOnlyNormalizationCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getDoOnlyNormalizationCheckBox() {
		if (doOnlyNormalizationCheckBox == null) {
			doOnlyNormalizationCheckBox = new JCheckBox();
			doOnlyNormalizationCheckBox.setText("do only normalization");
			doOnlyNormalizationCheckBox.setBounds(new Rectangle(9, 5, 145, 24));
			doOnlyNormalizationCheckBox.setSelected(model.isDoOnlyNormalization());
			doOnlyNormalizationCheckBox
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							model.setDoOnlyNormalization(!model.isDoOnlyNormalization());
						}
					});
		}
		return doOnlyNormalizationCheckBox;
	}

	/**
	 * This method initializes normalizationRadiusTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getNormalizationRadiusTextField() {
		if (normalizationRadiusTextField == null) {
			normalizationRadiusTextField = new JTextField();
			normalizationRadiusTextField.setBounds(new Rectangle(82, 32, 37, 20));
			normalizationRadiusTextField.setText(""+model.getNormalizationRadius());
			normalizationRadiusTextField
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							int value = Integer.parseInt(normalizationRadiusTextField.getText());
							model.setNormalizationRadius(value);
						}
					});
			normalizationRadiusTextField
					.addFocusListener(new java.awt.event.FocusAdapter() {
						public void focusLost(java.awt.event.FocusEvent e) {
							int value = Integer.parseInt(normalizationRadiusTextField.getText());
							model.setNormalizationRadius(value);
						}
					});
		}
		return normalizationRadiusTextField;
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
