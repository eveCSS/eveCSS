package de.ptb.epics.eve.data.scandescription.updater.patches.patch8o0t9o0;

import static de.ptb.epics.eve.util.xml.XMLUtil.asList;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import de.ptb.epics.eve.data.ComparisonTypes;
import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.scandescription.Limit;
import de.ptb.epics.eve.data.scandescription.processors.Literals;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
class PauseEvent {
	private static final Logger LOGGER = Logger.getLogger(
			PauseEvent.class.getName());
	
	private final String id;
	private final Limit limit;
	private final EventAction action;
	
	public PauseEvent(String id, Limit limit, EventAction action) {
		this.id = id;
		this.limit = limit;
		this.action = action;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the limit
	 */
	public Limit getLimit() {
		return limit;
	}

	/**
	 * @return the action
	 */
	public EventAction getAction() {
		return action;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (other.getClass() != getClass()) {
			return false;
		}
		if (!this.id.equals(((PauseEvent)other).getId())) {
			return false;
		}
		if (!this.limit.equals(((PauseEvent)other).getLimit())) {
			return false;
		}
		if (!this.action.equals(((PauseEvent)other).action)) {
			return false;
		}
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + limit.hashCode();
		result = 31 * result + action.hashCode();
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Pause Event (" + id + ", " + 
	limit.getComparison().toString() + " " + 
				limit.getValue() + ", " + action.toString() + ")";
	}
	
	protected static PauseEvent readPauseEvent(Node pauseEventNode) {
		Node monitoreventNode = null;
		Node actionNode = null;
		for (Node child : asList(pauseEventNode.getChildNodes())) {
			if (child.getNodeType() != Node.ELEMENT_NODE) { continue; }
			if (child.getNodeName().equals(Literals.XML_ELEMENT_NAME_MONITOREVENT)) {
				monitoreventNode = child;
			} else if (child.getNodeName().equals(Literals.XML_ELEMENT_NAME_ACTION)) {
				actionNode = child;
			}
		}
		if (monitoreventNode == null || actionNode == null) {
			LOGGER.error("no monitorevent definition found");
			return null;
		}
		Node idNode = null;
		Node limitNode = null;
		for (Node child : asList(monitoreventNode.getChildNodes())) {
			if (child.getNodeType() != Node.ELEMENT_NODE) { continue; }
			if (child.getNodeName().equals(Literals.XML_ELEMENT_NAME_ID)) {
				idNode = child;
			} else if (child.getNodeName().equals(Literals.XML_ELEMENT_NAME_LIMIT)) {
				limitNode = child;
			}
		}
		if (idNode == null || limitNode == null) {
			LOGGER.error("id or limit node not found");
			return null;
		}
		String type = limitNode.getAttributes().getNamedItem(
				Literals.XML_ATTRIBUTE_NAME_TYPE).getNodeValue();
		String comparison = limitNode.getAttributes().getNamedItem(
				Literals.XML_ATTRIBUTE_NAME_COMPARISON).getNodeValue();
		
		Limit limit = new Limit(DataTypes.stringToType(type), 
				ComparisonTypes.stringToType(comparison));
		limit.setValue(limitNode.getFirstChild().getNodeValue());
		
		return new PauseEvent(idNode.getFirstChild().getNodeValue(), 
				limit, 
				EventAction.stringToType(actionNode.getFirstChild().getNodeValue()));
	}
}
