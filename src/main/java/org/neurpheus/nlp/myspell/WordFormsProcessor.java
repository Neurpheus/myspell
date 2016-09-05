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

import java.util.List;

/**
 *  Processes all forms of words obtained from a MySpell dictionary.
 *
 * @author Jakub Strychowski
 */
public interface WordFormsProcessor {

    /**
     * Processes all forms of a specific word.
     * <p>
     * This method gets a list of all forms of any word and executes particular action.
     * The first element on the <code>forms</code> list is the base form of a word.
     * </p>
     *
     * @param   forms   The list of all forms of a word.
     *
     * @throws WordFormsProcessingException if any error occurred while processing.
     */
    void processWordForms(final List forms) throws WordFormsProcessingException;
    
}
