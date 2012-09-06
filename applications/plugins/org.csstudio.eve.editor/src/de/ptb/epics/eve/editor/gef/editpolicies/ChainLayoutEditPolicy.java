/**
 * 
 */
package de.ptb.epics.eve.editor.gef.editpolicies;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.LayoutEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.gef.commands.MoveScanModule;
import de.ptb.epics.eve.editor.gef.editparts.ScanModuleEditPart;

/**
 * @author mmichals
 *
 */
public class ChainLayoutEditPolicy extends XYLayoutEditPolicy {

	private static Logger logger = Logger.getLogger(ChainLayoutEditPolicy.class
			.getName());
	
	public ChainLayoutEditPolicy() {
		super();
		logger.debug("construct");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command createChangeConstraintCommand(
			ChangeBoundsRequest request, EditPart child, Object constraint) {
		logger.debug("createChangeConstraintCommand");
		if (child instanceof ScanModuleEditPart && constraint instanceof Rectangle) {
			return new MoveScanModule((ScanModule)child.getModel(), (Rectangle)constraint);
		}
		return super.createChangeConstraintCommand(request, child, constraint);
	}
	
	@Override
	protected Command createChangeConstraintCommand(EditPart child,
			Object constraint) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected Command getCreateCommand(CreateRequest arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Command getCommand(Request request) {
		// TODO Auto-generated method stub
		logger.debug("getCommand, Request: " + request.getType().toString());
		return super.getCommand(request);
	}
}