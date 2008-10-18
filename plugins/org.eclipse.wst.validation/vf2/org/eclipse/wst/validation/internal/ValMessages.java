/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

import org.eclipse.osgi.util.NLS;

public class ValMessages extends NLS {
	
	private static final String BUNDLE_NAME = "org.eclipse.wst.validation.internal.messages"; //$NON-NLS-1$
	   
	public static String ConfigError;
	
	public static String DecodeError1;
	public static String Error20;
	
	public static String ErrConfig;	
	public static String ErrFilterRule;	  
	public static String ErrGroupName;
	public static String ErrDependencyVersion;
	
	public static String ErrGroupNoType;
	public static String ErrGroupInvalidType;
	
	public static String ErrPatternAttrib;
	public static String ErrTypeReq;
	public static String ErrType;
	
	public static String VbfExcSyntaxNoValClass;
	public static String VbfExcSyntaxNoValRun;
	public static String VbfExcSyntaxNoValNull;
	
	public static String GroupInclude;
	
	public static String GroupExclude;
	
	public static String JobName;
	public static String JobNameMonitor;
	
	public static String JobIndexSave;
	
	public static String LogValStart;
	public static String LogValEnd;
	public static String LogValEndTime;
	public static String LogValSummary;
	public static String LogValSummary2;
	public static String LogSession;
	
	public static String MigrationJobName;
	
	public static String RogueValidator;
	
	public static String RuleProjectNature;
	public static String RuleFileExt;
	public static String RuleFile;
	public static String RuleFolder;
	public static String RuleFull;
	public static String RuleContentType;
	public static String RuleFacet;
	
	public static String ContentTypeExact;
	public static String ContentTypeNotExact;
	
	public static String FileExtWithCase;
	public static String FileExtWithoutCase;

	public static String SevError;
	public static String SevWarning;
	public static String SevIgnore;

	public static String TimeUnder;
	public static String TimeNano;
	public static String TimeMicro;
	public static String TimeSec;
	public static String TimeMin;
	
	public static String TypeInclude;
	public static String TypeExclude;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, ValMessages.class);
	  }

}
