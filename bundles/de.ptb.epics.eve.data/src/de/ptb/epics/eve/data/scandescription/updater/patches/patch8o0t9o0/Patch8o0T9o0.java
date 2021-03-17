package de.ptb.epics.eve.data.scandescription.updater.patches.patch8o0t9o0;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import de.ptb.epics.eve.data.scandescription.processors.Literals;
import de.ptb.epics.eve.data.scandescription.updater.AbstractModification;
import de.ptb.epics.eve.data.scandescription.updater.Modification;
import de.ptb.epics.eve.data.scandescription.updater.Patch;
import de.ptb.epics.eve.util.data.Version;

import static de.ptb.epics.eve.util.xml.XMLUtil.*;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public class Patch8o0T9o0 extends Patch {
	private static final Logger LOGGER = Logger.getLogger(
			Patch8o0T9o0.class.getName());
	private static Patch8o0T9o0 INSTANCE;
	
	// contains pause events of found chains
	private Map<Integer, List<PauseEvent>> chainEventMap;
	// first hash is the chain and second hash is scan module (id) and its events
	private Map<Integer, Map<Integer, List<PauseEvent>>> smEventMap;
	private List<PseudoPauseCondition> pauseConditions;
	
	private Patch8o0T9o0(Version source, Version target, 
			List<Modification> modifications) {
		super(source, target, modifications);
		modifications.add(new Mod0(this));
		modifications.add(new Mod1(this));
		modifications.add(new Mod2(this));
		modifications.add(new Mod3(this));
		modifications.add(new Mod4(this));
		modifications.add(new Mod5(this));
		modifications.add(new Mod6(this));
	}
	
	public static Patch8o0T9o0 getInstance() {
		if (INSTANCE == null) {
			List<Modification> modifications = new LinkedList<>();
			INSTANCE = new Patch8o0T9o0(new Version(8,0), new Version(9,0), 
					modifications);
		}
		return INSTANCE;
	}
	
	private class Mod0 extends AbstractModification {
		public Mod0(Patch patch) {
			super(patch, "setting scml version to 9.0");
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modify(Document document) {
			Node version = document.getElementsByTagName("version").item(0);
			if (version.getNodeType() == Node.ELEMENT_NODE) {
				((Text) version.getFirstChild()).setData("9.0");
			}
		}
	}
	
	private class Mod1 extends AbstractModification {
		public Mod1(Patch patch) {
			super(patch, "changing motor stop attribute type from OnOff to int");
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modify(Document document) {
			NodeList motorNodes = document.getElementsByTagName(
					Literals.XML_ELEMENT_NAME_MOTOR);
			for (Node motorNode : asList(motorNodes)) {
				for (Node axisNode : asList(motorNode.getChildNodes())) {
					if (axisNode.getNodeType() != Node.ELEMENT_NODE) {
						continue;
					}
					if (axisNode.getNodeName().equals(Literals.XML_ELEMENT_NAME_STOP)) {
						for (Node stopNode : asList(axisNode.getChildNodes())) {
							if (stopNode.getNodeName().equals(
									Literals.XML_ELEMENT_NAME_VALUE)) {
								stopNode.getAttributes().getNamedItem(
									Literals.XML_ATTRIBUTE_NAME_TYPE).
										setNodeValue("int");
							}
						}
					}
				}
			}
		}
	}
	
	private class Mod2 extends AbstractModification {
		public Mod2(Patch patch) {
			super(patch, "collecting pause events from chain(s) and scanmodules");
			chainEventMap = new HashMap<>();
			smEventMap = new HashMap<>();
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modify(Document document) {
			NodeList chainNodes = document.getElementsByTagName(
					Literals.XML_ELEMENT_NAME_CHAIN);
			for (Node chainNode : asList(chainNodes)) {
				int chainId = Integer.parseInt(chainNode.getAttributes().
						getNamedItem(Literals.XML_ATTRIBUTE_NAME_ID).
						getNodeValue());
				List<PauseEvent> chainPauseEvents = new ArrayList<>();
				Node scanmodulesNode = null;
				for (Node child : asList(chainNode.getChildNodes())) {
					if (child.getNodeType() != Node.ELEMENT_NODE) { continue; }
					if (child.getNodeName().equals(Literals.XML_ELEMENT_NAME_PAUSEEVENT)) {
						PauseEvent pauseEvent = PauseEvent.readPauseEvent(child);
						LOGGER.debug("found Chain (id: " + chainId + ") - " + 
								pauseEvent.toString());
						chainPauseEvents.add(pauseEvent);
					} else if (child.getNodeName().equals(Literals.XML_ELEMENT_NAME_SCANMODULES)) {
						scanmodulesNode = child;
					}
				}
				chainEventMap.put(chainId, chainPauseEvents);
				
				if (scanmodulesNode == null) {
					LOGGER.error("scanmodules element of chain " + chainId +
							"not found");
					return;
				}
				
				// iterate scanmodules of chain and collect pause events
				Map<Integer, List<PauseEvent>> chainSMEventsMap = new HashMap<>();
				NodeList smNodes = ((Element)scanmodulesNode).
					getElementsByTagName(Literals.XML_ELEMENT_NAME_SCANMODULE);
				for (Node child : asList(smNodes)) {
					int smId = Integer.parseInt(child.getAttributes().
							getNamedItem(Literals.XML_ATTRIBUTE_NAME_ID).
							getNodeValue());
					List<PauseEvent> smPauseEvents = new ArrayList<>();
					NodeList smPauseEventsList = ((Element)child).
						getElementsByTagName(Literals.XML_ELEMENT_NAME_PAUSEEVENT);
					for (Node pauseChild : asList(smPauseEventsList)) {
						smPauseEvents.add(PauseEvent.readPauseEvent(pauseChild));
					}
					chainSMEventsMap.put(smId, smPauseEvents);
				}
				smEventMap.put(chainId, chainSMEventsMap);
			}
		}
	}
	
	private class Mod3 extends AbstractModification {
		public Mod3(Patch patch) {
			super(patch, "combining events/removing duplicates");
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modify(Document document) {
			pauseConditions = new ArrayList<>();
			// TODO Auto-generated method stub
			
		}
	}
	
	private class Mod4 extends AbstractModification {
		public Mod4(Patch patch) {
			super(patch, "removing chain/scanmodule pause events");
		}
		
		public void modify(Document document) {
			NodeList chainNodes = document.getElementsByTagName(
					Literals.XML_ELEMENT_NAME_CHAIN);
			for (Node chainNode : asList(chainNodes)) {
				for (Node child : asList(chainNode.getChildNodes())) {
					if (child.getNodeType() != Node.ELEMENT_NODE) { continue; }
					if (child.getNodeName().equals(Literals.XML_ELEMENT_NAME_PAUSEEVENT)) {
						chainNode.removeChild(child);
					}
				}
			}
			NodeList smNodes = document.getElementsByTagName(
					Literals.XML_ELEMENT_NAME_SCANMODULE_CLASSIC);
			for (Node classicNode : asList(smNodes)) {
				for (Node child : asList(classicNode.getChildNodes())) {
					if (child.getNodeType() != Node.ELEMENT_NODE) { continue; }
					if (child.getNodeName().equals(Literals.XML_ELEMENT_NAME_PAUSEEVENT)) {
						classicNode.removeChild(child);
					}
				}
			}
		}
	}
	
	private class Mod5 extends AbstractModification {
		public Mod5(Patch patch) {
			super(patch, "create pause condition element in chain(s)");
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modify(Document document) {
			NodeList chainNodes = document.getElementsByTagName(
					Literals.XML_ELEMENT_NAME_CHAIN);
			Node scanmodulesNode = null;
			for (Node chainNode : asList(chainNodes)) {
				for (Node chainChild : asList(chainNode.getChildNodes())) {
					if (chainChild.getNodeName().equals(
							Literals.XML_ELEMENT_NAME_SCANMODULES)) {
						scanmodulesNode = chainChild;
					}
				}
				if (scanmodulesNode == null) {
					LOGGER.error("scanmodules element of chain " +
						chainNode.getAttributes().getNamedItem(
								Literals.XML_ATTRIBUTE_NAME_ID) + 
						"not found");
					return;
				}
				Element pauseconditionsElement = document.createElement(
						Literals.XML_ELEMENT_NAME_PAUSECONDITIONS);
				chainNode.insertBefore(pauseconditionsElement, scanmodulesNode);
			}
		}
	}
	
	private class Mod6 extends AbstractModification {
		public Mod6(Patch patch) {
			super(patch, "Insert pause conditions (derived from collected events");
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modify(Document document) {
			// TODO Auto-generated method stub
			
		}
	}
}
