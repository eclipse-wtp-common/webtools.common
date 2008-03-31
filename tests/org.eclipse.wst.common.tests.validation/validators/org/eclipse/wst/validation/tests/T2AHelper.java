package org.eclipse.wst.validation.tests;

import org.eclipse.wst.validation.internal.Tracing;
import org.eclipse.wst.validation.internal.operations.WorkbenchContext;

public class T2AHelper extends WorkbenchContext {

	public String[] getURIs() {
		return null;
	}

	public Object loadModel(String symbolicName) {
		logit("T2AHelper#loadModule: " + symbolicName);
		return null;
	}

	public Object loadModel(String symbolicName, Object[] parms) {
		logit("T2AHelper#loadModule2: " + symbolicName);
		return null;
	}
	
	private void logit(String line){
		Tracing.log(line);
	}

}
