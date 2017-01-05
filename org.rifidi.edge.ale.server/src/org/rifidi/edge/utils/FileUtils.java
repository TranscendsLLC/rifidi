package org.rifidi.edge.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

@Repository("persistenceFileUtils")
public class FileUtils {

	public static final String FILE_ENDING_XML = "xml";
	public static final String FILE_ENDING_LLRP = "llrp";
	public static final String FILE_ENDING_PROPERTES = "properties";

	/** logger. */
	private static final Logger LOG = Logger.getLogger(FileUtils.class.getName());

	public FileUtils() {
		super();
		System.out.println("FileUtils() call");
	}

	/**
	 * check if a given file exists on the given path.
	 * 
	 * @param fileName
	 *            the filename to check.
	 * @param filePath
	 *            the path where the file should be located.
	 * @return true if the file exists, false otherwise.
	 */
	public boolean fileExist(String fileName, String filePath) {
		File f = new File(filePath + File.separator + fileName);
		if (f.exists()) {
			return true;
		}
		return false;
	}

	/**
	 * check if a given path exists
	 * 
	 * @param filePath
	 *            the path to check
	 * @return true if the path exists, false otherwise.
	 */
	public boolean pathExist(String filePath) {
		File f = new File(filePath);
		if (f.exists()) {
			return true;
		}
		return false;
	}

	/**
	 * get the filenames contained in the given directory.
	 * 
	 * @param directoryPath
	 *            the path of the directory.
	 * @param fileEnding
	 *            the fileEnding of the files. if null, return all the files.
	 * @return a list of all contained filenames.
	 */
	public List<String> getFilesName(String directoryPath, String fileEnding) {
		File[] fileArray = new File(directoryPath).listFiles();
		List<File> fileList = new ArrayList<>();
		if (fileArray != null) {
			fileList = Arrays.asList(fileArray);
		}
		Collections.sort(fileList, new FileComparator());
		ArrayList<String> filesNameList = new ArrayList<String>();

		// if (files != null) {
		for (File file : fileList) {
			String fileName = file.getName();
			if (null == fileEnding) {
				filesNameList.add(fileName);
				LOG.debug("add file " + fileName + " to list to read");
			} else if (fileName.endsWith("." + fileEnding)) {
				filesNameList.add(fileName);
				LOG.debug("add file " + fileName + " to list to read");
			} else {
				LOG.debug("not adding file " + fileName + " to list to read");
			}
		}
		// }

		LOG.debug("list of file: " + filesNameList);
		return filesNameList;
	}

	/**
	 * delete a file from the given path.
	 * 
	 * @param directoryPath
	 *            the directory path.
	 * @param fileName
	 *            the files name.
	 * @return whether the file was deleted or not.
	 */
	public boolean removeFile(String directoryPath, String fileName) {
		return new File(directoryPath + File.separator + fileName).delete();
	}

}
