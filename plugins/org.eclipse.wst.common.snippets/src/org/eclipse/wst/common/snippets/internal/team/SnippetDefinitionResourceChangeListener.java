/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.snippets.internal.team;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.wst.common.snippets.core.ISnippetCategory;
import org.eclipse.wst.common.snippets.internal.Logger;
import org.eclipse.wst.common.snippets.internal.SnippetDefinitions;
import org.eclipse.wst.common.snippets.internal.model.SnippetManager;
import org.eclipse.wst.common.snippets.internal.palette.ModelFactoryForWorkspace;
import org.eclipse.wst.common.snippets.internal.palette.SnippetPaletteDrawer;


/**
 * Resource listener for Library Definition files. Each file contains exactly
 * one category.
 */

public class SnippetDefinitionResourceChangeListener implements IResourceChangeListener {

	/**
	 * Visitor for handling resource deltas.
	 */
	protected class LibraryDefinitionVisitor implements IResourceDeltaVisitor {
		/**
		 * @see IResourceDeltaVisitor#visit(IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) {
			if (delta == null) {
				return false;
			}
			if (0 != (delta.getFlags() & IResourceDelta.OPEN)) {
				if (delta.getResource() instanceof IProject) {
					IProject project = (IProject) delta.getResource();
					try {
						if (project.isOpen()) {
							SnippetDefinitionResourceChangeListener.this.projectOpened(project);
						}
						else {
							SnippetDefinitionResourceChangeListener.this.projectClosed(project);
						}
					}
					catch (CoreException e) {
						Logger.logException(e);
					}
				}
				return false;
			}
			IResource resource = delta.getResource();
			if (resource instanceof IFile) {
				IFile file = (IFile) resource;
				if (SnippetDefinitionResourceChangeListener.SNIPPET_DEFINITION_EXTENSION.equals(file.getFileExtension())) {
					IPath categoryPath = file.getLocation();
					// If the file has already been deleted, reconstruct the
					// full
					// filesystem path
					if (categoryPath == null) {
						IPath workspaceRelativePath = delta.getFullPath();
						categoryPath = SnippetDefinitionResourceChangeListener.this.getWorkspaceRoot().getLocation().append(workspaceRelativePath);
					}
					// TODO: Load category from disk.
					CategoryFileInfo handle = SnippetDefinitionResourceChangeListener.this.createCategoryInfo(file);
					switch (delta.getKind()) {
						case IResourceDelta.ADDED :
							SnippetDefinitionResourceChangeListener.this.categoryAdded(handle);
							break;
						case IResourceDelta.CHANGED :
							SnippetDefinitionResourceChangeListener.this.categoryChanged(handle);
							break;
						case IResourceDelta.REMOVED :
							SnippetDefinitionResourceChangeListener.this.categoryRemoved(handle);
							break;
					}
				}
				return false;
			}
			else if (resource instanceof IContainer) {
				return true;
			}
			return true;
		}
	}

	/**
	 * Collects files whose extension matches the launch configuration file
	 * extension.
	 */
	protected class ResourceProxyVisitor implements IResourceProxyVisitor {

		protected List fList;

		protected ResourceProxyVisitor(List list) {
			fList = list;
		}

		/**
		 * @see org.eclipse.core.resources.IResourceProxyVisitor#visit(org.eclipse.core.resources.IResourceProxy)
		 */
		public boolean visit(IResourceProxy proxy) throws CoreException {
			if (proxy.getType() == IResource.FILE) {
				if (SnippetDefinitionResourceChangeListener.SNIPPET_DEFINITION_EXTENSION.equalsIgnoreCase(proxy.requestFullPath().getFileExtension())) {
					fList.add(proxy.requestResource());
				}
				return false;
			}
			return true;
		}
	}

	public static final String SNIPPET_DEFINITION_EXTENSION = "snippet"; //$NON-NLS-1$

	protected List fCategoryList = null;

	protected LibraryDefinitionVisitor fVisitor = null;

	/**
	 * 
	 */
	public SnippetDefinitionResourceChangeListener() {
		super();
		initialize();
	}

	protected void categoryAdded(CategoryFileInfo info) { // add to
		// definition list
		categoryChanged(info);
	}

	protected void categoryChanged(CategoryFileInfo info) { // update in
		// definition list
		if (info.getCategory() == null)
			return;
		int oldIndex = -1;
		List categories = SnippetManager.getInstance().getPaletteRoot().getChildren();
		for (int i = 0; i < categories.size(); i++) {
			if (((SnippetPaletteDrawer) categories.get(i)).getId().equals(((SnippetPaletteDrawer) info.getCategory()).getId())) {
				SnippetManager.getInstance().getPaletteRoot().remove((PaletteEntry) categories.get(i));
				oldIndex = i;
				break;
			}
		}
		SnippetManager.getInstance().getPaletteRoot().remove((PaletteEntry) info.getCategory());
		if (oldIndex >= 0)
			SnippetManager.getInstance().getPaletteRoot().add(oldIndex, (PaletteDrawer) info.getCategory());
		else
			SnippetManager.getInstance().getPaletteRoot().add((PaletteDrawer) info.getCategory());
	}

	protected void categoryRemoved(CategoryFileInfo info) {
		if (info.getCategory() == null)
			return;
		SnippetManager.getInstance().getPaletteRoot().remove((PaletteEntry) info.getCategory());
	}

	protected CategoryFileInfo createCategoryInfo(IFile file) {
		SnippetDefinitions defs = ModelFactoryForWorkspace.getWorkspaceInstance().loadFrom(file);
		ISnippetCategory category = null;
		if (defs.getCategories().size() > 0) {
			category = (ISnippetCategory) defs.getCategories().get(0);
		}
		CategoryFileInfo result = new CategoryFileInfo(this, file, category);
		if (category != null)
			((SnippetPaletteDrawer) category).setSourceDescriptor(result);
		return result;
	}

	/**
	 * Finds and returns all launch configurations in the given container (and
	 * subcontainers)
	 * 
	 * @param container
	 *            the container to search
	 * @exception CoreException
	 *                an exception occurs traversing the container.
	 * @return all launch configurations in the given container
	 */
	protected List findCategories(IContainer container) throws CoreException {
		List list = new ArrayList(1);
		if (container instanceof IProject && !((IProject) container).isOpen()) {
			return list;
		}
		ResourceProxyVisitor visitor = new ResourceProxyVisitor(list);
		try {
			container.accept(visitor, IResource.NONE);
		}
		catch (CoreException ce) { // Closed project...should not be possible
			// with previous check
		}
		Iterator iter = list.iterator();
		List infos = new ArrayList(list.size());
		while (iter.hasNext()) {
			IFile file = (IFile) iter.next(); // TODO: create category from
			// file
			infos.add(createCategoryInfo(file));
		}
		return infos;
	}

	/**
	 * Returns a collection of all categories in the workspace. This
	 * collection is initialized lazily.
	 * 
	 * @return all launch configuration handles
	 */
	protected List getAllCategories() throws CoreException {
		if (fCategoryList == null) {
			fCategoryList = new ArrayList(0);
			List categories = findCategories(getWorkspaceRoot());
			fCategoryList.addAll(categories);
		}
		return fCategoryList;
	}

	/**
	 * @return
	 */
	public List getCategoryList() {
		if (fCategoryList == null)
			fCategoryList = new ArrayList();
		return fCategoryList;
	}

	/**
	 * Returns all launch configurations that are stored as resources in the
	 * given project.
	 * 
	 * @param project
	 *            a project
	 * @return collection of launch configurations that are stored as
	 *         resources in the given project
	 */
	protected List getCategoryRecords(IProject project) throws CoreException {
		Iterator iter = getAllCategories().iterator();
		List infos = new ArrayList();
		while (iter.hasNext()) {
			CategoryFileInfo info = (CategoryFileInfo) iter.next();
			IFile file = info.getFile();
			if (file != null && file.getProject().equals(project)) {
				infos.add(info);
			}
		}
		return infos;
	}

	protected IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	protected IWorkspaceRoot getWorkspaceRoot() {
		return getWorkspace().getRoot();
	}

	/**
	 * 
	 */
	protected void initialize() {
		IProject[] projects = getWorkspaceRoot().getProjects();
		for (int i = 0; i < projects.length; i++) {
			if (projects[i].isOpen()) {
				try {
					projectOpened(projects[i]);
				}
				catch (CoreException e) {
				}
			}
			else {
				try {
					projectClosed(projects[i]);
				}
				catch (CoreException e) {
				}
			}
		}
	}

	/**
	 * The specified project has just closed - remove its launch
	 * configurations from the cached index.
	 * 
	 * @param project
	 *            the project that has been closed
	 * @exception CoreException
	 *                if writing the index fails
	 */
	protected void projectClosed(IProject project) throws CoreException {
		List infos = getCategoryRecords(project);
		if (!infos.isEmpty()) {
			Iterator iterator = infos.iterator();
			while (iterator.hasNext()) { // TODO: remove the categories
				// from the model
				categoryRemoved((CategoryFileInfo) iterator.next());
			}
		}
	}

	/**
	 * The specified project has just opened - add all launch configs in the
	 * project to the index of all configs.
	 * 
	 * @param project
	 *            the project that has been opened
	 * @exception CoreException
	 *                if reading the index fails
	 */
	protected void projectOpened(IProject project) throws CoreException {
		// TODO: Add all of the definitions in this project to the model
		List configs = getCategoryRecords(project);
		if (!configs.isEmpty()) {
			Iterator iterator = configs.iterator();
			while (iterator.hasNext()) {
				// TODO: remove the categories
				// from the model
				categoryAdded((CategoryFileInfo) iterator.next());
			}
		}
	}

	/**
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta delta = event.getDelta();
		if (delta != null) {
			try {
				if (fVisitor == null) {
					fVisitor = new LibraryDefinitionVisitor();
				}
				delta.accept(fVisitor);
			}
			catch (CoreException e) {
				Logger.logException(e);
			}
		}
	}
}
