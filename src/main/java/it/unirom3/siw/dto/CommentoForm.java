package it.unirom3.siw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CommentoForm {
	@NotBlank(message = "Il commento non può essere vuoto")
	@Size(max = 1000, message = "Il commento non può superare 1000 caratteri")
	private String testo;

	public String getTesto() {
		return testo;
	}

	public void setTesto(String testo) {
		this.testo = testo;
	}
}
