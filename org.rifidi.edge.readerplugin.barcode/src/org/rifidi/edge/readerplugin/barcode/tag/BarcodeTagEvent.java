/*
 *  BarcodeTagEvent.java
 *
 *  Created:	Apr 23, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.barcode.tag;

import java.math.BigInteger;

import org.rifidi.edge.core.services.notification.data.DatacontainerEvent;

/**
 * This class represents a barcode tag for the reader.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class BarcodeTagEvent extends DatacontainerEvent {

	/**
	 * Generated UID.
	 */
	private static final long serialVersionUID = -5334113422705563853L;

	/** Store a decimal copy of the barcode */
	protected String barcode = "";

	/**
	 * Constructor.
	 */
	public BarcodeTagEvent() {
		memoryBanks.add(new MemoryBankLengthTuple(new BigInteger("0"), 1));
	}

	/**
	 * Set the barcode
	 * 
	 * @param memBank
	 * @param length
	 */
	public void setBarcode(BigInteger memBank, Integer length) {
		memoryBanks.get(0).setLength(length);
		memoryBanks.get(0).setMemory(memBank);
		barcode = format(memoryBanks.get(0).getMemory());
	}

	/**
	 * Returns the barcode in decimal encoding.
	 * 
	 * @return
	 */
	public String getBarcode() {
		return barcode;
	}

	/**
	 * Get the epc memory bank.
	 * 
	 * @return
	 */
	public BigInteger getBarcodeMemory() {
		return memoryBanks.get(0).getMemory();
	}

	/**
	 * Returns the length of the memory bank.
	 * 
	 * @return the number of bits in the EPC
	 */
	public Integer getBarcodeMemoryLength() {
		return memoryBanks.get(0).getLength();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BarcodeTagEvent) {
			return barcode.hashCode() == obj.hashCode();
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
		return barcode.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.services.notification.data.DatacontainerEvent#getID
	 * ()
	 */
	@Override
	public BigInteger getID() {
		return this.memoryBanks.get(0).getMemory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BarcodeTagEvent [id=" + barcode + "]";
	}
	
	/**
	 * 
	 * @param integer
	 * @return
	 */
	private String format(BigInteger integer){
		StringBuffer buffer = new StringBuffer(integer.toString(10));
		while(buffer.length()<10){
			buffer.insert(0, "0");
		}
		return buffer.toString();
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.services.notification.data.DatacontainerEvent#getFormattedID()
	 */
	@Override
	public String getFormattedID() {
		return barcode;
	}
	
}
