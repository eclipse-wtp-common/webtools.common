/******************************************************************************
 * Copyright (c) 2010 Red Hat and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rob Stryker - initial implementation and ongoing maintenance
 *    Konstantin Komissarchik - misc. UI cleanup
 ******************************************************************************/
package org.eclipse.jst.common.ui.internal.assembly.wizard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.layout.GridDataFactory;
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
import org.eclipse.jst.common.ui.internal.IJstCommonUIContextIds;
import org.eclipse.jst.common.ui.internal.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.resources.AbstractResourceListVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.componentcore.ui.ModuleCoreUIPlugin;
import org.eclipse.wst.common.componentcore.ui.internal.propertypage.DependencyPageExtensionManager;
import org.eclipse.wst.common.componentcore.ui.internal.propertypage.IVirtualComponentLabelProvider;
import org.eclipse.wst.common.componentcore.ui.propertypage.AbstractIModuleDependenciesControl;
import org.eclipse.wst.common.componentcore.ui.propertypage.ModuleAssemblyRootPage;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

public class ManifestModuleDependencyControl extends AbstractIModuleDependenciesControl {
	
	protected IProject project;
	protected IVirtualComponent rootComponent;
	protected IProject parentProject;
	protected ModuleAssemblyRootPage propPage;
	protected Button addButton, removeButton, moveUpButton, moveDownButton;
	protected Combo parentSelection;
	protected TableViewer manifestEntryViewer;
	private ArrayList<IVirtualReference> list = new ArrayList<IVirtualReference>();
	private String previousManifest = null;
	
	public ManifestModuleDependencyControl(final IProject project,
			final ModuleAssemblyRootPage page) {
		this.project = project;
		this.propPage = page;
		rootComponent = ComponentCore.createComponent(project);
	}

	private static GridLayout glayout( final int columns )
	{
		final GridLayout gl = new GridLayout( columns, false );
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		
		return gl;
	}
	
	public Composite createContents(Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(root, IJstCommonUIContextIds.DEPLOYMENT_ASSEMBLY_PREFERENCE_PAGE_MANIFEST_ENTRIES_TAB);
		root.setLayout(glayout(1));
		
		final Composite parentSelectionComposite = new Composite( root, SWT.NONE );
		parentSelectionComposite.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
		parentSelectionComposite.setLayout( glayout( 2 ) );
		
		Label l = new Label(parentSelectionComposite, SWT.NONE);
		l.setText(Messages.ParentProject);
		l.setLayoutData(new GridData());
		
		parentSelection = new Combo(parentSelectionComposite, SWT.READ_ONLY);
		parentSelection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		parentSelection.setItems(getPossibleParentProjects());
		parentSelection.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				refreshViewerFromNewParentProject();
			}
		});
		
		final Composite manifestEntryViewerComposite = new Composite( root, SWT.NONE );
		final GridData manifestEntryViewerCompositeLayoutData = new GridData( GridData.FILL_BOTH );
		manifestEntryViewerComposite.setLayoutData( manifestEntryViewerCompositeLayoutData );
		manifestEntryViewerComposite.setLayout( glayout( 2 ) );
		
		manifestEntryViewer = createManifestReferenceTableViewer(manifestEntryViewerComposite, SWT.SINGLE);
		GridData data = new GridData(GridData.FILL_BOTH);
		int numlines = Math.min(10, manifestEntryViewer.getTable().getItemCount());
		data.heightHint = manifestEntryViewer.getTable().getItemHeight() * numlines;
		manifestEntryViewer.getTable().setLayoutData(data);
		
		Composite buttonColumn = new Composite(manifestEntryViewerComposite, SWT.NONE);
		buttonColumn.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		buttonColumn.setLayout(glayout( 1 ));
		
		addButton = new Button(buttonColumn, SWT.PUSH);
		addButton.setText(Messages.Add);
		GridDataFactory.defaultsFor(addButton).applyTo(addButton);
		
		removeButton = new Button(buttonColumn, SWT.PUSH);
		removeButton.setText(Messages.Remove);
		GridDataFactory.defaultsFor(removeButton).applyTo(removeButton);
		
		moveUpButton = new Button(buttonColumn, SWT.PUSH);
		moveUpButton.setText(Messages.MoveUp);
		GridDataFactory.defaultsFor(moveUpButton).applyTo(moveUpButton);
		
		moveDownButton = new Button(buttonColumn, SWT.PUSH);
		moveDownButton.setText(Messages.MoveDown);
		GridDataFactory.defaultsFor(moveDownButton).applyTo(moveDownButton);
		
		IFile manifest = getManifestIFile(rootComponent);
		if(manifest == null) {
			addButton.setEnabled(false);
		} else {
			addButton.setEnabled(true);
			previousManifest = manifest.getFullPath().toOSString();
		}
		removeButton.setEnabled(false);
		moveUpButton.setEnabled(false);
		moveDownButton.setEnabled(false);
		
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
		if(!addButton.isEnabled()) {
			removeButton.setEnabled(false);
			moveUpButton.setEnabled(false);
			moveDownButton.setEnabled(false);
		} else {
			int index = manifestEntryViewer.getTable().getSelectionIndex();
			int size = manifestEntryViewer.getTable().getItems().length;
			removeButton.setEnabled(index != -1);
			moveUpButton.setEnabled(index != -1 && index != 0);
			moveDownButton.setEnabled(index != size-1);
		}
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
			getDelegates();
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
			refsForCurrentProject = sortRefsForCurrentProject(addMissingDummyEntries(current));
			for( int i = 0; i < refsForCurrentProject.length; i++ ) {
				list.add(refsForCurrentProject[i]);
			}
		}
		manifestEntryViewer.refresh();
	}
	
	private IVirtualReference[] sortRefsForCurrentProject(IVirtualReference[] currentRefs) {
		IFile manifestFile = getManifestIFile(rootComponent);
		if (manifestFile == null)
			return currentRefs;
		
		HashMap<String, IVirtualReference> unsortedRefMap = new HashMap<String, IVirtualReference>();
		for (int i = 0; i < currentRefs.length; i++) {
			IVirtualReference ref = currentRefs[i];
			String entryName = ref.getRuntimePath().append(ref.getArchiveName()).toString();
			unsortedRefMap.put(entryName, ref);
		}
		List<IVirtualReference> sortedRefs = new ArrayList<IVirtualReference>();
		ArchiveManifest manifest = ManifestUtilities.getManifest(manifestFile);
		String[] entries = manifest.getClassPathTokenized();
		for (int i = 0; i < entries.length; i++) {
			IVirtualReference ref = unsortedRefMap.get(entries[i]);
			if (ref != null) {
				sortedRefs.add(ref);
			}
		}
		return sortedRefs.toArray(new IVirtualReference[sortedRefs.size()]);
	}
	
	protected void refreshViewer() {
		refsForCurrentProject = list.toArray(new IVirtualReference[list.size()]);
		GridData data = new GridData(GridData.FILL_BOTH);
		int numlines = Math.min(10, manifestEntryViewer.getTable().getItemCount());
		data.heightHint = manifestEntryViewer.getTable().getItemHeight() * numlines;
		manifestEntryViewer.getTable().setLayoutData(data);
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
		if(addButton != null && addButton.isEnabled()) {
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
		}

		return true;
	}

	public void performDefaults() {
		list.clear();
		refreshViewerFromNewParentProject();
	}

	public boolean performCancel() {
		return false;
	}

	public void setVisible(boolean visible) {
	}

	public void dispose() {
	}

	public void performApply() {
		IFile manifest = getManifestIFile(rootComponent);
		if(manifest == null) {
			previousManifest = null;
			addButton.setEnabled(false);
			performDefaults();
		} else {
			if(previousManifest == null) {
				performDefaults();
			} else {
				String currentManifest = manifest.getFullPath().toOSString();
				if(!previousManifest.equals(currentManifest)) {
					performDefaults();
				}
			}
			previousManifest = manifest.getFullPath().toOSString();
			addButton.setEnabled(true);
			
		}
		updateButtons();
		performOk();
	}

}
