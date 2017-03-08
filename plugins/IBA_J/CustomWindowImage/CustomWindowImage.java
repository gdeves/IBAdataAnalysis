package IBA_J.CustomWindowImage;

import IBA_J.GeneratedMap.GeneratedMap;
import IBA_J.Spectra.Spectra;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.StackWindow;
import ij.gui.YesNoCancelDialog;
import ij.io.FileSaver;
import ij.macro.Interpreter;
import ij.plugin.frame.RoiManager;
import IBA_J.Prefs.PrefsManager;

import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseWheelEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class CustomWindowImage extends StackWindow implements ActionListener,AdjustmentListener{
    
    private Button buttonCalc;
    private Button buttonSave;
    private Button buttonSaveAll;
    private JLabel imageName;
    private JTextField nameRoiField;
    private final GeneratedMap[] selectedImages;
    private RoiManager manager;

    // constructor
    public CustomWindowImage(ImagePlus imp, GeneratedMap[] selectedImages) {
        super(imp);
        this.selectedImages = selectedImages;
        setLayout(new FlowLayout());
        if (selectedImages.length>1){
            remove(zSelector);
        }
        addPanel();
        manager = RoiManager.getInstance();
        if (manager==null) manager=new RoiManager();
    }   

    /**
     * This method will add a panel containing buttons to the window
     */
    // panel with image and buttons layout type for the image generated window
    private void addPanel() {
        Panel panel = new Panel();
        int nbFields=6;
        if (selectedImages.length>1)
            nbFields=7;
        panel.setLayout(new GridLayout(nbFields,1));
        nameRoiField = new JTextField();
        nameRoiField.setText(tr("ROI name"));
        buttonCalc = new Button(tr("Calculate spectra"));
        buttonCalc.addActionListener(this);
        buttonSave = new Button("Calculate all spectra");
        buttonSave.addActionListener(this);
        buttonSaveAll = new Button(tr("Save All"));
        buttonSaveAll.addActionListener(this);
        imageName = new JLabel();
        imageName.setText(selectedImages[0].getTitle());
        panel.add(imageName);
        if (selectedImages.length>1){
            panel.add(sliceSelector);
            sliceSelector.addAdjustmentListener(this); 
        }
        panel.add(new Label(""));
        //panel.add(nameRoiField);
        panel.add(buttonCalc);
        panel.add(buttonSave);
        panel.add(buttonSaveAll);
        add(panel);
        pack();  
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        RoiManager manager = RoiManager.getInstance();
        Object b = e.getSource();
        int z=0;
        if(selectedImages.length>1)
            z = zSelector.getValue()-1; 
        
        if (b==buttonCalc) {
            manager.add(imp,imp.getRoi(),manager.getCount()+1);
            IJ.log("calculating spectra from ROI "+manager.getName(manager.getCount()-1));
            Spectra roiSpectra=selectedImages[0].generateSpectraFromRoi();
            roiSpectra.setFilename(roiSpectra.getPath(manager.getName(manager.getCount()-1)));
            roiSpectra.plotSpectra((String)"Spectra from ROI: "+manager.getName(manager.getCount()-1), (String) tr("Datafile")+" "+roiSpectra.getPath()).showVisible();
        }
        if (b==buttonSave) {
                           
            for (int index=0; index<manager.getCount();index++){
                manager.select(index);
                IJ.log("calculating spectra from ROI "+manager.getName(index));
                Spectra roiSpectra=selectedImages[0].generateSpectraFromRoi();
                roiSpectra.setFilename(roiSpectra.getPath(manager.getName(index)));
                roiSpectra.plotSpectra((String)"Spectra from ROI: "+manager.getName(index), (String) tr("Datafile")+" "+roiSpectra.getPath()).showVisible();
                roiSpectra.getADC().saveGupixSpectra(roiSpectra.getPath()+".gup");
            
            
            } 
        }
        if (b==buttonSaveAll) {
            //save all images generate from parent Spectra
            String directory=selectDirectory();
            if (directory!=null)
                selectedImages[z].saveAll(directory);
        }
        ImageCanvas imageCanvas = imp.getCanvas();
        if (imageCanvas!=null)
            imageCanvas.requestFocus();
    }
    
    @Override
    public synchronized void adjustmentValueChanged(AdjustmentEvent adjEv) { 
        int z = zSelector.getValue(); 
        String currentName = selectedImages[z-1].getTitle();
        imageName.setText(currentName);
        super.adjustmentValueChanged(adjEv);
    } 
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent mWEvent) {
        super.mouseWheelMoved(mWEvent);
        if(selectedImages.length>1){
            int z = zSelector.getValue()-1; 
            String currentName = selectedImages[z].getTitle();
            imageName.setText(currentName);
        }
        updateStatusbarValue();
    }

    @Override
    public void mouseMoved(int x, int y) {
        int z=0;
        if(selectedImages.length>1)
            z = zSelector.getValue()-1; 
        IJ.showStatus(imp.getLocationAsString(x+1,y+1) + selectedImages[z].getValueAsString(x,y));
	savex=x; savey=y;
    }
	
    private int savex, savey;
    
    /**
     * Redisplays the (x,y) coordinates and pixel value (which may have changed) in the status bar. Called by the Next Slice and Previous Slice commands to update the z-coordinate and pixel value.
     */

    public void updateStatusbarValue() {
        int z=1;
        if(selectedImages.length>1)
            z = zSelector.getValue(); 
        IJ.showStatus(imp.getLocationAsString(savex+1,savey+1) + selectedImages[z-1].getValueAsString(savex,savey));
    }

    
    private String selectDirectory(){
        File selectedFile = null;
        PrefsManager prefs=new PrefsManager();
        prefs.setPreference();
        JFileChooser fileChooser = new JFileChooser();
        File myDir=new File(prefs.getLastUsedDirectory());
        IJ.log("start "+prefs.getLastUsedDirectory());
        fileChooser.setCurrentDirectory(myDir);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int option = fileChooser.showDialog(null,tr("Choose directory"));
        if (option == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
             // if the user accidently clicks on a file,the parent directory is selected.
            if (!selectedFile.isDirectory()) {
                selectedFile = selectedFile.getParentFile();
                prefs.saveDirectory(selectedFile.getAbsolutePath());
            }
        }
        if (selectedFile!=null){
            prefs.saveDirectory(selectedFile.getAbsolutePath());
            return selectedFile.getAbsolutePath()+"/";}
        return null;
    }

    /**
     * This method overides the supermethod to avoid the imp=null present in the method of ImageWindow
     * @return true when the window is closes
     */
    @Override
    public boolean close() { 
        boolean isRunning = running || running2;
        running = running2 = false;
        boolean virtual = imp.getStackSize()>1 && imp.getStack().isVirtual();
        if (isRunning) IJ.wait(500);
        if (ij==null || IJ.getApplet()!=null || Interpreter.isBatchMode() || IJ.macroRunning() || virtual)
                imp.changes = false;
        if (imp.changes) {
                String msg;
                String name = imp.getTitle();
                if (name.length()>22)
                        msg = "Save changes to\n" + "\"" + name + "\"?";
                else
                        msg = "Save changes to \"" + name + "\"?";
                YesNoCancelDialog d = new YesNoCancelDialog(this, "ImageJ", msg);
                if (d.cancelPressed())
                        return false;
                else if (d.yesPressed()) {
                        FileSaver fs = new FileSaver(imp);
                        if (!fs.save()) return false;
                }
        }
        closed = true;
        WindowManager.removeWindow(this);
        setVisible(false);
        if (ij!=null && ij.quitting())  // this may help avoid thread deadlocks
                return true;
        dispose();
        return true;
    }

    
    public String tr(String strToTranslate){
        return selectedImages[0].tr(strToTranslate);
    }
    
} 