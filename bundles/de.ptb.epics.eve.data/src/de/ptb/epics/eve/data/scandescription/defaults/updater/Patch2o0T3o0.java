package de.ptb.epics.eve.data.scandescription.defaults.updater;

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
 * Patches SCML v2.0 to v3.0 doing the following:
 * 
 * <ul>
 * <li>increment version to 3.0</li>
 * </ul> 
 * 
 * @author Marcus Michalsky
 * @since 1.28
 */
public class Patch2o0T3o0 extends Patch {
	private static Patch2o0T3o0 INSTANCE;
	
	private Patch2o0T3o0(Version source, Version target, List<Modification> modifications) {
		super(source, target, modifications);
		modifications.add(new Mod0(this));
	}

	public static Patch2o0T3o0 getInstance() {
		if (INSTANCE == null) {
			List<Modification> modifications = new LinkedList<Modification>();
			INSTANCE = new Patch2o0T3o0(new Version(2,0), 
					new Version(3,0), modifications);
		}
		return INSTANCE;
	}
	
	private class Mod0 extends AbstractModification {
		public Mod0(Patch patch) {
			super(patch, "setting defaults version to 3.0");
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modify(Document document) {
			Node version = document.getElementsByTagName("version").item(0);
			if (version.getNodeType() == Node.ELEMENT_NODE) {
				((Text)version.getFirstChild()).setData("3.0");
			}
		}
	}
}