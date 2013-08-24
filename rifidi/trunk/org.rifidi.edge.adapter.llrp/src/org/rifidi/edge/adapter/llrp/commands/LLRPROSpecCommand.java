/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.llrp.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.JDOMException;
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
import org.llrp.ltk.util.Util;
import org.rifidi.edge.adapter.llrp.AbstractLLRPCommand;
import org.rifidi.edge.adapter.llrp.LLRPReaderSession;

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
	private int rospec_id = 1;

	private String triggerType;

	private int rospec_duration = -1;
	
	private String filename="config"+File.separator+"default.llrp";

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
	public void setRoSpecTrigger(String type) {
		this.triggerType = type;
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
	 * @see org.rifidi.edge.sensors.commands.TimeoutCommand#execute()
	 */
	@Override
	protected void execute() throws TimeoutException {
		this.session = (LLRPReaderSession) this.sensorSession;
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
				logger.warn("ROSPEC with ID " + this.rospec_id
						+ " has already been created");
			} else {
				String directory = System.getProperty("org.rifidi.home");
				String filepath = directory + File.separator + filename;
				File llrpFile = new File(filepath);
				if (!llrpFile.exists()) {
					ADD_ROSPEC addRospec = new ADD_ROSPEC();

					ROSpec ro = new ROSpec();
					ro.setROSpecID(new UnsignedInteger(this.rospec_id));
					ro.setPriority(new UnsignedByte(0));
					ro.setCurrentState(new ROSpecState(ROSpecState.Disabled));

					ROBoundarySpec rbs = new ROBoundarySpec();
					ROSpecStartTrigger rst = new ROSpecStartTrigger();
					if (triggerType.equals("PUSH")) {
						rst.setROSpecStartTriggerType(new ROSpecStartTriggerType(
								ROSpecStartTriggerType.Immediate));
					} else {
						rst.setROSpecStartTriggerType(new ROSpecStartTriggerType(
								ROSpecStartTriggerType.Null));
					}
					rbs.setROSpecStartTrigger(rst);
					ROSpecStopTrigger rstopt = new ROSpecStopTrigger();
					if (triggerType.equals("PUSH")) {
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
					if (triggerType.equals("PUSH")) {
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
				} else {
					ADD_ROSPEC rospec = (ADD_ROSPEC) Util.loadXMLLLRPMessage(llrpFile);
					
					session.transact(rospec);
				}
				
				ENABLE_ROSPEC enRspec = new ENABLE_ROSPEC();
				enRspec.setROSpecID(new UnsignedInteger(this.rospec_id));

				session.transact(enRspec);

				if (!triggerType.equals("PUSH")) {
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
		} catch (FileNotFoundException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		} catch (JDOMException e) {
			logger.error(e);
		}

	}

}
