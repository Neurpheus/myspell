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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reads words from <a href="http://en.wikipedia.org/wiki/MySpell">MySpell</a> files and 
 * allows to execute any operation on all forms of succeeding words.
 *
 * @author Jakub Strychowski
 */
public class MySpellDictionary {

    /** Holds logger for this class */
    private static Logger logger = Logger.getLogger(MySpellDictionary.class.getName());
    
    /** Holds patterns of all words readed form the specific *.dic file. */
    private ArrayList wordsPatterns;
    
    /** Holds map of affix rules sets. */
    private HashMap affixRulesMap;
    
    /** Holds the name of charset of *.aff and *.dic files. */
    private String charsetName;
    
    private boolean dictionaryLoaded;
    
    /** 
     * Creates a new instance of MySpellDictionary 
     */
    public MySpellDictionary() {
        wordsPatterns = new ArrayList();
        affixRulesMap = new HashMap();
        setDictionaryLoaded(false);
    }
    
    /**
     * Loads MySpell dictionary from the given directory path using given dictionary symbol.
     *
     * The directory specified by the <code>path</code> parameter should contain 
     * <code>{$dictSymbol}.aff</code> and <code>{$dictSymbol}.dic</code> files.
     * 
     * @param   path        The location of the directory which contains MySpell files.
     * @param   dictSymbol  The symbol of MySpell dictionary, for example "en_US".
     *
     * @throws IOException if an error occurred while dictionary loading.
     * @throws MySpellSyntaxException if dictionary files contain any syntatic error.
     */
    public void load(final String path, final String dictSymbol) 
    throws IOException, MySpellSyntaxException {
        if (logger.isLoggable(Level.INFO)) {
            logger.info("Loading MySpell dictionary (" + dictSymbol + ") from path : " + path);
        }
        String tmp;
        if (!path.endsWith(File.separator)) {
            tmp = path + File.separator;
        } else {
            tmp = path;
        }
        String affixesPath = tmp + dictSymbol + ".aff";
        String wordsPath = tmp + dictSymbol + ".dic";
        loadFromFiles(affixesPath, wordsPath);
        if (logger.isLoggable(Level.INFO)) {
            logger.info("MySpell dictionary loaded.");
        }
    }

    /**
     * Loads MySpell dictionary from the given affixes and words files.
     * 
     * @param affixesPath   The location of affix rule definitions.
     * @param wordsPath     The location of word patterns. 
     *
     * @throws IOException if an error occurred while dictionary loading.
     * @throws MySpellSyntaxException if dictionary files contain any syntatic error.
     */
    public void loadFromFiles(final String affixesPath, final String wordsPath) 
    throws IOException, MySpellSyntaxException {
        loadAffixesFromFile(affixesPath);
        loadWordsFromFile(wordsPath);
        setDictionaryLoaded(true);
    }
    
    /**
     * Detects the charset used by MySpell dictionary files.
     *
     * This method reads the charset name from the affix rule definitions file. This object uses detected
     * charset name for dictionary loading.
     * 
     * @param   path    The path to the affix rule definitions file.
     *
     * @throws IOException if an error occurred while file processing.
     * @throws MySpellSyntaxException if dictionary files contain any syntatic error.
     */
    public String detectCharset(final String path) throws IOException, MySpellSyntaxException {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Reading charset used by the MySpell dictionary form file " + path);
        }
        BufferedReader reader = null;
        String result = null;
        try{
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "iso8859-1"));
            String line = null;
            do {
                line = reader.readLine();
                if (line != null) {
                    if (line.startsWith("SET ")) {
                        int pos = line.indexOf('#');
                        if (pos >= 0) {
                            line = line.substring(0, pos);
                        }
                        result = line.substring(4).trim();
                    }
                }
            } while (result == null && line != null);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        if (result == null) {
            throw new MySpellSyntaxException("Cannot find charset definition in the *.aff file.");
        } else {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Detected charset name : " + result);
            }
            return result;
        }
    }
    
    /**
     * Loads affix rule definitions from the given <code>.aff</code> file.
     * 
     * @param   path    The location of an affix rule definitions file.
     *
     * @throws IOException if an error occurred while dictionary loading.
     * @throws MySpellSyntaxException if dictionary files contain any syntatic error.
     */
    public void loadAffixesFromFile(final String path) throws IOException, MySpellSyntaxException {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Loading affixes rules definitions from file : " + path);
        }
        charsetName = detectCharset(path);
        Reader reader = null;
        try{
            reader = new InputStreamReader(new FileInputStream(path), charsetName);
            loadAffixes(reader);
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Affixes rules loaded.");
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * Loads word patterns for the given <code>.dic</code file.
     *
     * @param   path    The location of a word patterns file.
     *
     * @throws IOException if an error occurred while dictionary loading.
     * @throws MySpellSyntaxException if dictionary files contain any syntatic error.
     */
    public void loadWordsFromFile(final String path) throws IOException, MySpellSyntaxException {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Loading MySpell words patterns from file : " + path);
        }
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(path), charsetName);
            loadWords(reader);
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("MySpell words patterns loaded.");
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
    
    /**
     * Loads affix rule definitions using the given reader.
     * This method does not close the input reader.
     * 
     * @param   in  The reader which provides affix rule definitions data.
     *
     * @throws IOException if an error occurred while dictionary loading.
     * @throws MySpellSyntaxException if dictionary files contain any syntatic error.
     */
    public void loadAffixes(final Reader in) throws IOException, MySpellSyntaxException {
        BufferedReader reader = new BufferedReader(in);
        String line = null;
        AffixRulesSet rulesSet = null;
        int rulesCount = -1;
        int lineNumber = 0;
        do {
            if (logger.isLoggable(Level.FINE) && (lineNumber % 1000 == 0)) {
                logger.fine(" number of loaded lines : " + lineNumber);
            }
            ++lineNumber;
            line = reader.readLine();
            if (line != null) {
                line = line.trim();
                int pos = line.indexOf('#');
                if (pos >= 0) {
                    line = line.substring(0, pos);
                }
                if (line.length() > 0) {
                    boolean isSuffix = line.startsWith("SFX");
                    boolean isPrefix = line.startsWith("PFX");
                    if (isSuffix || isPrefix) {
                        if (rulesSet == null) {
                            rulesSet = new AffixRulesSet(line, lineNumber);
                            affixRulesMap.put(new Character(rulesSet.getSymbol()), rulesSet);
                            rulesCount = rulesSet.getRulesCount();
                        } else {
                            --rulesCount;
                            AffixRule ar = new AffixRule(
                                    isPrefix, 
                                    rulesSet.getSymbol(), 
                                    line, 
                                    lineNumber);
                            rulesSet.addAffixRule(ar);
                        }
                    }
                    if (rulesCount <= 0) {
                        rulesSet = null;
                    }
                }
            }
        } while (line != null);
    }
    
    /**
     * Loads word patterns using the given reader.
     * This method does not close the input reader.
     * 
     * @param   in  The reader which provides word patterns data.
     *
     * @throws IOException if an error occurred while dictionary loading.
     * @throws MySpellSyntaxException if dictionary files contain any syntatic error.
     */
    public void loadWords(final Reader in) throws IOException, MySpellSyntaxException {
        BufferedReader reader = new BufferedReader(in);
        String line = null;
        int count = -1;
        int lineNumber = 0;
        do {
            if (logger.isLoggable(Level.FINE) && (lineNumber % 1000 == 0)) {
                logger.fine(" number of loaded lines : " + lineNumber);
            }
            ++lineNumber;
            line = reader.readLine();
            if (line != null) {
                line = line.trim();
                int pos = line.indexOf('#');
                if (pos >= 0) {
                    line = line.substring(0, pos);
                }
                if (line.length() > 0) {
                    if (count == -1) {
                        count = Integer.parseInt(line);
                    } else {
                        wordsPatterns.add(new WordPattern(line));
                    }
                }
            }
        } while (line != null);
    }

    /**
     * Process all words contained in the MySpell dictionary calling the <code>processor</code> code for 
     * succeeding lists of word forms.
     *
     * @param   processor           An object which make processing for each word.
     * @param   breakOnException    If this parameter is <code>true</code> and an exception occured while 
     *                              word processing, this methods breaks processing. 
     *                              The <code>false</code> value means that exceptions are only logged out
     *                              and succeeding words are processed.
     *
     * @throws WordFormsProcessingException if <code>breakOnException</code> is true and any exception 
     *                                      occurred while word processing.
     */
    public void processAllForms(final WordFormsProcessor processor, final boolean breakOnException) 
    throws WordFormsProcessingException {
        if (!isDictionaryLoaded()) {
            throw new WordFormsProcessingException("Dictionary have not been loaded!");
        }
        int i = 0;
        for (Iterator it = getWordsPatterns().iterator(); it.hasNext(); i++) {
            if (logger.isLoggable(Level.FINE) && (i % 1000 == 0)) {
                logger.fine(i + " words already processed");
            }
            WordPattern wp = (WordPattern)it.next();
            try {
                try {
                    List forms = wp.getAllForms(affixRulesMap);
                    processor.processWordForms(forms);
                } catch (MySpellSyntaxException myspellException) {
                    throw new WordFormsProcessingException(myspellException);
                }
            } catch (WordFormsProcessingException e) {
                if (breakOnException) {
                    throw e;
                } else {
                    logger.log(Level.WARNING, "Error occurred during word forms processing.", e);
                }
            }
        }
        
        
    }

    
    
    public void print(final String filePath) 
    throws WordFormsProcessingException, IOException, MySpellSyntaxException {
        if (logger.isLoggable(Level.INFO)) {
            logger.info("Writing full dictionary based on MySpell to file : " + filePath);
            logger.info("  output charset name : " + "UTF-8");
        }
        PrintStream out = null;
        try {
            out = new PrintStream(new BufferedOutputStream(new FileOutputStream(filePath)), false, charsetName);
            print(out);
        } finally {
            if (out != null) {
                out.close();
            }
        }
        if (logger.isLoggable(Level.INFO)) {
            logger.info("Full dictionary generated.");
        }
    }

    public void print(final PrintStream out) 
    throws WordFormsProcessingException, IOException, MySpellSyntaxException {
        int i = 0;
        for (Iterator it = wordsPatterns.iterator(); it.hasNext(); i++) {
            if (logger.isLoggable(Level.FINE) && (i % 1000 == 0)) {
                logger.fine(i + " words already generated");
            }
            WordPattern wp = (WordPattern)it.next();
            out.println(wp.getAllForms(affixRulesMap));
        }
    }
    
    public static void generateFullDictionary(final String dirPath, final String dictSymbol) {
        MySpellDictionary dict = new MySpellDictionary();
        try{
            dict.load(dirPath, dictSymbol);
        } catch (Exception e){
            logger.log(Level.SEVERE, "Cannot load MySpell dictionary", e);
        }
        try{
            String outPath = dirPath + File.separator + dictSymbol + ".all";
            dict.print(outPath);
        } catch (Exception e){
            logger.log(Level.SEVERE, "Cannot generate full dictionary", e);
        }
    }

    public ArrayList getWordsPatterns() {
        return wordsPatterns;
    }

    public void setWordsPatterns(ArrayList wordsPatterns) {
        this.wordsPatterns = wordsPatterns;
    }

    public HashMap getAffixRulesMap() {
        return affixRulesMap;
    }

    public void setAffixRulesMap(HashMap affixRulesMap) {
        this.affixRulesMap = affixRulesMap;
    }

    public String getCharsetName() {
        return charsetName;
    }

    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }

    public boolean isDictionaryLoaded() {
        return dictionaryLoaded;
    }

    public void setDictionaryLoaded(boolean dictionaryLoaded) {
        this.dictionaryLoaded = dictionaryLoaded;
    }
    
}
