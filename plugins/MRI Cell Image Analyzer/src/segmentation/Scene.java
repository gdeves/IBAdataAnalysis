/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2008 INSERM and CNRS
 * MRI Cell Image Analyzer has been created at Montpellier RIO Imaging, 
 * by Volker B�cker
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
package segmentation;
import ij.ImagePlus;

public class Scene {
	
	protected ImageObject root;
	protected ImagePlus image;
	
	public ImagePlus getImage() {
		return image;
	}
	public void setImage(ImagePlus image) {
		this.image = image;
	}
	public ImageObject getRoot() {
		return root;
	}
	public void setRoot(ImageObject root) {
		this.root = root;
	}

}
