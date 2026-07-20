package it.unirom3.siw.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SquadraForm {
	@NotBlank(message = "Il nome è obbligatorio")
	@Size(max = 100)
	private String nome;
	@Min(value = 1800, message = "Anno non valido")
	@Max(value = 2100, message = "Anno non valido")
	private int annoFondazione;
	@NotBlank(message = "La città è obbligatoria")
	@Size(max = 100)
	private String citta;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getAnnoFondazione() {
		return annoFondazione;
	}

	public void setAnnoFondazione(int annoFondazione) {
		this.annoFondazione = annoFondazione;
	}

	public String getCitta() {
		return citta;
	}

	public void setCitta(String citta) {
		this.citta = citta;
	}
}
