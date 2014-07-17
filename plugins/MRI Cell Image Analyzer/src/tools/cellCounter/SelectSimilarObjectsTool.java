/*
 * Created on 30.11.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package tools.cellCounter;

import java.util.Vector;

/**
 * @author Volker
 *
 */
public class SelectSimilarObjectsTool {
	Vector<ObjectClass> allObjectClasses;
	ObjectClass classToSearchIn;
	/**
	 * @param objectClass
	 * @param objectClasses
	 */
	public SelectSimilarObjectsTool(ObjectClass objectClass, Vector<ObjectClass> objectClasses) {
		super();
		this.allObjectClasses = objectClasses;
		this.classToSearchIn = objectClass;
	}

	public String[] getObjectClassNames() {
		String[] names = new String[allObjectClasses.size()-1];
		int index = 0;
		for(ObjectClass current : allObjectClasses) {
			names[index] = current.getName();
			index++;
		}
		return names;
	}
}
