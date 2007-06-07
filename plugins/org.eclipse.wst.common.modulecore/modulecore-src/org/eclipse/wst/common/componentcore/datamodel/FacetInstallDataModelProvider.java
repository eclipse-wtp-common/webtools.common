/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.datamodel;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.project.facet.core.IActionConfigFactory;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

public class FacetInstallDataModelProvider extends FacetDataModelProvider implements IActionConfigFactory {

	public static final String MASTER_PROJECT_DM = "FacetInstallDataModelProvider.MASTER_PROJECT_DM"; //$NON-NLS-1$
	
	public Set getPropertyNames() {
		Set names = super.getPropertyNames();
		names.add(MASTER_PROJECT_DM);
		return names;
	}
	
	public FacetInstallDataModelProvider() {
		super();
	}

	public Object getDefaultProperty(String propertyName) {
		if (FACET_TYPE.equals(propertyName)) {
			return FACET_TYPE_INSTALL;
		} else if (FACET_VERSION_STR.equals(propertyName)) {
			IProjectFacetVersion version = (IProjectFacetVersion) getProperty(FACET_VERSION);
			return version.getVersionString();
		} else if (FACET_VERSION.equals(propertyName)) {
			IProjectFacetVersion version = ProjectFacetsManager.getProjectFacet(getStringProperty(FACET_ID)).getDefaultVersion();
			return version;
		}
		return super.getDefaultProperty(propertyName);
	}

	public boolean propertySet(String propertyName, Object propertyValue) {
		if (FACET_TYPE.equals(propertyName)) {
			throw new RuntimeException();
		} else if (FACET_VERSION_STR.equals(propertyName)) {
			DataModelPropertyDescriptor[] descriptors = getValidPropertyDescriptors(FACET_VERSION);
			for (int i = 0; i < descriptors.length; i++) {
				if (descriptors[i].getPropertyDescription().equals(propertyValue)) {
					setProperty(FACET_VERSION, descriptors[i].getPropertyValue());
					break;
				}
			}
		} else if (null != propertyValue && FACET_VERSION.equals(propertyName)) {
			IProjectFacetVersion version = (IProjectFacetVersion) propertyValue;
			setProperty(FACET_VERSION_STR, version.getVersionString());
		}
		return super.propertySet(propertyName, propertyValue);
	}

	protected DataModelPropertyDescriptor[] cachedVersionDescriptors;
	protected DataModelPropertyDescriptor[] cachedVersionStringDescriptors;

	public DataModelPropertyDescriptor[] getValidPropertyDescriptors(String propertyName) {
		if (FACET_VERSION.equals(propertyName)) {
			if (null == cachedVersionDescriptors) {
				Set versions = ProjectFacetsManager.getProjectFacet(getStringProperty(FACET_ID)).getVersions();
				List list = Collections.list(Collections.enumeration(versions));
				Collections.sort(list, new Comparator(){
					public int compare(Object o1, Object o2) {
						return ((IProjectFacetVersion)o1).getVersionString().compareTo(((IProjectFacetVersion)o2).getVersionString());
					}
				});
				
				cachedVersionDescriptors = new DataModelPropertyDescriptor[list.size()];
				Iterator iterator = list.iterator();
				for (int i = 0; i < cachedVersionDescriptors.length; i++) {
					IProjectFacetVersion version = (IProjectFacetVersion) iterator.next();
					cachedVersionDescriptors[i] = new DataModelPropertyDescriptor(version, version.getVersionString());
				}
			}
			return cachedVersionDescriptors;
		}
		if (FACET_VERSION_STR.equals(propertyName)) {
			if (null == cachedVersionStringDescriptors) {
				DataModelPropertyDescriptor[] versionDescriptors = getValidPropertyDescriptors(FACET_VERSION);
				cachedVersionStringDescriptors = new DataModelPropertyDescriptor[versionDescriptors.length];
				for (int i = 0; i < cachedVersionStringDescriptors.length; i++) {
					cachedVersionStringDescriptors[i] = new DataModelPropertyDescriptor(versionDescriptors[i].getPropertyDescription());
				}
			}
			return cachedVersionStringDescriptors;
		}
		return super.getValidPropertyDescriptors(propertyName);
	}

	public Object create() {
		return DataModelFactory.createDataModel(this);
	}

}
