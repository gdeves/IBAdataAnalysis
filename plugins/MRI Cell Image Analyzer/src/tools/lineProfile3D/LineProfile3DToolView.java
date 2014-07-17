package tools.lineProfile3D;

import ij.IJ;
import ij.ImagePlus;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JSpinner;

import java.awt.Toolkit;
import javax.swing.JButton;
import java.awt.Rectangle;
import javax.swing.JLabel;
import java.awt.Dimension;

public class LineProfile3DToolView extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JButton createButton = null;
	
	private JSpinner startSliceSpinner;
	
	private JSpinner endSliceSpinner;
	
	protected LineProfile3DTool model;

	private JLabel startSliceLabel = null;

	private JLabel endSliceLabel = null;

	private JButton setStartSliceButton = null;

	private JButton setEndSliceButton = null;

	/**
	 * This is the default constructor
	 */
	public LineProfile3DToolView() {
		super();
		model = new LineProfile3DTool();
		model.setView(this);
		initialize();
	}

	public LineProfile3DToolView(LineProfile3DTool tool) {
		super();
		model = tool;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(238, 154);
		this.setMaximumSize(new Dimension(238, 154));
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/icon.gif")));
		this.setContentPane(getJContentPane());
		this.setTitle("MRI 3D Line Profiler");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			endSliceLabel = new JLabel();
			endSliceLabel.setBounds(new Rectangle(15, 44, 60, 16));
			endSliceLabel.setText("end slice:");
			startSliceLabel = new JLabel();
			startSliceLabel.setBounds(new Rectangle(9, 20, 66, 16));
			startSliceLabel.setText("start slice:");
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getCreateButton(), null);
			jContentPane.add(startSliceLabel, null);
			jContentPane.add(endSliceLabel, null);
			jContentPane.add(getStartSliceSpinner(), null);
			jContentPane.add(getEndSliceSpinner(), null);
			jContentPane.add(getSetStartSliceButton(), null);
			jContentPane.add(getSetEndSliceButton(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes createButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCreateButton() {
		if (createButton == null) {
			createButton = new JButton();
			createButton.setBounds(new Rectangle(69, 72, 91, 31));
			createButton.setText("create");
			createButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.createProfile();
				}
			});
		}
		return createButton;
	}

	private JSpinner getStartSliceSpinner() {
		if (startSliceSpinner == null) {
			startSliceSpinner = new JSpinner(model.getZ1());
			startSliceSpinner.setPreferredSize(new java.awt.Dimension(102,22));
			startSliceSpinner.setBounds(new Rectangle(80, 20, 66, 16));
			startSliceSpinner.setName("startSliceSpinner");
		}
		return startSliceSpinner;
	}
	
	private JSpinner getEndSliceSpinner() {
		if (endSliceSpinner == null) {
			endSliceSpinner = new JSpinner(model.getZ2());
			endSliceSpinner.setPreferredSize(new java.awt.Dimension(102,22));
			endSliceSpinner.setBounds(new Rectangle(80, 44, 66, 16));
			endSliceSpinner.setName("endSliceSpinner");
		}
		return endSliceSpinner;
	}

	/**
	 * This method initializes setStartSliceButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSetStartSliceButton() {
		if (setStartSliceButton == null) {
			setStartSliceButton = new JButton();
			setStartSliceButton.setBounds(new Rectangle(165, 20, 60, 16));
			setStartSliceButton.setText("set");
			setStartSliceButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ImagePlus image = IJ.getImage();
					if (image==null) return;
					model.setImage(image);
					model.setZ1(image.getCurrentSlice());
				}
			});
		}
		return setStartSliceButton;
	}

	/**
	 * This method initializes setEndSliceButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSetEndSliceButton() {
		if (setEndSliceButton == null) {
			setEndSliceButton = new JButton();
			setEndSliceButton.setBounds(new Rectangle(165, 44, 60, 16));
			setEndSliceButton.setText("set");
			setEndSliceButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ImagePlus image = IJ.getImage();
					if (image==null) return;
					model.setImage(image);
					model.setZ2(image.getCurrentSlice());
				}
			});
		}
		return setEndSliceButton;
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
