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
 * @since 1.18
 */
public class ExamplePatch extends Patch {

	private static ExamplePatch INSTANCE;
	
	private ExamplePatch(Version source, Version target, 
			List<Modification> modifications) {
		super(source, target, modifications);
		modifications.add(new Mod0(this));
		modifications.add(new Mod1(this));
	}

	public static ExamplePatch getInstance() {
		if (INSTANCE == null) {
			List<Modification> modifications = new LinkedList<Modification>();

			INSTANCE = new ExamplePatch(new Version(2, 3), new Version(3, 0),
					modifications);
		}
		return INSTANCE;
	}
	
	private class Mod0 extends AbstractModification {

		public Mod0(Patch patch) {
			super(patch, "setting scml version to 3.0");
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
	
	private class Mod1 extends AbstractModification {
		
		public Mod1(Patch patch) {
			super(patch, "some modification text");
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modify(Document document) {
			// modifications
		}
	}
}