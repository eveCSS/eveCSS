package de.ptb.epics.eve.editor.gef.editparts.tree;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.swt.graphics.Image;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.Stepfunctions;
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
		StringBuffer s = new StringBuffer();
		switch (((ScanModule)getModel()).getType()) {
		case CLASSIC:
			s.append(((ScanModule)getModel()).getName());
			if (((ScanModule) getModel()).getAxes().length == 1
					&& ((ScanModule) getModel()).getAxes()[0].getStepfunction()
							.equals(Stepfunctions.ADD)) {
				Axis axis = ((ScanModule) getModel()).getAxes()[0];
				s.append(" (");
				s.append(axis.getMotorAxis().getName());
				s.append(": ");
				s.append(axis.getStart());
				s.append(" \u2192 ");
				s.append(axis.getStop());
				s.append(", Step: ");
				s.append(axis.getStepwidth());
				if (((ScanModule) getModel()).getChannels().length == 1) {
					Channel channel = ((ScanModule) getModel()).getChannels()[0];
					s.append(", ");
					s.append(channel.getDetectorChannel().getName());
					s.append(", ");
					s.append("Average: ");
					s.append(channel.getAverageCount());
				}
				
				s.append(")");
			}
			break;
		case SAVE_AXIS_POSITIONS:
			s.append("Save Axis Positions");
			break;
		case SAVE_CHANNEL_VALUES:
			s.append("Save Channel Values");
			break;
		default:
			s.append(((ScanModule)getModel()).getName());
			break;
		}
		return s.toString();
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