package de.ptb.epics.eve.editor.views.scanmoduleview.detectorchannelcomposite;

import javafx.collections.ListChangeListener;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ScanModule;

/**
 * @author Marcus Michalsky
 * @since 1.22
 */
public class ContentProvider implements IStructuredContentProvider,
		ListChangeListener<Channel> {
	private static final Logger LOGGER = Logger.getLogger(ContentProvider.class
			.getName());
	private TableViewer viewer;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		return ((ScanModule)inputElement).getChannels();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TableViewer)viewer;
		if (oldInput != null) {
			((ScanModule)oldInput).removeChannelChangeListener(this);
		}
		if (newInput != null) {
			((ScanModule)newInput).addChannelChangeListener(this);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onChanged(
			javafx.collections.ListChangeListener.Change<? extends Channel> change) {
		this.viewer.refresh();
		while (change.next()) {
			if (change.wasAdded()) {
				for (Channel channel : change.getAddedSubList()) {
					LOGGER.debug("set selection to: " + 
							channel.getDetectorChannel().getName());
					this.viewer.setSelection(
							new StructuredSelection(channel), true);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
	}
}