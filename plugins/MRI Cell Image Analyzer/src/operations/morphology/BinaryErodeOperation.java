package operations.morphology;

public class BinaryErodeOperation extends BinaryMorphologyOperation {
	private static final long serialVersionUID = 6385047479110999442L;

	@Override
	protected String getCommand() {
		return "Erode";
	}
}