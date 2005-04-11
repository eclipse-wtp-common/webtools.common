package org.eclipse.wst.common.componentcore.internal;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.wst.common.componentcore.internal.impl.PlatformURLModuleConnection;
import org.eclipse.wst.common.componentcore.internal.impl.WTPModulesInit;
import org.eclipse.wst.common.componentcore.internal.util.ArtifactEditAdapterFactory;
import org.eclipse.wst.common.componentcore.internal.util.ModuleCoreEclipseAdapterFactory;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class ModulecorePlugin extends Plugin {
	//The shared instance.
	private static ModulecorePlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	//plugin id
	public static final String PLUGIN_ID = "org.eclipse.wst.common.modulecore"; //$NON-NLS-1$
	
	/**
	 * The constructor.
	 */
	public ModulecorePlugin() {
		super();
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		IAdapterManager manager = Platform.getAdapterManager();
		
		manager.registerAdapters(new ModuleCoreEclipseAdapterFactory(), ModuleStructuralModel.class);
		manager.registerAdapters(new ArtifactEditAdapterFactory(), ArtifactEditModel.class);
		
		PlatformURLModuleConnection.startup();
		WTPModulesInit.init();
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
		resourceBundle = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static ModulecorePlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = ModulecorePlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		try {
			if (resourceBundle == null)
				resourceBundle = ResourceBundle.getBundle("modulecore"); //$NON-NLS-1$
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		return resourceBundle;
	}
}
