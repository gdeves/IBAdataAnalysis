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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import tools.moleculeLengthAnalyzer.progress.DefaultProgressIndicator;
import tools.moleculeLengthAnalyzer.progress.ProgressIndicator;

/**
 * The superclass of classes that want to read excel files. The setupWorkbook actually reads the data into memory. 
 * Can be run in a thread and report progress to an object whose class implements ProgressIndicator
 * 
 * @author baecker
 *
 */
public abstract class ExcelReader implements Runnable {

	private String filename;
	protected HSSFWorkbook workbook;
	protected ProgressIndicator progressIndicator;
	
	public ProgressIndicator getProgressIndicator() {
		if (progressIndicator==null) progressIndicator = new DefaultProgressIndicator();
		return progressIndicator;
	}

	public void setProgressIndicator(ProgressIndicator progressIndicator) {
		this.progressIndicator = progressIndicator;
	}

	public ExcelReader(String filename) {
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	public HSSFWorkbook getWorkbook() {
		return workbook;
	}
	
	protected void setupWorkbook() {
		InputStream input = null;
		try {
			input = new FileInputStream(new File(this.getFilename()));
			POIFSFileSystem fs = new POIFSFileSystem( input );
			this.workbook = new HSSFWorkbook(fs);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			progressIndicator.stopWithError();
		} catch (IOException e) {
			e.printStackTrace();
			progressIndicator.stopWithError();
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		this.execute();
	}
	
	abstract public void execute();
}
