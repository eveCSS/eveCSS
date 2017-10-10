package de.ptb.epics.eve.data.scandescription.defaults.updater;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import de.ptb.epics.eve.data.scandescription.updater.AbstractModification;
import de.ptb.epics.eve.data.scandescription.updater.Modification;
import de.ptb.epics.eve.data.scandescription.updater.Patch;
import de.ptb.epics.eve.util.data.Version;

/**
 * Patches Defaults v1.0 to v1.1 doing the following:
 * 
 * <ul>
 * <li>inserting version element (as first child)</li>
 * <li>set version to 1.1</li>
 * </ul>
 * 
 * @author Marcus Michalsky
 * @since 1.27
 */
public class Patch1o0T1o1 extends Patch {
	private static Patch1o0T1o1 INSTANCE;
	
	private Patch1o0T1o1(Version source, Version target, 
			List<Modification> modifications) {
		super(source, target, modifications);
		modifications.add(new Mod0(this));
	}
	
	public static Patch1o0T1o1 getInstance() {
		if (INSTANCE == null) {
			List<Modification> modifications = new LinkedList<Modification>();
			
			INSTANCE = new Patch1o0T1o1(new Version(1,0), new Version(1,1), modifications);
		}
		return INSTANCE;
	}
	
	private class Mod0 extends AbstractModification {
		public Mod0(Patch patch) {
			super(patch, "inserting version element with value 1.1");
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modify(Document document) {
			Node defaults = document.getDocumentElement();
			Element versionElement = document.createElement("version");
			Text text = document.createTextNode("1.1");
			versionElement.appendChild(text);
			defaults.insertBefore(versionElement, defaults.getFirstChild());
		}
	}
}