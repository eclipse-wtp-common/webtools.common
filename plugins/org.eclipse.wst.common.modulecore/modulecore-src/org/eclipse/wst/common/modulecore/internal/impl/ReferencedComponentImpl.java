/**
 * <copyright>
 * </copyright>
 *
 * $Id: ReferencedComponentImpl.java,v 1.3 2005/03/15 02:36:13 cbridgha Exp $
 */
package org.eclipse.wst.common.modulecore.internal.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.wst.common.modulecore.DependencyType;
import org.eclipse.wst.common.modulecore.ReferencedComponent;
import org.eclipse.wst.common.modulecore.ModuleCorePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Dependent Module</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.ReferencedComponentImpl#getHandle <em>Handle</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.ReferencedComponentImpl#getRuntimePath <em>Runtime Path</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.ReferencedComponentImpl#getDependencyType <em>Dependency Type</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ReferencedComponentImpl extends EObjectImpl implements ReferencedComponent {
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
	 * The default value of the '{@link #getDependencyType() <em>Dependency Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDependencyType()
	 * @generated
	 * @ordered
	 */
	protected static final DependencyType DEPENDENCY_TYPE_EDEFAULT = DependencyType.USES_LITERAL;

	/**
	 * The cached value of the '{@link #getDependencyType() <em>Dependency Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDependencyType()
	 * @generated
	 * @ordered
	 */
	protected DependencyType dependencyType = DEPENDENCY_TYPE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ReferencedComponentImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return ModuleCorePackage.eINSTANCE.getReferencedComponent();
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
			eNotify(new ENotificationImpl(this, Notification.SET, ModuleCorePackage.REFERENCED_COMPONENT__HANDLE, oldHandle, handle));
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
			eNotify(new ENotificationImpl(this, Notification.SET, ModuleCorePackage.REFERENCED_COMPONENT__RUNTIME_PATH, oldRuntimePath, runtimePath));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DependencyType getDependencyType() {
		return dependencyType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDependencyType(DependencyType newDependencyType) {
		DependencyType oldDependencyType = dependencyType;
		dependencyType = newDependencyType == null ? DEPENDENCY_TYPE_EDEFAULT : newDependencyType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModuleCorePackage.REFERENCED_COMPONENT__DEPENDENCY_TYPE, oldDependencyType, dependencyType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(EStructuralFeature eFeature, boolean resolve) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ModuleCorePackage.REFERENCED_COMPONENT__HANDLE:
				return getHandle();
			case ModuleCorePackage.REFERENCED_COMPONENT__RUNTIME_PATH:
				return getRuntimePath();
			case ModuleCorePackage.REFERENCED_COMPONENT__DEPENDENCY_TYPE:
				return getDependencyType();
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
			case ModuleCorePackage.REFERENCED_COMPONENT__HANDLE:
				setHandle((URI)newValue);
				return;
			case ModuleCorePackage.REFERENCED_COMPONENT__RUNTIME_PATH:
				setRuntimePath((URI)newValue);
				return;
			case ModuleCorePackage.REFERENCED_COMPONENT__DEPENDENCY_TYPE:
				setDependencyType((DependencyType)newValue);
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
			case ModuleCorePackage.REFERENCED_COMPONENT__HANDLE:
				setHandle(HANDLE_EDEFAULT);
				return;
			case ModuleCorePackage.REFERENCED_COMPONENT__RUNTIME_PATH:
				setRuntimePath(RUNTIME_PATH_EDEFAULT);
				return;
			case ModuleCorePackage.REFERENCED_COMPONENT__DEPENDENCY_TYPE:
				setDependencyType(DEPENDENCY_TYPE_EDEFAULT);
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
			case ModuleCorePackage.REFERENCED_COMPONENT__HANDLE:
				return HANDLE_EDEFAULT == null ? handle != null : !HANDLE_EDEFAULT.equals(handle);
			case ModuleCorePackage.REFERENCED_COMPONENT__RUNTIME_PATH:
				return RUNTIME_PATH_EDEFAULT == null ? runtimePath != null : !RUNTIME_PATH_EDEFAULT.equals(runtimePath);
			case ModuleCorePackage.REFERENCED_COMPONENT__DEPENDENCY_TYPE:
				return dependencyType != DEPENDENCY_TYPE_EDEFAULT;
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
		result.append(", runtimePath: ");
		result.append(runtimePath);
		result.append(", dependencyType: ");
		result.append(dependencyType);
		result.append(')');
		return result.toString();
	}

} //ReferencedComponentImpl
