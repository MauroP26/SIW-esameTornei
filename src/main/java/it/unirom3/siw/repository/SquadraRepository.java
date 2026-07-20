package it.unirom3.siw.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.unirom3.siw.model.Squadra;

public interface SquadraRepository extends JpaRepository<Squadra, Long> {
	List<Squadra> findByCitta(String citta);

	Squadra findByNome(String nome);

	Optional<Squadra> findByNomeIgnoreCase(String nome);

	/**
	 * Strategia di riferimento LAZY: carica le squadre del torneo, ma non le rose.
	 * L'accesso successivo a squadra.getGiocatori() genera una query per squadra.
	 */
	List<Squadra> findByTorneiIdOrderByNomeAsc(Long torneoId);

	/**
	 * Recupera squadre e giocatori con una JOIN FETCH esplicita. DISTINCT elimina i
	 * duplicati di Squadra prodotti dalla join uno-a-molti.
	 */
	@Query("""
			select distinct s
			from Squadra s
			join s.tornei t
			left join fetch s.giocatori
			where t.id = :torneoId
			order by s.nome
			""")
	List<Squadra> findRoseByTorneoIdJoinFetch(@Param("torneoId") Long torneoId);

	/**
	 * Stesso grafo dati della query precedente, ma dichiarato tramite EntityGraph.
	 * La query descrive quali squadre cercare; il grafo stabilisce che giocatori
	 * deve essere caricato insieme alla squadra.
	 */
	@EntityGraph(attributePaths = "giocatori")
	@Query("""
			select distinct s
			from Squadra s
			join s.tornei t
			where t.id = :torneoId
			order by s.nome
			""")
	List<Squadra> findRoseByTorneoIdEntityGraph(@Param("torneoId") Long torneoId);
}
