/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.emf.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.wst.common.internal.emf.utilities.Association;
import org.eclipse.wst.common.internal.emf.utilities.CloneablePublic;
import org.eclipse.wst.common.internal.emf.utilities.DeferredReferenceUtilityAction;


/**
 * Insert the type's description here. Creation date: (12/13/2000 9:10:55 PM)
 * 
 * @author: Administrator
 */
public class EtoolsCopyUtility {
	protected static final String REFENUMLITERAL_TYPE_NAME = "EEnumLiteral"; //$NON-NLS-1$
	private final EcorePackage pkg = EcorePackage.eINSTANCE;

	private final ETypedElement METAENAME = pkg.getENamedElement_Name();

	protected HashMap copiedObjects;
	protected List deferredReferenceCopies;
	protected boolean preserveIds = false;
	protected boolean copyAdapters = false;
	protected HashMap copiedAdapters;

	protected class DeferredSingleReferenceCopyAction extends DeferredReferenceUtilityAction {
		public DeferredSingleReferenceCopyAction(EReference aReference, Object aValue, String aSuffix, EObject aCopyContainer) {
			super(aReference, aValue, aSuffix, aCopyContainer);
		}

		public void performAction() {
			EObject value = (EObject) getReferenceValue();
			getCopyContainer().eSet(getReference(), getCopyIfFound(value));
		}
	}

	protected class DeferredManyReferenceCopyAction extends DeferredReferenceUtilityAction {
		public DeferredManyReferenceCopyAction(EReference aReference, List aValue, String aSuffix, EObject aCopyContainer) {
			super(aReference, aValue, aSuffix, aCopyContainer);
		}

		public void performAction() {
			List copyList = (List) getCopyContainer().eGet(getReference());
			Iterator it = ((List) getReferenceValue()).iterator();
			EObject next;
			while (it.hasNext()) {
				next = (EObject) it.next();
				copyList.add(getCopyIfFound(next));
			}
		}
	}

	/**
	 * Insert the method's description here. Creation date: (12/13/2000 9:14:26 PM)
	 * 
	 * @param aRefObject
	 *            org.eclipse.emf.ecore.EObject
	 */
	public EtoolsCopyUtility() {
	}

	/**
	 * Create a <code>DeferredManyReferenceCopyAction</code> and add it to the list of deferred
	 * copy actions.
	 */
	protected void addDeferredManyReferenceCopy(EReference reference, List aValue, String idSuffix, EObject aCopyContainer) {
		getDeferredReferenceCopies().add(new DeferredManyReferenceCopyAction(reference, aValue, idSuffix, aCopyContainer));
	}

	/**
	 * Create a <code>DeferredSingleReferenceCopyAction</code> and add it to the list of deferred
	 * copy actions.
	 */
	protected void addDeferredSingleReferenceCopy(EReference reference, EObject aValue, String idSuffix, EObject aCopyContainer) {
		getDeferredReferenceCopies().add(new DeferredSingleReferenceCopyAction(reference, aValue, idSuffix, aCopyContainer));
	}

	/**
	 * Check for an already copied object first.
	 */
	protected EObject containmentCopy(EObject anObject, String idSuffix) {
		EObject copied = getCopy(anObject);
		if (copied == null)
			copied = primCopy(anObject, idSuffix);
		return copied;
	}

	/**
	 * Check for an already copied object first.
	 */
	protected EObject containmentCopyObject(EObject anObject, String idSuffix) {
		EObject copied = getCopy(anObject);
		if (copied == null)
			copied = primCopyObject(anObject, idSuffix);
		return copied;
	}

	protected Resource containmentCopy(Resource aResource, String newUri) {
		/* copied resources are never cached */
		return primCopy(aResource, newUri);
	}

	/**
	 * Copy all Resources and RefObjects within <code>aGroup</code> and add them to
	 * <code>aGroup</code>. Non composite references will be deferred until all objects are
	 * copied from <code>aGroup</code>.
	 * 
	 * Copy Resources first and then copy RefObjects.
	 */
	public void copy(CopyGroup aGroup) {
		if (aGroup != null) {
			EtoolsCopySession session = new EtoolsCopySession(this);
			session.setPreserveIds(aGroup.getPreserveIds());
			session.copy(aGroup);
			session.flush();
		}
	}

	public EObject copy(EObject aRefObject) {
		return copy(aRefObject, null);
	}

	/**
	 * This method should be used if you are only going to copy <code>aRefObject</code> in this
	 * copy execution.
	 */
	public EObject copy(EObject aRefObject, String idSuffix) {
		EtoolsCopySession session = new EtoolsCopySession(this);
		EObject copied = session.copy(aRefObject, idSuffix);
		session.flush();
		return copied;
	}

	/**
	 * This method should be used if you are only going to copy <code>aRefObject</code> in this
	 * copy execution. This method only copies <code>aRefObject</code> with its properties, not
	 * references.
	 */
	public EObject copyObject(EObject aRefObject, String idSuffix) {
		EtoolsCopySession session = new EtoolsCopySession(this);
		EObject copied = session.copyObject(aRefObject, idSuffix);
		session.flush();
		return copied;
	}

	/**
	 * This method should be used if you are only going to copy <code>aResource</code> in this
	 * copy execution. The copied Resource will have a URI equal to <code>newUri</code>.
	 */
	public Resource copy(Resource aResource, String newUri) {
		EtoolsCopySession session = new EtoolsCopySession(this);
		Resource copied = session.copy(aResource, newUri);
		session.flush();
		return copied;
	}

	/**
	 * If an attribute is set and its value is not <code>null</code>, then perform copy
	 * <code>attribute</code> value from <code>aRefObject</code> to <code>copyRef</code>.
	 * Null is a valid value if the attribute is set.
	 */

	protected void copyAttribute(EAttribute attribute, EObject aRefObject, String idSuffix, EObject copyRef) {
		if (!attribute.isChangeable())
			return; //ignore
		if (attribute.isMany()) {
			List value = (List) aRefObject.eGet(attribute);
			if (value != null)
				copyManyAttribute(attribute, value, aRefObject, idSuffix, copyRef);
		} else if (aRefObject.eIsSet(attribute)) {
			Object value = aRefObject.eGet(attribute);
			if (value == null)
				copyRef.eSet(attribute, value);
			else
				copySingleAttribute(attribute, value, aRefObject, idSuffix, copyRef);
		} else if (attribute == METAENAME) {
			//set name to the ID (this is computed as a default), only get here if isSetName is
			// false.
			copyRef.eSet(attribute, aRefObject.eGet(attribute));
		}
	}

	/**
	 * Iterate over the attributes of the receiver and copy each attribute.
	 */
	protected void copyCurrentAttributes(EObject aRefObject, String idSuffix, EObject copyRef) {
		List attributes = aRefObject.eClass().getEAllAttributes();
		if (attributes != null) {
			Iterator it = attributes.iterator();
			EAttribute ra;
			while (it.hasNext()) {
				ra = (EAttribute) it.next();
				copyAttribute(ra, aRefObject, idSuffix, copyRef);
			}
		}
	}

	/**
	 * This method will iterate over the references of the receiver. If a reference's value is not
	 * <code>null</code>, then a series of tests are done before setting the value with the
	 * copied object, <code>copyRef</code>.
	 * 
	 * Tests: 1. Do nothing if the reference equals the metaEContainer or metaEContains references.
	 */

	protected void copyCurrentReferences(EObject aRefObject, String idSuffix, EObject copyRef) {
		List references = aRefObject.eClass().getEAllReferences();
		if (references != null) {
			Iterator it = references.iterator();
			EReference rr;
			while (it.hasNext()) {
				rr = (EReference) it.next();
				if (shouldCopyReference(rr))
					copyReference(rr, aRefObject, idSuffix, copyRef);
			}
		}
	}

	/**
	 * Iterate over <code>anExtent</code> and copy each element to <code>copyExtent</code>.
	 */
	protected void copyExtent(EList anExtent, EList copyExtent) {
		Iterator it = anExtent.iterator();
		EObject refObject;
		while (it.hasNext()) {
			refObject = (EObject) it.next();
			copyExtent.add(containmentCopy(refObject, null));
		}
	}

	/**
	 * Copy a many value attribute which is treated as a many valued reference.
	 * 
	 * Tests:
	 * 
	 * 1. If the type is <bold>not </bold> <code>null</code>, then set the value on
	 * <code>copyRef</code> without making a copy. 2. If the type is <code>null</code>, then
	 * obtain the list from <code>copyRef</code> and add a copy of each value from the original
	 * list to it.
	 */

	protected void copyManyAttribute(EAttribute attribute, List aValue, EObject aRefObject, String idSuffix, EObject copyRef) {
		if (attribute.getEType() == null)
			copyRef.eSet(attribute, aValue);
		else {
			List copyList = (List) copyRef.eGet(attribute);
			Iterator it = aValue.iterator();
			Object next;
			while (it.hasNext()) {
				next = it.next();
				if (next instanceof EObject)
					copyList.add(containmentCopy((EObject) next, idSuffix));
				else
					copyList.add(next);
			}
		}
	}

	/**
	 * This method will iterate over the references of the receiver. If a reference's value is not
	 * <code>null</code>, then a series of tests are done before setting the value with the
	 * copied object, <code>copyRef</code>.
	 * 
	 * Tests: 1. If the reference is many, obtain the list from the receiving object and make a copy
	 * of each value if the reference is composite before adding to the list; otherwise, just add
	 * each value to the of the <code>copyRef</code>.
	 */

	protected void copyManyReference(EReference aReference, List aList, EObject aRefObject, String idSuffix, EObject copyRef) {
		if (!aList.isEmpty()) {
			if (aReference.isContainment()) {
				List copyList = (List) copyRef.eGet(aReference);
				Iterator it = aList.iterator();
				EObject targetValue;
				while (it.hasNext()) {
					targetValue = (EObject) it.next();
					copyList.add(containmentCopy(targetValue, idSuffix));
				}
			} else
				addDeferredManyReferenceCopy(aReference, aList, idSuffix, copyRef);
		}
	}

	/**
	 * This method will iterate over the references of the receiver. If a reference's value is not
	 * <code>null</code>, then perform the copy.
	 */

	protected void copyReference(EReference aReference, EObject aRefObject, String idSuffix, EObject copyRef) {
		if (aReference.isMany()) {
			List value = (List) aRefObject.eGet(aReference);
			if (value != null)
				copyManyReference(aReference, value, aRefObject, idSuffix, copyRef);
		} else if (aRefObject.eIsSet(aReference)) {
			Object value = aRefObject.eGet(aReference);
			copySingleReference(aReference, (EObject) value, aRefObject, idSuffix, copyRef);
		}
	}

	/**
	 * Copy all RefObjects from <code>aGroup</code> and add the copy back to <code>aGroup</code>.
	 */
	protected void copyRefObjects(CopyGroup aGroup) {
		Association association;
		EObject refObject;
		String idSuffix;
		if (aGroup.primGetRefObjects() != null) {
			Iterator it = aGroup.primGetRefObjects().iterator();
			while (it.hasNext()) {
				association = (Association) it.next();
				refObject = (EObject) association.getKey();
				idSuffix = (String) association.getValue();
				if (idSuffix == null)
					idSuffix = aGroup.getDefaultIdSuffix();
				aGroup.addCopied(containmentCopy(refObject, idSuffix));
			}
		}
	}

	/**
	 * Copy all Resources from <code>aGroup</code> and add the copy back to <code>aGroup</code>.
	 */
	protected void copyResources(CopyGroup aGroup) {
		Association association;
		Resource resource;
		String uri;
		if (aGroup.primGetResources() != null) {
			Iterator it = aGroup.primGetResources().iterator();
			while (it.hasNext()) {
				association = (Association) it.next();
				resource = (Resource) association.getKey();
				uri = (String) association.getValue();
				Resource copied = containmentCopy(resource, uri);
				copyModificationFlag(resource, copied);
				aGroup.addCopied(copied);
			}
		}
	}

	/**
	 * Copy a single value attribute.
	 * 
	 * Tests:
	 * 
	 * 1. If an attribute type is not <code>null</code> then it is an object type and it must be
	 * copied. Do not copy the value if the attribute is an Enumeration type. 2. If an attribute
	 * type is <bold>not </bold> <code>null</code> then copy the value before setting it on
	 * <code>copyRef</code>.
	 */

	protected void copySingleAttribute(EAttribute attribute, Object aValue, EObject aRefObject, String idSuffix, EObject copyRef) {
		if (attribute.getEType() == null) {
			copyRef.eSet(attribute, aValue);
		} else {
			//MOF108
			// if (attribute.isObjectType())
			if (attribute.getEType() instanceof EClass)
				copyRef.eSet(attribute, containmentCopy((EObject) aValue, idSuffix));
			else
				copyRef.eSet(attribute, aValue);
		}
	}

	/**
	 * This method will iterate over the references of the receiver. If a reference's value is not
	 * <code>null</code>, then a series of tests are done before setting the value with the
	 * copied object, <code>copyRef</code>.
	 * 
	 * Tests: 1. If the reference is <bold>not </bold> many, make a copy of the value if the
	 * reference is composite before setting the value with <code>copyRef</code>; otherwise, just
	 * set the value as is.
	 */

	protected void copySingleReference(EReference aReference, EObject aValue, EObject aRefObject, String idSuffix, EObject copyRef) {
		//Exceptional case; the relationship is not the container relationship
		//And is not navigable in the other direction; go ahead and copy
		if (aValue != aRefObject.eContainer() || aReference.getEOpposite() == null) {
			if (aReference.isContainment())
				copyRef.eSet(aReference, containmentCopy(aValue, idSuffix));
			else
				addDeferredSingleReferenceCopy(aReference, aValue, idSuffix, copyRef);
		}
	}

	/**
	 * Copy all Resources and RefObjects within <code>aGroup</code> and add them to
	 * <code>aGroup</code>. Non composite references will be deferred until all objects are
	 * copied from <code>aGroup</code>.
	 * 
	 * Copy Resources first and then copy RefObjects.
	 */
	public static void createCopy(CopyGroup aGroup) {
		EtoolsCopyUtility utility = new EtoolsCopyUtility();
		utility.copy(aGroup);
	}

	/**
	 * Copies attributes
	 */
	public static void createAttributeCopy(EObject aRefObject, EObject copyObject) {
		EtoolsCopyUtility utility = new EtoolsCopyUtility();
		utility.copyCurrentAttributes(aRefObject, null, copyObject);
	}

	/**
	 * Copies all set attributes and references based on containment. Thus, it will copy the entire
	 * containment tree. Use the idSuffix to define the suffix that will be appended to the existing
	 * IDs of copied objects.
	 */
	public static EObject createCopy(EObject aRefObject) {
		return createCopy(aRefObject, null);
	}

	/**
	 * Copies all set attributes and references based on containment. Thus, it will copy the entire
	 * containment tree. Use the idSuffix to define the suffix that will be appended to the existing
	 * IDs of copied objects.
	 */
	public static EObject createCopy(EObject aRefObject, String idSuffix) {
		EtoolsCopyUtility utility = new EtoolsCopyUtility();
		return utility.copy(aRefObject, idSuffix);
	}

	/**
	 * Copy <code>aResource</code> using <code>newUri</code> for the URI.
	 */
	public static Resource createCopy(Resource aResource, String newUri) {
		EtoolsCopyUtility utility = new EtoolsCopyUtility();
		return utility.copy(aResource, newUri);
	}

	protected void executeDeferredCopyActions() {
		if (primGetDeferredReferenceCopies() != null) {
			Iterator it = primGetDeferredReferenceCopies().iterator();
			DeferredReferenceUtilityAction action;
			while (it.hasNext()) {
				action = (DeferredReferenceUtilityAction) it.next();
				action.performAction();
			}
			primGetDeferredReferenceCopies().clear();
		}
	}

	/**
	 * Insert the method's description here. Creation date: (12/13/2000 9:17:35 PM)
	 * 
	 * @return java.util.HashMap
	 */
	protected java.util.HashMap getCopiedObjects() {
		if (copiedObjects == null)
			copiedObjects = new HashMap(200);
		return copiedObjects;
	}

	protected java.util.HashMap getCopiedAdapters() {
		if (copiedAdapters == null)
			copiedAdapters = new HashMap(200);
		return copiedAdapters;
	}

	/**
	 * Return a cached copy.
	 */
	public EObject getCopy(EObject anObject) {
		if (anObject == null)
			return null;
		return (EObject) getCopiedObjects().get(anObject);
	}

	/**
	 * Return a cached copy, if none, return the argument.
	 */
	public EObject getCopyIfFound(EObject anObject) {
		EObject copied = getCopy(anObject);
		return copied == null ? anObject : copied;
	}

	/**
	 * Insert the method's description here. Creation date: (12/16/2000 9:11:32 AM)
	 * 
	 * @return java.util.List
	 */
	protected java.util.List getDeferredReferenceCopies() {
		if (deferredReferenceCopies == null)
			deferredReferenceCopies = new ArrayList(100);
		return deferredReferenceCopies;
	}

	/**
	 * Return an instance of EObject that is the same type as <code>aRefObject</code>.
	 */
	public EObject newInstance(EObject aRefObject) {
		if (aRefObject == null)
			return null;
		EPackage epkg = (EPackage) aRefObject.eClass().eContainer();
		EFactory factory = epkg.getEFactoryInstance();
		EClass refObj = aRefObject.eClass();
		return factory.create(refObj);
	}

	/**
	 * Return an instance of Resource that uses the same ResourceFactory as the one used by
	 * <code>aResource</code>. If <code>newUri</code> is <code>null</code> then use the URI
	 * from <code>aResource</code>.
	 */
	public Resource newInstance(Resource aResource, String newUri) {
		if (aResource == null)
			return null;
		Resource newResource;
		String originalUri = aResource.getURI().toString();
		newUri = newUri == null ? originalUri : newUri;

		Resource.Factory factory = null;
		if (aResource.getResourceSet() == null)
			factory = Resource.Factory.Registry.INSTANCE.getFactory(URI.createURI(originalUri));
		else
			factory = aResource.getResourceSet().getResourceFactoryRegistry().getFactory(URI.createURI(originalUri));


		newResource = factory.createResource(URI.createURI(newUri));
		return newResource;
	}

	/**
	 * Should the id be copied in the case where no suffix is specified? Defaults to false
	 */
	public boolean preserveIds() {
		return preserveIds;
	}

	/**
	 * Copies all set attributes and references based on containment. Thus, it will copy the entire
	 * containment tree. Use the idSuffix to define the suffix that will be appended to the existing
	 * IDs of copied objects. If an idSuffix does not exist, do not set an id on the copied object.
	 */
	protected EObject primCopy(EObject aRefObject, String idSuffix) {
		if (aRefObject == null)
			return null;
		EObject copyRef = primCopyObject(aRefObject, idSuffix);

		copyCurrentReferences(aRefObject, idSuffix, copyRef);
		return copyRef;
	}

	/**
	 * Copies all set attributes based on containment. Thus, it will copy the entire containment
	 * tree. Use the idSuffix to define the suffix that will be appended to the existing IDs of
	 * copied objects. If an idSuffix does not exist, do not set an id on the copied object.
	 */
	protected EObject primCopyObject(EObject aRefObject, String idSuffix) {
		if (aRefObject == null)
			return null;
		EObject copyRef = newInstance(aRefObject);

		recordCopy(aRefObject, copyRef);
		copyCurrentAttributes(aRefObject, idSuffix, copyRef);
		if (isCopyAdapters())
			copyAdapters(aRefObject, copyRef);
		return copyRef;
	}

	protected void copyAdapters(EObject aRefObject, EObject copyRef) {
		List adapters = aRefObject.eAdapters();
		for (int i = 0; i < adapters.size(); i++) {
			Adapter adapter = (Adapter) adapters.get(i);
			copyAdapter(aRefObject, copyRef, adapter);

		}
	}

	protected void copyAdapter(EObject aRefObject, EObject copyRef, Adapter adapter) {
		if (!(adapter instanceof CloneablePublic))
			return;
		CloneablePublic copyAdapter = (CloneablePublic) getCopiedAdapters().get(adapter);
		if (copyAdapter == null) {
			copyAdapter = (CloneablePublic) ((CloneablePublic) adapter).clone();
			getCopiedAdapters().put(adapter, copyAdapter);
		}
		if (copyAdapter != null)
			copyRef.eAdapters().add(copyAdapter);
	}

	/**
	 * Copies all set attributes and references based on containment. Thus, it will copy the entire
	 * containment tree. Use the idSuffix to define the suffix that will be appended to the existing
	 * IDs of copied objects.
	 */
	protected Resource primCopy(Resource aResource, String newUri) {
		if (aResource == null)
			return null;
		Resource copyResource = newInstance(aResource, newUri);
		copyExtent(aResource.getContents(), copyResource.getContents());
		return copyResource;
	}

	protected void copyModificationFlag(Resource aResource, Resource copied) {
		if (aResource.isModified())
			copied.setModified(true);
	}

	/**
	 * Insert the method's description here. Creation date: (12/16/2000 9:11:32 AM)
	 * 
	 * @return java.util.List
	 */
	private java.util.List primGetDeferredReferenceCopies() {
		return deferredReferenceCopies;
	}

	public void recordCopy(EObject aSource, EObject aCopy) {
		getCopiedObjects().put(aSource, aCopy);
	}

	public void recordCopy(Adapter aSource, Adapter aCopy) {
		getCopiedAdapters().put(aSource, aCopy);
	}

	/**
	 * Should the id be copied in the case where no suffix is specified?
	 */
	public void setPreserveIds(boolean value) {
		preserveIds = value;
	}

	protected boolean shouldCopyReference(EReference aReference) {
		return aReference.isChangeable();
	}

	/**
	 * @return
	 */
	public boolean isCopyAdapters() {
		return copyAdapters;
	}

	/**
	 * Flag used to indicate whether adapters should be copied as well. In order for an adapter to
	 * be copied, it must implement the {@link CloneablePublic}interface.
	 */
	public void setCopyAdapters(boolean b) {
		copyAdapters = b;
	}

}


