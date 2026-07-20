package it.unirom3.siw.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import it.unirom3.siw.model.Partita;
import it.unirom3.siw.model.Torneo;

public interface PartitaRepository extends JpaRepository<Partita, Long> {
	List<Partita> findByTorneo(Torneo torneo);

	List<Partita> findByDataOraAfter(LocalDateTime data);

	List<Partita> findByTorneoIdOrderByDataOraAsc(Long torneoId);

	List<Partita> findAllByOrderByDataOraAsc();

	boolean existsByHomeIdOrAwayId(Long homeId, Long awayId);

	boolean existsByArbitroId(Long arbitroId);
}
