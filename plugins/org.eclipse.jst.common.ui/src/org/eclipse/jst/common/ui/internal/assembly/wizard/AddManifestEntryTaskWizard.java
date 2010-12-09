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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jst.common.internal.modulecore.util.JavaModuleComponentUtility;
import org.eclipse.jst.common.ui.internal.JstCommonUIPlugin;
import org.eclipse.jst.common.ui.internal.Messages;
import org.eclipse.jst.common.ui.internal.assembly.wizard.ManifestModuleDependencyControl.ManifestLabelProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.IWorkbenchGraphicConstants;
import org.eclipse.ui.internal.WorkbenchImages;
import org.eclipse.ui.internal.dialogs.PropertyDialog;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.impl.TaskModel;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.componentcore.ui.internal.taskwizard.IWizardHandle;
import org.eclipse.wst.common.componentcore.ui.internal.taskwizard.TaskWizard;
import org.eclipse.wst.common.componentcore.ui.internal.taskwizard.WizardFragment;

public class AddManifestEntryTaskWizard extends TaskWizard {
	public static final String PARENT_PROJECT = "PARENT_PROJECT"; // data model key //$NON-NLS-1$
	public static final String CHILD_PROJECT = "CHILD_PROJECT"; // data model key //$NON-NLS-1$
	public static final String CURRENT_REFERENCES = "CURRENT_REFERENCES"; // data model key for return val //$NON-NLS-1$
	public static final String RETURNED_REFERENCES = "RETURNED_REFERENCES"; // data model key for return val //$NON-NLS-1$
	public AddManifestEntryTaskWizard() {
		super(Messages.AddManifestEntryTaskWizardTitle, new ManifestRootFragment());
	}
	
	public static class ManifestRootFragment extends WizardFragment {
		private TableViewer viewer;
		private Button addCustom;
		private Text customEntryText;
		protected IProject parentProject, childProject;
		private IVirtualReference[] selected;
		private ShowPossibleManifestEntryContentProvider contentProvider;
		private Link parentContainerLink;
		public boolean hasComposite() {
			return true;
		}
		private void updateWidgets() throws InvocationTargetException {
		
			viewer.setInput(ResourcesPlugin.getWorkspace());
			viewer.refresh();
		}
		
		private static GridLayout glayout( final int columns )
		{
			final GridLayout gl = new GridLayout( columns, false );
			gl.marginWidth = 0;
			gl.marginHeight = 0;
			
			return gl;
		}
		
		public Composite createComposite(Composite parent, IWizardHandle handle) {
			parentProject = (IProject)getTaskModel().getObject(PARENT_PROJECT);
			childProject = (IProject)getTaskModel().getObject(CHILD_PROJECT);
			handle.setTitle(Messages.AddManifestEntryTaskWizardTitle);
			handle.setDescription(NLS.bind(Messages.AddManifestEntryTaskWizardDesc, parentProject.getName()));
			handle.setImageDescriptor(WorkbenchImages.getImageDescriptor(IWorkbenchGraphicConstants.IMG_WIZBAN_EXPORT_WIZ));
			handle.setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( JstCommonUIPlugin.PLUGIN_ID, "icons/manifest-classpath-banner.png" ) );
			
			Composite root = new Composite(parent, SWT.NONE);
			root.setLayout(glayout(1));
			
			viewer = ManifestModuleDependencyControl.createManifestReferenceTableViewer(root, SWT.MULTI);
			final GridData gData = new GridData(GridData.FILL_BOTH);
			gData.heightHint = 350;
			viewer.getTable().setLayoutData(gData);
			contentProvider = new ShowPossibleManifestEntryContentProvider(parentProject, childProject, getTaskModel());
			viewer.setContentProvider(contentProvider);
			viewer.setLabelProvider(new ManifestLabelProvider());
			viewer.setInput(ResourcesPlugin.getWorkspace());
			viewer.addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					viewerSelectionChanged();
				}
			});
			
			final Composite customEntryComposite = new Composite(root,SWT.NONE);
			customEntryComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			customEntryComposite.setLayout(glayout(2));
			
			customEntryText = new Text(customEntryComposite, SWT.BORDER);
			customEntryText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			customEntryText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					if(customEntryText.getText() == null || customEntryText.getText().trim().length() == 0 || new Path(customEntryText.getText().trim()).makeRelative().toString().length() == 0)
						addCustom.setEnabled(false);
					else
						addCustom.setEnabled(true);
				} 
			});
			
			addCustom = new Button(customEntryComposite, SWT.PUSH);
			addCustom.setLayoutData(new GridData());
			addCustom.setText(Messages.CustomEntryButton);
			addCustom.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					addCustomPressed();
				}
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
			addCustom.setEnabled(false);
			
			createConfigLink(root);
			parentContainerLink.setLayoutData(new GridData());
			
			return root;
		}
		
		private void addCustomPressed() {
			IVirtualReference ref = ManifestModuleDependencyControl.createDummyReference(
					new Path(customEntryText.getText()).toString(), parentProject, ComponentCore.createComponent(parentProject));
			contentProvider.addPossibleReference(ref);
			customEntryText.setText(""); //$NON-NLS-1$
			addCustom.setEnabled(false);
			viewer.refresh();
		}
		
		private void viewerSelectionChanged() {
			IStructuredSelection sel = (IStructuredSelection)viewer.getSelection();
			Object[] obj = sel.toArray();
			IVirtualReference[] ret = new IVirtualReference[obj.length];
			for( int i = 0; i < ret.length; i++ ) {
				ret[i] = (IVirtualReference)obj[i];
			}
			selected = ret;
		}
		
		private void createConfigLink(Composite aGroup){
			parentContainerLink = new Link(aGroup,SWT.None);
			parentContainerLink.setText("<A>"+ //$NON-NLS-1$
					Messages.ConfigureParentLink+"</A>"); //$NON-NLS-1$
			parentContainerLink.addSelectionListener(new SelectionListener() {
				
				public void doLinkActivated(Link e) {
					IProject parentProject = ManifestRootFragment.this.parentProject;
					PreferenceDialog dialog = PropertyDialog.createDialogOn(ManifestRootFragment.this.getPage().getControl().getShell(),
							"org.eclipse.wst.common.componentcore.ui.DeploymentAssemblyPage", parentProject); //$NON-NLS-1$
					if (dialog != null) {
						dialog.open();
					}
					try {
						updateWidgets();
					} catch (InvocationTargetException ie) {

					}
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					doLinkActivated((Link) e.widget);					
				}

				public void widgetSelected(SelectionEvent e) {
					doLinkActivated((Link) e.widget);					
				}
			});
			
		}
		
		
		// just return the selected refs
		public void performFinish(IProgressMonitor monitor) throws CoreException {
			getTaskModel().putObject(RETURNED_REFERENCES, selected);
		}
	}
	
	public static class ShowPossibleManifestEntryContentProvider extends ArrayContentProvider {
		private IProject parent, child;
		private TaskModel model;
		private IVirtualReference[] possible;
		public ShowPossibleManifestEntryContentProvider(IProject parent, IProject child, TaskModel taskModel) {
			this.parent = parent;
			this.child = child;
			this.model = taskModel;
		}
		public Object[] getElements(Object inputElement) {
			if( possible == null ) {
				IVirtualReference[] current = (IVirtualReference[])model.getObject(CURRENT_REFERENCES);
				possible = JavaModuleComponentUtility.findPossibleManifestEntries(parent, child, current);
			}
			return possible;
		}
		public void addPossibleReference(IVirtualReference ref) {
			ArrayList<IVirtualReference> newRefs = new ArrayList<IVirtualReference>();
			newRefs.addAll(Arrays.asList(possible));
			newRefs.add(ref);
			possible = newRefs.toArray(new IVirtualReference[newRefs.size()]);
		}
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if (newInput != null) {
				possible = null;
				viewer.refresh();
			}
		}
		
	}
}
