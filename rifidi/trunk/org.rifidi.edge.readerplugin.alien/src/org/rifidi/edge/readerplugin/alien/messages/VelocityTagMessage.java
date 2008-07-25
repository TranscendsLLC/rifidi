package org.rifidi.edge.readerplugin.alien.messages;

import javax.xml.bind.annotation.XmlRootElement;

import org.rifidi.edge.core.readerplugin.messages.impl.TagMessage;

@XmlRootElement
public class VelocityTagMessage extends TagMessage {
	private float velocity;

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
}
