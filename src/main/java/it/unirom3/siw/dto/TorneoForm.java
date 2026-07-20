package it.unirom3.siw.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TorneoForm {
	@NotBlank(message = "Il nome è obbligatorio")
	@Size(max = 100, message = "Il nome può contenere al massimo 100 caratteri")
	private String nome;
	@Min(value = 1900, message = "L'anno deve essere almeno 1900")
	@Max(value = 2100, message = "L'anno non può superare 2100")
	private int anno;
	@Size(max = 1000, message = "La descrizione può contenere al massimo 1000 caratteri")
	private String descrizione;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getAnno() {
		return anno;
	}

	public void setAnno(int anno) {
		this.anno = anno;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
}
