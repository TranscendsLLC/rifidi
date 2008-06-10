package org.rifidi.edge.rmi.service;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;

public interface RMIServerService {

	public void start() throws RemoteException, AlreadyBoundException;

	public void stop();

}
