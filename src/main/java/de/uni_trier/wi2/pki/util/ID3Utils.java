package de.uni_trier.wi2.pki.util;

import de.uni_trier.wi2.pki.io.attr.CSVAttribute;
import de.uni_trier.wi2.pki.tree.DecisionTreeNode;
import de.uni_trier.wi2.pki.io.attr.CatAtt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility class for creating a decision tree with the ID3 algorithm.
 */
public class ID3Utils {

    /**
     * Create the decision tree given the example and the index of the label attribute.
     *
     * @param examples   The examples to train with. This is a collection of arrays.
     * @param labelIndex The label of the attribute that should be used as an index.
     * @return The root node of the decision tree
     */
    public static DecisionTreeNode generateTree(Collection<CSVAttribute[]> examples, int labelIndex) {
    	
    	if (examples != null && !examples.isEmpty()) {
    		ArrayList<CSVAttribute[]> dataset = (ArrayList<CSVAttribute[]>) examples;
    		
    		if (labelIndex >= dataset.get(0).length || labelIndex < 0) {
    			return null;
    		}
    		
    		if (((CatAtt) dataset.get(0)[labelIndex]).getBuilder().getCatVal().size() != 2) {
    			return null;
    		}
    		
    		for (CSVAttribute[] csvAttributes : dataset) {
    			for (CSVAttribute csvAttribute : csvAttributes) {
    				if (CatAtt.class != csvAttribute.getClass()) {
    					return null;
    				}
    			}
    		}

			// FIFO Liste erstellen
    		LinkedList<ArrayList<CSVAttribute[]>> subset = new LinkedList<>();
    		LinkedList<DecisionTreeNode> listNode = new LinkedList<>();

			// Attribut mit dem hoechsten Informationsgewinn wird genommen
    		ArrayList<Double> infoGainList = (ArrayList<Double>) EntropyUtils.calcInformationGain(dataset, labelIndex);
    		int indexNextAttribute = getMaxElementsInList(infoGainList);

			// Erstellung des Wurzelknotens
    		DecisionTreeNode rootNode = new DecisionTreeNode();
    		rootNode.setRoot();
    		rootNode.setAttIndex(indexNextAttribute);
    		rootNode.setAttribute(((CatAtt) dataset.get(0)[indexNextAttribute]).getBuilder().getHeader());
    		rootNode.setClassification(calcClassification(dataset, labelIndex, rootNode));

			// Aufteilung auf Basis des Wertes mit dem hoechsten Informationsgewinn
    		ArrayList<Collection<CSVAttribute[]>> collectionArrayList = PartUtils.partByValue(dataset, indexNextAttribute);

			// Hinzufuegen des Subsets zur FIFO Liste
    		for (int i = 0; i < collectionArrayList.size(); i++) {
    			subset.add((ArrayList<CSVAttribute[]>) collectionArrayList.get(i));
    			String splitValue = ((CatAtt) dataset.get(0)[indexNextAttribute]).getBuilder().getCatVal().get(i);
    			DecisionTreeNode tempNode = new DecisionTreeNode(rootNode);
    			rootNode.setSplit(splitValue, tempNode);
    			listNode.add(tempNode);
    		}
    		
    		while (!subset.isEmpty()) {
    			ArrayList<CSVAttribute[]> currentSubset = subset.pop();

				// Blattknoten erstellen, wenn das current Subset leer ist
    			if (currentSubset.isEmpty()) {
    				DecisionTreeNode newLeafNode = listNode.pop();
    				newLeafNode.setClassification(newLeafNode.getParent().getClassification());
    				newLeafNode.makeLeaf();
    				continue;
    			}

				// Blattknoten erstellen, wenn alle den selbsten Wert haben
    			if (sameValue(currentSubset, labelIndex) != null) {
    				DecisionTreeNode  newLeafNode = listNode.pop();
    				newLeafNode.setClassification(sameValue(currentSubset, labelIndex));
    				newLeafNode.makeLeaf();
    				continue;
    			}

				// Auswahl des Attributes mit dem hoechsten Informationsgewinn
    			infoGainList = (ArrayList<Double>) EntropyUtils.calcInformationGain(currentSubset, labelIndex);
    			indexNextAttribute = getMaxElementsInList(infoGainList);

				// alle Knoten wurden bereits überprüft -> Blattknoten erstellen
    			if (indexNextAttribute == -1) {
    				DecisionTreeNode newLeafNode = listNode.pop();
    				newLeafNode.setClassification(calcClassification(currentSubset, labelIndex, newLeafNode));
    				newLeafNode.makeLeaf();
    				continue;
    			}

				// wenn bis jetzt kein Blatt erstellt worden ist, muss ein normaler Knoten erstellt werden
    			DecisionTreeNode newNode = listNode.pop();
    			newNode.setClassification(calcClassification(currentSubset, labelIndex, newNode));
    			newNode.setAttIndex(indexNextAttribute);
    			newNode.setAttribute(((CatAtt) dataset.get(0)[indexNextAttribute]).getBuilder().getHeader());

				// erneute Aufteilung auf Basis des Wertes mit dem hoechsten Informationsgewinns
    			ArrayList<Collection<CSVAttribute[]>> nextSubset = PartUtils.partByValue(currentSubset, indexNextAttribute);
    			
    			for (int i = 0; i < nextSubset.size(); i++) {
    				subset.add((ArrayList<CSVAttribute[]>) nextSubset.get(i));
    				String splitValue = ((CatAtt) dataset.get(0)[indexNextAttribute]).getBuilder().getCatVal().get(i);
    				DecisionTreeNode tempNode = new DecisionTreeNode(newNode);
    				newNode.setSplit(splitValue, tempNode);
					listNode.add(tempNode);
    			}
    		}
    		return rootNode;
    	}    	
        return null;
    }

	/**
	 * Groeße des Baums bestimmen
	 * @param rootNode Wurzel des Baums
	 * @return Anzahl der Knoten
	 */
	public static int getTSize(DecisionTreeNode rootNode) {
		if (rootNode != null) {
			if (!rootNode.isLeaf()) {
				int size = 1;
				LinkedList<DecisionTreeNode> nodes = new LinkedList<>(rootNode.getChildren());

				while (!nodes.isEmpty()) {
					DecisionTreeNode tempNode = nodes.pop();
					if (!tempNode.isLeaf()) {
						nodes.addAll(tempNode.getChildren());
					}
					size++;
				}
				return size;
			}
			return 1;
		}
		return 0;
	}

	/**
	 * Ueberprueft, ob das Dataset für alle Attribute den gleichen Wert hat
	 * @param examples zu ueberpruefendes Dataset
	 * @param attributeIndex    Index des zu ueberpruefenden Attributes
	 * @return Gibt den Wert zurück, wenn er fuer alle ueberprueften Elemente gleich ist
	 */
	public static String sameValue(Collection<CSVAttribute[]> examples, int attributeIndex) {
		
		if (examples != null && !examples.isEmpty()) {
			ArrayList<CSVAttribute[]> datset = (ArrayList<CSVAttribute[]>) examples;
			
			if (attributeIndex >= datset.get(0).length || attributeIndex < 0) {
				return null;
			}
			
			String attributeValue = (String) datset.get(0)[attributeIndex].getValue();
			
			for (int i = 1; i < datset.size(); i++) {
				if (!attributeValue.equals(datset.get(i)[attributeIndex].getValue())) {
					return null;
				}
			}
			return attributeValue;
		}		
		return null;
	}

	/**
	 * Berechnung der Klassifikation für einen Knoten
	 * @param examples Dataset des Knoten
	 * @param labelIndex Index des Label Attributes
	 * @param node zu klassifizierender Knoten
	 * @return die Klassifikation
	 */
	public static String calcClassification(List<CSVAttribute[]> examples, int labelIndex, DecisionTreeNode node) {
		if (examples != null && !examples.isEmpty()) {
			ArrayList<CSVAttribute[]> dataset = (ArrayList<CSVAttribute[]>) examples;
			
			if (labelIndex >= dataset.get(0).length || labelIndex < 0) {
				return null;
			}
			
			if (((CatAtt) dataset.get(0)[labelIndex]).getBuilder().getCatVal().size() != 2) {
				return null;
			}
			
			ArrayList<String> classificationValue = new ArrayList<>();
			HashMap<String, Integer> count = new HashMap<>();
			
			for (int i = 0; i < dataset.size(); i++) {
				String tempKey = (String) dataset.get(i)[labelIndex].getValue();
				if (count.containsKey(tempKey)) {
					count.put(tempKey, count.get(tempKey) + 1);
				} else {
					count.put(tempKey, 1);
					classificationValue.add(tempKey);
				}
			}
			
			if (count.get(classificationValue.get(0)) > count.get(classificationValue.get(1))) {
				return classificationValue.get(0);
			} if (count.get(classificationValue.get(1)) > count.get(classificationValue.get(0))) {
				return classificationValue.get(1);
			} else {
				return node.getParent().getClassification();
			}	
		}		
		return null;
	}

	public static int getMaxElementsInList(List<Double> doubleList) {
		int maxElements = -1;
		if (doubleList != null && !doubleList.isEmpty()) {
			ArrayList<Double> liste = (ArrayList<Double>) doubleList;
			double maxValues = Double.MIN_VALUE;
			for (int i = 0; i < liste.size(); i++) {
				if (liste.get(i) > maxValues && liste.get(i) > 0) {
					maxValues = liste.get(i);
					maxElements = i;
				}
			}
		}
		return maxElements;
	}
}
