package de.ptb.epics.eve.editor.views.chainview;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.actions.CompoundContributionItem;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public class PauseConditionsMenuContribution extends CompoundContributionItem {
	private static final Logger LOGGER = Logger.getLogger(
			PauseConditionsMenuContribution.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IContributionItem[] getContributionItems() {
		ArrayList<IContributionItem> result = new ArrayList<>();
		// TODO Auto-generated method stub
		return result.toArray(new IContributionItem[0]);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDynamic() {
		return true;
	}
}
