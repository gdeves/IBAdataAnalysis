package Paparamborde.ATOM_DATA;
import java.io.*;
import java.util.StringTokenizer;
import ij.*;



public class ATOM_DATA {

//declaration

String IJdirectory, IJplugins, IJapp;
String path_ATOMDATA;

int A[]=new int[93];		/* Atomic number*/
String B[]=new String [93];	/* Element symbol*/
String C[]=new String [93];	/* Element name*/
int D[]=new int[93];		/* MAI mass*/
float E[]=new float[93];	/* MAI weight*/
float F[]=new float[93];	/* Natural Weight*/
float G[]=new float[93];	/* Density (g/cm3)*/
float H[]=new float[93];	/* Atomic Density*/
float I[]=new float[93];	/* Fermi velocity*/
float J[]=new float[93];	/* Heat Sublimation*/
float K[]=new float[93];	/* Gas density (g/cm3)*/
float L[]=new float[93];	/* Gas Atomic density /cm3*/


public ATOM_DATA(){
      /* constructor */
	  init();
	  readParameters();
	  }
 public ATOM_DATA(String Path){
	setPath(Path);
	readParameters();
	
 }
public void finalize(){
          /* destructor */
     }

public void init(){
	
	IJdirectory=System.getProperty("java.class.path").substring(0,System.getProperty("java.class.path").lastIndexOf(System.getProperty("file.separator")));
	IJplugins=IJdirectory+System.getProperty("file.separator")+"plugins";
	IJapp=IJplugins+System.getProperty("file.separator")+"Paparamborde"+System.getProperty("file.separator");
	path_ATOMDATA=IJapp+"ATOMDATA";
}


public void setPath(String Path){

	path_ATOMDATA=Path;

}

public void readParameters(){


try{
			InputStream ips=new FileInputStream(path_ATOMDATA); 
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			String ligne;
			//Discards header lines
			for (int i=0;i<2;i++){
			ligne=br.readLine();
			}
			
			//Converts each line into element information as described above
			for (int i=1;i<93;i++){
				ligne=br.readLine();
				StringTokenizer st = new StringTokenizer(ligne, " ");
					while (st.hasMoreTokens()) {
						A[i]=Integer.parseInt(st.nextToken());
						B[i]=st.nextToken();
						C[i]=st.nextToken();
						D[i]=Integer.parseInt(st.nextToken());
						E[i]=Float.parseFloat(st.nextToken());
						F[i]=Float.parseFloat(st.nextToken());
						G[i]=Float.parseFloat(st.nextToken());
						H[i]=Float.parseFloat(st.nextToken());
						I[i]=Float.parseFloat(st.nextToken());
						J[i]=Float.parseFloat(st.nextToken());
						K[i]=Float.parseFloat(st.nextToken());
						L[i]=Float.parseFloat(st.nextToken());
					}
			}
			
			br.close(); 
			}
	catch (Exception e){
			IJ.error(e.toString());
		}

}
public String getSymbol(int Z) {
	return B[Z];
}

public String getElName(int Z) {
	return C[Z];
}

public int getMAIMass(int Z) {
	return D[Z];
}

public float getMaiWeight(int Z) {
	return E[Z];
}

public float getNatWeight(int Z) {
	return F[Z];
}
public float getDensity(int Z) {
	return G[Z];	
}
public float getAtDensity(int Z) {
	return H[Z];	
}

public float getFermiVel(int Z) {
	return I[Z];	
}

public float getHeatSub(int Z) {
	return J[Z];	
}

public float getGasDensity(int Z) {
	return K[Z];	
}

public float getGasAtDensity(int Z) {
	return L[Z];	
}
public int getZ(String element){
	int i=1;
	while (!element.equals(getSymbol(i))){
	i++;
	}
	return i;
}
}