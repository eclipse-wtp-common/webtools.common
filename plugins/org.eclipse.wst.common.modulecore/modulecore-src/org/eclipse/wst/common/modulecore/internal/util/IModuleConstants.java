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
package org.eclipse.wst.common.modulecore.internal.util;

/**
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 */
public interface IModuleConstants {
	
    public static String DEPLOYABLE_MODULE_BUILDER_ID = "org.eclipse.wst.common.modulecore.DeployableModuleBuilder"; //$NON-NLS-1$
    public static String LOCAL_DEPENDENCY_RESOLVER_ID = "org.eclipse.wst.common.modulecore.LocalDependencyResolver"; //$NON-NLS-1$
    
	public final static String WTPMODULE_FILE_NAME = "wtpModule"; //$NON-NLS-1$
	public final static String MODULE_PLUG_IN_ID = "org.eclipse.wst.common.modulecore"; //$NON-NLS-1$
	public final static String MODULE_NATURE_ID = "org.eclipse.wst.common.modulecore.ModuleCoreNature"; //$NON-NLS-1$

	//moduleTypes
	public final static String JST_WEB_MODULE = "jst.web"; //$NON-NLS-1$
	public final static String JST_EJB_MODULE = "jst.ejb"; //$NON-NLS-1$
	public final static String JST_UTILITY_MODULE = "jst.utility"; //$NON-NLS-1$
	public final static String WST_WEB_MODULE = "wst.web"; //$NON-NLS-1$
	public final static String JST_EAR_MODULE = "jst.ear"; //$NON-NLS-1$
	public final static String JST_APPCLIENT_MODULE = "jst.appclient"; //$NON-NLS-1$
	
	public final static String J2EE_VERSION_1_2 = "1.2"; //$NON-NLS-1$
	public final static String J2EE_VERSION_1_3 = "1.3"; //$NON-NLS-1$
	public final static String J2EE_VERSION_1_4 = "1.4"; //$NON-NLS-1$
	
	
	public final static String JSP_VERSION_1_1 = "1.1"; //$NON-NLS-1$
	public final static String JSP_VERSION_1_2 = "1.2"; //$NON-NLS-1$
	public final static String JSP_VERSION_2_0 = "2.0";	//$NON-NLS-1$	
	
	
	public final static String SERVLET_VERSION_2_2 = "2.2"; //$NON-NLS-1$
	public final static String SERVLET_VERSION_2_3 = "2.3"; //$NON-NLS-1$
	public final static String SERVLET_VERSION_2_4 = "2.4"; //$NON-NLS-1$	
	 
}
