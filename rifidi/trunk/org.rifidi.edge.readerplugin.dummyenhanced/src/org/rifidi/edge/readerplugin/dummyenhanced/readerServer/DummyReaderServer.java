package org.rifidi.edge.readerplugin.dummyenhanced.readerServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.common.utilities.converter.ByteAndHexConvertingUtility;
import org.rifidi.edge.core.readerplugin.messages.impl.EnhancedTagMessage;

public class DummyReaderServer {

	Log logger = LogFactory.getLog(DummyReaderServer.class);
	private boolean run = false;
	private int port;
	private boolean echo;
	private Thread t;

	private static List<EnhancedTagMessage> tags = new ArrayList<EnhancedTagMessage>();

	public static void main(String[] args) {
		if (args.length == 2) {
			DummyReaderServer drs = new DummyReaderServer(Integer
					.parseInt(args[0]), Boolean.parseBoolean(args[1]));
			drs.start();
		} else {
			DummyReaderServer drs = new DummyReaderServer(2000, false);
			drs.start();
		}

		EnhancedTagMessage tag1 = new EnhancedTagMessage();
		EnhancedTagMessage tag2 = new EnhancedTagMessage();

		tag1.setId(ByteAndHexConvertingUtility
				.fromHexString("2f347d99d43fffbb38a4f945"));
		tag2.setId(ByteAndHexConvertingUtility
				.fromHexString("2f92ef48ef63e64f7a8cac89"));

		tags.add(tag1);
		tags.add(tag2);

		ServerConsole console = new ServerConsole(tags);
		console.go();
	}

	public void start() {
		run = true;
		t = new Thread(new DummyServerThread(), "Dummy Server Thread");
		t.start();
	}

	public void stop() {
		run = false;
		t.interrupt();
		t = null;
	}

	public DummyReaderServer(int _port, boolean echo) {
		this.port = _port;
		this.echo = echo;
	}

	private class DummyServerThread implements Runnable {

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
					BufferedReader in = new BufferedReader(
							new InputStreamReader(socket.getInputStream()));
					BufferedWriter out = new BufferedWriter(
							new OutputStreamWriter(socket.getOutputStream()));
					String input;

					while ((input = in.readLine()) != null) {
						input.trim();
						if (input.toLowerCase().equals("quit")) {
							System.out
									.println("Closing socket on quit command");
							in.close();
							out.close();
							socket.close();
						} else if (input.toLowerCase().equals("tag")) {
							synchronized (tags) {
								for (EnhancedTagMessage tag : tags) {
									out.write(ByteAndHexConvertingUtility
											.toHexString(tag.getId()).replace(
													" ", "")
											+ "|");
									out.write(Long.toString(System
											.currentTimeMillis())
											+ "|");
									out.write(Integer.toString(tag
											.getAntennaId())
											+ "|");
									out.write(Float.toString(tag.getVelocity())
											+ "|");
									out.write(Float.toString(tag
											.getSignalStrength()));
									out.write("\n");
								}
								out.write("\n");
							}
						} else {
							out.write("405: Bad Command.\n\n");
						}
						out.flush();
					}
					logger.debug("Connection closed");
				} catch (IOException e) {
					logger.debug("Connection lost");
				}
			}

		}

	}
}
