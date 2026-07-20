package it.unirom3.siw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.unirom3.siw.dto.GiocatoreForm;
import it.unirom3.siw.dto.SquadraForm;
import it.unirom3.siw.dto.TorneoForm;
import it.unirom3.siw.model.Giocatore;
import it.unirom3.siw.model.RuoloGiocatore;
import it.unirom3.siw.model.Squadra;
import it.unirom3.siw.model.Torneo;
import it.unirom3.siw.service.GiocatoreService;
import it.unirom3.siw.service.SquadraService;
import it.unirom3.siw.service.TorneoService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminController {
	private final TorneoService torneoService;
	private final SquadraService squadraService;
	private final GiocatoreService giocatoreService;

	public AdminController(TorneoService t, SquadraService s, GiocatoreService g) {
		torneoService = t;
		squadraService = s;
		giocatoreService = g;
	}

	@GetMapping
	public String dashboard(Model model) {
		model.addAttribute("tornei", torneoService.getTuttiTornei());
		model.addAttribute("squadre", squadraService.getTutteSquadre());
		model.addAttribute("giocatori", giocatoreService.getTuttiGiocatori());
		return "admin/dashboard";
	}

	@GetMapping("/tornei/nuovo")
	public String nuovoTorneo(Model m) {
		m.addAttribute("torneoForm", new TorneoForm());
		return "admin/formTorneo";
	}

	@PostMapping("/tornei")
	public String creaTorneo(@Valid @ModelAttribute TorneoForm f, BindingResult b, RedirectAttributes r) {
		if (b.hasErrors())
			return "admin/formTorneo";
		Torneo t = torneoService.crea(f.getNome(), f.getAnno(), f.getDescrizione());
		r.addFlashAttribute("successo", "Torneo creato");
		return "redirect:/tornei/" + t.getId();
	}

	@GetMapping("/tornei/{id}/modifica")
	public String modificaTorneo(@PathVariable Long id, Model m) {
		Torneo t = torneoService.getTorneoById(id);
		TorneoForm f = new TorneoForm();
		f.setNome(t.getNome());
		f.setAnno(t.getAnno());
		f.setDescrizione(t.getDescrizione());
		m.addAttribute("torneoForm", f);
		m.addAttribute("torneoId", id);
		return "admin/formTorneo";
	}

	@PostMapping("/tornei/{id}")
	public String salvaTorneo(@PathVariable Long id, @Valid @ModelAttribute TorneoForm f, BindingResult b, Model m,
			RedirectAttributes r) {
		if (b.hasErrors()) {
			m.addAttribute("torneoId", id);
			return "admin/formTorneo";
		}
		torneoService.modifica(id, f.getNome(), f.getAnno(), f.getDescrizione());
		r.addFlashAttribute("successo", "Torneo aggiornato");
		return "redirect:/tornei/" + id;
	}

	@PostMapping("/tornei/{id}/elimina")
	public String eliminaTorneo(@PathVariable Long id, RedirectAttributes r) {
		torneoService.eliminaTorneo(id);
		r.addFlashAttribute("successo", "Torneo eliminato");
		return "redirect:/admin";
	}

	@GetMapping("/squadre/nuova")
	public String nuovaSquadra(Model m) {
		m.addAttribute("squadraForm", new SquadraForm());
		return "admin/formSquadra";
	}

	@PostMapping("/squadre")
	public String creaSquadra(@Valid @ModelAttribute SquadraForm f, BindingResult b, RedirectAttributes r) {
		if (b.hasErrors())
			return "admin/formSquadra";
		Squadra s = squadraService.creaSquadra(f.getNome(), f.getAnnoFondazione(), f.getCitta());
		r.addFlashAttribute("successo", "Squadra creata");
		return "redirect:/squadre/" + s.getId();
	}

	@GetMapping("/squadre/{id}/modifica")
	public String modificaSquadra(@PathVariable Long id, Model m) {
		Squadra s = squadraService.getSquadraById(id);
		SquadraForm f = new SquadraForm();
		f.setNome(s.getNome());
		f.setAnnoFondazione(s.getAnnoFondazione());
		f.setCitta(s.getCitta());
		m.addAttribute("squadraForm", f);
		m.addAttribute("squadraId", id);
		return "admin/formSquadra";
	}

	@PostMapping("/squadre/{id}")
	public String salvaSquadra(@PathVariable Long id, @Valid @ModelAttribute SquadraForm f, BindingResult b, Model m,
			RedirectAttributes r) {
		if (b.hasErrors()) {
			m.addAttribute("squadraId", id);
			return "admin/formSquadra";
		}
		squadraService.modifica(id, f.getNome(), f.getAnnoFondazione(), f.getCitta());
		r.addFlashAttribute("successo", "Squadra aggiornata");
		return "redirect:/squadre/" + id;
	}

	@PostMapping("/squadre/{id}/elimina")
	public String eliminaSquadra(@PathVariable Long id, RedirectAttributes r) {
		squadraService.eliminaSquadra(id);
		r.addFlashAttribute("successo", "Squadra eliminata");
		return "redirect:/admin";
	}

	@PostMapping("/tornei/{torneoId}/squadre")
	public String associa(@PathVariable Long torneoId, @RequestParam Long squadraId, RedirectAttributes r) {
		squadraService.aggiungiSquadraAlTorneo(squadraId, torneoId);
		r.addFlashAttribute("successo", "Squadra associata al torneo");
		return "redirect:/tornei/" + torneoId;
	}

	@PostMapping("/tornei/{torneoId}/squadre/{squadraId}/rimuovi")
	public String rimuovi(@PathVariable Long torneoId, @PathVariable Long squadraId, RedirectAttributes r) {
		squadraService.rimuoviSquadraDalTorneo(squadraId, torneoId);
		r.addFlashAttribute("successo", "Squadra rimossa dal torneo");
		return "redirect:/tornei/" + torneoId;
	}

	@GetMapping("/giocatori/nuovo")
	public String nuovoGiocatore(@RequestParam(required = false) Long squadraId, Model m) {
		GiocatoreForm f = new GiocatoreForm();
		f.setSquadraId(squadraId);
		preparaGiocatore(m, f, null);
		return "admin/formGiocatore";
	}

	@PostMapping("/giocatori")
	public String creaGiocatore(@Valid @ModelAttribute GiocatoreForm f, BindingResult b, Model m,
			RedirectAttributes r) {
		if (b.hasErrors()) {
			preparaGiocatore(m, f, null);
			return "admin/formGiocatore";
		}
		Giocatore g = giocatoreService.aggiungiGiocatore(f.getNome(), f.getCognome(), f.getDataNascita(), f.getRuolo(),
				f.getAltezza(), f.getSquadraId());
		r.addFlashAttribute("successo", "Giocatore inserito");
		return "redirect:/squadre/" + g.getSquadra().getId();
	}

	@GetMapping("/giocatori/{id}/modifica")
	public String modificaGiocatore(@PathVariable Long id, Model m) {
		Giocatore g = giocatoreService.getGiocatoreById(id);
		GiocatoreForm f = new GiocatoreForm();
		f.setNome(g.getNome());
		f.setCognome(g.getCognome());
		f.setDataNascita(g.getDataNascita());
		f.setRuolo(g.getRuolo());
		f.setAltezza(g.getAltezza());
		f.setSquadraId(g.getSquadra().getId());
		preparaGiocatore(m, f, id);
		return "admin/formGiocatore";
	}

	@PostMapping("/giocatori/{id}")
	public String salvaGiocatore(@PathVariable Long id, @Valid @ModelAttribute GiocatoreForm f, BindingResult b,
			Model m, RedirectAttributes r) {
		if (b.hasErrors()) {
			preparaGiocatore(m, f, id);
			return "admin/formGiocatore";
		}
		Giocatore g = giocatoreService.modifica(id, f.getNome(), f.getCognome(), f.getDataNascita(), f.getRuolo(),
				f.getAltezza(), f.getSquadraId());
		r.addFlashAttribute("successo", "Giocatore aggiornato");
		return "redirect:/squadre/" + g.getSquadra().getId();
	}

	@PostMapping("/giocatori/{id}/elimina")
	public String eliminaGiocatore(@PathVariable Long id, RedirectAttributes r) {
		Giocatore g = giocatoreService.getGiocatoreById(id);
		Long squadraId = g.getSquadra().getId();
		giocatoreService.eliminaGiocatore(id);
		r.addFlashAttribute("successo", "Giocatore eliminato");
		return "redirect:/squadre/" + squadraId;
	}

	private void preparaGiocatore(Model m, GiocatoreForm f, Long id) {
		m.addAttribute("giocatoreForm", f);
		m.addAttribute("squadre", squadraService.getTutteSquadre());
		m.addAttribute("ruoli", RuoloGiocatore.values());
		if (id != null)
			m.addAttribute("giocatoreId", id);
	}
}
