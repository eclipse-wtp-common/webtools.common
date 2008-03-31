package org.eclipse.wst.validation.tests;

import org.eclipse.wst.validation.internal.Tracing;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;

/**
 * A V1 validator that is off by default.
 * @author karasiuk
 *
 */
public class T2A implements IValidator {

	public void cleanup(IReporter reporter) {
	}

	public void validate(IValidationContext helper, IReporter reporter)
			throws ValidationException {
		String[] uris = helper.getURIs();
		if (uris != null)Tracing.log("T2A-01: number of urs = " + uris.length);
	}

}
