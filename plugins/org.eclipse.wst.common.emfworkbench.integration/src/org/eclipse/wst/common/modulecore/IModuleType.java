/**
 * <copyright>
 * </copyright>
 *
 * $Id: IModuleType.java,v 1.4 2005/01/26 16:48:35 cbridgha Exp $
 */
package org.eclipse.wst.common.modulecore;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>IModule Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.modulecore.IModuleType#getRoot <em>Root</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.IModuleType#getMetadataResources <em>Metadata Resources</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.IModuleType#getTypeName <em>Type Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getIModuleType()
 * @model 
 * @generated
 */
public interface IModuleType extends EObject {
	/**
	 * Returns the value of the '<em><b>Root</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Root</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Root</em>' attribute.
	 * @see #setRoot(URI)
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getIModuleType_Root()
	 * @model dataType="org.eclipse.wst.common.modulecore.URI"
	 * @generated
	 */
	URI getRoot();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.modulecore.IModuleType#getRoot <em>Root</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Root</em>' attribute.
	 * @see #getRoot()
	 * @generated
	 */
	void setRoot(URI value);

	/**
	 * Returns the value of the '<em><b>Metadata Resources</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.emf.common.util.URI}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Metadata Resources</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Metadata Resources</em>' attribute list.
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getIModuleType_MetadataResources()
	 * @model type="org.eclipse.emf.common.util.URI" dataType="org.eclipse.wst.common.modulecore.URI"
	 * @generated
	 */
	EList getMetadataResources();

	/**
	 * Returns the value of the '<em><b>Type Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Type Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type Name</em>' attribute.
	 * @see #setTypeName(String)
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getIModuleType_TypeName()
	 * @model 
	 * @generated
	 */
	String getTypeName();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.modulecore.IModuleType#getTypeName <em>Type Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type Name</em>' attribute.
	 * @see #getTypeName()
	 * @generated
	 */
	void setTypeName(String value);

} // IModuleType
