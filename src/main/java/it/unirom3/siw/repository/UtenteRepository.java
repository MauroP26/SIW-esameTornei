package it.unirom3.siw.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import it.unirom3.siw.model.Utente;

public interface UtenteRepository extends JpaRepository<Utente, Long> {
    Optional<Utente> findByCredUsername(String username);
}
