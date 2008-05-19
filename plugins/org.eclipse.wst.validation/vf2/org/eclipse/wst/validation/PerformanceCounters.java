/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation;

import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.validation.internal.Misc;
import org.eclipse.wst.validation.internal.ValMessages;

/**
 * Some performance information for a validation invocation. This object is immutable.
 * <p>
 * <b>Provisional API:</b> This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 * </p>
 * @noextend
 * @author karasiuk
 *
 */
public class PerformanceCounters {
	
	private long	_when;
	private String 	_validatorId;
	private String	_validatorName;
	private String	_resourceName;
	private int		_numberInvocations;
	private	long	_elapsedTime;
	private long	_cpuTime;
	
	/**
	 * @param validatorId
	 * @param validatorName
	 * @param resourceName 
	 * @param numberInvocations number of times the validator was invoked
	 * @param elapsedTime elapsed time in milliseconds
	 * @param cpuTime CPU time in nanoseconds
	 */
	public PerformanceCounters(String validatorId, String validatorName, String resourceName, 
		int numberInvocations, long elapsedTime, long cpuTime){
		
		_when = System.currentTimeMillis();
		_validatorId = validatorId;
		_validatorName = validatorName;
		_resourceName = resourceName;
		_numberInvocations = numberInvocations;
		_elapsedTime = elapsedTime;
		_cpuTime = cpuTime;
	}
	
	public String getValidatorId() {
		return _validatorId;
	}
	public int getNumberInvocations() {
		return _numberInvocations;
	}
	
	/**
	 * Answer the elapsed time in milliseconds. 
	 */
	public long getElapsedTime() {
		return _elapsedTime;
	}
	
	/**
	 * Answer the amount of CPU time in nanoseconds. If this can not be determined,
	 * answer -1.
	 */
	public long getCpuTime() {
		return _cpuTime;
	}
	
	@Override
	public String toString() {
		return NLS.bind(ValMessages.LogValEndTime,	
			new Object[]{_validatorName, _validatorId, _resourceName, Misc.getTimeMS(_elapsedTime)});
	}
	
	public String toString(boolean asSummary){
		if (asSummary){
			if (_cpuTime != -1)return NLS.bind(ValMessages.LogValSummary2, 
				new Object[]{_validatorName, _validatorId, _numberInvocations, 
					Misc.getTimeMS(_elapsedTime), Misc.getTimeNano(_cpuTime)});
			
			return NLS.bind(ValMessages.LogValSummary, 
					new Object[]{_validatorName, _validatorId, _numberInvocations, Misc.getTimeMS(_elapsedTime)});
		}
		return toString();
	}

	public String getValidatorName() {
		return _validatorName;
	}

	/** 
	 * Answer when was the event logged. 
	 * 
	 * @see System#currentTimeMillis()
	 */
	public long getWhen() {
		return _when;
	}

	public String getResourceName() {
		return _resourceName;
	}

}
