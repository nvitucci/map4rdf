package es.upm.fi.dia.oeg.map4rdf.share;

/**
 * @author Filip
 */
public class RDFLiteral implements BasicRDFInformation{
	
	private static final long serialVersionUID = 7081274544935536676L;
	private URLSafety dataTypeURI;
	private String lexicalForm;
	private String fullName;
	private Boolean isLiteral;
	
	public RDFLiteral(){
		//for serialization
	}
	
	public RDFLiteral(String dataTypeURI, String lexicalForm, String fullName){
		this.setDataTypeURI(new URLSafety(dataTypeURI));
		this.setLexicalForm(lexicalForm);
		this.fullName = fullName;
	}

	public URLSafety getDataTypeURI() {
		return dataTypeURI;
	}

	public void setDataTypeURI(URLSafety dataTypeURI) {
		this.dataTypeURI = dataTypeURI;
	}

	public String getLexicalForm() {
		return lexicalForm;
	}

	public void setLexicalForm(String lexicalForm) {
		this.lexicalForm = lexicalForm;
	}

	public Boolean getIsLiteral() {
		return isLiteral;
	}

	public void setIsLiteral(Boolean isLiteral) {
		this.isLiteral = isLiteral;
	}

	@Override
	public String getText() {
		return fullName;
	}

	@Override
	public void setText(String text) {
		this.fullName = text;
	}

	@Override
	public Boolean isResource() {
		return false;
	}

	@Override
	public Boolean isLiteral() {
		return true;
	}
}
