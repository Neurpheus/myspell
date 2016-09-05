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

/**
 * Represents a single affix rule from the MySpell affixes definition file (*.aff file).
 *
 * <p>
 * Each rule is written as a single line in a *.aff file. Such lines consist of 5 tokens. A first token 
 * denotes if the rule is applied to prefixes ("PFX") or suffixes ("SFX"). A second token is a symbol of 
 * a rules set to which this rule belongs. The symbol is used in a words dictionary to mark the words 
 * which inflects according to the rules set represented by the symbol. Third token is an affix in form of
 * characters sequence. This affix should be replaced, while the rule is applied to the base form of a word. 
 * A fourth token is a replacement - a string which replace the affix. A last token is a rule condition in form
 * of a regular expression. The affix rule is applied to the base form, only if this condition matches the
 * form created after removing the affix.
 * </p>
 * <p>
 * This class parses a single line from a *.aff file, allows to read each token, checks condition on any
 * base form, produces a result form for the particular base form.
 * </p>
 *
 * @author Jakub Strychowski
 */
public class AffixRule {

    /** Holds numbers of tokens in a single line of a aff file. */
    private static final int AFF_SYNTAX_NUMBER_OF_TOKENS = 5;
    
    /** Holds the position of a symbol in the line from aff file. */
    private static final int AFF_SYNTAX_SYMBOL_POSITION = 1;
    
    /** Holds the position of a <em>change from</em> string in the line from aff file. */
    private static final int AFF_SYNTAX_CHANGE_FROM_POSITION = 2;
    
    /** Holds the position of a <em>change to</em> string in the line from aff file. */
    private static final int AFF_SYNTAX_CHANGE_TO_POSITION = 3;
    
    /** Holds the position of a <em>change condition</em> string in the line from aff file. */
    private static final int AFF_SYNTAX_CHANGE_CONDITION_POSITION = 4;
    
    
    /** Holds the affix which should be replaced. */
    private String changeFrom;
    
    /** Holds the string into which replace affix. */
    private String changeTo;
    
    /** Holds the condition (regular expression) which should be satisfied to make replacment. */
    private String changeCondition;
    
    /** Prefix flag - if true rule is applied to prefixes. */
    private boolean prefix;
    
    /**
     *  Initizlies affix rule.
     *
     *  @param  newPrefixValue  If true rule is applied to prefixes.
     *  @param  from            Denotes the affix which should be replaced.
     *  @param  to              Denotes replacement for the affix.
     *  @param  condition       Denotes condition which should be satisfied to apply rule.
     */
    private void init(
    final boolean newPrefixValue, final String from, final String to, final String condition) {
        if (from == null || from.equals("0")) {
            setChangeFrom("");
        } else {
            setChangeFrom(from);
        }
        if (to == null || to.equals("0")) {
            setChangeTo("");
        } else {
            setChangeTo(to);
        }
        if (condition == null || condition.equals("0")) {
            setChangeCondition("");
        } else {
            setChangeCondition(condition);
        }
        setPrefix(newPrefixValue);
    }
    
    /**
     *  Creates new affix rule parsing given line of *.aff file.
     *
     *  @param  newPrefixValue  If <code>true</code> rule is applied to prefixes.
     *  @param  symbol          Identifier of the rule.
     *  @param  line            Rule in form of single line from a *.aff file.
     *  @param  lineNumber      The line number in the *.aff file.
     *
     *  @throws MySpellSyntaxException if a parsing error occurred.
     */
    public AffixRule(
    final boolean newPrefixValue, final char symbol, final String line, final int lineNumber)
    throws MySpellSyntaxException {
        setPrefix(prefix);
        String[] tab = line.split("\\s");
        int pos = 0;
        for (int i = 0; i < tab.length; i++) {
            if (tab[i].length() > 0) {
                tab[pos++] = tab[i];
            }
        }
        String[] tmp = new String[pos];
        System.arraycopy(tab, 0, tmp, 0, pos);
        tab = tmp;
        if (tab.length != AFF_SYNTAX_NUMBER_OF_TOKENS) {
            throw new MySpellSyntaxException("Syntax error at line " + lineNumber + " : \"" + line
                    + "\" - Invalid number of parameters.");
        }
        if (tab[AFF_SYNTAX_SYMBOL_POSITION].charAt(0) != symbol) {
            throw new MySpellSyntaxException("Syntax error at line " + lineNumber + " : \"" + line
                    + "\" - Wrong symbol : '" + tab[1] + "' . '" + symbol + "' was expected.");
        }
        init(
            newPrefixValue, 
            tab[AFF_SYNTAX_CHANGE_FROM_POSITION], 
            tab[AFF_SYNTAX_CHANGE_TO_POSITION], 
            tab[AFF_SYNTAX_CHANGE_CONDITION_POSITION]);
    }
    
    /**
     * Returns the affix which should be replaced.
     *
     * @return The affix to replace.
     */
    public String getChangeFrom() {
        return changeFrom;
    }
    
    /**
     * Sets a new value for the affix which should be replaced.
     *
     * @param   newChangeFrom  The new value of the affix.
     */
    public void setChangeFrom(final String newChangeFrom) {
        this.changeFrom = newChangeFrom;
    }
    
    /**
     * Gets replacment for the affix.
     *
     * @return A string into which replace the affix.
     */
    public String getChangeTo() {
        return changeTo;
    }
    
    /**
     *  Sets a new value of the replacement for the affix.
     *
     * @param newChangeTo  the new value of the replacement.
     */
    public void setChangeTo(final String newChangeTo) {
        this.changeTo = newChangeTo;
    }
    
    /**
     *  Returns the condition (regular expression) which should be satisfied to apply rule.
     *
     * @return The string which should occur in a base form to apply this rule.
     */
    public String getChangeCondition() {
        return changeCondition;
    }
    
    /**
     *  Sets a new value of the condition (regular expression) for this rule.
     *
     * @param   newChangeCondition     The new value of the condition.
     */
    public void setChangeCondition(final String newChangeCondition) {
        this.changeCondition = newChangeCondition;
    }
    
    /**
     * Checks if this rule can be applied to the given base form according to condition (regular expression) 
     * defined in the rule.
     *
     * @param   baseForm    The base form to check.
     *
     * @return <code>true</code> if this rule can be applied to the given form.
     */
    public boolean checkCondition(final String baseForm) {
        if (isPrefix()) {
            if (changeFrom.length() > 0 && !baseForm.startsWith(changeFrom)) {
                return false;
            }
            if (changeCondition.length() != 0) {
                return baseForm.matches("^" + changeCondition + ".*$");
            }
        } else {
            if (changeFrom.length() > 0 && !baseForm.endsWith(changeFrom)) {
                return false;
            }
            if (changeCondition.length() != 0) {
                return baseForm.matches("^.*" + changeCondition + "$");
            }
        }
        return true;
    }
    
    /**
     *  Generates derived form using this rule on the fiven base form.
     *
     * @param   baseForm    The base form for which alternative form should be created.
     *
     * @return A new form of the given word which is created by this rule.
     */
    public String generateForm(final String baseForm) {
        StringBuffer res = new StringBuffer(changeTo.length() + baseForm.length() - changeFrom.length());
        if (isPrefix()) {
            res.append(changeTo);
            res.append(baseForm.substring(changeFrom.length()));
        } else {
            res.append(baseForm.substring(0, baseForm.length() - changeFrom.length()));
            res.append(changeTo);
        }
        return res.toString();
    }
    
    /**
     *  Checks if this rule should be applied to prefix.
     *
     * @return <code>true</code> if this rule defines the modification of a prefix.
     */
    public boolean isPrefix() {
        return prefix;
    }
    
    /**
     *  Sets the flag denoting if this rule should be applied to prefix.
     *
     * @param   newPrefixFlagValue  <code>true</code> if this rule defines the modification of a prefix.
     */
    public void setPrefix(final boolean newPrefixFlagValue) {
        this.prefix = newPrefixFlagValue;
    }
    
}
