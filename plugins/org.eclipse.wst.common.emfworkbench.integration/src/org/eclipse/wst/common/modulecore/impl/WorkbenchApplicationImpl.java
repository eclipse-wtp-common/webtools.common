/**
 * <copyright>
 * </copyright>
 *
 * $Id: WorkbenchApplicationImpl.java,v 1.7 2005/02/02 21:40:42 cbridgha Exp $
 */
package org.eclipse.wst.common.modulecore.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.wst.common.modulecore.DeployScheme;
import org.eclipse.wst.common.modulecore.ModuleCorePackage;
import org.eclipse.wst.common.modulecore.ModuleType;
import org.eclipse.wst.common.modulecore.WorkbenchApplication;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Workbench Application</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.WorkbenchApplicationImpl#getDeployScheme <em>Deploy Scheme</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class WorkbenchApplicationImpl extends WorkbenchModuleImpl implements WorkbenchApplication {
	/**
	 * The cached value of the '{@link #getDeployScheme() <em>Deploy Scheme</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDeployScheme()
	 * @generated
	 * @ordered
	 */
	protected DeployScheme deployScheme = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected WorkbenchApplicationImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return ModuleCorePackage.eINSTANCE.getWorkbenchApplication();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DeployScheme getDeployScheme() {
		if (deployScheme != null && deployScheme.eIsProxy()) {
			DeployScheme oldDeployScheme = deployScheme;
			deployScheme = (DeployScheme)eResolveProxy((InternalEObject)deployScheme);
			if (deployScheme != oldDeployScheme) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ModuleCorePackage.WORKBENCH_APPLICATION__DEPLOY_SCHEME, oldDeployScheme, deployScheme));
			}
		}
		return deployScheme;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DeployScheme basicGetDeployScheme() {
		return deployScheme;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDeployScheme(DeployScheme newDeployScheme) {
		DeployScheme oldDeployScheme = deployScheme;
		deployScheme = newDeployScheme;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModuleCorePackage.WORKBENCH_APPLICATION__DEPLOY_SCHEME, oldDeployScheme, deployScheme));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
		if (featureID >= 0) {
			switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
				case ModuleCorePackage.WORKBENCH_APPLICATION__RESOURCES:
					return ((InternalEList)getResources()).basicAdd(otherEnd, msgs);
				default:
					return eDynamicInverseAdd(otherEnd, featureID, baseClass, msgs);
			}
		}
		if (eContainer != null)
			msgs = eBasicRemoveFromContainer(msgs);
		return eBasicSetContainer(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
		if (featureID >= 0) {
			switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
				case ModuleCorePackage.WORKBENCH_APPLICATION__RESOURCES:
					return ((InternalEList)getResources()).basicRemove(otherEnd, msgs);
				default:
					return eDynamicInverseRemove(otherEnd, featureID, baseClass, msgs);
			}
		}
		return eBasicSetContainer(null, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(EStructuralFeature eFeature, boolean resolve) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ModuleCorePackage.WORKBENCH_APPLICATION__HANDLE:
				return getHandle();
			case ModuleCorePackage.WORKBENCH_APPLICATION__DEPLOYED_NAME:
				return getDeployedName();
			case ModuleCorePackage.WORKBENCH_APPLICATION__RESOURCES:
				return getResources();
			case ModuleCorePackage.WORKBENCH_APPLICATION__MODULE_TYPE:
				if (resolve) return getModuleType();
				return basicGetModuleType();
			case ModuleCorePackage.WORKBENCH_APPLICATION__MODULES:
				return getModules();
			case ModuleCorePackage.WORKBENCH_APPLICATION__DEPLOY_SCHEME:
				if (resolve) return getDeployScheme();
				return basicGetDeployScheme();
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
			case ModuleCorePackage.WORKBENCH_APPLICATION__HANDLE:
				setHandle((URI)newValue);
				return;
			case ModuleCorePackage.WORKBENCH_APPLICATION__DEPLOYED_NAME:
				setDeployedName((String)newValue);
				return;
			case ModuleCorePackage.WORKBENCH_APPLICATION__RESOURCES:
				getResources().clear();
				getResources().addAll((Collection)newValue);
				return;
			case ModuleCorePackage.WORKBENCH_APPLICATION__MODULE_TYPE:
				setModuleType((ModuleType)newValue);
				return;
			case ModuleCorePackage.WORKBENCH_APPLICATION__MODULES:
				getModules().clear();
				getModules().addAll((Collection)newValue);
				return;
			case ModuleCorePackage.WORKBENCH_APPLICATION__DEPLOY_SCHEME:
				setDeployScheme((DeployScheme)newValue);
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
			case ModuleCorePackage.WORKBENCH_APPLICATION__HANDLE:
				setHandle(HANDLE_EDEFAULT);
				return;
			case ModuleCorePackage.WORKBENCH_APPLICATION__DEPLOYED_NAME:
				setDeployedName(DEPLOYED_NAME_EDEFAULT);
				return;
			case ModuleCorePackage.WORKBENCH_APPLICATION__RESOURCES:
				getResources().clear();
				return;
			case ModuleCorePackage.WORKBENCH_APPLICATION__MODULE_TYPE:
				setModuleType((ModuleType)null);
				return;
			case ModuleCorePackage.WORKBENCH_APPLICATION__MODULES:
				getModules().clear();
				return;
			case ModuleCorePackage.WORKBENCH_APPLICATION__DEPLOY_SCHEME:
				setDeployScheme((DeployScheme)null);
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
			case ModuleCorePackage.WORKBENCH_APPLICATION__HANDLE:
				return HANDLE_EDEFAULT == null ? handle != null : !HANDLE_EDEFAULT.equals(handle);
			case ModuleCorePackage.WORKBENCH_APPLICATION__DEPLOYED_NAME:
				return DEPLOYED_NAME_EDEFAULT == null ? deployedName != null : !DEPLOYED_NAME_EDEFAULT.equals(deployedName);
			case ModuleCorePackage.WORKBENCH_APPLICATION__RESOURCES:
				return resources != null && !resources.isEmpty();
			case ModuleCorePackage.WORKBENCH_APPLICATION__MODULE_TYPE:
				return moduleType != null;
			case ModuleCorePackage.WORKBENCH_APPLICATION__MODULES:
				return modules != null && !modules.isEmpty();
			case ModuleCorePackage.WORKBENCH_APPLICATION__DEPLOY_SCHEME:
				return deployScheme != null;
		}
		return eDynamicIsSet(eFeature);
	}

} //WorkbenchApplicationImpl
