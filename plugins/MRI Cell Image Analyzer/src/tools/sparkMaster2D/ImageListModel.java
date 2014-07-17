package tools.sparkMaster2D;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Vector;
import javax.swing.AbstractListModel;

public class ImageListModel extends AbstractListModel {
	private static final long serialVersionUID = -8758029999944614317L;
	ArrayList<String> images = new ArrayList<String>();

	public Object getElementAt(int index) {
		return images.get(index);
	}

	public int getSize() {
		return images.size();
	}

	public void addAll(Vector<File> vector) {
		HashSet<String> files = new HashSet<String>();
		for(File aFile : vector) {
			String fileName = aFile.getAbsolutePath();
			String basename = this.getBasename(fileName);
			files.add(basename);
		}
		images.addAll(files);
		Collections.sort(images);
		fireContentsChanged(this, 0, images.size());
	}

	private String getBasename(String fileName) {
		String basename = fileName.substring(0, fileName.lastIndexOf('.'));
		int index= basename.length()-1;
		for (int i=basename.length()-1; i>=0; i--) {
			if (!Character.isDigit(basename.charAt(i))) {
				index = i;
				break;
			}
		}
		return basename.substring(0, index+1);
	}

	public void removeImages(Object[] selectedValues) {
		images.removeAll(Arrays.asList(selectedValues));
		fireContentsChanged(this, 0, images.size());
	}

}
