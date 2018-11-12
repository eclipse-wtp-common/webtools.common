/*******************************************************************************
 * Copyright (c) 2001, 2018 IBM Corporation and others.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.IContributedContentsView;
import org.eclipse.wst.validation.internal.ConfigurationManager;
import org.eclipse.wst.validation.internal.DisabledResourceManager;
import org.eclipse.wst.validation.internal.ValManager;
import org.eclipse.wst.validation.internal.ValType;
import org.eclipse.wst.validation.internal.ValidationSelectionHandlerRegistryReader;
import org.eclipse.wst.validation.internal.ui.plugin.ValidationUIPlugin;
import org.eclipse.wst.validation.ui.internal.ManualValidationRunner;

public class ValidationHandler extends AbstractHandler {
	private IResourceVisitor _folderVisitor;
	private IResourceVisitor _projectVisitor;
	private Map<IProject, Set<IResource>> _selectedResources;

	public ValidationHandler() {
		_selectedResources = new HashMap<IProject, Set<IResource>>();
	}

	void addSelected(IResource selected) {
		IProject project = selected.getProject();
		boolean added = _selectedResources.containsKey(project);
		Set<IResource> changedRes = null;
		if (added) {
			// If the value is null, the entire project needs to be validated anyway.
			changedRes = _selectedResources.get(project);
			if (changedRes == null)
				return;

		} else {
			changedRes = new HashSet<IResource>();
		}
		if (changedRes.add(selected)) {
			_selectedResources.put(project, changedRes);
		}
	}

	private void addSelected(ValidateAction action, Object selected) {
		if (selected instanceof IProject) {
			addVisitor((IProject) selected);
		} else if (selected instanceof IFile) {
			addSelected((IFile) selected);
		} else if (selected instanceof IFolder) {
			addVisitor((IFolder) selected);
		} else if (isValidType(getExtendedType(selected))) {
			addSelected(action, getExtendedType(selected));
		} else {
			// Not a valid input type. Must be IProject, IJavaProject, or IResource.
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
				IResource resource = Platform.getAdapterManager().getAdapter(selected, IResource.class);
				if (resource != null) {
					valid = true;
					addSelected(action, resource);
				}
			}
			if (!valid) {
				// Stop processing. This allows the "Run Validation" menu item
				// to gray out once an element that can not be validated is selected.
				_selectedResources.clear();
			}
		}
	}

	private void addVisitor(IFolder selected) {
		// add the folder and its children
		try {
			selected.accept(getFolderVisitor());
		} catch (CoreException exc) {
			ValidationUIPlugin.getPlugin().handleException(exc);
			return;
		}
	}

	private void addVisitor(IProject selected) {
		// add the folder and its children
		if (!selected.isAccessible())
			return;
		try {
			selected.accept(getProjectVisitor());
		} catch (CoreException exc) {
			ValidationUIPlugin.getPlugin().handleException(exc);
			return;
		}
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell activeShell = HandlerUtil.getActiveShell(event);
		IWorkbenchPart interestedPart = HandlerUtil.getActivePart(event);

		IStructuredSelection sel = HandlerUtil.getCurrentStructuredSelection(event);
		Map<IProject, Set<IResource>> projects = loadSelected(sel);

		if (projects == null || projects.isEmpty()) {
			if (!(interestedPart instanceof IEditorPart)) {
				IContributedContentsView contributedContentsView = interestedPart
						.getAdapter(IContributedContentsView.class);
				if (contributedContentsView != null) {
					interestedPart = contributedContentsView.getContributingPart();
				}
			}
			if (interestedPart instanceof IEditorPart) {
				IEditorInput editorInput = HandlerUtil.getActiveEditorInput(event);
				if (editorInput != null) {
					IResource resource = editorInput.getAdapter(IResource.class);
					if (resource != null) {
						sel = new StructuredSelection(resource);
					}
				}
			}
			projects = loadSelected(sel);
		}

		if (projects == null || projects.isEmpty()) {
			return null;
		}
		// If the files aren't saved do not run validation.
		if (!handleFilesToSave(projects, activeShell))
			return null;

		boolean confirm = ValManager.getDefault().getGlobalPreferences()
				.getConfirmDialog();
		ManualValidationRunner.validate(projects, ValType.Manual, confirm);

		return null;
	}

	private Object getExtendedType(Object selected) {
		Object result = ValidationSelectionHandlerRegistryReader.getInstance().getExtendedType(selected);
		return result == null ? selected : result;
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

	protected List<IFile> getIFiles(Map<IProject, Set<IResource>> projects) {
		List<IFile> fileList = new LinkedList<IFile>();
		for (IProject project : projects.keySet()) {
			for (IResource resource : projects.get(project)) {
				if (resource instanceof IFile)
					fileList.add((IFile) resource);
			}
		}
		return fileList;
	}

	private IResourceVisitor getProjectVisitor() {
		if (_projectVisitor == null) {
			_projectVisitor = new IResourceVisitor() {
				public boolean visit(IResource res) {
					if (DisabledResourceManager.getDefault().isDisabled(res))
						return false;
					if (res instanceof IFile)
						addSelected(res);
					else if (res instanceof IFolder)
						addSelected(res);
					else if (res instanceof IProject)
						addSelected(res);

					return true;
				}
			};
		}
		return _projectVisitor;
	}

	/**
	 * Handle any files that must be saved prior to running validation.
	 * 
	 * @param projects The list of projects that will be validated.
	 * @return True if all files have been saved, false otherwise.
	 */
	protected boolean handleFilesToSave(Map<IProject, Set<IResource>> projects, Shell shell) {
		List fileList = getIFiles(projects);
		final IEditorPart[] dirtyEditors = SaveFilesHelper.getDirtyEditors(fileList);
		if (dirtyEditors == null || dirtyEditors.length == 0)
			return true;
		boolean saveAutomatically = false;
		try {
			saveAutomatically = ConfigurationManager.getManager().getGlobalConfiguration().getSaveAutomatically();
		} catch (InvocationTargetException e) {
			// In this case simply default to false.
		}
		SaveFilesDialog sfDialog = null;
		if (!saveAutomatically) {
			sfDialog = new SaveFilesDialog(
					ValidationUIPlugin.getPlugin().getWorkbench().getActiveWorkbenchWindow().getShell());
			sfDialog.setInput(Arrays.asList(dirtyEditors));
		}

		if (saveAutomatically || sfDialog.open() == Window.OK) {
			ProgressMonitorDialog ctx = new ProgressMonitorDialog(shell);

			IRunnableWithProgress runnable = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						SubMonitor submonitor = SubMonitor.convert(monitor, ValidationUIMessages.SaveFilesDialog_saving, dirtyEditors.length);
						for (int i = 0; i < dirtyEditors.length; i++) {
							dirtyEditors[i].doSave(submonitor.newChild(1));
						}
					} finally {
						monitor.done();
					}
				}
			};

			try {
				ctx.run(false, true, runnable);
				return true;
			} catch (InvocationTargetException e) {
				ValidationUIPlugin.getPlugin().handleException(e);
			} catch (InterruptedException e) {
				ValidationUIPlugin.getPlugin().handleException(e);
			}
		}
		return false;
	}

	private boolean isValidType(Object object) {
		return object instanceof IProject || object instanceof IFile || object instanceof IFolder;
	}

	/**
	 * Return a map of the selected elements. Each key of the map is an IProject,
	 * and the value is a Set of the selected resources in that project. If a
	 * project is selected, and nothing else in the project is selected, a full
	 * validation (null value) will be done on the project. If a project is
	 * selected, and some files/folders in the project are also selected, only the
	 * files/folders will be validated. If a folder is selected, all of its contents
	 * are also validated.
	 * 
	 * @return null if there is no selection.
	 */
	private Map<IProject, Set<IResource>> loadSelected(IStructuredSelection selection) {
		// GRK previously this did not do a clear, but I couldn't understand why that
		// would be so I am forcing a clear
		// GRK In my testing, not doing a clear caused duplicate validations
		_selectedResources.clear();
		if ((selection == null) || selection.isEmpty() )
			return null;

		Object[] elements = selection.toArray();
		for (Object element : elements) {
			if (element != null)
				addSelected(null, element);
		}
		return _selectedResources;
	}
}
