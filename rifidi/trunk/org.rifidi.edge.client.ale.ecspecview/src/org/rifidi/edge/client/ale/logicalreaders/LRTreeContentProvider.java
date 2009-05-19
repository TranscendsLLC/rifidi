
package org.rifidi.edge.client.ale.logicalreaders;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.ALELRServicePortType;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.EmptyParms;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.GetLRSpec;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.ImplementationExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.NoSuchNameExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.SecurityExceptionResponse;
import org.rifidi.edge.client.ale.logicalreaders.decorators.LRSpecDecorator;
import org.rifidi.edge.client.ale.logicalreaders.decorators.LRSpecSubnodeDecorator;

/**
 * TODO: Class level comment.  
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public class LRTreeContentProvider implements ITreeContentProvider {
	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(LRTreeContentProvider.class);
	/** Viewer this content provider is responsible for. */
	private TreeViewer viewer;
	/** Service port connected to the edge server. */
	private ALELRServicePortType lrService;
	/** Logical readers. */
	private List<LRSpecDecorator> lrspecs;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.
	 * Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ALELRServicePortType) {
			return lrspecs.toArray();
		}

		if (parentElement instanceof LRSpecDecorator) {
			List<LRSpecSubnodeDecorator> subnodes = new ArrayList<LRSpecSubnodeDecorator>();
			for (String readerName : ((LRSpecDecorator) parentElement)
					.getReaders().getReader()) {
				GetLRSpec spec = new GetLRSpec();
				spec.setName(readerName);
				try {
					subnodes.add(new LRSpecSubnodeDecorator(readerName,
							lrService.getLRSpec(spec),
							(LRSpecDecorator) parentElement, this));
				} catch (SecurityExceptionResponse e) {
					logger.fatal(e);
				} catch (ImplementationExceptionResponse e) {
					logger.fatal(e);
				} catch (NoSuchNameExceptionResponse e) {
					logger.warn(e);
				}
			}
			return subnodes.toArray();
		}
		return null;
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
		if (element instanceof ALELRServicePortType) {
			return true;
		}
		if (element instanceof LRSpecDecorator) {
			return ((LRSpecDecorator) element).isIsComposite();
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
		return null;
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
	 * 
	 */
	private void updateReaderList(List<LRSpecDecorator> newList) {
		lrspecs.clear();
		lrspecs.addAll(newList);
		viewer.setInput(viewer.getInput());
	}

	/*
	 * 
	 */
	private List<LRSpecDecorator> getReaderList() {
		List<LRSpecDecorator> lrspecs = new ArrayList<LRSpecDecorator>();
		try {
			for (String name : lrService
					.getLogicalReaderNames(new EmptyParms()).getString()) {
				GetLRSpec spec = new GetLRSpec();
				spec.setName(name);
				// we need the reader names, decorate the reader
				lrspecs
						.add(new LRSpecDecorator(name, lrService
								.getLRSpec(spec)));
			}
			return lrspecs;
		} catch (SecurityExceptionResponse e) {
			logger.fatal(e);
		} catch (ImplementationExceptionResponse e) {
			logger.fatal(e);
		} catch (NoSuchNameExceptionResponse e) {
			logger.warn(e);
		} catch (Throwable t) {
			logger.warn(t);
		}
		return lrspecs;
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
		lrspecs = new ArrayList<LRSpecDecorator>();
		if (newInput == null) {
			this.lrService = null;
			return;
		}
		if (!(newInput == null) && !(newInput instanceof ALELRServicePortType)) {
			throw new RuntimeException("Expected ALELRServicePortTypeImpl got "
					+ newInput.getClass());
		}
		this.lrService = (ALELRServicePortType) newInput;
		this.viewer = (TreeViewer) viewer;
		lrspecs.addAll(getReaderList());
		viewer.refresh();
	}

}
