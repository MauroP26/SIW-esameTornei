package it.unirom3.siw.benchmark;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManagerFactory;

@Component
@Order(100)
@ConditionalOnProperty(name = "app.benchmark.enabled", havingValue = "true")
public class JpaBenchmarkRunner implements ApplicationRunner {

	private final BenchmarkDataService dataService;
	private final BenchmarkQueryService queryService;
	private final EntityManagerFactory entityManagerFactory;

	@Value("${app.benchmark.squadre:30}")
	private int numeroSquadre;

	@Value("${app.benchmark.giocatori-per-squadra:15}")
	private int giocatoriPerSquadra;

	@Value("${app.benchmark.warmup:3}")
	private int warmup;

	@Value("${app.benchmark.ripetizioni:10}")
	private int ripetizioni;

	@Value("${app.benchmark.output-directory:benchmark-results}")
	private String outputDirectory;

	public JpaBenchmarkRunner(BenchmarkDataService dataService, BenchmarkQueryService queryService,
			EntityManagerFactory entityManagerFactory) {
		this.dataService = dataService;
		this.queryService = queryService;
		this.entityManagerFactory = entityManagerFactory;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		verificaConfigurazione();

		System.out.println("\n============================================================");
		System.out.println(" BENCHMARK JPA: rose delle squadre di un torneo");
		System.out.println("============================================================");
		System.out.printf("Dataset: %d squadre x %d giocatori = %d giocatori%n", numeroSquadre, giocatoriPerSquadra,
				numeroSquadre * giocatoriPerSquadra);

		Long torneoId = dataService.preparaDataset(numeroSquadre, giocatoriPerSquadra);

		SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
		Statistics statistics = sessionFactory.getStatistics();
		statistics.setStatisticsEnabled(true);
		statistics.clear();

		eseguiWarmup(torneoId);
		List<RisultatoBenchmark> risultati = eseguiMisurazioni(torneoId, statistics);
		verificaEquivalenza(risultati);
		stampaRiepilogo(risultati);
		scriviReport(risultati, torneoId);

		System.out.println("============================================================\n");
	}

	private void verificaConfigurazione() {
		if (numeroSquadre < 1 || giocatoriPerSquadra < 1 || warmup < 0 || ripetizioni < 1) {
			throw new IllegalArgumentException("Parametri benchmark non validi");
		}
	}

	private void eseguiWarmup(Long torneoId) {
		System.out.printf("Warm-up: %d esecuzioni per strategia (non misurate)%n", warmup);
		for (int i = 0; i < warmup; i++) {
			for (StrategiaAccesso strategia : StrategiaAccesso.values()) {
				eseguiQuery(strategia, torneoId);
			}
		}
	}

	private List<RisultatoBenchmark> eseguiMisurazioni(Long torneoId, Statistics statistics) {
		List<RisultatoBenchmark> risultati = new ArrayList<>();

		/*
		 * A ogni ripetizione cambiamo l'ordine delle strategie con un seme fisso. Così
		 * una strategia non beneficia sempre dell'essere eseguita per ultima, ma
		 * l'esperimento rimane riproducibile.
		 */
		for (int ripetizione = 1; ripetizione <= ripetizioni; ripetizione++) {
			List<StrategiaAccesso> ordine = new ArrayList<>(List.of(StrategiaAccesso.values()));
			Collections.shuffle(ordine, new Random(42L + ripetizione));

			for (StrategiaAccesso strategia : ordine) {
				statistics.clear();
				long inizio = System.nanoTime();
				CaricamentoRoseResult risultato = eseguiQuery(strategia, torneoId);
				long tempo = System.nanoTime() - inizio;
				long query = statistics.getPrepareStatementCount();

				risultati.add(new RisultatoBenchmark(ripetizione, strategia, tempo, query, risultato.numeroSquadre(),
						risultato.numeroGiocatori(), risultato.checksum()));
			}
		}
		return risultati;
	}

	private CaricamentoRoseResult eseguiQuery(StrategiaAccesso strategia, Long torneoId) {
		return switch (strategia) {
		case LAZY_N_PLUS_ONE -> queryService.caricaRoseLazy(torneoId);
		case JOIN_FETCH -> queryService.caricaRoseJoinFetch(torneoId);
		case ENTITY_GRAPH -> queryService.caricaRoseEntityGraph(torneoId);
		};
	}

	private void verificaEquivalenza(List<RisultatoBenchmark> risultati) {
		RisultatoBenchmark riferimento = risultati.get(0);
		boolean equivalenti = risultati.stream().allMatch(r -> r.numeroSquadre() == riferimento.numeroSquadre()
				&& r.numeroGiocatori() == riferimento.numeroGiocatori() && r.checksum() == riferimento.checksum());

		if (!equivalenti) {
			throw new IllegalStateException(
					"Le strategie non hanno caricato lo stesso insieme di dati: benchmark non valido");
		}
	}

	private void stampaRiepilogo(List<RisultatoBenchmark> risultati) {
		System.out.println("\nRisultati medi:");
		System.out.printf("%-20s %12s %12s %12s%n", "Strategia", "media ms", "mediana ms", "query medie");

		for (StrategiaAccesso strategia : StrategiaAccesso.values()) {
			List<RisultatoBenchmark> gruppo = filtra(risultati, strategia);
			System.out.printf(Locale.ROOT, "%-20s %12.3f %12.3f %12.1f%n", strategia, mediaTempo(gruppo),
					medianaTempo(gruppo), mediaQuery(gruppo));
		}
		System.out.println("Nota: i tempi dipendono dalla macchina; il numero di query è il dato più stabile.");
	}

	private void scriviReport(List<RisultatoBenchmark> risultati, Long torneoId) throws IOException {
		Path directory = Path.of(outputDirectory);
		Files.createDirectories(directory);

		Path csv = directory.resolve("benchmark-jpa-dettaglio.csv");
		StringBuilder csvContent = new StringBuilder(
				"ripetizione,strategia,tempo_ms,query_sql,squadre,giocatori,checksum\n");
		risultati.stream()
				.sorted(Comparator.comparingInt(RisultatoBenchmark::ripetizione)
						.thenComparing(r -> r.strategia().name()))
				.forEach(r -> csvContent.append(String.format(Locale.ROOT, "%d,%s,%.6f,%d,%d,%d,%d%n", r.ripetizione(),
						r.strategia(), r.tempoMillisecondi(), r.querySql(), r.numeroSquadre(), r.numeroGiocatori(),
						r.checksum())));
		Files.writeString(csv, csvContent, StandardCharsets.UTF_8, StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING);

		Path markdown = directory.resolve("benchmark-jpa-riepilogo.md");
		Files.writeString(markdown, generaMarkdown(risultati, torneoId), StandardCharsets.UTF_8,
				StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

		System.out.println("Report CSV: " + csv.toAbsolutePath());
		System.out.println("Report Markdown: " + markdown.toAbsolutePath());
	}

	private String generaMarkdown(List<RisultatoBenchmark> risultati, Long torneoId) {
		StringBuilder md = new StringBuilder();
		md.append("# Risultati benchmark JPA/Hibernate\n\n");
		md.append("Esecuzione: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
				.append("\n\n");
		md.append("## Configurazione\n\n");
		md.append("- Torneo benchmark ID: ").append(torneoId).append("\n");
		md.append("- Squadre: ").append(numeroSquadre).append("\n");
		md.append("- Giocatori per squadra: ").append(giocatoriPerSquadra).append("\n");
		md.append("- Giocatori complessivi: ").append(numeroSquadre * giocatoriPerSquadra).append("\n");
		md.append("- Warm-up per strategia: ").append(warmup).append("\n");
		md.append("- Ripetizioni misurate: ").append(ripetizioni).append("\n\n");

		md.append("## Riepilogo\n\n");
		md.append("| Strategia | Query medie | Tempo medio (ms) | Mediana (ms) | Min (ms) | Max (ms) |\n");
		md.append("|---|---:|---:|---:|---:|---:|\n");
		for (StrategiaAccesso strategia : StrategiaAccesso.values()) {
			List<RisultatoBenchmark> gruppo = filtra(risultati, strategia);
			md.append(String.format(Locale.ROOT, "| %s | %.1f | %.3f | %.3f | %.3f | %.3f |%n", strategia,
					mediaQuery(gruppo), mediaTempo(gruppo), medianaTempo(gruppo), minimoTempo(gruppo),
					massimoTempo(gruppo)));
		}

		md.append("\n## Interpretazione\n\n");
		md.append("- **LAZY_N_PLUS_ONE** carica prima le squadre e poi inizializza la rosa di ogni squadra. ")
				.append("Con N squadre ci si attende circa `1 + N` query.\n");
		md.append(
				"- **JOIN_FETCH** dichiara esplicitamente nella JPQL che `giocatori` deve essere caricato nella stessa query.\n");
		md.append(
				"- **ENTITY_GRAPH** mantiene la query più indipendente dalla strategia di fetch e richiede lo stesso grafo dati tramite annotazione.\n");
		md.append(
				"- I tempi assoluti dipendono da macchina, JVM e database; il numero di query è normalmente il risultato più riproducibile.\n");
		md.append(
				"- Tutte le strategie sono state validate tramite conteggi e checksum uguali, quindi il confronto riguarda lo stesso risultato applicativo.\n");
		return md.toString();
	}

	private List<RisultatoBenchmark> filtra(List<RisultatoBenchmark> risultati, StrategiaAccesso strategia) {
		return risultati.stream().filter(r -> r.strategia() == strategia).toList();
	}

	private double mediaTempo(List<RisultatoBenchmark> risultati) {
		return risultati.stream().mapToDouble(RisultatoBenchmark::tempoMillisecondi).average().orElse(0);
	}

	private double medianaTempo(List<RisultatoBenchmark> risultati) {
		List<Double> ordinati = risultati.stream().map(RisultatoBenchmark::tempoMillisecondi).sorted().toList();
		int centro = ordinati.size() / 2;
		return ordinati.size() % 2 == 0 ? (ordinati.get(centro - 1) + ordinati.get(centro)) / 2.0
				: ordinati.get(centro);
	}

	private double minimoTempo(List<RisultatoBenchmark> risultati) {
		return risultati.stream().mapToDouble(RisultatoBenchmark::tempoMillisecondi).min().orElse(0);
	}

	private double massimoTempo(List<RisultatoBenchmark> risultati) {
		return risultati.stream().mapToDouble(RisultatoBenchmark::tempoMillisecondi).max().orElse(0);
	}

	private double mediaQuery(List<RisultatoBenchmark> risultati) {
		return risultati.stream().mapToLong(RisultatoBenchmark::querySql).average().orElse(0);
	}
}
