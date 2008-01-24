package org.eclipse.wst.validation.ui.internal;

import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.internal.ValOperation;
import org.eclipse.wst.validation.internal.ValidationRunner;
import org.eclipse.wst.validation.ui.internal.dialog.ResultsDialog;

/**
 * Run a manual validation. 
 * @author karasiuk
 *
 */
public class ManualValidationRunner extends WorkspaceJob {
	
	private Map<IProject, Set<IResource>> 	_projects;
	private boolean	_isManual;
	private boolean	_isBuild;
	private boolean	_showResults;
	
	/**
	 * Validate the selected projects and/or resources.
	 * 
	 * @param projects The selected projects. The key is an IProject and the value is the Set of
	 * IResources that were selected. Often this will be every resource in the project.
	 * 
	 * @param isManual is this a manual validation?
	 * 
	 * @param isBuild is this a build based validation?
	 * 
	 * @param showResults when the validation is finished, show the results in a dialog box.
	 */
	public static void validate(Map<IProject, Set<IResource>> projects, boolean isManual, 
			boolean isBuild, boolean showResults){
		ManualValidationRunner me = new ManualValidationRunner(projects, isManual, isBuild, showResults);
		me.schedule();
	}
	
	private ManualValidationRunner(Map<IProject, Set<IResource>> projects, boolean isManual, 
			boolean isBuild, boolean showResults){
		super(ValUIMessages.Validation);
		_projects = projects;
		_isManual = isManual;
		_isBuild = isBuild;
		_showResults = showResults;
	}

	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		
		long start = System.currentTimeMillis();
		final ValOperation vo = ValidationRunner.validate(_projects, _isManual, _isBuild, monitor);
		final long time = System.currentTimeMillis() - start;
		int resourceCount = 0;
		for (Set s : _projects.values())resourceCount += s.size();
		final int finalResourceCount = resourceCount;
		if (vo.getResult().isCanceled())return Status.CANCEL_STATUS;
		
		if (_showResults){
			Display display = Display.getDefault();
			Runnable run = new Runnable(){

				public void run() {
					ValidationResult vr = vo.getResult();
					ResultsDialog rd = new ResultsDialog(null, vr, time, finalResourceCount);
					rd.open();
				}
				
			};
			display.syncExec(run);			
		}
		return Status.OK_STATUS;
	}
}
