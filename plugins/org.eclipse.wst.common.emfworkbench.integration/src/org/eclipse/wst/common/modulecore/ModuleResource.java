/**
 * <copyright>
 * </copyright>
 *
 * $Id: ModuleResource.java,v 1.5 2005/01/26 16:48:35 cbridgha Exp $
 */
package org.eclipse.wst.common.modulecore;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Module Resource</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.modulecore.ModuleResource#getPath <em>Path</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.ModuleResource#getRoot <em>Root</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.ModuleResource#getExclusions <em>Exclusions</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getModuleResource()
 * @model 
 * @generated
 */
public interface ModuleResource extends EObject{
	/**
	 * Returns the value of the '<em><b>Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Path</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Path</em>' attribute.
	 * @see #setPath(URI)
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getModuleResource_Path()
	 * @model dataType="org.eclipse.wst.common.modulecore.URI"
	 * @generated
	 */
	URI getPath();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.modulecore.ModuleResource#getPath <em>Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Path</em>' attribute.
	 * @see #getPath()
	 * @generated
	 */
	void setPath(URI value);

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
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getModuleResource_Root()
	 * @model dataType="org.eclipse.wst.common.modulecore.URI"
	 * @generated
	 */
	URI getRoot();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.modulecore.ModuleResource#getRoot <em>Root</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Root</em>' attribute.
	 * @see #getRoot()
	 * @generated
	 */
	void setRoot(URI value);

	/**
	 * Returns the value of the '<em><b>Exclusions</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.emf.common.util.URI}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Exclusions</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Exclusions</em>' attribute list.
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getModuleResource_Exclusions()
	 * @model type="org.eclipse.emf.common.util.URI" dataType="org.eclipse.wst.common.modulecore.URI"
	 * @generated
	 */
	EList getExclusions();

} // ModuleResource
