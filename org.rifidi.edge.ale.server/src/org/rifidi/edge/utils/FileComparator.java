package org.rifidi.edge.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;

public class FileComparator implements Comparator<File> {

	public int compare(File f0, File f1) {
		Path path1 = Paths.get(f0.getAbsolutePath());
		Path path2 = Paths.get(f1.getAbsolutePath());
		try {
			BasicFileAttributes attr1 = Files.readAttributes(path1, BasicFileAttributes.class);
			BasicFileAttributes attr2 = Files.readAttributes(path2, BasicFileAttributes.class);
			
			long date1 = attr1.creationTime().toMillis();
			long date2 = attr2.creationTime().toMillis();
//			System.out.println("file1: " + path1 + "; creation time: " + attr1.creationTime());
//			System.out.println("file2: " + path2 + "; creation time: " + attr2.creationTime());
			
			if (date1 > date2)
				return 1;
			else if (date2 > date1)
				return -1;

			return 0;
		} catch (IOException e){
			throw new RuntimeException(e);
		}
		
		

		
	}
}