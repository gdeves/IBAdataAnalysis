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
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
/**
 * class to instantiate a stack with element maps adding supplementary buttons and information about chemical elements
 * @author deves
 */
public class CustomWindowImage extends StackWindow implements ActionListener,AdjustmentListener{
    
    private Button roiCalculation;
    private JLabel mapLabel;
    private JTextField roiName;
    private final GeneratedMap[] stack;
    private RoiManager manager;
    private int X, Y;

    // constructor
    /**
     * Instantiate a new stack of element maps
     * @param imp
     * @param selectedImages 
     */
    public CustomWindowImage(ImagePlus imp, GeneratedMap[] selectedImages) {
        super(imp);
        this.stack = selectedImages;
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
        if (stack.length>1)
            nbFields=7;
        panel.setLayout(new GridLayout(nbFields,1));
        roiName = new JTextField();
        roiName.setText(tr("ROI name"));
        roiCalculation = new Button("Calculate ROI spectra");
        roiCalculation.addActionListener(this);
        mapLabel = new JLabel();
        mapLabel.setText(stack[0].getTitle());
        panel.add(mapLabel);
        if (stack.length>1){
            panel.add(sliceSelector);
            sliceSelector.addAdjustmentListener(this); 
        }
        panel.add(new Label(""));
        panel.add(roiCalculation);
        add(panel);
        pack();  
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        RoiManager manager = RoiManager.getInstance();
        Object b = e.getSource();
        int z=0;
        if(stack.length>1)
            z = zSelector.getValue()-1; 
        
        
        if (b==roiCalculation) {
            if (imp.getRoi() ==null) manager.add(imp,imp.getRoi(),manager.getCount()+1);              
            for (int index=0; index<manager.getCount();index++){
                manager.select(index);
                IJ.log("calculating spectra from ROI "+manager.getName(index));
                Spectra roiSpectra=stack[0].roiSpectra();
                roiSpectra.setFilename(roiSpectra.getPath(manager.getName(index)));
                roiSpectra.plot((String)"Spectra from ROI: "+manager.getName(index), (String) tr("Datafile")+" "+roiSpectra.getPath()).showVisible();
                roiSpectra.getADC().saveGupixSpectra(roiSpectra.getPath()+".gup");       
                roiSpectra=null;
            }
            System.gc();
        }
        
        ImageCanvas imageCanvas = imp.getCanvas();
        if (imageCanvas!=null)
            imageCanvas.requestFocus();
    }
    
    @Override
    public synchronized void adjustmentValueChanged(AdjustmentEvent adjEv) { 
        int z = zSelector.getValue(); 
        String currentName = stack[z-1].getTitle();
        mapLabel.setText(currentName);
        super.adjustmentValueChanged(adjEv);
    } 
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent mWEvent) {
        super.mouseWheelMoved(mWEvent);
        if(stack.length>1){
            int z = zSelector.getValue()-1; 
            String currentName = stack[z].getTitle();
            mapLabel.setText(currentName);
        }
        updateStatusbarValue();
    }

    /**
     *Function to show information about element yield at mouse position
     * @param x
     * @param y
     */
    @Override
    public void mouseMoved(int x, int y) {
        int z=0;
        if(stack.length>1)
            z = zSelector.getValue()-1; 
        IJ.showStatus(imp.getLocationAsString(x,y) + stack[z].getYield(x,y));
	X=x;
        Y=y;
    }
	
    
    
    /**
     * Redisplays the (x,y) coordinates and pixel value (which may have changed) in the status bar. Called by the Next Slice and Previous Slice commands to update the z-coordinate and pixel value.
     */

    public void updateStatusbarValue() {
        int z=1;
        if(stack.length>1)
            z = zSelector.getValue(); 
        IJ.showStatus(imp.getLocationAsString(X,Y) + stack[z-1].getYield(X,Y));
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

    /**
     * Function to translate a string into french/english
     * @param sentence
     * @return the translated sentence
     */
    public String tr(String sentence){
        return stack[0].translate(sentence);
    }
    
} 