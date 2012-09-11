package de.ptb.epics.eve.editor.gef.editpolicies;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
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
	
	private RectangleFigure fig;
	
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
		return null; //new RenameScanModule();
	}
	/*
	@Override
	protected void showLayoutTargetFeedback(Request request) {
		logger.debug("Request: " + request.getType());
		logger.debug(request.getClass().getName());
		if (request instanceof ChangeBoundsRequest) {
			fig = new RectangleFigure();
			fig.setLocation(((ChangeBoundsRequest)request).getLocation());
			fig.setSize(10, 10);
			addFeedback(fig);
		}
		// TODO Auto-generated method stub
		super.showLayoutTargetFeedback(request);
	}
	
	@Override
	protected void eraseLayoutTargetFeedback(Request request) {
		if (request instanceof ChangeBoundsRequest) {
			removeFeedback(fig);
		}
		super.eraseLayoutTargetFeedback(request);
	}
	
	@Override
	protected void eraseSizeOnDropFeedback(Request request) {
		removeFeedback(fig);
		// TODO Auto-generated method stub
		super.eraseSizeOnDropFeedback(request);
	}*/
}