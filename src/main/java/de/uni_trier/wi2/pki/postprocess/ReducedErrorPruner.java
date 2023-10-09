package de.uni_trier.wi2.pki.postprocess;

import de.uni_trier.wi2.pki.io.attr.CSVAttribute;
import de.uni_trier.wi2.pki.tree.DecisionTreeNode;
import de.uni_trier.wi2.pki.util.TreePerformanceUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

//Prunes a trained decision tree in a post-pruning way.

public class ReducedErrorPruner {

    /**
     * Prunes the given decision tree in-place.
     *
     * @param trainedDecisionTree The decision tree to prune.
     * @param validationExamples  the examples to validate the pruning with.
     * @param labelAttributeId    The label attribute.
     * @throws Exception 
     */
    public void prune(DecisionTreeNode trainedDecisionTree, Collection<CSVAttribute[]> validationExamples, int labelAttributeId) {

		// Einsatz einer FIFO Liste
    	LinkedList<DecisionTreeNode> nodes = new LinkedList<>();
    	DecisionTreeNode currentNode;
    	DecisionTreeNode currentNodeForPrunning = null;
    	
    	double tempAccuracy;  // Genauigkeit nach Abschneiden des Knotens
    	double bestAccuracy;  // Beste Genauigkeit
    	double treeAccuracy;  // Genauigkeit des Baums an sich
    	
    	while (true) {
    		bestAccuracy = -1; // reset
    		treeAccuracy = TreePerformanceUtils.getClassAccuracy((List<CSVAttribute[]>) validationExamples, labelAttributeId, trainedDecisionTree);

			// Pruning funktioniert nur, solange der Knoten kein Blatt ist
    		if (!trainedDecisionTree.isLeaf()) {
    			nodes.add(trainedDecisionTree);
    		}

			// ueber alle Knoten gehen und nach dem FIFO Prinzip arbeiten
    		while (!nodes.isEmpty()) {
    			currentNode = nodes.pop();
    			currentNode.tempPrune();
    			tempAccuracy = TreePerformanceUtils.getClassAccuracy((List<CSVAttribute[]>) validationExamples, labelAttributeId, trainedDecisionTree);
    			currentNode.reverseTempPrune();
    			
    			if (tempAccuracy > bestAccuracy) {
    				bestAccuracy = tempAccuracy;
    				currentNodeForPrunning = currentNode;
    			}
    			
    			currentNode.getChildren().stream().filter(i -> !i.isLeaf()).forEach(nodes::add);
    		}
    		if (bestAccuracy >= treeAccuracy) {
    			currentNodeForPrunning.prune();
    		} else {
				// Abbruch, wenn nur eine schlechte Genauigkeit ermittelt wird
    			break;
    		}
    	}
    }
}
