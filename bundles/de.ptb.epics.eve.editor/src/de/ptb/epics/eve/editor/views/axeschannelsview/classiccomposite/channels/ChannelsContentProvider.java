package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import javafx.collections.ListChangeListener;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class ChannelsContentProvider implements IStructuredContentProvider, ListChangeListener<Channel> {
	private static final Logger LOGGER = Logger.getLogger(
			ChannelsContentProvider.class.getName());
	private TableViewer viewer;
	private ScanModule scanModule;
	
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
			this.scanModule = ((ScanModule)newInput);
		} else {
			this.scanModule = null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onChanged(Change<? extends Channel> change) {
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
		if (this.scanModule != null) {
			this.scanModule.removeChannelChangeListener(this);
		}
	}
}
