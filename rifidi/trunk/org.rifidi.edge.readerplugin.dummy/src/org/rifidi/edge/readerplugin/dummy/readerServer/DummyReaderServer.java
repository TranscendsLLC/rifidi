package org.rifidi.edge.readerplugin.dummy.readerServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DummyReaderServer {

	Log logger = LogFactory.getLog(DummyReaderServer.class);
	private boolean run=false;
	private int port;
	private boolean echo;
	private Thread t;
	
	public static void main(String[] args) {
		if (args.length == 2) {
			DummyReaderServer drs = new DummyReaderServer(Integer.parseInt(args[0]), Boolean.parseBoolean(args[1]));
			drs.start();
		} else {
			DummyReaderServer drs = new DummyReaderServer(2000, true);
			drs.start();
		}
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

	public DummyReaderServer(int _port, boolean echo) {
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
					
					while ((input = in.readLine()) != null) {
						input.trim();
						if (input.toLowerCase().equals("quit")) {
							System.out.println("Closing socket on quit command");
							in.close();
							out.close();
							socket.close();
						}

						if (echo) {
							out.write(input + "\n");
							out.flush();
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
