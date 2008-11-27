/*
 *  ALEPhysicalTagModelAdapter.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.ale.fields.adapters;

import org.rifidi.edge.ale.exceptions.ALEException;
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

/**
 * @author kyle
 * 
 */
public interface ALEPhysicalTagModelAdapter {

	public Fixed_ALEField getFixed(PhysicalTagModel tagModel,
			FixedAddress address) throws ALEException;

	public Variable_ALEField getVariable(PhysicalTagModel tagModel,
			VariableAddress address) throws ALEException;
	
	public EPC_ALEField getEPC(PhysicalTagModel tagModel) throws ALEException;
	
	public KillPwd_ALEField getKillPwd(PhysicalTagModel tagModel) throws ALEException;
	
	public AccessPwd_ALEField getAccessPwd(PhysicalTagModel tagModel) throws ALEException;
	
	public EPCBank_ALEField getEPCBank(PhysicalTagModel tagModel) throws ALEException;
	
	public TIDBank_ALEField getTIDBank(PhysicalTagModel tagModel) throws ALEException;
	
	public UserBank_ALEField getUserBank(PhysicalTagModel tagModel) throws ALEException;
	
	public AFI_ALEField getAFI(PhysicalTagModel tagModel) throws ALEException;
	
	public NSI_ALEField getNSI(PhysicalTagModel tagModel) throws ALEException;

}
