package de.ptb.epics.eve.data.scandescription.updater;

import java.util.List;

import org.w3c.dom.Document;

import de.ptb.epics.eve.util.data.Version;

/**
 * @author Marcus Michalsky
 * @since 1.18
 */
public class Patch {
	private final Version sourceVersion;
	private final Version targetVersion;
	
	private final List<Modification> modifications;
	
	/**
	 * Constructor.
	 * 
	 * @param source 
	 * @param target 
	 * @param modifications
	 */
	public Patch(Version source, Version target,
			List<Modification> modifications) {
		this.sourceVersion = source;
		this.targetVersion = target;
		this.modifications = modifications;
	}
	
	/**
	 * 
	 * @param document
	 */
	public void execute(Document document) {
		for (Modification mod : this.modifications) {
			mod.modify(document);
		}
	}

	/**
	 * @return the sourceVersion
	 */
	public Version getSourceVersion() {
		return sourceVersion;
	}

	/**
	 * @return the targetVersion
	 */
	public Version getTargetVersion() {
		return targetVersion;
	}
}