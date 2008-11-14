package org.fosstrak.tdt.activator;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Enumeration;

import org.fosstrak.tdt.fileservice.FileService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.xml.sax.InputSource;

public class Activator implements BundleActivator {

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		FileService fileService = new FileService(context.getBundle());
		context.registerService(FileService.class.getName(), fileService, null);
/*		
		
		Enumeration url = context.getBundle().getEntryPaths("/data/schemes");
		if(url!=null){
			while(url.hasMoreElements()){
				URL file = context.getBundle().getEntry(url.nextElement().toString());
				try{
					Reader r = new InputStreamReader(file.openStream());
					if(r!=null){
						StringBuffer sb = new StringBuffer();
						char ch = (char)r.read();
						int i=0;
						while(ch!=(char)-1){
							sb.append(ch);
							ch = (char)r.read();
							i++;
						}
						System.out.println("read " + i + " characters");
						//System.out.println(sb.toString());
					}else{
						System.out.println("reader is null");
					}
				}catch(Exception e){
					//e.printStackTrace();
				}
			}
		}else{
			System.out.println("url is null");
		}*/
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	}

}
