package org.rifidi.edge.readerplugin.alien.messages;

import javax.xml.bind.annotation.XmlRootElement;

import org.rifidi.edge.core.readerplugin.messages.impl.TagMessage;

@XmlRootElement
public class EnhancedTagMessage extends TagMessage {
	private float velocity;
	private float distance;
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
	public float getDistance() {
		return distance;
	}

	/**
	 * @param distance the distance to set
	 */
	public void setDistance(float distance) {
		this.distance = distance;
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
