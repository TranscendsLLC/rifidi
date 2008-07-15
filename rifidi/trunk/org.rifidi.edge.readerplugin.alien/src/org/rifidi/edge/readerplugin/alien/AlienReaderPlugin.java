/* 
 * AlienReaderPlugin.java
 *  Created:	Jul 10, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerplugin.alien;

import java.util.ArrayList;
import java.util.List;

import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.ReaderPlugin;
import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;
import org.rifidi.edge.core.readerplugin.protocol.MessageProtocol;
import org.rifidi.edge.readerplugin.alien.commands.AlienTagStreamCommand;
import org.rifidi.edge.readerplugin.alien.commands.GetTagList;
import org.rifidi.edge.readerplugin.alien.commands.general.AntennaSequence;
import org.rifidi.edge.readerplugin.alien.commands.general.BaudRate;
import org.rifidi.edge.readerplugin.alien.commands.general.DSPVersion;
import org.rifidi.edge.readerplugin.alien.commands.general.FactorySettings;
import org.rifidi.edge.readerplugin.alien.commands.general.Function;
import org.rifidi.edge.readerplugin.alien.commands.general.MaxAntenna;
import org.rifidi.edge.readerplugin.alien.commands.general.Password;
import org.rifidi.edge.readerplugin.alien.commands.general.RFAttenuation;
import org.rifidi.edge.readerplugin.alien.commands.general.RFLevel;
import org.rifidi.edge.readerplugin.alien.commands.general.RFModulation;
import org.rifidi.edge.readerplugin.alien.commands.general.ReaderName;
import org.rifidi.edge.readerplugin.alien.commands.general.ReaderNumber;
import org.rifidi.edge.readerplugin.alien.commands.general.ReaderType;
import org.rifidi.edge.readerplugin.alien.commands.general.ReaderVersion;
import org.rifidi.edge.readerplugin.alien.commands.general.Uptime;
import org.rifidi.edge.readerplugin.alien.commands.general.Username;
import org.rifidi.edge.readerplugin.alien.commands.gpio.ExternalInput;
import org.rifidi.edge.readerplugin.alien.commands.gpio.ExternalOutput;
import org.rifidi.edge.readerplugin.alien.commands.gpio.InvertExternalInput;
import org.rifidi.edge.readerplugin.alien.commands.gpio.InvertExternalOutput;
import org.rifidi.edge.readerplugin.alien.commands.network.CommandPort;
import org.rifidi.edge.readerplugin.alien.commands.network.DHCP;
import org.rifidi.edge.readerplugin.alien.commands.network.DHCPTimeout;
import org.rifidi.edge.readerplugin.alien.commands.network.DNS;
import org.rifidi.edge.readerplugin.alien.commands.network.DebugHost;
import org.rifidi.edge.readerplugin.alien.commands.network.Gateway;
import org.rifidi.edge.readerplugin.alien.commands.network.HeartbeatAddress;
import org.rifidi.edge.readerplugin.alien.commands.network.HeartbeatCount;
import org.rifidi.edge.readerplugin.alien.commands.network.HeartbeatNow;
import org.rifidi.edge.readerplugin.alien.commands.network.HeartbeatPort;
import org.rifidi.edge.readerplugin.alien.commands.network.HeartbeatTime;
import org.rifidi.edge.readerplugin.alien.commands.network.HostLog;
import org.rifidi.edge.readerplugin.alien.commands.network.Hostname;
import org.rifidi.edge.readerplugin.alien.commands.network.IPAddress;
import org.rifidi.edge.readerplugin.alien.commands.network.MACAddress;
import org.rifidi.edge.readerplugin.alien.commands.network.Netmask;
import org.rifidi.edge.readerplugin.alien.commands.network.NetworkTimeout;
import org.rifidi.edge.readerplugin.alien.commands.network.NetworkUpgrade;
import org.rifidi.edge.readerplugin.alien.commands.network.Ping;
import org.rifidi.edge.readerplugin.alien.commands.network.UpgradeAddress;
import org.rifidi.edge.readerplugin.alien.commands.network.WWWPort;
import org.rifidi.edge.readerplugin.alien.commands.time.Time;
import org.rifidi.edge.readerplugin.alien.commands.time.TimeServer;
import org.rifidi.edge.readerplugin.alien.commands.time.TimeZone;
import org.rifidi.edge.readerplugin.alien.protocol.AlienMessageProtocol;

/**
 * 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class AlienReaderPlugin implements ReaderPlugin {

	/**
	 * The list of commmands
	 */
	private List<Class<? extends Command>> commands;

	/**
	 * 
	 */
	public AlienReaderPlugin() {
		commands = new ArrayList<Class<? extends Command>>();
		
		//TODO: Figure out a way how to get all commands in the plugin 
		//dynamically and add them to the list.
		
		/* commands that come with this plugin. */
		commands.add(AlienTagStreamCommand.class);
		commands.add(GetTagList.class);
		
		/* gpio */
		commands.add(InvertExternalInput.class);
		commands.add(InvertExternalOutput.class);
		commands.add(ExternalInput.class);
		commands.add(ExternalOutput.class);
		
		/* general */
		commands.add(AntennaSequence.class);
		commands.add(BaudRate.class);
		commands.add(DSPVersion.class);
		commands.add(FactorySettings.class);
		commands.add(Function.class);
		commands.add(MaxAntenna.class);
		commands.add(Password.class);
		commands.add(ReaderName.class);
		commands.add(ReaderNumber.class);
		commands.add(ReaderType.class);
		commands.add(ReaderVersion.class);
		commands.add(RFAttenuation.class);
		commands.add(RFLevel.class);
		commands.add(RFModulation.class);
		commands.add(Uptime.class);
		commands.add(Username.class);
		
		/* network */
		commands.add(CommandPort.class);
		commands.add(DebugHost.class);
		commands.add(DHCP.class);
		commands.add(DHCPTimeout.class);
		commands.add(DNS.class);
		commands.add(Gateway.class);
		commands.add(HeartbeatAddress.class);
		commands.add(HeartbeatCount.class);
		commands.add(HeartbeatNow.class);
		commands.add(HeartbeatPort.class);
		commands.add(HeartbeatTime.class);
		commands.add(HostLog.class);
		commands.add(Hostname.class);
		commands.add(IPAddress.class);
		commands.add(MACAddress.class);
		commands.add(Netmask.class);
		commands.add(NetworkTimeout.class);
		commands.add(NetworkUpgrade.class);
		commands.add(Ping.class);
		commands.add(UpgradeAddress.class);
		commands.add(WWWPort.class);
		
		/* time */
		commands.add(Time.class);
		commands.add(TimeServer.class);
		commands.add(TimeZone.class);
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.ReaderPlugin#getAvailableCommands()
	 */
	@Override
	public List<Class<? extends Command>> getAvailableCommands() {
		return commands;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.ReaderPlugin#getMessageProtocol()
	 */
	@Override
	public MessageProtocol getMessageProtocol() {
		return new AlienMessageProtocol();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.ReaderPlugin#addCommand(java.util.List)
	 */
	@Override
	public void addCommand(List<Class<? extends Command>> commands) {
		commands.addAll(commands);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.ReaderPlugin#getConnectionManager(org.rifidi.edge.core.readerplugin.ReaderInfo)
	 */
	@Override
	public ConnectionManager getConnectionManager(ReaderInfo readerInfo) {
		return new AlienConnectionManager(readerInfo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.ReaderPlugin#removeCommand(java.util.List)
	 */
	@Override
	public void removeCommand(List<Class<? extends Command>> commands) {
		commands.removeAll(commands);
	}
}
