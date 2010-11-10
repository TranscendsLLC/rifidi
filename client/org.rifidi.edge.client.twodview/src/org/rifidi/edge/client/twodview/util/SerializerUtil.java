/* 
 *  SerializerUtil.java
 * 
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
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
			if(iFile.exists()){
			iFile.setContents(
					new ByteArrayInputStream(fileOutput.toByteArray()),
					IFile.FORCE, null);
			}else{
				iFile.create(new ByteArrayInputStream(fileOutput.toByteArray()), IFile.FORCE, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
