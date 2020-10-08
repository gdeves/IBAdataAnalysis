package IBA_J.Spectra;

import IBA_J.GeneratedMap.GeneratedMap;
import IBA_J.ConvertListFiles.ADC.ADC;
import IBA_J.MainFrame.MainFrame;

import ij.*;
import IBA_J.Prefs.PrefsManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import IBA_J.resources.lib.FrameSpectra;


/**
 * Class Spectra responsible for generating a spectra object using
 * an ADC object, and optionally the boundary energy values.
 */
public final class Spectra {
  private ADC adc;
  private double[] yields;
  private float minEnergy=0;
  private int minChannel=0;
  private Float energySlope=null;//value of energy between two channels
  private int width=0; // useful if Spectra generated from an ImageGenerated
  private int height=0; // useful if Spectra generated from an ImageGenerated
  private String directory=null;// useful for saving and restoring
  private String filename =null; //if spectra is open from a file or a parent spectra
  private boolean isFromSprectraHeritated=false; //to know if the spectra has been produced from a spectra or a file (if file : level=0)
  private static MainFrame parentWindow;
  private ArrayList<GeneratedMap> producedMaps;
 
  
  
  
  
  /**
   * This is the basic constructor when you want to create a Spectra from an ADC
   * @param adc the ADC source to build the spectra
   * @param filename name of the file (ex : "name".pixe (or an other extension) contains the ADC information)
   */
  public Spectra(ADC adc,String filename) {
    PrefsManager prefs=new PrefsManager();  
    this.producedMaps = new ArrayList<>();
    this.adc=adc;
    this.filename=filename;
    yields=adc.getSpectra();
    int i = yields.length-1;
    while (yields[i]<1){
      i--;
    }
    yields=Arrays.copyOfRange(yields, 0, i+1);
    int nEvents=adc.getNEvents();
    int maxXsize= 1024;
    int maxYsize=1024;
    //x=0 or y=0 is ignored with this code
  
    if (prefs.getManualROIState()) {
        maxXsize=prefs.ijGetIntValue(".IBA.roimap.sizeX",1024);
        maxYsize=prefs.ijGetIntValue(".IBA.roimap.sizeY",1024);

    } 
    
    for(int j=0;j<nEvents;j++){
        if(adc.getX(j)<0 || adc.getY(j)<0 || adc.getY(j)>maxYsize || adc.getX(j)>maxXsize ){
            adc.removeEvent(j);
            nEvents--;
        }
    }
    
  } 
  
  // constructor with an ADC, a float value for lower energy delimiter and
  // a boolean to retrieve the energy value
  /**
   * Use this constructor if the Spectra does not start at channel 0
     * @param adc
     * @param filename
     * @param energyMin
   * @param startAtEnergMin if true the first channel is adjusted 
   */
  public Spectra(ADC adc, String filename, float energyMin, boolean startAtEnergMin) {//to use if spectra don't begin at channel 0
    this(adc,filename);
        this.producedMaps = new ArrayList<>();
    if (startAtEnergMin){
      this.minChannel=getIndex(energyMin,false);
      yields=Arrays.copyOfRange(yields, minChannel, yields.length);
    }
    this.minEnergy=energyMin;
  }
  
 /**
  * Use this constructor if the Spectra is generated from an ImageGenerated
     * @param adc
     * @param filename
     * @param generatedMap
  */
  public Spectra(ADC adc,String filename,GeneratedMap generatedMap) {
    this(adc,filename);
    this.producedMaps = new ArrayList<>();
    //mapSizeX=generatedMap.getWidth()-1;
    //mapSizeY=generatedMap.getHeight()-1;
    width=generatedMap.getWidth();
    height=generatedMap.getHeight();
    
  }
  
  /**
   * Use this constructor if the Spectra is generated from an ImageGenerated and does not start at channel 0
     * @param adc
     * @param filename
     * @param gMap
     * @param energyMin
     * @param isStartingAtEnergyMin
   */
  public Spectra(ADC adc,String filename,GeneratedMap gMap, float energyMin, boolean isStartingAtEnergyMin) {
    this(adc,filename,energyMin,isStartingAtEnergyMin);
        this.producedMaps = new ArrayList<>();
    //mapSizeX=gMap.getWidth()-1;
    //mapSizeY=gMap.getHeight()-1;
    width=gMap.getWidth();
    height=gMap.getHeight();
    
  }
  
  @Override
  protected void finalize() throws Throwable {
      System.gc();
      super.finalize();
  }
  
  // getters & setters
  /**
   * Method to get an ADC
   * @return ADC
   */
  public ADC getADC (){
    return adc;
  }
  /**
   * Method to get the directory used to save session
   * @return directory
   */
  public String getDirectory(){
      return directory;
  }
  /**
   * Method to get the path corresponding to spectra
   * @return 
   */
  public String getPath(){
      if (!isFromSprectraHeritated)
        return filename;
      return filename;
  }
  /**
   * Method to get path for the corresponding spectra given a specified roi
   * @param roiName
   * @return 
   */
  public String getPath(String roiName){
      if (!isFromSprectraHeritated)
        return filename;
      return filename+"-"+roiName;
  }
  /**
   * Method to get filename (without path)
   * @return name of the file
   */
  public String getFilename(){
        File f=new File(getPath());
        if (!isFromSprectraHeritated)
        return f.getName();
      return f.getName()+"-"+String.valueOf(1);
  }
  /**
   * Method to get the minimum channel of the corresponding spectra
   * @return 
   */
  public int getMinChannel(){
      return minChannel;
  }
  
  /**
   * Method to known if the spectra was saved
   * @return the name of the directory used for saving or null if not saved
   */
  public boolean isSaved(){
      return directory!=null;
  }
  /**
   * Method to know if a spectra was heritated from a roi
   * @return 
   */
  public boolean getHeritage(){
      return isFromSprectraHeritated;
  }
  /**
   * Method to set the filename
   * @param filename 
   */
  public void setFilename(String filename){
      this.filename=filename;
  }   
  
    /**
     * Method to set Heritage for a spectra
     * @param isFromSpectraHeritated
     */
    public void setHeritage(boolean isFromSpectraHeritated){
      this.isFromSprectraHeritated=isFromSpectraHeritated;
  }
  
    /**
     * Method to set parent window
     * @param parentWindow
     */
    public void setParentWindow(MainFrame parentWindow){
      Spectra.parentWindow=parentWindow;
      parentWindow.addSpectra(this);
  }
  
    /**
     * Method to get parent window
     * @return
     */
    public MainFrame getParentWindow(){
      return parentWindow;
  }
  
    /**
     * Method to get parent window
     * @return
     */
    public static MainFrame getParentWindowS(){
      return parentWindow;
  }
  
  // this method returns true if energy vaue is within boundries
  /**
   * Use this method to check if an energy exists in the Spectra
   * @param energy the energy to check
   * @return true if the energy exists.
   */
  public boolean isAvailable(float energy){
    if (energy<minEnergy)
        return false;
    float energyMax;
    if (energySlope==null){
        energyMax = minEnergy + (yields.length-1) * 1;
    }
    else {
        energyMax = minEnergy + (yields.length-1) * energySlope;
    }
    return energy <= energyMax;
  }
  
  // 

    /**
     * Method to set calibration
     * @param minEnergy
     * @param energySlope
     */
      public void energyScaling(float minEnergy, float energySlope){
    this.minEnergy=minEnergy;
    this.energySlope=energySlope;
  }
  
    /**
     * Method to know if a spectra is energy scaled
     * @return
     */
    public boolean isScaled(){
    return energySlope!=null;
  }

  /**
   * Use this method to get the energies of the Spectra
   * @return An array containing all the energies of the Spectra
   */
  public float[] getEnergies(){
    float[] energies= new float[yields.length];
    energies[0]=minEnergy;
    float step;
    if (energySlope==null){step=1;}
    else{step=energySlope;}
    for (int i=1;i<energies.length;i++){
        energies[i]=energies[i-1]+step;
    }
    return energies;
  }
  
    /**
     * Method to convert a table of float to a table of double values
     * @param input
     * @return
     */
    public double[] convertFloatsToDoubles(float[] input){
    if (input == null){
        return null; 
    }
    double[] output = new double[input.length];
    for (int i = 0; i < input.length; i++){
        output[i] = (double) input[i];
    }
    return output;
  }
  
    /**
     * Method to plot a spectra
     * @param titleWindow
     * @param titleGraph
     * @return
     */
    public FrameSpectra plot(String titleWindow, String titleGraph){
      return plot(titleWindow, titleGraph,10);
  }
  
  /**
   * This method creates a new plot using FrameSpectra
   * @param windowTitle title of the windows where the Spectra will be draw
   * @param graphTitle title of the component where the Spectra will be draw
   * @param nROI the number of selected ROI
   * @return a frame of the class FrameSpectra, use showVisible to affich
   */
  public FrameSpectra plot(String windowTitle, String graphTitle, int nROI){
    PrefsManager prefs=new PrefsManager();
    if(nROI<=0)nROI=Integer.valueOf(prefs.ijGetValue("IBA.nROI",""+10));
      
    double[] xEnergies = convertFloatsToDoubles(getEnergies());
    FrameSpectra plot1=new FrameSpectra(this,windowTitle,graphTitle,xEnergies,yields,nROI);
    return plot1;
  }

  /**
     * @param energyToSearch
     * @param isLowerBounded
   * @return the index corresponding to the energy which is closer to energyToSearch in function of rightIncluded
   */
  public int getIndex(float energyToSearch, boolean isLowerBounded){
    float[] energies=getEnergies();
    if (isLowerBounded){
      int i=0;
      while(energies[i]<energyToSearch){
        i++;
      }
      return i;
    }
    else {
      int i=energies.length-1;
      while(energies[i]>energyToSearch){
        i--;
      }
      return i;
    }
  }

  /**
   * Use this method to search the max "x" or "y". It will help to define the resolution of an ImageGenerated
   * @param direction give the element to search : "x", "y" or "E"/"e" are accepted (all other values will be considered as "E")
   * @return the max value of the given element (valToSearch)
   */
  public int searchMax(String direction){
    int scan_direction;
      switch (direction) {
          case "x":
              scan_direction=0;
              break;
          case "y":
              scan_direction=1;
              break;
          default:
              return -1;
      }
    int max=0;
    for (int i=0;i<adc.getNEvents();i++){
      int[] event=adc.getEvent(i);
      if(event[scan_direction]>max){max=event[scan_direction];}
    }
    return max;
  }
  
  /**
   * generate a chemical Map from an energy window as a new ImageGenerated using this Spectra 
   * @param minEnergy float value as min channel
   * @param maxEnergy fload value as max channel
   * @return a chemical element map 
   */
  public GeneratedMap elementMap(float minEnergy,float maxEnergy){
    /*
      if (width==0)
        width=searchMax("x")+1;
        
        width=255;
    if (height==0)
        height=searchMax("y")+1;
        width=255;
      */
    width=adc.getMaxX();
    height=adc.getMaxY();
    IJ.log("Max X: " +Integer.toString(width));
    int minIndex= getIndex(minEnergy,false)+minChannel;
    int maxIndex= getIndex(maxEnergy,true)+minChannel;
    //double[] countPerPixel= new double[(width+1)*(height+1)];
    double[] countPerPixel= new double[(width+1)*(height+1)];
    for (int i=0;i<adc.getNEvents();i++){
      int[] event=adc.getEvent(i);
      try{
        if (event[2]>=minIndex && event[2]<=maxIndex){
            //countPerPixel[event[0]-1+(event[1]-1)*(width)]+=1;
            countPerPixel[event[0]+(event[1])*(width)]+=1;
            //countPerPixel[event[0]+(event[1])*(width)]+=1;
        }
      } catch(Exception e){
                IJ.log("** Warning** Event XYE "+event[0] + " " + event[1] + " " + event[2]  +" removed / " + e.toString());
                }
    }
    GeneratedMap img= new GeneratedMap(this,countPerPixel,minEnergy,maxEnergy,width,height);
    producedMaps.add(img);
    return img;
  }
  
  /**
   * same as generatePicture(start,end) but read the ADC only once for several ImageGenerateds
   * @param rois array containing the two bounding values for each ImageGenerated
   * @return a table containing all calculated maps
   */
  public GeneratedMap[] elementMaps(float[][] rois){
    /*
      if (width==0){
        //mapSizeX=searchMax("x")-1;//-1 because here x starts at 1 and for the picture x starts at 0
        width=searchMax("x")+1;
        width=255;
        }
    if (height==0){
        //mapSizeY=searchMax("y")-1;
        height=searchMax("y")+1;
        height=255;
        }
    */
    width=adc.getMaxX();
    height=adc.getMaxY();
    int[][] roiIndex = new int[rois.length][2];
    for(int i=0; i<rois.length;i++){
        roiIndex[i][0]= getIndex(rois[i][0],false)+minChannel;
        roiIndex[i][1]= getIndex(rois[i][1],true)+minChannel;
    }
    //double[][] countPerPixel= new double[rois.length][(width+1)*(height+1)];
    double[][] countPerPixel= new double[rois.length][(width+1)*(height+1)];
     IJ.log("width: " + Integer.toString(width));
     IJ.log("height: " + Integer.toString(height));
     
    for (int i=0;i<adc.getNEvents();i++){
      int[] event=adc.getEvent(i);
           
        for (int j=0; j<rois.length;j++){
            int minRoiIndex = roiIndex[j][0];
            int maxRoiIndex = roiIndex[j][1];
            
            try{
                if (event[2]>=minRoiIndex && event[2]<=maxRoiIndex){

                    //countPerPixel[j][event[0]-1+(event[1]-1)*(width)]+=1;
                    countPerPixel[j][event[0]+(event[1])*(width+1)]+=1;
                }
            } catch (Exception e){
                IJ.log("** Warning** Event XYE "+event[0] + " " + event[1] + " " + event[2] + " in map " + j +" removed / " + e.toString());
            }
            
        }
       
    }
    GeneratedMap[] arrayOfImgGen = new GeneratedMap[rois.length];
    
    for (int i=0; i<rois.length;i++){
        float start = rois[i][0];
        float end = rois[i][1];
        arrayOfImgGen[i]= new GeneratedMap(this,countPerPixel[i],start,end,width+1,height+1);
        producedMaps.add(arrayOfImgGen[i]);
    }
    
    
  return arrayOfImgGen;
  }   
  
    /**
     *
     * @return
     */
    public String getNameToSave(){
       return getPath()+".spct.spj";
   }
  
    /**
     * the save method stores spectra data to a file in the given directory
     * @param directory where the Spectra will be save
     */
    public void save(String directory){
        if (isSaved() && this.directory.equals(directory))
            return;
        this.directory=directory;
        String nameToSave=getNameToSave();
        DataOutputStream file = null;
        try {
            file = new DataOutputStream(new BufferedOutputStream(
                new FileOutputStream(directory+nameToSave)));
            file.writeFloat(minEnergy);
            file.writeInt(minChannel);
            if (energySlope==null){
                file.writeFloat(-1);
            }
            else{
                file.writeFloat(energySlope);
            }
            file.writeInt(width);
            file.writeInt(height);
            adc.saveXYEListFile(file);
        } 
        catch(FileNotFoundException e){
            IJ.log(tr("**Error** "+ e.toString()));
        }
        catch(IOException e2){
            IJ.log(tr("**Error** "+ e2.toString()));
        }
        try{
            if (file!=null) {
               file.close();
            }   
        }
        catch(IOException e){
            IJ.log(tr("**Error** "+ e.toString()));
        }
    }
    
    /**
     *
     * @param strToTranslate
     * @return
     */
    public String tr(String strToTranslate){
        return MainFrame.translate(strToTranslate);
    }
}