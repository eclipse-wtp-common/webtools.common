/**
 * <copyright>
 * </copyright>
 *
 * $Id: ComponentTypeImpl.java,v 1.1 2005/04/04 07:04:59 cbridgha Exp $
 */
package org.eclipse.wst.common.componentcore.internal.impl;

import java.util.Collection;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.wst.common.componentcore.internal.ComponentType;
import org.eclipse.wst.common.componentcore.internal.ComponentcorePackage;
import org.eclipse.wst.common.componentcore.internal.Property;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Module Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.impl.ComponentTypeImpl#getComponentTypeId <em>Component Type Id</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.impl.ComponentTypeImpl#getVersion <em>Version</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.impl.ComponentTypeImpl#getProperties <em>Properties</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.impl.ComponentTypeImpl#getMetadataResources <em>Metadata Resources</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ComponentTypeImpl extends EObjectImpl implements ComponentType {
	/**
	 * The default value of the '{@link #getComponentTypeId() <em>Component Type Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getComponentTypeId()
	 * @generated
	 * @ordered
	 */
	protected static final String COMPONENT_TYPE_ID_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getComponentTypeId() <em>Component Type Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getComponentTypeId()
	 * @generated
	 * @ordered
	 */
	protected String componentTypeId = COMPONENT_TYPE_ID_EDEFAULT;

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
		return ComponentcorePackage.eINSTANCE.getComponentType();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getComponentTypeId() {
		return componentTypeId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setComponentTypeId(String newComponentTypeId) {
		String oldComponentTypeId = componentTypeId;
		componentTypeId = newComponentTypeId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ComponentcorePackage.COMPONENT_TYPE__COMPONENT_TYPE_ID, oldComponentTypeId, componentTypeId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getMetadataResources() {
		if (metadataResources == null) {
			metadataResources = new EDataTypeUniqueEList(IPath.class, this, ComponentcorePackage.COMPONENT_TYPE__METADATA_RESOURCES);
		}
		return metadataResources;
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
			eNotify(new ENotificationImpl(this, Notification.SET, ComponentcorePackage.COMPONENT_TYPE__VERSION, oldVersion, version));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getProperties() {
		if (properties == null) {
			properties = new EObjectResolvingEList(Property.class, this, ComponentcorePackage.COMPONENT_TYPE__PROPERTIES);
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
			case ComponentcorePackage.COMPONENT_TYPE__COMPONENT_TYPE_ID:
				return getComponentTypeId();
			case ComponentcorePackage.COMPONENT_TYPE__VERSION:
				return getVersion();
			case ComponentcorePackage.COMPONENT_TYPE__PROPERTIES:
				return getProperties();
			case ComponentcorePackage.COMPONENT_TYPE__METADATA_RESOURCES:
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
			case ComponentcorePackage.COMPONENT_TYPE__COMPONENT_TYPE_ID:
				setComponentTypeId((String)newValue);
				return;
			case ComponentcorePackage.COMPONENT_TYPE__VERSION:
				setVersion((String)newValue);
				return;
			case ComponentcorePackage.COMPONENT_TYPE__PROPERTIES:
				getProperties().clear();
				getProperties().addAll((Collection)newValue);
				return;
			case ComponentcorePackage.COMPONENT_TYPE__METADATA_RESOURCES:
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
			case ComponentcorePackage.COMPONENT_TYPE__COMPONENT_TYPE_ID:
				setComponentTypeId(COMPONENT_TYPE_ID_EDEFAULT);
				return;
			case ComponentcorePackage.COMPONENT_TYPE__VERSION:
				setVersion(VERSION_EDEFAULT);
				return;
			case ComponentcorePackage.COMPONENT_TYPE__PROPERTIES:
				getProperties().clear();
				return;
			case ComponentcorePackage.COMPONENT_TYPE__METADATA_RESOURCES:
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
			case ComponentcorePackage.COMPONENT_TYPE__COMPONENT_TYPE_ID:
				return COMPONENT_TYPE_ID_EDEFAULT == null ? componentTypeId != null : !COMPONENT_TYPE_ID_EDEFAULT.equals(componentTypeId);
			case ComponentcorePackage.COMPONENT_TYPE__VERSION:
				return VERSION_EDEFAULT == null ? version != null : !VERSION_EDEFAULT.equals(version);
			case ComponentcorePackage.COMPONENT_TYPE__PROPERTIES:
				return properties != null && !properties.isEmpty();
			case ComponentcorePackage.COMPONENT_TYPE__METADATA_RESOURCES:
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
		result.append(" (componentTypeId: ");
		result.append(componentTypeId);
		result.append(", version: ");
		result.append(version);
		result.append(", metadataResources: ");
		result.append(metadataResources);
		result.append(')');
		return result.toString();
	}

} //ComponentTypeImpl
