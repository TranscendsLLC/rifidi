package org.rifidi.edge.core.session;

import java.util.List;

import org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo;
import org.rifidi.edge.core.readerAdapter.IReaderAdapter;

public class SessionRegistryServiceImpl implements SessionRegistryService {

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.session.SessionRegistryService#intalize()
	 */
	public void intalize(){
		
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.session.SessionRegistryService#createReaderSession(org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo)
	 */
	public int createReaderSession(AbstractConnectionInfo pattern){
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.session.SessionRegistryService#getReaderSession(int)
	 */
	public void getReaderSession(int apapterID){
		
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.session.SessionRegistryService#deleteReaderSession(int)
	 */
	public void deleteReaderSession(int apapterID){
		
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.session.SessionRegistryService#getAllReaderSessions()
	 */
	public List<IReaderAdapter> getAllReaderSessions(){
		
		return null;
	}
}
