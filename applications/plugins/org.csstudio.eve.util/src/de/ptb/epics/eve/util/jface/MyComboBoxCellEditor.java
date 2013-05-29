package de.ptb.epics.eve.util.jface;

import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Differs in behavior from {@link org.eclipse.jface.viewers.ComboBoxCellEditor} 
 * in the way that a selected value (mouse click) will be applied immediately.
 * <p>
 * It addresses the discomfort mentioned in feature #757 and Eclipse Bugs 
 * <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=285612">285612</a>, 
 * <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=230398">230398</a>, 
 * <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=54989">54989</a>.
 * <p>
 * <b>Use with Caution:</b> Neither the assumption that the event times are 
 * equal nor the assumption that the selection listener is always notified last 
 * could be verified via public API documentation!
 * 
 * @author Marcus Michalsky
 * @since 1.12
 */
public class MyComboBoxCellEditor extends ComboBoxCellEditor {
	
	private long timeOfNonFinalSelectionEvent = 0;
	
	/**
	 * @param parent the parent
	 * @param items the items
	 */
	protected MyComboBoxCellEditor(Composite parent, String[] items) {
		super(parent, items, SWT.READ_ONLY);
		setActivationStyle(DROP_DOWN_ON_MOUSE_ACTIVATION);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createControl(Composite parent) {
		Control comboBox = super.createControl(parent);
		
		if (comboBox instanceof CCombo) {
			final CCombo cCombo = (CCombo)comboBox;
			
			cCombo.addSelectionListener(new SelectionAdapter() {
				@Override public void widgetSelected(SelectionEvent e) {
					super.widgetSelected(e);
					if (isFinalSelection(e)) {
						fireApplyEditorValue();
					}
				}
				
				private boolean isFinalSelection(SelectionEvent e) {
					return e.time != timeOfNonFinalSelectionEvent;
				}
			});
			
			cCombo.addMouseWheelListener(new MouseWheelListener() {
				@Override public void mouseScrolled(MouseEvent e) {
					timeOfNonFinalSelectionEvent = e.time;
				}
			});
			
			cCombo.addKeyListener(new KeyAdapter() {
				@Override public void keyPressed(KeyEvent e) {
					if (e.keyCode == SWT.ARROW_UP || 
							e.keyCode == SWT.ARROW_DOWN) {
						timeOfNonFinalSelectionEvent = e.time;
					}
				}
			});
		}
		return comboBox;
	}
}