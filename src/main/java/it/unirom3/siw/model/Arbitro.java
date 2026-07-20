package it.unirom3.siw.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Arbitro {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotBlank
	@Size(max = 80)
	private String nome;
	@NotBlank
	@Size(max = 80)
	private String cognome;
	@Column(nullable = false, unique = true)
	@NotBlank
	@Size(max = 40)
	private String codiceArbitrale;
	@OneToMany(mappedBy = "arbitro")
	private List<Partita> partite = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public List<Partita> getPartite() {
		return partite;
	}

	public void setPartite(List<Partita> partite) {
		this.partite = partite == null ? new ArrayList<>() : partite;
	}
}
