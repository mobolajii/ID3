package de.uni_trier.wi2.pki;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

import de.uni_trier.wi2.pki.io.CSVReader;
import de.uni_trier.wi2.pki.io.XMLWriter;
import de.uni_trier.wi2.pki.io.attr.Builder;
import de.uni_trier.wi2.pki.io.attr.CSVAttribute;
import de.uni_trier.wi2.pki.postprocess.CrossValidator;
import de.uni_trier.wi2.pki.preprocess.BinningDiscretizer;
import de.uni_trier.wi2.pki.tree.DecisionTreeNode;
import de.uni_trier.wi2.pki.util.ID3Utils;
import de.uni_trier.wi2.pki.util.PartUtils;
import de.uni_trier.wi2.pki.util.TreePerformanceUtils;


public class Main {

    @SuppressWarnings("rawtypes")
	public static void main(String[] args) {
    	try {
    		 List<String[]> input = CSVReader.readCsvToArray("src/main/resources/churn_data.csv", ";", false);
    		 
    		 
    		 ArrayList<Builder> builders = new ArrayList<>();
    		 for(int i = 0; i < input.get(0).length; i++) {
    			 builders.add(new Builder(input.get(0)[i]));
    		 }
    		 
    		 input.remove(0);
    		 
    		 List<CSVAttribute[]> dataset = new ArrayList<>();
    		 
    		 for(int i = 0; i < input.size(); i++) {
    			 CSVAttribute[] row = new CSVAttribute[11];
    			 row[0] = builders.get(0).conAtt(input.get(i)[0]); // 1. CreditScore
    			 row[1] = builders.get(1).catAtt(input.get(i)[1]); // 2. Geography
    			 row[2] = builders.get(2).catAtt(input.get(i)[2]); // 3. Gender
    			 row[3] = builders.get(3).conAtt(input.get(i)[3]); // 4. Age
    			 row[4] = builders.get(4).conAtt(input.get(i)[4]); // 5. Tenure
    			 row[5] = builders.get(5).conAtt(input.get(i)[5]); // 6. Balance
    			 row[6] = builders.get(6).catAtt(input.get(i)[6]); // 7. NumOfProducts
    			 row[7] = builders.get(7).catAtt(input.get(i)[7]); // 8. HasCrCard
    			 row[8] = builders.get(8).catAtt(input.get(i)[8]); // 9. IsActiveMember
    			 row[9] = builders.get(9).conAtt(input.get(i)[9]); // 10. EstimatedSalary
    			 row[10] = builders.get(10).catAtt(input.get(i)[10]); // 11. Exited + Label
    			 
    			 dataset.add(row);
    		 }
    		 
    		 BinningDiscretizer binningDiscretizer = new BinningDiscretizer();
    		 dataset = binningDiscretizer.discretize(4, dataset, 0); // CreditScore
    		 dataset = binningDiscretizer.discretize(4, dataset, 3); // Age
    		 dataset = binningDiscretizer.discretize(4, dataset, 4); // Tenure
    		 dataset = binningDiscretizer.discretize(4, dataset, 5); // Balance
    		 //dataset = binningDiscretizer.discretize(4, dataset, 6); // NumOfProducts, weil kontinuierlich, aber eigentlich nur 4 Auspraegungen
    		 dataset = binningDiscretizer.discretize(4, dataset, 9); // EstimatedSalary
    		 
    		 ArrayList<Collection<CSVAttribute[]>> useDataset = PartUtils.partByNumber(dataset, 2);
    		 ArrayList<Collection<CSVAttribute[]>> testDataset = PartUtils.partByPercent(useDataset.get(1), 40);
    		 
    		 ArrayList<CSVAttribute[]> treeCreationDataset = (ArrayList<CSVAttribute[]>) useDataset.get(0); // 50 % fuer Baumerstellung
    		 ArrayList<CSVAttribute[]> prunDataset = (ArrayList<CSVAttribute[]>) testDataset.get(0); // 20 % fuer Prunning
    		 ArrayList<CSVAttribute[]> restTestDataset = (ArrayList<CSVAttribute[]>) testDataset.get(1); // 30 % für Test der Genauigkeit
    		 
    		 DecisionTreeNode decisionTree = ID3Utils.generateTree(treeCreationDataset, 10); // Baum erstellen

			 DecisionTreeNode crossValidation = CrossValidator.performCrossValidation(treeCreationDataset, 10, generateTree, 5);
    		 
    		 NumberFormat formate = new DecimalFormat("#0.0000");
    		 double[] treePerformance = TreePerformanceUtils.getTreePerformance(restTestDataset, 10, "1", crossValidation);
    		 System.out.println("Baum vor dem Pruning: ");
    		 System.out.println("Klassifikationsguete: " + formate.format(treePerformance[0]));
    		 System.out.println("Precision: " + formate.format(treePerformance[1]));
    		 System.out.println("Recall: " + formate.format(treePerformance[2]));
    		 System.out.println("Anzahl der Knoten: " + ID3Utils.getTSize(crossValidation) + "\n");
    		 
    		 DecisionTreeNode prunedTree = CrossValidator.performCrossValidation(treeCreationDataset, 10, generateTree, prunDataset, 5);
    		 
    		 treePerformance = TreePerformanceUtils.getTreePerformance(restTestDataset, 10, "1", prunedTree);
    		 System.out.println("Baum nach dem Pruning: ");
    		 System.out.println("Klassifikationsguete: " + formate.format(treePerformance[0]));
    		 System.out.println("Precision: " + formate.format(treePerformance[1]));
    		 System.out.println("Recall: " + formate.format(treePerformance[2]));
    		 System.out.println("Anzahl der Knoten: " + ID3Utils.getTSize(prunedTree));
    		 
    		 XMLWriter.writeXML("decisionTree.xml", prunedTree); 		 
    		 
    		
    	} catch (Exception e) { 
    		e.printStackTrace();
    	}
    }

	// Fuer CrossValidation
    static BiFunction<List<CSVAttribute[]>, Integer, DecisionTreeNode> generateTree = (x, y) -> {
    	try {
    		return ID3Utils.generateTree(x, y);
    	} catch (Exception e) {
    		System.out.println(e);
    		e.printStackTrace();
    	}
    	return null;
    };
}
