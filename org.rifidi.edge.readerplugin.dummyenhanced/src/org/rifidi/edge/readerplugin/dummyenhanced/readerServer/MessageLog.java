package org.rifidi.edge.readerplugin.dummyenhanced.readerServer;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MessageLog {
	//@XmlValue
	@XmlElementWrapper
	ArrayList<Object> log;
}
