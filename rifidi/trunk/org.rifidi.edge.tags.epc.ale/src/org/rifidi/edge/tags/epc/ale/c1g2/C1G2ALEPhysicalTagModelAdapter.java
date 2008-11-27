/*
 *  C1G2ALEPhysicalTagModelAdapter.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.tags.epc.ale.c1g2;

import org.rifidi.edge.ale.exceptions.ALEException;
import org.rifidi.edge.ale.exceptions.FieldNotFoundALEException;
import org.rifidi.edge.ale.exceptions.OperationNotPossibleALEException;
import org.rifidi.edge.ale.fields.adapters.ALEPhysicalTagModelAdapter;
import org.rifidi.edge.ale.fields.builtin.AFI_ALEField;
import org.rifidi.edge.ale.fields.builtin.AccessPwd_ALEField;
import org.rifidi.edge.ale.fields.builtin.EPCBank_ALEField;
import org.rifidi.edge.ale.fields.builtin.EPC_ALEField;
import org.rifidi.edge.ale.fields.builtin.KillPwd_ALEField;
import org.rifidi.edge.ale.fields.builtin.NSI_ALEField;
import org.rifidi.edge.ale.fields.builtin.TIDBank_ALEField;
import org.rifidi.edge.ale.fields.builtin.UserBank_ALEField;
import org.rifidi.edge.ale.fields.generic.FixedAddress;
import org.rifidi.edge.ale.fields.generic.Fixed_ALEField;
import org.rifidi.edge.ale.fields.generic.VariableAddress;
import org.rifidi.edge.ale.fields.generic.Variable_ALEField;
import org.rifidi.edge.tags.data.PhysicalTagModel;
import org.rifidi.edge.tags.data.memorybank.MemoryBank;
import org.rifidi.edge.tags.epc.c1g2.AbstractC1G2EPCBank;
import org.rifidi.edge.tags.epc.c1g2.C1G2PhysicalTagModel;
import org.rifidi.edge.tags.exceptions.IllegalBankAccessException;
import org.rifidi.edge.tags.util.BitVector;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class C1G2ALEPhysicalTagModelAdapter implements
		ALEPhysicalTagModelAdapter {
	/**
	 * When interacting with a Gen2 Tag, an ALE implementation SHALL interpret
	 * the afi fieldname as a synonym for the fieldname @1.8.24, that is, for
	 * offset 18h to 1Fh in t EPC/UII memory bank of a Gen2 Tag, which may hold
	 * the ISO 15962 Application Family Identifier (AFI). (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.ale.fields.adapters.ALEPhysicalTagModelAdapter#getAFI
	 *      (org.rifidi.edge.ale.fields.adapters.PhysicalTagModel)
	 */
	@Override
	public AFI_ALEField getAFI(PhysicalTagModel tagModel) throws ALEException {
		C1G2PhysicalTagModel gen2Tag = checkTagModel(tagModel);
		try {
			return new AFI_ALEField(gen2Tag.getEPCBank().getAFIBits());
		} catch (IllegalBankAccessException e) {
			throw new OperationNotPossibleALEException();
		}
	}

	/**
	 * When interacting with a Gen2 Tag, an ALE implementation SHALL interpret
	 * the accessPwd fieldname as a synonym for the fieldname @0.32.32, that is,
	 * for offset 20h to 3Fh in the RESERVED memory bank of a Gen2 Tag, which
	 * holds the Access Password. (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.ale.fields.adapters.ALEPhysicalTagModelAdapter#getAccessPwd
	 *      (org.rifidi.edge.ale.fields.adapters.PhysicalTagModel)
	 */
	@Override
	public AccessPwd_ALEField getAccessPwd(PhysicalTagModel tagModel)
			throws ALEException {
		C1G2PhysicalTagModel gen2Tag = checkTagModel(tagModel);
		try {
			// read @0.32.32
			return new AccessPwd_ALEField(gen2Tag.read(0, 32, 32));
		} catch (IllegalBankAccessException e) {
			throw new FieldNotFoundALEException();
		}
	}

	/**
	 * When interacting with a Gen2 Tag, an ALE implementation SHALL interpret
	 * the epc fieldname as referring to the EPC/UII content of the EPC memory
	 * bank (Bank 012) as defined in [Gen2]. Specifically, it refers to the
	 * toggle bit (bit 17h), the Reserved/AFI bits (bits 18h-1Fh), and the
	 * EPC/UII bits (bits 20h through the end of the EPC bank as indicated by
	 * the length bits 10h-14h).
	 * 
	 * @param tagModel
	 *            The phsysical tag
	 * @return An An EPC data type
	 * 
	 * @see org.rifidi.edge.ale.fields.adapters.ALEPhysicalTagModelAdapter#getEPC
	 *      (org.rifidi.edge.ale.fields.adapters.PhysicalTagModel)
	 **/
	@Override
	public EPC_ALEField getEPC(PhysicalTagModel tagModel) throws ALEException {
		C1G2PhysicalTagModel gen2Tag = checkTagModel(tagModel);
		try {
			return new EPC_ALEField(gen2Tag.getEPCBank().getEPCBits(), gen2Tag
					.getEPCBank().getPCBits());
		} catch (IllegalBankAccessException e) {
			throw new FieldNotFoundALEException();
		}
	}

	/**
	 * When interacting with a Gen2 Tag, an ALE implementation SHALL interpret
	 * the epcBank fieldname as referring to the content of the EPC memory bank
	 * (Bank 012) as defined in [Gen2]. Specifically, it refers to the offset
	 * 00h up to the end of this memory bank. When this fieldname is referred by
	 * an ALE write command the data is written from offset 00h till the length
	 * of the provided data length. When this fieldname is referred by ALE read
	 * command the data is read from offset 00h through the end of this memory
	 * bank. If the implementation cannot or does not wish to support reading to
	 * the end of the memory bank, an ALE implementation SHALL raise an
	 * “operation not possible” condition when an attempt is made to read from
	 * the epcBank field. (non-Javadoc)
	 * 
	 * TODO: think a little more about this. What happens if only the EPC is
	 * read from the tag. Should the epcbank be able to be read, or should it
	 * raise an 'operation not possible' condition?
	 * 
	 * @see org.rifidi.edge.ale.fields.adapters.ALEPhysicalTagModelAdapter#getEPCBank
	 *      (org.rifidi.edge.ale.fields.adapters.PhysicalTagModel)
	 */
	@Override
	public EPCBank_ALEField getEPCBank(PhysicalTagModel tagModel)
			throws ALEException {
		C1G2PhysicalTagModel gen2Tag = checkTagModel(tagModel);
		try {
			AbstractC1G2EPCBank bank = gen2Tag.getEPCBank();
			BitVector bv = bank.access(bank.getMemoryBankSize(), 0);
			return new EPCBank_ALEField(bv);
		} catch (IllegalBankAccessException e) {
			throw new FieldNotFoundALEException();
		}
	}

	/**
	 * When interacting with a Gen2 Tag, an ALE implementation SHALL interpret
	 * bank as follows: bank value Meaning (see [Gen2])
	 * 
	 * 0 Reserved bank (Bank 002)
	 * 
	 * 1 EPC/UII bank (Bank 012)
	 * 
	 * 2 TID bank (Bank 102)
	 * 
	 * 3 User bank (Bank 112)
	 * 
	 * Any other bank value SHALL result in a “field not found” condition when
	 * interacting with a Gen2 Tag. When interacting with a Gen2 Tag, the
	 * fieldname SHALL be interpreted as referring to the contiguous field whose
	 * most significant bit is offset and whose least significant bit is bit
	 * (offset + length – 1), following the addressing convention specified in
	 * [Gen2]. (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.ale.fields.adapters.ALEPhysicalTagModelAdapter#getFixed
	 *      (org.rifidi.edge.ale.fields.adapters.PhysicalTagModel,
	 *      org.rifidi.edge.ale.fields.generic.FixedAddress)
	 */
	@Override
	public Fixed_ALEField getFixed(PhysicalTagModel tagModel,
			FixedAddress address) throws ALEException {
		C1G2PhysicalTagModel gen2Tag = checkTagModel(tagModel);
		try {
			BitVector data = gen2Tag.read(address.get_bank(), address
					.get_length(), address.get_offset());
			return new Fixed_ALEField(data);
		} catch (IllegalBankAccessException e) {
			throw new FieldNotFoundALEException();
		}
	}

	/**
	 * When interacting with a Gen2 Tag, an ALE implementation SHALL interpret
	 * the killPwd fieldname as a synonym for the fieldname @0.32, that is, for
	 * offset 00h to 1Fh in the RESERVED memory bank of a Gen2 Tag, which holds
	 * the Kill Password.
	 * 
	 * @see org.rifidi.edge.ale.fields.adapters.ALEPhysicalTagModelAdapter#getKillPwd
	 *      (org.rifidi.edge.ale.fields.adapters.PhysicalTagModel)
	 */
	@Override
	public KillPwd_ALEField getKillPwd(PhysicalTagModel tagModel)
			throws ALEException {
		C1G2PhysicalTagModel gen2Tag = checkTagModel(tagModel);
		try {
			// read @0.32.0
			return new KillPwd_ALEField(gen2Tag.read(0, 32, 0));
		} catch (IllegalBankAccessException e) {
			throw new FieldNotFoundALEException();
		}
	}

	/**
	 * When interacting with a Gen2 Tag, an ALE implementation SHALL interpret
	 * the nsi fieldname as a synonym for the fieldname @1.9.23, that is, for
	 * offset 17h to 1Fh in the EPC/UII memory bank of a Gen2 Tag, which holds
	 * the Numbering System Identifier (NSI). When interacting with a Gen1 Tag,
	 * an ALE implementation SHALL interpret the nsi fieldname as a “field not
	 * found”. (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.ale.fields.adapters.ALEPhysicalTagModelAdapter#getNSI
	 *      (org.rifidi.edge.ale.fields.adapters.PhysicalTagModel)
	 */
	@Override
	public NSI_ALEField getNSI(PhysicalTagModel tagModel) throws ALEException {
		C1G2PhysicalTagModel gen2Tag = checkTagModel(tagModel);
		try {
			return new NSI_ALEField(gen2Tag.getEPCBank().getNSIBits());
		} catch (IllegalBankAccessException e) {
			throw new FieldNotFoundALEException();
		}
	}

	/**
	 * When interacting with a Gen2 Tag, an ALE implementation SHALL interpret
	 * the tidBank fieldname as referring to the content of the TID memory bank
	 * (Bank 102) as defined in [Gen2]. Specifically, it refers to the offset
	 * 00h up to the end of this memory bank. When this fieldname is referred by
	 * an ALE write command the data is written from offset 00h till the length
	 * of the provided data length. When this fieldname is referred by ALE read
	 * command the data is read from offset 00h through the end of this memory
	 * bank. If the implementation cannot or does not wish to support reading to
	 * the end of the memory bank, an ALE implementation SHALL raise an
	 * “operation not possible” condition when an attempt is made to read from
	 * the tidBank field.
	 * 
	 * @see org.rifidi.edge.ale.fields.adapters.ALEPhysicalTagModelAdapter#getTIDBank
	 *      (org.rifidi.edge.ale.fields.adapters.PhysicalTagModel)
	 */
	@Override
	public TIDBank_ALEField getTIDBank(PhysicalTagModel tagModel)
			throws ALEException {
		C1G2PhysicalTagModel gen2Tag = checkTagModel(tagModel);
		try {
			MemoryBank bank = gen2Tag.getTIDBank();
			BitVector bv = bank.access(bank.getMemoryBankSize(), 0);
			return new TIDBank_ALEField(bv);
		} catch (IllegalBankAccessException e) {
			throw new FieldNotFoundALEException();
		}
	}

	/**
	 * When interacting with a Gen2 Tag, an ALE implementation SHALL interpret
	 * the userBank fieldname as referring to the content of the User memory
	 * bank (Bank 112) as defined in [Gen2]. Specifically, it refers to the
	 * offset 00h up to the end of this memory bank. When this fieldname is
	 * referred by an ALE write command the data is written from offset 00h till
	 * the length of the provided data length. When this fieldname is referred
	 * by ALE read command the data is read from offset 00h through the end of
	 * this memory bank. If the implementation cannot or does not wish to
	 * support reading to the end of the memory bank, an ALE implementation
	 * SHALL raise an “operation not possible” condition when an attempt is made
	 * to read from the userBank field.
	 * 
	 * @see org.rifidi.edge.ale.fields.adapters.ALEPhysicalTagModelAdapter#getUserBank
	 *      (org.rifidi.edge.ale.fields.adapters.PhysicalTagModel)
	 */
	@Override
	public UserBank_ALEField getUserBank(PhysicalTagModel tagModel)
			throws ALEException {
		C1G2PhysicalTagModel gen2Tag = checkTagModel(tagModel);
		try {
			MemoryBank bank = gen2Tag.getUserBank();
			BitVector bv = bank.access(bank.getMemoryBankSize(), 0);
			return new UserBank_ALEField(bv);
		} catch (IllegalBankAccessException e) {
			throw new FieldNotFoundALEException();
		}
	}

	/**
	 * This operation is currently not supported on this implementation.
	 * 
	 * When interacting with a Gen2 Tag, an ALE implementation SHALL interpret
	 * bank as follows: bank value Meaning (see [Gen2]) 0 Invalid (“field not
	 * found” condition)
	 * 
	 * 1 EPC/UII bank (Bank 012)
	 * 
	 * 2 Invalid (“field not found” condition)
	 * 
	 * 3 User bank (Bank 112)
	 * 
	 * Table 12. Bank Values for Variable Fieldnames
	 * 
	 * Any other bank value SHALL result in a “field not found” condition when
	 * interacting with a Gen2 Tag. (non-Javadoc)
	 * 
	 * @throws FieldNotFoundALEException
	 * @throws OperationNotPossibleALEException
	 * 
	 * @see org.rifidi.edge.ale.fields.adapters.ALEPhysicalTagModelAdapter#getVariable
	 *      (org.rifidi.edge.ale.fields.adapters.PhysicalTagModel,
	 *      org.rifidi.edge.ale.fields.generic.VariableAddress)
	 */
	@Override
	public Variable_ALEField getVariable(PhysicalTagModel tagModel,
			VariableAddress address) throws ALEException {
		throw new OperationNotPossibleALEException();
	}

	/**
	 * A helper method that casts the PhysicalTagModel to a C1G2PhysicalTagModel
	 * and throws an IllegalArgumentException if there was a problem when doing
	 * this
	 * 
	 * @param tagModel
	 *            The tagModel to check
	 * @return the tagModel as a C1G2PhysicalTagModel
	 */
	private C1G2PhysicalTagModel checkTagModel(PhysicalTagModel tagModel) {
		if (tagModel instanceof C1G2PhysicalTagModel) {
			return (C1G2PhysicalTagModel) tagModel;

		} else {
			throw new IllegalArgumentException("tagModel must be instance of "
					+ C1G2PhysicalTagModel.class);
		}
	}

}
