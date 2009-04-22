package org.rifidi.edge.client.ale.ecspecview.views;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ALEServicePortType;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ArrayOfString;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.Define;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.DuplicateNameExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ECSpecValidationExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.EmptyParms;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.GetECSpec;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.GetSubscribers;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ImplementationExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.NoSuchNameExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.SecurityExceptionResponse;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec.LogicalReaders;
import org.rifidi.edge.client.ale.ecspecview.model.ECSpecDecorator;

/**
 * @author kyle
 * 
 */
public class ECSpecViewContentProvider implements ITreeContentProvider,
		ECSpecController {

	/** The viewer that this content provider works for */
	private Viewer viewer;
	/**
	 * The service port type this content provider works for. For now should
	 * only ever be one thing in here!
	 */
	private List<ALEServicePortType> servicePortType;
	/** The map of ECSpeNames to ECSpec Objects */
	private Map<String, ECSpec> specNameToSpec;
	/** The map of ECSpeNames to subscribers */
	private Map<String, ArrayOfString> specNameToSubscribers;
	/**The map of ECSpecNames to logicalReaders*/
	private Map<String, LogicalReaders>specNameToLogicalReaders;
	/** The static reference to this class */
	private static ECSpecViewContentProvider controller;
	private Log logger = LogFactory.getLog(ECSpecViewContentProvider.class);

	/**
	 * Constructor called by eclipse framerwork
	 */
	public ECSpecViewContentProvider() {
		super();
		controller = this;
	}

	public static ECSpecController getController() {
		return controller;
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
			List list = (List) parentElement;
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
			Object[] retVal = new Object[specNameToSpec.values().size()];
			specNameToSpec.values().toArray(retVal);
			return retVal;
		} else if (parentElement instanceof ECSpec) {
			Object[] retVal = new Object[2];
			ECSpecDecorator dec = (ECSpecDecorator)parentElement;
			retVal[0] = specNameToLogicalReaders.get(dec.getName());
			retVal[1] = specNameToSubscribers.get(dec.getName());
			return retVal;
		}else if(parentElement instanceof ArrayOfString){
			ArrayOfString aos = (ArrayOfString)parentElement;
			if(aos.getString()!=null){
				Object[] retVal = new Object[aos.getString().size()];
				aos.getString().toArray(retVal);
				return retVal;
			}
		}else if(parentElement instanceof LogicalReaders){
			LogicalReaders readers = (LogicalReaders)parentElement;
			if(readers.getLogicalReader()!=null){
				Object[] retVal = new Object[readers.getLogicalReader().size()];
				readers.getLogicalReader().toArray(retVal);
				return retVal;
			}
		}
		else{
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
			return !this.specNameToSpec.isEmpty();
		} else if (element instanceof ECSpec) {
			return true;
		} else if (element instanceof Collection) {
			return !((Collection) element).isEmpty();
		} else if(element instanceof LogicalReaders){
			LogicalReaders readers = (LogicalReaders)element;
			if(readers.getLogicalReader()!=null){
				return !readers.getLogicalReader().isEmpty();
			}
		}else if(element instanceof ArrayOfString){
			ArrayOfString subscribers = (ArrayOfString)element;
			if(subscribers.getString()!=null){
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
		this.servicePortType = (List) newInput;
		refresh();

	}

	/**
	 * Method to connect to the ALEPort and get the current list of ECSpecs.
	 */
	private void refresh() {
		try {
			ArrayOfString names = this.servicePortType.get(0).getECSpecNames(
					new EmptyParms());
			specNameToSpec = new HashMap<String, ECSpec>();
			specNameToSubscribers = new HashMap<String, ArrayOfString>();
			specNameToLogicalReaders=new HashMap<String, LogicalReaders>();

			// Get the LogicalReader and Subscribers for each ecspec name
			for (String name : names.getString()) {
				GetECSpec getECSpec = new GetECSpec();
				getECSpec.setSpecName(name);
				GetSubscribers getSubscribers = new GetSubscribers();
				getSubscribers.setSpecName(name);
				try {

					ECSpec spec = this.servicePortType.get(0).getECSpec(
							getECSpec);
					specNameToSpec.put(name, new ECSpecDecorator(name, spec));
					ArrayOfString subscribers = this.servicePortType.get(0)
							.getSubscribers(getSubscribers);
					if (subscribers!=null) {
						specNameToSubscribers
								.put(name, subscribers);
					} else {
						specNameToSubscribers
								.put(name, new ArrayOfString());
					}
					specNameToLogicalReaders.put(name, spec.getLogicalReaders());

				} catch (NoSuchNameExceptionResponse e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (ImplementationExceptionResponse e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityExceptionResponse e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.viewer.refresh();
	}

	@Override
	public void define(String name, ECSpec spec) {
		Define define = new Define();
		define.setSpec(spec);
		define.setSpecName(name);
		try {
			this.servicePortType.get(0).define(define);
		} catch (ImplementationExceptionResponse e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityExceptionResponse e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ECSpecValidationExceptionResponse e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DuplicateNameExceptionResponse e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		refresh();
	}

}
