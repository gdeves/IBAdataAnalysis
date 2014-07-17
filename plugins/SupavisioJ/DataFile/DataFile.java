package SupavisioJ.DataFile;
import SupavisioJ.ConvertListFiles.ADC.ADC;

/**
 * DataFile class represents the file to be processed.
 * This class is defined by:
 * a path : absolute path towards the file
*/
public abstract class DataFile {
    protected String filePath;
 

    public String getPath(){
        return filePath;
    }

    public void setPath(String path){
        filePath=path;
    }
    
    
    /**
     * @return path to the parent directory and the file name without its extention. 
     */   
    public String getDirectoryAndName(){
        return filePath.substring(0,filePath.lastIndexOf("."));
    }
    
    public String getName(){
        int index=filePath.lastIndexOf("/")+1;
        if (index==0)
            index=filePath.lastIndexOf("\\")+1;
        return filePath.substring(index,filePath.lastIndexOf("."));
    }
    
    /**
     * @return an ADC which contains all the events stored in the current file.
     */
    public abstract ADC open();
}

