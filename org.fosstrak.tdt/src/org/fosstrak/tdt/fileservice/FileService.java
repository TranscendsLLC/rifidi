package org.fosstrak.tdt.fileservice;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;

import org.osgi.framework.Bundle;

public class FileService {

	private Bundle _bundle;

	public FileService(Bundle bundle) {
		_bundle = bundle;
	}

	public URL getFile(String relativePath) throws FileNotFoundException {
		URL url = _bundle.getResource(relativePath);
		if (null != url) {
			String fileName = url.getFile();
			if (!fileName.equals("")) {
				return url;
			} else {
				throw new FileNotFoundException("File " + relativePath
						+ " cannot be found");
			}
		} else {
			throw new FileNotFoundException("File " + relativePath
					+ " cannot be found");
		}
	}
	
	public ArrayList<URL> getFiles(String path){
		URL url = _bundle.getResource(path);
		ArrayList<URL> files= new ArrayList<URL>();
		if(null!=url){
			Enumeration entries = _bundle.getEntryPaths(path);
			while(entries.hasMoreElements()){
				URL file = _bundle.getEntry(entries.nextElement().toString());
				files.add(file);
			}
		}
		return files;
	}

}
