package org.eclipse.wst.validation.internal;

import org.eclipse.osgi.util.NLS;

public class ValMessages extends NLS {
	
	private static final String BUNDLE_NAME = "org.eclipse.wst.validation.internal.messages"; //$NON-NLS-1$
	   
	public static String ErrConfig;	
	public static String ErrFilterRule;	  
	public static String ErrGroupName;
	public static String ErrDependencyVersion;
	
	public static String ErrGroupNoType;
	public static String ErrGroupInvalidType;
	
	public static String ErrPatternAttrib;
	public static String ErrTypeReq;
	public static String ErrType;
	
	public static String GroupInclude;
	
	public static String GroupExclude;
	
	public static String JobName;
	
	public static String JobIndexSave;
	
	public static String LogValStart;
	public static String LogValEnd;
	public static String LogValEndTime;
	
	public static String MigrationJobName;
	
	public static String RuleProjectNature;
	public static String RuleFileExt;
	public static String RuleFile;
	public static String RuleContentType;
	public static String RuleFacet;
	
	public static String FileExtWithCase;
	public static String FileExtWithoutCase;
	
	public static String TypeInclude;
	public static String TypeExclude;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, ValMessages.class);
	  }

}
