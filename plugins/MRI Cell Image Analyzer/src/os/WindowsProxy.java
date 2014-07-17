/*
 * Created on 18.11.2004
 *
 * This file is part of the platform accounting project.
 * The platform accounting project consists of a software that 
 * connects all machines of a platform to a central server. The
 * usage of the machines is registered and stored by the server.
 * Credit has to be bought by users in advance. For each hour a machine
 * is used the credit is decreased according to a tariff that depends
 * on the machine, the date and the time of day.
 * 
 * (c) 2004 by Montpellier RIO Imaging, all rights reserved
 * 
 * Homepage: http://www.mri.cnrs.fr/
 * 
 * created by
 * 		Volker Baecker and Pierre Travo
 * 
 * contact:
 * 	volker.baecker@crbm.cnrs.fr
 *  pierre.travo@crbm.cnrs.fr
 */
package os;

import java.io.IOException;

import config.Configuration;

/**
 * @author Volker
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WindowsProxy extends OperatingSystemProxy {

	public void logout() {
		Configuration config = Configuration.current();
		String logoffCommand = config.logoffCommand();
		if (logoffCommand == null) {
			logoffCommand = "shutdown -l -f";
		} else {
			try {
				Runtime.getRuntime().exec("cmd /C " + logoffCommand);
				return;
			}
			catch(IOException e) {
				System.err.println(e);
			}
		}
		while(true) {
			try {
				Runtime.getRuntime().exec("cmd /C " + logoffCommand);
			}
			catch(IOException e) {
				System.err.println(e);
			}
		}
	}
	
	public void startWindowsManager() {
		try {
			Runtime.getRuntime().exec("cmd /C explorer.exe");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @param string
	 */
	public void execute(String command) {
		try {
			Runtime.getRuntime().exec("cmd /C " + command);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isWindows() {
		return true;
	}
}
