package org.rifidi.edge.regression.createSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.jdom.JDOMException;
import org.junit.Test;


public class testAnnotationWrapper {
	
	@Test
	public void test(){
		InputStream annotationStream = getClass().getResourceAsStream("exampleAnnotation.xml");
		InputStream RIStream = getClass().getResourceAsStream("exampleReaderInfo.xml");
		String annotationString = convertStreamToString(annotationStream);
		String RIString = convertStreamToString(RIStream);
		try {
			AnnotationWrapper wrapper = new AnnotationWrapper(annotationString);
			String RIGenerated = wrapper.buildDefaultReaderInfo();
			Assert.assertEquals(RIString, RIGenerated);
		} catch (JDOMException e) {
			Assert.fail();
		} catch (IOException e) {
			Assert.fail();
		}
		
	}
	
    public String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
 
        return sb.toString();
    }


}
