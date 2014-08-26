import IBA_J.MainFrame.MainFrame;
import ij.IJ;
import ij.plugin.*;
import javax.swing.SwingUtilities;

/**
 * IBA_J is the first class run by ImageJ when the user launch the IBA_J plugin.
 */
public class IBA_J implements PlugIn {
  
  static MainFrame IBAFrame = new MainFrame();
  /**
  * function run by ImageJ when the user launch the IBA_J plugin.
  * This function will open a new window using the MainFrame class
     * @param arg
  */
  @Override
  public void run(String arg) {	
    SwingUtilities.invokeLater(new Runnable(){
        @Override
        public void run(){
            IJ.versionLessThan("1.45n");
            IBAFrame.setVisible(true);	
        }
    });
  }

}