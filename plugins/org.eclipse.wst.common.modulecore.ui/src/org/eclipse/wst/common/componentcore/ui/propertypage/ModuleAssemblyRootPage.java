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
 *    
 * API in these packages is provisional in this release
 ******************************************************************************/
package org.eclipse.wst.common.componentcore.ui.propertypage;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.ui.Messages;
import org.eclipse.wst.common.componentcore.ui.internal.propertypage.DependencyPageExtensionManager;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * Primary project property page for Module assembly;
 */
public class ModuleAssemblyRootPage extends PropertyPage {
	
	private IProject project;
	private IModuleDependenciesControl[] controls = new IModuleDependenciesControl[0];
	
	public ModuleAssemblyRootPage() {
		super();
	}
	
	private Composite getFacetErrorComposite(final Composite parent) {
		final String errorCheckingFacet = Messages.ErrorCheckingFacets;
		setErrorMessage(errorCheckingFacet);
		return getErrorComposite(parent, errorCheckingFacet);		
	}
	private Composite getVirtCompErrorComposite(final Composite parent) {
		final String errorCheckingFacet = Messages.ErrorNotVirtualComponent;
		setErrorMessage(errorCheckingFacet);
		return getErrorComposite(parent, errorCheckingFacet);		
	}
	
	
	private Composite getErrorComposite(final Composite parent, final String error) {
		final Composite composite = new Composite(parent, SWT.NONE);
		final GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginWidth = 0;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		final Label label= new Label(composite, SWT.NONE);
		label.setText(error);
		return composite;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {
		for (int i = 0; i < controls.length; i++) {
			if (controls[i] != null) {
				if (!controls[i].performOk()) {
					return false;
				}
			}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	public void performDefaults() {
		for (int i = 0; i < controls.length; i++) {
			if (controls[i] != null) {
				controls[i].performDefaults();
			}
		}
	}
	
	public void performApply() {
		for (int i = 0; i < controls.length; i++) {
			if (controls[i] != null) {
				controls[i].performApply();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferencePage#performCancel()
	 */
	public boolean performCancel() {
		for (int i = 0; i < controls.length; i++) {
			if (controls[i] != null) {
				if (!controls[i].performCancel()) {
					return false;
				}
			}
		}
		return super.performCancel();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		for (int i = 0; i < controls.length; i++) {
			if (controls[i] != null) {
				controls[i].setVisible(visible);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#dispose()
	 */
	public void dispose() {
		super.dispose();
		for (int i = 0; i < controls.length; i++) {
			if(controls[i] != null){
				controls[i].dispose();
			}
		}
	}

	protected static void createDescriptionComposite(final Composite parent, final String description) {
		Composite descriptionComp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		descriptionComp.setLayout(layout);
		descriptionComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fillDescription(descriptionComp, description);
	}
	
	private static void fillDescription(Composite c, String s) {
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 250;
		final Label label = new Label( c, SWT.NONE );
		label.setLayoutData(data);
		label.setText(s);
	}
	
	protected Control createContents(Composite parent) {
		
		// Need to find out what type of project we are handling
		project = (IProject) getElement().getAdapter(IResource.class);
		if( project != null ) {
			try {
				IFacetedProject facetedProject = ProjectFacetsManager.create(project); 
				IDependencyPageProvider provider = null;
				if( facetedProject == null )
					return getFacetErrorComposite(parent);
				
				provider = DependencyPageExtensionManager.getManager().getProvider(facetedProject);
				if( provider != null ) {
					controls = provider.createPages(facetedProject, this);
					controls = controls == null ? new IModuleDependenciesControl[]{} : controls;
					if (provider.getPageTitle(project) != null)
						setTitle(provider.getPageTitle(project));
					return provider.createRootControl(facetedProject, controls, parent);
				}
				
				if( ComponentCore.createComponent(project) == null )
					return getVirtCompErrorComposite(parent);
				
				AddModuleDependenciesPropertiesPage page = new AddModuleDependenciesPropertiesPage(project, this);
				controls = new IModuleDependenciesControl[1];
				controls[0] = page;
				return page.createContents(parent);
			} catch( CoreException ce )	{
			}
		}
		return getFacetErrorComposite(parent);
	}
	
    public void createControl(Composite parent){
    	super.createControl(parent);
    	getDefaultsButton().setText(Messages.Revert);
    }
}
