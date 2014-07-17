
import Paparamborde.PFrame2.PFrame2;
import ij.plugin.*;
import java.lang.Object.*;
import java.lang.System.*;
import java.lang.String.*;
import javax.swing.SwingUtilities;






public class Paparamborde_ implements PlugIn {
   
    public void run(String arg) {
          
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				
				PFrame2 pframe = new PFrame2();
				pframe.setVisible(true);
			}
		});
		//Create and set up the window.
       
		
		
		/*try {
		OpenDialog od=new OpenDialog("Select STIM spectra file", null);
		
		STIMspectra s=new STIMspectra(od.getDirectory()+od.getFileName());
			
		
		
		Sample s1=new Sample();
		s1.AddElement(6,16);
		s1.AddElement(1,14);
		s1.AddElement(8,3);
		s1.AddElement(7,2);
		s1.SetDensity(1.2);
		  
		  
		s.Calibrate(1,1510.0,0.0,2.422,-236.95);
		  
		SRIM sr1=new SRIM(s,s1);
		s.Measure(s1);
		}
		catch (Exception e) {
		}
		*/
	}
    }
   
    