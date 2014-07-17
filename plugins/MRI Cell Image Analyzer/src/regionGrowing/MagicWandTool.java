package regionGrowing;

import ij.IJ;
import ij.ImagePlus;
import ij.Macro;
import ij.gui.Roi;
import ij.gui.ShapeRoi;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class MagicWandTool {

	static public float threshold = 50;
	static public boolean isRunning = false;
	static public String NORMAL = "flags=16";
	static public String SHIFT = "flags=17";
	static public String ALT = "flags=24";
	JFrame selectThreshold;
	private JTextField fieldThreshold;
	private Roi lastRoi;

	public void run(String arg) {
		String options = Macro.getOptions();
		if (options.equals("options ")){
			this.openThresholdDialog("Select threshold");
			return;
		}
		synchronized(this){
			if (isRunning) return;
			isRunning = true;
		int x = getXOption(options);
		int y = getYOption(options);
		ImagePlus image = IJ.getImage();
		if (image==null) return;
		try {
			lastRoi = image.getRoi();
			image.killRoi();
			if (options.contains(ALT) && lastRoi==null){
				isRunning = false;
				return;
			}
			
			MagicWand magicWand = MagicWand.newFor(image);
			magicWand.setThreshold(threshold);
			Roi newRoi = magicWand.createRoi(x, y);
			
			if(options.contains(SHIFT)){
				ShapeRoi lastShape = new ShapeRoi(lastRoi);
				ShapeRoi newShape = new ShapeRoi(newRoi);
				newRoi = newShape.or(lastShape);
			}
			else if(options.contains(ALT)){
				ShapeRoi lastShape = new ShapeRoi(lastRoi);
				ShapeRoi newShape = new ShapeRoi(newRoi);
				newRoi = newShape.not(lastShape);
			}
			
			image.setRoi(newRoi);
			
		} catch (ImageTypeDoesntExist e) {
			e.printStackTrace();
		}
		isRunning = false;
		}
	}
	
	protected int getYOption(String options) {
		String[] theOptions = options.split(" ");
		String yOption = theOptions[1];
		int result = Integer.parseInt(yOption.split("=")[1]);
		return result;
	}

	protected int getXOption(String options) {
		String[] theOptions = options.split(" ");
		String xOption = theOptions[0];
		int result = Integer.parseInt(xOption.split("=")[1]);
		return result;
	}
	
	protected void openThresholdDialog(String title)
	{
		selectThreshold = new JFrame();
		selectThreshold.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/icon.gif")));
		selectThreshold.setTitle(title);
		fieldThreshold = new JTextField("50");
		JButton accept = new JButton("Ok");
		selectThreshold.setLayout(null);
		selectThreshold.setSize(200, 60);
		selectThreshold.add(fieldThreshold);
		fieldThreshold.setBounds(10, 4, 120, 20);
		selectThreshold.add(accept);
		accept.setBounds(140, 4, 50, 20);
		
		accept.addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				threshold = Float.parseFloat(fieldThreshold.getText());
			}
		});
		
		selectThreshold.setVisible(true);
	}
	
}
