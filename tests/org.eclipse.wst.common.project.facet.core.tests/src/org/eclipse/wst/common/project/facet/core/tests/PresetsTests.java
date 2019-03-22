/******************************************************************************
 * Copyright (c) 2005-2007 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *    Konstantin Komissarchik
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.tests;

import static org.eclipse.wst.common.project.facet.core.tests.BasicTests.f1v121;
import static org.eclipse.wst.common.project.facet.core.tests.BasicTests.f1v20;
import static org.eclipse.wst.common.project.facet.core.tests.BasicTests.f2extv10;
import static org.eclipse.wst.common.project.facet.core.tests.BasicTests.f2v35a;
import static org.eclipse.wst.common.project.facet.core.tests.BasicTests.f3av10;
import static org.eclipse.wst.common.project.facet.core.tests.BasicTests.f3bv10;
import static org.eclipse.wst.common.project.facet.core.tests.BasicTests.f3cv10;
import static org.eclipse.wst.common.project.facet.core.tests.support.TestUtils.asSet;

import java.util.Collections;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.IDynamicPreset;
import org.eclipse.wst.common.project.facet.core.IPreset;
import org.eclipse.wst.common.project.facet.core.IPresetFactory;
import org.eclipse.wst.common.project.facet.core.PresetDefinition;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class PresetsTests

    extends TestCase
    
{
    private PresetsTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "Presets Tests" );

        suite.addTest( new PresetsTests( "testSimpleStaticPresets" ) );
        suite.addTest( new PresetsTests( "testDerivedStaticPresets" ) );
        suite.addTest( new PresetsTests( "testDynamicPresets" ) );
        suite.addTest( new PresetsTests( "testStaticPresetsExtendingDynamicPresets1" ) );
        suite.addTest( new PresetsTests( "testStaticPresetsExtendingDynamicPresets2" ) );
        suite.addTest( new PresetsTests( "testUserDefinedPresets" ) );
        
        return suite;
    }
    
    public void testSimpleStaticPresets()
    {
        assertTrue( ProjectFacetsManager.isPresetDefined( "pt_preset1" ) );
        final IPreset preset1 = ProjectFacetsManager.getPreset( "pt_preset1" );
        assertTrue( ProjectFacetsManager.getPresets().contains( preset1 ) );        
        
        assertEquals( preset1.getId(), "pt_preset1" );
        assertEquals( preset1.getType(), IPreset.Type.STATIC );
        assertEquals( preset1.getLabel(), "Preset 1" );
        assertEquals( preset1.getDescription(), "This is the description for the first preset." );
        assertEquals( preset1.getProjectFacets(), asSet( f1v20, f2v35a, f2extv10 ) );
        
        assertTrue( ProjectFacetsManager.isPresetDefined( "pt_preset2" ) );
        final IPreset preset2 = ProjectFacetsManager.getPreset( "pt_preset2" );
        assertTrue( ProjectFacetsManager.getPresets().contains( preset2 ) );        
        
        assertEquals( preset2.getId(), "pt_preset2" );
        assertEquals( preset2.getType(), IPreset.Type.STATIC );
        assertEquals( preset2.getLabel(), "pt_preset2" );
        assertEquals( preset2.getDescription(), "" );
        assertEquals( preset2.getProjectFacets(), asSet( f3av10, f3bv10, f3cv10 ) );
    }

    public void testDerivedStaticPresets()
    {
        assertTrue( ProjectFacetsManager.isPresetDefined( "pt_preset3" ) );
        final IPreset preset3 = ProjectFacetsManager.getPreset( "pt_preset3" );
        assertTrue( ProjectFacetsManager.getPresets().contains( preset3 ) );        
        
        assertEquals( preset3.getId(), "pt_preset3" );
        assertEquals( preset3.getType(), IPreset.Type.STATIC );
        assertEquals( preset3.getProjectFacets(), asSet( f1v121, f2v35a, f2extv10, f3av10 ) );
    }
    
    public void testDynamicPresets()
    {
        assertTrue( ProjectFacetsManager.isPresetDefined( "pt_dyn_preset" ) );
        final IPreset preset = ProjectFacetsManager.getPreset( "pt_dyn_preset" );
        assertTrue( ProjectFacetsManager.getPresets().contains( preset ) );

        assertEquals( preset.getId(), "pt_dyn_preset" );
        assertEquals( preset.getType(), IPreset.Type.DYNAMIC );
        assertEquals( preset.getLabel(), "pt_dyn_preset" );
        assertEquals( preset.getDescription(), "" );
        assertEquals( preset.getProjectFacets(), Collections.emptySet() );
        
        final IDynamicPreset dynamicPreset = (IDynamicPreset) preset;
        final IPreset resolved = dynamicPreset.resolve( Collections.<String,Object>emptyMap() );
        
        assertEquals( resolved.getId(), "pt_dyn_preset" );
        assertEquals( resolved.getType(), IPreset.Type.STATIC );
        assertEquals( resolved.getLabel(), "Dynamic Preset" );
        assertEquals( resolved.getDescription(), "The description of the dynamic preset." );
        assertEquals( resolved.getProjectFacets(), asSet( f1v20, f2extv10, f3cv10 ) );
    }
    
    public void testStaticPresetsExtendingDynamicPresets1()
    {
        assertTrue( ProjectFacetsManager.isPresetDefined( "pt_static_extending_dynamic_preset_1" ) );
        final IPreset preset = ProjectFacetsManager.getPreset( "pt_static_extending_dynamic_preset_1" );
        assertTrue( ProjectFacetsManager.getPresets().contains( preset ) );

        assertEquals( preset.getId(), "pt_static_extending_dynamic_preset_1" );
        assertEquals( preset.getType(), IPreset.Type.DYNAMIC );
        assertEquals( preset.getLabel(), "Static Extending Dynamic Preset" );
        assertEquals( preset.getDescription(), "This is the static-dynamic description." );
        assertEquals( preset.getProjectFacets(), Collections.emptySet() );
        
        final IDynamicPreset dynamicPreset = (IDynamicPreset) preset;
        final IPreset resolved = dynamicPreset.resolve( Collections.<String,Object>emptyMap() );
        
        assertEquals( resolved.getId(), "pt_static_extending_dynamic_preset_1" );
        assertEquals( resolved.getType(), IPreset.Type.STATIC );
        assertEquals( resolved.getLabel(), "Static Extending Dynamic Preset" );
        assertEquals( resolved.getDescription(), "This is the static-dynamic description." );
        assertEquals( resolved.getProjectFacets(), asSet( f1v20, f2extv10, f3cv10, f2v35a, f3bv10 ) );
    }

    public void testStaticPresetsExtendingDynamicPresets2()
    {
        assertTrue( ProjectFacetsManager.isPresetDefined( "pt_static_extending_dynamic_preset_2" ) );
        final IPreset preset = ProjectFacetsManager.getPreset( "pt_static_extending_dynamic_preset_2" );
        assertTrue( ProjectFacetsManager.getPresets().contains( preset ) );

        assertEquals( preset.getId(), "pt_static_extending_dynamic_preset_2" );
        assertEquals( preset.getType(), IPreset.Type.DYNAMIC );
        assertEquals( preset.getLabel(), "Static Extending Dynamic Preset 2" );
        assertEquals( preset.getDescription(), "This is the static-dynamic description 2." );
        assertEquals( preset.getProjectFacets(), Collections.emptySet() );
        
        final IDynamicPreset dynamicPreset = (IDynamicPreset) preset;
        final IPreset resolved = dynamicPreset.resolve( Collections.<String,Object>emptyMap() );
        
        assertEquals( resolved.getId(), "pt_static_extending_dynamic_preset_2" );
        assertEquals( resolved.getType(), IPreset.Type.STATIC );
        assertEquals( resolved.getLabel(), "Static Extending Dynamic Preset 2" );
        assertEquals( resolved.getDescription(), "This is the static-dynamic description 2." );
        assertEquals( resolved.getProjectFacets(), asSet( f1v121, f2extv10, f3cv10, f2v35a, f3bv10, f3av10 ) );
    }
    
    public void testUserDefinedPresets()
    {
        assertFalse( ProjectFacetsManager.isPresetDefined( "pt_user_defined" ) );
        
        final IPreset preset
            = ProjectFacetsManager.definePreset( "pt_user_defined", "the description", 
                                                 asSet( f1v121, f2extv10 ) );
        
        assertTrue( ProjectFacetsManager.isPresetDefined( preset.getId() ) );
        assertTrue( ProjectFacetsManager.getPresets().contains( preset ) );

        assertEquals( preset.getType(), IPreset.Type.USER_DEFINED );
        assertEquals( preset.getLabel(), "pt_user_defined" );
        assertEquals( preset.getDescription(), "the description" );
        assertEquals( preset.getProjectFacets(), asSet( f1v121, f2extv10 ) );
        
        ProjectFacetsManager.deletePreset( preset );
        assertFalse( ProjectFacetsManager.isPresetDefined( "pt_user_defined" ) );
    }
    
    public static final class PresetFactory 
    
        implements IPresetFactory
        
    {
        public PresetDefinition createPreset( final String presetId,
                                              final Map<String,Object> context ) 
        
            throws CoreException
            
        {
            return new PresetDefinition( "Dynamic Preset", "The description of the dynamic preset.",
                                         asSet( f1v20, f2extv10, f3cv10 ) );
        }
    }
    
}
