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
package org.eclipse.wst.common.emfworkbench.integration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.command.AbstractCommand;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.wst.common.emf.utilities.ExtendedEcoreUtil;

/**
 * Insert the type's description here. Creation date: (4/6/2001 3:40:35 PM)
 * 
 * @author: Administrator
 */
public class ModelModifier {
	private static final String SET_PATTERN = "Set {0}"; //$NON-NLS-1$
	private static final String ADD_PATTERN = "Add {0}"; //$NON-NLS-1$
	private static final String REMOVE_PATTERN = "Remove {0}"; //$NON-NLS-1$
	private static final String DEFAULT_COMMAND_LABEL = "Command"; //$NON-NLS-1$
	private EditingDomain editingDomain;
	private List helpers;
	private List extendedHelpers;
	protected List additionalCommands;
	protected int status;
	public static final int NO_VALUE_CHANGE = 0;
	public static final int VALUE_CHANGE = 1;
	public static final int ERROR = 2;

	/**
	 * J2EEModelModifier constructor comment.
	 */
	public ModelModifier() {
		super();
	}

	/**
	 * J2EEModelModifier constructor comment.
	 * 
	 * @param aDomain
	 *            EditingDomain
	 */
	public ModelModifier(EditingDomain aDomain) {
		setEditingDomain(aDomain);
	}

	/**
	 * Add
	 * 
	 * @aHelper to the list of helper that will be executed.
	 */
	public void addHelper(ModifierHelper aHelper) {
		if (aHelper != null && !getHelpers().contains(aHelper))
			getHelpers().add(aHelper);
	}

	public void addAdditionalCommand(Command aCommand) {
		if (aCommand != null && !getAdditionalCommands().contains(aCommand))
			additionalCommands.add(aCommand);
	}

	/**
	 * Return true if this modifier can create a command that will perform the necessary operation.
	 */
	public boolean canExecuteCommand() {
		return getEditingDomain() != null;
	}

	public int executeWithStatus() {
		try {
			execute();
			return status;
		} finally {
			status = -1;
		}
	}

	/**
	 * Execute this modifier using the recording mechanism of the StructedTextUndoManager. If this
	 * modifier cannot record, try to execute using the CommandStack (if it can execute commands).
	 * Return true if the execution was attempted.
	 * 
	 * @see canExecuteCommand()
	 * @see canRecord()
	 * @see run()
	 */
	public boolean execute(ModifierHelper aHelper) {
		addHelper(aHelper);
		return execute();
	}

	/**
	 * Execute this modifier using the recording mechanism of the StructedTextUndoManager. If this
	 * modifier cannot record, try to execute using the CommandStack (if it can execute commands).
	 * Return true if the execution was attempted.
	 * 
	 * @see canExecuteCommand()
	 * @see canRecord()
	 * @see run()
	 */
	public boolean execute(List someHelpers) {
		setHelpers(someHelpers);
		return execute();
	}

	/**
	 * Execute this modifier by creating a Command that is executed on the CommandStack. If this
	 * modifier cannot execute commands, the execution will not take place. Return true if the
	 * execution was attempted.
	 * 
	 * @see canExecuteCommand()
	 */
	public boolean execute() {
		boolean result = false;
		if (canExecuteCommand()) {
			try {
				Command command = createCommand();
				result = command != null;
				if (result) {
					getCommandStack().execute(command);
				}
			} finally {
				release();
			}
		} else {
			setStatus(ERROR);
		}
		return result;
	}

	protected CommandStack getCommandStack() {
		if (getEditingDomain() != null)
			return getEditingDomain().getCommandStack();
		return null;
	}

	/**
	 * Insert the method's description here. Creation date: (4/6/2001 2:53:17 PM)
	 * 
	 * @return EditingDomain
	 */
	public EditingDomain getEditingDomain() {
		return editingDomain;
	}

	/**
	 * Insert the method's description here. Creation date: (4/10/2001 8:46:35 AM)
	 * 
	 * @return J2EEModifierHelper
	 */
	public ModifierHelper getFirstHelper() {
		if (helpers != null && getHelpers().size() > 0)
			return (ModifierHelper) getHelpers().get(0);
		return null;
	}

	/**
	 * Insert the method's description here. Creation date: (4/10/2001 8:46:35 AM)
	 * 
	 * @return java.util.List
	 */
	public java.util.List getHelpers() {
		if (helpers == null)
			helpers = new ArrayList();
		return helpers;
	}

	public java.util.List getAdditionalCommands() {
		if (additionalCommands == null)
			additionalCommands = new ArrayList();
		return additionalCommands;
	}

	/**
	 * Release all model artifacts.
	 */
	protected void release() {
		setEditingDomain(null);
		setHelpers(null);
	}

	/**
	 * Insert the method's description here. Creation date: (4/6/2001 2:53:17 PM)
	 * 
	 * @param newEditingDomain
	 *            EditingDomain
	 */
	public void setEditingDomain(EditingDomain newEditingDomain) {
		editingDomain = newEditingDomain;
	}

	/**
	 * Insert the method's description here. Creation date: (4/10/2001 8:46:35 AM)
	 * 
	 * @param newHelpers
	 *            java.util.List
	 */
	public void setHelpers(java.util.List newHelpers) {
		helpers = newHelpers;
	}

	protected void setStatus(int statusCode) {
		if (statusCode > status)
			status = statusCode;
	}

	/**
	 * Return an AddCommand that will be executed by a CommandStack.
	 */
	protected Command createAddCommand(ModifierHelper aHelper) {
		Object value = getValue(aHelper);
		Command command = null;
		if (valueChanged(aHelper.getOwner(), aHelper.getFeature(), value, false)) {
			command = AddCommand.create(getEditingDomain(), aHelper.getOwner(), aHelper.getFeature(), value);
			((AbstractCommand) command).setLabel(createCommandLabel(ADD_PATTERN, aHelper.getFeature()));
			setStatus(VALUE_CHANGE);
		} else {
			setStatus(NO_VALUE_CHANGE);
		}
		return command;
	}

	/**
	 * Return a Command that will be executed by a CommandStack. The default is to return null.
	 * Subclasses should override this method.
	 */
	public Command createCommand() {
		Command chainedCommand = createCommand(null, getHelpers());
		if (null == chainedCommand && additionalCommands != null && additionalCommands.isEmpty()) {
			setStatus(ERROR);
			return null;
		}
		chainedCommand = appendAdditionalCommands(chainedCommand);
		return chainedCommand;
	}

	protected Command createCommand(Command chainedCommand, List helpersArg) {
		if (null == extendedHelpers) {
			extendedHelpers = new ArrayList();
		}

		if (!helpersArg.isEmpty()) {
			Iterator it = helpersArg.iterator();
			Command nextCommand = null;
			while (it.hasNext()) {
				nextCommand = createCommand((ModifierHelper) it.next());
				if (chainedCommand == null)
					chainedCommand = nextCommand;
				else if (nextCommand != null)
					chainedCommand = chainedCommand.chain(nextCommand);
			}
		}
		if (!extendedHelpers.isEmpty()) {
			List copy = new ArrayList();
			copy.addAll(extendedHelpers);
			extendedHelpers.clear();
			chainedCommand = createCommand(chainedCommand, copy);
		}
		return chainedCommand;
	}

	protected Command appendAdditionalCommands(Command chainedCommand) {
		if (additionalCommands != null && !additionalCommands.isEmpty()) {
			Command command;
			for (int i = 0; i < additionalCommands.size(); i++) {
				command = (Command) additionalCommands.get(i);
				if (chainedCommand == null)
					chainedCommand = command;
				else
					chainedCommand = chainedCommand.chain(command);
			}
		}
		return chainedCommand;
	}

	/**
	 * Return a Command that will be executed by a CommandStack.
	 */
	protected Command createCommand(ModifierHelper aHelper) {
		if (aHelper == null)
			return null;
		Command command1, command2;
		ModifierHelper ownerHelper = aHelper.getOwnerHelper();
		if (aHelper.shouldUnsetValue() && ownerHelper != null)
			return null; //we are unsetting a value on an owner that does not exist so do not
		// create the owner
		command1 = createCommand(ownerHelper);
		command2 = primCreateCommand(aHelper);

		if (command1 != null) {
			if (command2 == null)
				command2 = command1;
			else
				command2 = command2.chain(command1);
		}
		return command2;
	}

	protected String createCommandLabel(String aPattern, EStructuralFeature feature) {
		String replacement = feature == null ? DEFAULT_COMMAND_LABEL : feature.getName();
		return java.text.MessageFormat.format(aPattern, new String[]{replacement});
	}

	/**
	 * Return a Command that will be executed by a CommandStack. The default is to return null.
	 * Subclasses should override this method.
	 */
	protected Command createManyCommand(ModifierHelper aHelper) {
		if (aHelper.shouldUnsetValue())
			return createRemoveCommand(aHelper);
		return createAddCommand(aHelper);
	}

	protected EObject createObjectFromHelper(ModifierHelper aHelper) {
		return aHelper.createNewObjectFromFeature();
	}

	public class ProxyWrappingCommand extends AbstractCommand {
		protected Command baseCommand = null;
		protected EObject eObject = null;
		protected Resource resource = null;

		public ProxyWrappingCommand(Command baseCommand, EObject eObject) {
			this.baseCommand = baseCommand;
			this.eObject = eObject;
			this.resource = eObject.eResource();
		}

		public boolean canExecute() {
			return baseCommand.canExecute();
		}

		public void execute() {
			ExtendedEcoreUtil.becomeProxy(eObject, resource);
			baseCommand.execute();
		}

		public boolean canUndo() {
			return baseCommand.canUndo();
		}

		public void undo() {
			baseCommand.undo();
			ExtendedEcoreUtil.removeProxy(eObject, resource);
		}

		public void redo() {
			baseCommand.redo();
		}

		public Collection getResult() {
			return baseCommand.getResult();
		}

		public Collection getAffectedObjects() {
			return baseCommand.getAffectedObjects();
		}

		public String getLabel() {
			return baseCommand.getLabel();
		}

		public String getDescription() {
			return baseCommand.getDescription();
		}

		public void dispose() {
			super.dispose();
			baseCommand.dispose();
		}
	};

	/**
	 * Return a Remove Command that will be executed by a CommandStack.
	 */
	protected Command createRemoveCommand(ModifierHelper aHelper) {
		Object value = getValue(aHelper);
		Command command = null;
		EStructuralFeature feature = aHelper.getFeature();
		if (valueChanged(aHelper.getOwner(), feature, value, true)) {
			if (isValueEqual(aHelper, value)) {
				command = RemoveCommand.create(getEditingDomain(), aHelper.getOwner(), feature, (Collection) value);
			} else {
				command = RemoveCommand.create(getEditingDomain(), aHelper.getOwner(), feature, value);
			}
			((AbstractCommand) command).setLabel(createCommandLabel(REMOVE_PATTERN, feature));
			setStatus(VALUE_CHANGE);
		} else {
			setStatus(NO_VALUE_CHANGE);
		}
		return command;
	}

	private boolean isValueEqual(ModifierHelper aHelper, Object value) {
		return aHelper.getOwner().eGet(aHelper.getFeature()) == value;
	}

	/**
	 * Return a SetCommand that will be executed by a CommandStack.
	 */
	protected Command createSingleCommand(ModifierHelper aHelper) {
		Object value = getValue(aHelper);
		Command command = null;
		if (valueChanged(aHelper.getOwner(), aHelper.getFeature(), value, aHelper.shouldUnsetValue())) {
			command = SetCommand.create(getEditingDomain(), aHelper.getOwner(), aHelper.getFeature(), value);
			((AbstractCommand) command).setLabel(createCommandLabel(SET_PATTERN, aHelper.getFeature()));
			setStatus(VALUE_CHANGE);
		} else {
			setStatus(NO_VALUE_CHANGE);
		}
		return command;
	}

	protected Object createValueFromHelper(ModifierHelper aHelper) {
		EObject newObject = createObjectFromHelper(aHelper);
		setNewObjectAttributes(newObject, aHelper);
		return newObject;
	}

	protected boolean enumValueChanged(EObject anObject, EStructuralFeature aFeature, Object aValue) {
		if (!anObject.eIsSet(aFeature))
			return true;
		Enumerator existingEnumerator = (Enumerator) anObject.eGet(aFeature);
		Enumerator newEnumerator = getEnumerator(aFeature, aValue);
		return existingEnumerator != newEnumerator;
	}

	private Enumerator getEnumerator(EStructuralFeature aFeature, Object aValue) {
		if (aValue instanceof Enumerator)
			return (Enumerator) aValue;
		EEnum anEnum = (EEnum) aFeature.getEType();
		EEnumLiteral literal = null;
		if (aValue instanceof String)
			literal = anEnum.getEEnumLiteral((String) aValue);
		else if (aValue instanceof Integer)
			literal = anEnum.getEEnumLiteral(((Integer) aValue).intValue());
		if (literal != null)
			return literal.getInstance();
		return null;
	}

	protected Object getValue(ModifierHelper aHelper) {
		if (aHelper.mustCreateValue()) {
			Object value = createValueFromHelper(aHelper);
			aHelper.setValue(value);
		}
		return aHelper.getValue();
	}

	protected boolean manyValueChanged(EObject anObject, EStructuralFeature aFeature, Object aValue, boolean isUnset) {
		List list = (List) anObject.eGet(aFeature);
		if (isUnset)
			return list.contains(aValue) || (list == aValue && !list.isEmpty());
		return !list.contains(aValue);
	}

	/**
	 * Return a Command that will be executed by a CommandStack. The default is to return null.
	 * Subclasses should override this method.
	 */
	protected Command primCreateCommand(ModifierHelper aHelper) {
		Command command = doCreateCommand(aHelper);
		if (aHelper.shouldUnsetValue()) {
			Object value = aHelper.getValue();
			if (value instanceof EObject && !((EObject) value).eIsProxy()) {
				command = new ProxyWrappingCommand(command, (EObject) value);
			}
		}
		return command;
	}

	protected Command doCreateCommand(ModifierHelper aHelper) {
		if (!aHelper.isComplete()) {
			setStatus(ERROR);
			return null;
		}
		Command command = null;
		if (aHelper.getFeature().isMany())
			command = createManyCommand(aHelper);
		else
			command = createSingleCommand(aHelper);

		if (null != command) {
			List localHelpers = ModifierHelperRegistry.getInstance().getHelpers(aHelper);
			if (null != localHelpers) {
				extendedHelpers.addAll(localHelpers);
			}
		}
		return command;

	}

	/**
	 * Run using
	 * 
	 * @aHelper. This will set a MOF attibute value to the owner of the helper.
	 */
	protected void primRun(ModifierHelper aHelper) {
		if (aHelper.isComplete()) {
			Object value = getValue(aHelper);
			if (valueChanged(aHelper.getOwner(), aHelper.getFeature(), value, aHelper.shouldUnsetValue()))
				setObjectAttribute(aHelper.getOwner(), aHelper.getFeature(), value, aHelper.shouldUnsetValue());
		}
	}

	/**
	 * The default is to do nothing. Subclasses should override this method if they are using
	 * recordable commands. The implementation of this method should update the MOF model directly.
	 * Any modification will be recorded.
	 */
	public void run() {
		if (!getHelpers().isEmpty()) {
			Iterator it = getHelpers().iterator();
			while (it.hasNext())
				run((ModifierHelper) it.next());
		}
	}

	/**
	 * Run using
	 * 
	 * @aHelper's ownerHelper first before running with
	 * @aHelper.
	 */
	protected void run(ModifierHelper aHelper) {
		if (aHelper != null) {
			run(aHelper.getOwnerHelper());
			primRun(aHelper);
		}
	}

	protected void setNewObjectAttributes(EObject anObject, ModifierHelper aHelper) {
		HashMap attributes = aHelper.getAttributes();
		Iterator it = attributes.keySet().iterator();
		EStructuralFeature feature;
		Object value = null;
		while (it.hasNext()) {
			feature = (EStructuralFeature) it.next();
			value = attributes.get(feature);
			setObjectAttribute(anObject, feature, value, false);
		}
	}

	protected void setObjectAttribute(EObject anObject, EStructuralFeature aFeature, Object aValue, boolean shouldUnsetValue) {
		if (aFeature.isMany())
			setObjectManyAttribute(anObject, aFeature, aValue, shouldUnsetValue);
		else
			setObjectSingleAttribute(anObject, aFeature, aValue, shouldUnsetValue);
	}

	protected void setObjectEnumAttribute(EObject anObject, EStructuralFeature aFeature, Object aValue) {
		Enumerator enumerator = getEnumerator(aFeature, aValue);
		anObject.eSet(aFeature, enumerator);
	}

	protected void setObjectManyAttribute(EObject anObject, EStructuralFeature aFeature, Object aValue, boolean shouldUnsetValue) {
		List list = (List) anObject.eGet(aFeature);
		if (shouldUnsetValue)
			list.remove(aValue);
		else
			list.add(aValue);
	}

	protected void setObjectSingleAttribute(EObject anObject, EStructuralFeature aFeature, Object aValue, boolean shouldUnsetValue) {
		if (shouldUnsetValue)
			anObject.eUnset(aFeature);
		else if (aFeature.getEType() instanceof EEnum)
			setObjectEnumAttribute(anObject, aFeature, aValue);
		else
			anObject.eSet(aFeature, aValue);
	}

	protected boolean singleValueChanged(EObject anObject, EStructuralFeature aFeature, Object aValue, boolean isUnset) {
		if (aFeature.getEType() instanceof EEnum)
			return enumValueChanged(anObject, aFeature, aValue);

		Object existingValue = anObject.eGet(aFeature);
		if (existingValue == null && aValue == null)
			return false;
		if (existingValue != null && !existingValue.equals(aValue))
			return true;
		if (aValue != null && !aValue.equals(existingValue))
			return true;
		return false;
	}

	protected boolean valueChanged(EObject anObject, EStructuralFeature aFeature, Object aValue, boolean isUnset) {
		if (aFeature.isMany())
			return manyValueChanged(anObject, aFeature, aValue, isUnset);
		return singleValueChanged(anObject, aFeature, aValue, isUnset);
	}
}