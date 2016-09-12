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
 * TODO
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