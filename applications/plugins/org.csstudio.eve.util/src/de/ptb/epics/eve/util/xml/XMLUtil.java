package de.ptb.epics.eve.util.xml;

import java.util.AbstractList;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Marcus Michalsky
 * @since 1.23
 * @see http://stackoverflow.com/questions/19589231/can-i-iterate-through-nodelist-using-foreach-in-java
 */
public class XMLUtil {
	private XMLUtil() {
	}
	
	public static List<Node> asList(NodeList nodeList) {
		return nodeList.getLength() == 0 ?
				Collections.<Node>emptyList() : new NodeListWrapper(nodeList);
	}
	
	private static final class NodeListWrapper extends AbstractList<Node>
			implements RandomAccess {
		private final NodeList nodeList;
		
		NodeListWrapper(NodeList nodeList) {
			this.nodeList = nodeList;
		}
		
		@Override
		public Node get(int index) {
			return nodeList.item(index);
		}

		@Override
		public int size() {
			return nodeList.getLength();
		}
	}
}