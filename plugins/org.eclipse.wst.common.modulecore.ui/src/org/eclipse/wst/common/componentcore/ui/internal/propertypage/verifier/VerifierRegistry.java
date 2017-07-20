/*******************************************************************************
 * Copyright (c) 2010, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.common.componentcore.ui.internal.propertypage.verifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

public class VerifierRegistry {
	/**
	 *  
	 */
	private static VerifierRegistry INSTANCE;
	private HashMap assemblyVerifierExtensions = new HashMap();

	public VerifierRegistry() {
		super();
	}

	/**
	 * @param verifier
	 * @param serverTargets
	 * @param components
	 */
	protected void register(IConfigurationElement verifier, List serverTargets, List components) {
		HashMap targetVerifiers;
		for (Iterator iter = components.iterator(); iter.hasNext();) {
			String compID = (String) iter.next();
			for (Iterator iterator = serverTargets.iterator(); iterator.hasNext();) {
				String runtimeID = (String) iterator.next();
				targetVerifiers = getVerifierExtensions(compID);
				getTargetVerifiers(targetVerifiers, runtimeID).add(verifier);
			}
		}
	}

	private List getVerifiers(String compID, String serverTarget) {
		HashMap targetVerifiers = getVerifierExtensions(compID);
		return getTargetVerifiers(targetVerifiers, serverTarget);
	}

	protected static VerifierRegistry instance() {
		if (INSTANCE == null) {
			INSTANCE = new VerifierRegistry();
			readRegistry();
		}
		return INSTANCE;
	}

	/**
	 *  
	 */
	private static void readRegistry() {
		VerifierRegistryReader reader = new VerifierRegistryReader();
		reader.readRegistry();
	}

	/**
	 * @param targetVerifiers
	 * @param serverTarget
	 */
	private List getTargetVerifiers(HashMap targetVerifiers, String serverTarget) {
		if (targetVerifiers.get(serverTarget) == null)
			targetVerifiers.put(serverTarget, new ArrayList());
		return (List) targetVerifiers.get(serverTarget);
	}

	/**
	 * @param compID
	 * @return
	 */
	private HashMap getVerifierExtensions(String compID) {
		if (getVerifierExtensions().get(compID) == null)
			getVerifierExtensions().put(compID, new HashMap());
		return (HashMap) getVerifierExtensions().get(compID);
	}

	/**
	 * @return Returns the verifierExtensions.
	 */
	private HashMap getVerifierExtensions() {
		return assemblyVerifierExtensions;
	}

	/**
	 * @param facetTypeID
	 * @param runtime
	 * @return List of IConfigurationElements representing instances of IDeploymentAssemblyVerifier
	 */
	public List getVerifierExtensions(String facetTypeID, String runtimeId) {
		// Identifier used by verifiers that will run for any runtime
		String allRuntimes = "org.eclipse.wst.common.modulecore.ui.deploymentAssemblyVerifier.anyruntime"; //$NON-NLS-1$
		if (runtimeId == null)
			runtimeId = "None"; //$NON-NLS-1$
		// Get the verifiers specific for the target runtime
		List verifiers = getVerifiers(facetTypeID, runtimeId);		
		if (verifiers == null)
			verifiers = Collections.EMPTY_LIST;
		// Get the verifiers for any runtime
		List genericVerifiers = getVerifiers(facetTypeID, allRuntimes);
		if (genericVerifiers == null)
			genericVerifiers = Collections.EMPTY_LIST;
		// Merge both verifiers into one list and return
		List result = new ArrayList(verifiers);
		result.addAll(genericVerifiers);
		return result;
	}


}
