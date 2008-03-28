package org.eclipse.wst.validation.internal;

/**
 * Type of validation. Build or Manual?
 * @author karasiuk
 *
 */
public enum ValType {
	/** The validation is triggered via a resource change and the build process. */
	Build, 
	
	/** The user manually requested the validation. */
	Manual,
	
	/** 
	 * Used for selecting validators, where all the type are enabled before the validator is selected.
	 */
	All,
	
	/**
	 * Used to ensure that the validator is turned on for at least one of the validation types (i.e. Build
	 * or Manual).
	 */
	AtLeastOne,
	
	/** Not applicable. The Build or Manual setting is not used to filter out any validators. */
	NA;
	
}
