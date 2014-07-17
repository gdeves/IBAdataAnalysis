/*
 * Created on 30.11.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package patternMatching;

import java.io.IOException;
import java.io.Writer;

/**
 * @author Volker
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FeatureVector {
	int size;
	double[] vector; 
	
	public FeatureVector(int size) {
		this.size = size;
		vector = new double[size];
	}
	
	public boolean set(int index, double value) {
		if (index<0 || index>=size) return false;
		vector[index] = value;
		return true;
	}
	
	public double get(int index) {
		return vector[index];
	}
	
	public int size() {
		return size;
	}

	/**
	 * @param output
	 * @throws IOException
	 */
	public void putOn(Writer output) throws IOException {
		for (int i=0; i<size; i++) {
			output.write(Double.toString(vector[i]));
			if (i<size-1) {
				output.write(", ");
			}
		}
		output.write("\n");
	}

	/**
	 * @param featureVector
	 * @return
	 */
	public double distanceTo(FeatureVector featureVector) {
		double[] components = new double[size];
		for (int i=0; i<size; i++) {
			components[i] = (featureVector.get(i) - this.get(i)) * (featureVector.get(i) - this.get(i));
		}
		double sum = 0;
		for (int i=0; i<size; i++) {
			sum += components[i];
		}
		double result = Math.sqrt(sum);
		return result;
	}
}
