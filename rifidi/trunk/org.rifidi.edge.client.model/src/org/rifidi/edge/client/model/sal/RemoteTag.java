package org.rifidi.edge.client.model.sal;

import java.util.Hashtable;

import org.epcglobalinc.LevelTypeList;
import org.fosstrak.tdt.TDTEngine;
import org.rifidi.edge.core.api.tags.TagDTO;

/**
 * A Model Object for a tag read
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class RemoteTag {

	/** The ID of the tag */
	private String id;
	/** The antenna this tag was seen on */
	private int antenna;
	/** A hashcode used for comparing RemoteTags */
	private String hashcode;
	/** The timestamp of when this tag was seen */
	private long timestamp;

	/**
	 * Constructor.
	 * 
	 * @param dto
	 *            Data Transfer Object for the tag
	 * @param engine
	 *            The TDT Engine for converting the tag data to an EPC URN
	 */
	public RemoteTag(TagDTO dto, TDTEngine engine) {
		String bin = dto.getTagID().toString(2).toUpperCase();
		while (bin.length() % 4 != 0) {
			bin = "0" + bin;
		}
		antenna = dto.getAntennaNumber();

		this.timestamp = dto.getTimestamp();
		try {
			id = engine.convert(bin, new Hashtable<String, String>(),
					LevelTypeList.PURE_IDENTITY);
		} catch (Exception ex) {
			id = dto.getTagID().toString(16).toUpperCase();
		}

		hashcode = id + Integer.toString(antenna);
	}

	/**
	 * Returns the ID of this tag.
	 * 
	 * @return
	 */
	public String getID() {
		return id;
	}

	/**
	 * Returns the antenna this tag was seen on.
	 * 
	 * @return
	 */
	public int getAntennaID() {
		return antenna;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RemoteTag) {
			return this.hashcode.equals(((RemoteTag) obj).hashcode);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return hashcode.hashCode();
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

}
