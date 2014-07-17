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

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Volker
 *
 * The operating system proxy handles the communication with
 * the underlying operating system.
 */
public class OperatingSystemProxy {
	
	private static OperatingSystemProxy current;
	
	public static OperatingSystemProxy current() {
		if (current==null) {
			String os = operatingSystem().toLowerCase();
			if (os.indexOf("windows")!=-1) {
				current = new WindowsProxy();
			}
			if (os.indexOf("linux")!=-1) {
				current = new UnixProxy();
			}
			if (os.indexOf("mac")!=-1) {
				current = new MacProxy();
			}
		}
		if (current==null) {current = new UnixProxy();}
		return current;
	}
	
	public String username() {
		String username = System.getProperty( "user.name" );
		return username;
	}
	
	public String hostname() throws UnknownHostException {
		InetAddress localHost = InetAddress.getLocalHost();
		return localHost.getHostName();
	}
	
	public String ipAddress() throws UnknownHostException {
		InetAddress localHost = InetAddress.getLocalHost();
		return localHost.getHostAddress();
	}
	
	public void logout() {
		// OS specific 
	}
	
	public static String operatingSystem() {
		String os = System.getProperty( "os.name" );
		return os;
	}
	
	public void startWindowsManager() {
		//		 OS specific 
	}

	/**
	 * @param string
	 */
	public void execute(String string) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @param username
	 */
	public void startAccounting(String username) {
		// TODO Auto-generated method stub	
	}
	
	public boolean isWindows() {
		return false;
	}
	
	public boolean isUnix() {
		return false;
	}
	
	public boolean isMac() {
		return false;
	}
}
