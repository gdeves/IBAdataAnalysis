package IBA_J.MainFrame;

import IBA_J.ConvertListFiles.ADC.ADC;
import IBA_J.ConvertListFiles.FrameC.FrameC;
import IBA_J.FrameConfigSave.FrameConfigSave;
import IBA_J.FrameConfigLang.FrameConfigLang;
import IBA_J.Prefs.PrefsManager;
import IBA_J.Spectra.Spectra;
import ij.IJ;
import java.awt.HeadlessException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *This class is the first window which will be open at the launch of IBA_J
 */
public final class MainFrame extends javax.swing.JFrame {
    private FrameConfigLang frameConfigLang= null;
    private final FrameC frameConfigLst = new FrameC();
    private final ArrayList<Spectra> spectrasProduced = new ArrayList<>();
    private final String nameOfApplication = "Ion Beam Data Analysis";
    private static ArrayList<String> availableLanguages = new ArrayList<>();
    private static ArrayList<String[]> languageData = new ArrayList<>();

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {        
        availableLanguages = new ArrayList<>();
        languageData = new ArrayList<>();                
        searchAvailableLanguages();
        String language = chooseLanguage();
        initComponents();
        chooseIconLanguage(language);
        String languageName=null;
        try{
            languageName = readLanguageName("plugins/IBA_J/resources/language/"+language+".txt");
        }
        catch(IOException e){}
        frameConfigLang=new FrameConfigLang(this,languageName);
        this.setIconImage(new ImageIcon(getClass()
                .getResource("/IBA_J/resources/images" + "/atome-16.png")).getImage());
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButtonOpenLst = new javax.swing.JButton();
        jButtonOpenXYEList = new javax.swing.JButton();
        jButtonParamLst = new javax.swing.JButton();
        jButtonParamPIXE = new javax.swing.JButton();
        jButtonLanguage = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("IBA analysis - v03-2017-1");

        jButtonOpenLst.setText(translate("Handle raw listfiles"));
        jButtonOpenLst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOpenLstActionPerformed(evt);
            }
        });

        jButtonOpenXYEList.setText(translate("Work with ADC listfile"));
        jButtonOpenXYEList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOpenXYEListActionPerformed(evt);
            }
        });

        jButtonParamLst.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IBA_J/resources/images/avance-parametres-32.png"))); // NOI18N
        jButtonParamLst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonParamLstActionPerformed(evt);
            }
        });

        jButtonParamPIXE.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IBA_J/resources/images/avance-parametres-32.png"))); // NOI18N
        jButtonParamPIXE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonParamPIXEActionPerformed(evt);
            }
        });

        jButtonLanguage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IBA_J/resources/language/source.png"))); // NOI18N
        jButtonLanguage.setBorderPainted(false);
        jButtonLanguage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLanguageActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addComponent(jButtonOpenXYEList, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonParamPIXE, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(49, 49, 49)
                                .addComponent(jButtonOpenLst, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jButtonLanguage, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(jButtonParamLst, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(53, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonLanguage, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonOpenLst)
                    .addComponent(jButtonParamLst, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonOpenXYEList)
                    .addComponent(jButtonParamPIXE, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonOpenLstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOpenLstActionPerformed
        frameConfigLst.openLST();
    }//GEN-LAST:event_jButtonOpenLstActionPerformed

    private void jButtonParamLstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonParamLstActionPerformed
        frameConfigLst.setVisible(true);
    }//GEN-LAST:event_jButtonParamLstActionPerformed

    private void jButtonOpenXYEListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOpenXYEListActionPerformed
      
      String path=selectFile();
      DataInputStream ips=null;
      PrefsManager prefs=new PrefsManager();
      try{
	ips=new DataInputStream(new BufferedInputStream(new FileInputStream(path))); 
         }
      catch (FileNotFoundException e){
          IJ.log("**Error** File was not found");
      }
        ADC adc=new ADC(path);
        if (path.substring(path.length()-1).equals("2")){
            File f=new File(path);
            IJ.log("Opening file : " + f.getName());
            adc.open(ips);
        }
        else {
            IJ.log("**Error** Wrong file format");
            
        }
        try {
            if (ips != null) ips.close(); 
        }
        catch(IOException e){
            IJ.log("**Error ** " + e.toString());
        }
            

        //if ( adc.getNEvents()>1 && (adc.getlastEvent()[0]!=0 && adc.getlastEvent()[1]!=0) ){//check if a correct file has been open
        if ( adc.getNEvents()>1){
            Spectra spectraXYE= new Spectra(adc,path);
            if(spectraXYE.getEnergies().length>1){//check if a correct file has been open
                spectraXYE.setParentWindow(this);
                File f=new File(path);
                int nROI=Integer.valueOf(prefs.ijGetValue("IBA.nROI", ""+5));
                spectraXYE.plot(nameOfApplication, (String) translate("File: ")+f.getName(),nROI).showVisible();
                IJ.log("Total events: " + spectraXYE.getADC().getNEvents());
            }
        }
        java.lang.System.gc();
    }//GEN-LAST:event_jButtonOpenXYEListActionPerformed

    private void jButtonParamPIXEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonParamPIXEActionPerformed
        IJ.open("IJ_Prefs.txt");
    }//GEN-LAST:event_jButtonParamPIXEActionPerformed

    private void jButtonLanguageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLanguageActionPerformed
        frameConfigLang.setVisible(true);
    }//GEN-LAST:event_jButtonLanguageActionPerformed

    
    
    
    private void searchAvailableLanguages(){
        final File folder = new File("plugins/IBA_J/resources/language");
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.getName().endsWith("txt")){
                try{
                    String name = readLanguageName(file.getAbsolutePath());
                    if (name!=null){
                       availableLanguages.add(name); 
                    }
                }
                catch(IOException e){}
            }
        }
    }
    
    private String chooseLanguage(){
        try{
            String[] lines =readLinesFile("plugins/IBA_J/resources/language/default");
            if (!lines[0].contains("english")&&!lines[0].contains("source")){
                String[] linesSource=readLinesFile("plugins/IBA_J/resources/language/source.txt");
                String[] linesTranslated=readLinesFile("plugins/IBA_J/resources/language/"+lines[0]+".txt");
                languageData.add(linesSource);
                languageData.add(linesTranslated);
                return lines[0];
            }
            else{
                return lines[0];
            }
        }
        catch(IOException e){}
        return null;
    }
    
    private void chooseIconLanguage(String language){
        jButtonLanguage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/IBA_J/resources/language/"+language+".png")));
    }
    
    /**
     * Method to get the available languages
     * @return available languages
     */
    public String[] getLanguages(){
        return availableLanguages.toArray( new String[availableLanguages.size()] );
    }
    
    /**
     * Method to retrieve available languages
     * @param languageName
     * @return available languages
     */
    public String getNameFileLang(String languageName){
        final File folder = new File("plugins/IBA_J/resources/language");//");
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.getName().endsWith("txt")){
                try{
                    String name = readLanguageName(file.getAbsolutePath());
                    if (name!=null && name.equals(languageName)){
                        String fileName = file.getName();
                        return fileName.substring(0,fileName.lastIndexOf("."));
                    }
                }
                catch(IOException e){}
            }
        }
        return "source";
    }
    /**
     * 
     * @param dataToTranslate
     * @return a translated string
     */
    public static String translate(String dataToTranslate){
        if(dataToTranslate!=null){
            if(languageData.size()>1){
                String[] source=languageData.get(0);
                for (int i=0;i<source.length;i++){
                    String lineSource=source[i];
                    if (lineSource.equals(dataToTranslate)){
                        String[] translate = languageData.get(1);
                        String line = translate[i];
                        return line;
                    }
                }
            }
        }
        return dataToTranslate;
    }
    
    /**
     * Method to retrieve used language
     * @param path
     * @return used language
     * @throws IOException 
     */
    public String readLanguageName(String path)throws IOException{
        BufferedReader buff=null;
        String languageName= null;
        try {
          buff=buff = new BufferedReader(new FileReader(path));//file opening
          for (int i=0;i<3;i++) {
            if(i==2){
                languageName = buff.readLine();
            }
            else{
                buff.readLine();
            }
          }
          buff.close();
        }	
        catch(FileNotFoundException e){
          IJ.log(translate("**Error** Language file not found or invalid"));
        }
        catch (NullPointerException e){//end of the file
          buff.close();
        }
        return languageName;
    }
  
    /**
     * Method to read a text file
     * @param path of the file
     * @return return an array containing the lines of file. "\n" has been removed.
     * @throws IOException
     */
    public String[] readLinesFile(String path)throws IOException{
        BufferedReader br=null;
        ArrayList<String> arrayLines=new ArrayList<>();
        try {
          br = new BufferedReader(new FileReader(path));//file opening
          for (int i=0;;i++) {
            String line = br.readLine();
            arrayLines.add(line);
            if (line.equals(null) | line.equals("\n")){//end of the file can produced a NullPointerException
              arrayLines.remove(arrayLines.size()-1);
              br.close();
              break;
            }
          }
        }	
        catch(FileNotFoundException e){
          IJ.log(translate("**Error** File not found."));
        }
        catch (NullPointerException e){//end of the file
          arrayLines.remove(arrayLines.size()-1);
          br.close();
        }
        String[] tabLines =  new String[arrayLines.size()];//convert arrayList to String[]
        for (int i=0;i<arrayLines.size();i++){
          tabLines[i]= ((String)arrayLines.get(i));
        }
        return tabLines;
    }
    
    /**
     * Method to select one and only one file
     * @return the absolute path of the file
     */
    private String selectFile(){
        File selectedFile = null;
        PrefsManager  prefs=new PrefsManager();
        //prefs.setPreference();
        try{
          JFileChooser jF = new JFileChooser();
          File myDir=new File(prefs.ijGetLastUsedDirectory());
          jF.addChoosableFileFilter(new FileNameExtensionFilter("PIXE AIFIRA file", "pixe2"));
          jF.addChoosableFileFilter(new FileNameExtensionFilter("RBS AIFIRA file", "rbs2"));
          jF.setFileFilter(jF.getChoosableFileFilters()[1]);

          jF.setCurrentDirectory(myDir);
          jF.setApproveButtonText(translate("OK")); 
          jF.setMultiSelectionEnabled(false);
          //FileFilter filter = new FileNameExtensionFilter("PIXE AIFIRA file", "pixe2");
          //jF.setFileFilter(filter);
          jF.showOpenDialog(null); 

          selectedFile = jF.getSelectedFile();
          prefs.ijPrefsSaveDirectory(selectedFile.getAbsolutePath());
          
        }
        catch (HeadlessException e){
          IJ.log(translate("**Error** Can not open file"));
        }
        if (selectedFile!=null)
            return selectedFile.getAbsolutePath();
        return null;        
    }
    
    /**
     * Method to select one or several files
     * @return array containing the absolute path of the files
     */
    private String[] selectFiles(){
    PrefsManager  prefs=new PrefsManager();    
    //prefs.setPreference();
        
        File[] selectedFiles = null;
        try{
          JFileChooser jF = new JFileChooser();
          File myDir=new File(prefs.ijGetLastUsedDirectory());
          jF.setApproveButtonText(translate("OK")); 
          jF.setMultiSelectionEnabled(true);

          jF.showOpenDialog(null); 
          selectedFiles = jF.getSelectedFiles();
          prefs.ijPrefsSaveDirectory(jF.getName());
        }
        catch (HeadlessException e){
          IJ.log(translate("**Error** Can not open files"));
        }
        String[] pathsToReturn = null;
        if (selectedFiles!=null){
            pathsToReturn = new String[selectedFiles.length];
            for(int i=0;i<selectedFiles.length;i++){
                pathsToReturn[i]=selectedFiles[i].getAbsolutePath();
                
            }
        }
        return pathsToReturn; 
    }
    
    /**
     * Method to select one and only one directory
     * Files are showed to show context to the user but can not be selected
     * @return the absolute path of the directory
     */
    private String selectDirectory(){
        File selectedFile = null;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int option = fileChooser.showDialog(null,translate("Choose a directory"));
        if (option == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
             // if the user accidently click a file, then select the parent directory.
            if (!selectedFile.isDirectory()) {
                selectedFile = selectedFile.getParentFile();
            }
        }
        if (selectedFile!=null)
            return selectedFile.getAbsolutePath()+"/";
        return null;
    }
    
    /**
     * Add a Spectra : useful for saving all the Spectras when saving a session 
     * @param spectra
     */
    public void addSpectra(Spectra spectra){
        spectrasProduced.add(spectra);
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonLanguage;
    private javax.swing.JButton jButtonOpenLst;
    private javax.swing.JButton jButtonOpenXYEList;
    private javax.swing.JButton jButtonParamLst;
    private javax.swing.JButton jButtonParamPIXE;
    // End of variables declaration//GEN-END:variables
}
