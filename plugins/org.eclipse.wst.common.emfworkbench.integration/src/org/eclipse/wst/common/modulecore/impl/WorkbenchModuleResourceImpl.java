/**
 * <copyright>
 * </copyright>
 *
 * $Id: WorkbenchModuleResourceImpl.java,v 1.6 2005/02/07 21:07:01 cbridgha Exp $
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
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.wst.common.modulecore.ModuleCorePackage;
import org.eclipse.wst.common.modulecore.WorkbenchModule;
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
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.WorkbenchModuleResourceImpl#getModule <em>Module</em>}</li>
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
	public WorkbenchModule getModule() {
		if (eContainerFeatureID != ModuleCorePackage.WORKBENCH_MODULE_RESOURCE__MODULE) return null;
		return (WorkbenchModule)eContainer;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setModule(WorkbenchModule newModule) {
		if (newModule != eContainer || (eContainerFeatureID != ModuleCorePackage.WORKBENCH_MODULE_RESOURCE__MODULE && newModule != null)) {
			if (EcoreUtil.isAncestor(this, newModule))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eContainer != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newModule != null)
				msgs = ((InternalEObject)newModule).eInverseAdd(this, ModuleCorePackage.WORKBENCH_MODULE__RESOURCES, WorkbenchModule.class, msgs);
			msgs = eBasicSetContainer((InternalEObject)newModule, ModuleCorePackage.WORKBENCH_MODULE_RESOURCE__MODULE, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModuleCorePackage.WORKBENCH_MODULE_RESOURCE__MODULE, newModule, newModule));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
		if (featureID >= 0) {
			switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
				case ModuleCorePackage.WORKBENCH_MODULE_RESOURCE__MODULE:
					if (eContainer != null)
						msgs = eBasicRemoveFromContainer(msgs);
					return eBasicSetContainer(otherEnd, ModuleCorePackage.WORKBENCH_MODULE_RESOURCE__MODULE, msgs);
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
				case ModuleCorePackage.WORKBENCH_MODULE_RESOURCE__MODULE:
					return eBasicSetContainer(null, ModuleCorePackage.WORKBENCH_MODULE_RESOURCE__MODULE, msgs);
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
	public NotificationChain eBasicRemoveFromContainer(NotificationChain msgs) {
		if (eContainerFeatureID >= 0) {
			switch (eContainerFeatureID) {
				case ModuleCorePackage.WORKBENCH_MODULE_RESOURCE__MODULE:
					return eContainer.eInverseRemove(this, ModuleCorePackage.WORKBENCH_MODULE__RESOURCES, WorkbenchModule.class, msgs);
				default:
					return eDynamicBasicRemoveFromContainer(msgs);
			}
		}
		return eContainer.eInverseRemove(this, EOPPOSITE_FEATURE_BASE - eContainerFeatureID, null, msgs);
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
			case ModuleCorePackage.WORKBENCH_MODULE_RESOURCE__MODULE:
				return getModule();
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
			case ModuleCorePackage.WORKBENCH_MODULE_RESOURCE__MODULE:
				setModule((WorkbenchModule)newValue);
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
			case ModuleCorePackage.WORKBENCH_MODULE_RESOURCE__MODULE:
				setModule((WorkbenchModule)null);
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
			case ModuleCorePackage.WORKBENCH_MODULE_RESOURCE__MODULE:
				return getModule() != null;
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
