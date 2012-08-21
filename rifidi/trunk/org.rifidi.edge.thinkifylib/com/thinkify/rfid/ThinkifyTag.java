package com.thinkify.rfid;

import java.util.Date;

/**
 * Container class for tag data received by a Thinkify RFID reader.
 * 
 * @author David Krull
 */
public class ThinkifyTag {
	
	private String  epc;
	private int     antenna;
	private int     count;
	private long    timeDisc, timeLast; //, lastTimeLast;
	private float   rssi;
	private float   frequency;
	
	/**
	 * This is a flag that can be used by the developer when processing a taglist,
	 * for marking which tags have been reported (or any other flag indication).
	 */
	public  boolean reported;

	
	/**
	 * Constructs a ThinkifyTag, based on a single "Tag=" line received from the reader.
	 * 
	 * @param tagStringFromReader
	 */
	public ThinkifyTag(String tagStringFromReader) {
		createFromReaderString(tagStringFromReader);
	}
	
	public ThinkifyTag() {	
	}
	
	
	/**
	 * Returns this tag's EPC data.
	 * @return the epc
	 */
	public String getEpc() {
		return epc;
	}
	/**
	 * Sets this tag's EPC data.
	 * @param epc the epc to set
	 */
	public void setEpc(String epc) {
		this.epc = epc;
	}

	/**
	 * Returns this tag's antenna index.
	 * @return the antenna
	 */
	public int getAntenna() {
		return antenna;
	}
	/**
	 * Sets this tag's antenna index.
	 * @param antenna the antenna to set
	 */
	public void setAntenna(int antenna) {
		this.antenna = antenna;
	}

	/**
	 * Returns this tag's read counter.
	 * @return the readCount
	 */
	public int getReadCount() {
		return count;
	}
	/**
	 * Sets this tag's read counter.
	 * @param readCount the readCount to set
	 */
	public void setReadCount(int readCount) {
		this.count = readCount;
	}

	/**
	 * Returns this tag's time of discovery (when received by the computer).
	 * @return the discoverTime
	 */
	public long getDiscoverTime() {
		return timeDisc;
	}
	/**
	 * Sets this tag's time of discovery.
	 * @param timeDisc the discoverTime to set
	 */
	public void setDiscoverTime(long timeDisc) {
		this.timeDisc = timeDisc;
	}

	/**
	 * Returns the last time this tag was seen.
	 * @return the last seen time
	 */
	public long getLastSeenTime() {
		return timeLast;
	}
	/**
	 * Sets the last time this tag was seen.
	 * @param timeLast the timeLast to set
	 */
	public void setLastSeenTime(long timeLast) {
		this.timeLast = timeLast;
	}

	/**
	 * Returns the frequency info for this tag.
	 * @return the frequency
	 */
	public float getFrequecy() {
		return frequency;
	}
	/**
	 * Setst the frequency info for this tag
	 * @param frequency the frequency to set
	 */
	public void setFrequecy(float frequency) {
		this.frequency = frequency;
	}

	/**
	 * Returns the Received Signal Strength Indicator (RSSI) for this tag.
	 * @return the rssi
	 */
	public float getRSSI() {
		return rssi;
	}
	/**
	 * Sets the Received Signal Strength Indicator (RSSI) for this tag.
	 * @param rssi the rssi to set
	 */
	public void setRSSI(float rssi) {
		this.rssi = rssi;
	}
	/**
	 * Sets the RSSI for this tag, based on the two I-channel and Q-channel
	 * strength indicators.
	 * @param iSig the signal strength of the I-channel
	 * @param qSig the signal strength of the Q-channel
	 */
	public void setRSSI(int iSig, int qSig) {
		float hiRSSI;
		float deltaRSSI;
		
		float f1 = iSig;
		float f2 = qSig;
		
		deltaRSSI = Math.abs(f2-f1);
		
		hiRSSI = Math.max(f1, f2);
		setRSSI(2*hiRSSI + 10*(float)Math.log10(1 + Math.pow(10, (-deltaRSSI/10))));
	}
	
    /**
     * Populates the elements of this class from a single tag-read message from the reader. 
     * Data is of the form:
     * 
     *  TAG=3000111122223333444455556666 923750 03 0 3 Q B2F8
     *  TAG=3000111122223333444455556666 923750 03 0 3 Q B30A
     *  TAG=3000111122223333444455556666 923750 00 0 3 Q B31E
     * 
     * the fields are: 
     * epc, frequency, slot counter, isig, qsig, read channel, timestamp
     * 
     * All lines that do not start with "TAG=" are ignored.
     * 
     * @param stringFromReader Tag data line of text to parts
     * @return true if the data parses into a ThinkifyTag correctly
     */
    public boolean createFromReaderString(String stringFromReader) {
        boolean retValue = false;

        String delimRegExp = "[= ]";
        String[] parts = stringFromReader.trim().split(delimRegExp);

        // Reality check - first part should be "TAG"
        if (!parts[0].equals("TAG")) {
        	System.out.println("Doesn't look like a tag!");
        	return false;
        }
        
        // Second part should always be the EPC
        if (parts.length >= 2) {
        	retValue = true;
        	this.epc = parts[1];
            this.timeDisc = System.currentTimeMillis();
            this.timeLast = this.timeDisc;
            this.count = 1;
            this.antenna = 0;
            this.reported = false;
            
            if (parts.length >= 6) {
            	this.frequency = Float.parseFloat(parts[2]) / 1000;

            	// Parse the iSig and qSig, and calculate RSSI
                int  iSig = Integer.parseInt(parts[4], 16);
                int  qSig = Integer.parseInt(parts[5], 16);
                setRSSI(iSig, qSig);
            }
        }
        return retValue;
    }
    
    /**
     * Updates this ThinkifyTag with the data contained in newTag. This is
     * useful if you've already read a tag, but just want to update the readCount,
     * lastSeenTime, RSSI, frequency, and antenna values with the data from
     * the more recently read.
     * 
     * @param newTag the ThinkifyTag with new data to be added to this tag
     */
    public void update(ThinkifyTag newTag) {
    	// Copy the new last time-stamp
    	//this.lastTimeLast = this.timeLast;
    	this.timeLast = newTag.timeLast;
    	
    	// Increment the count
    	this.count += newTag.count;
    	
    	// Try averaging the RSSI (or keeping max?)
    	this.rssi = newTag.rssi;
    	
    	this.frequency = newTag.frequency;
    	this.antenna = newTag.antenna;
    }
    
    
    /**
     * Overrides Object.equals(), so that ThinkifyTags can be easily searched for
     * in lists. ThinkifyTags are considered equal if they have the same EPC value.
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object b) {
    	if (b instanceof ThinkifyTag) {
    		return this.epc.equals(((ThinkifyTag)b).getEpc());
    	} else {
    		return false;
    	}
    }
    
    
    /**
     * Returns a human-friendly string representation of this ThinkifyTag.
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
    	return "EPC:" + this.epc + "; @" + new Date(this.timeDisc) + "; RSSI=" + this.getRSSI() + "; count=" + this.getReadCount();
    }
}
