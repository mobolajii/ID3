package de.uni_trier.wi2.pki.io.attr;

import java.util.ArrayList;

//Instanzfeld der unterschiedlichen Attribute
public class Builder {
	private final String header;
	private final ArrayList<String> catVal = new ArrayList<>();
	private final ArrayList<String> conVal = new ArrayList<>();

	/**
	 * Konstruktormethode: Initialiserung der Instanz
	 *
	 * @param header
	 */
	public Builder(String header) {
		this.header = header;
	}

	/**
	 * Objekt der Klasse ConAtt (d.h. die kontinuierlichen Attribute)  werden erstellt
	 *
	 * @param value
	 * @return
	 */
	public ConAtt conAtt(String value) {
		storeConValue(value);
		return new ConAtt(value, this);
	}

    /** 
     * Methode, die den ert speichert, wenn conVal nicht in value enthalten ist
     * Wenn dies der Fall ist, wird value hinzugefuegt
	 *
     * @param value
     */
	private void storeConValue(String value) {
		if (!conVal.contains(value)) {
			conVal.add(value);
		}
	}

	public CatAtt catAtt(String value) {
		storeCatValue(value);
		return new CatAtt(value, this);
	}

	private void storeCatValue(String value) {
		if (!catVal.contains(value)) {
			catVal.add(value);
		}
	}

    /**
     * Speicherplatz schaffen für spaeter
     */
	public void clearConValue() {
		conVal.clear();
	}


	/**
	 * @return Liste mit den kategorischen Werten zurueckgeben
	 */
	public ArrayList<String> getCatVal() {
		return catVal;
	}

	/**
	 * @return Liste mit den kategorischen Werten zurueckgeben
	 */
	public ArrayList<String> getConVal() {
		return conVal;
	}

	/**
	 * @return Den Namen des Attributes / Spaltenname
	 */
	public String getHeader() {
		return header;
	}
}
