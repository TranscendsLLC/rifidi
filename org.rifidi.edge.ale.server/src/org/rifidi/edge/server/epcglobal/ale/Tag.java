/*
 * 12/12/2015
 * The original work can be found in
 * https://github.com/Fosstrak/fosstrak-fc/blob/master/fc-server/src/main/java/org/fosstrak/ale/server
 * 
 */

package org.rifidi.edge.server.epcglobal.ale;

import java.math.BigInteger;
import java.util.Date;

import org.rifidi.edge.notification.DatacontainerEvent;
import org.rifidi.edge.notification.TagReadEvent;

//import org.rifidi.edge.server.ale.infrastructure.TagReadEvent;

public class Tag
		extends TagReadEvent {
	
	/** name of the (composite) reader where the tag has been read. */
	private String reader = null;
	
	/** name of the reader where the tag was read from a physicalReader. */
	private String origin  = null;
	
	/** id of this tag. */
	private byte[] tagID = null;
	
	/** id as pure uri*/
	private String tagIDAsPureURI = null;
	
	/** id as binary string. */
	private String binary = null;
	
	/** trace where the tag passed through the ALE.  */
	private String trace = null;
	
	/** timestamp when the tag occured. */
	private long timestamp = -1;
	
	/** the tag length. */
	private String tagLength = null;
	
	/** the filter value. */
	private String filter = null;
	
	/** the company prefix length. */
	private String companyPrefixLength = null;
	
	/** ORANGE: user memory of the tag. */
	private String userMemory = null;

	private int antennaOrigin;

	private String readerOrigin;
	
	/**
	 * constructor for a tag. (default constructor).
	 */
	public Tag() {
		super();
	}
	
	/**
	 * assignment constructor for Tag.
	 * @param origin reader where the tag was read originally 
	 * @param tagId the tagID
	 * @param discoveryTime the discoveryTime
	 */
	public Tag(String id, String readerId, int antennaId, byte[] tagId, String tagIDAsPureURI, long discoveryTime) {
		super(id, (DatacontainerEvent)null, antennaId, discoveryTime);
		setReaderOrigin(readerId);
		setAntennaOrigin(antennaId);
		setReader(origin);
		setTagID(tagId);
		setTagIDAsPureURI(tagIDAsPureURI);
		setTimestamp(discoveryTime);		
	}
	
	/**
	 * constructor for a tag.(assignment constructor).
	 * @param origin the baseReader where the tag has been read
	 */
	public Tag(String readerOrigin, int antennaOrigin) {
		setReaderOrigin(readerOrigin);
		setAntennaOrigin(antennaOrigin);
		setReader(readerOrigin);
	}
	
	/**
	 * constructs a tag from another existing tag. (copy constructor).
	 * @param tag the tag to be copied into a new tag
	 */
	public Tag(Tag tag) {
		setReaderOrigin(tag.getReaderOrigin());
		setAntennaOrigin(tag.getAntennaOrigin());
		setTimestamp(tag.getTimestamp());
		setReader(tag.getReader());
		this.trace = tag.getTrace();
		setTagID(tag.getTagID());
	}

	/**
	 * gets the name of the reader that read this tag.
	 * @return name of a logicalReader
	 */
	public String getReader() {
		return reader;
	}

	/**
	 * sets the name of the reader that read this tag. when a
	 * reader is part of a composite reader then the reader will set
	 * to the name of the compositeReader. if you want to get the 
	 * original reader refer to origin.
	 * @param reader a name of a logicalReader
	 */
	public void setReader(String reader) {
		this.reader = reader;
	}

	/**
	 * returns the id of this tag.
	 * @return byte[] containing the tag id
	 */
	public byte[] getTagID() {
		return tagID;
	}

	/**
	 * sets the tag id.
	 * @param tagID a byte[] holding the tag id.
	 */
	public void setTagID(byte[] tagID) {
		this.tagID = tagID;
	}
	
	/**
	 * returns the timestamp when the tag occured.
	 * @return timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * sets the timestamp when a tag has been read.
	 * @param timestamp time when tag has been read.
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	/**
	 * prepends a trace to the given trace.
	 * @param suffix  a trace path item
	 */
	public void addTrace(String suffix) {
		if (trace == null) {
			trace = suffix;
		} else {
			trace = trace + "-" + suffix;
		}
	}
	
	/** 
	 * returns the trace path of the tag.
	 * @return a string containing the tracepath
	 */
	public String getTrace() {
		return trace;
	}
	
	@Override
	public String toString() {
		return String.format("[Tag id: %s, ReaderName: %s, ReaderOriginName: %s, AntenaOriginName: %s, Trace: %s, Timestamp: %d, Binary: %s, PureID: %s]", 
				HexUtil.byteArrayToHexString(getTagID()), getReader(), getReaderOrigin(),getAntennaOrigin(), getTrace(), getTimestamp(), getTagAsBinary(), getTagIDAsPureURI());
	}

	/**
	 * returns the name of the baseReader where the tag has been read.
	 * @return returns the name of the baseReader where the tag has been read.
	 */
	public int getAntennaOrigin() {
		return antennaOrigin;
	}
	
	/**
	 * sets the origin  (baseReader) where the tag has been read.
	 * @param origin the name of the baseReader where the tag has been read.
	 */
	public void setAntennaOrigin(int origin) {
		this.antennaOrigin = origin;
	}
	
	/**
	 * returns the name of the baseReader where the tag has been read.
	 * @return returns the name of the baseReader where the tag has been read.
	 */
	public String getReaderOrigin() {
		return readerOrigin;
	}
	
	/**
	 * sets the origin  (baseReader) where the tag has been read.
	 * @param origin the name of the baseReader where the tag has been read.
	 */
	public void setReaderOrigin(String origin) {
		this.readerOrigin = origin;
	}

	/**
	 * comparator to check whether two tags are the same.
	 * @param tag the other tag to be checked.
	 * @return boolean value flagging whether equal or not
	 */
	public boolean equalsTag(Tag tag) {
		// if the origin in both tags is null, then do not take the 
		// origin into account when comparing
		if ((getReaderOrigin() != null) && (tag.getReaderOrigin() != null)) { 
			// compare the origin
			if (!tag.getReaderOrigin().equalsIgnoreCase(getReaderOrigin()) || tag.getAntennaOrigin() != getAntennaOrigin()) {
				return false;
				
			}
		} else if ((getReaderOrigin() == null) || (tag.getReaderOrigin() == null)) {
			return false;
		}
		
		// if the reader in both tags is null, then do not take the
		// reader into account when comparing
		if ((getReader() != null) && (tag.getReader() != null)) {
			// compare the reader
			if (!tag.getReader().equalsIgnoreCase(getReader()) || tag.getAntennaOrigin() != getAntennaOrigin())  {
				return false;
			}
		} else if ((getReader() == null) || (tag.getReader() == null)) {
			return false;
		}
		
		// compare the tag id
		if ((null != tag.getTagIDAsPureURI()) && (null != getTagIDAsPureURI())) {
			// both not null, so compare
			if (!tag.getTagIDAsPureURI().equalsIgnoreCase(getTagIDAsPureURI())) {
				return false;
			}				
		} else if ((null != tag.getTagIDAsPureURI()) || (null != getTagIDAsPureURI())) {
			// only one null, therefore not equals
			return false;
		}
		
		// try to compare the binary value
		if ((null != tag.getTagAsBinary()) && (null != getTagAsBinary())) {
			// both not null, so compare
			if (!tag.getTagAsBinary().equalsIgnoreCase(getTagAsBinary())) {
				return false;
			}				
		} else if ((null != tag.getTagAsBinary()) || (null != getTagAsBinary())) {
			// only one null, therefore not equals
			return false;
		}
		
		return true;
	}
	
	public boolean equals(Tag tag) {
		return equalsTag(tag);
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof Tag) {
			return equalsTag((Tag)obj);
		}
		return false;
	}
	
	public int hashCode() {
		String val = getTagAsBinary();
		if (null != val) {
			BigInteger dec = new BigInteger(val, 2);
			return dec.getLowestSetBit();
		}
		return super.hashCode();
	}

	/**
	 * returns the id of this tag as pure uri.
	 * @return String containing the tag id
	 */
	public String getTagIDAsPureURI() {
		return tagIDAsPureURI;
	}

	/**
	 * sets the tag id as pure uri.
	 * @param tagIDAsPureURI a string holding the tag id.
	 */
	public void setTagIDAsPureURI(String tagIDAsPureURI) {
		this.tagIDAsPureURI = tagIDAsPureURI;
	}
	
	/**
	 * sets the tag in binary format.
	 * @param binary the tag in binary format.
	 */
	public void setTagAsBinary(String binary) {
		this.binary = binary;
	}
	
	/**
	 * @return the tag in binary format.
	 */
	public String getTagAsBinary() {
		return binary;
	}
		
	/**
	 * @param tagLength the tagLength to set
	 */
	public void setTagLength(String tagLength) {
		this.tagLength = tagLength;
	}

	/**
	 * @return the tagLength
	 */
	public String getTagLength() {
		return tagLength;
	}

	/**
	 * @param filter the filter to set
	 */
	public void setFilter(String filter) {
		this.filter = filter;
	}

	/**
	 * @return the filter
	 */
	public String getFilter() {
		return filter;
	}

	/**
	 * @param companyPrefixLength the companyPrefixLength to set
	 */
	public void setCompanyPrefixLength(String companyPrefixLength) {
		this.companyPrefixLength = companyPrefixLength;
	}

	/**
	 * @return the companyPrefixLength
	 */
	public String getCompanyPrefixLength() {
		return companyPrefixLength;
	}
	
	/**
	 * ORANGE: returns the user memory of this tag.
	 * @return String containing the tag user memory.
	 */
	public String getUserMemory() {
		return userMemory;
	}

	/**
	 * ORANGE: sets the user memory of this tag. 
	 * @param userMemory the String holding the tag user memory.
	 */
	public void setUserMemory(String userMemory) {
		this.userMemory = userMemory;
	}
}
