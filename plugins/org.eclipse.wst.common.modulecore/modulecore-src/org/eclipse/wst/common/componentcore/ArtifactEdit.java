/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore;

import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jem.internal.util.emf.workbench.nls.EMFWorkbenchResourceHandler;
import org.eclipse.jem.util.UIContextDetermination;
import org.eclipse.wst.common.componentcore.internal.ArtifactEditModel;
import org.eclipse.wst.common.componentcore.internal.BinaryComponentHelper;
import org.eclipse.wst.common.componentcore.internal.impl.ArtifactEditModelFactory;
import org.eclipse.wst.common.componentcore.internal.impl.ModuleURIUtil;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.frameworks.internal.operations.IOperationHandler;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModel;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModelListener;
import org.eclipse.wst.common.internal.emfworkbench.validateedit.IValidateEditContext;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * Provides a Facade pattern for accessing Module Content Metamodels for Web Tools Platform flexible
 * modules.
 * <p>
 * ArtifactEdit hides the management of accessing edit models ({@see ArtifactEditModel})
 * correctly. Each project may have multiple ({@see ArtifactEditModel})s depending on the number
 * of modules contained by the project. Clients should use ArtifactEdit or an appropriate subclass
 * when working with the content models of WTP modules.
 * </p>
 * 
 * <p>
 * Each ArtifactEdit facade is designed to manage the EditModel lifecycle for clients. However,
 * while each ArtifactEdit is designed to be passed around as needed, clients must enforce the
 * ArtifactEdit lifecycle. The most common method of acquiring a ArtifactEdit instance facade is to
 * use {@see #getArtifactEditForRead(WorkbenchComponent)}&nbsp;or
 * {@see #getArtifactEditForWrite(WorkbenchComponent)}.
 * </p>
 * <p>
 * When clients have concluded their use of the instance, <b>clients must call {@see #dispose()}
 * </b>.
 * </p>
 * <p>
 * This class is experimental until fully documented.
 * </p>
 * 
 * @see ModuleCoreNature
 * @see ArtifactEditModel
 * @plannedfor 1.0
 */
public class ArtifactEdit implements IEditModelHandler, IAdaptable{

	public static final Class ADAPTER_TYPE = ArtifactEdit.class;
	private final ArtifactEditModel artifactEditModel;
	private boolean isReadOnly;
	private boolean isArtifactEditModelSelfManaged;

	private boolean isBinary;
	private BinaryComponentHelper binaryComponentHelper;
	private final IProject project;
	
	/**
	 * 
	 */
	protected ArtifactEdit() {
		super();
		artifactEditModel = null;
		project = null;
	}

	/**
	 * <p>
	 * Returns an instance facade to manage the underlying edit model for the given
	 * {@see WorkbenchComponent}. Instances of ArtifactEdit that are returned through this method
	 * must be {@see #dispose()}ed of when no longer in use.
	 * </p>
	 * <p>
	 * Use to acquire an ArtifactEdit facade for a specific {@see WorkbenchComponent}&nbsp;that
	 * will not be used for editing. Invocations of any save*() API on an instance returned from
	 * this method will throw exceptions.
	 * </p>
	 * <p>
	 * <b>The following method may return null. </b>
	 * </p>
	 * <p>Note: This method is for internal use only. Clients should not call this method.</p>
	 * @param aModule
	 *            A valid {@see WorkbenchComponent}&nbsp;with a handle that resolves to an
	 *            accessible project in the workspace
	 * @return An instance of ArtifactEdit that may only be used to read the underlying content
	 *         model
	 */
	public static ArtifactEdit getArtifactEditForRead(IVirtualComponent aModule) {
		if(aModule.isBinary()){
			return new ArtifactEdit(aModule);
		}
		if (isValidEditableModule(aModule)) {
			IProject project = aModule.getProject();
			ModuleCoreNature nature = ModuleCoreNature.getModuleCoreNature(project);
			return new ArtifactEdit(nature, aModule, true);
		} 
		return null;
	}

	/**
	 * <p>
	 * Returns an instance facade to manage the underlying edit model for the given
	 * {@see WorkbenchComponent}. Instances of ArtifactEdit that are returned through this method
	 * must be {@see #dispose()}ed of when no longer in use.
	 * </p>
	 * <p>
	 * Use to acquire an ArtifactEdit facade for a specific {@see WorkbenchComponent}&nbsp;that
	 * will be used for editing.
	 * </p>
	 * <p>
	 * <b>The following method may return null. </b>
	 * </p>
	 * <p>Note: This method is for internal use only. Clients should not call this method.</p>
	 * @param aModule
	 *            A valid {@see WorkbenchComponent}&nbsp;with a handle that resolves to an
	 *            accessible project in the workspace
	 * @return An instance of ArtifactEdit that may be used to modify and persist changes to the
	 *         underlying content model
	 */
	public static ArtifactEdit getArtifactEditForWrite(IVirtualComponent aModule) {
		if (!aModule.isBinary() && isValidEditableModule(aModule)) {
			IProject project = aModule.getProject();
			ModuleCoreNature nature = ModuleCoreNature.getModuleCoreNature(project);
			return new ArtifactEdit(nature, aModule, false);
		}
		return null;
	}

	/**
	 * <p>
	 * Returns an instance facade to manage the underlying edit model for the given
	 * {@see WorkbenchComponent}. Instances of ArtifactEdit that are returned through this method
	 * must be {@see #dispose()}ed of when no longer in use.
	 * </p>
	 * <p>
	 * Use to acquire an ArtifactEdit facade for a specific {@see WorkbenchComponent}&nbsp;that
	 * will not be used for editing. Invocations of any save*() API on an instance returned from
	 * this method will throw exceptions.
	 * </p>
	 * <p>
	 * <b>The following method may return null. </b>
	 * </p>
	 * 
	 * @param aModule
	 *            A valid {@see WorkbenchComponent}&nbsp;with a handle that resolves to an
	 *            accessible project in the workspace
	 * @return An instance of ArtifactEdit that may only be used to read the underlying content
	 *         model
	 */
	public static ArtifactEdit getArtifactEditForRead(IProject aProject) {
		ArtifactEdit artifactEdit = null;
		try {
			artifactEdit = new ArtifactEdit(aProject, true);
		} catch (IllegalArgumentException iae) {
			artifactEdit = null;
		}
		return artifactEdit;
	}

	/**
	 * <p>
	 * Returns an instance facade to manage the underlying edit model for the given
	 * {@see WorkbenchComponent}. Instances of ArtifactEdit that are returned through this method
	 * must be {@see #dispose()}ed of when no longer in use.
	 * </p>
	 * <p>
	 * Use to acquire an ArtifactEdit facade for a specific {@see WorkbenchComponent}&nbsp;that
	 * will be used for editing.
	 * </p>
	 * <p>
	 * <b>The following method may return null. </b>
	 * </p>
	 * 
	 * @param aModule
	 *            A valid {@see WorkbenchComponent}&nbsp;with a handle that resolves to an
	 *            accessible project in the workspace
	 * @return An instance of ArtifactEdit that may be used to modify and persist changes to the
	 *         underlying content model
	 */
	public static ArtifactEdit getArtifactEditForWrite(IProject aProject) {
		ArtifactEdit artifactEdit = null;
		try {
			artifactEdit = new ArtifactEdit(aProject, false);
		} catch (IllegalArgumentException iae) {
			artifactEdit = null;
		}
		return artifactEdit;
	}

	/**
	 * <p>Note: This method is for internal use only. Clients should not call this method.</p>
	 * @param module
	 *            A {@see WorkbenchComponent}
	 * @return True if the supplied module has a moduleTypeId which has a defined
	 *         {@see IEditModelFactory}&nbsp;and is contained by an accessible project
	 */
	public static boolean isValidEditableModule(IVirtualComponent aModule) {
		if (aModule == null)
			return false;
		if (ModuleURIUtil.fullyQualifyURI(aModule.getProject()) == null)
				return false;
			/* and the containing project must be resolveable and accessible */
			IProject project = aModule.getProject();
			if (project == null || !project.isAccessible())
				return false;
		return true;
	}


	/**
	 * <p>
	 * Creates an instance facade for the given {@see ArtifactEditModel}.
	 * </p>
	 * 
	 * @param anArtifactEditModel
	 */
	public ArtifactEdit(ArtifactEditModel anArtifactEditModel) {
		artifactEditModel = anArtifactEditModel;
		isReadOnly = artifactEditModel.isReadOnly();
		isArtifactEditModelSelfManaged = false;
		
		project = anArtifactEditModel.getProject();
	}

	
	protected ArtifactEdit(IVirtualComponent aBinaryModule){
		if(!aBinaryModule.isBinary()){
			throw new RuntimeException("This constructor is only for binary components.");
		}
		binaryComponentHelper = initBinaryComponentHelper(aBinaryModule);
		artifactEditModel = null;
		isReadOnly = true;
		isBinary = true;
		isArtifactEditModelSelfManaged = true;
		project = null;
		
	}
	
	protected BinaryComponentHelper initBinaryComponentHelper(IVirtualComponent binaryModule) {
		return null;
	}

	/**
	 * <p>
	 * Creates an instance facade for the given {@see WorkbenchComponent}.
	 * </p>
	 * <p>Note: This method is for internal use only. Clients should not call this method.</p>
	 * @param aNature
	 *            A non-null {@see ModuleCoreNature}&nbsp;for an accessible project
	 * @param aModule
	 *            A non-null {@see WorkbenchComponent}&nbsp;pointing to a module from the given
	 *            {@see ModuleCoreNature}
	 */
	protected ArtifactEdit(ModuleCoreNature aNature, IVirtualComponent aModule, boolean toAccessAsReadOnly) {
		
		isReadOnly = toAccessAsReadOnly;
		isArtifactEditModelSelfManaged = true;
		project = aNature.getProject();
		IProject aProject = aModule.getProject();
		URI componentURI = ModuleURIUtil.fullyQualifyURI(aProject);
		Map editModelParams = null;
		if (getContentTypeDescriber() != null) {
			editModelParams = new HashMap();
			editModelParams.put(ArtifactEditModelFactory.PARAM_ROOT_URI, getRootURI());
			editModelParams.put(ArtifactEditModelFactory.PARAM_ROOT_CONTENT_TYPE, getContentTypeDescriber());
		}
		if (toAccessAsReadOnly) 
			artifactEditModel = aNature.getArtifactEditModelForRead(componentURI, this, null, editModelParams);
		else 
			artifactEditModel = aNature.getArtifactEditModelForWrite(componentURI, this, null, editModelParams);
	}

	/**
	 * <p>
	 * Creates an instance facade for the given {@see WorkbenchComponent}.
	 * </p>
	 * 
	 * @param aNature
	 *            A non-null {@see ModuleCoreNature}&nbsp;for an accessible project
	 * @param aModule
	 *            A non-null {@see WorkbenchComponent}&nbsp;pointing to a module from the given
	 *            {@see ModuleCoreNature}
	 */
	public ArtifactEdit(IProject aProject, boolean toAccessAsReadOnly) throws IllegalArgumentException {
		this(aProject,toAccessAsReadOnly,false,null);
	}
	
	/**
	 * <p>
	 * Creates an instance facade for the given {@see WorkbenchComponent}.
	 * </p>
	 * 
	 * @param aProject
	 * @param toAccessAsReadOnly
	 * @param forCreate
	 * @param projectType
	 * @throws IllegalArgumentException
	 */
	protected ArtifactEdit(IProject aProject, boolean toAccessAsReadOnly, boolean forCreate, String projectType) throws IllegalArgumentException {
		
		this(aProject,toAccessAsReadOnly,forCreate,projectType,null);
	}
	
	protected void verifyOperationSupported() {
		if(!validArtifactEdit){
			throw new RuntimeException("Invalid Artifact Edit access (model version not supported)");
		}
	}
	
	private boolean validArtifactEdit = true;
	
	public boolean isValid() {
		return validArtifactEdit;
	}
	
	protected void markInvalid(){
		Logger.global.log(Level.WARNING, "Invalid Artifact Edit access (model version not supported)");
		validArtifactEdit = false;
	}
	
	
	/**
	 * <p>
	 * Creates an instance facade for the given {@see WorkbenchComponent}.
	 * </p>
	 * 
	 * @param aProject
	 * @param toAccessAsReadOnly
	 * @param forCreate
	 * @param projectType - Used to pass specific editModel edit (Used to lookup factory)
	 * @param editModelParams - Properties that can be used to create cacheKey on editModelFactory
	 * @throws IllegalArgumentException
	 */
	protected ArtifactEdit(IProject aProject, boolean toAccessAsReadOnly, boolean forCreate, String projectType, Map editModelParams) throws IllegalArgumentException {

		if (aProject == null || !aProject.isAccessible())
			throw new IllegalArgumentException("Invalid project: " + aProject);

		ModuleCoreNature nature = ModuleCoreNature.getModuleCoreNature(aProject);

		if (nature == null)
			throw new IllegalArgumentException("Project does not have ModuleCoreNature: " + aProject);
		if (!validProjectVersion(aProject)){
			markInvalid();
		}
		IVirtualComponent component = ComponentCore.createComponent(aProject);
		if (component == null)
			throw new IllegalArgumentException("Invalid component handle: " + aProject);
		if (!forCreate && !isValidEditableModule(component))
			throw new IllegalArgumentException("Invalid component handle: " + aProject);
		project = aProject;
		URI componentURI = ModuleURIUtil.fullyQualifyURI(aProject);
		if (getContentTypeDescriber() != null) {
			if (editModelParams == null)
				editModelParams = new HashMap();
			editModelParams.put(ArtifactEditModelFactory.PARAM_ROOT_URI, getRootURI());
			editModelParams.put(ArtifactEditModelFactory.PARAM_ROOT_CONTENT_TYPE, getContentTypeDescriber());
		}
		if (toAccessAsReadOnly) 
			artifactEditModel = nature.getArtifactEditModelForRead(componentURI, this, projectType, editModelParams);
		else 
			artifactEditModel = nature.getArtifactEditModelForWrite(componentURI, this, projectType, editModelParams);
		
			
		isReadOnly = toAccessAsReadOnly;
		isArtifactEditModelSelfManaged = true;
	}
	
	public boolean isProjectOfType(IProject project, String typeID) {
		IFacetedProject facetedProject = null;
		try {
			facetedProject = ProjectFacetsManager.create(project);
		} catch (CoreException e) {
			return false;
		}
		if (facetedProject != null && ProjectFacetsManager.isProjectFacetDefined(typeID)) {
			IProjectFacet projectFacet = ProjectFacetsManager.getProjectFacet(typeID);
			return projectFacet != null && facetedProject.hasProjectFacet(projectFacet);
		}
		return false;
	}
	/**
	 * Used to optionally define an associated content type for XML file creation
	 * @return
	 */
	protected String getContentTypeDescriber() {
		
		if (isProjectOfType(project, IModuleConstants.JST_EJB_MODULE))
			return "org.eclipse.jst.j2ee.ejbDD";
		if (isProjectOfType(project, IModuleConstants.JST_WEB_MODULE))
			return "org.eclipse.jst.j2ee.webDD";
		if (isProjectOfType(project, IModuleConstants.JST_EAR_MODULE))
			return "org.eclipse.jst.j2ee.earDD";
		if (isProjectOfType(project, IModuleConstants.JST_APPCLIENT_MODULE))
			return "org.eclipse.jst.j2ee.appclientDD";
		return null;
	}
	
	/**
	 * Used to optionally define an root URI for the project
	 * @return
	 */
	protected URI getRootURI() {
		
		if (isProjectOfType(project, IModuleConstants.JST_EJB_MODULE))
			return URI.createURI("META-INF/ejb-jar.xml");
		if (isProjectOfType(project, IModuleConstants.JST_WEB_MODULE))
			return URI.createURI("WEB-INF/web.xml");
		if (isProjectOfType(project, IModuleConstants.JST_EAR_MODULE))
			return URI.createURI("META-INF/application.xml");
		if (isProjectOfType(project, IModuleConstants.JST_APPCLIENT_MODULE))
			return URI.createURI("META-INF/application-client.xml");
		return null;
	}

	protected boolean validProjectVersion(IProject project2) {
		return true;
	}

	/**
	 * <p>
	 * Force a save of the underlying model. The following method should be used with care. Unless
	 * required, use {@see #saveIfNecessary(IProgressMonitor)}&nbsp; instead.
	 * </p>
	 * 
	 * @see org.eclipse.wst.common.componentcore.IEditModelHandler#save()
	 * @throws IllegalStateException
	 *             If the ModuleCore object was created as read-only
	 */
	public void save(IProgressMonitor aMonitor) {
		if (isReadOnly())
			throwAttemptedReadOnlyModification();
		else if (validateEdit().isOK())
			artifactEditModel.save(aMonitor, this);
	}

	/**
	 * <p>
	 * Save the underlying model only if no other clients are currently using the model. If the
	 * model is not shared, it will be saved. If it is shared, the save will be deferred.
	 * </p>
	 * 
	 * @see org.eclipse.wst.common.componentcore.IEditModelHandler#saveIfNecessary()
	 * @throws IllegalStateException
	 *             If the ModuleCore object was created as read-only
	 */
	public void saveIfNecessary(IProgressMonitor aMonitor) {
		if (isReadOnly())
			throwAttemptedReadOnlyModification();
		else if (validateEdit().isOK())
			artifactEditModel.saveIfNecessary(aMonitor, this);
	}
	
	/**
	 * Validate edit for resource state
	 */
	public IStatus validateEdit() {
		IValidateEditContext validator = (IValidateEditContext) UIContextDetermination.createInstance(IValidateEditContext.CLASS_KEY);
		return validator.validateState(getArtifactEditModel());
	}

	/**
	 * Save only if necessary. If typically a save would not occur because this edit model is
	 * shared, the user will be prompted using the @operationHandler. 
	 * If the prompt returns true (the user wants to save) and the model is not shared, 
	 * the entire edit model will be saved. You may pass in a boolean <code>wasDirty</code> to
	 * indicate whether this edit model was dirty prior to making any changes and
	 * calling this method. {@link EditModel#isDirty()}
	 */
	public void saveIfNecessaryWithPrompt(IProgressMonitor monitor, IOperationHandler operationHandler, boolean wasDirty) {

		if (shouldSave(operationHandler, wasDirty))
			saveIfNecessary(monitor);
		else
			handleSaveIfNecessaryDidNotSave(monitor);
	}

	/**
	 * Default is to do nothing. This method is called if a saveIfNecessary or
	 * saveIfNecessaryWithPrompt determines not to save. This provides subclasses with an
	 * opportunity to do some other action.
	 */
	private void handleSaveIfNecessaryDidNotSave(IProgressMonitor monitor) {
		// do nothing
	}

	/**
	 * Should the resources be saved.
	 */
	private boolean shouldSave(IOperationHandler operationHandler, boolean wasDirty) {
		return !wasDirty ? shouldSave() : shouldSave(operationHandler);
	}

	/**
	 * Prompt for a save.
	 */
	private boolean promptToSave(IOperationHandler operationHandler) {
		if (operationHandler == null)
			return false;
		return operationHandler.canContinue(EMFWorkbenchResourceHandler.getString("The_following_resources_ne_UI_"), getArtifactEditModel().getResourceURIs(true)); //$NON-NLS-1$ = "The following resources need to be saved but are currently shared, do you want to save now?"
	}

	/**
	 * Should the resources be saved.
	 */
	private boolean shouldSave(IOperationHandler operationHandler) {
		return shouldSave() || promptToSave(operationHandler);
	}

	/**
	 * Should the resources be saved.
	 */
	private boolean shouldSave() {
		return !isReadOnly() && isArtifactEditModelSelfManaged;
	}

	/**
	 * <p>
	 * Clients must call the following method when they have finished using the model, even if the
	 * ArtifactEdit instance facade was created as read-only.
	 * </p>
	 * 
	 * @see org.eclipse.wst.common.componentcore.IEditModelHandler#dispose()
	 */
	public void dispose() {
		if(isBinary()){
			binaryComponentHelper.dispose();
		} else if (isArtifactEditModelSelfManaged && artifactEditModel != null)
			artifactEditModel.releaseAccess(this);
	}

	/**
	 * <p>
	 * Returns the root object for read or write access (depending on how the current ArtifactEdit
	 * was loaded).
	 * </p>
	 * 
	 * @return The root object of the underlying model
	 */
	public EObject getContentModelRoot() {
		if(isBinary())
		   return binaryComponentHelper.getPrimaryRootObject();
		if (artifactEditModel!=null)
			return artifactEditModel.getPrimaryRootObject();
		return null;
	}

	/**
	 * <p>
	 * Add a listener to track lifecylce events from the underlying EditModel.
	 * </p>
	 * 
	 * @param listener
	 *            A non-null EditModelListener
	 */
	public void addListener(EditModelListener listener) {
		if(isBinary()){
			
		} else {
			if (artifactEditModel!=null && listener!=null)
				artifactEditModel.addListener(listener);
		}
	}

	/**
	 * <p>
	 * Remove the supplied listener
	 * </p>
	 * 
	 * @param listener
	 *            A non-null EditModelListener
	 *           
	 */
	public void removeListener(EditModelListener listener) {
		if(isBinary()){
		} else if (artifactEditModel!=null && !artifactEditModel.isDisposed()) {
			artifactEditModel.removeListener(listener);
		}
	}

	/**
	 * <p>
	 * This method may be removed soon. Avoid adding dependencies to it.
	 * </p>
	 * <p>
	 * This method is considered internal and not published as API.
	 * </p>
	 * @param editModel
	 * @return
	 */
	public boolean hasEditModel(EditModel editModel) {
		if(isBinary()){
			return false;
		}
		return artifactEditModel == editModel;
	}
	/**
	 * 
	 * @return IProject - returns the project of the underlying workbench component.
	 */
	public IProject getProject() {
		if(isBinary()){
			return null;
		}
		return project;
	}
	/**
	 * 
	 * @return IVirtualComponent - returns the underlying workbench component.
	 */
	public IVirtualComponent getComponent() {
		if(isBinary()){
			return binaryComponentHelper.getComponent();
		}
		return getArtifactEditModel().getVirtualComponent();
	}

	/**
	 * @return The underlying managed edit model
	 */
	protected ArtifactEditModel getArtifactEditModel() {
		if(isBinary()){
			throwAttemptedBinaryEditModelAccess();
		}
		return artifactEditModel;
	}
	
	protected BinaryComponentHelper getBinaryComponentHelper() {
		return binaryComponentHelper;
	}
	
	/**
	 * @return The EMF command stack managed by the underlying editmodel
	 */
	public CommandStack getCommandStack() {
		if(isBinary()){
			return new BasicCommandStack();
		}
		return artifactEditModel.getCommandStack();
	}
	/**
	 * 
	 * @deprecated Use ((ArtifactEditModel)getAdapter(ArtifactEditModel.ADAPTER_TYPE)).deleteResource(aResource);
	 */
	public void deleteResource(Resource aResource) {
		if(isBinary()){
			throwAttemptedBinaryEditModelAccess();
		}
		artifactEditModel.deleteResource(aResource);
	}
	/**
	 * @return The isDirty flag based the underlying editmodel's list of resources.
	 */
	public boolean isDirty() {
		if(isBinary()){
			return false;
		}
		return artifactEditModel.isDirty();
	}

	private void throwAttemptedReadOnlyModification() {
		throw new IllegalStateException("Attempt to modify an ArtifactEdit instance facade that was loaded as read-only.");
	}

	protected void throwAttemptedBinaryEditModelAccess() {
		throw new IllegalStateException("Attempt to modify an ArtifactEdit instance facade that was loaded as binary.");
	}
	
	public boolean isReadOnly() {
		return isReadOnly;
	}

	public boolean isBinary() {
		return isBinary;
	}
	
	/**
	 * Force all of the known resource URIs to be loaded
	 * if they are not already.
	 */
	public void forceLoadKnownResources() {
		if(isBinary()){
		
		} else {
			List uris = getArtifactEditModel().getKnownResourceUris();
			URI uri = null;
			for (int i = 0; i < uris.size(); i++) {
				uri = (URI) uris.get(i);
				getArtifactEditModel().getResource(uri);
			}
		}
	}
	
	/**
	 * Return a Resource for @aUri.
	 * @deprecated Use ((ArtifactEditModel)getAdapter(ArtifactEditModel.ADAPTER_TYPE)).getResource(aResource);
	 */
	public Resource getResource(URI aUri) {
		if(isBinary()){
			return binaryComponentHelper.getResource(aUri);
		}
		return getArtifactEditModel().getResource(aUri);
	}

	public Object getAdapter(Class adapterType) {
		if (adapterType == ArtifactEditModel.class)
			return getArtifactEditModel();
		return Platform.getAdapterManager().getAdapter(this, adapterType);
	}

	public void commandStackChanged(EventObject event) {
		getArtifactEditModel().commandStackChanged(event);
	}
}
