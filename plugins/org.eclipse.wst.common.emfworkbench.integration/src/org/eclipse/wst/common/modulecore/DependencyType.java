/**
 * <copyright>
 * </copyright>
 *
 * $Id: DependencyType.java,v 1.1 2005/02/09 02:48:39 cbridgha Exp $
 */
package org.eclipse.wst.common.modulecore;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.AbstractEnumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Dependency Type</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * <!-- begin-model-doc -->
 * uses=0
 * consumes=1
 * <!-- end-model-doc -->
 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getDependencyType()
 * @model
 * @generated
 */
public final class DependencyType extends AbstractEnumerator {
	/**
	 * The '<em><b>Uses</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Uses</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #USES_LITERAL
	 * @model name="uses"
	 * @generated
	 * @ordered
	 */
	public static final int USES = 0;

	/**
	 * The '<em><b>Consumes</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>Consumes</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #CONSUMES_LITERAL
	 * @model name="consumes"
	 * @generated
	 * @ordered
	 */
	public static final int CONSUMES = 1;

	/**
	 * The '<em><b>Uses</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #USES
	 * @generated
	 * @ordered
	 */
	public static final DependencyType USES_LITERAL = new DependencyType(USES, "uses");

	/**
	 * The '<em><b>Consumes</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #CONSUMES
	 * @generated
	 * @ordered
	 */
	public static final DependencyType CONSUMES_LITERAL = new DependencyType(CONSUMES, "consumes");

	/**
	 * An array of all the '<em><b>Dependency Type</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final DependencyType[] VALUES_ARRAY =
		new DependencyType[] {
			USES_LITERAL,
			CONSUMES_LITERAL,
		};

	/**
	 * A public read-only list of all the '<em><b>Dependency Type</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Dependency Type</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static DependencyType get(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			DependencyType result = VALUES_ARRAY[i];
			if (result.toString().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Dependency Type</b></em>' literal with the specified value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static DependencyType get(int value) {
		switch (value) {
			case USES: return USES_LITERAL;
			case CONSUMES: return CONSUMES_LITERAL;
		}
		return null;	
	}

	/**
	 * Only this class can construct instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private DependencyType(int value, String name) {
		super(value, name);
	}

} //DependencyType
