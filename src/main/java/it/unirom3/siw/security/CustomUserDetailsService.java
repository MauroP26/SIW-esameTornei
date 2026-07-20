package it.unirom3.siw.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import it.unirom3.siw.model.Utente;
import it.unirom3.siw.repository.UtenteRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	private final UtenteRepository utenteRepository;

	public CustomUserDetailsService(UtenteRepository utenteRepository) {
		this.utenteRepository = utenteRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Utente utente = utenteRepository.findByCredUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Utente non trovato"));
		return User.builder().username(utente.getCred().getUsername()).password(utente.getCred().getPassword())
				.roles(utente.getCred().getRole().name()).build();
	}
}
