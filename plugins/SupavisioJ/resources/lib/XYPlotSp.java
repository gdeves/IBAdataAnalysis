package SupavisioJ.resources.lib;

import SupavisioJ.ImageGenerated.ImageGenerated;
import SupavisioJ.MainFrame.MainFrame;
import SupavisioJ.Spectra.Spectra;
import SupavisioJ.resources.lib.CheckBoxListenerSp;

import ij.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

/**
 * Class XYPlotSp is the class representing the spectra management window
 */
public class XYPlotSp extends JFrame {
    
    private String titleGraph;
    private Spectra spectraDrew;
    private JFreeChart chart;
    private static MainFrame parentWindow;
    private boolean yIsLog=false;
    private CheckBoxListenerSp checkBoxListener;
    private final CustomChartPanel chartPanel;
    
    public XYPlotSp(Spectra spectraDrew, final String titleWindow,final String titleGraph2, double[] energiesX, double[] dataY,int nbFieldsToProduce) {
        super(titleWindow);
        this.parentWindow=spectraDrew.getParentWindowS();
        this.titleGraph=titleGraph2;
        this.spectraDrew=spectraDrew;
        //create the dataset
        final XYDataset dataset = createDataset(energiesX,dataY);
        //create the chart
        final JFreeChart chart = createChart(dataset,titleGraph2,checkCalib());
        this.chart = chart;
        this.chartPanel= new CustomChartPanel(chart,this);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setPreferredSize(new Dimension(500, 270));
        jButtonLogLinActionPerformed(null);
        initComponents(nbFieldsToProduce);
    }
    
    public Vector getVectButtonsSupp(){
        return vectButtonsSupp;
    }
    
    public JFreeChart getChart(){
        return chart;
    }
    /**
     * Creates the dataset of the Spectra
     * @returns a dataset.
     */
    private static XYDataset createDataset(double[] energiesX,double[] dataY) {
        final XYSeries series1 = new XYSeries(tr("Spectra")+" 1");
        for(int i=0;i<energiesX.length;i++){
            if(dataY[i]>0)
                series1.add(energiesX[i],dataY[i]);
        }
        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);
        return dataset;
    }
    
    /**
     * Creates a chart.
     * @param dataset  the data for the chart.
     * @returns a chart.
     */
    private static JFreeChart createChart(final XYDataset dataset, final String titleGraph2, final String titleAxisX) {
        // create the chart...
        final JFreeChart chart = ChartFactory.createXYLineChart(
            titleGraph2,      // chart title
            tr(titleAxisX),                      // x axis label
            tr("Events number"),              // y axis label
            dataset,                  // data
            PlotOrientation.VERTICAL, // plot orientation (pretty obvious this one)
            false,                    // if true show the legeng (useful for several lignes)
            true,                     // tooltips
            false                     // URLs
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.setBackgroundPaint(Color.white);
        //final StandardLegend legend = (StandardLegend) chart.getLegend();
        //legend.setDisplaySeriesShapes(true);
        
        // get a reference to the plot for further customisation...
        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.lightGray);
        //plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        
        //code for points and no lines on the chart
        //final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        //renderer.setSeriesLinesVisible(0, false);
        //renderer.setSeriesShapesVisible(1, false);
        //plot.setRenderer(renderer);

        // change the auto tick unit selection to integer units only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        
        // OPTIONAL CUSTOMISATION COMPLETED.
        return chart;
    }
    
    /**
     * Use this method to show the window to the user
     */
    public void showVisible() {
        pack();
        RefineryUtilities.centerFrameOnScreen(this);
        setVisible(true);
    }
    
    public void windowClosing(final WindowEvent evt){
        if(evt.getWindow() == this){
            dispose();
        }
    }
    
    /**
     * This method will link each CheckBox to CheckBoxListenerSp
     * @see CheckBoxListenerSp
     */
    private void initComponents(int nbFieldsToProduce) {
        checkBoxListener = new CheckBoxListenerSp(this);
        int nbFieldsToAdd=nbFieldsToProduce-vectButtonsSupp.size();
        if (nbFieldsToAdd>0){
            if((vectButtonsSupp.size()%3)!=0){
                nbFieldsToAdd=3-(vectButtonsSupp.size()%3);
                for(int j=0;j<nbFieldsToAdd;j++){
                    addField();
                }
            }
            nbFieldsToAdd=nbFieldsToProduce-vectButtonsSupp.size();
            int nbLinesToAdd=nbFieldsToAdd/3;
            if((nbFieldsToAdd%3)!=0)
                nbLinesToAdd+=1;
            int nbFieldsPerLine=3;
            for (int i=0; i<nbLinesToAdd;i++){
                if (i==nbLinesToAdd-1)
                    nbFieldsPerLine=(nbFieldsToAdd%3);
                if (nbFieldsPerLine==0)
                    nbFieldsPerLine=3;
                for(int j=0;j<nbFieldsPerLine;j++){
                    addField();
                }
            }
        }
        jButtonPlus = new JButton();
        jButtonPlus.setText(tr("More ..."));
        
        jButtonPlus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMoreActionPerformed(evt);
            }
        });
        jButtonGenImg = new JButton();
        jButtonGenImg.setText(tr("Generate pictures"));
        jButtonGenImg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGenImgActionPerformed(evt);
            }
        });
        
        jButtonLogLin = new JButton();
        jButtonLogLin.setText(tr("Log/Lin"));
        jButtonLogLin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLogLinActionPerformed(evt);
            }
        });
        
        jButtonSave = new JButton();
        jButtonSave.setText(tr("Save"));
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });
        
        jButtonSaveGup = new JButton();
        jButtonSaveGup.setText(tr("Save Gupix"));
        jButtonSaveGup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveGupActionPerformed(evt);
            }
        });
        JPanel panel = new JPanel();
        remove(getContentPane());
        setContentPane(panel);
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        
        //Layout horizontal
        ParallelGroup paralGroupHor=layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        SequentialGroup grpChartPanel=layout.createSequentialGroup();
        grpChartPanel.addComponent(chartPanel);
        paralGroupHor.addGroup(grpChartPanel);
        
        //panel for fields
       
        JPanel panelFields = new JPanel();
        GroupLayout layoutPanelFields = new GroupLayout(panelFields);
        panelFields.setLayout(layoutPanelFields);
        ParallelGroup paralGroupHorPanelFields=layoutPanelFields.createParallelGroup(GroupLayout.Alignment.LEADING);
        int nbLines=nbFieldsToProduce/3;
        int nbFieldsPerLine=3;
        for (int i=0; i<nbLines;i++){
            IJ.log("nbLines "+String.valueOf(nbLines));
            IJ.log("nbFieldsToProduce "+String.valueOf(nbFieldsToProduce));
            SequentialGroup grp1 = layoutPanelFields.createSequentialGroup();
            grp1.addContainerGap();
            IJ.log("nbFieldsPerLine avt "+String.valueOf(nbFieldsPerLine));
            if (i==nbLines-1)
                nbFieldsPerLine=(nbFieldsToProduce%3);
            IJ.log("nbFieldsPerLine apCond1 "+String.valueOf(nbFieldsPerLine));
            if (nbFieldsPerLine==0)
                nbFieldsPerLine=3;
            IJ.log("nbFieldsPerLine apCond2 "+String.valueOf(nbFieldsPerLine));
            for(int j=0;j<nbFieldsPerLine;j++){        
                int currentField=i*3+j;
                IJ.log("currentField "+String.valueOf(currentField));
                JComponent[] tablJComp = (JComponent[]) vectButtonsSupp.get(currentField);
                JCheckBox checkBoxCurrent = (JCheckBox) tablJComp[0];
                JTextField textFieldCurrentName= (JTextField) tablJComp[1];
                JTextField textFieldCurrentMin= (JTextField) tablJComp[2];
                JTextField textFieldCurrentMax=(JTextField) tablJComp[3];
                grp1.addComponent(checkBoxCurrent, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE);
                grp1.addPreferredGap(ComponentPlacement.RELATED);
                grp1.addComponent(textFieldCurrentName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
                grp1.addPreferredGap(ComponentPlacement.RELATED);
                grp1.addComponent(textFieldCurrentMin, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE);
                grp1.addPreferredGap(ComponentPlacement.RELATED);
                grp1.addComponent(textFieldCurrentMax, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE);
                if (j!=3){
                    grp1.addGap(40,40,40);
                }
                else{
                    grp1.addContainerGap(25, Short.MAX_VALUE);
                }
            }
            paralGroupHorPanelFields.addGroup(grp1);
        }
        JScrollPane scroll = new JScrollPane(panelFields,JScrollPane.VERTICAL_SCROLLBAR_NEVER,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        if(nbFieldsToProduce>15){//so enable scrolling
            scroll = new JScrollPane(panelFields,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);    
            scroll.setPreferredSize(new Dimension(320,260));
        }
        scroll.setBorder(null);
        SequentialGroup grpPanelFields=layout.createSequentialGroup();
        grpPanelFields.addComponent(scroll);
        paralGroupHor.addGroup(grpPanelFields);
        //layout horizontal for buttons
        SequentialGroup grpButton = layout.createSequentialGroup();
        grpButton.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        //grpButton.addGap(130);
        grpButton.addComponent(jButtonPlus, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE);
        grpButton.addPreferredGap(ComponentPlacement.RELATED);
        grpButton.addComponent(jButtonGenImg, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE);
        grpButton.addPreferredGap(ComponentPlacement.RELATED);
        grpButton.addComponent(jButtonLogLin, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE);
        grpButton.addPreferredGap(ComponentPlacement.RELATED);
        grpButton.addComponent(jButtonSave, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE);
        grpButton.addPreferredGap(ComponentPlacement.RELATED);
        grpButton.addComponent(jButtonSaveGup, GroupLayout.PREFERRED_SIZE, 125, GroupLayout.PREFERRED_SIZE);
        grpButton.addGap(70,70,70);
        paralGroupHor.addGroup(GroupLayout.Alignment.TRAILING, grpButton);
        
        
        //Layout vertical 
        ParallelGroup paralGroup2Vert=layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        SequentialGroup grpAll = layout.createSequentialGroup();
        ParallelGroup gpChart = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
        gpChart.addComponent(chartPanel);
        grpAll.addGroup(gpChart);
        
        ParallelGroup paralGroup2VertPanFields=layoutPanelFields.createParallelGroup(GroupLayout.Alignment.LEADING);
        SequentialGroup grpAllFields = layoutPanelFields.createSequentialGroup();
        nbFieldsPerLine=3;
        for (int i=0; i<nbLines;i++){
            ParallelGroup grpIntChannel = layoutPanelFields.createParallelGroup(GroupLayout.Alignment.BASELINE);
            if (i==nbLines-1)
                nbFieldsPerLine=(nbFieldsToProduce%3);
            if (nbFieldsPerLine==0)
                nbFieldsPerLine=3;
            for(int j=0;j<nbFieldsPerLine;j++){
                int currentField=i*3+j;
                JComponent[] tablJComp = (JComponent[]) vectButtonsSupp.get(currentField);
                JCheckBox checkBoxCurrent = (JCheckBox) tablJComp[0];
                JTextField textFieldCurrentName= (JTextField) tablJComp[1];
                JTextField textFieldCurrentMin= (JTextField) tablJComp[2];
                JTextField textFieldCurrentMax= (JTextField) tablJComp[3];
                grpIntChannel.addComponent(checkBoxCurrent);
                grpIntChannel.addComponent(textFieldCurrentName,GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE);
                grpIntChannel.addComponent(textFieldCurrentMin,GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE);
                grpIntChannel.addComponent(textFieldCurrentMax,GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE);
            }
            grpAllFields.addContainerGap(10, Short.MAX_VALUE).addGroup(grpIntChannel);
            grpAllFields.addPreferredGap(ComponentPlacement.RELATED);
        }
        grpAllFields.addGap(6, 6, 6);
        paralGroup2VertPanFields.addGroup(GroupLayout.Alignment.TRAILING,grpAllFields);
        
        ParallelGroup grpPanFields = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
        grpPanFields.addComponent(scroll);
        grpAll.addGroup(grpPanFields);
     
        ParallelGroup grpButtons = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
        grpButtons.addComponent(jButtonPlus, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE);
        grpButtons.addComponent(jButtonGenImg, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE);
        grpButtons.addComponent(jButtonLogLin, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE);
        grpButtons.addComponent(jButtonSave, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE);
        grpButtons.addComponent(jButtonSaveGup, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE);
        grpAll.addContainerGap(10, Short.MAX_VALUE).addGroup(grpButtons);
        grpAll.addGap(6, 6, 6);
      
        paralGroup2Vert.addGroup(GroupLayout.Alignment.TRAILING,grpAll);
        
        layoutPanelFields.setHorizontalGroup(paralGroupHorPanelFields);
        layoutPanelFields.setVerticalGroup(paralGroup2VertPanFields);
        layout.setHorizontalGroup(paralGroupHor);
        layout.setVerticalGroup(paralGroup2Vert);
        pack();
    }                 
    
    private void addField(){
        JComponent[] buttonsToAdd= new JComponent[4];
        buttonsToAdd[0] = new JCheckBox();
        buttonsToAdd[1] = new JTextField();
        buttonsToAdd[2] = new JTextField();
        buttonsToAdd[3] = new JTextField();
        vectButtonsSupp.add(buttonsToAdd);
        JCheckBox checkBoxCurrent = (JCheckBox) buttonsToAdd[0];
        JTextField textFieldCurrentName= (JTextField) buttonsToAdd[1];
        JTextField textFieldCurrentMin= (JTextField) buttonsToAdd[2];
        JTextField textFieldCurrentMax=(JTextField) buttonsToAdd[3];
        checkBoxCurrent.setText(tr("NA"));
        textFieldCurrentName.setText(tr("Name"));
        textFieldCurrentMin.setText(tr("Min"));
        textFieldCurrentMax.setText(tr("Max"));
        checkBoxCurrent.addItemListener(checkBoxListener);
        textFieldCurrentMin.getDocument().addDocumentListener(checkBoxListener);
        textFieldCurrentMax.getDocument().addDocumentListener(checkBoxListener);
    }

    private void jButtonMoreActionPerformed(java.awt.event.ActionEvent evt) {                                           
        initComponents(vectButtonsSupp.size()+3);
    }   
    
    private void jButtonGenImgActionPerformed(java.awt.event.ActionEvent evt) {                                         
        // gen img for all checkbox selected
        //First which case is selected
        // number of case = vectButtonsSupp.size()
        // the whole is taken if JComp[0] is selected and 1,2 et 3 are corrects
        try {
            ImageGenerated[] tabImgGenFromSpectra;
            Vector vectValues = getValuesMinMaxNames(false,true,true);
            ArrayList<String> nameOfImgGen = (ArrayList<String>) vectValues.get(0);
            ArrayList<float[]> minMaxSpectra = (ArrayList<float[]>) vectValues.get(1);
            float[][] minMaxSpectraArray = minMaxSpectra.toArray(new float[minMaxSpectra.size()][2]);
            if(minMaxSpectraArray.length>0){
                tabImgGenFromSpectra = spectraDrew.generatePicture(minMaxSpectraArray);
                ImageGenerated imgToShow=tabImgGenFromSpectra[0];
                imgToShow.setTitle(nameOfImgGen.get(0));
                int nbOfOtherImg=tabImgGenFromSpectra.length-1;
                if(nbOfOtherImg>0){//check if more than one image to show
                    ImageGenerated[] tabOtherImgToShow = new ImageGenerated[nbOfOtherImg];
                    for (int i=0;i<nbOfOtherImg;i++){
                        tabOtherImgToShow[i]=tabImgGenFromSpectra[i+1];
                        tabOtherImgToShow[i].setTitle(nameOfImgGen.get(i+1));
                    }
                    imgToShow.showWithOtherImgs(tabOtherImgToShow);
                }
                else{
                    imgToShow.show();
                }
            }
        }
        catch(NullPointerException e){}
    }
    
    
    public ArrayList<JCheckBox> getCheckBoxSelected(){
        ArrayList<JCheckBox> tabCheckBoxSelected = new ArrayList<JCheckBox>();
        for(int i=0;i<vectButtonsSupp.size();i++){
            JComponent[] tabJComp = (JComponent[]) vectButtonsSupp.get(i);
            JCheckBox checkBoxCurrent = (JCheckBox) tabJComp[0];
            if(checkBoxCurrent.isSelected()){
                tabCheckBoxSelected.add(checkBoxCurrent);
            }
        }
        return tabCheckBoxSelected;
    }
    
    public JCheckBox getLastCheckBoxActivated(){
        return checkBoxListener.getLastCheckBoxActivated();
    }
    
    public void updateLinesOnChart(JCheckBox checkBoxToUpdate){
        checkBoxListener.update(checkBoxToUpdate);
    }
    
    /**
     * Gets the min, max and name values if checkboxs are selected
     * @param minOrMax if false min AND max have to be correct 
     * @param nameIsImportant if false the method will not check the name
     * @return a vector containing the arraylist of the names and the arraylist of the [min,max]
     */
    public Vector getValuesMinMaxNames(boolean minOrMax, boolean nameIsImportant, boolean showError){
        ArrayList<String> nameOfImgGen = new ArrayList<String>();
        ArrayList<float[]> minMaxSpectra = new ArrayList<float[]>();
        boolean errorShowed1=false, errorShowed2=false, errorShowed3=false, errorShowed4=false;
        for(int i=0; i<vectButtonsSupp.size();i++){
            JComponent[] tabJCompToCheck = (JComponent[]) vectButtonsSupp.get(i);
            JCheckBox checkBoxCurrent = (JCheckBox) tabJCompToCheck[0];
            if(checkBoxCurrent.isSelected()){
                JTextField name= (JTextField) tabJCompToCheck[1];
                JTextField min= (JTextField) tabJCompToCheck[2];
                JTextField max=(JTextField) tabJCompToCheck[3];
                if(!( (min.getText().equals(tr("Min")) && max.getText().equals(tr("Max"))) )){
                    if(minOrMax|| !((min.getText().equals(tr("Min")) || max.getText().equals(tr("Max"))))){
                        if ( !nameIsImportant || !(name.getText().equals(tr("Name"))) ) {
                            float start=-1;
                            float end=-1;
                            try {
                                start = Float.valueOf(min.getText());
                            }
                            catch(NumberFormatException e){
                                if(showError && !errorShowed1){
                                    errorShowed1=true;
                                    IJ.error(tr("Put numbers in the Min and Max text fields"));
                                }
                            }
                            try {
                                end = Float.valueOf(max.getText());
                            }
                            catch(NumberFormatException e){}
                            if(minOrMax||start<end){
                                if (spectraDrew.energyExist(start) || spectraDrew.energyExist(end)){
                                    if (!spectraDrew.energyExist(start)){
                                        start=spectraDrew.getEnergies()[0];
                                    }
                                    else if(!spectraDrew.energyExist(end)) {
                                        int length = spectraDrew.getEnergies().length;
                                        end=spectraDrew.getEnergies()[length-1];
                                    }
                                    if( !nameIsImportant || !nameOfImgGen.contains(name.getText()) ){//useful to avoid saving problems
                                        if( !nameIsImportant || !(name.getText().contains("_")) ){//useful to avoid restoring problems
                                            nameOfImgGen.add(name.getText());
                                            float[] tabMinMax = new float[2];
                                            tabMinMax[0]=start;
                                            tabMinMax[1]=end;
                                            minMaxSpectra.add(tabMinMax);
                                        }
                                        else{
                                            if(showError && !errorShowed1 && !errorShowed2){
                                                IJ.error(tr("The character '_' is not allowed"));
                                                errorShowed2=true;
                                            }
                                        }
                                    }
                                    else{
                                        if(showError && !errorShowed1 && !errorShowed2 && !errorShowed3){
                                            IJ.error(tr("Give different title for each picture please"));
                                            errorShowed3=true;
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            if(showError && !errorShowed1 && !errorShowed2 && !errorShowed3 && !errorShowed4){
                                IJ.error(tr("Give names please (other that Name)"));
                                errorShowed4=true;
                            }
                        }
                    }
                }
            }
        }
        Vector vectToReturn = new Vector();
        vectToReturn.add(nameOfImgGen);
        vectToReturn.add(minMaxSpectra);
        return vectToReturn;
    }
    
    private String selectDirectory(){
        File selectedFile = null;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int option = fileChooser.showDialog(null,tr("Choose directory"));
        if (option == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
             // if the user accidently clicks on a file, the parent directory is selected.
            if (!selectedFile.isDirectory()) {
                selectedFile = selectedFile.getParentFile();
            }
        }
        if (selectedFile!=null)
            return selectedFile.getAbsolutePath()+"/";
        return null;
    }
    
    private void jButtonLogLinActionPerformed(java.awt.event.ActionEvent evt) {                                         
        XYPlot plot = (XYPlot) chart.getPlot();
        if (!yIsLog){
            plot.setRangeAxis(new LogarithmicAxis(tr("Events number")));
            yIsLog=true;
        }
        else{
            plot.setRangeAxis(new NumberAxis(tr("Events number")));
            yIsLog=false;
        }
    }
    
    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {                                         
        String directory=selectDirectory();
        if(directory!=null){
            spectraDrew.save(directory);
        }
    }
    
    /**
     * @see ADC.saveGupixSpectra 
     */
    private void jButtonSaveGupActionPerformed(java.awt.event.ActionEvent evt) {                                         
        String directory=selectDirectory();
        if(directory!=null){
            String nameToSave=spectraDrew.getFileName()+".gup";
            spectraDrew.getADC().saveGupixSpectra(directory+nameToSave);
        }
    }
    
    public static String tr(String strToTranslate){
        return parentWindow.tr(strToTranslate);
    }
    
    public float getRealXValue(double value, boolean rightIncluded){
        if(spectraDrew.energyExist((float) value)){
            int indiceNearValue=spectraDrew.getIndiceEnergy((float)value,rightIncluded);
            return spectraDrew.getEnergies()[indiceNearValue];
        }
        return -1;
    }
    
    public JTextField getField(JCheckBox checkBoxOfInterest,String nameField){
        for(int i=0;i<vectButtonsSupp.size();i++){
            JComponent[] tabJComp = (JComponent[]) vectButtonsSupp.get(i);
            JCheckBox checkBoxCurrent = (JCheckBox) tabJComp[0];
            if(checkBoxCurrent==checkBoxOfInterest){
                int indexOfInterest=-1;
                switch(nameField){
                    case "Name" : indexOfInterest=1;break;
                    case "Min" : indexOfInterest=2; break;
                    case "Max" : indexOfInterest=3;break;
                }
                if (indexOfInterest!=-1){
                    return (JTextField) tabJComp[indexOfInterest];
                }
            }
        }
        return null;
    }
    
    public JCheckBox getCheckBoxRelatedToField(String text){
        for(int i=0;i<vectButtonsSupp.size();i++){
            JComponent[] tabJComp = (JComponent[]) vectButtonsSupp.get(i);
            for(int j=2;j<4;j++){
                JTextField currentTextField = (JTextField) tabJComp[j];
                if(currentTextField.getText().equals(text))
                    return (JCheckBox) tabJComp[0];
            }
        }
        return null;
    }
    
    public String checkCalib(){
        if(spectraDrew.channelsAreCalibrated())
            return "Energies";
        return "Energy channels";
    }
        
    // Variables declaration - do not modify 
    private Vector vectButtonsSupp=new Vector();
    private JButton jButtonPlus;
    private JButton jButtonGenImg;
    private JButton jButtonLogLin;
    private JButton jButtonSave;
    private JButton jButtonSaveGup;
    // End of variables declaration               
}

