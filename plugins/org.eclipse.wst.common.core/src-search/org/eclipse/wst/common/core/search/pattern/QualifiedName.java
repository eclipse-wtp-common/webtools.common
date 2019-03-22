/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.common.core.search.pattern;

/**
 * This data class represents a qualified name, consisting of a local name and a
 * qualifier
 * 
 * <p>
 * <b>Note:</b> This class/interface is part of an interim API that is still under development and expected to
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
 * (repeatedly) as the API evolves.
 * </p>
 */
// issue (cs) should this go in the 'matching' package (BTW... rename
// matching->pattern)
public class QualifiedName
{

	private String qualifier;

	private String name;

	private static final String NS_DELIM_1 = "{"; //$NON-NLS-1$

	private static final String NS_DELIM_2 = "}"; //$NON-NLS-1$

	/**
	 * Constructor Creates a qualified name from a namespace and local name.
	 * 
	 * @param namespace
	 * @param localName
	 */
	public QualifiedName(String namespace, String localName)
	{

		super();

		this.qualifier = namespace;
		if (namespace != null && namespace.length() == 0)
		{
			this.qualifier = null;
		}
		if (this.qualifier != null && this.qualifier.length() == 0)
		{
			this.qualifier = null;
		}

		this.name = localName;
		if (localName != null && localName.length() == 0)
		{
			this.name = null;
		}

	}

	/**
	 * Returns the namespace component of the qualified name.
	 * 
	 * @return The namespace; <code>null</code> if none is specified
	 */
	public String getNamespace()
	{
		return this.qualifier;
	}

	/**
	 * Returns the local name component of the qualified name.
	 * 
	 * @return The local name; <code>null</code> if none is specified
	 */
	public String getLocalName()
	{
		return this.name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		boolean isEqual = false;
		if (this == obj)
		{
		  isEqual = true;
		}
		else
		{	
		  if (obj instanceof QualifiedName)
		  {
			QualifiedName that = (QualifiedName) obj;
		    isEqual = isMatch(this.getNamespace(), that.getNamespace()) &&
		              isMatch(this.getLocalName(), that.getLocalName());	
		  }  
		}
		return isEqual;
	}
	
	protected boolean isMatch(String a, String b)
	{
	  return a != null ? a.equals(b) : a == b;
	}	

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		int hash = 0;
		if (this.qualifier != null)
		{
			hash += this.qualifier.hashCode();
		}
		if (this.name != null)
		{
			hash += this.name.hashCode();
		}
		return hash;
	}

	/**
	 * Returns a string representation of the qualified name, of the form:
	 * {namespace}localname. If no namespace is specified, the string has the
	 * form: {}localname. Note that the string format may change in the future.
	 * 
	 * @return The string value
	 */
	public String toString()
	{

		return appendStrings(new String[]
		{ NS_DELIM_1, // {
				this.qualifier, NS_DELIM_2, // }
				this.name });

	}

	/**
	 * Factory-like method to create a QName object from the string form of a
	 * QName. The string must have the same format as returned by
	 * QName.toString().
	 * 
	 * @param qnameString -
	 *            String form of a QName
	 * @return The created QName object created from the specified string
	 * @throws IllegalArgumentException -
	 *             Missing namespace delimiters
	 */
	public static QualifiedName valueOf(String qnameString)
			throws IllegalArgumentException
	{

		String namespace = null;
		String localName = null;
		if (qnameString == null || qnameString.length() == 0)
		{
			// Both namespace and local name are null.
		} else if (qnameString.startsWith(NS_DELIM_1))
		{
			// The QName has the notation specifying a namespace.
			int index = qnameString.indexOf(NS_DELIM_2);
			if (index == -1)
			{
				// The end delimiter for the namespace was not found. The QName
				// string
				// is malformed.
				throw new IllegalArgumentException(
						"qnameString = " + qnameString); //$NON-NLS-1$
			}
			namespace = qnameString.substring(1, index);
			localName = qnameString.substring(index + 1);
		} else
		{
			// Assume no namespace is specified and the string is a local name.
			localName = qnameString;
		}
		return new QualifiedName(namespace, localName);

	}

	/**
	 * Creates a single string by appending together an array of strings,
	 * skipping null strings.
	 * 
	 * @param strings -
	 *            Strings to be appended together
	 * @return Resulting string
	 */
	public static String appendStrings(String[] strings)
	{

		String result = null;
		if (strings != null)
		{
			StringBuffer tokenBuffer = new StringBuffer();
			int maxCount = strings.length;
			String string;
			for (int i = 0; i < maxCount; i++)
			{
				string = strings[i];
				if (string != null && string.length() > 0)
				{
					tokenBuffer.append(string);
				}// if
			}// for
			result = tokenBuffer.toString();
		}// if
		return result;

	}// appendStrings()

}// class QName
