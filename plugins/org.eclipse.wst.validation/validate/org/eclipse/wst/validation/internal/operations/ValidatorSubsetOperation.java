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


import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.wst.validation.internal.FilterUtil;
import org.eclipse.wst.validation.internal.InternalValidatorManager;
import org.eclipse.wst.validation.internal.RegistryConstants;
import org.eclipse.wst.validation.internal.ResourceConstants;
import org.eclipse.wst.validation.internal.ResourceHandler;
import org.eclipse.wst.validation.internal.ValidationRegistryReader;
import org.eclipse.wst.validation.internal.ValidatorMetaData;


/**
 * Run some validators on a given IProject. Any validators which cannot be loaded or which are not
 * registered against this type of project will be ignored.
 * 
 * This operation is not intended to be subclassed outside of the validation framework.
 */
public class ValidatorSubsetOperation extends ValidationOperation {
	protected static final String DEFAULT_DEFAULTEXTENSION = null; // By default, assume that there

	// is no default fallback
	// extension

	/**
	 * Create an operation that runs a full validation on the named validators either if validation
	 * needs to (@see ValidatorSubsetOperation(IProject)) or if <code>force</code> is true.
	 * 
	 * IProject must exist and be open.
	 * 
	 * If async is true, the validation will run all thread-safe validators in the background
	 * validation thread, and all other validators in the main thread. If async is false, all
	 * validators will run in in the main thread.
	 */
	public ValidatorSubsetOperation(IProject project, boolean force, boolean async) {
		this(project, force, RegistryConstants.ATT_RULE_GROUP_DEFAULT, async);
	}

	/**
	 * Create an operation that runs a full validation on the named validators using the
	 * <code>ruleGroup</code> pass. Use this constructor only if you want to run a validator that
	 * supports the two passes: FAST and FULL.
	 * 
	 * If force is true, validation is run whether or not it needs to.
	 * 
	 * IProject must exist and be open.
	 * 
	 * If async is true, the validation will run all thread-safe validators in the background
	 * validation thread, and all other validators in the main thread. If async is false, all
	 * validators will run in in the main thread.
	 */
	public ValidatorSubsetOperation(IProject project, IWorkbenchContext aWorkenchContext, boolean force, int ruleGroup, boolean async) {
		super(project, aWorkenchContext, null, null, ruleGroup, force, async);
	}
	

	/**
	 * Create an operation that runs a full validation on the named validators using the
	 * <code>ruleGroup</code> pass. Use this constructor only if you want to run a validator that
	 * supports the two passes: FAST and FULL.
	 * 
	 * If force is true, validation is run whether or not it needs to.
	 * 
	 * IProject must exist and be open.
	 * 
	 * If async is true, the validation will run all thread-safe validators in the background
	 * validation thread, and all other validators in the main thread. If async is false, all
	 * validators will run in in the main thread.
	 */
	public ValidatorSubsetOperation(IProject project, boolean force, int ruleGroup, Object[] changedResources, boolean async) {
		super(project, null, null, ruleGroup, force, async);
		setEnabledValidators(ValidatorManager.getManager().getManualEnabledValidators(project));
		setFileDeltas(FilterUtil.getFileDeltas(getEnabledValidators(), changedResources, false));
	}
	
	/**
	 * Create an operation that runs a full validation on the named validators using the
	 * <code>ruleGroup</code> pass. Use this constructor only if you want to run a validator that
	 * supports the two passes: FAST and FULL.
	 * 
	 * If force is true, validation is run whether or not it needs to.
	 * 
	 * IProject must exist and be open.
	 * 
	 * If async is true, the validation will run all thread-safe validators in the background
	 * validation thread, and all other validators in the main thread. If async is false, all
	 * validators will run in in the main thread.
	 */
	public ValidatorSubsetOperation(IProject project, boolean force, int ruleGroup, boolean async) {
		super(project, null, null, ruleGroup, force, async);
	}

	/**
	 * The fileExtension parameter must be ".X", where X is the extension. Do not type "*.X" or "X"
	 * (i.e., without the dot). The parameter could also be the file name, e.g. "foo.X".
	 * 
	 * This constructor should be used when the invoker wishes to force validation on certain
	 * resources, without waiting for the user to save their changes.
	 * 
	 * An IllegalArgumentException is thrown if there are no validators registered for the
	 * fileExtension on the given IProject.
	 * 
	 * IProject must exist and be open.
	 * 
	 * If async is true, the validation will run all thread-safe validators in the background
	 * validation thread, and all other validators in the main thread. If async is false, all
	 * validators will run in in the main thread.
	 */
	public ValidatorSubsetOperation(IProject project, String fileExtension, Object[] changedResources, boolean async) throws IllegalArgumentException {
		this(project, fileExtension, DEFAULT_DEFAULTEXTENSION, changedResources, async);
	}

	/**
	 * The fileExtension parameter must be ".X", where X is the extension. Do not type "*.X" or "X"
	 * (i.e., without the dot). The parameter could also be the file name, e.g. "foo.X".
	 * 
	 * This constructor should be used when the invoker wishes to force validation on certain
	 * resources, without waiting for the user to save their changes.
	 * 
	 * If there are no validators configured on files named ".X", then use the validators configured
	 * on validators named ".Y", where defaultExtension identifies the fallback extension to use.
	 * defaultExtension follows the same syntax as fileExtension.
	 * 
	 * An IllegalArgumentException is thrown if there are no validators registered for the
	 * fileExtension or defaultExtension on the given IProject.
	 * 
	 * IProject must exist and be open.
	 * 
	 * If async is true, the validation will run all thread-safe validators in the background
	 * validation thread, and all other validators in the main thread. If async is false, all
	 * validators will run in in the main thread.
	 */
	public ValidatorSubsetOperation(IProject project, String fileExtension, String defaultExtension, Object[] changedResources, boolean async) throws IllegalArgumentException {
		super(project, shouldForce(changedResources), async);

		boolean filterIn = false; // force the resources to be filtered in even if the validator
		// doesn't normally take them?
		ValidatorMetaData[] vmds = InternalValidatorManager.getManager().getValidatorsForExtension(project, fileExtension); // return
		// a
		// list
		// of
		// validators
		// which
		// are
		// configured
		// to
		// run
		// on
		// files
		// with
		// that
		// extension.
		// A
		// validator
		// will
		// be
		// in
		// the
		// list
		// whether
		// it
		// has
		// been
		// enabled
		// or
		// disabled
		// by
		// the
		// user.
		if ((defaultExtension != null) && ((vmds == null) || (vmds.length == 0))) {
			filterIn = true;
			vmds = InternalValidatorManager.getManager().getValidatorsForExtension(project, defaultExtension);
		}

		if ((vmds == null) || (vmds.length == 0)) {
			throw new IllegalArgumentException();
		}

		setEnabledValidators(InternalValidatorManager.wrapInSet(vmds));

		setFileDeltas(FilterUtil.getFileDeltas(getEnabledValidators(), changedResources, filterIn)); // construct
		// an
		// array
		// of
		// IFileDelta[]
		// to
		// wrap
		// the
		// Object[];
		// one
		// IFileDelta
		// for
		// each
		// Object
		// in
		// the
		// array
	}

	/**
	 * This constructor is provided for the validation async testing, and is not intended to be
	 * called outside the validation framework.
	 * 
	 * Run validation on the changed resources with the given validators. All resources must be from
	 * the same project; if they're not, an IllegalArgumentException will be thrown. All validators
	 * must be able to run on the resources' project; if not, an IllegalArgumentException will be
	 * thrown. If the vmds are either empty or null, an IllegalArgumentExeption will be thrown. If
	 * the project is closed or doesn't exist then an IllegalArgumentException will be thrown.
	 * 
	 * The ifileDeltaType is one of the IFileDelta constants: ADDED, CHANGED, or DELETED.
	 * 
	 * IProject must exist and be open.
	 * 
	 * If async is true, the validation will run all thread-safe validators in the background
	 * validation thread, and all other validators in the main thread. If async is false, all
	 * validators will run in in the main thread.
	 */
	public ValidatorSubsetOperation(IProject project, ValidatorMetaData[] vmds, IResource[] changedResources, int ifileDeltaType, boolean force, boolean async) throws IllegalArgumentException {
		// Have to have the IProject as a parameter because ValidationOperation needs the IProject,
		// and the super(..)
		// must be called before anything else in this constructor is called.
		super(project, force, async);

		if ((vmds == null) || (vmds.length == 0)) {
			throw new IllegalArgumentException(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_BADVMD));
		}

		if (!project.isOpen()) {
			throw new IllegalArgumentException(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_OPENPRJ, new String[]{project.getName()}));
		}
		if (!project.exists()) {
			throw new IllegalArgumentException(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_EXISTPRJ, new String[]{project.getName()}));
		}

		if ((changedResources != null) && (changedResources.length > 0)) {
			Set<IProject> tempSet = new HashSet<IProject>();
			for (int i = 0; i < changedResources.length; i++) {
				IProject p = changedResources[i].getProject();
				if (!p.isOpen()) {
					throw new IllegalArgumentException(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_OPENPRJ, new String[]{p.getName()}));
				}
				if (!p.exists()) {
					throw new IllegalArgumentException(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_EXISTPRJ, new String[]{p.getName()}));
				}
				tempSet.add(project);
			}

			if (!tempSet.contains(project)) {
				throw new IllegalArgumentException(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_BADPRJ, new String[]{project.getName()}));
			}
			if (tempSet.size() != 1) {
				StringBuffer buffer = new StringBuffer("\n"); //$NON-NLS-1$
				Iterator<IProject> iterator = tempSet.iterator();
				while (iterator.hasNext()) {
					IProject p = iterator.next();
					buffer.append("\t"); //$NON-NLS-1$
					buffer.append(p.getName());
					if (iterator.hasNext()) {
						buffer.append(", "); //$NON-NLS-1$
					}
				}
				throw new IllegalArgumentException(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_MULTIPRJ, new String[]{buffer.toString()}));
			}
		}

		for (int i = 0; i < vmds.length; i++) {
			ValidatorMetaData vmd = vmds[i];
			if (!ValidationRegistryReader.getReader().isConfiguredOnProject(vmd, project)) {
				throw new IllegalArgumentException(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_BADVAL, new String[]{vmd.getValidatorDisplayName(), project.getName()}));
			}
		}

		setEnabledValidators(InternalValidatorManager.wrapInSet(vmds));
		setFileDeltas(FilterUtil.getFileDeltas(getEnabledValidators(), changedResources, ifileDeltaType)); // construct
		// an array of IFileDelta[] to wrap the IResource[]; one IFileDelta for each IResource in the array
	}

	/**
	 * Given an array of fully-qualified class names of validators, create the list of validators to
	 * be run. The array is not checked for duplicates or for invalid validators (i.e., a validator
	 * of that class type is not loaded, or the validator is loaded but cannot run against this type
	 * of IProject.)
	 */
	public void setValidators(String[] validatorNames) throws IllegalArgumentException {
		Set<ValidatorMetaData> enabled = new HashSet<ValidatorMetaData>();
		for (String name : validatorNames) {
			ValidatorMetaData vmd = ValidationRegistryReader.getReader().getValidatorMetaData(name);
			if (vmd == null) {
				// No validator, with that plugin id, can be run on that project.
				// Either the validator isn't installed, or the IProject passed in
				// doesn't have the necessary nature.
				throw new IllegalArgumentException(name);
			}
			enabled.add(vmd);
		}
		setEnabledValidators(enabled);
	}

	/**
	 * @deprecated Will be removed in Milestone 3. Use setForce(boolean)
	 */
	public void setAlwaysRun(boolean force) {
		setForce(force);
	}
}
