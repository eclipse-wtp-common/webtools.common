/*
 * Created on Jan 5, 2004
 * 
 * To change the template for this generated file go to Window - Preferences - Java - Code
 * Generation - Code and Comments
 */
package org.eclipse.wst.common.tests;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.common.frameworks.operations.WTPOperationDataModel;

import com.ibm.wtp.common.RegistryReader;

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
	 * @see com.ibm.wtp.common.RegistryReader#readElement(org.eclipse.core.runtime.IConfigurationElement)
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

	public DataModelVerifier createVerifier(WTPOperationDataModel model) {
		DataModelVerifier verifier = null;
		//String verifierClassName = null;
		if (model != null) {
			verifier = (DataModelVerifier) getDataModelVerifiersMap().get(model.getClass().getName());
			/*if (verifierClassName != null) {
				try {
					//Class verifierClass = Class.forName(verifierClassName);
					//verifier = (DataModelVerifier) verifierClass.newInstance();
				} catch (Exception e) {
				}
			}*/
			if (verifier == null) {
				Assert.fail("No Verifier for model:" + model.getClass().getName());
			}
		}
		return verifier;
	}
	
	protected void addToDataModelVerifiersMap(Map dataModelVerifiers){
		if (dataModelVerifiersMap == null)
			dataModelVerifiersMap = new HashMap();
		dataModelVerifiersMap.putAll(dataModelVerifiers);
	}
	
	/**
	 * @return Returns the dataModelVerifiersMap.
	 */
	protected Map getDataModelVerifiersMap() {
		if (dataModelVerifiersMap == null) {
			dataModelVerifiersMap = new HashMap();
		}
		return dataModelVerifiersMap;
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

}
