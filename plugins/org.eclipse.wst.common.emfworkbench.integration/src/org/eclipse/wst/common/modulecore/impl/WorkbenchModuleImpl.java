/**
 * <copyright>
 * </copyright>
 *
 * $Id: WorkbenchModuleImpl.java,v 1.7 2005/02/02 19:51:06 cbridgha Exp $
 */
package org.eclipse.wst.common.modulecore.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.wst.common.modulecore.IModuleType;
import org.eclipse.wst.common.modulecore.ModuleCorePackage;
import org.eclipse.wst.common.modulecore.ModuleType;
import org.eclipse.wst.common.modulecore.WorkbenchModule;
import org.eclipse.wst.common.modulecore.WorkbenchModuleResource;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Workbench Module</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.WorkbenchModuleImpl#getHandle <em>Handle</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.WorkbenchModuleImpl#getDeployedName <em>Deployed Name</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.WorkbenchModuleImpl#getDeployedPath <em>Deployed Path</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.WorkbenchModuleImpl#getModules <em>Modules</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.WorkbenchModuleImpl#getResources <em>Resources</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.WorkbenchModuleImpl#getModuleType <em>Module Type</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class WorkbenchModuleImpl extends EObjectImpl implements WorkbenchModule {
	/**
	 * The default value of the '{@link #getHandle() <em>Handle</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHandle()
	 * @generated
	 * @ordered
	 */
	protected static final URI HANDLE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getHandle() <em>Handle</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHandle()
	 * @generated
	 * @ordered
	 */
	protected URI handle = HANDLE_EDEFAULT;

	/**
	 * The default value of the '{@link #getDeployedName() <em>Deployed Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDeployedName()
	 * @generated
	 * @ordered
	 */
	protected static final String DEPLOYED_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDeployedName() <em>Deployed Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDeployedName()
	 * @generated
	 * @ordered
	 */
	protected String deployedName = DEPLOYED_NAME_EDEFAULT;

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
	 * The cached value of the '{@link #getModules() <em>Modules</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getModules()
	 * @generated
	 * @ordered
	 */
	protected EList modules = null;

	/**
	 * The cached value of the '{@link #getResources() <em>Resources</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResources()
	 * @generated
	 * @ordered
	 */
	protected EList resources = null;

	/**
	 * The cached value of the '{@link #getModuleType() <em>Module Type</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getModuleType()
	 * @generated
	 * @ordered
	 */
	protected EList moduleType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected WorkbenchModuleImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return ModuleCorePackage.eINSTANCE.getWorkbenchModule();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public URI getHandle() {
		return handle;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setHandle(URI newHandle) {
		URI oldHandle = handle;
		handle = newHandle;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModuleCorePackage.WORKBENCH_MODULE__HANDLE, oldHandle, handle));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getDeployedName() {
		return deployedName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDeployedName(String newDeployedName) {
		String oldDeployedName = deployedName;
		deployedName = newDeployedName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModuleCorePackage.WORKBENCH_MODULE__DEPLOYED_NAME, oldDeployedName, deployedName));
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
			eNotify(new ENotificationImpl(this, Notification.SET, ModuleCorePackage.WORKBENCH_MODULE__DEPLOYED_PATH, oldDeployedPath, deployedPath));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getModules() {
		if (modules == null) {
			modules = new EDataTypeUniqueEList(URI.class, this, ModuleCorePackage.WORKBENCH_MODULE__MODULES);
		}
		return modules;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getResources() {
		if (resources == null) {
			resources = new EObjectContainmentWithInverseEList(WorkbenchModuleResource.class, this, ModuleCorePackage.WORKBENCH_MODULE__RESOURCES, ModuleCorePackage.WORKBENCH_MODULE_RESOURCE__MODULE);
		}
		return resources;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getModuleType() {
		if (moduleType == null) {
			moduleType = new EObjectResolvingEList(ModuleType.class, this, ModuleCorePackage.WORKBENCH_MODULE__MODULE_TYPE);
		}
		return moduleType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
		if (featureID >= 0) {
			switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
				case ModuleCorePackage.WORKBENCH_MODULE__RESOURCES:
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
				case ModuleCorePackage.WORKBENCH_MODULE__RESOURCES:
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
			case ModuleCorePackage.WORKBENCH_MODULE__HANDLE:
				return getHandle();
			case ModuleCorePackage.WORKBENCH_MODULE__DEPLOYED_NAME:
				return getDeployedName();
			case ModuleCorePackage.WORKBENCH_MODULE__DEPLOYED_PATH:
				return getDeployedPath();
			case ModuleCorePackage.WORKBENCH_MODULE__MODULES:
				return getModules();
			case ModuleCorePackage.WORKBENCH_MODULE__RESOURCES:
				return getResources();
			case ModuleCorePackage.WORKBENCH_MODULE__MODULE_TYPE:
				return getModuleType();
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
			case ModuleCorePackage.WORKBENCH_MODULE__HANDLE:
				setHandle((URI)newValue);
				return;
			case ModuleCorePackage.WORKBENCH_MODULE__DEPLOYED_NAME:
				setDeployedName((String)newValue);
				return;
			case ModuleCorePackage.WORKBENCH_MODULE__DEPLOYED_PATH:
				setDeployedPath((URI)newValue);
				return;
			case ModuleCorePackage.WORKBENCH_MODULE__MODULES:
				getModules().clear();
				getModules().addAll((Collection)newValue);
				return;
			case ModuleCorePackage.WORKBENCH_MODULE__RESOURCES:
				getResources().clear();
				getResources().addAll((Collection)newValue);
				return;
			case ModuleCorePackage.WORKBENCH_MODULE__MODULE_TYPE:
				getModuleType().clear();
				getModuleType().addAll((Collection)newValue);
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
			case ModuleCorePackage.WORKBENCH_MODULE__HANDLE:
				setHandle(HANDLE_EDEFAULT);
				return;
			case ModuleCorePackage.WORKBENCH_MODULE__DEPLOYED_NAME:
				setDeployedName(DEPLOYED_NAME_EDEFAULT);
				return;
			case ModuleCorePackage.WORKBENCH_MODULE__DEPLOYED_PATH:
				setDeployedPath(DEPLOYED_PATH_EDEFAULT);
				return;
			case ModuleCorePackage.WORKBENCH_MODULE__MODULES:
				getModules().clear();
				return;
			case ModuleCorePackage.WORKBENCH_MODULE__RESOURCES:
				getResources().clear();
				return;
			case ModuleCorePackage.WORKBENCH_MODULE__MODULE_TYPE:
				getModuleType().clear();
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
			case ModuleCorePackage.WORKBENCH_MODULE__HANDLE:
				return HANDLE_EDEFAULT == null ? handle != null : !HANDLE_EDEFAULT.equals(handle);
			case ModuleCorePackage.WORKBENCH_MODULE__DEPLOYED_NAME:
				return DEPLOYED_NAME_EDEFAULT == null ? deployedName != null : !DEPLOYED_NAME_EDEFAULT.equals(deployedName);
			case ModuleCorePackage.WORKBENCH_MODULE__DEPLOYED_PATH:
				return DEPLOYED_PATH_EDEFAULT == null ? deployedPath != null : !DEPLOYED_PATH_EDEFAULT.equals(deployedPath);
			case ModuleCorePackage.WORKBENCH_MODULE__MODULES:
				return modules != null && !modules.isEmpty();
			case ModuleCorePackage.WORKBENCH_MODULE__RESOURCES:
				return resources != null && !resources.isEmpty();
			case ModuleCorePackage.WORKBENCH_MODULE__MODULE_TYPE:
				return moduleType != null && !moduleType.isEmpty();
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
		result.append(" (handle: ");
		result.append(handle);
		result.append(", deployedName: ");
		result.append(deployedName);
		result.append(", deployedPath: ");
		result.append(deployedPath);
		result.append(", modules: ");
		result.append(modules);
		result.append(')');
		return result.toString();
	}

} //WorkbenchModuleImpl
