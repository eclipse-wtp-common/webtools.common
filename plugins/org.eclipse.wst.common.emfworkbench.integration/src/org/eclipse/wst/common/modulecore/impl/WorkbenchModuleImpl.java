/**
 * <copyright>
 * </copyright>
 *
 * $Id: WorkbenchModuleImpl.java,v 1.1 2005/01/17 21:08:18 cbridgha Exp $
 */
package org.eclipse.wst.common.modulecore.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import org.eclipse.wst.common.modulecore.IModuleHandle;
import org.eclipse.wst.common.modulecore.IModuleType;
import org.eclipse.wst.common.modulecore.ModuleCorePackage;
import org.eclipse.wst.common.modulecore.ModuleResource;
import org.eclipse.wst.common.modulecore.WorkbenchModule;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Workbench Module</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.WorkbenchModuleImpl#getHandle <em>Handle</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.WorkbenchModuleImpl#getDependentModules <em>Dependent Modules</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.WorkbenchModuleImpl#getResources <em>Resources</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.WorkbenchModuleImpl#getModuleType <em>Module Type</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class WorkbenchModuleImpl extends EObjectImpl implements WorkbenchModule {
	/**
	 * The cached value of the '{@link #getHandle() <em>Handle</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHandle()
	 * @generated
	 * @ordered
	 */
	protected IModuleHandle handle = null;

	/**
	 * The cached value of the '{@link #getDependentModules() <em>Dependent Modules</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDependentModules()
	 * @generated
	 * @ordered
	 */
	protected EList dependentModules = null;

	/**
	 * The cached value of the '{@link #getResources() <em>Resources</em>}' reference list.
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
	public IModuleHandle getHandle() {
		return handle;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetHandle(IModuleHandle newHandle, NotificationChain msgs) {
		IModuleHandle oldHandle = handle;
		handle = newHandle;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ModuleCorePackage.WORKBENCH_MODULE__HANDLE, oldHandle, newHandle);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setHandle(IModuleHandle newHandle) {
		if (newHandle != handle) {
			NotificationChain msgs = null;
			if (handle != null)
				msgs = ((InternalEObject)handle).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ModuleCorePackage.WORKBENCH_MODULE__HANDLE, null, msgs);
			if (newHandle != null)
				msgs = ((InternalEObject)newHandle).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ModuleCorePackage.WORKBENCH_MODULE__HANDLE, null, msgs);
			msgs = basicSetHandle(newHandle, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModuleCorePackage.WORKBENCH_MODULE__HANDLE, newHandle, newHandle));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getDependentModules() {
		if (dependentModules == null) {
			dependentModules = new EObjectResolvingEList(WorkbenchModule.class, this, ModuleCorePackage.WORKBENCH_MODULE__DEPENDENT_MODULES);
		}
		return dependentModules;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getResources() {
		if (resources == null) {
			resources = new EObjectResolvingEList(ModuleResource.class, this, ModuleCorePackage.WORKBENCH_MODULE__RESOURCES);
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
			moduleType = new EObjectResolvingEList(IModuleType.class, this, ModuleCorePackage.WORKBENCH_MODULE__MODULE_TYPE);
		}
		return moduleType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
		if (featureID >= 0) {
			switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
				case ModuleCorePackage.WORKBENCH_MODULE__HANDLE:
					return basicSetHandle(null, msgs);
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
			case ModuleCorePackage.WORKBENCH_MODULE__DEPENDENT_MODULES:
				return getDependentModules();
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
				setHandle((IModuleHandle)newValue);
				return;
			case ModuleCorePackage.WORKBENCH_MODULE__DEPENDENT_MODULES:
				getDependentModules().clear();
				getDependentModules().addAll((Collection)newValue);
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
				setHandle((IModuleHandle)null);
				return;
			case ModuleCorePackage.WORKBENCH_MODULE__DEPENDENT_MODULES:
				getDependentModules().clear();
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
				return handle != null;
			case ModuleCorePackage.WORKBENCH_MODULE__DEPENDENT_MODULES:
				return dependentModules != null && !dependentModules.isEmpty();
			case ModuleCorePackage.WORKBENCH_MODULE__RESOURCES:
				return resources != null && !resources.isEmpty();
			case ModuleCorePackage.WORKBENCH_MODULE__MODULE_TYPE:
				return moduleType != null && !moduleType.isEmpty();
		}
		return eDynamicIsSet(eFeature);
	}

} //WorkbenchModuleImpl
