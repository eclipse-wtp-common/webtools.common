package org.eclipse.wst.common.frameworks.ui;

/*
 * Licensed Material - Property of IBM (C) Copyright IBM Corp. 2001, 2002 - All Rights Reserved. US
 * Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP
 * Schedule Contract with IBM Corp.
 */
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.common.frameworks.internal.ui.ValidationStatus;
import org.eclipse.wst.common.frameworks.operations.WTPOperationDataModel;
import org.eclipse.wst.common.frameworks.operations.WTPOperationDataModelEvent;
import org.eclipse.wst.common.frameworks.operations.WTPOperationDataModelListener;

/**
 * This class is EXPERIMENTAL and is subject to substantial changes.
 */
public abstract class WTPWizardPage extends WizardPage implements Listener, WTPOperationDataModelListener {
	protected static final int NEXT = 1;
	protected static final int PREVIOUS = 2;
	protected static final int COMPOSITE_BORDER = SWT.NULL;
	private ValidationStatus status = new ValidationStatus();
	private boolean isValidating = false;
	protected boolean isFirstTimeToPage = true;
	protected WTPOperationDataModel model;
	private Map validationMap;
	private String[] validationPropertyNames;
	protected WTPDataModelSynchHelper synchHelper;
	private String infopopID;

	/**
	 * J2EEWizardPage constructor comment.
	 * 
	 * @param pageName
	 *            java.lang.String
	 */
	protected WTPWizardPage(WTPOperationDataModel model, String pageName) {
		super(pageName);
		this.model = model;
		model.addListener(this);
		synchHelper = initializeSynchHelper(model);
	}

	/**
	 * @return
	 */
	public WTPDataModelSynchHelper initializeSynchHelper(WTPOperationDataModel dm) {
		return new WTPDataModelSynchHelper(dm);
	}

	/**
	 * J2EEWizardPage constructor comment.
	 * 
	 * @param pageName
	 *            java.lang.String
	 * @param title
	 *            java.lang.String
	 * @param titleImage
	 *            org.eclipse.jface.resource.ImageDescriptor
	 */
	protected WTPWizardPage(WTPOperationDataModel model, String pageName, String title, org.eclipse.jface.resource.ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		this.model = model;
		model.addListener(this);
		synchHelper = initializeSynchHelper(model);
	}

	protected IPath convertPackageNameToPath(String qualifiedName) {
		String name = qualifiedName.replace('.', '/');
		return new Path(name);
	}

	/**
	 * Creates the top level control for this dialog page under the given parent composite. This
	 * method has been abstract and it uses the template pattern to get the correct items setup in
	 * the correct order. See the set of methods below that are called in this method.
	 * <p>
	 * Implementors are responsible for ensuring that the created control can be accessed via
	 * <code>getControl</code>
	 * </p>
	 * 
	 * @param parent
	 *            the parent composite
	 */
	public final void createControl(org.eclipse.swt.widgets.Composite parent) {
		Composite top = createTopLevelComposite(parent);
		setControl(top);
		setupInfopop(top);
		setDefaults();
		addListeners();
		initializeValidationProperties();
	}

	private void initializeValidationProperties() {
		validationPropertyNames = getValidationPropertyNames();
		if (validationPropertyNames == null || validationPropertyNames.length == 0)
			validationMap = Collections.EMPTY_MAP;
		else {
			validationMap = new HashMap(validationPropertyNames.length);
			for (int i = 0; i < validationPropertyNames.length; i++)
				validationMap.put(validationPropertyNames[i], new Integer(i));
		}
	}

	/**
	 * Subclass should return the model property names that need to be validated on this page in the
	 * order that they should present their messages.
	 * 
	 * @return
	 */
	protected abstract String[] getValidationPropertyNames();

	/**
	 * Return true of the property validation status is OK.
	 * 
	 * @param propertyKey
	 * @return
	 */
	protected boolean getPropertyOKStatus(String propertyKey) {
		Integer validationKey = getValidationKey(propertyKey);
		if (validationKey != null)
			return getStatus(validationKey);
		return true;
	}

	/**
	 * @param propertyKey
	 * @return
	 */
	private Integer getValidationKey(String propertyKey) {
		return (Integer) validationMap.get(propertyKey);
	}

	/**
	 * Return the top level Composite for this page.
	 */
	protected abstract Composite createTopLevelComposite(Composite parent);

	/**
	 * Set up info pop hooks if set.
	 */
	protected void setupInfopop(Control parent) {
		if (getInfopopID() != null)
			WorkbenchHelp.setHelp(parent, getInfopopID());
	}

	/**
	 * Setup the default values for this page. Subclasses should override to provide appropriate
	 * defaults.
	 */
	protected void setDefaults() {
		restoreDefaultSettings();
	}

	/**
	 * Subclasses should implement this method if they have default settings that have been stored
	 * and need to be restored.
	 * 
	 * @see storeDefaultSettings()
	 */
	protected void restoreDefaultSettings() {
	}

	/**
	 * Add Listeners to controls at this point to avoid unnecessary events. Subclasses should
	 * override to add listeners to its controls.
	 */
	protected void addListeners() {
	}

	/**
	 * The page is being made current and visible. Subclasses may extend.
	 */
	protected void enter() {
		try {
			validatePage(showValidationErrorsOnEnter());
		} finally {
			isFirstTimeToPage = false;
		}
	}

	/**
	 * The default behavior is to return true unless it is the first time entering this page in
	 * which case we check to see if there is a previous page and return true if there is not.
	 * Subclasses should override if they do not want this default behavior.
	 */
	protected boolean showValidationErrorsOnEnter() {
		return !isFirstTimeToPage();
	}

	/**
	 * The default behavior is to return true unless it is the first time entering this page in
	 * which case we check to see if there is a previous page and return true if there is not.
	 * Subclasses should override if they do not want this default behavior.
	 * 
	 * @deprecated - use showValidatoinErrorsOnEnter instead
	 */
	protected boolean shouldValidateOnEnter() {
		return showValidationErrorsOnEnter();
	}

	/**
	 * Exiting the page. Subclasses may extend.
	 */
	protected void exit() {
	}

	protected boolean getStatus(Integer key) {
		return status.hasError(key);
	}

	/**
	 * Sent when an event that the receiver has registered for occurs. If a subclass overrides this
	 * method, it must call super.
	 * 
	 * @param event
	 *            the event which occurred
	 */
	public void handleEvent(org.eclipse.swt.widgets.Event event) {
		//validatePage();
	}

	/**
	 * Set the error message for this page based on the last error in the ValidationStatus.
	 */
	protected void setErrorMessage() {
		String error = status.getLastErrMsg();
		if (error == null) {
			if (getErrorMessage() != null)
				setErrorMessage((String) null);
			String warning = status.getLastWarningMsg();
			if (warning == null) {
				if (getMessage() != null && getMessageType() == IMessageProvider.WARNING)
					setMessage(null, IMessageProvider.WARNING);
			} else if (!warning.equals(getMessage()))
				setMessage(warning, IMessageProvider.WARNING);
		} else if (!error.equals(getErrorMessage()))
			setErrorMessage(error);
	}

	protected void setErrorStatus(Integer key, String errorMessage) {
		status.setErrorStatus(key, errorMessage);
	}

	protected void setWarningStatus(Integer key, String warningMessage) {
		status.setWarningStatus(key, warningMessage);
	}

	protected void setOKStatus(Integer key) {
		status.setOKStatus(key);
	}

	/**
	 * The <code>DialogPage</code> implementation of this <code>IDialogPage</code> method sets
	 * the control to the given visibility state. Subclasses may extend.
	 */

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible)
			enter();
		else
			exit();
	}

	/**
	 * This should be called by the Wizard just prior to running the performFinish operation.
	 * Subclasses should override to store their default settings.
	 */
	public void storeDefaultSettings() {
	}

	/**
	 * The page is now being validated. At this time, each control is validated and then the
	 * controls are updated based on the results in the ValidationStatus which was updated during
	 * <code>validateControls()</code>. Finally, it will display the last error message and it
	 * will set the page complete. Subclasses will not typically override this method.
	 */
	protected void validatePage() {
		validatePage(true);
	}

	protected void validatePage(boolean showMessage) {
		if (!isValidating) {
			isValidating = true;
			try {
				validateControlsBase();
				updateControls();
				if (showMessage)
					setErrorMessage();
				setPageComplete(status.getLastErrMsg() == null);
			} finally {
				isValidating = false;
			}
		}
	}

	/**
	 * Validate individual controls. Use validation keys to keep track of errors.
	 * 
	 * @see setOKStatus(Integer) and setErrorMessage(Integer, String)
	 */
	protected final String validateControlsBase() {
		if (!validationMap.isEmpty()) {
			String propName;
			for (int i = 0; i < validationPropertyNames.length; i++) {
				propName = validationPropertyNames[i];
				Integer valKey = (Integer) validationMap.get(propName);
				if (valKey != null)
					validateProperty(propName, valKey);
				if (!getStatus(valKey))
					return propName;
			}
		}
		return null;
	}

	/**
	 * @param propertyName
	 * @param validationkey
	 */
	private void validateProperty(String propertyName, Integer validationKey) {
		setOKStatus(validationKey);
		IStatus status1 = model.validateProperty(propertyName);
		if (!status1.isOK()) {
			String message = status1.isMultiStatus() ? status1.getChildren()[0].getMessage() : status1.getMessage();
			switch (status1.getSeverity()) {
				case IStatus.ERROR :
					setErrorStatus(validationKey, message);
					break;
				case IStatus.WARNING :
					setWarningStatus(validationKey, message);
					break;
			}
		}
	}

	/**
	 * Update the enablement of controls after validation. Sublcasses should check the status of
	 * validation keys to determine enablement.
	 */
	protected void updateControls() {
	}

	/**
	 * Gets the isFirstTimeToPage.
	 * 
	 * @return Returns a boolean
	 */
	protected boolean isFirstTimeToPage() {
		return isFirstTimeToPage;
	}

	protected void setJavaStatusMessage(IStatus javaStatus, Integer statusKey, String message) {
		if (javaStatus.getSeverity() == IStatus.WARNING)
			setWarningStatus(statusKey, message);
		else
			setErrorStatus(statusKey, message);
	}

	/**
	 * @param b
	 */
	public void setFirstTimeToPage(boolean b) {
		isFirstTimeToPage = b;
	}

	/*
	 * If a property changes that we want to validate, force validation on this page.
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.operation.WTPOperationDataModelListener#propertyChanged(java.lang.String,
	 *      java.lang.Object, java.lang.Object)
	 */
	public void propertyChanged(WTPOperationDataModelEvent event) {
		String propertyName = event.getPropertyName();
		if (validationPropertyNames != null && (event.getFlag() == WTPOperationDataModelEvent.PROPERTY_CHG || (!isPageComplete() && event.getFlag() == WTPOperationDataModelEvent.VALID_VALUES_CHG))) {
			for (int i = 0; i < validationPropertyNames.length; i++) {
				if (validationPropertyNames[i].equals(propertyName)) {
					validatePage();
					break;
				}
			}
		}
	}

	protected void addSpacers(Composite comp, int spacers) {
		for (int i = 0; i < spacers; i++)
			new Label(comp, SWT.NONE);
	}

	/**
	 * @return Returns the model.
	 */
	protected WTPOperationDataModel getModel() {
		return model;
	}

	/**
	 * @return Returns the synchHelper.
	 */
	protected WTPDataModelSynchHelper getSynchHelper() {
		return synchHelper;
	}

	public void dispose() {
		super.dispose();
		if (synchHelper != null) {
			synchHelper.dispose();
			synchHelper = null;
		}
	}

	protected String getInfopopID() {
		return infopopID;
	}

	public void setInfopopID(String infopopID) {
		this.infopopID = infopopID;
	}
}