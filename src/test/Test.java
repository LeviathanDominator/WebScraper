package test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.ibm.icu.text.SimpleDateFormat;

import models.Property;
import models.Wrestler;
import scraper.Scraper;

public class Test {

	private static Scraper scraper;
	private static List<Wrestler> scrapedWrestlers;
	private static String url = "https://prowrestling.fandom.com/wiki/";
	private static String[] categories = { "WWE_NXT_current_roster", "World_Wrestling_Entertainment_current_roster",
			"All_Elite_Wrestling_current_roster", "NXT_Champions", "NXT_Women's_Champions", "Male_wrestlers",
			"Female_wrestlers", "Wrestlers_who_have_died" };

	public static void main(String args[]) {
		scraper = new Scraper();
		scrapedWrestlers = new ArrayList<>();
		try {
			print("Began scraping...");
			for (String category : categories) {
				try {
				System.out.println("Scraping category: " + category);
				scrapWrestlers(category);
				System.out.println("\n");
				} catch (FileNotFoundException e) {
					System.out.println(e.toString());
				}
			}
			// scraper.scrapWrestler("https://prowrestling.fandom.com/wiki/", "Johnny
			// Gargano");
			System.out.println("Exported successfully");
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*
		 * scrapedData.add(result); try { scraper.exportToHtml(scrapedData, result);
		 * print("Finished scraping"); } catch (FileNotFoundException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
	}

	private static void scrapWrestlers(String category) throws IOException {
		String lastWrestler = "";
		boolean loop = true;
		while (loop) {
			if (!scrapedWrestlers.isEmpty()) {
				// lastWrestler = scrapedWrestlers.get(scrapedWrestlers.size() - 1).name;
				// System.out.println(lastWrestler);
			}
			List<String> result = new ArrayList<>();
			result = scraper.scrapWrestlers(url + "Category:" + category + "?from=" + lastWrestler);
			if (result.size() <= 1) {
				loop = false;
			}
			// scrapedWrestlers.addAll(result);
			// scrapedWrestlers = Stream.concat(scrapedWrestlers.stream(),
			// result.stream()).collect(Collectors.toList());
			for (int i = 0; i < result.size(); i++) {
				if (i == 0) {
					if (scrapedWrestlers.isEmpty()) {
						addWrestler(result.get(i));
					}
				} else {
					addWrestler(result.get(i));
				}
			}
			lastWrestler = result.get(result.size() - 1);
		}
		scraper.exportToCsvWrestlers(scrapedWrestlers, category, "csv");
	}

	private static void addWrestler(String wrestlerName) throws IOException {
		Wrestler wrestler = scraper.scrapWrestler(url, wrestlerName);
		if (wrestler.getName() != null) {
			scrapedWrestlers.add(wrestler);
			System.out.println(new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()) + " - Stored "
					+ wrestler.getName() + "'s info in the database");
		}
	}

	private static void print(String result) {
		if (result != null) {
			System.out.println(result);
		}
	}

}
