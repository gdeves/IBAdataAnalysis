import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.*;
import ij.plugin.frame.*;
import javax.swing.SwingUtilities;

public class stitch_PIXE implements PlugIn {
        @Override
	public void run(String arg) {
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run(){
		int X0=75;
                int Y0=0;
		int trans_X1=15;
                int trans_Y1=236;
                int trans_X2=-35;
                int trans_Y2=472;
                String command="";
                
		ImagePlus imp = IJ.getImage();
		IJ.run("Hyperstack...", "title=Fused type=32-bit display=Grayscale width=500 height=800 channels=1 slices=1 frames=10 label");
                command="source=PIXE_03.tif destination=Fused x="+Integer.toString(X0) + " y="+Integer.toString(Y0);
		IJ.run("Insert...", command);
                command="source=PIXE_02.tif destination=Fused x="+Integer.toString(X0+trans_X1)+" y=" + Integer.toString(Y0+trans_Y1);
		IJ.run("Insert...", command);
                command="source=PIXE_02.tif destination=Fused x="+Integer.toString(X0+trans_X2)+" y=" + Integer.toString(Y0+trans_Y2);
		IJ.run("Insert...", command);
	}
            });
}
}
