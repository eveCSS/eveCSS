package de.ptb.epics.eve.editor.gef;

import java.util.LinkedList;
import java.util.List;

import de.ptb.epics.eve.data.scandescription.Connector;
import de.ptb.epics.eve.data.scandescription.ScanModule;

/**
 * Class wrapper for GEF clipboard content.
 * 
 * @author Marcus Michalsky
 * @since 1.19
 */
public class ClipboardContent {
	private List<ScanModule> scanModules;
	private List<Connector> connections;
	
	/**
	 * Constructor.
	 */
	public ClipboardContent() {
		this.scanModules = new LinkedList<ScanModule>();
		this.connections = new LinkedList<Connector>();
	}
	
	/**
	 * @return the scanModules
	 */
	public List<ScanModule> getScanModules() {
		return scanModules;
	}
	/**
	 * @param scanModules the scanModules to set
	 */
	public void setScanModules(List<ScanModule> scanModules) {
		this.scanModules = scanModules;
	}
	/**
	 * @return the connections
	 */
	public List<Connector> getConnections() {
		return connections;
	}
	/**
	 * @param connections the connections to set
	 */
	public void setConnections(List<Connector> connections) {
		this.connections = connections;
	}
}