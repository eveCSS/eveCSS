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

import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;
import de.ptb.epics.eve.data.scandescription.processors.Literals;
import de.ptb.epics.eve.data.scandescription.updater.AbstractModification;
import de.ptb.epics.eve.data.scandescription.updater.Modification;
import de.ptb.epics.eve.data.scandescription.updater.Patch;
import de.ptb.epics.eve.util.data.Version;

import static de.ptb.epics.eve.util.xml.XMLUtil.*;

/**
 * Patches SCML v6.0 to v7.0 doing the following:
 * 
 * <ul>
 * <li>increment version to 7.0</li>
 * <li>introduces child elements of scanmodule as follows:
 * <ul>
 * <li>the type element is removed</li>
 * <li>the elements name, xpos, ypos, parent, nested and appended remain as
 * child elements of scanmodule</li>
 * <li>scanmodule will contain one of five newly introduced types</li>
 * <li>all other elements (which are specific to classic scan modules) are moved
 * to the newly introduced sub element</li>
 * <li>schema definition changes from:
 * 
 * <pre>
 * &lt;complexType name="scanmodule"&gt;
    &lt;sequence&gt;
      &lt;element name="type" type="tns:smtype"&gt;&lt;/element&gt;
      &lt;element name="name" type="string"&gt;&lt;/element&gt;
      &lt;element name="xpos" type="nonNegativeInteger"&gt;&lt;/element&gt;
      &lt;element name="ypos" type="nonNegativeInteger"&gt;&lt;/element&gt;
      &lt;element name="parent" type="integer"&gt;&lt;/element&gt;
      &lt;element name="nested" type="positiveInteger"  minOccurs="0"&gt;&lt;/element&gt;
      &lt;element name="appended" type="positiveInteger" minOccurs="0"&gt;&lt;/element&gt;
      &lt;element name="valuecount" type="positiveInteger" default="1"&gt;&lt;/element&gt;
      &lt;element name="settletime" type="double" minOccurs="0"&gt;&lt;/element&gt;
      &lt;element name="triggerdelay" type="double" minOccurs="0"&gt;&lt;/element&gt;
      &lt;element name="triggerconfirmaxis" type="boolean" minOccurs="0"&gt;&lt;/element&gt;
      &lt;element name="triggerconfirmchannel" type="boolean" minOccurs="0"&gt;&lt;/element&gt;
      &lt;element name="triggerevent" type="tns:smevent" minOccurs="0" maxOccurs="unbounded"&gt;&lt;/element&gt;
      &lt;element name="pauseevent" type="tns:pauseevent" minOccurs="0" maxOccurs="unbounded"&gt;&lt;/element&gt;
      &lt;element name="redoevent" type="tns:smevent" minOccurs="0" maxOccurs="unbounded"&gt;&lt;/element&gt;
      &lt;element name="breakevent" type="tns:smevent" minOccurs="0" maxOccurs="unbounded"&gt;&lt;/element&gt;
      &lt;element name="prescan" minOccurs="0" maxOccurs="unbounded"&gt;
        &lt;complexType&gt;
          &lt;sequence&gt;
            &lt;element name="id" type="tns:identifier"&gt;&lt;/element&gt;
            &lt;element name="value" type="tns:typevalue"&gt;&lt;/element&gt;
          &lt;/sequence&gt;
        &lt;/complexType&gt;
      &lt;/element&gt;
      &lt;element name="smaxis" type="tns:smaxis" minOccurs="0" maxOccurs="unbounded"&gt;&lt;/element&gt;
      &lt;element name="smchannel" type="tns:smchannel" minOccurs="0" maxOccurs="unbounded"&gt;&lt;/element&gt;
      &lt;element name="postscan" minOccurs="0" maxOccurs="unbounded"&gt;
        &lt;complexType&gt;
          &lt;sequence&gt;
            &lt;element name="id" type="tns:identifier"&gt;&lt;/element&gt;
            &lt;choice&gt;
              &lt;element name="value" type="tns:typevalue"&gt;&lt;/element&gt;
              &lt;element name="reset_originalvalue" type="boolean"&gt;&lt;/element&gt;
            &lt;/choice&gt;
          &lt;/sequence&gt;
        &lt;/complexType&gt;
      &lt;/element&gt;
      &lt;element name="positioning" type="tns:positioning" minOccurs="0" maxOccurs="unbounded"&gt;&lt;/element&gt;
      &lt;element name="plot" type="tns:plot" minOccurs="0" maxOccurs="unbounded"&gt;&lt;/element&gt;
    &lt;/sequence&gt;
    &lt;attribute name="id" type="positiveInteger"&gt;&lt;/attribute&gt;
  &lt;/complexType&gt;
 * </pre>
 * 
 * to
 * 
 * <pre>
 *  &lt;complexType name="scanmodule"&gt;
    &lt;sequence&gt;
      &lt;element name="name" type="string"&gt;&lt;/element&gt;
      &lt;element name="xpos" type="nonNegativeInteger"&gt;&lt;/element&gt;
      &lt;element name="ypos" type="nonNegativeInteger"&gt;&lt;/element&gt;
      &lt;element name="parent" type="integer"&gt;&lt;/element&gt;
      &lt;element name="nested" type="positiveInteger"  minOccurs="0"&gt;&lt;/element&gt;
      &lt;element name="appended" type="positiveInteger" minOccurs="0"&gt;&lt;/element&gt;
      &lt;choice&gt;
        &lt;element name="classic" type="tns:smClassic"&gt;&lt;/element&gt;
        &lt;element name="save_axis_positions" type="tns:smSaveAxisPositions"&gt;&lt;/element&gt;
        &lt;element name="save_channel_values" type="tns:smSaveChannelValues"&gt;&lt;/element&gt;
        &lt;element name="dynamic_axis_positions" type="tns:smDynamicAxisPositions"&gt;&lt;/element&gt;
        &lt;element name="dynamic_channel_values" type="tns:smDynamicChannelValues"&gt;&lt;/element&gt;
      &lt;/choice&gt;
    &lt;/sequence&gt;
    &lt;attribute name="id" type="positiveInteger"&gt;&lt;/attribute&gt;
  &lt;/complexType&gt;

  &lt;complexType name="smClassic"&gt;
    &lt;sequence&gt;
      &lt;element name="valuecount" type="positiveInteger" default="1"&gt;&lt;/element&gt;
      &lt;element name="settletime" type="double" minOccurs="0"&gt;&lt;/element&gt;
      &lt;element name="triggerdelay" type="double" minOccurs="0"&gt;&lt;/element&gt;
      &lt;element name="triggerconfirmaxis" type="boolean" minOccurs="0"&gt;&lt;/element&gt;
      &lt;element name="triggerconfirmchannel" type="boolean" minOccurs="0"&gt;&lt;/element&gt;
      &lt;element name="triggerevent" type="tns:smevent" minOccurs="0" maxOccurs="unbounded"&gt;&lt;/element&gt;
      &lt;element name="pauseevent" type="tns:pauseevent" minOccurs="0" maxOccurs="unbounded"&gt;&lt;/element&gt;
      &lt;element name="redoevent" type="tns:smevent" minOccurs="0" maxOccurs="unbounded"&gt;&lt;/element&gt;
      &lt;element name="breakevent" type="tns:smevent" minOccurs="0" maxOccurs="unbounded"&gt;&lt;/element&gt;
      &lt;element name="prescan" minOccurs="0" maxOccurs="unbounded"&gt;
        &lt;complexType&gt;
          &lt;sequence&gt;
            &lt;element name="id" type="tns:identifier"&gt;&lt;/element&gt;
            &lt;element name="value" type="tns:typevalue"&gt;&lt;/element&gt;
          &lt;/sequence&gt;
        &lt;/complexType&gt;
      &lt;/element&gt;
      &lt;element name="smaxis" type="tns:smaxis" minOccurs="0" maxOccurs="unbounded"&gt;&lt;/element&gt;
      &lt;element name="smchannel" type="tns:smchannel" minOccurs="0" maxOccurs="unbounded"&gt;&lt;/element&gt;
      &lt;element name="postscan" minOccurs="0" maxOccurs="unbounded"&gt;
        &lt;complexType&gt;
          &lt;sequence&gt;
            &lt;element name="id" type="tns:identifier"&gt;&lt;/element&gt;
            &lt;choice&gt;
              &lt;element name="value" type="tns:typevalue"&gt;&lt;/element&gt;
              &lt;element name="reset_originalvalue" type="boolean"&gt;&lt;/element&gt;
            &lt;/choice&gt;
          &lt;/sequence&gt;
        &lt;/complexType&gt;
      &lt;/element&gt;
      &lt;element name="positioning" type="tns:positioning" minOccurs="0" maxOccurs="unbounded"&gt;&lt;/element&gt;
      &lt;element name="plot" type="tns:plot" minOccurs="0" maxOccurs="unbounded"&gt;&lt;/element&gt;
    &lt;/sequence&gt;
  &lt;/complexType&gt;
  
  &lt;complexType name="smSaveAxisPositions"&gt;
    &lt;sequence&gt;
      &lt;element name="smaxis" type="tns:smaxis" minOccurs="0" maxOccurs="unbounded"&gt;&lt;/element&gt;
    &lt;/sequence&gt;
  &lt;/complexType&gt;
  
  &lt;complexType name="smSaveChannelValues"&gt;
    &lt;sequence&gt;
      &lt;element name="smchannel" type="tns:smchannel" minOccurs="0" maxOccurs="unbounded"&gt;&lt;/element&gt;
    &lt;/sequence&gt;
  &lt;/complexType&gt;
  
  &lt;complexType name="smDynamicAxisPositions"&gt;&lt;/complexType&gt;
  
  &lt;complexType name="smDynamicChannelValues"&gt;&lt;/complexType&gt;
 * </pre>
 * 
 * An example scan containing all five possible choices of a scan module is
 * given below:
 * 
 * <pre>
&lt;scanmodules&gt;
  &lt;scanmodule id="1"&gt;
    &lt;name&gt;SM 1&lt;/name&gt;
    &lt;xpos&gt;145&lt;/xpos&gt;
    &lt;ypos&gt;15&lt;/ypos&gt;
    &lt;parent&gt;0&lt;/parent&gt;
    &lt;appended&gt;2&lt;/appended&gt;
    &lt;classic&gt;
      &lt;valuecount&gt;1&lt;/valuecount&gt;
      &lt;settletime&gt;0.0&lt;/settletime&gt;
      &lt;triggerdelay&gt;0.0&lt;/triggerdelay&gt;
      &lt;triggerconfirmaxis&gt;false&lt;/triggerconfirmaxis&gt;
      &lt;triggerconfirmchannel&gt;false&lt;/triggerconfirmchannel&gt;
      &lt;smaxis&gt;
        &lt;axisid&gt;Counter&lt;/axisid&gt;
        &lt;stepfunction&gt;Positionlist&lt;/stepfunction&gt;
        &lt;positionmode&gt;absolute&lt;/positionmode&gt;
        &lt;positionlist&gt;1300&lt;/positionlist&gt;
      &lt;/smaxis&gt;
      &lt;smchannel&gt;
        &lt;channelid&gt;bIICurrent:Mnt1chan1&lt;/channelid&gt;
        &lt;standard&gt;
          &lt;averagecount&gt;5&lt;/averagecount&gt;
          &lt;maxdeviation&gt;5.0&lt;/maxdeviation&gt;
          &lt;minimum&gt;100.0&lt;/minimum&gt;
          &lt;maxattempts&gt;32768&lt;/maxattempts&gt;
        &lt;/standard&gt;
      &lt;/smchannel&gt;
      &lt;plot id="1"&gt;
        &lt;name&gt;Plot 1&lt;/name&gt;
        &lt;xaxis&gt;
          &lt;id&gt;Counter&lt;/id&gt;
          &lt;mode&gt;linear&lt;/mode&gt;
        &lt;/xaxis&gt;
        &lt;init&gt;false&lt;/init&gt;
        &lt;yaxis&gt;
          &lt;id&gt;bIICurrent:Mnt1chan1&lt;/id&gt;
          &lt;mode&gt;linear&lt;/mode&gt;
          &lt;modifier&gt;NONE&lt;/modifier&gt;
          &lt;linestyle&gt;Solid Line&lt;/linestyle&gt;
          &lt;color&gt;0000ff&lt;/color&gt;
          &lt;markstyle&gt;Point&lt;/markstyle&gt;
        &lt;/yaxis&gt;
      &lt;/plot&gt;
    &lt;/classic&gt;
  &lt;/scanmodule&gt;
  &lt;scanmodule id="2"&gt;
    &lt;name&gt;S APOS&lt;/name&gt;
    &lt;xpos&gt;129&lt;/xpos&gt;
    &lt;ypos&gt;117&lt;/ypos&gt;
    &lt;parent&gt;1&lt;/parent&gt;
    &lt;appended&gt;3&lt;/appended&gt;
    &lt;save_axis_positions&gt;
      &lt;smaxis&gt;
        &lt;axisid&gt;PPSMC:gw23715000&lt;/axisid&gt;
        &lt;stepfunction&gt;Plugin&lt;/stepfunction&gt;
        &lt;positionmode&gt;absolute&lt;/positionmode&gt;
        &lt;plugin name="MotionDisabled"&gt;
          &lt;parameter name="location"&gt;/path/to/referenceadd&lt;/parameter&gt;
        &lt;/plugin&gt;
      &lt;/smaxis&gt;
      &lt;smaxis&gt;
        &lt;axisid&gt;PPSMC:gw23715001&lt;/axisid&gt;
        &lt;stepfunction&gt;Plugin&lt;/stepfunction&gt;
        &lt;positionmode&gt;absolute&lt;/positionmode&gt;
        &lt;plugin name="MotionDisabled"&gt;
          &lt;parameter name="location"&gt;/path/to/referenceadd&lt;/parameter&gt;
        &lt;/plugin&gt;
      &lt;/smaxis&gt;
    &lt;/save_axis_positions&gt;
  &lt;/scanmodule&gt;
  &lt;scanmodule id="3"&gt;
    &lt;name&gt;S CVAL&lt;/name&gt;
    &lt;xpos&gt;288&lt;/xpos&gt;
    &lt;ypos&gt;139&lt;/ypos&gt;
    &lt;parent&gt;2&lt;/parent&gt;
    &lt;appended&gt;4&lt;/appended&gt;
    &lt;save_channel_values&gt;
      &lt;smchannel&gt;
        &lt;channelid&gt;bIICurrent:Mnt1chan1&lt;/channelid&gt;
        &lt;standard/&gt;
      &lt;/smchannel&gt;
      &lt;smchannel&gt;
        &lt;channelid&gt;bIICurrent:Mnt1lifeTimechan1&lt;/channelid&gt;
        &lt;standard/&gt;
      &lt;/smchannel&gt;
    &lt;/save_channel_values&gt;
  &lt;/scanmodule&gt;
  &lt;scanmodule id="4"&gt;
    &lt;name&gt;&lt;/name&gt;
    &lt;xpos&gt;176&lt;/xpos&gt;
    &lt;ypos&gt;237&lt;/ypos&gt;
    &lt;parent&gt;3&lt;/parent&gt;
    &lt;appended&gt;5&lt;/appended&gt;
    &lt;dynamic_axis_positions/&gt;
  &lt;/scanmodule&gt;
  &lt;scanmodule id="5"&gt;
    &lt;name&gt;&lt;/name&gt;
    &lt;xpos&gt;305&lt;/xpos&gt;
    &lt;ypos&gt;276&lt;/ypos&gt;
    &lt;parent&gt;4&lt;/parent&gt;
    &lt;dynamic_channel_values/&gt;
  &lt;/scanmodule&gt;
&lt;/scanmodules&gt;
 * </pre>
 * 
 * </li>
 * </ul>
 * </li>
 * <li>the simple type <code>saveAxisPositions</code> is removed:
 * 
 * <pre>
 *   &lt;simpleType name="saveAxisPositions"&gt;
    &lt;restriction base="string"&gt;
      &lt;enumeration value="never"&gt;&lt;/enumeration&gt;
      &lt;enumeration value="before"&gt;&lt;/enumeration&gt;
      &lt;enumeration value="after"&gt;&lt;/enumeration&gt;
      &lt;enumeration value="both"&gt;&lt;/enumeration&gt;
    &lt;/restriction&gt;
  &lt;/simpleType&gt;
 * </pre>
 * 
 * </li>
 * <li>the simple type <code>smtype</code> is removed:
 * 
 * <pre>
 *  &lt;simpleType name="smtype"&gt;
    &lt;restriction base="string"&gt;
      &lt;enumeration value="classic"&gt;&lt;/enumeration&gt;
      &lt;enumeration value="save_axis_positions"&gt;&lt;/enumeration&gt;
      &lt;enumeration value="save_channel_values"&gt;&lt;/enumeration&gt;
    &lt;/restriction&gt;
  &lt;/simpleType&gt;
 * </pre>
 * 
 * </li>
 * </ul>
 * 
 * @author Marcus Michalsky
 * @since 1.31
 */
public class Patch6o0T7o0 extends Patch {
	private static final Logger LOGGER = Logger.getLogger(Patch6o0T7o0.class.getName());
	private static Patch6o0T7o0 INSTANCE;

	private Patch6o0T7o0(Version source, Version target, List<Modification> modifications) {
		super(source, target, modifications);
		modifications.add(new Mod0(this));
		modifications.add(new Mod1(this));
		modifications.add(new Mod2(this));
	}

	public static Patch6o0T7o0 getInstance() {
		if (INSTANCE == null) {
			List<Modification> modifications = new LinkedList<>();
			INSTANCE = new Patch6o0T7o0(new Version(6, 0), new Version(7, 0), modifications);
		}
		return INSTANCE;
	}

	private class Mod0 extends AbstractModification {
		public Mod0(Patch patch) {
			super(patch, "setting scml version to 7.0");
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modify(Document document) {
			Node version = document.getElementsByTagName("version").item(0);
			if (version.getNodeType() == Node.ELEMENT_NODE) {
				((Text) version.getFirstChild()).setData("7.0");
			}
		}
	}

	private class Mod1 extends AbstractModification {
		public Mod1(Patch patch) {
			super(patch, "added type elements to scanmodules and adjusted content accordingly");
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modify(Document document) {
			NodeList scanModules = document.getElementsByTagName(Literals.XML_ELEMENT_NAME_SCANMODULE);
			for (Node scanModule : asList(scanModules)) {
				ScanModuleTypes smType = determineSMType(asList(scanModule.getChildNodes()));
				List<Node> childsToRemove = determineChildsToRemove(asList(scanModule.getChildNodes()));
				List<Node> smAxes = determineNodes(asList(scanModule.getChildNodes()),
						Literals.XML_ELEMENT_NAME_SMAXIS);
				List<Node> smChannels = determineNodes(asList(scanModule.getChildNodes()),
						Literals.XML_ELEMENT_NAME_SMCHANNEL);
				for (Node node : childsToRemove) {
					scanModule.removeChild(node);
				}
				Element typeElement = document.createElement(smType.toString());
				scanModule.appendChild(typeElement);
				switch (smType) {
				case CLASSIC:
					for (Node node : childsToRemove) {
						typeElement.appendChild(node);
					}
					break;
				case SAVE_AXIS_POSITIONS:
					for (Node smAxis : smAxes) {
						typeElement.appendChild(smAxis);
					}
					break;
				case SAVE_CHANNEL_VALUES:
					for (Node smChannel : smChannels) {
						typeElement.appendChild(smChannel);
					}
					break;
				default:
					break;
				}
			}
		}

		private ScanModuleTypes determineSMType(List<Node> smChilds) {
			for (Node child : smChilds) {
				if (child.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				if (child.getNodeName().equals(Literals.XML_ELEMENT_NAME_TYPE)) {
					switch (child.getTextContent()) {
					case Literals.XML_ELEMENT_NAME_SCANMODULE_CLASSIC:
						return ScanModuleTypes.CLASSIC;
					case Literals.XML_ELEMENT_NAME_SCANMODULE_SAVE_AXIS_POSITIONS:
						return ScanModuleTypes.SAVE_AXIS_POSITIONS;
					case Literals.XML_ELEMENT_NAME_SCANMODULE_SAVE_CHANNEL_VALUES:
						return ScanModuleTypes.SAVE_CHANNEL_VALUES;
					default:
						LOGGER.error("Scanmodule Type could not be determined!");
						return null;
					}
				}
			}
			LOGGER.error("No type element found for current scan module!");
			return null;
		}

		private List<Node> determineChildsToRemove(List<Node> smChilds) {
			List<Node> childsToRemove = new ArrayList<>();
			for (Node child : smChilds) {
				if (child.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				// collecting nodes that have to be modified
				switch (child.getNodeName()) {
				case Literals.XML_ELEMENT_NAME_VALUECOUNT:
				case Literals.XML_ELEMENT_NAME_SETTLETIME:
				case Literals.XML_ELEMENT_NAME_TRIGGERDELAY:
				case Literals.XML_ELEMENT_NAME_TRIGGERCONFIRMAXIS:
				case Literals.XML_ELEMENT_NAME_TRIGGERCONFIRMCHANNEL:
				case Literals.XML_ELEMENT_NAME_TRIGGEREVENT:
				case Literals.XML_ELEMENT_NAME_PAUSEEVENT:
				case Literals.XML_ELEMENT_NAME_REDOEVENT:
				case Literals.XML_ELEMENT_NAME_BREAKEVENT:
				case Literals.XML_ELEMENT_NAME_PRESCAN:
				case Literals.XML_ELEMENT_NAME_SMAXIS:
				case Literals.XML_ELEMENT_NAME_SMCHANNEL:
				case Literals.XML_ELEMENT_NAME_POSTSCAN:
				case Literals.XML_ELEMENT_NAME_POSITIONING:
				case Literals.XML_ELEMENT_NAME_PLOT:
					childsToRemove.add(child);
					break;
				default:
					break;
				}
			}
			return childsToRemove;
		}
		
		private List<Node> determineNodes(List<Node> smChilds, String nodeName) {
			List<Node> nodes = new ArrayList<>();
			for (Node child : smChilds) {
				if (child.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				if (child.getNodeName().equals(nodeName)) {
					nodes.add(child);
				}
			}
			return nodes;
		}
	}
	
	private class Mod2 extends AbstractModification {
		public Mod2(Patch patch) {
			super(patch, "remove type element from scanmodules");
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modify(Document document) {
			NodeList scanModules = document.getElementsByTagName(Literals.XML_ELEMENT_NAME_SCANMODULE);
			for (Node scanModule : asList(scanModules)) {
				Node typeNode = null;
				for (Node child : asList(scanModule.getChildNodes())) {
					if (child.getNodeType() != Node.ELEMENT_NODE) {
						continue;
					}
					if (child.getNodeName().equals(Literals.XML_ELEMENT_NAME_TYPE)) {
						typeNode = child;
					}
				}
				if (typeNode != null) {
					try {
						scanModule.removeChild(typeNode);
					} catch (DOMException e) {
						LOGGER.error(e.getMessage(), e);
					}
				}
			}
		}
	}
}
