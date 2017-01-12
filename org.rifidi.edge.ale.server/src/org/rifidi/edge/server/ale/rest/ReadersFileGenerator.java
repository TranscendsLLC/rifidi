package org.rifidi.edge.server.ale.rest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.rifidi.edge.epcglobal.alelr.AddReaders;
import org.rifidi.edge.utils.SerializerUtil;

public class ReadersFileGenerator {

	// hash-map for JAXB context.
	private static final Map<String, JAXBContext> contexts = new ConcurrentHashMap<String, JAXBContext>();

	// logger
	private static final Logger log = Logger.getLogger(SerializerUtil.class);
	
	public static void main(String[] args)
			throws Exception {
		// TODO Auto-generated method stub
		
//		FileOutputStream fileOutputStream = new FileOutputStream("D:\\rifidi_workspace\\ale.server2\\readers.xml");
		
		AddReaders addReaders = createDummyAddReaders();
		  SerializerUtil.serializeAddReaders(addReaders, "D:\\rifidi_workspace\\ale.server2\\readers.xml");
		
		
//		String[] readers = new String[]{"LLRP_3", "LLRP_4", "LLRP_5"};
//		readersTest.setReaders(readers);
		
//		ReadersFileGenerator.marshall("org.rifidi.edge.server.ale.rest", readersTest, fileOutputStream, true);

	}
	

//	private static void marshall(String jaxbContext, Object o, Object of, boolean pretty) throws Exception {
//		try {
//			Marshaller marshaller = getMarshaller(ReadersTest.class);
//			if (of instanceof Writer) {
//				marshaller.marshal(o, (Writer) of);
//			} else {
//				OutputStream fof = null;
//				if (of instanceof String) {
//					fof = new FileOutputStream((String) of);
//				} else if (of instanceof OutputStream) {
//					fof = (OutputStream) of;
//				} else {
//					throw new Exception("Wrong writer provided.");
//				}
//				marshaller.marshal(o, fof);
//			}
//
//		} catch (Exception e) {
//			log.error(String.format("Caught exception during marshalling:\n%s", e));
//			throw e;
//		}
//	}

	/**
	 * creates a marshaller on pooled JAXBContext instances.
	 * 
	 * @param jaxbContext
	 *            the context on which to create a marshaller.
	 * @param pretty
	 *            if formatted or not.
	 * @return the marshaller.
	 * @throws JAXBException
	 *             when unable to create the marshaller.
	 */
	private static Marshaller getMarshaller(Class _class) throws JAXBException {
		
		JAXBContext jaxbContext = JAXBContext.newInstance(_class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
		return jaxbMarshaller;
	}
	
	public static AddReaders createDummyAddReaders() {
		  AddReaders addReaders = new AddReaders();
		  AddReaders.Readers readers = new AddReaders.Readers();
		  readers.getReader().add("LLRP_2");
		  addReaders.setName("LLRP_2");
		  addReaders.setReaders(readers);
		  return addReaders;
		 }

	

}
