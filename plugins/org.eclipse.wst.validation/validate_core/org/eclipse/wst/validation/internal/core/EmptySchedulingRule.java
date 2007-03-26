package org.eclipse.wst.validation.internal.core;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

public class EmptySchedulingRule implements ISchedulingRule {
		
    public boolean contains(ISchedulingRule rule) 
    {
         return rule == this;
    }

    public boolean isConflicting(ISchedulingRule rule) {
         return rule == this;
    }
}
