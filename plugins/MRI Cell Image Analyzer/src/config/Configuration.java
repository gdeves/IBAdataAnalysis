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
package config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 *  Query the config file of the MRI Cell Image Analyzer. 
 *  
 * @author	Volker Baecker
 **/
public class Configuration {

	private static Configuration current;
	private Properties config;
	
	public Configuration() {
		this.load();
	}
	
	/**
	 * Answer the current configuration instance.
	 * 
	 * @return 	The current configuration instance
	 */
	public static Configuration current() {
		if (current==null) {
			current = new Configuration();
		}
		return current;
	}
	
	public boolean load() {
		boolean result = true;
		this.config = new Properties();
		FileInputStream in = null;
		try {
				in = new FileInputStream("mri_cia_config.txt");
				this.config.load(in);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				result = false;
			} catch (IOException e1) {
				e1.printStackTrace();
				result = false;
			} finally {try {in.close();} catch (IOException e2) {/*ignore*/}
		}
		return result;
	}
	
	public String htmlEditor() {
		return config.getProperty("html_editor");
	}


	public String logoffCommand() {
		return config.getProperty("logoff command");
	}
}
