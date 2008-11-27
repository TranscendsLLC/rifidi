/*
 *  C1G1PhysicalTagModel.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.tags.epc.c1g1;

import org.rifidi.edge.tags.data.PhysicalTagModel;
import org.rifidi.edge.tags.exceptions.IllegalBankAccessException;
import org.rifidi.edge.tags.util.BitVector;

/**
 * @author kyle
 * 
 */
public class C1G1PhysicalTagModel extends PhysicalTagModel {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The EPC Memory Bank
	 */
	private C1G1EPCBank _epcMB;

	public C1G1PhysicalTagModel(C1G1EPCBank epcBank ){
		if(null==epcBank){
			throw new IllegalArgumentException("epcBank cannot be null");
		}
		_epcMB = epcBank;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.tags.data.PhysicalTagModel#getTagType()
	 */
	@Override
	public String getTagType() {
		return C1G1PhysicalTagModel.class.getCanonicalName();
	}

	/**
	 * Read <code>length</code> number of bits from MemoryBank
	 * <code>memBank</code> starting at position <code>offset</code>
	 * 
	 * @param memBank
	 *            the memory bank to read from: 0-EPC
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
		if (memBank != 0) {
			throw new IllegalBankAccessException();
		}
		if (null == _epcMB) {
			throw new IllegalBankAccessException();
		}
		return _epcMB.access(length, offset);
	}

	/**
	 * Gets the EPC bank for this Gen1 tag
	 * 
	 * @return Bank 0
	 * @throws IllegalBankAccessException
	 *             If no data is available for bank 0
	 */
	public C1G1EPCBank getEPCBank() throws IllegalBankAccessException {
		if (null == _epcMB) {
			throw new IllegalBankAccessException("EPC Bank has no data");
		}
		return _epcMB;
	}

}
