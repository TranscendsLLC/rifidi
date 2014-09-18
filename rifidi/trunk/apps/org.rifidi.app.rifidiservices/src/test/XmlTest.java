package test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.rifidi.app.rifidiservices.dto.StableSetMessageDto;

public class XmlTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		XmlTest xmlTest = new XmlTest();
		xmlTest.createXml();

	}
	
	public void createXml(){
		
		StableSetMessageDto stMes = new StableSetMessageDto();
		//stMes.setMsgNumber(1);
		//stMes.setStationId("LLRP_1");
		stMes.setTimeStamp(1234567l);
		
		//TagMessageDto dto = new TagMessageDto();
		//dto.setTag("3044BEDD3B30A64BE881D03C");
		//dto.setTestVal("val1");
		stMes.addTag("3044BEDD3B30A64BE881D03C");
		
		//dto = new TagMessageDto();
		//dto.setTag("308493ABED2FF7107F3FDBE0");
		//dto.setTestVal("val2");
		stMes.addTag("308493ABED2FF7107F3FDBE0");
		
		//dto = new TagMessageDto();
		//dto.setTag("3068B7FB1D5E6FF1E1955AAD");
		//dto.setTestVal("val3");
		stMes.addTag("3068B7FB1D5E6FF1E1955AAD");
		
		printXml(stMes);
		
		
	}
	
	private void printXml(StableSetMessageDto stableSetMessageDto) {

		try {

			JAXBContext jaxbContext = JAXBContext
					.newInstance(StableSetMessageDto.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);

			// Create xml String and send to server using mqttClient
			Writer writer = new StringWriter();
			jaxbMarshaller.marshal(stableSetMessageDto, writer);
			String content = writer.toString();
			writer.close();

			System.out.println("XML:\n" + content);

		} catch (JAXBException jEx) {

			// TODO log
			throw new RuntimeException(jEx);

		} catch (IOException ioEx) {

			// TODO log
			throw new RuntimeException(ioEx);

		}

	}

}
