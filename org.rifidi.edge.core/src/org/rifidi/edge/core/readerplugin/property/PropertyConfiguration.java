package org.rifidi.edge.core.readerplugin.property;

import org.rifidi.edge.core.exceptions.RifidiInvalidConfigurationException;
import org.w3c.dom.Document;

public class PropertyConfiguration {
	
	private Document doc;
	
	public PropertyConfiguration(Document doc) {
		this.doc = doc;
	}
	
	public int numProperties() throws RifidiInvalidConfigurationException{
		
		return 0;
	}

}
