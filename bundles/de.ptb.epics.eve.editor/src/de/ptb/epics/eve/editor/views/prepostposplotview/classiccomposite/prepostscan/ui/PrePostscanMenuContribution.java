package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.prepostscan.ui;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.actions.CompoundContributionItem;

import de.ptb.epics.eve.editor.Activator;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class PrePostscanMenuContribution extends CompoundContributionItem {
	private static final Logger LOGGER = Logger.getLogger(
			PrePostscanMenuContribution.class.getName());
	
	private final ImageDescriptor axisImage = ImageDescriptor.createFromImage(
			Activator.getDefault().getImageRegistry().get("AXIS"));
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IContributionItem[] getContributionItems() {
		// TODO
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDynamic() {
		return true;
	}

}
