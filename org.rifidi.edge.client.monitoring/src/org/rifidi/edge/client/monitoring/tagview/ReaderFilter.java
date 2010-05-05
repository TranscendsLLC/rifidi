/**
 * 
 */
package org.rifidi.edge.client.monitoring.tagview;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.rifidi.edge.client.monitoring.tagview.model.TagModel;

/**
 * @author kyle
 * 
 */
public class ReaderFilter extends ViewerFilter {

	private Set<String> readerIDs;

	public ReaderFilter() {
		readerIDs = new HashSet<String>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers
	 * .Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (readerIDs.isEmpty()) {
			return true;
		}
		TagModel tag = (TagModel) element;
		if (tag.getReaderID() == null || tag.getReaderID().isEmpty()) {
			return true;
		}
		if (readerIDs.contains(tag.getReaderID())) {
			return true;
		} else {
			return false;
		}
	}

	public void addReader(String readerID) {
		this.readerIDs.add(readerID);
	}

}
