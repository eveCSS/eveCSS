package de.ptb.epics.eve.data.scandescription.updater.patches;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
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
 * Patches SCML v7.0 to v8.0 doing the following:
 * 
 * <ul>
 * <li>increment version to 8.0</li>
 * <li>moves the following tags from chain up to scan (after repeatcount):
 * 	<ul>
 * 		<li>comment</li>
 * 		<li>savefilename</li>
 * 		<li>confirmsave</li>
 * 		<li>autonumber</li>
 * 		<li>savescandescription</li>
 * 		<li>saveplugin</li>
 * 	</ul>
 * <li>the filegroup ist not optional anymore</li>
 * <li>elements savescandescription and saveplugin of filegroup are not optional anymore</li>
 * <li>schema definition changes from:
 * 
 * <pre>
 *   &lt;complexType name="scan"&gt;
    &lt;sequence&gt;
      &lt;element name="repeatcount" type="tns:repeatcount" default="0"&gt;&lt;/element&gt;
      &lt;element name="chain" type="tns:chain" maxOccurs="unbounded"&gt;&lt;/element&gt;
      &lt;element name="monitoroptions" type="tns:monitoroptions"&gt;&lt;/element&gt;
    &lt;/sequence&gt;
  &lt;/complexType&gt;

  &lt;complexType name="chain"&gt;
    &lt;sequence&gt;
      &lt;element name="comment" type="string" minOccurs="0"&gt;&lt;/element&gt;
      &lt;group ref="tns:fileGroup" minOccurs="0"&gt;&lt;/group&gt;
      &lt;element name="startevent" type="tns:smevent" minOccurs="0"&gt;&lt;/element&gt;
      &lt;element name="pauseevent" type="tns:pauseevent" minOccurs="0" maxOccurs="unbounded"&gt;&lt;/element&gt;
      &lt;element name="redoevent" type="tns:smevent" minOccurs="0" maxOccurs="unbounded"&gt;&lt;/element&gt;
      &lt;element name="breakevent" type="tns:smevent" minOccurs="0" maxOccurs="unbounded"&gt;&lt;/element&gt;
      &lt;element name="stopevent" type="tns:smevent" minOccurs="0" maxOccurs="unbounded"&gt;&lt;/element&gt;
      &lt;element name="scanmodules" type="tns:scanmodules"&gt;&lt;/element&gt;
    &lt;/sequence&gt;
  &lt;attribute name="id" type="tns:oneToThousand"&gt;&lt;/attribute&gt;
  &lt;/complexType&gt;
  
   &lt;group name="fileGroup"&gt;
    &lt;sequence&gt;
      &lt;element name="savefilename" type="string"&gt;&lt;/element&gt;
      &lt;element name="confirmsave" type="boolean" default="false"&gt;&lt;/element&gt;
      &lt;element name="autonumber" type="boolean" default="true"&gt;&lt;/element&gt;
      &lt;element name="savescandescription" type="boolean" minOccurs="0"&gt;&lt;/element&gt;
      &lt;element name="saveplugin" type="tns:controller" minOccurs="0"&gt;&lt;/element&gt;
    &lt;/sequence&gt;
  &lt;/group&gt;
  </pre>
 * 
 * to
 * 
 * <pre>
 *  &lt;complexType name="scan"&gt;
    &lt;sequence&gt;
      &lt;element name="repeatcount" type="tns:repeatcount" default="0"&gt;&lt;/element&gt;
      &lt;element name="comment" type="string" minOccurs="0"&gt;&lt;/element&gt;
      &lt;group ref="tns:fileGroup"&gt;&lt;/group&gt;
      &lt;element name="chain" type="tns:chain" maxOccurs="unbounded"&gt;&lt;/element&gt;
      &lt;element name="monitoroptions" type="tns:monitoroptions"&gt;&lt;/element&gt;
    &lt;/sequence&gt;
  &lt;/complexType&gt;

  &lt;complexType name="chain"&gt;
    &lt;sequence&gt;
      &lt;element name="startevent" type="tns:smevent" minOccurs="0"&gt;&lt;/element&gt;
      &lt;element name="pauseevent" type="tns:pauseevent" minOccurs="0" maxOccurs="unbounded"&gt;&lt;/element&gt;
      &lt;element name="redoevent" type="tns:smevent" minOccurs="0" maxOccurs="unbounded"&gt;&lt;/element&gt;
      &lt;element name="breakevent" type="tns:smevent" minOccurs="0" maxOccurs="unbounded"&gt;&lt;/element&gt;
      &lt;element name="stopevent" type="tns:smevent" minOccurs="0" maxOccurs="unbounded"&gt;&lt;/element&gt;
      &lt;element name="scanmodules" type="tns:scanmodules"&gt;&lt;/element&gt;
    &lt;/sequence&gt;
  &lt;attribute name="id" type="tns:oneToThousand"&gt;&lt;/attribute&gt;
  &lt;/complexType&gt;
  
   &lt;group name="fileGroup"&gt;
    &lt;sequence&gt;
      &lt;element name="savefilename" type="string"&gt;&lt;/element&gt;
      &lt;element name="confirmsave" type="boolean" default="false"&gt;&lt;/element&gt;
      &lt;element name="autonumber" type="boolean" default="true"&gt;&lt;/element&gt;
      &lt;element name="savescandescription" type="boolean" default="true"&gt;&lt;/element&gt;
      &lt;element name="saveplugin" type="tns:controller"&gt;&lt;/element&gt;
    &lt;/sequence&gt;
  &lt;/group&gt;
 * </pre>
 * </ul>
 * 
 * @author Marcus Michalsky
 * @since 1.33
 */
public class Patch7o0T8o0 extends Patch {
	private static final Logger LOGGER = Logger.getLogger(Patch7o0T8o0.class.getName());
	private static Patch7o0T8o0 INSTANCE;

	private Patch7o0T8o0(Version source, Version target, List<Modification> modifications) {
		super(source, target, modifications);
		modifications.add(new Mod0(this));
		modifications.add(new Mod1(this));
	}
	
	public static Patch7o0T8o0 getInstance() {
		if (INSTANCE == null) {
			List<Modification> modifications = new LinkedList<>();
			INSTANCE = new Patch7o0T8o0(new Version(7, 0), new Version (8, 0), modifications);
		}
		return INSTANCE;
	}
	
	private class Mod0 extends AbstractModification {
		public Mod0(Patch patch) {
			super(patch, "setting scml version to 8.0");
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modify(Document document) {
			Node version = document.getElementsByTagName("version").item(0);
			if (version.getNodeType() == Node.ELEMENT_NODE) {
				((Text) version.getFirstChild()).setData("8.0");
			}
		}
	}
	
	private class Mod1 extends AbstractModification {
		public Mod1(Patch patch) {
			super(patch, "moving attributes from chain to scan");
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modify(Document document) {
			NodeList scanNodes = document.getElementsByTagName(Literals.XML_ELEMENT_NAME_SCAN);
			if (scanNodes.getLength() != 1) {
				LOGGER.error("no scan/multiple scan elements found.");
				return;
			}
			Node scan = scanNodes.item(0);
			
			NodeList chainNodes = document.getElementsByTagName(Literals.XML_ELEMENT_NAME_CHAIN);
			Node chain1 = null;
			for (Node chainNode : asList(chainNodes)) {
				if (chainNode.getAttributes().getNamedItem("id") != null &&
						chainNode.getAttributes().getNamedItem("id").getNodeValue().equals("1")) {
					chain1 = chainNode;
				}
			}
			if (chain1 == null) {
				LOGGER.error("chain 1 not found");
				return;
			}
			
			List<Node> toRemove = new LinkedList<>();
			
			for (Node chainChild : asList(chain1.getChildNodes())) {
				if (chainChild.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				switch (chainChild.getNodeName()) {
				case Literals.XML_ELEMENT_NAME_COMMENT:
				case Literals.XML_ELEMENT_NAME_SAVEFILENAME:
				case Literals.XML_ELEMENT_NAME_CONFIRMSAVE:
				case Literals.XML_ELEMENT_NAME_AUTONUMBER:
				case Literals.XML_ELEMENT_NAME_SAVESCANDESCRIPTION:
				case Literals.XML_ELEMENT_NAME_SAVEPLUGIN:
					scan.insertBefore(chainChild.cloneNode(true), chain1);
					toRemove.add(chainChild);
					break;
				default:
					break;
				}
			}
			
			for (Node node : toRemove) {
				chain1.removeChild(node);
			}
		}
	}
}
