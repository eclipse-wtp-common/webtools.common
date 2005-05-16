/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.internet.cache.internal;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.internet.cache.internal.preferences.PreferenceConstants;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class CachePlugin extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "org.eclipse.wst.internet.cache";
	//The shared instance.
	private static CachePlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
  
  private CacheJob job = null;
	
	/**
	 * The constructor.
	 */
	public CachePlugin() {
		super();
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
    ToCacheRegistryReader.getInstance().readRegistry();
    Cache.open(Platform.getPluginStateLocation(this));
    if(getPluginPreferences().contains(PreferenceConstants.CACHE_ENABLED))
    {
      setCacheEnabled(getPluginPreferences().getBoolean(PreferenceConstants.CACHE_ENABLED));
    }
    else
    {
      setCacheEnabled(true);
    }
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		Cache.getInstance().close();
    stopJob();
		super.stop(context);
		plugin = null;
		resourceBundle = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static CachePlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = ResourceBundle.getBundle("org.eclipse.wst.internet.cache.internal.CachePluginResources");
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}
  
  /**
   * Returns the string from the plugin's resource bundle using the specified object,
   * or 'key' if not found.
   */
  public static String getResourceString(String key, Object s1)
  {
    return MessageFormat.format(getResourceString(key), new Object[] { s1 });
  }

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		try {
			if (resourceBundle == null)
				resourceBundle = ResourceBundle.getBundle("plugin");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		return resourceBundle;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.wst.internet.cache", path);
	}
  
  public void setCacheEnabled(boolean enabled)
  {
    getPluginPreferences().setValue(PreferenceConstants.CACHE_ENABLED, enabled);
    if(enabled)
    {
      startJob();
    }
    else
    {
      stopJob();
    }
  }
  
  public boolean isCacheEnabled()
  {
    if(getPluginPreferences().contains(PreferenceConstants.CACHE_ENABLED))
      return getPluginPreferences().getBoolean(PreferenceConstants.CACHE_ENABLED);
    return true;
  }
  
  private void startJob()
  {
    if(job == null)
    {
      job = new CacheJob();
      job.setPriority(CacheJob.DECORATE);
      job.schedule(); // start as soon as possible
    }
  }
  
  private void stopJob()
  {
    if(job != null)
    {
      job.cancel();
    }
    job = null;
  }
}
