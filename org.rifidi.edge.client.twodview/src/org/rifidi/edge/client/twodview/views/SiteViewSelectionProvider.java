/**
 * 
 */
package org.rifidi.edge.client.twodview.views;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.rifidi.edge.client.model.sal.RemoteReader;
import org.rifidi.edge.client.twodview.layers.ListeningScalableLayeredPane;
import org.rifidi.edge.client.twodview.listeners.SiteViewFigureSelectionListener;
import org.rifidi.edge.client.twodview.sfx.ReaderAlphaImageFigure;

/**
 * @author kyle
 * 
 */
public class SiteViewSelectionProvider implements ISelectionProvider,
		SiteViewFigureSelectionListener {

	/** The selection listeners */
	private Set<ISelectionChangedListener> listeners;
	/** The current selection for this class */
	private ISelection selection;
	private ListeningScalableLayeredPane pane;

	/**
	 * Constructor
	 */
	public SiteViewSelectionProvider(ListeningScalableLayeredPane pane) {
		listeners = new HashSet<ISelectionChangedListener>();
		pane.addImageSelectionListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	@Override
	public ISelection getSelection() {
		return selection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse
	 * .jface.viewers.ISelection)
	 */
	@Override
	public void setSelection(ISelection selection) {
		if (this.selection != selection) {
			this.selection = selection;
			SelectionChangedEvent sce = new SelectionChangedEvent(this,
					getSelection());
			for (ISelectionChangedListener l : listeners) {
				l.selectionChanged(sce);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener
	 * (org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		this.listeners.add(listener);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener
	 * (org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		this.listeners.remove(listener);
	}

	public void dispose() {
		if(pane!=null){
			pane.remoteImageSelectionListener(this);
		}
	}

	@Override
	public void figureSelected(IFigure figure) {
		if (figure == null) {
			setSelection(new StructuredSelection());
			return;
		}
		if (figure instanceof ReaderAlphaImageFigure) {
			RemoteReader reader = ((ReaderAlphaImageFigure) figure).getReader();
			setSelection(new StructuredSelection(reader));
			return;
		}
		setSelection(new StructuredSelection());

	}


}
