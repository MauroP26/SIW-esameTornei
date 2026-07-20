package it.unirom3.siw.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import it.unirom3.siw.model.Credenziali;

public interface CredenzialiRepository extends JpaRepository<Credenziali, Long>{
	
	public Optional<Credenziali> findByUsername(String n);
	
	public boolean existsByUsername(String n);

}
