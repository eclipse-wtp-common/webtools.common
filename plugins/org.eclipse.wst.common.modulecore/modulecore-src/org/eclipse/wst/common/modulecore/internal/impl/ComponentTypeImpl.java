/**
 * <copyright>
 * </copyright>
 *
 * $Id: ComponentTypeImpl.java,v 1.3 2005/03/24 23:03:49 cbridgha Exp $
 */
package org.eclipse.wst.common.modulecore.internal.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.wst.common.modulecore.ComponentType;
import org.eclipse.wst.common.modulecore.ModuleCorePackage;
import org.eclipse.wst.common.modulecore.Property;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Module Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.ComponentTypeImpl#getModuleTypeId <em>Module Type Id</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.ComponentTypeImpl#getVersion <em>Version</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.ComponentTypeImpl#getProperties <em>Properties</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.ComponentTypeImpl#getMetadataResources <em>Metadata Resources</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ComponentTypeImpl extends EObjectImpl implements ComponentType {
	/**
	 * The default value of the '{@link #getModuleTypeId() <em>Module Type Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getModuleTypeId()
	 * @generated
	 * @ordered
	 */
	protected static final String MODULE_TYPE_ID_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getModuleTypeId() <em>Module Type Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getModuleTypeId()
	 * @generated
	 * @ordered
	 */
	protected String moduleTypeId = MODULE_TYPE_ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getVersion() <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVersion()
	 * @generated
	 * @ordered
	 */
	protected static final String VERSION_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getVersion() <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVersion()
	 * @generated
	 * @ordered
	 */
	protected String version = VERSION_EDEFAULT;

	/**
	 * The cached value of the '{@link #getProperties() <em>Properties</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProperties()
	 * @generated
	 * @ordered
	 */
	protected EList properties = null;

	/**
	 * The cached value of the '{@link #getMetadataResources() <em>Metadata Resources</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMetadataResources()
	 * @generated
	 * @ordered
	 */
	protected EList metadataResources = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ComponentTypeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return ModuleCorePackage.eINSTANCE.getComponentType();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getMetadataResources() {
		if (metadataResources == null) {
			metadataResources = new EDataTypeUniqueEList(URI.class, this, ModuleCorePackage.COMPONENT_TYPE__METADATA_RESOURCES);
		}
		return metadataResources;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getModuleTypeId() {
		return moduleTypeId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setModuleTypeId(String newModuleTypeId) {
		String oldModuleTypeId = moduleTypeId;
		moduleTypeId = newModuleTypeId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModuleCorePackage.COMPONENT_TYPE__MODULE_TYPE_ID, oldModuleTypeId, moduleTypeId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setVersion(String newVersion) {
		String oldVersion = version;
		version = newVersion;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModuleCorePackage.COMPONENT_TYPE__VERSION, oldVersion, version));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getProperties() {
		if (properties == null) {
			properties = new EObjectResolvingEList(Property.class, this, ModuleCorePackage.COMPONENT_TYPE__PROPERTIES);
		}
		return properties;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(EStructuralFeature eFeature, boolean resolve) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ModuleCorePackage.COMPONENT_TYPE__MODULE_TYPE_ID:
				return getModuleTypeId();
			case ModuleCorePackage.COMPONENT_TYPE__VERSION:
				return getVersion();
			case ModuleCorePackage.COMPONENT_TYPE__PROPERTIES:
				return getProperties();
			case ModuleCorePackage.COMPONENT_TYPE__METADATA_RESOURCES:
				return getMetadataResources();
		}
		return eDynamicGet(eFeature, resolve);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void eSet(EStructuralFeature eFeature, Object newValue) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ModuleCorePackage.COMPONENT_TYPE__MODULE_TYPE_ID:
				setModuleTypeId((String)newValue);
				return;
			case ModuleCorePackage.COMPONENT_TYPE__VERSION:
				setVersion((String)newValue);
				return;
			case ModuleCorePackage.COMPONENT_TYPE__PROPERTIES:
				getProperties().clear();
				getProperties().addAll((Collection)newValue);
				return;
			case ModuleCorePackage.COMPONENT_TYPE__METADATA_RESOURCES:
				getMetadataResources().clear();
				getMetadataResources().addAll((Collection)newValue);
				return;
		}
		eDynamicSet(eFeature, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void eUnset(EStructuralFeature eFeature) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ModuleCorePackage.COMPONENT_TYPE__MODULE_TYPE_ID:
				setModuleTypeId(MODULE_TYPE_ID_EDEFAULT);
				return;
			case ModuleCorePackage.COMPONENT_TYPE__VERSION:
				setVersion(VERSION_EDEFAULT);
				return;
			case ModuleCorePackage.COMPONENT_TYPE__PROPERTIES:
				getProperties().clear();
				return;
			case ModuleCorePackage.COMPONENT_TYPE__METADATA_RESOURCES:
				getMetadataResources().clear();
				return;
		}
		eDynamicUnset(eFeature);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean eIsSet(EStructuralFeature eFeature) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ModuleCorePackage.COMPONENT_TYPE__MODULE_TYPE_ID:
				return MODULE_TYPE_ID_EDEFAULT == null ? moduleTypeId != null : !MODULE_TYPE_ID_EDEFAULT.equals(moduleTypeId);
			case ModuleCorePackage.COMPONENT_TYPE__VERSION:
				return VERSION_EDEFAULT == null ? version != null : !VERSION_EDEFAULT.equals(version);
			case ModuleCorePackage.COMPONENT_TYPE__PROPERTIES:
				return properties != null && !properties.isEmpty();
			case ModuleCorePackage.COMPONENT_TYPE__METADATA_RESOURCES:
				return metadataResources != null && !metadataResources.isEmpty();
		}
		return eDynamicIsSet(eFeature);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (moduleTypeId: ");
		result.append(moduleTypeId);
		result.append(", version: ");
		result.append(version);
		result.append(", metadataResources: ");
		result.append(metadataResources);
		result.append(')');
		return result.toString();
	}

} //ComponentTypeImpl
