package de.ptb.epics.eve.editor.gef.editparts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import de.ptb.epics.eve.data.scandescription.StartEvent;
import de.ptb.epics.eve.editor.gef.figures.StartEventFigure;

/**
 * @author Marcus Michalsky
 * @since 1.6
 */
public class StartEventEditPart extends AbstractGraphicalEditPart implements
		NodeEditPart {

	/**
	 * Constructor.
	 * 
	 * @param event the model element to set
	 */
	public StartEventEditPart(StartEvent event) {
		this.setModel(event);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure createFigure() {
		return new StartEventFigure(this.getModel().getEvent().getID());
	}

	/**
	 * Returns the model element.
	 */
	public StartEvent getModel() {
		return (StartEvent)super.getModel();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<?> getModelSourceConnections() {
		final List<Object> sourceList = new ArrayList<Object>();
		final StartEvent startEvent = (StartEvent)this.getModel();
		
		if(startEvent.getConnector() != null) {
			sourceList.add(startEvent.getConnector());
		}
		return sourceList;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createEditPolicies() {
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConnectionAnchor getSourceConnectionAnchor(
									final ConnectionEditPart connection) {
		return ((StartEventFigure)this.figure).getSourceAnchor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConnectionAnchor getSourceConnectionAnchor(final Request request) {
		return ((StartEventFigure)this.figure).getSourceAnchor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConnectionAnchor getTargetConnectionAnchor(
										final ConnectionEditPart connection) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConnectionAnchor getTargetConnectionAnchor(final Request request) {
		return null;
	}
}