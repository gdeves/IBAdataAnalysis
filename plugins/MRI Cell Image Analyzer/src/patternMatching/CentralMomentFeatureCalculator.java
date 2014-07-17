/*
 * Created on 30.11.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package patternMatching;


/**
 * @author Volker
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CentralMomentFeatureCalculator extends FeatureCalculator {

	/* (non-Javadoc)
	 * @see patternMatching.FeatureCalculator#run()
	 */
	public void run() {
		CentralMomentCalculator calc = new CentralMomentCalculator();
		calc.setImage(this.inputImage);
		calc.calculate();
		result = new FeatureVector(7);
		result.set(0, calc.feature[0]);
		result.set(1, calc.feature[1]);
		result.set(2, calc.feature[2]);
		result.set(3, calc.feature[3]);
		result.set(4, calc.feature[4]);
		result.set(5, calc.feature[5]);
		result.set(6, calc.feature[6]);
	}

}
