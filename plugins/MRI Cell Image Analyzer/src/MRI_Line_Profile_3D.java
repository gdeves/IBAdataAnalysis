/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2008 INSERM and CNRS
 *
 * MRI Cell Image Analyzer has been created at Montpellier RIO Imaging
 * www.mri.cnrs.fr, by Volker Bäcker
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
import tools.lineProfile3D.LineProfile3DTool;
import ij.plugin.PlugIn;
import imagejProxies.MRIJava2;

public class MRI_Line_Profile_3D implements PlugIn {
	private static final long serialVersionUID = -8470742486264306061L;

	public void run(String arg) {
		MRIJava2.setLookAndFeelSet(true);
		LineProfile3DTool tool = new LineProfile3DTool();
		tool.show();
	}
	

}
