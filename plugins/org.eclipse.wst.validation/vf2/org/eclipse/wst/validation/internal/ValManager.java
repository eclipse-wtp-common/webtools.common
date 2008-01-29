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
public class ValManager {
	
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
	}
	
	/**
	 * Answer all the registered validators.
	 * 
	 * @return Answer an empty array if there are no validators.
	 */
	public Validator[] getValidators(){
		return getValidators(false);
	}
	
	/**
	 * Answer all the validators that are in effect for the given project.
	 * <p>
	 * Individual projects may override the global validation preference settings. If this is allowed and if
	 * the project has it's own settings, then those validators are returned via this method.
	 * <p>
	 * The following approach is used. For version 1 validators, the validator is only returned if it
	 * is defined to operate on this project type. This is the way that the previous version of the framework
	 * did it. For version 2 validators, they are all returned.
	 * 
	 * @param project this may be null, in which case the global preferences are used.
	 * @return
	 */
	public Validator[] getValidators(IProject project) throws ProjectUnavailableError {
		if (project == null)return getValidators();
		if (!getGlobalPreferences().getOverride())return getValidators(false, project);
		
		ProjectPreferences pp = getProjectPreferences(project);
		if (pp == null || !pp.getOverride())return getValidators(false, project);
		return pp.getValidators();		
	}
	
	/**
	 * Answer the validator with the given id that is in effect for the given project.
	 * 
	 * @param id validator dependency id.
	 * @param project
	 * @return null if the validator is not found
	 */
	public Validator getValidator(String id, IProject project){
		Validator[] vals = getValidators(project);
		for (Validator v : vals){
			if (v.getDependencyId().equals(id))return v;
		}
		return null;
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
	
	Validator[] getValidators(boolean forceDefaults){
		Validator[] vals = null;
		try {
			vals = getValidators(forceDefaults, null);
		}
		catch (ProjectUnavailableError e){
			// can't happen since the project is null
		}
		return vals;
	}
		
	/**
	 * Answer all the registered validators.
	 * 
	 * @param forceDefaults if true then we reload the validators from the extension points,
	 * discarding any user preferences.
	 * 
	 * @param project the project to use for getting the version 1 validator settings. This can
	 * be null in which case the global preferences are used.
	 * 
	 * @return Answer an empty array if there are no validators.
	 */
	Validator[] getValidators(boolean forceDefaults, IProject project) throws ProjectUnavailableError {
		// If I use a local variable I don't need to synchronize the method.
		Validator[] validators = _validators;
		if (!forceDefaults && project == null && validators != null)return validators;
				
		Validator[] val = loadExtensions(false);
		if (forceDefaults){
			val = restoreDefaults2();
			saveStateTimestamp();
		}
		else {
			ValPrefManagerGlobal vpm = new ValPrefManagerGlobal();
			if (!vpm.loadPreferences(val)){
				val = restoreDefaults2();
				saveStateTimestamp();				
			}
			else {
				if (getGlobalPreferences().getStateTimeStamp() != Platform.getStateStamp())
					val = migrateSettings(val);
			}
		}
		
		TreeSet<Validator> set = new TreeSet<Validator>();
		for (Validator v : val)set.add(v);
		
		List<Validator> list = new LinkedList<Validator>();
		ValidationConfiguration vc = null;
		try {
			if (project == null){
				vc = ConfigurationManager.getManager().getGlobalConfiguration();
				for (ValidatorMetaData vmd : ValidationRegistryReader.getReader().getAllValidators()){
					list.add(Validator.create(vmd, vc));
				}			
			}
			else {
				vc = ConfigurationManager.getManager().getProjectConfiguration(project);
				for (ValidatorMetaData vmd : vc.getValidators()){
					list.add(Validator.create(vmd, vc));
				}							
			}
		}
		catch (InvocationTargetException e){
			if (!project.exists() || !project.isOpen())throw new ProjectUnavailableError(project);
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
	private Validator[] migrateSettings(Validator[] validators) {
		Validator[] newVals = ValidatorExtensionReader.migrate(validators);
		ValPrefManagerGlobal vpm = new ValPrefManagerGlobal();
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
			ValPrefManagerGlobal vpm = new ValPrefManagerGlobal();
			gp = vpm.loadGlobalPreferences();
			_globalPreferences = gp;
		}
		return gp;		
	}
	
	/**
	 * Answer the project specific validation preferences. If the project doesn't have any 
	 * project specific validation preferences then answer null.
	 * 
	 * @param project
	 */
	public ProjectPreferences getProjectPreferences(IProject project){
		if (_projectPreferences.containsKey(project)){
			return _projectPreferences.get(project);
		}
			
		ValPrefManagerProject vpm = new ValPrefManagerProject(project);
		ProjectPreferences pp = vpm.loadProjectPreferences();
		_projectPreferences.put(project, pp);
		return pp;
	}
	
	/**
	 * Restore all the validation defaults, as defined by the individual validators via the
	 * validation extension point.
	 */
	public synchronized void restoreDefaults() {
		getGlobalPreferences().resetToDefault();
		_validators = null;
		getValidators(true);
	}
	

	/**
	 * Restore all the validation defaults, as defined by the individual validators.
	 */
	private synchronized Validator[] restoreDefaults2() {
		Validator[] val = ValidatorExtensionReader.process(true);
		ValPrefManagerGlobal vpm = new ValPrefManagerGlobal();
		vpm.saveAsPrefs(val);
		return val;
	}
	
	/**
	 * Load the version 2 validators from the extensions.
	 * 
	 * @param deep if true load all the configuration elements for each validator, if false
	 * do a shallow load, where only the validator class, id and name's are loaded.
	 */
	private Validator[] loadExtensions(boolean deep) {
		// doesn't need to be synchronized
		return ValidatorExtensionReader.process(deep);
	}

	/**
	 * Run all the validators that are applicable to this resource.
	 * <p>
	 * If this is a manual validation both the version 1 and version 2 validators are run. If it
	 * is a build validation, then only the version 2 validators are run, because the old framework handles
	 * the running of the old validators.
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
		
		deleteMarkers(resource);
		
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
						for (IResource resource : dependencies){
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
			PerformanceCounters pc = new PerformanceCounters(validator.getId(), 
				validator.getName(), resource.getName(),
				vr.getNumberOfValidatedResources(),	System.currentTimeMillis()-time, cpuTime);
			pm.add(pc);
		}
		if (ValidationPlugin.getPlugin().isDebugging()){
			String msg = time != 0 ? 
				NLS.bind(ValMessages.LogValEndTime,	new Object[]{validator.getName(), 
					validator.getId(), resource, String.valueOf(System.currentTimeMillis()-time)}) :
				NLS.bind(ValMessages.LogValEnd, validator.getName(), resource);
			Misc.log(msg);
		}
		operation.getResult().mergeResults(vr);				
	}
	
	private void deleteMarkers(IResource resource){
		try {
			resource.deleteMarkers(ValConstants.ProblemMarker, false, IResource.DEPTH_ZERO);
		}
		catch (CoreException e){
			IProject project = resource.getProject();
			if (!project.exists() || !project.isOpen())throw new ProjectUnavailableError(project);
			if (!resource.exists())throw new ResourceUnavailableError(resource);
			ValidationPlugin.getPlugin().handleException(e);
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
		
		GlobalPreferences gp = getGlobalPreferences();
		if (gp.getDisableAllValidation())return;
		
		for (Validator val : getValidators(project)){
			if (monitor.isCanceled())return;
			if (val.shouldValidateProject(project, isManual, isBuild)){
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
		GlobalPreferences gp = getGlobalPreferences();
		if (gp.getDisableAllValidation())return;
		ProjectPreferences pp = null;
		if (gp.getOverride())pp = getProjectPreferences(project);
		if (pp != null && pp.getSuspend())return;
		
		for (Validator val : getValidators(project)){
			if (monitor.isCanceled())return;
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

}
