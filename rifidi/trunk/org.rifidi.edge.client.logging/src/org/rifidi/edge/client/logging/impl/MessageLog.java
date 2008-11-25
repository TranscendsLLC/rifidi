package org.rifidi.edge.client.logging.impl;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement
public class MessageLog {
	//@XmlValue
	@XmlElementWrapper
	ArrayList<Object> log;
}
