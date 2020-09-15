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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
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
		Long startTime = System.currentTimeMillis();
		try {
			disable = Boolean.parseBoolean(this.getProperty("disablestats", "false"));
		} catch (Exception e) {}
		try {
			license = this.getProperty("license", "");
		} catch (Exception e) {}
		try {
			sendOverride = Boolean.parseBoolean(this.getProperty("sendoverride", "false"));
		} catch (Exception e) {}
		
		if (!disable) {
			StatsThread statsthread = new StatsThread(sensorService, appManager, license, sendOverride, startTime, "startupevent");
			StatsThread statsthread2 = new StatsThread(sensorService, appManager, license, sendOverride, startTime, "uptime");
			Timer timer = new Timer();
			//startupevent 10 minutes after startup
			timer.schedule(statsthread, 600000);
			//uptime every month
			//2592000000 milliseconds in 30 days
			timer.schedule(statsthread2, 2592000000L, 2592000000L);
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
	private class StatsThread extends TimerTask {

		private AppManager appManager;
		private SensorManagerService sensorService;
		private String license;
		private Boolean sendOverride;
		private Long startTime;
		private String eventType;

		public StatsThread(SensorManagerService sensorService, AppManager appManager, String license, Boolean sendOverride, Long startTime, String eventType) {
			this.appManager = appManager;
			this.sensorService = sensorService;
			this.license = license;
			this.sendOverride = sendOverride;
			this.startTime = startTime;
			this.eventType = eventType;
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
					Integer numapps = appManager.getApps().size();
					StringBuilder appbldr = new StringBuilder();
					boolean first = true;
					for (RifidiApp app : appManager.getApps().values()) {
						if (first) {
							first = false;
						} else {
							appbldr.append("|");
						}
						appbldr.append(app.getName());
						appbldr.append(":");
						appbldr.append(app.getState());
					}
					String appstr = appbldr.toString();					
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
					StringBuilder readerbldr = new StringBuilder();
					first = true;
					for (String reader : readerTypeToCount.keySet()) {
						if (!first) {
							readerbldr.append("|");
						}
						first = false;
						readerbldr.append(reader);
						readerbldr.append(":");
						readerbldr.append(readerTypeToCount.get(reader));
					}
					String readerstr = readerbldr.toString();
					String email = "";
					try {
						email = readFile(emailfile, StandardCharsets.UTF_8);
					} catch (Exception e) {}
					email=URLEncoder.encode(email, StandardCharsets.UTF_8.name());

					String xmlstr = "";
					try {
						xmlstr = readFile(xmlfile, StandardCharsets.UTF_8);
					} catch (Exception e) {}
					// encode xml
					xmlstr = URLEncoder.encode(xmlstr, StandardCharsets.UTF_8.name());
					String version = "";
					try {
						version = readFile(versionfile, StandardCharsets.UTF_8);
						version = version.split(" ")[1];
					} catch (Exception e) {}
					String os = "";
					try{
						os=System.getProperty("os.name");
					} catch(Exception e) {}
					
					List<NameValuePair> data = new ArrayList<NameValuePair>(Arrays.asList(
							new BasicNameValuePair("uptime", getUptime(startTime).toString()),
						    new BasicNameValuePair("token", "Rifidi@2006"),
						    new BasicNameValuePair("event", eventType),
						    new BasicNameValuePair("local_ip", localip.getHostAddress()),
						    new BasicNameValuePair("os", os),
						    new BasicNameValuePair("version", version.trim()),
						    new BasicNameValuePair("uid", macsb.toString()),
						    new BasicNameValuePair("email", email),
						    new BasicNameValuePair("num_apps", numapps.toString()),
						    new BasicNameValuePair("apps", appstr),
						    new BasicNameValuePair("num_readers", numreaders.toString()),
						    new BasicNameValuePair("reader_type", readerstr),
						    new BasicNameValuePair("config", xmlstr))
					);
					
					StringBuilder url = new StringBuilder("http://transcends.co/www/rifidistats.php");
					HttpClient client = HttpClients.createDefault();
					HttpPost post = new HttpPost(url.toString());
					post.setEntity(new UrlEncodedFormEntity(data));
					//HttpResponse response = 
					client.execute(post);
					//HttpEntity entity = response.getEntity();

					// get result
//					BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
//					String next;
//					while((next = br.readLine())!=null) {
//						System.out.println(next.trim());
//					}
//					br.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private Long getUptime(Long startTime) {
			return System.currentTimeMillis() - startTime;
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
