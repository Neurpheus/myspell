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
import java.util.Collection;
import java.util.HashSet;

/**
 * Represents a set of affix rules from the MySpell affixes definition file (*.aff file).
 *
 * <p>
 * Each set of affix rules is represented by a unique symbol (single character). This symbol is used in
 * a base forms dictionary to mark words which inflects according to
 * the rules set represented by this symbol.
 * </p>
 * <p>
 * Each affix rules set is defined in the *.aff file in the form of a list of lines. A first line declares
 * the affix rule set. This line contains 4 tokens. A first token decides if the rules set is applied to
 * prefixes ("PFX") or suffixes ("SFX"). Next token is the symbol of the rules set. Third token
 * is the 'Y' or 'N' character. 'Y' means that this rules set can be combined with other rules set used
 * for the same base form. Last token is the number of affix rules in the set.
 * </p>
 * <p>
 * More information about MySpell affix file format can be found on the page :
 * <a href="http://lingucomponent.openoffice.org/affix.readme">Understanding the Affix File Format</a>
 * </p>
 * <p>
 * This class parses information about the rules set, holds obtained information and provides rules
 * previously added to this set.
 * </p>
 *
 * @author Jakub Struchowski
 */
public class AffixRulesSet {
    
    /** Holds the string which marks the rules set as a set of rules operating on suffixes. */
    private static final String SUFFIX_TAG = "SFX";
    
    /** Holds the string which marks the rules set as a set of rules operating on prefixes. */
    private static final String PREFIX_TAG = "PFX";
    
    /** Holds the string which denotes that this rules set can be combined with other sets. */
    private static final String YES_TAG = "Y";
    
    /** Holds the string which denotes that this rules set cannot be combined with other sets. */
    private static final String NO_TAG = "N";
    
    /** Holds numbers of tokens in a line of rules set declaration. */
    private static final int AFF_SYNTAX_NUMBER_OF_TOKENS = 4;

    /** Holds the position of a token which decides if the rules set operates on prefixes . */
    private static final int AFF_SYNTAX_PFX_SFX_POSITION = 0;
    
    /** Holds the position of a symbol in the rules set declaration. */
    private static final int AFF_SYNTAX_SYMBOL_POSITION = 1;
    
    /** Holds the position of a flag which decides if the rules set can be combined with other rules sets.*/
    private static final int AFF_SYNTAX_MAY_BE_COMBINED_POSITION = 2;

    /** Holds the position of the token which is a number of rules in the set. */
    private static final int AFF_SYNTAX_NUMBER_OF_RULES_POSITION = 3;
    
    /** Holds all rules from this rules set. */
    private Collection rules;
    
    /** Holds the symbol representing this rules set. */
    private char symbol;
    
    /** Holds the number of rules in this rules set. */
    private int rulesCount;
    
    /** Flag which denotes if this rules set operates on suffixes. */
    private boolean suffix;
    
    /** Flag which denotes if this rules set operates on prefixes. */
    private boolean prefix;
    
    
    /**
     * Flag which denotes if this rules set can be combined
     * with other rules sets which operate on prefixes.
     */
    private boolean useWithPrefixes;
    
    /**
     * Creates a new instance of AffixRulesSet parsing given line from a *.aff file.
     *
     * @param   line        The line which declares the rules set in the *.aff file.
     * @param   lineNumber  The line number in the *.aff file (used for logging).
     *
     * @throws MySpellSyntaxException if parsing process fails.
     */
    public AffixRulesSet(final String line, final int lineNumber) throws MySpellSyntaxException {
        String[] tab = line.split("\\s");
        if (tab.length != AFF_SYNTAX_NUMBER_OF_TOKENS) {
            throw new MySpellSyntaxException("Syntax error at line " + lineNumber + " : \"" + line + "\" - "
                    + "Invalid number of tokens in line. 4 tokens is required.");
        }
        
        setSuffix(tab[AFF_SYNTAX_PFX_SFX_POSITION].equals(SUFFIX_TAG));
        setPrefix(tab[AFF_SYNTAX_PFX_SFX_POSITION].equals(PREFIX_TAG));
        if (!isSuffix() && !isPrefix()) {
            throw new MySpellSyntaxException("Syntax error at line " + lineNumber + " : \"" + line + "\" - "
                    + "Invalid first token. Suffix or prefix declaration is required.");
        }
        
        if (!tab[AFF_SYNTAX_MAY_BE_COMBINED_POSITION].equals(YES_TAG) 
        && !tab[AFF_SYNTAX_MAY_BE_COMBINED_POSITION].equals(NO_TAG)) {
            throw new MySpellSyntaxException("Syntax error at line " + lineNumber + " : \"" + line + "\" - "
                    + "'Y' or 'N' character is required as third token.");
        }
        setUseWithPrefixes(tab[AFF_SYNTAX_MAY_BE_COMBINED_POSITION].equals(YES_TAG));
        
        if (tab[AFF_SYNTAX_SYMBOL_POSITION].length() != 1) {
            throw new MySpellSyntaxException("Syntax error at line " + lineNumber + " : \"" + line + "\" - "
                    + "A rule symbol has to be a single character.");
        }
        setSymbol(tab[AFF_SYNTAX_SYMBOL_POSITION].charAt(0));
        
        try {
            setRulesCount(Integer.parseInt(tab[AFF_SYNTAX_NUMBER_OF_RULES_POSITION]));
        } catch (NumberFormatException e) {
            throw new MySpellSyntaxException("Syntax error at line " + lineNumber + " : \"" + line + "\" - "
                    + "Invalid number of rules.");
        }
        
        rules = new HashSet();
    }
    
    /**
     * Adds given affix rule to this rules set.
     *
     * @param   rule    The rule to add.
     */
    public void addAffixRule(final AffixRule rule) {
        rules.add(rule);
        rulesCount = rules.size();
    }
    
    /**
     * Returns collection of rules from this rules set.
     *
     * @return  The list of {@link AffixRule} objects.
     */
    public Collection getRules() {
        return rules;
    }
    
    /**
     * Sets new collection of rules for this rules set.
     *
     * @param   newRules   The new list of {@link AffixRule} objects.
     */
    public void setRules(final Collection newRules) {
        this.rules = newRules;
        this.rulesCount = rules.size();
    }
    
    /**
     *  Returns a symbol which represents this rules set.
     *
     * @return The symbol of this rules set.
     */
    public char getSymbol() {
        return symbol;
    }
    
    /**
     * Sets a symbol which should represent this rules set.
     *
     * @param   newSymbol  The new symbol of this rules set.
     */
    public void setSymbol(final char newSymbol) {
        this.symbol = newSymbol;
    }
    
    /**
     * Returns the number of rules in this rules set.
     *
     * @return The numebr of rules.
     */
    public int getRulesCount() {
        return rulesCount;
    }
    
    /**
     * Sets the number of rules in this rules set.
     *
     * @param   newRulesCount  The new number of rules.
     */
    public void setRulesCount(final int newRulesCount) {
        this.rulesCount = newRulesCount;
    }
    
    /**
     * Checks if this rules set applies to the prefixes.
     *
     * @return <code>true</code> if this rules set is applied to the prefixes.
     */
    public boolean isPrefix() {
        return prefix;
    }
    
    /**
     * Sets the flag which informs if this rules set applies to the prefixes.
     *
     * @param   newPrefix  The new value of the flag.
     */
    public void setPrefix(final boolean newPrefix) {
        this.prefix = newPrefix;
    }
    
    /**
     * Checks if this rules set applies to the suffixes.
     *
     * @return <code>true</code> if this rules set is applied to the suffixes.
     */
    public boolean isSuffix() {
        return suffix;
    }
    
    /**
     * Sets the flag which informs if this rules set applies to the suffixes.
     *
     * @param   newSuffix   The new value of the flag.
     */
    public void setSuffix(final boolean newSuffix) {
        this.suffix = newSuffix;
    }
    
    
    /**
     * Checks if this rules set can be combined with other rules sets which operate on prefixes.
     *
     * @return if <code>true</code> this rules set can be combined with other rules sets.
     */
    public boolean isUseWithPrefixes() {
        return useWithPrefixes;
    }
    
    /**
     * Sets the flag which informs if this rules set can be combined with other rules sets
     * operating on prefixes.
     *
     * @param   newUseWithPrefixes  The new value of the flag.
     */
    public void setUseWithPrefixes(final boolean newUseWithPrefixes) {
        this.useWithPrefixes = newUseWithPrefixes;
    }
    
    
}
