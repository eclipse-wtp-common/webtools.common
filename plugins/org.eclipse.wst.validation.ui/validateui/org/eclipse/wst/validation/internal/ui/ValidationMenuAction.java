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
package org.eclipse.wst.validation.internal.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jem.util.logger.LogEntry;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.wst.common.frameworks.internal.ValidationSelectionHandlerRegistryReader;
import org.eclipse.wst.common.frameworks.internal.ui.WTPUIPlugin;
import org.eclipse.wst.validation.internal.ConfigurationManager;
import org.eclipse.wst.validation.internal.ProjectConfiguration;
import org.eclipse.wst.validation.internal.ValidationRegistryReader;
import org.eclipse.wst.validation.internal.operations.EnabledIncrementalValidatorsOperation;
import org.eclipse.wst.validation.internal.operations.EnabledValidatorsOperation;
import org.eclipse.wst.validation.internal.operations.IWorkbenchContext;
import org.eclipse.wst.validation.internal.operations.ValidatorManager;
import org.eclipse.wst.validation.internal.operations.WorkbenchContext;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;
import org.eclipse.wst.validation.internal.ui.plugin.ValidationUIPlugin;

/**
 * This class implements the pop-up menu item "Run Validation" When the item is selected, this
 * action triggers a validation of the project, using all configured, enabled validators.
 */
public class ValidationMenuAction implements IViewActionDelegate {
	private ISelection _currentSelection = null;
	protected static final String SEP = "/"; //$NON-NLS-1$
	private Display _currentDisplay = null;
	private IResourceVisitor _folderVisitor = null;
	private Map _selectedResources = null;
	protected IWorkbenchContext workbenchContext;

	public ValidationMenuAction() {
		super();
		_currentDisplay = Display.getCurrent(); // cache the display before
		// this action is forked. After
		// the action is forked,
		// Display.getCurrent() returns
		// null.
		_selectedResources = new HashMap();
	}

	private Display getDisplay() {
		return (_currentDisplay == null) ? Display.getCurrent() : _currentDisplay;
	}

	/**
	 * Return the wizard's shell.
	 */
	Shell getShell() {
		Display display = getDisplay();
		Shell shell = (display == null) ? null : display.getActiveShell();
		if (shell == null && display != null) {
			Shell[] shells = display.getShells();
			if (shells.length > 0)
				shell = shells[0];
		}
		return shell;
	}

	private ISelection getCurrentSelection() {
		return _currentSelection;
	}

	/**
	 * Return a map of the selected elements. Each key of the map is an IProject, and the value is a
	 * Set of the selected resources in that project. If a project is selected, and nothing else in
	 * the project is selected, a full validation (null value) will be done on the project. If a
	 * project is selected, and some files/folders in the project are also selected, only the
	 * files/folders will be validated. If a folder is selected, all of its contents are also
	 * validated.
	 */
	private Map loadSelected(ValidateAction action, boolean refresh) {
		if (refresh) {
			// selectionChanged(IAction, ISelection) has been called. Flush the
			// existing cache of resources and
			// add just the currently selected ones.
			_selectedResources.clear();
		}
		ISelection selection = getCurrentSelection();
		if ((selection == null) || selection.isEmpty() || !(selection instanceof IStructuredSelection)) {
			return null;
		}
		Object[] elements = ((IStructuredSelection) selection).toArray();
		for (int i = 0; i < elements.length; i++) {
			Object element = elements[i];
			if (element == null) {
				continue;
			}
			addSelected(action, element);
		}
		return _selectedResources;
	}

	private void addSelected(ValidateAction action, Object selected) {
		if (selected instanceof IProject) {
			addSelected((IProject) selected);
		} else if (selected instanceof IFile) {
			addSelected((IFile) selected);
		} else if (selected instanceof IFolder) {
			addVisitor((IFolder) selected);
		} else if (isValidType(getExtendedType(selected))) {
			addSelected(action,getExtendedType(selected));
		} else {
			// Not a valid input type. Must be IProject, IJavaProject, or
			// IResource.
			// If this ValidationMenuAction is a delegate of ValidateAction, is
			// the input type recognized by the ValidateAction?
			boolean valid = false;
			if (action != null) {
				IResource[] resources = action.getResource(selected);
				if (resources != null) {
					valid = true;
					for (int i = 0; i < resources.length; i++) {
						addSelected(action, resources[i]);
					}
				}
			}
			if (!valid) {
				// Stop processing. (This allows the "Run Validation" menu item
				// to gray
				// out once at least one non-validatable element is selected.)
				_selectedResources.clear();
			}
		}
	}
	
	private Object getExtendedType(Object selected) {
		Object result = ValidationSelectionHandlerRegistryReader.getInstance().getExtendedType(selected);
		return result == null ? selected : result;
	}
	
	private boolean isValidType(Object object) {
		return object instanceof IProject || object instanceof IFile || object instanceof IFolder;
	}

	private void addSelected(IProject selected) {
		_selectedResources.put(selected, null); // whatever the values were
		// before, the entire project
		// needs to be revalidated now
	}

//	private void addSelected(IJavaProject selected) {
//		_selectedResources.put(selected.getProject(), null); // whatever the
//		// values were
//		// before, the
//		// entire project
//		// needs to be
//		// revalidated now
//	}

	void addSelected(IResource selected) {
		IProject project = selected.getProject();
		boolean added = _selectedResources.containsKey(project);
		List changedRes = null;
		if (added) {
			// If the value is null, the entire project needs to be validated
			// anyway.
			changedRes = (List) _selectedResources.get(project);
			if (changedRes == null) {
				return;
			}
		} else {
			changedRes = new ArrayList();
		}
		if (!changedRes.contains(selected)) {
			changedRes.add(selected);
			_selectedResources.put(project, changedRes);
		}
	}

	private void addVisitor(IFolder selected) {
		// add the folder and its children
		try {
			selected.accept(getFolderVisitor());
		} catch (CoreException exc) {
			Logger logger = WTPUIPlugin.getLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationUIPlugin.getLogEntry();
				entry.setSourceIdentifier("ValidationMenuAction.addSelected(IFolder)"); //$NON-NLS-1$
				entry.setMessageTypeIdentifier(ResourceConstants.VBF_EXC_INTERNAL);
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);
			}
			return;
		}
	}

	private IResourceVisitor getFolderVisitor() {
		if (_folderVisitor == null) {
			_folderVisitor = new IResourceVisitor() {
				public boolean visit(IResource res) {
					if (res instanceof IFile) {
						addSelected(res);
					} else if (res instanceof IFolder) {
						addSelected(res);
					}
					return true; // visit the resource's children
				}
			};
		}
		return _folderVisitor;
	}

	/**
	 * The delegating action has been performed. Implement this method to do the actual work.
	 * 
	 * @param action
	 *            action proxy that handles the presentation portion of the plugin action
	 * @param window
	 *            the desktop window passed by the action proxy
	 */
	public void run(IAction action) {
		ValidateAction vaction = null;
		if (action instanceof ValidateAction) {
			vaction = (ValidateAction) action;
		}
		final Map projects = loadSelected(vaction, false);
		if ((projects == null) || (projects.size() == 0)) {
			return;
		}
		final ProgressAndTextDialog dialog = new ProgressAndTextDialog(getShell());
		try {
			IRunnableWithProgress runnable = ValidationUIPlugin.getRunnableWithProgress(new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) {
					validate(monitor, projects, dialog);
				}
			});
			// validate all EJBs in this project
			dialog.run(true, true, runnable); // fork, cancelable.
		} catch (InvocationTargetException exc) {
			Logger logger = WTPUIPlugin.getLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationUIPlugin.getLogEntry();
				entry.setSourceID("ValidationMenuAction.run(IAction)"); //$NON-NLS-1$
				entry.setMessageTypeIdentifier(ResourceConstants.VBF_EXC_INTERNAL);
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);
				if (exc.getTargetException() != null) {
					entry.setTargetException(exc.getTargetException());
					logger.write(Level.SEVERE, entry);
				}
			}
			String internalErrorMessage = ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL);
			dialog.addText(internalErrorMessage);
		} catch (InterruptedException exc) {
			// User cancelled validation
		} catch (Throwable exc) {
			Logger logger = WTPUIPlugin.getLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationUIPlugin.getLogEntry();
				entry.setSourceID("ValidationMenuAction.run(IAction)"); //$NON-NLS-1$
				entry.setMessageTypeIdentifier(ResourceConstants.VBF_EXC_INTERNAL);
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);
			}
			String internalErrorMessage = ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL);
			dialog.addText(internalErrorMessage);
		} finally {
			_selectedResources.clear();
		}
	}

	void validate(final IProgressMonitor monitor, final Map projects, ProgressAndTextDialog dialog) {
		boolean cancelled = false; // Was the operation cancelled?
		Iterator iterator = projects.keySet().iterator();
		while (iterator.hasNext()) {
			IProject project = (IProject) iterator.next();
			if (project == null) {
				continue;
			}
			try {
				if (cancelled) {
					String message = ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_UI_RESCANCELLED, new String[]{project.getName()});
					dialog.addText(message);
					continue;
				}
				if (!project.isOpen()) {
					String message = ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_UI_CLOSED_PROJECT, new String[]{project.getName()});
					dialog.addText(message);
					continue;
				}
				performValidation(monitor, projects, dialog, project);
			} catch (OperationCanceledException exc) {
				// When loading file deltas, if the operation has been
				// cancelled, then
				// resource.accept throws an OperationCanceledException.
				cancelled = true;
				String message = ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_UI_RESCANCELLED, new String[]{project.getName()});
				dialog.addText(message);
			} catch (Throwable exc) {
				logException(dialog, project, exc);
			}
		}
	}

	/**
	 * @param dialog
	 * @param project
	 * @param exc
	 */
	private void logException(ProgressAndTextDialog dialog, IProject project, Throwable exc) {
		Logger logger = WTPUIPlugin.getLogger();
		if (logger.isLoggingLevel(Level.SEVERE)) {
			LogEntry entry = ValidationUIPlugin.getLogEntry();
			entry.setSourceID("ValidationMenuAction.validate"); //$NON-NLS-1$
			entry.setMessageTypeIdentifier(ResourceConstants.VBF_EXC_INTERNAL);
			entry.setTargetException(exc);
			logger.write(Level.SEVERE, entry);
		}
		String internalErrorMessage = ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PROJECT, new String[]{project.getName()});
		dialog.addText(internalErrorMessage);
	}

	/**
	 * @param monitor
	 * @param projects
	 * @param dialog
	 * @param project
	 * @throws CoreException
	 */
	private void performValidation(final IProgressMonitor monitor, final Map projects, ProgressAndTextDialog dialog, IProject project) throws CoreException {
		// Even if the "maximum number of messages" message is on
		// the task list,
		// run validation, because some messages may have been
		// fixed
		// and there may be space for more messages.
		List changedResources = (List) projects.get(project);
		IResource[] resources = null;
		if (changedResources != null) {
			resources = new IResource[changedResources.size()];
			changedResources.toArray(resources);
		}
		try {
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			if (prjp.numberOfEnabledValidators() > 0) {
				checkProjectConfiguration(monitor, dialog, project, resources, prjp);
			} else {
				String message = ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_UI_NO_VALIDATORS_ENABLED, new String[]{project.getName()});
				dialog.addText(message);
			}
		} catch (InvocationTargetException exc) {
			Logger logger = ValidationPlugin.getPlugin().getLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceIdentifier("ValidationMenuAction::run"); //$NON-NLS-1$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);
				if (exc.getTargetException() != null) {
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
			}
		}
	}

	/**
	 * @param monitor
	 * @param dialog
	 * @param project
	 * @param resources
	 * @param prjp
	 * @throws InvocationTargetException
	 * @throws CoreException
	 */
	private void checkProjectConfiguration(final IProgressMonitor monitor, ProgressAndTextDialog dialog, IProject project, IResource[] resources, ProjectConfiguration prjp) throws InvocationTargetException, CoreException {
		boolean successful = true; // Did the operation
		// complete
		// successfully?
		EnabledValidatorsOperation validOp = null;
		if (resources == null) {
			validOp = new EnabledValidatorsOperation(project,getWorkbenchContext(), prjp.runAsync());
		} else {
			validOp = new EnabledIncrementalValidatorsOperation(resources,getWorkbenchContext(),project, prjp.runAsync());
		}
		if (validOp.isNecessary(monitor)) {
			ResourcesPlugin.getWorkspace().run(validOp, monitor);
		} else {
			if (resources == null) {
				String message = ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_UI_PRJNEEDINPUT, new String[]{project.getName()});
				dialog.addText(message);
			} else {
				for (int i = 0; i < resources.length; i++) {
					String message = ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_UI_RESNEEDINPUT, new String[]{resources[i].getFullPath().toString()});
					dialog.addText(message);
				}
			}
		}
		if (successful) {
			performSucessful(dialog, project, resources);
		} else {
			String internalErrorMessage = ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PROJECT, new String[]{project.getName()});
			dialog.addText(internalErrorMessage);
		}
	}

	public IWorkbenchContext getWorkbenchContext() {
		if(workbenchContext == null)
			workbenchContext = new WorkbenchContext();
		return workbenchContext;
	}

	/**
	 * @param dialog
	 * @param project
	 * @param resources
	 */
	private void performSucessful(ProgressAndTextDialog dialog, IProject project, IResource[] resources) {
		boolean limitExceeded = ValidatorManager.getManager().wasValidationTerminated(project);
		if (limitExceeded) {
			String message = ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_UI_MAX_REPORTED, new String[]{project.getName()});
			dialog.addText(message);
		} else if (resources == null) {
			String message = ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_UI_PRJVALIDATED, new String[]{project.getName()});
			dialog.addText(message);
		} else {
			for (int i = 0; i < resources.length; i++) {
				String message = ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_UI_RESVALIDATED, new String[]{resources[i].getFullPath().toString()});
				dialog.addText(message);
			}
		}
	}

	/**
	 * Selection in the desktop has changed. Plugin provider can use it to change the availability
	 * of the action or to modify other presentation properties.
	 * 
	 * <p>
	 * Action delegate cannot be notified about selection changes before it is loaded. For that
	 * reason, control of action's enable state should also be performed through simple XML rules
	 * defined for the extension point. These rules allow enable state control before the delegate
	 * has been loaded.
	 * </p>
	 * 
	 * @param action
	 *            action proxy that handles presentation portion of the plugin action
	 * @param selection
	 *            current selection in the desktop
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		_currentSelection = selection;
		int count = 0;
		boolean fwkActivated = (ValidationPlugin.isActivated() && ValidationRegistryReader.isActivated());
		if (fwkActivated) {
			ValidateAction vaction = null;
			if (action instanceof ValidateAction) {
				vaction = (ValidateAction) action;
			}
			final Map projects = loadSelected(vaction, true);
			if ((projects != null) && (projects.size() > 0)) {
				Iterator iterator = projects.keySet().iterator();
				while (iterator.hasNext()) {
					// If at least one project can be validated, make "enabled"
					// true and
					// let the dialog tell the user which projects need to be
					// opened,
					// validators enabled, etc.
					IProject project = (IProject) iterator.next();
					if ((project != null) && (project.isOpen())) {
						// If the validation plugin hasn't been activated yet,
						// don't activate it just to
						// find out if there are validators. Only ask if there
						// are enabled validators if
						// the plugin has already been activated.
						try {
							ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfigurationWithoutMigrate(project);
							count += prjp.numberOfEnabledValidators();
						} catch (InvocationTargetException exc) {
							Logger logger = ValidationPlugin.getPlugin().getLogger();
							if (logger.isLoggingLevel(Level.SEVERE)) {
								LogEntry entry = ValidationPlugin.getLogEntry();
								entry.setSourceIdentifier("ValidationMenuAction::selectionChanged"); //$NON-NLS-1$
								entry.setTargetException(exc);
								logger.write(Level.SEVERE, entry);
								if (exc.getTargetException() != null) {
									entry.setTargetException(exc);
									logger.write(Level.SEVERE, entry);
								}
							}
						}
						if (count > 0)
							break;
					}
				}
			}
		}
		action.setEnabled((count > 0) || (!fwkActivated)); // Don't disable the
		// action just
		// because the
		// framework hasn't
		// been activated.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.navigator.internal.views.navigator.INavigatorActionsExtension#init(org.eclipse.wst.common.navigator.internal.views.navigator.INavigatorExtensionSite)
	 */


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate2#runWithEvent(org.eclipse.jface.action.IAction,
	 *      org.eclipse.swt.widgets.Event)
	 */
	public void runWithEvent(IAction action, Event event) {
		run(action);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate2#init(org.eclipse.jface.action.IAction)
	 */
	public void init(IAction action) {
		//init
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate2#dispose()
	 */
	public void dispose() { 
		//dispose
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view) { 
		//init
		
	}
}