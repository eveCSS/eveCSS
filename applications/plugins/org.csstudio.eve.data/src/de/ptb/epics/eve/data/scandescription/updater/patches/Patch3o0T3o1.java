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
 * @author Marcus Michalsky
 * @since 1.22
 */
public class Patch3o0T3o1 extends Patch {
	private static Patch3o0T3o1 INSTANCE;

	private Patch3o0T3o1(Version source, Version target,
			List<Modification> modifications) {
		super(source, target, modifications);
		modifications.add(new Mod0(this));
	}
	
	public static Patch3o0T3o1 getInstance() {
		if (INSTANCE == null) {
			List<Modification> modifications = new LinkedList<Modification>();
			
			INSTANCE = new Patch3o0T3o1(new Version(3, 0), new Version(3, 1), 
				modifications);
		}
		return INSTANCE;
	}

	private class Mod0 extends AbstractModification {
		public Mod0(Patch patch) {
			super(patch, "setting scml version to 3.1");
		}
		
		/**
		 * {@inheritDoc}
		 * <p>
		 * Incrementing version to 3.1.
		 */
		@Override
		public void modify(Document document) {
			Node version = document.getElementsByTagName("version").item(0);
			if (version.getNodeType() == Node.ELEMENT_NODE) {
				((Text)version.getFirstChild()).setData("3.1");
			}
		}
	}
}