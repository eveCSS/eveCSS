package de.ptb.epics.eve.data.scandescription.updater.patches;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import de.ptb.epics.eve.data.scandescription.updater.AbstractModification;
import de.ptb.epics.eve.data.scandescription.updater.Modification;
import de.ptb.epics.eve.data.scandescription.updater.Patch;
import de.ptb.epics.eve.util.data.Version;

/**
 * Patches SCML v3.1 to v3.2 doing the following:
 * 
 * <ul>
 * <li>increment version to 3.2</li>
 * </ul>
 * 
 * @author Marcus Michalsky
 * @since 1.23
 */
public class Patch3o1T3o2 extends Patch {
	private static Patch3o1T3o2 INSTANCE;
	
	private Patch3o1T3o2(Version source, Version target, 
			List<Modification> modifications) {
		super(source, target, modifications);
		modifications.add(new Mod0(this));
	}
	
	public static Patch3o1T3o2 getInstance() {
		if (INSTANCE == null) {
			List<Modification> modifications = new LinkedList<Modification>();
			
			INSTANCE = new Patch3o1T3o2(new Version(3, 1), new Version(3, 2),
					modifications);
		}
		return INSTANCE;
	}

	private class Mod0 extends AbstractModification {
		public Mod0(Patch patch) {
			super(patch, "setting scml version to 3.2");
		}
		
		/**
		 * {@inheritDoc}
		 * <p>
		 * Incrementing version to 3.2.
		 */
		@Override
		public void modify(Document document) {
			Node version = document.getElementsByTagName("version").item(0);
			if (version.getNodeType() == Node.ELEMENT_NODE) {
				((Text)version.getFirstChild()).setData("3.2");
			}
		}
	}
}