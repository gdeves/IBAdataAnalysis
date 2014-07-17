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

import java.io.IOException;

import no.geosoft.cc.io.Browser;

/**
 * Open a file in a webbrowser on windows and unix systems.
 * This code comes from http://www.javaworld.com/javaworld/javatips/jw-javatip66.html
 *  
 * @author	Volker Baecker
 **/
public class BrowserControl {
    
	 /**
     * Display a file in the system browser.  If you want to display a
     * file, you must include the absolute path name.
     *
     * @param url the file's url (the url must start with either "http://"
     * or "file://").
	 * @throws IOException 
	 * @throws IOException 
     */
    public static void displayURL(String url) throws IOException
    {
    	Browser.openUrl(url);
    }
}
