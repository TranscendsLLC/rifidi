/*
 *  C1G1ALEPhysicalTagModelAdapter.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.tags.epc.ale.c1g1;

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
import org.rifidi.edge.tags.epc.c1g1.C1G1PhysicalTagModel;
import org.rifidi.edge.tags.epc.c1g2.C1G2PhysicalTagModel;
import org.rifidi.edge.tags.exceptions.IllegalBankAccessException;

/**
 * @author kyle
 * 
 */
public class C1G1ALEPhysicalTagModelAdapter implements
		ALEPhysicalTagModelAdapter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.ale.fields.adapters.ALEPhysicalTagModelAdapter#getEPC
	 * (org.rifidi.edge.tags.data.PhysicalTagModel)
	 */
	@Override
	public EPC_ALEField getEPC(PhysicalTagModel tagModel) throws ALEException {
		C1G1PhysicalTagModel gen1Tag = checkTagModel(tagModel);
		try {
			return new EPC_ALEField(gen1Tag.getEPCBank().getEPC(), null);
		} catch (IllegalBankAccessException e) {
			throw new OperationNotPossibleALEException();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.ale.fields.adapters.ALEPhysicalTagModelAdapter#getKillPwd
	 * (org.rifidi.edge.tags.data.PhysicalTagModel)
	 */
	@Override
	public KillPwd_ALEField getKillPwd(PhysicalTagModel tagModel)
			throws ALEException {
		throw new FieldNotFoundALEException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.ale.fields.adapters.ALEPhysicalTagModelAdapter#getAccessPwd
	 * (org.rifidi.edge.tags.data.PhysicalTagModel)
	 */
	@Override
	public AccessPwd_ALEField getAccessPwd(PhysicalTagModel tagModel)
			throws ALEException {
		throw new FieldNotFoundALEException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.ale.fields.adapters.ALEPhysicalTagModelAdapter#getEPCBank
	 * (org.rifidi.edge.tags.data.PhysicalTagModel)
	 */
	@Override
	public EPCBank_ALEField getEPCBank(PhysicalTagModel tagModel)
			throws ALEException {
		throw new FieldNotFoundALEException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.ale.fields.adapters.ALEPhysicalTagModelAdapter#getTIDBank
	 * (org.rifidi.edge.tags.data.PhysicalTagModel)
	 */
	@Override
	public TIDBank_ALEField getTIDBank(PhysicalTagModel tagModel)
			throws ALEException {
		throw new FieldNotFoundALEException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.ale.fields.adapters.ALEPhysicalTagModelAdapter#getUserBank
	 * (org.rifidi.edge.tags.data.PhysicalTagModel)
	 */
	@Override
	public UserBank_ALEField getUserBank(PhysicalTagModel tagModel)
			throws ALEException {
		throw new FieldNotFoundALEException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.ale.fields.adapters.ALEPhysicalTagModelAdapter#getAFI
	 * (org.rifidi.edge.tags.data.PhysicalTagModel)
	 */
	@Override
	public AFI_ALEField getAFI(PhysicalTagModel tagModel) throws ALEException {
		throw new FieldNotFoundALEException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.ale.fields.adapters.ALEPhysicalTagModelAdapter#getNSI
	 * (org.rifidi.edge.tags.data.PhysicalTagModel)
	 */
	@Override
	public NSI_ALEField getNSI(PhysicalTagModel tagModel) throws ALEException {
		throw new FieldNotFoundALEException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.ale.fields.adapters.ALEPhysicalTagModelAdapter#getFixed
	 * (org.rifidi.edge.tags.data.PhysicalTagModel,
	 * org.rifidi.edge.ale.fields.generic.FixedAddress)
	 */
	@Override
	public Fixed_ALEField getFixed(PhysicalTagModel tagModel,
			FixedAddress address) throws ALEException {
		C1G1PhysicalTagModel gen1Tag = checkTagModel(tagModel);
		try {
			return new Fixed_ALEField(gen1Tag.read(address.get_bank(), address
					.get_length(), address.get_offset()));
		} catch (IllegalBankAccessException e) {
			throw new FieldNotFoundALEException();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.ale.fields.adapters.ALEPhysicalTagModelAdapter#getVariable
	 * (org.rifidi.edge.tags.data.PhysicalTagModel,
	 * org.rifidi.edge.ale.fields.generic.VariableAddress)
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
	private C1G1PhysicalTagModel checkTagModel(PhysicalTagModel tagModel) {
		if (tagModel instanceof C1G1PhysicalTagModel) {
			return (C1G1PhysicalTagModel) tagModel;

		} else {
			throw new IllegalArgumentException("tagModel must be instance of "
					+ C1G2PhysicalTagModel.class);
		}
	}

}
