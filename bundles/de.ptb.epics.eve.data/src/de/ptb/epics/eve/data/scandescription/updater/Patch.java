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
	 * @param source the source version of the patch
	 * @param target the target version of the patch
	 * @param modifications a list of modifications
	 */
	protected Patch(Version source, Version target,
			List<Modification> modifications) {
		this.sourceVersion = source;
		this.targetVersion = target;
		this.modifications = modifications;
	}
	
	/**
	 * Subsequently applies all modifications to the given document.
	 * 
	 * @param document the document the patch should be applied to
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
	
	/**
	 * @return a list of modifications executed by this patch
	 */
	public List<Modification> getModifications() {
		return this.modifications;
	}
	
	@Override
	public String toString() {
		return "Patch " + this.sourceVersion + " -> " + this.targetVersion;
	}
}