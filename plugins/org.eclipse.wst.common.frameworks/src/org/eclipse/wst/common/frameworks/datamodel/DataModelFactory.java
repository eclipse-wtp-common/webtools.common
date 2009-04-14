/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.datamodel;

import org.eclipse.wst.common.frameworks.internal.datamodel.DataModelExtensionReader;
import org.eclipse.wst.common.frameworks.internal.datamodel.DataModelImpl;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonPlugin;

/**
 * <p>
 * DataModelFactory is used to create IDataModel instances.
 * </p>
 * 
 * @since 1.0
 */
public class DataModelFactory {

	private static DataModelExtensionReader reader;

	private DataModelFactory() {
	}

	/**
	 * <p>
	 * Looks up the appropriate IDataModelProvider by the specified id and constructs a new
	 * IDataModel. If the IDataModelProvider is not found then a RuntimeException is logged and null
	 * is returned.
	 * </p>
	 * 
	 * @param dataModelProviderID
	 *            the id of the IDataModelProvider
	 * @return a new IDataModel
	 */
	public static IDataModel createDataModel(String dataModelProviderID) {
		IDataModelProvider provider = loadProvider(dataModelProviderID);
		if (provider == null)
			return null;
		return createDataModel(provider);
	}

	private static IDataModelProvider loadProvider(String id) {
		if (null == reader) {
			reader = new DataModelExtensionReader();
		}
		return reader.getProvider(id);
	}

	/**
	 * <p>
	 * Looks up the appropriate dataModelProviderIDs by the specified dataModelProviderKindID.
	 * </p>
	 * 
	 * @param dataModelProviderKindID
	 *            the String id of the dataModelProviderKindID
	 * 
	 * @return the array of valid dataModelProviderIDs or an empty array if there are none.
	 */
	public static String[] getDataModelProviderIDsForKind(String dataModelProviderKindID) {
		String[] validProviderIDs = loadProviderForProviderKind(dataModelProviderKindID);
		return null != validProviderIDs ? validProviderIDs : new String[0];
	}


	private static String[] loadProviderForProviderKind(String providerKind) {
		if (null == reader) {
			reader = new DataModelExtensionReader();
		}
		return reader.getProviderDescriptorsForProviderKind(providerKind);
	}

	/**
	 * <p>
	 * Builds an {@link IDataModel} using the specified Class. This Class will
	 * normally be a sub interface of {@link IDataModelProperties}. If the
	 * Class it is an {@link IDataModelProperties}.class then this method will
	 * attempt to use the {@link IDataModelProperties#_provider_class} field to
	 * load the {@link IDataModelProvider} with which to back the
	 * {@link IDataModel}. If this fails, or if the Class is not an interface,
	 * then this method is equavalent to
	 * <code>createDataModel(aClass.getName())</code>.
	 * </p>
	 * 
	 * @param aClass
	 *            an {@link IDataModelProperties}.class or the class whose name
	 *            is the id of the IDataModelProvider
	 * @return a new IDataModel
	 */
	public static IDataModel createDataModel(Class aClass) {
		if(aClass.isInterface()){
				try{
					Class clazz = (Class)aClass.getField("_provider_class").get(null);
					if(clazz != null){
						IDataModelProvider provider = (IDataModelProvider)clazz.newInstance();
						return createDataModel(provider);
					}
				} catch (NoSuchFieldException e) {
					//ignore; the interface may not have defined the field and is relying on extensions.
				} catch (IllegalArgumentException e) {
					WTPCommonPlugin.logError(e);
				} catch (SecurityException e) {
					WTPCommonPlugin.logError(e);
				} catch (IllegalAccessException e) {
					WTPCommonPlugin.logError(e);
				} catch (InstantiationException e) {
					WTPCommonPlugin.logError(e);
				}  
		}
		return createDataModel(aClass.getName());
	}

	/**
	 * <p>
	 * Creates a new IDataModel using the the specified instance of an IDataModelProvider.
	 * </p>
	 * 
	 * @param provider
	 * @return a new IDataModel
	 */
	public static IDataModel createDataModel(IDataModelProvider provider) {
		if (null == provider) {
			throw new NullPointerException();
		}
		DataModelImpl dataModel = new DataModelImpl(provider);
		return dataModel;
	}

}
