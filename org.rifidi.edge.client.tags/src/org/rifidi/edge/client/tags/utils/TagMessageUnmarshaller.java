package org.rifidi.edge.client.tags.utils;

import java.io.Reader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.rifidi.edge.core.readerplugin.messages.impl.EnhancedTagMessage;
import org.rifidi.edge.core.readerplugin.messages.impl.TagMessage;

public class TagMessageUnmarshaller {

	private static TagMessageUnmarshaller instance;
	private JAXBContext context;
	private Unmarshaller unmarshaller;

	private TagMessageUnmarshaller() throws JAXBException {

		context = JAXBContext.newInstance(TagMessage.class,
				EnhancedTagMessage.class);
		unmarshaller = context.createUnmarshaller();

	}

	public static TagMessageUnmarshaller getInstance() throws JAXBException {
		if (instance == null) {
			instance = new TagMessageUnmarshaller();
		}
		return instance;
	}

	public TagMessage unmarshall(Reader reader) throws JAXBException {
		if (context != null) {

			return (TagMessage) unmarshaller.unmarshal(reader);

		}
		return null;
	}

}
