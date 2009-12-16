/******************************************************************************
 * Copyright (c) 2009 Red Hat
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rob Stryker - initial implementation and ongoing maintenance
 ******************************************************************************/
package org.eclipse.wst.common.componentcore.ui.propertypage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.datamodel.properties.ICreateReferenceComponentsDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.ComponentResource;
import org.eclipse.wst.common.componentcore.internal.DefaultModuleHandler;
import org.eclipse.wst.common.componentcore.internal.DependencyType;
import org.eclipse.wst.common.componentcore.internal.IModuleHandler;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.internal.operation.CreateReferenceComponentsDataModelProvider;
import org.eclipse.wst.common.componentcore.internal.operation.RemoveReferenceComponentsDataModelProvider;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualArchiveComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.componentcore.ui.Messages;
import org.eclipse.wst.common.componentcore.ui.ModuleCoreUIPlugin;
import org.eclipse.wst.common.componentcore.ui.internal.propertypage.AddFolderDialog;
import org.eclipse.wst.common.componentcore.ui.internal.propertypage.ComponentDependencyContentProvider;
import org.eclipse.wst.common.componentcore.ui.internal.propertypage.NewReferenceWizard;
import org.eclipse.wst.common.componentcore.ui.internal.taskwizard.WizardFragment;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;
 
public class AddModuleDependenciesPropertiesPage implements Listener,
		IModuleDependenciesControl, ILabelProviderListener {

	private static final String DEPLOY_PATH_PROPERTY = new Integer(0).toString();
	private static final String SOURCE_PROPERTY = new Integer(1).toString();
	
	
	protected final String PATH_SEPARATOR = String.valueOf(IPath.SEPARATOR);
	private boolean hasInitialized = false;
	protected final IProject project;
	protected final ModuleAssemblyRootPage propPage;
	protected IVirtualComponent rootComponent = null;
	protected Text componentNameText;
	protected Label availableModules;
	protected TableViewer availableComponentsViewer;
	protected Button addMappingButton, addReferenceButton, editReferenceButton, removeButton;
	protected Composite buttonColumn;
	protected static final IStatus OK_STATUS = IDataModelProvider.OK_STATUS;
	protected Listener tableListener;
	protected Listener labelListener;

	// Mappings that existed when the page was opened (or last saved)
	protected HashMap<IVirtualComponent, String> oldComponentToRuntimePath = new HashMap<IVirtualComponent, String>();

	// Mappings that are current
	protected HashMap<IVirtualComponent, String> objectToRuntimePath = new HashMap<IVirtualComponent, String>();

	protected ArrayList<IVirtualComponent> consumedReferences = new ArrayList<IVirtualComponent>();

	// A single list of wb-resource mappings. If there's any change, 
	// all old will be removed and new ones added
	protected ArrayList<ComponentResourceProxy> resourceMappings = new ArrayList<ComponentResourceProxy>();
	
	// keeps track if a change has occurred in wb-resource mappings
	protected boolean resourceMappingsChanged = false;
	
	protected IModuleHandler moduleHandler;
	
	/**
	 * Constructor for AddModulestoEARPropertiesControl.
	 */
	public AddModuleDependenciesPropertiesPage(final IProject project,
			final ModuleAssemblyRootPage page) {
		this.project = project;
		this.propPage = page;
		rootComponent = ComponentCore.createComponent(project);
	}

	/*
	 * UI Creation Methods
	 */

	public Composite createContents(final Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		ModuleAssemblyRootPage.createDescriptionComposite(composite,
				getModuleAssemblyRootPageDescription());
		createListGroup(composite);
		refresh();
		Dialog.applyDialogFont(parent);
		return composite;
	}

	protected String getModuleAssemblyRootPageDescription() {
		return Messages.ModuleAssemblyRootPageDescription;
	}

	protected void createListGroup(Composite parent) {
		Composite listGroup = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		listGroup.setLayout(layout);
		GridData gData = new GridData(GridData.FILL_BOTH);
		gData.horizontalIndent = 5;
		listGroup.setLayoutData(gData);

		availableModules = new Label(listGroup, SWT.NONE);
		gData = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_FILL);
		availableModules.setText(Messages.ModuleAssembly); 
		availableModules.setLayoutData(gData);
		createTableComposite(listGroup);
	}

	protected void createTableComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridData gData = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(gData);
		fillComposite(composite);
	}

	public void fillComposite(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		parent.setLayout(layout);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		createTable(parent);
		createButtonColumn(parent);
	}

	protected void createButtonColumn(Composite parent) {
		buttonColumn = createButtonColumnComposite(parent);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END);
		buttonColumn.setLayoutData(data);
		createPushButtons();
	}

	protected void createPushButtons() {
		addMappingButton = createPushButton(getAddFolderLabel());
		addReferenceButton = createPushButton(getAddReferenceLabel());
		editReferenceButton = createPushButton(getEditReferenceLabel());
		removeButton = createPushButton(getRemoveSelectedLabel());
	}

	protected String getRemoveSelectedLabel() {
		return Messages.RemoveSelected;
	}

	protected String getEditReferenceLabel() {
		return Messages.EditReference;
	}

	protected String getAddReferenceLabel() {
		return Messages.AddReference;
	}

	protected String getAddFolderLabel() {
		return Messages.AddFolderElipses;
	}

	protected Button createPushButton(String label) {
		Button aButton = new Button(buttonColumn, SWT.PUSH);
		aButton.setText(label);
		aButton.addListener(SWT.Selection, this);
		aButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return aButton;
	}

	public Composite createButtonColumnComposite(Composite parent) {
		Composite aButtonColumn = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		aButtonColumn.setLayout(layout);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		aButtonColumn.setLayoutData(data);
		return aButtonColumn;
	}

	public Group createGroup(Composite parent) {
		return new Group(parent, SWT.NULL);
	}

	protected void createTable(Composite parent) {
		if (rootComponent != null) {
			availableComponentsViewer = createAvailableComponentsViewer(parent);
			GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL
					| GridData.FILL_VERTICAL);
			availableComponentsViewer.getTable().setLayoutData(gd);

			ComponentDependencyContentProvider provider = createProvider();
			provider.setRuntimePaths(objectToRuntimePath);
			provider.setResourceMappings(resourceMappings);
			availableComponentsViewer.setContentProvider(provider);
			//availableComponentsViewer.setLabelProvider(new DecoratingLabelProvider(
	        //        new WorkbenchLabelProvider(), PlatformUI.getWorkbench().
	        //         getDecoratorManager().getLabelDecorator()));
			availableComponentsViewer.setLabelProvider(provider);
			addTableListeners();
		}
	}

	/**
	 * Subclasses should over-ride this and extend the class
	 */
	protected ComponentDependencyContentProvider createProvider() {
		return new ComponentDependencyContentProvider(this);
	}

	/*
	 * Listeners of various events
	 */

	protected void addTableListeners() {
		addHoverHelpListeners();
		addDoubleClickListener();
		addSelectionListener();
	}

	protected void addHoverHelpListeners() {
		final Table table = availableComponentsViewer.getTable();
		createLabelListener(table);
		createTableListener(table);
		table.addListener(SWT.Dispose, tableListener);
		table.addListener(SWT.KeyDown, tableListener);
		table.addListener(SWT.MouseMove, tableListener);
		table.addListener(SWT.MouseHover, tableListener);
	}

	protected void createLabelListener(final Table table) {
		labelListener = new Listener() {
			public void handleEvent(Event event) {
				Label label = (Label) event.widget;
				Shell shell = label.getShell();
				switch (event.type) {
				case SWT.MouseDown:
					Event e = new Event();
					e.item = (TableItem) label.getData("_TABLEITEM"); //$NON-NLS-1$
					table.setSelection(new TableItem[] { (TableItem) e.item });
					table.notifyListeners(SWT.Selection, e);
					shell.dispose();
					table.setFocus();
					break;
				case SWT.MouseExit:
					shell.dispose();
					break;
				}
			}
		};
	}

	protected void createTableListener(final Table table) {
		tableListener = new Listener() {
			Shell tip = null;
			Label label = null;

			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.Dispose:
				case SWT.KeyDown:
				case SWT.MouseMove: {
					if (tip == null)
						break;
					tip.dispose();
					tip = null;
					label = null;
					break;
				}
				case SWT.MouseHover: {
					TableItem item = table.getItem(new Point(event.x, event.y));
					if (item != null && item.getData() != null && !canEdit(item.getData())) {
						if (tip != null && !tip.isDisposed())
							tip.dispose();
						tip = new Shell(PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow().getShell(),
								SWT.ON_TOP | SWT.NO_FOCUS | SWT.TOOL);
						tip.setBackground(Display.getDefault().getSystemColor(
								SWT.COLOR_INFO_BACKGROUND));
						FillLayout layout = new FillLayout();
						layout.marginWidth = 2;
						tip.setLayout(layout);
						label = new Label(tip, SWT.WRAP);
						label.setForeground(Display.getDefault()
								.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
						label.setBackground(Display.getDefault()
								.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
						label.setData("_TABLEITEM", item); //$NON-NLS-1$
						label.setText( Messages.InternalLibJarWarning);
						label.addListener(SWT.MouseExit, labelListener);
						label.addListener(SWT.MouseDown, labelListener);
						Point size = tip.computeSize(SWT.DEFAULT, SWT.DEFAULT);
						Rectangle rect = item.getBounds(0);
						Point pt = table.toDisplay(rect.x, rect.y);
						tip.setBounds(pt.x, pt.y - size.y, size.x, size.y);
						tip.setVisible(true);
					}
				}
				}
			}
		};
	}

	protected boolean canEdit(Object data) {
		if( data == null ) return false;
		if( !(data instanceof VirtualArchiveComponent)) return true;
		
		VirtualArchiveComponent d2 = (VirtualArchiveComponent)data;
		boolean sameProject = d2.getWorkspaceRelativePath() != null
			&& d2.getWorkspaceRelativePath().segment(0)
				.equals(rootComponent.getProject().getName());
		return !(sameProject && isPhysicallyAdded(d2));
	}
	
	protected void addDoubleClickListener() {
		availableComponentsViewer.setColumnProperties(new String[] { 
				DEPLOY_PATH_PROPERTY, SOURCE_PROPERTY });
		
		CellEditor[] editors = new CellEditor[] { 
				new TextCellEditor(availableComponentsViewer.getTable()),
				new TextCellEditor()};
		availableComponentsViewer.setCellEditors(editors);
		availableComponentsViewer
				.setCellModifier(new RuntimePathCellModifier());
	}

	protected void addSelectionListener() {
		availableComponentsViewer.addSelectionChangedListener(
				new ISelectionChangedListener(){
					public void selectionChanged(SelectionChangedEvent event) {
						viewerSelectionChanged();
					}
				});
	}
	
	protected void viewerSelectionChanged() {
		editReferenceButton.setEnabled(hasEditWizardPage(getSelectedObject()));
		removeButton.setEnabled(getSelectedObject() != null && canEdit(getSelectedObject()));
	}
	
	protected boolean hasEditWizardPage(Object o) {
		if( o == null || !(o instanceof IVirtualComponent)) 
			return false;
		WizardFragment wf = NewReferenceWizard.getFirstEditingFragment((IVirtualComponent)o);
		return wf != null;
	}

	protected Object getSelectedObject() {
		IStructuredSelection sel = (IStructuredSelection)availableComponentsViewer.getSelection();
		return sel.getFirstElement();
	}
	
	private class RuntimePathCellModifier implements ICellModifier {

		public boolean canModify(Object element, String property) {
			if( property.equals(DEPLOY_PATH_PROPERTY)) {
				if (element instanceof VirtualArchiveComponent) {
					try {
						return canEdit(element);
					} catch (IllegalArgumentException iae) {
					}
				}
				return true;
			}
			return false;
		}

		public Object getValue(Object element, String property) {
			Object data = element; //((TableItem)element).getData();
			if( data instanceof IVirtualComponent ) {
				return objectToRuntimePath.get(element) == null ? new Path("/") //$NON-NLS-1$
						.toString() : objectToRuntimePath.get(element);
			} else if( data instanceof ComponentResourceProxy) {
				return ((ComponentResourceProxy)data).runtimePath.toString();
			}
			return new Path("/"); //$NON-NLS-1$
		}

		public void modify(Object element, String property, Object value) {
			if (property.equals(DEPLOY_PATH_PROPERTY)) {
				TableItem item = (TableItem) element;
				if( item.getData() instanceof IVirtualComponent) {
					objectToRuntimePath.put((IVirtualComponent)item.getData(), (String) value);
				} else if( item.getData() instanceof ComponentResourceProxy) {
					ComponentResourceProxy c = ((ComponentResourceProxy)item.getData());
					c.runtimePath = new Path((String)value);
					resourceMappingsChanged = true;
				}
				refresh();
			}
		}

	}

	public void handleEvent(Event event) {
		if( event.widget == addMappingButton) 
			handleAddMappingButton();
		else if( event.widget == addReferenceButton) 
			handleAddReferenceButton();
		else if( event.widget == editReferenceButton) 
			handleEditReferenceButton();
		else if( event.widget == removeButton ) 
			handleRemoveSelectedButton();
	}

	protected void handleAddMappingButton() {
		AddFolderDialog afd = new AddFolderDialog(addMappingButton.getShell(), project);
		if( afd.open() == Window.OK) {
			IContainer c = afd.getSelected();
			if( c != null ) {
				IPath p = c.getProjectRelativePath();
				ComponentResourceProxy proxy = new ComponentResourceProxy(p, new Path("/")); //$NON-NLS-1$
				resourceMappings.add(proxy);
				refresh();
			}
		}
	}
	
	protected void handleAddReferenceButton() {
		showReferenceWizard(false);
	}
	
	protected void handleEditReferenceButton() {
		showReferenceWizard(true);
	}
	
	protected void showReferenceWizard(boolean editing) {
		NewReferenceWizard wizard = new NewReferenceWizard();
		// fill the task model
		wizard.getTaskModel().putObject(IReferenceWizardConstants.PROJECT, project);
		wizard.getTaskModel().putObject(IReferenceWizardConstants.ROOT_COMPONENT, rootComponent);
		wizard.getTaskModel().putObject(IReferenceWizardConstants.MODULEHANDLER, getModuleHandler());

		IVirtualComponent selected = null;
		if( editing ) {
			Object o = ((IStructuredSelection)availableComponentsViewer.getSelection()).getFirstElement();
			if( o instanceof IVirtualComponent ) {
				selected = (IVirtualComponent)o;
				wizard.getTaskModel().putObject(IReferenceWizardConstants.COMPONENT, selected);
				wizard.getTaskModel().putObject(IReferenceWizardConstants.COMPONENT_PATH, objectToRuntimePath.get(selected));
			}
		}
		
		WizardDialog wd = new WizardDialog(addReferenceButton.getShell(), wizard);
		if( wd.open() != Window.CANCEL) {
			if( editing && selected != null) {
				// remove old
				objectToRuntimePath.remove(selected); 
				consumedReferences.remove(selected);
			}
			
			Object c1 = wizard.getTaskModel().getObject(IReferenceWizardConstants.COMPONENT);
			Object p1 = wizard.getTaskModel().getObject(IReferenceWizardConstants.COMPONENT_PATH);
			DependencyType type = (DependencyType)wizard.getTaskModel().getObject(IReferenceWizardConstants.DEPENDENCY_TYPE);
			boolean consumed = type == null ? false : type.equals(DependencyType.CONSUMES_LITERAL);
			
			IVirtualComponent[] compArr = c1 instanceof IVirtualComponent ? 
					new IVirtualComponent[] { (IVirtualComponent)c1 } : 
						(IVirtualComponent[])c1;
			String[] pathArr = p1 instanceof String ? 
							new String[] { (String)p1 } : 
								(String[])p1;
			for( int i = 0; i < compArr.length; i++ ) {
				objectToRuntimePath.put(compArr[i], 
						getRuntimePath(compArr[i], pathArr[i]));
				if( consumed ) 
					consumedReferences.add(compArr[i]);
			}
			refresh();
		}
	}
	
	/**
	 * Subclasses are encouraged to override this method if 
	 * they have some specific place (webinf/lib etc) where certain
	 * types of references should default to. 
	 * 
	 * If the subclass does not understand or know about the 
	 * this component type, or if it has no customizations to perform,
	 * it should return the provided wizardPath unchanged
	 * 
	 * @param addedComp The component being added as a reference
	 * @param addedPath The path that the wizard suggests as the path
	 * @return The runtime path that should be added to the properties page
	 */
	protected String getRuntimePath(IVirtualComponent addedComp, String wizardPath) {
		return wizardPath;
	}
	
	protected void handleRemoveSelectedButton() {
		ISelection sel = availableComponentsViewer.getSelection();
		if( sel instanceof IStructuredSelection ) {
			Object o = ((IStructuredSelection)sel).getFirstElement();
			if( o instanceof IVirtualComponent)
				objectToRuntimePath.remove(o);
			else if( o instanceof ComponentResourceProxy) 
				resourceMappings.remove(o);
			refresh();
		}
	}

	public TableViewer createAvailableComponentsViewer(Composite parent) {
		int flags = SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI;

		Table table = new Table(parent, flags);
		availableComponentsViewer = new TableViewer(table);

		// set up table layout
		TableLayout tableLayout = new org.eclipse.jface.viewers.TableLayout();
		tableLayout.addColumnData(new ColumnWeightData(400, true));
		tableLayout.addColumnData(new ColumnWeightData(500, true));
		table.setLayout(tableLayout);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		availableComponentsViewer.setSorter(null);

		TableColumn bndColumn = new TableColumn(table, SWT.NONE, 0);
		bndColumn.setText(Messages.DeployPathColumn);
		bndColumn.setResizable(true);

		TableColumn projectColumn = new TableColumn(table, SWT.NONE, 1);
		projectColumn.setText(Messages.SourceColumn);
		projectColumn.setResizable(true);

		tableLayout.layout(table, true);
		return availableComponentsViewer;

	}

	protected boolean isPhysicallyAdded(VirtualArchiveComponent component) {
		try {
			component.getProjectRelativePath();
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	/**
	 * This should only be called on changes, such as adding a project
	 * reference, adding a lib reference etc.
	 * 
	 * It will reset the input, manually re-add missing elements, and do other
	 * tasks
	 */
	public void refresh() {
		resetTableUI();
		if (!hasInitialized) {
			initialize();
			resetTableUI();
		}

	}

	protected void resetTableUI() {
		IWorkspaceRoot input = ResourcesPlugin.getWorkspace().getRoot();
		availableComponentsViewer.setInput(input);
		GridData data = new GridData(GridData.FILL_BOTH);
		int numlines = Math.min(10, availableComponentsViewer.getTable()
				.getItemCount());
		data.heightHint = availableComponentsViewer.getTable().getItemHeight()
				* numlines;
		availableComponentsViewer.getTable().setLayoutData(data);
		GridData btndata = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		buttonColumn.setLayoutData(btndata);
	}

	protected void initialize() {
		IVirtualReference[] refs = rootComponent.getReferences();
		IVirtualComponent comp;
		for( int i = 0; i < refs.length; i++ ) { 
			comp = refs[i].getReferencedComponent();
			String archiveName = refs[i].getDependencyType() == DependencyType.CONSUMES ? null : refs[i].getArchiveName();
			String val = (archiveName != null) ? refs[i].getRuntimePath().append(archiveName).toString() : refs[i].getRuntimePath().toString();
			objectToRuntimePath.put(comp, val);
			oldComponentToRuntimePath.put(comp, val);
			if( refs[i].getDependencyType() == DependencyType.CONSUMES)
				consumedReferences.add(comp);
		}

		ComponentResource[] allMappings = findAllMappings();
		for( int i = 0; i < allMappings.length; i++ ) {
			resourceMappings.add(new ComponentResourceProxy(
					allMappings[i].getSourcePath(), allMappings[i].getRuntimePath()
			));
		}
		editReferenceButton.setEnabled(false);
		removeButton.setEnabled(false);
		hasInitialized = true;
	}

	protected ComponentResource[] findAllMappings() {
		StructureEdit structureEdit = null;
		try {
			structureEdit = StructureEdit.getStructureEditForRead(project);
			WorkbenchComponent component = structureEdit.getComponent();
			Object[] arr = component.getResources().toArray();
			ComponentResource[] result = new ComponentResource[arr.length];
			for( int i = 0; i < arr.length; i++ )
				result[i] = (ComponentResource)arr[i];
			return result;
		} catch(Exception e) {
		} finally {
			structureEdit.dispose();
		}
		return new ComponentResource[]{};
	}
	
	public class ComponentResourceProxy {
		public IPath source, runtimePath;
		public ComponentResourceProxy(IPath source, IPath runtimePath) {
			this.source = source;
			this.runtimePath = runtimePath;
		}
	}
	
	/*
	 * Clean-up methods are below. These include performCancel, performDefaults,
	 * performOK, and any other methods that are called *only* by this one.
	 */
	public void setVisible(boolean visible) {
	}

	public void performDefaults() {
	}

	public boolean performCancel() {
		return true;
	}

	public void dispose() {
		Table table = null;
		if (availableComponentsViewer != null) {
			table = availableComponentsViewer.getTable();
		}
		if (table == null || tableListener == null)
			return; 
		table.removeListener(SWT.Dispose, tableListener);
		table.removeListener(SWT.KeyDown, tableListener);
		table.removeListener(SWT.MouseMove, tableListener);
		table.removeListener(SWT.MouseHover, tableListener);
	}

	
	
	/*
	 * This is where the OK work goes. Lots of it. Watch your head.
	 * xiao xin
	 */
	protected boolean preHandleChanges(IProgressMonitor monitor) {
		return true;
	}

	protected boolean postHandleChanges(IProgressMonitor monitor) {
		return true;
	}

	public boolean performOk() {
		boolean result = true;
		result &= saveResourceChanges();
		result &= saveReferenceChanges();
		return result;
	}
	
	protected boolean saveResourceChanges() {
		removeAllResourceMappings();
		addNewResourceMappings();
		return true;
	}
	protected boolean addNewResourceMappings() {
		ComponentResourceProxy[] proxies = resourceMappings.toArray(new ComponentResourceProxy[resourceMappings.size()]);
		IVirtualFolder rootFolder = rootComponent.getRootFolder();
		for( int i = 0; i < proxies.length; i++ ) {
			try {
				rootFolder.getFolder(proxies[i].runtimePath).createLink(proxies[i].source, 0, null);
			} catch( CoreException ce ) {
			}
		}
		resourceMappingsChanged = false;
		return true;
	}
	
	protected boolean removeAllResourceMappings() {
		StructureEdit moduleCore = null;
		try {
			moduleCore = StructureEdit.getStructureEditForWrite(project);
			moduleCore.getComponent().getResources().clear();
		}
		finally {
			if (moduleCore != null) {
				moduleCore.saveIfNecessary(new NullProgressMonitor());
				moduleCore.dispose();
			}
		}
		return true;
	}
	
	protected boolean saveReferenceChanges() {
		// Fill our delta lists
		ArrayList<IVirtualComponent> added = new ArrayList<IVirtualComponent>();
		ArrayList<IVirtualComponent> removed = new ArrayList<IVirtualComponent>();
		ArrayList<IVirtualComponent> changed = new ArrayList<IVirtualComponent>();

		Iterator<IVirtualComponent> j = oldComponentToRuntimePath.keySet().iterator();
		Object key, val;
		while (j.hasNext()) {
			key = j.next();
			val = oldComponentToRuntimePath.get(key);
			if( !objectToRuntimePath.containsKey(key))
				removed.add((IVirtualComponent)key);
			else if (!val.equals(objectToRuntimePath.get(key)))
				changed.add((IVirtualComponent)key);
		}

		j = objectToRuntimePath.keySet().iterator();
		while (j.hasNext()) {
			key = j.next();
			if (!oldComponentToRuntimePath.containsKey(key))
				added.add((IVirtualComponent)key);
		}

		NullProgressMonitor monitor = new NullProgressMonitor();
		boolean subResult = preHandleChanges(monitor);
		if( !subResult )
			return false;
		
		handleDeltas(removed, changed, added);
		subResult &= postHandleChanges(monitor);
		
		// Now update the variables
		oldComponentToRuntimePath.clear();
		ArrayList<IVirtualComponent> keys = new ArrayList<IVirtualComponent>();
		keys.addAll(objectToRuntimePath.keySet());
		Iterator<IVirtualComponent> i = keys.iterator();
		while(i.hasNext()) {
			IVirtualComponent vc = i.next();
			String path = objectToRuntimePath.get(vc);
			oldComponentToRuntimePath.put(vc, path);
		}
		return subResult;
	}
	
	// Subclass can override if it has a good way to handle changed elements
	protected void handleDeltas(ArrayList<IVirtualComponent> removed, 
			ArrayList<IVirtualComponent> changed, ArrayList<IVirtualComponent> added) {
		ArrayList<IVirtualComponent> removed2 = new ArrayList<IVirtualComponent>();
		ArrayList<IVirtualComponent> added2 = new ArrayList<IVirtualComponent>();
		removed2.addAll(removed);
		removed2.addAll(changed);
		added2.addAll(added);
		added2.addAll(changed);

		// meld the changed into the added / removed for less efficiency ;) 
		// basically we lack "change" operations and only have add / remove
		handleRemoved(removed2);
		handleAdded(added2);
	}	
	protected void handleRemoved(ArrayList<IVirtualComponent> removed) {
		// If it's removed it should *only* be a virtual component already
		if(removed.isEmpty()) return;
		final ArrayList<IVirtualComponent> components = new ArrayList<IVirtualComponent>();
		Iterator<IVirtualComponent> i = removed.iterator();
		IVirtualComponent o;
		while(i.hasNext()) {
			o = i.next();
			components.add(o);
		}
		IWorkspaceRunnable runnable = new IWorkspaceRunnable(){
			public void run(IProgressMonitor monitor) throws CoreException{
				removeComponents(components);
			}
		};
		try {
			ResourcesPlugin.getWorkspace().run(runnable, new NullProgressMonitor());
		} catch( CoreException e ) {
			ModuleCoreUIPlugin.logError(e);
		}
		
	}
	
	protected void removeComponents(ArrayList<IVirtualComponent> removed) {
		Iterator<IVirtualComponent> i = removed.iterator();
		while(i.hasNext()) {
			removeOneComponent(i.next());
		}
		
	}

	protected void removeOneComponent(IVirtualComponent comp) {
		try {
			IDataModelOperation operation = getRemoveComponentOperation(comp);
			operation.execute(null, null);
		} catch( ExecutionException e) {
			ModuleCoreUIPlugin.logError(e);
		}
		
	}

	protected IDataModelOperation getRemoveComponentOperation(IVirtualComponent component) {
		String path, archiveName;
		path = archiveName = null;
		
		if( !consumedReferences.contains(component)) {
			path = new Path(oldComponentToRuntimePath.get(component)).removeLastSegments(1).toString();
			archiveName = new Path(oldComponentToRuntimePath.get(component)).lastSegment(); 
		} else {
			path = oldComponentToRuntimePath.get(component);
		}
		IDataModelProvider provider = getRemoveReferenceDataModelProvider(component);
		IDataModel model = DataModelFactory.createDataModel(provider);
		model.setProperty(ICreateReferenceComponentsDataModelProperties.SOURCE_COMPONENT, rootComponent);
		List<IVirtualComponent> modHandlesList = (List<IVirtualComponent>) model.getProperty(ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENT_LIST);
		modHandlesList.add(component);
		model.setProperty(ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENT_LIST, modHandlesList);
        model.setProperty(ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENTS_DEPLOY_PATH, path);
		Map<IVirtualComponent, String> uriMap = new HashMap<IVirtualComponent, String>();
		uriMap.put(component, archiveName);
		model.setProperty(ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENTS_TO_URI_MAP, uriMap);
		return model.getDefaultOperation();
	}
	
	protected IDataModelProvider getRemoveReferenceDataModelProvider(IVirtualComponent component) {
		return new RemoveReferenceComponentsDataModelProvider();
	}

	
	protected void handleChanged(ArrayList<IVirtualComponent> changed) {
		Iterator<IVirtualComponent> i = changed.iterator(); 
		IVirtualComponent component;
		IVirtualReference ref;
		IPath p;
		while(i.hasNext()) {
			component = i.next();
			ref = rootComponent.getReference(component.getName());
			p = new Path(objectToRuntimePath.get(component));
			ref.setRuntimePath(p);
		}
	}
	
	protected void handleAdded(ArrayList<IVirtualComponent> added) {
		final ArrayList<IVirtualComponent> components = new ArrayList<IVirtualComponent>();
		Iterator<IVirtualComponent> i = added.iterator();
		IVirtualComponent o;
		while(i.hasNext()) {
			o = i.next();
			components.add(o);
		}
		
		IWorkspaceRunnable runnable = new IWorkspaceRunnable(){
			public void run(IProgressMonitor monitor) throws CoreException{
				addComponents(components);
			}
		};
		try {
			ResourcesPlugin.getWorkspace().run(runnable, new NullProgressMonitor());
		} catch( CoreException e ) {
			ModuleCoreUIPlugin.logError(e);
		}
	}
	
	protected void addComponents(ArrayList<IVirtualComponent> components) throws CoreException {
		Iterator<IVirtualComponent> i = components.iterator();
		while(i.hasNext()) {
			addOneComponent(i.next());
		}
	}
	
	protected IDataModelProvider getAddReferenceDataModelProvider(IVirtualComponent component) {
		return new CreateReferenceComponentsDataModelProvider();
	}
	
	protected void addOneComponent(IVirtualComponent component) throws CoreException {
		String path, archiveName;
		path = new Path(objectToRuntimePath.get(component)).removeLastSegments(1).toString();
		archiveName = new Path(objectToRuntimePath.get(component)).lastSegment();

		IDataModelProvider provider = getAddReferenceDataModelProvider(component);
		IDataModel dm = DataModelFactory.createDataModel(provider);
		
		dm.setProperty(ICreateReferenceComponentsDataModelProperties.SOURCE_COMPONENT, rootComponent);
		dm.setProperty(ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENT_LIST, Arrays.asList(component));
		
		//[Bug 238264] the uri map needs to be manually set correctly
		Map<IVirtualComponent, String> uriMap = new HashMap<IVirtualComponent, String>();
		uriMap.put(component, archiveName);
		dm.setProperty(ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENTS_TO_URI_MAP, uriMap);
        dm.setProperty(ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENTS_DEPLOY_PATH, path);

		IStatus stat = dm.validateProperty(ICreateReferenceComponentsDataModelProperties.TARGET_COMPONENT_LIST);
		if (stat != OK_STATUS)
			throw new CoreException(stat);
		try {
			dm.getDefaultOperation().execute(new NullProgressMonitor(), null);
		} catch (ExecutionException e) {
			ModuleCoreUIPlugin.logError(e);
		}	
	}

	public void labelProviderChanged(LabelProviderChangedEvent event) {
		if(!availableComponentsViewer.getTable().isDisposed())
			availableComponentsViewer.refresh(true);
	}

	protected IModuleHandler getModuleHandler() {
		if(moduleHandler == null)
			moduleHandler = new DefaultModuleHandler();
		return moduleHandler;
	}
	
//	/**
//	 * [Bug 238264]
//	 * determines a unique URI mapping name for a given component
//	 * this is in case two components have the same name.
//	 * 
//	 * @return returns a valid (none duplicate) uri mapping name for the given component\
//	 */
//	private String getURIMappingName(IVirtualComponent archive) {
//		
//		//get the default uri map name for the given archive
//		IPath componentPath = Path.fromOSString(archive.getName());
//		String uriMapName = componentPath.lastSegment().replace(' ', '_');
//		
//		
//		//check to be sure this uri mapping is not already in use by another reference
//		boolean dupeArchiveName;
//		String refedCompName;
//		int lastDotIndex;
//		String increment;
//		IVirtualReference [] existingRefs = rootComponent.getReferences();
//		for(int i=0;i<existingRefs.length;i++){
//			refedCompName = existingRefs[i].getReferencedComponent().getName();
//			
//			//if uri mapping names of the refed component and the given archive are the same
//			//  find a new uri map name for the given archive
//			if(existingRefs[i].getArchiveName().equals(uriMapName)){
//				dupeArchiveName = true;
//				//find a new uriMapName for the given component
//				for(int j=1; dupeArchiveName; j++){
//					lastDotIndex = uriMapName.lastIndexOf('.');
//					increment = "_"+j; //$NON-NLS-1$
//					
//					//create the new potential name
//					if(lastDotIndex != -1){
//						uriMapName = uriMapName.substring(0, lastDotIndex) + increment + uriMapName.substring(lastDotIndex);
//					} else {
//						uriMapName = uriMapName.substring(0)+increment;
//					}
//					
//					//determine if the new potential name is valid
//					for(int k=0; k<existingRefs.length; k++) {
//						dupeArchiveName = existingRefs[k].getArchiveName().equals(uriMapName);
//						if(dupeArchiveName) {
//							break;
//						}
//					}
//				}
//			}
//		}
//		
//		return uriMapName;
//	}
}
