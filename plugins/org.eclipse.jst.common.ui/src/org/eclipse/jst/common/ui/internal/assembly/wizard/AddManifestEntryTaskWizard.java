package org.eclipse.jst.common.ui.internal.assembly.wizard;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jst.common.internal.modulecore.util.JavaModuleComponentUtility;
import org.eclipse.jst.common.ui.internal.Messages;
import org.eclipse.jst.common.ui.internal.assembly.wizard.ManifestModuleDependencyControl.ManifestLabelProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
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
		private IProject parentProject, childProject;
		private IVirtualReference[] selected;
		private ShowPossibleManifestEntryContentProvider contentProvider;
		public boolean hasComposite() {
			return true;
		}

		public Composite createComposite(Composite parent, IWizardHandle handle) {
			parentProject = (IProject)getTaskModel().getObject(PARENT_PROJECT);
			childProject = (IProject)getTaskModel().getObject(CHILD_PROJECT);
			handle.setDescription(NLS.bind(Messages.AddManifestEntryTaskWizardDesc, parentProject.getName()));
			Composite root = new Composite(parent, SWT.NONE);
			root.setLayout(new FormLayout());
			customEntryText = new Text(root, SWT.BORDER);
			addCustom = new Button(root, SWT.PUSH);
			customEntryText.setLayoutData(ManifestModuleDependencyControl.createFormData(null, 0, 100, -5, 0, 5, addCustom, -5));
			addCustom.setText(Messages.CustomEntryButton);
			addCustom.setLayoutData(ManifestModuleDependencyControl.createFormData(null, 0, 100, -5, null, 0, 100, -5));
			addCustom.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					addCustomPressed();
				}
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

			viewer = ManifestModuleDependencyControl.createManifestReferenceTableViewer(root, SWT.MULTI);
			viewer.getTable().setLayoutData(ManifestModuleDependencyControl.createFormData(0, 5, addCustom, 0, 0, 5, 100, -5));
			contentProvider = new ShowPossibleManifestEntryContentProvider(parentProject, childProject, getTaskModel());
			viewer.setContentProvider(contentProvider);
			viewer.setLabelProvider(new ManifestLabelProvider());
			viewer.setInput(ResourcesPlugin.getWorkspace());
			viewer.addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					viewerSelectionChanged();
				}
			});
			return root;
		}
		
		private void addCustomPressed() {
			IVirtualReference ref = ManifestModuleDependencyControl.createDummyReference(
					customEntryText.getText(), parentProject, ComponentCore.createComponent(parentProject));
			contentProvider.addPossibleReference(ref);
			customEntryText.setText(""); //$NON-NLS-1$
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
		
	}
}
