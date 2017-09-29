package de.ptb.epics.eve.editor.views.detectorchannelview.ui;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.util.io.StringUtil;

/**
 * @author Marcus Michalsky
 * @since 1.27
 */
public class NormalizeComposite extends Composite {
	Label infoText;
	
	public NormalizeComposite(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		this.setLayout(gridLayout);
		Label infoImage = new Label(this, SWT.NONE);
		infoImage.setImage(Activator.getDefault().getImageRegistry().get("INFO"));
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.TOP;
		infoImage.setLayoutData(gridData);
		
		this.infoText = new Label(this, SWT.WRAP);
		this.infoText.setText("Channel is used as Normalize Channel");
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		this.infoText.setLayoutData(gridData);
	}
	
	public void setChannels(List<Channel> channels) {
		this.infoText.setText("Channel is used as Normalize Channel by " + 
				StringUtil.buildCommaSeparatedString(channels));
		this.layout();
	}
}