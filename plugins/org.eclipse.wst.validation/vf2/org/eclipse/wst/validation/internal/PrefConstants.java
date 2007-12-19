package org.eclipse.wst.validation.internal;

/**
 * The constants that are used in the preference store so save the validator configurations. 
 * <p>
 * The entries in the preference hierarchy are organized something like this:
 * <pre>
 * /instance/frame-work-id/saveAuto=true|false
 * /instance/frame-work-id/suspend=true|false
 * /instance/frame-work-id/vf.version=2
 * /instance/frame-work-id/filters/val-id/build=true|false
 * /instance/frame-work-id/filters/val-id/manual=true|false
 * /instance/frame-work-id/filters/val-id/version=someNumber
 * /instance/frame-work-id/filters/val-id/groups/0/type=include|exclude
 * /instance/frame-work-id/filters/val-id/groups/0/rules/0/type=file|fileext|projectNature|...
 * /instance/frame-work-id/filters/val-id/groups/0/rules/0/pattern=...
 * /instance/frame-work-id/filters/val-id/groups/0/rules/1/type=file|fileext|projectNature|...
 * /instance/frame-work-id/filters/val-id/groups/0/rules/1/pattern=...
 * 
 * </pre>
 * 
 * @author karasiuk
 *
 */
public interface PrefConstants {
	
	/** filters - node where all the filters are saved. */
	String filters = "filters"; //$NON-NLS-1$
	
	/** build - is the validator enabled for builds? */
	String build = "build"; //$NON-NLS-1$
	
	/** delegate - the delegating implementation to use */
	String delegate = "delegate"; //$NON-NLS-1$
	
	/** manual - is the validator enabled for manual validation? */
	String manual = "manual"; //$NON-NLS-1$
	
	/** 
	 * version - version of the filter definition. This is something that a client can use to keep track
	 * of changes that they might make to their validation extension.
	 */
	String version = "version"; //$NON-NLS-1$
	
	/** vf.version - version of the validation framework. */
	String frameworkVersion = "vf.version"; //$NON-NLS-1$
	
	/** groups - filter group. */
	String groups = "groups"; //$NON-NLS-1$
	
	/** type - type of group, either include or exclude. */
	String type = "type"; //$NON-NLS-1$
	
	/** type - the type of rule. */
	String ruleType = "type"; //$NON-NLS-1$
	
	/** rules - a filter rule. */
	String rules = "rules"; //$NON-NLS-1$
	
	/** pattern - the pattern part of the rule. */
	String pattern = "pattern"; //$NON-NLS-1$
	
	/** saveAuto - can we save all the resources automatically? true/false */
	String saveAuto = "saveAuto"; //$NON-NLS-1$
	
	/** stateTS - plug-in state time stamp */
	String stateTS = "stateTS"; //$NON-NLS-1$
	
	/** suspend - suspend all validation? true/false */
	String suspend = "suspend"; //$NON-NLS-1$
	
	/** confirmDialog - should we show a confirmation dialog when doing a manual validation? */
	String confirmDialog = "confirmDialog"; //$NON-NLS-1$
	
	/** override - should we show projects to override the global preferences? */
	String override = "override"; //$NON-NLS-1$
	
	
}
