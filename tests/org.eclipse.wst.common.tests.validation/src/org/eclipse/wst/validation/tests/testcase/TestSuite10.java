/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.validation.tests.testcase;

import java.io.UnsupportedEncodingException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.validation.internal.model.FilterRule;

/** Test the new target runtime filter rule. Can be used for new filter rules in general*/
@SuppressWarnings("restriction")
public class TestSuite10 extends TestCase {
	
	private TestEnvironment _env;
	private IProject		_project;
	
	private static final String TARGET_RUNTIME_VALIDATOR_ID = "org.eclipse.wst.common.tests.validation.TargetRuntimeValidator"; //$NON-NLS-1$
	private static final String TARGET_RUNTIME				= "targetRuntime"; 	//$NON-NLS-1$
	private static final String RULES						= "rules"; 			//$NON-NLS-1$
	private static final String ID							= "id"; 			//$NON-NLS-1$
	
	public static Test suite() {
		return new TestSuite(TestSuite10.class);
	} 
	
	public TestSuite10(String name){
		super(name);
	}	

	protected void setUp() throws Exception {
		super.setUp();
		_env = new TestEnvironment();
		_project = _env.createProject("TestSuite10");
		
		TestEnvironment.enableOnlyTheseValidators("T10");
	}
	
	protected void tearDown() throws Exception {
		_project.delete(true, null);
		_env.dispose();
		super.tearDown();
	}
	
	/**
	 * Ensure the target runtime rule is working.
	 * @throws Exception
	 */
	
	public void testTargetRuntimeFilterRule() throws CoreException, UnsupportedEncodingException, InterruptedException {
		
		String ruleName = null;
		String ruleID = null;
		
		IConfigurationElement[] children = getIncludesAndExcludes(TARGET_RUNTIME_VALIDATOR_ID);
		for (IConfigurationElement child : children) {
			IConfigurationElement[] rules = child.getChildren(RULES);
			for (int i=0; i<rules.length; i++){
				IConfigurationElement[] r = rules[i].getChildren();
				for(int j=0; j<r.length; j++){
					IConfigurationElement rule = r[j];	
					ruleName = rule.getName();
								
					if(ruleName.equals(TARGET_RUNTIME)) {
						FilterRule filterRule = FilterRule.create(rule);
						assertNotNull(filterRule);
						ruleID = rule.getAttribute(ID);
						assertNotNull(ruleName);
						assertNotNull(ruleID);	
					}
				}
			}
		}		
	}
	
	private IConfigurationElement[] getIncludesAndExcludes(String validatorID){
		
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry.getExtensionPoint("org.eclipse.wst.validation", "validatorV2");
		
		for (IExtension ext : extensionPoint.getExtensions()){
			for (IConfigurationElement validator : ext.getConfigurationElements()){
				String id = ext.getUniqueIdentifier();
					if(id.equals(validatorID)) {
						return validator.getChildren();
					}
			}
		}
		
		return null;
	}
}
