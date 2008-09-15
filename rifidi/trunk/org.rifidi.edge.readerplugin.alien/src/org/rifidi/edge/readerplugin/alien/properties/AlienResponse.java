/*
 *  AlienResponse.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.readerplugin.alien.properties;

import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AlienResponse {

	String response = null;
	boolean error = false;

	public void setResponseMessage(String response) {
		if (response == null) {
			error = true;
			this.response = "Error";
		} else {
			if (response.contains("=")) {
				String[] temp = response.split("=");
				this.response = temp[1].trim();

			} else {
				error = true;
				if (response.startsWith("Error")) {
					this.response = response.trim();
				} else {
					this.response = "Error";
				}
			}
		}
	}

	public Element formulateResponseXML(Element parentElement,
			String propertyName, String formElementName) {
		Element returnNode = parentElement.getOwnerDocument().createElement(
				propertyName);
		Element formElementNode = parentElement.getOwnerDocument()
				.createElement(formElementName);
		if (error) {
			formElementNode.setAttribute("error", "true");
		} else {
			formElementNode.setAttribute("error", "false");
		}
		Text textNode = parentElement.getOwnerDocument().createTextNode(
				response);
		formElementNode.appendChild(textNode);
		returnNode.appendChild(formElementNode);
		return returnNode;
	}

}
