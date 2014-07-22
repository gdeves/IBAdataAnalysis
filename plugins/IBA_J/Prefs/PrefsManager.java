package IBA_J.Prefs;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.prefs.Preferences;
import ij.*;


/**
 *
 * @author deves
 */
public class PrefsManager {
  public Preferences prefs;
  public Prefs ijPrefs;
  public void setPreference() {
     prefs = Preferences.userRoot().node(this.getClass().getName());
  }
  //Setters
  public void saveValue(String key, String value){
      prefs.put(key,value);
  }
  public void saveValue(String key, int value){
      prefs.putInt(key,value);
  }
  public void saveValue(String key, boolean value){
      prefs.putBoolean(key,value);
  }
  public void saveDirectory(String value){
      prefs.put("LAST_USED_DIR",value);
  }
  public void ijPrefsSaveValue(String key, String value){
      Prefs.set(key,value);
  }
  public void ijPrefsSaveValue(String key, int value){
      Prefs.set(key,value);
  }
  public void ijPrefsSaveValue(String key, boolean value){
      Prefs.set(key,value);
  }
  public void ijPrefsSaveDirectory(String value){
      Prefs.set("IBA.LAST_USED_DIR",value);
  }
  
  
  //Getter
  public int getValue(String key, int def){
      return prefs.getInt(key,def);
  }
  public boolean getValue(String key, boolean def){
      return prefs.getBoolean(key,def);
  }
  public String getValue(String key, String def){
      return prefs.get(key,def);
  }
  public String getLastUsedDirectory(){
      return prefs.get("LAST_USED_DIR", System.getProperty("user.dir"));
  }
  public int ijGetIntValue(String key, int def){
      return Prefs.getInt(key,def);
  }
  public boolean ijGetBoolValue(String key, boolean def){
      return Prefs.getBoolean(key,def);
  }
  public String ijGetValue(String key, String def){
      return Prefs.get(key,def);
  }
  public String ijGetLastUsedDirectory(){
      return Prefs.get("IBA.LAST_USED_DIR", System.getProperty("user.dir"));
  }
}
