package com.csc.rfid.toolcrib.test.esper;

public class StartCollectionEvent {
	
	private CollectionType collectionType;

	public StartCollectionEvent(CollectionType collectionType) {
		this.collectionType = collectionType;
	}

	public CollectionType getCollectionType() {
		return collectionType;
	}
	
}
