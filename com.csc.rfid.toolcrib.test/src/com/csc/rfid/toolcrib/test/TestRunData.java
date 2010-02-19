package com.csc.rfid.toolcrib.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.rifidi.edge.core.services.notification.data.EPCGeneration2Event;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

import com.csc.rfid.toolcrib.test.esper.CollectionType;

public class TestRunData {
	private final List<TagReadEvent> tagReadEvents;
	private final CollectionType type;
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"h:m:s a");
	public static String newline = System.getProperty("line.separator");

	public TestRunData(List<TagReadEvent> tagReadEvents, CollectionType type) {
		super();
		this.tagReadEvents = tagReadEvents;
		this.type = type;
	}

	public List<TagReadEvent> getTagReadEvents() {
		return tagReadEvents;
	}

	public CollectionType getType() {
		return type;
	}

	public String getPrintString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("*** " + type + newline);
		for (TagReadEvent e : tagReadEvents) {
			stringBuilder.append("*");
			stringBuilder.append(" "
					+ dateFormat.format(new Date(e.getTimestamp())));
			stringBuilder.append(" "
					+ ((EPCGeneration2Event) e.getTag()).getEpc());
			stringBuilder.append(" " + e.getReaderID());
			stringBuilder.append(" " + e.getAntennaID());
			stringBuilder.append(" " + e.getExtraInformation().get("Speed"));
			stringBuilder.append(newline);

		}
		stringBuilder.append("***" + newline + newline);
		return stringBuilder.toString();
	}

}
