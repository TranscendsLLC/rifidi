package org.rifidi.edge.adminclient.testreaderthread;

import java.rmi.RemoteException;
import java.util.List;

import org.rifidi.edge.adminclient.commands.edgeServer.EdgeServerCommands;
import org.rifidi.edge.rmi.ReaderConnection.RemoteReaderConnection;

/**
 * @author Jerry Maine - jerry@pramari.com
 * 
 */
public class ErrorTestThread extends AbstractThread {
	EdgeServerCommands c;

	public ErrorTestThread(String threadName, EdgeServerCommands c) {
		super(threadName);
		this.c = c;
	}

	@Override
	public void run() {

		while (running) {
			if (c.getRemoteReaderConnectionRegistry() != null) {
				List<RemoteReaderConnection> connections = null;
				try {
					connections = c.getRemoteReaderConnectionRegistry()
							.getAllReaderConnections();

					if (connections == null)
						continue;

					for (RemoteReaderConnection connection : connections) {
						if (connection.getReaderState().equalsIgnoreCase(
								"ERROR"));
//							System.out.println(connection.getReaderInfo()
//									.getReaderType()
//									+ "had ");
					}
				} catch (RemoteException e) {

					// e.printStackTrace();
					System.out.println("RMI connection Failed.");
					stop();
				}
			}
			try {
				synchronized (this) {
					wait(500);
				}
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	public void finalize() {
		stop();
	}
}
