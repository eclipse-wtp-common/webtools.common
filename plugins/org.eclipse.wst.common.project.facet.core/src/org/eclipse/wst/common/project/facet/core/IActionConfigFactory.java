package org.eclipse.wst.common.project.facet.core;

import org.eclipse.core.runtime.CoreException;

/**
 * <p><i>This class is part of an interim API that is still under development 
 * and expected to change significantly before reaching stability. It is being 
 * made available at this early stage to solicit feedback from pioneering 
 * adopters on the understanding that any code that uses this API will almost 
 * certainly be broken (repeatedly) as the API evolves.</i></p>
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public interface IActionConfigFactory
{
    Object create()
    
        throws CoreException;
    
}
