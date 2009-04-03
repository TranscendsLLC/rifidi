/**
 * 
 */
package org.rifidi.edge.client.model.sal;

import java.util.Hashtable;

import org.epcglobalinc.LevelTypeList;
import org.fosstrak.tdt.TDTEngine;
import org.rifidi.edge.core.api.tags.TagDTO;

/**
 * @author kyle
 * 
 */
public class RemoteTag {

	private String id;
	private int antenna;
	private String hashcode;
	private long timestamp;

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

	public String getID() {
		return id;
	}

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
