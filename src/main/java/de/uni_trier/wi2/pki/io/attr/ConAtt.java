package de.uni_trier.wi2.pki.io.attr;

@SuppressWarnings("rawtypes")
/**
 * Klasse fuer kontinuierliche Attribute
 */
public class ConAtt implements CSVAttribute {
	double value;
	Builder builder;

	/**
	 * Constructor Methode für die Umwandlung von String zu Double
	 * @param value
	 * @param builder
	 */
	public ConAtt(String value, Builder builder) {
		this.value = Double.parseDouble(value);
		this.builder = builder;
	}

	/**
	 * Wert für das Attribut zuweisen und Umwandlung in Double
	 */
	@Override
	public void setValue(Object value) {
		this.value = (double) value;
	}
	
	@Override
	public Object getValue() {
		return this.value;
	}
	
	public Builder getBuilder() {
		return builder;
	}
}
