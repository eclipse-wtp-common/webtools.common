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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
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
	private SashForm sform1;
	private Composite topComposite;
	private ScrolledComposite problemsViewComposite;
	private Composite subProblemsViewComposite;
	
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
		final Text label = new Text( c, SWT.WRAP | SWT.READ_ONLY);
		label.setBackground(c.getBackground());
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
					
					this.sform1 = new SashForm( parent, SWT.VERTICAL | SWT.SMOOTH );
					GridData gd1 = new GridData(SWT.LEFT, SWT.FILL, false, false);
			        this.sform1.setLayoutData(gd1);
			        
					topComposite = provider.createRootControl(facetedProject, controls, sform1);
				} else {				
					if( ComponentCore.createComponent(project) == null )
						return getVirtCompErrorComposite(parent);
					
					AddModuleDependenciesPropertiesPage page = new AddModuleDependenciesPropertiesPage(project, this);
					controls = new IModuleDependenciesControl[1];
					controls[0] = page;
					this.sform1 = new SashForm( parent, SWT.VERTICAL | SWT.SMOOTH );
					GridData gd1 = new GridData(SWT.LEFT, SWT.FILL, false, false);
			        this.sform1.setLayoutData(gd1);
			        
					topComposite = page.createContents(sform1);
				}
				
				problemsViewComposite = new ScrolledComposite(sform1, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);				
				fillProblemsViewComposite();
		        this.sform1.setWeights( new int[] { 80, 20 } );
		        problemsViewComposite.addListener(SWT.Resize, new Listener() {
					public void handleEvent(Event e) {
						handleProblemsViewResize();
					}
				});
		        return sform1;
			} catch( CoreException ce )	{
			}
		}
		return getFacetErrorComposite(parent);
	}
	
	private void handleProblemsViewResize() {
		if(subProblemsViewComposite != null && !subProblemsViewComposite.isDisposed() && topComposite != null) {
           	int width = topComposite.getClientArea().width;
           	if(width < 400) {
           		width = 400;
           	}
			int i = 1;
			for( Control child : subProblemsViewComposite.getChildren() )
			{
				if(i%2 == 0) {
					GridData gd = (GridData) child.getLayoutData();
					gd.widthHint = width - 50;
				}
				i++;
			}
			subProblemsViewComposite.setSize(subProblemsViewComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        	subProblemsViewComposite.update();
		}
	}
	
	protected void fillProblemsViewComposite() {
		if(problemsViewComposite != null) {
			boolean resize = false;
			for( Control child : problemsViewComposite.getChildren() )
			{
				child.dispose();
				resize = true;
			}
			IStatus [] problems = getProblemElements();
	    	if(problems != null && problems.length > 0) {
	    		GridData gd = new GridData();
	    		gd.heightHint = 100;
	        	gd.horizontalAlignment = SWT.FILL;
	    		gd.verticalAlignment = SWT.BOTTOM;
	    		problemsViewComposite.setLayoutData(gd);
	    		problemsViewComposite.setBackground( Display.getDefault().getSystemColor(SWT.COLOR_WHITE) );
	        	subProblemsViewComposite = new Composite( problemsViewComposite, SWT.NONE);
	            subProblemsViewComposite.setLayoutData(new GridData( SWT.FILL, SWT.FILL, true, true));
	            subProblemsViewComposite.setLayout(glmargins( new GridLayout(2, false ), 0, 0, 5, 5) );
	            subProblemsViewComposite.setBackground( Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

	            int width = 400;
	            if(topComposite != null && topComposite.getClientArea().width > 400) {
	            	width = topComposite.getClientArea().width;
	            }
	            
	            Listener focusOnProblemListener = new Listener() {
	                public void handleEvent(Event e) {
	                  Control problemLabel = (Control) e.widget;
	                  Rectangle problemLabelBounds = problemLabel.getBounds();
	                  Rectangle scrollableArea = problemsViewComposite.getClientArea();
	                  Point currentScrollPosition = problemsViewComposite.getOrigin();
	                  if (currentScrollPosition.y > problemLabelBounds.y)
	                	  currentScrollPosition.y = Math.max(0, problemLabelBounds.y);
	                  if (currentScrollPosition.y + scrollableArea.height < problemLabelBounds.y + problemLabelBounds.height)
	                	  currentScrollPosition.y = Math.max(0, problemLabelBounds.y + problemLabelBounds.height - scrollableArea.height);
	                  problemsViewComposite.setOrigin(currentScrollPosition);
	                }
	              };
	           	
	        	for (int i = 0; i < problems.length; i++) {
	    			IStatus singleStatus = problems[i];
	    			if (!singleStatus.isOK() && singleStatus.getMessage() != null) {
	    				final Label image = new Label( subProblemsViewComposite, SWT.NONE );
	    				GridData gdImage = new GridData();	    				
	    				gdImage.verticalAlignment = SWT.BEGINNING;
	    	            image.setBackground( Display.getDefault().getSystemColor(SWT.COLOR_WHITE) );
	    	            image.setImage(getProblemImage(singleStatus));
	    	            image.setLayoutData(gdImage);
	    	            
	    	            final Text text  = new Text( subProblemsViewComposite, SWT.WRAP | SWT.READ_ONLY);
	    	            GridData gdLabel = new GridData();
	    	            gdLabel.widthHint = width - 50;
	    	            text.setBackground( Display.getDefault().getSystemColor(SWT.COLOR_WHITE) );
	    	            text.setText(singleStatus.getMessage());
	    	            text.setLayoutData(gdLabel);
	    	            text.addListener(SWT.Activate, focusOnProblemListener);
	    			}
	    		}
	            problemsViewComposite.setContent(subProblemsViewComposite);
	            
	            if(resize)
	            	handleProblemsViewResize();
	            
	            if( this.sform1.getMaximizedControl() != null ) {
	                this.sform1.setMaximizedControl( null );
	            }
	    	} else {
	    		this.sform1.setMaximizedControl( this.topComposite );
	    	}
		}
	}
	
	
	private final static GridLayout glmargins( final GridLayout layout,
			final int marginWidth,
			final int marginHeight,
			final int marginTop,
			final int marginBottom)
	{
		layout.marginWidth = marginWidth;
		layout.marginHeight = marginHeight;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = marginTop;
		layout.marginBottom = marginBottom;

		return layout;
	}
	
    public void createControl(Composite parent){
    	super.createControl(parent);
    	getDefaultsButton().setText(Messages.Revert);
    }
    
    public void refreshProblemsView(){
    	setErrorMessage(null);
		setMessage(null);
    	setValid(true);
    	fillProblemsViewComposite();
    }
    
    private Image getProblemImage(IStatus element) {
		final ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		final String imageType;

		if(element.getSeverity() == IStatus.ERROR) {
			imageType = ISharedImages.IMG_OBJS_ERROR_TSK;
		}
		else {
			imageType = ISharedImages.IMG_OBJS_WARN_TSK;
		}
		return sharedImages.getImage( imageType );
	}
    
    protected IStatus[] getProblemElements() {
		final List<IStatus> errors = new ArrayList<IStatus>();
		final List<IStatus> warnings = new ArrayList<IStatus>();
        
		for (int i = 0; i < controls.length; i++) {
			if (controls[i] != null) {
				IStatus status = Status.OK_STATUS;
				if(controls[i] instanceof AbstractIModuleDependenciesControl)
					status = ((AbstractIModuleDependenciesControl) controls[i]).validate();
				if(status != null) {
					if (status.isMultiStatus()) {
						MultiStatus multi = (MultiStatus)status;
						if (!multi.isOK()) {
							for (int y = 0; y < multi.getChildren().length; y++) {
								IStatus singleStatus = multi.getChildren()[y];
								if(singleStatus.getMessage() != null && singleStatus.getMessage().trim().length() > 0) {
									if(multi.getChildren()[y].getSeverity() == IStatus.ERROR) {
										errors.add(multi.getChildren()[y]);
									} else {
										warnings.add(multi.getChildren()[y]);
									}
								}
							}
						}
					} else if (!status.isOK()) {
						if(status.getMessage() != null && status.getMessage().trim().length() > 0) {
							if(status.getSeverity() == IStatus.ERROR) {
								errors.add(status);
							} else {
								warnings.add(status);
							}
						}
					}
				}
			}
		}
		if(errors.size() > 0) {
			setValid(false);
			errors.addAll(warnings);
			// This returns all the errors followed by all the warnings
			return errors.toArray(new IStatus[errors.size()]);
		}
		return warnings.toArray(new IStatus[warnings.size()]);
	}
}
