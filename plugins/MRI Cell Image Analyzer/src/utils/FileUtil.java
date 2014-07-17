package utils;

public class FileUtil {

	public static String getBaseName(String filename) {
		String resultString = "";
		for (int i=filename.length()-1; i>=0; i--) {
			char current = filename.charAt(i);
			if (!Character.isDigit(current)) resultString = String.valueOf(current) + resultString;
			else continue;
		}
		return resultString;
	}

	public static String getNumberString(String filename) {
		String resultString = "";
		for (int i=filename.length()-1; i>=0; i--) {
			char current = filename.charAt(i);
			if (Character.isDigit(current)) resultString = String.valueOf(current) + resultString;
			else break;
		}
		return resultString;
	}
	
	public static String getShortFilename(String filename) {
		int index = filename.lastIndexOf('.');
		if (index==-1) return filename;
		String result = filename.substring(0, index);
		return result;
	}

}
