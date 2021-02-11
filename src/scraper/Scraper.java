package scraper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Scraper {

	public Scraper() {
		System.out.println("Scraper online");
	}

	public List<String> scrapUrl(String url, String tag, boolean removeEmptyLines) throws IOException {
		List<String> scrapedStrings = new ArrayList<>();
		Pattern pattern = Pattern.compile(String.format("<%1$s>\\s*?</%1$s>", tag), Pattern.CASE_INSENSITIVE);
		Document doc = Jsoup.connect(url).get();
		Elements elements = doc.select(tag);
		for (int i = 0; i < elements.size(); i++) {
			if (removeEmptyLines) {
				String element = elements.get(i).toString();
				Matcher matcher = pattern.matcher(element);
				if (!matcher.matches()) {
					scrapedStrings.add(element);
					// scrapedString = scrapedString.concat(element + "\n");
				}
			} else {
				scrapedStrings.add(elements.get(i).toString());
				// scrapedString = scrapedString.concat(elements.get(i).toString() + "\n");
			}
		}
		System.out.println(scrapedStrings);
		return scrapedStrings;
	}

	public void exportToCsv(List<String> scrapedData, String url) throws FileNotFoundException {
		System.out.println(scrapedData);
		File csvOutputFile = new File("ScrapTest.csv");
		try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
			scrapedData.stream().map(dat -> convertToCSV(dat)).forEach(pw::println);
		}
	}

	private String convertToCSV(String dat) {
		return Stream.of(dat).map(this::escapeSpecialCharacters).collect(Collectors.joining(","));

	}

	public String escapeSpecialCharacters(String data) {
		String escapedData = data.replaceAll("\\R", " ");
		if (data.contains(",") || data.contains("\"") || data.contains("'")) {
			data = data.replace("\"", "\"\"");
			escapedData = "\"" + data + "\"";
		}
		return escapedData;
	}

}
