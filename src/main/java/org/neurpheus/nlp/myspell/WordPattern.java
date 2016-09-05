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
package org.neurpheus.nlp.myspell;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Represents a definition of a single word readed from the MySpell words definition file (*.dic file).
 *
 * <p>
 * Each definition of a word consist of the base form of this word and a sequence of affix rule set symbols.
 * All forms of the word may be produced through the application of the rules from the rule sets.
 * </p>
 * <p>
 * This class produces all forms of the word represented by a definition.
 * </p>
 *
 * @author Jakub Strychowski
 */
public class WordPattern {
 
    /** Holds base form of the word. */
    private String baseForm;
    
    /** Holds symbols representing affix rule sets which apply to the base form. */
    private char[] rulesSymbols;
    
    /**
     * Creates a new instance of WordPattern.
     *
     * @param   base        The base form of a word.
     * @param   symbols     The sequence of characters representing symbols of rule sets.
     */
    public WordPattern(final String base, final char[] symbols) {
        this.baseForm = baseForm;
        this.rulesSymbols = symbols;
    }
    
    /**
     * Creates a new instance fo WordPattern parsing given line from the *.dic file.
     * 
     * @param   line    The line which defines the word pattern.
     *
     * @throws MySpellSyntaxException if any error occurred while parsing.
     */
    public WordPattern(final String line) throws MySpellSyntaxException {
        int pos = line.indexOf('/');
        if (pos > 0) {
            this.baseForm = line.substring(0, pos);
            this.rulesSymbols = line.substring(pos + 1).toCharArray();
        } else {
            this.baseForm = line;
            this.rulesSymbols = null;
        }
    }
    
    /**
     *  Returns base form of this word pattern.
     *
     *  @return The base form.
     */
    public String getBaseForm() {
        return baseForm;
    }

    /**
     *  Sets the new value of a base form of this word pattern.
     *
     *  @param  newBaseForm     The new base form.
     *
     */
    public void setBaseForm(final String newBaseForm) {
        this.baseForm = newBaseForm;
    }

    /**
     *  Returns the symbols of affix rule sets which apply to this word pattern.
     *
     *  @return The array of affix rules set symbols.
     */
    public char[] getRulesSymbols() {
        return rulesSymbols;
    }

    /**
     *  Sets the symbols of affix rule sets which should apply to this word pattern.
     *
     *  @param  newRulesSymbols     The new array of affix rule set symbols.
     */
    public void setRulesSymbols(final char[] newRulesSymbols) {
        this.rulesSymbols = newRulesSymbols;
    }

    /**
     * Produces all forms of the word defined in this word pattern.
     *
     * This method returns array of word forms. The first form in the returned array is the base form. 
     * All other forms are returned ordered (the order depends on the charset used by mutable strings). 
     * 
     * @param   affixRulesMap   The map of affix rule sets. Keys of this map are symbols of rule sets, and
     *                          values are {@link AffixRulesSet} objects.
     *
     * @return  The list of mutable strings.
     *
     * @throws  MySpellSyntaxException If forms cannot be generate 
     *          (for example required rules set cannot be obtained from the map).
     */
    public List getAllForms(final Map affixRulesMap) throws MySpellSyntaxException {
        ArrayList res = new ArrayList();
        res.add(this.baseForm);
        if (rulesSymbols != null && rulesSymbols.length > 0) {
            HashSet tmp = new HashSet();

            HashSet prefixRules = new HashSet();
            HashSet suffixRules = new HashSet();
            HashSet suffixRulesWithPrefixes = new HashSet();
            
            for (int i = 0; i < rulesSymbols.length; i++) {
                char symbol = rulesSymbols[i];
                AffixRulesSet rulesSet = (AffixRulesSet) affixRulesMap.get(new Character(symbol));
                if (rulesSet == null) {
                    throw new MySpellSyntaxException("Cannot find rules set having symbol : " + symbol);
                }
                for (Iterator it = rulesSet.getRules().iterator(); it.hasNext();) {
                    AffixRule ar = (AffixRule) it.next();
                    if (ar.checkCondition(this.baseForm)) {
                        if (ar.isPrefix()) {
                            prefixRules.add(ar);
                        } else {
                            suffixRules.add(ar);
                            if (rulesSet.isUseWithPrefixes()) {
                                suffixRulesWithPrefixes.add(ar);
                            }
                        }
                    }
                }
            }
            
            for (Iterator pit = prefixRules.iterator(); pit.hasNext();) {
                AffixRule pr = (AffixRule) pit.next();
                String tmpForm = pr.generateForm(this.baseForm);
                tmp.add(tmpForm);
                for (Iterator sit = suffixRulesWithPrefixes.iterator(); sit.hasNext();) {
                    AffixRule sr = (AffixRule) sit.next();
                    tmp.add(sr.generateForm(tmpForm));
                }
            }
            for (Iterator sit = suffixRules.iterator(); sit.hasNext();) {
                AffixRule sr = (AffixRule) sit.next();
                tmp.add(sr.generateForm(this.baseForm));
            }
            
            ArrayList list = new ArrayList();
            list.addAll(tmp);
            Collections.sort(list);
            for (Iterator it = list.iterator(); it.hasNext();) {
                res.add(it.next());
            }
        }
        return res;
    }
    
}
