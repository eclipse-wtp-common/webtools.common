package org.eclipse.wst.validation.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.validation.IPerformanceMonitor;
import org.eclipse.wst.validation.PerformanceCounters;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.model.GlobalPreferences;
import org.eclipse.wst.validation.internal.model.IValidatorVisitor;
import org.eclipse.wst.validation.internal.model.ProjectPreferences;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;
import org.osgi.service.prefs.BackingStoreException;

/**
 * A central place to keep track of all the validators.
 * @author karasiuk
 *
 */
public class ValManager implements IValChangedListener {
	
	private static ValManager _me;

	/** All the known, global, validators. If this is null it means that the validators have not been loaded yet. */
	private Validator[] _validators;
		
	/**
	 * Projects may be allowed to override the global validation settings. If that is the case then those
	 * project specific settings are saved here. If the key exists, but the value is null, then that
	 * means that the project has been checked and it does not have any specific settings.
	 */
	private Map<IProject, ProjectPreferences> _projectPreferences = 
		Collections.synchronizedMap(new HashMap<IProject, ProjectPreferences>(50));
	
	private GlobalPreferences _globalPreferences;
		
	public static synchronized ValManager getDefault(){
		if (_me == null)_me = new ValManager();
		return _me;
	}
	
	private ValManager(){
		ValPrefManagerGlobal.getDefault().addListener(this);
		ValPrefManagerProject.addListener(this);
	}
	
	/**
	 * Answer all the registered validators.
	 * 
	 * @return Answer an empty array if there are no validators.
	 */
	public Validator[] getValidators(){
		return getValidators2(null);
	}
		
	/**
	 * Answer all the validators that are in effect for the given project.
	 * <p>
	 * Individual projects may override the global validation preference settings. If this is allowed and if
	 * the project has it's own settings, then those validators are returned via this method.
	 * </p>
	 * <p>
	 * The following approach is used. For version 1 validators, the validator is only returned if it
	 * is defined to operate on this project type. This is the way that the previous version of the framework
	 * did it. For version 2 validators, they are all returned.
	 * </p>
	 * @param project this may be null, in which case the global preferences are used.
	 * @return
	 */
	public Validator[] getValidators(IProject project) throws ProjectUnavailableError {
		if (project == null)return getValidators2(null);
		if (!getGlobalPreferences().getOverride())return getValidators2(null);
		
		ProjectPreferences pp = getProjectPreferences(project);
		if (pp == null || !pp.getOverride())return getValidators2(null);
		return pp.getValidators();		
	}
	
	/**
	 * Answer the validator with the given id that is in effect for the given project.
	 * 
	 * @param dependencyId validator dependency id.
	 * @param project
	 * @return null if the validator is not found
	 */
	public Validator getValidator(String dependencyId, IProject project){
		Validator[] vals = getValidators(project);
		for (Validator v : vals){
			if (v.getDependencyId().equals(dependencyId))return v;
		}
		return null;
	}
	
	/**
	 * @see ValidationFramework#getValidator(String, IProject)
	 */
	public Validator getValidatorWithId(String id, IProject project){
		Validator[] vals = getValidators(project);
		for (Validator v : vals){
			if (v.getId().equals(id))return v;
		}
		return null;
	}
	
	/**
	 * Answer true if this project has enabled project preferences. That is, the global settings allow
	 * projects to override the global settings, and the project has settings that are enabled.
	 * 
	 * @param project this can be null, in which case this method will return false.
	 */
	public boolean hasEnabledProjectPreferences(IProject project){
		if (project == null)return false;
		if (!getGlobalPreferences().getOverride())return false;
		ProjectPreferences pp = getProjectPreferences(project);
		if (pp == null || !pp.getOverride())return false;
		return true;
	}
				
	/**
	 * Answer true if the resource has any enabled validators.
	 * 
	 * @param resource a file, folder or project.
	 * 
	 * @param isManual if true then the validator must be turned on for manual validation. 
	 * If false then the isManualValidation setting isn't used to filter out validators.
	 *   
	 * @param isBuild if true then the validator must be turned on for build based validation.
	 * If false then the isBuildValidation setting isn't used to filter out validators.  
	 */
	public boolean hasValidators(IResource resource, boolean isManual, boolean isBuild){
		if (resource instanceof IProject){
			IProject project = (IProject)resource;
			return ValManager.getDefault().getValidators(project).length > 0;
		}
		else if (resource instanceof IFolder){
			IFolder folder = (IFolder)resource;
			HasValidatorVisitor v = new HasValidatorVisitor(isManual, isBuild);
			return v.hasValidator(folder);
		}
		else {
			for (Validator val : ValManager.getDefault().getValidators(resource.getProject())){
				if (val.shouldValidate(resource, isManual, isBuild))return true;
			}			
		}
		return false;
	}
	
	/**
	 * Answer true if the project has disabled all of it's validators, or of project overrides are not
	 * allowed if global validation has been disabled.
	 * 
	 * @param project the project that is being consulted, or null if only the global settings are to be 
	 * checked.
	 */
	public boolean isDisabled(IProject project){
		GlobalPreferences gp = getGlobalPreferences();
		if (!gp.getOverride() || project == null)return gp.getDisableAllValidation();
		
		ProjectPreferences pp = getProjectPreferences(project);
		if (pp == null)return gp.getDisableAllValidation();
		return pp.getSuspend();		
	}
			
	/**
	 * Answer all the registered validators as they were defined by the extension points. That is
	 * answer the validators as if the user has never applied any customizations.
	 * 
	 * @return Answer an empty array if there are no validators.
	 */
	public static Validator[] getDefaultValidators() throws InvocationTargetException {
		Validator[] val = restoreDefaults2(null);
		
		TreeSet<Validator> set = new TreeSet<Validator>();
		for (Validator v : val)set.add(v);
		
		List<Validator> list = new LinkedList<Validator>();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		GlobalConfiguration gc = new GlobalConfiguration(root);
		gc.resetToDefault();
		for (ValidatorMetaData vmd : gc.getValidators()){
			list.add(Validator.create(vmd, gc, null));
		}							
					
		set.addAll(list);
		val = new Validator[set.size()];
		set.toArray(val);
		return val;
	}

	public static Validator[] getDefaultValidators(IProject project) throws InvocationTargetException {
		Validator[] val = restoreDefaults2(project);
		
		TreeSet<Validator> set = new TreeSet<Validator>();
		for (Validator v : val)set.add(v);
		
		List<Validator> list = new LinkedList<Validator>();
		ProjectConfiguration pc = new ProjectConfiguration(project);
		pc.resetToDefault();
		for (ValidatorMetaData vmd : pc.getValidators()){
			list.add(Validator.create(vmd, pc, project));
		}							
					
		set.addAll(list);
		val = new Validator[set.size()];
		set.toArray(val);
		return val;
	}

	/**
	 * Answer all the registered validators.
	 * 
	 * @param project the project to use for getting the version 1 validator settings. This can
	 * be null in which case the global preferences are used.
	 * 
	 * @return Answer an empty array if there are no validators.
	 */
	Validator[] getValidators2(IProject project) throws ProjectUnavailableError {
		// If I use a local variable I don't need to synchronize the method.
		Validator[] validators = _validators;
		if (project == null && validators != null)return validators;
				
		Validator[] val = loadExtensions(false, project);
		ValPrefManagerGlobal vpm = ValPrefManagerGlobal.getDefault();
		if (!vpm.loadPreferences(val)){
			val = restoreDefaults2(project);
			saveStateTimestamp();				
		}
		else {
			if (getGlobalPreferences().getStateTimeStamp() != Platform.getStateStamp())
				val = migrateSettings(val, project);
		}
		
		TreeSet<Validator> set = new TreeSet<Validator>();
		for (Validator v : val)set.add(v);
		
		List<Validator> list = new LinkedList<Validator>();
		try {
			ValidationConfiguration vc = ConfigurationManager.getManager().getConfiguration(project);
			for (ValidatorMetaData vmd : vc.getValidators()){
				list.add(Validator.create(vmd, vc, project));
			}							
			
		}
		catch (InvocationTargetException e){
			if (project != null && (!project.exists() || !project.isOpen()))
				throw new ProjectUnavailableError(project);
			ValidationPlugin.getPlugin().handleException(e);
		}
		
		set.addAll(list);
		val = new Validator[set.size()];
		set.toArray(val);
		if (project == null)_validators = val;
		return val;
	}
	
	private void saveStateTimestamp() {
		try {
			IEclipsePreferences prefs = ValidationFramework.getDefault().getPreferenceStore();
			long ts = Platform.getStateStamp();
			getGlobalPreferences().setStateTimeStamp(ts);
			prefs.putLong(PrefConstants.stateTS, ts);
			prefs.flush();
		}
		catch (BackingStoreException e){
			ValidationPlugin.getPlugin().handleException(e);
		}
	}

	/**
	 * The plug-in definitions may have changed, so check to see if any of the settings need to be updated.
	 */
	private Validator[] migrateSettings(Validator[] validators, IProject project) {
		Validator[] newVals = ValidatorExtensionReader.getDefault().migrate(validators, project);
		ValPrefManagerGlobal vpm = ValPrefManagerGlobal.getDefault();
		if (newVals != null){
			validators = newVals;
			vpm.saveAsPrefs(validators);
		}
		saveStateTimestamp();
		return validators;
	}

	/**
	 * Answer the global validation preferences.
	 */
	public GlobalPreferences getGlobalPreferences(){
		GlobalPreferences gp = _globalPreferences;
		if (gp == null){
			ValPrefManagerGlobal vpm = ValPrefManagerGlobal.getDefault();
			gp = new GlobalPreferences();
			vpm.loadGlobalPreferences(gp);
			_globalPreferences = gp;
		}
		return gp;		
	}
	
	/**
	 * Answer the project specific validation preferences. 
	 * 
	 * @param project
	 * 
	 * @return null if the project does not have any specific preferences.
	 */
	public ProjectPreferences getProjectPreferences(IProject project){
		if (_projectPreferences.containsKey(project)){
			return _projectPreferences.get(project);
		}
			
		ValPrefManagerProject vpm = new ValPrefManagerProject(project);
		ProjectPreferences pp = new ProjectPreferences(project); 
		vpm.loadProjectPreferences(pp);
		_projectPreferences.put(project, pp);
		return pp;
	}
	
	/**
	 * Restore all the validation defaults, as defined by the individual validators via the
	 * validation extension point.
	 */
//	public synchronized void restoreDefaults() {
//		getGlobalPreferences().resetToDefault();
//		_validators = null;
//		getValidators(true);
//	}
	

	/**
	 * Restore all the validation defaults, as defined by the individual validators.
	 */
	private static synchronized Validator[] restoreDefaults2(IProject project) {
		Validator[] val = ValidatorExtensionReader.getDefault().process(true, project);
//		ValPrefManagerGlobal vpm = ValPrefManagerGlobal.getDefault();
//		vpm.saveAsPrefs(val);
		return val;
	}
	
	/**
	 * Load the version 2 validators from the extensions.
	 * 
	 * @param deep if true load all the configuration elements for each validator, if false
	 * do a shallow load, where the validator class, id, name and message categories are loaded.
	 */
	private static Validator[] loadExtensions(boolean deep, IProject project) {
		// doesn't need to be synchronized
		return ValidatorExtensionReader.getDefault().process(deep, project);
	}

	/**
	 * Run all the validators that are applicable to this resource.
	 * <p>
	 * If this is a manual validation both the version 1 and version 2 validators are run. If it
	 * is a build validation, then only the version 2 validators are run, because the old framework handles
	 * the running of the old validators.
	 * </p>
	 * 
	 * @param project project that is being validated
	 * 
	 * @param resource the resource that is being validated
	 * 
	 * @param kind the kind of resource delta. It will be one of the IResourceDelta constants, like
	 * IResourceDelta.CHANGED for example.
	 * 
	 * @param isManual is this a manual validation request?
	 * @param isBuild is this a build based validation request?
	 * @param buildKind the kind of build that triggered this validation. See IncrementalProjectBuilder for values.
	 * @param operation the operation that this validation is running under
	 * @param monitor the monitor to use to report progress 
	 */
	public void validate(IProject project, final IResource resource, final int kind, boolean isManual, 
		boolean isBuild, int buildKind, ValOperation operation, final IProgressMonitor monitor) {
		
		MarkerManager.getDefault().deleteMarkers(resource, operation.getStarted());
		
		IValidatorVisitor visitor = new IValidatorVisitor(){

			public void visit(Validator validator, IProject project, boolean isManual,
				boolean isBuild, ValOperation operation, IProgressMonitor monitor) {
								
				Validator.V1 v1 = validator.asV1Validator();
				if (isBuild && v1 != null)return;
				
				validate(validator, operation, resource, kind, monitor);
				if ((kind & (IResourceDelta.CONTENT | IResourceDelta.CHANGED)) != 0){
					IResource[] dependencies = ValidationFramework.getDefault()
						.getDependencyIndex().get(validator.getDependencyId(), resource);
					if (dependencies != null){
						MarkerManager mm = MarkerManager.getDefault();
						String id = validator.getId();
						for (IResource resource : dependencies){
							try {
								mm.clearMarker(resource, id);
							}
							catch (CoreException e){
								//eat this one
							}
							validate(validator, operation, resource, IResourceDelta.NO_CHANGE, monitor);
							operation.addValidated(validator.getId(), resource);
						}
					}
				}		
			}
						
			
		};
		accept(visitor, project, resource, isManual, isBuild, operation, monitor);
		
	}
	
	/**
	 * Validate a single resource with a single validator. This will call the validator whether the validator
	 * is enabled or not.
	 * <p>
	 * Callers of this method should ensure that the shouldValidate was tested before making this call.
	 * 
	 * @param validator the validator
	 * @param operation the operation that the validation is running in.
	 * @param resource the resource to validate
	 * @param kind the kind of resource change. See IResourceDelta.
	 * @param monitor
	 */
	public void validate(Validator validator, ValOperation operation, IResource resource, int kind, 
			IProgressMonitor monitor){
		if (operation.isValidated(validator.getId(), resource))return;
		long time = 0;
		long cpuTime = -1;
		String msg1 = NLS.bind(ValMessages.LogValStart, validator.getName(), resource.getName());
		monitor.subTask(msg1);
		IPerformanceMonitor pm = ValidationFramework.getDefault().getPerformanceMonitor();
		if (pm.isCollecting()){
			time = System.currentTimeMillis();
			cpuTime = Misc.getCPUTime();
		}
		ValidationResult vr = validator.validate(resource, kind, operation, monitor);
		if (pm.isCollecting()){
			if (cpuTime != -1){
				cpuTime = Misc.getCPUTime() - cpuTime;
			}
			int num = 0;
			if (vr != null)num = vr.getNumberOfValidatedResources();
			PerformanceCounters pc = new PerformanceCounters(validator.getId(), 
				validator.getName(), resource.getName(),
				num, System.currentTimeMillis()-time, cpuTime);
			pm.add(pc);
		}
		if (ValidationPlugin.getPlugin().isDebugging() && !pm.isCollecting()){
			String msg = time != 0 ? 
				NLS.bind(ValMessages.LogValEndTime,	new Object[]{validator.getName(), 
					validator.getId(), resource, Misc.getTimeMS(System.currentTimeMillis()-time)}) :
				NLS.bind(ValMessages.LogValEnd, validator.getName(), resource);
			Tracing.log(msg);
		}
		if (vr != null){
			operation.getResult().mergeResults(vr);
			if (vr.getSuspendValidation() != null)operation.suspendValidation(vr.getSuspendValidation(), validator);
		}
	}
	
	/**
	 * Accept a visitor for all the validators that are enabled for the given project.
	 * 
	 * @param visitor
	 * @param project
	 * @param isManual is this a manual validation?
	 * @param isBuild is this a builder based validation?
	 * @param operation
	 * @param monitor
	 */
	public void accept(IValidatorVisitor visitor, IProject project, boolean isManual, boolean isBuild, 
		ValOperation operation, IProgressMonitor monitor){
		
		if (isDisabled(project))return;
		
		boolean hasProcessedProject = operation.hasProcessedProject(project);
		for (Validator val : getValidators(project)){
			if (monitor.isCanceled())return;
			if (!operation.shouldExclude(val, project, hasProcessedProject, isManual, isBuild)){
				try {
					visitor.visit(val, project, isManual, isBuild, operation, monitor);
				}
				catch (Exception e){
					ValidationPlugin.getPlugin().handleException(e);
				}
			}
		}		
	}
	
	/**
	 * Accept a visitor for all the validators that are enabled for the given project, resource, and validation mode.
	 */
	public void accept(IValidatorVisitor visitor, IProject project, IResource resource, boolean isManual, 
			boolean isBuild, ValOperation operation, IProgressMonitor monitor){
		
		if (isDisabled(project))return;
		
		boolean hasProcessedProject = operation.hasProcessedProject(project);
		for (Validator val : getValidators(project)){
			if (monitor.isCanceled())return;
			if (operation.shouldExclude(val, project, hasProcessedProject, isManual, isBuild))continue;
			if (val.shouldValidate(resource, isManual, isBuild)){
				try {
					visitor.visit(val, project, isManual, isBuild, operation, monitor);
				}
				catch (Exception e){
					ValidationPlugin.getPlugin().handleException(e);
				}
			}
		}		
	}
	
	/**
	 * Let each of the enabled validators know that a clean has been requested.
	 * 
	 * @param project the project that is being cleaned, or null if the entire workspace is being cleaned.
	 * @param monitor
	 */
	void clean(final IProject project, final ValOperation operation, final IProgressMonitor monitor) {
		IValidatorVisitor visitor = new IValidatorVisitor(){

			public void visit(Validator validator, IProject project, boolean isManual,
				boolean isBuild, ValOperation operation, IProgressMonitor monitor) {
				validator.clean(project, monitor);					
			}
			
		};
		accept(visitor, project, false, false, operation, monitor);
	}
	
	/**
	 * Let each of the enabled validators know that a clean has been requested.
	 * 
	 * @param project the project that is being cleaned, or null if the entire workspace is being cleaned.
	 * @param monitor
	 */
	public void clean(IProject project, IProgressMonitor monitor){
		IValidatorVisitor visitor = new IValidatorVisitor(){

			public void visit(Validator validator, IProject project, boolean isManual,
				boolean isBuild, ValOperation operation, IProgressMonitor monitor) {
				validator.clean(project, monitor);					
			}
			
		};
		ValidationFramework.getDefault().getDependencyIndex().clear(project);
		ValOperation operation = new ValOperation();
		accept(visitor, project, false, true, operation, monitor);
	}
	
	private class HasValidatorVisitor implements IResourceVisitor {
		
		private boolean 	_hasValidator;
		private boolean		_isManual;
		private boolean		_isBuild;
		
		public HasValidatorVisitor(boolean isManual, boolean isBuild){
			_isManual = isManual;
			_isBuild = isBuild;			
		}
		
		public boolean hasValidator(IFolder folder){
			try {
				folder.accept(this);
			}
			catch (CoreException e){
				ValidationPlugin.getPlugin().handleException(e);
			}
			return _hasValidator;
		}

		public boolean visit(IResource resource) throws CoreException {
			if (resource instanceof IFolder)return true;
			if (hasValidators(resource, _isManual, _isBuild)){
				_hasValidator = true;
				return false;
			}
			return true;
		}
	}

	public void validatorsForProjectChanged(IProject project) {
		if (project == null)_validators = null;	
		else _projectPreferences.remove(project);
	}

}
