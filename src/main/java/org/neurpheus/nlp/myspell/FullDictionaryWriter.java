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

import java.io.PrintStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Writes MySpell dictionary to the given output in human readable form.
 *
 * @author Jakub Strychowski
 */
public class FullDictionaryWriter implements WordFormsProcessor {

    /** Holds the output stream where the dictionary is written. */
    private PrintStream out;
    
    private Collection ignoredLexemes;
    
    
    
    /**
     * Creates a new instance of FullDictionaryWriter.
     *
     * @param   output  The output where dictionary should be written.
     */
    public FullDictionaryWriter(final PrintStream output, final Collection ignoredLex) {
        this.out = output;
        this.ignoredLexemes = ignoredLex;
    }
    
    /**
     * Writes all forms of a word in the human readable form.
     *
     * @param   forms   The list of all forms of a word.
     *
     * @throws WordFormsProcessingException if any error occurred while processing.
     */
    public void processWordForms(final List forms) throws WordFormsProcessingException {
        int i = 0;
        if (forms.size() > 0) {
            String s = forms.get(0).toString().trim();
            if (!ignoredLexemes.contains(s)) {
                for (Iterator it = forms.iterator(); it.hasNext(); i++) {
                    s = it.next().toString().trim();
                    out.print(s);
                    if (it.hasNext()) {
                        out.print(',');
                    }
                }
                out.println();
            }
        }
    }
    
}
