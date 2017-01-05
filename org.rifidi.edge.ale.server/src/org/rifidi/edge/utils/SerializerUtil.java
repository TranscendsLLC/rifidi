package org.rifidi.edge.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.rifidi.edge.epcglobal.ale.ECReports;
import org.rifidi.edge.epcglobal.ale.ECSpec;
import org.rifidi.edge.epcglobal.ale.ImplementationException;
import org.rifidi.edge.epcglobal.ale.ImplementationExceptionResponse;
import org.rifidi.edge.epcglobal.alelr.AddReaders;
import org.rifidi.edge.epcglobal.alelr.LRSpec;
import org.rifidi.edge.epcglobal.alelr.RemoveReaders;
import org.rifidi.edge.epcglobal.alelr.SetProperties;
import org.rifidi.edge.epcglobal.alelr.SetReaders;

import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.llrp.ltk.exceptions.InvalidLLRPMessageException;
import org.llrp.ltk.generated.messages.ADD_ACCESSSPEC;
import org.llrp.ltk.generated.messages.ADD_ROSPEC;



public class SerializerUtil {
	
	// object factory for ALE
	private static final org.rifidi.edge.epcglobal.ale.ObjectFactory objectFactoryALE = new org.rifidi.edge.epcglobal.ale.ObjectFactory();
	
	// object factory for ALELR
	private static final org.rifidi.edge.epcglobal.alelr.ObjectFactory objectFactoryALELR = new org.rifidi.edge.epcglobal.alelr.ObjectFactory();
	
	// hash-map for JAXB context.
	private static final Map<String, JAXBContext> contexts = new ConcurrentHashMap<String, JAXBContext> ();
	
	// logger
	private static final Logger log = Logger.getLogger(SerializerUtil.class);
	
	/**
	 * This method serializes an ec specification to an xml and writes it into a writer.
	 * 
	 * @param ecSpec to serialize
	 * @param writer containing the xml
	 * @throws Exception upon error.
	 */
	public static void serializeECSpec(ECSpec ecSpec, OutputStream writer) throws Exception {	
		serializeECSpec(ecSpec, writer, false);		
	}
	
	/**
	 * This method serializes an ec specification to a well formed xml and writes it into a writer.
	 * 
	 * @param ecSpec to serialize
	 * @param writer to write the well formed xml into
	 * @throws Exception upon error.
	 */
	public static void serializeECSpecPretty(ECSpec ecSpec, OutputStream writer) throws Exception {		
		serializeECSpec(ecSpec, writer, true);		
	}
	
	/**
	 * This method serializes ec reports to an xml and writes it into a writer.
	 *  
	 * @param ecReports to serialize
	 * @param writer to write the xml into
	 * @throws Exception upon error.
	 */
	public static void serializeECReports(ECReports ecReports, Writer writer) throws Exception {		
		serializeECReports(ecReports, writer, false);		
	}
	
	public static void serializeImplementationExceptionResponse(ImplementationExceptionResponse e, Writer writer) throws Exception {		
		serializeImplementationExceptionResponse(e, writer, false);		
	}
	
	/**
	 * This method serializes ec reports to a well formed xml and writes it into a writer.
	 *  
	 * @param ecReports to serialize
	 * @param writer to write the well formed xml into
	 * @throws Exception upon error.
	 */
	public static void serializeECReportsPretty(ECReports ecReports, Writer writer) throws Exception {		
		serializeECReports(ecReports, writer, true);		
	}
		
	/**
	 * This method serializes an LRSpec to an xml and writes it into a file.
	 * @param spec the LRSpec to be written into a file
	 * @param pathName the file where to store
	 * @param pretty flag whether well-formed xml or not
	 * @throws Exception upon error.
	 */
	public static void serializeLRSpec(LRSpec spec, String pathName, boolean pretty) throws Exception {
		marshall("org.rifidi.edge.epcglobal.alelr", objectFactoryALELR.createLRSpec(spec), pathName, pretty);
	}
	
	/**
	 * This method serializes an LRSpec to an xml and writes it into a file.
	 * @param spec the LRSpec to be written into a file
	 * @param pathName the file where to store
	 * @param pretty flag whether well-formed xml or not
	 * @throws Exception upon error.
	 */
	public static void serializeLRSpec(LRSpec spec, Writer writer) throws Exception {
		marshall("org.rifidi.edge.epcglobal.alelr", objectFactoryALELR.createLRSpec(spec), writer, true);
	}
	
	/**
	 * Serializes an SetProperties to xml and stores this xml into a file.
	 * @param props the SetProperties to be serialized.
	 * @param pathName the path to the file where to store the xml.
	 * @throws Exception upon error.
	 */
	public static void serializeSetProperties(SetProperties props, String pathName) throws Exception {
		marshall("org.rifidi.edge.epcglobal.alelr", objectFactoryALELR.createLRSpecProperties()
				.getProperty().addAll(props.getProperties().getProperty()), pathName, true);
	}
	
	/**
	 * Serializes a RemoveReaders to xml and stores this xml into a file.
	 * @param readers the RemoveReaders to be serialized.
	 * @param pathName the path to the file where to store the xml.
	 * @throws Exception upon error.
	 */
	public static void serializeRemoveReaders(RemoveReaders readers, String pathName) throws Exception {
		marshall("org.rifidi.edge.epcglobal.alelr", objectFactoryALELR.createLRSpecReaders()
				.getReader().removeAll(readers.getReaders().getReader()), pathName, true);
	}
	
	/**
	 * Serializes a SetReaders to xml and stores this xml into a file.
	 * @param readers the SetReaders to be serialized.
	 * @param pathName the path to the file where to store the xml.
	 * @throws Exception upon error.
	 */
	public static void serializeSetReaders(SetReaders readers, String pathName) throws Exception {
		objectFactoryALELR.createLRSpecReaders().getReader().clear();
		marshall("org.rifidi.edge.epcglobal.alelr", objectFactoryALELR.createLRSpecReaders().
				getReader().addAll(readers.getReaders().getReader()), pathName, true);
	}
	
	/**
	 * Serializes an AddReaders to xml and stores this xml into a file.
	 * @param readers the AddReaders to be serialized.
	 * @param pathName the path to the file where to store the xml.
	 * @throws Exception upon error.
	 */
	public static void serializeAddReaders(AddReaders readers, String pathName) throws Exception {
		marshall("org.rifidi.edge.epcglobal.alelr", objectFactoryALELR.createLRSpecReaders().
				getReader().addAll(readers.getReaders().getReader()), pathName, true);
	}
	
	
	//
	// private methods
	//
	
	/**
	 * This method serializes an ec specification to an xml and writes it into a writer.
	 * 
	 * @param ecSpec to serialize
	 * @param writer to write the xml into
	 * @param pretty indicates if the xml should be well formed or not
	 * @throws Exception upon error.
	 */
	private static void serializeECSpec(ECSpec ecSpec, OutputStream writer, boolean pretty) throws Exception {
		marshall("org.rifidi.edge.epcglobal.ale", objectFactoryALE.createECSpec(ecSpec), writer, pretty);
	}
	
	
	/**
	 * This method serializes en ECSpec to an xml and writes it into a writer.
	 * @param ecSpec spec to be serialized.
	 * @param writer to writer where to store.
	 * @throws Exception upon error.
	 */
	public static void serializeECSpec(ECSpec ecSpec, Writer writer) throws Exception {
		marshall("org.rifidi.edge.epcglobal.ale", objectFactoryALE.createECSpec(ecSpec), writer, true);		
	}	
	
	
	/**
	 * This method serializes ec reports to an xml and writes it into a writer.
	 * 
	 * @param ecReports to serialize
	 * @param writer to write the xml into
	 * @param pretty indicates if the xml should be well formed or not
	 * @throws Exception upon error.
	 * @throws IOException if deserialization fails
	 */
	private static void serializeECReports(ECReports ecReports, Writer writer, boolean pretty) throws Exception {
		marshall("org.rifidi.edge.epcglobal.ale", objectFactoryALE.createECReports(ecReports), writer, pretty);	
	}
	
	private static void serializeImplementationExceptionResponse(ImplementationExceptionResponse e, Writer writer, boolean pretty) throws Exception {
		ImplementationException implEx = new ImplementationException();
		implEx.setReason(e.getMessage());
//		marshall("org.rifidi.edge.epcglobal.ale", objectFactoryALE.createImplementationExceptionResponse(e), writer, pretty);	
		marshall("org.rifidi.edge.epcglobal.ale", objectFactoryALE.createImplementationException(implEx), writer, pretty);
	}
	
	/**
	 * marshalles the object into the stream. if errors occur, they are written to the log.
	 * @param marshaller the marshaller.
	 * @param pretty if formatted or not.
	 * @param o the object to write.
	 * @param of the output stream.
	 * @throws Exception upon error.
	 */
	private static void marshall(String jaxbContext, Object o, Object of, boolean pretty) throws Exception {
		try {
			Marshaller marshaller = getMarshaller(jaxbContext, pretty);
			if (of instanceof Writer) {
				marshaller.marshal(o, (Writer) of);
			} else {
				OutputStream fof = null;
				if (of instanceof String) {
					fof = new FileOutputStream((String) of);
				} else if (of instanceof OutputStream) {
					fof = (OutputStream) of;
				} else {
					throw new Exception("Wrong writer provided.");
				}
				marshaller.marshal(o, fof);
			}
				
		} catch (Exception e) {
			log.error(String.format("Caught exception during marshalling:\n%s", e));
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * creates a marshaller on pooled JAXBContext instances.
	 * @param jaxbContext the context on which to create a marshaller.
	 * @param pretty if formatted or not.
	 * @return the marshaller.
	 * @throws JAXBException when unable to create the marshaller.
	 */
	private static Marshaller getMarshaller(String jaxbContext, boolean pretty) throws JAXBException {
		JAXBContext context = null;
		synchronized (contexts) {
			context = contexts.get(jaxbContext);
			if (null == context) {
				context = JAXBContext.newInstance(jaxbContext);
				contexts.put(jaxbContext, context);
			}
		}
		Marshaller marshaller = null;
		synchronized (context) {
			marshaller = context.createMarshaller();
		}			
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(pretty));
		return marshaller;
	}
	
	/**
	 * ORANGE: This method serializes a ADD_ROSPEC to an xml and writes it into a file.
	 * @param addRoSpec containing the ADD_ROSPEC to be written into a file
	 * @param pathName the file where to store
	 * @throws IOException whenever an io problem occurs
	 */
	public static void serializeAddROSpec(ADD_ROSPEC addRoSpec, String pathName) throws IOException {
		try {
			Document document = addRoSpec.encodeXML();
			XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
			outputter.output(document, new FileOutputStream(pathName));
		} catch (InvalidLLRPMessageException e) {
			log.error("could not serialize AddROSpec:", e);
		}
	}
	
	/**
	 * ORANGE: This method serializes a ADD_ROSPEC to an xml and writes it into a file.
	 * @param roSpec containing the ADD_ROSPEC to be written into a file
	 * @param writer to write the xml into
	 * @throws IOException whenever an io problem occurs
	 */
	public static void serializeAddROSpec(ADD_ROSPEC addRoSpec, Writer writer) throws IOException {
		try {
			Document document = addRoSpec.encodeXML();
			XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
			outputter.output(document, writer);
		} catch (InvalidLLRPMessageException e) {
			log.error("could not serialize AddROSpec:", e);
		}
	}
	
	
	/**
	 * ORANGE: This method serializes an ADD_ACCESSSPEC to an xml and writes it into a file.
	 * @param addAccessSpec containing the ADD_ACCESSSPEC to be written into a file
	 * @param pathName the file where to store
	 * @throws IOException whenever an io problem occurs
	 */
	public static void serializeAddAccessSpec(ADD_ACCESSSPEC addAccessSpec, String pathName) throws IOException {
		try {
			Document document = addAccessSpec.encodeXML();
			XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
			outputter.output(document, new FileOutputStream(pathName));
		} catch (InvalidLLRPMessageException e) {
			log.error("could not serialize AddAccessSpec:", e);
		}
	}
	
	/**
	 * ORANGE: This method serializes an ADD_ACCESSSPEC to an xml and writes it into a file.
	 * @param addAccessSpec containing the ADD_ACCESSSPEC to be written into a file
	 * @param writer to write the xml into
	 * @throws IOException whenever an io problem occurs
	 */
	public static void serializeAddAccessSpec(ADD_ACCESSSPEC addAccessSpec, Writer writer) throws IOException {
		try {
			Document document = addAccessSpec.encodeXML();
			XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
			outputter.output(document, writer);
		} catch (InvalidLLRPMessageException e) {
			log.error("could not serialize AddAccessSpec:", e);
		}
	}
	
}
