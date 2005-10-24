package org.eclipse.wst.validation.internal.plugin;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jem.util.RegistryReader;
import org.eclipse.wst.validation.internal.IProjectValidationHelper;

public class ValidationHelperRegistryReader extends RegistryReader {

	public static final String VALIDATION_HELPER = "validationHelper"; //$NON-NLS-1$
	static final String ATT_ID = "id"; //$NON-NLS-1$ 
	static final String ATT_HELPER_CLASS = "helperClass"; //$NON-NLS-1$
	private static ValidationHelperRegistryReader INSTANCE = null;
	private List validationHelpers = null;
	
	/**
	 * Default constructor
	 */
	public ValidationHelperRegistryReader() {
		super(ValidationPlugin.PLUGIN_ID, VALIDATION_HELPER);
	}
	
	public static ValidationHelperRegistryReader getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ValidationHelperRegistryReader();
			INSTANCE.readRegistry();
		}
		return INSTANCE;
	}
	
	private List getValidationHelpers() {
		if (validationHelpers == null)
			validationHelpers = new ArrayList();
		return validationHelpers;
	}

	public boolean readElement(IConfigurationElement element) {
		if (!element.getName().equals(VALIDATION_HELPER))
			return false;
		IProjectValidationHelper helper = null;
		try {
			helper = (IProjectValidationHelper) element.createExecutableExtension(ATT_HELPER_CLASS);
			getValidationHelpers().add(helper);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public IProjectValidationHelper getValidationHelper() {
		return (IProjectValidationHelper) getValidationHelpers().get(0);
	}

}
