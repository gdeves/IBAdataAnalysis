package Paparamborde.SRIM;

import ij.*;
import Paparamborde.ATOM_DATA.*;
import Paparamborde.STIMspectra.STIMspectra;
import Paparamborde.Layer.Layer;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.io.*;

import java.lang.ProcessBuilder.*;
import java.lang.Process.*;



public class SRIM{
	
	/* Declarations */ 
	static int iZ;				//Z of incoming ion
	static float iMass;		//Mass of incoming particule
	int solidTarget;	//target type: 0/solid; 1/Gas
	float density;		//Density of target
	float corr;			//Compound correction
	int stop_unit;		//stopping units for calculation
	
	static String path_SRIN, path_SROUT, path_SRModule;
	static String IJdirectory, IJplugins, IJapp;
	static ArrayList <String> data_SROUT;
	
	ATOM_DATA a=new ATOM_DATA();

	
public SRIM(){
	data_SROUT=new ArrayList <String>();
	//constructor
	setPaths();
	solidTarget=(int)0;	
	setCorr((float)1);			
	setDensity((float)1);		
	setStoppingUnits(4); 
}
public SRIM(STIMspectra spectra, Layer sample){
	data_SROUT=new ArrayList <String>();
	//constructor
	setPaths();
	solidTarget=(int)0;	
	setCorr((float)1);			
	setDensity((float)1);		
	setStoppingUnits(4); 
	runSRIM(spectra, sample);
}
public String getSRout(){
    String srout="";
    for (int i=0;i<data_SROUT.size();i++){
        srout+=data_SROUT.get(i)+System.getProperty("line.separator");
    }
    return srout;
}
/*setter */
public static void setPaths(){
	IJdirectory=System.getProperty("java.class.path").substring(0,System.getProperty("java.class.path").lastIndexOf(System.getProperty("file.separator")));
	IJplugins=IJdirectory+System.getProperty("file.separator")+"plugins";
	IJapp=IJplugins+System.getProperty("file.separator")+"Paparamborde"+System.getProperty("file.separator");
	path_SRIN=IJapp+"SR.IN";
	path_SROUT=IJapp+"SR.OUT";
	path_SRModule=IJapp+"SRModule.exe";
}
public void setSolidTarget(int solidTarget){
	this.solidTarget=solidTarget;
}
public void setCorr(float corr){
	this.corr=corr;
}
private void setStoppingUnits(int stop_unit){
	this.stop_unit=stop_unit;
}
public void setDensity(float density){
	this.density=density;
}
public void writeSRIN(STIMspectra s, Layer layer, int channel_min, int channel_max) throws IOException{
	File toFile= new File(path_SRIN);
	BufferedWriter writer = new BufferedWriter(new FileWriter(toFile));

	channel_max=Math.min(channel_max,s.getLastChannel());
	channel_min=Math.max(channel_min, s.get1stChannel());
		
	writer.write("---Stopping/Range Input Data (Number-format: Period = Decimal Point)"+ System.getProperty("line.separator"));
	writer.write("---Output File Name"+ System.getProperty("line.separator"));
	writer.write("\"" + path_SROUT + "\"" +System.getProperty("line.separator"));
	writer.write("---Ion(Z), Ion Mass(u)"+ System.getProperty("line.separator"));
	writer.write(s.getIon() + " " + a.getNatWeight(s.getIon()) + System.getProperty("line.separator"));
	writer.write("---Target Data: (Solid=0,Gas=1), Density(g/cm3), Compound Corr."+System.getProperty("line.separator"));
	writer.write(solidTarget + " " + layer.getDensity() + " " + corr + System.getProperty("line.separator"));
	writer.write("---Number of Target Elements"+ System.getProperty("line.separator"));
	writer.write(layer.getNElement() + System.getProperty("line.separator"));
	writer.write("---Target Elements: (Z), Target name, Stoich, Target Mass(u)" + System.getProperty("line.separator"));
	for (int i=0;i<layer.getNElement();i++){
		writer.write(layer.getZ(i) + " \"" + a.getElName(layer.getZ(i)) + "\" " + layer.getAtConc(i)/layer.NormalizeAtConc() + " " + a.getNatWeight(layer.getZ(i)) + System.getProperty("line.separator"));
	}
	writer.write("---Output Stopping Units (1-8)" + System.getProperty("line.separator"));
	writer.write(stop_unit + System.getProperty("line.separator"));
	writer.write("---Ion Energy : E-Min(keV), E-Max(keV)" + System.getProperty("line.separator"));
	writer.write("0 0" + System.getProperty("line.separator"));
	
	for (int i=channel_min;i<=channel_max;i++){
		writer.write(s.getEnergy(i) + System.getProperty("line.separator"));
	}
	writer.write("0" + System.getProperty("line.separator"));
	writer.close();
        


}
public void writeSRIN(STIMspectra s, Layer e) throws IOException{
	File toFile= new File(path_SRIN);
	BufferedWriter writer = new BufferedWriter(new FileWriter(toFile));
	int channel_min=0;
	int channel_max=s.getCounts(0);
	
	channel_max=Math.min(channel_max,s.getLastChannel());
	channel_min=Math.max(channel_min, s.get1stChannel());
		
	writer.write("---Stopping/Range Input Data (Number-format: Period = Decimal Point)"+ System.getProperty("line.separator"));
	writer.write("---Output File Name"+ System.getProperty("line.separator"));
	writer.write("\"" + path_SROUT + "\"" +System.getProperty("line.separator"));
	writer.write("---Ion(Z), Ion Mass(u)"+ System.getProperty("line.separator"));
	writer.write(s.getIon() + " " + a.getNatWeight(s.getIon()) + System.getProperty("line.separator"));
	writer.write("---Target Data: (Solid=0,Gas=1), Density(g/cm3), Compound Corr."+System.getProperty("line.separator"));
	writer.write(solidTarget + " " + e.getDensity() + " " + corr + System.getProperty("line.separator"));
	writer.write("---Number of Target Elements"+ System.getProperty("line.separator"));
	writer.write(e.getNElement() + System.getProperty("line.separator"));
	writer.write("---Target Elements: (Z), Target name, Stoich, Target Mass(u)" + System.getProperty("line.separator"));
	for (int i=0;i<e.getNElement();i++){
		writer.write(e.getZ(i) + " \"" + a.getElName(e.getZ(i)) + "\" " + e.getAtConc(i) + " " + a.getNatWeight(e.getZ(i)) + System.getProperty("line.separator"));
	}
	writer.write("---Output Stopping Units (1-8)" + System.getProperty("line.separator"));
	writer.write(stop_unit + System.getProperty("line.separator"));
	writer.write("---Ion Energy : E-Min(keV), E-Max(keV)" + System.getProperty("line.separator"));
	writer.write("0 0" + System.getProperty("line.separator"));
	
	for (int i=channel_min;i<=channel_max;i++){
		writer.write(s.getEnergy(i) + System.getProperty("line.separator"));
	}
	writer.write("0" + System.getProperty("line.separator"));
	writer.close();
        

}
public void runSRModule(){
	//path SRModule
	setPaths();
	
	try{
	ProcessBuilder pb= new ProcessBuilder();
	pb.directory(new File(IJapp));
	pb.command(path_SRModule);
	pb.start();
        }
	catch (Exception e){
            IJ.error(e.toString());
        }
        
}
public void readSROUT(STIMspectra s, int channel_min){
	data_SROUT.clear();
        
        try{
			InputStream ips=new FileInputStream(path_SROUT); 
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			String ligne;
                        s.getDeDxEArray().clear();
                        s.getDeDxNArray().clear();
			for (int i=0;i<channel_min;i++){
			s.addDeDxE(0);
			s.addDeDxN(0);
			}
			//Discard header lines
			for (int i=0;i<3;i++){
				ligne=br.readLine();			
			}
			//Converts each line into element information as described above
				ligne=br.readLine();
                                data_SROUT.add(ligne);
				while ((ligne=br.readLine())!= null){
                                    data_SROUT.add(ligne);
                                    StringTokenizer st=new StringTokenizer(ligne);
                                    st.nextToken();
                                    s.addDeDxE(Float.parseFloat(st.nextToken()));
                                    s.addDeDxN(Float.parseFloat(st.nextToken()));
				}
			
			br.close(); 
			
			}
	catch (Exception e){
			IJ.error(e.toString());
		}
}
public void readSROUT(STIMspectra s, int channel_min, Layer layer){
	data_SROUT.clear();
        
        try{
			InputStream ips=new FileInputStream(path_SROUT); 
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			String ligne;
                        s.getDeDxEArray().clear();
                        s.getDeDxNArray().clear();
			for (int i=0;i<channel_min;i++){
			s.addDeDxE(0);
			s.addDeDxN(0);
			}
			//Discard header lines
			for (int i=0;i<3;i++){
				ligne=br.readLine();			
			}
			//Converts each line into element information as described above
				ligne=br.readLine();
                                data_SROUT.add(ligne);
				while ((ligne=br.readLine())!= null){
                                    data_SROUT.add(ligne);
                                    StringTokenizer st=new StringTokenizer(ligne);
                                    st.nextToken();
                                    s.addDeDxE(Float.parseFloat(st.nextToken()));
                                    s.addDeDxN(Float.parseFloat(st.nextToken()));
				}
			
			br.close(); 
			//layer.setMPCArray(s.getDeDxArray());
			}
                        
	catch (Exception e){
			IJ.error(e.toString());
		}
}
public String getSRINpath(){
	return path_SRIN;		
}
private void runSRIM(STIMspectra spectra, Layer sample){
	try{
		writeSRIN(spectra, sample);
		}
		catch (IOException e){
		}
		runSRModule();
		
		readSROUT(spectra,spectra.get1stChannel());
}
void runSRIM(STIMspectra spectra, Layer sample, int channel_min, int channel_max){
	try{
		writeSRIN(spectra, sample, channel_min, channel_max);
		}
		catch (IOException e){
		}
		runSRModule();
		
		readSROUT(spectra,spectra.get1stChannel());
}
}

