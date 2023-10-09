package de.uni_trier.wi2.pki.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.uni_trier.wi2.pki.io.attr.CSVAttribute;
import de.uni_trier.wi2.pki.io.attr.CatAtt;

@SuppressWarnings("rawtypes")

public class PartUtils {

	/**
	 * Unterteilung der Collection in mehrere Teile auf Basis von bestimmten Attributen
	 * @param examples
	 * @param valueAttributeIndex
	 * @return
	 */
	
	public static ArrayList<Collection<CSVAttribute[]>> partByValue(Collection<CSVAttribute[]> examples, int valueAttributeIndex) {
		if (examples != null && !examples.isEmpty()) {
			ArrayList<CSVAttribute[]> dataset = (ArrayList<CSVAttribute[]>) examples;
			ArrayList<Collection<CSVAttribute[]>> output = new ArrayList<>();
			
			if (valueAttributeIndex >= dataset.get(0).length || valueAttributeIndex < 0) {
				return null;
			}
			
			ArrayList<String> differentAttValue = ((CatAtt) dataset.get(0)[valueAttributeIndex]).getBuilder().getCatVal();
			
			for (int i = 0; i < differentAttValue.size(); i++) {
				ArrayList<CSVAttribute[]> tempArrayList = new ArrayList<>();
				output.add(tempArrayList);
			}
			
			for (int i = 0; i < dataset.size(); i++) {
				String tempString = (String) dataset.get(i)[valueAttributeIndex].getValue();
				int tempIndex = differentAttValue.indexOf(tempString);
				output.get(tempIndex).add(dataset.get(i));
			}
			return output;			
		}	
		return null;
	}

	/**
	 * Aufteilung in mehrere disjunkte Subsets von gleicher Groeße
	 * @param examples
	 * @param numOfPartition
	 * @return
	 */
	public static ArrayList<Collection<CSVAttribute[]>> partByNumber(Collection<CSVAttribute[]> examples, int numOfPartition) {
		
		if (examples != null && !examples.isEmpty()) {
			ArrayList<CSVAttribute[]> dataset = (ArrayList<CSVAttribute[]>) examples;
			ArrayList<Collection<CSVAttribute[]>> output = new ArrayList<>();
			
			if (numOfPartition > dataset.size() || numOfPartition <= 1) {
				return null;
			}
			
			int sizeOfSubset = (dataset.size() / numOfPartition);
			int start = 0;
			
			for (int i = 0; i < numOfPartition; i++) {
				List<CSVAttribute[]> subset; 
				if ( i == numOfPartition - 1) {
					subset = new ArrayList<>(dataset.subList(start, dataset.size()));
				} else {
					subset = new ArrayList<>(dataset.subList(start, start + sizeOfSubset));
				}
				output.add(subset);
				start += sizeOfSubset;
			}
			return output;
		}		
		return null;
	}

	/**
	 * Aufteilung in mehrere Subsets nach Vorgabe in der Aufgabenstelleung (50 % zur Baumerstellung, 30 % zum Testen und 20 % zum Prunen)
	 * @param examples
	 * @param percentNewDataset
	 * @return
	 */
	public static ArrayList<Collection<CSVAttribute[]>> partByPercent(Collection<CSVAttribute[]> examples, int percentNewDataset) {
		if (examples != null && !examples.isEmpty()) {
			ArrayList<CSVAttribute[]> newDataset = (ArrayList<CSVAttribute[]>) examples;
			ArrayList<Collection<CSVAttribute[]>> output = new ArrayList<>();
			
			if (percentNewDataset >= 100 || percentNewDataset <= 0) {
				return null;
			}
			
			int sizeOfSubset = (int) (newDataset.size() * ((double) percentNewDataset / 100));
			int start = 0;
			
			for (int i = 0; i < 2; i++ ) {
				List<CSVAttribute[]> subset;
				if (i == 1) {
					// erstes Subset
					subset = new ArrayList<>(newDataset.subList(start, newDataset.size()));
				} else {
					// zweites Subset
					subset = new ArrayList<>(newDataset.subList(start, start + sizeOfSubset));
				}
				output.add(subset);
				start += sizeOfSubset;
			}
			return output;
		}
		return null;
	}
}
