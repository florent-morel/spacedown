package utils;

import java.io.File;

public class Utils {

	/**
	 * Create folder if not exists
	 * 
	 * @param folderName
	 */
	public static void createFolder(String folderName) {
		File folder = new File(folderName);
		// create output folder
		if (!folder.exists()) {
			folder.mkdir();
		}
	}
}
