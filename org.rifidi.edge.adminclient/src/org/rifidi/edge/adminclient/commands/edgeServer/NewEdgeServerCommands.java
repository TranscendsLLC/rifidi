package org.rifidi.edge.adminclient.commands.edgeServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collection;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.rifidi.edge.adminclient.annotations.Command;
import org.rifidi.edge.adminclient.commands.ICommand;
import org.rifidi.edge.adminclient.jms.JMSConsumerFactory;
import org.rifidi.edge.core.exceptions.RifidiWidgetAnnotationException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection;
import org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnectionRegistry;

public class NewEdgeServerCommands implements ICommand {

	private RemoteReaderConnectionRegistry remoteReaderConnectionRegistry;

	private JMSConsumerFactory jmsFactory;

	/**
	 * initialize a new JMS Factory
	 * 
	 * @throws JMSException
	 */
	public void initializeJMS() throws JMSException {
		jmsFactory = new JMSConsumerFactory("tcp://localhost:61616");
		jmsFactory.start();
	}

	/**
	 * Connect to the RMIServer using the default values. hostname: localhost
	 * port: 1099
	 * 
	 * @return status
	 */
	@Command(name = "connectDefault")
	public String connectDefault() {
		return this.connect("localhost", "1099");
	}

	/**
	 * Connect to the RMIServer
	 * 
	 * @param hostname
	 *            hostname of the RMIServer
	 * @param port
	 *            port of the RMIServer
	 * @return status
	 */
	@Command(name = "connect", arguments = { "hostname", "port" })
	public String connect(String hostname, String port) {
		Registry registry = null;
		try {
			registry = LocateRegistry.getRegistry(hostname, Integer
					.parseInt(port));
			remoteReaderConnectionRegistry = (RemoteReaderConnectionRegistry) registry
					.lookup(RemoteReaderConnectionRegistry.class.getName());

			// TODO: Uh, we should never just catch generic exceptions.
		} catch (Exception e) {
			return "Could not connect. " + e.getMessage();
		}

		return "Connection established to " + hostname + ":" + port;
	}

	@Command(name = "availableReaderTypes")
	public String getAvailableReaderTypes() {
		if (remoteReaderConnectionRegistry == null)
			return "No connection to the EdgeServer";

		StringBuffer retVal = new StringBuffer();
		try {
			for (String readerType : remoteReaderConnectionRegistry
					.getAvailableReaderPlugins()) {
				retVal.append(readerType + "\n");
			}
			return retVal.toString();
		} catch (RemoteException e) {
			return "Error while getting available reader types. "
					+ e.getMessage();
		}
	}

	@Command(name = "activeConnections")
	public String getActiveConnections() {
		if (remoteReaderConnectionRegistry == null)
			return "No connection to the EdgeServer";

		StringBuffer retVal = new StringBuffer();
		try {
			retVal.append("Size of the remoteRegistry "
					+ remoteReaderConnectionRegistry.getAllReaderConnections()
							.size() + "\n");
			for (RemoteReaderConnection reader : remoteReaderConnectionRegistry
					.getAllReaderConnections()) {
				retVal
						.append(reader.getMessageQueueName()
								+ ": "
								+ reader.getReaderInfo().getClass()
										.getSimpleName() + " ID " + " Status: "
								+ reader.getReaderState() + "\n");
			}
		} catch (Exception e) {
			return "Error while recieving active connections. "
					+ e.getMessage();
		}
		if (retVal.length() <= 0)
			return "No active connections";
		else
			return retVal.toString();
	}

	@Command(name = "getReaderInfoAnnotation", arguments = { "readerInfoClassName" })
	public String getReaderInfoAnnotation(String readerInfoClassName) {
		if (remoteReaderConnectionRegistry == null)
			return "No connection to the EdgeServer";

		try {
			return remoteReaderConnectionRegistry
					.getReaderInfoAnnotation(readerInfoClassName);
		} catch (Exception e) {
			return "Error while recieving active connections. "
					+ e.getMessage();
		}
	}

	@Command(name = "getAdaptersInErrorState")
	public String getAdaptersInErrorState() {
		try {
			System.out.println("-- List of Readers in error state --");
			List<RemoteReaderConnection> connections = remoteReaderConnectionRegistry
					.getAllReaderConnections();
			for (RemoteReaderConnection connection : connections) {
				if (connection.getReaderState().equalsIgnoreCase("ERROR")) {
					//System.out.print(connection.getReaderInfo().getReaderType(
					// )
					// + " " + connection.getReaderInfo().getIpAddress()
					// + ":" + connection.getReaderInfo().getPort());

					// TODO Find a better, more clean way of finding the type of
					// error.
					/*
					 * the idea is to send a command out even though we know it
					 * will cause an error to get the cause of the error.
					 */
					try {
						connection
								.executeCommand("<TagStreaming></TagStreaming>");
					} catch (Exception e) {
						Exception e2 = e;
						while ((e2 != null) && (e2 instanceof RemoteException)) {
							e2 = (Exception) e2.getCause();
						}
						if (e2 != null) {
							// go past RifidiPreviousErrorException
							e2 = (Exception) e2.getCause();
							System.out.println(" " + e2.getMessage());
						}
					}
				}
			}
		} catch (RemoteException e) {
			return "Could not get adapters in error state.";
		}
		return "-- End of list --";
	}

	@Command(name = "createConnection")
	public String createReader() {
		if (remoteReaderConnectionRegistry == null)
			return "No connection to the EdgeServer";
		try {
			List<String> availableReaderTypes = null;
			try {
				availableReaderTypes = remoteReaderConnectionRegistry
						.getAvailableReaderPlugins();
			} catch (RemoteException e3) {
				return "ERROR. " + e3.getMessage();
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					System.in));
			int selection = 0;
			try {

				do {
					for (int i = 1; i <= availableReaderTypes.size(); i++) {
						System.out.println(i + " : "
								+ availableReaderTypes.get(i - 1));
					}
					System.out
							.print("Please choose the reader "
									+ "type you want to create? (0 to create no reader): ");
					try {
						selection = Integer.parseInt(reader.readLine());
					} catch (NumberFormatException e) {
						System.out
								.println("Selection must be a number between 0 and "
										+ availableReaderTypes.size());
						continue;
					}
					System.out.println("You selected : " + selection);

					if (selection == 0)
						return "No reader created.";
				} while (!((selection > 0) && (selection <= availableReaderTypes
						.size())));

			} catch (RemoteException e1) {
				e1.printStackTrace();
				return "Error while getting available ReaderPlugins";
			} catch (IOException e) {
				e.printStackTrace();
				return "Error while reading in the reader type to create";
			}

			ReaderInfo readerInfo = null;

			try {
				Class<?> readerInfoClazz = Class.forName(availableReaderTypes
						.get(selection - 1));
				readerInfo = (ReaderInfo) readerInfoClazz.getConstructor(
					new Class[0]).newInstance(new Object[0]);
				// System.out.println(readerInfo.getReaderType());
				for (Method m : readerInfoClazz.getMethods()) {
					if (m.getName().startsWith("set")) {
						System.out.print(m.getName().substring(3) + ": ");
						for (Class<?> c : m.getParameterTypes()) {
							// System.out.println(c.getName());
							try {
								String input = reader.readLine();
								if (c.equals(Integer.TYPE)
										|| c.equals(Integer.class)) {
									int i = Integer.parseInt(input);
									m.invoke(readerInfo, i);
								}
								if (c.equals(Long.TYPE) || c.equals(Long.class)) {
									long l = Long.parseLong(input);
									m.invoke(readerInfo, l);
								}
								if (c.equals(Byte.TYPE) || c.equals(Byte.class)) {
									byte b = Byte.parseByte(input);
									m.invoke(readerInfo, b);
								}
								if (c.equals(Short.TYPE)
										|| c.equals(Short.class)) {
									short s = Short.parseShort(input);
									m.invoke(readerInfo, s);
								}
								if (c.equals(Double.TYPE)
										|| c.equals(Double.class)) {
									double d = Double.parseDouble(input);
									m.invoke(readerInfo, d);
								}
								if (c.equals(Float.TYPE)
										|| c.equals(Float.class)) {
									float d = Float.parseFloat(input);
									m.invoke(readerInfo, d);
								}
								if (c.equals(String.class)) {
									input.trim();
									m.invoke(readerInfo, input);
								}
							} catch (IOException e) {
								e.printStackTrace();
								return "ERROR";
							} catch (NumberFormatException e) {
								e.printStackTrace();
								return "ERROR";
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
								return "ERROR";
							} catch (IllegalAccessException e) {
								e.printStackTrace();
								return "ERROR";
							} catch (InvocationTargetException e) {
								e.printStackTrace();
								return "ERROR";
							}
						}
					}
				}

			} catch (SecurityException e1) {
				e1.printStackTrace();
				return "ERROR";
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
				return "ERROR";
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return "ERROR";
			} catch (InstantiationException e) {
				e.printStackTrace();
				return "ERROR";
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return "ERROR";
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				return "ERROR";
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				return "ERROR";
			}

			try {

				JAXBContext context = JAXBContext.newInstance(readerInfo
						.getClass());
				Marshaller m = context.createMarshaller();
				Writer writer = new StringWriter();
				m.marshal(readerInfo, writer);

				remoteReaderConnectionRegistry.createReaderConnection(writer
						.toString());
			} catch (RemoteException e) {
				e.printStackTrace();
				return "ERROR";

			}

		} catch (Exception e) {
			e.printStackTrace();
			return "ERROR";

		}
		return "Reader successfully created";
	}

	@Command(name = "deleteConnection", arguments = { "ID" })
	public String deleteConnection(String id) {
		if (remoteReaderConnectionRegistry == null)
			return "No connection to the EdgeServer";
		try {
			for (RemoteReaderConnection readerConnection : remoteReaderConnectionRegistry
					.getAllReaderConnections()) {
				try {
					if (readerConnection.getMessageQueueName().equals(id)) {
						remoteReaderConnectionRegistry
								.deleteReaderConnection(readerConnection);
						return "ReaderConnection deleted";
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return "RemoteReaderConnection not found";
	}

	@Command(name = "execute", arguments = { "connection id", "commandName" })
	public String executeCommand(String id, String commandName) {
		RemoteReaderConnection remoteReaderConnection = null;
		try {
			for (RemoteReaderConnection readerConnection : remoteReaderConnectionRegistry
					.getAllReaderConnections()) {
				if (readerConnection.getMessageQueueName().equals(id)) {
					remoteReaderConnection = readerConnection;
					break;
				}
			}
		} catch (RemoteException e) {
			return "Command not found. " + e.getMessage();
		}
		if (remoteReaderConnection == null) {
			return "ERROR. Connection not found";
		}
		try {
			long commandID = remoteReaderConnection.executeCommand(commandName);
			return "Command started: " + commandID;
		} catch (RemoteException e) {
			return "Command not found. " + e.getMessage();
		} catch (Exception e) {
			return "General Error. " + e.getMessage();
		}

	}

	@Command(name = "stop", arguments = { "connection id" })
	public String stopExecution(String id) {
		RemoteReaderConnection remoteReaderConnection = null;
		try {
			for (RemoteReaderConnection readerConnection : remoteReaderConnectionRegistry
					.getAllReaderConnections()) {
				if (readerConnection.getMessageQueueName().equals(id)) {
					remoteReaderConnection = readerConnection;
					break;
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		if (remoteReaderConnection == null) {
			return "ERROR. Connection not found";
		}
		try {
			remoteReaderConnection.stopCurCommand(false);
			return "Command stopped";
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "ERROR";
	}

	@Command(name = "getTagReads", arguments = { "id", "count" })
	public String getTagReads(String id, String count) {

		RemoteReaderConnection remoteReaderConnection = null;
		try {
			for (RemoteReaderConnection readerConnection : remoteReaderConnectionRegistry
					.getAllReaderConnections()) {
				if (readerConnection.getMessageQueueName().equals(id)) {
					remoteReaderConnection = readerConnection;
					break;
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		if (remoteReaderConnection == null) {
			return "RemoteReaderConnection not found";
		}

		try {
			remoteReaderConnection
					.executeCommand("<TagStreaming></TagStreaming>");
		} catch (Exception e2) {
			e2.printStackTrace();
		}

		if (jmsFactory == null) {
			try {
				initializeJMS();
			} catch (JMSException e1) {
				e1.printStackTrace();
				return "Error while connecting to JMS";
			}
		}

		MessageConsumer consumer = null;
		try {
			consumer = jmsFactory.createConsumer(remoteReaderConnection);
			int intCount = Integer.parseInt(count);
			for (int i = 0; i < intCount; i++) {
				System.out.println("wait until receive TagRead message:");
				TextMessage message = (TextMessage) consumer.receive();
				System.out.println(message.getText());
			}
		} catch (RemoteException e2) {
			e2.printStackTrace();
		} catch (JMSException e2) {
			e2.printStackTrace();
		}

		try {
			remoteReaderConnection.stopCurCommand(false);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return "";
	}

	@Command(name = "availableCommands", arguments = { "ConnectionsID" })
	public String getAvailableCommands(String ID) {
		if (remoteReaderConnectionRegistry == null) {
			return "Error. No Connection";
		}

		RemoteReaderConnection readerConnection = null;
		try {
			for (RemoteReaderConnection connection : remoteReaderConnectionRegistry
					.getAllReaderConnections()) {
				if (connection.getMessageQueueName().equals(ID)) {
					readerConnection = connection;
				}
			}

			if (readerConnection == null) {
				return "ERROR." + " RemoteReaderConnection not found";
			}

			Collection<String> groups = readerConnection.getAvailableCommands();
			System.out.println(groups.size() + " Commands found ");
			StringBuffer buffer = new StringBuffer();
			for (String group : groups) {
				buffer.append(group + "\n");
			}
			return buffer.toString();
		} catch (RemoteException e) {
			e.printStackTrace();
			return "ERROR. " + e.getMessage();
		}
	}

	@Command(name = "availableCommandGroups", arguments = { "ConnectionID" })
	public String getCommandGroups(String ID) {
		if (remoteReaderConnectionRegistry == null) {
			return "Error. No Connection";
		}

		RemoteReaderConnection readerConnection = null;
		try {
			for (RemoteReaderConnection connection : remoteReaderConnectionRegistry
					.getAllReaderConnections()) {
				if (connection.getMessageQueueName().equals(ID)) {
					readerConnection = connection;
				}
			}

			if (readerConnection == null) {
				return "ERROR." + " RemoteReaderConnection not found";
			}

			Collection<String> groups = readerConnection
					.getAvailableCommandGroups();
			System.out.println("# CommandGroups found " + groups.size());
			StringBuffer buffer = new StringBuffer();
			for (String group : groups) {
				buffer.append(group + "\n");
			}
			return buffer.toString();
		} catch (RemoteException e) {
			e.printStackTrace();
			return "ERROR. " + e.getMessage();
		}
	}

	@Command(name = "availableCommandsForGroup", arguments = { "ConnectionID",
			"Groupname" })
	public String getCommandsForGroup(String ID, String groupname) {
		if (remoteReaderConnectionRegistry == null) {
			return "Error. No Connection";
		}

		RemoteReaderConnection readerConnection = null;
		try {
			for (RemoteReaderConnection connection : remoteReaderConnectionRegistry
					.getAllReaderConnections()) {
				if (connection.getMessageQueueName().equals(ID)) {
					readerConnection = connection;
				}
			}

			if (readerConnection == null) {
				return "ERROR." + " RemoteReaderConnection not found";
			}

			Collection<String> groups = readerConnection
					.getAvailableCommands(groupname);
			System.out.println("# Commands in Group found " + groups.size());
			StringBuffer buffer = new StringBuffer();
			for (String group : groups) {
				buffer.append(group + "\n");
			}
			return buffer.toString();
		} catch (RemoteException e) {
			e.printStackTrace();
			return "ERROR. " + e.getMessage();
		}
	}

	@Command(name = "propertyAnnotations", arguments = { "ConnectionID",
			"Groupname" })
	public String getPropertyAnnotations(String ID, String groupname) {
		if (remoteReaderConnectionRegistry == null) {
			return "Error. No Connection";
		}

		RemoteReaderConnection readerConnection = null;
		try {
			for (RemoteReaderConnection connection : remoteReaderConnectionRegistry
					.getAllReaderConnections()) {
				if (connection.getMessageQueueName().equals(ID)) {
					readerConnection = connection;
				}
			}

			if (readerConnection == null) {
				return "ERROR." + " RemoteReaderConnection not found";
			}
			
			try {
				System.out.println("about to get annotations");
				String s = readerConnection.getPropertyAnnotations(groupname);
				if (s==null || s.equals("")){
					return "no properties for group " + groupname;
				}else return s;
			} catch (RifidiWidgetAnnotationException e) {
				e.printStackTrace();
				return "ERROR. " + e.getMessage();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
			return "ERROR. " + e.getMessage();
		}
	}

	@Command(name = "getCommandStatus", arguments = { "ConnectionID",
			"CommandID" })
	public String getCommandStatus(String connectionID, String commandID) {
		if (remoteReaderConnectionRegistry == null) {
			return "Error. No Connection";
		}

		RemoteReaderConnection readerConnection = null;
		try {
			for (RemoteReaderConnection connection : remoteReaderConnectionRegistry
					.getAllReaderConnections()) {
				if (connection.getMessageQueueName().equals(connectionID)) {
					readerConnection = connection;
				}
			}

			if (readerConnection == null) {
				return "ERROR." + " RemoteReaderConnection not found";
			}
			return readerConnection.commandStatus(Long.parseLong(commandID));
		} catch (Exception e) {
			return "ERROR" + e.getMessage();
		}
	}

	@Command(name = "resetConnection", arguments = { "ConnectionID" })
	public String resetCommection(String connectionID) {
		if (remoteReaderConnectionRegistry == null) {
			return "Error. No Connection";
		}
		RemoteReaderConnection readerConnection = null;
		try {
			for (RemoteReaderConnection connection : remoteReaderConnectionRegistry
					.getAllReaderConnections()) {
				if (connection.getMessageQueueName().equals(connectionID)) {
					readerConnection = connection;
				}
			}

			if (readerConnection == null) {
				return "ERROR." + " RemoteReaderConnection not found";
			}
			readerConnection.resetReaderConnection();
			return "Connection reset successfully";
		} catch (Exception e) {
			return "ERROR" + e.getMessage();
		}
	}

}
