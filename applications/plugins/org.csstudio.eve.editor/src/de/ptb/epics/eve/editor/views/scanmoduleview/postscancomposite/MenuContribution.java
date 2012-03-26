package de.ptb.epics.eve.editor.views.scanmoduleview.postscancomposite;

import java.util.ArrayList;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.actions.CompoundContributionItem;

import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView;

/**
 * @author Marcus Michalsky
 * @since 1.2
 */
public class MenuContribution extends CompoundContributionItem {

	final ImageDescriptor classImage = ImageDescriptor.createFromImage(
			Activator.getDefault().
			getImageRegistry().get("CLASS"));
	final ImageDescriptor motorImage = ImageDescriptor.createFromImage(
			Activator.getDefault().
			getImageRegistry().get("MOTOR"));
	final ImageDescriptor axisImage = ImageDescriptor.createFromImage(
			Activator.getDefault().
			getImageRegistry().get("AXIS"));
	final ImageDescriptor detectorImage = ImageDescriptor.createFromImage(
			Activator.getDefault().
			getImageRegistry().get("DETECTOR"));
	final ImageDescriptor channelImage = ImageDescriptor.createFromImage(
			Activator.getDefault().
			getImageRegistry().get("CHANNEL"));
	final ImageDescriptor deviceImage = ImageDescriptor.createFromImage(
			Activator.getDefault().
			getImageRegistry().get("DEVICE"));
	final ImageDescriptor optionImage = ImageDescriptor.createFromImage(
			Activator.getDefault().
			getImageRegistry().get("OPTION"));

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IContributionItem[] getContributionItems() {
		ArrayList<IContributionItem> result = new ArrayList<IContributionItem>();
		
		ScanModule sm = ((ScanModuleView)Activator.getDefault().getWorkbench().
				getActiveWorkbenchWindow().getPartService().getActivePart()).
				getCurrentScanModule();
		
		
		
		// TODO Auto-generated method stub
		return null;
	}
}