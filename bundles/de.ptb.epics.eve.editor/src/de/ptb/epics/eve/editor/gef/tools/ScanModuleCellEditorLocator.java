package de.ptb.epics.eve.editor.gef.tools;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Text;

import de.ptb.epics.eve.editor.gef.figures.ScanModuleFigure;

/**
 * Calculates where the cell editor appears within the edit part.
 * 
 * @author Marcus Michalsky
 * @since 1.6
 */
public class ScanModuleCellEditorLocator implements CellEditorLocator {

	private ScanModuleFigure figure;
	
	/**
	 * Constructor.
	 * 
	 * @param figure the host figure
	 */
	public ScanModuleCellEditorLocator(ScanModuleFigure figure) {
		this.figure = figure;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void relocate(CellEditor cellEditor) {
		Text text = (Text) cellEditor.getControl();
		Point pref = text.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		Rectangle rect = figure.getBounds().getCopy();
		figure.translateToAbsolute(rect);
		text.setBounds(rect.x - 1, rect.y - 1, pref.x + 1, pref.y + 1);
	}
}