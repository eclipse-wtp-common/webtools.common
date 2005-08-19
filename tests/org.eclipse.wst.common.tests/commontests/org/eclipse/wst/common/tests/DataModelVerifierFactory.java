/*
 * Created on Jan 5, 2004
 * 
 * To change the template for this generated file go to Window - Preferences - Java - Code
 * Generation - Code and Comments
 */
package org.eclipse.wst.common.tests;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jem.util.RegistryReader;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Administrator
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class DataModelVerifierFactory extends RegistryReader{
	static final String DATA_MODEL_VERIFIER_LIST_EXT = "dataModelVerifierList";
	static final String LIST_CLASS = "listClass";
	private Map dataModelVerifiersMap = null;
	private static DataModelVerifierFactory instance = null;
	private DataModelVerifier defaultDataModelVerifier = new DataModelVerifier();
	
	public DataModelVerifierFactory() {
		super(CommonTestsPlugin.PLUGIN_ID, "DataModelVerifier"); //$NON-NLS-1$
	}
	
	public static DataModelVerifierFactory getInstance() {
		if (instance == null){
			instance = new DataModelVerifierFactory();
			instance.readRegistry();
		}
		return instance;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jem.util.RegistryReader#readElement(org.eclipse.core.runtime.IConfigurationElement)
	 */
	public boolean readElement(IConfigurationElement element) {
		if (!element.getName().equals(DATA_MODEL_VERIFIER_LIST_EXT))
			return false;
		try {
			DataModelVerifierList list = (DataModelVerifierList)element.createExecutableExtension(LIST_CLASS);
			addToDataModelVerifiersMap(list.getDataModelVerifiers());
		}
		catch(CoreException e){
			e.printStackTrace();
		}
		return true;

	}
	
	protected void addToDataModelVerifiersMap(Map dataModelVerifiers){
		if (dataModelVerifiersMap == null)
			dataModelVerifiersMap = initDataModelVerifiersMap();
		dataModelVerifiersMap.putAll(dataModelVerifiers);
	}
	
	/**
	 * @return Returns the dataModelVerifiersMap.
	 */
	public Map getDataModelVerifiersMap() {
		if (dataModelVerifiersMap == null) {
			dataModelVerifiersMap = initDataModelVerifiersMap();
		}
		return dataModelVerifiersMap;
	}

	protected Map initDataModelVerifiersMap() {
		return new HashMap();
	}

	/**
	 * @return Returns the defaultDataModelVerifier.
	 */
	protected DataModelVerifier getDefaultDataModelVerifier() {
		return defaultDataModelVerifier;
	}
	
	  /*private void loadConfiguration() {
        //TestCollectorPlugin plugin = TestCollectorPlugin.instance;
	  	CommonTestsPlugin plugin = CommonTestsPlugin.instance;
        IExtension[] dataModelVerifierExts = plugin.dataModelVerifierExt.getExtensions();

        for (int i = 0; i < dataModelVerifierExts.length; i++) {
            IExtension extension = dataModelVerifierExts[i];
            IConfigurationElement[] factories = extension.getConfigurationElements();
            for (int j = 0; j < factories.length; j++) {
                try {
                    IConfigurationElement element = factories[j];
                    DataModelVerifierList list = (DataModelVerifierList)element.createExecutableExtension("listClass");
                    //ClassLoader classLoader = (ClassLoader) extension.getDeclaringPluginDescriptor().getPluginClassLoader();
                    //DataModelVerifierList list = (DataModelVerifierList) classLoader.loadClass(factoryClass).newInstance();
                    addToDataModelVerifiersMap(list.getDataModelVerifiers());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }*/
	
	public DataModelVerifier createVerifier(IDataModel model)  {
		DataModelVerifier verifier = getDefaultDataModelVerifier();
		String verifierClassName = null;
		if (model != null) {
			verifierClassName = (String) getDataModelVerifiersMap().get(model.getClass().getName());
			if (verifierClassName != null) {
				try {
					Class verifierClass = Class.forName(verifierClassName);
					verifier = (DataModelVerifier) verifierClass.newInstance();
				} catch (Exception e) { 
					verifier = getDefaultDataModelVerifier();
				}
			}
		}
		return verifier;
	}


}
