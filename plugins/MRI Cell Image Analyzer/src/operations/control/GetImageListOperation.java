/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2007 Volker B�cker
 * MRI Cell Image Analyzer has been created at Montpellier RIO Imaging
 * www.mri.cnrs.fr
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 */
package operations.control;

import java.io.File;
import java.util.Vector;

/** 
 * Ask the user to provide a list of images. The list will be available to other operations 
 * as a result of this operation. The operation can be used to provide the input for the
 * auto align slices and calculate alignment translations operations. 
 *  
 * @author Volker Baecker
 */
public class GetImageListOperation extends ForeachImageInListDoOperation {
	private static final long serialVersionUID = 839373886566497170L;
	protected String filename;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[0];
		parameterNames = new String[0];
		optionsNames = new String[2];
		optionsNames[0] = "image list";
		optionsNames[1] = "use sequence opener";
		resultTypes = new Class[2];
		resultTypes[0] = Class.forName("java.util.Vector");
		resultTypes[1] = Class.forName("java.lang.String");
		resultNames = new String[2];
		resultNames[0] = "ImageList";
		resultNames[1] = "Filename";
	}
	
	public void doIt() {
		if (this.getImageList().size()>0) filename = ((File)(this.getImageList().elementAt(0))).getAbsolutePath();
	}

	public boolean isLoop() {
		return false;
	}
	
	public void setImageList(Vector<File> list) {
		// noop
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}	
}
