package de.uni_trier.wi2.pki.io.attr;

@SuppressWarnings("rawtypes")

/**
 * Klasse für die kategorischen Attribute
 * @implements greift auf die Schnittstelle CSVAttribute zu und übernimmt hinterlegte Datentypen (Teil einer generischen Implementierung)
 */
public class CatAtt implements CSVAttribute {
	String value;
	Builder builder;

	/**
	 * Constructor
	 *
	 * @param value
	 * @param builder
	 */
	public CatAtt(String value, Builder builder) {
		this.value = value;
		this.builder = builder;
	}

	/**
	 * @Override weil damit die eingelesenen Werte aus CSVAttribute(Superclass) überschrieben werden können
	 *
	 * Set Wert für das Attribut
	 *
	 * @param value the value
	 */
	@Override
	public void setValue(Object value) {
		this.value = (String) value;
	}

	/**
	 * Get Wert für Attribut
	 *
	 * @return
	 */
	@Override
	public Object getValue() {
		return value;
	}

	/**
	 * Get für Attribut Builder
	 *
	 * @return builder
	 */
	public Builder getBuilder() {
		return builder;
	}
}
