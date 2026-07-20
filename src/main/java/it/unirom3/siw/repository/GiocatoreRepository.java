package it.unirom3.siw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.unirom3.siw.model.Giocatore;
import it.unirom3.siw.model.Squadra;

public interface GiocatoreRepository extends JpaRepository<Giocatore, Long> {

    List<Giocatore> findBySquadra(Squadra squadra);

    List<Giocatore> findByCognomeContainingIgnoreCase(String cognome);

}