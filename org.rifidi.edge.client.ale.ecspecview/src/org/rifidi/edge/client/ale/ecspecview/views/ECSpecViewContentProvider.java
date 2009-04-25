package org.rifidi.edge.client.ale.ecspecview.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ALEServicePortType;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ArrayOfString;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.NoSuchNameExceptionResponse;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec.LogicalReaders;
import org.rifidi.edge.client.ale.ecspecview.Activator;
import org.rifidi.edge.client.ale.ecspecview.model.ECSpecDecorator;
import org.rifidi.edge.client.ale.logicalreaders.ALEService;

/**
 * @author kyle
 * 
 */
public class ECSpecViewContentProvider implements ITreeContentProvider {

	/** Logger for this class. */
	private Log logger = LogFactory.getLog(ECSpecViewContentProvider.class);
	/** The viewer that this content provider works for */
	private Viewer viewer;
	/** Service for managing ecspecs. */
	private ALEService service;

	/**
	 * Constructor called by eclipse framerwork
	 */
	public ECSpecViewContentProvider() {
		super();
		service = Activator.getDefault().getAleService();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.
	 * Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof List) {
			List<?> list = (List<?>) parentElement;
			if (list.size() > 0) {
				Object o = list.get(0);
				if (o != null && o instanceof ALEServicePortType) {
					Object[] retVal = new Object[1];
					retVal[0] = (ALEServicePortType) o;
					return retVal;
				}

			}
		}
		if (parentElement instanceof ALEServicePortType) {
			List<ECSpecDecorator> ret = new ArrayList<ECSpecDecorator>();
			for (String name : service.getAvailableECSpecNames()) {
				try {
					ret.add(new ECSpecDecorator(name, service.getECSpec(name)));
				} catch (NoSuchNameExceptionResponse e) {
					logger.warn(e);
				}
			}
			return ret.toArray();
		} else if (parentElement instanceof ECSpecDecorator) {
			Object[] retVal = new Object[2];
			ECSpecDecorator dec = (ECSpecDecorator) parentElement;
			retVal[0] = dec.getLogicalReaders();
			try {
				retVal[1] = service.getSubscribers(dec.getName());
			} catch (NoSuchNameExceptionResponse e) {
				logger.fatal(e);
			}
			return retVal;
		} else if (parentElement instanceof ArrayOfString) {
			ArrayOfString aos = (ArrayOfString) parentElement;
			if (aos.getString() != null) {
				Object[] retVal = new Object[aos.getString().size()];
				aos.getString().toArray(retVal);
				return retVal;
			}
		} else if (parentElement instanceof LogicalReaders) {
			LogicalReaders readers = (LogicalReaders) parentElement;
			if (readers.getLogicalReader() != null) {
				Object[] retVal = new Object[readers.getLogicalReader().size()];
				readers.getLogicalReader().toArray(retVal);
				return retVal;
			}
		} else {
			logger.debug(parentElement);
		}
		return new Object[] {};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object
	 * )
	 */
	@Override
	public Object getParent(Object element) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.
	 * Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof ALEServicePortType) {
			return true;
		} else if (element instanceof ECSpecDecorator) {
			return true;
		} else if (element instanceof Collection) {
			return !((Collection<?>) element).isEmpty();
		} else if (element instanceof LogicalReaders) {
			LogicalReaders readers = (LogicalReaders) element;
			if (readers.getLogicalReader() != null) {
				return !readers.getLogicalReader().isEmpty();
			}
		} else if (element instanceof ArrayOfString) {
			ArrayOfString subscribers = (ArrayOfString) element;
			if (subscribers.getString() != null) {
				return !subscribers.getString().isEmpty();
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java
	 * .lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface
	 * .viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput == null) {
			logger.error("newInput is null!");
			return;
		}
		if (!(newInput instanceof List)) {
			throw new RuntimeException(
					"Expected object to be an ALEServicePortType but was "
							+ newInput.getClass());
		}
		this.viewer = viewer;
		viewer.refresh();
	}

}
