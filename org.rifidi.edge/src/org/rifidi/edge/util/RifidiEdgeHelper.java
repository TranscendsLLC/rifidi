package org.rifidi.edge.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.management.Attribute;
import javax.management.AttributeList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.service.tagmonitor.ReadZone;
import org.rifidi.edge.exceptions.PropertiesFileNotFoundException;

public class RifidiEdgeHelper implements Serializable {

	private static final String READZONES_FOLDER_NAME = "readzones";

	private static final String READZONE_FILE_NAME_PREFIX = "readzone";

	/** The logger for this class */
	private static final Log logger = LogFactory.getLog(RifidiEdgeHelper.class);

	/**
	 * RifidiApps can store data files in their data directory. The file name
	 * convention is [prefix]-[id].[suffix]. This method returns a map of all
	 * files in the data directory with the given prefix. The key in the hashmap
	 * is the file's id.
	 * 
	 * @param fileNamePrefix
	 *            The prefix of the files to return
	 * @param group
	 *            The name of group
	 * @return
	 */
	public static final HashMap<String, byte[]> getReadzoneDataFiles(
			final String fileNamePrefix, String group) {
		return getReadzoneFiles(fileNamePrefix, "data", group);
	}

	/**
	 * RifidiApps can store data files in their data directory. The file name
	 * convention is [prefix]-[id].[suffix]. This method writes a file to the
	 * data directory. If the file already exists, it will overwrite it.
	 * 
	 * @param filePrefix
	 *            The prefix of the file
	 * @param fileID
	 *            The ID of the file
	 * @param fileSuffix
	 *            The sufix of the file
	 * @param data
	 *            The data to write
	 * @param group
	 *            The group of app
	 */
	private static final void writeData(String filePrefix, String fileID,
			String fileSuffix, byte[] data, String group) {
		String dataDir = getDataDirPath(group, "data");
		String fileName = dataDir + File.separator + filePrefix + "-" + fileID
				+ "." + fileSuffix;
		DataOutputStream os = null;
		try {
			os = new DataOutputStream(new FileOutputStream(fileName));
			os.write(data);
			os.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null)
					os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static final void writeProperties(String fileName, byte[] data) {
		DataOutputStream os = null;
		try {
			//Thread.sleep(3000);
			os = new DataOutputStream(new FileOutputStream(fileName));
			os.write(data);
			os.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} /*catch (InterruptedException iEx) {
			iEx.printStackTrace();
		}*/ finally {
			try {
				if (os != null)
					os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static final void writeReadZoneData(String group,
			String readZoneName, byte[] data) {
		String dataDir = getDataDirPath(group, READZONES_FOLDER_NAME);
		String fileName = dataDir + File.separator + READZONE_FILE_NAME_PREFIX
				+ "-" + readZoneName + ".properties";
		DataOutputStream os = null;
		try {
			os = new DataOutputStream(new FileOutputStream(fileName));
			os.write(data);
			os.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null)
					os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * This is a helper method that does the work of loading the read zones
	 */
	public static HashMap<String, ReadZone> getReadZones(String group) {
		HashMap<String, byte[]> fileMap = getReadzoneFiles(
				READZONE_FILE_NAME_PREFIX, READZONES_FOLDER_NAME, group);
		HashMap<String, ReadZone> readZones = new HashMap<String, ReadZone>();
		for (String readZoneName : fileMap.keySet()) {
			byte[] file = fileMap.get(readZoneName);
			Properties properties = new Properties();
			try {
				properties.load(new ByteArrayInputStream(file));
				readZones
						.put(readZoneName, ReadZone.createReadZone(properties));
			} catch (IOException e) {
			}
		}
		return readZones;
	}

	/**
	 * Get the readzone properties
	 * 
	 * @param groupName
	 *            the name of application group
	 * @param appName
	 *            the name of application
	 * @param readZoneName
	 *            the name of readzone
	 * @return the readzone properties
	 * @throws PropertiesFileNotFoundException
	 *             if cannot get the readzone properties file
	 * @throws IOException
	 *             if cannot load the properties from file
	 */
	public static Properties getReadZoneProperties(String groupName,
			String appName, String readZoneName)
			throws PropertiesFileNotFoundException, IOException {

		byte[] appPropBytes = getReadZonePropertiesFile(groupName, appName,
				readZoneName);

		Properties properties = new Properties();
		properties.load(new ByteArrayInputStream(appPropBytes));
		return properties;
	}

	/**
	 * Deletes readzone
	 * 
	 * @param groupName
	 *            group name of application
	 * @param readZone
	 *            readzone name
	 * @throws Exception
	 *             if readzone does not exist
	 */
	public static void deleteReadZone(String groupName, String readZone)
			throws Exception {

		String dataPath = getDataDirPath(groupName, READZONES_FOLDER_NAME);
		String fileName = dataPath + File.separator + READZONE_FILE_NAME_PREFIX
				+ "-" + readZone + ".properties";

		// check if file exists
		File file = new File(fileName);
		if (!file.exists()) {
			// If not exists, can not delete
			throw new Exception("Readzone " + readZone + " does not exist");
		} else {

			// exists, delete file
			file.delete();
		}

	}

	/**
	 * Get the application properties
	 * 
	 * @param groupName
	 *            application group name
	 * @param appName
	 *            application name
	 * @return the application properties
	 * @throws PropertiesFileNotFoundException
	 *             if cannot get the application properties file
	 * @throws IOException
	 *             if cannot load the properties from file
	 */
	public static Properties getApplicationProperties(String groupName,
			String appName) throws PropertiesFileNotFoundException, IOException {

		byte[] appPropBytes = getApplicationPropertiesFile(groupName, appName);

		Properties properties = new Properties();
		properties.load(new ByteArrayInputStream(appPropBytes));
		return properties;
	}

	/**
	 * Set application properties
	 * 
	 * @param groupName
	 *            group name of application
	 * @param appName
	 *            application name
	 * @param attributes
	 *            attributes to be set
	 * @throws IOException
	 *             if there is an error setting the properties
	 */
	public static void setApplicationProperties(String groupName,
			String appName, AttributeList attributes) throws IOException {

		String dataPath = getDataDirPath(groupName, null);
		String fileName = dataPath + File.separator + appName + ".properties";
		setProperties(fileName, attributes);

	}

	/**
	 * Add a readZone
	 * 
	 * @param groupName
	 *            name of application group
	 * @param readZone
	 *            readzone name to add
	 * @param attributes
	 *            attributes to be set on readzone
	 * @throws IOException
	 *             if there is an error setting the properties on file
	 * @throws Exception
	 *             if readzone already exists
	 */
	public static void addReadZone(String groupName, String readZone,
			AttributeList attributes) throws IOException, Exception {

		String dataPath = getDataDirPath(groupName, READZONES_FOLDER_NAME);
		String fileName = dataPath + File.separator + READZONE_FILE_NAME_PREFIX
				+ "-" + readZone + ".properties";

		// check if file exists
		File file = new File(fileName);
		if (file.exists()) {
			throw new Exception("Readzone " + readZone + " already exists.");
		}

		setProperties(fileName, attributes);

	}

	/**
	 * Set properties for an existing readzone
	 * 
	 * @param groupName
	 *            name of application group
	 * @param readZone
	 *            readzone name to set properties
	 * @param attributes
	 *            attributes to be set on readzone
	 * @throws IOException
	 *             if there is an error setting the properties on file
	 * @throws Exception
	 *             if readzone does not exist
	 */
	public static void setReadZoneProperties(String groupName, String readZone,
			AttributeList attributes) throws IOException, Exception {

		String dataPath = getDataDirPath(groupName, READZONES_FOLDER_NAME);
		String fileName = dataPath + File.separator + READZONE_FILE_NAME_PREFIX
				+ "-" + readZone + ".properties";

		// check if file exists
		File file = new File(fileName);
		if (!file.exists()) {
			throw new Exception("Readzone " + readZone + " does not exist.");
		}

		setProperties(fileName, attributes);

	}

	/**
	 * Set group properties
	 * 
	 * @param groupName
	 *            group name of application
	 * @param attributes
	 *            attributes to be set
	 * @throws IOException
	 *             if there is an error setting the properties
	 */
	public static void setGroupProperties(String groupName,
			AttributeList attributes) throws IOException {

		String dataPath = getDataDirPath(groupName, null);
		String fileName = dataPath + File.separator + groupName + ".properties";
		setProperties(fileName, attributes);

	}

	/**
	 * Set properties in specified properties file
	 * 
	 * @param fileName
	 *            file where properties are going to be set
	 * @param attributes
	 *            the list of properties
	 * @throws IOException
	 *             if there is an error reading the file
	 */
	private static void setProperties(String fileName, AttributeList attributes)
			throws IOException {

		File file = null;
		InputStream ips = null;
		InputStreamReader ipsr = null;
		BufferedReader br = null;
		
		String dataToWrite = null;
		
		try {

			// check if file exists
			file = new File(fileName);
			if (!file.exists()) {
				// If not exists, create it
				file.createNewFile();
			}

			List<String> fileLines = new LinkedList<String>();

			ips = new FileInputStream(fileName);
			ipsr = new InputStreamReader(ips);
			br = new BufferedReader(ipsr);
			String line;
			while ((line = br.readLine()) != null) {
				fileLines.add(line);
			}

			// Iterate properties received to see if they exist in file, and
			// update
			// them,
			// or add to file if they do not exist.
			for (Attribute attribute : attributes.asList()) {

				String propName = attribute.getName();
				boolean attributeFound = false;
				for (String fileLine : fileLines) {
					if (!fileLine.isEmpty() && !fileLine.startsWith("#")) {
						String[] splittedLine = fileLine.split("=");
						if (splittedLine.length > 0) {
							String filePropName = splittedLine[0];
							if (filePropName.trim().equals(propName.trim())) {
								attributeFound = true;
								int index = fileLines.indexOf(fileLine);
								fileLines.set(index, filePropName + "="
										+ attribute.getValue());
							}
						}
					}
				}

				if (!attributeFound) {
					// add new attribute
					fileLines.add(attribute.getName() + "="
							+ attribute.getValue());
				}

			}

			// fileLines has the attributes to be set
			dataToWrite = "";
			for (String data : fileLines) {
				dataToWrite += data + "\n";
			}
			
		} finally {

			//close file resources
			
			if (br != null){
				br.close();
			}
			
			if (ipsr != null){
				ipsr.close();
			}
			
			if (ips != null){
				ips.close();
			}
			
		}

		// Write to app properties file
		if (dataToWrite != null){
			writeProperties(fileName, dataToWrite.getBytes());
		}

	}

	/**
	 * Get the properties for application group
	 * 
	 * @param groupName
	 *            the name of application group
	 * @return the properties for application group
	 * @throws PropertiesFileNotFoundException
	 *             if cannot get properties file
	 * @throws IOException
	 *             if cannot load the properties from file
	 */
	public static Properties getGroupProperties(String groupName)
			throws PropertiesFileNotFoundException, IOException {

		byte[] groupPropBytes = getGroupPropertiesFile(groupName);

		Properties properties = new Properties();
		properties.load(new ByteArrayInputStream(groupPropBytes));
		return properties;
	}

	/**
	 * This is a helper method that does the work of reading in files
	 * 
	 * @param fileNamePrefix
	 * @param dir
	 * @param group
	 * @return
	 */
	private static HashMap<String, byte[]> getReadzoneFiles(
			final String fileNamePrefix, String dir, String group) {
		HashMap<String, byte[]> fileMap = new HashMap<String, byte[]>();

		// the path of the directory to read files from
		String dataPath = getDataDirPath(group, dir);

		// get the files to read in by filtering out the ones we don't want
		File[] dataFiles = new File(dataPath).listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File arg0, String arg1) {
				if (arg0.isHidden())
					return false;
				if (arg1.endsWith("~")) {
					return false;
				}
				return arg1.startsWith(fileNamePrefix);
			}
		});

		// if we did not read in any files
		if (dataFiles == null) {
			return fileMap;
		}

		for (File f : dataFiles) {
						
			byte[] bytes = readContentIntoByteArray(f);
			
			// the file id is in between the '-' and the '.'
			String id = f.getName().substring(f.getName().indexOf('-') + 1,
					f.getName().indexOf('.'));
			fileMap.put(id, bytes);
		
		}
		return fileMap;

	}

	/**
	 * Get the application properties file
	 * 
	 * @param groupName
	 *            group name
	 * @param appName
	 *            application name
	 * @return the application properties file
	 * @throws PropertiesFileNotFoundException
	 *             if properties file for application does not exist
	 */
	private static byte[] getApplicationPropertiesFile(String groupName,
			final String appName) throws PropertiesFileNotFoundException {

		// the path of the directory to read files from
		String dataPath = getDataDirPath(groupName, null);

		// get the files to read in by filtering out the ones we don't want
		File[] dataFiles = new File(dataPath).listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File arg0, String arg1) {
				if (arg0.isHidden())
					return false;
				if (arg1.endsWith("~")) {
					return false;
				}
				return arg1.startsWith(appName);
			}
		});

		// if we did not read in any files
		if (dataFiles == null || dataFiles.length < 1) {
			throw new PropertiesFileNotFoundException(groupName, appName);
		}

		// Assume there is only one application property file
		File f = dataFiles[0];
		return readContentIntoByteArray(f);
	}
	
	public static byte[] getServersFile() throws Exception {

		// the path of the directory to read files from
		String dataPath = getServersDirPath() + File.separator + "servers.json";
		
		File file = new File(dataPath);
	       
		byte[] bytes = readContentIntoByteArray(file);
		return bytes;
		
	}
	
	private static byte[] readContentIntoByteArray(File file)
	   {
	      FileInputStream fileInputStream = null;
	      byte[] bFile = new byte[(int) file.length()];
	      try
	      {
	         //convert file into array of bytes
	         fileInputStream = new FileInputStream(file);
	         fileInputStream.read(bFile);
	         fileInputStream.close();
	         for (int i = 0; i < bFile.length; i++)
	         {
	            System.out.print((char) bFile[i]);
	         }
	      }
	      catch (Exception e)
	      {
	         e.printStackTrace();
	      }
	      return bFile;
	   }


	/**
	 * Get application group properties file
	 * 
	 * @param groupName
	 *            the group name of application
	 * @return the application group properties file
	 * @throws PropertiesFileNotFoundException
	 *             if properties file for application group does not exist
	 */
	private static byte[] getGroupPropertiesFile(final String groupName)
			throws PropertiesFileNotFoundException {

		// the path of the directory to read files from
		String dataPath = getDataDirPath(groupName, null);

		// get the files to read in by filtering out the ones we don't want
		File[] dataFiles = new File(dataPath).listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File arg0, String arg1) {
				if (arg0.isHidden())
					return false;
				if (arg1.endsWith("~")) {
					return false;
				}
				return arg1.startsWith(groupName);
			}
		});

		// if we did not read in any files
		if (dataFiles == null || dataFiles.length < 1) {
			throw new PropertiesFileNotFoundException(groupName);
		}

		// Assume there is only one group property file
		File f = dataFiles[0];
		return readContentIntoByteArray(f);

	}

	/**
	 * Get the readzone properties file
	 * 
	 * @param groupName
	 *            the group name of application
	 * @param appName
	 *            the application name
	 * @param readZoneName
	 *            the readzone name
	 * @return the readzone properties file
	 * @throws PropertiesFileNotFoundException
	 *             if properties file for readzone does not exist
	 */
	private static byte[] getReadZonePropertiesFile(String groupName,
			String appName, final String readZoneName)
			throws PropertiesFileNotFoundException {

		// the path of the directory to read files from
		String dataPath = getDataDirPath(groupName, READZONES_FOLDER_NAME);

		// get the files to read in by filtering out the ones we don't want
		File[] dataFiles = new File(dataPath).listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File arg0, String arg1) {
				if (arg0.isHidden())
					return false;
				if (arg1.endsWith("~")) {
					return false;
				}
				return arg1.startsWith(READZONE_FILE_NAME_PREFIX + "-"
						+ readZoneName);
			}
		});

		// if we did not read in any files
		if (dataFiles == null || dataFiles.length < 1) {
			throw new PropertiesFileNotFoundException(groupName, appName,
					readZoneName);
		}

		// Assume there is only one readzone property file
		File f = dataFiles[0];
		return readContentIntoByteArray(f);
	}

	/**
	 * This method returns the path of the supplied directory name relative to
	 * the application/${groupName} folder
	 * 
	 * @param groupName
	 * @param dir
	 * 
	 * @return
	 */
	private static String getDataDirPath(String groupName, String dir) {
		return System.getProperty("org.rifidi.home") + File.separator
				+ System.getProperty("org.rifidi.edge.applications")
				+ File.separator + groupName + File.separator
				+ (dir != null ? dir : "");
	}
	
	private static String getServersDirPath() {
		return  System.getProperty("org.rifidi.home") + File.separator
				+ "admin" + File.separator + "config";
	}
	

	/**
	 * Sets the content of servers.json
	 * @param content the content to be set in servers.json file
	 * @throws IOException
	 */
	public static void updateServersFile(String content) throws Exception {
		String dataPath = getServersDirPath();
		String fileName = dataPath + File.separator + "servers.json";
		writeToServersFile(fileName, content.getBytes());

	}
	
	
	private static final void writeToServersFile(String fileName, byte[] data) 
			throws Exception {
		DataOutputStream os = null;
		try {
			//Thread.sleep(6000);
			os = new DataOutputStream(new FileOutputStream(fileName));
			os.write(data);
			os.flush();
		}  finally {
			try {
				if (os != null)
					os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

}
