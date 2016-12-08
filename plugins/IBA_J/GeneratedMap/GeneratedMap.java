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
 * in order to generate an image from a spectra portion.
 */

public class GeneratedMap {
  Spectra sourceSpectra;//the parent spectra
  double[] sourcePixels;//if the user change the imageProc, you can use this array
  ImageProcessor imageProc;
  float startSpectra;//an energy of the parent Spectra
  float endSpectra;
  String title;
  CustomWindowImage imgWindow=null;//the window where the ImageGenerated will be show to the user
  
  
  public GeneratedMap(Spectra spectra,double[] valNbEventPerXY,float start, float end,int resX,int resY) {
    sourceSpectra=spectra;
    sourcePixels=valNbEventPerXY;
    imageProc = new FloatProcessor(resY+1,resX+1,valNbEventPerXY);
    startSpectra=start;
    endSpectra=end;
  }
  
  public int getWidth(){
      return imageProc.getWidth();
  }
          
  public int getHeight(){
      return imageProc.getHeight();
  }
  
  public String getTitle(){
      return title;
  }
  
  public void setTitle(String title){
      this.title=title;
  }
  
  public ImageProcessor getImgProcessor(){
      return imageProc;
  }
  
  public void show(String title){
    setTitle(title);
    show();
  }
  
  public void showWithOtherImgs(GeneratedMap[] otherImagesToShow){
      try{
        ImageStack imgStack = groupWithOtherImgs(otherImagesToShow);
        ImagePlus ipOfImageGen = getImagePlus(imgStack);
        int numberOfImg = otherImagesToShow.length+1;
        GeneratedMap[] tabOfImgGen = new GeneratedMap[numberOfImg];
        tabOfImgGen[0]=this;
        System.arraycopy(otherImagesToShow, 0, tabOfImgGen, 1, otherImagesToShow.length);
        imgWindow = new CustomWindowImage(ipOfImageGen,tabOfImgGen);
      }
      catch(InvalidAttributeValueException e){IJ.log(tr("Error"));}
  }
  
  /**
   * This method will create a new CustomWindowImage at each call
   */
  public void show(){
    ImagePlus ipOfImageGen = getImagePlus();
    GeneratedMap[] tabOfImgGen = new GeneratedMap[1];
    tabOfImgGen[0]= this;
    imgWindow = new CustomWindowImage(ipOfImageGen,tabOfImgGen);
    ImageCanvas icOfImageGen = imgWindow.getCanvas();
    icOfImageGen.requestFocus();
  }
  
  public ImageStack groupWithOtherImgs(GeneratedMap[] otherImagesToShow) throws InvalidAttributeValueException {
    int width = getWidth();
    int height = getHeight();
    ImageStack imgStack = new ImageStack(width,height);
    imgStack.addSlice(getTitle(), getImgProcessor());
    for (GeneratedMap otherImageToShow : otherImagesToShow){
        if(otherImageToShow.getHeight()!=height || otherImageToShow.getWidth()!=width)
            throw new InvalidAttributeValueException();
        imgStack.addSlice(otherImageToShow.getTitle(), otherImageToShow.getImgProcessor());
    }
    return imgStack;
  }
  
  public ImagePlus getImagePlus(){
      if(imgWindow==null){
        return new ImagePlus(sourceSpectra.getPath()+"_"+title,imageProc);
      }
      else
        return imgWindow.getImagePlus();
  }
  
  public ImagePlus getImagePlus(ImageStack imgStack){
      return new ImagePlus(sourceSpectra.getPath(),imgStack);
  }
    
  public ImageProcessor getIrregularRoi(){
    return imgWindow.getImagePlus().getMask();
  }
  
  public Roi getRoi(){
    return imgWindow.getImagePlus().getRoi();
  }
  
  public String getValueAsString(int x,int y){
      int resX=getWidth()-1;
      int position = x+y*(resX+1);
      return ", nbEvents="+String.valueOf(sourcePixels[position]);
  }
  
  /**
   * This method creates a new spectra from selected ROI, using the sourceSpectra.
   * @return the calculated Spectra or null if no ROI was found
   */
  public Spectra generateSpectraFromRoi(){
      Roi ipRoi = getRoi();
      if(ipRoi!=null){
        ADC adcToCalcFromRoi = new ADC();
        ADC sourceAdc = sourceSpectra.getADC();
        for (int nbEvt=0; nbEvt<sourceAdc.getNEvents(); nbEvt++){
            int[] currentEvt= sourceAdc.getEvent(nbEvt);
            int xPix = currentEvt[0]-1;
            int yPix = currentEvt[1]-1;
            if (ipRoi.contains(xPix,yPix)){
                  adcToCalcFromRoi.addEvent(currentEvt);
              }
        }   
        String nameFile=sourceSpectra.getPath();
        Spectra spectreNewCalc= new Spectra(adcToCalcFromRoi,nameFile,this);
        spectreNewCalc.setLevel(sourceSpectra.getLevel()+1);
        spectreNewCalc.setParentWindow(sourceSpectra.getParentWindow());
        return spectreNewCalc;
      }
      else{
          IJ.log(tr("Please make a selection"));
          return null;
      }
  }
  
  /**
   * @return a name containing the sourceSpectra name, the ImageGen name and the extension of the file to save.
   */
  public String getNameToSave(){
       return sourceSpectra.getPath()+"_"+getTitle()+".img.spj";
  }
  
  /**
   * This method will saved the ImageGenerated and its parent Spectra in the given directory
   * @param directory  where files have to be saved*/
  
  public void save(String directory){
        DataOutputStream file=null;
        String nameToSave = getNameToSave();
        try {
            file = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(directory+nameToSave)));
            file.writeFloat(startSpectra);
            file.writeFloat(endSpectra);
            if( !(sourceSpectra.isSaved() && directory.equals(sourceSpectra.getDirectory())) ){
                sourceSpectra.save(directory);
            }
        }
        catch (IOException e) {
            IJ.log(tr("Fail to save"));
        }
        finally {
           try {
               if(file != null) {
                   file.close();
               }
           } 
           catch (IOException e2) {
               IJ.log(tr("Fail to save"));
           }
        } 
  }
   /**
    * Saves all generated images from the parent Spectra to the given directory
     * @param directory where all files have to be saved
    */
  public void saveAll(String directory){
      sourceSpectra.saveAllImgGen(directory);
  }        
  
  public String tr(String strToTranslate){
      return sourceSpectra.tr(strToTranslate);
  }
    
}