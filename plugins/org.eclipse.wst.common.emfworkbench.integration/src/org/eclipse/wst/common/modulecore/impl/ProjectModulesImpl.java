/**
 * <copyright>
 * </copyright>
 *
 * $Id: ProjectModulesImpl.java,v 1.1 2005/01/24 21:34:03 cbridgha Exp $
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

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.wst.common.modulecore.DeployedApplication;
import org.eclipse.wst.common.modulecore.ModuleCorePackage;
import org.eclipse.wst.common.modulecore.ProjectModules;
import org.eclipse.wst.common.modulecore.WorkbenchApplication;
import org.eclipse.wst.common.modulecore.WorkbenchModule;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Project Modules</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.ProjectModulesImpl#getProjectName <em>Project Name</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.ProjectModulesImpl#getDeployedApplications <em>Deployed Applications</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.ProjectModulesImpl#getWorkbenchApplications <em>Workbench Applications</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.ProjectModulesImpl#getWorkbenchModules <em>Workbench Modules</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ProjectModulesImpl extends EObjectImpl implements ProjectModules {
	/**
	 * The default value of the '{@link #getProjectName() <em>Project Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProjectName()
	 * @generated
	 * @ordered
	 */
	protected static final String PROJECT_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getProjectName() <em>Project Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProjectName()
	 * @generated
	 * @ordered
	 */
	protected String projectName = PROJECT_NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getDeployedApplications() <em>Deployed Applications</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDeployedApplications()
	 * @generated
	 * @ordered
	 */
	protected EList deployedApplications = null;

	/**
	 * The cached value of the '{@link #getWorkbenchApplications() <em>Workbench Applications</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWorkbenchApplications()
	 * @generated
	 * @ordered
	 */
	protected EList workbenchApplications = null;

	/**
	 * The cached value of the '{@link #getWorkbenchModules() <em>Workbench Modules</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWorkbenchModules()
	 * @generated
	 * @ordered
	 */
	protected EList workbenchModules = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ProjectModulesImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return ModuleCorePackage.eINSTANCE.getProjectModules();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setProjectName(String newProjectName) {
		String oldProjectName = projectName;
		projectName = newProjectName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModuleCorePackage.PROJECT_MODULES__PROJECT_NAME, oldProjectName, projectName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getDeployedApplications() {
		if (deployedApplications == null) {
			deployedApplications = new EObjectContainmentEList(DeployedApplication.class, this, ModuleCorePackage.PROJECT_MODULES__DEPLOYED_APPLICATIONS);
		}
		return deployedApplications;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getWorkbenchApplications() {
		if (workbenchApplications == null) {
			workbenchApplications = new EObjectContainmentEList(WorkbenchApplication.class, this, ModuleCorePackage.PROJECT_MODULES__WORKBENCH_APPLICATIONS);
		}
		return workbenchApplications;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getWorkbenchModules() {
		if (workbenchModules == null) {
			workbenchModules = new EObjectContainmentEList(WorkbenchModule.class, this, ModuleCorePackage.PROJECT_MODULES__WORKBENCH_MODULES);
		}
		return workbenchModules;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
		if (featureID >= 0) {
			switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
				case ModuleCorePackage.PROJECT_MODULES__DEPLOYED_APPLICATIONS:
					return ((InternalEList)getDeployedApplications()).basicRemove(otherEnd, msgs);
				case ModuleCorePackage.PROJECT_MODULES__WORKBENCH_APPLICATIONS:
					return ((InternalEList)getWorkbenchApplications()).basicRemove(otherEnd, msgs);
				case ModuleCorePackage.PROJECT_MODULES__WORKBENCH_MODULES:
					return ((InternalEList)getWorkbenchModules()).basicRemove(otherEnd, msgs);
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
			case ModuleCorePackage.PROJECT_MODULES__PROJECT_NAME:
				return getProjectName();
			case ModuleCorePackage.PROJECT_MODULES__DEPLOYED_APPLICATIONS:
				return getDeployedApplications();
			case ModuleCorePackage.PROJECT_MODULES__WORKBENCH_APPLICATIONS:
				return getWorkbenchApplications();
			case ModuleCorePackage.PROJECT_MODULES__WORKBENCH_MODULES:
				return getWorkbenchModules();
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
			case ModuleCorePackage.PROJECT_MODULES__PROJECT_NAME:
				setProjectName((String)newValue);
				return;
			case ModuleCorePackage.PROJECT_MODULES__DEPLOYED_APPLICATIONS:
				getDeployedApplications().clear();
				getDeployedApplications().addAll((Collection)newValue);
				return;
			case ModuleCorePackage.PROJECT_MODULES__WORKBENCH_APPLICATIONS:
				getWorkbenchApplications().clear();
				getWorkbenchApplications().addAll((Collection)newValue);
				return;
			case ModuleCorePackage.PROJECT_MODULES__WORKBENCH_MODULES:
				getWorkbenchModules().clear();
				getWorkbenchModules().addAll((Collection)newValue);
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
			case ModuleCorePackage.PROJECT_MODULES__PROJECT_NAME:
				setProjectName(PROJECT_NAME_EDEFAULT);
				return;
			case ModuleCorePackage.PROJECT_MODULES__DEPLOYED_APPLICATIONS:
				getDeployedApplications().clear();
				return;
			case ModuleCorePackage.PROJECT_MODULES__WORKBENCH_APPLICATIONS:
				getWorkbenchApplications().clear();
				return;
			case ModuleCorePackage.PROJECT_MODULES__WORKBENCH_MODULES:
				getWorkbenchModules().clear();
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
			case ModuleCorePackage.PROJECT_MODULES__PROJECT_NAME:
				return PROJECT_NAME_EDEFAULT == null ? projectName != null : !PROJECT_NAME_EDEFAULT.equals(projectName);
			case ModuleCorePackage.PROJECT_MODULES__DEPLOYED_APPLICATIONS:
				return deployedApplications != null && !deployedApplications.isEmpty();
			case ModuleCorePackage.PROJECT_MODULES__WORKBENCH_APPLICATIONS:
				return workbenchApplications != null && !workbenchApplications.isEmpty();
			case ModuleCorePackage.PROJECT_MODULES__WORKBENCH_MODULES:
				return workbenchModules != null && !workbenchModules.isEmpty();
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
		result.append(" (projectName: ");
		result.append(projectName);
		result.append(')');
		return result.toString();
	}

} //ProjectModulesImpl
