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
package gui.options;


/**
 * An option of the visual scripting framework . The value of the option is a matrix of
 * numbers.
 * 
 * @author	Volker Baecker
 **/
public class MatrixOption extends Option {

	private static final long serialVersionUID = 7029996532649990961L;

	public boolean isMatrix() {
		return true;
	}
	
	public float[] getMatrixValue() {
		String[] lines = this.getValue().split(";");
		float[] result = new float[this.height()*this.width()];
		for(int i=0; i<lines.length;i++) {
			String line = lines[i];
			String[] values = line.split(",");
			for (int j=0; j<values.length;j++) {
				String aValue = values[j];
				float value = Float.parseFloat(aValue);
				result[(i * this.width()) +j ] = value;
			}
		}
		return result;
	}
	
	public int height() {
		return this.getValue().split(";").length;
	}
	
	public int width() {
		return this.getValue().split(";")[0].split(",").length;
	}
}
