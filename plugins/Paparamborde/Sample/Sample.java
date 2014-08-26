/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Paparamborde.Sample;

import Paparamborde.Layer.Layer;
import Paparamborde.STIMspectra.STIMspectra;
import java.util.ArrayList;


/**
 *
 * @author deves, CENBG
 */
public class Sample extends Layer{
    //declaration
    ArrayList <Layer> sample = new ArrayList <Layer>();
    ArrayList <Integer> counts=new ArrayList <Integer>();
    float [] layerMass=new float[6];
    
    
    //constructor
    public Sample(){
        
    }
    public Sample (int nLayers){
      for (int i=0;i<nLayers;i++) {
          sample.add(new Layer());
          
      }
      counts.add(new Integer(0));
    }
    
     
    //Setters
    public void setLayers(Layer layer1,Layer layer2){
        
            sample.add(layer1);
            sample.add(layer2);
    }
    public void setLayer (int nLayer, Layer layer){
        sample.set(nLayer, layer);
    }
    public void setCounts(STIMspectra spectra){
       this.counts=(ArrayList<Integer>)spectra.getCountsArray().clone();
    }
    public void setLayerMPC(int nLayer, ArrayList <Float> MPC){
        sample.get(nLayer).setMPCArray(MPC);
    }
    
    
       
    
    //Getters
    public Layer getLayer(int nLayer){
        return sample.get(nLayer);
    }
    public int getNLayers(){
        return sample.size();
    }
    public float getChannelMass(int layer, int channel){
        return sample.get(layer).getMPC(channel);
    }
    public float getMass(int nLayer){
       float t=0;
       for (int i=1;i<sample.get(nLayer).getMPCArray().size();i++){
           t+=sample.get(nLayer).getMPC(i)*counts.get(i);
           
       }
       return (t/getNCounts());
    }
    public float getShiftedMass(ArrayList <Float> MPC, int shift){
        float t=0;
        for (int i=0;i<MPC.size()-shift;i++) t+=MPC.get(i+shift)*counts.get(i);
        return t/getNCounts();
    }
    public float getCorrectedMass(ArrayList <Float> MPC, int shift){
        float t=0;
        for (int i=shift;i<MPC.size();i++) t+=MPC.get(i)*counts.get(i-shift);
        return t/getNCounts();
    }
    public float [] getMass(int nLayer, float limit){
        layerMass[1]=0;
        layerMass[2]=0;
        ArrayList <Float> t0=(ArrayList<Float>)(sample.get(0).getMPCArray()).clone();
        ArrayList <Float> t1=(ArrayList<Float>)(sample.get(1).getMPCArray()).clone();
        ArrayList <Float> dt0= new ArrayList<Float>();
        ArrayList <Float> dt1= new ArrayList<Float>();
        
        //initialize
            for (int i=0;i<t0.size()-1;i++) {
                if (t0.get(i)>0) dt0.add(t0.get(i)-t0.get(i+1));
                else dt0.add((float)0);
            }
            dt0.add((float)0);
            for (int i=0;i<t1.size()-1;i++) {
                if (t1.get(i)>0) dt1.add(t1.get(i)-t1.get(i+1));
                else dt1.add((float)0);
            }
            dt1.add((float)0);
            
        //starting mass with only support
        for (int i=0;i<t1.size();i++) layerMass[2]=layerMass[2]+t1.get(i)*counts.get(i);
        layerMass[2]/=getNCounts();
        int start=1;
        while (layerMass[2]>limit){
            for (int i=t1.size()-1;i>0;i--){
                if (i>(t1.size()-1-start)) t1.set(i, t0.get(i));
                else if (t1.get(i)>0) t1.set(i, t1.get(i+1)+dt1.get(i));
            }
            for (int i=0;i<t1.size();i++) layerMass[2]=layerMass[2]+t1.get(i)*counts.get(i);
            layerMass[2]/=getNCounts();
            layerMass[1]=t0.get(t1.size()-start);
            layerMass[2]-=layerMass[1];
            start+=1;
        }
        layerMass[0]=start;
        //********************************************************************
        //second way to calculate
        //********************************************************************
        /*
        layerMass[5]=0;
        t0=(ArrayList<Float>)(sample.get(0).getMPCArray()).clone();
        //starting mass with only support
        for (int i=0;i<t1.size();i++) layerMass[4]=layerMass[4]+t0.get(i)*counts.get(i);
        layerMass[4]/=getNCounts();
        start=1;
        while ((layerMass[5]<limit)&&(layerMass[4]>0)){
            layerMass[5]=getCorrectedMass(sample.get(0).getMPCArray(),0)-getCorrectedMass(sample.get(0).getMPCArray(),start);
            layerMass[4]=getCorrectedMass(sample.get(0).getMPCArray(),start);
            start+=1;
        }
        layerMass[3]=start;
        */
        return layerMass;
    }
    

    public float getNCounts(){
        float t=0;
        for (int i=1;i<counts.size();i++) t+=counts.get(i);
        return t;
    }
    
    //Methods
    public void clear(){
        sample.clear();
        counts.clear();
    }
    public int addNLayers(int nLayer){
        for (int i=0;i<nLayer;i++) sample.add(new Layer());
        if (counts.isEmpty()) counts.add(new Integer(0));
        return sample.size();
    }
    public void addLayer(Layer layer){
        sample.add(layer);
        if (counts.isEmpty()) counts.add(new Integer(0));
        //return sample.size();
    }
    public int addLayer(int nLayer,Layer layer){
        if (sample.size()<=nLayer) sample.add(layer);
        else sample.set(nLayer, layer);
        if (counts.isEmpty()) counts.add(new Integer(0));
        return sample.size();
    }

}
