/* 
 *  LrView.java
 *  Created:	Feb 16, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.ui
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.ui.views;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.rifidi.edge.client.ale.api.util.DeserializerUtil;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReports;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class LrView extends ViewPart {

	private Log logger = LogFactory.getLog(LrView.class);
	private ServerSocket ss = null;
	private int port = 10000;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(Composite arg0) {
		// create a new server socket to retrieve ECReports
		try {
			ss = new ServerSocket(port);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		while (true) {
			// accept an incoming message
			Socket s;
			try {
				s = ss.accept();

				BufferedReader in = new BufferedReader(new InputStreamReader(s
						.getInputStream()));

				String data = in.readLine();
				String buf = "";
				// ignore the http header
				data = in.readLine();
				data = in.readLine();
				data = in.readLine();
				data = in.readLine();

				while (data != null) {
					buf += data;
					data = in.readLine();
				}
				// create a stream from the buf
		        InputStream parseStream = new ByteArrayInputStream(buf.getBytes());
		        
		        // parse the string through the serializer/deserializer utils.
		        ECReports reports = DeserializerUtil.deserializeECReports(parseStream);
		        if (reports != null) {
		                // call the handler that will process the ECReports
		                handleReports(reports);
		        }
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
			

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
