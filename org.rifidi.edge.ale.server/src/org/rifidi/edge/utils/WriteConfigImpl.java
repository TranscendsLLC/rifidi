package org.rifidi.edge.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.llrp.ltk.generated.messages.ADD_ACCESSSPEC;
import org.llrp.ltk.generated.messages.ADD_ROSPEC;
import org.rifidi.edge.epcglobal.ale.ECSpec;
import org.rifidi.edge.epcglobal.alelr.LRSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository("writeConfigImpl")
public class WriteConfigImpl implements WriteConfig {
	
	/**	logger. */
	private static final Logger LOG = Logger.getLogger(WriteConfigImpl.class.getName());

	@Autowired
	private PersistenceConfig config;
	
	@Autowired
	private FileUtils fileUtils;
	
	
	
	public WriteConfigImpl() {
		super();
		System.out.println("WriteConfigImpl() call");
	}

	@Override
	public void writeECSpec(String specName, ECSpec spec) {
		final String path = config.getRealPathECSpecDir();
		System.out.println("WriteConfigImpl.writeECSpec path: " + path);
		final String fileName = specName + ".xml";
			 
		if (!fileUtils.fileExist(fileName, path)) {			
			try {
				
				boolean dirCreated = new File(path).mkdirs();
				
				if (!dirCreated) {
					LOG.debug("cannot create directories or directories already exist : " + path);
				}		
				
				LOG.debug("try to create file for ecspec: " + fileName);
				FileOutputStream fileOutputStream = new FileOutputStream(path + fileName);
				SerializerUtil.serializeECSpec(spec, fileOutputStream);
				LOG.debug("ecspec file " + fileName + " created on path: " + path);	
								
			} catch (FileNotFoundException e) {
				LOG.error("error create ecspec file: " + path + fileName, e);		
			} catch (IOException e) {			
				LOG.error("error serialize ecspec file: " + path + fileName, e);
			} catch (Exception e) {
				LOG.error("error ecspec file: " + path + fileName, e);
			}
		
		} else {			
			LOG.debug("ecspec file " + fileName + " already exist on path: " + path);			
		}
	}

	@Override
	public void writeECSpecSubscriber(String specName, String notificationURI) {
		
		LOG.debug("start create file for ecspec subscriber: " + specName + ".properties");
		
		String path = config.getRealPathECSpecSubscriberDir();
		String fileName = specName + ".properties";
		
		// create file and directory
		if (!fileUtils.fileExist(fileName, path)) {
			
			boolean dirCreated = new File(path).mkdirs();
			
			if (!dirCreated) {
				LOG.debug("cannot create directories or directories already exist : " + path);
			}	
			
			try {
				
				LOG.debug("create properties file for ecspec subscriber: " + specName + ".properties:" + path);
				
				OutputStream outputStream = new FileOutputStream(path + fileName);
				Properties properties = new Properties();				
				properties.store(outputStream, "");
				outputStream.flush();
				outputStream.close();
				
			} catch (IOException e) {
				LOG.error("error create ecspec subscriber file: " + path + fileName, e);		
			}
			
			
		}
				
		// write in the properties file
		try {			
			
			LOG.debug("load properties file for ecspec subscriber: " + specName + ".properties: " + path);
						
			Properties properties = new Properties();	
			FileInputStream fileInputStream = new FileInputStream(path + fileName);	
			properties.load(fileInputStream);
			
			Iterator<Object> it = properties.keySet().iterator();
			int i = 1;
			
			// properties file is empty
			if (!it.hasNext()) {
							
				LOG.debug("properties file empty => add properties uri_" + i + " = " + notificationURI);					
				properties.setProperty("uri_" + i, notificationURI);
				
			// properties file is not empty
			} else {
			
				boolean uriExist = false;
				
				while (it.hasNext()) {
					
					String propertyName = (String)it.next();
					String propertyValue = properties.getProperty(propertyName);
					
					if (propertyValue.equalsIgnoreCase(notificationURI.trim())) {
						LOG.debug("uri already exist => don t add properties uri_" + i + " = " + notificationURI);
						uriExist = true;
						break;
					}
					
					i++;				
					
				}
				
				if (!uriExist) {
					LOG.debug("add properties uri_" + i + " = " + notificationURI);					
					properties.setProperty("uri_" + i, notificationURI);
				}
				
			}
			LOG.debug("save properties file for ecspec subscriber: " + specName + ".properties: " + path);
			
			OutputStream outputStream = new FileOutputStream(path + fileName);				
			properties.store(outputStream, "");
			outputStream.flush();
			outputStream.close();			
			
		} catch (FileNotFoundException e) {						
			LOG.error("error read ecspec subscriber file: " + path + fileName, e);				
		} catch (IOException e) {
			LOG.error("error read ecspec subscriber file: " + path + fileName, e);			
		}
	}

	@Override
	public void writeLRSpec(String specName, LRSpec spec) {
		LOG.debug("start create file for lrspec: " + specName + ".xml");
	
		String path = config.getRealPathLRSpecDir();
		String fileName = specName + ".xml";
			 
		if (!fileUtils.fileExist(fileName, path)) {
			
			try {
				
				boolean dirCreated = new File(path).mkdirs();
				
				if (!dirCreated) {
					LOG.debug("cannot create directories or directories already exist : " + path);
				}		
				
				LOG.debug("try to create file for lrspec: " + fileName);
				SerializerUtil.serializeLRSpec(spec, path + fileName, false);
				LOG.debug("lrspec file " + fileName + " created on path: " + path);	
								
			} catch (FileNotFoundException e) {
				LOG.error("error create lrspec file: " + path + fileName, e);		
			} catch (IOException e) {			
				LOG.error("error serialize lrspec file: " + path + fileName, e);
			}  catch (Exception e) {
				LOG.error("error lrspec file: " + path + fileName, e);
			}
		
		} else {			
			LOG.debug("lrspec file " + fileName + " already exist on path: " + path);			
		}
	}

	@Override
	public void writeAddROSpec(String specName, ADD_ROSPEC addRoSpec) {
		LOG.debug("start write file for add_rospec: " + specName + ".llrp");
		
		String path = config.getRealPathROSpecDir();
		String fileName = specName + ".llrp";
		if (!fileUtils.fileExist(fileName, path)) {
			try {
				boolean dirCreated = new File(path).mkdirs();
				if (!dirCreated) {
					LOG.debug("cannot create directories or directories already exist : " + path);
				}		
				LOG.debug("try to create file for add_rospec: " + fileName);
				SerializerUtil.serializeAddROSpec(addRoSpec, path + fileName);
				LOG.debug("add_rospec file " + fileName + " created on path: " + path);	
								
			} catch (FileNotFoundException e) {
				LOG.error("error create rospec file: " + path + fileName, e);		
			} catch (IOException e) {			
				LOG.error("error serialize rospec file: " + path + fileName, e);
			}
		
		} else {			
			LOG.debug("rospec file " + fileName + " already exist on path: " + path);			
		}
	}

	@Override
	public void writeAddAccessSpec(String specName, ADD_ACCESSSPEC addAccessSpec) {
		LOG.debug("start write file for add_accessspec: " + specName + ".llrp");
		
		String path = config.getRealPathAccessSpecDir();
		String fileName = specName + ".llrp";
		if (!fileUtils.fileExist(fileName, path)) {
			try {
				boolean dirCreated = new File(path).mkdirs();
				if (!dirCreated) {
					LOG.debug("cannot create directories or directories already exist : " + path);
				}		
				LOG.debug("try to create file for add_accessspec: " + fileName);
				SerializerUtil.serializeAddAccessSpec(addAccessSpec, path + fileName);
				LOG.debug("add_accessspec file " + fileName + " created on path: " + path);	
								
			} catch (FileNotFoundException e) {
				LOG.error("error create accessspec file: " + path + fileName, e);		
			} catch (IOException e) {			
				LOG.error("error serialize accessspec file: " + path + fileName, e);
			}
		
		} else {			
			LOG.debug("accessspec file " + fileName + " already exist on path: " + path);			
		}
	}

}
