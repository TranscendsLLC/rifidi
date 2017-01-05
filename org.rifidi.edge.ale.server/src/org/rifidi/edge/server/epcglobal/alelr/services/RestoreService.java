package org.rifidi.edge.server.epcglobal.alelr.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;
import org.rifidi.edge.configuration.ConfigurationService;
import org.rifidi.edge.epcglobal.ale.ECSpec;
import org.rifidi.edge.epcglobal.alelr.ImplementationExceptionResponse;
import org.rifidi.edge.epcglobal.alelr.LRSpec;
import org.rifidi.edge.epcglobal.alelr.NoSuchNameExceptionResponse;
import org.rifidi.edge.epcglobal.alelr.SecurityExceptionResponse;
import org.rifidi.edge.server.epcglobal.ale.services.ApplicationLevelEventService;
import org.rifidi.edge.utils.DeserializerUtil;
import org.rifidi.edge.utils.FileUtils;
import org.rifidi.edge.utils.PersistenceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("restoreService")
public class RestoreService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4017049382707365822L;

	/** logger. */
	private static final Logger LOG = Logger.getLogger(RestoreService.class.getName());

	@Autowired
	private PersistenceConfig config;

	@Autowired
	private FileUtils fileUtils;

	@Autowired
	ReaderService alelrService;

	@Autowired
	ApplicationLevelEventService aleService;

	public RestoreService() {
		super();
		System.out.println("RestoreService() call");
	}

	public List<Exception> restoreLRSpecs() {

		List<Exception> exceptions = new ArrayList<>();
		
		List<String> files = fileUtils.getFilesName(config.getRealPathLRSpecDir(), "xml");
		List<String> restoredFileNames = new ArrayList<>();
		
		for (String fileName : files) {
			try {
				
				String specName = fileName.substring(0, fileName.indexOf(".xml"));
				//if (fileUtils.fileExist(fileName, path)) {
					
					recursiveLRRestore(restoredFileNames, fileName);
					
				//}
				
			} catch (Exception e){
				exceptions.add(e);
			}
		}
		
		return exceptions;
		
	}
	
	private void recursiveLRRestore(List<String> restoredFileNames, String fileName )
			throws ImplementationExceptionResponse, SecurityExceptionResponse, NoSuchNameExceptionResponse,
				Exception{
		
		String path = config.getRealPathLRSpecDir();
		LRSpec spec = DeserializerUtil.deserializeLRSpec(path + fileName);
		//Determine if it is base reader or composite
		
		if ( !spec.isIsComposite() ){
			//It is a base reader
			if ( !restoredFileNames.contains(fileName) ){
				restoreLRSpec(fileName);
				restoredFileNames.add(fileName);
			}
		} else {
			//It is composite reader
			List<String> readers = spec.getReaders().getReader();
			for(String reader : readers){
				LRSpec lrSpec = DeserializerUtil.deserializeLRSpec(path + reader + ".xml");
				
				recursiveLRRestore(restoredFileNames, reader + ".xml");
			}
			//Restore this composite reader
			if ( !restoredFileNames.contains(fileName) ){
				restoreLRSpec(fileName);
				restoredFileNames.add(fileName);
			}
			
		}
		
	}

	public List<Exception> restoreECSpecs() {

		List<Exception> exceptions = new ArrayList<>();
		
		List<String> files = fileUtils.getFilesName(config.getRealPathECSpecDir(), "xml");
		for (String file : files) {
			try{
				restoreECSpec(file);
			} catch (Exception e){
				exceptions.add(e);
			}
		}
		return exceptions;
	}

	public List<Exception> restoreECSubscribers() {
		
		List<Exception> exceptions = new ArrayList<>();

		List<String> files = fileUtils.getFilesName(config.getRealPathECSpecSubscriberDir(), "properties");
		for (String file : files) {
			try{
				restoreECSubscriber(file);
			} catch (Exception e){
				exceptions.add(e);
			}
		}
		return exceptions;
	}

	private void restoreLRSpec(String fileName) {
		LOG.debug("start restoreLRSpec for file: " + fileName);
		System.out.println("start restoreLRSpec for file: " + fileName);

		String path = config.getRealPathLRSpecDir();
		String specName = fileName.substring(0, fileName.indexOf(".xml"));

		if (fileUtils.fileExist(fileName, path)) {

			try {

				LRSpec spec = DeserializerUtil.deserializeLRSpec(path + fileName);
				alelrService.define(specName, spec);

				LOG.debug("lrspec file " + fileName + " restored");
				System.out.println("lrspec file " + fileName + " restored");

			} catch (FileNotFoundException e) {
				LOG.error("error reading lrspec file: " + path + fileName, e);
				System.out.println("error readind lrspec file: " + path + fileName);
				throw new RuntimeException(e);
			} catch (IOException e) {
				LOG.error("error deserialize lrspec file: " + path + fileName, e);
				System.out.println("error deserialize lrspec file: " + path + fileName);
				throw new RuntimeException(e);
			} catch (Exception e) {
				LOG.error("error lrspec file: " + path + fileName, e);
				System.out.println("error lrspec file: " + path + fileName);
				throw new RuntimeException(e);
			}

		}
	}

	private void restoreECSpec(String fileName) {
		LOG.debug("start restoreECSpec for file: " + fileName);
		System.out.println("start restoreECSpec for file: " + fileName);

		String path = config.getRealPathECSpecDir();
		String specName = fileName.substring(0, fileName.indexOf(".xml"));

		if (fileUtils.fileExist(fileName, path)) {

			try {

				ECSpec spec = DeserializerUtil.deserializeECSpec(path + fileName);
				aleService.define(specName, spec);

				LOG.debug("ecspec file " + fileName + " restored");
				System.out.println("ecspec file " + fileName + " restored");

			} catch (FileNotFoundException e) {
				LOG.error("error reading ecspec file: " + path + fileName, e);
				System.out.println("error reading ecspec file: " + path + fileName);
				throw new RuntimeException(e);
			} catch (IOException e) {
				LOG.error("error deserialize ecspec file: " + path + fileName, e);
				System.out.println("error deserialize ecspec file: " + path + fileName);
				throw new RuntimeException(e);
			} catch (Exception e) {
				LOG.error("error ecspec file: " + path + fileName, e);
				System.out.println("error ecspec file: " + path + fileName);
				throw new RuntimeException(e);
			}

		}
	}

	private void restoreECSubscriber(String fileName) {
		LOG.debug("start restoreSubscriber for file: " + fileName);
		System.out.println("start restoreSubscriber for file: " + fileName);

		String path = config.getRealPathECSpecSubscriberDir();
		String specName = fileName.substring(0, fileName.indexOf(".properties"));

		if (fileUtils.fileExist(fileName, path)) {

			try {

				java.util.Properties properties = DeserializerUtil.deserializeProperties(path + fileName);
				Enumeration<?> propertiesEnum = properties.propertyNames();
				while (propertiesEnum.hasMoreElements()) {
					String key = (String) propertiesEnum.nextElement();
					String value = properties.getProperty(key);

					aleService.subscribe(specName, value);

					LOG.debug("subscriber ecspec uri " + value + " restored");
					System.out.println("subscriber ecspec uri " + value + " restored");

				}

				LOG.debug("subscriber ecspec file " + fileName + " restored");
				System.out.println("subscriber ecspec file " + fileName + " restored");

			} catch (FileNotFoundException e) {
				LOG.error("error reading subscriber ecspec file: " + path + fileName, e);
				System.out.println("error reading subscriber ecspec file: " + path + fileName);
				throw new RuntimeException(e);
			} catch (IOException e) {
				LOG.error("error deserialize subscriber ecspec file: " + path + fileName, e);
				System.out.println("error deserialize subscriber ecspec file: " + path + fileName);
				throw new RuntimeException(e);
			} catch (Exception e) {
				LOG.error("error subscriber ecspec file: " + path + fileName, e);
				System.out.println("error subscriber ecspec file: " + path + fileName);
				throw new RuntimeException(e);
			}

		}
	}

}
