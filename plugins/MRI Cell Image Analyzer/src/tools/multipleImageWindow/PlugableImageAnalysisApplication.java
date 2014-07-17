package tools.multipleImageWindow;
import ij.ImagePlus;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;


public interface PlugableImageAnalysisApplication {
	JFrame getOptions();
	JComponent getComponent();
	JMenu getMenus();
	JButton[] getTaskbarButtons();
	JFrame getWizard();
	void setRGBImage(ImagePlus inputImage);
	void setRedImage(ImagePlus redInputImage);
	void setGreenImage(ImagePlus greenImage);
	void setBlueImage(ImagePlus blueImage);
}
