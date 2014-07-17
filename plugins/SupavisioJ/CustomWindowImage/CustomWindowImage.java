package SupavisioJ.CustomWindowImage;

import SupavisioJ.ImageGenerated.ImageGenerated;
import SupavisioJ.Spectra.Spectra;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.StackWindow;
import ij.gui.YesNoCancelDialog;
import ij.io.FileSaver;
import ij.macro.Interpreter;

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
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class CustomWindowImage extends StackWindow implements ActionListener,AdjustmentListener{
    
    private Button buttonCalc;
    private Button buttonSave;
    private Button buttonSaveAll;
    private JLabel imageName;
    private JTextField nameRoiField;
    private ImageGenerated[] selectedImages; 

    // constructor
    public CustomWindowImage(ImagePlus imp, ImageGenerated[] selectedImages) {
        super(imp);
        this.selectedImages = selectedImages;
        setLayout(new FlowLayout());
        if (selectedImages.length>1){
            remove(zSelector);
        }
        addPanel();
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
        nameRoiField.setText(tr("Name of the ROI"));
        buttonCalc = new Button(tr("Calculate spectra"));
        buttonCalc.addActionListener(this);
        buttonSave = new Button(tr("Save"));
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
        panel.add(nameRoiField);
        panel.add(buttonCalc);
        panel.add(buttonSave);
        panel.add(buttonSaveAll);
        add(panel);
        pack();  
    }

    public void actionPerformed(ActionEvent e) {
        Object b = e.getSource();
        int z=0;
        if(selectedImages.length>1)
            z = zSelector.getValue()-1; 
        if (b==buttonCalc) {
            Spectra spectraCalc=selectedImages[z].generateSpectraFromRoi();
            spectraCalc.plotSpectra((String)"SupavisioJ", (String) tr("Calculated spectra")+" "+spectraCalc.getFileName()).showVisible();
        }
        if (b==buttonSave) {
            // save selected image
            String directory=selectDirectory();
            if (directory!=null){
                selectedImages[z].save(directory);
            }
        }
        if (b==buttonSaveAll) {
            //save all images generate from parent Spectra
            String directory=selectDirectory();
            if (directory!=null)
                selectedImages[z].saveAll(directory);
        }
        ImageCanvas ic = imp.getCanvas();
        if (ic!=null)
            ic.requestFocus();
    }
    
    public synchronized void adjustmentValueChanged(AdjustmentEvent adjEv) { 
        int z = zSelector.getValue(); 
        String currentName = selectedImages[z-1].getTitle();
        imageName.setText(currentName);
        super.adjustmentValueChanged(adjEv);
    } 
    
    public void mouseWheelMoved(MouseWheelEvent mWEvent) {
        super.mouseWheelMoved(mWEvent);
        if(selectedImages.length>1){
            int z = zSelector.getValue()-1; 
            String currentName = selectedImages[z].getTitle();
            imageName.setText(currentName);
        }
        updateStatusbarValue();
    }

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
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int option = fileChooser.showDialog(null,tr("Choose directory"));
        if (option == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
             // if the user accidently clicks on a file,the parent directory is selected.
            if (!selectedFile.isDirectory()) {
                selectedFile = selectedFile.getParentFile();
            }
        }
        if (selectedFile!=null)
            return selectedFile.getAbsolutePath()+"/";
        return null;
    }

    /**
     * This method overides the supermethod to avoid the imp=null present in the method of ImageWindow
     */
    public boolean close() { //code taken (partially) from ImageWindow
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