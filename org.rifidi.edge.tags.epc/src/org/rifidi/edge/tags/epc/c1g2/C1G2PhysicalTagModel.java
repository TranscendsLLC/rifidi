/*
 *  C1G2PhysicalTagModel.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.tags.epc.c1g2;

import org.rifidi.edge.tags.data.PhysicalTagModel;
import org.rifidi.edge.tags.data.memorybank.MemoryBank;
import org.rifidi.edge.tags.exceptions.IllegalBankAccessException;
import org.rifidi.edge.tags.util.BitVector;

/**
 * This is a concrete implementation of a PhysicalTagModel for Class 1 Gen 2
 * (C1G2) tags. C1G2 tags have four memory banks: 0 - Reserved Bank, which
 * contains Access and Kill Passwords 1 - EPC Bank, which contains the EPC along
 * with a header 2 - TID Bank 3 - User Bank
 * 
 * Because this class is used to represent the data that was read from a tag,
 * all four memory banks may not have data in them (for example, if the TID bank
 * was not read during the tag operation)
 * 
 * This class can be constructed from the four memory banks or from banks 0,2,
 * and 3 plus the EPC bits from bank 1. This is because often readers do not
 * read the entirety of bank 1 when getting the EPC of a tag, and instead return
 * the EPC only.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class C1G2PhysicalTagModel extends PhysicalTagModel {

	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The Reserved Memory Bank. (bank 0)
	 */
	private C1G2ReservedBank _reservedMB;

	/**
	 * The EPC Memory Bank (bank 1)
	 */
	private AbstractC1G2EPCBank _epcMB;

	/**
	 * The TID Memory Bank (bank 2)
	 */
	private C1G2TIDBank _tidMB;

	/**
	 * The User Memory Bank (bank 4)
	 */
	private C1G2UserBank _userMB;

	/**
	 * Constructs a new C1G2PhysicalTagModel from the four memory banks. If no
	 * data is available for a particular bank, null should be passed in as an
	 * argument
	 * 
	 * @param reservedBank
	 *            bank 0
	 * @param epcBank
	 *            bank 1
	 * @param tidBank
	 *            bank 2
	 * @param userBank
	 *            bank 3
	 */
	public C1G2PhysicalTagModel(C1G2ReservedBank reservedBank,
			AbstractC1G2EPCBank epcBank, C1G2TIDBank tidBank,
			C1G2UserBank userBank) {
		_reservedMB = reservedBank;
		_epcMB = epcBank;
		_tidMB = tidBank;
		_userMB = userBank;
	}

	/**
	 * Constructs a new C1G2PhysicalTagModel only from the EPC data portion of
	 * bank 1(not including CRC and PC bits). The other memory banks on this tag
	 * model will be null.
	 * 
	 * This is a convenience constructor for when only the EPC bits are needed
	 * for a C1G2PhsyicalTagModel. Please note that the 9 Numbering System
	 * Identifier bits (the toggle bit and the AFI bits) will be set to all 0s.
	 * This means you should use this constructor only if you know that your EPC
	 * is part of the EPC encoding family as defined in the Tag Data
	 * Specification (e.g SGTIN, GID, DOD). This method will also generate a
	 * default PC Bits. For more information on how that is done see
	 * C1G2EPCBankWithoutHeader
	 * 
	 * @see C1G2EPCBankWithoutHeader#C1G2EPCBankWithoutHeader(String)
	 * 
	 * @param epc
	 *            the EPC bits from bank 1, not including the CRC and PC bits
	 *            (i.e. only the portion of bank 1 after address x1F)
	 */
	public C1G2PhysicalTagModel(String epc) {
		this(null, new C1G2EPCBankWithoutHeader(epc), null, null);
	}

	/**
	 * Read <code>length</code> number of bits from MemoryBank
	 * <code>memBank</code> starting at position <code>offset</code>
	 * 
	 * @param memBank
	 *            the memory bank to read from: 0-Reserved 1-EPC 2-TID 3-User
	 * @param length
	 *            The number of bits to read
	 * @param offset
	 *            The index of the first bit to read. 0 indicates start from the
	 *            beginning
	 * @return a BitVector representing the tag read
	 * @throws IllegalBankAccessException
	 *             If there was an error while reading (such as index out of
	 *             bounds)
	 */
	public BitVector read(int memBank, int length, int offset)
			throws IllegalBankAccessException {
		MemoryBank mb;
		switch (memBank) {
		case 0:
			mb = _reservedMB;
			break;
		case 1:
			mb = _epcMB;
			break;
		case 2:
			mb = _tidMB;
			break;
		case 3:
			mb = _userMB;
			break;
		default:
			throw new IllegalBankAccessException();
		}
		if (null == mb) {
			throw new IllegalBankAccessException();
		}
		return mb.access(length, offset);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.tags.data.PhysicalTagModel#getTagType()
	 */
	@Override
	public String getTagType() {
		return this.getClass().getCanonicalName();
	}

	/**
	 * 
	 * @return Bank 0
	 * @throws IllegalBankAccessException
	 *             If no data is available for bank 0
	 */
	public C1G2ReservedBank getReservedBank() throws IllegalBankAccessException {
		if (null == _reservedMB) {
			throw new IllegalBankAccessException("Reserved Bank has no data");
		}
		return _reservedMB;
	}

	/**
	 * Remember that the 32-bit header for this bank (i.e. CRC and PC bits) are
	 * not valid if this object was constructed from the EPC bits and not the
	 * EPCBank
	 * 
	 * @return Bank 1
	 * @throws IllegalBankAccessException
	 *             If no data is available for bank 1
	 */
	public AbstractC1G2EPCBank getEPCBank() throws IllegalBankAccessException {
		if (null == _epcMB) {
			throw new IllegalBankAccessException("EPC Bank has no data");
		}
		return _epcMB;
	}

	/**
	 * 
	 * @return Bank 2
	 * @throws IllegalBankAccessException
	 *             If no data is available for bank 2
	 */
	public C1G2TIDBank getTIDBank() throws IllegalBankAccessException {
		if (null == _tidMB) {
			throw new IllegalBankAccessException("TID Bank has no data");
		}
		return _tidMB;
	}

	/**
	 * 
	 * @return Bank 3
	 * @throws IllegalBankAccessException
	 *             If no data is available for bank 3
	 */
	public C1G2UserBank getUserBank() throws IllegalBankAccessException {
		if (null == _userMB) {
			throw new IllegalBankAccessException("User Bank has no data");
		}
		return _userMB;
	}

	/**
	 * 
	 * @return Only the EPC bits from the EPC Bank (does not include CRC and PC
	 *         bits)
	 * @throws IllegalBankAccessException
	 *             If no data is available for bank 1
	 */
	public BitVector getEPCBits() throws IllegalBankAccessException {
		if (_epcMB == null) {
			throw new IllegalBankAccessException("EPC Bank has no data");
		}
		return _epcMB.getEPCBits();
	}

}
