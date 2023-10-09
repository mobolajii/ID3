package de.uni_trier.wi2.pki.preprocess;

import de.uni_trier.wi2.pki.io.attr.CSVAttribute;
import de.uni_trier.wi2.pki.io.attr.ConAtt;

import java.util.ArrayList;
import java.util.List;

// Class that holds logic for discretizing values.
public class BinningDiscretizer {

    /**
     * Discretizes a collection of examples according to the number of bins and the respective attribute ID.
     *
     * @param numberOfBins Specifies the number of numeric ranges that the data will be split up in.
     * @param examples     The list of examples to discretize.
     * @param attributeId  The ID of the attribute to discretize.
     * @return the list of discretized examples.
     */
    public List<CSVAttribute[]> discretize(int numberOfBins, List<CSVAttribute[]> examples, int attributeId) {
    	// Gueltigkeit des Dataset ueberprüfen
    	if (examples != null && !examples.isEmpty()) {

			// Es duerfen nicht mehr Attribute enthalten sein, als in der Liste stehen
			if (attributeId >= examples.get(0).length || attributeId < 0) {
				return null;
			}

			// Fuer den Fall, dass mehr Bins angegeben werden, als das Attribut Auspraegungen hat
			if (((ConAtt) examples.get(0)[attributeId]).getBuilder().getConVal().size() <= numberOfBins) {
				System.out.println("Es wurden mehr Bins gewaehlt, als es Attributsauspraegungen gibt! Bitte korrigieren.");
			}

			// Nur kontinuierliche Attribute können diskretisiert werden
			for (int i = 0; i < examples.size(); i++) {
    			if (ConAtt.class != examples.get(i)[attributeId].getClass()) {
    				return null;
    			}
    		}

			double minimum = Double.MAX_VALUE; // Initialisierung mit dem hoechsten moeglichen Wert
			double maximum = Double.MIN_VALUE; // Initialisierung mit dem niedrigsten moeglichen Wert

    		// Minimum und Maximum finden um danach die Bins festlegen zu koennen
    		for (int i = 0; i < examples.size(); i++) {
    			ConAtt tempVal = (ConAtt) examples.get(i)[attributeId];
    			if ((double) tempVal.getValue() > maximum) {
    				maximum = (double) tempVal.getValue();
    			} else if ((double) tempVal.getValue() < minimum) {
    				minimum = (double) tempVal.getValue();
    			}
    		}

			// Groeße des Intervals wird festgelegt, indem das Maximum vom Minimum subtrahiert wird und anschließend durch die Anzahl der Bins dividiert
    		double sizeOfInterval = (maximum - minimum) / numberOfBins;
    		double tempVal = minimum + sizeOfInterval;
    		ArrayList<Double> interval = new ArrayList<>();

			// Speicherung der Intervalle
    		for (int i = 0; i < numberOfBins-1; i++) {
    			interval.add(Math.round(tempVal * 100d) / 100d);
    			tempVal += sizeOfInterval;
    		}

			// Anschließend wird das komplette Datset durchgegangen und die Ausgabe für den Baum erstellt
    		for (int i = 0; i < examples.size(); i++) {
    			ConAtt att = (ConAtt) examples.get(i)[attributeId];
    			for (int j = 0; j < interval.size(); j++) {
    				if ((double) att.getValue() <= interval.get(j)) {
    					if ( j == 0) {
    						((ConAtt) examples.get(i)[attributeId]).getBuilder().clearConValue();
    						examples.get(i)[attributeId] = ((ConAtt) examples.get(i)[attributeId]).getBuilder().catAtt("kleiner oder gleich " + interval.get(j));
    						break;
    					} else {
    						((ConAtt) examples.get(i)[attributeId]).getBuilder().clearConValue();
    						examples.get(i)[attributeId] = ((ConAtt) examples.get(i)[attributeId]).getBuilder().catAtt("zwischen " + interval.get(j-1) + "und " + interval.get(j));
    						break;
    					}
    				} else if (j == interval.size()-1) {
    					((ConAtt) examples.get(i)[attributeId]).getBuilder().clearConValue();
    					examples.get(i)[attributeId] = ((ConAtt) examples.get(i)[attributeId]).getBuilder().catAtt("mehr als " + interval.get(j));
    				}
    			}
    		}
    		return examples;
    	}    	
        return null;
    }
}
