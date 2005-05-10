package org.eclipse.wst.common.frameworks.internal;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonPlugin;



public class FlexibleJavaProjectPreferenceUtil{
	
	public static boolean getMultipleModulesPerProjectProp(){
	
		Preferences prefs = WTPCommonPlugin.getDefault().getPluginPreferences();
		return prefs.getBoolean(FlexibleJavaProjectPreferenceConstants.ALLOW_MULTIPLE_MODULES);
	}
	
	public static void setMultipleModulesPerProjectProp(boolean val){
		Preferences prefs = WTPCommonPlugin.getDefault().getPluginPreferences();
		prefs.setValue(FlexibleJavaProjectPreferenceConstants.ALLOW_MULTIPLE_MODULES, val);
		WTPCommonPlugin.getDefault().savePluginPreferences();
	}
}