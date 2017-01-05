package test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.rifidi.app.rifidimqtt.dto.TagMessageDto;

public class XmlTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		XmlTest xmlTest = new XmlTest();
		xmlTest.createXml();

	}

	public void createXml() {

		TagMessageDto dto = new TagMessageDto();
		dto.setTag("3044BEDD3B30A64BE881D03C");

		printXml(dto);

	}

	private void printXml(Object msg) {

		try {

			JAXBContext jaxbContext = JAXBContext.newInstance(msg.getClass());
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);

			// Create xml String and send to server using mqttClient
			Writer writer = new StringWriter();
			jaxbMarshaller.marshal(msg, writer);
			String content = writer.toString();
			writer.close();

			System.out.println("XML:\n" + content);

		} catch (JAXBException jEx) {

			throw new RuntimeException(jEx);

		} catch (IOException ioEx) {

			throw new RuntimeException(ioEx);

		}

	}

}
