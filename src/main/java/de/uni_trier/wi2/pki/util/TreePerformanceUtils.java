package de.uni_trier.wi2.pki.util;

import java.util.ArrayList;
import java.util.List;

import de.uni_trier.wi2.pki.io.attr.CSVAttribute;
import de.uni_trier.wi2.pki.tree.DecisionTreeNode;

public class TreePerformanceUtils {

	@SuppressWarnings("rawtypes")
	public static double[] getTreePerformance(List<CSVAttribute[]> examples, int labelIndex, String positiveLabelValue, DecisionTreeNode root) {
		ArrayList<CSVAttribute[]> dataset = (ArrayList<CSVAttribute[]>) examples;
		double[] performance = new double[3];

		/**
		 * Berechnung der Performance
		 * 0 = Klassifikationsguete
		 * 1 = Praezision
		 * 2 = Recall
		 */
		if(!dataset.isEmpty() && root != null && labelIndex <= dataset.get(0).length) {
			int predictionCount = 0;
			int positiveCount = 0;
			int truePos = 0;
			int falsePos = 0;

			for (int i = 0; i < dataset.size(); i++) {
				DecisionTreeNode currentNode = root;
				while (!currentNode.isLeaf()) {
					int tempAttIndex = currentNode.getAttIndex();
					String tempValue = (String) dataset.get(i)[tempAttIndex].getValue();
					currentNode = currentNode.getNext(tempValue);
				}
				String actualClass = (String) dataset.get(i)[labelIndex].getValue();
				String predictedClass = currentNode.getClassification();
				// Klassifikationsguete
				if (predictedClass.equals(actualClass)) {
					predictionCount++;
				}
				// Recall
				if (actualClass.equals(positiveLabelValue)) {
					positiveCount++;
					if (predictedClass.equals(actualClass)) {
						truePos++;
					}
				// Precision
				} else {
					if (predictedClass.equals(positiveLabelValue)) {
						falsePos++;
					}
				}
			}
			performance[0] = (predictionCount / (double) dataset.size()) ; // Klassifikationsguete
			// performance[] = (predictionCount / (double) dataset.size()) * 100; -> Falls Ausgabe in % gewuenscht ist

			performance[1] = truePos / (double) (truePos + falsePos); // Precision
			//performance[] = (truePos / (double) (truePos + falsePos)) * 100; -> Falls Ausgabe in % gewuenscht ist

			performance[2] = truePos / (double) positiveCount; // Recall
			//performance[] = (truePos / (double) positiveCount) * 100; -> Falls Ausgabe in % gewuenscht ist
				
			return performance;
		}
		return null;
		}

		
	
	/**
	 * 
	 * @param examples
	 * @param labelIndex
	 * @param root
	 * @return
	 */

	@SuppressWarnings("rawtypes")
	public static double getClassAccuracy(List<CSVAttribute[]> examples, int labelIndex, DecisionTreeNode root) {
		return getTreePerformance(examples, labelIndex, null, root)[0];
	}
}
