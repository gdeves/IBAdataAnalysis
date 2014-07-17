import tools.moleculeLengthAnalyzer.gui.MoleculeLengthAnalyzer;
import ij.plugin.PlugIn;


public class MRI_MoleculeLengthAnalyzer implements PlugIn {

	@Override
	public void run(String arg) {
		new MoleculeLengthAnalyzer().show();
	}

}
