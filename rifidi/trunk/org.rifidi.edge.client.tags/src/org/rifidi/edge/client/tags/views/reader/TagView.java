package org.rifidi.edge.client.tags.views.reader;

import java.io.CharArrayReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;
import org.rifidi.edge.client.connections.remotereader.RemoteReader;
import org.rifidi.edge.client.connections.remotereader.listeners.ReaderMessageListener;
import org.rifidi.edge.client.tags.utils.TagContainer;
import org.rifidi.edge.client.tags.utils.TagMessageUnmarshaller;
import org.rifidi.edge.core.readerplugin.messages.impl.EnhancedTagMessage;
import org.rifidi.edge.core.readerplugin.messages.impl.TagMessage;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * @author jerry
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class TagView extends ViewPart implements ReaderMessageListener {
	static private Log logger = LogFactory.getLog(TagView.class);
	private Composite composite;
	private TableViewer table;
	private Set<Button> buttons = new HashSet<Button>();

	// TODO Put this someplace better -- maybe
	private Set<TagContainer> tags = new TreeSet<TagContainer>();

	private AtomicBoolean lock = new AtomicBoolean(false);
	
	private Listener selChangeListener = new Listener() {
		public void handleEvent(Event event) {
			table.getTable().setSortColumn((TableColumn) event.widget);
			table.refresh();
		}
	};

	private RefreshThread thread;
	private RemoteReader connection;

	// private MessageConvertingService messageConvertingService;

	@Override
	public void createPartControl(Composite parent) {
		logger.debug("Creating " + this.getClass().getSimpleName()
				+ " control.");
		ServiceRegistry.getInstance().service(this);

//		composite = new Composite(parent, SWT.NONE);
//		composite.setLayout(new RowLayout());
//
//		Group group = new Group(composite, SWT.NONE);
//		group.setText("Antenna Selection");
//		group.setLayout(new FillLayout());
//
//		for (int i = 0; i < 3; i++) {
//			Button button = new Button(group, SWT.CHECK);
//			button.setText("Antenna #" + i);
//			button.setData("Antenna", i);
//			button.setSelection(true);
//			buttons.add(button);
//
//		}

		table = new TableViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.WRAP
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

		table.setContentProvider(new TagTableContentProvider());
		table.setLabelProvider(new TagTableLabelProvider());
		table.setInput(tags);

		getSite().setSelectionProvider(table);
		setPartName(getViewSite().getSecondaryId().replace("&colon;", ":"));
		setContentDescription(getViewSite().getSecondaryId().replace("&colon;",
				":"));
	}

	@Override
	public void setFocus() {
		table.getControl().setFocus();
	}

	public void dispose() {
		tags.clear();
		connection.removeMessageListener(this);

		thread.interrupt();
		thread = null;
		super.dispose();
	}

	@Override
	public void onMessage(Message message, RemoteReader reader) {
		logger.debug(this.getClass().getName()+": "+message.toString());
		 logger.debug("TagView: onMessage: " + message.toString() +
		 " from Reader: "+reader.toString());
		if (lock.compareAndSet(false, true)) {
			table.getTable().getDisplay().syncExec(
					new MessageRunner((TextMessage) message));
		}
	}

	public void initTagView(RemoteReader connection) {
		this.connection = connection;
		this.connection.addMessageListener(this);
		thread = new RefreshThread();
		thread.start();
	}
	
	public HashSet<Integer> getSelectedAntennas(){
		HashSet<Integer> selectedAnt = new HashSet<Integer>();
		for (Button button : buttons) {
			if(button.getSelection()){
				selectedAnt.add((Integer)button.getData("Antenna"));
			}
			
		}
		return selectedAnt;
	}

	private class MessageRunner implements Runnable {

		private TextMessage message;

		public MessageRunner(TextMessage message) {
			this.message = message;
			// logger.debug("in message runner tag view");
		}

		@Override
		public void run() {
			try {
				Reader reader;
				try {
					reader = new CharArrayReader(message.getText()
							.toCharArray());
				} catch (JMSException e1) {
					logger.error(e1);
					connection.removeMessageListener(TagView.this);
					return;
				}
				TagMessage tagMessage=new TagMessage();
				try {

					tagMessage = (TagMessage) TagMessageUnmarshaller
							.getInstance().unmarshall(reader);
				} catch (JAXBException e) {
					logger.error(e);
					connection.removeMessageListener(TagView.this);
					return;
				}
				//TODO: check if tag is on a selected antenna - if not: throw it out.
//				EnhancedTagMessage etm = (EnhancedTagMessage)tagMessage;
//				if(!getSelectedAntennas().contains(etm.getAntennaId())) return;
				TagContainer tm2;
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
			} finally {
				lock.compareAndSet(true, false);
			}
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
				for (TagContainer tm : tags) {
					// logger.debug(ByteAndHexConvertingUtility.toHexString(tm.getTag().getId())
					// + ": " + (System.currentTimeMillis() -
					// tm.getInternalTime()));
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

				if (!table.getControl().isDisposed()) {
					// synchronized(this) {
					table.getControl().getDisplay().syncExec(new Runnable() {
						@Override
						public void run() {
							table.refresh();
						}
					});

					// }

					try {
						Thread.sleep(refreashRate);
					} catch (InterruptedException e) {
						interrupt();
					}
				}
			}
		}

	}
}