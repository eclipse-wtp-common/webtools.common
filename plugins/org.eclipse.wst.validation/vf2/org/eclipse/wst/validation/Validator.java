/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.validation.internal.ConfigurationConstants;
import org.eclipse.wst.validation.internal.ConfigurationManager;
import org.eclipse.wst.validation.internal.ContentTypeWrapper;
import org.eclipse.wst.validation.internal.ExtensionConstants;
import org.eclipse.wst.validation.internal.MarkerManager;
import org.eclipse.wst.validation.internal.Misc;
import org.eclipse.wst.validation.internal.NullValidator;
import org.eclipse.wst.validation.internal.SummaryReporter;
import org.eclipse.wst.validation.internal.Tracing;
import org.eclipse.wst.validation.internal.ValManager;
import org.eclipse.wst.validation.internal.ValMessages;
import org.eclipse.wst.validation.internal.ValOperation;
import org.eclipse.wst.validation.internal.ValPrefManagerGlobal;
import org.eclipse.wst.validation.internal.ValPrefManagerProject;
import org.eclipse.wst.validation.internal.ValType;
import org.eclipse.wst.validation.internal.ValidationConfiguration;
import org.eclipse.wst.validation.internal.ValidatorExtensionReader;
import org.eclipse.wst.validation.internal.ValidatorMetaData;
import org.eclipse.wst.validation.internal.core.ValidatorLauncher;
import org.eclipse.wst.validation.internal.delegates.ValidatorDelegateDescriptor;
import org.eclipse.wst.validation.internal.delegates.ValidatorDelegatesRegistry;
import org.eclipse.wst.validation.internal.model.FilterGroup;
import org.eclipse.wst.validation.internal.operations.IWorkbenchContext;
import org.eclipse.wst.validation.internal.operations.WorkbenchContext;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;

/**
 * Represents a validator. This gets instantiated through one of the validator extension points.
 * <p>
 * <b>This class is not API</b>.
 * </p>
 * 
 * @author karasiuk
 *
 */
public abstract class Validator implements Comparable<Validator> {
	// Remember if you add a new instance variable, make sure that you update the copy and become methods
	
	/**
	 * The level of configuration for the validator.
	 * <ul>
	 * <li>Extension - Defined by an extension point.</li>
	 * <li>Global - Defined by a global preference.</li>
	 * <li>Project - Defined by a project property.</li>
	 * </ul>
	 */
	public enum Level {Extension, Global, Project};
	
	protected boolean	_buildValidation = true;
	
	/** If this is a delegating validator, then this field holds the validator that will be delegated to. */
	private String 		_delegatingId;
	
	/** 
	 * If this validator is also used to control an ISource validator, the id of the ISource validator is
	 * registered here.
	 */
	private String		_sourceId;
	
	protected boolean 	_manualValidation = true;
	
	/** An optional customized marker id for this validator. */
	private String 		_markerId;
	
	/** 
	 * Version of the filter definition. By increasing this number the framework can know that a plug-in has 
	 * changed it's filters.
	 */
	private int			_version = 1;
	
	/** Map simple message id's to message settings. */
	private Map<String, MessageSeveritySetting> _messageSettings;
	
	/** The project that you are defined in. This can be null which means that you are a global validator. */
	protected IProject	_project;
		
	/** How many times has a global field in this validator been changed since it was created (or copied)? */
	protected transient int _changeCountGlobal;
	
	/** How many times has a message field in this validator been changed since it was created (or copied)? */
	protected transient int _changeCountMessages;
	
	/** Has the validator been migrated from an earlier version in this session, but not yet saved? */
	private boolean _migrated;
		
	void setMigrated(boolean migrated){
		_migrated = migrated;
	}
	
	/**
	 * Create a new validator based on a abstract validator.
	 * 
	 * @param validator
	 *            The validator that is being wrapped.
	 * 
	 * @param project
	 *            The project that you are defined in. This can be null which
	 *            means that you are a global validator.
	 */
	public static Validator create(IConfigurationElement validator, IProject project) {
		V2 v2 = new V2(validator, project);
		return v2;
	}
	
	/**
	 * Create a new validator based on validator meta data.
	 * 
	 * @param project
	 *            The project that you are defined in. This can be null which
	 *            means that you are a global validator.
	 */
	public static Validator create(ValidatorMetaData vmd, ValidationConfiguration config, IProject project){
		V1 v1 = new V1(vmd, config);
		v1._project = project;
		return v1;
	}
	
	/**
	 * If you are a version 1 validator, answer yourself as one, otherwise answer null.
	 */
	public V1 asV1Validator(){
		return null;
	}
	
	/**
	 * If you are a version 2 validator, answer yourself as one, otherwise answer null.
	 */
	public V2 asV2Validator() {
		return null;
	}
	
	/**
	 * The project is being cleaned, this method gives the validator a chance to do any special cleanup.
	 * The default is to do nothing.
	 * 
	 * @param project the project being built.
	 * @param monitor the monitor that should be used for reporting progress if the clean takes a long time.
	 */
	public void clean(IProject project, ValOperation operation, IProgressMonitor monitor){
	}	
	
	/**
	 * Compare yourself based on Validator name.
	 */
	public int compareTo(Validator validator) {
		return getName().compareTo(validator.getName());			
	}
	
	/** Answer a deep copy of yourself. */
	public Validator copy(){
		return copy(false);
	}
	
	public abstract Validator copy(boolean includeChangeCounts);
	
	/**
	 * Update your direct, non transient fields from the fields in v.
	 */
	protected void copyLocal(Validator v, boolean includeChangeCounts){
		_buildValidation = v._buildValidation;
		_delegatingId = v._delegatingId;
		_manualValidation = v._manualValidation;
		_markerId = v._markerId;
		_messageSettings = v._messageSettings;
		_project = v._project;
		_sourceId = v._sourceId;
		_version = v._version;
		_migrated = v._migrated;
		
		if (includeChangeCounts){
			_changeCountGlobal = v._changeCountGlobal;
			_changeCountMessages = v._changeCountMessages;
		}
	}
	
	/**
	 * Should the validation framework first clear the markers that this
	 * validator has placed on this resource? This method can be overridden by
	 * validator implementors to provide a validator specific behavior.
	 * 
	 * @param event
	 *            The validation event that triggered the validation.
	 * @return true if the validation framework should first clear all the
	 *         markers that this validator produced. This is the default
	 *         behavior. Return false to leave the markers unchanged. It then
	 *         becomes the responsibility of the validator to manage it's own
	 *         markers for this resource, for this validation event.
	 */
	public boolean shouldClearMarkers(ValidationEvent event){
		return true;
	}
	
	/**
	 * Answer true if this validator, based on it's filters, should validate
	 * this resource. This method does not check to see if global validation or
	 * project validation has been suspended or not.
	 * 
	 * @param resource
	 *            The resource to be checked.
	 * @param isManual
	 *            If true then this validator must also be enabled for manual
	 *            validation.
	 * @param isBuild
	 *            If true then this validator must also be enabled for builder
	 *            based validation.
	 * 
	 * @return true if the resource should be validated.
	 */
	public boolean shouldValidate(IResource resource, boolean isManual, boolean isBuild){
		return shouldValidate(resource, isManual, isBuild, new ContentTypeWrapper());
	}
	
	/**
	 * Answer true if this validator, based on it's filters, should validate
	 * this resource. This method does not check to see if global validation or
	 * project validation has been suspended or not.
	 * 
	 * @param resource
	 *            The resource to be checked.
	 * @param isManual
	 *            If true then this validator must also be enabled for manual
	 *            validation.
	 * @param isBuild
	 *            If true then this validator must also be enabled for builder
	 *            based validation.
	 * @param contentTypeWrapper 
	 *            For repeated calls on the same resource, it is more efficient
	 *            to remember the content type.
	 * @return true if the resource should be validated.
	 * @see Friend#shouldValidate(Validator, IResource, boolean, boolean, ContentTypeWrapper)
	 */
	boolean shouldValidate(IResource resource, boolean isManual, boolean isBuild, 
		ContentTypeWrapper contentTypeWrapper){
		
		if (isManual && !_manualValidation)return false;
		if (isBuild && !_buildValidation)return false;
		
		return shouldValidate(resource, contentTypeWrapper);
	}
	
	/**
	 * Answer true if this validator, based on it's filters, should validate
	 * this resource. This method does not check to see if global validation or
	 * project validation has been suspended or not.
	 * 
	 * @param resource
	 *            The resource to be checked.
	 * @param valType
	 *            The context to use when performing the check.
	 * 
	 * @return true if the resource should be validated.
	 */
	public boolean shouldValidate(IResource resource, ValType valType){
		return shouldValidate(resource, valType, new ContentTypeWrapper());
	}
	
	/**
	 * Answer true if this validator, based on it's filters, should validate
	 * this resource. This method does not check to see if global validation or
	 * project validation has been suspended or not.
	 * 
	 * @param resource
	 *            The resource to be checked.
	 * @param valType
	 *            The context to use when performing the check.
	 * @param contentTypeWrapper
	 *            For repeated calls on the same resource, it is more efficient
	 *            to remember the content type.
	 * 
	 * @return true if the resource should be validated.
	 * 
	 * @see Friend#shouldValidate(Validator, IResource, ValType, ContentTypeWrapper)
	 */
	boolean shouldValidate(IResource resource, ValType valType, ContentTypeWrapper contentTypeWrapper){
		if (Tracing.matchesExtraDetail(getId())){
			Tracing.log("Validator-01: checking if " + getId() + " should validate " + resource); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (valType == ValType.Manual && !_manualValidation)return false;
		if (valType == ValType.Build && !_buildValidation)return false;
		
		boolean result = shouldValidate(resource, contentTypeWrapper);
		if (Tracing.matchesExtraDetail(getId())){
			Tracing.log("Validator-02: result = " + result); //$NON-NLS-1$
		}
		
		return result;
	}
	
	/**
	 * Answer true if this validator, based on it's filters, should validate this project. This method
	 * does not check to see if global validation or project validation has been suspended or not.
	 * 
	 * @param project the project to be checked
	 * @param type The type of validation request
	 * 
	 * @return true if the project should be validated.
	 */
	public boolean shouldValidateProject(IProject project, ValType type){
		return shouldValidateProject(project, type == ValType.Manual, type == ValType.Build);
	}
	
	/**
	 * Answer true if this validator, based on it's filters, should validate this project. This method
	 * does not check to see if global validation or project validation has been suspended or not.
	 * 
	 * @param project the project to be checked
	 * @param isManual if true then this validator must also be enabled for manual validation.
	 * @param isBuild if true then this validator must also be enabled for builder based validation.
	 * 
	 * @return true if the project should be validated.
	 */
	public boolean shouldValidateProject(IProject project, boolean isManual, boolean isBuild){
		if (Tracing.matchesExtraDetail(getId())){
			Tracing.log("Validator-03: checking if " + getId() + " should validate " + project); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (isManual && !_manualValidation)return false;
		if (isBuild && !_buildValidation)return false;
		if (project == null || !project.isOpen())return false;
		boolean result = shouldValidateProject(project);
		if (Tracing.matchesExtraDetail(getId())){
			Tracing.log("Validator-04: result " + result); //$NON-NLS-1$
		}
		return result;
	}
	
	/**
	 * Validate the resource.
	 * 
	 * @param resource
	 *            The resource to be validated.
	 * @param kind
	 *            The kind of resource change, see IResourceDelta for values.
	 * @param operation
	 *            The operation that this validation is running under. This can
	 *            be null.
	 * @param monitor
	 *            A way to report progress. This can be null.
	 * 
	 * @return the result of doing the validation, it can be, but usually isn't
	 *         null.
	 */
	public abstract ValidationResult validate(IResource resource, int kind, ValOperation operation, IProgressMonitor monitor);
	
	/**
	 * Validate the resource.
	 * 
	 * @param resource
	 *            The resource to be validated.
	 * @param kind
	 *            The kind of resource change, see IResourceDelta for values.
	 * @param operation
	 *            The operation that this validation is running under. This can
	 *            be null.
	 * @param monitor
	 *            A way to report progress. This can be null.
	 * @param event
	 *            An event that describes in more detail what should be
	 *            validated and why it should be validated. This can be null.
	 * 
	 * @return the result of doing the validation, it can be, but usually isn't
	 *         null.
	 */
	public ValidationResult validate(IResource resource, int kind, ValOperation operation, IProgressMonitor monitor, ValidationEvent event){
		// The reason that the resource and kind are still specified, is that I didn't want to remove a public method in the service
		// stream. 
		return validate(resource, kind, operation, monitor);		
	}

	/**
	 * This method will be called before any validation takes place. It allows validators to perform any
	 * initialization that they might need. 
	 *  
	 * @param project the project that is being validated. For the very first call in the validation phase,
	 * this will be null. That is the signal to the validator that a top level validation is starting.
	 * Subsequently, the project will be set, as each of the individual projects are validated.
	 * 
	 * @param state a way to pass arbitrary, validator specific, data from one invocation of a validator to
	 * the next, during the validation phase.
	 * 
	 * @param monitor the monitor that should be used for reporting progress if the clean takes a long time.
	 */
	public void validationStarting(IProject project, ValidationState state, IProgressMonitor monitor){
		// subclasses need to override this, if they wish to let their validators know about this event
	}
	
	/**
	 * This method will be called when validation is complete. It allows validators to perform any
	 * cleanup that they might need to do.  
	 *  
	 * @param project the project that was validated. The very last call in the validation will set this to 
	 * null so that the validator knows that all the projects have now been validated.
	 * 
	 * @param state a way to pass arbitrary, validator specific, data from one invocation of a validator to
	 * the next, during the validation phase.
	 * 
	 * @param monitor the monitor that should be used for reporting progress if the clean takes a long time.
	 */
	public void validationFinishing(IProject project, ValidationState state, IProgressMonitor monitor){		
		// subclasses need to override this, if they wish to let their validators know about this event
	}
	
	void add(MessageSeveritySetting message){
		// I can't use getMessageSettings() here, as that will put us into an infinite loop
		if (_messageSettings == null){
			_messageSettings = new HashMap<String, MessageSeveritySetting>(10);
		}
		_messageSettings.put(message.getId(), message);
		bumpChangeCountMessages();
	}
	
	public IValidator asIValidator(){
		return null;
	}
	
	protected abstract boolean shouldValidate(IResource resource, ContentTypeWrapper contentTypeWrapper);
	protected abstract boolean shouldValidateProject(IProject project);
			
	public abstract String getId();
	
	public MessageSeveritySetting getMessage(String id){
		return getMessageSettings().get(id);
	}
	
	/**
	 * Answer all the message settings that this validator has defined.
	 * 
	 * @return an empty map if the validator did not define any message settings.
	 */
	public Map<String, MessageSeveritySetting> getMessageSettings(){
		Map<String, MessageSeveritySetting> settings = _messageSettings;
		if (settings == null){
			settings = new HashMap<String, MessageSeveritySetting>(10);
			init(settings);
			if (ValManager.getDefault().mustUseGlobalValidators(getProject())){
				ValPrefManagerGlobal gp = ValPrefManagerGlobal.getDefault();
				gp.loadMessages(this, settings);
			}
			else {
				ValPrefManagerProject vp = new ValPrefManagerProject(getProject());
				vp.loadMessages(this, settings);				
			}
			_messageSettings = settings;
		}
		return settings;
	}
	
	/**
	 * Answer a hash code for the configurable fields so that we can quickly determine if two
	 * validators are the same.
	 */
	public int hashCodeForConfig(){
		int h = 0;
		if (_buildValidation)h += 101;
		if (_delegatingId != null)h += _delegatingId.hashCode();
		if (_manualValidation)h += 201;
		if (_messageSettings != null){
			for (MessageSeveritySetting ms : _messageSettings.values())h += ms.hashCode();
		}
		if (_sourceId != null)h += _sourceId.hashCode();
		h += _version;
		return h;
	}
	
	private void init(Map<String, MessageSeveritySetting> settings) {
		for (MessageSeveritySetting ms : ValidatorExtensionReader.getDefault().addMessages(this)){
			settings.put(ms.getId(), ms);
		}		
	}

	public abstract String getName();
	
	/**
	 * Answer the project that you were enabled on. 
	 * 
	 * @return null if you are a global (i.e. workspace level) validator.
	 */
	public IProject getProject(){
		return _project;
	}
		
	/**
	 * Answer the name of the class that implements the validator.
	 */
	public abstract String getValidatorClassname();
	
	/**
	 * Is this validator currently enabled for validations that are triggered manually? 
	 */
	public boolean isManualValidation() {
		return _manualValidation;
	}

	/**
	 * Set whether this validator should be triggered as part of a manual validation.
	 * 
	 * @param manualValidation
	 * @return true if the setting changed.
	 */
	public boolean setManualValidation(boolean manualValidation) {
		return setManualValidation2(manualValidation);
	}
	
	protected final boolean setManualValidation2(boolean manualValidation) {
		boolean changed = false;
		if (_manualValidation != manualValidation){
			bumpChangeCountGlobal();
			changed = true;
			_manualValidation = manualValidation;
		}
		return changed;
	}

	/**
	 * Is this validator currently enabled for validations that are triggered by builds? 
	 */
	public boolean isBuildValidation() {
		return _buildValidation;
	}
	
	/**
	 * Has the validator changed since it was last created or copied? Or was it migrated from an earlier version. 
	 */
	public boolean isChanged(){
		if (_changeCountGlobal > 0 || _changeCountMessages > 0 || _migrated)return true;
		return false;
	}
	
	/**
	 * Has the validator's implementation been loaded yet? This is used by some test cases to ensure that 
	 * plug-ins are not loaded too early.
	 */
	abstract boolean isLoaded();
	

	/**
	 * Set whether this validator should be triggered by the build process.
	 * 
	 * @param buildValidation
	 * @return true if the setting changed.
	 */
	public boolean setBuildValidation(boolean buildValidation) {
		return setBuildValidation2(buildValidation);
	}
	
	protected final boolean setBuildValidation2(boolean buildValidation) {
		boolean changed = false;
		if (_buildValidation != buildValidation){
			bumpChangeCountGlobal();
			changed = true;
			_buildValidation = buildValidation;
		}
		return changed;
	}

	/**
	 * Get the id of the "real" validator, that is the validator that will be called when this delegating
	 * validator is asked to validate something. If this isn't a delegating validator answer null.
	 */
	public String getDelegatingId() {
		return _delegatingId;
	}
	
	/**
	 * Set the id of the "real" validator, that is the validator that will be called when this delegating
	 * validator is asked to validate something.
	 * 
	 * @param delegating the id of the validator that is actually going to perform the validation.
	 */
	public void setDelegatingId(String delegating) {
		if (!Misc.same(_delegatingId, delegating)){
			_delegatingId = delegating;
			bumpChangeCountGlobal();
		}
	}
	
	public int getVersion() {
		return _version;
	}
	
	public void setVersion(int version) {
		if (_version != version){
			_version = version;
			bumpChangeCountGlobal();
		}
	}
	
	@Override
	public String toString() {
		return getName();
	}
		
/**
 * A validator that uses version 1 of the validation framework.
 * @author karasiuk
 *
 */ 
public static class V1 extends Validator {
	private ValidatorMetaData _vmd;
	
	/**
	 * Create a new version 1 validator. 
	 * @param vmd
	 * @param config this is used to set the global enablement options. In some case this can be null.
	 */
	public V1(ValidatorMetaData vmd, ValidationConfiguration config){
		_vmd = vmd;
		if (config != null){
			setBuildValidation(config.isBuildEnabled(vmd));
			setManualValidation(config.isManualEnabled(vmd));
		}
		setDelegatingId(ValidatorDelegatesRegistry.getInstance().getDefaultDelegate(_vmd.getValidatorUniqueName()));
		if (_vmd.getMarkerIds() != null && _vmd.getMarkerIds().length > 0)setMarkerId(_vmd.getMarkerIds()[0]);
		resetChangeCounters();
	}
	
	@Override
	public IValidator asIValidator() {
		IValidator v = null;
		try {
			v = _vmd.getValidator();
		}
		catch (InstantiationException e){
			ValidationPlugin.getPlugin().handleException(e);
			return null;
		}
		return v;
	}
	
	public V1 asV1Validator() {
		return this;
	}
	
	@Override
	public void become(Validator val) {
		super.become(val);
		V1 v1 = val.asV1Validator();
		if (v1 == null)throw new IllegalArgumentException("Internal error, the incoming validator must be a v1 validator"); //$NON-NLS-1$
		_vmd = v1._vmd;
	}
		
	public Validator copy(boolean includeChangeCounts) {
		V1 v = new V1Copy(_vmd, null);
		v.copyLocal(this, includeChangeCounts);
				
		return v;
	}
	
	public String getName() {
		return _vmd.getValidatorDisplayName();
	}
	
	public ValidatorMetaData getVmd(){
		return _vmd;
	}
	
	public String getValidatorClassname(){
		String name = ""; //$NON-NLS-1$
		try {
			name = _vmd.getValidator().getClass().getName();
		}
		catch (Exception e){
			// eat it
		}
		return name;
	}
	public String getId() {
		return _vmd.getValidatorUniqueName();
	}
	
	@Override
	boolean isLoaded() {
		return _vmd.isActive();
	}
	
	@Override
	public boolean setBuildValidation(boolean buildValidation) {
		boolean changed = super.setBuildValidation(buildValidation);
		_vmd.setBuildValidation(buildValidation);
		return changed;
	}
	
	@Override
	public boolean setManualValidation(boolean manualValidation) {
		boolean changed = super.setManualValidation(manualValidation);
		_vmd.setManualValidation(manualValidation);
		return changed;
	}

	@Override
	protected boolean shouldValidate(IResource resource, ContentTypeWrapper contentTypeWrapper) {
		return _vmd.isApplicableTo(resource);
	}

	@Override
	protected boolean shouldValidateProject(IProject project) {
		// TODO determine if this can be optimized
		return true;
	}

	@Override
	public ValidationResult validate(IResource resource, int kind, ValOperation operation, 
		IProgressMonitor monitor) {
		
		if (monitor == null)monitor = new NullProgressMonitor();
		
		ValidationResult vr = new ValidationResult();
		IValidator v = asIValidator();
		if (v == null)return null;
		
		try {
			IProject project = resource.getProject();
			SummaryReporter reporter = new SummaryReporter(project, monitor);
			IWorkbenchContext helper = _vmd.getHelper(project);
			if (helper instanceof WorkbenchContext){
				WorkbenchContext wc = (WorkbenchContext)helper;
				List<String> files = new LinkedList<String>();
				// [213631] The JSP validator expects full paths not relative paths, but the XML validator
				// expects relative paths.
				files.add(wc.getPortableName(resource));
				wc.setValidationFileURIs(files);
			}
			try {
				ValidatorLauncher.getLauncher().start(helper, v, reporter);
			}
			finally {
				helper.cleanup(reporter);
			}
			
			vr.incrementError(reporter.getSeverityHigh());
			vr.incrementWarning(reporter.getSeverityNormal());
			vr.incrementInfo(reporter.getSeverityLow());
			
		}
		catch (Exception e){
			ValidationPlugin.getPlugin().handleException(e);
		}
		return vr;
	}
	
	/*
	 * GRK - Because I didn't want to try to make a true copy of the V1 validator, (because I didn't
	 * want to copy the vmd object), I came up with this approach to only copy the fields that
	 * the preference page was worried about. 
	 */
	public static class V1Copy extends V1 {
		public V1Copy(ValidatorMetaData vmd, ValidationConfiguration vc){
			super(vmd, vc);
		}
		
		@Override
		public boolean setManualValidation(boolean bool) {
			return setManualValidation2(bool);
		}
		
		@Override
		public boolean setBuildValidation(boolean bool) {
			return setBuildValidation2(bool);
		}
		
		@Override
		public void become(Validator val) {
			super.become(val);
			super.setBuildValidation(val.isBuildValidation());
			super.setManualValidation(val.isManualValidation());
		}
		
	}
		
}

/**
 * A validator that uses version 2 of the validation framework.
 * @author karasiuk
 *
 */
public final static class V2 extends Validator implements IAdaptable {
	private AbstractValidator	_validator;
	
	private List<FilterGroup>	_groups = new LinkedList<FilterGroup>();
	private FilterGroup[]		_groupsArray;
	
	/** The full id of the extension. */
	private String			_id;
	
	/** Name of the validator. */
	private String			_name;
	
	/** 
	 * We don't want to create the validator too early, as it may trigger new plug-ins to be loaded.
	 * We delay that as long as possible, by starting with just the config element.
	 */
	private IConfigurationElement _validatorConfigElement;
	
	private String	_validatorClassName;
	
	/**
	 * An array containing the validator group IDs for which this validator is a member.
	 */
	private String[] _validatorGroupIds;
		
	/** 
	 * If this validator is a delegating validator, then this is the "real" validator (i.e. the one that
	 * does the work).
	 */
	private AbstractValidator	_delegated;
		
	/** How many times has a group field in this validator been changed since it was created (or copied)? */
	protected transient int _changeCountGroups;
		
	private Level _level;
	
	/**
	 * Do we still need to invoke the validateStarting method for this validator, for the null project?
	 * 
	 * Because we do not want to activate a validator's plug-in too soon, we do not activate the validator
	 * as a reaction to the global validation starting event. Instead we mark it pending, and wait until
	 * we are sure that we have something to validate.
	 * 
	 * If this flag is true, it means that the validateStarting method still needs to be called for this validator.
	 */
	private AtomicBoolean _pendingValidationStarted = new AtomicBoolean();
	
	V2(IConfigurationElement configElement, IProject project){
		assert configElement != null;
		_validatorConfigElement = configElement;
		_validatorClassName = configElement.getAttribute(ExtensionConstants.AttribClass);
		_project = project;

		IConfigurationElement[] groupReferenceElements = configElement.getChildren(ExtensionConstants.Group.elementGroup);
		List<String> validatorGroupIDs = new ArrayList<String>();
		for (IConfigurationElement groupElement : groupReferenceElements) {
			String id = groupElement.getAttribute(ExtensionConstants.Group.attId);
			if (id != null)validatorGroupIDs.add(id);
		}
		_validatorGroupIds = validatorGroupIDs.toArray(new String[validatorGroupIDs.size()]);
			
		init();
	}
	
	private V2(IProject project, String validatorClassName, AbstractValidator validator){
		assert validator != null;
		
		_project = project;
		_validatorClassName = validatorClassName;
		_validator = validator;
		init();
	}
	
	private void init(){
		try {
			String id = ConfigurationManager.getManager().getConfiguration(_project).getDelegateForTarget(_validatorClassName);
			if (id == null) id = ValidatorDelegatesRegistry.getInstance().getDefaultDelegate(_validatorClassName);
			setDelegatingId(id);
		}
		catch (InvocationTargetException e){
			ValidationPlugin.getPlugin().handleException(e);
		}
		resetChangeCounters();		
	}

	public synchronized void add(FilterGroup fg) {
		assert fg != null;
		_groupsArray = null;
		_groups.add(fg);
		bumpChangeCountGroups();
	}
	
	@Override
	public IValidator asIValidator() {
		AbstractValidator av = getDelegatedValidator();
		if (av instanceof IValidator)return (IValidator)av;
		return super.asIValidator();
	}
	
	@Override
	public V2 asV2Validator() {
		return this;
	}
	
	/**
	 * Let the validator know that a clean is about to happen.
	 * 
	 * @param project the project that is being cleaned. This can be null which means that the workspace
	 * is being cleaned (in which case a separate call will be made for each open project).
	 * 
	 */
	@Override
	public void clean(IProject project, ValOperation operation, IProgressMonitor monitor) {
		getDelegatedValidator().clean(project, operation.getState(), monitor);
	}
	
	@Override
	public Validator copy(boolean includeChangeCounts) {
		V2 v = null;
		if (_validatorConfigElement != null)v = new V2(_validatorConfigElement, _project);
		else v = new V2(_project, _validatorClassName, _validator);
		v.copyLocal(this, includeChangeCounts);
		
		if (includeChangeCounts)v._changeCountGroups = _changeCountGroups;
		
		FilterGroup[] groups = getGroups();
		v._groupsArray = new FilterGroup[groups.length];
		for (int i=0; i<groups.length; i++){
			v._groupsArray[i] = groups[i];
			v._groups.add(groups[i]);
		}

		v._id = _id;
		v._name = _name;
		v._validatorGroupIds = _validatorGroupIds;
		v._pendingValidationStarted = _pendingValidationStarted;
				
		return v;
	}
	
	public int getChangeCountGroups(){
		return _changeCountGroups;
	}
	
	public void bumpChangeCountGroups(){
		_changeCountGroups++;
	}
	
	public Level getLevel() {
		return _level;
	}

	public void setLevel(Level level) {
		assert _level == null;
		_level = level;
	}
	
	/**
	 * Answer the actual validator that is going to perform the validation. If this is a normal validator this
	 * method will simply answer itself. However if this is a delegating validator, then this will answer the
	 * "real" validator.
	 */
	public AbstractValidator getDelegatedValidator(){
		AbstractValidator delegated = _delegated;
		if (delegated != null)return delegated;
		else if (getDelegatingId() == null)return getValidator();
		try {
			ValidatorDelegateDescriptor vdd = ValidatorDelegatesRegistry.getInstance()
				.getDescriptor(getValidatorClassname(), getDelegatingId());
			if (vdd == null)return getValidator();
			delegated = vdd.getValidator2();
		}
		catch (Exception e){
			ValidationPlugin.getPlugin().handleException(e);
			delegated = new NullValidator();
		}
		delegated.setParent(this);
		_delegated = delegated;
		return delegated;
	}
		
	@Override
	public String getId() {
		return _id;
	}
	
	/**
	 * Answer the validator's filter groups.
	 * @return an empty array if the validator does not have any filter groups.
	 */
	public synchronized FilterGroup[] getGroups(){
		FilterGroup[] groups = _groupsArray;
		if (groups == null){
			groups = new FilterGroup[_groups.size()];
			_groups.toArray(groups);
			_groupsArray = groups;
		}
		return groups;
	}
	
	@Override
	public String getName() {
		return _name;
	}
	
	public AbstractValidator getValidator() {
		if (_validator == null){
			try {
				_validator = (AbstractValidator)_validatorConfigElement.createExecutableExtension(ExtensionConstants.AttribClass);
			}
			catch (Exception e){
				ValidationPlugin.getPlugin().handleException(e);
				IContributor contrib = _validatorConfigElement.getContributor();
				String message = NLS.bind(ValMessages.ErrConfig, contrib.getName());
				ValidationPlugin.getPlugin().logMessage(IStatus.ERROR, message);
				_validator = new NullValidator();
			}
			_validator.setParent(this);
			_validatorConfigElement = null;

		}
		return _validator;
	}
	
	@Override
	public String getValidatorClassname(){
		return _validatorClassName;
	}
	
	public String[] getValidatorGroups(){
		return _validatorGroupIds;
	}
	
	@Override
	public int hashCodeForConfig() {
		int h =  super.hashCodeForConfig();
		if (_id != null)h += _id.hashCode();
		if (_groups != null){
			for (FilterGroup fg : _groups)h += fg.hashCodeForConfig();
		}
		return h;
	}
	
	@Override
	public boolean isChanged() {
		if (_changeCountGroups > 0)return true;
		return super.isChanged();
	}
	
	@Override
	boolean isLoaded() {
		return _validator != null;
	}
	
	@Override
	public boolean shouldClearMarkers(ValidationEvent event) {
		return getValidator().shouldClearMarkers(event);
	}
		
	/**
	 * Answer true if this validator, based on it's filters, should validate this resource.
	 * 
	 * @return true if the resource should be validated.
	 */
	@Override
	protected boolean shouldValidate(IResource resource, ContentTypeWrapper contentTypeWrapper) {
		FilterGroup[] groups = getGroups();
		IProject project = resource.getProject();
		for (FilterGroup group : groups){
			if (!group.shouldValidate(project, resource, contentTypeWrapper))return false;
		}
		return true;
	}
	
	
	@Override
	public void setDelegatingId(String delegating) {
		super.setDelegatingId(delegating);
		_delegated = null;
	}
	
	public synchronized void setGroups(List<FilterGroup> groups){
		_groups = groups;
		_groupsArray = null;
		bumpChangeCountGroups();
	}

	public void setId(String id) {
		if (!Misc.same(_id, id)){
			_id = id;
			bumpChangeCountGlobal();
		}
	}
	
	public void setName(String name) {
		if (!Misc.same(_name, name)){
			_name = name;
			bumpChangeCountGlobal();
		}
	}
	
	@Override
	public ValidationResult validate(IResource resource, int kind, ValOperation operation, IProgressMonitor monitor){
		return validate(resource, kind, operation, monitor, null);
	}
	
	@Override
	public ValidationResult validate(IResource resource, int kind, ValOperation operation, IProgressMonitor monitor, ValidationEvent event) {
		ValidationResult vr = null;
		if (operation == null)operation = new ValOperation();
		if (monitor == null)monitor = new NullProgressMonitor();
		try {
			if (event == null)event = new ValidationEvent(resource, kind, null);
			vr = getDelegatedValidator().validate(event, operation.getState(), monitor);
			if (vr == null)vr = getDelegatedValidator().validate(resource, kind, operation.getState(), monitor);
		}
		catch (Exception e){
			try {
				String msg = NLS.bind(ValMessages.LogValEnd, getName(), resource.getLocationURI());
				ValidationPlugin.getPlugin().logMessage(IStatus.ERROR, msg);
			}
			catch (Exception e2 ){
				// ignore it
			}
			ValidationPlugin.getPlugin().handleException(e);
		}
		
		if (vr != null){
			if (vr.getValidationException() != null){
				ValidationPlugin.getPlugin().handleException(vr.getValidationException());
			}
			updateResults(vr);
			if (vr.getDependsOn() != null){
				ValidationFramework.getDefault().getDependencyIndex().set(getId(), resource, vr.getDependsOn());
			}
			IResource[] validated = vr.getValidated();
			if (validated != null){
				for (int i=0; i<validated.length; i++){
					operation.addValidated(getId(), validated[i]);
				}
			}
			
			ValidatorMessage[] msgs = vr.getMessages();
//			if (sanityTest(msgs.length, resource)){
				MarkerManager mm = MarkerManager.getDefault();
				for (ValidatorMessage m : msgs){
					mm.createMarker(m, getId());
				}
//			}
//			else {
//				setBuildValidation(false);
//				setManualValidation(false);
//			}
		}
		return vr;		
	}
	
	/**
	 * Perform a simple sanity test to ensure that the validator is configured correctly.
	 * @param numberofMessages number of messages that the validator produced.
	 * @return true if the test passed
	 */
//	private boolean sanityTest(int numberofMessages, IResource resource) {
//		//FIXME make this more general and configurable
//		if (numberofMessages < 201)return true;
//		
//		String resName = ""; //$NON-NLS-1$
//		if (resource != null)resName = resource.getName();
//		String message = NLS.bind(ValMessages.ConfigError, new Object[]{
//				getName(), getId(), String.valueOf(numberofMessages), resName});
//		ValidationPlugin.getPlugin().logMessage(IStatus.ERROR, message);
//		
//		return false;
//	}

	/**
	 * If the validator is using a report helper then update it with any of the messages that were
	 * added directly to the validation result.
	 * @param vr
	 */
	private void updateResults(ValidationResult vr) {
		ReporterHelper rh = vr.getReporterHelper();
		if (rh == null)return;
		ClassLoader classloader = getDelegatedValidator().getClass().getClassLoader();
		for (IMessage message : rh.getMessages()){
			Object target = message.getTargetObject();
			if (target != null){
				IResource res = null;
				if (target instanceof IResource)res = (IResource)target;
				if (res == null){
					target = message.getAttribute(IMessage.TargetResource);
					if (target != null && target instanceof IResource)res = (IResource)target;
				}
				if (res != null){
					
					ValidatorMessage vm = ValidatorMessage.create(message.getText(classloader), res);
					if (getMarkerId() != null)vm.setType(getMarkerId());
					vr.add(vm);
					int markerSeverity = IMarker.SEVERITY_INFO;
					int sev = message.getSeverity();
					if ((sev & IMessage.HIGH_SEVERITY) != 0)markerSeverity = IMarker.SEVERITY_ERROR;
					else if ((sev & IMessage.NORMAL_SEVERITY) != 0)markerSeverity = IMarker.SEVERITY_WARNING;
					vm.setAttribute(IMarker.SEVERITY, markerSeverity);
					vm.setAttribute(IMarker.LINE_NUMBER, message.getLineNumber());
					int offset = message.getOffset();
					if (offset != IMessage.OFFSET_UNSET){
						vm.setAttribute(IMarker.CHAR_START, offset);
						int len = message.getLength();
						if (len != IMessage.OFFSET_UNSET){
							vm.setAttribute(IMarker.CHAR_START, offset);
							vm.setAttribute(IMarker.CHAR_END, offset+len);
						}
					}
					String groupName = message.getGroupName();
					if (groupName != null){
						vm.setAttribute(ConfigurationConstants.VALIDATION_MARKER_GROUP, groupName);
					}
					
					copyAttributes(message, vm);
				}
			}
		}		
	}

	@SuppressWarnings("unchecked")
	private void copyAttributes(IMessage message, ValidatorMessage vm) {
		// I made this a separate method, so that I could localize the suppression of unchecked warnings.
		Map attributes = message.getAttributes();
		if (attributes != null){						
			for (Iterator it = attributes.entrySet().iterator(); it.hasNext();){
				Map.Entry me = (Map.Entry)it.next();
				String key = (String)me.getKey();
				vm.setAttribute(key, me.getValue());
			}
		}
	}
	
	@Override
	public void validationStarting(IProject project, ValidationState state, IProgressMonitor monitor) {
		if (project == null)_pendingValidationStarted.set(true);
		else {
			AbstractValidator val = getDelegatedValidator();
			if (_pendingValidationStarted.getAndSet(false)){
				val.validationStarting(null, state, monitor);
			}
			val.validationStarting(project, state, monitor);
		}
	}
	
	@Override
	public void validationFinishing(IProject project, ValidationState state, IProgressMonitor monitor) {
		if (project == null){
			if (!_pendingValidationStarted.getAndSet(false))getDelegatedValidator().validationFinishing(null, state, monitor);
		}
		else getDelegatedValidator().validationFinishing(project, state, monitor);
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	public synchronized void remove(FilterGroup group) {
		_groups.remove(group);
		_groupsArray = null;	
		bumpChangeCountGroups();
	}
	
	@Override
	public void resetChangeCounters() {
		super.resetChangeCounters();
		_changeCountGroups = 0;
	}

	@Override
	protected boolean shouldValidateProject(IProject project) {
		FilterGroup[] groups = getGroups();
		ContentTypeWrapper ctw = new ContentTypeWrapper();
		for (FilterGroup group : groups){
			if (!group.shouldValidate(project, null, ctw))return false;
		}
		return true;
	}
	
	@Override
	public void become(Validator val) {
		super.become(val);
		V2 v2 = val.asV2Validator();
		if (v2 == null)throw new IllegalArgumentException(ValMessages.Error20);
		_changeCountGroups = v2._changeCountGroups;
		_delegated = v2._delegated;
		_groups = v2._groups;
		_groupsArray = v2._groupsArray;
		_id = v2._id;
		_name = v2._name;
		_pendingValidationStarted = v2._pendingValidationStarted;
		_validator = v2._validator;
		_validatorConfigElement = v2._validatorConfigElement;
		_validatorClassName = v2._validatorClassName;
		_validatorGroupIds = v2._validatorGroupIds;
	}

	public synchronized void replaceFilterGroup(FilterGroup existing, FilterGroup merged) {
		remove(existing);
		add(merged);
	}

}

public String getSourceId() {
	return _sourceId;
}

public void setSourceId(String sourceId) {
	if (!Misc.same(_sourceId, sourceId)){
		_sourceId = sourceId;
		bumpChangeCountGlobal();
	}
}

/**
 * Take the instance variables from the incoming validator and set them to yourself.
 * @param validator
 */
public void become(Validator validator) {
	_buildValidation = validator._buildValidation;
	_delegatingId = validator._delegatingId;
	_manualValidation = validator._manualValidation;
	_markerId = validator._markerId;
	_messageSettings = validator._messageSettings;
	_project = validator._project;
	_sourceId = validator._sourceId;
	_version = validator._version;
	_changeCountGlobal = validator._changeCountGlobal;
	_changeCountMessages = validator._changeCountMessages;
	_migrated = validator._migrated;
}

void setMessages(Map<String, MessageSeveritySetting> map) {
	_messageSettings = map;
	bumpChangeCountMessages();
}

public int getChangeCountGlobal() {
	return _changeCountGlobal;
}

public boolean hasGlobalChanges(){
	return _migrated || _changeCountGlobal > 0;
}

public int getChangeCountMessages() {
	return _changeCountMessages;
}

public void bumpChangeCountMessages(){
	_changeCountMessages++;
}

public void resetChangeCounters() {
	_changeCountGlobal = 0;
	_changeCountMessages = 0;
}

public void bumpChangeCountGlobal(){
	_changeCountGlobal++;
}

/**
 * Answer true if you have the same configuration settings as validator.
 * @param validator this can be null.
 */
public boolean sameConfig(Validator validator) {
	if (validator == null)return false;
	return hashCodeForConfig() == validator.hashCodeForConfig();
}

public String getMarkerId() {
	return _markerId;
}

public void setMarkerId(String markerId) {
	_markerId = markerId;
	if (markerId != null)MarkerManager.getDefault().getMarkers().add(markerId);
}

}
