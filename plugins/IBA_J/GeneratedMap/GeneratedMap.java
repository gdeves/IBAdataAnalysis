package IBA_J.GeneratedMap;

import IBA_J.ConvertListFiles.ADC.ADC;
import IBA_J.CustomWindowImage.CustomWindowImage;
import IBA_J.Spectra.Spectra;

import ij.*;
import ij.gui.ImageCanvas;
import ij.gui.Roi;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.management.InvalidAttributeValueException;

/**
 * Class ImageGenerated handles the data retrieved from spectra
 * in order to generate an image from a spectra ROI.
 */

public class GeneratedMap {
  Spectra source;//the parent spectra
  double[] roiPixels;//if the user change the ip, you can use this array
  ImageProcessor ip;
  float minROI;
  float maxROI;
  String title;
  CustomWindowImage activeWindow=null;//the window where the ImageGenerated will be shown to the user
  
  /**
   * generates a stack of element maps
   * @param spectra
   * @param countPerPixel
   * @param start
   * @param end
   * @param width
   * @param height 
   */
  public GeneratedMap(Spectra spectra,double[] countPerPixel,float start, float end,int width,int height) {
    source=spectra;
    roiPixels=countPerPixel;
    ip = new FloatProcessor(height,width,countPerPixel);
    minROI=start;
    maxROI=end;
  }
  
  /**
   * Getter
   * @return width of maps
   */
  public int getWidth(){
      return ip.getWidth();
  }
  
  /**
   * Getter
   * @return height of the maps
   */
  public int getHeight(){
      return ip.getHeight();
  }
  
  /**
   * Getter
   * @return title of the maps
   */
  public String getTitle(){
      return title;
  }
  
  /**
   * Set the title of the maps
   * @param title 
   */
  public void setTitle(String title){
      this.title=title;
  }
  
  /**
   * Getter
   * @return reference of the image processor
   */
  public ImageProcessor getImageProcessor(){
      return ip;
  }
  
  /**
   * Shows the maps
   * @param title 
   */
  public void show(String title){
    setTitle(title);
    show();
    
    
  }
  
  /**
   * Function to show images
   * @param imagesToShow 
   */
  public void showMaps(GeneratedMap[] imagesToShow){
      try{
        ImageStack stack = imageStack(imagesToShow);
        
        ImagePlus ipStack = getImagePlus(stack);
        int size = imagesToShow.length+1;
        GeneratedMap[] stackedMaps = new GeneratedMap[size];
        stackedMaps[0]=this;
        System.arraycopy(imagesToShow, 0, stackedMaps, 1, imagesToShow.length);
        activeWindow = new CustomWindowImage(ipStack,stackedMaps);
        
      }
      catch(InvalidAttributeValueException e){IJ.log(translate("**Error**" + e.toString()));}
  }
  
  /**
   * Function to show element maps
   * This method will create a new CustomWindowImage at each call
   */
  public void show(){
    ImagePlus ipMap = getImagePlus();
    GeneratedMap[] stackedMap = new GeneratedMap[1];
    stackedMap[0]= this;
    activeWindow = new CustomWindowImage(ipMap,stackedMap);
    ImageCanvas icMap = activeWindow.getCanvas();
    icMap.requestFocus();
    
  }
  
  /**
   * Generates an image stack
   * @param images to be shown
   * @return a stack of images to be shown
   * @throws InvalidAttributeValueException if images have different width or height
   */
  public ImageStack imageStack(GeneratedMap[] images) throws InvalidAttributeValueException {
    int width = getWidth();
    int height = getHeight();
    ImageStack stack = new ImageStack(width,height);
    stack.addSlice(getTitle(), getImageProcessor());
    for (GeneratedMap showMaps : images){
        if(showMaps.getHeight()!=height || showMaps.getWidth()!=width)
            throw new InvalidAttributeValueException();
        stack.addSlice(showMaps.getTitle(), showMaps.getImageProcessor());
    }
    
    return stack;
  }
  
  /**
   * Getter
   * @return IP reference for the active map or create a new IP with same path and title if NULL
   */
  public ImagePlus getImagePlus(){
      if(activeWindow==null){
        return new ImagePlus(source.getPath()+"_"+title,ip);
      }
      else
        return activeWindow.getImagePlus();
  }
  
  /**
   * Getter
   * @param stack 
   * @return IP reference for the considered stack
   */
  public ImagePlus getImagePlus(ImageStack stack){
      return new ImagePlus(source.getPath(),stack);
  }
  
  /**
   * Getter
   * @return the processor for the defined mask
   */
  public ImageProcessor getMaskProcessor(){
    return activeWindow.getImagePlus().getMask();
  }
  
  /**
   * Getter
   * @return the considered ROI
   */
  public Roi getRoi(){
    return activeWindow.getImagePlus().getRoi();
  }
  
  /**
   * Getter
   * @param x horizontal position on the map
   * @param y vertical position on the map
   * @return the yield at the considered (x,y) position
   */
  public String getYield(int x,int y){
      int width=getWidth();
      int position = x+y*width;
      return ", nbEvents="+String.valueOf(roiPixels[position]);
  }
  
  /**
   * This method creates a new spectra from selected ROI, using the source.
   * @return the calculated Spectra or null if no ROI was found
   */
  public Spectra roiSpectra(){
      Roi ipRoi = getRoi();
      if(ipRoi!=null){
        ADC roiADC = new ADC();
        ADC sourceADC = source.getADC();
        for (int index=0; index<sourceADC.getNEvents(); index++){
            int[] event= sourceADC.getEvent(index);
            int x = event[0];
            int y = event[1];
            if (ipRoi.contains(x,y)){
                  roiADC.addEvent(event);
              }
        }   
        String filename=source.getPath();
        Spectra calculatedSpectra= new Spectra(roiADC,filename,this);
        calculatedSpectra.setHeritage(true);
        calculatedSpectra.setParentWindow(source.getParentWindow());
        return calculatedSpectra;
      }
      else{
          IJ.log(translate("**Error** No selection"));
          return null;
      }
  }
  
  /**
   * Getter
   * @return a name containing the source name, the ImageGen name and the extension of the file to save.
   */
  public String getFilename(){
       return source.getPath()+"_"+getTitle()+".img.spj";
  }
  
  /**
   * This method will saved the ImageGenerated and its parent Spectra in the given directory
   * @param directory  where files have to be saved
   */
    public void save(String directory){
        DataOutputStream os=null;
        String filename = getFilename();
        try {
            os = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(directory+filename)));
            os.writeFloat(minROI);
            os.writeFloat(maxROI);
            if( !(source.isSaved() && directory.equals(source.getDirectory())) ){
                source.save(directory);
            }
        }
        catch (IOException e) {
            IJ.log(translate("**Error** : " +e.toString()));
        }
        finally {
           try {
               if(os != null) {
                   os.close();
               }
           } 
           catch (IOException e2) {
               IJ.log(translate("**Error** Saving failed"));
           }
        } 
  }
  /**
   * Translation tool
   * @param sentence
   * @return a translated sentence
   */   
  public String translate(String sentence){
      return source.tr(sentence);
  }
    
}