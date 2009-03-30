/*
 *  LLRPHelloWorld.java
 *  Created:	May 26, 2008
 *  Author:    Kyle Neumeier - kyle@pramari.com
 */

package sandbox;

import org.apache.log4j.BasicConfigurator;
import org.llrp.ltk.generated.enumerations.AISpecStopTriggerType;
import org.llrp.ltk.generated.enumerations.AirProtocols;
import org.llrp.ltk.generated.enumerations.ROReportTriggerType;
import org.llrp.ltk.generated.enumerations.ROSpecStartTriggerType;
import org.llrp.ltk.generated.enumerations.ROSpecState;
import org.llrp.ltk.generated.enumerations.ROSpecStopTriggerType;
import org.llrp.ltk.generated.enumerations.StatusCode;
import org.llrp.ltk.generated.messages.ADD_ROSPEC;
import org.llrp.ltk.generated.messages.ADD_ROSPEC_RESPONSE;
import org.llrp.ltk.generated.messages.DELETE_ROSPEC;
import org.llrp.ltk.generated.messages.ENABLE_ROSPEC;
import org.llrp.ltk.generated.messages.GET_REPORT;
import org.llrp.ltk.generated.messages.RO_ACCESS_REPORT;
import org.llrp.ltk.generated.messages.START_ROSPEC;
import org.llrp.ltk.generated.parameters.AISpec;
import org.llrp.ltk.generated.parameters.AISpecStopTrigger;
import org.llrp.ltk.generated.parameters.C1G2EPCMemorySelector;
import org.llrp.ltk.generated.parameters.InventoryParameterSpec;
import org.llrp.ltk.generated.parameters.ROBoundarySpec;
import org.llrp.ltk.generated.parameters.ROReportSpec;
import org.llrp.ltk.generated.parameters.ROSpec;
import org.llrp.ltk.generated.parameters.ROSpecStartTrigger;
import org.llrp.ltk.generated.parameters.ROSpecStopTrigger;
import org.llrp.ltk.generated.parameters.TagReportContentSelector;
import org.llrp.ltk.net.LLRPConnection;
import org.llrp.ltk.net.LLRPConnectionAttemptFailedException;
import org.llrp.ltk.net.LLRPConnector;
import org.llrp.ltk.net.LLRPEndpoint;
import org.llrp.ltk.types.Bit;
import org.llrp.ltk.types.LLRPMessage;
import org.llrp.ltk.types.UnsignedByte;
import org.llrp.ltk.types.UnsignedInteger;
import org.llrp.ltk.types.UnsignedShort;
import org.llrp.ltk.types.UnsignedShortArray;
 
public class LLRPHelloWorld implements LLRPEndpoint {
 
    private LLRPConnection connection;
    //private static Logger logger;
 
    public LLRPHelloWorld() {
 
        // set logging level
        //logger = Logger.getLogger("org.llrp.ltk.example.LTKJavaExample");
       //logger.setLevel(Level.DEBUG);
 
        configure();
    }
 
    private void configure() {
 
        // set IP address of RFID reader
        String READER_IP_ADDRESS = "127.0.0.1";
 
        // create client-initiated LLRP connection
 
        connection = new LLRPConnector(this, READER_IP_ADDRESS);
 
        // connect to reader
        // LLRPConnector.connect waits for successful
        // READER_EVENT_NOTIFICATION from reader
 
        try {
            //logger.info("Initiate LLRP connection to reader");
            ((LLRPConnector) connection).connect();
        } catch (LLRPConnectionAttemptFailedException e1) {
            e1.printStackTrace();
            System.exit(1);
        }
 
 
 
        LLRPMessage response;
 
        try {
 
            // delete ROSpec
            //logger.info("Delete ROSPEC message ...");
            DELETE_ROSPEC del = new DELETE_ROSPEC();
            del.setROSpecID(new UnsignedInteger(1));
            response =  connection.transact(del, 10000);
 
            // load ADD_ROSPEC message
            //logger.info("Loading ADD_ROSPEC message from file ADD_ROSPEC.xml ...");
            
            ADD_ROSPEC addRospec = new ADD_ROSPEC();
            
            ROSpec ro = new ROSpec();
            ro.setROSpecID(new UnsignedInteger(1));
            ro.setPriority(new UnsignedByte(0));
            ro.setCurrentState(new ROSpecState(ROSpecState.Disabled));
            
            ROBoundarySpec rbs = new ROBoundarySpec();
            ROSpecStartTrigger rst = new ROSpecStartTrigger();
            rst.setROSpecStartTriggerType(new ROSpecStartTriggerType(ROSpecStartTriggerType.Null));
            rbs.setROSpecStartTrigger(rst);
            ROSpecStopTrigger rstopt = new ROSpecStopTrigger();
            rstopt.setROSpecStopTriggerType(new ROSpecStopTriggerType(ROSpecStopTriggerType.Null));
            rstopt.setDurationTriggerValue(new UnsignedInteger(0));
            rbs.setROSpecStopTrigger(rstopt);
            ro.setROBoundarySpec(rbs);
            
            AISpec ais = new AISpec();
            UnsignedShortArray usa = new UnsignedShortArray();
            usa.add(new UnsignedShort(0));
            ais.setAntennaIDs(usa);
            AISpecStopTrigger ast = new AISpecStopTrigger();
            ast.setAISpecStopTriggerType(new AISpecStopTriggerType(AISpecStopTriggerType.Null));
            ast.setDurationTrigger(new UnsignedInteger(0));
            ais.setAISpecStopTrigger(ast);
            InventoryParameterSpec ips = new InventoryParameterSpec();
            ips.setInventoryParameterSpecID(new UnsignedShort(9));
            AirProtocols ap = new AirProtocols();
            ap.set(AirProtocols.EPCGlobalClass1Gen2);
            ips.setProtocolID(ap);
            ais.addToInventoryParameterSpecList(ips);
            ro.addToSpecParameterList(ais);
            
            ROReportSpec rrs = new ROReportSpec();
            rrs.setROReportTrigger(new ROReportTriggerType(ROReportTriggerType.None));
            rrs.setN(new UnsignedShort(1));
            TagReportContentSelector trcs = new TagReportContentSelector();
            trcs.setEnableROSpecID(new Bit(1));
            trcs.setEnableSpecIndex(new Bit(1));
            trcs.setEnableInventoryParameterSpecID(new Bit(1));
            trcs.setEnableAntennaID(new Bit(1));
            trcs.setEnableChannelIndex(new Bit(1));
            trcs.setEnablePeakRSSI(new Bit(1));
            trcs.setEnableFirstSeenTimestamp(new Bit(1));
            trcs.setEnableLastSeenTimestamp(new Bit(1));
            trcs.setEnableTagSeenCount(new Bit(1));
            trcs.setEnableAccessSpecID(new Bit(1));
            C1G2EPCMemorySelector cgems = new C1G2EPCMemorySelector();
            cgems.setEnableCRC(new Bit(1));
            cgems.setEnablePCBits(new Bit(1));
            trcs.addToAirProtocolEPCMemorySelectorList(cgems);
            rrs.setTagReportContentSelector(trcs);
            ro.setROReportSpec(rrs);
            
            addRospec.setROSpec(ro);
 
            // send message to LLRP reader and wait for response
 
            //logger.info("Sending ADD_ROSPEC message ...");
            response =  connection.transact(addRospec, 10000);
 
            // check whether ROSpec addition was successful
            StatusCode status = ((ADD_ROSPEC_RESPONSE)response).getLLRPStatus().getStatusCode();
            if (status.equals(new StatusCode("M_Success"))) {
                //logger.info("Addition of ROSPEC was successful");
            }
            else {
                //logger.info(response.toXMLString());
                //logger.info("Addition of ROSPEC was unsuccessful, exiting");
                System.exit(1);
            }
 
            //logger.info("Enable ROSPEC ...");
            ENABLE_ROSPEC enable = new ENABLE_ROSPEC();
            enable.setROSpecID(new UnsignedInteger(1));
            response = connection.transact(enable, 10000);
 
 
            //logger.info("Start ROSPEC ...");
            START_ROSPEC start = new START_ROSPEC();
            start.setROSpecID(new UnsignedInteger(1));
            response = connection.transact(start, 10000);
            
            GET_REPORT gr = new GET_REPORT();
            connection.send(gr);
            //System.out.println("Get report response: " + omg.getResponseType() );
            
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
 
 
        //logger.info("RO_ACCESS_REPORT will be arriving asynchronously ...");
 
    }
 
    // messageReceived method is called whenever a message is received
    // asynchronously on the LLRP connection.
 
    public void messageReceived(LLRPMessage message) {
 
        // convert all messages received to LTK-XML representation
        // and print them to the console
 
        //logger.info("Received " + message.getName() + " message asychronously");
    	if(message.getClass().equals(RO_ACCESS_REPORT.class)) {
    		
    	}
 
    }
 
    public void errorOccured(String s) {
        //logger.info("Error Occured: " + s);
    }
 
 
    /**
     * @param args
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        new LLRPHelloWorld();
    }
 
}
