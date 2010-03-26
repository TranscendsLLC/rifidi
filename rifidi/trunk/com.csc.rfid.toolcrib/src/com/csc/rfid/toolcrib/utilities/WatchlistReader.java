/*
 *  WatchlistReader.java
 *
 *  Created:	Mar 5, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package com.csc.rfid.toolcrib.utilities;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This file reads in the list of tags which are "bad" and returns the list of
 * values it finds.
 * 
 * @author Matthew Dean
 */
public class WatchlistReader {

	private static final Log logger = LogFactory.getLog(WatchlistReader.class);

	private File file = null;

	/**
	 * Constructor.  
	 */
	public WatchlistReader(String filename) {
		file = new File(filename);
	}

	/**
	 * Searches the file and returns the list of bad tags. If an exception
	 * occurs, the method will return null.
	 * 
	 * @return
	 */
	public List<String> getWatchlistTags() {
		if (file.exists()) {
			FileInputStream fstream = null;
			DataInputStream in = null;
			BufferedReader br = null;
			try {
				fstream = new FileInputStream(file);
				in = new DataInputStream(fstream);
				br = new BufferedReader(new InputStreamReader(in));
				String line;
				List<String> tagList = new LinkedList<String>();
				while ((line = br.readLine()) != null) {
					tagList.add(line);
				}
				if (logger.isDebugEnabled())
					logger.debug("watchlist tags: " + tagList);
				return tagList;
			} catch (FileNotFoundException e) {
				logger.warn("File not Found: " + e.getMessage());
			} catch (IOException e) {
				logger.warn("IOException: " + e.getMessage());
			} finally {
				if (fstream != null) {
					try {
						fstream.close();
					} catch (IOException e) {
					}
				}
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
					}
				}
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
					}
				}
			}
		}
		return new LinkedList<String>();
	}
}
