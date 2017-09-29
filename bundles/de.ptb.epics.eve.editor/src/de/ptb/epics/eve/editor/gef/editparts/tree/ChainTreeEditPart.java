package de.ptb.epics.eve.editor.gef.editparts.tree;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.swt.graphics.Image;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.Activator;

/**
 * @author Marcus Michalsky
 * @since 1.18
 */
public class ChainTreeEditPart extends AbstractTreeEditPart {
	
	protected List<ScanModule> getModelChildren() {
		List<ScanModule> children = new ArrayList<ScanModule>();
		if (((Chain)getModel()).getStartEvent().getConnector() != null) {
			ScanModule sm1 = ((Chain) getModel()).getStartEvent().getConnector()
					.getChildScanModule();
			children.add(sm1);
			ScanModule scanModule = sm1;
			while(scanModule.getAppended() != null) {
				ScanModule appended = scanModule.getAppended()
						.getChildScanModule();
				children.add(appended);
				scanModule = appended;
			}
		}
		return children;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getText() {
		return "Chain " + ((Chain)getModel()).getId();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Image getImage() {
		return Activator.getDefault().getImageRegistry().get("CHAIN");
	}
}