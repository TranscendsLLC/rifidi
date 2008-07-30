package org.rifidi.edge.readerplugin.dummyenhanced.readerServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.common.utilities.converter.ByteAndHexConvertingUtility;
import org.rifidi.edge.common.utilities.resource.Resource;
import org.rifidi.edge.core.readerplugin.messages.impl.EnhancedTagMessage;
import org.rifidi.edge.core.readerplugin.messages.impl.TagMessage;


public class DummyReaderServer2 {

	Log logger = LogFactory.getLog(DummyReaderServer2.class);
	private boolean run=false;
	private int port;
	private boolean echo;
	private Thread t;
	
	private static List<Object> tags = new ArrayList<Object>();
	
	public static void main(String[] args) throws Exception {
		if (args.length == 2) {
			DummyReaderServer2 drs = new DummyReaderServer2(Integer.parseInt(args[0]), Boolean.parseBoolean(args[1]));
			drs.start();
		} else {
			DummyReaderServer2 drs = new DummyReaderServer2(2000, false);
			drs.start();
		}
		
		JAXBContext context = JAXBContext.newInstance(TagMessage.class,
				EnhancedTagMessage.class, MessageLog.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		
		MessageLog ml = (MessageLog) unmarshaller.unmarshal(Resource.getResource(DummyReaderServer2.class, "taglog.xml"));
		tags = ml.log;
		//ServerConsole console = new ServerConsole(tags);
		//console.go();
	}
	
	public void start(){
		run = true;
		t = new Thread(new DummyServerThread(), "Dummy Server Thread");
		t.start();
	}
	
	public void stop(){
		run = false;
		t.interrupt();
		t=null;
	}

	public DummyReaderServer2(int _port, boolean echo) {
		this.port = _port;
		this.echo = echo;
	}
	
	private class DummyServerThread implements Runnable{

		@Override
		public void run() {
			logger.debug("starting ServerSocket at " + port);
			ServerSocket serverSocket = null;
			try {
				serverSocket = new ServerSocket(port);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			while (run) {
				try {
					logger.debug("Waiting for Connection");
					Socket socket = serverSocket.accept();
					logger.debug("Connection");
					BufferedReader in = new BufferedReader(new InputStreamReader(
							socket.getInputStream()));
					BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
							socket.getOutputStream()));
					String input;
					Iterator<Object> iterator = tags.iterator();
					while ((input = in.readLine()) != null) {
						input.trim();
						if (input.toLowerCase().equals("quit")) {
							System.out.println("Closing socket on quit command");
							in.close();
							out.close();
							socket.close();
						} else if (input.toLowerCase().equals("tag")){
							synchronized (tags) {
								if (iterator.hasNext()) {
									Object o = iterator.next();
									if (o instanceof EnhancedTagMessage) {
										EnhancedTagMessage tag = (EnhancedTagMessage) o;
										out.write(ByteAndHexConvertingUtility.toHexString(tag.getId()).replace(" ", "") + "|");
										out.write(Long.toString(System.currentTimeMillis()) + "|");
										out.write(Integer.toString( tag.getAntennaId()) + "|");
										out.write(Float.toString( tag.getVelocity()) + "|");
										out.write(Float.toString( tag.getSignalStrength()));
										
									
										out.write("\n");
									}
								}
									
								out.write("\n");
							}
						} else {
							out.write("405: Bad Command.\n\n");
						}
						out.flush();
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					logger.debug("Connection closed");
				} catch (IOException e) {
					logger.debug("Connection lost");
				}
			}
			
		}
		
	}
}
