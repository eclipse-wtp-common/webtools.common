/**
 * <copyright>
 * </copyright>
 *
 * $Id: ModuleTypeImpl.java,v 1.1 2005/02/02 19:51:06 cbridgha Exp $
 */
package org.eclipse.wst.common.modulecore.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;

import org.eclipse.wst.common.modulecore.ModuleCorePackage;
import org.eclipse.wst.common.modulecore.ModuleType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Module Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.ModuleTypeImpl#getMetadataResources <em>Metadata Resources</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.ModuleTypeImpl#getModuleTypeId <em>Module Type Id</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ModuleTypeImpl extends EObjectImpl implements ModuleType {
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
	 * The default value of the '{@link #getModuleTypeId() <em>Module Type Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getModuleTypeId()
	 * @generated
	 * @ordered
	 */
	protected static final String MODULE_TYPE_ID_EDEFAULT = null;

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
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ModuleTypeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return ModuleCorePackage.eINSTANCE.getModuleType();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getMetadataResources() {
		if (metadataResources == null) {
			metadataResources = new EDataTypeUniqueEList(URI.class, this, ModuleCorePackage.MODULE_TYPE__METADATA_RESOURCES);
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
			eNotify(new ENotificationImpl(this, Notification.SET, ModuleCorePackage.MODULE_TYPE__MODULE_TYPE_ID, oldModuleTypeId, moduleTypeId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(EStructuralFeature eFeature, boolean resolve) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ModuleCorePackage.MODULE_TYPE__METADATA_RESOURCES:
				return getMetadataResources();
			case ModuleCorePackage.MODULE_TYPE__MODULE_TYPE_ID:
				return getModuleTypeId();
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
			case ModuleCorePackage.MODULE_TYPE__METADATA_RESOURCES:
				getMetadataResources().clear();
				getMetadataResources().addAll((Collection)newValue);
				return;
			case ModuleCorePackage.MODULE_TYPE__MODULE_TYPE_ID:
				setModuleTypeId((String)newValue);
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
			case ModuleCorePackage.MODULE_TYPE__METADATA_RESOURCES:
				getMetadataResources().clear();
				return;
			case ModuleCorePackage.MODULE_TYPE__MODULE_TYPE_ID:
				setModuleTypeId(MODULE_TYPE_ID_EDEFAULT);
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
			case ModuleCorePackage.MODULE_TYPE__METADATA_RESOURCES:
				return metadataResources != null && !metadataResources.isEmpty();
			case ModuleCorePackage.MODULE_TYPE__MODULE_TYPE_ID:
				return MODULE_TYPE_ID_EDEFAULT == null ? moduleTypeId != null : !MODULE_TYPE_ID_EDEFAULT.equals(moduleTypeId);
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
		result.append(" (metadataResources: ");
		result.append(metadataResources);
		result.append(", moduleTypeId: ");
		result.append(moduleTypeId);
		result.append(')');
		return result.toString();
	}

} //ModuleTypeImpl
