package de.uni_trier.wi2.pki.io;

import de.uni_trier.wi2.pki.tree.DecisionTreeNode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * Serializes the decision tree in form of an XML structure.
 */
public class XMLWriter {

    /**
     * Serialize decision tree to specified path.
     *
     * @param path         the path to write to.
     * @param decisionTree the tree to serialize.
     * @throws IOException if something goes wrong.
     */
    public static void writeXML(String path, DecisionTreeNode decisionTree) throws IOException {
    	Element firstShift = new Element("DecisionTree"); // Baum "erstellen"
    	LinkedList<DecisionTreeNode> nodes = new LinkedList<>(); //Liste für die Knoten
    	LinkedList<Element> link = new LinkedList<>();	// Liste fuer die einzelnen ELemente
    	nodes.add(decisionTree); // erster Knoten wird hinzugefuegt
    	link.add(firstShift);

		// Fuer den Fall, dass die Liste der Knoten leer ist, ist die Baumerstellung beendet
		// ansonsten wird die Schleife so lange durchlaufen, bis alle Knoten betrachtet worden sind
    	while (!nodes.isEmpty()) {
    		DecisionTreeNode actualNode = nodes.pop();
			// Wenn es sich um ein Blatt handelt, kann keine weitere Verzweigen erfolgen
			// ansonsten wird ein neues XML Element erstellt
    		if (!actualNode.isLeaf()) {
    			Element nodeAttribute = new Element("Node");
    			nodeAttribute.setAttribute("attribute", actualNode.getAttribute());
				// Wenn die Liste nicht leer ist, dann wird ein Link zum Attribut erstellt
    			if (!link.isEmpty()) {
    				link.pop().addContent(nodeAttribute);
    			}

				// Solange der Index kleiner ist als die Kinder-Knoten, wird ein neues Element erstellt
				// Kinder werden dann in der Liste hinzugefügt
    			for (int i = 0; i < actualNode.getChildren().size(); i++) {
    				Element nextShift = new Element("IF");
    				nextShift.setAttribute("value", actualNode.getValuesForChildren().get(i));
    				nodeAttribute.addContent(nextShift);
    				link.add(nextShift);
    				nodes.add(actualNode.getChildren().get(i));
    			}
			  // Erstellung des Blattknotens und Hinzufügen des Links
    		} else {
    			Element thirdShift = new Element("LeafNode");
    			thirdShift.setAttribute("class", actualNode.getClassification());
    			if (!link.isEmpty()) {
    				link.pop().addContent(thirdShift);
    			}
    		}
    	}
		// Dokument erstellen und als XML ausgeben
    	Document ended = new Document(firstShift);
    	XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
    	try(FileWriter fileWrite = new FileWriter(path)) {
    		xmlOutput.output(ended, fileWrite);
    	}
    }
}
