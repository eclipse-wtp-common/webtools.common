/**
 * <copyright>
 * </copyright>
 *
 * $Id: ComponentResourceImpl.java,v 1.3 2005/03/15 02:36:13 cbridgha Exp $
 */
package org.eclipse.wst.common.modulecore.internal.impl;

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
import org.eclipse.wst.common.modulecore.WorkbenchComponent;
import org.eclipse.wst.common.modulecore.ComponentResource;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Workbench Module Resource</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.ComponentResourceImpl#getSourcePath <em>Source Path</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.ComponentResourceImpl#getRuntimePath <em>Runtime Path</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.ComponentResourceImpl#getExclusions <em>Exclusions</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.ComponentResourceImpl#getComponent <em>Component</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ComponentResourceImpl extends EObjectImpl implements ComponentResource {
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
	 * The default value of the '{@link #getRuntimePath() <em>Runtime Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRuntimePath()
	 * @generated
	 * @ordered
	 */
	protected static final URI RUNTIME_PATH_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRuntimePath() <em>Runtime Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRuntimePath()
	 * @generated
	 * @ordered
	 */
	protected URI runtimePath = RUNTIME_PATH_EDEFAULT;

	/**
	 * The cached value of the '{@link #getExclusions() <em>Exclusions</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExclusions()
	 * @generated
	 * @ordered
	 */
	protected EList exclusions = null;
	
	protected static final int VIRTUAL = 0;
	protected static final int PERSISTED = 1;
	
	private int type = PERSISTED;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ComponentResourceImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return ModuleCorePackage.eINSTANCE.getComponentResource();
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
			eNotify(new ENotificationImpl(this, Notification.SET, ModuleCorePackage.COMPONENT_RESOURCE__SOURCE_PATH, oldSourcePath, sourcePath));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public URI getRuntimePath() {
		return runtimePath;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRuntimePath(URI newRuntimePath) {
		URI oldRuntimePath = runtimePath;
		runtimePath = newRuntimePath;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModuleCorePackage.COMPONENT_RESOURCE__RUNTIME_PATH, oldRuntimePath, runtimePath));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getExclusions() {
		if (exclusions == null) {
			exclusions = new EDataTypeUniqueEList(String.class, this, ModuleCorePackage.COMPONENT_RESOURCE__EXCLUSIONS);
		}
		return exclusions;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public WorkbenchComponent getComponent() {
		if (eContainerFeatureID != ModuleCorePackage.COMPONENT_RESOURCE__COMPONENT) return null;
		return (WorkbenchComponent)eContainer;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setComponent(WorkbenchComponent newComponent) {
		if (newComponent != eContainer || (eContainerFeatureID != ModuleCorePackage.COMPONENT_RESOURCE__COMPONENT && newComponent != null)) {
			if (EcoreUtil.isAncestor(this, newComponent))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eContainer != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newComponent != null)
				msgs = ((InternalEObject)newComponent).eInverseAdd(this, ModuleCorePackage.WORKBENCH_COMPONENT__RESOURCES, WorkbenchComponent.class, msgs);
			msgs = eBasicSetContainer((InternalEObject)newComponent, ModuleCorePackage.COMPONENT_RESOURCE__COMPONENT, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModuleCorePackage.COMPONENT_RESOURCE__COMPONENT, newComponent, newComponent));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
		if (featureID >= 0) {
			switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
				case ModuleCorePackage.COMPONENT_RESOURCE__COMPONENT:
					if (eContainer != null)
						msgs = eBasicRemoveFromContainer(msgs);
					return eBasicSetContainer(otherEnd, ModuleCorePackage.COMPONENT_RESOURCE__COMPONENT, msgs);
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
				case ModuleCorePackage.COMPONENT_RESOURCE__COMPONENT:
					return eBasicSetContainer(null, ModuleCorePackage.COMPONENT_RESOURCE__COMPONENT, msgs);
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
				case ModuleCorePackage.COMPONENT_RESOURCE__COMPONENT:
					return eContainer.eInverseRemove(this, ModuleCorePackage.WORKBENCH_COMPONENT__RESOURCES, WorkbenchComponent.class, msgs);
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
			case ModuleCorePackage.COMPONENT_RESOURCE__SOURCE_PATH:
				return getSourcePath();
			case ModuleCorePackage.COMPONENT_RESOURCE__RUNTIME_PATH:
				return getRuntimePath();
			case ModuleCorePackage.COMPONENT_RESOURCE__EXCLUSIONS:
				return getExclusions();
			case ModuleCorePackage.COMPONENT_RESOURCE__COMPONENT:
				return getComponent();
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
			case ModuleCorePackage.COMPONENT_RESOURCE__SOURCE_PATH:
				setSourcePath((URI)newValue);
				return;
			case ModuleCorePackage.COMPONENT_RESOURCE__RUNTIME_PATH:
				setRuntimePath((URI)newValue);
				return;
			case ModuleCorePackage.COMPONENT_RESOURCE__EXCLUSIONS:
				getExclusions().clear();
				getExclusions().addAll((Collection)newValue);
				return;
			case ModuleCorePackage.COMPONENT_RESOURCE__COMPONENT:
				setComponent((WorkbenchComponent)newValue);
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
			case ModuleCorePackage.COMPONENT_RESOURCE__SOURCE_PATH:
				setSourcePath(SOURCE_PATH_EDEFAULT);
				return;
			case ModuleCorePackage.COMPONENT_RESOURCE__RUNTIME_PATH:
				setRuntimePath(RUNTIME_PATH_EDEFAULT);
				return;
			case ModuleCorePackage.COMPONENT_RESOURCE__EXCLUSIONS:
				getExclusions().clear();
				return;
			case ModuleCorePackage.COMPONENT_RESOURCE__COMPONENT:
				setComponent((WorkbenchComponent)null);
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
			case ModuleCorePackage.COMPONENT_RESOURCE__SOURCE_PATH:
				return SOURCE_PATH_EDEFAULT == null ? sourcePath != null : !SOURCE_PATH_EDEFAULT.equals(sourcePath);
			case ModuleCorePackage.COMPONENT_RESOURCE__RUNTIME_PATH:
				return RUNTIME_PATH_EDEFAULT == null ? runtimePath != null : !RUNTIME_PATH_EDEFAULT.equals(runtimePath);
			case ModuleCorePackage.COMPONENT_RESOURCE__EXCLUSIONS:
				return exclusions != null && !exclusions.isEmpty();
			case ModuleCorePackage.COMPONENT_RESOURCE__COMPONENT:
				return getComponent() != null;
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
		result.append(", runtimePath: ");
		result.append(runtimePath);
		result.append(", exclusions: ");
		result.append(exclusions);
		result.append(')');
		return result.toString();
	}

	public int getType() {
		return type;
	}
	
	
	public void setType(int type) {
		this.type = type;
	}
	

} //ComponentResourceImpl
