/**
 * <copyright>
 * </copyright>
 *
 * $Id: DeploySchemeImpl.java,v 1.1 2005/01/17 21:08:17 cbridgha Exp $
 */
package org.eclipse.wst.common.modulecore.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.wst.common.modulecore.DeployScheme;
import org.eclipse.wst.common.modulecore.ModuleCorePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Deploy Scheme</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.DeploySchemeImpl#getType <em>Type</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.impl.DeploySchemeImpl#getServerTarget <em>Server Target</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DeploySchemeImpl extends EObjectImpl implements DeployScheme {
	/**
	 * The default value of the '{@link #getType() <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected static final String TYPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getType() <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected String type = TYPE_EDEFAULT;

	/**
	 * The default value of the '{@link #getServerTarget() <em>Server Target</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getServerTarget()
	 * @generated
	 * @ordered
	 */
	protected static final String SERVER_TARGET_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getServerTarget() <em>Server Target</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getServerTarget()
	 * @generated
	 * @ordered
	 */
	protected String serverTarget = SERVER_TARGET_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DeploySchemeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return ModuleCorePackage.eINSTANCE.getDeployScheme();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getType() {
		return type;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setType(String newType) {
		String oldType = type;
		type = newType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModuleCorePackage.DEPLOY_SCHEME__TYPE, oldType, type));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getServerTarget() {
		return serverTarget;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setServerTarget(String newServerTarget) {
		String oldServerTarget = serverTarget;
		serverTarget = newServerTarget;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ModuleCorePackage.DEPLOY_SCHEME__SERVER_TARGET, oldServerTarget, serverTarget));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(EStructuralFeature eFeature, boolean resolve) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ModuleCorePackage.DEPLOY_SCHEME__TYPE:
				return getType();
			case ModuleCorePackage.DEPLOY_SCHEME__SERVER_TARGET:
				return getServerTarget();
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
			case ModuleCorePackage.DEPLOY_SCHEME__TYPE:
				setType((String)newValue);
				return;
			case ModuleCorePackage.DEPLOY_SCHEME__SERVER_TARGET:
				setServerTarget((String)newValue);
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
			case ModuleCorePackage.DEPLOY_SCHEME__TYPE:
				setType(TYPE_EDEFAULT);
				return;
			case ModuleCorePackage.DEPLOY_SCHEME__SERVER_TARGET:
				setServerTarget(SERVER_TARGET_EDEFAULT);
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
			case ModuleCorePackage.DEPLOY_SCHEME__TYPE:
				return TYPE_EDEFAULT == null ? type != null : !TYPE_EDEFAULT.equals(type);
			case ModuleCorePackage.DEPLOY_SCHEME__SERVER_TARGET:
				return SERVER_TARGET_EDEFAULT == null ? serverTarget != null : !SERVER_TARGET_EDEFAULT.equals(serverTarget);
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
		result.append(" (type: ");
		result.append(type);
		result.append(", serverTarget: ");
		result.append(serverTarget);
		result.append(')');
		return result.toString();
	}

} //DeploySchemeImpl
