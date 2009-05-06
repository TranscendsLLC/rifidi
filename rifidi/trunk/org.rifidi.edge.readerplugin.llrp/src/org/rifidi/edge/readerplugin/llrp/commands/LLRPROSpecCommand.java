/*
 *  LLRPROSpecCommand.java
 *
 *  Created:	Mar 20, 2009
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerplugin.llrp.commands;
//TODO: Comments
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.llrp.ltk.exceptions.InvalidLLRPMessageException;
import org.llrp.ltk.generated.enumerations.AISpecStopTriggerType;
import org.llrp.ltk.generated.enumerations.AirProtocols;
import org.llrp.ltk.generated.enumerations.ROReportTriggerType;
import org.llrp.ltk.generated.enumerations.ROSpecStartTriggerType;
import org.llrp.ltk.generated.enumerations.ROSpecState;
import org.llrp.ltk.generated.enumerations.ROSpecStopTriggerType;
import org.llrp.ltk.generated.messages.ADD_ROSPEC;
import org.llrp.ltk.generated.messages.ENABLE_ROSPEC;
import org.llrp.ltk.generated.messages.GET_ROSPECS;
import org.llrp.ltk.generated.messages.GET_ROSPECS_RESPONSE;
import org.llrp.ltk.generated.messages.START_ROSPEC;
import org.llrp.ltk.generated.parameters.AISpec;
import org.llrp.ltk.generated.parameters.AISpecStopTrigger;
import org.llrp.ltk.generated.parameters.C1G2EPCMemorySelector;
import org.llrp.ltk.generated.parameters.InventoryParameterSpec;
import org.llrp.ltk.generated.parameters.ROBoundarySpec;
import org.llrp.ltk.generated.parameters.ROReportSpec;
import org.llrp.ltk.generated.parameters.ROSpec;
import org.llrp.ltk.generated.parameters.ROSpecStartTrigger;
import org.llrp.ltk.generated.parameters.ROSpecStopTrigger;
import org.llrp.ltk.generated.parameters.TagReportContentSelector;
import org.llrp.ltk.types.Bit;
import org.llrp.ltk.types.UnsignedByte;
import org.llrp.ltk.types.UnsignedInteger;
import org.llrp.ltk.types.UnsignedShort;
import org.llrp.ltk.types.UnsignedShortArray;
import org.rifidi.edge.readerplugin.llrp.AbstractLLRPCommand;
import org.rifidi.edge.readerplugin.llrp.LLRPReaderSession;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class LLRPROSpecCommand extends AbstractLLRPCommand {

	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(LLRPROSpecCommand.class);

	/**
	 * 
	 */
	private int rospec_id = -1;

	/**
	 * 
	 */
	private LLRPReaderSession session = null;
	
	private String antennaSequence = "0";

	/**
	 * 
	 * @param commandID
	 * @param session
	 */
	public LLRPROSpecCommand(String commandID) {
		super(commandID);
	}

	/**
	 * 
	 * @param rospec_id
	 */
	public void setRoSpecID(int rospec_id) {
		this.rospec_id = rospec_id;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getAntennaIDs() {
		return antennaSequence;
	}
	
	/**
	 * 
	 */
	public void setAntennaIDs(String antennaIDs) {
		
	}
	
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		this.session = (LLRPReaderSession)this.readerSession;
		//System.out.println("Session has been set: " + this.session);
		//System.out.println("Running LLRPROSpecCommand");
		try {
			boolean is_taken = false;
			GET_ROSPECS rospecs = new GET_ROSPECS();
			GET_ROSPECS_RESPONSE response = null;
			try {
				response = (GET_ROSPECS_RESPONSE) session.transact(rospecs);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			List<ROSpec> rospecList = response.getROSpecList();
			for (ROSpec rspec : rospecList) {
				if (this.rospec_id == rspec.getROSpecID().intValue()) {
					is_taken = true;
				}
			}
			if (is_taken) {
				// TODO: handle this
			} else {
				ADD_ROSPEC addRospec = new ADD_ROSPEC();

				ROSpec ro = new ROSpec();
				ro.setROSpecID(new UnsignedInteger(1));
				ro.setPriority(new UnsignedByte(0));
				ro.setCurrentState(new ROSpecState(ROSpecState.Disabled));

				ROBoundarySpec rbs = new ROBoundarySpec();
				ROSpecStartTrigger rst = new ROSpecStartTrigger();
				rst.setROSpecStartTriggerType(new ROSpecStartTriggerType(
						ROSpecStartTriggerType.Null));
				rbs.setROSpecStartTrigger(rst);
				ROSpecStopTrigger rstopt = new ROSpecStopTrigger();
				rstopt.setROSpecStopTriggerType(new ROSpecStopTriggerType(
						ROSpecStopTriggerType.Null));
				rstopt.setDurationTriggerValue(new UnsignedInteger(0));
				rbs.setROSpecStopTrigger(rstopt);
				ro.setROBoundarySpec(rbs);

				AISpec ais = new AISpec();
				UnsignedShortArray usa = new UnsignedShortArray();
				usa.add(new UnsignedShort(0));
				ais.setAntennaIDs(usa);
				AISpecStopTrigger ast = new AISpecStopTrigger();
				ast.setAISpecStopTriggerType(new AISpecStopTriggerType(
						AISpecStopTriggerType.Null));
				ast.setDurationTrigger(new UnsignedInteger(0));
				ais.setAISpecStopTrigger(ast);
				InventoryParameterSpec ips = new InventoryParameterSpec();
				ips.setInventoryParameterSpecID(new UnsignedShort(9));
				AirProtocols ap = new AirProtocols();
				ap.set(AirProtocols.EPCGlobalClass1Gen2);
				ips.setProtocolID(ap);
				ais.addToInventoryParameterSpecList(ips);
				ro.addToSpecParameterList(ais);

				ROReportSpec rrs = new ROReportSpec();
				rrs.setROReportTrigger(new ROReportTriggerType(
						ROReportTriggerType.None));
				rrs.setN(new UnsignedShort(1));
				TagReportContentSelector trcs = new TagReportContentSelector();
				trcs.setEnableROSpecID(new Bit(1));
				trcs.setEnableSpecIndex(new Bit(1));
				trcs.setEnableInventoryParameterSpecID(new Bit(1));
				trcs.setEnableAntennaID(new Bit(1));
				trcs.setEnableChannelIndex(new Bit(1));
				trcs.setEnablePeakRSSI(new Bit(1));
				trcs.setEnableFirstSeenTimestamp(new Bit(1));
				trcs.setEnableLastSeenTimestamp(new Bit(1));
				trcs.setEnableTagSeenCount(new Bit(1));
				trcs.setEnableAccessSpecID(new Bit(1));
				C1G2EPCMemorySelector cgems = new C1G2EPCMemorySelector();
				cgems.setEnableCRC(new Bit(1));
				cgems.setEnablePCBits(new Bit(1));
				trcs.addToAirProtocolEPCMemorySelectorList(cgems);
				rrs.setTagReportContentSelector(trcs);
				ro.setROReportSpec(rrs);

				addRospec.setROSpec(ro);

				// TODO: Check if it worked
				session.transact(addRospec);

				ENABLE_ROSPEC enRspec = new ENABLE_ROSPEC();
				enRspec.setROSpecID(new UnsignedInteger(this.rospec_id));

				START_ROSPEC sr = new START_ROSPEC();
				sr.setROSpecID(new UnsignedInteger(this.rospec_id));

				session.transact(enRspec);

				session.transact(sr);

				logger.debug(response.toXMLString());

			}
		} catch (ClassCastException e) {
			e.printStackTrace();
		} catch (InvalidLLRPMessageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
