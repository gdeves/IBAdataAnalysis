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

import java.util.Iterator;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import tools.moleculeLengthAnalyzer.model.Spot;
import tools.moleculeLengthAnalyzer.model.Spots;
import tools.moleculeLengthAnalyzer.progress.ProgressIndicator;

/**
 * Read 3d spots defined in an excel-sheet into a list. 
 * 
 * @author baecker
 *
 */
public class ExcelSpotReader extends ExcelReader {
	private Spots spots;
	private MoleculeLengthAnalyzerProperties props = MoleculeLengthAnalyzerProperties.getInstance();
	private HSSFSheet sheet;
	private short xIndex;
	private short yIndex;
	private short zIndex;
	private short idIndex;
	private short unitIndex;
	private String unit;

	public ExcelSpotReader(String filename) {
		super(filename);
		spots = new Spots();
	}

	private void loadWorkbookAndDoSetUp() {
		this.setupWorkbook();
		sheet = workbook.getSheet(props.getWorkSheet());
		if (sheet==null) 
			getProgressIndicator().stopWithError();
		spots = new Spots();
		this.setupColumnIndices();
		this.setupUnit();
	}
	
	@SuppressWarnings("unchecked")
	private void setupColumnIndices() {
		HSSFRow firstRow = sheet.getRow(0);
		Iterator<HSSFCell> cells = firstRow.cellIterator();
		short index = 0;
		while(cells.hasNext()) {
			HSSFCell cell = cells.next();
			if (cell.getStringCellValue().equalsIgnoreCase(props.getXColumn())) xIndex = index;
			if (cell.getStringCellValue().equalsIgnoreCase(props.getYColumn())) yIndex = index;
			if (cell.getStringCellValue().equalsIgnoreCase(props.getZColumn())) zIndex = index;
			if (cell.getStringCellValue().equalsIgnoreCase(props.getIdColumn())) idIndex = index;
			if (cell.getStringCellValue().equalsIgnoreCase(props.getUnitColumn())) unitIndex = index;
			index++;
		}
	}

	@SuppressWarnings("unchecked")
	public void execute() {
		loadWorkbookAndDoSetUp();
		ProgressIndicator progress = this.getProgressIndicator(); 
		Iterator<HSSFRow> rows = sheet.rowIterator();
		rows.next();
		int numberOfDataRows = sheet.getLastRowNum();
		int index = 1;
		progress.setMaxProgress(numberOfDataRows);
		while(rows.hasNext()) {
			HSSFRow row = rows.next();
			int id = (int)row.getCell(idIndex).getNumericCellValue();
			double x = row.getCell(xIndex).getNumericCellValue();
			double y = row.getCell(yIndex).getNumericCellValue();
			double z = row.getCell(zIndex).getNumericCellValue();
			spots.add(new Spot(id, x, y, z));
			progress.setProgress(index);
			index++;
		}
	}

	
	private void setupUnit() {
		HSSFRow row = sheet.getRow(1);
		HSSFCell cell = row.getCell(unitIndex); 
		unit = cell.getStringCellValue();
	}
	
	public Spots getSpots() {
		return spots;
	}
	
	public String getUnit() {
		return unit;
	}
}
