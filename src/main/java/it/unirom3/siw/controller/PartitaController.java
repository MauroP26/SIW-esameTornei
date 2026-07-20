package it.unirom3.siw.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.unirom3.siw.dto.PartitaForm;
import it.unirom3.siw.dto.ArbitroForm;
import it.unirom3.siw.dto.RisultatoForm;
import it.unirom3.siw.exception.RegolaBusinessException;
import it.unirom3.siw.exception.RisorsaNonTrovataException;
import it.unirom3.siw.model.Partita;
import it.unirom3.siw.service.ArbitroService;
import it.unirom3.siw.service.CommentoService;
import it.unirom3.siw.service.PartitaService;
import it.unirom3.siw.service.SquadraService;
import it.unirom3.siw.service.TorneoService;
import jakarta.validation.Valid;

@Controller
public class PartitaController {

	private final PartitaService partitaService;
	private final TorneoService torneoService;
	private final SquadraService squadraService;
	private final ArbitroService arbitroService;
	private final CommentoService commentoService;

	public PartitaController(PartitaService p, TorneoService t, SquadraService s, ArbitroService a, CommentoService c) {
		this.partitaService = p;
		this.torneoService = t;
		this.squadraService = s;
		this.arbitroService = a;
		this.commentoService = c;
	}

	@GetMapping("/partite")
	public String getPartite(Model model) {
		model.addAttribute("partite", partitaService.getTuttePartite());
		return "partite";
	}

	@PostMapping("/admin/partite/{id}/risultato")
	public String registraRisultato(@PathVariable Long id, @Valid @ModelAttribute("risultatoForm") RisultatoForm form,
			BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("erroreRisultato", primoErrore(bindingResult));
			return "redirect:/partite/" + id;
		}
		try {
			partitaService.registraRisultato(id, form.getGoalCasa(), form.getGoalOspiti());
			redirectAttributes.addFlashAttribute("successo", "Risultato registrato");
		} catch (RegolaBusinessException ex) {
			redirectAttributes.addFlashAttribute("erroreRisultato", ex.getMessage());
		}
		return "redirect:/partite/" + id;
	}

	@PostMapping("/admin/partite/{partitaId}/arbitro")
	public String assegnaArbitro(@PathVariable Long partitaId, @RequestParam Long arbitroId,
			RedirectAttributes redirectAttributes) {
		partitaService.assegnaArbitro(partitaId, arbitroId);
		redirectAttributes.addFlashAttribute("successo", "Arbitro assegnato");
		return "redirect:/partite/" + partitaId;
	}

	@GetMapping("/partite/{id}")
	public String getPartita(@PathVariable Long id, Model model, Principal principal) {
		Partita partita = partitaService.getPartitaById(id)
				.orElseThrow(() -> new RisorsaNonTrovataException("Partita non trovata"));
		model.addAttribute("partita", partita);
		model.addAttribute("commenti", commentoService.getCommentiPartita(id));
		model.addAttribute("risultatoForm", new RisultatoForm());
		model.addAttribute("arbitri", arbitroService.getAllArbitri());
		if (principal != null) {
			model.addAttribute("mioCommento", commentoService.getCommentoUtente(id, principal.getName()).orElse(null));
		}
		return "partita";
	}

	@GetMapping("/admin/partite/nuova")
	public String formNuovaPartita(Model model) {
		if (!model.containsAttribute("partitaForm")) {
			model.addAttribute("partitaForm", new PartitaForm());
		}
		caricaDatiForm(model);
		return "admin/formPartita";
	}

	@PostMapping("/admin/partite")
	public String creaPartita(@Valid @ModelAttribute("partitaForm") PartitaForm form, BindingResult bindingResult,
			Model model, RedirectAttributes redirectAttributes) {
		validaSquadre(form, bindingResult);
		if (bindingResult.hasErrors()) {
			caricaDatiForm(model);
			return "admin/formPartita";
		}
		try {
			Partita partita = partitaService.creaPartita(form.getTorneoId(), form.getSquadraCasaId(),
					form.getSquadraOspiteId(), form.getArbitroId(), form.getDataOra(), form.getLuogo());
			redirectAttributes.addFlashAttribute("successo", "Partita creata correttamente");
			return "redirect:/partite/" + partita.getId();
		} catch (RegolaBusinessException ex) {
			bindingResult.reject("partita.nonValida", ex.getMessage());
			caricaDatiForm(model);
			return "admin/formPartita";
		}
	}

	@GetMapping("/admin/partite/{id}/modifica")
	public String formModificaPartita(@PathVariable Long id, Model model) {
		Partita p = partitaService.getPartitaById(id)
				.orElseThrow(() -> new RisorsaNonTrovataException("Partita non trovata"));
		if (p.getStato().name().equals("PLAYED"))
			throw new RegolaBusinessException("Una partita già disputata non può essere modificata");
		PartitaForm f = new PartitaForm();
		f.setTorneoId(p.getTorneo().getId());
		f.setSquadraCasaId(p.getHome().getId());
		f.setSquadraOspiteId(p.getAway().getId());
		f.setArbitroId(p.getArbitro().getId());
		f.setDataOra(p.getDataOra());
		f.setLuogo(p.getLuogo());
		model.addAttribute("partitaForm", f);
		model.addAttribute("partitaId", id);
		caricaDatiForm(model);
		return "admin/formPartita";
	}

	@PostMapping("/admin/partite/{id}")
	public String modificaPartita(@PathVariable Long id, @Valid @ModelAttribute("partitaForm") PartitaForm form,
			BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
		validaSquadre(form, bindingResult);
		if (bindingResult.hasErrors()) {
			model.addAttribute("partitaId", id);
			caricaDatiForm(model);
			return "admin/formPartita";
		}
		try {
			partitaService.modificaPartita(id, form.getTorneoId(), form.getSquadraCasaId(), form.getSquadraOspiteId(),
					form.getArbitroId(), form.getDataOra(), form.getLuogo());
			redirectAttributes.addFlashAttribute("successo", "Partita aggiornata");
			return "redirect:/partite/" + id;
		} catch (RegolaBusinessException ex) {
			bindingResult.reject("partita.nonValida", ex.getMessage());
			model.addAttribute("partitaId", id);
			caricaDatiForm(model);
			return "admin/formPartita";
		}
	}

	@PostMapping("/admin/partite/{id}/elimina")
	public String eliminaPartita(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		partitaService.eliminaPartita(id);
		redirectAttributes.addFlashAttribute("successo", "Partita eliminata");
		return "redirect:/partite";
	}

	@GetMapping("/admin/arbitri")
	public String elencoArbitri(Model model) {
		model.addAttribute("arbitri", arbitroService.getAllArbitri());
		return "admin/arbitri";
	}

	@GetMapping("/admin/arbitri/nuovo")
	public String nuovoArbitro(Model model) {
		model.addAttribute("arbitroForm", new ArbitroForm());
		return "admin/formArbitro";
	}

	@PostMapping("/admin/arbitri")
	public String creaArbitro(@Valid @ModelAttribute ArbitroForm form, BindingResult errors, RedirectAttributes ra) {
		if (errors.hasErrors())
			return "admin/formArbitro";
		try {
			arbitroService.crea(form.getNome(), form.getCognome(), form.getCodiceArbitrale());
			ra.addFlashAttribute("successo", "Arbitro creato");
			return "redirect:/admin/arbitri";
		} catch (RegolaBusinessException ex) {
			errors.rejectValue("codiceArbitrale", "duplicato", ex.getMessage());
			return "admin/formArbitro";
		}
	}

	@GetMapping("/admin/arbitri/{id}/modifica")
	public String modificaArbitroForm(@PathVariable Long id, Model model) {
		var a = arbitroService.getArbitroById(id);
		var f = new ArbitroForm();
		f.setNome(a.getNome());
		f.setCognome(a.getCognome());
		f.setCodiceArbitrale(a.getCodiceArbitrale());
		model.addAttribute("arbitroForm", f);
		model.addAttribute("arbitroId", id);
		return "admin/formArbitro";
	}

	@PostMapping("/admin/arbitri/{id}")
	public String modificaArbitro(@PathVariable Long id, @Valid @ModelAttribute ArbitroForm form, BindingResult errors,
			Model model, RedirectAttributes ra) {
		if (errors.hasErrors()) {
			model.addAttribute("arbitroId", id);
			return "admin/formArbitro";
		}
		try {
			arbitroService.modifica(id, form.getNome(), form.getCognome(), form.getCodiceArbitrale());
			ra.addFlashAttribute("successo", "Arbitro aggiornato");
			return "redirect:/admin/arbitri";
		} catch (RegolaBusinessException ex) {
			errors.rejectValue("codiceArbitrale", "duplicato", ex.getMessage());
			model.addAttribute("arbitroId", id);
			return "admin/formArbitro";
		}
	}

	@PostMapping("/admin/arbitri/{id}/elimina")
	public String eliminaArbitro(@PathVariable Long id, RedirectAttributes ra) {
		arbitroService.eliminaArbitro(id);
		ra.addFlashAttribute("successo", "Arbitro eliminato");
		return "redirect:/admin/arbitri";
	}

	private void validaSquadre(PartitaForm form, BindingResult bindingResult) {
		if (form.getSquadraCasaId() != null && form.getSquadraCasaId().equals(form.getSquadraOspiteId()))
			bindingResult.rejectValue("squadraOspiteId", "squadre.uguali", "Le due squadre devono essere diverse");
	}

	private void caricaDatiForm(Model model) {
		model.addAttribute("tornei", torneoService.getTuttiTornei());
		model.addAttribute("squadre", squadraService.getTutteSquadre());
		model.addAttribute("arbitri", arbitroService.getAllArbitri());
	}

	private String primoErrore(BindingResult bindingResult) {
		return bindingResult.getAllErrors().isEmpty() ? "Dati non validi"
				: bindingResult.getAllErrors().get(0).getDefaultMessage();
	}
}
