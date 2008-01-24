package org.eclipse.wst.validation;

import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.validation.internal.Misc;
import org.eclipse.wst.validation.internal.ValMessages;

/**
 * Some performance information for a validation invocation. This object is immutable.
 * 
 * @author karasiuk
 *
 */
public class PerformanceCounters {
	
	private String 	_validatorId;
	private String	_validatorName;
	private String	_resourceName;
	private int		_numberInvocations;
	private	long	_elapsedTime;
	private long	_cpuTime;
	
	public PerformanceCounters(String validatorId, String validatorName, String resourceName, 
		int numberInvocations, long elapsedTime, long cpuTime){
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
	 * Answer the amount of CPU time in milliseconds. If this can not be determined,
	 * answer -1;
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

}
