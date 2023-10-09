package de.uni_trier.wi2.pki.tree;

import java.util.ArrayList;
import java.util.HashMap;

// Class for representing a node in the decision tree.

public class DecisionTreeNode {

    //The parent node in the decision tree.

    protected DecisionTreeNode parent;

    //The attribute index to check.

    protected int attributeIndex;
    
    protected String attribute;

    //The checked split condition values and the nodes for these conditions.

    HashMap<String, DecisionTreeNode> splits = null;
    
    protected boolean isRootNode = false;
    
    protected boolean isLeafNode = false;
    
    protected String classification;

    // Constructor
    public DecisionTreeNode() {
    	}

    // Ein Knoten wird genutzt und dieser als Elternknoten gesetzt
    public DecisionTreeNode(DecisionTreeNode parent) {
    	setParent(parent);
    }

    // Wurzelknoten setzen; Elternknoten wird zum Wurzelknoten
    public void setRoot() {
    	setParent(null);
    	isRootNode = true;
    }

    /**
     * @return Attribut des Knotens wird zurueckgegeben
     */
    public String getAttribute() {
    	return attribute;
    }

    public void setAttribute(String attribute) {
    	this.attribute = attribute;
    }
    
    /**
     * 
     * @param classification des Knotens
     */
    public void setClassification(String classification) {
    	this.classification = classification;
    }

    public String getClassification() {
    	return classification;
    }

    // Alle Kindknoten werden zurückgegeben
    public ArrayList<DecisionTreeNode> getChildren() {
    	if (splits != null) {
    		return new ArrayList<>(splits.values());
    	} else {
    		return null;
    	}
    }

    // Werte für die Kindknoten
    public ArrayList<String> getValuesForChildren() {
    	if (splits != null) {
    		return new ArrayList<>(splits.keySet());
    	} else {
    		return null;
    	}
    }

    // Knoten wird als Blatt gesetzt
    public void setLeaf(boolean status) {
    	isLeafNode = status;
    }

    public void makeLeaf() {
    	setLeaf(true);
    	splits = null;
    }

    // Elternknoten auf NULL setzen für alle vorherigen Kindknoten
    public void prune() {
    	getChildren().forEach(x -> x.setParent(null));
    	makeLeaf();
    }

    // Ein nur voruebergehend genutzer Knoten fuer das Pruning
    public void tempPrune() {
    	setLeaf(true);
    }

    // "Rueckgaengig" machen der vorherigen Aktion tempPrune
    public void reverseTempPrune() {
    	setLeaf(false);
    }

    
    public DecisionTreeNode getNext(String value) {
    	if (splits != null) {
    		return splits.get(value);
    	}
    	return null;
    }

    public void setSplit(String value, DecisionTreeNode node) {
    	if (splits != null) {
    		splits.put(value, node);
    	} else {
    		splits = new HashMap<>();
    		splits.put(value, node);
    	}
    }
    
    public boolean isLeaf() {
    	return isLeafNode;
    }
    
    public void setAttIndex(int index) {
    	attributeIndex = index;
    }
    
    public int getAttIndex() {
    	if (!isLeafNode) {
    		return attributeIndex;
    	}
    	return -1;
    }
    
    public void setParent(DecisionTreeNode parent) {
    	this.parent = parent;
    }
    
    public DecisionTreeNode getParent() {
    	return parent;
    }  
}
