package it.unirom3.siw.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.unirom3.siw.model.Utente;
import it.unirom3.siw.repository.UtenteRepository;

@Service
public class UtenteService {

	private UtenteRepository utenteRepo;

	public UtenteService(UtenteRepository u) {
		this.utenteRepo = u;
	}

	@Transactional
	public Utente salva(Utente u) {
		return this.utenteRepo.save(u);
	}

	@Transactional(readOnly = true)
	public List<Utente> getTuttiUtenti() {
		return utenteRepo.findAll();
	}

	@Transactional(readOnly = true)
	public Optional<Utente> getUtenteById(Long id) {
		return utenteRepo.findById(id);
	}

	@Transactional
	public void eliminaUtente(Long id) {
		this.utenteRepo.deleteById(id);
	}

}
