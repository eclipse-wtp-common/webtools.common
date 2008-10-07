package org.eclipse.wst.validation.tests.testcase;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.Validator.V1;
import org.eclipse.wst.validation.internal.ConfigurationManager;
import org.eclipse.wst.validation.internal.GlobalConfiguration;
import org.eclipse.wst.validation.internal.ValManager;
import org.eclipse.wst.validation.internal.ValPrefManagerGlobal;
import org.eclipse.wst.validation.internal.ValidatorMetaData;
import org.eclipse.wst.validation.internal.operations.ValidatorManager;

public class TestEnvironment {
	
	public static final boolean DEBUG = true;
	
	private IWorkspace	_workspace;
	private HashMap<String, IProject> _projects = new HashMap<String, IProject>(20);
	
	public TestEnvironment() throws CoreException {
		_workspace = ResourcesPlugin.getWorkspace();
		if (DEBUG){
			_workspace.getRoot().delete(true, true, null);
		}
	}
	
	public IPath addFolder(IPath root, String folderName) throws CoreException {
		IPath path = root.append(folderName);
		createFolder(path);
		return path;
	}
	
	public void incrementalBuild() throws CoreException{
		getWorkspace().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
	}
	
	/**
	 * Start a full build.
	 */
	public void fullBuild() throws CoreException{
		getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, null);
	}
	
	/**
	 * Do a full build, and wait until all the validation has finished.
	 * @param monitor
	 */
	public void fullBuild(IProgressMonitor monitor) throws CoreException, InterruptedException {
		fullBuild();
		Thread.sleep(1000);
		ValidationFramework.getDefault().join(monitor);
		Thread.sleep(2000);  // we need to sleep here to give the "finished" job a chance to run.		
	}
	
	private IFolder createFolder(IPath path) throws CoreException {
		if (path.segmentCount() <= 1)return null;
		
		IFolder folder = _workspace.getRoot().getFolder(path);
		if (!folder.exists()){
			folder.create(true, true, null);
		}
		return folder;
	}

	public IProject createProject(String name) throws CoreException {
		final IProject project = _workspace.getRoot().getProject(name);
		IWorkspaceRunnable create = new IWorkspaceRunnable() {

			public void run(IProgressMonitor monitor) throws CoreException {
				project.create(monitor);
				project.open(monitor);	
				ValidatorManager.addProjectBuildValidationSupport(project);
			}		
		};
		
		_workspace.run(create, null);
		_projects.put(name, project);
		
		return project;
	}
	
	public void dispose() throws CoreException {
		if (DEBUG)return;
		for (Iterator<IProject> it=_projects.values().iterator(); it.hasNext();){
			IProject project = it.next();
			project.delete(true, null);
		}
	}

	public IFile addFile(IPath folder, String fileName, String contents) throws CoreException, UnsupportedEncodingException {
		IPath filePath = folder.append(fileName);
		return createFile(filePath, contents.getBytes("UTF8"));
	}

	private IFile createFile(IPath filePath, byte[] contents) throws CoreException {
		IFile file = _workspace.getRoot().getFile(filePath);
		ByteArrayInputStream in = new ByteArrayInputStream(contents);
		if (file.exists())file.setContents(in, true, false, null);
		else file.create(in, true, null);
		return file;
	}
	
	public IWorkspace getWorkspace(){
		return _workspace;
	}

	public IProject findProject(String name) {
		IProject project = _workspace.getRoot().getProject(name);
		if (project.exists())return project;
		return null;
	}
	
	/**
	 * Since other plug-ins can add and remove validators, turn off all the ones that are not part of
	 * these tests.
	 * 
	 * @param validatorPrefix The start of the validator ID. For example "T5".
	 */
	public static void enableOnlyTheseValidators(String validatorPrefix) throws InvocationTargetException {
		Validator[] vals = ValManager.getDefault().getValidatorsCopy();
		String name = "org.eclipse.wst.validation.tests." + validatorPrefix;
		for (Validator v : vals){
			boolean enable = v.getValidatorClassname().startsWith(name);
			v.setBuildValidation(enable);
			v.setManualValidation(enable);
		}
		ValPrefManagerGlobal gp = ValPrefManagerGlobal.getDefault();
		gp.saveAsPrefs(vals);		
		TestEnvironment.saveV1Preferences(vals);
	}

	
	/**
	 * Save the V1 preferences.
	 */
	public static void saveV1Preferences(Validator[] validators) throws InvocationTargetException {
		GlobalConfiguration gc = ConfigurationManager.getManager().getGlobalConfiguration();
		
		List<ValidatorMetaData> manual = new LinkedList<ValidatorMetaData>();
		List<ValidatorMetaData> build = new LinkedList<ValidatorMetaData>();
		for (Validator v : validators){
			V1 v1 = v.asV1Validator();
			if (v1 == null)continue;
			if (v1.isManualValidation())manual.add(v1.getVmd());
			if (v1.isBuildValidation())build.add(v1.getVmd());
		}
		
		ValidatorMetaData[] array = new ValidatorMetaData[manual.size()];
		gc.setEnabledManualValidators(manual.toArray(array));
		
		array = new ValidatorMetaData[build.size()];
		gc.setEnabledBuildValidators(build.toArray(array));

		gc.passivate();
		gc.store();
	}

	public void turnoffAutoBuild() throws CoreException {
		IWorkspaceDescription wd = _workspace.getDescription();
		if (wd.isAutoBuilding()){
			wd.setAutoBuilding(false);
			_workspace.setDescription(wd);
		}
		
	}
	
	public void turnOnAutoBuild() throws CoreException {
		IWorkspaceDescription wd = _workspace.getDescription();
		if (!wd.isAutoBuilding()){
			wd.setAutoBuilding(true);
			_workspace.setDescription(wd);
		}		
	}

}
