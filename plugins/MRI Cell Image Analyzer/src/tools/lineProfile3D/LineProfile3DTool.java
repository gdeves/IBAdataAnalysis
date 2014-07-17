package tools.lineProfile3D;

import ij.ImagePlus;
import ij.gui.Line;
import ij.gui.Roi;

import java.util.Observable;

import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

public class LineProfile3DTool extends Observable {
	protected SpinnerNumberModel z1;
	protected SpinnerNumberModel z2;
	protected ImagePlus image;
	protected LineProfile3DToolView view;
	
	public LineProfile3DTool() {
		z1 = new SpinnerNumberModel();
		z1.setMinimum(1);
		z1.setMaximum(1);
		z1.setValue(1);
		z1.setStepSize(1);
		z2 = new SpinnerNumberModel();
		z2.setMinimum(1);
		z2.setMaximum(1);
		z2.setValue(1);
		z2.setStepSize(1);
	}
	public void show() {
		this.getView().setVisible(true);
	}

	protected LineProfile3DToolView getView() {
		if (this.view==null) this.view = new LineProfile3DToolView(this);
 		return view;
	}

	public SpinnerModel getZ1() {
		return z1;
	}
	
	public void setImage(ImagePlus image) {
		this.image = image;
		z2.setMaximum(image.getImageStackSize());
		if (z2.getNumber().intValue()>image.getImageStackSize())
			z2.setValue(image.getImageStackSize());
		if (z1.getNumber().intValue()>z2.getNumber().intValue()) 
			z1.setValue(z2.getNumber().intValue());
	}
	
	public SpinnerModel getZ2() {
		return z2;
	}
	
	public void setView(LineProfile3DToolView aView) {
		this.view = aView;
	}
	
	public void setZ1(int currentSlice) {
		z1.setValue(currentSlice);
		if (z2.getNumber().intValue()<z1.getNumber().intValue())
			z2.setValue(z1.getNumber().intValue());
	}
	public void setZ2(int currentSlice) {
		z2.setValue(currentSlice);
		if (z1.getNumber().intValue()>z2.getNumber().intValue())
			z1.setValue(z2.getNumber().intValue());
	}
	public void createProfile() {
		Roi roi = image.getRoi();
		if (roi.getClass()!=Line.class) return;
		Line line = (Line)roi;
		LineProfile3D profile = new LineProfile3D(line.x1, line.y1, z1.getNumber().intValue(),
												  line.x2, line.y2, z2.getNumber().intValue(),
												  image);
		profile.show();
	}	
}
