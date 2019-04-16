package de.ptb.epics.eve.editor.views.scanmoduleview.classiccomposite.plotcomposite;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.scandescription.PlotWindow;

/**
 * @author Marcus Michalsky
 * @since 1.30
 */
public class IdEditingSupport extends EditingSupport {
	private static Image ERROR_IMG = PlatformUI.getWorkbench().getSharedImages().
			getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
	
	private TableViewer viewer;
	
	public IdEditingSupport(ColumnViewer viewer) {
		super(viewer);
		this.viewer = (TableViewer)viewer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		final TextCellEditor editor = new TextCellEditor(this.viewer.getTable()) {
			@Override protected void focusLost() {
				if(isActivated()) {
					fireCancelEditor();
				}
				deactivate();
			}
		};
		editor.setValidator(new ICellEditorValidator() {
					
					@Override
					public String isValid(Object value) {
						try {
							Integer.parseInt((String)value);
						} catch (NumberFormatException e) {
							return e.getMessage();
						}
						return null;
					}
				});
		editor.addListener(new ICellEditorListener() {
			
			@Override
			public void editorValueChanged(boolean oldValidState,
					boolean newValidState) {
				if (!editor.isValueValid()) {
				viewer.getTable().getColumn(1).setImage(
						IdEditingSupport.ERROR_IMG);
				} else {
					viewer.getTable().getColumn(1).setImage(null);
				}
			}
			
			@Override
			public void cancelEditor() {
				viewer.getTable().getColumn(1).setImage(null);
			}
			
			@Override
			public void applyEditorValue() {
				viewer.getTable().getColumn(1).setImage(null);
			}
		});
		return editor;
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
		return Integer.toString(((PlotWindow)element).getId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {
		if (value == null) {
			return;
		}
		((PlotWindow)element).setId(Integer.parseInt((String)value));
	}
}