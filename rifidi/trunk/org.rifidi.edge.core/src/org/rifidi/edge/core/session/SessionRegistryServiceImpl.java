package org.rifidi.edge.core.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo;
import org.rifidi.edge.core.readerAdapter.IReaderAdapter;
import org.rifidi.edge.core.readerAdapter.ReaderAdapterFactory;

public class SessionRegistryServiceImpl implements SessionRegistryService {

	HashMap<Integer, Session> idMap;
	
	int counter;
	
	public SessionRegistryServiceImpl(){
		initialize();
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.session.SessionRegistryService#intalize()
	 */
	public void initialize(){
		counter=-1;
		idMap = new HashMap<Integer, Session>();
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.session.SessionRegistryService#createReaderSession(org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo)
	 */
	public int createReaderSession(AbstractConnectionInfo abstractConnectionInfo){
		counter++;
		IReaderAdapter adapter = ReaderAdapterFactory.createReaderAdapter(abstractConnectionInfo);
		Session session = new Session(abstractConnectionInfo, adapter, counter);
		
		idMap.put(counter, session);
		
		return counter;
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.session.SessionRegistryService#getReaderSession(int)
	 */
	public Session getReaderSession(int sessionID){
		return idMap.get(sessionID);
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
	public List<IReaderAdapter> getAllReaderSessions(){
		
		List<IReaderAdapter> list = new ArrayList<IReaderAdapter>();
		
		for(Session session: idMap.values() ){
			list.add(session.getAdapter());
		}
		return list;
	}
}
