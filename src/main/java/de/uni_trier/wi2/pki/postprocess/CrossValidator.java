package de.uni_trier.wi2.pki.postprocess;

import de.uni_trier.wi2.pki.io.attr.CSVAttribute;
import de.uni_trier.wi2.pki.tree.DecisionTreeNode;
import de.uni_trier.wi2.pki.util.PartUtils;
import de.uni_trier.wi2.pki.util.TreePerformanceUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

//Contains util methods for performing a cross-validation.

public class CrossValidator {

    /**
     * Performs a cross-validation with the specified dataset and the function to train the model.
     *
     * @param dataset        the complete dataset to use.
     * @param labelAttribute the label attribute.
     * @param trainFunction  the function to train the model with.
     * @param numFolds       the number of data folds.
     * @return bestDecision der Baum mit der besten Abschaetzung
     *
     */
    @SuppressWarnings("rawtypes")
	public static DecisionTreeNode performCrossValidation(List<CSVAttribute[]> dataset, int labelAttribute,
                                                          BiFunction<List<CSVAttribute[]>, Integer, DecisionTreeNode> trainFunction,
                                                          int numFolds) {
    	//
    	ArrayList<Collection<CSVAttribute[]>> subset = PartUtils.partByNumber(dataset, numFolds);
    	DecisionTreeNode bestDecision = null; // Variable fuer den besten Baum
    	double bestAccuracy = Double.MIN_VALUE; // Genauigkeitsberechnung mit der geringsten Laufzeit
    	double tempAccuracy; // aktuell betrachtete Genauigkeitsberechnung

		// Aufteilung in die angegebenen Subsets und jedes in neue Liste speichern
    	for (int i = 1; i < numFolds; i++) {
    		ArrayList<CSVAttribute[]> currentList = new ArrayList<>();
    		for (int j = 0; j < subset.size(); j++) {
    			if (j != i) {
    				currentList.addAll(subset.get(j));
    			}
    		}

			// fuer die erstellten Subsets einen Baum trainieren
			// Genauigkeit berechnen
    		DecisionTreeNode tree = trainFunction.apply(currentList, labelAttribute);
    		tempAccuracy = TreePerformanceUtils.getClassAccuracy((List<CSVAttribute[]>) subset.get(i), labelAttribute, tree);
			// Accuracies vergleichen und die beste zurueckgeben
			if (tempAccuracy > bestAccuracy) {
    			bestAccuracy = tempAccuracy;
    			bestDecision = tree;
    		}
    	}
    	return bestDecision;
    }
    
    /**
     * Durchfuehrung der CrossValidation mit dem pruned Datasets
	 * Dadurch soll ueberprueft werden, ob sich die Performance verbessert hat
     * 
     * @param dataset        the complete dataset to use.
     * @param labelAttribute the label attribute.
     * @param trainFunction  the function to train the model with.
     * @param numFolds       the number of data folds.
     * @return bestDecision
     *
     */

	@SuppressWarnings("rawtypes")
	public static DecisionTreeNode performCrossValidation(List<CSVAttribute[]> dataset, int labelAttribute,
														  BiFunction<List<CSVAttribute[]>, Integer, DecisionTreeNode> trainFunction,
														  List<CSVAttribute[]> pruningDataset, int numFolds) throws Exception {

		ArrayList<Collection<CSVAttribute[]>> subset = PartUtils.partByNumber(dataset, numFolds);
		DecisionTreeNode bestDecision = null;
		double bestAccuracy = Double.MIN_VALUE;
		double tempAccuracy;

		// Aufteilung
		for (int i = 1; i < numFolds; i++) {
			ArrayList<CSVAttribute[]> currentList = new ArrayList<>();
			for (int j = 0; j < subset.size(); j++) {
				if (j != i) {
					currentList.addAll(subset.get(j));
				}
			}
			// Baum trainieren
			DecisionTreeNode tree = trainFunction.apply(currentList, labelAttribute);
			// Den Baum prunen um ein overfitting der trainFunction zu vermeiden
			ReducedErrorPruner pruning = new ReducedErrorPruner();
			pruning.prune(tree, pruningDataset, labelAttribute);
			tempAccuracy = TreePerformanceUtils.getClassAccuracy((List<CSVAttribute[]>) subset.get(i), labelAttribute, tree);
			if (tempAccuracy > bestAccuracy) {
				bestAccuracy = tempAccuracy;
				bestDecision = tree;
			}
		}
		return bestDecision;
	}
}
