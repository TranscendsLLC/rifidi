package org.rifidi.edge.core.connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.jms.ConnectionFactory;

import org.rifidi.edge.core.connection.jms.JMSHelper;
import org.rifidi.edge.core.connection.jms.JMSMessageThread;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;
import org.rifidi.edge.core.readerPlugin.ReaderPluginFactory;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

public class ReaderConnectionRegistryServiceImpl implements ReaderConnectionRegistryService{

	HashMap<Integer, ReaderConnection> idMap;
	
	int counter;
	
	ConnectionFactory connectionFactory;
	
	public ReaderConnectionRegistryServiceImpl(){
		initialize();
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.session.SessionRegistryService#intalize()
	 */
	public void initialize(){
		//counter=-1;
		idMap = new HashMap<Integer, ReaderConnection>();
		ServiceRegistry.getInstance().service(this);
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.session.SessionRegistryService#createReaderSession(org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo)
	 */
	public ReaderConnection createReaderSession(AbstractReaderInfo abstractConnectionInfo){
		if (abstractConnectionInfo == null)
			throw new IllegalArgumentException("Null references not allowed.");
			
		if (counter >= Integer.MAX_VALUE - 100)
			throw new RuntimeException("Session counter reached max value: " + (Integer.MAX_VALUE - 100));
		
		counter++;
		
		JMSHelper jmsHelper = new JMSHelper();
		jmsHelper.initializeJMSQueue(connectionFactory, Integer.toString(counter));

		IReaderPlugin adapter = ReaderPluginFactory.getInstance().createReaderAdapter(abstractConnectionInfo);
		
		JMSMessageThread jmsThread = new JMSMessageThread(counter, adapter, jmsHelper);
		
		ReaderConnection session = new ReaderConnection(abstractConnectionInfo, adapter, counter, jmsThread);
		
		idMap.put(counter, session);
		
		return session;
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.abstractConnectionInfocore.session.SessionRegistryService#getReaderSession(int)
	 */
	public ReaderConnection getReaderSession(int sessionID){
		return idMap.get(sessionID);
	}
	
	@Override
	public boolean containsReaderSession(int sessionID) {
		return idMap.containsKey(sessionID);
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.session.SessionRegistryService#deleteReaderSession(int)
	 */
	public void deleteReaderSession(int sessionID){
		idMap.remove(sessionID);
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.session.SessionRegistryService#getAllReaderSessions()
	 */
	public List<IReaderPlugin> getAllReaderSessions(){
		
		List<IReaderPlugin> list = new ArrayList<IReaderPlugin>();
		
		for(ReaderConnection session: idMap.values() ){
			list.add(session.getAdapter());
		}
		return list;
	}

	@Override
	public int sessionCount() {
		return idMap.size();
	}
	
	@Inject
	public void setConnectionFactory(ConnectionFactory connectionFactory){
		this.connectionFactory = connectionFactory;
	}

}
