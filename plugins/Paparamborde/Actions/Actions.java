package Paparamborde.Actions;
import Paparamborde.ATOM_DATA.ATOM_DATA;
import Paparamborde.SRIM.SRIM;
import Paparamborde.STIMspectra.STIMspectra;
import Paparamborde.Layer.Layer;
import Paparamborde.Sample.Sample;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import javax.swing.JTextArea;
import javax.swing.JFileChooser;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 *
 * @author Devès Guillaume, CENBG
 */

public class Actions{

public Actions(){
}

public void saveResults(javax.swing.JTextArea j){
	
}
/**
 * Selects files to be computed and implements arraylist
 * @param spectraArray
 * @param jTA
 * @param jFC 
 */
public void selectFiles(ArrayList <STIMspectra> spectraArray, JTextArea jTA, JFileChooser jFC){
    	//Action performed for 'select files' button 
        spectraArray.clear();
	jTA.setText("");
        File [] selectedFiles = jFC.getSelectedFiles(); 
        for (File f : selectedFiles){
            jFC.setCurrentDirectory(f);
            jTA.append(f.getAbsolutePath()+System.getProperty("line.separator" ));
            STIMspectra spectra=new STIMspectra(f.getAbsolutePath());
            spectraArray.add(spectra);
        }
        
}

/**
 * This function is used when update sample button is pressed
 * @param layer1 Layer type Top layer
 * @param layer2  Layer type Back layer
 * @param jTA_results Text Area where results are displayed
 * @param jTF_layer1 Text Field for top layer composition
 * @param jTF_layer2 Text Field for back layer composition
 * @param atm Atom_Data file required for srim
 * @param srim 
 * @param jTF_density1 Text field for top layer density 
 * @param jTF_density2 Text field for back layer density 
 * @param jRB_state solid or gaz state of sample
 * @param jTF_corr correction for stopping power calculation
 */
public void UpdateSample(Layer layer1, Layer layer2, JTextArea jTA_results, JTextField jTF_layer1, JTextField jTF_layer2, ATOM_DATA atm, SRIM srim1, JTextField jTF_density1, JTextField jTF_density2, JRadioButton jRB_state, JTextField jTF_corr){
    try{
		
		// Tab sample; Add button
		layer1.reset();
                layer2.reset();
		jTA_results.setText("");
				
		StringTokenizer st=new StringTokenizer(jTF_layer1.getText());
		while (st.hasMoreTokens()) {
			layer1.addElement(atm.getZ(st.nextToken()),Float.valueOf(st.nextToken()));
		}
                StringTokenizer st2=new StringTokenizer(jTF_layer2.getText());
                while (st2.hasMoreTokens()) {
			layer2.addElement(atm.getZ(st2.nextToken()),Float.valueOf(st2.nextToken()));
		}
		layer1.setDensity(Float.valueOf(jTF_density1.getText()));
                layer2.setDensity(Float.valueOf(jTF_density2.getText()));
		srim1.setSolidTarget((jRB_state.isSelected())?0:1);	
		srim1.setCorr(Float.valueOf(jTF_corr.getText()));	
		
		//Displays read composition in text field
		jTA_results.append("Layer 1 :"+"\n");
		for (int i=0;i<layer1.getNElement();i++){
			jTA_results.append(String.valueOf(layer1.getZ(i))+ " " + String.valueOf(layer1.getAtConc(i)/layer1.NormalizeAtConc())+"\n");
		}
		jTA_results.append("Density: " +String.valueOf(layer1.getDensity())+  "\n"+"\n");
		jTA_results.append("Layer 2 :"+"\n");
		for (int i=0;i<layer2.getNElement();i++){
			jTA_results.append(String.valueOf(layer2.getZ(i))+ " " + String.valueOf(layer2.getAtConc(i)/layer2.NormalizeAtConc())+"\n");
		}
		jTA_results.append("Density: " +String.valueOf(layer2.getDensity())+  "\n");
		Date d=new Date();
		jTA_results.append("Done: "+d.toString()+"\n");
		}
		catch (Exception e){
		jTA_results.append("Error: " +e.toString());
		}
    
}
public void UpdateSample(Layer layer, JTextArea jTA_results, JTextField jTF_sample,  ATOM_DATA atm, SRIM srim, JTextField jTF_density, JRadioButton jRB_state, JTextField jTF_corr){
    try{
		
		// Tab sample; Add button
		layer.reset();
                				
		StringTokenizer st=new StringTokenizer(jTF_sample.getText());
		while (st.hasMoreTokens()) {
			layer.addElement(atm.getZ(st.nextToken()),Float.valueOf(st.nextToken()));
		}
                
		layer.setDensity(Float.valueOf(jTF_density.getText()));
                srim.setSolidTarget((jRB_state.isSelected())?0:1);	
		srim.setCorr(Float.valueOf(jTF_corr.getText()));	
		
		//Displays read composition in text field
		jTA_results.append("Layer :"+"\n");
		for (int i=0;i<layer.getNElement();i++){
			jTA_results.append(String.valueOf(layer.getZ(i))+ " " + String.valueOf(layer.getAtConc(i)/layer.NormalizeAtConc())+"\n");
		}
		jTA_results.append("Density: " +String.valueOf(layer.getDensity())+  "\n"+"\n");
		
		Date d=new Date();
		jTA_results.append("Done: "+d.toString()+"\n"+"\n");
		}
		catch (Exception e){
		jTA_results.append("Error: " +e.toString());
		}
    
}
public void Calibrate(JTextField jTF_Z, JTextField jTF_E0, JTextField jTF_a,JTextField jTF_b,JTextField jTF_c, ArrayList <STIMspectra> spectraArray, JTextArea jTA_results){
    //Action performed for 'calibrate' button 
    jTA_results.setText("");
    int Z=Integer.valueOf(jTF_Z.getText());
    float E0, a, b, c;
    E0=Float.valueOf(jTF_E0.getText());
    a=Float.valueOf(jTF_a.getText());
    b=Float.valueOf(jTF_b.getText());
    c=Float.valueOf(jTF_c.getText());
				
    for (int i=0;i<spectraArray.size();i++){
    	(spectraArray.get(i)).calibrate(Z,E0,a,b,c);
    }
		
    jTA_results.append("number of files= "+ String.valueOf(spectraArray.size())+"\n");
    if (spectraArray.size()>0){
        jTA_results.append("E0="+String.valueOf((spectraArray.get(0)).getE0())+"\n");
	jTA_results.append("Z="+String.valueOf((spectraArray.get(0)).getIon())+"\n");
	jTA_results.append("a="+String.valueOf((spectraArray.get(0)).getA())+"\n");
	jTA_results.append("b="+String.valueOf((spectraArray.get(0)).getB())+"\n");
	jTA_results.append("c="+String.valueOf((spectraArray.get(0)).getC())+"\n");
	}
    Date d=new Date();
    jTA_results.append("Done: "+d.toString()+"\n");
}
/*public void WriteSRIN(JTextArea jTA_IN, SRIM srim, ArrayList <STIMspectra> spectraArray, Layer smp, JTextField jTF_min, JTextField jTF_max){
    jTA_IN.setText("");
    Date d=new Date();
		
    try {
	srim.writeSRIN(spectraArray.get(0),smp,Integer.valueOf(jTF_min.getText()),Integer.valueOf(jTF_max.getText()));
	
        srim.runSRModule();
        Thread.sleep(200);
        //Implements spectra
        
        for (int i=0;i<spectraArray.size();i++){
		srim.readSROUT(spectraArray.get(i),spectraArray.get(i).get1stChannel());
	}
        
	//Display result in window
        InputStream ips=new FileInputStream(srim.getSRINpath()); 
	InputStreamReader ipsr=new InputStreamReader(ips);
	BufferedReader br=new BufferedReader(ipsr);
	String ligne=br.readLine();
	while (ligne!= null){
		jTA_IN.append(ligne+"\n");
		ligne=br.readLine();
	}
	jTA_IN.append("Done: " +d.toString());
	}
	catch (Exception e){
		jTA_IN.append(d.toString() +"Error: " +e.toString()+"\n"+d.toString()+"\n");
	}
}*/
public void WriteSRIN(JTextArea jTA_IN, SRIM srim, ArrayList <STIMspectra> spectraArray, Sample sample,int nlayer, JTextField jTF_min, JTextField jTF_max){
    //jTA_IN.setText("");
    Date d=new Date();
		
    try {
	srim.writeSRIN(spectraArray.get(0),sample.getLayer(nlayer),Integer.valueOf(jTF_min.getText()),Integer.valueOf(jTF_max.getText()));
	
        srim.runSRModule();
        Thread.sleep(200);
        //Implements spectra
        srim.readSROUT(spectraArray.get(0), spectraArray.get(0).get1stChannel());
        spectraArray.get(0).measure();
        sample.setLayerMPC(nlayer,(ArrayList<Float>)(spectraArray.get(0).getMassArray()).clone());
        
        
	//Display result in window
        InputStream ips=new FileInputStream(srim.getSRINpath()); 
	InputStreamReader ipsr=new InputStreamReader(ips);
	BufferedReader br=new BufferedReader(ipsr);
	String ligne=br.readLine();
	while (ligne!= null){
		jTA_IN.append(ligne+"\n");
		ligne=br.readLine();
	}
	jTA_IN.append("Done: " +d.toString());
	}
	catch (Exception e){
		jTA_IN.append(d.toString() +"Error: " +e.toString()+"\n"+d.toString()+"\n");
	}
}
public void Calculate(ArrayList <STIMspectra> spectraArray,JTextArea jTA_results, JTextField jTF_min,JTextField jTF_max, JTextField jTF_density, int nLayer){
    Date d=new Date();
    Sample s=new Sample(2);
    
    //try{
	for (int i=0;i<spectraArray.size();i++){
            
            spectraArray.get(i).measure(Integer.valueOf(jTF_min.getText()),Integer.valueOf(jTF_max.getText()));
            
            s.setCounts(spectraArray.get(i));
            jTA_results.append("Spectrum: " + spectraArray.get(i).getPath()+" Mass (µg/cm²): " + spectraArray.get(i).massCalc()+"\n");
            jTA_results.append("Thichness (nm): " + 10*spectraArray.get(i).massCalc()/Float.valueOf(jTF_density.getText()) +"\n");
            jTA_results.append("Mass: " + s.getMass(nLayer-1) +"\n");
	}
	jTA_results.append("Done: "+d.toString()+"\n");
	/*}
	catch (Exception e){
		jTA_results.append(d.toString()+ " Error: " +e.toString()+"\n");
	}*/
}
public void Calculate(Sample sample,ArrayList <STIMspectra> spectraArray,JTextArea jTA_results, JTextField jTF_min,JTextField jTF_max, JTextField jTF_density, int nLayer, JTextField jTF_limit){
    Date d=new Date();
    
    
    //try{
	for (int i=0;i<spectraArray.size();i++){
            sample.setCounts(spectraArray.get(i));
            float [] results = new float [2];
            results=sample.getMass(2, Float.valueOf(jTF_limit.getText()));           
            jTA_results.append("Spectrum: " + spectraArray.get(i).getPath()+" Layer 1 (µg/cm²): " + String.valueOf(results[1]) +"\n");
            jTA_results.append("Spectrum: " + spectraArray.get(i).getPath()+" Layer 2 (µg/cm²): " + String.valueOf(results[2]) +"\n");
            jTA_results.append("Spectrum: " + spectraArray.get(i).getPath()+" Not corrected (µg/cm²): " + String.valueOf(spectraArray.get(0).massCalc()) +"\n");
            jTA_results.append("Interface (keV): [" +  String.valueOf(results[0]) +"] " +String.valueOf(spectraArray.get(0).getE0()-results[0]*spectraArray.get(0).getB())+"\n");
            
	}
	jTA_results.append("Done: "+d.toString()+"\n");
	/*}
	catch (Exception e){
		jTA_results.append(d.toString()+ " Error: " +e.toString()+"\n");
	}*/
}
public void Calculate(ArrayList <STIMspectra> spectraArray,JTextArea jTA_results){
    Date d=new Date();
    try{
	for (int i=0;i<spectraArray.size();i++){
            spectraArray.get(i).measure();
            jTA_results.append("Spectrum: " + spectraArray.get(i).getPath()+" Mass (µg/cm²): " + spectraArray.get(i).massCalc()+"\n");
	}
	jTA_results.append("Done: "+d.toString()+"\n");
	}
	catch (Exception e){
		jTA_results.append(d.toString()+ " Error: " +e.toString()+"\n");
	}
}
public void ReadSROut(JTextArea jTA_OUT, SRIM srim){
    //jTA_OUT.setText("");
    jTA_OUT.setText(srim.getSRout());
}
public void runSRModule(SRIM srim){
    srim.runSRModule();
}
}