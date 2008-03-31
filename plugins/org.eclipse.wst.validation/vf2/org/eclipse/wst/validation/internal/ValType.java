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
}
