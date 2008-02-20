package org.eclipse.wst.validation.internal;

/**
 * A class that wants to be notified when the validator preferences change.
 * @author karasiuk
 *
 */
public interface IValChangedListener {
	
	/**
	 * The validators for the project have changed.
	 *  
	 * @param project the project can be null, which means that the global validation preferences have
	 * changed.
	 */
	public void validatorsForProjectChanged(org.eclipse.core.resources.IProject project);
}
