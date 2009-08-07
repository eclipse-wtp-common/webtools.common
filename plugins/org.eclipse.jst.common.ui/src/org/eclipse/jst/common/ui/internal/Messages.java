package org.eclipse.jst.common.ui.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.jst.common.ui.messages"; //$NON-NLS-1$
	public static String JarTitle;
	public static String JarDescription;
	public static String ExternalJarTitle;
	public static String ExternalJarDescription;
	public static String Browse;
	public static String VariableReferenceTitle;
	public static String VariableReferenceDescription;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
