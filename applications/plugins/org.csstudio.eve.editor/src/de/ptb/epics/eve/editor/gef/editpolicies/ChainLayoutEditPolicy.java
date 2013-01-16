package de.ptb.epics.eve.editor.gef.editpolicies;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;
import de.ptb.epics.eve.editor.gef.commands.CreateScanModule;
import de.ptb.epics.eve.editor.gef.commands.MoveScanModule;
import de.ptb.epics.eve.editor.gef.editparts.ScanModuleEditPart;

/**
 * @author Marcus Michalsky
 * @since 1.6
 */
public class ChainLayoutEditPolicy extends XYLayoutEditPolicy {

	private static Logger logger = Logger.getLogger(ChainLayoutEditPolicy.class
			.getName());

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command createChangeConstraintCommand(
			ChangeBoundsRequest request, EditPart child, Object constraint) {
		logger.debug("createChangeConstraintCommand");
		if (child instanceof ScanModuleEditPart
				&& constraint instanceof Rectangle) {
			return new MoveScanModule((ScanModule) child.getModel(),
					(Rectangle) constraint, request);
		}
		return super.createChangeConstraintCommand(request, child, constraint);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command createChangeConstraintCommand(EditPart child,
			Object constraint) {
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getCreateCommand(CreateRequest request) {
		if (request.getNewObjectType().equals(ScanModuleTypes.CLASSIC)) {
			return new CreateScanModule((Chain) this.getHost().getModel(),
					(Rectangle) this.getConstraintFor(request), 
					ScanModuleTypes.CLASSIC);
		} else if (request.getNewObjectType().equals(ScanModuleTypes.SAVE_AXIS_POSITIONS)) {
			return new CreateScanModule((Chain) this.getHost().getModel(),
					(Rectangle) this.getConstraintFor(request), 
					ScanModuleTypes.SAVE_AXIS_POSITIONS);
		} else if (request.getNewObjectType().equals(ScanModuleTypes.SAVE_CHANNEL_VALUES)) {
			return new CreateScanModule((Chain) this.getHost().getModel(),
					(Rectangle) this.getConstraintFor(request), 
					ScanModuleTypes.SAVE_CHANNEL_VALUES);
		}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected EditPolicy createChildEditPolicy(EditPart child) {
		if (child.getModel() instanceof ScanModule) {
			return new NonResizableEditPolicy();
		}
		return super.createChildEditPolicy(child);
	}
}