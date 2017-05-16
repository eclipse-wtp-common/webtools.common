/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Tom Schindl <tom.schindl@bestsolution.at> - bugfix in 174739
 *     Konstantin Komissarchik - mproving behavior in the read-only case 
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.ui.internal.util;

import java.text.MessageFormat; // Not using ICU to support standalone JFace scenario
import java.util.ArrayList;
import java.util.EventListener;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleControlAdapter;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.accessibility.AccessibleTextAdapter;
import org.eclipse.swt.accessibility.AccessibleTextEvent;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TypedListener;

/**
 * A cell editor that presents a list of items in a combo box.
 * The cell editor's value is the zero-based index of the selected
 * item.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 */

// The code contained in this file has been copied from SWT/JFace to experiment
// with improving behavior of the ComboBoxCellEditor in the read-only case. 
// The changes made to the code have been annotated with "kosta" comments.
// Eventually these should be converted into patches for incorporation back
// into SWT/JFace.

public class ReadOnlyComboBoxCellEditor extends CellEditor {

    /**
     * The list of items to present in the combo box.
     */
    private String[] items;

    /**
     * The zero-based index of the selected item.
     */
    int selection;

    /**
     * The custom combo box control.
     */
    CustomCCombo comboBox;

    /**
     * Default ComboBoxCellEditor style
     */
    private static final int defaultStyle = SWT.NONE;

    /**
     * Creates a new cell editor with no control and no  st of choices. Initially,
     * the cell editor has no cell validator.
     * 
     * @since 2.1
     * @see CellEditor#setStyle
     * @see CellEditor#create
     * @see ReadOnlyComboBoxCellEditor#setItems
     * @see CellEditor#dispose
     */
    public ReadOnlyComboBoxCellEditor() {
        setStyle(defaultStyle);
    }

    /**
     * Creates a new cell editor with a combo containing the given 
     * list of choices and parented under the given control. The cell
     * editor value is the zero-based index of the selected item.
     * Initially, the cell editor has no cell validator and
     * the first item in the list is selected. 
     *
     * @param parent the parent control
     * @param items the list of strings for the combo box
     */
    public ReadOnlyComboBoxCellEditor(Composite parent, String[] items) {
        this(parent, items, defaultStyle);
    }

    /**
     * Creates a new cell editor with a combo containing the given 
     * list of choices and parented under the given control. The cell
     * editor value is the zero-based index of the selected item.
     * Initially, the cell editor has no cell validator and
     * the first item in the list is selected. 
     *
     * @param parent the parent control
     * @param items the list of strings for the combo box
     * @param style the style bits
     * @since 2.1
     */
    public ReadOnlyComboBoxCellEditor(Composite parent, String[] items, int style) {
        super(parent, style);
        setItems(items);
    }

    /**
     * Returns the list of choices for the combo box
     *
     * @return the list of choices for the combo box
     */
    public String[] getItems() {
        return this.items;
    }

    /**
     * Sets the list of choices for the combo box
     *
     * @param items the list of choices for the combo box
     */
    public void setItems(String[] items) {
        Assert.isNotNull(items);
        this.items = items;
        populateComboBoxItems();
    }

    /* (non-Javadoc)
     * Method declared on CellEditor.
     */
    protected Control createControl(Composite parent) {

        this.comboBox = new CustomCCombo(parent, getStyle());
        this.comboBox.setFont(parent.getFont());
        
        populateComboBoxItems();

        this.comboBox.addKeyListener(new KeyAdapter() {
            // hook key pressed - see PR 14201  
            public void keyPressed(KeyEvent e) {
                keyReleaseOccured(e);
            }
        });

        this.comboBox.addSelectionListener(new SelectionAdapter() {
            public void widgetDefaultSelected(SelectionEvent event) {
                applyEditorValueAndDeactivate();
            }

            public void widgetSelected(SelectionEvent event) {
                ReadOnlyComboBoxCellEditor.this.selection = ReadOnlyComboBoxCellEditor.this.comboBox.getSelectionIndex();
            }
        });

        this.comboBox.addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent e) {
                if (e.detail == SWT.TRAVERSE_ESCAPE
                        || e.detail == SWT.TRAVERSE_RETURN) {
                    e.doit = false;
                }
            }
        });

        this.comboBox.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                ReadOnlyComboBoxCellEditor.this.focusLost();
            }
        });
        
        this.comboBox.addSelectionFinalizedListener(new CustomCCombo.SelectionFinalizedListener() { // kosta
            public void handleEvent() {
                handleSelectionFinalized();
            }
        });
        
        return this.comboBox;
    }

    /**
     * The <code>ComboBoxCellEditor</code> implementation of
     * this <code>CellEditor</code> framework method returns
     * the zero-based index of the current selection.
     *
     * @return the zero-based index of the current selection wrapped
     *  as an <code>Integer</code>
     */
    protected Object doGetValue() {
        return new Integer(this.selection);
    }

    /* (non-Javadoc)
     * Method declared on CellEditor.
     */
    protected void doSetFocus() {
        this.comboBox.setFocus();
        this.comboBox.dropDown( true );  // kosta
    }

    /**
     * The <code>ComboBoxCellEditor</code> implementation of
     * this <code>CellEditor</code> framework method sets the 
     * minimum width of the cell.  The minimum width is 10 characters
     * if <code>comboBox</code> is not <code>null</code> or <code>disposed</code>
     * else it is 60 pixels to make sure the arrow button and some text is visible.
     * The list of CCombo will be wide enough to show its longest item.
     */
    public LayoutData getLayoutData() {
        LayoutData layoutData = super.getLayoutData();
        if ((this.comboBox == null) || this.comboBox.isDisposed()) {
            layoutData.minimumWidth = 60;
        } else {
            // make the comboBox 10 characters wide
            GC gc = new GC(this.comboBox);
            layoutData.minimumWidth = (gc.getFontMetrics()
                    .getAverageCharWidth() * 10) + 10;
            gc.dispose();
        }
        return layoutData;
    }

    /**
     * The <code>ComboBoxCellEditor</code> implementation of
     * this <code>CellEditor</code> framework method
     * accepts a zero-based index of a selection.
     *
     * @param value the zero-based index of the selection wrapped
     *   as an <code>Integer</code>
     */
    protected void doSetValue(Object value) {
        Assert.isTrue(this.comboBox != null && (value instanceof Integer));
        this.selection = ((Integer) value).intValue();
        this.comboBox.select(this.selection);
    }

    /**
     * Updates the list of choices for the combo box for the current control.
     */
    private void populateComboBoxItems() {
        if (this.comboBox != null && this.items != null) {
            this.comboBox.removeAll();
            for (int i = 0; i < this.items.length; i++) {
                this.comboBox.add(this.items[i], i);
            }

            setValueValid(true);
            this.selection = 0;
        }
    }

    /**
     * Applies the currently selected value and deactivates the cell editor
     */
    void applyEditorValueAndDeactivate() {
        //  must set the selection before getting value
        this.selection = this.comboBox.getSelectionIndex();
        Object newValue = doGetValue();
        markDirty();
        boolean isValid = isCorrect(newValue);
        setValueValid(isValid);
        
        if (!isValid) {
            // Only format if the 'index' is valid
            if (this.items.length > 0 && this.selection >= 0 && this.selection < this.items.length) {
                // try to insert the current value into the error message.
                setErrorMessage(MessageFormat.format(getErrorMessage(),
                        new Object[] { this.items[this.selection] }));
            }
            else {
                // Since we don't have a valid index, assume we're using an 'edit'
                // combo so format using its text value
                setErrorMessage(MessageFormat.format(getErrorMessage(),
                        new Object[] { this.comboBox.getText() }));
            }
        }

        fireApplyEditorValue();
        deactivate();
    }

    /*
     *  (non-Javadoc)
     * @see org.eclipse.jface.viewers.CellEditor#focusLost()
     */
    protected void focusLost() {
        if (isActivated()) {
            applyEditorValueAndDeactivate();
        }
    }

    /*
     *  (non-Javadoc)
     * @see org.eclipse.jface.viewers.CellEditor#keyReleaseOccured(org.eclipse.swt.events.KeyEvent)
     */
    protected void keyReleaseOccured(KeyEvent keyEvent) {
        if (keyEvent.character == '\u001b') { // Escape character
            fireCancelEditor();
        } else if (keyEvent.character == '\t') { // tab key
            applyEditorValueAndDeactivate();
        }
    }
    
    protected void handleSelectionFinalized() // kosta
    {
        applyEditorValueAndDeactivate();
    }
    
    /**
     * The CCombo class represents a selectable user interface object
     * that combines a text field and a list and issues notification
     * when an item is selected from the list.
     * <p>
     * CCombo was written to work around certain limitations in the native
     * combo box. Specifically, on win32, the height of a CCombo can be set;
     * attempts to set the height of a Combo are ignored. CCombo can be used
     * anywhere that having the increased flexibility is more important than
     * getting native L&F, but the decision should not be taken lightly. 
     * There is no is no strict requirement that CCombo look or behave
     * the same as the native combo box.
     * </p>
     * <p>
     * Note that although this class is a subclass of <code>Composite</code>,
     * it does not make sense to add children to it, or set a layout on it.
     * </p>
     * <dl>
     * <dt><b>Styles:</b>
     * <dd>BORDER, READ_ONLY, FLAT</dd>
     * <dt><b>Events:</b>
     * <dd>DefaultSelection, Modify, Selection, Verify</dd>
     * </dl>
     */
    private static final class CustomCCombo extends Composite {
        
        public static class SelectionFinalizedListener implements EventListener // kosta
        {
            public void handleEvent()
            {
            }
        }
    
        Text text;
        List list;
        int visibleItemCount = 5;
        Shell popup;
        Button arrow;
        boolean hasFocus;
        Listener listener, filter;
        Color foreground, background;
        Font font;
        java.util.List<SelectionFinalizedListener> selectionFinalizedListeners; // kosta
        
        
    /**
     * Constructs a new instance of this class given its parent
     * and a style value describing its behavior and appearance.
     * <p>
     * The style value is either one of the style constants defined in
     * class <code>SWT</code> which is applicable to instances of this
     * class, or must be built by <em>bitwise OR</em>'ing together 
     * (that is, using the <code>int</code> "|" operator) two or more
     * of those <code>SWT</code> style constants. The class description
     * lists the style constants that are applicable to the class.
     * Style bits are also inherited from superclasses.
     * </p>
     *
     * @param parent a widget which will be the parent of the new instance (cannot be null)
     * @param style the style of widget to construct
     *
     * @exception IllegalArgumentException <ul>
     *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
     * </ul>
     * @exception SWTException <ul>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
     * </ul>
     *
     * @see SWT#BORDER
     * @see SWT#READ_ONLY
     * @see SWT#FLAT
     * @see Widget#getStyle()
     */
    public CustomCCombo (Composite parent, int style) {
        super (parent, style = checkStyle (style));
        
        this.selectionFinalizedListeners = new ArrayList<SelectionFinalizedListener>(); // kosta
        
        int textStyle = SWT.SINGLE;
        if ((style & SWT.READ_ONLY) != 0) textStyle |= SWT.READ_ONLY;
        if ((style & SWT.FLAT) != 0) textStyle |= SWT.FLAT;
        this.text = new Text (this, textStyle);
        int arrowStyle = SWT.ARROW | SWT.DOWN;
        if ((style & SWT.FLAT) != 0) arrowStyle |= SWT.FLAT;
        this.arrow = new Button (this, arrowStyle);
    
        this.listener = new Listener () {
            public void handleEvent (Event event) {
                if (CustomCCombo.this.popup == event.widget) {
                    popupEvent (event);
                    return;
                }
                if (CustomCCombo.this.text == event.widget) {
                    textEvent (event);
                    return;
                }
                if (CustomCCombo.this.list == event.widget) {
                    listEvent (event);
                    return;
                }
                if (CustomCCombo.this.arrow == event.widget) {
                    arrowEvent (event);
                    return;
                }
                if (CustomCCombo.this == event.widget) {
                    comboEvent (event);
                    return;
                }
                if (getShell () == event.widget) {
                    handleFocus (SWT.FocusOut);
                }
            }
        };
        this.filter = new Listener() {
            public void handleEvent(Event event) {
                Shell shell = ((Control)event.widget).getShell ();
                if (shell == CustomCCombo.this.getShell ()) {
                    handleFocus (SWT.FocusOut);
                }
            }
        };
        
        int [] comboEvents = {SWT.Dispose, SWT.Move, SWT.Resize};
        for (int i=0; i<comboEvents.length; i++) this.addListener (comboEvents [i], this.listener);
        
        int [] textEvents = {SWT.DefaultSelection, SWT.KeyDown, SWT.KeyUp, SWT.MenuDetect, SWT.Modify, SWT.MouseDown, SWT.MouseUp, SWT.Traverse, SWT.FocusIn, SWT.Verify};
        for (int i=0; i<textEvents.length; i++) this.text.addListener (textEvents [i], this.listener);
        
        int [] arrowEvents = {SWT.Selection, SWT.FocusIn};
        for (int i=0; i<arrowEvents.length; i++) this.arrow.addListener (arrowEvents [i], this.listener);
        
        createPopup(null, -1);
        initAccessible();
    }
    static int checkStyle (int style) {
        int mask = SWT.BORDER | SWT.READ_ONLY | SWT.FLAT | SWT.LEFT_TO_RIGHT | SWT.RIGHT_TO_LEFT;
        return style & mask;
    }
    /**
     * Adds the argument to the end of the receiver's list.
     *
     * @param string the new item
     *
     * @exception IllegalArgumentException <ul>
     *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
     * </ul>
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     *
     * @see #add(String,int)
     */
    public void add (String string) {
        checkWidget();
        if (string == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
        this.list.add (string);
    }
    /**
     * Adds the argument to the receiver's list at the given
     * zero-relative index.
     * <p>
     * Note: To add an item at the end of the list, use the
     * result of calling <code>getItemCount()</code> as the
     * index or use <code>add(String)</code>.
     * </p>
     *
     * @param string the new item
     * @param index the index for the item
     *
     * @exception IllegalArgumentException <ul>
     *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
     *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list (inclusive)</li>
     * </ul>
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     *
     * @see #add(String)
     */
    public void add (String string, int index) {
        checkWidget();
        if (string == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
        this.list.add (string, index);
    }
    /**
     * Adds the listener to the collection of listeners who will
     * be notified when the receiver's text is modified, by sending
     * it one of the messages defined in the <code>ModifyListener</code>
     * interface.
     *
     * @param listener the listener which should be notified
     *
     * @exception IllegalArgumentException <ul>
     *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
     * </ul>
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     *
     * @see ModifyListener
     * @see #removeModifyListener
     */
    public void addModifyListener (ModifyListener listener) {
        checkWidget();
        if (listener == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
        TypedListener typedListener = new TypedListener (listener);
        addListener (SWT.Modify, typedListener);
    }
    /**
     * Adds the listener to the collection of listeners who will
     * be notified when the user changes the receiver's selection, by sending
     * it one of the messages defined in the <code>SelectionListener</code>
     * interface.
     * <p>
     * <code>widgetSelected</code> is called when the combo's list selection changes.
     * <code>widgetDefaultSelected</code> is typically called when ENTER is pressed the combo's text area.
     * </p>
     *
     * @param listener the listener which should be notified when the user changes the receiver's selection
     *
     * @exception IllegalArgumentException <ul>
     *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
     * </ul>
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     *
     * @see SelectionListener
     * @see #removeSelectionListener
     * @see SelectionEvent
     */
    public void addSelectionListener(SelectionListener listener) {
        checkWidget();
        if (listener == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
        TypedListener typedListener = new TypedListener (listener);
        addListener (SWT.Selection,typedListener);
        addListener (SWT.DefaultSelection,typedListener);
    }
    /**
     * Adds the listener to the collection of listeners who will
     * be notified when the receiver's text is verified, by sending
     * it one of the messages defined in the <code>VerifyListener</code>
     * interface.
     *
     * @param listener the listener which should be notified
     *
     * @exception IllegalArgumentException <ul>
     *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
     * </ul>
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     *
     * @see VerifyListener
     * @see #removeVerifyListener
     * 
     * @since 3.3
     */
    public void addVerifyListener (VerifyListener listener) {
        checkWidget();
        if (listener == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
        TypedListener typedListener = new TypedListener (listener);
        addListener (SWT.Verify,typedListener);
    }
    
    public void addSelectionFinalizedListener( SelectionFinalizedListener listener ) // kosta
    {
        checkWidget();
        if (listener == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
        this.selectionFinalizedListeners.add( listener );
    }
    
    public void removeDropDownListener( SelectionFinalizedListener listener ) // kosta
    {
        checkWidget();
        if (listener == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
        this.selectionFinalizedListeners.remove( listener );
    }
    
    private void notifySelectionFinalizedListeners() // kosta
    {
        checkWidget();
        
        for( SelectionFinalizedListener listener : this.selectionFinalizedListeners )
        {
            listener.handleEvent();
        }
    }
    
    void arrowEvent (Event event) {
        switch (event.type) {
            case SWT.FocusIn: {
                handleFocus (SWT.FocusIn);
                break;
            }
            case SWT.Selection: {
                dropDown (!isDropped ());
                break;
            }
        }
    }
    /**
     * Sets the selection in the receiver's text field to an empty
     * selection starting just before the first character. If the
     * text field is editable, this has the effect of placing the
     * i-beam at the start of the text.
     * <p>
     * Note: To clear the selected items in the receiver's list, 
     * use <code>deselectAll()</code>.
     * </p>
     *
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     *
     * @see #deselectAll
     */
    public void clearSelection () {
        checkWidget ();
        this.text.clearSelection ();
        this.list.deselectAll ();
    }
    void comboEvent (Event event) {
        switch (event.type) {
            case SWT.Dispose:
                if (this.popup != null && !this.popup.isDisposed ()) {
                    this.list.removeListener (SWT.Dispose, this.listener);
                    this.popup.dispose ();
                }
                Shell shell = getShell ();
                shell.removeListener (SWT.Deactivate, this.listener);
                Display display = getDisplay ();
                display.removeFilter (SWT.FocusIn, this.filter);
                this.popup = null;  
                this.text = null;  
                this.list = null;  
                this.arrow = null;
                break;
            case SWT.Move:
                dropDown (false);
                break;
            case SWT.Resize:
                internalLayout (false);
                break;
        }
    }
    
    public Point computeSize (int wHint, int hHint, boolean changed) {
        checkWidget ();
        int width = 0, height = 0;
        String[] items = this.list.getItems ();
        GC gc = new GC (this.text);
        int spacer = gc.stringExtent (" ").x; //$NON-NLS-1$
        int textWidth = gc.stringExtent (this.text.getText ()).x;
        for (int i = 0; i < items.length; i++) {
            textWidth = Math.max (gc.stringExtent (items[i]).x, textWidth);
        }
        gc.dispose ();
        Point textSize = this.text.computeSize (SWT.DEFAULT, SWT.DEFAULT, changed);
        Point arrowSize = this.arrow.computeSize (SWT.DEFAULT, SWT.DEFAULT, changed);
        Point listSize = this.list.computeSize (SWT.DEFAULT, SWT.DEFAULT, changed);
        int borderWidth = getBorderWidth ();
        
        height = Math.max (textSize.y, arrowSize.y);
        width = Math.max (textWidth + 2*spacer + arrowSize.x + 2*borderWidth, listSize.x);
        if (wHint != SWT.DEFAULT) width = wHint;
        if (hHint != SWT.DEFAULT) height = hHint;
        return new Point (width + 2*borderWidth, height + 2*borderWidth);
    }
    /**
     * Copies the selected text.
     * <p>
     * The current selection is copied to the clipboard.
     * </p>
     *
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     * 
     * @since 3.3
     */
    public void copy () {
        checkWidget ();
        this.text.copy ();
    }
    void createPopup(String[] items, int selectionIndex) {      
        // create shell and list
        this.popup = new Shell (getShell (), SWT.NO_TRIM | SWT.ON_TOP);
        int style = getStyle ();
        int listStyle = SWT.SINGLE | SWT.V_SCROLL;
        if ((style & SWT.FLAT) != 0) listStyle |= SWT.FLAT;
        if ((style & SWT.RIGHT_TO_LEFT) != 0) listStyle |= SWT.RIGHT_TO_LEFT;
        if ((style & SWT.LEFT_TO_RIGHT) != 0) listStyle |= SWT.LEFT_TO_RIGHT;
        this.list = new List (this.popup, listStyle);
        if (this.font != null) this.list.setFont (this.font);
        if (this.foreground != null) this.list.setForeground (this.foreground);
        if (this.background != null) this.list.setBackground (this.background);
    
        int [] popupEvents = {SWT.Close, SWT.Paint, SWT.Deactivate};
        for (int i=0; i<popupEvents.length; i++) this.popup.addListener (popupEvents [i], this.listener);
        int [] listEvents = {SWT.MouseUp, SWT.Selection, SWT.Traverse, SWT.KeyDown, SWT.KeyUp, SWT.FocusIn, SWT.Dispose};
        for (int i=0; i<listEvents.length; i++) this.list.addListener (listEvents [i], this.listener);
    
        if (items != null) this.list.setItems (items);
        if (selectionIndex != -1) this.list.setSelection (selectionIndex);
    }
    /**
     * Cuts the selected text.
     * <p>
     * The current selection is first copied to the
     * clipboard and then deleted from the widget.
     * </p>
     *
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     * 
     * @since 3.3
     */
    public void cut () {
        checkWidget ();
        this.text.cut ();
    }
    /**
     * Deselects the item at the given zero-relative index in the receiver's 
     * list.  If the item at the index was already deselected, it remains
     * deselected. Indices that are out of range are ignored.
     *
     * @param index the index of the item to deselect
     *
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     */
    public void deselect (int index) {
        checkWidget ();
        this.list.deselect (index);
    }
    /**
     * Deselects all selected items in the receiver's list.
     * <p>
     * Note: To clear the selection in the receiver's text field,
     * use <code>clearSelection()</code>.
     * </p>
     *
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     *
     * @see #clearSelection
     */
    public void deselectAll () {
        checkWidget ();
        this.list.deselectAll ();
    }
    void dropDown (boolean drop) {
        dropDown(drop, true);
    }
    void dropDown (boolean drop, boolean focus) {
        /*try
        {
            throw new RuntimeException();
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }*/
        if (drop == isDropped ()) return;
        if (!drop) {
            this.popup.setVisible (false);
            if (!isDisposed ()&& this.arrow.isFocusControl() && focus) {
                this.text.setFocus();
            }
            return;
        }
    
        if (getShell() != this.popup.getParent ()) {
            String[] items = this.list.getItems ();
            int selectionIndex = this.list.getSelectionIndex ();
            this.list.removeListener (SWT.Dispose, this.listener);
            this.popup.dispose();
            this.popup = null;
            this.list = null;
            createPopup (items, selectionIndex);
        }
        
        Point size = getSize ();
        int itemCount = this.list.getItemCount ();
        itemCount = (itemCount == 0) ? this.visibleItemCount : Math.min(this.visibleItemCount, itemCount);
        int itemHeight = this.list.getItemHeight () * itemCount;
        Point listSize = this.list.computeSize (SWT.DEFAULT, itemHeight, false);
        this.list.setBounds (1, 1, Math.max (size.x - 2, listSize.x), listSize.y);
        
        int index = this.list.getSelectionIndex ();
        if (index != -1) this.list.setTopIndex (index);
        Display display = getDisplay ();
        Rectangle listRect = this.list.getBounds ();
        Rectangle parentRect = display.map (getParent (), null, getBounds ());
        Point comboSize = getSize ();
        Rectangle displayRect = getMonitor ().getClientArea ();
        int width = Math.max (comboSize.x, listRect.width + 2);
        int height = listRect.height + 2;
        int x = parentRect.x;
        int y = parentRect.y + comboSize.y;
        if (y + height > displayRect.y + displayRect.height) y = parentRect.y - height;
        if (x + width > displayRect.x + displayRect.width) x = displayRect.x + displayRect.width - listRect.width;
        this.popup.setBounds (x, y, width, height);
        this.popup.setVisible (true);
        if (focus) this.list.setFocus ();
    }
    /*
     * Return the lowercase of the first non-'&' character following
     * an '&' character in the given string. If there are no '&'
     * characters in the given string, return '\0'.
     */
    char _findMnemonic (String string) {
        if (string == null) return '\0';
        int index = 0;
        int length = string.length ();
        do {
            while (index < length && string.charAt (index) != '&') index++;
            if (++index >= length) return '\0';
            if (string.charAt (index) != '&') return Character.toLowerCase (string.charAt (index));
            index++;
        } while (index < length);
        return '\0';
    }
    /* 
     * Return the Label immediately preceding the receiver in the z-order, 
     * or null if none. 
     */
    Label getAssociatedLabel () {
        Control[] siblings = getParent ().getChildren ();
        for (int i = 0; i < siblings.length; i++) {
            if (siblings [i] == this) {
                if (i > 0 && siblings [i-1] instanceof Label) {
                    return (Label) siblings [i-1];
                }
            }
        }
        return null;
    }
    public Control [] getChildren () {
        checkWidget();
        return new Control [0];
    }
    /**
     * Gets the editable state.
     *
     * @return whether or not the receiver is editable
     * 
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     * 
     * @since 3.0
     */
    public boolean getEditable () {
        checkWidget ();
        return this.text.getEditable();
    }
    /**
     * Returns the item at the given, zero-relative index in the
     * receiver's list. Throws an exception if the index is out
     * of range.
     *
     * @param index the index of the item to return
     * @return the item at the given index
     *
     * @exception IllegalArgumentException <ul>
     *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
     * </ul>
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     */
    public String getItem (int index) {
        checkWidget();
        return this.list.getItem (index);
    }
    /**
     * Returns the number of items contained in the receiver's list.
     *
     * @return the number of items
     *
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     */
    public int getItemCount () {
        checkWidget ();
        return this.list.getItemCount ();
    }
    /**
     * Returns the height of the area which would be used to
     * display <em>one</em> of the items in the receiver's list.
     *
     * @return the height of one item
     *
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     */
    public int getItemHeight () {
        checkWidget ();
        return this.list.getItemHeight ();
    }
    /**
     * Returns an array of <code>String</code>s which are the items
     * in the receiver's list. 
     * <p>
     * Note: This is not the actual structure used by the receiver
     * to maintain its list of items, so modifying the array will
     * not affect the receiver. 
     * </p>
     *
     * @return the items in the receiver's list
     *
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     */
    public String [] getItems () {
        checkWidget ();
        return this.list.getItems ();
    }
    /**
     * Returns <code>true</code> if the receiver's list is visible,
     * and <code>false</code> otherwise.
     * <p>
     * If one of the receiver's ancestors is not visible or some
     * other condition makes the receiver not visible, this method
     * may still indicate that it is considered visible even though
     * it may not actually be showing.
     * </p>
     *
     * @return the receiver's list's visibility state
     *
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     * 
     * @since 3.4
     */
    public boolean getListVisible () {
        checkWidget ();
        return isDropped();
    }
    public Menu getMenu() {
        return this.text.getMenu();
    }
    /**
     * Returns a <code>Point</code> whose x coordinate is the start
     * of the selection in the receiver's text field, and whose y
     * coordinate is the end of the selection. The returned values
     * are zero-relative. An "empty" selection as indicated by
     * the the x and y coordinates having the same value.
     *
     * @return a point representing the selection start and end
     *
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     */
    public Point getSelection () {
        checkWidget ();
        return this.text.getSelection ();
    }
    /**
     * Returns the zero-relative index of the item which is currently
     * selected in the receiver's list, or -1 if no item is selected.
     *
     * @return the index of the selected item
     *
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     */
    public int getSelectionIndex () {
        checkWidget ();
        return this.list.getSelectionIndex ();
    }
    public int getStyle () {
        int style = super.getStyle ();
        style &= ~SWT.READ_ONLY;
        if (!this.text.getEditable()) style |= SWT.READ_ONLY; 
        return style;
    }
    /**
     * Returns a string containing a copy of the contents of the
     * receiver's text field.
     *
     * @return the receiver's text
     *
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     */
    public String getText () {
        checkWidget ();
        return this.text.getText ();
    }
    /**
     * Returns the height of the receivers's text field.
     *
     * @return the text height
     *
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     */
    public int getTextHeight () {
        checkWidget ();
        return this.text.getLineHeight ();
    }
    /**
     * Returns the maximum number of characters that the receiver's
     * text field is capable of holding. If this has not been changed
     * by <code>setTextLimit()</code>, it will be the constant
     * <code>Combo.LIMIT</code>.
     * 
     * @return the text limit
     * 
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     */
    public int getTextLimit () {
        checkWidget ();
        return this.text.getTextLimit ();
    }
    /**
     * Gets the number of items that are visible in the drop
     * down portion of the receiver's list.
     *
     * @return the number of items that are visible
     *
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     * 
     * @since 3.0
     */
    public int getVisibleItemCount () {
        checkWidget ();
        return this.visibleItemCount;
    }
    void handleFocus (int type) {
        if (isDisposed ()) return;
        switch (type) {
            case SWT.FocusIn: {
                if (this.hasFocus) return;
                if (getEditable ()) this.text.selectAll ();
                this.hasFocus = true;
                Shell shell = getShell ();
                shell.removeListener (SWT.Deactivate, this.listener);
                shell.addListener (SWT.Deactivate, this.listener);
                Display display = getDisplay ();
                display.removeFilter (SWT.FocusIn, this.filter);
                display.addFilter (SWT.FocusIn, this.filter);
                Event e = new Event ();
                notifyListeners (SWT.FocusIn, e);
                break;
            }
            case SWT.FocusOut: {
                if (!this.hasFocus) return;
                Control focusControl = getDisplay ().getFocusControl ();
                if (focusControl == this.arrow || focusControl == this.list || focusControl == this.text) return;
                this.hasFocus = false;
                Shell shell = getShell ();
                shell.removeListener(SWT.Deactivate, this.listener);
                Display display = getDisplay ();
                display.removeFilter (SWT.FocusIn, this.filter);
                Event e = new Event ();
                notifyListeners (SWT.FocusOut, e);
                break;
            }
        }
    }
    /**
     * Searches the receiver's list starting at the first item
     * (index 0) until an item is found that is equal to the 
     * argument, and returns the index of that item. If no item
     * is found, returns -1.
     *
     * @param string the search item
     * @return the index of the item
     *
     * @exception IllegalArgumentException <ul>
     *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
     * </ul>
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     */
    public int indexOf (String string) {
        checkWidget ();
        if (string == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
        return this.list.indexOf (string);
    }
    /**
     * Searches the receiver's list starting at the given, 
     * zero-relative index until an item is found that is equal
     * to the argument, and returns the index of that item. If
     * no item is found or the starting index is out of range,
     * returns -1.
     *
     * @param string the search item
     * @param start the zero-relative index at which to begin the search
     * @return the index of the item
     *
     * @exception IllegalArgumentException <ul>
     *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
     * </ul>
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     */
    public int indexOf (String string, int start) {
        checkWidget ();
        if (string == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
        return this.list.indexOf (string, start);
    }
    
    void initAccessible() {
        AccessibleAdapter accessibleAdapter = new AccessibleAdapter () {
            public void getName (AccessibleEvent e) {
                String name = null;
                Label label = getAssociatedLabel ();
                if (label != null) {
                    name = stripMnemonic (label.getText());
                }
                e.result = name;
            }
            public void getKeyboardShortcut(AccessibleEvent e) {
                String shortcut = null;
                Label label = getAssociatedLabel ();
                if (label != null) {
                    String text = label.getText ();
                    if (text != null) {
                        char mnemonic = _findMnemonic (text);
                        if (mnemonic != '\0') {
                            shortcut = "Alt+"+mnemonic; //$NON-NLS-1$
                        }
                    }
                }
                e.result = shortcut;
            }
            public void getHelp (AccessibleEvent e) {
                e.result = getToolTipText ();
            }
        };
        getAccessible ().addAccessibleListener (accessibleAdapter);
        this.text.getAccessible ().addAccessibleListener (accessibleAdapter);
        this.list.getAccessible ().addAccessibleListener (accessibleAdapter);
        
        this.arrow.getAccessible ().addAccessibleListener (new AccessibleAdapter() {
            public void getName (AccessibleEvent e) {
                e.result = isDropped () ? SWT.getMessage ("SWT_Close") : SWT.getMessage ("SWT_Open"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            public void getKeyboardShortcut (AccessibleEvent e) {
                e.result = "Alt+Down Arrow"; //$NON-NLS-1$
            }
            public void getHelp (AccessibleEvent e) {
                e.result = getToolTipText ();
            }
        });
    
        getAccessible().addAccessibleTextListener (new AccessibleTextAdapter() {
            public void getCaretOffset (AccessibleTextEvent e) {
                e.offset = CustomCCombo.this.text.getCaretPosition ();
            }
            public void getSelectionRange(AccessibleTextEvent e) {
                Point sel = CustomCCombo.this.text.getSelection();
                e.offset = sel.x;
                e.length = sel.y - sel.x;
            }
        });
        
        getAccessible().addAccessibleControlListener (new AccessibleControlAdapter() {
            public void getChildAtPoint (AccessibleControlEvent e) {
                Point testPoint = toControl (e.x, e.y);
                if (getBounds ().contains (testPoint)) {
                    e.childID = ACC.CHILDID_SELF;
                }
            }
            
            public void getLocation (AccessibleControlEvent e) {
                Rectangle location = getBounds ();
                Point pt = getParent().toDisplay (location.x, location.y);
                e.x = pt.x;
                e.y = pt.y;
                e.width = location.width;
                e.height = location.height;
            }
            
            public void getChildCount (AccessibleControlEvent e) {
                e.detail = 0;
            }
            
            public void getRole (AccessibleControlEvent e) {
                e.detail = ACC.ROLE_COMBOBOX;
            }
            
            public void getState (AccessibleControlEvent e) {
                e.detail = ACC.STATE_NORMAL;
            }
    
            public void getValue (AccessibleControlEvent e) {
                e.result = getText ();
            }
        });
    
        this.text.getAccessible ().addAccessibleControlListener (new AccessibleControlAdapter () {
            public void getRole (AccessibleControlEvent e) {
                e.detail = CustomCCombo.this.text.getEditable () ? ACC.ROLE_TEXT : ACC.ROLE_LABEL;
            }
        });
    
        this.arrow.getAccessible ().addAccessibleControlListener (new AccessibleControlAdapter() {
            public void getDefaultAction (AccessibleControlEvent e) {
                e.result = isDropped () ? SWT.getMessage ("SWT_Close") : SWT.getMessage ("SWT_Open"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        });
    }
    boolean isDropped () {
        return this.popup.getVisible ();
    }
    public boolean isFocusControl () {
        checkWidget();
        if (this.text.isFocusControl () || this.arrow.isFocusControl () || this.list.isFocusControl () || this.popup.isFocusControl ()) {
            return true;
        } 
        return super.isFocusControl ();
    }
    void internalLayout (boolean changed) {
        if (isDropped ()) dropDown (false);
        Rectangle rect = getClientArea ();
        int width = rect.width;
        int height = rect.height;
        Point arrowSize = this.arrow.computeSize (SWT.DEFAULT, height, changed);
        this.text.setBounds (0, 0, width - arrowSize.x, height);
        this.arrow.setBounds (width - arrowSize.x, 0, arrowSize.x, arrowSize.y);
    }
    void listEvent (Event event) {
        switch (event.type) {
            case SWT.Dispose:
                if (getShell () != this.popup.getParent ()) {
                    String[] items = this.list.getItems ();
                    int selectionIndex = this.list.getSelectionIndex ();
                    this.popup = null;
                    this.list = null;
                    createPopup (items, selectionIndex);
                }
                break;
            case SWT.FocusIn: {
                handleFocus (SWT.FocusIn);
                break;
            }
            case SWT.MouseUp: {
                if (event.button != 1) return;
                dropDown (false);
                notifySelectionFinalizedListeners(); // kosta
                break;
            }
            case SWT.Selection: {
                int index = this.list.getSelectionIndex ();
                if (index == -1) return;
                this.text.setText (this.list.getItem (index));
                this.text.selectAll ();
                this.list.setSelection (index);
                Event e = new Event ();
                e.time = event.time;
                e.stateMask = event.stateMask;
                e.doit = event.doit;
                notifyListeners (SWT.Selection, e);
                event.doit = e.doit;
                break;
            }
            case SWT.Traverse: {
                switch (event.detail) {
                    case SWT.TRAVERSE_RETURN:
                    case SWT.TRAVERSE_ESCAPE:
                    case SWT.TRAVERSE_ARROW_PREVIOUS:
                    case SWT.TRAVERSE_ARROW_NEXT:
                        event.doit = false;
                        break;
                }
                Event e = new Event ();
                e.time = event.time;
                e.detail = event.detail;
                e.doit = event.doit;
                e.character = event.character;
                e.keyCode = event.keyCode;
                notifyListeners (SWT.Traverse, e);
                event.doit = e.doit;
                event.detail = e.detail;
                break;
            }
            case SWT.KeyUp: {       
                Event e = new Event ();
                e.time = event.time;
                e.character = event.character;
                e.keyCode = event.keyCode;
                e.stateMask = event.stateMask;
                notifyListeners (SWT.KeyUp, e);
                break;
            }
            case SWT.KeyDown: {
                if (event.character == SWT.ESC) { 
                    // Escape key cancels popup list
                    dropDown (false);
                }
                if ((event.stateMask & SWT.ALT) != 0 && (event.keyCode == SWT.ARROW_UP || event.keyCode == SWT.ARROW_DOWN)) {
                    dropDown (false);
                }
                if (event.character == SWT.CR) {
                    // Enter causes default selection
                    dropDown (false);
                    Event e = new Event ();
                    e.time = event.time;
                    e.stateMask = event.stateMask;
                    notifyListeners (SWT.DefaultSelection, e);
                    notifySelectionFinalizedListeners(); // kosta
                }
                // At this point the widget may have been disposed.
                // If so, do not continue.
                if (isDisposed ()) break;
                Event e = new Event();
                e.time = event.time;
                e.character = event.character;
                e.keyCode = event.keyCode;
                e.stateMask = event.stateMask;
                notifyListeners(SWT.KeyDown, e);
                break;
                
            }
        }
    }
    /**
     * Pastes text from clipboard.
     * <p>
     * The selected text is deleted from the widget
     * and new text inserted from the clipboard.
     * </p>
     *
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     * 
     * @since 3.3
     */
    public void paste () {
        checkWidget ();
        this.text.paste ();
    }
    void popupEvent(Event event) {
        switch (event.type) {
            case SWT.Paint:
                // draw black rectangle around list
                Rectangle listRect = this.list.getBounds();
                Color black = getDisplay().getSystemColor(SWT.COLOR_BLACK);
                event.gc.setForeground(black);
                event.gc.drawRectangle(0, 0, listRect.width + 1, listRect.height + 1);
                break;
            case SWT.Close:
                event.doit = false;
                dropDown (false);
                break;
            case SWT.Deactivate:
                /*
                 * Bug in GTK. When the arrow button is pressed the popup control receives a
                 * deactivate event and then the arrow button receives a selection event. If 
                 * we hide the popup in the deactivate event, the selection event will show 
                 * it again. To prevent the popup from showing again, we will let the selection 
                 * event of the arrow button hide the popup.
                 * In Windows, hiding the popup during the deactivate causes the deactivate 
                 * to be called twice and the selection event to be disappear.
                 */
                if (!"carbon".equals(SWT.getPlatform())) { //$NON-NLS-1$
                    Point point = this.arrow.toControl(getDisplay().getCursorLocation());
                    Point size = this.arrow.getSize();
                    Rectangle rect = new Rectangle(0, 0, size.x, size.y);
                    if (!rect.contains(point)) 
                    {
                        dropDown (false);
                        
                        final Thread thread = new Thread()  //kosta
                        {
                            public void run()
                            {
                                final Runnable runnable = new Runnable()
                                {
                                    public void run()
                                    {
                                        notifySelectionFinalizedListeners();
                                    }
                                };
                                
                                getDisplay().syncExec( runnable );
                            }
                        };
                        
                        thread.start();  //kosta
                    }
                } else {
                    dropDown(false);
                }
                break;
        }
    }
    public void redraw () {
        super.redraw();
        this.text.redraw();
        this.arrow.redraw();
        if (this.popup.isVisible()) this.list.redraw();
    }
    public void redraw (int x, int y, int width, int height, boolean all) {
        super.redraw(x, y, width, height, true);
    }
    
    /**
     * Removes the item from the receiver's list at the given
     * zero-relative index.
     *
     * @param index the index for the item
     *
     * @exception IllegalArgumentException <ul>
     *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
     * </ul>
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     */
    public void remove (int index) {
        checkWidget();
        this.list.remove (index);
    }
    /**
     * Removes the items from the receiver's list which are
     * between the given zero-relative start and end 
     * indices (inclusive).
     *
     * @param start the start of the range
     * @param end the end of the range
     *
     * @exception IllegalArgumentException <ul>
     *    <li>ERROR_INVALID_RANGE - if either the start or end are not between 0 and the number of elements in the list minus 1 (inclusive)</li>
     * </ul>
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     */
    public void remove (int start, int end) {
        checkWidget();
        this.list.remove (start, end);
    }
    /**
     * Searches the receiver's list starting at the first item
     * until an item is found that is equal to the argument, 
     * and removes that item from the list.
     *
     * @param string the item to remove
     *
     * @exception IllegalArgumentException <ul>
     *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
     *    <li>ERROR_INVALID_ARGUMENT - if the string is not found in the list</li>
     * </ul>
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     */
    public void remove (String string) {
        checkWidget();
        if (string == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
        this.list.remove (string);
    }
    /**
     * Removes all of the items from the receiver's list and clear the
     * contents of receiver's text field.
     * <p>
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     */
    public void removeAll () {
        checkWidget();
        this.text.setText (""); //$NON-NLS-1$
        this.list.removeAll ();
    }
    /**
     * Removes the listener from the collection of listeners who will
     * be notified when the receiver's text is modified.
     *
     * @param listener the listener which should no longer be notified
     *
     * @exception IllegalArgumentException <ul>
     *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
     * </ul>
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     *
     * @see ModifyListener
     * @see #addModifyListener
     */
    public void removeModifyListener (ModifyListener listener) {
        checkWidget();
        if (listener == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
        removeListener(SWT.Modify, listener);   
    }
    /**
     * Removes the listener from the collection of listeners who will
     * be notified when the user changes the receiver's selection.
     *
     * @param listener the listener which should no longer be notified
     *
     * @exception IllegalArgumentException <ul>
     *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
     * </ul>
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     *
     * @see SelectionListener
     * @see #addSelectionListener
     */
    public void removeSelectionListener (SelectionListener listener) {
        checkWidget();
        if (listener == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
        removeListener(SWT.Selection, listener);
        removeListener(SWT.DefaultSelection,listener);  
    }
    /**
     * Removes the listener from the collection of listeners who will
     * be notified when the control is verified.
     *
     * @param listener the listener which should no longer be notified
     *
     * @exception IllegalArgumentException <ul>
     *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
     * </ul>
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     *
     * @see VerifyListener
     * @see #addVerifyListener
     * 
     * @since 3.3
     */
    public void removeVerifyListener (VerifyListener listener) {
        checkWidget();
        if (listener == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
        removeListener(SWT.Verify, listener);
    }
    /**
     * Selects the item at the given zero-relative index in the receiver's 
     * list.  If the item at the index was already selected, it remains
     * selected. Indices that are out of range are ignored.
     *
     * @param index the index of the item to select
     *
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     */
    public void select (int index) {
        checkWidget();
        if (index == -1) {
            this.list.deselectAll ();
            this.text.setText (""); //$NON-NLS-1$
            return;
        }
        if (0 <= index && index < this.list.getItemCount()) {
            if (index != getSelectionIndex()) {
                this.text.setText (this.list.getItem (index));
                this.text.selectAll ();
                this.list.select (index);
                this.list.showSelection ();
            }
        }
    }
    public void setBackground (Color color) {
        super.setBackground(color);
        this.background = color;
        if (this.text != null) this.text.setBackground(color);
        if (this.list != null) this.list.setBackground(color);
        if (this.arrow != null) this.arrow.setBackground(color);
    }
    /**
     * Sets the editable state.
     *
     * @param editable the new editable state
     *
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     * 
     * @since 3.0
     */
    public void setEditable (boolean editable) {
        checkWidget ();
        this.text.setEditable(editable);
    }
    public void setEnabled (boolean enabled) {
        super.setEnabled(enabled);
        if (this.popup != null) this.popup.setVisible (false);
        if (this.text != null) this.text.setEnabled(enabled);
        if (this.arrow != null) this.arrow.setEnabled(enabled);
    }
    public boolean setFocus () {
        checkWidget();
        if (!isEnabled () || !isVisible ()) return false;
        if (isFocusControl ()) return true;
        return this.text.setFocus ();
    }
    public void setFont (Font font) {
        super.setFont (font);
        this.font = font;
        this.text.setFont (font);
        this.list.setFont (font);
        internalLayout (true);
    }
    public void setForeground (Color color) {
        super.setForeground(color);
        this.foreground = color;
        if (this.text != null) this.text.setForeground(color);
        if (this.list != null) this.list.setForeground(color);
        if (this.arrow != null) this.arrow.setForeground(color);
    }
    /**
     * Sets the text of the item in the receiver's list at the given
     * zero-relative index to the string argument. This is equivalent
     * to <code>remove</code>'ing the old item at the index, and then
     * <code>add</code>'ing the new item at that index.
     *
     * @param index the index for the item
     * @param string the new text for the item
     *
     * @exception IllegalArgumentException <ul>
     *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
     *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
     * </ul>
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     */
    public void setItem (int index, String string) {
        checkWidget();
        this.list.setItem (index, string);
    }
    /**
     * Sets the receiver's list to be the given array of items.
     *
     * @param items the array of items
     *
     * @exception IllegalArgumentException <ul>
     *    <li>ERROR_NULL_ARGUMENT - if the items array is null</li>
     *    <li>ERROR_INVALID_ARGUMENT - if an item in the items array is null</li>
     * </ul>
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     */
    public void setItems (String [] items) {
        checkWidget ();
        this.list.setItems (items);
        if (!this.text.getEditable ()) this.text.setText (""); //$NON-NLS-1$
    }
    /**
     * Sets the layout which is associated with the receiver to be
     * the argument which may be null.
     * <p>
     * Note: No Layout can be set on this Control because it already
     * manages the size and position of its children.
     * </p>
     *
     * @param layout the receiver's new layout or null
     *
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     */
    public void setLayout (Layout layout) {
        checkWidget ();
        return;
    }
    /**
     * Marks the receiver's list as visible if the argument is <code>true</code>,
     * and marks it invisible otherwise.
     * <p>
     * If one of the receiver's ancestors is not visible or some
     * other condition makes the receiver not visible, marking
     * it visible may not actually cause it to be displayed.
     * </p>
     *
     * @param visible the new visibility state
     *
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     * 
     * @since 3.4
     */
    public void setListVisible (boolean visible) {
        checkWidget ();
        dropDown(visible, false);
    }
    public void setMenu(Menu menu) {
        this.text.setMenu(menu);
    }
    /**
     * Sets the selection in the receiver's text field to the
     * range specified by the argument whose x coordinate is the
     * start of the selection and whose y coordinate is the end
     * of the selection. 
     *
     * @param selection a point representing the new selection start and end
     *
     * @exception IllegalArgumentException <ul>
     *    <li>ERROR_NULL_ARGUMENT - if the point is null</li>
     * </ul>
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     */
    public void setSelection (Point selection) {
        checkWidget();
        if (selection == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
        this.text.setSelection (selection.x, selection.y);
    }
    
    /**
     * Sets the contents of the receiver's text field to the
     * given string.
     * <p>
     * Note: The text field in a <code>Combo</code> is typically
     * only capable of displaying a single line of text. Thus,
     * setting the text to a string containing line breaks or
     * other special characters will probably cause it to 
     * display incorrectly.
     * </p>
     *
     * @param string the new text
     *
     * @exception IllegalArgumentException <ul>
     *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
     * </ul>
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     */
    public void setText (String string) {
        checkWidget();
        if (string == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
        int index = this.list.indexOf (string);
        if (index == -1) {
            this.list.deselectAll ();
            this.text.setText (string);
            return;
        }
        this.text.setText (string);
        this.text.selectAll ();
        this.list.setSelection (index);
        this.list.showSelection ();
    }
    /**
     * Sets the maximum number of characters that the receiver's
     * text field is capable of holding to be the argument.
     *
     * @param limit new text limit
     *
     * @exception IllegalArgumentException <ul>
     *    <li>ERROR_CANNOT_BE_ZERO - if the limit is zero</li>
     * </ul>
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     */
    public void setTextLimit (int limit) {
        checkWidget();
        this.text.setTextLimit (limit);
    }
    
    public void setToolTipText (String string) {
        checkWidget();
        super.setToolTipText(string);
        this.arrow.setToolTipText (string);
        this.text.setToolTipText (string);       
    }
    
    public void setVisible (boolean visible) {
        super.setVisible(visible);
        /* 
         * At this point the widget may have been disposed in a FocusOut event.
         * If so then do not continue.
         */
        if (isDisposed ()) return;
        if (!visible) this.popup.setVisible(false);
    }
    /**
     * Sets the number of items that are visible in the drop
     * down portion of the receiver's list.
     *
     * @param count the new number of items to be visible
     *
     * @exception SWTException <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     * 
     * @since 3.0
     */
    public void setVisibleItemCount (int count) {
        checkWidget ();
        if (count < 0) return;
        this.visibleItemCount = count;
    }
    String stripMnemonic (String string) {
        int index = 0;
        int length = string.length ();
        do {
            while ((index < length) && (string.charAt (index) != '&')) index++;
            if (++index >= length) return string;
            if (string.charAt (index) != '&') {
                return string.substring(0, index-1) + string.substring(index, length);
            }
            index++;
        } while (index < length);
        return string;
    }
    void textEvent (Event event) {
        switch (event.type) {
            case SWT.FocusIn: {
                handleFocus (SWT.FocusIn);
                break;
            }
            case SWT.DefaultSelection: {
                dropDown (false);
                Event e = new Event ();
                e.time = event.time;
                e.stateMask = event.stateMask;
                notifyListeners (SWT.DefaultSelection, e);
                break;
            }
            case SWT.KeyDown: {
                Event keyEvent = new Event ();
                keyEvent.time = event.time;
                keyEvent.character = event.character;
                keyEvent.keyCode = event.keyCode;
                keyEvent.stateMask = event.stateMask;
                notifyListeners (SWT.KeyDown, keyEvent);
                if (isDisposed ()) break;
                event.doit = keyEvent.doit;
                if (!event.doit) break;
                if (event.keyCode == SWT.ARROW_UP || event.keyCode == SWT.ARROW_DOWN) {
                    event.doit = false;
                    if ((event.stateMask & SWT.ALT) != 0) {
                        boolean dropped = isDropped ();
                        this.text.selectAll ();
                        if (!dropped) setFocus ();
                        dropDown (!dropped);
                        break;
                    }
    
                    int oldIndex = getSelectionIndex ();
                    if (event.keyCode == SWT.ARROW_UP) {
                        select (Math.max (oldIndex - 1, 0));
                    } else {
                        select (Math.min (oldIndex + 1, getItemCount () - 1));
                    }
                    if (oldIndex != getSelectionIndex ()) {
                        Event e = new Event();
                        e.time = event.time;
                        e.stateMask = event.stateMask;
                        notifyListeners (SWT.Selection, e);
                    }
                    if (isDisposed ()) break;
                }
                
                // Further work : Need to add support for incremental search in 
                // pop up list as characters typed in text widget
                break;
            }
            case SWT.KeyUp: {
                Event e = new Event ();
                e.time = event.time;
                e.character = event.character;
                e.keyCode = event.keyCode;
                e.stateMask = event.stateMask;
                notifyListeners (SWT.KeyUp, e);
                event.doit = e.doit;
                break;
            }
            case SWT.MenuDetect: {
                Event e = new Event ();
                e.time = event.time;
                notifyListeners (SWT.MenuDetect, e);
                break;
            }
            case SWT.Modify: {
                this.list.deselectAll ();
                Event e = new Event ();
                e.time = event.time;
                notifyListeners (SWT.Modify, e);
                break;
            }
            case SWT.MouseDown: {
                if (event.button != 1) return;
                if (this.text.getEditable ()) return;
                boolean dropped = isDropped ();
                this.text.selectAll ();
                if (!dropped) setFocus ();
                dropDown (!dropped);
                break;
            }
            case SWT.MouseUp: {
                if (event.button != 1) return;
                if (this.text.getEditable ()) return;
                this.text.selectAll ();
                break;
            }
            case SWT.Traverse: {        
                switch (event.detail) {
                    //NOT SURE
    //              case SWT.TRAVERSE_RETURN:
                    case SWT.TRAVERSE_ARROW_PREVIOUS:
                    case SWT.TRAVERSE_ARROW_NEXT:
                        // The enter causes default selection and
                        // the arrow keys are used to manipulate the list contents so
                        // do not use them for traversal.
                        event.doit = false;
                        break;
                }
                
                Event e = new Event ();
                e.time = event.time;
                e.detail = event.detail;
                e.doit = event.doit;
                e.character = event.character;
                e.keyCode = event.keyCode;
                notifyListeners (SWT.Traverse, e);
                event.doit = e.doit;
                event.detail = e.detail;
                break;
            }
            case SWT.Verify: {
                Event e = new Event ();
                e.text = event.text;
                e.start = event.start;
                e.end = event.end;
                e.character = event.character;
                e.keyCode = event.keyCode;
                e.stateMask = event.stateMask;
                notifyListeners (SWT.Verify, e);
                event.doit = e.doit;
                break;
            }
        }
    }
    }
    
    
    
    
    
    
    
}
