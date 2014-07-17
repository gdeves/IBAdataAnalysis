/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2007 Volker Bäcker
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
package objectModelingWorkbench;

import java.awt.image.ColorModel;
import java.util.Observable;

/**
 * The display options of an image in the ObjectModelingWorkbench. The options are
 * the opacity and the lut.
 * 
 * @author Volker Bäcker
 */
public class ImageDisplayOptions extends Observable {
	protected float alpha = 1.0f;
	protected  ColorModel colorModel = null;
	protected ImageDisplayOptionsView view;
	
	public ImageDisplayOptions() {
		super();
	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
		this.changed("alpha");
	}

	public int getAlphaAsInt() {
		int result = Math.round(100 * alpha);
		return result;
	}
	
	protected ImageDisplayOptionsView getView() {
		if (view==null) view = new ImageDisplayOptionsView(this);
		return view;
	}
	
	public void show() {
		this.getView().setVisible(true);
	}
	
	private void changed(String string) {
		this.setChanged();
		this.notifyObservers(string);
		this.clearChanged();
	}

	public ColorModel getColorModel() {
		return colorModel;
	}

	public void setColorModel(ColorModel colorModel) {
		this.colorModel = colorModel;
	}
}
