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
package help;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;

import os.OperatingSystemProxy;

import config.Configuration;

/**
 * Create new help files from a template, open help files or edit help files.
 *  
 * @author	Volker Baecker
 **/
public class HelpSystem {
	static protected HelpSystem current; 

	public static HelpSystem getCurrent() {
		if (current == null) {
			current = new HelpSystem(); 
		}
		return current;
	}

	public static void setCurrent(HelpSystem current) {
		HelpSystem.current = current;
	}

	public void openHelpFor(String operatorName) {
		this.openHelpFor(operatorName, null);
	}

	private String fileExtension() {
		return ".html";
	}

	public static String baseFolder() {
		String folder = System.getProperty("user.dir") + "//_help";
		return folder;
	}

	public void openHelpFor(String anOperation, String anOption) {
		String helpFileName = baseFolder() + File.separator + anOperation
				+ fileExtension();
		File helpFile = new File(helpFileName);
		String url = "";
		if (helpFile.exists()) {
			try {
				url = "file://" + helpFile.getCanonicalPath();
				if (anOption != null && !anOption.equals("")) {
					String optionName = anOption.replace(" ", "_");
					url += "#" + optionName;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				BrowserControl.displayURL(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			int answer = JOptionPane.showConfirmDialog(null, "A help for " + anOperation + " doesn't exist. Do you want to create it now?", 
										  "Create help file?", JOptionPane.YES_NO_OPTION);
			if (answer==JOptionPane.NO_OPTION) return;
			this.createNewHelpFile(helpFile, anOperation);
			this.editHelpFile(helpFile);
		}
	}

	private void editHelpFile(File helpFile) {
		String editor = Configuration.current().htmlEditor();
		String[] params = new String[1];
		params[0] = helpFile.getAbsolutePath();
	
			String command = editor + " " + "\"" + helpFile.getAbsolutePath() + "\"";
			System.out.println(command);
			OperatingSystemProxy os = OperatingSystemProxy.current();
			os.execute(command);
			// Runtime.getRuntime().exec("cmd /C " + command);
		
		
	}

	private void createNewHelpFile(File helpFile, String anOperation) {
		File template = new File(baseFolder() + File.separator + helpTemplateFile());
		BufferedReader in = null;
		BufferedWriter out = null;
		try {
			FileReader reader = new FileReader(template);
			FileWriter writer = new FileWriter(helpFile);
			in = new BufferedReader(reader);
			out = new BufferedWriter(writer);
			String line = null;
			while ((line = in.readLine()) != null) {
				if (line.contains("Operation: &lt;name&gt;")) {
					line = line.replace("&lt;name&gt;", anOperation);
				}
				out.write(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in!=null) {
				try {
					in.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (out!=null) {
				try {
					out.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	static public String helpTemplateFile() {
		return "help template.html";
	}
}
