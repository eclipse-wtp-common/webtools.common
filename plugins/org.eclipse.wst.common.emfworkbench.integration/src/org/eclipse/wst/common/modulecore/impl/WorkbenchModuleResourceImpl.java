/**
 * <copyright>
 * </copyright>
 *
 * $Id: WorkbenchModuleResourceImpl.java,v 1.1 2005/01/21 17:13:39 cbridgha Exp $
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
import org.eclipse.wst.common.modulecore.WorkbenchModuleResource;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Workbench Module Resource</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.WorkbenchModuleResourceImpl#getSourcePath <em>Source Path</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.WorkbenchModuleResourceImpl#getDeployedPath <em>Deployed Path</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.WorkbenchModuleResourceImpl#getExclusions <em>Exclusions</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class WorkbenchModuleResourceImpl extends EObjectImpl implements WorkbenchModuleResource {
	/**
	 * The default value of the '{@link #getSourcePath() <em>Source Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSourcePath()
	 * @generated
	 * @ordered
	 */
	protected static final URI SOURCE_PATH_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSourcePath() <em>Source Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSourcePath()
	 * @generated
	 * @ordered
	 */
	protected URI sourcePath = SOURCE_PATH_EDEFAULT;

	/**
	 * The default value of the '{@link #getDeployedPath() <em>Deployed Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDeployedPath()
	 * @generated
	 * @ordered
	 */
	protected static final URI DEPLOYED_PATH_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDeployedPath() <em>Deployed Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDeployedPath()
	 * @generated
	 * @ordered
	 */
	protected URI deployedPath = DEPLOYED_PATH_EDEFAULT;

	/**
	 * The cached value of the '{@link #getExclusions() <em>Exclusions</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExclusions()
	 * @generated
	 * @ordered
	 */
	protected EList exclusions = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected WorkbenchModuleResourceImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return ModuleCorePackage.eINSTANCE.getWorkbenchModuleResource();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public URI getSourcePath() {
		return sourcePath;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSourcePath(URI newSourcePath) {
		URI oldSourcePath = sourcePath;
		sourcePath = newSourcePath;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModuleCorePackage.WORKBENCH_MODULE_RESOURCE__SOURCE_PATH, oldSourcePath, sourcePath));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public URI getDeployedPath() {
		return deployedPath;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDeployedPath(URI newDeployedPath) {
		URI oldDeployedPath = deployedPath;
		deployedPath = newDeployedPath;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModuleCorePackage.WORKBENCH_MODULE_RESOURCE__DEPLOYED_PATH, oldDeployedPath, deployedPath));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getExclusions() {
		if (exclusions == null) {
			exclusions = new EDataTypeUniqueEList(URI.class, this, ModuleCorePackage.WORKBENCH_MODULE_RESOURCE__EXCLUSIONS);
		}
		return exclusions;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(EStructuralFeature eFeature, boolean resolve) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ModuleCorePackage.WORKBENCH_MODULE_RESOURCE__SOURCE_PATH:
				return getSourcePath();
			case ModuleCorePackage.WORKBENCH_MODULE_RESOURCE__DEPLOYED_PATH:
				return getDeployedPath();
			case ModuleCorePackage.WORKBENCH_MODULE_RESOURCE__EXCLUSIONS:
				return getExclusions();
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
			case ModuleCorePackage.WORKBENCH_MODULE_RESOURCE__SOURCE_PATH:
				setSourcePath((URI)newValue);
				return;
			case ModuleCorePackage.WORKBENCH_MODULE_RESOURCE__DEPLOYED_PATH:
				setDeployedPath((URI)newValue);
				return;
			case ModuleCorePackage.WORKBENCH_MODULE_RESOURCE__EXCLUSIONS:
				getExclusions().clear();
				getExclusions().addAll((Collection)newValue);
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
			case ModuleCorePackage.WORKBENCH_MODULE_RESOURCE__SOURCE_PATH:
				setSourcePath(SOURCE_PATH_EDEFAULT);
				return;
			case ModuleCorePackage.WORKBENCH_MODULE_RESOURCE__DEPLOYED_PATH:
				setDeployedPath(DEPLOYED_PATH_EDEFAULT);
				return;
			case ModuleCorePackage.WORKBENCH_MODULE_RESOURCE__EXCLUSIONS:
				getExclusions().clear();
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
			case ModuleCorePackage.WORKBENCH_MODULE_RESOURCE__SOURCE_PATH:
				return SOURCE_PATH_EDEFAULT == null ? sourcePath != null : !SOURCE_PATH_EDEFAULT.equals(sourcePath);
			case ModuleCorePackage.WORKBENCH_MODULE_RESOURCE__DEPLOYED_PATH:
				return DEPLOYED_PATH_EDEFAULT == null ? deployedPath != null : !DEPLOYED_PATH_EDEFAULT.equals(deployedPath);
			case ModuleCorePackage.WORKBENCH_MODULE_RESOURCE__EXCLUSIONS:
				return exclusions != null && !exclusions.isEmpty();
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
		result.append(" (sourcePath: ");
		result.append(sourcePath);
		result.append(", deployedPath: ");
		result.append(deployedPath);
		result.append(", exclusions: ");
		result.append(exclusions);
		result.append(')');
		return result.toString();
	}

} //WorkbenchModuleResourceImpl
