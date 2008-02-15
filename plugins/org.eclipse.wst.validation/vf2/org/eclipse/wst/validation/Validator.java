package org.eclipse.wst.validation;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.validation.internal.ResourceUnavailableError;
import org.eclipse.wst.validation.internal.SummaryReporter;
import org.eclipse.wst.validation.internal.ValOperation;
import org.eclipse.wst.validation.internal.ValOperationManager;
import org.eclipse.wst.validation.internal.ValidationConfiguration;
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
 * 
 * @author karasiuk
 *
 */
public abstract class Validator implements Comparable {
	
	protected boolean	_buildValidation = true;
	
	/** If this is a delegating validator, then this field holds the validator that will be delegated to. */
	private String 		_delegatingId;
	
	/** 
	 * If this validator is also used to control an ISource validator, the id of the ISource validator is
	 * registered here.
	 */
	private String		_sourceId;
	
	protected boolean 	_manualValidation = true;
	
	/** 
	 * Version of the filter definition. By increasing this number the framework can know that a plug-in has 
	 * changed it's filters.
	 */
	private int			_version = 1;
	
	public static Validator create(AbstractValidator validator) {
		return new V2(validator);
	}
	
	public static Validator create(ValidatorMetaData vmd, ValidationConfiguration config){
		return new V1(vmd, config);
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
	public void clean(IProject project, IProgressMonitor monitor){
	}	
	
	/**
	 * Compare yourself based on Validator name.
	 */
	public int compareTo(Object validator) {
		if (validator instanceof Validator){
			Validator other = (Validator)validator;
			return getName().compareTo(other.getName());			
		}
		return -1;
	}
	
	/** Answer a deep copy of yourself. */
	public abstract Validator copy();
	
	/**
	 * Update your direct, non transient fields from the fields in v.
	 */
	protected void copyLocal(Validator v){
		_buildValidation = v._buildValidation;
		_delegatingId = v._delegatingId;
		_manualValidation = v._manualValidation;
		_version = v._version;
	}
	
	/**
	 * Answer true if this validator, based on it's filters, should validate this resource. This method
	 * does not check to see if global validation or project validation has been suspended or not.
	 * 
	 * @param resource the resource to be checked
	 * @param isManual if true then this validator must also be enabled for manual validation.
	 * @param isBuild if true then this validator must also be enabled for builder based validation.
	 * 
	 * @return true if the resource should be validated.
	 */
	public boolean shouldValidate(IResource resource, boolean isManual, boolean isBuild){
		if (isManual && !_manualValidation)return false;
		if (isBuild && !_buildValidation)return false;
		
		return shouldValidate(resource);
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
		if (isManual && !_manualValidation)return false;
		if (isBuild && !_buildValidation)return false;
		if (project == null || !project.isOpen())return false;
		return shouldValidateProject(project);
	}
	/**
	 * Validate the resource.
	 * 
	 * @param resource the resource to be validated
	 * @param kind the kind of resource change, see IResourceDelta for values.
	 * @param operation the operation that this validation is running under. This can be null.
	 * @param monitor a way to report progress. This can be null.
	 * 
	 * @return the result of doing the validation, it can be, but usually isn't null.
	 */
	public abstract ValidationResult validate(IResource resource, int kind, ValOperation operation, IProgressMonitor monitor);	

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
	
	public IValidator asIValidator(){
		return null;
	}

	
	protected abstract boolean shouldValidate(IResource resource);
	protected abstract boolean shouldValidateProject(IProject project);
			
	public abstract String getId();

	public abstract String getName();
		
	/**
	 * Answer the name of the class that implements the validator.
	 * @return
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
	 */
	public void setManualValidation(boolean manualValidation) {
		_manualValidation = manualValidation;
	}

	/**
	 * Is this validator currently enabled for validations that are triggered by builds? 
	 */
	public boolean isBuildValidation() {
		return _buildValidation;
	}

	/**
	 * Set whether this validator should be triggered by the build process.
	 * 
	 * @param buildValidation
	 */
	public void setBuildValidation(boolean buildValidation) {
		_buildValidation = buildValidation;
	}

	/**
	 * Get the id of the "real" validator, that is the validator that will be called when this delegating
	 * validator is asked to validate something. If this isn't a delegating validator answer null.
	 */
	public String getDelegatingId() {
		return _delegatingId;
	}
	
	public String getDependencyId(){
		return getId();
	}
	
	/**
	 * Set the id of the "real" validator, that is the validator that will be called when this delegating
	 * validator is asked to validate something.
	 * 
	 * @param delegating the id of the validator that is actually going to perform the validation.
	 */
	public void setDelegatingId(String delegating) {
		_delegatingId = delegating;
	}
	
	public int getVersion() {
		return _version;
	}
	
	public void setVersion(int version) {
		_version = version;
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
public final static class V1 extends Validator {
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
		setDelegatingId(ValidatorDelegatesRegistry.getInstance().getDefaultDelegate(getValidatorClassname()));
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
		
	public Validator copy() {
		V1 v = new V1(_vmd, null);
		v.copyLocal(this);
				
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
	public void setBuildValidation(boolean buildValidation) {
		super.setBuildValidation(buildValidation);
		_vmd.setBuildValidation(buildValidation);
	}
	
	@Override
	public void setManualValidation(boolean manualValidation) {
		super.setManualValidation(manualValidation);
		_vmd.setManualValidation(manualValidation);
	}

	@Override
	protected boolean shouldValidate(IResource resource) {
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
			ValidatorLauncher.getLauncher().start(helper, v, reporter);
			
			vr.incrementError(reporter.getSeverityHigh());
			vr.incrementWarning(reporter.getSeverityNormal());
			vr.incrementInfo(reporter.getSeverityLow());
			
		}
		catch (Exception e){
			ValidationPlugin.getPlugin().handleException(e);
		}
		return vr;
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
	 * If this validator is a delegating validator, then this is the "real" validator (i.e. the one that
	 * does the work).
	 */
	private AbstractValidator	_delegated;
	
	public V2(AbstractValidator base){
		_validator = base;
		setDelegatingId(ValidatorDelegatesRegistry.getInstance().getDefaultDelegate(getValidatorClassname()));
	}

	public synchronized void add(FilterGroup fg) {
		_groupsArray = null;
		_groups.add(fg);
	}
	
	@Override
	public IValidator asIValidator() {
		AbstractValidator av = getDelegatedValidator();
		if (av instanceof IValidator)return (IValidator)av;
		return super.asIValidator();
	}
	
	public V2 asV2Validator() {
		return this;
	}
	
	/**
	 * Let the validator know that a clean is about to happen.
	 * 
	 * @param project the project that is being cleaned. This can be null which means that the workspace
	 * is being cleaned (in which case a separate call will be made for each open project).
	 */
	public void clean(IProject project, IProgressMonitor monitor) {
		getDelegatedValidator().clean(project, ValOperationManager.getDefault().getOperation().getState(), monitor);
	}
	
	public Validator copy() {
		V2 v = new V2(_validator);
		v.copyLocal(this);
		
		FilterGroup[] groups = getGroups();
		v._groupsArray = new FilterGroup[groups.length];
		for (int i=0; i<groups.length; i++){
			v._groupsArray[i] = groups[i].copy();
			v._groups.add(v._groupsArray[i]);
		}

		v._id = _id;
		v._name = _name;
				
		return v;
	}
	
	public String getDependencyId(){
		String id = getDelegatedValidator().getDependencyId();
		if (id != null)return id;
		return getId();
	}
	
	/**
	 * Answer the actual validator that is going to perform the validation. If this is a normal validator this
	 * method will simply answer itself. However if this is a delegating validator, then this will answer the
	 * "real" validator.
	 *
	 * @return
	 */
	public AbstractValidator getDelegatedValidator(){
		AbstractValidator delegated = _delegated;
		if (delegated != null)return delegated;
		else if (getDelegatingId() == null)return _validator;
		try {
			ValidatorDelegateDescriptor vdd = ValidatorDelegatesRegistry.getInstance()
				.getDescriptor(getValidatorClassname(), getDelegatingId());
			if (vdd == null)return _validator;
			delegated = vdd.getValidator2();
		}
		catch (Exception e){
			ValidationPlugin.getPlugin().handleException(e);
		}
		if (delegated == null)return _validator;
		_delegated = delegated;
		return delegated;
	}
	
	public String getId() {
		return _id;
	}
	
	/**
	 * Answer the validator's filter groups.
	 *  
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
	
	public String getName() {
		return _name;
	}
	
	public AbstractValidator getValidator() {
		return _validator;
	}
	
	public String getValidatorClassname(){
		return getValidator().getClass().getName();
	}
		
	/**
	 * Answer true if this validator, based on it's filters, should validate this resource.
	 * 
	 * @return true if the resource should be validated.
	 */
	protected boolean shouldValidate(IResource resource) {
		FilterGroup[] groups = getGroups();
		IProject project = resource.getProject();
		for (FilterGroup group : groups){
			if (!group.shouldValidate(project, resource))return false;
		}
		return true;
	}
	
	
	@Override
	public void setDelegatingId(String delegating) {
		super.setDelegatingId(delegating);
		_delegated = null;
	}
	
	public synchronized void setGroups(FilterGroup[] groups){
		_groups.clear();
		_groupsArray = null;
		for (FilterGroup group : groups)_groups.add(group);
	}

	public void setId(String id) {
		_id = id;
	}
	
	public void setName(String name) {
		_name = name;
	}
	
	@Override
	public ValidationResult validate(IResource resource, int kind, ValOperation operation, IProgressMonitor monitor) {
		ValidationResult vr = null;
		if (operation == null)operation = new ValOperation();
		if (monitor == null)monitor = new NullProgressMonitor();
		try {
			vr = getDelegatedValidator().validate(resource, kind, operation.getState(), monitor);
		}
		catch (Exception e){
			ValidationPlugin.getPlugin().handleException(e);
		}
		
		if (vr != null){
			if (vr.getValidationException() != null){
				ValidationPlugin.getPlugin().handleException(vr.getValidationException());
			}
			updateResults(vr);
			if (vr.getDependsOn() != null){
				ValidationFramework.getDefault().getDependencyIndex().set(getDependencyId(), resource, vr.getDependsOn());
			}
			IResource[] validated = vr.getValidated();
			if (validated != null){
				for (int i=0; i<validated.length; i++){
					operation.addValidated(getId(), validated[i]);
				}
			}
			
			ValidatorMessage[] msgs = vr.getMessages();
			for (int i=0; i<msgs.length; i++){
				ValidatorMessage m = msgs[i];
				try {
					IMarker marker = m.getResource().createMarker(m.getType());
					marker.setAttributes(m.getAttributes());
				}
				catch (CoreException e){
					if (!m.getResource().exists())throw new ResourceUnavailableError(m.getResource());
					ValidationPlugin.getPlugin().handleException(e);
				}
			}
		}
		return vr;		
	}
	
	private void updateResults(ValidationResult vr) {
		ReporterHelper rh = vr.getReporterHelper();
		if (rh == null)return;
		for (IMessage message : rh.getMessages()){
			Object target = message.getTargetObject();
			if (target != null){
				if (target instanceof IResource){
					IResource res = (IResource)target;
					ValidatorMessage vm = ValidatorMessage.create(message.getText(), res);
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
				}
			}
		}		
	}

	@Override
	public void validationStarting(IProject project, ValidationState state, IProgressMonitor monitor) {
		getDelegatedValidator().validationStarting(project, state, monitor);
	}
	
	@Override
	public void validationFinishing(IProject project, ValidationState state, IProgressMonitor monitor) {
		getDelegatedValidator().validationFinishing(project, state, monitor);
	}

	public Object getAdapter(Class adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	public synchronized void remove(FilterGroup group) {
		_groups.remove(group);
		_groupsArray = null;	
	}

	@Override
	protected boolean shouldValidateProject(IProject project) {
		FilterGroup[] groups = getGroups();
		for (FilterGroup group : groups){
			if (!group.shouldValidate(project, null))return false;
		}
		return true;
	}
}

public String getSourceId() {
	return _sourceId;
}

public void setSourceId(String sourceId) {
	_sourceId = sourceId;
}
}
