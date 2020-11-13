package de.ptb.epics.eve.editor.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import de.ptb.epics.eve.data.scandescription.PluginController;
import de.ptb.epics.eve.editor.views.PluginControllerComposite;

/**
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class PluginControllerDialog extends TitleAreaDialog {

	private PluginController pluginController;
	private PluginControllerComposite pluginControllerComposite;
	
	/**
	 * Constructs a <code>PluginControllerDialog</code>.
	 * 
	 * @param shell the shell the dialog should appear in
	 * @param pluginController the plug in controller 
	 */
	public PluginControllerDialog(final Shell shell, 
								  final PluginController pluginController) {
		super(shell);
		this.pluginController = pluginController;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(final Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		
		this.pluginControllerComposite = 
			new PluginControllerComposite(area, SWT.NONE);
		this.pluginControllerComposite.setLayoutData(
			new GridData(GridData.FILL_BOTH));
		
		this.pluginControllerComposite.setPluginController(
			this.pluginController);
		
		this.setTitle("Plugin Options");
		this.setMessage("");
		
		return area;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID, "Close", true);
	}
}