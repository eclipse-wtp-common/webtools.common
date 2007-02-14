/**
 * <copyright>
 * </copyright>
 *
 * $Id: ReferencedComponentImpl.java,v 1.4 2007/02/14 16:00:52 jsholl Exp $
 */
package org.eclipse.wst.common.componentcore.internal.impl;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.wst.common.componentcore.internal.ComponentcorePackage;
import org.eclipse.wst.common.componentcore.internal.DependencyType;
import org.eclipse.wst.common.componentcore.internal.ReferencedComponent;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Dependent Module</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.impl.ReferencedComponentImpl#getHandle <em>Handle</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.impl.ReferencedComponentImpl#getRuntimePath <em>Runtime Path</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.impl.ReferencedComponentImpl#getDependencyType <em>Dependency Type</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.impl.ReferencedComponentImpl#getDependentObject <em>Dependent Object</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.impl.ReferencedComponentImpl#getArchiveName <em>Archive Name</em>}</li>
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
	protected static final IPath RUNTIME_PATH_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRuntimePath() <em>Runtime Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRuntimePath()
	 * @generated
	 * @ordered
	 */
	protected IPath runtimePath = RUNTIME_PATH_EDEFAULT;

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
	 * The cached value of the '{@link #getDependentObject() <em>Dependent Object</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDependentObject()
	 * @generated
	 * @ordered
	 */
	protected EObject dependentObject = null;

	/**
	 * The default value of the '{@link #getArchiveName() <em>Archive Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getArchiveName()
	 * @generated
	 * @ordered
	 */
	protected static final String ARCHIVE_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getArchiveName() <em>Archive Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getArchiveName()
	 * @generated
	 * @ordered
	 */
	protected String archiveName = ARCHIVE_NAME_EDEFAULT;

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
		return ComponentcorePackage.eINSTANCE.getReferencedComponent();
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
			eNotify(new ENotificationImpl(this, Notification.SET, ComponentcorePackage.REFERENCED_COMPONENT__HANDLE, oldHandle, handle));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IPath getRuntimePath() {
		return runtimePath;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRuntimePath(IPath newRuntimePath) {
		IPath oldRuntimePath = runtimePath;
		runtimePath = newRuntimePath;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ComponentcorePackage.REFERENCED_COMPONENT__RUNTIME_PATH, oldRuntimePath, runtimePath));
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
			eNotify(new ENotificationImpl(this, Notification.SET, ComponentcorePackage.REFERENCED_COMPONENT__DEPENDENCY_TYPE, oldDependencyType, dependencyType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EObject getDependentObject() {
		if (dependentObject != null && dependentObject.eIsProxy()) {
			EObject oldDependentObject = dependentObject;
			dependentObject = eResolveProxy((InternalEObject)dependentObject);
			if (dependentObject != oldDependentObject) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ComponentcorePackage.REFERENCED_COMPONENT__DEPENDENT_OBJECT, oldDependentObject, dependentObject));
			}
		}
		return dependentObject;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EObject basicGetDependentObject() {
		return dependentObject;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDependentObject(EObject newDependentObject) {
		EObject oldDependentObject = dependentObject;
		dependentObject = newDependentObject;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ComponentcorePackage.REFERENCED_COMPONENT__DEPENDENT_OBJECT, oldDependentObject, dependentObject));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getArchiveName() {
		return archiveName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setArchiveName(String newArchiveName) {
		String oldArchiveName = archiveName;
		archiveName = newArchiveName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ComponentcorePackage.REFERENCED_COMPONENT__ARCHIVE_NAME, oldArchiveName, archiveName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(EStructuralFeature eFeature, boolean resolve) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ComponentcorePackage.REFERENCED_COMPONENT__HANDLE:
				return getHandle();
			case ComponentcorePackage.REFERENCED_COMPONENT__RUNTIME_PATH:
				return getRuntimePath();
			case ComponentcorePackage.REFERENCED_COMPONENT__DEPENDENCY_TYPE:
				return getDependencyType();
			case ComponentcorePackage.REFERENCED_COMPONENT__DEPENDENT_OBJECT:
				if (resolve) return getDependentObject();
				return basicGetDependentObject();
			case ComponentcorePackage.REFERENCED_COMPONENT__ARCHIVE_NAME:
				return getArchiveName();
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
			case ComponentcorePackage.REFERENCED_COMPONENT__HANDLE:
				setHandle((URI)newValue);
				return;
			case ComponentcorePackage.REFERENCED_COMPONENT__RUNTIME_PATH:
				setRuntimePath((IPath)newValue);
				return;
			case ComponentcorePackage.REFERENCED_COMPONENT__DEPENDENCY_TYPE:
				setDependencyType((DependencyType)newValue);
				return;
			case ComponentcorePackage.REFERENCED_COMPONENT__DEPENDENT_OBJECT:
				setDependentObject((EObject)newValue);
				return;
			case ComponentcorePackage.REFERENCED_COMPONENT__ARCHIVE_NAME:
				setArchiveName((String)newValue);
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
			case ComponentcorePackage.REFERENCED_COMPONENT__HANDLE:
				setHandle(HANDLE_EDEFAULT);
				return;
			case ComponentcorePackage.REFERENCED_COMPONENT__RUNTIME_PATH:
				setRuntimePath(RUNTIME_PATH_EDEFAULT);
				return;
			case ComponentcorePackage.REFERENCED_COMPONENT__DEPENDENCY_TYPE:
				setDependencyType(DEPENDENCY_TYPE_EDEFAULT);
				return;
			case ComponentcorePackage.REFERENCED_COMPONENT__DEPENDENT_OBJECT:
				setDependentObject((EObject)null);
				return;
			case ComponentcorePackage.REFERENCED_COMPONENT__ARCHIVE_NAME:
				setArchiveName(ARCHIVE_NAME_EDEFAULT);
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
			case ComponentcorePackage.REFERENCED_COMPONENT__HANDLE:
				return HANDLE_EDEFAULT == null ? handle != null : !HANDLE_EDEFAULT.equals(handle);
			case ComponentcorePackage.REFERENCED_COMPONENT__RUNTIME_PATH:
				return RUNTIME_PATH_EDEFAULT == null ? runtimePath != null : !RUNTIME_PATH_EDEFAULT.equals(runtimePath);
			case ComponentcorePackage.REFERENCED_COMPONENT__DEPENDENCY_TYPE:
				return dependencyType != DEPENDENCY_TYPE_EDEFAULT;
			case ComponentcorePackage.REFERENCED_COMPONENT__DEPENDENT_OBJECT:
				return dependentObject != null;
			case ComponentcorePackage.REFERENCED_COMPONENT__ARCHIVE_NAME:
				return ARCHIVE_NAME_EDEFAULT == null ? archiveName != null : !ARCHIVE_NAME_EDEFAULT.equals(archiveName);
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
		result.append(", archiveName: ");
		result.append(archiveName);
		result.append(')');
		return result.toString();
	}

} //ReferencedComponentImpl
