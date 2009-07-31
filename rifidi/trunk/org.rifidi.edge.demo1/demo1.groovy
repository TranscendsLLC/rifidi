byte[] pass = [0x00, 0x00, 0x00, 0x00 ];
byte[] access = [0x00, 0x00, 0x00, 0x00 ];

tagSet = new HashSet();
tagSet.add(readerManager.createGen2Class1Tag((byte[])[53, -24, -98, -23, -7, -67, 93, 53, 5, 43, 58, 115],pass,access));
tagSet.add(readerManager.createGen2Class1Tag((byte[])[53, 98, -102, -105, -79, 81, 55, -29, 100, 12, 113, 118],pass,access));
tagSet.add(readerManager.createGen2Class1Tag((byte[])[53, -73, 8, 85, -64, -65, 88, 119, 7, 2, 53, 38],pass,access));
tagSet.add(readerManager.createGen2Class1Tag((byte[])[53, 85, -57, 38, -7, 101, 95, 86, 74, 102, 36, 11],pass,access));
tagSet.add(readerManager.createGen2Class1Tag((byte[])[53, 109, -86, 39, -4, 84, -111, 46, -115, -123, 54, 44],pass,access));
tagSet.add(readerManager.createGen2Class1Tag((byte[])[53, -102, -23, 53, 121, -89, -62, 60, -13, 26, 57, 40],pass,access));

tagSet2 = new HashSet();
tagSet2.add(readerManager.createGen2Class1Tag((byte[])[53, 22, 70, -107, 94, -34, -100, -98, -77, 8, 20, 92],pass,access));
tagSet2.add(readerManager.createGen2Class1Tag((byte[])[53, 81, 5, 25, -62, 71, -115, 79, 41, -62, -54, 108],pass,access));
tagSet2.add(readerManager.createGen2Class1Tag((byte[])[53, 29, -18, -63, 48, -48, 36, -55, 28, 119, 114, 7],pass,access));
tagSet2.add(readerManager.createGen2Class1Tag((byte[])[53, 15, -29, 87, 57, 93, 43, -121, 22, 75, -48, 112],pass,access));
tagSet2.add(readerManager.createGen2Class1Tag((byte[])[53, -3, -22, 72, 124, 76, -121, 0, 114, -52, -94, 55],pass,access));
tagSet2.add(readerManager.createGen2Class1Tag((byte[])[53, 85, 5, 91, 59, 122, 26, 80, -76, -111, 72, 4],pass,access));


properties = readerManager.getDefault("AlienALR9800");
properties.setReaderName("alien1");
properties.setProperty("inet_address","127.0.0.1:30000");
readerName = readerManager.createReader(properties);
readerManager.start(readerName);

Thread.start{
	while(true){
		rssi=1000;
		(1..6).each{
			tagSet.each{ i -> 
				i.setRssi(rssi);
				rssi+=300;
				i.setSpeed(0.5);
			};
			readerManager.removeTags(readerName,0,tagSet);
			readerManager.addTags(readerName,0,tagSet);
			sleep(30);
		};
		readerManager.removeTags(readerName,0,tagSet);
		sleep(10);
		(1..6).each{
			tagSet.each{ i -> 
				i.setRssi(rssi);
				rssi-=300;
				i.setSpeed(-0.5);
			};
			readerManager.removeTags(readerName,1,tagSet);
			readerManager.addTags(readerName,1,tagSet);
			sleep(30);
		};
		readerManager.removeTags(readerName,1,tagSet);
		sleep(7000);

		rssi=1000;
		(1..6).each{
			tagSet.each{ i -> 
				i.setRssi(rssi);
				rssi+=300;
				i.setSpeed(0.5);
			};
			readerManager.removeTags(readerName,1,tagSet);
			readerManager.addTags(readerName,1,tagSet);
			sleep(30);
		};
		readerManager.removeTags(readerName,1,tagSet);
		sleep(10);
		(1..6).each{
			tagSet.each{ i -> 
				i.setRssi(rssi);
				rssi-=300;
				i.setSpeed(-0.5);
			};
			readerManager.removeTags(readerName,0,tagSet);
			readerManager.addTags(readerName,0,tagSet);
			sleep(30);
		};
		readerManager.removeTags(readerName,0,tagSet);
		sleep(4000);
	};
};


properties2 = readerManager.getDefault("AlienALR9800");
properties2.setReaderName("alien2");
properties2.setProperty("inet_address","127.0.0.1:20000");
readerName2 = readerManager.createReader(properties2);
readerManager.start(readerName2);

Thread.start{
	while(true){
		rssi=1000;
		(1..6).each{
			tagSet2.each{ i -> 
				i.setRssi(rssi);
				rssi+=300;
				i.setSpeed(0.5);
			};
			readerManager.removeTags(readerName2,0,tagSet2);
			readerManager.addTags(readerName2,0,tagSet2);
			sleep(30);
		};
		readerManager.removeTags(readerName2,0,tagSet2);
		sleep(10);
		(1..6).each{
			tagSet2.each{ i -> 
				i.setRssi(rssi);
				rssi-=300;
				i.setSpeed(-0.5);
			};
			readerManager.removeTags(readerName2,1,tagSet2);
			readerManager.addTags(readerName2,1,tagSet2);
			sleep(30);
		};
		readerManager.removeTags(readerName2,1,tagSet2);
		sleep(6000);


		rssi=1000;
		(1..6).each{
			tagSet2.each{ i -> 
				i.setRssi(rssi);
				rssi+=300;
				i.setSpeed(0.5);
			};
			readerManager.removeTags(readerName2,1,tagSet2);
			readerManager.addTags(readerName2,1,tagSet2);
			sleep(30);
		};
		readerManager.removeTags(readerName2,1,tagSet2);
		sleep(10);
		(1..6).each{
			tagSet2.each{ i -> 
				i.setRssi(rssi);
				rssi-=300;
				i.setSpeed(-0.5);
			};
			readerManager.removeTags(readerName2,0,tagSet2);
			readerManager.addTags(readerName2,0,tagSet2);
			sleep(30);
		};
		readerManager.removeTags(readerName2,0,tagSet2);
		sleep(4000);
	};
};
