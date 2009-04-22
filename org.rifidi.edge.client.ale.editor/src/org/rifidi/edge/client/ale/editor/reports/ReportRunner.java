/* 
 *  ReportRunner.java
 *  Created:	Apr 22, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.editor
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.editor.reports;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Text;
import org.rifidi.edge.client.ale.api.util.DeserializerUtil;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReports;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class ReportRunner implements Runnable {

	private ServerSocket ss = null;
	private Text cText = null;
	private CTabItem item=null;
	private Log logger = LogFactory.getLog(ReportRunner.class);

	/**
	 * Runnable that updates the passed in Text and CTabItem with the
	 * information that it withdraws from the server socket.
	 * @param ss ServerSocket to use
	 * @param text Text widget to update with tag data
	 * @param item CTabItem that should get it's Caption updated
	 */
	public ReportRunner(ServerSocket ss, Text text, CTabItem item) {
		this.ss = ss;
		this.cText = text;
		this.item=item;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		Socket s = null;

		try {
			s = ss.accept();
			//BufferedReader that allows for handling the InputStream
			BufferedReader in = new BufferedReader(new InputStreamReader(s
					.getInputStream()));
			// String data to read line by line
			String data = in.readLine();
			// String buf to add together all the lines
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
			// create a stream from buf
			InputStream parseStream = new ByteArrayInputStream(buf.getBytes());

			// parse the string through the serializer/deserializer utils.
			ECReports reports = DeserializerUtil
					.deserializeECReports(parseStream);
			if (reports != null) {
				// call the handler that will process the ECReports
				ReportHandler handler = new ReportHandler();
				// get the reported data into a String List
				ArrayList<String> strings = handler.handleReports(reports);
				
				if (!strings.isEmpty()) {
					String sText = "";
					//format the string for the Text widget
					for (String string : strings) {
						sText += string + "\n";
					}
					// fill text widget
					this.cText.setText(sText);
					// show ammount of reported tags on tab
					this.item.setText("Reports ("+strings.size()+")");

				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

	}

}
