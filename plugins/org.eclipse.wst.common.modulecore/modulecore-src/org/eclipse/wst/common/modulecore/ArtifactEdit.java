/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.modulecore;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;

/**
 * <p>
 * Provides a Facade pattern for accessing content models for Web Tools Platform flexible modules.
 * </p>
 * <p>
 * ArtifactEdit hides the management of accessing edit models ({@see ArtifactEditModel})
 * correctly. Each project may have multiple ({@see ArtifactEditModel})s depending on the number
 * of modules contained by the project. Clients should use ArtifactEdit or an appropriate 
 * subclass when working with the content models of WTP modules. 
 * </p>
 * 
 * <p>
 * Each ArtifactEdit instance facade is designed to manage the Edit Model lifecycle for clients.
 * However, while each ArtifactEdit is designed to be passed around as needed, clients must enforce
 * the ArtifactEdit lifecycle. The most common method of acquiring a ArtifactEdit instance facade is to
 * use {@see #getArtifactEditForRead(WorkbenchModule)}or {@see #getModuleEditModelForWrite(WorkbenchModule, Object)}.
 * </p>
 * <p>
 * When clients have concluded their use of the instance, <b>clients must call {@see #dispose()}
 * </b>.
 * </p>
 * 
 * <p>
 * The following class is experimental until fully documented.
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
	 * {@see WorkbenchModule}. Instances of ArtifactEdit that are returned through this method must be 
	 * {@see #dispose()}ed of when no longer in use.
	 * </p>
	 * <p>
	 * Use to acquire an ArtifactEdit facade for a specific {@see WorkbenchModule}that will not be
	 * used for editing. Invocations of any save*() API on an instance returned from this method
	 * will throw exceptions.
	 * </p>
	 * <p>
	 * <b>The following method may return null. </b>
	 * </p>
	 * 
	 * @param aModule
	 *            A valid {@see WorkbenchModule}with a handle that resolves to an accessible
	 *            project in the workspace
	 * @return An instance of ArtifactEdit that may only be used to read the underlying content model
	 */
	public static ArtifactEdit getArtifactEditForRead(WorkbenchModule aModule) {
		try {
			IProject project = ModuleCore.getContainingProject(aModule.getHandle());
			if (project.isAccessible()) {
				ModuleCoreNature nature = ModuleCoreNature.getModuleCoreNature(project);
				return new ArtifactEdit(nature, aModule, true);
			}
		} catch (UnresolveableURIException uue) {
		}
		return null;
	}

	/**
	 * <p>
	 * Returns an instance facade to manage the underlying edit model for the given
	 * {@see WorkbenchModule}. Instances of ArtifactEdit that are returned through this method must be 
	 * {@see #dispose()}ed of when no longer in use.
	 * </p>
	 * <p>
	 * Use to acquire an ArtifactEdit facade for a specific {@see WorkbenchModule}that will be
	 * used for editing. 
	 * </p>
	 * <p>
	 * <b>The following method may return null. </b>
	 * </p>
	 * 
	 * @param aModule
	 *            A valid {@see WorkbenchModule}with a handle that resolves to an accessible
	 *            project in the workspace 
	 * @return An instance of ArtifactEdit that may be used to modify and persist changes to the underlying content model
	 */	
	public static ArtifactEdit getArtifactEditForWrite(WorkbenchModule aModule) {
		try {
			IProject project = ModuleCore.getContainingProject(aModule.getHandle());
			if (project.isAccessible()) {
				ModuleCoreNature nature = ModuleCoreNature.getModuleCoreNature(project);
				return new ArtifactEdit(nature, aModule, false);
			}
		} catch (UnresolveableURIException uue) {
		}
		return null;
	}
	

	/**
	 * <p>
	 * Creates an instance facade for the given {@see ArtifactEditModel}
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
	 * Creates an instance facade for the given {@see ArtifactEditModel}
	 * </p>
	 * 
	 * @param aNature
	 *            A non-null {@see ModuleCoreNature}for an accessible project
	 * @param aModule
	 *            A non-null {@see WorkbenchModule}pointing to a module from the given
	 *            {@see ModuleCoreNature}
	 */
	public ArtifactEdit(ModuleCoreNature aNature, WorkbenchModule aModule, boolean toAccessAsReadOnly) {
		if(toAccessAsReadOnly)
			artifactEditModel = aNature.getArtifactEditModelForRead(aModule.getHandle(), this);
		else 
			artifactEditModel = aNature.getArtifactEditModelForWrite(aModule.getHandle(), this);
		isReadOnly = toAccessAsReadOnly;
		isArtifactEditModelSelfManaged = true;
	}

	
	/**
	 * <p>
	 * Force a save of the underlying model. The following method should be used with care. Unless
	 * required, use {@see #saveIfNecessary(IProgressMonitor) instead.
	 * </p>
	 * 
	 * @see org.eclipse.wst.common.modulecore.IEditModelHandler#save()
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
	 * @see org.eclipse.wst.common.modulecore.IEditModelHandler#saveIfNecessary()
	 * @throws IllegalStateException
	 *             If the ModuleCore object was created as read-only
	 */
	public void saveIfNecessary(IProgressMonitor aMonitor) {
		if (isReadOnly)
			throwAttemptedReadOnlyModification();
		artifactEditModel.saveIfNecessary(aMonitor, this);
	}

	/**
	 * <p>
	 * Clients must call the following method when they have finished using the model, even if the
	 * ArtifactEdit instance facade was created as read-only.
	 * </p>
	 * 
	 * @see org.eclipse.wst.common.modulecore.IEditModelHandler#dispose()
	 */
	public void dispose() {
		if (isArtifactEditModelSelfManaged)
			artifactEditModel.releaseAccess(this);
	}

	/**
	 * <p>
	 * Returns the root object for read or write access (depending on how the current ArtifactEdit was loaded).
	 * </p>
	 * 
	 * @return The root object of the underlying model
	 */
	public EObject getContentModelRoot() {
		return artifactEditModel.getPrimaryRootObject();
	}

	/**
	 * 
	 * @return The underlying managed edit model 
	 */
	protected ArtifactEditModel getArtifactEditModel() {
		return artifactEditModel;
	}
	
	private void throwAttemptedReadOnlyModification() {
		throw new IllegalStateException("Attempt to modify an ArtifactEdit instance facade that was loaded as read-only.");
	}
}