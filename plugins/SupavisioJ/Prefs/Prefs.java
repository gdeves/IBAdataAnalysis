package SupavisioJ.Prefs;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.prefs.Preferences;


/**
 *
 * @author deves
 */
public class Prefs {
  public Preferences prefs;
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
}
