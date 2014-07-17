package Paparamborde.STIMspectra;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;
import ij.text.TextWindow.*;
import ij.gui.Plot;


public class STIMspectra{

//declaration
private ArrayList <Integer> counts=new ArrayList <Integer>();
private ArrayList <Float> energy=new ArrayList <Float>();
private ArrayList <Float> dedxe=new ArrayList <Float>();
private ArrayList <Float> dedxn=new ArrayList <Float>();
private ArrayList <Float> mass=new ArrayList <Float>();
private ArrayList <Float> weight=new ArrayList <Float>();
private float [] point=new float[4];
private float [] parameter=new float[5];//Z ion, E0, a, b, c (E=a*x2+b*x+c)
private String path;


public STIMspectra(){
}
public STIMspectra (String path){
	readSpectra(path);
	this.path=path;        
}

//getter
public float getEnergy(int channel){
	return energy.get(channel);
}
public float energy(int channel){
	return getEnergy(channel);
}
public int getCounts(int channel){
	return counts.get(channel);
}
public ArrayList<Integer> getCountsArray(){
    return counts;
}
public float getDeDxE(int channel){
	return dedxe.get(channel);
}
public float getDeDxN(int channel){
	return dedxn.get(channel);
}
public float getMass(int channel){
	return mass.get(channel);
}
public ArrayList <Float> getMassArray(){
    return mass;
}
public ArrayList <Float> getWeightedMass(){
    ArrayList <Float> t=(ArrayList<Float>)mass.clone();
    for (int i=0;i<mass.size();i++) {
        float wm=mass.get(i)*weight.get(i);
        t.set(i, wm);
    }
    return t;
}
public float getMinMass(){
	return getMass(mass.lastIndexOf((float)0)+1);
}
public float [] getPoint(int channel){
	point[0]=(float)getCounts(channel);
	point[1]=getEnergy(channel);
	point[2]=getDeDxE(channel);
	point[3]=getDeDxN(channel);
	point[4]=getMass(channel);
	return point;
}
public int getIon() {
	return (int)parameter[0];
}
public float getE0() {
	return parameter[1];
}
public float getA() {
	return parameter[2];
}
public float getB() {
	return parameter[3];
}
public float getC(){
	return parameter[4];
}
public int getChannel(float E) {
	return Math.round((E-getC())/getB());
}
public int get1stChannel(){
	int i=1;
	while (getEnergy(i)==0){
	i++;	
	}
	return i;
}
public int getLastChannel(){
	int i=1;
	while (getEnergy(i)<getE0()){
	i++;	
	}
	return i-1;
}
public String getPath(){
	return path;
}
public ArrayList <Float> getDeDxEArray(){
    return dedxe;
}
public ArrayList <Float> getDeDxNArray(){
    return dedxn;
}
public ArrayList <Float> getDeDxArray(){
    ArrayList <Float> dedx=new ArrayList <Float>();
    float t;
    for (int i=0;i<dedxe.size();i++){
        t=getDeDxE(i)+getDeDxN(i);
        dedx.add(t);
    }
    return dedx;
}

// setter
void setCounts(int channel, int counts){
	this.counts.add(channel,counts);
	}
void setEnergy(int channel, float energy){
	this.energy.add(channel,energy);
	}
void setDeDxE(int channel, float dedxe){
	this.dedxe.add(channel,dedxe);
	}
public void addDeDxE(float dedxe){
	this.dedxe.add(dedxe);
}
void setDeDxN(int channel, float dedxn){
	this.dedxn.add(channel,dedxn);
	}
public void addDeDxN(float dedxn){
	this.dedxn.add(dedxn);
}
void setMass(int channel, float mass){
	this.mass.add(channel,mass);
	}
void setPoint(int channel,int counts, float energy, float dedxe, float dedxn){
	this.counts.add(channel,counts);
	this.energy.add(channel,energy);
	this.dedxe.add(channel,dedxe);
	this.dedxn.add(channel,dedxn);
}
void setIon(int Z){
	parameter[0]=(float)Z;
}
void setE0(float E0){
	if (E0<10) {
		parameter[1]=1000*E0;
	}
	else {
		parameter[1]=E0;
	}
}
void setA(float a){
	parameter[2]=a;
}
void setB(float b){
	parameter[3]=b;
}
void setC(float c){
	parameter[4]=c;
}
void setParameters(int Z, float E0, float a, float b, float c){
	setIon (Z);
	setE0(E0);
	setA(a);
	setB(b);
	setC(c);
}
public void setMass(int channel_min, int channel_max){
	//calculation of mass per channel=a_keV/(epsilon_electronic(i)+epsilon_nuclear(i))
	
	channel_max=Math.min(channel_max, getLastChannel());
	channel_min=Math.max(channel_min,get1stChannel());
	mass.clear();
        
        for (int i=0;i<=channel_max;i++){
            mass.add((float)0);
        }
        
        for (int channel=channel_max; channel>channel_min; channel--){
            if (energy(channel)>0){
		mass.set(channel,mass.get(channel+1)+getB()/(getDeDxE(channel-1)+getDeDxN(channel-1)));
            }
            else {
		mass.set(channel,(float)0);
            }
		
	}

}

//Methods
public void calibrate(){
        energy.clear();
        energy.add((float)(0));
	for (int channel=1; channel<counts.size(); channel++){
		energy.add(Math.max(getA()*(channel^2)+getB()*channel+getC(),0));
	}
        
}
public void calibrate(int Z, float E0, float a, float b, float c){
	setParameters(Z, E0, a, b, c);
        energy.clear();
        energy.add((float)(0));
        for (int channel=1; channel<counts.size(); channel++){
            energy.add(Math.max(getA()*(channel^2)+getB()*channel+getC(),0));
        }
}
public void calibrate(int Z, double E0, double a, double b, double c){
	setParameters(Z, (float)E0, (float)a, (float)b, (float)c);
        energy.clear();
        energy.add((float)(0));
        for (int channel=1; channel<counts.size(); channel++){
            energy.add(Math.max(getA()*(channel^2)+getB()*channel+getC(),0));
        }
}
public void calibrate(float E0, float a, float b, float c){
	int Z=getIon();
	setParameters(Z, E0, a, b, c);
        energy.clear();
        energy.add((float)(0));
        for (int channel=1; channel<counts.size(); channel++){
            energy.add(Math.max(getA()*(channel^2)+getB()*channel+getC(),0));
        }
}
public void calibrate(float a, float b, float c){
	int Z=getIon();
	float E0=getE0();
	setParameters(Z, E0, a, b, c);
        energy.clear();
        energy.add((float)(0));
        for (int channel=1; channel<counts.size(); channel++){
		energy.add(Math.max(getA()*(channel^2)+getB()*channel+getC(),0));
        }
}
public float massCalc(int channel_min, int channel_max){
	//calculation of mass per channel=a_keV/(epsilon_electronic(i)+epsilon_nuclear(i))
	
	channel_max=Math.min(channel_min, getLastChannel());
	channel_min=Math.max(channel_max,get1stChannel());
	mass.clear();
        
        for (int i=0;i<=channel_max;i++){
            mass.add((float)0);
        }
        
        for (int channel=channel_max; channel>=channel_min; channel--){
            if (energy(channel)>0){
		mass.set(channel,mass.get(channel+1)+getB()/(getDeDxE(channel-1)+getDeDxN(channel-1)));
            }
            else {
		mass.set(channel,(float)0);
            }
		
	}
		return totalMassCalc(channel_min, channel_max);
}

public void initWeight(){
   float t=0;
   for (int i=1;i<counts.size();i++) t+=counts.get(i);
   for (int i=1;i<counts.size();i++) weight.add(counts.get(i)/t);
}
public float massCalc(){
	//calculation of mass per channel=a_keV/(epsilon_electronic(i)+epsilon_nuclear(i))
	int channel_min, channel_max;
	channel_max=Math.min(getCounts(0), getLastChannel());
	channel_min=Math.max(0,get1stChannel());
	mass.clear();
        for (int i=0;i<=channel_max;i++){
		mass.add((float)0);
        }
        
	for (int channel=channel_max-1; channel>=channel_min; channel--){
		if (energy(channel)>0){
		mass.set(channel,mass.get(channel+1)+getB()/(getDeDxE(channel)+getDeDxN(channel)));
		}
		else {
			mass.set(channel,(float)0);
		}
		
	}
        return totalMassCalc(channel_min, channel_max);
}
float totalMassCalc(int start, int stop){
	float totalMass=0;
	float totalCounts=0;
	

	for (int i=start; i<=stop; i++){
		
		totalMass+=mass.get(i)*counts.get(i);
		totalCounts+=counts.get(i);
		}
		totalMass/=totalCounts;
		
	return totalMass;
}

public void readSpectra(String path){
	this.path=path;
        counts.clear();
        weight.clear();
        try{
            InputStream ips=new FileInputStream(path); 
            InputStreamReader ipsr=new InputStreamReader(ips);
            BufferedReader br=new BufferedReader(ipsr);
            String ligne;
            ligne=br.readLine();			
            //Converts each line into element information as described above
            while (ligne != null){
		counts.add(Integer.parseInt(ligne));
		ligne=br.readLine();
            }
            counts.set(0,counts.size());
            br.close(); 
	}
	catch (Exception e){
	}
        initWeight();

	}


int getMedianChan(){
	float totalcounts=0;
	for (int i=1;i<counts.size();i++){
		totalcounts+=getCounts(i);
	}
	float limit=totalcounts/2;
	int i=1;
	while (totalcounts>limit){
		totalcounts-=getCounts(i);
		i++;
	}
	return i;

}
void fitCalibration(int x1, float y1){
	//x1, channel
	//y1, mass corresponding to channel x1

	float x0= (float)getChannel(getE0());
	float y0=getE0();
	
	float a1,b1, xmean,ymean=getMinMass();
	
	//return the channel corresponding to calibrated mass and get the corresponding energy
	int i=mass.lastIndexOf((float)0)+1;
	while ((ymean>y1)){
		ymean=getMass(i);
		i++;
	}
	y1=energy(i);
	
	//calculation of the new energy calibration: E=a1*x+b1
	xmean=(x0+x1)/2;
	ymean=(y0+y1)/2;

	a1=((x0-xmean)*(y0-ymean)+(x1-xmean)*(y1-ymean))/2;
	a1/=((x0-xmean)*(x0-xmean)+(x1-xmean)*(x1-xmean))/2;
	b1=ymean-a1*xmean;
	
	/*cout <<"x0: " << x0 << " y0: " << y0 << endl;
	cout <<"x1: " << x1 << " y1: " << y1 << endl;
	cout <<"a1: "<< a1 << " b1: " << b1 << endl;*/

}
void fitCalibration(int x1, float y1, float x0){
	//x1, channel
	//y1, mass corresponding to channel x1

	float y0=getE0();
	
	float a1,b1, xmean,ymean=getMinMass();
	
	
	//return the channel corresponding to calibrated mass and get the corresponding energy
	int i=mass.lastIndexOf((float)0)+1;
	while ((ymean>y1)){
		ymean=getMass(i);
		i++;
	}
	y1=energy(i);
	
	//calculation of the new energy calibration: E=a1*x+b1
	xmean=(x0+x1)/2;
	ymean=(y0+y1)/2;

	a1=((x0-xmean)*(y0-ymean)+(x1-xmean)*(y1-ymean))/2;
	a1/=((x0-xmean)*(x0-xmean)+(x1-xmean)*(x1-xmean))/2;
	b1=ymean-a1*xmean;
	
	/*cout <<"x0: " << x0 << " y0: " << y0 << endl;
	cout <<"x1: " << x1 << " y1: " << y1 << endl;
	cout <<"a1: "<< a1 << " b1: " << b1 << endl;*/

}
public void showResults(){
	ij.text.TextWindow r=new ij.text.TextWindow("Results", "Channel"+"\t"+"Counts"+"\t"+" Energy"+"\t"+"DeDxE"+"\t"+"DeDxN"+"\t"+"Mass i", "", 500, 500);
	r.append("File: "+path);
	r.append("Z Ion = " + String.valueOf(getIon()));
	r.append("E0 = "+String.valueOf(getE0())+ " keV");
	r.append("Calibration (E=ax² + bx + c): a = " + String.valueOf(getA())+" ; b = " + String.valueOf(getB())+" ; c = " +String.valueOf(getC()));
	r.append("Total mass (µg/cm²): "+String.valueOf(massCalc()));
	try{
	for (int i=0; i<=getLastChannel(); i++){
		r.append(String.valueOf(i)+"\t"+ String.valueOf(getCounts(i))+"\t" +String.valueOf(energy(i))+"\t" +String.valueOf(getDeDxE(i))+"\t" +String.valueOf(getDeDxN(i))+"\t" +String.valueOf(getMass(i)));
	}
	}
	catch (Exception e){
	}
}
public void shortResults(){
	ij.text.TextWindow r=new ij.text.TextWindow("Results","Summary", "", 500, 500);
	r.append("File: "+path);
	r.append("Z Ion = " + String.valueOf(getIon()));
	r.append("E0 = "+String.valueOf(getE0())+ " keV");
	r.append("Calibration (E=ax² + bx + c): a = " + String.valueOf(getA())+" ; b = " + String.valueOf(getB())+" ; c = " +String.valueOf(getC()));
	r.append("Total mass (µg/cm²): "+String.valueOf(massCalc()));
	}
public void measure(){
	
		massCalc();
        //shortResults();
		//makeProfile(mass, "Mass", "Energy", "Mass (µg/cm²)");
		//makeProfile(dedxe, "Elec. Stop. Pow.", "Energy", "Stop.Pow. (keV/µg/cm²)");
		//makeProfileInt(counts, path, "Energy", "Counts");
}
public void measure(int channel_min, int channel_max){
	
		massCalc(channel_min,channel_max);
        //shortResults();
		//makeProfile(mass, "Mass", "Energy", "Mass (µg/cm²)");
		//makeProfile(dedxe, "Elec. Stop. Pow.", "Energy", "Stop.Pow. (keV/µg/cm²)");
		//makeProfileInt(counts, path, "Energy", "Counts");
}
public void makeProfile(ArrayList <Float> table, String title, String xAxis, String yAxis){
	double [] xValues = new double [table.size()];
	double [] yValues = new double [table.size()] ;
	
	for (int i=get1stChannel();i<=getLastChannel();i++){
			xValues[i]=energy.get(i);
			yValues[i]=table.get(i);
		}
		
	Plot p=new Plot(title,xAxis,yAxis,xValues,yValues);
	//p.setLimits(get1stChannel(),getLastChannel(),getMin(yValues), getMax(yValues));
	p.show();
}
public void makeProfileInt(ArrayList <Integer> table, String title, String xAxis, String yAxis){
	double [] xValues = new double [table.size()];
	double [] yValues = new double [table.size()] ;
	
	for (int i=get1stChannel();i<=getLastChannel();i++){
			xValues[i]=energy.get(i);
			yValues[i]=table.get(i);
		}
		
	Plot p=new Plot(title,xAxis,yAxis,xValues,yValues);
	//p.setLimits(get1stChannel(),getLastChannel(),getMin(yValues), getMax(yValues));
	p.show();
}
double getMin(double [] table){
	double [] t = table;
	Arrays.sort(t);
	return t[0];
}
double getMax(double [] table){
	double [] t = table;
	Arrays.sort(t);
	return t[t.length-1];
}
}
