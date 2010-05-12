package org.rifidi.edge.readerplugin.serialsensor;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		System.out.println("LISTING THE PORTS: ");
		listPorts();
		System.out.println("DONE LISTING THE PORTS");
		System.out.flush();
		(new SerialTester()).start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	}

	static void listPorts() {
		java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier
				.getPortIdentifiers();
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier portIdentifier = portEnum.nextElement();
			System.out.println(portIdentifier.getName() + " - "
					+ getPortTypeName(portIdentifier.getPortType()));
		}
	}

	static String getPortTypeName(int portType) {
		switch (portType) {
		case CommPortIdentifier.PORT_I2C:
			return "I2C";
		case CommPortIdentifier.PORT_PARALLEL:
			return "Parallel";
		case CommPortIdentifier.PORT_RAW:
			return "Raw";
		case CommPortIdentifier.PORT_RS485:
			return "RS485";
		case CommPortIdentifier.PORT_SERIAL:
			return "Serial";
		default:
			return "unknown type";
		}
	}

	public class SerialTester {
		public SerialTester() {
			super();
		}

		void connect(String portName) throws Exception {
			CommPortIdentifier portIdentifier = CommPortIdentifier
					.getPortIdentifier(portName);
			if (portIdentifier.isCurrentlyOwned()) {
				System.out.println("Error: Port is currently in use");
			} else {
				CommPort commPort = portIdentifier.open(this.getClass()
						.getName(), 2000);

				if (commPort instanceof SerialPort) {
					SerialPort serialPort = (SerialPort) commPort;
					serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
							SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

					InputStream in = serialPort.getInputStream();
					OutputStream out = serialPort.getOutputStream();

					(new Thread(new SerialReader(in))).start();
					(new Thread(new SerialWriter(out))).start();

				} else {
					System.out
							.println("Error: Only serial ports are handled by this example.");
				}
			}
		}

		/** */
		public class SerialReader implements Runnable {
			InputStream in;

			public SerialReader(InputStream in) {
				this.in = in;
			}

			public void run() {
				byte[] buffer = new byte[1024];
				int len = -1;
				int data;
				try {
					while ((data = in.read()) > -1) {
						System.out.print(data + " ");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		/** */
		public class SerialWriter implements Runnable {
			OutputStream out;

			public SerialWriter(OutputStream out) {
				this.out = out;
			}

			public void run() {
				try {
					int c = 0;
					while ((c = System.in.read()) > -1) {
						this.out.write(c);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		public void start() {
			try {
				StringBuilder sb = new StringBuilder("/dev/ttyUSB1");
				(new SerialTester()).connect(sb.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}
