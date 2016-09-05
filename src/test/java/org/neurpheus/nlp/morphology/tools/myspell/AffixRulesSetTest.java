/*
 * Neurpheus - MySpell Reader
 *
 * Copyright (C) 2006 Jakub Strychowski
 *
 *  This library is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as published by the Free
 *  Software Foundation; either version 2.1 of the License, or (at your option)
 *  any later version.
 *
 *  This library is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 *  for more details.
 */
package org.neurpheus.nlp.morphology.tools.myspell;

import java.util.ArrayList;
import junit.framework.*;
import org.neurpheus.nlp.myspell.AffixRule;
import org.neurpheus.nlp.myspell.AffixRulesSet;
import org.neurpheus.nlp.myspell.MySpellSyntaxException;

/**
 *  Tests the AffixRulesSet class.
 *
 * @author Jakub Strychowski
 */
public class AffixRulesSetTest extends TestCase {
    
    public AffixRulesSetTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(AffixRulesSetTest.class);
        return suite;
    }
    
    /**
     * Test of addAffixRule method, of class org.neurpheus.nlp.morphology.tools.myspell.AffixRulesSet.
     */
    public void testAddAffixRule() {
        try {
            AffixRulesSet ars = new AffixRulesSet("SFX A Y 10", 1);
            assertEquals(10, ars.getRulesCount());
            AffixRule ar1 = new AffixRule(false, 'A', "SFX A abc ab ^w", 2);
            AffixRule ar2 = new AffixRule(false, 'A', "SFX A abcd ab ^w", 3);
            ars.addAffixRule(ar1);
            assertTrue(ars.getRules().contains(ar1));
            assertEquals(1, ars.getRulesCount());
            assertEquals(1, ars.getRules().size());
            ars.addAffixRule(ar1);
            assertTrue(ars.getRules().contains(ar1));
            assertEquals(1, ars.getRulesCount());
            assertEquals(1, ars.getRules().size());
            ars.addAffixRule(ar2);
            assertTrue(ars.getRules().contains(ar1));
            assertTrue(ars.getRules().contains(ar2));
            assertEquals(2, ars.getRulesCount());
            assertEquals(2, ars.getRules().size());
        } catch (MySpellSyntaxException e) {
            fail(e.getMessage());
        }
    }
    
    /**
     * Test of setRules method, of class org.neurpheus.nlp.morphology.tools.myspell.AffixRulesSet.
     */
    public void testSetRules() {
        try {
            AffixRulesSet ars = new AffixRulesSet("SFX A Y 10", 1);
            assertEquals(10, ars.getRulesCount());
            AffixRule ar1 = new AffixRule(false, 'A', "SFX A abc ab ^w", 2);
            AffixRule ar2 = new AffixRule(false, 'A', "SFX A abcd ab ^w", 3);
            ArrayList tmp = new ArrayList();
            tmp.add(ar1);
            tmp.add(ar2);
            ars.setRules(tmp);
            assertEquals(2, ars.getRulesCount());
            assertEquals(2, ars.getRules().size());
            assertTrue(ars.getRules().contains(ar1));
            assertTrue(ars.getRules().contains(ar2));
        } catch (MySpellSyntaxException e) {
            fail(e.getMessage());
        }
    }
    
    /**
     * Test of getSymbol method, of class org.neurpheus.nlp.morphology.tools.myspell.AffixRulesSet.
     */
    public void testGetSymbol() {
        try {
            AffixRulesSet ars = new AffixRulesSet("SFX A Y 10", 1);
            assertEquals('A', ars.getSymbol());
            ars.setSymbol('B');
            assertEquals('B', ars.getSymbol());
        } catch (MySpellSyntaxException e) {
            fail(e.getMessage());
        }
    }
    
    
    
    /**
     * Test of setRulesCount method, of class org.neurpheus.nlp.morphology.tools.myspell.AffixRulesSet.
     */
    public void testSetRulesCount() {
        try {
            AffixRulesSet ars = new AffixRulesSet("SFX A Y 10", 1);
            assertEquals(10, ars.getRulesCount());
            ars.setRulesCount(5);
            assertEquals(5, ars.getRulesCount());
            AffixRule ar = new AffixRule(false, 'A', "SFX A abc ab ^w", 2);
            ars.addAffixRule(ar);
            assertEquals(1, ars.getRulesCount());
        } catch (MySpellSyntaxException e) {
            fail(e.getMessage());
        }
    }
    
    /**
     * Test of isPrefix method, of class org.neurpheus.nlp.morphology.tools.myspell.AffixRulesSet.
     */
    public void testIsPrefix() {
        try {
            AffixRulesSet ars = new AffixRulesSet("SFX A Y 10", 1);
            assertFalse(ars.isPrefix());
            ars.setPrefix(true);
            assertTrue(ars.isPrefix());
            ars.setPrefix(false);
            assertFalse(ars.isPrefix());
            ars = new AffixRulesSet("PFX A Y 10", 1);
            assertTrue(ars.isPrefix());
            ars.setPrefix(false);
            assertFalse(ars.isPrefix());
            ars.setPrefix(true);
            assertTrue(ars.isPrefix());
        } catch (MySpellSyntaxException e) {
            fail(e.getMessage());
        }
    }
    
    
    /**
     * Test of isSuffix method, of class org.neurpheus.nlp.morphology.tools.myspell.AffixRulesSet.
     */
    public void testIsSuffix() {
        try {
            AffixRulesSet ars = new AffixRulesSet("PFX A Y 10", 1);
            assertFalse(ars.isSuffix());
            ars.setSuffix(true);
            assertTrue(ars.isSuffix());
            ars.setSuffix(false);
            assertFalse(ars.isSuffix());
            ars = new AffixRulesSet("SFX A Y 10", 1);
            assertTrue(ars.isSuffix());
            ars.setSuffix(false);
            assertFalse(ars.isSuffix());
            ars.setSuffix(true);
            assertTrue(ars.isSuffix());
        } catch (MySpellSyntaxException e) {
            fail(e.getMessage());
        }
    }
    
    
    /**
     * Test of isUseWithPrefixes method, of class org.neurpheus.nlp.morphology.tools.myspell.AffixRulesSet.
     */
    public void testIsUseWithPrefixes() {
        try {
            AffixRulesSet ars = new AffixRulesSet("SFX A N 10", 1);
            assertFalse(ars.isUseWithPrefixes());
            ars.setUseWithPrefixes(true);
            assertTrue(ars.isUseWithPrefixes());
            ars.setUseWithPrefixes(false);
            assertFalse(ars.isUseWithPrefixes());
            ars = new AffixRulesSet("SFX A Y 10", 1);
            assertTrue(ars.isUseWithPrefixes());
            ars.setUseWithPrefixes(false);
            assertFalse(ars.isUseWithPrefixes());
            ars.setUseWithPrefixes(true);
            assertTrue(ars.isUseWithPrefixes());
        } catch (MySpellSyntaxException e) {
            fail(e.getMessage());
        }
    }
    
    
}
