package scraper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ibm.icu.text.SimpleDateFormat;

import models.Property;
import models.Wrestler;

public class Scraper {

	public Scraper() {
		System.out.println("Scraper online");
	}

	public String scrapUrl(String url) throws IOException {
		return Jsoup.connect(url).get().html();
	}

	public Wrestler scrapWrestler(String url, String name) throws IOException {
		Wrestler wrestler = new Wrestler(name);
		Document document = Jsoup.connect(url + name).get();
		Elements elements = document.select("body");
		for (Element element : elements) {
			Elements data = element.select("div.pi-item");
			// List<String> list = new ArrayList<>();
			wrestler.addImage(element.select("img.pi-image-thumbnail").attr("src"));
			for (Element dat : data) {
				String type = dat.select("h3").text();
				Elements subElements = dat.select("div.pi-data-value");
				// System.out.println(type + ": " + datear.toString().replaceAll("<[^>]*>", "")
				// + "\n\n");
				wrestler.addData(subElements, type);
				// list.add(subElements.text());
			}

		}
		if (wrestler.getName() != null) {
			System.out.println(new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date())
					+ " - Scraped info on " + wrestler.getName());
		}
		return wrestler;
	}

	public List<String> scrapWrestlers(String url) throws IOException {
		List<String> list = new ArrayList<>();
		Document document = Jsoup.connect(url).get();
		Elements elements = document.select("div.category-page__members ul li");
		for (Element element : elements) {
			String name = element.select("a").text();
			list.add(name);
		}
		return list;
	}

	public Property scrapPisosComData(String url) throws IOException {
		Document document = Jsoup.connect(url).get();
		String title = document.select("h3.title").text().replace(',', '.');
		String description = document.select("div#descriptionBody").text().replace(',', '.');
		String name = document.select("div.owner-data-info").text().split(" Actualizado el")[0].replace(',', '.');
		String phone = document.select("span.number").text(); // TODO Use Selenium to use event in webpage so it can
																// show the entire number.
		// System.out.println(title + " " + description + " " + name + " " + phone);
		return new Property(title, description, name, phone);
	}

	public List<String> scapPisosComUrls(String url) throws IOException {
		// TODO Auto-generated method stub
		Document document = Jsoup.connect(url).get();
		Elements pagination = document.select("div");
		List<String> result = new ArrayList<>();
		for (Element row : pagination) {
			result.add(row.attr("data-navigate-ref"));
		}
		List<String> newResult = new ArrayList<>();
		result.forEach(v -> {
			if (!v.isEmpty()) {
				newResult.add(v);
			}
		});
		// System.out.println(newResult);
		return newResult;
	}

	public List<String> scrapFotoCasa(String[] types, String url, String baseUrl, String fotocasaSearch)
			throws IOException {
		List<String> list = new ArrayList<>();
		List<String> urlList = getUrls(url, baseUrl, types[0], fotocasaSearch);
		List<String> urlList2 = getUrls(url, baseUrl, types[1], fotocasaSearch);
		for (String urlao : urlList2) {
			urlList.add(urlao);
		}

		// String Url1 = pagination.attr("href");
		// System.out.println("pagination-link1 = " + Url1);

		// return Jsoup.connect(url).get().html();
		return urlList;
	}

	// FOTOCASA
	private List<String> getUrls(String url, String baseUrl, String type, String search) throws IOException {
		List<String> urlList = new ArrayList<>();
		List<String> urlList2 = new ArrayList<>();
		Document document = Jsoup.connect(baseUrl + type + url + search).get();
		Elements pagination = document.select("div.re-Searchpage-wrapperContent a");
		pagination.forEach(a -> {
			urlList.add(a.attr("href"));
		});
		for (int i = 0; i < urlList.size(); i++) {
			urlList.set(i, urlList.get(i).replaceAll("\\D+", ""));
		}
		for (int i = 0; i < urlList.size(); i++) {
			if (urlList.get(i).length() == 9) {
				urlList2.add(urlList.get(i));
			}
		}
		// System.out.println(urlList2);
		List<String> result = urlList2.stream().distinct().collect(Collectors.toList());
		List<String> result2 = new ArrayList<>();
		for (String wa : result) {
			result2.add(String.format("%s%s%s/%s/d", baseUrl, type, url, wa));
		}
		return result2.stream().distinct().collect(Collectors.toList());
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
		// System.out.println(scrapedStrings);
		return scrapedStrings;
	}

	public void exportToCsv(List<Property> scrapedData, String fileName, String extension)
			throws FileNotFoundException {
		System.out.println(scrapedData);
		File csvOutputFile = new File(fileName + "." + extension);
		try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
			scrapedData.stream().map(dat -> convertToCSV(dat.toString())).forEach(pw::println);
		}
	}

	// TEMPORAL METHOD

	public void exportToCsvString(List<String> scrapedData, String fileName, String extension)
			throws FileNotFoundException {
		System.out.println(scrapedData);
		File csvOutputFile = new File(fileName + "." + extension);
		try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
			scrapedData.stream().map(dat -> convertToCSV(dat.toString())).forEach(pw::println);
		}
	}

	public void exportToCsvWrestlers(List<Wrestler> wrestlers, String fileName, String extension)
			throws FileNotFoundException {
		File csvOutputFile = new File(fileName + "." + extension);
		try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
			wrestlers.stream().map(dat -> dat.toString()).forEach(pw::println);
		}
	}

	private String convertToCSV(String dat) {
		return Stream.of(dat).map(this::escapeSpecialCharacters).collect(Collectors.joining(","));

	}

	public String escapeSpecialCharacters(String data) {
		if (data == null) {
			return "";
		}
		String escapedData = data.replaceAll("\\R", " ").replaceAll("\"", "");
		if (data.contains(",") || data.contains("\"") || data.contains("'")) {
			data = data.replace("\"", "\"\"");
			escapedData = "\"" + data + "\"";
		}
		return escapedData;
	}

}
