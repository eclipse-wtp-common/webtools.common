package org.eclipse.wst.validation.internal;

import org.eclipse.core.resources.IProject;

/**
 * A call back for listeners that are interested when a project changes.
 * @author karasiuk
 *
 */
public interface IProjectChangeListener {
	
	int ProjectOpened = 1;
	int ProjectClosed = 2;
	int ProjectDeleted = 4;
	
	/** 8 - Something in the project description has changed. For example, a Nature was added. */
	int ProjectChanged = 8;
	
	/**
	 * The project has changed in some way.
	 * 
	 * @param project The project that has changed.
	 * @param type The type of change. This will be one of the Project constants;
	 */
	void projectChanged(IProject project, int type);
}
