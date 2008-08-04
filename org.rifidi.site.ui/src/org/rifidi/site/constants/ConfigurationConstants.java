/* 
 * ConfigurationConstants.java
 *  Created:	Jul 28, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.site.constants;

/**
 * This class holds all of the constants that are needed for the config.ini
 * file.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class ConfigurationConstants {
	/**
	 * 
	 */
	public static final String OSGI_BUNDLES = "osgi.bundles=";

	/**
	 * 
	 */
	public static final String OSGI_BUNDLES_DEFAULTSTARTLEVEL = "osgi.bundles.defaultStartLevel=1";

	/**
	 * 
	 */
	public static final String PLUGIN_FOLDER_PATH = "../../core/plugins/";

	/**
	 * 
	 */
	public static final String READER_FOLDER_PATH = "../../core/plugins/readers/";

	/**
	 * 
	 */
	public static final String OSGI_FRAMEWORK = "osgi.framework=";

	/**
	 * 
	 */
	public static final String OSGI_CASCADED = "osgi.configuration.cascaded=false";

	/**
	 * 
	 */
	public static final String ECLIPSE_IGNORE_APP = "eclipse.ignoreApp=true";

	/**
	 * 
	 */
	public static final String COMMA = ",";

	/**
	 * 
	 */
	public static final String NEWLINE = "\r\n";
}
