package workspace.neuronJ;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;
import java.util.Observer;

import neuronJNewGUI.NeuronJ;
import neuronJgui.NJ;
import neuronJgui.NeuronJ_;
import ij.IJ;
import ij.ImageJ;

public class TracingHandlerWorkspace implements Observer, MouseListener {

	protected NeuronJ neuronj;
	protected ImageJ imageJ;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TracingHandlerWorkspace thw = new TracingHandlerWorkspace();
		thw.run();
	}

	protected void run() {
		imageJ = new ImageJ();
		imageJ.setVisible(true);
		(new NeuronJ_()).run("");
		NJ.changeBroadcaster.addObserver(this);
	}

	public void update(Observable arg0, Object arg1) {
		System.out.println(arg0 + "-" + arg1);
		if (arg1.equals("image:loaded")) {
			IJ.getImage().getWindow().getCanvas().addMouseListener(this);
		}
	}

	public void mouseClicked(MouseEvent arg0) {
		System.out.println("mouse clicked");
	}

	public void mousePressed(MouseEvent arg0) {
		System.out.println("mouse pressed");
		
	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
