package IBA_J.Spectra;

import IBA_J.GeneratedMap.GeneratedMap;
import IBA_J.ConvertListFiles.ADC.ADC;
import IBA_J.MainFrame.MainFrame;

import ij.*;
import IBA_J.Prefs.PrefsManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import IBA_J.resources.lib.XYPlotSp;
import ij.plugin.frame.RoiManager;
import ij.*;


/**
 * Class Spectra responsible for generating a spectra object using
 * an ADC object, and optionally the boundary energy values.
 */
public final class Spectra {
  private ADC adc;
  private double[] yEvt;
  private float minEnergy=0;
  private int minChannel=0;
  private Float energySlope=null;//value of energy between two channels
  private int resX=0; // useful if Spectra generated from an ImageGenerated
  private int resY=0; // useful if Spectra generated from an ImageGenerated
  private String directory=null;// useful for saving and restoring
  private String filename =null; //if spectra is open from a file or a parent spectra
  private boolean isFromSprectraHeritated=false; //to know if the spectra has been produced from a spectra or a file (if file : level=0)
  private static MainFrame parentWindow;
  private ArrayList<GeneratedMap> producedMaps;
  private RoiManager roiManager;
  
  
  /**
   * This constructor is used to restore a Spectra from a path
   * @param path
   * @param parentWindow 
   */
  public Spectra(String path, MainFrame parentWindow){ 
      this.producedMaps = new ArrayList<>();
      restore(path,parentWindow);
  }
  
  /**
   * This is the basic constructor when you want to create a Spectra from an ADC
   * @param adc the ADC source to build the spectra
   * @param filename name of the file (ex : "name".pixe (or an other extension) contains the ADC information)
   */
  public Spectra(ADC adc,String filename) {
    this.producedMaps = new ArrayList<>();
    this.adc=adc;
    this.filename=filename;
    yEvt=adc.getSpectra();
    int i = yEvt.length-1;
    while (yEvt[i]<1){
      i--;
    }
    yEvt=Arrays.copyOfRange(yEvt, 0, i+1);
    int nEvents=adc.getNEvents();
    //x=0 or y=0 is ignored with this code
    for(int j=0;j<nEvents;j++){
        if(adc.getX(j)<1 || adc.getY(j)<1 || adc.getY(j)>255 || adc.getX(j)>255){
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
      yEvt=Arrays.copyOfRange(yEvt, minChannel, yEvt.length);
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
    resX=generatedMap.getWidth()-1;
    resY=generatedMap.getHeight()-1;
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
    resX=gMap.getWidth()-1;
    resY=gMap.getHeight()-1;
  }
  
  @Override
  protected void finalize() throws Throwable {
   super.finalize();
  }
  
  // getters & setters
  public ADC getADC (){
    return adc;
  }
  
  public String getDirectory(){
      return directory;
  }
  
  public String getPath(){
      if (!isFromSprectraHeritated)
        return filename;
      return filename;
  }
  public String getPath(String roiName){
      if (!isFromSprectraHeritated)
        return filename;
      return filename+"-"+roiName;
  }
    public String getFilename(){
        File f=new File(getPath());
        if (!isFromSprectraHeritated)
        return f.getName();
      return f.getName()+"-"+String.valueOf(isFromSprectraHeritated);
  }
  public int getMinChannel(){
      return minChannel;
  }
  
  
  public boolean isSaved(){
      return directory!=null;
  }
  
  public boolean getHeritage(){
      return isFromSprectraHeritated;
  } 
  public void setFilename(String filename){
      this.filename=filename;
  }   
  public void setHeritage(boolean isFromSpectraHeritated){
      this.isFromSprectraHeritated=isFromSpectraHeritated;
  }
  
  public void setParentWindow(MainFrame parentWindow){
      Spectra.parentWindow=parentWindow;
      parentWindow.addSpectra(this);
  }
  
  public MainFrame getParentWindow(){
      return parentWindow;
  }
  
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
        energyMax = minEnergy + (yEvt.length-1) * 1;
    }
    else {
        energyMax = minEnergy + (yEvt.length-1) * energySlope;
    }
    return energy <= energyMax;
  }
  
  // this method sets the energyMin and energyMax
  public void energyScaling(float minEnergy, float energySlope){
    this.minEnergy=minEnergy;
    this.energySlope=energySlope;
  }
  
  public boolean isScaled(){
    return energySlope!=null;
  }

  /**
   * Use this method to get the energies of the Spectra
   * @return An array containing all the energies of the Spectra
   */
  public float[] getEnergies(){
    float[] energies= new float[yEvt.length];
    energies[0]=minEnergy;
    float step;
    if (energySlope==null){step=1;}
    else{step=energySlope;}
    for (int i=1;i<energies.length;i++){
        energies[i]=energies[i-1]+step;
    }
    return energies;
  }
  
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
  
  public XYPlotSp plotSpectra(String titleWindow, String titleGraph){
      return plotSpectra(titleWindow, titleGraph,10);
  }
  
  /**
   * This method creates a new plot using XYPlotSp
   * @param titleWindow title of the windows where the Spectra will be draw
   * @param titleGraph title of the component where the Spectra will be draw
   * @param nROI the number of selected ROI
   * @return a frame of the class XYPlotSp, use showVisible to affich
   */
  public XYPlotSp plotSpectra(String titleWindow, String titleGraph, int nROI){
    PrefsManager prefs=new PrefsManager();
    if(nROI<=0)nROI=Integer.valueOf(prefs.ijGetValue("IBA.nROI",""+10));
      
    double[] xEnergies = convertFloatsToDoubles(getEnergies());
    XYPlotSp plot1=new XYPlotSp(this,titleWindow,titleGraph,xEnergies,yEvt,nROI);
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
    if (resX==0)
        resX=searchMax("x");
    if (resY==0)
        resY=searchMax("y");
    int indMin= getIndex(minEnergy,false)+minChannel;
    int indMax= getIndex(maxEnergy,true)+minChannel;
    double[] valNbEventPerXY= new double[(resX+1)*(resY+1)];
    for (int i=0;i<adc.getNEvents();i++){
      int[] event=adc.getEvent(i);
      try{
        if (event[2]>=indMin && event[2]<=indMax){
            valNbEventPerXY[event[0]-1+(event[1]-1)*(resX+1)]+=1;
        }
      } catch(Exception e){}
    }
    GeneratedMap img= new GeneratedMap(this,valNbEventPerXY,minEnergy,maxEnergy,resX,resY);
    producedMaps.add(img);
    return img;
  }
  
  /**
   * same as generatePicture(start,end) but read the ADC only once for several ImageGenerateds
   * @param rois array containing the two bounding values for each ImageGenerated
   * @return a table containing all calculated maps
   */
  public GeneratedMap[] elementMaps(float[][] rois){
    
      if (resX==0)
        resX=searchMax("x")-1;//-1 because here x starts at 1 and for the picture x starts at 0
    if (resY==0)
        resY=searchMax("y")-1;
    int[][] roiIndex = new int[rois.length][2];
    for(int i=0; i<rois.length;i++){
        roiIndex[i][0]= getIndex(rois[i][0],false)+minChannel;
        roiIndex[i][1]= getIndex(rois[i][1],true)+minChannel;
    }
    double[][] countPerPixel= new double[rois.length][(resX+1)*(resY+1)];
    
    for (int i=0;i<adc.getNEvents();i++){
      int[] event=adc.getEvent(i);
           
        for (int j=0; j<rois.length;j++){
            int minRoiIndex = roiIndex[j][0];
            int maxRoiIndex = roiIndex[j][1];
            
            try{
                if (event[2]>=minRoiIndex && event[2]<=maxRoiIndex){
                    countPerPixel[j][event[0]-1+(event[1]-1)*(resX+1)]+=1;
                }
            } catch (Exception e){IJ.log("** Warning** Event XYE "+event[0] + " " + event[1] + " " + event[2] + " in map " + j +" removed");}
            
        }
       
    }
    GeneratedMap[] arrayOfImgGen = new GeneratedMap[rois.length];
    
    for (int i=0; i<rois.length;i++){
        float start = rois[i][0];
        float end = rois[i][1];
        arrayOfImgGen[i]= new GeneratedMap(this,countPerPixel[i],start,end,resX,resY);
        producedMaps.add(arrayOfImgGen[i]);
    }
    
    
  return arrayOfImgGen;
  }   
  
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
            file.writeInt(resX);
            file.writeInt(resY);
            adc.saveXYEListFile(file);
        } 
        catch(FileNotFoundException e){
            IJ.log(tr("Saving file failure")+" "+directory);
        }
        catch(IOException e2){
            IJ.log(tr("Saving file failure"));
        }
        try{
            if (file!=null) {
               file.close();
            }   
        }
        catch(IOException e){
            IJ.log(tr("Saving file failure"));
        }
    }
    
    /**
     * Uses the method save of ImageGenerated for all the ImageGenerateds produced by this Spectra
     * @param directory where all the ImageGen will be saved
     * @return an arraylist containing the name of the saved ImageGen
     */
    public String[] saveAllImgGen(String directory){
        String[] namesImgSaved = new String[producedMaps.size()];
        for(int i=0;i<producedMaps.size();i++){
            producedMaps.get(i).save(directory);
            namesImgSaved[i]=producedMaps.get(i).getNameToSave();
        }
        return namesImgSaved;
    }
    
    /**
     * This method restores a Spectra
     * @param path absolute path
     */
    private void restore(String path, MainFrame parentWindow){
        try{
            try (DataInputStream ips = new DataInputStream(new BufferedInputStream(new FileInputStream(path)))) {
                minEnergy=ips.readFloat();
                minChannel=ips.readInt();
                float stepEnergyTmp=ips.readFloat();
                if (stepEnergyTmp>-0.5){
                    energySlope=stepEnergyTmp;
                }
                else{
                    energySlope=null;
                }
                resX=ips.readInt();
                resY=ips.readInt();
                adc = new ADC();
                adc.open(ips);
                int index=path.lastIndexOf("/")+1;
                if (index==0)
                    index=path.lastIndexOf("\\")+1;
                directory=path.substring(0,index);
                if (path.lastIndexOf("-")!=-1){
                    try{
                        filename = path.substring(index, path.lastIndexOf("-"));
                        String levelStr=path.substring(path.lastIndexOf("-")+1, path.lastIndexOf("."));
                        levelStr=levelStr.substring(0,levelStr.lastIndexOf("."));
                        isFromSprectraHeritated = !"0".equals(levelStr);
                    }
                    catch (NumberFormatException e){
                        filename = path.substring(index, path.lastIndexOf("."));
                        filename=filename.substring(0,path.lastIndexOf("."));
                        isFromSprectraHeritated=false;
                    }
                }
                else{
                    filename = path.substring(index, path.lastIndexOf("."));
                    filename=filename.substring(0,filename.lastIndexOf("."));
                    isFromSprectraHeritated=false;
                }
                setParentWindow(parentWindow);
            }
            yEvt=adc.getSpectra();
            int i = yEvt.length-1;
            while (yEvt[i]<1){
              i--;
            }
            yEvt=Arrays.copyOfRange(yEvt, minChannel, i+1);
        }
        catch(FileNotFoundException e){}
        catch(IOException e2) {}
    }
    
    /**
     * method restores imageGenerateds of this Spectra from the given path
     * @param path absolute path to the file to restore
     * @return the restored ImageGenerated or null if unsuccessfull
     */
    public GeneratedMap restoreImgGen(String path){
        try{
            DataInputStream ips=new DataInputStream(new BufferedInputStream(new FileInputStream(path)));                 
            float start=ips.readFloat();
            float end=ips.readFloat();
            ips.close();
            String titleImg = path.substring(path.lastIndexOf("_")+1, path.lastIndexOf("."));
            titleImg=titleImg.substring(0,titleImg.lastIndexOf("."));
            GeneratedMap imgGen= elementMap(start,end);
            imgGen.show(titleImg);
            return imgGen;
        }
        catch(FileNotFoundException e){}
        catch(IOException e2) {}
        return null; 
    }    
    
    
    public String tr(String strToTranslate){
        return MainFrame.tr(strToTranslate);
    }
}