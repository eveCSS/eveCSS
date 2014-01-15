package de.ptb.epics.eve.editor.gef.editparts.tree;

import java.util.List;

import org.eclipse.gef.editparts.AbstractTreeEditPart;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.ScanDescription;

/**
 * @author Marcus Michalsky
 * @since 1.18
 */
public class ScanDescriptionTreeEditPart extends AbstractTreeEditPart {
	
	protected List<Chain> getModelChildren() {
		return ((ScanDescription)getModel()).getChains();
	}
}