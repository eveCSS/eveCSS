package de.ptb.epics.eve.editor.gef.editparts.tree;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.swt.graphics.Image;

import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.Activator;

/**
 * @author Marcus Michalsky
 * @since 1.18
 */
public class ScanModuleTreeEditPart extends AbstractTreeEditPart {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<ScanModule> getModelChildren() {
		List<ScanModule> children = new ArrayList<ScanModule>();
		if (((ScanModule)getModel()).getNested() != null) {
			ScanModule sm1 = ((ScanModule)getModel()).getNested().
					getChildScanModule();
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
		switch (((ScanModule)getModel()).getType()) {
		case CLASSIC:
			return ((ScanModule)getModel()).getName();
		case SAVE_AXIS_POSITIONS:
			return "Save Axis Positions";
		case SAVE_CHANNEL_VALUES:
			return "Save Channel Values";
		default:
			return ((ScanModule)getModel()).getName();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Image getImage() {
		switch (((ScanModule) getModel()).getType()) {
		case CLASSIC:
			return Activator.getDefault().getImageRegistry().get("SCANMODULE");
		case SAVE_AXIS_POSITIONS:
			return Activator.getDefault().getImageRegistry().get("AXIS");
		case SAVE_CHANNEL_VALUES:
			return Activator.getDefault().getImageRegistry().get("CHANNEL");
		default:
			return Activator.getDefault().getImageRegistry().get("SCANMODULE");
		}
	}
}