package de.andrena.java8;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class PersonenGenerator {

	private final List<String> vornamen;
	private final List<String> nachnamen;
	private final List<Ort> orte;

	private final Random random = new Random();

	public PersonenGenerator() throws URISyntaxException, IOException {
		vornamen = readFile("vornamen.txt");
		nachnamen = readFile("nachnamen.txt");
		orte = streamFile("Liste-Staedte-in-Deutschland.csv") //
				.skip(1) //
				.map(line -> line.split(";")) //
				.map(parts -> new Ort(parts[2], parts[1])) //
				.collect(toList());
	}

	public Stream<Person> generiereStream() {
		return Stream.generate(() -> generierePerson());
	}

	private Person generierePerson() {
		String vorname = vornamen.get(random.nextInt(vornamen.size()));
		String nachname = nachnamen.get(random.nextInt(nachnamen.size()));
		Adresse adresse = generiereAdresse();
		LocalDate geburtstag = generiereGeburtstag();
		return new Person(vorname, nachname, adresse, geburtstag);
	}

	private Adresse generiereAdresse() {
		String strasse = "Demostrasse";
		int hausnummer = random.nextInt(100);
		Ort ort = orte.get(random.nextInt(orte.size()));
		return new Adresse(strasse, hausnummer, ort.plz, ort.name);
	}

	private LocalDate generiereGeburtstag() {
		int vor100Jahren = LocalDate.now().getYear() - 100;
		int year = vor100Jahren + random.nextInt(100);
		Month month = zufaelligerMonat();
		int dayOfMonth = 1 + random.nextInt(month.length(false));
		return LocalDate.of(year, month, dayOfMonth);
	}

	private Month zufaelligerMonat() {
		return Month.values()[random.nextInt(Month.values().length)];
	}

	private Stream<String> streamFile(String file) throws URISyntaxException, IOException {
		return Files.newBufferedReader(toPath(file)).lines();
	}

	private List<String> readFile(String file) throws URISyntaxException, IOException {
		return Files.readAllLines(toPath(file));
	}

	private Path toPath(String file) throws URISyntaxException {
		return Paths.get(getClass().getResource("/" + file).toURI());
	}

	private class Ort {
		String plz;
		String name;

		Ort(String plz, String name) {
			this.plz = plz;
			this.name = name;
		}
	}

}