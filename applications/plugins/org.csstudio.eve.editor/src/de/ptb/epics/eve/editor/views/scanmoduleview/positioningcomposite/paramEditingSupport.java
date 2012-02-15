package de.ptb.epics.eve.editor.views.scanmoduleview.positioningcomposite;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.ptb.epics.eve.data.scandescription.PluginController;
import de.ptb.epics.eve.data.scandescription.Positioning;
import de.ptb.epics.eve.editor.dialogs.PluginControllerDialog;

/**
 * 
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class paramEditingSupport extends EditingSupport {

	private TableViewer viewer;
	
	/**
	 * Constructor.
	 * 
	 * @param viewer
	 */
	public paramEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		return new DialogCellEditor() {
			@Override protected Button createButton(Composite parent) {
				final Button button = new Button(parent, SWT.PUSH);
				button.setText("Edit");
				return button; 
			}
			
			@Override protected Object openDialogBox(Control cellEditorWindow) {
				PluginControllerDialog dialog = new PluginControllerDialog(
						null, (PluginController)this.getValue());
				dialog.setBlockOnOpen(true);
				dialog.open();
				return null;
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
		return positioning.getPluginController();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected void setValue(Object element, Object value) {
		// TODO Auto-generated method stub

	}
}