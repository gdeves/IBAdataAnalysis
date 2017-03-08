package IBA_J.ConvertListFiles.ActionsC;
import IBA_J.ConvertListFiles.FrameC.FrameC;
import IBA_J.ConvertListFiles.ADC.ADC;
import IBA_J.ConvertListFiles.MPA3.MPA3;
import IBA_J.ConvertListFiles.listFiles.listFiles;
import IBA_J.Prefs.PrefsManager;
import javax.swing.JFileChooser;
import java.io.File;
import java.util.ArrayList;
import ij.gui.Plot;
import ij.*;
import ij.io.FileSaver;
import ij.plugin.ContrastEnhancer;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import java.awt.Component;
import java.awt.HeadlessException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import javax.swing.JRadioButton;
import javax.swing.JPanel;


public class ActionsC{
  private final Integer sizeMapX=256;
  private final Integer sizeMapY=256;

  ArrayList <listFiles> listFilesArray=new ArrayList <>();
  int [] flags=new int[28];
  ArrayList <double []> pixe_stack=new ArrayList <>();
  ImageStack stimStack=new ImageStack(sizeMapX-2,sizeMapY-1);

  /**
  * Constructor for ActionsC class with default initialization of flags and pixe_stack
  */
  public ActionsC(){
          initFlags();
          init_pixe_stack();
  }
  
  /**
  * Gets ADC index for scanning X direction
     * @return the index for adc corresponding the X position of the beam
  */
  public int getAdcIndexScanX(){
      int indexOfAdc=0;
      for (int i=0;i<16;i++){
          if (flags[i]==4) indexOfAdc=i;
      }
      return indexOfAdc;    
  }
  
  /**
  * Gets ADC index for scanning Y direction
     * @return the index for adc corresponding the Y position of the beam
  */
  public int getAdcIndexScanY(){
      int indexOfAdc=0;
      for (int i=0;i<16;i++){
          if (flags[i]==5) indexOfAdc=i;
      }
      return indexOfAdc;    
  }
    
  /**
  * Displays a dialog box for user to select files to be processed (multiple selection possible)
  * Is used to modify the ArrayList listFilesArray
  */    
  public void selectFiles(){     
          try{
                  listFilesArray.clear();
                  PrefsManager prefs=new PrefsManager();
                  prefs.setPreference();
                  JFileChooser jF = new JFileChooser();  // a new filechosser is created
                  File myDir=new File(prefs.getLastUsedDirectory());
                  jF.setCurrentDirectory(myDir);
                  jF.setApproveButtonText("OK");         // button title
                  jF.setMultiSelectionEnabled(true);
                  
                  jF.showOpenDialog(null);               // displays the dialog box
                  
                  File [] selectedFiles = jF.getSelectedFiles(); 
                          for (File f : selectedFiles){
                                  listFiles lf=new listFiles(f.getAbsolutePath(),getAdcIndexScanX(),getAdcIndexScanY());
                                  listFilesArray.add(lf);
                                  prefs.saveDirectory(f.getAbsolutePath());
                          }
                  
          }
          catch (HeadlessException e){
          }
  }

  //Working with flags

  /**
  * Initializes flags to 1 except for Tomo projection sum and pixe map
  */
  // flags[0] to flags[15] corresponds to ADC status (1 is on)
  // flags[16] - saving XYE listfile
  // flags[17] - save gupix spectra for each ADC separately
  // flags[18] - save gupix spectra for the sum of pixe adc
  // flags[19] - save 'channel-counts' 2 columns text file (ie used for rbs)
  // flags[20] - save stim spectra as a text file
  // flags[21] - save stim maps separately as a text image readable using imageJ
  // flags[22] - plot spectra
  // flags[23] - save stim stack as TIFF
  // flags[24] - process pixe stack
  // flags[25] - display stim stack
  // flags[26] - calculate pixe map
  // flags[27] - save XYE listfile compatible with supavisio

  private void initFlags(){
          for (int i=0;i<flags.length;i++) setFlags(i,0);
          for (int i =16;i<19;i++) setFlags(i,1);//TODO remove this part of function to FrameC
  }
  /**
  * Resets all flags for ADC to 0.
  */
  private void resetJbuttonFlags(){
          for (int i=0;i<16;i++) flags[i]=0;
  }
  /**
  * Initializes arraylist containing the pixe sum spectra for all selected list files.
  */
  private void init_pixe_stack(){
      for (int i=0; i<9;i++){
          pixe_stack.add(new double[4096]);
      }
  }
  /**
  * Clears the arraylist containing projection sum spectra and initializes a new stack.
  */
  private void reset_pixe_stack(){
      //Default number of adc is 8 for AIFIRA. The 9th corresponds to pixe sum adc
      pixe_stack.clear();
      for (int i=0; i<9;i++){
          pixe_stack.add(new double[4096]);
      }
  }
  /**
  * Sets value to selected flag
      @param n index of the flag
      @param value value of the flag
      * */
  public  void setFlags(int n, int value){
      flags[n]=value;
  }
  /**
  * Sets values to flags after checking ADC status
  * flags 0 to 15 corresponding to ADC 1 to 16
  * @param f the frame containing components to be checked
  */
  public void setFlags(FrameC f){
          resetJbuttonFlags();
          int nPanel=-1; //first panel found corresponding to adc will get index 0
          
          //check for jRadioButton
          Component[] C1 = (f.jPanelB).getComponents();
          for (Component C2 : C1){
                  if (C2 instanceof JPanel){
                          nPanel+=1;
                          int nButton=0;
                          
                          Component[] C3 = ((JPanel)C2).getComponents();
                          for (Component C4 : C3) {
                                  if (C4 instanceof JRadioButton){
                                          nButton+=1;
                                          
                                          if (((JRadioButton)C4).isSelected()) flags[nPanel]=nButton;
                                  }
                          }
                  }
          }
  }
  //processing data
  /**
  * Plots a spectra in ImageJ
  * @param Yvalues double [4096] array containing values to be plotted
  * @param title Name of the plot window
  */
  private void plotSpectra(double [] Yvalues, String title){
      double [] xValues = new double [4096];
      for (int i=0;i<4096;i++) xValues[i]=i;
      
      Plot p=new Plot(title,"x","y",xValues,Yvalues);
      p.show();    
  }
  /**
  * Actions performed when ADC corresponding to RBS is detected according to flags
  * @param rbs ADC corresponding to RBS
  * @param lF name of processed list file
  * @param indexOfADC Index of RBS adc  in MPA
  */
  private void processRBS(ADC rbs, listFiles lF, int indexOfADC){
          
          if (flags[19]==1) rbs.saveChannelCountsSpectra(lF.setExtension("asc.dat"));
          if (flags[27]==1) rbs.saveXYEListFile(lF.setExtension("RBS"), (short)1);
          else if (flags[16]==1) rbs.saveXYEListFile(lF.setExtension("ADC"+Integer.toString(indexOfADC+1)+".rbs2"));
          if (flags[22]==1){
            String title=lF.getPath()+" ADC: "+String.valueOf(indexOfADC+1)+": RBS - N counts = " +String.valueOf(rbs.getNEvents()); 
            plotSpectra(rbs.getSpectra(),title);
          }
          java.lang.System.gc();
  }
  /**
  * Actions performed when ADC corresponding to PIXE is detected according to flags
  * @param pixe ADC corresponding to PIXE
  * @param lF name of processed list file
  * @param indexOfADC index of PIXE adc in MPA
  */
  private void processPIXE(ADC pixe, listFiles lF, int indexOfADC){
          //saving spectra
          if (flags[17]==1) pixe.saveGupixSpectra(lF.setExtension("ADC"+Integer.toString(indexOfADC+1)+".gup"));
          if (flags[18]==1){
              if (indexOfADC==16) pixe.saveGupixSpectra(lF.setExtension("ADC"+Integer.toString(indexOfADC+1)+".gup"));
          }
          if (flags[27]==1) pixe.saveXYEListFile(lF.setExtension("ADC"+Integer.toString(indexOfADC+1)+".PIXE"),(short)0);
          else if (flags[16]==1) pixe.saveXYEListFile(lF.setExtension("ADC"+Integer.toString(indexOfADC+1)+".pixe2"));
          //displays spectra
          if (flags[22]==1){
              if (flags[18]==1){
                  if (indexOfADC==16){
                      String title=lF.getPath()+" ADC: "+String.valueOf(indexOfADC+1)+": PIXE - N counts = " +String.valueOf(pixe.getNEvents());
                      plotSpectra(pixe.getSpectra(),title);
                  }
              }
              else if (flags[17]==1){
                  String title=lF.getPath()+" ADC: "+String.valueOf(indexOfADC+1)+": PIXE - N counts = " +String.valueOf(pixe.getNEvents());
                  plotSpectra(pixe.getSpectra(),title);
              }
          }
          if (flags[24]==1){
              double [] t=pixe.getSpectra();
              for (int i=0;i<4096;i++){
                  pixe_stack.get(indexOfADC)[i]+=t[i];
          }
                      
          }
          java.lang.System.gc();
  }
  /**
  * Actions performed when ADC corresponding to STIM is detected according to flags
  * @param adc ADC corresponding to STIM
  * @param lF name of processed list file
  * @param indexOfADC index of PIXE adc in MPA
  */
  private void processSTIM(ADC adc, listFiles lF, int indexOfADC){

      if((flags[21]==1)||(flags[25]==1)) adc.medianSort(); //map calculation
      if (flags[21]==1) adc.saveMedianTextImage(lF.setExtension("medMap.txt")); //saving map
      //if (flags[21]==1) adc.saveMedianImage(lF.setExtension("medMap.txt")); //saving map
      if (flags[25]==1) fillStack(adc,lF); //displaying map
      if (flags[20]==1) adc.saveCountsSpectra(lF.setExtension("stim.asc")); // save spectra
      //save XYE list file
      if (flags[27]==1) adc.saveXYEListFile(lF.setExtension("STIM"),(short)2); // save stim
      else if (flags[16]==1) adc.saveXYEListFile(lF.setExtension("stim2"));
      //Output: display spectra
      if (flags[22]==1){
          String title=lF.getPath()+" ADC: "+String.valueOf(indexOfADC+1)+": STIM - N counts = " +String.valueOf(adc.getNEvents());
          plotSpectra(adc.getSpectra(),title);
      }
      //Prefs ijPrefs=new Prefs();
      //ijPrefs.set(".convertListFiles.lastUsedFile", lf.getPath());
      
      java.lang.System.gc();
  }
  /**
  * This method adds median map to stim stack using
  * @param adc ADC to be added
  * @param lF name of the file
  */
  private void fillStack(ADC adc,listFiles lF){
      ImageProcessor ip = new ShortProcessor(sizeMapX-2, sizeMapY-1);  
      String title = lF.setExtension("");
      for (int x=2;x<sizeMapX;x++){
          for (int y=1;y<sizeMapY;y++){
              ip.set(x-2,y-1,adc.getMedianValue(x, y));
          }
      }
      stimStack.addSlice(title, ip);
  }

  /**
  * Processes all files in listFilesArray according to their type (RBS, STIM or PIXE)
  */
  public void process(){				
          reset_pixe_stack();
          reset_stim_stack();
          
          for (int indexOfFile=0;indexOfFile<listFilesArray.size();indexOfFile++){
              
              processFile(indexOfFile);
          }
          if (flags[24]==1){
              for (int i=0;i<16;i++){
                  if (flags[i]==3) writePixeStack(listFilesArray.get(0).setExtension("sum"+Integer.toString(i+1)),pixe_stack.get(i));
              }
              if (flags[18]==1) writePixeStack(listFilesArray.get(0).setExtension("sum"+"9"),pixe_stack.get(16));
          }
          if (flags[25]==1){
              try {
                  ImagePlus imp = new ImagePlus("Median maps", stimStack);
                  ContrastEnhancer ce=new ContrastEnhancer();
                  ce.stretchHistogram(imp, (double)0.35);
                  imp.show();
                  if (flags[23]==1) {
                      FileSaver fs = new FileSaver( imp) ;
                      Date d= new Date();
                      fs.saveAsTiff();
                  }
              }
              catch (Exception e){
              }
              
          }
          java.lang.System.gc();	
          IJ.showStatus("done");
  }

  /**
  * Processes selected file in listFilesArray
  * @param indexOfFile Index of file in listFilesArray
  */
  public void processFile(int indexOfFile){
        MPA3 mpa=listFilesArray.get(indexOfFile).readListFile(getActiveADCs());
        boolean debug = true;
        IJ.log("Reading file: "+ listFilesArray.get(indexOfFile).getPath());
        if (debug){
          int activeADCs[]=getActiveADCs();
          for (int i=0;i<activeADCs.length;i++){
              int indexOfAdc = activeADCs[i];
              int totPeriods= mpa.getADC(indexOfAdc).getNActivationPeriods();
              int inactivePeriodsCounter=0;
              for (int j=0;j<totPeriods;j++){
                  if (mpa.getADC(indexOfAdc).getActivationPeriod(j)==0)
                      inactivePeriodsCounter++;
              }
              float inactivePeriodsCounterfl=inactivePeriodsCounter;
              float tempsMort=100*inactivePeriodsCounterfl/totPeriods;
              
              if (indexOfAdc>7) IJ.log("Dead Time - ADC "+(indexOfAdc+1) +" = "+tempsMort +"%");
          }
        }
        for (int indexOfAdc=0;indexOfAdc<16;indexOfAdc++){
                switch (flags[indexOfAdc]){
                case 1: //RBS
                        processRBS(mpa.getADC(indexOfAdc),listFilesArray.get(indexOfFile), indexOfAdc);
                        break;
                case 2: //STIM
                        processSTIM(mpa.getADC(indexOfAdc),listFilesArray.get(indexOfFile), indexOfAdc);
                        break;
                case 3: //PIXE
                        if ((flags[17]==1)||(flags[16]==1)||(flags[22]==1)||(flags[24]==1)) processPIXE(mpa.getADC(indexOfAdc),listFilesArray.get(indexOfFile), indexOfAdc);
                        if (flags[18]==1) {
                            mpa.addToSum(indexOfAdc,16);
                            int lastPixeADCIndex=15;
                            while (flags[lastPixeADCIndex]!=3) lastPixeADCIndex-=1;
                            if (indexOfAdc==lastPixeADCIndex) processPIXE(mpa.getADC(16),listFilesArray.get(indexOfFile),16);
                        }
                        break;
                default: //other cases
                        break;
                }
        }
        IJ.log("Done.");
        java.lang.System.gc();
  }
  /**
  * Resets listFilesArray, list of files to be processed
  */
  public void reset(){
          listFilesArray.clear();
  }
  /**
  * Resets STIM image stack
  */
  private void reset_stim_stack(){
      for (int i=0;i<stimStack.getSize()+1;i++) stimStack.deleteLastSlice();
      
  }
  
  /**
   * Gets an arraylist containing the active ADCs 
   * @return arrayActivatedADCs arraylist containing the active ADCs
   */
  public int[] getActiveADCs(){
    ArrayList<Integer> listActivatedADCs = new ArrayList<>();
    for (int i=0;i<16;i++){
        if (flags[i]!=6)
            listActivatedADCs.add(i);
    }
    int[] arrayActivatedADCs = new int[listActivatedADCs.size()];
    for (int i=0;i<listActivatedADCs.size();i++){
        arrayActivatedADCs[i]=listActivatedADCs.get(i);
    } 
    return arrayActivatedADCs;
  }
  
  /**
  * Writes a pixe spectra stack
  * @param path
  * @param stack 
  */
  public void writePixeStack (String path, double [] stack){
      try{
                  PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path)));
                  out.println("4096 0");
                  for (int i=0;i<4096;i++) {
                          out.println(String.valueOf((int)stack[i]));
                  }
                  out.close();
          }
          catch (IOException e){
          }
  }
}


