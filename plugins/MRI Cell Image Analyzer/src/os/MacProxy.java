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

/**
 * @author Volker
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MacProxy extends OperatingSystemProxy {
	public void logout() {
		String logoffCommand = "logout.scpt";
		try {
			Runtime.getRuntime().exec("osascript " + logoffCommand);
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isMac() {
		return true;
	}
}

