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
package org.eclipse.wst.common.componentcore.internal.util;

import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;

/**
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 */
public interface IModuleConstants {
	
	public static final String DOT_SETTINGS =  ".settings"; //$NON-NLS-1$
	
	public final static String COMPONENT_FILE_NAME = ".component"; //$NON-NLS-1$
	public final static Path COMPONENT_FILE_PATH = new Path(StructureEdit.MODULE_META_FILE_NAME);
    public final static String WTPMODULE_FILE_NAME = ".wtpmodules"; //$NON-NLS-1$
    public final static Path R1_MODULE_META_FILE_PATH = new Path(".settings/.component"); //$NON-NLS-1$
	public final static Path WTPMODULE_FILE_PATH = new Path(WTPMODULE_FILE_NAME);
	public final static String MODULE_PLUG_IN_ID = "org.eclipse.wst.common.modulecore"; //$NON-NLS-1$
	public final static String MODULE_NATURE_ID = "org.eclipse.wst.common.modulecore.ModuleCoreNature"; //$NON-NLS-1$

	//moduleTypes
	public final static String JST_WEB_MODULE = "jst.web"; //$NON-NLS-1$
	public final static String JST_EJB_MODULE = "jst.ejb"; //$NON-NLS-1$
	public final static String JST_UTILITY_MODULE = "jst.utility"; //$NON-NLS-1$
	public final static String WST_WEB_MODULE = "wst.web"; //$NON-NLS-1$
	public final static String JST_EAR_MODULE = "jst.ear"; //$NON-NLS-1$
	public final static String JST_APPCLIENT_MODULE = "jst.appclient"; //$NON-NLS-1$
	public final static String JST_CONNECTOR_MODULE = "jst.connector"; //$NON-NLS-1$
	public final static String JST_JAVA = "jst.java"; //$NON-NLS-1$
	
	// Extensions
	public final static String EAR_EXTENSION = ".ear"; //$NON-NLS-1$
	public final static String WAR_EXTENSION = ".war"; //$NON-NLS-1$
	public final static String RAR_EXTENSION = ".rar"; //$NON-NLS-1$
	public final static String JAR_EXTENSION = ".jar"; //$NON-NLS-1$
	
	public final static String DEPENDENT_MODULE = "dependent"; //$NON-NLS-1$
    
    //Property Constants
    public final static String PROJ_REL_JAVA_OUTPUT_PATH = "java-output-path"; //$NON-NLS-1$ 
    
    public final static String CONTEXTROOT = "context-root"; //$NON-NLS-1$
	 
}
