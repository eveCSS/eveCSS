package de.ptb.epics.eve.editor.views.scanmoduleview.positioningcomposite;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.Positioning;
import de.ptb.epics.eve.data.scandescription.ScanModule;

/**
 * {@link org.eclipse.jface.viewers.EditingSupport} for the channel column.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class ChannelEditingSupport extends EditingSupport {

	private static Logger logger = 
			Logger.getLogger(ChannelEditingSupport.class.getName());
	
	private TableViewer viewer;
	
	private List<String> channelStrings;
	private List<DetectorChannel> channels;
	
	/**
	 * Constructor.
	 * 
	 * @param viewer
	 */
	public ChannelEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		ScanModule scanModule = (ScanModule)this.viewer.getInput();
		channelStrings = new ArrayList<String>();
		channels = new ArrayList<DetectorChannel>();
		for(Channel ch : scanModule.getChannels()) {
			channels.add(ch.getDetectorChannel());
			channelStrings.add(ch.getDetectorChannel().getName());
		}
		return new ComboBoxCellEditor(this.viewer.getTable(), 
				channelStrings.toArray(new String[0]), SWT.READ_ONLY) {
			@Override protected void focusLost() {
				if(isActivated()) {
					fireCancelEditor();
				}
				deactivate();
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getValue(Object element) {
		final Positioning positioning = (Positioning)element;
		if(positioning.getDetectorChannel() != null) {
			return channels.indexOf(positioning.getDetectorChannel());
		}
		return 0;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void setValue(Object element, Object value) {
		final Positioning positioning = (Positioning)element;
		DetectorChannel ch = channels.get((Integer)value);
		positioning.setDetectorChannel(ch);
		if(logger.isDebugEnabled()) {
			logger.debug("set channel " + ch.getName() + " for positioning " + 
						positioning.getMotorAxis().getName());
		}
	}
}