package operations.stack;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.ZProjector;
import gui.Options;
import gui.options.ChoiceOption;
import operations.Operation;

public class ZProjectionOperation extends Operation {
	private static final long serialVersionUID = 6696168696008497541L;
	protected ChoiceOption method;

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		// Set parameterTypes, parameterNames and resultType
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		optionsNames = new String[1];
		optionsNames[0] = "method";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultNames = new String[1];
		resultNames[0] = "Result";
	}
	
	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		this.method = new ChoiceOption(ZProjector.METHODS);
		this.method.setName(optionsNames[0]);
		method.setValue(ZProjector.METHODS[0]);
		options.add(this.method);
	}

	public String getMethod() {
		return method.getValue();
	}

	public void setMethod(String method) {
		this.method.setValue(method);
	}
	
	public void connectOptions() {
		this.method = (ChoiceOption) this.options.getOptions().get(0);
	}
	
	public void doIt() {
		result = this.getCopyOfOrReferenceTo(this.getInputImage(), this.getInputImage().getTitle());
		ZProjector projector = new ZProjector();
		projector.setImage(result);
		projector.setMethod(this.indexOfMethod());
		if (result.getType()==ImagePlus.COLOR_RGB) {
			int method = this.indexOfMethod();
			if(method==ZProjector.SUM_METHOD || method==ZProjector.SD_METHOD || method==ZProjector.MEDIAN_METHOD) {
	    		IJ.error("ZProjection", "Sum, StdDev and Median methods \nnot available with RGB stacks."); 
	    		return; 
			}
			projector.doRGBProjection();
		} else 
			projector.doProjection();
		result = projector.getProjection();
	}

	private int indexOfMethod() {
		int i;
		for (i=0; i<ZProjector.METHODS.length; i++) {
			if (this.getMethod().equals(ZProjector.METHODS[i])) break;
		}
		return i;
	}
}
