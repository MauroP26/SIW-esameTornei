package it.unirom3.siw.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import it.unirom3.siw.model.Arbitro;

public interface ArbitroRepository extends JpaRepository<Arbitro, Long> {

    Optional<Arbitro> findByCodiceArbitrale(String codiceArbitrale);

}
