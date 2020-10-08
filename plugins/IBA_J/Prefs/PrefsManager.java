package IBA_J.Prefs;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.prefs.Preferences;
import ij.*;


/**
 * Class to handle preferences
 * @author deves
 * 
 */
public class PrefsManager {
  public Preferences prefs;
  public Prefs ijPrefs;
  
  /**
   * Set preference
   */
  public void setPreference() {
     prefs = Preferences.userRoot().node(this.getClass().getName());
  }
  //Setters
  
  /**
   * Method to save a string value to preferences file
   * @param key
   * @param value 
   */
  public void saveValue(String key, String value){
      prefs.put(key,value);
  }
  /**
   * Method to save an integer value to preferences file
   * @param key
   * @param value 
   */
  public void saveValue(String key, int value){
      prefs.putInt(key,value);
  }
  /**
   * Method to save a boolean value to preferences file
   * @param key
   * @param value 
   */
  public void saveValue(String key, boolean value){
      prefs.putBoolean(key,value);
  }
  /**
   * Method to save the last used directory
   * @param value 
   */
  public void saveDirectory(String value){
      prefs.put("LAST_USED_DIR",value);
  }
  public void saveLastUsedFile(String value){
      prefs.put("LAST_USED_FILE",value);
  }
  /**
   * Method to modify a String value to preferences file
   * @param key
   * @param value 
   */
  public void ijPrefsSaveValue(String key, String value){
      Prefs.set(key,value);
  }
  /**
   * Method to modify an integer value to preferences file
   * @param key
   * @param value 
   */
  public void ijPrefsSaveValue(String key, int value){
      Prefs.set(key,value);
  }
  /**
   * Method to modify a boolean value to preferences file
   * @param key
   * @param value 
   */
  public void ijPrefsSaveValue(String key, boolean value){
      Prefs.set(key,value);
  }
  /**
   * Method to modify the last used directory
   * @param value 
   */
  public void ijPrefsSaveDirectory(String value){
      Prefs.set("IBA.LAST_USED_DIR",value);
  }
  /**
   * Method to save states (checked/unchecked) of ROI fields
   * @param states 
   */
  public void ijPrefsSaveStates(boolean[] states){
      int nROI=states.length;
      for (int i=0;i<nROI;i++){
          ijPrefsSaveValue("IBA.roi"+String.valueOf(i+1)+".isActive", states[i]);
      }
  }
  
  
  //Getter
  /**
   * Method to retrieve an integer value from preferences
   * @param key
   * @param def
   * @return 
   */
  public int getValue(String key, int def){
      return prefs.getInt(key,def);
  }
  /**
   * Method to retrieve a boolean value from preferences
   * @param key
   * @param def
   * @return 
   */
  public boolean getValue(String key, boolean def){
      return prefs.getBoolean(key,def);
  }
  /**
   * Method to retrieve a string value from preferences
   * @param key
   * @param def
   * @return 
   */
  public String getValue(String key, String def){
      return prefs.get(key,def);
  }
  /**
   * Method to retrieve the last used directory
   * @return last used directory
   */
  public String getLastUsedDirectory(){
      return prefs.get("LAST_USED_DIR", System.getProperty("user.dir"));
  }
    public String getLastUsedFile(){
      return prefs.get("LAST_USED_FILE","");
  }
  /**
   * Method to retrieve an integer value from preferences
   * @param key
   * @param def
   * @return 
   */
  public int ijGetIntValue(String key, int def){
      return ijPrefs.getInt(key,def);
  }
  /**
   * Method to retrieve a bollean value from preferences
   * @param key
   * @param def
   * @return 
   */
  public boolean ijGetBoolValue(String key, boolean def){
      return Boolean.valueOf(ijPrefs.getBoolean(key,def));
  }
  /**
   * Method to retrieve a string value from preferences
   * @param key
   * @param def
   * @return 
   */
  public String ijGetValue(String key, String def){
      return ijPrefs.get(key,def);
  }
  /**
   * Method to retrieve the last used directory
   * @return 
   */
  public String ijGetLastUsedDirectory(){
      return ijPrefs.get("IBA.LAST_USED_DIR", System.getProperty("user.dir"));
  }
  /**
   * Method to retrieve the mode to build roi map, eg. using manual X,Y limits
   * @return 
   */
    public boolean getManualROIState(){
      return ijGetBoolValue(".IBA.roimap.manualEnabled",false);
  }
  /**
   * Method to retrieve the specified ROI field state
   * @param roi
   * @return 
   */
  public boolean getROIState(int roi){
      return ijGetValue("IBA.roi"+String.valueOf(roi)+".isActive","").equals("true");
  }
  /**
   * Method to retrieve all roi fields states
   * @return 
   */
  public boolean[] getROIStates (){
      int nROI=ijGetIntValue("IBA.nROI",0);
      boolean [] activeROIs=new boolean[nROI];
      for (int i=0;i<nROI;i++){
        activeROIs[i]=ijGetBoolValue("IBA.roi"+String.valueOf(i)+".isActive",false);  
      }
      return activeROIs;
  }
  /**
   * Method to retrieve the number of saved ROIs in prefs
   * @return number of ROIS
   */
  public int getNroi(){
      return ijGetIntValue("IBA.nROI",0);
  }
}
