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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.ale.exceptions.FieldNotFoundALEException;
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

	private static Log logger = LogFactory.getLog(C1G2ALEPhysicalTagModelAdapter.class);
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.ale.fields.adapters.ALEPhysicalTagModelAdapter#getAFI
	 * (org.rifidi.edge.ale.fields.adapters.PhysicalTagModel)
	 */
	@Override
	public AFI_ALEField getAFI(PhysicalTagModel tagModel) {
		return null;
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
			throws FieldNotFoundALEException {
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
	public EPC_ALEField getEPC(PhysicalTagModel tagModel)
			throws FieldNotFoundALEException {
		C1G2PhysicalTagModel gen2Tag = checkTagModel(tagModel);
		try {
			return new EPC_ALEField(gen2Tag.getEPCBank().getEPCBits(), gen2Tag
					.getEPCBank().getPCBits());
		} catch (IllegalBankAccessException e) {
			throw new FieldNotFoundALEException();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.ale.fields.adapters.ALEPhysicalTagModelAdapter#getEPCBank
	 * (org.rifidi.edge.ale.fields.adapters.PhysicalTagModel)
	 */
	@Override
	public EPCBank_ALEField getEPCBank(PhysicalTagModel tagModel)
			throws FieldNotFoundALEException {
		C1G2PhysicalTagModel gen2Tag = checkTagModel(tagModel);
		try {
			AbstractC1G2EPCBank bank = gen2Tag.getEPCBank();
			BitVector bv = bank.access(bank.getMemoryBankSize(), 0);
			return new EPCBank_ALEField(bv);
		} catch (IllegalBankAccessException e) {
			throw new FieldNotFoundALEException();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.ale.fields.adapters.ALEPhysicalTagModelAdapter#getFixed
	 * (org.rifidi.edge.ale.fields.adapters.PhysicalTagModel,
	 * org.rifidi.edge.ale.fields.generic.FixedAddress)
	 */
	@Override
	public Fixed_ALEField getFixed(PhysicalTagModel tagModel,
			FixedAddress address) {
		// TODO Auto-generated method stub
		return null;
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
			throws FieldNotFoundALEException {
		C1G2PhysicalTagModel gen2Tag = checkTagModel(tagModel);
		try {
			// read @0.32.0
			return new KillPwd_ALEField(gen2Tag.read(0, 32, 0));
		} catch (IllegalBankAccessException e) {
			throw new FieldNotFoundALEException();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.ale.fields.adapters.ALEPhysicalTagModelAdapter#getNSI
	 * (org.rifidi.edge.ale.fields.adapters.PhysicalTagModel)
	 */
	@Override
	public NSI_ALEField getNSI(PhysicalTagModel tagModel) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.ale.fields.adapters.ALEPhysicalTagModelAdapter#getTIDBank
	 * (org.rifidi.edge.ale.fields.adapters.PhysicalTagModel)
	 */
	@Override
	public TIDBank_ALEField getTIDBank(PhysicalTagModel tagModel) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.ale.fields.adapters.ALEPhysicalTagModelAdapter#getUserBank
	 * (org.rifidi.edge.ale.fields.adapters.PhysicalTagModel)
	 */
	@Override
	public UserBank_ALEField getUserBank(PhysicalTagModel tagModel) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.ale.fields.adapters.ALEPhysicalTagModelAdapter#getVariable
	 * (org.rifidi.edge.ale.fields.adapters.PhysicalTagModel,
	 * org.rifidi.edge.ale.fields.generic.VariableAddress)
	 */
	@Override
	public Variable_ALEField getVariable(PhysicalTagModel tagModel,
			VariableAddress address) {
		// TODO Auto-generated method stub
		return null;
	}

	private C1G2PhysicalTagModel checkTagModel(PhysicalTagModel tagModel) {
		if (tagModel instanceof C1G2PhysicalTagModel) {
			return (C1G2PhysicalTagModel) tagModel;

		} else {
			throw new IllegalArgumentException("tagModel must be instance of "
					+ C1G2PhysicalTagModel.class);
		}
	}

}
