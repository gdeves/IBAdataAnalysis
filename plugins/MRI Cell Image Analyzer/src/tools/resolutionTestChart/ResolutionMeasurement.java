package tools.resolutionTestChart;

public class ResolutionMeasurement {
	protected double value;
	protected String element;
	protected double linesPerMillimeter;
	
	public ResolutionMeasurement(double value, String element, double linesPerMillimeter) {
		this.value = value;
		this.element = element;
		this.linesPerMillimeter = linesPerMillimeter;
	}
	public String getElement() {
		return element;
	}
	
	public double getLinesPerMillimeter() {
		return linesPerMillimeter;
	}
	
	public double getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		String result;
		result = element + " - " + value;
		return result;
	}
}
