package it.unirom3.siw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ArbitroForm {
	@NotBlank(message = "Il nome è obbligatorio")
	@Size(max = 80, message = "Il nome non può superare 80 caratteri")
	private String nome;
	@NotBlank(message = "Il cognome è obbligatorio")
	@Size(max = 80, message = "Il cognome non può superare 80 caratteri")
	private String cognome;
	@NotBlank(message = "Il codice arbitrale è obbligatorio")
	@Size(max = 40, message = "Il codice arbitrale non può superare 40 caratteri")
	private String codiceArbitrale;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getCodiceArbitrale() {
		return codiceArbitrale;
	}

	public void setCodiceArbitrale(String codiceArbitrale) {
		this.codiceArbitrale = codiceArbitrale;
	}
}
