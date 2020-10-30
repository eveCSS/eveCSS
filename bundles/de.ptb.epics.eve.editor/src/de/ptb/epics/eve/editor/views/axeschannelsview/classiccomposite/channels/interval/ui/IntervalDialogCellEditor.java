package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.interval.ui;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.ptb.epics.eve.data.scandescription.Channel;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class IntervalDialogCellEditor extends DialogCellEditor {
	private Channel channel;
	
	public IntervalDialogCellEditor(Composite parent, Channel channel) {
		super(parent, SWT.NONE);
		this.channel = channel;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		new ChannelIntervalDialog(cellEditorWindow.getShell(), getControl(), 
				channel).open();
		return channel;
	}
}
