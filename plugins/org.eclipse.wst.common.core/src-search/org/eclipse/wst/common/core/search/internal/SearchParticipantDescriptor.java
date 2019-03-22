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

package org.eclipse.wst.common.core.search.internal;

import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.ExpressionConverter;
import org.eclipse.core.expressions.ExpressionTagNames;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.Assert; 
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.common.core.search.SearchParticipant;

public class SearchParticipantDescriptor
{

	private IConfigurationElement fElement;

	private SearchParticipant participant;

	public SearchParticipantDescriptor(IConfigurationElement element)
	{
		this.fElement = element;
	}

	public SearchParticipant getSearchParticipant()
	{
		if (participant == null)
		{
			try
			{
				participant = (SearchParticipant) fElement
						.createExecutableExtension("class"); //$NON-NLS-1$
			} catch (Exception e)
			{
				// e.printStackTrace();
			}
		}
		return participant;
	}

	public boolean matches(IEvaluationContext context) throws CoreException
	{
		IConfigurationElement[] elements = fElement
				.getChildren(ExpressionTagNames.ENABLEMENT);
		if (elements.length == 0)
			return false;
		Assert.isTrue(elements.length == 1);
		Expression exp = ExpressionConverter.getDefault().perform(elements[0]);
		return convert(exp.evaluate(context));
	}

	private boolean convert(EvaluationResult eval)
	{
		if (eval == EvaluationResult.FALSE)
			return false;
		return true;
	}

	/**
	 * @deprecated No replacement
	 */
	public String[] getSupportedContentTypes()
	{
		return new String[0];
	}

	/**
	 * @deprecated No replacement
	 */
	public void addSupportedContentTypeId(String contentTypeId)
	{
	}
	
	public String getElementId(){
		return fElement.getAttribute("id");
		
	}
}