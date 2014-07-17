package operations.morphology;

public class BinaryDilateOperation extends BinaryMorphologyOperation {
	private static final long serialVersionUID = -6308197745098038196L;

	@Override
	protected String getCommand() {
		return "Dilate";
	}

}
