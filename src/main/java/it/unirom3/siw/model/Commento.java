package it.unirom3.siw.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "autore_id", "partita_id" }))
public class Commento {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 1000)
	private String testo;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Utente autore;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Partita partita;

	@Column(nullable = false, updatable = false)
	private LocalDateTime creatoIl;

	@Column(nullable = false)
	private LocalDateTime modificatoIl;

	@PrePersist
	void prePersist() {
		LocalDateTime adesso = LocalDateTime.now();
		this.creatoIl = adesso;
		this.modificatoIl = adesso;
	}

	@PreUpdate
	void preUpdate() {
		this.modificatoIl = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTesto() {
		return testo;
	}

	public void setTesto(String testo) {
		this.testo = testo;
	}

	public Utente getAutore() {
		return autore;
	}

	public void setAutore(Utente autore) {
		this.autore = autore;
	}

	public Partita getPartita() {
		return partita;
	}

	public void setPartita(Partita partita) {
		this.partita = partita;
	}

	public LocalDateTime getCreatoIl() {
		return creatoIl;
	}

	public LocalDateTime getModificatoIl() {
		return modificatoIl;
	}
}
