/* 
 *  SerializerUtil.java
 *  Created:	May 11, 2009
 *  Project:	RiFidi org.rifidi.edge.client.twodview
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.twodview.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.eclipse.core.resources.IFile;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class SerializerUtil {

	public static void serializeEdgeUi(EdgeUi edgeUi, IFile iFile) {
		try {
			ArrayList<Class> classes = new ArrayList<Class>();
			classes.add(EdgeUi.class);
			classes.add(ReaderPos.class);
			// String JAXB_CONTEXT = "org.rifidi.edge.client.twodview.util";
			JAXBContext context = JAXBContext.newInstance(classes
					.toArray(new Class[0]));
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					new Boolean(true));
			ByteArrayOutputStream fileOutput = new ByteArrayOutputStream();
			marshaller.marshal(edgeUi, fileOutput);
			iFile.setContents(
					new ByteArrayInputStream(fileOutput.toByteArray()),
					IFile.FORCE, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
