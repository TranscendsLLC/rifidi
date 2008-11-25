/*
 *  RemoteReaderID.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.client.connections.remotereader;

/**
 * 
 * This Object is used as a qualified ID for the remote sessions.
 * 
 * @author Kyle Neumeier
 * 
 */
public class RemoteReaderID {

	/**
	 * The ID of the server this session is on
	 */
	private int serverID;

	/**
	 * The ID of the remote session on the server
	 */
	private long remoteSessionID;

	/**
	 * Create a new RemoteReaderID
	 * 
	 * @param serverID
	 *            the ID of the Edge Server this remote reader is on
	 * @param remoteSessionID
	 *            The ID of the session on that server
	 */
	public RemoteReaderID(int serverID, long remoteSessionID) {
		this.serverID = serverID;
		this.remoteSessionID = remoteSessionID;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof RemoteReaderID) {
			RemoteReaderID that = (RemoteReaderID) object;
			if (this.serverID == that.serverID) {
				if (this.remoteSessionID == that.remoteSessionID) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	int getServerID() {
		return this.serverID;
	}

	public long getRemoteSessionID() {
		return this.remoteSessionID;
	}

	@Override
	public String toString() {
		return Integer.toString(serverID) + ":"
				+ Long.toString(remoteSessionID);
	}

	/**
	 * This method takes the input from the toString() method of the
	 * RemoteReaderID and creates a new RemoteReaderID object from it
	 * 
	 * @param idString
	 *            The output of the toString() method in this class
	 * @return A RemoteReaderID a RemoteReaderID that corresponds to the string
	 *         that was passed in. Returns null if there was an error
	 */
	public static RemoteReaderID createIDFromString(String idString) {
		String[] ids = idString.split(":");
		if (ids.length != 2) {
			throw new IllegalArgumentException("ID string: " + idString
					+ " is not a valid RemoteReaderID");
		}
		int serverID; 
		try{
			serverID = Integer.parseInt(ids[0]);
		}catch(NumberFormatException e){
			throw new IllegalArgumentException("ServerID is not valid: " + ids[0]);
		}
		
		long sessionID;
		try{
			sessionID = Long.parseLong(ids[1]);
		}catch(NumberFormatException e){
			throw new IllegalArgumentException("RemoteSessionID is not valid: " + ids[1]);
		}
		
		return new RemoteReaderID(serverID, sessionID);
	}

}
