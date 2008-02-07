package org.eclipse.wst.validation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.wst.validation.internal.DebugConstants;
import org.eclipse.wst.validation.internal.DependencyIndex;
import org.eclipse.wst.validation.internal.Misc;
import org.eclipse.wst.validation.internal.PerformanceMonitor;
import org.eclipse.wst.validation.internal.ValManager;
import org.eclipse.wst.validation.internal.ValOperation;
import org.eclipse.wst.validation.internal.ValidationRunner;
import org.eclipse.wst.validation.internal.operations.WorkbenchReporter;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;


/**
 * The central class of the Validation Framework.
 * <p>
 * This is a singleton class that is accessed through the getDefault() method. 
 * @author karasiuk
 *
 */
public final class ValidationFramework {
	
	private IDependencyIndex 			_dependencyIndex;
	private IPerformanceMonitor			_performanceMonitor;
	private static ValidationFramework 	_me;
	
	/** 
	 * Answer the singleton, default instance of this class.
	 */
	public static ValidationFramework getDefault(){
		if (_me == null)_me = new ValidationFramework();
		return _me;
	}
	
	private ValidationFramework(){}
	
	/**
	 * Answer the dependency index. Validators can use this to determine which resources depend on which
	 * other resources.
	 */
	public IDependencyIndex getDependencyIndex(){
		if (_dependencyIndex != null)return _dependencyIndex;
		return getDependencyIndex2();
	}

	private synchronized IDependencyIndex getDependencyIndex2() {
		if (_dependencyIndex == null)_dependencyIndex = new DependencyIndex();
		return _dependencyIndex;
	}
	
	public synchronized IPerformanceMonitor getPerformanceMonitor(){
		if (_performanceMonitor == null){
			boolean traceTimes = Misc.debugOptionAsBoolean(DebugConstants.TraceTimes);
			String traceFile = Platform.getDebugOption(DebugConstants.TraceTimesFile);

			_performanceMonitor = PerformanceMonitor.create(traceTimes, traceFile);
		}
		return _performanceMonitor;
	}
	
	/**
	 * Answer the preference store that holds the global validation settings.
	 */
	public IEclipsePreferences getPreferenceStore(){
		return new InstanceScope().getNode(ValidationPlugin.PLUGIN_ID);
	}
	
	public IReporter getReporter(IProject project, IProgressMonitor monitor){
		return new WorkbenchReporter(project, monitor);
	}
	
	/**
	 * Answer all the validators that are applicable for the given resource, even if the
	 * validator has been turned off by the user.
	 * <p>
	 * The caller may still need to test if the validator has been turned off by the
	 * user, by using the isBuildValidation() and isManualValidation() methods.
	 * 
	 * @param resource the resource that determines which validators are applicable.
	 * 
	 * @param isManual if true then the validator must be turned on for manual validation. 
	 * If false then the isManualValidation setting isn't used to filter out validators.
	 *   
	 * @param isBuild if true then the validator must be turned on for build based validation.
	 * If false then the isBuildValidation setting isn't used to filter out validators.  
	 * 
	 * @see Validator#isBuildValidation()
	 * @see Validator#isManualValidation()
	 */
	public Validator[] getValidatorsFor(IResource resource, boolean isManual, boolean isBuild){
		IProject project = resource.getProject();
		List<Validator> list = new LinkedList<Validator>();
		for (Validator val : ValManager.getDefault().getValidators(project)){
			if (val.shouldValidate(resource, isManual, isBuild))list.add(val);
		}
		
		Validator[] result = new Validator[list.size()];
		list.toArray(result);
		return result;
	}
	
	/**
	 * Answer all the validators that should not validate the resource, either because
	 * their filters don't support the resource, or the validator has been disabled for
	 * both build validation and manual validation.
	 * @param resource
	 */
	public Set<Validator> getDisabledValidatorsFor(IResource resource){
		IProject project = resource.getProject();
		Set<Validator> set = new HashSet<Validator>(10);
		for (Validator val : ValManager.getDefault().getValidators(project)){
			boolean validateIt = false;
			if (val.shouldValidate(resource, false, false)){
				validateIt = val.isBuildValidation() || val.isManualValidation();
			}
			if (!validateIt)set.add(val);
		}
		return set;
	}
	
	/**
	 * Answer the validator with the given id.
	 * @param id
	 * @return null if the validator is not found
	 */
	public Validator getValidator(String id){
		return ValManager.getDefault().getValidatorWithId(id, null);
	}
	
	/**
	 * Answer all the validators that are applicable for the given resource.
	 * By convention this is the method that is used
	 * by the as-you-type validators, to determine if they should be validating the resource.
	 * 
	 * @param resource the resource that determines which validators are applicable.
	 */
	public Validator[] getValidatorsFor(IResource resource){
		List<Validator> list = new LinkedList<Validator>();
		for (Validator v : getValidatorsFor(resource, false, false)){
			if (v.isBuildValidation() || v.isManualValidation())list.add(v);
		}
		Validator[] vals = new Validator[list.size()];
		return list.toArray(vals);
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
		return ValManager.getDefault().hasValidators(resource, isManual, isBuild);
	}
	
	/**
	 * Validate the projects.
	 *  
	 * @param projects the projects to be validated.
	 * 
	 * @param isManual is this being done as part of a manual validation? i.e. did the user select the
	 * Validate menu item?
	 * 
	 * @param isBuild is this being done as part of a build?
	 * 
	 * @param monitor
	 * 
	 * @return the validation result is the combined result for all the resources that were validated.
	 */
	public ValidationResults validate(IProject[] projects, final boolean isManual, final boolean isBuild,
		IProgressMonitor monitor) throws CoreException{
		ValOperation vo = ValidationRunner.validate(createMap(projects), isManual, isBuild, monitor);
		return vo.getResults();
	}
	
	/**
	 * Answer all the resources in the projects as a map.
	 * @param projects
	 */
	private Map<IProject, Set<IResource>> createMap(IProject[] projects) throws CoreException{
		final HashMap<IProject, Set<IResource>> map = new HashMap<IProject, Set<IResource>>(1000);
			
		for (IProject p : projects){
			Set<IResource> set = new HashSet<IResource>(1000);
			ResourceAdder ra = new ResourceAdder(set);
			p.accept(ra);
			map.put(p, set);
		}
		return map;
	}
	
	public static class ResourceAdder implements IResourceVisitor {
		
		private Set<IResource> _set;
		
		/**
		 * A class that knows how to add resources to a set.
		 * @param set the set where the resources are added.
		 */
		public ResourceAdder(Set<IResource> set){
			_set = set;
		}

		public boolean visit(IResource resource) throws CoreException {
			_set.add(resource);
			return true;
		}
		
	}


}
