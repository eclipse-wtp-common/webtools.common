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
package org.eclipse.jst.common.ui.internal.assembly.wizard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jst.common.internal.modulecore.util.ArchiveManifest;
import org.eclipse.jst.common.internal.modulecore.util.JavaModuleComponentUtility;
import org.eclipse.jst.common.internal.modulecore.util.ManifestUtilities;
import org.eclipse.jst.common.internal.modulecore.util.UpdateManifestDataModelProperties;
import org.eclipse.jst.common.internal.modulecore.util.UpdateManifestDataModelProvider;
import org.eclipse.jst.common.ui.internal.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.resources.AbstractResourceListVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.componentcore.ui.ModuleCoreUIPlugin;
import org.eclipse.wst.common.componentcore.ui.internal.propertypage.DependencyPageExtensionManager;
import org.eclipse.wst.common.componentcore.ui.internal.propertypage.IVirtualComponentLabelProvider;
import org.eclipse.wst.common.componentcore.ui.propertypage.IModuleDependenciesControl;
import org.eclipse.wst.common.componentcore.ui.propertypage.ModuleAssemblyRootPage;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

public class ManifestModuleDependencyControl implements
		IModuleDependenciesControl {
	
	protected IProject project;
	protected IVirtualComponent rootComponent;
	protected IProject parentProject;
	protected ModuleAssemblyRootPage propPage;
	protected Button addButton, removeButton, moveUpButton, moveDownButton;
	protected Combo parentSelection;
	protected TableViewer manifestEntryViewer;
	private ArrayList<IVirtualReference> list = new ArrayList<IVirtualReference>();
	
	public ManifestModuleDependencyControl(final IProject project,
			final ModuleAssemblyRootPage page) {
		this.project = project;
		this.propPage = page;
		rootComponent = ComponentCore.createComponent(project);
	}

	/*
	 * Convenience method to quickly create form datas, 
	 * referencing either pixel counts (via autoboxing) or controls.
	 * If the object is null, no attachment will be created for that side
	 */
	public static FormData createFormData(Object top, int topDif, Object bottom, int bottomDif,
						Object left, int leftDif, Object right, int rightDif) {
		FormData fd = new FormData();
		if( top instanceof Control ) fd.top = new FormAttachment((Control)top, topDif);
		else if( top != null ) fd.top = new FormAttachment(((Integer)top).intValue(), topDif);
		
		if( bottom instanceof Control ) fd.bottom = new FormAttachment((Control)bottom, bottomDif);
		else if( bottom != null ) fd.bottom = new FormAttachment(((Integer)bottom).intValue(), bottomDif);
		
		if( left instanceof Control ) fd.left = new FormAttachment((Control)left, leftDif);
		else if( left != null ) fd.left = new FormAttachment(((Integer)left).intValue(), leftDif);
		
		if( right instanceof Control ) fd.right = new FormAttachment((Control)right, rightDif);
		else if( right != null ) fd.right = new FormAttachment(((Integer)right).intValue(), rightDif);
		
		return fd;
	}
	
	public Composite createContents(Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
		root.setLayout(new FormLayout());
		Label l = new Label(root, SWT.NONE);
		l.setText(Messages.ParentProjects);
		l.setLayoutData(createFormData(0,10,null,0, 0, 5, null,0));
		parentSelection = new Combo(root, SWT.READ_ONLY);
		parentSelection.setLayoutData(createFormData(0,5,null,0,l,5,100,-5));
		parentSelection.setItems(getPossibleParentProjects());
		parentSelection.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				refreshViewerFromNewParentProject();
			}
		});
		manifestEntryViewer = createManifestReferenceTableViewer(root, SWT.SINGLE);
		
		addButton = new Button(root, SWT.PUSH);
		removeButton = new Button(root, SWT.PUSH);
		moveUpButton = new Button(root, SWT.PUSH);
		moveDownButton = new Button(root, SWT.PUSH);
		
		addButton.setText(Messages.Add);
		removeButton.setText(Messages.Remove);
		moveUpButton.setText(Messages.MoveUp);
		moveDownButton.setText(Messages.MoveDown);
		removeButton.setEnabled(false);
		moveUpButton.setEnabled(false);
		moveDownButton.setEnabled(false);
		
		addButton.setLayoutData(createFormData(parentSelection,5,null,0,manifestEntryViewer.getTable(),5,100,-5));
		removeButton.setLayoutData(createFormData(addButton,5,null,0,manifestEntryViewer.getTable(),5,100,-5));
		moveUpButton.setLayoutData(createFormData(removeButton,5,null,0,manifestEntryViewer.getTable(),5,100,-5));
		moveDownButton.setLayoutData(createFormData(moveUpButton,5,null,0,null,0,100,-5));
		
		addButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				addPressed();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		removeButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				removePressed();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		moveUpButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				moveUp();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		moveDownButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				moveDown();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		manifestEntryViewer.getTable().setLayoutData(createFormData(
				parentSelection, 5, 100, -5, 0, 5, moveDownButton, -5));
		manifestEntryViewer.setLabelProvider(new ManifestLabelProvider());
		manifestEntryViewer.setContentProvider(new ManifestContentProvider());
		manifestEntryViewer.setInput(ResourcesPlugin.getWorkspace());
		parentSelection.select(0);
		
		manifestEntryViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				viewerSelectionChanged();
			}
		});
		return root;
	}
	
	protected void viewerSelectionChanged() {
		updateButtons();
	}
	
	protected void updateButtons() {
		int index = manifestEntryViewer.getTable().getSelectionIndex();
		int size = manifestEntryViewer.getTable().getItems().length;
		removeButton.setEnabled(index != -1);
		moveUpButton.setEnabled(index != -1 && index != 0);
		moveDownButton.setEnabled(index != size-1);
	}
	
	protected void moveUp() {
		int index = manifestEntryViewer.getTable().getSelectionIndex();
		IVirtualReference ref = list.remove(index);
		list.add(index-1,ref);
		refreshViewer();
		updateButtons();
	}
	protected void moveDown() {
		int index = manifestEntryViewer.getTable().getSelectionIndex();
		IVirtualReference ref = list.remove(index);
		list.add(index+1,ref);
		refreshViewer();
		updateButtons();
	}
	
	protected void addPressed() {
		AddManifestEntryTaskWizard wizard = new AddManifestEntryTaskWizard();
		wizard.getTaskModel().putObject(AddManifestEntryTaskWizard.CHILD_PROJECT, project);
		wizard.getTaskModel().putObject(AddManifestEntryTaskWizard.PARENT_PROJECT, parentProject);
		wizard.getTaskModel().putObject(AddManifestEntryTaskWizard.CURRENT_REFERENCES, list.toArray(new IVirtualReference[list.size()]));
		WizardDialog wd = new WizardDialog(addButton.getShell(), wizard);
		if( wd.open() != Window.CANCEL) {
			IVirtualReference[] ret = (IVirtualReference[])wizard.getTaskModel().getObject(AddManifestEntryTaskWizard.RETURNED_REFERENCES);
			if (ret != null)
				list.addAll(Arrays.asList(ret));
			refreshViewer();
			updateButtons();
		}
	}

	protected void removePressed() {
		IStructuredSelection sel = (IStructuredSelection)manifestEntryViewer.getSelection();
		Iterator i = sel.iterator();
		while(i.hasNext()) {
			list.remove(i.next());
		}
		refreshViewer();
		updateButtons();
	}
	
	public static class ManifestLabelProvider extends BaseLabelProvider implements ITableLabelProvider {
		private IVirtualComponentLabelProvider[] delegates;
		protected void getDelegates() {
			if( delegates == null )
				delegates = DependencyPageExtensionManager.loadDelegates();
		}
		public Image getColumnImage(Object element, int columnIndex) {
			getDelegates();
			if( columnIndex == 1 ) {
				if( element instanceof IVirtualReference ) {
					for( int i = 0; i < delegates.length; i++ )
						if( delegates[i].canHandle(((IVirtualReference)element).getReferencedComponent()))
							return delegates[i].getSourceImage(((IVirtualReference)element).getReferencedComponent());
				}
			}
			return ModuleCoreUIPlugin.getInstance().getImage("jar_obj");
		}
		
		public String getColumnText(Object element, int columnIndex) {
			if( element instanceof IVirtualReference ) {
				IVirtualReference ref = (IVirtualReference)element;
				if( columnIndex == 0 && ref.getArchiveName() != null)
					return ref.getRuntimePath().append(ref.getArchiveName()).toString();
				if( columnIndex == 1 )
					return handleSourceText(ref.getReferencedComponent());
			}
			return null;
		}
		
		private String handleSourceText(IVirtualComponent component) {
			if( delegates == null )
				delegates = DependencyPageExtensionManager.loadDelegates();
			for( int i = 0; i < delegates.length; i++ )
				if( delegates[i].canHandle(component))
					return delegates[i].getSourceText(component);

			// default impl
			if( component.isBinary() ) {
				IPath p = (IPath)component.getAdapter(IPath.class);
				return p == null ? null : p.toString();
			}
			return component.getProject().getName();
		}
	}
	
	
	private class ManifestContentProvider implements IStructuredContentProvider {
		public void dispose() {
		}
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
		public Object[] getElements(Object inputElement) {
			return refsForCurrentProject == null ? new Object[]{} : refsForCurrentProject;
		}
	}
	
	public static TableViewer createManifestReferenceTableViewer(Composite parent, int style) {
		int flags = SWT.BORDER | style;

		Table table = new Table(parent, flags);
		TableViewer viewer = new TableViewer(table);

		// set up table layout
		TableLayout tableLayout = new org.eclipse.jface.viewers.TableLayout();
		tableLayout.addColumnData(new ColumnWeightData(200, true));
		tableLayout.addColumnData(new ColumnWeightData(200, true));
		table.setLayout(tableLayout);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		// table columns
		TableColumn fileNameColumn = new TableColumn(table, SWT.NONE, 0);
		fileNameColumn.setText(Messages.ManifestEntryColumn);
		fileNameColumn.setResizable(true);

		TableColumn projectColumn = new TableColumn(table, SWT.NONE, 1);
		projectColumn.setText(Messages.ManifestEntrySourceColumn);
		projectColumn.setResizable(true);
		tableLayout.layout(table, true);
		return viewer;
	}

	private IVirtualReference[] refsForCurrentProject;
	protected void refreshViewerFromNewParentProject() {
		String parentString = parentSelection.getText();
		refsForCurrentProject = new IVirtualReference[]{};
		if( parentString != null && !"".equals(parentString)) { //$NON-NLS-1$
			parentProject = ResourcesPlugin.getWorkspace().getRoot().getProject(parentString);
			IVirtualReference[] current  = JavaModuleComponentUtility.findCurrentManifestEntries(parentProject, project);
			refsForCurrentProject = addMissingDummyEntries(current);
			for( int i = 0; i < refsForCurrentProject.length; i++ ) {
				list.add(refsForCurrentProject[i]);
			}
		}
		manifestEntryViewer.refresh();
	}
	
	protected void refreshViewer() {
		refsForCurrentProject = list.toArray(new IVirtualReference[list.size()]);
		manifestEntryViewer.refresh();
	}
	
	protected IVirtualReference[] addMissingDummyEntries(IVirtualReference[] current) {
		IFile manifestFile = getManifestIFile(rootComponent);
		if( manifestFile != null ) {
			ArrayList<IVirtualReference> refs = new ArrayList<IVirtualReference>();
			refs.addAll(Arrays.asList(current));
			ArchiveManifest manifest = ManifestUtilities.getManifest(manifestFile);
			String[] entries = manifest.getClassPathTokenized();
			String[] unusedEntries = findUnusedEntries(current, entries);
			for( int i = 0; i < unusedEntries.length; i++ ) {
				refs.add(createDummyReference(unusedEntries[i]));
			}
			return refs.toArray(new IVirtualReference[refs.size()]);
		}
		return current;
	}
	
	
	public static class DummyVirtualComponent extends AbstractResourceListVirtualComponent {
		private static final String DUMMY_FIRST_SEGMENT = "dummyVirtualComponent"; //$NON-NLS-1$
		public DummyVirtualComponent(IProject p, IVirtualComponent referencingComponent) {
			super(p, referencingComponent);
		}

		protected String getFirstIdSegment() {
			return DUMMY_FIRST_SEGMENT;
		}

		protected IContainer[] getUnderlyingContainers() {
			return new IContainer[]{};
		}

		protected IResource[] getLooseResources() {
			return new IResource[]{};
		}
	}
	
	private IVirtualReference createDummyReference(String path) {
		return createDummyReference(path, parentProject, ComponentCore.createComponent(parentProject));
	}
	
	public static IVirtualReference createDummyReference(String path, IProject project, IVirtualComponent rootComponent) {
		IVirtualComponent comp = new DummyVirtualComponent(project, rootComponent);
		IVirtualReference ref = ComponentCore.createReference(rootComponent, comp);
		IPath path2 = new Path(path);
		IPath runtimePath = path2.segmentCount() > 1 ? path2.removeLastSegments(1) : new Path("/"); //$NON-NLS-1$
		runtimePath = runtimePath.makeRelative();
		ref.setRuntimePath(runtimePath);
		ref.setArchiveName(path2.lastSegment());
		return ref;
	}
	
	private String[] findUnusedEntries(IVirtualReference[] current, String[] entries) {
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(Arrays.asList(entries));
		for( int i = 0; i < current.length; i++ ) {
			String currentEntry = current[i].getRuntimePath().append(current[i].getArchiveName()).toString();
			list.remove(currentEntry);
		}
		return list.toArray(new String[list.size()]);
	}
	
	/**
	 * Clients who find this unacceptable should override
	 * @param root
	 * @return
	 */
	public IFile getManifestIFile(IVirtualComponent root) {
		IVirtualFolder rootFolder = root.getRootFolder();
		IVirtualFile vf = rootFolder.getFile(new Path("META-INF/MANIFEST.MF"));
		if( vf.exists() )
			return vf.getUnderlyingFile();
		return null;
	}
	
	protected String[] getPossibleParentProjects() {
		IProject[] projects = JavaModuleComponentUtility.findParentProjects(project);
		String[] strings = new String[projects.length];
		for( int i = 0; i < projects.length; i++ ) {
			strings[i] = projects[i].getName();
		}
		return strings;
	}


	public boolean performOk() {
		IDataModel updateManifestDataModel = DataModelFactory.createDataModel(new UpdateManifestDataModelProvider());
		updateManifestDataModel.setProperty(UpdateManifestDataModelProperties.PROJECT_NAME, project.getName());
		updateManifestDataModel.setBooleanProperty(UpdateManifestDataModelProperties.MERGE, false);
		updateManifestDataModel.setProperty(UpdateManifestDataModelProperties.MANIFEST_FILE, getManifestIFile(rootComponent));
		ArrayList<String> asStrings = new ArrayList<String>();
		Iterator<IVirtualReference> i = list.iterator();
		IVirtualReference tmp;
		while(i.hasNext()) {
			tmp = i.next();
			asStrings.add(tmp.getRuntimePath().append(tmp.getArchiveName()).toString());
		}
		updateManifestDataModel.setProperty(UpdateManifestDataModelProperties.JAR_LIST, asStrings);
		try {
			updateManifestDataModel.getDefaultOperation().execute(new NullProgressMonitor(), null );
		} catch (ExecutionException e) {
			// TODO log J2EEUIPlugin.logError(e);
		}

		return true;
	}

	public void performDefaults() {
	}

	public boolean performCancel() {
		return false;
	}

	public void setVisible(boolean visible) {
	}

	public void dispose() {
	}

}
