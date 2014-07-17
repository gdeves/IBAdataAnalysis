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
package operations.image;

import gui.options.Option;

/**
 * The abstract superclass of all image math operations that use a constant parameter.
 * 
 * @author Volker
 */
abstract public class ImageMathWithParameterOperation extends ImageMathOperation {
	private static final long serialVersionUID = -360038230391253212L;
	
	protected Option value;
	
	public ImageMathWithParameterOperation() {
		super();
	}
	
	protected void initialize() throws ClassNotFoundException  {
		super.initialize();
		optionsNames = new String[1];
		optionsNames[0] = "output type";
	}
	
	public double getValue() {
		return value.getDoubleValue();
	}

	public void setValue(double value) {
		this.value.setValue(Double.toString(value));
	}

	public void connectOptions() {
		value = (Option) options.getOptions().get(0);
	}
}
