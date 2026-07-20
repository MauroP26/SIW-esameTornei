package it.unirom3.siw.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import it.unirom3.siw.model.Torneo;

public interface TorneoRepository extends JpaRepository<Torneo, Long> {
	Optional<Torneo> findByNome(String nome);

	Optional<Torneo> findByNomeIgnoreCaseAndAnno(String nome, int anno);
}
