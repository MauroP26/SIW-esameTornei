package it.unirom3.siw.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Torneo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Il nome del torneo è obbligatorio")
	@Size(max = 100)
	private String nome;
	@Min(1900)
	@Max(2100)
	private int anno;
	@Size(max = 1000)
	private String descrizione;

	@ManyToMany
	@JoinTable(name = "torneo_squadre", joinColumns = @JoinColumn(name = "torneo_id"), inverseJoinColumns = @JoinColumn(name = "squadra_id"))
	private List<Squadra> squadre = new ArrayList<>();

	@OneToMany(mappedBy = "torneo", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Partita> partite = new ArrayList<>();

	public void aggiungiSquadra(Squadra squadra) {
		if (!squadre.contains(squadra))
			squadre.add(squadra);
		if (!squadra.getTornei().contains(this))
			squadra.getTornei().add(this);
	}

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

	public List<Squadra> getSquadre() {
		return squadre;
	}

	public void setSquadre(List<Squadra> squadre) {
		this.squadre = squadre == null ? new ArrayList<>() : squadre;
	}

	public List<Partita> getPartite() {
		return partite;
	}

	public void setPartite(List<Partita> partite) {
		this.partite = partite == null ? new ArrayList<>() : partite;
	}
}
