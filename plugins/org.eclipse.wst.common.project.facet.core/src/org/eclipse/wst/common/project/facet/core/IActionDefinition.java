/******************************************************************************
 * Copyright (c) 2006 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core;

import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;

/**
 * An action is an operation on a single facet within a faceted project to
 * install, uninstall, or change the version of the facet. An action definition
 * represents the information supplied by the facet author regarding the
 * implementation of an action. A single action definition can apply to multiple
 * facet versions. For instance, the facet author may choose to supply one
 * action definition for all versions of his facet. 
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public interface IActionDefinition
{
    String getId();
    IProjectFacet getProjectFacet();
    IVersionExpr getVersionExpr();
    Action.Type getActionType();
}