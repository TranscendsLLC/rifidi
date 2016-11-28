package org.rifidi.edge.utils;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service("persistenceConfig")
public class PersistenceConfig {
	
	/**	logger. */
	private static final Logger LOG = Logger.getLogger(PersistenceConfig.class.getName());
	
	private static final String LR_SPECS_WEBAPP_PATH = File.separator + "lrspecs" + File.separator;
	private static final String EC_SPECS_WEBAPP_PATH = File.separator + "ecspecs" + File.separator;
	private static final String EC_SPEC_SUBSCRIBER_WEBAPP_PATH = File.separator + "subscriber" + File.separator + "ecspec" + File.separator;
	private static final String RO_SPECS_WEBAPP_PATH = File.separator + "rospecs" + File.separator;
	private static final String ACCESS_SPECS_WEBAPP_PATH = File.separator + "accessspecs" + File.separator;
	private static final String LLRP_WEBAPP_PATH = File.separator + "llrp" + File.separator;
	
	/**
     *  default path, modified by servlet startup
     *  @see InitPersistence 
     */
//	private String realPathWebapp = null;
//	private String realPathWebapp = "file:///" + System.getProperty("org.rifidi.home") + File.separator + "config" + File.separator;
	private String realPathWebapp = System.getProperty("org.rifidi.home") + File.separator + "config" + File.separator;
		
	
	public PersistenceConfig() {
		super();
		System.out.println("PersistenceConfig() call");
	}

	/**
	 * this method is called at the startup of the application server by the PersistenceServlet and set the real path of the webapp
	 * @param realPathWebapp
	 */
	public void setRealPathWebapp(String realPathWebapp) {
		LOG.debug("set real path of webapp: " + realPathWebapp);
		this.realPathWebapp = realPathWebapp;
	}
	
	/**
	 * @return absolute path of the web applications deployment directory.
	 */
	public String getRealPathWebapp() {
		return realPathWebapp;		
	}
	
	/**
	 * @return absolute path to the LRSpecs persistence path.
	 */
	public String getRealPathLRSpecDir() {
		return realPathWebapp + LR_SPECS_WEBAPP_PATH;
	}
	
	/**
	 * @return absolute path to the LRSpecs persistence path.
	 */
	public String getRealPathECSpecDir() {
		return realPathWebapp +EC_SPECS_WEBAPP_PATH;
	}
	
	/**
	 * @return absolute path to the ECSpecSubscriber persistence path.
	 */
	public String getRealPathECSpecSubscriberDir() {
		return realPathWebapp + EC_SPEC_SUBSCRIBER_WEBAPP_PATH;
	}

	/**
	 * @return absolute path to the RoSpec persistence path.
	 */
	public String getRealPathROSpecDir() {
		return realPathWebapp + RO_SPECS_WEBAPP_PATH;
	}
	
	/**
	 * @return absolute path to the AccessSpec persistence path.
	 */
	public String getRealPathAccessSpecDir() {
		return realPathWebapp + ACCESS_SPECS_WEBAPP_PATH;
	}
	
	/**
	 * @return absolute path to the LLRP messages persistence path.
	 */
	public String getRealPathLLRPSpecDir() {
		return realPathWebapp + LLRP_WEBAPP_PATH;
	}
	
}
