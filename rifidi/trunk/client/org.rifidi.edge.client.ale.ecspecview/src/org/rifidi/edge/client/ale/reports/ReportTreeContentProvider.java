
package org.rifidi.edge.client.ale.reports;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.list.ListDiffEntry;
import org.eclipse.core.databinding.observable.list.ObservableList;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReport;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReportGroup;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReports;

/**
 * TODO: Class level comment.  
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public class ReportTreeContentProvider implements ITreeContentProvider,
		IListChangeListener {
	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(ReportTreeContentProvider.class);
	/** Viewer this content provider is responsible for. */
	private TreeViewer viewer;
	/** List containing the incoming reports. */
	private ObservableList model;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.
	 * Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ObservableList) {
			return ((ObservableList) parentElement).toArray();
		}
		if (parentElement instanceof ECReports) {
			return ((ECReports) parentElement).getReports().getReport()
					.toArray();
		}
		if (parentElement instanceof ECReport) {
			return ((ECReport) parentElement).getGroup().toArray();
		}
		if (parentElement instanceof ECReportGroup) {
			return ((ECReportGroup) parentElement).getGroupList().getMember()
					.toArray();
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
		if (element instanceof ObservableList) {
			return true;
		}
		if (element instanceof ECReports) {
			return true;
		}
		if (element instanceof ECReport) {
			return true;
		}
		if (element instanceof ECReportGroup) {
			return true;
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
			return;
		}
		if (!(newInput instanceof ObservableList)) {
			throw new RuntimeException("Expected an ObservableList but got "
					+ newInput.getClass());
		}
		if (!(viewer instanceof TreeViewer)) {
			throw new RuntimeException("Expected a TreeViewer but got "
					+ viewer.getClass());
		}
		this.viewer = (TreeViewer) viewer;
		if (model != null) {
			model.removeListChangeListener(this);
		}
		model = (ObservableList) newInput;
		model.addListChangeListener(this);
		viewer.refresh();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.core.databinding.observable.list.IListChangeListener#
	 * handleListChange
	 * (org.eclipse.core.databinding.observable.list.ListChangeEvent)
	 */
	@Override
	public void handleListChange(ListChangeEvent event) {
		for (ListDiffEntry entry : event.diff.getDifferences()) {
			if (entry.isAddition()) {
				viewer.add(model, entry.getElement());
			} else {
				viewer.remove(entry.getElement());
			}
		}
	}

}
