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

import junit.framework.*;
import org.neurpheus.nlp.myspell.AffixRule;

/**
 *  Tests AffixRule class.
 *
 * @author Jakub Strychowski
 */
public class AffixRuleTest extends TestCase {
    
    public AffixRuleTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(AffixRuleTest.class);
        
        return suite;
    }

    public void testGetChangeFrom() {
        try {
            AffixRule ar = new AffixRule(false, 'A', "SFX A sc slem asc", 1);
            assertEquals("sc", ar.getChangeFrom().toString());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void testSetChangeFrom() {
        try {
            AffixRule ar = new AffixRule(false, 'A', "SFX A sc slem asc", 1);
            ar.setChangeFrom("abc");
            assertEquals("abc", ar.getChangeFrom().toString());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void testGetChangeTo() {
        try {
            AffixRule ar = new AffixRule(false, 'A', "SFX A sc slem asc", 1);
            assertEquals("slem", ar.getChangeTo().toString());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void testSetChangeTo() {
        try {
            AffixRule ar = new AffixRule(false, 'A', "SFX A sc slem asc", 1);
            ar.setChangeTo("def");
            assertEquals("def", ar.getChangeTo().toString());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void testGetChangeCondition() {
        try {
            AffixRule ar = new AffixRule(false, 'A', "SFX A sc slem asc", 1);
            assertEquals("asc", ar.getChangeCondition().toString());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void testSetChangeCondition() {
        try {
            AffixRule ar = new AffixRule(false, 'A', "SFX A sc slem asc", 1);
            ar.setChangeCondition("123");
            assertEquals("123", ar.getChangeCondition().toString());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void testCheckCondition() {
        try {
            AffixRule ar;
            ar = new AffixRule(false, 'A', "SFX A sc slem [^w]asc", 1);
            assertTrue(ar.checkCondition("krasc"));
            assertFalse(ar.checkCondition("wasc"));
            assertFalse(ar.checkCondition("milosc"));
            ar = new AffixRule(true, 'B', "PFX B nie 0 nie(([^b])|(.[^o]))", 1);
            assertTrue(ar.checkCondition("niewysoki"));
            assertTrue(ar.checkCondition("niewysoki"));
            assertFalse(ar.checkCondition("niebo"));
            assertFalse(ar.checkCondition("nieboski"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void testIsPrefix() {
        try {
            AffixRule ar;
            ar = new AffixRule(false, 'A', "SFX A sc slem [^w]asc", 1);
            assertFalse(ar.isPrefix());
            ar = new AffixRule(true, 'A', "SFX A sc slem [^w]asc", 1);
            assertTrue(ar.isPrefix());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void testSetPrefix() {
        try {
            AffixRule ar;
            ar = new AffixRule(false, 'A', "SFX A sc slem [^w]asc", 1);
            assertFalse(ar.isPrefix());
            ar.setPrefix(true);
            assertTrue(ar.isPrefix());
            ar.setPrefix(false);
            assertFalse(ar.isPrefix());
            ar = new AffixRule(true, 'A', "SFX A sc slem [^w]asc", 1);
            assertTrue(ar.isPrefix());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void testGenerateForm() {
        try {
            AffixRule ar;
            ar = new AffixRule(false, 'A', "SFX A sc slem [^w]asc", 1);
            assertEquals("abcslem", ar.generateForm("abcsc"));
            assertEquals("slem", ar.generateForm("sc"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
}
