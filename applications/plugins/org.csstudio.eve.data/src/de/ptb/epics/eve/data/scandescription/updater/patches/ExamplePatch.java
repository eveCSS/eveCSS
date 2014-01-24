package de.ptb.epics.eve.data.scandescription.updater.patches;

import java.util.LinkedList;
import java.util.List;

import de.ptb.epics.eve.data.scandescription.updater.Modification;
import de.ptb.epics.eve.data.scandescription.updater.Patch;
import de.ptb.epics.eve.util.data.Version;

/**
 * @author Marcus Michalsky
 * @since 1.18
 */
public class ExamplePatch extends Patch {

	private ExamplePatch(Version source, Version target,
			List<Modification> modifications) {
		// modifications.add(...);
		super(source, target, modifications);
	}
	
	public static Patch getInstance() {
		return new ExamplePatch(new Version(1, 0), new Version(1, 1),
				new LinkedList<Modification>());
	}
}