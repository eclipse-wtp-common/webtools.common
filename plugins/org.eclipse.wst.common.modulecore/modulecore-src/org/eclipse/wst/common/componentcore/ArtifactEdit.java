/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.componentcore;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jem.internal.util.emf.workbench.nls.EMFWorkbenchResourceHandler;
import org.eclipse.wst.common.componentcore.internal.ArtifactEditModel;
import org.eclipse.wst.common.componentcore.internal.ComponentType;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.internal.resources.ComponentHandle;
import org.eclipse.wst.common.frameworks.internal.operations.IOperationHandler;
import org.eclipse.wst.common.internal.emfworkbench.edit.EditModelRegistry;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModel;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModelListener;
import org.eclipse.wst.common.internal.emfworkbench.integration.IEditModelFactory;

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
 */
public class ArtifactEdit implements IEditModelHandler {

	public static final Class ADAPTER_TYPE = ArtifactEdit.class;
	private final ArtifactEditModel artifactEditModel;
	private boolean isReadOnly;
	private boolean isArtifactEditModelSelfManaged;

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
	public static ArtifactEdit getArtifactEditForRead(WorkbenchComponent aModule) {
		if (isValidEditableModule(aModule)) {
			IProject project = StructureEdit.getContainingProject(aModule);
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
	 * 
	 * @param aModule
	 *            A valid {@see WorkbenchComponent}&nbsp;with a handle that resolves to an
	 *            accessible project in the workspace
	 * @return An instance of ArtifactEdit that may be used to modify and persist changes to the
	 *         underlying content model
	 */
	public static ArtifactEdit getArtifactEditForWrite(WorkbenchComponent aModule) {
		if (isValidEditableModule(aModule)) {
			IProject project = StructureEdit.getContainingProject(aModule);
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
	public static ArtifactEdit getArtifactEditForRead(ComponentHandle aHandle) {
		ArtifactEdit artifactEdit = null;
		try {
			artifactEdit = new ArtifactEdit(aHandle, true);
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
	public static ArtifactEdit getArtifactEditForWrite(ComponentHandle aHandle) {
		ArtifactEdit artifactEdit = null;
		try {
			artifactEdit = new ArtifactEdit(aHandle, false);
		} catch (IllegalArgumentException iae) {
			artifactEdit = null;
		}
		return artifactEdit;
	}

	/**
	 * @param module
	 *            A {@see WorkbenchComponent}
	 * @return True if the supplied module has a moduleTypeId which has a defined
	 *         {@see IEditModelFactory}&nbsp;and is contained by an accessible project
	 */
	public static boolean isValidEditableModule(WorkbenchComponent aModule) {
		/* The ComponentType must be non-null, and the moduleTypeId must be non-null */
		ComponentType moduleType = aModule.getComponentType();
		if (moduleType == null || moduleType.getComponentTypeId() == null)
			return false;
		if (aModule.getHandle() == null)
			return false;
		/* and the containing project must be resolveable and accessible */
		IProject project = StructureEdit.getContainingProject(aModule);
		if (project == null || !project.isAccessible())
			return false;
		/* and an edit model factory must be defined for the module type */
		IEditModelFactory factory = EditModelRegistry.getInstance().findEditModelFactoryByKey(moduleType.getComponentTypeId());
		if (factory == null)
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
	public ArtifactEdit(ModuleCoreNature aNature, WorkbenchComponent aModule, boolean toAccessAsReadOnly) {
		if (toAccessAsReadOnly)
			artifactEditModel = aNature.getArtifactEditModelForRead(aModule.getHandle(), this);
		else
			artifactEditModel = aNature.getArtifactEditModelForWrite(aModule.getHandle(), this);
		isReadOnly = toAccessAsReadOnly;
		isArtifactEditModelSelfManaged = true;
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
	public ArtifactEdit(ComponentHandle aHandle, boolean toAccessAsReadOnly) throws IllegalArgumentException {

		if (aHandle.getProject() == null || !aHandle.getProject().isAccessible())
			throw new IllegalArgumentException("Invalid project specified by handle: " + aHandle.getProject());

		ModuleCoreNature nature = ModuleCoreNature.getModuleCoreNature(aHandle.getProject());

		if (nature == null)
			throw new IllegalArgumentException("Project does not have ModuleCoreNature." + aHandle.getProject());

		StructureEdit moduleCore = null;
		URI componentURI = null;
		try {
			moduleCore = StructureEdit.getStructureEditForRead(aHandle.getProject());
			if (moduleCore == null)
				throw new IllegalArgumentException("Invalid component handle: " + aHandle);
			WorkbenchComponent component = moduleCore.findComponentByName(aHandle.getName());
			if (component == null)
				throw new IllegalArgumentException("Invalid component handle: " + aHandle);
			if (!isValidEditableModule(component))
				throw new IllegalArgumentException("Invalid component handle: " + aHandle);
			componentURI = component.getHandle();
		} finally {
			if (moduleCore != null)
				moduleCore.dispose();
		}


		if (toAccessAsReadOnly)
			artifactEditModel = nature.getArtifactEditModelForRead(componentURI, this);
		else
			artifactEditModel = nature.getArtifactEditModelForWrite(componentURI, this);
		isReadOnly = toAccessAsReadOnly;
		isArtifactEditModelSelfManaged = true;
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
		if (isReadOnly)
			throwAttemptedReadOnlyModification();
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
		if (isReadOnly)
			throwAttemptedReadOnlyModification();
		artifactEditModel.saveIfNecessary(aMonitor, this);
	}

	/**
	 * Save only if necessary. If typically a save would not occur because this edit model is
	 * shared, the user will be prompted using the
	 * 
	 * @operationHandler. If the prompt returns true (the user wants to save) the entire edit model
	 *                    will be saved. You may pass in a boolean <code>wasDirty</code> to
	 *                    indicate whether this edit model was dirty prior to making any changes and
	 *                    calling this method. {@link EditModel#isDirty()}
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
		return !isReadOnly && isArtifactEditModelSelfManaged;
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
		if (isArtifactEditModelSelfManaged)
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
		return artifactEditModel.getPrimaryRootObject();
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
		artifactEditModel.addListener(listener);
	}

	/**
	 * <p>
	 * Remove the supplied listener
	 * </p>
	 * 
	 * @param listener
	 *            A non-null EditModelListener
	 */
	public void removeListener(EditModelListener listener) {
		artifactEditModel.removeListener(listener);
	}

	/**
	 * <p>
	 * This method may be removed soon. Avoid adding dependencies to it.
	 * </p>
	 * 
	 * @param editModel
	 * @return
	 */
	public boolean hasEditModel(EditModel editModel) {
		return artifactEditModel == editModel;
	}
	/**
	 * 
	 * @return ComponentHandle - returns the handle of the underlying workbench component.
	 */
	public ComponentHandle getComponentHandle() {
		
		return getArtifactEditModel().getComponentHandle();
	}

	/**
	 * @return The underlying managed edit model
	 */
	protected ArtifactEditModel getArtifactEditModel() {
		return artifactEditModel;
	}
	
	/**
	 * @return The EMF command stack managed by the underlying editmodel
	 */
	public CommandStack getCommandStack() {
		return artifactEditModel.getCommandStack();
	}
	/**
	 * @return The isDirty flag based the underlying editmodel's list of resources.
	 */
	public boolean isDirty() {
		return artifactEditModel.isDirty();
	}

	private void throwAttemptedReadOnlyModification() {
		throw new IllegalStateException("Attempt to modify an ArtifactEdit instance facade that was loaded as read-only.");
	}
}