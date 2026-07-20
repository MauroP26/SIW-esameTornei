package it.unirom3.siw.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.unirom3.siw.model.Credenziali;
import it.unirom3.siw.repository.CredenzialiRepository;

@Service
public class CredenzialiService {

	private final CredenzialiRepository credenzialiRepository;

	public CredenzialiService(CredenzialiRepository credenzialiRepository) {
		this.credenzialiRepository = credenzialiRepository;
	}

	@Transactional(readOnly = true)
	public Credenziali getCredenziali(String username) {
		return credenzialiRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("Credenziali non trovate"));
	}

	@Transactional(readOnly = true)
	public boolean existsByUsername(String username) {
		return credenzialiRepository.existsByUsername(username);
	}

}