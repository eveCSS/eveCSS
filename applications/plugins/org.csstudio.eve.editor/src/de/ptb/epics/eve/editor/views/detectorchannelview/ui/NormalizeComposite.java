package de.ptb.epics.eve.editor.views.detectorchannelview.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.editor.Activator;

/**
 * @author Marcus Michalsky
 * @since 1.27
 */
public class NormalizeComposite extends Composite {
	Label infoText;
	
	public NormalizeComposite(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new RowLayout());
		Label infoImage = new Label(this, SWT.NONE);
		infoImage.setImage(Activator.getDefault().getImageRegistry().get("INFO"));
		this.infoText = new Label(this, SWT.NONE);
		this.infoText.setText("Channel is used as Normalize Channel");
	}
	
	public void setChannel(Channel channel) {
		this.infoText.setText("Channel is used as Normalize Channel by " + channel.getDetectorChannel().getName());
		this.layout();
	}
}