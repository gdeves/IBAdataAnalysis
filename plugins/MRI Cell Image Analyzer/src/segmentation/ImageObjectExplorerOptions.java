package segmentation;

import java.util.ArrayList;
import java.util.Observable;

public class ImageObjectExplorerOptions extends Observable {
	protected ImageObjectExplorerOptionsView view;
	protected boolean sortDescending = true;
	protected ArrayList<String> sortByMeasurements = new ArrayList<String>(); 

	public void show() {
		this.getView().setVisible(true);
	}

	public ImageObjectExplorerOptionsView getView() {
		if (this.view==null) this.view = new ImageObjectExplorerOptionsView(this);
		return view;
	}
	
	public String[] getAvailableMeasurements() {
		return Measurements.availableMeasurements();
	}

	public void setView(ImageObjectExplorerOptionsView view) {
		this.view = view;
	}

	public boolean isSortDescending() {
		return sortDescending;
	}

	public void setSortDescending(boolean sortDescending) {
		this.sortDescending = sortDescending;
	}

	public void addMeasurements(Object[] selectedValues) {
		for (int i=0; i<selectedValues.length; i++) {
			String value = (String) selectedValues[i];
			if (!sortByMeasurements.contains(value)) sortByMeasurements.add(value);
		}
		this.changed("sortByMeasurements");
	}

	public void removeMeasurements(Object[] selectedValues) {
		for (int i=0; i<selectedValues.length; i++) {
			String value = (String) selectedValues[i];
			if (sortByMeasurements.contains(value)) sortByMeasurements.remove(value);
		}
		this.changed("sortByMeasurements");
	}

	public int[] moveMeasurementsUpByOne(Object[] selectedValues) {
		for (int i=0; i<selectedValues.length; i++) {
			String value = (String) selectedValues[i];
			int index = sortByMeasurements.indexOf(value);
			if (index<1) continue;
			sortByMeasurements.remove(value);
			sortByMeasurements.add(index-1, value);
		}
		int[] indices = new int[selectedValues.length];
		for (int i=0; i<selectedValues.length; i++) {
			String value = (String) selectedValues[i];
			int index = sortByMeasurements.indexOf(value);
			indices[i] = index;
		}
		this.changed("sortByMeasurements");
		return indices;
	}

	public int[] moveMeasurementsDownByOne(Object[] selectedValues) {
		for (int i=0; i<selectedValues.length; i++) {
			String value = (String) selectedValues[i];
			int index = sortByMeasurements.indexOf(value);
			if (index>=sortByMeasurements.size()-1) continue;
			sortByMeasurements.remove(value);
			sortByMeasurements.add(index+1, value);
		}
		int[] indices = new int[selectedValues.length];
		for (int i=0; i<selectedValues.length; i++) {
			String value = (String) selectedValues[i];
			int index = sortByMeasurements.indexOf(value);
			indices[i] = index;
		}
		this.changed("sortByMeasurements");	
		return indices;
	}
	
	public void changed(String string) {
		this.setChanged();
		this.notifyObservers(string);
		this.clearChanged();
	}

	public ArrayList<String> getSortByMeasurements() {
		return sortByMeasurements;
	}
}
