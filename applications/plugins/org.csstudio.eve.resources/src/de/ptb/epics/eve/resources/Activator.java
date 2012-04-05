package de.ptb.epics.eve.resources;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * 
 * @author Marcus Michalsky
 * @since 1.2
 */
public class Activator implements BundleActivator {

	/** */
	public static String PLUGIN_ID = "de.ptb.epics.eve.resources";
	
	private static BundleContext context;

	private static Activator plugin;
	
	/**
	 * 
	 */
	public Activator() {
		plugin = this;
	}

	/**
	 * 
	 * @return
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * 
	 * @return
	 */
	public static BundleContext getContext() {
		return context;
	}

	/**
	 * {@inheritDoc}
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
	}

	/**
	 * {@inheritDoc}
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}
	
	/**
	 * 
	 * @return
	 */
	public static File getXMLSchema() {
		
		URL url;
		try {
			url = new URL("platform:/plugin/org.csstudio.eve.product/cfg/schema.xsd");
			System.out.println(url.getPath());
			File file = new File(FileLocator.toFileURL(url).toURI());
			System.out.println(file.getPath());
			return file;
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		Bundle bundle = Platform.getBundle(PLUGIN_ID);

		URL fileUrl = FileLocator.find(bundle, new Path("cfg/schema.xsd"), null);
		
		try {
			return new File(FileLocator.toFileURL(fileUrl).toURI());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}