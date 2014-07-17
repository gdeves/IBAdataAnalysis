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
package tools.frap;

import java.io.File;
import java.util.Comparator;

import utils.FileUtil;

/**
 *  Compare filenames in a way that the basename is compared and
 *  then trailing numbers are compared by their integer values. This
 *  makes test10 come after test1, test2 and so on.
 *  
 * @author	Volker Baecker
 **/
public class FileComparator implements Comparator<Object> {

	public int compare(Object file1, Object file2) {
		String filename1 = "";
		String filename2 = "";
		if (file1.getClass()==String.class) filename1 = (String)file1;
		else
			filename1 = ((File)file1).getName();
		if (file2.getClass()==String.class) filename2 = (String)file2; 
		else filename2 = ((File)file2).getName();
		
		String shortFilename1 = FileUtil.getShortFilename(filename1);
		String shortFilename2 = FileUtil.getShortFilename(filename2);
		
		String basename1 = FileUtil.getBaseName(shortFilename1);
		String basename2 = FileUtil.getBaseName(shortFilename2);
		
		String numberString1 = FileUtil.getNumberString(shortFilename1);
		String numberString2 = FileUtil.getNumberString(shortFilename2);
		
		int baseComparison = basename1.compareTo(basename2);
		if (baseComparison!=0 || numberString1.length()==0 || numberString2.length()==0) return baseComparison;
		
		int number1 = Integer.parseInt(numberString1);
		int number2 = Integer.parseInt(numberString2);
		
		if (number1<number2) return -1;
		if (number1>number2) return 1;
		
		return 0;
	}
}
