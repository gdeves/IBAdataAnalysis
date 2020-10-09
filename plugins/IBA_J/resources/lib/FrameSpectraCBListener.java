package IBA_J.resources.lib;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;

/**
 * CheckBoxListener is the class responsible for the events related
 * with the checked / unchecked state of the checkboxes indicating the Min and MlastCheckBoxActivatedax spectra values.
 * It will draw lines on the Spectra where the user has choosed energies
 */
public class FrameSpectraCBListener implements ItemListener,DocumentListener{
     private final FrameSpectra sourceFrame;
     private final ArrayList<JCheckBox> checkBoxList = new ArrayList<>();
     private final ArrayList<int[]> colorList = new ArrayList<>(); //color corresponding to the checkboxes
     private final ArrayList<Float> usedLinePositions = new ArrayList<>();
     private final ArrayList<ValueMarker> usedLineMarkers = new ArrayList<>();
     private final ArrayList<int[]> usedLineColors = new ArrayList<>();
     private JCheckBox lastUsedCheckBox; 

    /**
     *
     * @param sourceFrame
     */
    public FrameSpectraCBListener(FrameSpectra sourceFrame) {
      this.sourceFrame= sourceFrame;    
   }
   
    /**
     *
     * @return
     */
    public JCheckBox getLastUsedCheckBox(){
       return lastUsedCheckBox;
   }
  
    /**
     * Method to check if the generated color is available to be used as marker
     * @param R
     * @param G
     * @param B
     * @param numberOfCall
     * @return
     */
    public boolean isColorAvailable(int R, int G,int B, int numberOfCall){
        //checks if the generated color is available to be used as marker
        if (  G+B>(70*R/100) ) { //to avoid the red color (similar to graph color)
            if(colorList.isEmpty())
                return true;
            //checks if the color is not already taken (or close to it)
            //chooses a low treshold if numberOfCall is high
            int treshold;
            if(numberOfCall>40){
                treshold=1;
            }
            else if(numberOfCall>25){
                treshold=5;
            }
            else if(numberOfCall>10){
                treshold=10;
            }
            else {
                treshold=20;
            }
            for (int[] colorRGB : colorList) {
                int r2=colorRGB[0], g2=colorRGB[1], b2=colorRGB[2];
                if ( (R>r2+treshold || R<r2-treshold) && (G>g2+treshold || G<g2-treshold) ){
                    return true;
                }
                if ( (G>g2+treshold || G<g2-treshold) && (B>b2+treshold || B<b2-treshold) ){
                    return true;
                }
            }
        }
        return false;
   } 
   
   /**
    * this method uses a marker to draw a vertical line on the displayed spectra front layer.
    * @param position
    * @param CurrentCheckbox
    */
   public void drawVerticalLine(float position, JCheckBox CurrentCheckbox){
        // position is the value on the axis
        ValueMarker marker = new ValueMarker(position);
        Color color;
        int R=0,G=0,B=0;
        if(checkBoxList.contains(CurrentCheckbox)){
            int index;
            index = checkBoxList.indexOf(CurrentCheckbox);
            int[] colorRGB = colorList.get(index);
            R = colorRGB[0];
            G = colorRGB[1];
            B = colorRGB[2];
        }
        else {
            int numberOfCall=0;
            while(!isColorAvailable(R,G,B,numberOfCall)){
                R=randomInteger(100,200);
                G=randomInteger(120,220);
                B=randomInteger(120,220);
                numberOfCall++;
            }
        }
        color = new Color(0,0,0);
        //colorToDraw = new Color(r,g,b);
        marker.setPaint(color);
        marker.setOutlinePaint(color);
        // change line style (dashboard)
        BasicStroke dashedStroke = new BasicStroke(
                   1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                   1.0f, new float[] {6.0f, 6.0f}, 0.0f);

        marker.setStroke(dashedStroke);
        JFreeChart chart=sourceFrame.getChart();
        XYPlot XYPlotOfChart = (XYPlot) chart.getPlot();
        //draw vertical line 
        XYPlotOfChart.addDomainMarker(marker);
        
        int[] usedColor = new int[3];
        usedColor[0]=R;
        usedColor[1]=G;
        usedColor[2]=B;
        usedLinePositions.add(position);
        usedLineMarkers.add(marker);
        usedLineColors.add(usedColor);

        if (!checkBoxList.contains(CurrentCheckbox)){
            checkBoxList.add(CurrentCheckbox);
            colorList.add(usedColor);            
        }
    }
    
    /**
     * this method removes the marker used as bouding line on the dispayed spectra's front layer.
     * @param position
     * @param checkBoxCurrent 
     */
    public void removeVerticalLine(float position,JCheckBox checkBoxCurrent){
        JFreeChart chart=sourceFrame.getChart();
        XYPlot XYPlotOfChart = (XYPlot) chart.getPlot();
        //before remove : check if the vertical line is not the same for an other checkbox
        //in this case the position is also taken in another checkbox selected
        ArrayList<JCheckBox> checkBoxsSelected = sourceFrame.getSelectedCheckBox();
        boolean isPresent = false;
        int indexIsPresent=-1;
        if (checkBoxsSelected.size()>0){//so other box are selected
            Vector vectValues = sourceFrame.getRoiProperties(true,false,false);
            ArrayList<float[]> minMaxSpectra = (ArrayList<float[]>) vectValues.get(1);
            for(int i=0;i<minMaxSpectra.size();i++){
                boolean isEqual1 = Float.compare(position,minMaxSpectra.get(i)[0])==0;
                boolean isEqual2 = Float.compare(position,minMaxSpectra.get(i)[1])==0;
                if (isEqual1||isEqual2) {//so no suppression
                    //no suppression but color may not correspond
                    //--> suppression then readd
                    isPresent=true;
                    indexIsPresent=i;
                }           
            }
        }
        //remove vertical line
        int index = usedLinePositions.indexOf(position);
        ValueMarker marker = usedLineMarkers.get(index);
        XYPlotOfChart.removeDomainMarker(marker);
        usedLinePositions.remove(position);
        usedLineMarkers.remove(marker);
        usedLineColors.remove(index);
        //commented code below : if you want to NOT keep the same color after checking/unchecking one checkbox
        //if (numberOfCall==2){
        //    int index2 = checkBoxList.indexOf(checkBoxCurrent);
        //    colorList.remove(index2);
        //    checkBoxList.remove(index2);
        //}
        if(isPresent){ //so need to found the right color/box and draw
            //find only checkboxselected
            JCheckBox checkBoxSimilarValue = checkBoxsSelected.get(indexIsPresent);
            drawVerticalLine(position, checkBoxSimilarValue);
        } 
    }
    /**
     * This method is called each time that a checkbox is selected/unselected 
     * @param e
     */
     @Override
    public void itemStateChanged(ItemEvent e) {
        update(e.getSource());
    }
        
    /**
     *
     * @param objectActivated
     */
    public void update(Object objectActivated){
        Vector vectButtons = sourceFrame.getVectButtonsSupp();
         for (Object vectButton : vectButtons) {
             JComponent[] tabJCompToCheck = (JComponent[]) vectButton;
             JCheckBox checkBoxCurrent = (JCheckBox) tabJCompToCheck[0];
             if(objectActivated==checkBoxCurrent){
                 if(checkBoxCurrent.isSelected())
                     lastUsedCheckBox=checkBoxCurrent;
                 //first reset : check up; if min/max has been changed, markers' colors need to be rechecked.
                 // this checkbox = one color so it checks if markers of this color remain, they have to be removed.
                 int indexOfCheckBox = checkBoxList.indexOf(checkBoxCurrent);
                 if (indexOfCheckBox!=-1){
                     int[] colorRGBOfCheckBox=colorList.get(indexOfCheckBox);
                     int r=colorRGBOfCheckBox[0];
                     int g=colorRGBOfCheckBox[1];
                     int b=colorRGBOfCheckBox[2];
                     ArrayList<Integer> valToRemove= new ArrayList<>();
                     for(int j=0;j<usedLineColors.size();j++){
                         int[] colorToCheck = usedLineColors.get(j);
                         if (r==colorToCheck[0] && g==colorToCheck[1] && b==colorToCheck[2]){
                             valToRemove.add(j);
                         }
                     }
                     for(int j=0;j<valToRemove.size();j++){
                         float positionToRemove= usedLinePositions.get(valToRemove.get(j));
                         removeVerticalLine(positionToRemove,checkBoxCurrent);
                         for(int k=0;k<valToRemove.size();k++){
                             valToRemove.set(k,valToRemove.get(k)-1);
                         }
                     }
                 }
                 //get the min and max values to draw
                 for (int j=0;j<2;j++){
                     JTextField textFieldMinMax = (JTextField) tabJCompToCheck[j+2];
                     if ( !(textFieldMinMax.getText().equals("Min") || textFieldMinMax.getText().equals("Max")) ) {
                         float minMax = -1;
                         try {
                             minMax = Float.valueOf(textFieldMinMax.getText());
                         }
                         catch(NumberFormatException e2){}
                         if (minMax>-1){
                             if(checkBoxCurrent.isSelected()) {
                                 if(! usedLinePositions.contains(minMax)){
                                     drawVerticalLine(minMax,checkBoxCurrent);
                                 }
                             }
                         }
                     }
                 }
             }
         }
    }
    
    /**
     * @param min
     * @param max
     * @return a randomly generated number between min and max (max included)
     */
    public static int randomInteger(int min, int max) {
        // Usually this can be a field rather than a method variable
        Random rand = new Random();
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    /**
     *
     * @param dEvt
     */
    public void handlesDocumentEvt(DocumentEvent dEvt){
        int lengthText = dEvt.getDocument().getLength();
        try{
            String text = dEvt.getDocument().getText(0,lengthText);
            JCheckBox checkBoxToUpdate = sourceFrame.getCheckBoxRelatedToField(text);
            if(checkBoxToUpdate!=null && checkBoxToUpdate.isSelected()){
                update(checkBoxToUpdate);
            }
        }
        catch(BadLocationException e){}
    }
    
     @Override
    public void insertUpdate(DocumentEvent dEvt) {
        handlesDocumentEvt(dEvt);
    }

     @Override
    public void removeUpdate(DocumentEvent dEvt) {
        handlesDocumentEvt(dEvt);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        //not used
    }
    
}
