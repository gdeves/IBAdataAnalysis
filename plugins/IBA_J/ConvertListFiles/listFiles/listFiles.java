package IBA_J.ConvertListFiles.listFiles;

import IBA_J.ConvertListFiles.ADC.ADC;
import java.io.*;
import IBA_J.ConvertListFiles.MPA3.MPA3;
import ij.*;

/**
* listFiles is the class representing an input file of type *.lst.
* A file *.lst will contains a header, event tags and timer tags.
* The timer tags allow to know when an ADC can receive a signal
* The event tags indicate if ADCs have received a signal and information corresponding to follows
*/

public class listFiles{
  //declaration
  private String path;
  private int adcIndexScanX;
  private int adcIndexScanY;
  private int adcRangeX, adcRangeY;


  //constructors
  /**
  * Default constructor
  */
  public listFiles(){
  }
  /**
  * Constructor with initialization of pathname
  * @param path absolute path of the file
  */
  public listFiles(String path){
    this.path=path;
  }

  /**
  * Constructor with initialization of pathname
  * @param adcIndexScanX the ADC index for scanning X
  * @param adcIndexScanY the ADC index for scanning Y
  */
  public listFiles(int adcIndexScanX, int adcIndexScanY){
    this.adcIndexScanX=adcIndexScanX;
    this.adcIndexScanY=adcIndexScanY;
  }

  /**
  * Constructor with initialization of pathname
  * @param path absolute path of the file
  * @param adcIndexScanX the ADC index for scanning X
  * @param adcIndexScanY the ADC index for scanning Y	  
  */
  public listFiles(String path, int adcIndexScanX, int adcIndexScanY){
	  this.path=path;
	  this.adcIndexScanX=adcIndexScanX;
	  this.adcIndexScanY=adcIndexScanY;
  }
  
  /**
   * This method returns true if the reading has reached the end of the header
   * @param ips the open file to check
     * @return a state indicating if file header has been read or not
     * @throws java.lang.Exception
   */
  public boolean isReadingHeader(DataInputStream ips) throws Exception {
    boolean flag=true;
    //Discards header searching for '[Listdata]'
      if (ips.readUnsignedByte()==91 && ips.readUnsignedByte()==76 && ips.readUnsignedByte()==73 && ips.readUnsignedByte()==83 && ips.readUnsignedByte()==84){
	  if (ips.readUnsignedByte()==68 && ips.readUnsignedByte()==65 && ips.readUnsignedByte()==84 && ips.readUnsignedByte()==65 && ips.readUnsignedByte()==93){
	      flag=false;
	  }
      }
      
      return flag;
  }
  
  

  
  public void ReadHeader(BufferedReader br, int ADC_X, int ADC_Y) throws Exception {
    //Reading X range
    String word="[ADC"+Integer.toString(ADC_X)+"]";
    String line=br.readLine();
    while (!line.equals(word)){
        line=br.readLine();
    }
    line=br.readLine();
    
    while (!(line.substring(0, 5)).equals("range")){
        line=br.readLine();
    }
    setXRange(Integer.parseInt(line.substring(6)));
     IJ.log("X range=" +line.substring(6));
    
    //Reading Y range
    word="[ADC"+Integer.toString(ADC_Y)+"]";
    while (!line.equals(word)){
        line=br.readLine();
    }
    line=br.readLine();
    
    while (!(line.substring(0, 5)).equals("range")){
        line=br.readLine();
    }
    setYRange(Integer.parseInt(line.substring(6)));
     IJ.log("Y range=" +line.substring(6));
     
  }
  
  /**
  * Sorts a list file
  * @param arrayOfActiveADC contains the index of actives/functionning ADCs
  * @return  A MPA3 object containing a list of ADC. Each event is stored in its corresponding ADC
  */  
  public MPA3 readListFile(int[] arrayOfActiveADC){
	
        MPA3 mpa=new MPA3();
	  try{
		  BufferedReader br = new BufferedReader(new FileReader(path));
                  DataInputStream ips=new DataInputStream(new BufferedInputStream(new FileInputStream(path)));  
		  ReadHeader(br,adcIndexScanX+1,adcIndexScanY+1);
                  while(isReadingHeader(ips)){
		   
                  //while(isReadingHeader(br)){
                      //TODO : get useful informations from header as date or calibration
		  }
		  
		  ips.readUnsignedByte();
		  
		  //Reset timer tags counter
		  int timerTag=0;
		  int eventTag=0;
		  int SynTag=0;
		  int zevt=0;
		  int tmp=0;
                  
		  
                  
		  // b is a 4 bytes word
		  // b[0] is the high word and b[3] the lowest
		  // Dummy Word is 0xFFFFFFFF
		  //Timer Tag is 0x4000 ie 0100 0000 0000 0000 xxxx xxxx xxxx xxxx
		  // xxxx xxxx xxxx xxxx represent active ADC (bit=1) and inactive ADC (bit=0)
		  // ADC[1] corresponds to bit 0
		  int [] b=new int[4];
		  try{
                  	                  
                          b[0]=ips.readUnsignedByte();
			  //loop until end of file is reached (b=-1)
                                                    //while (subFile<36000){subFile+=1;}
			  while (b[0]>-1){
                          //while (b[0]>-1{   
                           //Read 4 bytes and fills b[] tab, reversing order to switch from little endian to big endian order
				  for (int i=0;i<4;i++){
					  b[3-i]=ips.readUnsignedByte();
				  }
				  //Counting the number of timer tags
				  switch (checkBit(b[0],6)){
					  //Timer tag or Synchronize mark
					  case 1:
						  switch (checkBit(b[0],7) ){
							  //Synchronize mark
							  case 1:
							    SynTag+=1;
							  break;
							  //Timer tag
							  case 0:
							    timerTag+=1;
							    int[] tmtagPerADC=new int[16];
							    for (int i=0;i<8;i++){
							      tmtagPerADC[i]=checkBit(b[3],i);
							    }
							    for (int i=0;i<8;i++){
							      tmtagPerADC[i+8]=checkBit(b[2],i);
							    }
                                                            for (int i=0;i<arrayOfActiveADC.length;i++){
                                                                int indexOfADC = arrayOfActiveADC[i];
                                                                ADC adcToComplete= mpa.getADC(indexOfADC);
                                                                adcToComplete.addPeriod(tmtagPerADC[indexOfADC]);
                                                            }
							  break;
						  }
					  break;
					  //Event tag
					  case 0:
						  
                                                  eventTag+=1;
						  //dummy word
						  if (checkBit(b[0],7)==1) ips.readShort();
						  int [] evt= new int[16];
                                                  for (int index=0;index<evt.length;index++) evt[index]=-1;
						  for (int i=0;i<8;i++){
							  if (checkBit(b[3],i)==1) evt[i]=safeBytesToInt(ips.readUnsignedByte(),ips.readUnsignedByte());
						  }
						  for (int i=0;i<8;i++){
							  if (checkBit(b[2],i)==1) evt[i+8]=safeBytesToInt(ips.readUnsignedByte(),ips.readUnsignedByte());
						  }
                                                  //Skipping n=000 events occuring at the beggining
                                                   if (eventTag>000){
                                                    zevt+=sortEvents(mpa,evt);
                                                  }
                                                  //if (timerTag>108000 & timerTag<112000) zevt+=sortEvents(mpa,evt);
                                                  
					  break;
				  }
                                  
                          }
			  			
		  }
		  catch (EOFException e){
		  }
		  ips.close(); 
                  IJ.log("Timer tags = "+ timerTag);
                  
                  
                  
                  
	  }
	  catch (Exception e){
	  }
	  return mpa;
        
  }

  //getters
  /**
   * 
   * @return path for this listfile
   */
  public String getPath(){
	  return path;
  }
  /**
   * Get range for X axis
   * @return 
   */
  public int getRangeX(){
      return adcRangeX;
  }
  /**
   * Get range for Y axis
   * @return 
   */
  public int getRangeY(){
      return adcRangeY;
  }
  /**
   * 
   * @param path to set 
   */
  public void setPath(String path){
	  this.path=path;
  }
  /**
   * Set the range for X axis
   * @param adcRangeX 
   */
  public void setXRange (int adcRangeX){
      this.adcRangeX=adcRangeX;
  }
  /**
   * Set the range for Y axis
   * @param adcRangeY 
   */
    public void setYRange (int adcRangeY){
      this.adcRangeY=adcRangeY;
  }
  
  /**
   * This method return an absolute path. This method can be used if you want to save a file in the directory of the lst file
   * @param ext An extension to add to the return String
   * @return absolute path of the parent directory of the current file concatened with his name and a new extension
   */
  public String setExtension(String ext){
	  String name=getPath().substring(0,getPath().lastIndexOf("."));
	  return name+"."+ext;
  }	
  //Reading tags for listfiles
  private boolean checkTimerTag(int thisByte){
      return checkBit(thisByte,6)==1;
  }
  private boolean checkEventTag(int thisByte){
      return checkBit(thisByte,6)==0;

  }
  private boolean checkDummyWord (int thisByte){
      return thisByte>0x80;
  }
  
  /**
   * Use this method to know if an ADC is active
   * @param thisByte byte to check
   * @param position index of ADC to check (between 0 and 8)
   * @return 0 or 1. 1 correspond to an active ADC.
   */
  private int checkBit(int thisByte, int position){
	  if (((thisByte>>position)&0x1)==1) return 1;
	  else return 0;

  }

  //conversion of bytes to int
  private int bytesToInt(int byte1, int byte2){
	  return ((byte2<<8)|byte1);
  }

  //conversion of bytes to int
  private int safeBytesToInt(int byte1, int byte2){
      
      return ((byte2<<8)|byte1)&0xFFF;
  }


  /**
   * This method will write into the ADCs of the MPA3 the information concerning each event
   * @param mpa object MPA3 to complete
   * @param evt information coming from one event tag concerning all ADCs of the MPA3
   */
  private int sortEvents(MPA3 mpa, int [] evt){
	  int ct=0;
	  for (int i=0;i<16;i++){
	      if (i!=(adcIndexScanX) & i!=(adcIndexScanY)){
                    //replace condition for coincidence on adc 15 and 16
                  
                    //Condition to remove defective' channels
                    //if (evt[i]>0 & evt[i]!=192 & evt[i]!=448 & evt[i]!=704 & evt[i]!=3839 & evt[i]!=255 & evt[i]!=512 & evt[i]!=1024 & evt[i]!=4095){
                          if (evt[i]>-1){
                          //if (evt[14]>0 & evt[15]>0){
                        if (evt[adcIndexScanY]<getRangeY() & evt[adcIndexScanX]<getRangeX()){
			  int [] event=new int[3];
			  //Y value, X value, Energy
			  event[1]=evt[adcIndexScanX];
			  event[0]=evt[adcIndexScanY];
			  event[2]=evt[i];
			  if (event[2]>0){
                              mpa.getADC(i).addEvent(event);
                              ct+=1;
                          }
                        
			  
			  
                            //test for extreme values 4095 instead of 1024                          
                            //if (i>7) IJ.log("voie :" + i + " X-Y-E = "+ event[0] +"-"+ event[1]+"-"+ event[2]);
                    }
                    }
	      }
	  }
          
	  if (ct>0) return 0;
	  else return 1;
  }

}