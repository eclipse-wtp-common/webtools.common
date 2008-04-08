/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.ui.internal.dialog;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.wst.validation.MessageSeveritySetting;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.ValManager;
import org.eclipse.wst.validation.internal.ValMessages;
import org.eclipse.wst.validation.internal.delegates.ValidatorDelegateDescriptor;
import org.eclipse.wst.validation.internal.delegates.ValidatorDelegatesRegistry;
import org.eclipse.wst.validation.internal.model.FilterGroup;
import org.eclipse.wst.validation.internal.model.FilterRule;
import org.eclipse.wst.validation.internal.model.ValidatorHelper;
import org.eclipse.wst.validation.ui.internal.AdapterFactory;
import org.eclipse.wst.validation.ui.internal.ValUIMessages;

/**
 * Display a filter dialog for a particular Validator, that is a list of all the filters that are active for
 * the validator.
 * 
 * @author karasiuk
 *
 */
public class FilterDialog extends Dialog {
	
	/** 
	 * If we are doing project level filters this will point to the project. This is null if we are doing
	 * workspace level filters. 
	 */
	private IProject	_project;
	
	/** 
	 * A deep copy of the validator, so that we can use it as a model object, and not worry about the
	 * user not saving their changes.
	 */
	private Validator 	_validator;
	
	// The V2 version of _validator.
	private Validator.V2	_v2;
	private TreeViewer		_tree;
	private Combo			_delegating;
	private IAdapterFactory _adaptorFactory = new AdapterFactory();
	
	private Button		_addGroupInclude;
	private Button		_addGroupExclude;
	private Button		_addRule;
	private Button		_remove;
	private ISelectionChangedListener	_nodeChangedListener;
	
	private FilterGroup	_selectedGroup;
	private FilterRule	_selectedRule;
	
	private Combo[]		_messageSev;
	
	private static String[] _messages = new String[]{
		ValMessages.SevError, ValMessages.SevWarning, ValMessages.SevIgnore};
	
	private Shell		_shell;
	
	/**
	 * Create a dialog that knows how to change a validator's filters.
	 * 
	 * @param shell
	 * 
	 * @param validator the validator that is being updated.
	 * 
	 * @param project the project that the filters are being added to. If these are workspace
	 * level filters, then this must be null. 
	 */
	public FilterDialog(Shell shell, Validator validator, IProject project){
		super(shell);
		_shell = shell;
		setShellStyle(getShellStyle() | SWT.CLOSE|SWT.MIN|SWT.MAX|SWT.RESIZE);
		_validator = validator.copy(true);
		_v2 = _validator.asV2Validator();
		_project = project;
	}
	
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(NLS.bind(ValUIMessages.fdTitle, _validator.getName()));
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite c = (Composite)super.createDialogArea(parent);
		c.setLayout(new GridLayout(2, false));
		if (_v2 == null){
			new Label(c, SWT.NONE).setText(ValUIMessages.fdNoFilters);
		}
		else {
			Label blurb = new Label(c, SWT.LEFT | SWT.WRAP);
			blurb.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
			blurb.setText(ValUIMessages.FilterHelp);
			
			_tree = new TreeViewer(c, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
			_tree.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			Platform.getAdapterManager().registerAdapters(_adaptorFactory, Validator.V2.class);
			Platform.getAdapterManager().registerAdapters(_adaptorFactory, FilterGroup.class);
			Platform.getAdapterManager().registerAdapters(_adaptorFactory, FilterRule.class);
			_tree.setContentProvider(new BaseWorkbenchContentProvider());
			_tree.setLabelProvider(new WorkbenchLabelProvider());
			_tree.setInput(_v2);
			_tree.expandAll();
			
			addButtons(c);
			
			String delegatingId = _v2.getDelegatingId();
			if (delegatingId != null){
				addDelegatorSelection(c);
			}
				
			_nodeChangedListener = new ISelectionChangedListener(){

				public void selectionChanged(SelectionChangedEvent event) {
					_selectedGroup = null;
					_selectedRule = null;
					if (event.getSelection() instanceof IStructuredSelection){
						IStructuredSelection sel = (IStructuredSelection)event.getSelection();
						if (sel.getFirstElement() instanceof FilterGroup){
							_selectedGroup = (FilterGroup)sel.getFirstElement();
						}
						else if (sel.getFirstElement() instanceof FilterRule){
							_selectedRule = (FilterRule)sel.getFirstElement();
						}
					}
					updateButtons();
				}
				
			};
			
			_tree.addSelectionChangedListener(_nodeChangedListener);
			
			addMessageMappings(c);
		}
		return c;
	}

	private void addButtons(Composite c) {
		Composite buttons = new Composite(c, SWT.TOP);
		GridData gd = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		buttons.setLayoutData(gd);
		buttons.setLayout(new GridLayout(1, true));
		_addGroupInclude = new Button(buttons, SWT.PUSH | SWT.FILL | SWT.CENTER);
		_addGroupInclude.setText(ValUIMessages.ButtonAddGroupInclude);
		_addGroupInclude.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		
		_addGroupInclude.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				addGroup(false);				
			}

			public void widgetSelected(SelectionEvent e) {
				addGroup(false);						
			}
			
		});
			
		_addGroupExclude = new Button(buttons, SWT.PUSH | SWT.FILL | SWT.CENTER);
		_addGroupExclude.setText(ValUIMessages.ButtonAddGroupExclude);
		_addGroupExclude.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		
		_addGroupExclude.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				addGroup(true);				
			}

			public void widgetSelected(SelectionEvent e) {
				addGroup(true);						
			}
			
		});

		_addRule = new Button(buttons, SWT.PUSH | SWT.FILL | SWT.CENTER);
		_addRule.setText(ValUIMessages.ButtonAddRule);
		_addRule.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		_addRule.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				doIt();
			}

			public void widgetSelected(SelectionEvent e) {
				doIt();
			}
			
			private void doIt(){
				NewFilterRule nfr = new NewFilterRule(_project);
				WizardDialog wd = new WizardDialog(_shell, nfr);
				wd.setBlockOnOpen(true);
				int rc = wd.open();
				if (rc == WizardDialog.CANCEL)return;
				
				FilterRule rule = nfr.getRule();
				if (rule != null){
					_selectedGroup.add(rule);
					_v2.bumpChangeCountGroups();
					refresh();
				}
			}
			
		});

		_remove = new Button(buttons, SWT.PUSH | SWT.FILL | SWT.CENTER);
		_remove.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		_remove.setText(ValUIMessages.ButtonRemove);
		_remove.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				remove();					
			}

			public void widgetSelected(SelectionEvent e) {
				remove();						
			}				
		});
		
		Button restore = new Button(buttons, SWT.PUSH | SWT.FILL | SWT.CENTER);
		restore.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		String defaults = JFaceResources.getString("defaults"); //$NON-NLS-1$
		restore.setText(defaults);
		restore.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				restoreDefaults();					
			}

			public void widgetSelected(SelectionEvent e) {
				restoreDefaults();						
			}				
		});
	}
	
	private void restoreDefaults() {
		if (_v2 != null){
			try {
				String id = _v2.getId();
				Validator[] vals = ValManager.getDefaultValidators();
				for (Validator v : vals){
					if (v.getId().equals(id)){
						_validator = v;
						_v2 = v.asV2Validator();
						_tree.setInput(_v2);
						_tree.expandAll();
						refresh();
						return;
					}
				}
			}
			catch (InvocationTargetException e){
				
			}
		}
		
	}


	private void addMessageMappings(Composite c) {
		if (_v2 == null)return;
		Map<String,MessageSeveritySetting> mappings = _validator.getMessageSettings();
		if (mappings == null || mappings.size() == 0)return;
		
		Group group = new Group(c, SWT.NONE);
		group.setText(ValUIMessages.FrMsgSev);
		group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
		group.setLayout(new GridLayout(2, false));
		
//		Label heading = new Label(c, SWT.LEFT);
//		heading.setText(ValUIMessages.ErrorsWarnings);
//		heading.setFont(JFaceResources.getHeaderFont());
//		heading.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
		
		_messageSev = new Combo[mappings.size()];
		int i= 0;
		for (MessageSeveritySetting ms : mappings.values()){
			Label label = new Label(group, SWT.LEFT);
			label.setText("   " + ms.getLabel() + ":"); //$NON-NLS-1$ //$NON-NLS-2$
			
			Combo sev = new Combo(group, SWT.RIGHT);
			_messageSev[i++] = sev;
			sev.setItems(_messages);
			sev.select(ms.getCurrent().ordinal());
			sev.setData(ms);
			sev.addSelectionListener(new SelectionListener(){

				public void widgetDefaultSelected(SelectionEvent e) {
					select(e);
				}

				public void widgetSelected(SelectionEvent e) {
					select(e);
				}
				
				private void select(SelectionEvent e){
					Combo w = (Combo)e.widget;
					MessageSeveritySetting ms = (MessageSeveritySetting)w.getData();
					int i = w.getSelectionIndex();
					if (ms.setCurrent(MessageSeveritySetting.Severity.values()[i]))
						_validator.bumpChangeCountMessages();
				}
				
			});
		}
	}
		
	/**
	 * Add a combo box so that the user can change which delegating validator to call.
	 */
	private void addDelegatorSelection(Composite c) {
		Map map = ValidatorDelegatesRegistry.getInstance().getDelegateDescriptors(_v2.getValidatorClassname());
		if (map == null)return;
		
		Composite line = new Composite(c, SWT.NONE);
		line.setLayout(new RowLayout(SWT.HORIZONTAL));

		Label label = new Label(line, SWT.CENTER);
		label.setText(ValUIMessages.DelegatesComboLabel);

				
		_delegating = new Combo(line, SWT.READ_ONLY);
		String[] items = new String[map.size()];
		final String ids[] = new String[map.size()];
		String selected = null;
		Iterator it = map.values().iterator();
		for (int i=0; i<items.length;i++){
			ValidatorDelegateDescriptor vd = (ValidatorDelegateDescriptor)it.next();
			items[i] = vd.getName();
			ids[i] = vd.getId();
			if (vd.getId().equals(_v2.getDelegatingId())){
				selected = vd.getName();
			}
		}
		_delegating.setItems(items);
		_delegating.setText(selected);
		_delegating.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				int sel = _delegating.getSelectionIndex();
				_v2.setDelegatingId(ids[sel]);
			}
			
		});
	}

	/**
	 * Add a new filter group to the validator.
	 * @param exclude
	 */
	private void addGroup(boolean exclude){
		if (_v2 == null)return;
		_v2.add(FilterGroup.create(exclude));
		refresh();
		
	}
	
	private void refresh(){
		_tree.refresh();
		updateButtons();		
	}
	
	/**
	 * Remove the current selection from the validator.
	 */
	private void remove(){
		if (_selectedRule != null){
			FilterGroup[] groups = _v2.getGroups();
			for (int i=0; i<groups.length; i++){
				if (groups[i].remove(_selectedRule)){
					_v2.bumpChangeCountGroups();
					refresh();
					return;
				}
			}
		}
		
		if (_selectedGroup != null){
			_v2.remove(_selectedGroup);
			refresh();
			return;
		}
	}
	
	private void updateButtons() {
		if (_v2 != null){
			_addGroupExclude.setEnabled(!ValidatorHelper.hasExcludeGroup(_v2));
		}
		_addRule.setEnabled(_selectedGroup != null);
		_remove.setEnabled(_selectedGroup != null || _selectedRule != null);
		if (_messageSev != null){
			Map<String,MessageSeveritySetting> msgs = _validator.getMessageSettings();
			if (msgs != null && _messageSev.length == msgs.size()){
				int i = 0;
				for (MessageSeveritySetting ms : msgs.values()){
					_messageSev[i++].select(ms.getCurrent().ordinal());
				}
			}
		}
	}


	public boolean close() {
		Platform.getAdapterManager().unregisterAdapters(_adaptorFactory);
		if (_tree != null)_tree.removeSelectionChangedListener(_nodeChangedListener);
		return super.close();
	}
	
	protected Point getInitialSize() {
		return new Point(550, 475);
	}

	public Validator getValidator() {
		return _validator;
	}
}
