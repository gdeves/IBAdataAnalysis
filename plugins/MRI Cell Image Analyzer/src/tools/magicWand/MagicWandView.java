package tools.magicWand;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import java.awt.Toolkit;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JProgressBar;

public class MagicWandView extends JFrame implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6631469439486899042L;
	private JPanel jContentPane = null;
	private JPanel optionsPanel = null;
	private JLabel onLabel = null;
	private JLabel imageNameLabel = null;
	private JLabel methodLabel = null;
	private JComboBox methodComboBox = null;
	private JCheckBox activeCheckBox = null;
	protected MagicWand model;
	private JButton optionsButton = null;
	private JButton closeSelectionButton = null;
	private JButton splitButton = null;
	private JProgressBar jProgressBar = null;

	public void update(Observable sender, Object aspect) {
		if (aspect.equals("image")) {
			this.handleImageChanged();
		}
		if (aspect.equals("progress")) {
			this.handleProgressChanged();
		}
		if (aspect.equals("stop progress")) {
			this.handleStopProgressChanged();
		}
	}

	protected void handleStopProgressChanged() {
		this.getJProgressBar().setIndeterminate(false);
	}

	protected void handleProgressChanged() {
		this.getJProgressBar().setIndeterminate(true);
	}

	protected void handleImageChanged() {
		this.imageNameLabel.setText(model.getImage().getTitle());
	}

	/**
	 * This is the default constructor
	 */
	public MagicWandView() {
		super();
		initialize();
	}
	
	/**
	 * This is the default constructor
	 */
	public MagicWandView(MagicWand model) {
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
		this.setSize(363, 153);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/icon.gif")));
		this.setContentPane(getJContentPane());
		this.setTitle("MRI Magic Wand");
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
			jContentPane.add(getOptionsPanel(), java.awt.BorderLayout.NORTH);
			jContentPane.add(getJProgressBar(), java.awt.BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	/**
	 * This method initializes optionsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getOptionsPanel() {
		if (optionsPanel == null) {
			methodLabel = new JLabel();
			methodLabel.setBounds(new java.awt.Rectangle(16,34,51,16));
			methodLabel.setText("method:");
			imageNameLabel = new JLabel();
			imageNameLabel.setBounds(new java.awt.Rectangle(68,8,218,16));
			imageNameLabel.setText(model.getImageName());
			onLabel = new JLabel();
			onLabel.setBounds(new java.awt.Rectangle(16,8,38,16));
			onLabel.setText("on: ");
			optionsPanel = new JPanel();
			optionsPanel.setLayout(null);
			optionsPanel.setPreferredSize(new java.awt.Dimension(1,100));
			optionsPanel.add(onLabel, null);
			optionsPanel.add(imageNameLabel, null);
			optionsPanel.add(methodLabel, null);
			optionsPanel.add(getMethodComboBox(), null);
			optionsPanel.add(getActiveCheckBox(), null);
			optionsPanel.add(getOptionsButton(), null);
			optionsPanel.add(getCloseSelectionButton(), null);
			optionsPanel.add(getSplitButton(), null);
		}
		return optionsPanel;
	}

	/**
	 * This method initializes methodComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getMethodComboBox() {
		if (methodComboBox == null) {
			methodComboBox = new JComboBox(model.getBackstoreCreationMethodNames());
			methodComboBox.setBounds(new java.awt.Rectangle(68,34,186,26));
			methodComboBox.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					int index = methodComboBox.getSelectedIndex();
					MagicWandBackstoreImageCreator method = (MagicWandBackstoreImageCreator) model.getBackstoreCreationMethods().elementAt(index);
					model.setBackstoreImageCreator(method);
				}
			});
		}
		return methodComboBox;
	}

	/**
	 * This method initializes activeCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getActiveCheckBox() {
		if (activeCheckBox == null) {
			activeCheckBox = new JCheckBox();
			activeCheckBox.setBounds(new java.awt.Rectangle(16,69,76,21));
			activeCheckBox.setText("active");
			activeCheckBox.setSelected(model.isActive());
			activeCheckBox.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.setActive(!model.isActive());
				}
			});
		}
		return activeCheckBox;
	}

	/**
	 * This method initializes optionsButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getOptionsButton() {
		if (optionsButton == null) {
			optionsButton = new JButton();
			optionsButton.setBounds(new java.awt.Rectangle(261,34,86,26));
			optionsButton.setText("options");
			optionsButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					MagicWandBackstoreImageCreator method = model.getBackstoreImageCreator();
					if (method==null) return;
					method.showOptions();
					model.setBackstore(null);
				}
			});
		}
		return optionsButton;
	}

	/**
	 * This method initializes closeSelectionButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCloseSelectionButton() {
		if (closeSelectionButton == null) {
			closeSelectionButton = new JButton();
			closeSelectionButton.setBounds(new java.awt.Rectangle(216,69,131,21));
			closeSelectionButton.setText("close selection");
			closeSelectionButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.closeSelection();
				}
			});
		}
		return closeSelectionButton;
	}

	/**
	 * This method initializes splitButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSplitButton() {
		if (splitButton == null) {
			splitButton = new JButton();
			splitButton.setBounds(new java.awt.Rectangle(112,69,78,21));
			splitButton.setText("split");
			splitButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.split();
				}
			});
		}
		return splitButton;
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

}  //  @jve:decl-index=0:visual-constraint="10,10"
