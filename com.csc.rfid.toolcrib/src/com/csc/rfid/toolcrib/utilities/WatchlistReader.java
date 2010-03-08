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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * This file reads in the list of tags which are "bad" and returns the list of
 * values it finds.
 * 
 * @author Matthew Dean
 */
public class WatchlistReader {

	private File file = null;

	/**
	 * 
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
			try {
				FileInputStream fstream = new FileInputStream(file);
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(
						new InputStreamReader(in));
				String line;
				List<String> tagList = new LinkedList<String>();
				while ((line = br.readLine()) != null) {
					tagList.add(line);
				}
				return tagList;
			} catch (FileNotFoundException e) {
				// TODO: Probably should log this or something
			} catch (IOException e) {
				// TODO: Log this here probably
			}
		}
		return new LinkedList<String>();
	}
}
