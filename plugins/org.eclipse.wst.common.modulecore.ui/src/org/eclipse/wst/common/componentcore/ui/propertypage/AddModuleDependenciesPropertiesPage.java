/******************************************************************************
 * Copyright (c) 2009 Red Hat
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rob Stryker - initial implementation and ongoing maintenance
 *    
 * API in these packages is provisional in this release
 ******************************************************************************/
package org.eclipse.wst.common.componentcore.ui.propertypage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.datamodel.properties.IAddReferenceDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.ComponentResource;
import org.eclipse.wst.common.componentcore.internal.DefaultModuleHandler;
import org.eclipse.wst.common.componentcore.internal.DependencyType;
import org.eclipse.wst.common.componentcore.internal.IModuleHandler;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.internal.impl.TaskModel;
import org.eclipse.wst.common.componentcore.internal.operation.AddReferenceDataModelProvider;
import org.eclipse.wst.common.componentcore.internal.operation.RemoveReferenceDataModelProvider;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualReference;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.componentcore.ui.Messages;
import org.eclipse.wst.common.componentcore.ui.ModuleCoreUIPlugin;
import org.eclipse.wst.common.componentcore.ui.internal.propertypage.ComponentDependencyContentProvider;
import org.eclipse.wst.common.componentcore.ui.internal.propertypage.DependencyPageExtensionManager;
import org.eclipse.wst.common.componentcore.ui.internal.propertypage.DependencyPageExtensionManager.ReferenceExtension;
import org.eclipse.wst.common.componentcore.ui.internal.propertypage.NewReferenceWizard;
import org.eclipse.wst.common.componentcore.ui.internal.propertypage.verifier.DeploymentAssemblyVerifierHelper;
import org.eclipse.wst.common.componentcore.ui.internal.taskwizard.TaskWizard;
import org.eclipse.wst.common.componentcore.ui.internal.taskwizard.WizardFragment;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.internal.facets.FacetUtil;
 
public class AddModuleDependenciesPropertiesPage implements Listener,
		IModuleDependenciesControl, ILabelProviderListener {

	protected static final String DEPLOY_PATH_PROPERTY = new Integer(0).toString();
	protected static final String SOURCE_PROPERTY = new Integer(1).toString();
	
	
	protected final String PATH_SEPARATOR = String.valueOf(IPath.SEPARATOR);
	private boolean hasInitialized = false;
	protected final IProject project;
	protected final IRuntime runtime;
	protected final ModuleAssemblyRootPage propPage;
	protected IVirtualComponent rootComponent = null;
	protected Text componentNameText;
	protected Label availableModules;
	protected TableViewer availableComponentsViewer;
	protected Button addMappingButton, addReferenceButton, editReferenceButton, removeButton;
	protected Composite buttonColumn;
	protected static final IStatus OK_STATUS = IDataModelProvider.OK_STATUS;
	//protected Listener tableListener;
	protected Listener labelListener;

	protected ArrayList<IVirtualReference> originalReferences = new ArrayList<IVirtualReference>();
	protected ArrayList<IVirtualReference> currentReferences = new ArrayList<IVirtualReference>();

	// A single list of wb-resource mappings. If there's any change, 
	// all old will be removed and new ones added
	protected ArrayList<ComponentResourceProxy> resourceMappings = new ArrayList<ComponentResourceProxy>();
	
	// keeps track if a change has occurred in wb-resource mappings
	protected boolean resourceMappingsChanged = false;
	
	protected IModuleHandler moduleHandler;
	
	public static String getSafeRuntimePath(IVirtualReference ref) {
		String archiveName = ref.getDependencyType() == DependencyType.CONSUMES ? null : ref.getArchiveName();
		String val = (archiveName != null) ? ref.getRuntimePath().append(archiveName).toString() : ref.getRuntimePath().toString();
		if( val == null ) val = "/";
		return val;
	}

	public static void setRuntimePathSafe(IVirtualReference ref, String newPath) {
		if( ref.getDependencyType() == IVirtualReference.DEPENDENCY_TYPE_CONSUMES)
			ref.setRuntimePath(new Path(newPath == null ? "/" : newPath));
		else {
			IPath path2 = new Path(newPath == null ? "/" : newPath);
			ref.setRuntimePath(path2.segmentCount() > 1 ? path2.removeLastSegments(1) : new Path("/"));
			ref.setArchiveName(path2.segmentCount() > 0 ? path2.lastSegment() : "");
		}
	}
	
	/**
	 * Constructor for AddModulestoEARPropertiesControl.
	 */
	public AddModuleDependenciesPropertiesPage(final IProject project,
			final ModuleAssemblyRootPage page) {
		this.project = project;
		this.propPage = page;
		this.runtime = setRuntime();
		rootComponent = ComponentCore.createComponent(project);
	}

	private IRuntime setRuntime() {
		IRuntime aRuntime = null;
		try {
			aRuntime = getServerRuntime(project);
		}
		catch (CoreException e) {
			ModuleCoreUIPlugin.log(e);
		}
		return aRuntime;
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
		createDescriptionComposite(composite);
		createListGroup(composite);
		refresh();
		Dialog.applyDialogFont(parent);
		return composite;
	}
	
	protected void createDescriptionComposite(Composite parent){
		ModuleAssemblyRootPage.createDescriptionComposite(parent, getModuleAssemblyRootPageDescription());
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
		fillTableComposite(composite);
	}

	public void fillTableComposite(Composite parent) {
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
		addReferenceButton = createPushButton(Messages.AddEllipsis);
		editReferenceButton = createPushButton(Messages.EditEllipsis);
		removeButton = createPushButton(Messages.RemoveSelected);
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
			provider.setRuntimePaths(currentReferences);
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
//		createTableListener(table);
//		table.addListener(SWT.Dispose, tableListener);
//		table.addListener(SWT.KeyDown, tableListener);
//		table.addListener(SWT.MouseMove, tableListener);
//		table.addListener(SWT.MouseHover, tableListener);
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

//	protected void createTableListener(final Table table) {
//		tableListener = new Listener() {
//			Shell tip = null;
//			Label label = null;
//
//			public void handleEvent(Event event) {
//				switch (event.type) {
//				case SWT.Dispose:
//				case SWT.KeyDown:
//				case SWT.MouseMove: {
//					if (tip == null)
//						break;
//					tip.dispose();
//					tip = null;
//					label = null;
//					break;
//				}
//				case SWT.MouseHover: {
//					TableItem item = table.getItem(new Point(event.x, event.y));
//					if (item != null && item.getData() != null && !canEdit(item.getData())) {
//						if (tip != null && !tip.isDisposed())
//							tip.dispose();
//						tip = new Shell(PlatformUI.getWorkbench()
//								.getActiveWorkbenchWindow().getShell(),
//								SWT.ON_TOP | SWT.NO_FOCUS | SWT.TOOL);
//						tip.setBackground(Display.getDefault().getSystemColor(
//								SWT.COLOR_INFO_BACKGROUND));
//						FillLayout layout = new FillLayout();
//						layout.marginWidth = 2;
//						tip.setLayout(layout);
//						label = new Label(tip, SWT.WRAP);
//						label.setForeground(Display.getDefault()
//								.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
//						label.setBackground(Display.getDefault()
//								.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
//						label.setData("_TABLEITEM", item); //$NON-NLS-1$
//						label.setText( Messages.InternalLibJarWarning);
//						label.addListener(SWT.MouseExit, labelListener);
//						label.addListener(SWT.MouseDown, labelListener);
//						Point size = tip.computeSize(SWT.DEFAULT, SWT.DEFAULT);
//						Rectangle rect = item.getBounds(0);
//						Point pt = table.toDisplay(rect.x, rect.y);
//						tip.setBounds(pt.x, pt.y - size.y, size.x, size.y);
//						tip.setVisible(true);
//					}
//				}
//				}
//			}
//		};
//	}

//	protected boolean canEdit(Object data) {
//		if( data == null ) return false;
//		if( !(data instanceof VirtualArchiveComponent)) return true;
//		
//		VirtualArchiveComponent d2 = (VirtualArchiveComponent)data;
//		boolean sameProject = d2.getWorkspaceRelativePath() != null
//			&& d2.getWorkspaceRelativePath().segment(0)
//				.equals(rootComponent.getProject().getName());
//		return !(sameProject && isPhysicallyAdded(d2));
//	}
	
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
		removeButton.setEnabled(getSelectedObject() != null);
	}
	
	protected boolean hasEditWizardPage(Object o) {
		if( o == null || !(o instanceof IVirtualReference)) 
			return false;
		WizardFragment wf = NewReferenceWizard.getFirstEditingFragment((IVirtualReference)o);
		return wf != null;
	}

	protected Object getSelectedObject() {
		IStructuredSelection sel = (IStructuredSelection)availableComponentsViewer.getSelection();
		return sel.getFirstElement();
	}
	
	private class RuntimePathCellModifier implements ICellModifier {

		public boolean canModify(Object element, String property) {
			if( property.equals(DEPLOY_PATH_PROPERTY)) {
				if( element instanceof IVirtualReference && !((IVirtualReference)element).isDerived())
					return true;
				if( element instanceof ComponentResourceProxy )
					return true; 
			}
			return false;
		}

		public Object getValue(Object element, String property) {
			if( element instanceof IVirtualReference ) {
				return getSafeRuntimePath((IVirtualReference)element);
			} else if( element instanceof ComponentResourceProxy) {
				return ((ComponentResourceProxy)element).runtimePath.toString();
			}
			return new Path("/"); //$NON-NLS-1$
		}

		public void modify(Object element, String property, Object value) {
			if (property.equals(DEPLOY_PATH_PROPERTY)) {
				TableItem item = (TableItem) element;
				if( item.getData() instanceof IVirtualReference) {
					setRuntimePathSafe((IVirtualReference)item.getData(), (String) value);
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
		if( event.widget == addReferenceButton) 
			handleAddReferenceButton();
		else if( event.widget == editReferenceButton) 
			handleEditReferenceButton();
		else if( event.widget == removeButton ) 
			handleRemoveSelectedButton();
	}
	
	protected void handleAddReferenceButton() {
		showReferenceWizard(false);
	}
	
	protected void handleEditReferenceButton() {
		showReferenceWizard(true);
	}
	
	protected ReferenceExtension[] filterReferenceTypes(ReferenceExtension[] defaults) {
		return defaults;
	}
	
	protected void showReferenceWizard(boolean editing) {
		ReferenceExtension[] extensions = 
			DependencyPageExtensionManager.getManager().getExposedReferenceExtensions();
		extensions = filterReferenceTypes(extensions);
		NewReferenceWizard wizard = new NewReferenceWizard(extensions);
		// fill the task model
		wizard.getTaskModel().putObject(IReferenceWizardConstants.PROJECT, project);
		wizard.getTaskModel().putObject(IReferenceWizardConstants.ROOT_COMPONENT, rootComponent);
		wizard.getTaskModel().putObject(IReferenceWizardConstants.MODULEHANDLER, getModuleHandler());
		wizard.getTaskModel().putObject(IReferenceWizardConstants.CURRENT_REFS, currentReferences);
		setCustomReferenceWizardProperties(wizard.getTaskModel());

		IVirtualReference selected = null;
		if( editing ) {
			Object o = ((IStructuredSelection)availableComponentsViewer.getSelection()).getFirstElement();
			if( o instanceof IVirtualReference ) {
				selected = (IVirtualReference)o;
				wizard.getTaskModel().putObject(IReferenceWizardConstants.ORIGINAL_REFERENCE, selected);
			} 
		}
		
		WizardDialog wd = new WizardDialog(addReferenceButton.getShell(), wizard);
		if( wd.open() != Window.CANCEL) {
			if( editing && selected != null) {
				// remove old
				currentReferences.remove(selected);
			}
			
			if( wizard.getTaskModel().getObject(IReferenceWizardConstants.FOLDER_MAPPING) != null )
				handleAddFolderMapping(wizard);
			else
				handleAddNewReference(wizard);
			refresh();
		}
	}
	
	/*
	 * Clients can override this to set custom properties
	 */
	protected void setCustomReferenceWizardProperties(TaskModel model) {
		// do nothing
	}
	
	protected void handleAddFolderMapping(TaskWizard wizard) {
		Object o = wizard.getTaskModel().getObject(IReferenceWizardConstants.FOLDER_MAPPING);
		if( o != null && o instanceof ComponentResourceProxy ) {
			ComponentResourceProxy proxy = (ComponentResourceProxy)o;
			resourceMappings.add(proxy);
			resourceMappingsChanged = true;
		}
	}
	
	protected void handleAddNewReference(TaskWizard wizard) {
		Object c1 = wizard.getTaskModel().getObject(IReferenceWizardConstants.FINAL_REFERENCE);
		if(c1 != null) {
			IVirtualReference[] referenceArray = c1 instanceof IVirtualReference ? 
					new IVirtualReference[] { (IVirtualReference)c1 } : 
						(IVirtualReference[])c1;
			currentReferences.addAll(Arrays.asList(referenceArray));
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
			IStructuredSelection sel2 = (IStructuredSelection)sel;
			Object[] selectedStuff = sel2.toArray();
			for( int i = 0; i < selectedStuff.length; i++) {
				Object o = selectedStuff[i];
				if( o instanceof IVirtualReference)
					currentReferences.remove(o);
				else if( o instanceof ComponentResourceProxy) {
					resourceMappings.remove(o);
					resourceMappingsChanged = true;
				}
			}
			refresh();
		}
	}

	public TableViewer createAvailableComponentsViewer(Composite parent) {
		int flags = SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI;

		Table table = new Table(parent, flags);
		TableViewer tempViewer = new TableViewer(table);

		// set up table layout
		TableLayout tableLayout = new org.eclipse.jface.viewers.TableLayout();
		tableLayout.addColumnData(new ColumnWeightData(400, true));
		tableLayout.addColumnData(new ColumnWeightData(500, true));
		table.setLayout(tableLayout);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		tempViewer.setSorter(null);

		TableColumn bndColumn = new TableColumn(table, SWT.NONE, 0);
		bndColumn.setText(Messages.DeployPathColumn);
		bndColumn.setResizable(true);

		TableColumn projectColumn = new TableColumn(table, SWT.NONE, 1);
		projectColumn.setText(Messages.SourceColumn);
		projectColumn.setResizable(true);

		tableLayout.layout(table, true);
		return tempViewer;

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
		verify();

	}

	protected void verify() {
		
		IStatus status = DeploymentAssemblyVerifierHelper.verify(rootComponent, runtime, currentReferences, resourceMappings,resourceMappingsChanged);
		// Clear the messages
		propPage.setErrorMessage(null);
		propPage.setMessage(null);
		if (status.isMultiStatus()) {
			MultiStatus multi = (MultiStatus)status;
			if (!multi.isOK()) {
				propPage.setMessage(getMessage(multi), multi.getSeverity());
				if (multi.getSeverity() == IStatus.ERROR) {
					propPage.setErrorMessage(getMessage(multi));
					propPage.setValid(false);
				}
				else 
					propPage.setValid(true);
			} else propPage.setValid(true);
		} else if (status.isOK()) propPage.setValid(true);
		propPage.getContainer().updateMessage();
	}

	private String getMessage(MultiStatus multi) {
		//Append Messages
		StringBuffer message = new StringBuffer();
		
		for (int i = 0; i < multi.getChildren().length; i++) {
			IStatus status = multi.getChildren()[i];
			if (status.getMessage() != null) {
				message.append(status.getMessage());
				message.append(" "); //$NON-NLS-1$
			}
		}
		return message.toString();
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
		Map<String, Object> options = new HashMap<String, Object>();
		options.put(IVirtualComponent.REQUESTED_REFERENCE_TYPE, IVirtualComponent.DISPLAYABLE_REFERENCES);
		IVirtualReference[] refs = rootComponent.getReferences(options);
		IVirtualComponent comp;
		originalReferences.addAll(Arrays.asList(refs));
		currentReferences.addAll(Arrays.asList(cloneReferences(refs)));

		ComponentResource[] allMappings = findAllMappings();
		for( int i = 0; i < allMappings.length; i++ ) {
			resourceMappings.add(new ComponentResourceProxy(
					allMappings[i].getSourcePath(), allMappings[i].getRuntimePath()
			));
		}
		if(editReferenceButton != null)
			editReferenceButton.setEnabled(false);
		if(removeButton != null)
			removeButton.setEnabled(false);
		hasInitialized = true;
	}

	private IRuntime getServerRuntime(IProject project2) throws CoreException {
		if (project == null)
			return null;
		IFacetedProject facetedProject = ProjectFacetsManager.create(project);
		if (facetedProject == null)
			return null;
		org.eclipse.wst.common.project.facet.core.runtime.IRuntime runtime = facetedProject.getRuntime();
		if (runtime == null)
			return null;
		return FacetUtil.getRuntime(runtime);
	}

	protected IVirtualReference[] cloneReferences(IVirtualReference[] refs) {
		IVirtualReference[] result = new IVirtualReference[refs.length];
		VirtualReference temp;
		for( int i = 0; i < result.length; i++ ) {
			temp = new VirtualReference(refs[i].getEnclosingComponent(), refs[i].getReferencedComponent());
			temp.setDependencyType(refs[i].getDependencyType());
			temp.setDerived(refs[i].isDerived());
			temp.setArchiveName(refs[i].getArchiveName());
			temp.setRuntimePath(refs[i].getRuntimePath());
			result[i] = temp;
		}
		return result;
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
	
	public static class ComponentResourceProxy {
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
//		if (table == null || tableListener == null)
//			return; 
//		table.removeListener(SWT.Dispose, tableListener);
//		table.removeListener(SWT.KeyDown, tableListener);
//		table.removeListener(SWT.MouseMove, tableListener);
//		table.removeListener(SWT.MouseHover, tableListener);
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
		if( resourceMappingsChanged ) {
			removeAllResourceMappings();
			addNewResourceMappings();
		}
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
		ArrayList<IVirtualReference> added = new ArrayList<IVirtualReference>();
		ArrayList<IVirtualReference> removed = new ArrayList<IVirtualReference>();

		HashMap<IVirtualComponent, IVirtualReference> map = new HashMap<IVirtualComponent, IVirtualReference>();
		Iterator<IVirtualReference> k = currentReferences.iterator();
		IVirtualReference v1;
		while(k.hasNext()) {
			v1 = k.next();
			map.put(v1.getReferencedComponent(), v1);
		}
		
		Iterator<IVirtualReference> j = originalReferences.iterator();
		IVirtualReference origRef, newRef;
		while (j.hasNext()) {
			origRef = j.next();
			newRef = map.get(origRef.getReferencedComponent());
			if( newRef == null )
				removed.add(origRef);
			else if( !getSafeRuntimePath(origRef).equals(getSafeRuntimePath(newRef))) {
				removed.add(origRef);
				added.add(newRef);
			}
			map.remove(origRef.getReferencedComponent());
		}

		added.addAll(map.values());

		NullProgressMonitor monitor = new NullProgressMonitor();
		boolean subResult = preHandleChanges(monitor);
		if( !subResult )
			return false;
		
		handleRemoved(removed);
		handleAdded(added);

		subResult &= postHandleChanges(monitor);
		
		originalReferences.clear();
		originalReferences.addAll(currentReferences);
		currentReferences.clear();
		IVirtualReference[] currentTmp =
			originalReferences.toArray(new IVirtualReference[originalReferences.size()]); 
		currentReferences.addAll(Arrays.asList(cloneReferences(currentTmp)));
		return subResult;
	}

	protected void handleRemoved(ArrayList<IVirtualReference> removed) {
		// If it's removed it should *only* be a virtual component already
		if(removed.isEmpty()) return;
		final ArrayList<IVirtualReference> refs = new ArrayList<IVirtualReference>();
		Iterator<IVirtualReference> i = removed.iterator();
		IVirtualReference o;
		while(i.hasNext()) {
			o = i.next();
			refs.add(o);
		}
		IWorkspaceRunnable runnable = new IWorkspaceRunnable(){
			public void run(IProgressMonitor monitor) throws CoreException{
				removeReferences(refs);
			}
		};
		try {
			ResourcesPlugin.getWorkspace().run(runnable, new NullProgressMonitor());
		} catch( CoreException e ) {
			ModuleCoreUIPlugin.logError(e);
		}
		
	}
	
	protected void removeReferences(ArrayList<IVirtualReference> removed) {
		Iterator<IVirtualReference> i = removed.iterator();
		while(i.hasNext()) {
			removeOneReference(i.next());
		}
	}

	protected void removeOneReference(IVirtualReference comp) {
		try {
			IDataModelOperation operation = getRemoveComponentOperation(comp);
			operation.execute(null, null);
		} catch( ExecutionException e) {
			ModuleCoreUIPlugin.logError(e);
		}
		
	}

	protected IDataModelOperation getRemoveComponentOperation(IVirtualReference reference) {
		IDataModelProvider provider = getRemoveReferenceDataModelProvider(reference);
		IDataModel model = DataModelFactory.createDataModel(provider);
		model.setProperty(IAddReferenceDataModelProperties.SOURCE_COMPONENT, rootComponent);
		List<IVirtualReference> toRemove = new ArrayList<IVirtualReference>();
		toRemove.add(reference); 
		model.setProperty(IAddReferenceDataModelProperties.TARGET_REFERENCE_LIST, toRemove);
		return model.getDefaultOperation();
	}
	
	protected IDataModelProvider getRemoveReferenceDataModelProvider(IVirtualReference reference) {
		return new RemoveReferenceDataModelProvider();
	}
	
	protected void handleAdded(ArrayList<IVirtualReference> added) {
		final ArrayList<IVirtualReference> refs = new ArrayList<IVirtualReference>();
		Iterator<IVirtualReference> i = added.iterator();
		IVirtualReference o;
		while(i.hasNext()) {
			o = i.next();
			refs.add(o);
		}
		
		IWorkspaceRunnable runnable = new IWorkspaceRunnable(){
			public void run(IProgressMonitor monitor) throws CoreException{
				addReferences(refs);
			}
		};
		try {
			ResourcesPlugin.getWorkspace().run(runnable, new NullProgressMonitor());
		} catch( CoreException e ) {
			ModuleCoreUIPlugin.logError(e);
		}
	}
	
	protected void addReferences(ArrayList<IVirtualReference> refs) throws CoreException {
		Iterator<IVirtualReference> i = refs.iterator();
		while(i.hasNext()) {
			addOneReference(i.next());
		}
	}
	
	protected IDataModelProvider getAddReferenceDataModelProvider(IVirtualReference component) {
		return new AddReferenceDataModelProvider();
	}
	
	protected void addOneReference(IVirtualReference ref) throws CoreException {
		String path, archiveName;

		IDataModelProvider provider = getAddReferenceDataModelProvider(ref);
		IDataModel dm = DataModelFactory.createDataModel(provider);
		dm.setProperty(IAddReferenceDataModelProperties.SOURCE_COMPONENT, rootComponent);
		dm.setProperty(IAddReferenceDataModelProperties.TARGET_REFERENCE_LIST, Arrays.asList(ref));
		
		IStatus stat = dm.validateProperty(IAddReferenceDataModelProperties.TARGET_REFERENCE_LIST);
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

}
