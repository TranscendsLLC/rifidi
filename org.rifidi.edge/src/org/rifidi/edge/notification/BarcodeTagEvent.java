/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.notification;

import java.math.BigInteger;

import org.rifidi.edge.notification.DatacontainerEvent.MemoryBankLengthTuple;


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

	protected byte[] barcodeBytes;
	/** Store a decimal copy of the barcode */
	protected String barcode = "";
	protected BigInteger barcodeBigInt;

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
	 */
	public void setBarcode(byte[] memBank) {
		barcodeBytes = memBank;
		barcodeBigInt= new BigInteger(memBank);
		barcode = format();
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
	public byte[] getBarcodeMemory() {
		return barcodeBytes;
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
	 * org.rifidi.edge.notification.data.DatacontainerEvent#getID
	 * ()
	 */
	@Override
	public BigInteger getID() {
		return barcodeBigInt;
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
	private String format(){
		StringBuffer buffer = new StringBuffer();
		for(byte b : barcodeBytes){
			buffer.append((char)b);
		}
		return buffer.toString();
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.notification.data.DatacontainerEvent#getFormattedID()
	 */
	@Override
	public String getFormattedID() {
		return barcode;
	}
	
}
