/* 
 *  ReportRunner.java
 *  Created:	Mar 3, 2009
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
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.rifidi.edge.client.ale.api.util.DeserializerUtil;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReport;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReportGroup;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReportGroupListMember;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReports;
import org.rifidi.edge.client.ale.api.xsd.epcglobal.EPC;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class ReportRunner implements Runnable {

	private ServerSocket ss = null;
	private WritableList list = null;

	/**
	 * @param ss2
	 * @param list
	 */
	public ReportRunner(ServerSocket ss, WritableList list) {
		super();
		this.ss = ss;
		this.list = list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

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
			ECReports reports = DeserializerUtil
					.deserializeECReports(parseStream);
			if (reports != null) {
				// call the handler that will process the ECReports
				handleReports(reports);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void handleReports(ECReports reports) throws IOException,
			JAXBException {
		System.out.println("Handling incomming reports");

		List<ECReport> theReports = reports.getReports().getReport();
		// collect all the tags
		List<EPC> epcs = new LinkedList<EPC>();
		if (theReports != null) {
			for (ECReport report : theReports) {
				if (report.getGroup() != null) {
					for (ECReportGroup group : report.getGroup()) {
						if (group.getGroupList() != null) {
							for (ECReportGroupListMember member : group
									.getGroupList().getMember()) {
								if (member.getRawHex() != null) {
									epcs.add(member.getRawHex());
//									if(!list.contains(member)){
//										list.add(member);
//									}
									
								}
							}
						}
					}
				}
			}
		}
		
//		while(list.listIterator().hasNext()) {
//			ECReportGroupListMember member = (ECReportGroupListMember)list.listIterator().next();
//			if(!epcs.contains(member.getEpc())){
//				list.remove(member);
//			}
//			
//		}
			
		
		if (epcs.size() == 0) {
			System.out.println("no epc received - generating no event");
			return;
		} else {
			for (EPC epc : epcs) {
				System.out.println(epc.getValue());
				if(!list.contains(epc))list.add(epc);
				
			}
		}

	}
}
