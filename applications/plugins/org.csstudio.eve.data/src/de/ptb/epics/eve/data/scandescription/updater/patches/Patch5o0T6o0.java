package de.ptb.epics.eve.data.scandescription.updater.patches;

import static de.ptb.epics.eve.util.xml.XMLUtil.asList;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import de.ptb.epics.eve.data.scandescription.YAxisModifier;
import de.ptb.epics.eve.data.scandescription.processors.Literals;
import de.ptb.epics.eve.data.scandescription.updater.AbstractModification;
import de.ptb.epics.eve.data.scandescription.updater.Modification;
import de.ptb.epics.eve.data.scandescription.updater.Patch;
import de.ptb.epics.eve.util.data.Version;

/**
 *  * Patches SCML v5.0 to v6.0 doing the following:
 * 
 * <ul>
 * <li>increment version to 6.0</li>
 * <li>introduces a modifier element (for yaxes of plotwindow) as follows:
 * <ul>
 * <li>schema definition changes from:
 * 
 * <pre>
 *  &lt;complexType name=&quot;plotaxis&quot;&gt;
 *    &lt;sequence&gt;
 *      &lt;element name=&quot;id&quot; type=&quot;tns:identifier&quot;&gt;&lt;/element&gt;
 *      &lt;element name=&quot;mode&quot; type=&quot;tns:plotmode&quot;&gt;&lt;/element&gt;
 *      &lt;element name=&quot;normalize_id&quot; type=&quot;tns:identifier&quot; minOccurs=&quot;0&quot;&gt;&lt;/element&gt;
 *      &lt;element name=&quot;linestyle&quot; type=&quot;string&quot;&gt;&lt;/element&gt;
 *      &lt;element name=&quot;color&quot; type=&quot;tns:color&quot;&gt;&lt;/element&gt;
 *      &lt;element name=&quot;markstyle&quot; type=&quot;string&quot;&gt;&lt;/element&gt;
 *    &lt;/sequence&gt;
 *  &lt;/complexType&gt;
 * </pre>
 * 
 * to
 * 
 * <pre>
 *  &lt;simpleType name=&quot;axismodifier&quot;&gt;
 *    &lt;restriction base=&quot;string&quot;&gt;
 *      &lt;enumeration value=&quot;NONE&quot;&gt;&lt;/enumeration&gt;
 *      &lt;enumeration value=&quot;INVERSE&quot;&gt;&lt/enumeration&gt;
 *    &lt;/restriction&gt;
 *  &lt;/simpleType&gt;
 *
 *  &lt;complexType name=&quot;plotaxis&quot;&gt;
 *    &lt;sequence&gt;
 *      &lt;element name=&quot;id&quot; type=&quot;tns:identifier&quot;&gt;&lt;/element&gt;
 *      &lt;element name=&quot;mode&quot; type=&quot;tns:plotmode&quot;&gt;&lt;/element&gt;
 *      &lt;element name=&quot;modifier&quot; type=&quot;tns:axismodifier&quot;&gt;&lt;/element&gt;
 *      &lt;element name=&quot;normalize_id&quot; type=&quot;tns:identifier&quot; minOccurs=&quot;0&quot;&gt;&lt;/element&gt;
 *      &lt;element name=&quot;linestyle&quot; type=&quot;string&quot;&gt;&lt;/element&gt;
 *      &lt;element name=&quot;color&quot; type=&quot;tns:color&quot;&gt;&lt;/element&gt;
 *      &lt;element name=&quot;markstyle&quot; type=&quot;string&quot;&gt;&lt;/element&gt;
 *    &lt;/sequence&gt;
 *  &lt;/complexType&gt;
 * </pre>
 * </li>
 * </ul>
 * </li>
 * <li>
 *   introduces a new stepfunction called range
 *   <ul>
 *     <li>
 *       schema definition changes from:
 *       
 *       <pre>
 *       &lt;complexType name="smaxis"&gt;
           &lt;sequence&gt;
             &lt;element name="axisid" type="tns:identifier"&gt;&lt;/element&gt;
             &lt;element name="stepfunction" type="tns:stepfunction"&gt;&lt;/element&gt;
             &lt;element name="positionmode"&gt;
               &lt;simpleType&gt;
                 &lt;restriction base="string"&gt;
                   &lt;enumeration value="relative"&gt;&lt;/enumeration&gt;
                   &lt;enumeration value="absolute"&gt;&lt;/enumeration&gt;
                 &lt;/restriction&gt;
               &lt;/simpleType&gt;
             &lt;/element&gt;
             &lt;choice&gt;
               &lt;element name="startstopstep" type="tns:startstopstep"&gt;&lt;/element&gt;
               &lt;element name="stepfilename" type="string"&gt;&lt;/element&gt;
               &lt;element name="plugin" type="tns:controller"&gt;&lt;/element&gt;
               &lt;element name="positionlist" type="string"&gt;&lt;/element&gt;
             &lt;/choice&gt;
           &lt;/sequence&gt;
         &lt;/complexType&gt;

         &lt;simpleType name="stepfunction"&gt;
           &lt;restriction base="string"&gt;
             &lt;enumeration value="Add"&gt;&lt;/enumeration&gt;
             &lt;enumeration value="Multiply"&gt;&lt;/enumeration&gt;
             &lt;enumeration value="File"&gt;&lt;/enumeration&gt;
             &lt;enumeration value="Plugin"&gt;&lt;/enumeration&gt;
             &lt;enumeration value="Positionlist"&gt;&lt;/enumeration&gt;
           &lt;/restriction&gt;
         &lt;/simpleType&gt;
 *       </pre>
 *       to
 *       <pre>
 *       &lt;complexType name="smaxis"&gt;
           &lt;sequence&gt;
             &lt;element name="axisid" type="tns:identifier"&gt;&lt;/element&gt;
             &lt;element name="stepfunction" type="tns:stepfunction"&gt;&lt;/element&gt;
             &lt;element name="positionmode"&gt;
               &lt;simpleType&gt;
                 &lt;restriction base="string"&gt;
                   &lt;enumeration value="relative"&gt;&lt;/enumeration&gt;
                   &lt;enumeration value="absolute"&gt;&lt;/enumeration&gt;
                 &lt;/restriction&gt;
               &lt;/simpleType&gt;
             &lt;/element&gt;
             &lt;choice&gt;
               &lt;element name="startstopstep" type="tns:startstopstep"&gt;&lt;/element&gt;
               &lt;element name="stepfilename" type="string"&gt;&lt;/element&gt;
               &lt;element name="plugin" type="tns:controller"&gt;&lt;/element&gt;
               &lt;element name="positionlist" type="string"&gt;&lt;/element&gt;
               &lt;element name="range" type="string"&gt;&lt;/element&gt;
             &lt;/choice&gt;
           &lt;/sequence&gt;
         &lt;/complexType&gt;

         &lt;simpleType name="stepfunction"&gt;
           &lt;restriction base="string"&gt;
             &lt;enumeration value="Add"&gt;&lt;/enumeration&gt;
             &lt;enumeration value="Multiply"&gt;&lt;/enumeration&gt;
             &lt;enumeration value="File"&gt;&lt;/enumeration&gt;
             &lt;enumeration value="Plugin"&gt;&lt;/enumeration&gt;
             &lt;enumeration value="Positionlist"&gt;&lt;/enumeration&gt;
             &lt;enumeration value="Range"&gt;&lt;/enumeration&gt;
           &lt;/restriction&gt;
         &lt;/simpleType&gt;
 *       </pre>
 *     </li>
 *   </ul>
 * </li>
 * <li>removed smselections
 * <ul>
 * <li>removed the following definitions
 * <pre>
 *   &lt;complexType name="selection"&gt;
     &lt;sequence&gt;
       &lt;element name="stepfunction" type="tns:stepfunctionlist"&gt;&lt;/element&gt;
       &lt;element name="smtype" type="string"&gt;&lt;/element&gt;
     &lt;/sequence&gt;
   &lt;/complexType&gt;

   &lt;simpleType name="stepfunctionlist"&gt;
     &lt;restriction base="string"&gt;
       &lt;pattern value="Add,Multiply,File,Plugin,Positionlist"/&gt;
     &lt;/restriction&gt;
   &lt;/simpleType&gt;
 * </pre>
 * </li>
 * <li>removed the following element from the "scml" element:
 * <pre>
 *  &lt;element name="smselection" type="tns:selection" minOccurs="0"&gt;&lt;/element&gt;
 * </pre>
 * </li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @author Marcus Michalsky
 * @since 1.28
 */
public class Patch5o0T6o0 extends Patch {
	private static Patch5o0T6o0 INSTANCE;
	
	
	private Patch5o0T6o0(Version source, Version target, List<Modification> modifications) {
		super(source, target, modifications);
		modifications.add(new Mod0(this));
		modifications.add(new Mod1(this));
		modifications.add(new Mod2(this));
	}
	
	public static Patch5o0T6o0 getInstance() {
		if (INSTANCE == null) {
			List<Modification> modifications = new LinkedList<Modification>();
			
			INSTANCE = new Patch5o0T6o0(new Version(5, 0), new Version(6, 0), modifications);
		}
		return INSTANCE;
	}
	
	private class Mod0 extends AbstractModification {
		public Mod0(Patch patch) {
			super(patch, "setting scml version to 6.0");
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modify(Document document) {
			Node version = document.getElementsByTagName("version").item(0);
			if (version.getNodeType() == Node.ELEMENT_NODE) {
				((Text)version.getFirstChild()).setData("6.0");
			}
		}
	}
	
	private class Mod1 extends AbstractModification {
		public Mod1(Patch patch) {
			super(patch, "add modifier element to plot y axes");
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modify(Document document) {
			NodeList yAxes = document.getElementsByTagName("yaxis");
			for (Node yAxis : asList(yAxes)) {
				Node previousSibling = null;
				for (Node child : asList(yAxis.getChildNodes())) {
					if (child.getNodeType() != Node.ELEMENT_NODE) {
						continue;
					}
					if (child.getNodeName().equals("mode")) {
						previousSibling = child;
					}
				}
				if (previousSibling.getNextSibling() != null) {
					Element modifierElement = document.createElement(
							Literals.XML_ELEMENT_NAME_MODIFIER);
					Text text = document.createTextNode(YAxisModifier.NONE.name());
					modifierElement.appendChild(text);
					yAxis.insertBefore(modifierElement, previousSibling.getNextSibling());
				}
			}
		}
	}
	
	private class Mod2 extends AbstractModification {
		public Mod2(Patch patch) {
			super(patch, "remove smselection element from scml element");
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modify(Document document) {
			Node selection = document.getElementsByTagName("smselection").item(0);
			selection.getParentNode().removeChild(selection);
		}
	}
}