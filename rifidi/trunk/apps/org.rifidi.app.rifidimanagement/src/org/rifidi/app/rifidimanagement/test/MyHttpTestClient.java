package org.rifidi.app.rifidimanagement.test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.JAXB;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class MyHttpTestClient {

	private String USER_AGENT = "Mozilla/5.0";

	private String strProtocol = "http";
	private String strHost = "localhost";
	private String strPort = "8111";
	
	private static enum RequestMethodEnum {POST, GET};

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		MyHttpTestClient myHttpTestClient = new MyHttpTestClient();
		
		String strCommand = "startsession/Front_Door_1/1";

		// HTTP GET request
		myHttpTestClient.process(RequestMethodEnum.GET, strCommand);
		
		// HTTP POST request
		//testClient.process(RequestMethodEnum.POST, strCommand);

	}

	public void process(RequestMethodEnum requestMethodEnum, String strCommand) 
			throws MalformedURLException, SAXException,	IOException {

		String strUrl = strProtocol + "://" + strHost + ":" + strPort + "/"
				+ strCommand;
		
		if (requestMethodEnum.equals(RequestMethodEnum.GET)){
		
			sendGet(strUrl);
			
		} else if (requestMethodEnum.equals(RequestMethodEnum.POST)) {
		
			sendPost(strUrl, null);
		}

		// the SAX way:
		// XMLReader myReader = XMLReaderFactory.createXMLReader();
		// myReader.setContentHandler(handler);
		// myReader.parse(new InputSource(url.openStream()));

		// test post and get

		// unmarshal xml url
		// RestResponseMessageDTO responseDTO = JAXB.unmarshal(url,
		// RestResponseMessageDTO.class);

		// System.out.println("responseDTO.message: " +
		// responseDTO.getMessage());

	}

	// HTTP GET request
	private void sendGet(String url)
			throws MalformedURLException, IOException {

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		// add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
		
		if (responseCode == 200){

			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
	
			// print result
			System.out.println(response.toString());
			
		} else {
			
			System.out.println("response not ok");
			
		}

	}

	// HTTP POST request
	private void sendPost(String url, String urlParameters)
			throws MalformedURLException, IOException{

		// String url = "https://selfsolve.apple.com/wcResults.do";
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

		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		System.out.println(response.toString());

	}

}
