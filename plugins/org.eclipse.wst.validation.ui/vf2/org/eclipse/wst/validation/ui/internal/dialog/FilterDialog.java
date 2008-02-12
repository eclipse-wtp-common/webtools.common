package org.eclipse.wst.validation.ui.internal.dialog;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.ValPrefManagerGlobal;
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
	
	private Validator	_origValidator;
	
	/** 
	 * If we are doing project level filters this will point to the project. If this is null if we are doing
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
		setShellStyle(SWT.CLOSE|SWT.MIN|SWT.MAX|SWT.RESIZE);
		_origValidator = validator;
		_validator = validator.copy();
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

		}
		return c;
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
	
	protected void okPressed() {
		super.okPressed();
		Validator.V2 v = _origValidator.asV2Validator();
		if (v != null){
			v.setGroups(_v2.getGroups());
			v.setDelegatingId(_v2.getDelegatingId());
			ValPrefManagerGlobal vpm = new ValPrefManagerGlobal();
			vpm.save(v);
		}
	}
		
	private void updateButtons() {
		if (_v2 != null){
			_addGroupExclude.setEnabled(!ValidatorHelper.hasExcludeGroup(_v2));
		}
		_addRule.setEnabled(_selectedGroup != null);
		_remove.setEnabled(_selectedGroup != null || _selectedRule != null);
	}


	public boolean close() {
		Platform.getAdapterManager().unregisterAdapters(_adaptorFactory);
		if (_tree != null)_tree.removeSelectionChangedListener(_nodeChangedListener);
		return super.close();
	}
	
	protected Point getInitialSize() {
		return new Point(550, 475);
	}
}
