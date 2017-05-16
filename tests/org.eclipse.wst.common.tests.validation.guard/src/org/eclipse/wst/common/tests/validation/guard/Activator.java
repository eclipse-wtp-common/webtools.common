package org.eclipse.wst.common.tests.validation.guard;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * The purpose of this plug-in is to ensure that it never gets loaded. If it does it means that we are
 * activating validators too early.
 */
public class Activator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.wst.common.tests.validation.guard";

	// The shared instance
	private static Activator plugin;
	
	public Activator() {
		RuntimeException re = new RuntimeException("This plug-in should never be loaded. If it is it means that we are loading " +
			"validators too early, and causing unneccessary plug-in activation.");
		handleException(re);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	/**
	 * Write this exception to the log.
	 * <p>
	 * We are in the transition of moving to a new approach for localized messages. This is the new 
	 * approach for exceptions.
	 * 
	 * @param e the throwable, this can be null in which case it is a nop.
	 */
	public void handleException(Throwable e){
		if (e == null)return;
		Status status = new Status(IStatus.ERROR, PLUGIN_ID, e.getLocalizedMessage(), e);
		getLog().log(status);
	}

}
