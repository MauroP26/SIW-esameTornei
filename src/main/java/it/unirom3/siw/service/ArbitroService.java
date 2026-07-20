package it.unirom3.siw.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import it.unirom3.siw.exception.RegolaBusinessException;
import it.unirom3.siw.exception.RisorsaNonTrovataException;
import it.unirom3.siw.model.Arbitro;
import it.unirom3.siw.repository.ArbitroRepository;
import it.unirom3.siw.repository.PartitaRepository;

@Service
public class ArbitroService {
	private final ArbitroRepository arbitroRepository;
	private final PartitaRepository partitaRepository;

	public ArbitroService(ArbitroRepository a, PartitaRepository p) {
		this.arbitroRepository = a;
		this.partitaRepository = p;
	}

	@Transactional
	public Arbitro crea(String nome, String cognome, String codice) {
		if (arbitroRepository.findByCodiceArbitrale(codice.trim()).isPresent())
			throw new RegolaBusinessException("Codice arbitrale già presente");
		Arbitro a = new Arbitro();
		a.setNome(nome.trim());
		a.setCognome(cognome.trim());
		a.setCodiceArbitrale(codice.trim());
		return arbitroRepository.save(a);
	}

	@Transactional
	public Arbitro modifica(Long id, String nome, String cognome, String codice) {
		Arbitro a = getArbitroById(id);
		arbitroRepository.findByCodiceArbitrale(codice.trim()).filter(x -> !x.getId().equals(id)).ifPresent(x -> {
			throw new RegolaBusinessException("Codice arbitrale già presente");
		});
		a.setNome(nome.trim());
		a.setCognome(cognome.trim());
		a.setCodiceArbitrale(codice.trim());
		return arbitroRepository.save(a);
	}

	@Transactional(readOnly = true)
	public Arbitro getArbitroById(Long id) {
		return arbitroRepository.findById(id).orElseThrow(() -> new RisorsaNonTrovataException("Arbitro non trovato"));
	}

	@Transactional(readOnly = true)
	public List<Arbitro> getAllArbitri() {
		return arbitroRepository.findAll();
	}

	@Transactional
	public void eliminaArbitro(Long id) {
		getArbitroById(id);
		if (partitaRepository.existsByArbitroId(id))
			throw new RegolaBusinessException("L'arbitro non può essere eliminato perché è associato a partite");
		arbitroRepository.deleteById(id);
	}
}
