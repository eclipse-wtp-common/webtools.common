/**
 * <copyright>
 * </copyright>
 *
 * $Id: WorkbenchModuleImpl.java,v 1.1 2005/01/14 21:02:41 cbridgha Exp $
 */
package org.eclipse.wst.common.projectmodule.impl;

import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import org.eclipse.wst.common.projectmodule.ModuleResource;
import org.eclipse.wst.common.projectmodule.ProjectModulePackage;
import org.eclipse.wst.common.projectmodule.WorkbenchModule;

import com.ibm.wtp.emf.workbench.ProjectUtilities;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Workbench Module</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.projectmodule.impl.WorkbenchModuleImpl#getModuleType <em>Module Type</em>}</li>
 *   <li>{@link org.eclipse.wst.common.projectmodule.impl.WorkbenchModuleImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.wst.common.projectmodule.impl.WorkbenchModuleImpl#getDependentModules <em>Dependent Modules</em>}</li>
 *   <li>{@link org.eclipse.wst.common.projectmodule.impl.WorkbenchModuleImpl#getResources <em>Resources</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class WorkbenchModuleImpl extends EObjectImpl implements WorkbenchModule {
	/**
	 * The default value of the '{@link #getModuleType() <em>Module Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getModuleType()
	 * @generated
	 * @ordered
	 */
	protected static final String MODULE_TYPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getModuleType() <em>Module Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getModuleType()
	 * @generated
	 * @ordered
	 */
	protected String moduleType = MODULE_TYPE_EDEFAULT;

	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

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
		return ProjectModulePackage.eINSTANCE.getWorkbenchModule();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getModuleType() {
		return moduleType;
	}

	/**
	 * Assuming modules are defined and contained within one project.
	 * This api may need to change in the future
	 * @return IProject
	 */
	public IProject getProject() {
		return ProjectUtilities.getProject(eResource());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setModuleType(String newModuleType) {
		String oldModuleType = moduleType;
		moduleType = newModuleType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ProjectModulePackage.WORKBENCH_MODULE__MODULE_TYPE, oldModuleType, moduleType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ProjectModulePackage.WORKBENCH_MODULE__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getDependentModules() {
		if (dependentModules == null) {
			dependentModules = new EObjectResolvingEList(WorkbenchModule.class, this, ProjectModulePackage.WORKBENCH_MODULE__DEPENDENT_MODULES);
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
			resources = new EObjectResolvingEList(ModuleResource.class, this, ProjectModulePackage.WORKBENCH_MODULE__RESOURCES);
		}
		return resources;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(EStructuralFeature eFeature, boolean resolve) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ProjectModulePackage.WORKBENCH_MODULE__MODULE_TYPE:
				return getModuleType();
			case ProjectModulePackage.WORKBENCH_MODULE__NAME:
				return getName();
			case ProjectModulePackage.WORKBENCH_MODULE__DEPENDENT_MODULES:
				return getDependentModules();
			case ProjectModulePackage.WORKBENCH_MODULE__RESOURCES:
				return getResources();
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
			case ProjectModulePackage.WORKBENCH_MODULE__MODULE_TYPE:
				setModuleType((String)newValue);
				return;
			case ProjectModulePackage.WORKBENCH_MODULE__NAME:
				setName((String)newValue);
				return;
			case ProjectModulePackage.WORKBENCH_MODULE__DEPENDENT_MODULES:
				getDependentModules().clear();
				getDependentModules().addAll((Collection)newValue);
				return;
			case ProjectModulePackage.WORKBENCH_MODULE__RESOURCES:
				getResources().clear();
				getResources().addAll((Collection)newValue);
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
			case ProjectModulePackage.WORKBENCH_MODULE__MODULE_TYPE:
				setModuleType(MODULE_TYPE_EDEFAULT);
				return;
			case ProjectModulePackage.WORKBENCH_MODULE__NAME:
				setName(NAME_EDEFAULT);
				return;
			case ProjectModulePackage.WORKBENCH_MODULE__DEPENDENT_MODULES:
				getDependentModules().clear();
				return;
			case ProjectModulePackage.WORKBENCH_MODULE__RESOURCES:
				getResources().clear();
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
			case ProjectModulePackage.WORKBENCH_MODULE__MODULE_TYPE:
				return MODULE_TYPE_EDEFAULT == null ? moduleType != null : !MODULE_TYPE_EDEFAULT.equals(moduleType);
			case ProjectModulePackage.WORKBENCH_MODULE__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case ProjectModulePackage.WORKBENCH_MODULE__DEPENDENT_MODULES:
				return dependentModules != null && !dependentModules.isEmpty();
			case ProjectModulePackage.WORKBENCH_MODULE__RESOURCES:
				return resources != null && !resources.isEmpty();
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
		result.append(" (moduleType: ");
		result.append(moduleType);
		result.append(", name: ");
		result.append(name);
		result.append(')');
		return result.toString();
	}

} //WorkbenchModuleImpl
