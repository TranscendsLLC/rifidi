package org.rifidi.edge.util;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.service.tagmonitor.ReadZone;
import org.rifidi.edge.exceptions.PropertiesFileNotFoundException;

public class RifidiEdgeHelper implements Serializable {
	
	private static final String READZONES_FOLDER_NAME = "readzones";
	
	private static final String READZONE_PREFIX_FILE_NAME = "readzone";

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

	/**
	 * This is a helper method that does the work of loading the read zones
	 */
	public static HashMap<String, ReadZone> getReadZones(String group) {
		HashMap<String, byte[]> fileMap = getReadzoneFiles("readzone",
				"readzones", group);
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
	 * @param groupName the name of application group
	 * @param appName the name of application
	 * @param readZoneName the name of readzone
	 * @return the readzone properties
	 * @throws PropertiesFileNotFoundException if cannot get the readzone properties 
	 * file
	 * @throws IOException if cannot load the properties from file
	 */
	public static Properties getReadZoneProperties(String groupName, 
			String appName, String readZoneName)
			throws PropertiesFileNotFoundException, IOException {
		
		byte[] appPropBytes = getReadZonePropertiesFile(groupName, appName, readZoneName);

		Properties properties = new Properties();
		properties.load(new ByteArrayInputStream(appPropBytes));
		return properties;
	}


	/**
	 * Get the application properties
	 * @param groupName application group name
	 * @param appName application name
	 * @return the application properties
	 * @throws PropertiesFileNotFoundException if cannot get the application properties 
	 * file
	 * @throws IOException if cannot load the properties from file
	 */
	public static Properties getApplicationProperties(String groupName,
			String appName) throws PropertiesFileNotFoundException,
			IOException {

		byte[] appPropBytes = getApplicationPropertiesFile(groupName, appName);

		Properties properties = new Properties();
		properties.load(new ByteArrayInputStream(appPropBytes));
		return properties;
	}
	
	
	/**
	 * Get the properties for application group
	 * @param groupName the name of application group
	 * @return the properties for application group
	 * @throws PropertiesFileNotFoundException if cannot get properties file
	 * @throws IOException if cannot load the properties from file
	 */
	public static Properties getGroupProperties(String groupName) 
			throws PropertiesFileNotFoundException,
			IOException {

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
			try {
				// read in the file
				FileInputStream fileStream = new FileInputStream(f);
				FileChannel channel = fileStream.getChannel();
				MappedByteBuffer bb = channel.map(MapMode.READ_ONLY, 0,
						channel.size());
				byte[] bytes = new byte[(int) channel.size()];
				bb.get(bytes);

				// the file id is in between the '-' and the '.'
				String id = f.getName().substring(f.getName().indexOf('-') + 1,
						f.getName().indexOf('.'));
				fileMap.put(id, bytes);
			} catch (FileNotFoundException e) {
				// ignore
				logger.error("Cannot read file: " + f.getAbsolutePath());
			} catch (IOException e) {
				// ignore
				logger.error("Cannot read file: " + f.getAbsolutePath());
			}

		}
		return fileMap;

	}

	/**
	 * Get the application properties file
	 * @param groupName group name
	 * @param appName application name
	 * @return the application properties file
	 * @throws PropertiesFileNotFoundException if properties file for application 
	 * does not exist  
	 */
	private static byte[] getApplicationPropertiesFile(String groupName,
			final String appName)
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
				return arg1.startsWith(appName);
			}
		});

		// if we did not read in any files
		if (dataFiles == null || dataFiles.length < 1) {
			throw new PropertiesFileNotFoundException(groupName,
					appName);
		}

		// Assume there is only one application property file
		File f = dataFiles[0];

		FileInputStream fileStream = null;

		try {
			// read in the file
			fileStream = new FileInputStream(f);
			FileChannel channel = fileStream.getChannel();
			MappedByteBuffer bb = channel.map(MapMode.READ_ONLY, 0,
					channel.size());
			byte[] bytes = new byte[(int) channel.size()];
			bb.get(bytes);

			return bytes;
		} catch (FileNotFoundException e) {
			throw new PropertiesFileNotFoundException(groupName,
					appName);
		} catch (IOException e) {
			throw new PropertiesFileNotFoundException(groupName,
					appName);
		} finally {
			try {
				if (fileStream != null) {
					fileStream.close();
				}
			} catch (IOException e) {

			}
		}
	}
	
	
	/**
	 * Get application group properties file 
	 * @param groupName the group name of application
	 * @return the application group properties file
	 * @throws PropertiesFileNotFoundException if properties file for application 
	 * group does not exist 
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

		FileInputStream fileStream = null;

		try {
			// read in the file
			fileStream = new FileInputStream(f);
			FileChannel channel = fileStream.getChannel();
			MappedByteBuffer bb = channel.map(MapMode.READ_ONLY, 0,
					channel.size());
			byte[] bytes = new byte[(int) channel.size()];
			bb.get(bytes);

			return bytes;
		} catch (FileNotFoundException e) {
			throw new PropertiesFileNotFoundException(groupName);
		} catch (IOException e) {
			throw new PropertiesFileNotFoundException(groupName);
		} finally {
			try {
				if (fileStream != null) {
					fileStream.close();
				}
			} catch (IOException e) {

			}
		}

	}
	
	/**
	 * Get the readzone properties file
	 * @param groupName the group name of application
	 * @param appName the application name
	 * @param readZoneName the readzone name
	 * @return the readzone properties file
	 * @throws PropertiesFileNotFoundException if properties file for readzone 
	 * does not exist
	 */
	private static byte[] getReadZonePropertiesFile(String groupName, String appName,
			final String readZoneName)
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
				return arg1.startsWith(READZONE_PREFIX_FILE_NAME + "-" + readZoneName);
			}
		});

		// if we did not read in any files
		if (dataFiles == null || dataFiles.length < 1) {
			throw new PropertiesFileNotFoundException(groupName,
					appName, readZoneName);
		}

		// Assume there is only one readzone property file
		File f = dataFiles[0];

		FileInputStream fileStream = null;

		try {
			// read in the file
			fileStream = new FileInputStream(f);
			FileChannel channel = fileStream.getChannel();
			MappedByteBuffer bb = channel.map(MapMode.READ_ONLY, 0,
					channel.size());
			byte[] bytes = new byte[(int) channel.size()];
			bb.get(bytes);

			return bytes;
		} catch (FileNotFoundException e) {
			throw new PropertiesFileNotFoundException(groupName,
					appName, readZoneName);
		} catch (IOException e) {
			throw new PropertiesFileNotFoundException(groupName,
					appName, readZoneName);
		} finally {
			try {
				if (fileStream != null) {
					fileStream.close();
				}
			} catch (IOException e) {

			}
		}
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

}
