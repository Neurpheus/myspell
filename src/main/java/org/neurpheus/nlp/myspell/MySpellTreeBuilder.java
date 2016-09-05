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

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neurpheus.collections.tree.Tree;
import org.neurpheus.collections.tree.TreeNodeWithData;
import org.neurpheus.collections.tree.TreeNode;
import org.neurpheus.collections.tree.objecttree.ObjectTreeFactory;
import org.neurpheus.collections.tree.linkedlist.LinkedListTreeFactory;
import org.neurpheus.collections.tree.util.DefaultNodePrinter;
import org.neurpheus.collections.tree.util.TreeHelper;

/**
 *
 * @author Jakub
 */
public class MySpellTreeBuilder implements WordFormsProcessor {

    private static Logger logger = Logger.getLogger(MySpellTreeBuilder.class.getName());
    private Tree tree;
    private String dirPath;
    private String symbol;
    private boolean onlyBaseForms;
    private boolean transducer;

    /** Creates a new instance of MySpellTreeBuilder */
    public MySpellTreeBuilder(String dirPath, String symbol, boolean onlyBaseForms,
                              boolean transducer) {
        this.dirPath = dirPath;
        this.symbol = symbol;
        this.tree = null;
        this.onlyBaseForms = onlyBaseForms;
        this.transducer = transducer;
    }

    private void addString(String s) {
        TreeNode parentNode = null;
        TreeNode node = tree.getRoot();
        for (int i = 0; i < s.length(); i++) {
            parentNode = node;
            char c = s.charAt(i);
            node = parentNode.getChild(new Character(c));
            if (node == null) {
                node = tree.getFactory().createTreeNode(new Character(c));
                parentNode.addChild(node);
            }
        }
        if (!node.hasExtraData()) {
            TreeNodeWithData nodeWithData = tree.getFactory().createTreeNodeWithAdditionalData(node.
                    getValue(), null);
            nodeWithData.setChildren(node.getChildren());
            parentNode.replaceChild(node, nodeWithData);
        }
    }

    public void processWordForms(final List forms) throws WordFormsProcessingException {
        String baseForm = ((String) forms.get(0));
        if (onlyBaseForms) {
            addString(baseForm);
        } else {
            for (Iterator it = forms.iterator(); it.hasNext();) {
                String form = (String) it.next();
                if (transducer) {
                    form += '*' + baseForm;
                }
                addString(form);
            }
        }
    }

    public class MySpellNodePrinter extends DefaultNodePrinter {

        public String getValueString(Object value) {
            char c = (char) Integer.parseInt(value.toString());
            return "" + c;
        }
    }

    public Tree getTree(boolean generateLogFile) {
        MySpellDictionary dict = new MySpellDictionary();
        try {
            dict.load(dirPath, symbol);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Cannot load MySpell dictionary.", e);
        }
        String logPath = dirPath + symbol + ".treelog";
        PrintStream out = null;
        try {
            logger.info("Generating LZTree representing all words of MySpell dictionary " + symbol);
            tree = ObjectTreeFactory.getInstance().createTree();
            dict.processAllForms(this, true);
            logger.info("Sorting tree.");
            ObjectTreeFactory.getInstance().sortTree(tree);
            tree = LinkedListTreeFactory.getInstance().createTree(tree, true, true, false);
            if (generateLogFile) {
                logger.info("Printing tree to file : " + logPath);
                out = new PrintStream(new FileOutputStream(new File(logPath)));
                TreeHelper.printTreeWords(tree, out, new MySpellNodePrinter());
                // TreeHelper.printTree(tree, out, new MyPellNodePrinter());
            }
            logger.info("Tree generation finished.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Cannot generate LZTree.", e);
            return null;
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return tree;
    }

    public static void main(String[] args) {
        String path = "C:\\projekty\\neurpheus\\data\\dictionaries";
        //String symbol = "alt_pl_PL";
        String symbol = "en_US";
        if (args.length > 0) {
            symbol = args[0];
        }
        if (args.length > 1) {
            path = args[1];
        }
        boolean transducer = false;
        boolean onlyBaseForms = false;
        MySpellTreeBuilder builder = new MySpellTreeBuilder(path, symbol, onlyBaseForms, transducer);
        Tree tree = builder.getTree(true);
    }
}
