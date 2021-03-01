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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ibm.icu.text.SimpleDateFormat;

import models.Item;

public class Scraper {

	public Scraper() {
		System.out.println("Scraper online");
	}

	public String scrapUrl(String url) throws IOException {
		return Jsoup.connect(url).get().html();
	}

	public Item scrapItem(String url, String name) throws IOException {
		Item vehicle = new Item(name);
		Document document = Jsoup.connect(url + name).get();
		Elements elements = document.select("body");
		for (Element element : elements) {
			Elements data = element.select("div.pi-item");
			vehicle.addImage(element.select("img.pi-image-thumbnail").attr("src"));
			for (Element dat : data) {
				String type = dat.select("h3").text();
				Elements subElements = dat.select("div.pi-data-value");
				vehicle.addData(subElements, type);
			}

		}
		if (vehicle.getName() != null && !vehicle.getImage().isEmpty()) {
			System.out.println(new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date())
					+ " - Scraped info on " + vehicle.getName());
		}
		return vehicle;
	}

	public List<String> scrapItem(String url) throws IOException {
		List<String> list = new ArrayList<>();
		Document document = Jsoup.connect(url).get();
		Elements elements = document.select("div.category-page__members ul li");
		for (Element element : elements) {
			String name = element.select("a").text();
			list.add(name);
		}
		return list;
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

	public void exportToCsv(List<Item> items, String wikiName, String fileName, String extension)
			throws FileNotFoundException {
		String dirName = "data";
		File csvOutputFile = new File(dirName + "/" + wikiName + "/" + fileName.replace(':', '_') + "." + extension);
		if (!csvOutputFile.exists()) {
			createDir(dirName, wikiName);
		}
		try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
			items.stream().map(dat -> dat.parseToCsv()).forEach(pw::println);
		}
	}

	private void createDir(String dirName, String wikiName) {
		File wikiDir = new File(dirName + "/" + wikiName);
		if (!wikiDir.exists()) {
			File dataDir = new File(dirName);
			if (!dataDir.exists()) {
				dataDir.mkdir();
			}
			wikiDir.mkdir();
		}
	}

}
