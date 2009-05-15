/* 
 *  DeserializerUtil.java
 *  Created:	May 11, 2009
 *  Project:	RiFidi org.rifidi.edge.client.twodview
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.twodview.util;

import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class DeserializerUtil {

	public static EdgeUi deserializeEdgeUi(IFile iFile) {
		EdgeUi eui = null;

		try {
			ArrayList<Class> classes = new ArrayList<Class>();
			classes.add(EdgeUi.class);
			classes.add(ReaderPos.class);
			// String JAXB_CONTEXT = "org.rifidi.edge.client.twodview.util";
			JAXBContext context = JAXBContext.newInstance(classes
					.toArray(new Class[0]));
			Unmarshaller unmarshaller = context.createUnmarshaller();
			eui = (EdgeUi) unmarshaller.unmarshal(iFile.getContents());
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return eui;
	}

}
