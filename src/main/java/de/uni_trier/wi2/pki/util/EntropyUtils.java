package de.uni_trier.wi2.pki.util;

import de.uni_trier.wi2.pki.io.attr.CSVAttribute;
import de.uni_trier.wi2.pki.io.attr.CatAtt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


@SuppressWarnings("rawtypes")

/**
 * Contains methods that help with computing the entropy.
 */
public class EntropyUtils {

    /**
     * Calculates the information gain for all attributes
     *
     * @param matrix     Matrix of the training data (example data), e.g. ArrayList<String[]>
     * @param labelIndex the index of the attribute that contains the class. If the dataset is [Temperature,Weather,PlayFootball] and you want to predict playing
     *                   football, than labelIndex is 2
     * @return the information gain for each attribute
     */
    public static List<Double> calcInformationGain(Collection<CSVAttribute[]> matrix, int labelIndex) {
    	if (matrix != null && !matrix.isEmpty()) {

			// Aufteilung in die beiden Auspraegungen, die das LabelAttribute annehmen kann (0,1)
			ArrayList<CSVAttribute[]> classificationVal0 = (ArrayList<CSVAttribute[]>) PartUtils.partByValue(matrix, labelIndex).get(0);
    		ArrayList<CSVAttribute[]> classificationVal1 = (ArrayList<CSVAttribute[]>) PartUtils.partByValue(matrix, labelIndex).get(1);
    		
    		if (labelIndex >= classificationVal0.get(0).length || labelIndex < 0) {
    			return null;
    		}
    		// binaerer LabelIndex notwendig
    		if (((CatAtt) classificationVal0.get(0)[labelIndex]).getBuilder().getCatVal().size() != 2) {
    			return null;
    		}
    		// Anzahl der Klassifizierungen pro Auspraegung
    		double countClassification0 = classificationVal0.size();
    		double countClassification1 = classificationVal1.size();

			// Entropy berechnen
    		double entropy = EntropyUtils.calcEntropy(countClassification0, countClassification1);

    		ArrayList<HashMap<String, Integer>> hashMapList1 = new ArrayList<>();
    		ArrayList<HashMap<String, Integer>> hashMapList2 = new ArrayList<>();
    		
    		ArrayList<ArrayList<String>> arrayList = new ArrayList<>();
    		
    		for (int i = 0; i < classificationVal0.get(0).length; i++) {
    			hashMapList1.add(new HashMap<>());
    			hashMapList2.add(new HashMap<>());
    			arrayList.add(new ArrayList<>());
    		}

			// Iteration über die erste Auspraegung
    		for (int i = 0; i < classificationVal0.get(0).length; i++) {
    			for (CSVAttribute[] attributes : classificationVal0) {
    				if (i != labelIndex) {
    				String currentKey = (String) attributes[i].getValue();
    				if (hashMapList1.get(i).containsKey(currentKey)) {
    					hashMapList1.get(i).put(currentKey, hashMapList1.get(i).get(currentKey) + 1);
    				} else {
    					hashMapList1.get(i).put(currentKey, 1);
    					if (!arrayList.get(i).contains(currentKey)) {
    						arrayList.get(i).add(currentKey);
    					}
    				}
    			}
    		}

			// Iteration über die zweite Auspraegung
    		for (CSVAttribute[] csvAttributes : classificationVal1) {
    			if (i != labelIndex) {
    				String currentKey = (String) csvAttributes[i].getValue();
    				if (hashMapList2.get(i).containsKey(currentKey)) {
    					hashMapList2.get(i).put(currentKey, hashMapList2.get(i).get(currentKey) + 1);
    				} else {
    					hashMapList2.get(i).put(currentKey, 1);
    					if (!arrayList.get(i).contains(currentKey)) {
    						arrayList.get(i).add(currentKey);
    					}
    				}
    			}
    		}    		
    	}
        // Output für den Informationsgehalt
    	List<Double> infoList = new ArrayList<>();

		// Berechnung des Informationsgewinns
    	for (int i = 0; i < classificationVal0.get(0).length; i++) {
    		if (i != labelIndex) {
    			
    			ArrayList<String> stringArrayList = arrayList.get(i);
    			ArrayList<Double> entropyArrayList = new ArrayList<>();
    			ArrayList<Double> count0ArrayList = new ArrayList<>();
    			ArrayList<Double> count1ArrayList = new ArrayList<>();
    			
    			HashMap<String, Integer> hashMap1 = hashMapList1.get(i);
    			HashMap<String, Integer> hashMap2 = hashMapList2.get(i);
    			
    			for (String currentKey : stringArrayList) {
    				double number1 = hashMap1.getOrDefault(currentKey, 0);
    				double number2 = hashMap2.getOrDefault(currentKey, 0);

					// Entropy berechnen
    				double useEntropy = EntropyUtils.calcEntropy(number1, number2);
    				
    				count0ArrayList.add(number1);
    				count1ArrayList.add(number2);
    				entropyArrayList.add(useEntropy);
    			}
    			
    			double rest = 0;
    			for (int j = 0; j < count0ArrayList.size(); j++) {
    				rest += ((count0ArrayList.get(j) + count1ArrayList.get(j)) / (countClassification0 + countClassification1)) * entropyArrayList.get(j);
    			}
    			double infoGain = entropy - rest;
    			infoList.add(infoGain);
    		} else {
    			infoList.add(-1.0);
    		}
    	}	
    	return infoList;
    	}
        return null;
    }

    public static double lg(double x) {
    	return Math.log(x) / Math.log(2);
    }
    
	private static double calcEntropy(double number1, double number2) {
		if (number1 == 0 && number2 == 0) {
			return 0;
		} else if (number1 == 0) {
			return -(number2 / (number1 + number2)) * lg(number2 / (number1 + number2));
		} else if (number2 == 0) {
			return -(number1 / (number1 + number2)) * lg(number1 / (number1 + number2));
		}
		return -(number1 / (number1 + number2)) * lg(number1 / (number1 + number2)) - (number2 / (number1 + number2)) * lg(number2 / (number1 + number2));
	}
}
