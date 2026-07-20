package it.unirom3.siw.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import it.unirom3.siw.model.Commento;

public interface CommentoRepository extends JpaRepository<Commento, Long> {

    @EntityGraph(attributePaths = "autore.cred")
    List<Commento> findByPartitaIdOrderByCreatoIlAsc(Long partitaId);

    Optional<Commento> findByPartitaIdAndAutoreCredUsername(Long partitaId, String username);
}
