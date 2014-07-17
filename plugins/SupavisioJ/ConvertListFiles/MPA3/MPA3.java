package SupavisioJ.ConvertListFiles.MPA3;
import java.util.ArrayList;
import SupavisioJ.ConvertListFiles.ADC.ADC;

/**
* MPA contains a list of ADCs
*/
public class MPA3{

    private ArrayList < ADC > mpa = new ArrayList<ADC>();
    
    /**
    * Default constructor. MPA3 is a list of ADCs (default is 16 ADCs)
    * @see initialize
    */
    public MPA3(){
        initialize();
    }
    
    /**
     * Constructor for any number of ADCs in an MPA3
     * @param adc Number of ADCs in an MPA3
     */
    public MPA3(int adc){
        initialize(adc);	
    }
    
    /**
     * Gets an ADC in MPA3
     * @param adc Index of ADC in list
     * @returns the ADC corresponding to the index
     */
    
    public ADC getADC(int adc){
        return mpa.get(adc);
    }
    
    /**
     * Creates a list of 16 ADCs (AIFIRA default channels) in MPA
     */
    public void initialize(){
        for (int i=0;i<16;i++) mpa.add(new ADC());
    }
    
    /**
     * Creates a list of ADCs in MPA
     * @param nADC Number of ADC in the list
     */
    public void initialize(int nADC){
        for (int i=0;i<nADC;i++) mpa.add(new ADC());
    }
    
    /**
     * Processes the sum of 2 ADCs and adds it at the end of the MPA list
     * @param adc1 index of 1st ADC
     * @param adc2 index of 2nd ADC
     * @returns The number of ADCs in the MPA after adding the new summed ADCs
     */
    
    public int sumADC(int adc1, int adc2){
        ADC a=getADC(adc1);
        ADC b=getADC(adc2);
        for (int i=0;i<getADC(adc2).getNEvents();i++) a.addEvent(b.getEvent(i));
        mpa.add(a);
        return mpa.size();
    }
    
    /**
     * Adds the events of the specified ADCs at the ADC corresponding to the specified position
     * @param adc1 ADC to be added
     * @param position index of the ADC which will contains the sum of the events
     */
    public void addToSum(int adc1, int position){
        if (mpa.size()==16) mpa.add(getADC(adc1));
        else {
        ADC a=getADC(position);
        ADC b=getADC(adc1);
        for (int i=0;i<getADC(adc1).getNEvents();i++) a.addEvent(b.getEvent(i));
        }
    }
    
    /**
     * Processes the sum of 2 ADC and adds it to the specified position in mpa list
     * @param adc1 Index of the first adc to be summed
     * @param adc2 Index of the second adc to be summed
     * @param position Index in the mpa list of recorded adc
     */
    public void sumADC(int adc1, int adc2, int position){
        ADC a=getADC(adc1);
        ADC b=getADC(adc2);
        for (int i=0;i<getADC(adc2).getNEvents();i++) a.addEvent(b.getEvent(i));
        mpa.add(position,a);
    }

}