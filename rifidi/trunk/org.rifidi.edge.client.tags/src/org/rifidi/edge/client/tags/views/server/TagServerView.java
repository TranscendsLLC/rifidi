package org.rifidi.edge.client.tags.views.server;

import java.io.CharArrayReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;
import org.rifidi.edge.client.connections.edgeserver.EdgeServerConnection;
import org.rifidi.edge.client.connections.registryservice.RemoteReaderConnectionRegistryService;
import org.rifidi.edge.client.connections.registryservice.listeners.RemoteReaderConnectionRegistryListener;
import org.rifidi.edge.client.connections.remotereader.RemoteReader;
import org.rifidi.edge.client.connections.remotereader.RemoteReaderID;
import org.rifidi.edge.client.connections.remotereader.listeners.ReaderMessageListener;
import org.rifidi.edge.client.tags.utils.TagContainer;
import org.rifidi.edge.core.readerplugin.messages.impl.EnhancedTagMessage;
import org.rifidi.edge.core.readerplugin.messages.impl.TagMessage;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * @author jerry
 * 
 */
public class TagServerView extends ViewPart implements ReaderMessageListener,
		RemoteReaderConnectionRegistryListener {
	static private Log logger = LogFactory.getLog(TagServerView.class);
	private Composite composite;
	private TableViewer table;

	// TODO Put this someplace better -- maybe
	private Set<TagContainer> tags = new TreeSet<TagContainer>();

	private Object locker = new Object();

	private Listener selChangeListener = new Listener() {
		public void handleEvent(Event event) {
			table.getTable().setSortColumn((TableColumn) event.widget);
			table.refresh();
		}
	};

	private RefreshThread thread;
	private EdgeServerConnection connection;
	private RemoteReaderConnectionRegistryService readerConnectionRegistryService;

	public TagServerView() {
		ServiceRegistry.getInstance().service(this);
	}

	@Override
	public void createPartControl(Composite parent) {
		logger.debug("Creating " + this.getClass().getSimpleName()
				+ " control.");
		ServiceRegistry.getInstance().service(this);

		composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		table = new TableViewer(composite, SWT.BORDER | SWT.V_SCROLL | SWT.WRAP
				| SWT.H_SCROLL | SWT.FULL_SELECTION);

		table.getTable().setLinesVisible(true);
		table.getTable().setHeaderVisible(true);

		String[] columnNames = { "Tag ID", "Time Stamp", "Antenna ID",
				"Signal Strength", "Velocity" };
		int[] columnWidths = new int[] { 225, 120, 80, 110, 80 };
		int[] columnAlignments = new int[] { SWT.LEFT, SWT.LEFT, SWT.LEFT,
				SWT.LEFT, SWT.LEFT };

		List<TableColumn> tableColumns = new ArrayList<TableColumn>();
		// create columns and add listeners to them
		for (int i = 0; i < columnNames.length; i++) {
			TableColumn tableColumn = new TableColumn(table.getTable(),
					columnAlignments[i]);
			tableColumn.setText(columnNames[i]);
			tableColumn.setWidth(columnWidths[i]);
			// Add Listener
			tableColumn.addListener(SWT.Selection, this.selChangeListener);
			tableColumns.add(tableColumn);
		}

		table.setContentProvider(new TagTableServerContentProvider());
		table.setLabelProvider(new TagTableServerLabelProvider());
		table.setInput(tags);

		getSite().setSelectionProvider(table);
		setPartName(getViewSite().getSecondaryId().replace("&colon;", ":"));
		setContentDescription(getViewSite().getSecondaryId().replace("&colon;",
				":"));
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		table.getControl().setFocus();
	}

	public void dispose() {
		tags.clear();
		readerConnectionRegistryService.addListener(this);

		thread.interrupt();
		super.dispose();
	}

	@Override
	public void onMessage(Message message, RemoteReader reader) {
		logger.debug("TagServerView: onMessage: " + message.toString() + " from Reader: "+reader.toString());
		if (!table.getControl().isDisposed()) {
			// synchronized(this) {
			table.getTable().getDisplay().syncExec(
					new MessageRunner((TextMessage) message, reader));
			// }
		}
	}

	private class MessageRunner implements Runnable {

		private TextMessage message;

		public MessageRunner(TextMessage message, RemoteReader reader) {
			this.message = message;
			logger.debug("in message runner server");
		}

		@Override
		public void run() {
			Reader reader;
			try {
				reader = new CharArrayReader(message.getText().toCharArray());
			} catch (JMSException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();

				completelyRemoveListeners();

				return;
			}
			JAXBContext context;
			TagMessage tagMessage;
			try {
				context = JAXBContext.newInstance(TagMessage.class,
						EnhancedTagMessage.class);
				Unmarshaller unmarshaller = context.createUnmarshaller();
				tagMessage = (TagMessage) unmarshaller.unmarshal(reader);
				// tagMessage = (TagMessage)
				// messageConvertingService.MessageFromXML(message.getText());
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

				completelyRemoveListeners();

				return;
			}
			// catch (JMSException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// connection.removeListener(TagView.this);
			//				
			// return;
			// }

			TagContainer tm2;
			synchronized (locker) {
				if ((tm2 = getTagByID(tagMessage, tags)) != null) {
					tm2.setTag(tagMessage);
					tm2.setInternalTime(System.currentTimeMillis());
					table.refresh(tm2);
					// logger.debug(tm2);
				} else {
					tags.add(new TagContainer(tagMessage, System
							.currentTimeMillis()));
					// TODO find a better way of doing this.
					table.refresh();
				}
				locker.notify();
			}
			// table.refresh();
			// logger.debug(tagMessage.toXML());
		}

		private TagContainer getTagByID(TagMessage tm1,
				Collection<TagContainer> tags) {
			for (TagContainer tm : tags) {
				if (Arrays.equals(tm1.getId(), tm.getTag().getId()))
					return tm;
			}
			return null;
		}
	}

	public void initTagView(EdgeServerConnection connection) {
		// TODO Auto-generated method stub
		this.connection = connection;
		completelyRemoveListeners();
		thread = new RefreshThread();
		thread.start();
	}

	// TODO Implement this better.
	private class RefreshThread extends Thread {

		private long refreashRate = 1000;
		private long retainTime = 3000;

		public RefreshThread() {
			super(getViewSite().getSecondaryId().replace("&colon;", ":")
					+ " Refresh thread.");
			// TODO Auto-generated constructor stub
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (!isInterrupted()) {
				List<TagContainer> tagsToKeep = new ArrayList<TagContainer>();
				// logger.debug(tags);
				// Convoluted but needed
				synchronized (locker) {
					for (TagContainer tm : tags) {
						// logger.debug(ByteAndHexConvertingUtility.toHexString(tm
						// .getTag().getId()) + ": " +
						// (System.currentTimeMillis() - tm.getInternalTime()));
						if (System.currentTimeMillis() - tm.getInternalTime() <= retainTime) {
							// logger.debug("Keeping\n" + tm);
							tagsToKeep.add(tm);
						}
					}
					tags.clear();
					for (TagContainer tm : tagsToKeep) {
						tags.add(tm);
					}
					tagsToKeep.clear();
					locker.notify();
				}

				if (!table.getControl().isDisposed()) {
					// synchronized(this) {
					table.getControl().getDisplay().syncExec(new Runnable() {
						@Override
						public void run() {
							table.refresh();
						}
					});
					// }
				}
				try {
					Thread.sleep(refreashRate);
				} catch (InterruptedException e) {
					interrupt();
				}
			}
		}
	}

	private void completelyRemoveListeners() {
		readerConnectionRegistryService.removeListener(this);

		for (RemoteReaderID id : connection.getRemoteReaderIDs()) {
			readerConnectionRegistryService.getRemoteReader(id)
					.removeMessageListener(this);
		}

	}

	@Override
	public void readerAdded(RemoteReader remoteReader) {
		if (remoteReader.getServerID() == connection.getID()) {
			remoteReader.addMessageListener(this);
		}

	}

	@Override
	public void readerRemoved(RemoteReader remoteReader) {
		if (remoteReader.getServerID() == connection.getID()) {
			remoteReader.removeMessageListener(this);
		}

	}

	@Inject
	public void setReaderConnectionRegistryService(
			RemoteReaderConnectionRegistryService readerConnectionRegistryService) {
		this.readerConnectionRegistryService = readerConnectionRegistryService;
	}
}