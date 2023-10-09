package de.uni_trier.wi2.pki.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Is used to read in CSV files.
 */
public class CSVReader {

    /**
     * Read a CSV file and return a list of string arrays.
     *
     * @param relativePath the path where the CSV file is located (has to be relative path!)
     * @param delimiter    the delimiter symbol which is used in the CSV
     * @param ignoreHeader A boolean that indicates whether to ignore the header line or not, i.e., whether to include the first line into the list or not
     * @return A list that contains string arrays. Each string array stands for one parsed line of the CSV file
     * @throws IOException if something goes wrong. Exception should be handled at the calling function.
     */
    public static List<String[]> readCsvToArray(String relativePath, String delimiter, boolean ignoreHeader) throws IOException {
		//Gueltigkeit der eingelesenen Datei ueberpruefen neue Liste erstellen für die Ausgabe

		if (relativePath != null && delimiter != null) {
    		File csvInput = new File(relativePath);
    		List<String[]> output = new ArrayList<>();

			// BufferedReader nutzen um die Daten zeilenweise einzulesen
    		if (csvInput.exists()) {
    			BufferedReader bReader = new BufferedReader(new FileReader(csvInput));
    			String line;

				// Header ignorieren, weil dieser nicht notwendig ist
    			if (ignoreHeader) {
    				bReader.readLine();
    			}

				// So lange noch Zeilen vorhanden sind, teilt der StringTokenizer in einzelne Elemente ein (anhand Delimiter)
				// Daten werden anschließend in String Array gespeichert
				// und der Ausgabeliste hinzugefügt
    			while ((line = bReader.readLine()) != null) {
    				StringTokenizer stringToken = new StringTokenizer(line, delimiter);
    				ArrayList<String> temp = new ArrayList<>();

					// so lange noch Zeilen zum Einlesen vorhanden sind,
    				while (stringToken.hasMoreTokens()) {
    					temp.add(stringToken.nextToken());
    				}
    				output.add(Arrays.copyOf(temp.toArray(), temp.toArray().length, String[].class));
    			}
    			bReader.close(); // BufferedReader muss geschlossen werden
    		} 
    		return output;
    	}
        return null;
    }
}
