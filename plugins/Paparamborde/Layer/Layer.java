package Paparamborde.Layer;
import java.util.ArrayList;

public class Layer {
// declaration 
//A sample is defined as a list of (Z, composition) values and by its density
ArrayList <Float> composition=new ArrayList <Float>();
ArrayList <Integer> Z = new ArrayList <Integer>();
ArrayList <Float> massPerChannel =new ArrayList <Float>();
float density;

//Constructors
public Layer(){
    massPerChannel.add(new Float(0));
}
//Setters
public void setDensity(float density){
	this.density=density;
}
public void setMPC(int channel, float mass){
    massPerChannel.set(channel, mass);
}
public void setMPCArray(ArrayList <Float> massPerChannel){
    this.massPerChannel=massPerChannel;
}
public void setDensity(double density){
	this.density=(float)density;
}

//Getters
public float getMPC (int channel){
    return massPerChannel.get(channel);
}
public ArrayList <Float> getMPCArray(){
    return massPerChannel;
}
public float getDensity() {
	return density;
}
public int getNElement() {
	return composition.size();
}
public int getZ(int i) {
	return Z.get(i);
}
public float getAtConc(int i) {
	return composition.get(i);
}

//Methods
public void addElement(int Z, float atConc){
	//Warning!!!!
	//there is no check for doubles in element input although 'SRModule' would not run in this case
	
	//add a line to array (Z, composition)
	this.Z.add(Z);
	composition.add(atConc);
	//get the new size of vector composition
	
}
public float NormalizeAtConc(){
	//Normalize the at. concentration to 1
	float sum=0;
	int size=composition.size();
		
	//calculate the NormalizeAtConc of atomic concentration
	for (int i=0;i<size;i++){sum+=composition.get(i);}
	
	return sum;
}
public void reset(){
	composition.clear();
	Z.clear();
        massPerChannel.clear();
}
	
}
