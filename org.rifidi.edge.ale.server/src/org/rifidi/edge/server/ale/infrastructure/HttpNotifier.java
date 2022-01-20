package org.rifidi.edge.server.ale.infrastructure;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Date;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.rifidi.edge.epcglobal.ale.ECReports;
import org.rifidi.edge.epcglobal.ale.ImplementationExceptionResponse;
import org.rifidi.edge.epcglobal.ale.InvalidURIExceptionResponse;
import org.rifidi.edge.utils.RifidiHelper;

/**
 * HTTP POST Notifier
 * @author mikk.leini@krakul.eu
 */
public class HttpNotifier extends Notifier {

    /** logger */
    private static final Logger LOG = Logger.getLogger(HttpNotifier.class);

    private URI uri;
    //FIXME ALE take from jvm
    private String topic = "ale";

    //FIXME ALE take from jvm
    private final int qos = 2;

    private String notificationURI;

    private String clientId;

    @Override
    public void init(String uri, RifidiHelper rifidiHelper) throws InvalidURIExceptionResponse {
        // TODO Auto-generated method stub

        notificationURI = uri;
        try {
            this.uri = new URI(notificationURI);
        } catch (Exception e) {
            LOG.error("malformed URI");
            throw new InvalidURIExceptionResponse("malformed URI: ", e);
        }

        if (!("http".equalsIgnoreCase(this.uri.getScheme()))) {
            LOG.error("invalid scheme: " + this.uri.getScheme());
            throw new InvalidURIExceptionResponse("invalid scheme: " + this.uri.getScheme());
        }

        setRifidiHelper(rifidiHelper);

        clientId = (new Date().getTime() + getNotifierId()).substring(0, 23);
        System.out.println("clientId: " + clientId);
    }

    @Override
    public void notifySubscriber(ECReports reports) throws ImplementationExceptionResponse {
        // TODO Auto-generated method stub
        publishToHttp(getXml(reports));
    }

    @Override
    public void notifyExceptionToSubscriber(ImplementationExceptionResponse e) {
        // TODO Auto-generated method stub
        try {
            String strXml = getXml(e);
            System.out.println("HttpNotifier.notifyExceptionToSubscriber.strXml: " + strXml);
            publishToHttp(strXml);
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public String getURIfromFields() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Publish XML content to server
     * @note Code from: https://www.baeldung.com/httpclient-post-http-request
     * @param content XML string
     */
    public void publishToHttp(String content) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(uri);

        // Set headers
        post.setHeader("Accept", "application/xml");
        post.setHeader("Content-type", "application/xml");
        post.setHeader("User-Agent", clientId);

        // Set content
        try {
            StringEntity entity = new StringEntity(content);
            post.setEntity(entity);
        } catch (UnsupportedEncodingException e) {
            LOG.error("Failed to set HTTP POST (URL " + uri + ") entity: " + e.getMessage());
            return;
        }

        // Send request and try to get response
        CloseableHttpResponse response = null;
        try {
            response = client.execute(post);
        } catch (IOException e) {
            LOG.error("Failed to send HTTP POST (URL " + uri + ") request: " + e.getMessage());
            return;
        }

        int responseCode = response.getStatusLine().getStatusCode();
        if (responseCode != HttpStatus.SC_OK) {
            LOG.error("HTTP POST (URL " + uri + ") error code " + responseCode +
                    ", content: " + response.getEntity().toString());
            return;
        }

        // Close connection
        try {
            client.close();
        } catch (IOException e) {
            LOG.error("Failed to close HTTP client connection (URL " + uri + "): " + e.getMessage());
            return;
        }
    }
}
