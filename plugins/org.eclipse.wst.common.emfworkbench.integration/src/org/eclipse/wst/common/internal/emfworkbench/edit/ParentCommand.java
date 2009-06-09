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
package org.eclipse.wst.common.internal.emfworkbench.edit;



import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.wst.common.internal.emfworkbench.integration.AbstractEditModelCommand;
import org.eclipse.wst.common.internal.emfworkbench.integration.ComposedEditModel;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModel;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModelCommand;

/**
 * Insert the type's description here. Creation date: (05/22/01 8:57:38 AM)
 * 
 * @author: Administrator
 */
public class ParentCommand extends EditModelCommand {
	private List children;
	private ComposedEditModel editModel;
	private List affectedModels;

	public ParentCommand(Command targetCommand, ComposedEditModel anEditModel) {
		super(targetCommand);
		editModel = anEditModel;
		children = new ArrayList(1);
	}

	@Override
	public boolean canExecute() {
		return getTarget().canExecute();
	}

	protected void computeAffectedModels() {
		ResourceSet set = null;
		List editModels = getEditModel().getChildren();
		Iterator it = computeAffectedResourceSets().iterator();
		while (it.hasNext()) {
			set = (ResourceSet) it.next();
			for (int i = 0; i < editModels.size(); i++) {
				EditModel model = (EditModel) editModels.get(i);
				if (model.getResourceSet() == set) {
					getAffectedModels().add(new EditModelRetriever(model.getEmfContext(), model.getEditModelID(), model.getParams()));
					continue;
				}
			}
		}
	}

	protected Set computeAffectedResourceSets() {
		Iterator objects = getTarget().getAffectedObjects().iterator();
		Set resourceSets = new HashSet();
		Object o = null;
		EObject ref = null;
		ResourceSet set = null;
		while (objects.hasNext()) {
			o = objects.next();
			if (!(o instanceof EObject))
				continue;
			ref = (EObject) o;
			if (ref.eResource() != null) {
				set = ref.eResource().getResourceSet();
				if (set != null)
					resourceSets.add(set);
			}
		}
		return resourceSets;
	}

	protected ChildCommand createChildCommand(EditModelRetriever retriever) {
		return new ChildCommand(this, getTarget(), retriever);
	}

	public void execute() {
		getTarget().execute();
		computeAffectedModels();
		pushChildrenForExecute();
	}

	@Override
	protected void executeInModel(AbstractEditModelCommand cmd) {
		getEditModel().getCommandStack().execute(cmd);
	}

	protected List getAffectedModels() {
		if (affectedModels == null)
			affectedModels = new ArrayList(1);
		return affectedModels;
	}

	protected List getChildren() {
		return children;
	}

	protected ComposedEditModel getEditModel() {
		return editModel;
	}

	protected void invertChildren() {
		invertChildrenExcept((ChildCommand) null);
	}

	protected void invertChildrenExcept(ChildCommand caller) {
		for (int i = 0; i < children.size(); i++) {
			ChildCommand childCmd = (ChildCommand) children.get(i);
			if (caller == null || childCmd != caller)
				childCmd.invertAndPush();
		}
	}

	protected void invertFrom(ChildCommand caller) {
		invertAndPush();
		invertChildrenExcept(caller);
	}

	protected void pushChildrenForExecute() {
		for (int i = 0; i < getAffectedModels().size(); i++) {
			EditModelRetriever retriever = (EditModelRetriever) getAffectedModels().get(i);
			ChildCommand command = createChildCommand(retriever);
			getChildren().add(command);
			command.executeInModel(command);
		}
	}

	public void redo() {
		getTarget().redo();
		invertChildren();
	}

	public void redoFrom(ChildCommand child) {
		invertFrom(child);
	}

	@Override
	public void undo() {
		getTarget().undo();
		invertChildren();
	}

	public void undoFrom(ChildCommand child) {
		invertFrom(child);
	}
}