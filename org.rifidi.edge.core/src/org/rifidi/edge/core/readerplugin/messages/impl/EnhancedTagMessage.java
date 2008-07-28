package org.rifidi.edge.core.readerplugin.messages.impl;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class EnhancedTagMessage extends TagMessage {
	private float velocity;
	private float signalStrength;
	private int antennaId;

	/**
	 * @return the velocity
	 */
	public float getVelocity() {
		return velocity;
	}

	/**
	 * @param velocity the velocity to set
	 */
	public void setVelocity(float velocity) {
		this.velocity = velocity;
	}

	/**
	 * @return the distance
	 */
	public float getSignalStrength() {
		return signalStrength;
	}

	/**
	 * @param distance the distance to set
	 */
	public void setSignalStrength(float distance) {
		this.signalStrength = distance;
	}

	/**
	 * @return the antennaId
	 */
	public int getAntennaId() {
		return antennaId;
	}

	/**
	 * @param antennaId the antennaId to set
	 */
	public void setAntennaId(int antennaId) {
		this.antennaId = antennaId;
	}
}
