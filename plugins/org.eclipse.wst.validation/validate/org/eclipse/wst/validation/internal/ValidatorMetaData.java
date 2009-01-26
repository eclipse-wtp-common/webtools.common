/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.expressions.Expression;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.wst.validation.internal.delegates.ValidatorDelegatesRegistry;
import org.eclipse.wst.validation.internal.operations.IWorkbenchContext;
import org.eclipse.wst.validation.internal.operations.WorkbenchContext;
import org.eclipse.wst.validation.internal.plugin.ValidationHelperRegistryReader;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.validation.internal.provisional.core.IValidatorJob;
import org.osgi.framework.Bundle;

/**
 * This class stores information, as specified by a validator's plugin.xml tags. There is one
 * ValidatorMetaData for each Validator. No Validator should attempt to access its
 * ValidatorMetaData; it is for use by the base framework only.
 */
public class ValidatorMetaData {
	private final ValidatorFilter[] _filters;
	private final ValidatorNameFilter[] 	_projectNatureFilters;
	private final String[]			_facetFilters;
	private final AtomicReference<IValidator>	_validator = new AtomicReference<IValidator>();
	private final AtomicReference<IWorkbenchContext> 		_helper = new AtomicReference<IWorkbenchContext>();
	private final String 			_validatorDisplayName;
	private final String 			_validatorUniqueName;
	
	/**
	 * The list of class names of every validator which this validator aggregates. For
	 * example, if the EJB Validator instantiated another validator, and started its validate
	 * method, then that instantiated class' name should be in this list.
	 */
	private final String[] 	_aggregatedValidators;
    private final String[] 	_contentTypeIds;
	private final String[] 	_validatorNames;
	private final String 	_pluginId;
	private final boolean	_supportsIncremental;
	private final boolean 	_supportsFullBuild;
	private final boolean 	_isEnabledByDefault;
	private final MigrationMetaData _migrationMetaData;
	private final int 		_ruleGroup;
	private final boolean 	_async;
	private final boolean 	_dependentValidator;
	private final String[]	_markerIds;
	private final String 	_helperClassName;
	private final IConfigurationElement _helperClassElement;
	private final IConfigurationElement _validatorClassElement;
	private volatile boolean 	_cannotLoad;
	private volatile boolean 	_manualValidation = true;
	private volatile boolean	_buildValidation = true;
	private final Map<IValidatorJob, IWorkbenchContext> _helpers = 
		Collections.synchronizedMap( new HashMap<IValidatorJob, IWorkbenchContext>() );
	private final Expression 		_enablementExpression;

	ValidatorMetaData(boolean async, String[] aggregatedValidators, boolean isEnabledByDefault, boolean supportsIncremental,
			boolean supportsFullBuild, IConfigurationElement helperClassElement, String helperClassName, 
			MigrationMetaData migrationMetaData, String pluginId, int ruleGroup, IConfigurationElement validatorClassElement,
			String validatorDisplayName, String validatorUniqueName, String[] contentTypeIds, boolean dependentValidator,
			Expression enablementExpression, String[] facetFilters, ValidatorFilter[] filters,
			ValidatorNameFilter[] projectNatureFilters, String[] markerIds) {
		_async = async;
		_aggregatedValidators = aggregatedValidators;
		_isEnabledByDefault = isEnabledByDefault;
		_supportsIncremental = supportsIncremental;
		_supportsFullBuild = supportsFullBuild;
		_helperClassElement = helperClassElement;
		_helperClassName = helperClassName;
		_migrationMetaData = migrationMetaData;
		_pluginId = pluginId;
		_ruleGroup = ruleGroup;
		_validatorClassElement = validatorClassElement;
		_validatorDisplayName = validatorDisplayName;
		_validatorUniqueName = validatorUniqueName;
		_contentTypeIds = contentTypeIds;
		_dependentValidator = dependentValidator;
		_enablementExpression = enablementExpression;
		_facetFilters = facetFilters;
		_filters = filters;
		_projectNatureFilters = projectNatureFilters;
		_markerIds = markerIds;
		_validatorNames = buildValidatorNames();
	}
		
	protected String[] getFacetFilters() {
		return _facetFilters;
	}

	public List<String> getNameFilters() {
		List<String> nameFilters = new ArrayList<String>();
		if (_filters != null && _filters.length > 0) {
			for (ValidatorFilter filter : _filters) {
				ValidatorNameFilter nameFilter = filter.get_nameFilter();
				if (nameFilter != null) {
					nameFilters.add(nameFilter.getNameFilter());
				}
			}
		}
		return nameFilters;
	}

	/**
	 * Return the list of class names of the primary validator and its aggregates.
	 */
	public String[] getValidatorNames() {
		return _validatorNames;
	}
	
	private String[] buildValidatorNames() {
		int aLength = (_aggregatedValidators == null) ? 0 : _aggregatedValidators.length;
		String [] validatorNames = new String[aLength + 1]; // add 1 for the primary validator name
		validatorNames[0] = getValidatorUniqueName();
		if (_aggregatedValidators != null) {
			System.arraycopy(_aggregatedValidators, 0, validatorNames, 1, aLength);
		}
		return validatorNames;
	}

	/**
	 * Return the list of class names of every validator which this validator aggregates. For
	 * example, if the EJB Validator instantiated another validator, and started its validate
	 * method, then that instantiated class' name should be in this list.
	 */
	public String[] getAggregatedValidatorNames() {
		return _aggregatedValidators;
	}

	/**
	 * Return the name/type filter pairs.
	 */
	public ValidatorFilter[] getFilters() {
		return _filters;
	}

	/**
	 * Return true if this vmd's helper and validator have been instantiated, and also if this
	 * validator's plugin is active.
	 */
	public boolean isActive() {
		if (_helperClassElement != null) {
			return false;
		}

		if (_validatorClassElement != null) {
			return false;
		}

		Bundle bundle = Platform.getBundle(_pluginId);
		if (bundle != null)
			return bundle.getState() == Bundle.ACTIVE;

		return false;
	}

	/**
	 * This method will throw an InstantiationException if the helper cannot be instantiated, e.g.,
	 * if the helper's plugin is disabled for some reason. Before the InstantiationException is
	 * thrown, this validator will be disabled.
	 * 
	 * The IWorkbenchContext must ALWAYS have its project set before it is used; but it can't be
	 * created with the IProject. So, before using the single instance, always initialize that
	 * instance with the IProject.
	 * 
	 * If this validator supports asynchronous validation, then instead of maintaining a single the
	 * helper instance, create a new IWorkbenchContext instance every time that the helper is needed.
	 * This feature is provided because several validation Runnables may be queued to run, and if
	 * those Runnables's project is different from the current validation's project, then the
	 * current validation will suddenly start validating another project.
	 */
	//TODO just want to remember to figure out the many-temporary-objects problem if this method
	// continues to new an IValidationContext every time - Ruth
	public IWorkbenchContext getHelper(IProject project) throws InstantiationException {
		IWorkbenchContext helper = _helper.get();
		if (helper != null)return helper;
		
		helper = ValidationRegistryReader.createHelper(_helperClassElement, _helperClassName);
		if (helper == null)helper = new WorkbenchContext();
		
		if ((helper.getProject() == null) || !(helper.getProject().equals(project))) {
			helper.setProject(project);
		}
		if (_helper.compareAndSet(null, helper))return helper;
		return _helper.get();
	}

	/**
	 * cannotLoad is false if both the IValidator and IWorkbenchContext instance can be instantiated.
	 */
	private void setCannotLoad() {
		_cannotLoad = true;
	}

	/**
	 * Return false if both the IValidator and IWorkbenchContext instance can be instantiated.
	 * 
	 * @return boolean
	 */
	public boolean cannotLoad() {
		return _cannotLoad;
	}

	public MigrationMetaData getMigrationMetaData() {
		return _migrationMetaData;
	}

	/**
	 * Return the IRuleGroup integer indicating which groups of rules this validator recognizes.
	 */
	public int getRuleGroup() {
		return _ruleGroup;
	}

	/**
	 * Return the filters which identify which project(s) this validator may run on.
	 */
	ValidatorNameFilter[] getProjectNatureFilters() {
		return _projectNatureFilters;
	}

	/**
	 * This method returns the validator if it can be loaded; if the validator cannot be loaded,
	 * e.g., if its plugin is disabled for some reason, then this method throws an
	 * InstantiationException. Before the CoreException is thrown, this validator is disabled.
	 */
	public IValidator getValidator() throws InstantiationException {
		IValidator val = _validator.get();
		if (val != null)return val;
		
		val = ValidationRegistryReader.createValidator(_validatorClassElement, getValidatorUniqueName());

		if (val == null) {
			setCannotLoad();
			throw new InstantiationException(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_DISABLEV, new String[]{getValidatorUniqueName()}));
		}
		if (_validator.compareAndSet(null, val))return val;
		return _validator.get();
	}

	public String getValidatorDisplayName() {
		return _validatorDisplayName;
	}

	public String getValidatorUniqueName() {
		return _validatorUniqueName;
	}

	/**
	 * If the resource is applicable to the Validator which this ValidatorMetaData is associated
	 * with, return true; else return false.
	 * 
	 * A resource is applicable if it passes the name/type filters. This method is called if there
	 * is no resource delta (i.e., a full validation).
	 */
	public boolean isApplicableTo(IResource resource) {
		return isApplicableTo(resource, ValidatorActionFilter.ALL_ACTIONS);
	}

	/**
	 * If the resource is applicable to the Validator which this ValidatorMetaData is associated
	 * with, return true; else return false.
	 * 
	 * A resource is applicable if it passes the name/type filters.
	 */
	public boolean isApplicableTo(IResource resource, int resourceDelta) {
		// If no filters are specified, then every type of resource should be validated/trigger a
		// rebuild of the model cache.
		// Also make sure no content type id is specified (BUG 193816)
		if (_filters == null  && getContentTypeIds() == null)return true;

		return isApplicableTo(resource, resourceDelta, _filters);
	}

	/**
	 * Return true if the resource passes the name/type filters for this validator.
	 */
	boolean isApplicableTo(IResource resource, int resourceDelta, ValidatorFilter[] filters) {
		// Are any of the filters satisfied? (i.e., OR them, not AND them.)
		// make sure filters is not null (BUG 193816)
		if (filters != null && checkIfValidSourceFile(resource)) {
			for (ValidatorFilter filter : filters) {
				if (filter.isApplicableType(resource)
						&& filter.isApplicableName(resource)
						&& filter.isApplicableAction(resourceDelta)) {
					return true;
				}
			}
		}
		if (getContentTypeIds() != null) {
			IContentDescription description = null;
			try {
				if (resource.getType() == IResource.FILE && resource.exists())
					description = ((IFile) resource).getContentDescription();
			} catch (CoreException e) {
				//Resource exceptions
			}
			if (description == null)return false;
			if (isApplicableContentType(description))return true;
		}
		return false;
	}

	private boolean checkIfValidSourceFile(IResource file) {
		if (file.getType() == IResource.FILE) {
			IProjectValidationHelper helper = ValidationHelperRegistryReader.getInstance().getValidationHelper();
			IProject project = file.getProject();
			if (helper == null || project == null)
				return true;
			IContainer[] outputContainers = helper.getOutputContainers(project);
			IContainer[] sourceContainers = helper.getSourceContainers(project);
			for (int i=0; i<outputContainers.length; i++) {
				String outputPath = outputContainers[i].getProjectRelativePath().makeAbsolute().toString();
                String filePath = file.getProjectRelativePath().makeAbsolute().toString();
				if (filePath.startsWith(outputPath)) {
					//The file is in an output container.
					//If it is a source container return true and false otherwise.
					for (int j=0;j<sourceContainers.length; j++) {
	                    if(outputContainers[i].equals(sourceContainers[j])){
	                    	return true;
	                    }
						return false;
					}
				}
			}
		}
		return true;
	}
		
		
	/**
	 * If this validator recognizes the project nature, whether included or excluded, return the
	 * name filter which describes the nature. Otherwise return null.
	 */
	ValidatorNameFilter findProjectNature(String projId) {
		if (projId == null) {
			return null;
		}

		if (_projectNatureFilters == null) {
			// If no tag is specified, this validator is configured on all IProjects
			return null;
		}

		for (int i = 0; i < _projectNatureFilters.length; i++) {
			ValidatorNameFilter filter = _projectNatureFilters[i];
			// In this case, we're not checking if the project is an instance of the filter class,
			// but if it has the Nature specified in the filter class.
			String projectNatureID = filter.getNameFilter();
			if (projId.equals(projectNatureID)) {
				return filter;
			}
		}
		return null;
	}

	/**
	 * Convenience method. Rather than store the is-this-vmd-configured-on-this-IProject algorithm
	 * in two places, refer back to the reader's cache.
	 */
	public boolean isConfiguredOnProject(IProject project) {
		return ValidationRegistryReader.getReader().isConfiguredOnProject(this, project);
	}

	public boolean isEnabledByDefault() {
		return _isEnabledByDefault;
	}

	public boolean isIncremental() {
		return _supportsIncremental;
	}

	public boolean isFullBuild() {
		return _supportsFullBuild;
	}

	/**
	 * Return true if the validator is thread-safe and can be run asynchronously.
	 */
	public boolean isAsync() {
		return _async;
	}

	public String toString() {
		return getValidatorUniqueName();
	}

	public final static class MigrationMetaData {
		private Set<String[]> _ids;

		public MigrationMetaData() {
		}

		public void addId(String oldId, String newId) {
			if (oldId == null)return;
			if (newId == null)return;

			String[] ids = new String[]{oldId, newId};
			getIds().add(ids);
		}

		public Set<String[]> getIds() {
			if (_ids == null)_ids = new HashSet<String[]>();
			return _ids;
		}
	}

	public boolean isDependentValidator() {
		return _dependentValidator;
	}

	/**
	 * @return Returns the markerId.
	 */
	public String[] getMarkerIds() {
		return _markerIds;
	}

	public boolean isBuildValidation() {
		return _buildValidation;
	}

	public void setBuildValidation(boolean buildValidation) {
		_buildValidation = buildValidation;
	}

	public boolean isManualValidation() {
		return _manualValidation;
	}

	public void setManualValidation(boolean manualValidation) {
		_manualValidation = manualValidation;
	}
  
	/**
   * Determines if the validator described by this metadata object is a delegating validator. 
   * @return true if the validator described by this metadata object is a delegating validator, false otherwise.
	 */
  public boolean isDelegating() {
    String targetID = getValidatorUniqueName();
    return ValidatorDelegatesRegistry.getInstance().hasDelegates(targetID);
  }
  

	public IValidator createValidator() throws InstantiationException {
		return  ValidationRegistryReader.createValidator(_validatorClassElement, getValidatorUniqueName());
	}
	
	public IWorkbenchContext createHelper(IProject project) throws InstantiationException {
		IWorkbenchContext helper = ValidationRegistryReader.createHelper(_helperClassElement, _helperClassName);
		if (helper == null) {
			helper = new WorkbenchContext();
		}
		helper.setProject(project);
		return helper;
	}	  
	
   public void addHelper( IValidatorJob validator, IWorkbenchContext helper ){
	   _helpers.put( validator, helper );
   }
   
   public void removeHelper( IValidatorJob validator ){
	   _helpers.remove( validator );
   }
   
   private IWorkbenchContext getHelper( IValidatorJob validator ){
	   return _helpers.get( validator );
   }   
   
   public IWorkbenchContext getHelper( IProject project, IValidator validator ){
	   
	   if( validator instanceof IValidatorJob ){
		   IWorkbenchContext helper = getHelper( (IValidatorJob)validator );
		   if( helper == null ){
			   try{
				helper =  getHelper( project );
				return helper;
			   }catch (InstantiationException e) {
					e.printStackTrace();
				}			   
		   }
	   	return helper;
	   }
	   else{
		   try {
			IWorkbenchContext helper =  getHelper( project );
			return helper;
			} catch (InstantiationException e) {
				e.printStackTrace();
			}
	   }
	   
	   return null;
   }   
   
   public Expression getEnablementExpresion() {
		return _enablementExpression;
	}

public String[] getContentTypeIds() {
	return _contentTypeIds;
}

 
private boolean isApplicableContentType(IContentDescription desc){
	
	IContentType ct = desc.getContentType();
	String[] applicableContentTypes = getContentTypeIds();
	if (applicableContentTypes != null) {
		for (int i = 0; i < applicableContentTypes.length; i ++){
			if(applicableContentTypes[i].equals(ct.getId()))
				return true;
		}
	}
	return false;
}
   
}
