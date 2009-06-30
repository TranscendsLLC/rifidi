/*
 *  LLRPROSpecCommand.java
 *
 *  Created:	Mar 9, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.llrp.commands;

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
import org.llrp.ltk.generated.parameters.PeriodicTriggerValue;
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
 * This class creates a ROSpec which can be used for a GetTagList command.
 * 
 * @author Matthew Dean
 */
public class LLRPROSpecCommand extends AbstractLLRPCommand {

	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(LLRPROSpecCommand.class);

	/**
	 * The ROSpecID.
	 */
	private int rospec_id = -1;

	private int rospec_trigger = -1;

	private int rospec_duration = -1;

	/**
	 * The session.
	 */
	private LLRPReaderSession session = null;

	/**
	 * The initial antennaSequence.
	 */
	private String antennaSequence = "0";

	private UnsignedShortArray antenna_array = null;

	/**
	 * The constructor for the LLRPROSpecCommand.
	 * 
	 * @param commandID
	 */
	public LLRPROSpecCommand(String commandID) {
		super(commandID);
		antenna_array = new UnsignedShortArray();
	}

	/**
	 * Sets teh ROSpecID.
	 * 
	 * @param rospec_id
	 */
	public void setRoSpecID(int rospec_id) {
		this.rospec_id = rospec_id;
	}

	/**
	 * Sets teh ROSpecID.
	 * 
	 * @param rospec_id
	 */
	public void setRoSpecTrigger(int rospec_trigger) {
		this.rospec_trigger = rospec_trigger;
	}

	/**
	 * Sets teh ROSpecID.
	 * 
	 * @param rospec_id
	 */
	public void setRoSpecDuration(int rospec_duration) {
		this.rospec_duration = rospec_duration;
	}

	/**
	 * Gets the AntennaID.
	 * 
	 * @return
	 */
	public String getAntennaIDs() {
		return antennaSequence;
	}

	/**
	 * Sets the antennaIDs.
	 */
	public void setAntennaIDs(String antennaIDs) {
		this.antennaSequence = antennaIDs;
		String[] splitstring = antennaIDs.split(",");

		for (String i : splitstring) {
			antenna_array.add(new UnsignedShort(i));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		this.session = (LLRPReaderSession) this.readerSession;

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
				ro.setROSpecID(new UnsignedInteger(this.rospec_id));
				ro.setPriority(new UnsignedByte(0));
				ro.setCurrentState(new ROSpecState(ROSpecState.Disabled));

				ROBoundarySpec rbs = new ROBoundarySpec();
				ROSpecStartTrigger rst = new ROSpecStartTrigger();
				if(this.rospec_trigger==2) {
					rst.setROSpecStartTriggerType(new ROSpecStartTriggerType(
							ROSpecStartTriggerType.Periodic));
					PeriodicTriggerValue ptv = new PeriodicTriggerValue();
					ptv.setPeriod(new UnsignedInteger(100));
					ptv.setOffset(new UnsignedInteger(0));
					rst.setPeriodicTriggerValue(ptv);
				} else {
					rst.setROSpecStartTriggerType(new ROSpecStartTriggerType(
							ROSpecStartTriggerType.Null));
				}
				rbs.setROSpecStartTrigger(rst);
				ROSpecStopTrigger rstopt = new ROSpecStopTrigger();
				if (this.rospec_trigger == 2) {
					rstopt.setROSpecStopTriggerType(new ROSpecStopTriggerType(
							ROSpecStopTriggerType.Duration));
					rstopt.setDurationTriggerValue(new UnsignedInteger(
							this.rospec_duration));
				} else {
					rstopt.setROSpecStopTriggerType(new ROSpecStopTriggerType(
							ROSpecStopTriggerType.Null));
					rstopt.setDurationTriggerValue(new UnsignedInteger(0));
				}

				rbs.setROSpecStopTrigger(rstopt);
				ro.setROBoundarySpec(rbs);

				AISpec ais = new AISpec();
				UnsignedShortArray usa = antenna_array;
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
				if (this.rospec_trigger == 2) {
					rrs.setROReportTrigger(new ROReportTriggerType(
							ROReportTriggerType.Upon_N_Tags_Or_End_Of_ROSpec));
				} else {
					rrs.setROReportTrigger(new ROReportTriggerType(
							ROReportTriggerType.None));
				}
				rrs.setN(new UnsignedShort(0));
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

				session.transact(enRspec);
				
				if(this.rospec_trigger!=2) {
					START_ROSPEC sr = new START_ROSPEC();
					sr.setROSpecID(new UnsignedInteger(this.rospec_id));
					
					session.transact(sr);
				}
			


				

				logger.debug(response.toXMLString());

			}
		} catch (ClassCastException e) {
			logger.error(e);
		} catch (InvalidLLRPMessageException e) {
			logger.error(e);
		}
	}

}
