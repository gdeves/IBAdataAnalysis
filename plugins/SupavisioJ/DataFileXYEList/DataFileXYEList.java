package SupavisioJ.DataFileXYEList;

import ij.*;
import java.io.*;
import SupavisioJ.ConvertListFiles.ADC.ADC;
import SupavisioJ.DataFile.DataFile;

/**
 *This class represents a file which can be a pixe, stim, rbs file or any other file containing events(x,y,E) in a binary format
 */
public class DataFileXYEList extends DataFile {
    private ADC adc=new ADC();
    
    public DataFileXYEList(String path){
      filePath=path;
    }
    
    public ADC open(){
      DataInputStream ips=null;
      try{
	ips=new DataInputStream(new BufferedInputStream(new FileInputStream(filePath))); 
        adc.restoreXYEListFile(ips);
      }
      catch (FileNotFoundException e){
          IJ.log("File not found");
      }
      if(ips!=null){
        try{ips.close();}
        catch (IOException e2){
            IJ.log("Fail to open the file "+filePath);
        }
      }
      return adc;
    }
}
