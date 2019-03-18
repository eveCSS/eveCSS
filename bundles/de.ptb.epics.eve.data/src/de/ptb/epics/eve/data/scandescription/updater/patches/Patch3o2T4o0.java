package de.ptb.epics.eve.data.scandescription.updater.patches;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import de.ptb.epics.eve.data.EventTypes;
import de.ptb.epics.eve.data.scandescription.updater.AbstractModification;
import de.ptb.epics.eve.data.scandescription.updater.Modification;
import de.ptb.epics.eve.data.scandescription.updater.Patch;
import de.ptb.epics.eve.util.data.Version;
import static de.ptb.epics.eve.util.xml.XMLUtil.*;

/**
 * Patches SCML v3.2 to v4.0 doing the following:
 * 
 * <ul>
 * <li>increment version to 4.0-</li>
 * <li>replace event type attribute with tag as follows:
 * <ul>
 * <li>schema definition changes from:
 * 
 * <pre>
 *   &lt;complexType name=&quot;smevent&quot;&gt;
 *     &lt;sequence&gt;
 *       &lt;group ref=&quot;tns:smeventGroup&quot;&gt;&lt;/group&gt;
 *     &lt;/sequence&gt;
 *     &lt;attribute name=&quot;type&quot; type=&quot;tns:event&quot; use=&quot;required&quot;&gt;&lt;/attribute&gt;
 *   &lt;/complexType&gt;
 * 
 *   &lt;group name=&quot;smeventGroup&quot;&gt;
 *     &lt;sequence&gt;
 *       &lt;choice&gt;
 *         &lt;sequence&gt;
 *           &lt;element name=&quot;id&quot; type=&quot;tns:identifier&quot;&gt;&lt;/element&gt;
 *           &lt;element name=&quot;limit&quot; type=&quot;tns:limit&quot; minOccurs=&quot;0&quot;&gt;&lt;/element&gt;
 *         &lt;/sequence&gt;
 *         &lt;sequence&gt;
 *           &lt;element name=&quot;incident&quot; type=&quot;tns:incident&quot; default=&quot;End&quot;&gt;&lt;/element&gt;
 *           &lt;element name=&quot;chainid&quot; type=&quot;tns:zeroToThousand&quot;&gt;&lt;/element&gt;
 *           &lt;element name=&quot;smid&quot; type=&quot;tns:zeroToHundredthousand&quot;&gt;&lt;/element&gt;
 *         &lt;/sequence&gt;
 *       &lt;/choice&gt;
 *     &lt;/sequence&gt;
 *   &lt;/group&gt;
 * </pre>
 * 
 * to:
 * 
 * <pre>
 *   &lt;complexType name=&quot;smevent&quot;&gt;
 *     &lt;sequence&gt;
 *       &lt;group ref=&quot;tns:smeventGroup&quot;&gt;&lt;/group&gt;
 *     &lt;/sequence&gt;
 *   &lt;/complexType&gt;
 * 
 *   &lt;group name=&quot;smeventGroup&quot;&gt;
 *     &lt;choice&gt;
 *       &lt;element name=&quot;detectorevent&quot; type=&quot;tns:detectorevent&quot;&gt;&lt;/element&gt;
 *       &lt;element name=&quot;monitorevent&quot; type=&quot;tns:monitorevent&quot;&gt;&lt;/element&gt;
 *       &lt;element name=&quot;scheduleevent&quot; type=&quot;tns:scheduleevent&quot;&gt;&lt;/element&gt;
 *     &lt;/choice&gt;
 *   &lt;/group&gt;
 * 
 *   &lt;complexType name=&quot;detectorevent&quot;&gt;
 *     &lt;sequence&gt;
 *       &lt;element name=&quot;id&quot; type=&quot;tns:identifier&quot;&gt;&lt;/element&gt;
 *     &lt;/sequence&gt;
 *   &lt;/complexType&gt;
 * 
 *   &lt;complexType name=&quot;monitorevent&quot;&gt;
 *     &lt;sequence&gt;
 *       &lt;element name=&quot;id&quot; type=&quot;tns:identifier&quot;&gt;&lt;/element&gt;
 *       &lt;element name=&quot;limit&quot; type=&quot;tns:limit&quot;&gt;&lt;/element&gt;
 *     &lt;/sequence&gt;
 *   &lt;/complexType&gt;
 * 
 *   &lt;complexType name=&quot;scheduleevent&quot;&gt;
 *     &lt;sequence&gt;
 *       &lt;element name=&quot;incident&quot; type=&quot;tns:incident&quot; default=&quot;End&quot;&gt;&lt;/element&gt;
 *       &lt;element name=&quot;chainid&quot; type=&quot;tns:zeroToThousand&quot;&gt;&lt;/element&gt;
 *       &lt;element name=&quot;smid&quot; type=&quot;tns:zeroToHundredthousand&quot;&gt;&lt;/element&gt;
 *     &lt;/sequence&gt;
 *   &lt;/complexType&gt;
 * </pre>
 * 
 * </li>
 * <li>
 * A detector channel defined in SCML v3.2:
 * 
 * <pre>
 * &lt;smchannel&gt;
 *     &lt;channelid&gt;P5000:gw2370700&lt;/channelid&gt;
 *     &lt;sendreadyevent&gt;true&lt;/sendreadyevent&gt;
 *     &lt;redoevent type=&quot;detector&quot;&gt;
 *         &lt;id&gt;D-1-1-P5000:gw2370700&lt;/id&gt;
 *     &lt;/redoevent&gt;
 *     &lt;redoevent type=&quot;monitor&quot;&gt;
 *         &lt;id&gt;K6517:rioc62800chan1&lt;/id&gt;
 *         &lt;limit type=&quot;double&quot; comparison=&quot;eq&quot;&gt;2&lt;/limit&gt;
 *     &lt;/redoevent&gt;
 *     &lt;redoevent type=&quot;schedule&quot;&gt;
 *         &lt;incident&gt;E&lt;/incident&gt;
 *         &lt;chainid&gt;0&lt;/chainid&gt;
 *         &lt;smid&gt;0&lt;/smid&gt;
 *     &lt;/redoevent&gt;
 * &lt;/smchannel&gt;
 * </pre>
 * 
 * is defined in SCML v4.0 as follows:
 * 
 * <pre>
 * &lt;smchannel&gt;
 *     &lt;channelid&gt;P5000:gw2370700&lt;/channelid&gt;
 *     &lt;sendreadyevent&gt;true&lt;/sendreadyevent&gt;
 *     &lt;redoevent&gt;
 *         &lt;detectorevent&gt;
 *             &lt;id&gt;D-1-1-P5000:gw2370700&lt;/id&gt;
 *         &lt;/detectorevent&gt;
 *     &lt;/redoevent&gt;
 *     &lt;redoevent&gt;
 *         &lt;monitorevent&gt;
 *             &lt;id&gt;K6517:rioc62800chan1&lt;/id&gt;
 *             &lt;limit type=&quot;double&quot; comparison=&quot;eq&quot;&gt;2&lt;/limit&gt;
 *         &lt;/monitorevent&gt;
 *     &lt;/redoevent&gt;
 *     &lt;redoevent&gt;
 *         &lt;scheduleevent&gt;
 *             &lt;incident&gt;End&lt;/incident&gt;
 *             &lt;chainid&gt;0&lt;/chainid&gt;
 *             &lt;smid&gt;0&lt;/smid&gt;
 *         &lt;/scheduleevent&gt;
 *     &lt;/redoevent&gt;
 * &lt;/smchannel&gt;
 * </pre>
 * 
 * </li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @author Marcus Michalsky
 * @since 1.23
 */
public class Patch3o2T4o0 extends Patch {
	private static final Logger LOGGER = Logger.getLogger(Patch3o2T4o0.class
			.getName());
	
	private static Patch3o2T4o0 INSTANCE;
	
	private Patch3o2T4o0(Version source, Version target, 
			List<Modification> modifications) {
		super(source, target, modifications);
		modifications.add(new Mod0(this));
		modifications.add(new Mod1(this));
	}
	
	public static Patch3o2T4o0 getInstance() {
		if (INSTANCE == null) {
			List<Modification> modifications = new LinkedList<>();
			
			INSTANCE = new Patch3o2T4o0(new Version(3, 2), new Version(4, 0),
					modifications);
		}
		return INSTANCE;
	}

	private class Mod0 extends AbstractModification {
		public Mod0(Patch patch) {
			super(patch, "setting scml version to 4.0");
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modify(Document document) {
			Node version = document.getElementsByTagName("version").item(0);
			if (version.getNodeType() == Node.ELEMENT_NODE) {
				((Text)version.getFirstChild()).setData("4.0");
			}
		}
	}
	
	private class Mod1 extends AbstractModification {
		public Mod1(Patch patch) {
			super(patch, "embedding events in type dependend tags");
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modify(Document document) {
			List<Node> allEvents = new ArrayList<>();
			
			NodeList startEvents = document.getElementsByTagName("startevent");
			allEvents.addAll(asList(startEvents));
			NodeList redoEvents = document.getElementsByTagName("redoevent");
			allEvents.addAll(asList(redoEvents));
			NodeList breakEvents = document.getElementsByTagName("breakevent");
			allEvents.addAll(asList(breakEvents));
			NodeList stopEvents = document.getElementsByTagName("stopevent");
			allEvents.addAll(asList(stopEvents));
			NodeList triggerEvents = document.getElementsByTagName("triggerevent");
			allEvents.addAll(asList(triggerEvents));
			NodeList pauseEvents = document.getElementsByTagName("pauseevent");
			allEvents.addAll(asList(pauseEvents));

			LOGGER.debug("Found " + allEvents.size() + " other events.");
			
			for (Node node : allEvents) {
				EventTypes eventType = EventTypes.stringToType(node
						.getAttributes().getNamedItem("type").getNodeValue());
				LOGGER.debug("Processing " + node.getNodeName() + " ("
						+ eventType + ")");
				LOGGER.debug("removing attribute 'type'");
				node.getAttributes().removeNamedItem("type");
				LOGGER.debug("Create new element: " + eventType);
				Element element = document.createElement(eventType.toString()
						+ "event");
				node.appendChild(element);
				while (!node.getFirstChild().equals(element)) {
						element.appendChild(node.getFirstChild());
					}
				
				List<Node> children = asList(element.getChildNodes());
				for (Node child : children) {
					if(child.getNodeName().equals("action")) {
						node.appendChild(child);
					}
				}
			}
		}
	}
}
