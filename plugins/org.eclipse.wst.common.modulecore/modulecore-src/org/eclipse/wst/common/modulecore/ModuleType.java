/**
 * <copyright>
 * </copyright>
 *
 * $Id: ModuleType.java,v 1.1 2005/02/13 16:27:46 cbridgha Exp $
 */
package org.eclipse.wst.common.modulecore;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Module Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.modulecore.ModuleType#getMetadataResources <em>Metadata Resources</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.ModuleType#getModuleTypeId <em>Module Type Id</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getModuleType()
 * @model 
 * @generated
 */
public interface ModuleType extends EObject {
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
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getModuleType_MetadataResources()
	 * @model type="org.eclipse.emf.common.util.URI" dataType="org.eclipse.wst.common.modulecore.URI"
	 * @generated
	 */
	EList getMetadataResources();

	/**
	 * Returns the value of the '<em><b>Module Type Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Module Type Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Module Type Id</em>' attribute.
	 * @see #setModuleTypeId(String)
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getModuleType_ModuleTypeId()
	 * @model 
	 * @generated
	 */
	String getModuleTypeId();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.modulecore.ModuleType#getModuleTypeId <em>Module Type Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Module Type Id</em>' attribute.
	 * @see #getModuleTypeId()
	 * @generated
	 */
	void setModuleTypeId(String value);

} // ModuleType
