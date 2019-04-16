package de.ptb.epics.eve.editor.views.scanmoduleview.classiccomposite.postscancomposite;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.scandescription.Postscan;
import de.ptb.epics.eve.util.ui.jface.MyComboBoxCellEditor;

/**
 * {@link org.eclipse.jface.viewers.EditingSupport} for the value column.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class ValueEditingSupport extends EditingSupport {

	private TableViewer viewer;
	
	/**
	 * Constructor.
	 * 
	 * @param viewer
	 */
	public ValueEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		final Postscan postscan = (Postscan)element;
		if(postscan.getAbstractPrePostscanDevice().isDiscrete()) {
			List<String> discreteValues = new ArrayList<String>();
			if (postscan.getAbstractPrePostscanDevice().getValue().
					getType().equals(DataTypes.ONOFF)) {
				discreteValues.add("On");
				discreteValues.add("Off");
			} else if (postscan.getAbstractPrePostscanDevice().getValue().
					getType().equals(DataTypes.OPENCLOSE)) {
				discreteValues.add("Open");
				discreteValues.add("Close");
			} else {
				discreteValues.addAll(postscan.getAbstractPrePostscanDevice().
						getValue().getDiscreteValues());
			}
			return new MyComboBoxCellEditor(this.viewer.getTable(), 
					discreteValues.toArray(new String[0]));
		} else {
			return new TextCellEditor(this.viewer.getTable()) {
				@Override protected void focusLost() {
					if(isActivated()) {
						fireCancelEditor();
					}
					deactivate();
				}
			};
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean canEdit(Object element) {
		final Postscan postscan = (Postscan)element;
		if (postscan.isReset()) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getValue(Object element) {
		final Postscan postscan = (Postscan)element;
		if(postscan.getAbstractPrePostscanDevice().isDiscrete()) {
			if (postscan.getAbstractPrePostscanDevice().getValue().
					getType().equals(DataTypes.ONOFF)) {
				if (postscan.getValue().equals("On")) {
					return 0;
				} else if (postscan.getValue().equals("Off")) {
					return 1;
				} else {
					return 0;
				}
			} else if (postscan.getAbstractPrePostscanDevice().getValue().
					getType().equals(DataTypes.OPENCLOSE)) {
				if (postscan.getValue().equals("Open")) {
					return 0;
				} else if (postscan.getValue().equals("Close")) {
					return 1;
				} else {
					return 0;
				}
			} else {
				int index = postscan.getAbstractPrePostscanDevice().getValue().
						getDiscreteValues().indexOf(postscan.getValue());
				return index == -1 ? 0 : index;
			}
		} else {
			return postscan.getValue();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {
		final Postscan postscan = (Postscan)element;
		if(postscan.getAbstractPrePostscanDevice().isDiscrete()) {
			if (postscan.getAbstractPrePostscanDevice().getValue().
					getType().equals(DataTypes.ONOFF)) {
				if (((Integer)value) == 0) {
					postscan.setValue("On");
				} else {
					postscan.setValue("Off");
				}
			} else if (postscan.getAbstractPrePostscanDevice().getValue().
					getType().equals(DataTypes.OPENCLOSE)) {
				if (((Integer)value) == 0) {
					postscan.setValue("Open");
				} else {
					postscan.setValue("Close");
				}
			} else {
				postscan.setValue(postscan.getAbstractPrePostscanDevice().
					getValue().getDiscreteValues().get((Integer)value));
			}
		} else {
			postscan.setValue((String)value);
		}
	}
}