package de.ptb.epics.eve.data.scandescription.updater.patches;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import de.ptb.epics.eve.data.scandescription.updater.AbstractModification;
import de.ptb.epics.eve.data.scandescription.updater.Modification;
import de.ptb.epics.eve.data.scandescription.updater.Patch;
import de.ptb.epics.eve.util.data.Version;

import static de.ptb.epics.eve.util.xml.XMLUtil.*;

/**
 * Patches SCML v4.0 to v5.0 doing the following:
 * 
 * <ul>
 * <li>increment version to 5.0-</li>
 * <li>introduces a standard tag (channel mode) and moves specific elements to it as follows:
 * <ul>
 * <li>schema definition changes from:
 * 
 * <pre>
 *  &lt;complexType name=&quot;smchannel&quot;&gt;
 *    &lt;sequence&gt;
 *      &lt;element name=&quot;channelid&quot; type=&quot;tns:identifier&quot;&gt;&lt;/element&gt;
 *      &lt;element name=&quot;averagecount&quot; type=&quot;nonNegativeInteger&quot; minOccurs=&quot;0&quot;&gt;&lt;/element&gt;
 *      &lt;element name=&quot;maxdeviation&quot; type=&quot;double&quot; minOccurs=&quot;0&quot;&gt;&lt;/element&gt;
 *      &lt;element name=&quot;minimum&quot; type=&quot;double&quot; minOccurs=&quot;0&quot;&gt;&lt;/element&gt;
 *      &lt;element name=&quot;maxattempts&quot; type=&quot;nonNegativeInteger&quot; minOccurs=&quot;0&quot;&gt;&lt;/element&gt;
 *      &lt;element name=&quot;normalize_id&quot; type=&quot;tns:identifier&quot; minOccurs=&quot;0&quot;&gt;&lt;/element&gt;
 *      &lt;element name=&quot;sendreadyevent&quot; type=&quot;boolean&quot; minOccurs=&quot;0&quot;&gt;&lt;/element&gt;
 *      &lt;element name=&quot;redoevent&quot; type=&quot;tns:smevent&quot; minOccurs=&quot;0&quot; maxOccurs=&quot;unbounded&quot;&gt;&lt;/element&gt;
 *      &lt;element name=&quot;deferredtrigger&quot; type=&quot;boolean&quot; minOccurs=&quot;0&quot;&gt;&lt;/element&gt;
 *    &lt;/sequence&gt;
 *  &lt;/complexType&gt;
 * </pre>
 * 
 * to:
 * 
 * <pre>
 *  &lt;complexType name=&quot;smchannel&quot;&gt;
 *    &lt;sequence&gt;
 *      &lt;element name=&quot;channelid&quot; type=&quot;tns:identifier&quot;&gt;&lt;/element&gt;
 *      &lt;element name=&quot;normalize_id&quot; type=&quot;tns:identifier&quot; minOccurs=&quot;0&quot;&gt;&lt;/element&gt;
 *      &lt;choice&gt;
 *        &lt;element name=&quot;standard&quot; type=&quot;tns:standardchannel&quot;&gt;&lt;/element&gt;
 *        &lt;element name=&quot;interval&quot; type=&quot;tns:intervalchannel&quot;&gt;&lt;/element&gt;
 *      &lt;/choice&gt;
 *    &lt;/sequence&gt;
 *  &lt;/complexType&gt;

 *  &lt;complexType name=&quot;standardchannel&quot;&gt;
 *    &lt;sequence&gt;
 *      &lt;element name=&quot;averagecount&quot; type=&quot;nonNegativeInteger&quot; minOccurs=&quot;0&quot;&gt;&lt;/element&gt;
 *      &lt;element name=&quot;maxdeviation&quot; type=&quot;double&quot; minOccurs=&quot;0&quot;&gt;&lt;/element&gt;
 *      &lt;element name=&quot;minimum&quot; type=&quot;double&quot; minOccurs=&quot;0&quot;&gt;&lt;/element&gt;
 *      &lt;element name=&quot;maxattempts&quot; type=&quot;nonNegativeInteger&quot; minOccurs=&quot;0&quot;&gt;&lt;/element&gt;
 *      &lt;element name=&quot;sendreadyevent&quot; type=&quot;boolean&quot; minOccurs=&quot;0&quot;&gt;&lt;/element&gt;
 *      &lt;element name=&quot;redoevent&quot; type=&quot;tns:smevent&quot; minOccurs=&quot;0&quot; maxOccurs=&quot;unbounded&quot;&gt;&lt;/element&gt;
 *      &lt;element name=&quot;deferredtrigger&quot; type=&quot;boolean&quot; minOccurs=&quot;0&quot;&gt;&lt;/element&gt;
 *    &lt;/sequence&gt;
 *  &lt;/complexType&gt;
  
 *  &lt;complexType name=&quot;intervalchannel&quot;&gt;
 *    &lt;sequence&gt;
 *      &lt;element name=&quot;triggerinterval&quot; type=&quot;tns:positiveDouble&quot;&gt;&lt;/element&gt;
 *      &lt;element name=&quot;stoppedby&quot; type=&quot;tns:identifier&quot;&gt;&lt;/element&gt;
 *    &lt;/sequence&gt;
 *  &lt;/complexType&gt;
 *  
 *  &lt;simpleType name=&quot;positiveDouble&quot;&gt;
 *    &lt;restriction base=&quot;double&quot;&gt;
 *      &lt;minInclusive value=&quot;0&quot;&gt;&lt;/minInclusive&gt;
 *    &lt;/restriction&gt;
 *  &lt;/simpleType&gt;
 * </pre>
 * 
 * </li>
 * <li>
 * A detector channel defined in SCML v4.0:
 * 
 * <pre>
 *  &lt;smchannel&gt;
 *    &lt;channelid&gt;bIICurrent:Mnt1chan1&lt;/channelid&gt;
 *    &lt;averagecount&gt;10&lt;/averagecount&gt;
 *    &lt;maxdeviation&gt;2.0&lt;/maxdeviation&gt;
 *    &lt;minimum&gt;3.0&lt;/minimum&gt;
 *    &lt;maxattempts&gt;4&lt;/maxattempts&gt;
 *    &lt;normalize_id&gt;bIICurrent:Mnt1lifeTimechan1&lt;/normalize_id&gt;
 *    &lt;redoevent&gt;
 *      &lt;monitorevent&gt;
 *        &lt;id&gt;DiscPosPPSMC:gw23715000&lt;/id&gt;
 *        &lt;limit type=&quot;string&quot; comparison=&quot;eq&quot;&gt;Position1&lt;/limit&gt;
 *      &lt;/monitorevent&gt;
 *    &lt;/redoevent&gt;
 *  &lt;/smchannel&gt;
 * </pre>
 * 
 * is defined in SCML v5.0 as follows:
 * 
 * <pre>
 *  &lt;smchannel&gt;
 *    &lt;channelid&gt;bIICurrent:Mnt1chan1&lt;/channelid&gt;
 *    &lt;normalize_id&gt;bIICurrent:Mnt1lifeTimechan1&lt;/normalize_id&gt;
 *    &lt;standard&gt;
 *      &lt;averagecount&gt;10&lt;/averagecount&gt;
 *      &lt;maxdeviation&gt;2.0&lt;/maxdeviation&gt;
 *      &lt;minimum&gt;3.0&lt;/minimum&gt;
 *      &lt;maxattempts&gt;4&lt;/maxattempts&gt;
 *      &lt;redoevent&gt;
 *        &lt;monitorevent&gt;
 *          &lt;id&gt;DiscPosPPSMC:gw23715000&lt;/id&gt;
 *          &lt;limit type=&quot;string&quot; comparison=&quot;eq&quot;&gt;Position1&lt;/limit&gt;
 *        &lt;/monitorevent&gt;
 *      &lt;/redoevent&gt;
 *    &lt;/standard&gt;
 *  &lt;/smchannel&gt;
 * </pre>
 * 
 * </li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @author Marcus Michalsky
 * @since 1.27
 */
public class Patch4o0T5o0 extends Patch {
	private static final Logger LOGGER = Logger.getLogger(Patch4o0T5o0.class.getName());
	
	private static Patch4o0T5o0 INSTANCE;

	private Patch4o0T5o0(Version source, Version target, List<Modification> modifications) {
		super(source, target, modifications);
		modifications.add(new Mod0(this));
		modifications.add(new Mod1(this));
	}
	
	public static Patch4o0T5o0 getInstance() {
		if (INSTANCE == null) {
			List<Modification> modifications = new LinkedList<Modification>();
			
			INSTANCE = new Patch4o0T5o0(new Version(4, 0), new Version(5, 0), modifications);
		}
		return INSTANCE;
	}
	
	private class Mod0 extends AbstractModification {
		public Mod0(Patch patch) {
			super(patch, "setting scml version to 5.0");
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modify(Document document) {
			Node version = document.getElementsByTagName("version").item(0);
			if (version.getNodeType() == Node.ELEMENT_NODE) {
				((Text)version.getFirstChild()).setData("5.0");
			}
		}
	}
	
	private class Mod1 extends AbstractModification {
		public Mod1(Patch patch) {
			super(patch, "converted scan module channels to (newly introduced) standard mode channels");
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modify(Document document) {
			NodeList smChannels = document.getElementsByTagName("smchannel");
			for (Node smChannel : asList(smChannels)) {
				LOGGER.debug("collecting child elements of smchannel");
				Node channelid = null;
				Node normalizeid = null;
				List<Node> other = new ArrayList<>();
				for (Node child : asList(smChannel.getChildNodes())) {
					if (child.getNodeType() != Node.ELEMENT_NODE) {
						continue;
					}
					if (child.getNodeName().equals("channelid")) {
						channelid = child;
						LOGGER.debug("smchannel is " + channelid.getTextContent());
					} else if (child.getNodeName().equals("normalize_id")) {
						normalizeid = child;
						LOGGER.debug("normalize id is " + normalizeid.getTextContent());
					} else {
						other.add(child);
						LOGGER.debug("found element " + child.getNodeName());
					}
				}
				LOGGER.debug("adding standard element to smchannel");
				Element standardElement = document.createElement("standard");
				smChannel.appendChild(standardElement);
				LOGGER.debug("moving nodes to standard element");
				// move "other" nodes (all childs but channelid and normalizeid) to standard element
				for (Node node : other) {
					try {
						smChannel.removeChild(node);
						standardElement.appendChild(node);
						LOGGER.debug("moved " + node.getNodeName());
					} catch (DOMException e) {
						LOGGER.error(e.getMessage(), e);
					}
				}
			}
		}
	}
}