/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal.operations;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.validation.internal.RegistryConstants;
import org.eclipse.wst.validation.internal.Tracing;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;

/**
 * Abstract base class for registration of symbolic model names, and also provides the mechanism for
 * registering the load model method which loads a specific type of MOF model, as identified by the
 * symbolic model name.
 */
public class WorkbenchContext implements IWorkbenchContext {
	private IProject 	_project;
	private Hashtable<String,Method> 	_modelRegistry;
	private int 		_ruleGroup = RegistryConstants.ATT_RULE_GROUP_DEFAULT;
	public List<String> validationFileURIs; 
	public static final String GET_PROJECT_FILES = "getAllFiles"; //$NON-NLS-1$
	public static final String GET_FILE = "getFile"; //$NON-NLS-1$
	public static final String VALIDATION_MARKER = "com.ibm.etools.validation.problemmarker"; //$NON-NLS-1$
	public static final String VALIDATION_MARKER_OWNER = "owner";  //$NON-NLS-1$

	public WorkbenchContext() {
		super();
		_modelRegistry = new Hashtable<String, Method>();

		registerModel(IRuleGroup.PASS_LEVEL, "loadRuleGroup"); //$NON-NLS-1$
		
		//the following will register the helper's symbolic methods
	    Class [] args = new Class[1] ;
	    args[0] = String.class ;  // a string argument denoting a specific JSP.
		registerModel(GET_FILE, "getFile", args);//$NON-NLS-1$
		registerModel(GET_PROJECT_FILES, "getFiles", args);//$NON-NLS-1$
	}

	/**
	 * When the validation is complete, this method will be called so that the IWorkbenchContext can
	 * clean up any resources it allocated during the validation.
	 * 
	 * If the cleanup is a long-running operation, subtask messages should be sent to the IReporter.
	 */
	public void cleanup(WorkbenchReporter reporter) {
		// Default: nothing to clean up
	}

	/**
	 * When the IProject is closing, perform any cleanup that needs to be done. When this method is
	 * called, if the helper has cached any resources from the IProject, it should release those
	 * resources. If you do not allocate resources in your helper, then this method should be a
	 * no-op.
	 */
	public void closing() {
		closing(getProject());
	}

	/**
	 * When the IProject is closing, perform any cleanup that needs to be done. When this method is
	 * called, if the helper has cached any resources from the IProject, it should release those
	 * resources. If you do not allocate resources in your helper, then this method should be a
	 * no-op.
	 * 
	 * @deprecated Override closing() instead, and use getProject()
	 */
	public void closing(IProject project) {
		//do nothing
	}

	/**
	 * When the IProject is being deleted, perform any cleanup that needs to be done. When this
	 * method is called, if the helper has cached any resources from the IProject, it should release
	 * those resources. If you do not allocate resources in your helper, then this method should be
	 * a no-op.
	 */
	public void deleting() {
		deleting(getProject());
	}

	/**
	 * When the IProject is being deleted, perform any cleanup that needs to be done. When this
	 * method is called, if the helper has cached any resources from the IProject, it should release
	 * those resources. If you do not allocate resources in your helper, then this method should be
	 * a no-op.
	 * 
	 * @deprecated Override deleting() instead, and use getProject()
	 */
	public void deleting(IProject project) {
		//do nothing
	}

	/**
	 * Returns the IPath of a resource, relative to the container. If the IResource is not a member
	 * of the container, return null. This method should be useful for implementors of this class;
	 * specifically, in their getPortableName method.
	 */
	public static String getContainerRelativePath(String fullPath, IContainer container) {
		if ((fullPath == null) || (container == null))
			return null;
		IPath relPath = getContainerRelativePath(new Path(fullPath), container);
		if (relPath != null)
			return relPath.toString();
		return null;
	}

	/**
	 * Returns the IPath of a resource, relative to the container. If the IResource is not a member
	 * of the container, return null. This method should be useful for implementors of this class;
	 * specifically, in their getPortableName method.
	 */
	public static IPath getContainerRelativePath(IResource resource, IContainer container) {
		if ((resource == null) || (container == null)) {
			return null;
		}

		IResource foundResource = null;
		IPath relPath = getContainerRelativePath(resource.getFullPath(), container);
		if (relPath != null) {
			// if relPath is null, the resource is not a direct member of the container
			try {
				if (!resource.exists()) { // findMember won't work
					if (resource instanceof IFile) {
						foundResource = container.getFile(relPath);
					} else if (resource instanceof IFolder) {
						foundResource = container.getFolder(relPath);
					}
				} else {
					foundResource = container.findMember(relPath, true); // true means include
					// phantom resources
					if ((foundResource != null) && !foundResource.exists()) {
						foundResource = null;
					}
				}
			} catch (IllegalArgumentException e) {
				foundResource = null;
				ValidationPlugin.getPlugin().handleException(e);
			}
		}

		if (foundResource == null) {
			return null;
		}

		// file has been found
		int matchingFirstSegments = container.getProjectRelativePath().matchingFirstSegments(resource.getProjectRelativePath());
		return resource.getProjectRelativePath().removeFirstSegments(matchingFirstSegments);
	}

	/**
	 * Given an IPath, if the IPath is absolute, and is a part of the IContainer, return an IPath
	 * which is relative to the container. If the IPath is not part of the IContainer, return null.
	 */
	public static IPath getContainerRelativePath(IPath path, IContainer container) {
		if ((path == null) || (container == null)) {
			return null;
		}

		if (path.isAbsolute()) {
			// Is the path part of the IContainer?
			int matchingFirstSegments = path.matchingFirstSegments(container.getFullPath());
			if ((matchingFirstSegments > 0) && (matchingFirstSegments == container.getFullPath().segmentCount())) {
				// part of the IContainer
				return path.removeFirstSegments(matchingFirstSegments);
			}
			// not part of the IContainer
			return null;
		}
		// path is relative
		// Is the path part of the IContainer?
		//TODO don't have time to implement this now, but should in future. - Ruth
		return null;
	}

	/**
	 * Given an IMessage's target object, return a string which identifies the object, so that the
	 * user can locate it.
	 */
	public String getDescription(Object object) {
		if (object == null) {
			return ""; //$NON-NLS-1$
		}

		if (object instanceof WorkbenchFileDelta) {
			WorkbenchFileDelta wfd = (WorkbenchFileDelta) object;
			if (wfd.getResource() != null) {
				// resource will be null if WorkbenchFileDelta was constructed from an Object
				// instead of an IResource
				return wfd.getResource().getFullPath().toString();
			}
		}

		return object.toString();
	}

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
	 */
	public IFile getFile(Object obj) {
		return null;
	}

	public IResource getResource(Object obj) {
		if (obj == null) {
			return null;
		}

		IResource res = null;
		if (obj instanceof WorkbenchFileDelta) {
			// resource may be null if WorkbenchFileDelta was constructed from an Object instead of
			// an IResource
			res = ((WorkbenchFileDelta) obj).getResource();
		} else if (obj instanceof IResource) {
			res = (IResource) obj;
		}

		if ((res == null) || (!res.exists())) {
			return getFile(obj);
		}

		return res;
	}

	/**
	 * If the IProject is associated with an EJBNatureRuntime, return the IJavaProject which
	 * represents it.
	 */
//	public static IJavaProject getJavaProject(IProject project) {
//		if (project == null) {
//			return null;
//		}
//		return JavaCore.create(project);
//	}

	/**
	 * Given an IMessage's target object, return the line number, of the IFile, which the target
	 * object represents. If the object is null, or if access to line numbers is not possible,
	 * return "0".
	 */
	public int getLineNo(Object object) {
		IResourceUtil util = ValidatorManager.getResourceUtil();
		if (util == null) {
			return IMessage.LINENO_UNSET;
		}
		try {
			return util.getLineNo(object);
		} catch (Exception e) {
			ValidationPlugin.getPlugin().handleException(e);
			return IMessage.LINENO_UNSET;
		}

	}
	
	/**
	 * Given an IMessage's target object, return the line number, of the IFile, which the target
	 * object represents, if possible. If the object is null, or if access to line numbers is not
	 * possible, return a text description of the location.
	 * 
	 * This method will be called whether or not the IResource is an IFile, IFolder, or IProject.
	 * Line numbers are valid only for IFile types; if the resource is not an IFile, then a text
	 * description of the location must be returned.
	 */
	public String getLocation(Object object) {
		IResource res = getResource(object);
		if ((res == null) || !(res instanceof IFile))
			// return a text description
			return getDescription(object);
		// default to a line number, if it's available. Else, use a text description.
		int lineNumber = getLineNo(object);
		if (lineNumber == IMessage.LINENO_UNSET) {
			return getDescription(object);
		}
		// return the line number
		return String.valueOf(lineNumber);
	}

	/**
	 * Given a name of a load method, and the types of parameters it takes (this method is always
	 * called with null as the second parameter), return the java.lang.reflect.Method which
	 * represents the load method.
	 */
	private final Method getMethod(String methodName, Class[] parmTypes) {
		Method m = null;
		try {
			m = getClass().getMethod(methodName, parmTypes);
		} catch (NoSuchMethodException e) {
			ValidationPlugin.getPlugin().handleException(e);
			return null;
		}

		return m;
	}

	/**
	 * @see IWorkbenchContext.getPortableName(IResource)
	 */
	public String getPortableName(IResource resource) {
		return resource.getFullPath().toString();
	}

	/**
	 * Return the IProject which is about to be validated. Each IWorkbenchContext knows how to
	 * traverse a certain type of IProject, for example, an EJB project or a web project.
	 */
	public final IProject getProject() {
		return _project;
	}
	
	  /**
	   * Get the IFile for the given filename.
	   * 
	   * @param filename The name of the file to retrieve.
	   * @return An IFile representing the file specified or null if it can't be resolved.
	   */
	  public IFile getFile(String filename)
	  {
	    //    System.out.println("file name = " + filename);
	    IResource res = getProject().findMember(filename, true); // true means include phantom resources
	    if (res instanceof IFile) 
	    {
	      return (IFile) res;
	    }
	    return null;
	  }
	  
	  /**
	   * Get the collection of files from the project that are relevant for the
	   * validator with the given class name.
	   * 
	   * @param validatorClassName The name of the validator class.
	   * @return The collection of files relevant for the validator class specified.
	   */
	  public Collection<IFile> getFiles(String validatorClassName)
	  {
	    IProject project = getProject();
	    List<IFile> files = new ArrayList<IFile>();
	    getFiles(files, project, validatorClassName);
	    return files;
	  }

	  /**
	   * Get the collection of files from the project that are relevant for the
	   * validator with the given class name.
	   * 
	   * @param files The files relevant for the class name.
	   * @param resource The resource to look for files in.
	   * @param validatorClassName The name of the validator class.
	   */
	  protected void getFiles(Collection<IFile> files, IContainer resource, String validatorClassName)
	  {
	    try
	    {
	      IResource [] resourceArray = resource.members(false);
	      for (int i=0; i<resourceArray.length; i++)
	      {       
	        if (ValidatorManager.getManager().isApplicableTo(validatorClassName, resourceArray[i])) 
	        {
	          if (resourceArray[i] instanceof IFile) 
				  files.add((IFile)resourceArray[i]);
	        }
	        if (resourceArray[i].getType() == IResource.FOLDER)
	         getFiles(files,(IContainer)resourceArray[i], validatorClassName) ;
	      }
	    }
	    catch (Exception e) {}
	  }
	  

//	/**
//	 * Return the folders (or project) which contain the .java source files.
//	 */
//	public static IContainer[] getProjectSourceContainers(IProject project) {
//		if (project == null) {
//			return NO_CONTAINERS;
//		}
//
//		IJavaProject jp = getJavaProject(project);
//		if (jp == null) {
//			return NO_CONTAINERS;
//		}
//
//		return getProjectSourceContainers(jp);
//	}

	/**
	 * Return the folders (or project) which contain the .java source files.
	 */
//	public static IContainer[] getProjectSourceContainers(IJavaProject javaProject) {
//		if (javaProject == null) {
//			return NO_CONTAINERS;
//		}
//		IProject project = javaProject.getProject();
//
//		IClasspathEntry[] classpath = null;
//		try {
//			classpath = javaProject.getResolvedClasspath(true); // true means ignore unresolved
//			// (missing) variables, instead of
//			// throwing an exception
//		} catch (JavaModelException exc) {
//			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
//			if (logger.isLoggingLevel(Level.SEVERE)) {
//				LogEntry entry = ValidationPlugin.getLogEntry();
//				entry.setSourceID("WorkbenchContext::getProjectSourceContainers(IJavaProject)"); //$NON-NLS-1$
//				entry.setTargetException(exc);
//				logger.write(Level.SEVERE, entry);
//			}
//			return NO_CONTAINERS;
//		}
//
//		if (classpath == null) {
//			return NO_CONTAINERS;
//		}
//
//		// Traverse the classpath, and calculate a list of just the
//		// IFolders and IProjects (i.e., IContainers) which contain source
//		IContainer[] icontainers = new IContainer[classpath.length];
//		int validCount = 0;
//		for (int i = 0; i < classpath.length; i++) {
//			IClasspathEntry entry = classpath[i];
//			if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
//				IPath entryPath = entry.getPath();
//				IPath relEntryPath = getContainerRelativePath(entryPath, project);
//				if (relEntryPath == null) {
//					// classpath entry refers to something which doesn't exist
//					continue;
//				}
//
//				IResource outputResource = project.findMember(relEntryPath);
//				if (outputResource == null) {
//					// classpath entry refers to something which doesn't exist
//					continue;
//				}
//
//				if (outputResource instanceof IContainer) {
//					icontainers[validCount++] = (IContainer) outputResource;
//				}
//			}
//		}
//
//		//  following line causes a ClassCastException, so construct an array of IContainers
//		// explicitly
//		//	return (IContainer[])icontainers.toArray();
//		IContainer[] containers = new IContainer[validCount];
//		System.arraycopy(icontainers, 0, containers, 0, validCount);
//		return containers;
//	}

	public int getRuleGroup() {
		return _ruleGroup;
	}

	/**
	 * This method can be overriden by AWorkbenchHelpers, if they wish to perform some
	 * initialization once the IProject is set. Default is to do nothing.
	 * 
	 * For example, if this IWorkbenchContext delegates to another IWorkbenchContext, then that
	 * IWorkbenchContext's setProject() method should be called here.
	 */
	public void initialize() {
		//do nothing
	}

	/**
	 * Return true if the given resource is in an IJavaProject, and if it is in one of the source
	 * folders in the IJavaProject. If the project's source folder happens to be the project's
	 * output folder too, this method will return true. If res is null, or is not found in one of
	 * the source containers, this method will return false.
	 */
//	public boolean isInJavaSourcePath(IResource res) {
//		if (res == null) {
//			return false;
//		}
//
//		IContainer[] containers = getProjectSourceContainers(res.getProject());
//		for (int c = 0; c < containers.length; c++) {
//			IPath resourcePath = getContainerRelativePath(res, containers[c]);
//			if (resourcePath != null) {
//				// file has been found
//				return true;
//			}
//		}
//
//		return false;
//	}

	/**
	 * Return true if the given symbolic name is registered, and can be loaded by WorkbenchContext's
	 * "loadModel" method. For further information, see the comment above WorkbenchContext's
	 * "registerModel" method.
	 */
	public final boolean isRegistered(String symbolicName) {
		if (symbolicName == null)return false;
		return _modelRegistry.containsKey(symbolicName);
	}

	/**
	 * Given a symbolic name, load the MOF model which is identified by that name.
	 * 
	 * For support of dynamic symbolic names, such as file names, read the comment above the
	 * registerModel method.
	 * 
	 * @see registerModel
	 */
	public Object loadModel(String symbolicName) {
		return loadModel(symbolicName, null);
	}

	/**
	 * Given a symbolic name, load the MOF model which is identified by that name.
	 * 
	 * For support of dynamic symbolic names, such as file names, read the comment above the
	 * registerModel method.
	 * 
	 * @see registerModel
	 */
	public Object loadModel(String symbolicName, Object[] parms) {
		try {
			Method loader = _modelRegistry.get(symbolicName);
			if (loader == null)return null;

			return loader.invoke(this, parms);
		} catch (IllegalAccessException e) {
			ValidationPlugin.getPlugin().handleException(e);
			return null;
		} catch (InvocationTargetException e) {
			ValidationPlugin.getPlugin().handleException(e);
			ValidationPlugin.getPlugin().handleException(e.getTargetException());
			return null;
		} catch (NullPointerException e) {
			ValidationPlugin.getPlugin().handleException(e);
			return null;
		} catch (ExceptionInInitializerError e) {
			ValidationPlugin.getPlugin().handleException(e);
			return null;
		}
	}

	public Object loadRuleGroup() {
		return new Integer(getRuleGroup()); // have to wrap the int in an Integer because can't cast
		// an int to an Object
	}

	/**
	 * This method should be used only by the validation framework. This method will be called by
	 * ValidationOperation, before the IValidator's validate method is called, to set the group of
	 * rules which the IValidator should check.
	 */
	public void setRuleGroup(int rg) {
		_ruleGroup = rg;
	}

	/**
	 * Register a load method for a symbolic name. A load method is defined as a method which takes
	 * no parameters and returns a RefObject.
	 * 
	 * Every subclass of WorkbenchContext, for every static type of symbolic name which it supports,
	 * should call registerModel. For IWorkbenchHelpers which support dynamic symbolic names, such
	 * as file names, each IWorkbenchContext should override the "loadModel" method. Their
	 * "loadModel" should first call this class' "isRegistered" method to see if they're dealing
	 * with a static symbolic name, or a dynamic one. If the symbolic name is registered, the
	 * child's "loadModel" method should just return the result of WorkbenchContext's "loadModel"
	 * method. Otherwise, it should return the result based on its own processing.
	 * 
	 * When this method is called, the load method identified by loadMethodName is located & stored
	 * for future retrieval by the "loadModel" method. When the IValidator calls "loadModel" with a
	 * symbolic name, the java.lang.reflect.Method which was loaded by this method is invoked, and
	 * the result (RefObject) returned by that method is returned by "loadModel".
	 * 
	 * symbolicName must not be null or the empty string. loadMethodName must not be null or the
	 * empty string.
	 */
	protected final void registerModel(String symbolicName, String loadMethodName) {
		registerModel(symbolicName, loadMethodName, null);
	}

	/**
	 * Register a load method for a symbolic name. A load method is defined as a method which takes
	 * no parameters and returns a RefObject.
	 * 
	 * Every subclass of WorkbenchContext, for every static type of symbolic name which it supports,
	 * should call registerModel. For IWorkbenchHelpers which support dynamic symbolic names, such
	 * as file names, each IWorkbenchContext should override the "loadModel" method. Their
	 * "loadModel" should first call this class' "isRegistered" method to see if they're dealing
	 * with a static symbolic name, or a dynamic one. If the symbolic name is registered, the
	 * child's "loadModel" method should just return the result of WorkbenchContext's "loadModel"
	 * method. Otherwise, it should return the result based on its own processing.
	 * 
	 * When this method is called, the load method identified by loadMethodName is located & stored
	 * for future retrieval by the "loadModel" method. When the IValidator calls "loadModel" with a
	 * symbolic name, the java.lang.reflect.Method which was loaded by this method is invoked, and
	 * the result (RefObject) returned by that method is returned by "loadModel".
	 * 
	 * symbolicName must not be null or the empty string. loadMethodName must not be null or the
	 * empty string.
	 */
	protected final void registerModel(String symbolicName, String loadMethodName, Class[] parms) {
		Method method = getMethod(loadMethodName, parms);
		if (method == null) {
			if (Tracing.isLogging()) {
				StringBuffer buffer = new StringBuffer("Load method "); //$NON-NLS-1$
				buffer.append(loadMethodName);
				buffer.append("("); //$NON-NLS-1$
				for (int i = 0; (parms != null) && (i < parms.length); i++) {
					buffer.append(parms[i]);
				}
				buffer.append(") must exist. " + getClass().getName() + " cannot support model " + symbolicName); //$NON-NLS-1$ //$NON-NLS-2$

				Tracing.log(buffer);
			}
		} else {
			_modelRegistry.put(symbolicName, method);
		}
	}

	/**
	 * Whether full or incremental validation is running, this method will be called, for every
	 * IResource which is filtered in by the IValidator, so that the IValidationContext can receive
	 * notification that one of the resources, which validation will run on, is being filtered in.
	 */
	public void registerResource(IResource resource) {
		// default: do nothing
	}

	/**
	 * This method is called by the Validation Framework, to initialize the IWorkbenchContext so that
	 * it can gather information from the current project.
	 * 
	 * If an IWorkbenchContext delegates some model loading to another IWorkbenchContext, this method
	 * should be overriden so that the delegatee IWorkbenchContext is initialized with the IProject.
	 */
	public final void setProject(IProject project) {
		_project = project;

		if (project != null) {
			// Project will be set back to null once the validation of the project is complete.
			initialize();
		}
	}

	/**
	 * Notifies this IWorkbenchContext that the Validation Framework is shutting down. There will be
	 * calls to closing(IProject) and possibly deleting(IProject) following this call, but the
	 * resources may already be closed by the time that those methods are called, so EVERYTHING
	 * should be cleaned up in this method. The parameter passed in is the project which is about to
	 * shut down. This method will be called once for every IProject in the workbench. The IProject
	 * may, or may not, be closed.
	 */
	public void shutdown() {
		// Default is to assume that no resources were allocated; therefore,
		// no cleanup needs to be done.
		shutdown(getProject());
	}

	/**
	 * Notifies this IWorkbenchContext that the Validation Framework is shutting down. There will be
	 * calls to closing(IProject) and possibly deleting(IProject) following this call, but the
	 * resources may already be closed by the time that those methods are called, so EVERYTHING
	 * should be cleaned up in this method. The parameter passed in is the project which is about to
	 * shut down. This method will be called once for every IProject in the workbench. The IProject
	 * may, or may not, be closed.
	 * 
	 * @deprecated Override shutdown() instead, and use getProject()
	 */
	public void shutdown(IProject project) {
		// Default is to assume that no resources were allocated; therefore,
		// no cleanup needs to be done.
	}

	public String getTargetObjectName(Object object) {
		return null;
	}

	public String[] getURIs() {
		String[] uris = new String[validationFileURIs.size()];
		validationFileURIs.toArray(uris);
		return uris;
	}

	/**
	 * @return Returns the validationFileURIs.
	 */
	public List<String> getValidationFileURIs() {
		return validationFileURIs;
	}

	/**
	 * @param validationFileURIs The validationFileURIs to set.
	 */
	public void setValidationFileURIs(List<String> validationFileURIs) {
		this.validationFileURIs = validationFileURIs;
	}
}
