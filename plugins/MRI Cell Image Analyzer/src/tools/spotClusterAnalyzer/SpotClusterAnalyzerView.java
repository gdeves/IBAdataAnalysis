package tools.spotClusterAnalyzer;

import ij.IJ;
import ij.ImagePlus;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JLabel;
import java.awt.Rectangle;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import javax.swing.BoxLayout;
import operations.file.OpenImageOperation;

public class SpotClusterAnalyzerView extends JFrame implements Observer {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JPanel imagePanel = null;

	private JLabel blueImageLabel = null;

	private JLabel redImageLabel = null;

	private JLabel greenImageLabel = null;

	private JTextField blueTextField = null;

	private JTextField redTextField = null;

	private JTextField greenTextField = null;

	private JButton browseBlueButton = null;

	private JButton browseRedButton = null;

	private JButton browseGreenButton = null;

	private JButton showHideGreenButton = null;

	private JButton showHideRedButton = null;

	private JButton showHideblueButton = null;

	private JLabel compositeImageLabel = null;

	private JButton showHideCompositeButton = null;

	private JPanel mainPanel = null;

	private JPanel pointsPanel = null;

	private JLabel redPointsLabel = null;

	private JTextField redPointsTextField = null;

	private JButton showHideRedPointsButton = null;

	private JButton calculateRedPoints = null;

	private JLabel greenPointsLabel = null;

	private JTextField greenPointsTextField = null;

	private JButton calculateGreenPoints = null;

	private JButton showHideGreenPointsButton = null;

	private JPanel graphPanel = null;

	private JButton calculateGraphButton = null;

	private JButton showGraphTextButton = null;

	private JButton showGraphGraphicalButton = null;

	private SpotClusterAnalyzer model;

	private JLabel minLabel = null;

	private JSpinner thresholdSpinner = null;

	private JLabel maxDistLabel = null;
	
	private JSpinner maxDistanceSpinner = null;

	/**
	 * This is the default constructor
	 */
	public SpotClusterAnalyzerView() {
		super();
		initialize();
		this.model = new SpotClusterAnalyzer();
		model.addObserver(this);
	}

	public SpotClusterAnalyzerView(SpotClusterAnalyzer analyzer) {
		super();
		this.model = analyzer;
		initialize();
		model.addObserver(this);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(360, 364);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/clusters.gif")));
		this.setContentPane(getJContentPane());
		this.setTitle("MRI Spot-Cluster Analyzer");
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
			jContentPane.add(getMainPanel(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes imagePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getImagePanel() {
		if (imagePanel == null) {
			compositeImageLabel = new JLabel();
			compositeImageLabel.setBounds(new Rectangle(83, 79, 83, 19));
			compositeImageLabel.setText("composite:");
			compositeImageLabel.setFont(new Font("Dialog", Font.BOLD, 14));
			greenImageLabel = new JLabel();
			greenImageLabel.setBounds(new Rectangle(11, 57, 49, 19));
			greenImageLabel.setText("green:");
			greenImageLabel.setFont(new Font("Dialog", Font.BOLD, 14));
			redImageLabel = new JLabel();
			redImageLabel.setBounds(new Rectangle(11, 36, 35, 19));
			redImageLabel.setText("red:");
			redImageLabel.setFont(new Font("Dialog", Font.BOLD, 14));
			blueImageLabel = new JLabel();
			blueImageLabel.setBounds(new Rectangle(11, 16, 38, 16));
			blueImageLabel.setFont(new Font("Dialog", Font.BOLD, 14));
			blueImageLabel.setText("blue:");
			imagePanel = new JPanel();
			imagePanel.setLayout(null);
			imagePanel.setBorder(BorderFactory.createTitledBorder(null, "images", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
			imagePanel.setPreferredSize(new Dimension(1, 100));
			imagePanel.setMaximumSize(new Dimension(32767, 100));
			imagePanel.add(blueImageLabel, null);
			imagePanel.add(redImageLabel, null);
			imagePanel.add(greenImageLabel, null);
			imagePanel.add(getBlueTextField(), null);
			imagePanel.add(getRedTextField(), null);
			imagePanel.add(getGreenTextField(), null);
			imagePanel.add(getBrowseBlueButton(), null);
			imagePanel.add(getBrowseRedButton(), null);
			imagePanel.add(getBrowseGreenButton(), null);
			imagePanel.add(getShowHideGreenButton(), null);
			imagePanel.add(getShowHideRedButton(), null);
			imagePanel.add(getShowHideblueButton(), null);
			imagePanel.add(compositeImageLabel, null);
			imagePanel.add(getShowHideCompositeButton(), null);
		}
		return imagePanel;
	}

	/**
	 * This method initializes blueTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getBlueTextField() {
		if (blueTextField == null) {
			blueTextField = new JTextField();
			blueTextField.setBounds(new Rectangle(65, 15, 101, 18));
			blueTextField.setEditable(false);
			blueTextField.setEnabled(true);
		}
		return blueTextField;
	}

	/**
	 * This method initializes redTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getRedTextField() {
		if (redTextField == null) {
			redTextField = new JTextField();
			redTextField.setBounds(new Rectangle(65, 36, 101, 18));
			redTextField.setEnabled(true);
			redTextField.setEditable(false);
		}
		return redTextField;
	}

	/**
	 * This method initializes greenTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getGreenTextField() {
		if (greenTextField == null) {
			greenTextField = new JTextField();
			greenTextField.setBounds(new Rectangle(65, 57, 101, 18));
			greenTextField.setEnabled(true);
			greenTextField.setEditable(false);
		}
		return greenTextField;
	}

	/**
	 * This method initializes browseBlueButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBrowseBlueButton() {
		if (browseBlueButton == null) {
			browseBlueButton = new JButton();
			browseBlueButton.setBounds(new Rectangle(173, 15, 67, 18));
			browseBlueButton.setFont(new Font("Dialog", Font.PLAIN, 10));
			browseBlueButton.setText("browse");
			browseBlueButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					OpenImageOperation open = openImage();
					if (open.getResult()==null) return;
					model.setBlueImage(open.getResult()); 
				}
			});
		}
		return browseBlueButton;
	}

	/**
	 * This method initializes browseRedButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBrowseRedButton() {
		if (browseRedButton == null) {
			browseRedButton = new JButton();
			browseRedButton.setBounds(new Rectangle(173, 36, 67, 18));
			browseRedButton.setText("browse");
			browseRedButton.setFont(new Font("Dialog", Font.PLAIN, 10));
			browseRedButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					OpenImageOperation open = openImage();
					if (open.getResult()==null) return;
					model.setRedImage(open.getResult()); 
				}
			});
		}
		return browseRedButton;
	}

	/**
	 * This method initializes browseGreenButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBrowseGreenButton() {
		if (browseGreenButton == null) {
			browseGreenButton = new JButton();
			browseGreenButton.setBounds(new Rectangle(173, 57, 66, 18));
			browseGreenButton.setText("browse");
			browseGreenButton.setFont(new Font("Dialog", Font.PLAIN, 10));
			browseGreenButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					OpenImageOperation open = openImage();
					if (open.getResult()==null) return;
					model.setGreenImage(open.getResult()); 
				}
			});
		}
		return browseGreenButton;
	}

	/**
	 * This method initializes showHideGreenButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getShowHideGreenButton() {
		if (showHideGreenButton == null) {
			showHideGreenButton = new JButton();
			showHideGreenButton.setBounds(new Rectangle(247, 57, 94, 18));
			showHideGreenButton.setText("show / hide");
			showHideGreenButton.setFont(new Font("Dialog", Font.PLAIN, 10));
			showHideGreenButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.toggleShowImage(model.getGreenImage());
				}
			});
		}
		return showHideGreenButton;
	}

	/**
	 * This method initializes showHideRedButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getShowHideRedButton() {
		if (showHideRedButton == null) {
			showHideRedButton = new JButton();
			showHideRedButton.setBounds(new Rectangle(247, 36, 94, 18));
			showHideRedButton.setText("show / hide");
			showHideRedButton.setFont(new Font("Dialog", Font.PLAIN, 10));
			showHideRedButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.toggleShowImage(model.getRedImage());
				}
			});
		}
		return showHideRedButton;
	}

	/**
	 * This method initializes showHideblueButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getShowHideblueButton() {
		if (showHideblueButton == null) {
			showHideblueButton = new JButton();
			showHideblueButton.setBounds(new Rectangle(247, 15, 94, 18));
			showHideblueButton.setText("show / hide");
			showHideblueButton.setFont(new Font("Dialog", Font.PLAIN, 10));
			showHideblueButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.toggleShowImage(model.getBlueImage());
				}
			});
		}
		return showHideblueButton;
	}

	/**
	 * This method initializes showHideCompositeButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getShowHideCompositeButton() {
		if (showHideCompositeButton == null) {
			showHideCompositeButton = new JButton();
			showHideCompositeButton.setBounds(new Rectangle(247, 79, 94, 18));
			showHideCompositeButton.setText("show / hide");
			showHideCompositeButton.setFont(new Font("Dialog", Font.PLAIN, 10));
			showHideCompositeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.toggleShowImage(model.getComposite());
				}
			});
		}
		return showHideCompositeButton;
	}

	/**
	 * This method initializes mainPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(new BoxLayout(getMainPanel(), BoxLayout.Y_AXIS));
			mainPanel.add(getImagePanel(), null);
			mainPanel.add(getPointsPanel(), null);
			mainPanel.add(getGraphPanel(), null);
		}
		return mainPanel;
	}

	/**
	 * This method initializes pointsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getPointsPanel() {
		if (pointsPanel == null) {
			greenPointsLabel = new JLabel();
			greenPointsLabel.setBounds(new Rectangle(5, 41, 58, 19));
			greenPointsLabel.setText("green:");
			greenPointsLabel.setFont(new Font("Dialog", Font.BOLD, 14));
			redPointsLabel = new JLabel();
			redPointsLabel.setBounds(new Rectangle(5, 19, 54, 16));
			redPointsLabel.setFont(new Font("Dialog", Font.BOLD, 14));
			redPointsLabel.setText("red:");
			pointsPanel = new JPanel();
			pointsPanel.setLayout(null);
			pointsPanel.setPreferredSize(new Dimension(1, 1));
			pointsPanel.setBorder(BorderFactory.createTitledBorder(null, "points", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
			pointsPanel.setMaximumSize(new Dimension(32767, 100));
			pointsPanel.add(redPointsLabel, null);
			pointsPanel.add(getRedPointsTextField(), null);
			pointsPanel.add(getShowHideRedPointsButton(), null);
			pointsPanel.add(getCalculateRedPoints(), null);
			pointsPanel.add(greenPointsLabel, null);
			pointsPanel.add(getGreenPointsTextField(), null);
			pointsPanel.add(getCalculateGreenPoints(), null);
			pointsPanel.add(getShowHideGreenPointsButton(), null);
		}
		return pointsPanel;
	}

	/**
	 * This method initializes redPointsTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getRedPointsTextField() {
		if (redPointsTextField == null) {
			redPointsTextField = new JTextField();
			redPointsTextField.setBounds(new Rectangle(64, 19, 83, 18));
			redPointsTextField.setEditable(false);
		}
		return redPointsTextField;
	}

	/**
	 * This method initializes showHideRedPointsButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getShowHideRedPointsButton() {
		if (showHideRedPointsButton == null) {
			showHideRedPointsButton = new JButton();
			showHideRedPointsButton.setBounds(new Rectangle(246, 19, 94, 18));
			showHideRedPointsButton.setText("show / hide");
			showHideRedPointsButton.setFont(new Font("Dialog", Font.PLAIN, 10));
			showHideRedPointsButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.toggleShowRedPoints();
				}
			});
		}
		return showHideRedPointsButton;
	}

	/**
	 * This method initializes calculateRedPoints	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCalculateRedPoints() {
		if (calculateRedPoints == null) {
			calculateRedPoints = new JButton();
			calculateRedPoints.setBounds(new Rectangle(153, 19, 83, 18));
			calculateRedPoints.setText("calculate");
			calculateRedPoints.setFont(new Font("Dialog", Font.PLAIN, 10));
			calculateRedPoints.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ImagePlus mask = IJ.getImage();
					model.calculateRedPoints(mask);
				}
			});
		}
		return calculateRedPoints;
	}

	/**
	 * This method initializes greenPointsTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getGreenPointsTextField() {
		if (greenPointsTextField == null) {
			greenPointsTextField = new JTextField();
			greenPointsTextField.setBounds(new Rectangle(64, 41, 83, 18));
			greenPointsTextField.setEditable(false);
		}
		return greenPointsTextField;
	}

	/**
	 * This method initializes calculateGreenPoints	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCalculateGreenPoints() {
		if (calculateGreenPoints == null) {
			calculateGreenPoints = new JButton();
			calculateGreenPoints.setBounds(new Rectangle(153, 41, 83, 18));
			calculateGreenPoints.setText("calculate");
			calculateGreenPoints.setFont(new Font("Dialog", Font.PLAIN, 10));
			calculateGreenPoints.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ImagePlus mask = IJ.getImage();
					model.calculateGreenPoints(mask);
				}
			});
		}
		return calculateGreenPoints;
	}

	/**
	 * This method initializes showHideGreenPointsButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getShowHideGreenPointsButton() {
		if (showHideGreenPointsButton == null) {
			showHideGreenPointsButton = new JButton();
			showHideGreenPointsButton.setBounds(new Rectangle(246, 41, 94, 18));
			showHideGreenPointsButton.setText("show / hide");
			showHideGreenPointsButton.setFont(new Font("Dialog", Font.PLAIN, 10));
			showHideGreenPointsButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							model.toggleShowGreenPoints();
						}
					});
		}
		return showHideGreenPointsButton;
	}

	/**
	 * This method initializes graphPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getGraphPanel() {
		if (graphPanel == null) {
			maxDistLabel = new JLabel();
			maxDistLabel.setBounds(new Rectangle(201, 23, 59, 16));
			maxDistLabel.setText("max dist:");
			minLabel = new JLabel();
			minLabel.setBounds(new Rectangle(19, 23, 50, 16));
			minLabel.setText("min int:");
			graphPanel = new JPanel();
			graphPanel.setLayout(null);
			graphPanel.setMaximumSize(new Dimension(32767, 100));
			graphPanel.setBorder(BorderFactory.createTitledBorder(null, "graph", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
			graphPanel.add(getCalculateGraphButton(), null);
			graphPanel.add(getShowGraphTextButton(), null);
			graphPanel.add(getShowGraphGraphicalButton(), null);
			graphPanel.add(minLabel, null);
			graphPanel.add(getThresholdSpinner(), null);
			graphPanel.add(maxDistLabel, null);
			graphPanel.add(getMaxDistanceSpinner(), null);
		}
		return graphPanel;
	}

	/**
	 * This method initializes calculateGraphButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCalculateGraphButton() {
		if (calculateGraphButton == null) {
			calculateGraphButton = new JButton();
			calculateGraphButton.setBounds(new Rectangle(15, 60, 94, 31));
			calculateGraphButton.setText("calculate");
			calculateGraphButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.calculateGraph();
				}
			});
		}
		return calculateGraphButton;
	}

	/**
	 * This method initializes showGraphTextButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getShowGraphTextButton() {
		if (showGraphTextButton == null) {
			showGraphTextButton = new JButton();
			showGraphTextButton.setBounds(new Rectangle(124, 60, 94, 31));
			showGraphTextButton.setText("show text");
		}
		return showGraphTextButton;
	}

	/**
	 * This method initializes showGraphGraphicalButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getShowGraphGraphicalButton() {
		if (showGraphGraphicalButton == null) {
			showGraphGraphicalButton = new JButton();
			showGraphGraphicalButton.setBounds(new Rectangle(233, 60, 101, 31));
			showGraphGraphicalButton.setText("show graph");
			showGraphGraphicalButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ImagePlus stack = model.newStack();
					model.drawGraph(stack);
					stack.show();
				}
			});
		}
		return showGraphGraphicalButton;
	}

	public void update(Observable sender, Object aspect) {
		if (aspect.equals("blueImage")) this.handleBlueImageChanged();
		if (aspect.equals("greenImage")) this.handleGreenImageChanged();
		if (aspect.equals("redImage")) this.handleRedImageChanged();
		if (aspect.equals("red points")) this.handleRedPointsChanged();
		if (aspect.equals("green points")) this.handleGreenPointsChanged();
	}

	private void handleGreenPointsChanged() {
		this.getGreenPointsTextField().setText(Integer.toString(model.getNumberOfGreenPoints()));
	}

	private void handleRedPointsChanged() {
		this.getRedPointsTextField().setText(Integer.toString(model.getNumberOfRedPoints()));
	}

	private void handleRedImageChanged() {
		String text = "";
		ImagePlus redImage = model.getRedImage();
		if (redImage!=null) text = redImage.getTitle();
		getRedTextField().setText(text);
	}

	private void handleGreenImageChanged() {
		String text = "";
		ImagePlus greenImage = model.getGreenImage();
		if (greenImage!=null) text = greenImage.getTitle();
		getGreenTextField().setText(text);
	}

	private void handleBlueImageChanged() {
		String text = "";
		ImagePlus blueImage = model.getBlueImage();
		if (blueImage!=null) text = blueImage.getTitle();
		getBlueTextField().setText(text);
	}

	public SpotClusterAnalyzer getModel() {
		return model;
	}

	private OpenImageOperation openImage() {
		OpenImageOperation open = new OpenImageOperation();
		open.setShowResult(false);
		open.run();
		return open;
	}

	/**
	 * This method initializes thresholdSpinner	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JSpinner getThresholdSpinner() {
		if (thresholdSpinner == null) {
			thresholdSpinner = new JSpinner(model.getThreshold());
			thresholdSpinner.setBounds(new Rectangle(75, 23, 75, 16));
		}
		return thresholdSpinner;
	}
	
	/**
	 * This method initializes thresholdSpinner	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JSpinner getMaxDistanceSpinner() {
		if (maxDistanceSpinner == null) {
			maxDistanceSpinner = new JSpinner(model.getMaxDistance());
			maxDistanceSpinner.setBounds(new Rectangle(270, 23, 75, 16));
		}
		return maxDistanceSpinner;
	}
	

}  //  @jve:decl-index=0:visual-constraint="10,10"
