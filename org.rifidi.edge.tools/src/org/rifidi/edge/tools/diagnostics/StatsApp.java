/**
 * 
 */
package org.rifidi.edge.tools.diagnostics;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.rifidi.edge.api.AbstractRifidiApp;
import org.rifidi.edge.api.ReaderDTO;
import org.rifidi.edge.api.RifidiApp;
import org.rifidi.edge.api.SensorManagerService;
import org.rifidi.edge.api.service.appmanager.AppManager;

/**
 * 
 * 
 * @author Matthew Dean - matt@transcends.co
 */
public class StatsApp extends AbstractRifidiApp {

	/** The sensor manager service for sensor commands */
	public SensorManagerService sensorService;

	/** App manager */
	public AppManager appManager;

	public StatsApp(String group, String name) {
		super(group, name);
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.api.AbstractRifidiApp#lazyStart()
	 */
	@Override
	public boolean lazyStart() {
		String lazyStart=getProperty(LAZY_START, "false");
		return Boolean.parseBoolean(lazyStart);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.AbstractRifidiApp#initialize()
	 */
	@Override
	public void initialize() {
	}

	@Override
	public void _start() {
		super._start();
		Boolean disable = false;
		String license = null;
		Boolean sendOverride = false;
		try {
			disable = Boolean.parseBoolean(System.getProperty("org.rifidi.disablestats", null));
		} catch (Exception e) {}
		try {
			license = System.getProperty("org.rifidi.license");
		} catch (Exception e) {}
		try {
			sendOverride = Boolean.parseBoolean(System.getProperty("org.rifidi.sendoverride", "false"));
		} catch (Exception e) {}

		String home = System.getProperty("org.rifidi.home");
		if (!disable && home.contains("Rifidi-SDK/RifidiHome")) {
			Thread thread = new Thread(new StatsThread(sensorService, appManager, license, sendOverride));
			thread.start();
		}
	}

	public void setSensorManagerService(SensorManagerService sensorService) {
		this.sensorService = sensorService;
	}

	public void setAppManager(AppManager appManager) {
		this.appManager = appManager;
	}

	/**
	 * Long running thread
	 */
	private class StatsThread implements Runnable {

		private AppManager appManager;
		private SensorManagerService sensorService;
		private String license;
		private Boolean sendOverride;

		public StatsThread(SensorManagerService sensorService, AppManager appManager, String license, Boolean sendOverride) {
			this.appManager = appManager;
			this.sensorService = sensorService;
			this.license = license;
			this.sendOverride = sendOverride;
		}

		@Override
		public void run() {
			try {
				InetAddress localip = getFirstNonLoopbackAddress(true, false);
				NetworkInterface network = NetworkInterface.getByInetAddress(localip);
				byte[] mac = network.getHardwareAddress();
				StringBuilder macsb = new StringBuilder();
				for (int i = 0; i < mac.length; i++) {
					macsb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));
				}
				Boolean validlicense = false;
				StringBuilder licenseurl = new StringBuilder("http://transcends.co/www/validatelicense.php?token=Rifidi@2006&license=");
				if (license != null && !license.equals("")) {
					licenseurl.append(license);
					licenseurl.append("&uid=");
					licenseurl.append(macsb.toString());
					licenseurl.append("&product=Edge");

					URL postlicenseurl = new URL(licenseurl.toString());
					URLConnection urlc = postlicenseurl.openConnection();

					// get result
					BufferedReader licensebr = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
					String lr = licensebr.readLine();
					try {
						validlicense = Boolean.parseBoolean(lr.split(":")[0]);
					} catch (Exception e) {
					} finally {
						licensebr.close();
					}
				}

				
				if (!validlicense || sendOverride) {
					String home = System.getProperty("org.rifidi.home");
					String emailfile = home + File.separator + "configuration" + File.separator + "emailcontact";
					String xmlfile = home + File.separator + "config" + File.separator + "rifidi.xml";
					String versionfile = home + File.separator + "version.txt";
					try {
						Thread.sleep(600000);
					} catch (InterruptedException e) {
					}
					Integer numapps = appManager.getApps().size();
					StringBuilder appstr = new StringBuilder();
					boolean first = true;
					for (RifidiApp app : appManager.getApps().values()) {
						if (first) {
							first = false;
						} else {
							appstr.append("|");
						}
						appstr.append(app.getName());
						appstr.append(":");
						appstr.append(app.getState());
					}
					Integer numreaders = sensorService.getReaders().size();
					Map<String, Integer> readerTypeToCount = new HashMap<String, Integer>();
					for (ReaderDTO reader : sensorService.getReaders()) {
						if (readerTypeToCount.containsKey(reader.getReaderFactoryID())) {
							readerTypeToCount.put(reader.getDisplayName(),
									readerTypeToCount.get(reader.getReaderFactoryID()) + 1);
						} else {
							readerTypeToCount.put(reader.getDisplayName(), 1);
						}
					}
					StringBuilder readerstr = new StringBuilder();
					first = true;
					for (String reader : readerTypeToCount.keySet()) {
						if (!first) {
							readerstr.append("|");
						}
						readerstr.append(reader);
						readerstr.append(":");
						readerstr.append(readerTypeToCount.get(reader));
					}
					String email = "";
					try {
						email = readFile(emailfile, StandardCharsets.UTF_8);
					} catch (Exception e) {
					}

					String xmlstr = "";
					try {
						xmlstr = readFile(xmlfile, StandardCharsets.UTF_8);
					} catch (Exception e) {
					}
					// encode xml
					xmlstr = URLEncoder.encode(xmlstr, StandardCharsets.UTF_8.name());
					String version = "";
					try {
						version = readFile(versionfile, StandardCharsets.UTF_8);
					} catch (Exception e) {
					}
					
					StringBuilder url = new StringBuilder("http://transcends.co/www/rifidistats.php?token=Rifidi@2006&local_ip=");
					url.append(localip.getHostAddress());
					url.append("&version=");
					url.append(version);
					url.append("&uid=");
					url.append(macsb.toString());
					url.append("&email=");
					url.append(email);
					url.append("&num_apps=");
					url.append(numapps);
					url.append("&apps=");
					url.append(appstr);
					url.append("&num_readers=");
					url.append(numreaders);
					url.append("&reader_type=");
					url.append(readerstr);
					url.append("&config=");
					url.append(xmlstr);

					URL posturl = new URL(url.toString());
					URLConnection urlc = posturl.openConnection();

					// get result
					BufferedReader br = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
					br.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	//clever method to get the first non-loopback inet address
	static InetAddress getFirstNonLoopbackAddress(boolean preferIpv4, boolean preferIPv6) throws SocketException {
	    Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
	    while (en.hasMoreElements()) {
	        NetworkInterface i = (NetworkInterface) en.nextElement();
	        for (Enumeration<InetAddress> en2 = i.getInetAddresses(); en2.hasMoreElements();) {
	            InetAddress addr = (InetAddress) en2.nextElement();
	            if (!addr.isLoopbackAddress()) {
	                if (addr instanceof Inet4Address) {
	                    if (preferIPv6) {
	                        continue;
	                    }
	                    return addr;
	                }
	                if (addr instanceof Inet6Address) {
	                    if (preferIpv4) {
	                        continue;
	                    }
	                    return addr;
	                }
	            }
	        }
	    }
	    return null;
	}

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

}
