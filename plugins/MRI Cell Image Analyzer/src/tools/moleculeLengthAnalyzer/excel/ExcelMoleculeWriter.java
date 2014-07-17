/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2009 INSERM 
 * MRI Cell Image Analyzer has been created at Montpellier RIO Imaging
 * (www.mri.cnrs.fr) by Volker Bäcker
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
package tools.moleculeLengthAnalyzer.excel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import tools.moleculeLengthAnalyzer.model.Molecule;
import tools.moleculeLengthAnalyzer.model.Molecules;
import tools.moleculeLengthAnalyzer.model.Spot;

/**
 * Report the detected molecules into an excel file. A molecule consists of two 3d points. 
 * The start point and the end point. The length between start and end point is reported. 
 * 
 * @author baecker
 *
 */
public class ExcelMoleculeWriter {

	private String filename;
	private Molecules molecules;
	private HSSFWorkbook workbook;
	private HSSFSheet sheet;

	public ExcelMoleculeWriter(String filename, Molecules molecules) {
		this.filename = filename;
		this.molecules = molecules;
	}
	
	public void writeFile() {
		workbook = new HSSFWorkbook();
		sheet = workbook.createSheet("molecules");
		this.createHeader();
		this.writeData();
		this.saveFile();
	}

	private void createHeader() {
		HSSFRow firstRow = sheet.createRow((short)0);
		HSSFCell cell = firstRow.createCell((short)0);
		cell.setCellValue("green id");
		cell = firstRow.createCell((short)1);
		cell.setCellValue("green x");
		cell = firstRow.createCell((short)2);
		cell.setCellValue("green y");
		cell = firstRow.createCell((short)3);
		cell.setCellValue("green z");
		cell = firstRow.createCell((short)4);
		cell.setCellValue("-");
		cell = firstRow.createCell((short)5);
		cell.setCellValue("red id");
		cell = firstRow.createCell((short)6);
		cell.setCellValue("red x");
		cell = firstRow.createCell((short)7);
		cell.setCellValue("red y");
		cell = firstRow.createCell((short)8);
		cell.setCellValue("red z");
		cell = firstRow.createCell((short)9);
		cell.setCellValue("-");
		cell = firstRow.createCell((short)10);
		cell.setCellValue("distance");		
	}
	
	private void writeData() {
		int index=1;
		HSSFCell cell = null;
		for (Molecule molecule : molecules) {
			HSSFRow row = sheet.createRow((short)index);
			Spot green = molecule.getGreenSpot();
			Spot red = molecule.getRedSpot();
			cell = row.createCell((short)0);
			cell.setCellValue(green.getId());
			cell = row.createCell((short)1);
			cell.setCellValue(green.getX());
			cell = row.createCell((short)2);
			cell.setCellValue(green.getY());
			cell = row.createCell((short)3);
			cell.setCellValue(green.getZ());
			cell = row.createCell((short)5);
			cell.setCellValue(red.getId());
			cell = row.createCell((short)6);
			cell.setCellValue(red.getX());
			cell = row.createCell((short)7);
			cell.setCellValue(red.getY());
			cell = row.createCell((short)8);
			cell.setCellValue(red.getZ());
			cell = row.createCell((short)10);
			cell.setCellValue(molecule.getLength());	
			index++;
		}
	}
	
	private void saveFile() {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(this.filename);
			workbook.write(out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out!=null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
