/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emfworkbench.integration;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.wst.common.frameworks.internal.operations.IOperationHandler;
import org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchContext;
import org.eclipse.wst.common.internal.emfworkbench.edit.WrappingCommandStack;

/**
 * Insert the type's description here. Creation date: (05/21/01 8:26:32 PM)
 * 
 * @author: Administrator
 */
public class ComposedEditModel extends EditModel implements EditModelListener {

	private List children = null;
	private Map childrenMap = null;
	private List cachedKeys = new ArrayList();

	/**
	 * ComposedEditModel constructor comment.
	 */
	public ComposedEditModel(String editModelID, EMFWorkbenchContext context) {
		super(editModelID, context, false);
	}


	public void createChildrenIfNecessary(ComposedAccessorKey composedAccessorKey) {
		//do nothing
	}

	/**
	 * @param newKey
	 */
	public void cacheAccessorKey(ComposedAccessorKey newKey) {
		if (!cachedKeys.contains(newKey))
			cachedKeys.add(newKey);
	}

	// Is this necessary anymore?  The only diff seems to be it doesn't release resources
//	public void dispose() {
//		disposing = true;
//		if (commandStack != null)
//			commandStack.removeCommandStackListener(this);
//		if (hasListeners())
//			notifyListeners(new EditModelEvent(EditModelEvent.PRE_DISPOSE, this));
//		releasePreloadResources();
//		releaseIdentifiers();
//		emfContext = null; 
//		resources = null;
//		disposing = false;
//		project = null;
//		cachedKeys = new ArrayList();
//	}

	public EditModel.Reference addChild(String editModelID, Map params, Object accessorKey) {
		return addChild(editModelID, params, ComposedAccessorKey.getComposedAccessorKey(accessorKey, this));
	}

	public EditModel.Reference addChild(String editModelID, Map params, ComposedAccessorKey composedAccessorKey) {

		EditModel editModel = getEmfContext().getEditModelForWrite(editModelID, composedAccessorKey, params);
		editModel.addListener(this);

		getChildrenMap().put(editModel.getReference(), editModel);
		getChildren().add(editModel);

		return editModel.getReference();
	}

	public void removeChild(EditModel.Reference reference, Object accessorKey) {
		ComposedAccessorKey composedAccessorKey = ComposedAccessorKey.getComposedAccessorKey(accessorKey, this);
		EditModel editModel = (EditModel) getChildrenMap().remove(reference);
		if (editModel != null) {
			editModel.releaseAccess(composedAccessorKey);
			editModel.removeListener(this);
			getChildren().remove(editModel);
		}
	}

	public Iterator getContainedReferences() {
		return getChildrenMap().keySet().iterator();
	}

	public EditModel getContainedEditModel(EditModel.Reference reference) {
		return (EditModel) getChildrenMap().get(reference);
	}

	/**
	 * Return the CommandStack.
	 */
	protected BasicCommandStack createCommandStack() {
		return new WrappingCommandStack(this);
	}

	/**
	 * Forward all events to the listeners for this model
	 */
	public void editModelChanged(EditModelEvent anEvent) {
		if (hasListeners())
			notifyListeners(anEvent);
	}

	public Set getAffectedFiles() {
		Set aSet = new HashSet();
		List models = getChildren();
		for (int i = 0; i < models.size(); i++) {
			EditModel child = (EditModel) models.get(i);
			aSet.addAll(child.getAffectedFiles());
		}
		return aSet;
	}

	public List getChildren() {
		if (children == null)
			children = new ArrayList();
		return children;
	}

	protected Map getChildrenMap() {
		if (childrenMap == null)
			childrenMap = new HashMap();
		return childrenMap;
	}

	/**
	 * Pass along to children.
	 */
	protected void handleSaveIfNecessaryDidNotSave(IProgressMonitor monitor) {
		List list = getChildren();
		EditModel editModel;
		for (int i = 0; i < list.size(); i++) {
			editModel = (EditModel) list.get(i);
			editModel.handleSaveIfNecessaryDidNotSave(monitor);
		}
	}

	/**
	 * Return whether a save is needed on the CommandStack
	 */
	public boolean isDirty() {
		Iterator editModels = getChildren().iterator();
		while (editModels.hasNext()) {
			EditModel editModel = (EditModel) editModels.next();
			if (editModel.isDirty())
				return true;
		}
		return false;
	}

	public boolean isReadOnly() {
		return false;
	}

	/**
	 * Return whether a save is needed on the CommandStack
	 */
	public boolean isInterrestedInResource(Resource aResource) {
		Iterator editModels = getChildren().iterator();
		while (editModels.hasNext()) {
			EditModel editModel = (EditModel) editModels.next();
			if (editModel.isInterrestedInResource(aResource))
				return true;
		}
		return false;
	}

	public void primSave(IProgressMonitor monitor) {
		List list = getChildren();
		for (int i = 0; i < list.size(); i++)
			((EditModel) list.get(i)).primSave(monitor);
	}

	/**
	 * This only increments the reference count of the children and should only be called if you
	 * know what you are doing.
	 */
	public void access(Object accessorKey) {
		ComposedAccessorKey composedAccessorKey = ComposedAccessorKey.getComposedAccessorKey(accessorKey, this);
		if (getChildren().size() == 0) {
			createChildrenIfNecessary(composedAccessorKey);
		} else {

			List tempchildren = getChildren();
			for (int i = 0; i < tempchildren.size(); i++) {
				EditModel model = (EditModel) tempchildren.get(i);
				model.access(composedAccessorKey);
			}
		}
		// removing for defect 1978, children should do all the accessing
		super.access(accessorKey);

	}

	/**
	 * This method should be called from each client when they are finished working with the
	 * EditModel.
	 */
	public void releaseAccess(Object accessorKey) {
		List tempchildren = getChildren();
		ComposedAccessorKey composedAccessorKey = ComposedAccessorKey.getComposedAccessorKey(accessorKey, this);
		for (int i = 0; i < tempchildren.size(); i++) {
			EditModel model = (EditModel) tempchildren.get(i);
			model.releaseAccess(composedAccessorKey);
		}
		removeKeyFromCache(composedAccessorKey);
		// Removing this call... Children should be able to handle all releasing defect 1978
		super.releaseAccess(accessorKey);
	}

	public void removeKeyFromCache(ComposedAccessorKey key) {
		cachedKeys.remove(key);
	}

	/**
	 * If one should save, they all should save.
	 */
	protected boolean shouldSave() {
		List list = getChildren();
		EditModel editModel;
		for (int i = 0; i < list.size(); i++) {
			editModel = (EditModel) list.get(i);
			if (editModel.shouldSave())
				return true;
		}
		return false;
	}

	/**
	 * If one should save, they all should save.
	 */
	protected boolean shouldSave(IOperationHandler operationHandler) {
		List list = getChildren();
		EditModel editModel;
		for (int i = 0; i < list.size(); i++) {
			editModel = (EditModel) list.get(i);
			if (editModel.shouldSave(operationHandler))
				return true;
		}
		return false;
	}

	/**
	 * @see com.ibm.etools.j2ee.workbench.EditModel#getNonResourceFiles()
	 */
	public List getNonResourceFiles() {
		List list = getChildren();
		List result = new ArrayList();
		EditModel editModel;
		for (int i = 0; i < list.size(); i++) {
			editModel = (EditModel) list.get(i);
			List files = editModel.getNonResourceFiles();
			if (files != null && !files.isEmpty())
				result.addAll(files);
		}
		return result;
	}

}
