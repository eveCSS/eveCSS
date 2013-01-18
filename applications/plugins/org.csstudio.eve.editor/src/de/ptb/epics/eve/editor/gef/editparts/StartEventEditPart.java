package de.ptb.epics.eve.editor.gef.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import de.ptb.epics.eve.data.scandescription.StartEvent;
import de.ptb.epics.eve.editor.gef.editpolicies.StartEventGraphicalNodeEditPolicy;
import de.ptb.epics.eve.editor.gef.figures.StartEventFigure;

/**
 * @author Marcus Michalsky
 * @since 1.6
 */
public class StartEventEditPart extends AbstractGraphicalEditPart implements
		NodeEditPart, PropertyChangeListener {

	private static Logger logger = Logger.getLogger(StartEventEditPart.class
			.getName());
	
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
	public void activate() {
		this.getModel().addPropertyChangeListener(StartEvent.X_PROP, this);
		this.getModel().addPropertyChangeListener(StartEvent.Y_PROP, this);
		this.getModel()
				.addPropertyChangeListener(StartEvent.CONNECT_PROP, this);
		super.activate();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deactivate() {
		this.getModel().removePropertyChangeListener(StartEvent.X_PROP, this);
		this.getModel().removePropertyChangeListener(StartEvent.Y_PROP, this);
		this.getModel().removePropertyChangeListener(StartEvent.CONNECT_PROP,
				this);
		super.deactivate();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure createFigure() {
		return new StartEventFigure(this.getModel().getEvent().getID(), 
				this.getModel().getX(), this.getModel().getY());
	}

	/**
	 * Returns the model element.
	 * 
	 * @return the model element
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
		final StartEvent startEvent = this.getModel();
		
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
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new StartEventGraphicalNodeEditPolicy());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConnectionAnchor getSourceConnectionAnchor(
									final ConnectionEditPart connection) {
		logger.debug("get source conn anchors");
		return ((StartEventFigure)this.getFigure()).getSourceAnchor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConnectionAnchor getSourceConnectionAnchor(final Request request) {
		logger.debug("get source conn anchors req");
		return ((StartEventFigure)this.getFigure()).getSourceAnchor();
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals(StartEvent.X_PROP)) {
			((StartEventFigure)this.getFigure()).setX((Integer)e.getNewValue());
		} else if (e.getPropertyName().equals(StartEvent.Y_PROP)) {
			((StartEventFigure)this.getFigure()).setY((Integer)e.getNewValue());
		} else if (e.getPropertyName().equals(StartEvent.CONNECT_PROP)) {
			this.refreshSourceConnections();
		}
		this.refreshVisuals();
	}
}