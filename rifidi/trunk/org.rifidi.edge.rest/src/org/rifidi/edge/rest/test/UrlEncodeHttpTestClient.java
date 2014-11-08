package org.rifidi.edge.rest.test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.imageio.IIOException;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.JAXB;
import javax.xml.bind.helpers.ParseConversionEventImpl;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class UrlEncodeHttpTestClient {

	private String USER_AGENT = "Mozilla/5.0";

	private String strProtocol = "http";
	private String strHost = "localhost";
	private String strPort = "8111";

	private static enum RequestMethodEnum {
		POST, GET
	};

	private final String SUCCESS_MESSAGE = "Success";

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		UrlEncodeHttpTestClient myHttpTestClient = new UrlEncodeHttpTestClient();

		String strCommand = "setGroupProperties/7/";
		String prop = "mqttBroker=tcp://test.mqtt-dashboard.com:1883";
		String encodedProp = URLEncoder.encode(prop, "UTF-8");
		System.out.println("encodeProp: " + encodedProp);
		String url = strCommand + encodedProp;


		// You can submit a GET or a POST request

		// HTTP GET request
		myHttpTestClient.processCommand(RequestMethodEnum.GET, url);

		// HTTP POST request
		// myHttpTestClient.processCommand(RequestMethodEnum.POST, url);

	}

	public void processCommand(RequestMethodEnum requestMethodEnum,
			String strCommand) throws MalformedURLException, SAXException,
			IOException, ParserConfigurationException {

		String strUrl = strProtocol + "://" + strHost + ":" + strPort + "/"
				+ strCommand;

		String xmlResponse = null;

		if (requestMethodEnum.equals(RequestMethodEnum.GET)) {

			xmlResponse = sendGet(strUrl);

		} else if (requestMethodEnum.equals(RequestMethodEnum.POST)) {

			xmlResponse = sendPost(strUrl, null);
		}

		processXml(xmlResponse);

	}

	// HTTP GET request
	private String sendGet(String url) throws MalformedURLException,
			IOException {

		String xmlResponse = null;

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		// add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		if (responseCode == 200) {

			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			xmlResponse = response.toString();

			// print result
			System.out.println(response.toString());

		} else {

			System.out.println("response not ok");

		}

		return xmlResponse;

	}

	// HTTP POST request
	private String sendPost(String url, String urlParameters)
			throws MalformedURLException, IOException,
			ParserConfigurationException, SAXException {

		String xmlResponse = null;

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// add request header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());

		if (urlParameters != null && !urlParameters.isEmpty()) {
			wr.writeBytes(urlParameters);
		}

		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();

		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		if (responseCode == 200) {

			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			xmlResponse = response.toString();

			// print result
			System.out.println(response.toString());
			
		} else {

			System.out.println("response not ok");

		}

		return xmlResponse;

	}

	public void processXml(String xml) throws SAXException,
			ParserConfigurationException, IOException {

		// obtain and configure a SAX based parser
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

		// obtain object for SAX parser
		SAXParser saxParser = saxParserFactory.newSAXParser();

		// create the reader (scanner)
		XMLReader xmlreader = saxParser.getXMLReader();

		UrlEncodeResponseHandler myResponseHandler = new UrlEncodeResponseHandler();

		// assign our handler
		xmlreader.setContentHandler(myResponseHandler);

		// perform the synchronous parse
		xmlreader.parse(new InputSource(new StringReader(xml)));

		System.out.println("myResponseHandler.getMessageValue(): "
				+ myResponseHandler.getMessageValue());

		if (myResponseHandler.getMessageValue().equals(SUCCESS_MESSAGE)) {

			// Do whatever logic with success outcome
		} else {

			// Not a success outcome
		}

	}

}
