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
package org.eclipse.wst.common.componentcore.ui.internal.propertypage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.componentcore.ui.Messages;
import org.eclipse.wst.common.componentcore.ui.internal.propertypage.DependencyPageExtensionManager.ReferenceExtension;
import org.eclipse.wst.common.componentcore.ui.internal.taskwizard.IWizardHandle;
import org.eclipse.wst.common.componentcore.ui.internal.taskwizard.WizardFragment;

public class NewReferenceRootWizardFragment extends WizardFragment {
	protected Map<String, WizardFragment> fragmentMap = 
		new HashMap<String, WizardFragment>();
	private IWizardHandle wizard;
	private TreeViewer viewer;
	public boolean hasComposite() {
		return true;
	}

	public Composite createComposite(Composite parent, IWizardHandle wizard) {
		this.wizard = wizard;
		wizard.setTitle(Messages.NewReferenceTitle);
		wizard.setDescription(Messages.NewReferenceDescription);
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new FillLayout());
		viewer = new TreeViewer(c, SWT.SINGLE | SWT.BORDER);
		viewer.setLabelProvider(getLabelProvider());
		viewer.setContentProvider(getContentProvider());
		viewer.setInput(ResourcesPlugin.getWorkspace());
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				viewerSelectionChanged();
			}
		});
		return c;
	}

	protected void viewerSelectionChanged() {
		wizard.update();
	}
	
	protected WizardFragment getWizardFragment(String extensionPointID) {
		try {
			WizardFragment fragment = fragmentMap.get(extensionPointID);
			if (fragment != null)
				return fragment;
		} catch (Exception e) {
			// ignore
		}
		
		WizardFragment fragment = DependencyPageExtensionManager.getManager().loadReferenceWizardFragment(extensionPointID);
		if (fragment != null)
			fragmentMap.put(extensionPointID, fragment);
		return fragment;
	}

	public List getChildFragments() {
		List<WizardFragment> listImpl = new ArrayList<WizardFragment>();
		createChildFragments(listImpl);
		return listImpl;
	}

	protected void createChildFragments(List<WizardFragment> list) {
		// Instantiate and add the fragment for the current ID
		if( viewer != null ) {
			IStructuredSelection sel = (IStructuredSelection)viewer.getSelection();
			ReferenceExtension selected = (ReferenceExtension)sel.getFirstElement();
			if( selected != null ) {
				WizardFragment child = getWizardFragment(selected.getId());
				if( child != null )
					list.add(child);
			}
		}
	}

	public boolean isComplete() {
		return true;
	}

	
	private LabelProvider labelProvider = null;
	private ITreeContentProvider contentProvider = null;
	protected LabelProvider getLabelProvider() {
		if( labelProvider == null ) {
			labelProvider = new LabelProvider() {
				public Image getImage(Object element) {
					if( element instanceof ReferenceExtension)
						return ((ReferenceExtension)element).getImage();
					return null;
				}
				public String getText(Object element) {
					if( element instanceof ReferenceExtension)
						return ((ReferenceExtension)element).getName();
					return element == null ? "" : element.toString();//$NON-NLS-1$
				}
			   public void dispose() {
			    	super.dispose();
			    	if( extensions != null ) {
			    		for( int i = 0; i < extensions.length; i++) {
			    			extensions[i].disposeImage();
			    		}
			    	}
			    }
			};
		}
		return labelProvider;
	}
	
	private ReferenceExtension[] extensions = null;
	protected ITreeContentProvider getContentProvider() {
		if( contentProvider == null ) {
			contentProvider = new ITreeContentProvider() {
				public Object[] getElements(Object inputElement) {
					if( extensions == null )
						extensions = DependencyPageExtensionManager.getManager().getReferenceExtensions();
					return extensions;
				}
				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				}
				public void dispose() {
				}
				public boolean hasChildren(Object element) {
					return false;
				}
				public Object getParent(Object element) {
					return null;
				}
				public Object[] getChildren(Object parentElement) {
					return null;
				}
			};
		}
		return contentProvider;
	}
}
