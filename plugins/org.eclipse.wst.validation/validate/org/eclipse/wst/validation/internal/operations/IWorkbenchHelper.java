/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal.operations;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.wst.validation.internal.core.IHelper;




/**
 * In the eclipse environment, not only does the IHelper need to be able to load the MOF model, it
 * also needs to load items from the eclipse workbench. This interface should be extended by
 * workbench IHelpers, so that items can be added to, and from, the task list.
 */
public interface IWorkbenchHelper extends IHelper {
	/**
	 * When the validation is complete, this method will be called so that the IWorkbenchHelper can
	 * clean up any resources it allocated during the validation.
	 * 
	 * If the cleanup is a long-running operation, subtask messages should be sent to the IReporter.
	 */
	public void cleanup(WorkbenchReporter reporter);

	/**
	 * When the IProject is closing, perform any cleanup that needs to be done. When this method is
	 * called, if the helper has cached any resources from the IProject, it should release those
	 * resources. If you do not allocate resources in your helper, then this method should be a
	 * no-op.
	 */
	public void closing();

	/**
	 * When the IProject is being deleted, perform any cleanup that needs to be done. When this
	 * method is called, if the helper has cached any resources from the IProject, it should release
	 * those resources. If you do not allocate resources in your helper, then this method should be
	 * a no-op.
	 */
	public void deleting();

	/**
	 * Given an Object, if the object has a corresponding IFile in the workbench, return the IFile.
	 * Otherwise return null.
	 * 
	 * This method is used by the WorkbenchReporter. In eclipse, in order to add or remove a task
	 * list entry, the IResource, to which the entry applies, must be identified. The IReporter
	 * interface passes in an Object in these methods:
	 * 
	 * addValidationMessage(IValidator, IMessage) // Object is a part of IMessage
	 * 
	 * removeAllMessages(IValidator, Object),
	 * 
	 * Thus, the WorkbenchReporter needs to know how, given the Object, which IFile that the Object
	 * represents in the workbench, in order to be able to add the task list entry.
	 * 
	 * If this method returns null, then the WorkbenchReporter will add the message to the IProject
	 * instead of an IFile.
	 * 
	 * @deprecated Implement getResource(Object) instead.
	 */
	public IFile getFile(Object object);

	/**
	 * Given an Object, if the object has a corresponding IResource in the workbench, return the
	 * IResourcee. Otherwise return null.
	 * 
	 * This method is used by the WorkbenchReporter. In eclipse, in order to add or remove a task
	 * list entry, the IResource, to which the entry applies, must be identified. The IReporter
	 * interface passes in an Object in these methods:
	 * 
	 * addValidationMessage(IValidator, IMessage) // Object is a part of IMessage
	 * 
	 * removeAllMessages(IValidator, Object),
	 * 
	 * Thus, the WorkbenchReporter needs to know how, given the Object, which IFile that the Object
	 * represents in the workbench, in order to be able to add the task list entry.
	 * 
	 * If this method returns null, then the WorkbenchReporter will add the message to the IProject
	 * instead of an IResource.
	 */
	public IResource getResource(Object object);

	/**
	 * Given an IMessage's target object, return the line number, of the IFile, which the target
	 * object represents, if possible. If the object is null, or if access to line numbers is not
	 * possible, return a text description of the location.
	 * 
	 * This method will be called whether or not the IResource is an IFile, IFolder, or IProject.
	 * Line numbers are valid only for IFile types; if the resource is not an IFile, then a text
	 * description of the location must be returned.
	 */
	public String getLocation(Object object);

	/**
	 * Return the name of the resource, without the project-specific information in front.
	 * 
	 * This method is used by ValidationOperation to calculate the non-environment specific names of
	 * the files. Only the IWorkbenchHelper implementation knows how much information to strip off
	 * of the IResource name. For example, if there is an EJB Project named "MyEJBProject", and it
	 * uses the default names for the source and output folders, "source" and "ejbModule",
	 * respectively, then the current implementation of EJB Helper knows how much of that structure
	 * is eclipse-specific.
	 * 
	 * Since the "source" folder contains Java source files, a portable name would be the
	 * fully-qualified name of the Java class, without the eclipse-specific project and folder names
	 * in front of the file name. The EJBHelper knows that everything up to the "source" folder, for
	 * example, can be removed, because, according to the definition of the EJB Project, everything
	 * contained in the source folder is java source code. So if there is an IResource in an EJB
	 * Project named "/MyEJBProject/source/com/ibm/myclasses/MyJavaFile.java", this method would
	 * make this name portable by stripping off the "/MyEJBProject/source", and returning
	 * "com/ibm/myclasses/MyJavaFile.java".
	 * 
	 * The output of this method is used by the ValidationOperation, when it is calculating the list
	 * of added/changed/deleted file names for incremental validation. If getPortableName(IResource)
	 * returns null, that means that the IWorkbenchHelper's implementation does not support that
	 * particular type of resource, and the resource should not be included in the array of
	 * IFileDelta objects in the IValidator's "validate" method.
	 */
	public String getPortableName(IResource resource);

	/**
	 * Return the IProject which is about to be validated. Each IWorkbenchHelper knows how to
	 * traverse a certain type of IProject, for example, an EJB project or a web project.
	 */
	public IProject getProject();

	/**
	 * When an IValidator associates a target object with an IMessage, the WorkbenchReporter
	 * eventually resolves that target object with an IResource. Sometimes more than one target
	 * object resolves to the same IResource (usually the IProject, which is the default IResource
	 * when an IFile cannot be found). This method is called, by the WorkbenchReporter, so that the
	 * WorkbenchReporter can distinguish between the IMessages which are on the same IResource, but
	 * refer to different target objects. This is needed for the removeAllMessages(IValidator,
	 * Object) method, so that when one target object removes all of its messages, that it doesn't
	 * remove another target object's messages.
	 * 
	 * This method may return null only if object is null. Otherwise, an id which can uniquely
	 * identify a particular object must be returned. The id needs to be unique only within one
	 * particular IValidator.
	 */
	public String getTargetObjectName(Object object);

	/**
	 * Whether full or incremental validation is running, this method will be called, by the
	 * Validation Framework, for every IResource which is filtered in by the IValidator, so that the
	 * IHelper can receive notification that one of the resources, which validation will run on, is
	 * being filtered in.
	 */
	public void registerResource(IResource resource);

	/**
	 * This method is called by the Validation Framework, to initialize the IWorkbenchHelper so that
	 * it can gather information from the current project.
	 */
	public void setProject(IProject project);

	/**
	 * Notifies this IWorkbenchHelper that the Validation Framework is shutting down. There will be
	 * calls to closing(IProject) and possibly deleting(IProject) following this call, but the
	 * resources may already be closed by the time that those methods are called, so EVERYTHING
	 * should be cleaned up in this method. The parameter passed in is the project which is about to
	 * shut down. This method will be called once for every IProject in the workbench. The IProject
	 * may, or may not, be closed.
	 */
	public void shutdown();
}