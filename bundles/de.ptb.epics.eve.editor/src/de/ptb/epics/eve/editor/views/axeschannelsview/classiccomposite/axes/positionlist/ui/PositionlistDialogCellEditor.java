package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.positionlist.ui;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.ui.ValuesDialogCellEditor;
import de.ptb.epics.eve.util.io.StringUtil;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class PositionlistDialogCellEditor extends ValuesDialogCellEditor {
	private static final Logger LOGGER = Logger.getLogger(
			PositionlistDialogCellEditor.class.getName());
	
	public PositionlistDialogCellEditor(Composite parent, Axis axis) {
		super(parent, axis);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String validate(String positions) {
		if (positions == null) {
			return null;
		}
		if (positions.isEmpty()) {
			LOGGER.debug("validation failed: positionlist is empty");
			return "Positionlist is empty.";
		}
		boolean valid = false;
		switch (getAxis().getType()) {
		case DOUBLE:
			valid = StringUtil.isPositionList(positions, Double.class);
			break;
		case INT:
			valid = StringUtil.isPositionList(positions, Integer.class);
			break;
		case STRING:
			valid = StringUtil.isPositionList(positions, String.class);
			break;
		default:
			LOGGER.debug("validation failed: wrong type");
			return "Only axes of type int, double or string are supported.";
		}
		if (valid) {
			LOGGER.debug("validation ok");
			return null;
		}
		LOGGER.debug("validation failed: syntax errors");
		return "The list contains syntax errors";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		new PositionlistDialog(cellEditorWindow.getShell(), getControl(), 
				getAxis()).open();
		return getAxis().getPositionlist();
	}

}
