/*
 * AlienAutonomousModeCommandConfiguration.java
 * 
 * Created:     July 22nd, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */
/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.commands;

import javax.management.MBeanInfo;

import org.rifidi.edge.core.configuration.annotations.JMXMBean;
import org.rifidi.edge.core.configuration.annotations.Property;
import org.rifidi.edge.core.configuration.annotations.PropertyType;
import org.rifidi.edge.core.configuration.mbeanstrategies.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;

/**
 * @author Kyle Nuemeier - kyle@pramari.com
 * 
 */
@JMXMBean
public class AlienAutonomousModeCommandConfiguration extends
		AbstractCommandConfiguration<AlienAutonomousModeCommand> {

	private String notifyTrigger = "TrueFalse";
	private String notifyAddressHost = "127.0.0.1";
	private Integer notifyAddressPort = 54321;
	private Integer notifyTime = 0;
	private Integer autoWaitOutput = 0;
	private String autoStartTrigger = "0,0";
	private Integer autoStartPause = 0;
	private Integer autoWorkOutput = 0;
	private String autoStopTrigger = "0,0";
	private Integer autoStopTimer = 1000;
	private Integer autoStopPause = 0;
	private Integer autoTrueOutput = 0;
	private Integer autoTruePause = 0;
	private Integer autoFalseOutput = 0;
	private Integer autoFalsePause = 0;

	public static final MBeanInfo mbeaninfo;
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy
				.getMBeanInfo(AlienAutonomousModeCommandConfiguration.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration#getCommand
	 * (java.lang.String)
	 */
	@Override
	public AlienAutonomousModeCommand getCommand(String readerID) {
		AlienAutonomousModeCommand command = new AlienAutonomousModeCommand(
				super.getID());
		command.setNotifyTrigger(notifyTrigger);
		command.setNotifyAddress(notifyAddressHost + ":" + notifyAddressPort);
		command.setNotifyTime(notifyTime.toString());
		command.setAutoWaitOutput(autoWaitOutput.toString());
		command.setAutoStartTrigger(autoStartTrigger);
		command.setAutoStartPause(autoStartPause.toString());
		command.setAutoWorkOutput(autoWorkOutput.toString());
		command.setAutoStopTrigger(autoStopTrigger);
		command.setAutoStopTimer(autoStopTimer.toString());
		command.setAutoStopPause(autoStopPause.toString());
		command.setAutoTrueOutput(autoTrueOutput.toString());
		command.setAutoTruePause(autoTruePause.toString());
		command.setAutoFalseOutput(autoFalseOutput.toString());
		command.setAutoFalsePause(autoFalsePause.toString());
		return command;
	}

	/**
	 * @return the notifyAddressHost
	 */
	@Property(category = "Notify", defaultValue = "127.0.0.1", description = "The hostname of the place where notifications should be delivered", displayName = "Notify Host", orderValue = 1, type = PropertyType.PT_STRING, writable = true)
	public String getNotifyAddressHost() {
		return notifyAddressHost;
	}

	/**
	 * @param notifyAddressHost
	 *            the notifyAddressHost to set
	 */
	public void setNotifyAddressHost(String notifyAddressHost) {
		this.notifyAddressHost = notifyAddressHost;
	}

	/**
	 * @return the notifyAddressPort
	 */
	@Property(category = "Notify", defaultValue = "54321", description = "The port of the place where notifications should be delivered", displayName = "Notify Port", type = PropertyType.PT_INTEGER, writable = true, minValue = "0", maxValue = "65535", orderValue = 2)
	public Integer getNotifyAddressPort() {
		return notifyAddressPort;
	}

	/**
	 * @param notifyAddressPort
	 *            the notifyAddressPort to set
	 */
	public void setNotifyAddressPort(Integer notifyAddressPort) {
		if (notifyAddressPort < 0 || notifyAddressPort > 65535) {
			throw new IllegalArgumentException(
					"Port range must be between 0 and 65535");
		}
		this.notifyAddressPort = notifyAddressPort;
	}

	/**
	 * @return the autoStopTimer
	 */
	@Property(category = "Autonomous", defaultValue = "1000", description = "Time in MS to wait in the work state before jumping to the evaluation state. A value of -1 indicates the reader should remain in the work state indefinitely or until an AutoStopTrigger is received", displayName = "Auto Stop Timer", type = PropertyType.PT_INTEGER, writable = true, minValue = "-1", orderValue = 1)
	public Integer getAutoStopTimer() {
		return autoStopTimer;
	}

	/**
	 * @param autoStopTimer
	 *            the autoStopTimer to set
	 */
	public void setAutoStopTimer(Integer autoStopTimer) {
		this.autoStopTimer = autoStopTimer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.configuration.RifidiService#getMBeanInfo()
	 */
	@Override
	public MBeanInfo getMBeanInfo() {
		return (MBeanInfo) mbeaninfo.clone();
	}

}
