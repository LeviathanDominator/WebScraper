package main;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.jsoup.HttpStatusException;

import com.ibm.icu.text.SimpleDateFormat;

import models.Item;
import scraper.Scraper;

public class Main {

	private static Scraper scraper;
	private static List<Item> scrapedItems;
	private static String wiki = "overwatch";
	private static String url = "https://" + wiki + ".fandom.com/wiki/";
	private static String[] categories = { "Hero" };

	public static void main(String[] args) {
		scraper = new Scraper();
		scrapedItems = new ArrayList<>();
		try {
			scrapItems(categories[0]);

			/*
			 * Item item = scraper.scrapItem(url, "Kratos"); System.out.println(item);
			 */

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void scrapItems(String category) throws IOException {
		String lastItem = "";
		boolean loop = true;
		while (loop) {
			List<String> result = new ArrayList<>();
			result = scraper.scrapItem(url + "Category:" + category + "?from=" + lastItem);
			Object[] resultArray = result.stream().filter(f -> !f.startsWith("Category:")).toArray();
			if (result.size() <= 1) {
				loop = false;
			}
			for (int i = 0; i < result.size(); i++) {
				if (i == 0) {
					if (scrapedItems.isEmpty()) {
						addItem(result.get(i));
					}
				} else {
					addItem(result.get(i));
				}
			}
			if (resultArray.length != 0) {
				if (resultArray[resultArray.length - 1].toString().equals(lastItem)) {
					loop = false;
				} else {
					lastItem = resultArray[resultArray.length - 1].toString();
				}
			} else {
				loop = false;
			}
		}
		if (!scrapedItems.isEmpty()) {
			scraper.exportToCsv(scrapedItems, wiki, category, "csv");
			exportImages(scrapedItems, wiki, category);
			System.out.println(new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date())
					+ " - Exported succesfully to " + category + ".csv");
		} else {
			System.out.println(new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date())
					+ " - No data was scraped");
		}

	}

	private static void exportImages(List<Item> scrapedItems, String wikiName, String category) throws IOException {
		String dirName = "images";
		File wikiDir = new File(dirName + "/" + wikiName);
		if (!wikiDir.exists()) {
			File dataDir = new File(dirName);
			if (!dataDir.exists()) {
				dataDir.mkdir();
			}
			wikiDir.mkdir();
		}
		for (Item item : scrapedItems) {
			/*
			 * URL url = new URL(item.getImage()); InputStream in = new
			 * BufferedInputStream(url.openStream()); ByteArrayOutputStream out = new
			 * ByteArrayOutputStream(); byte[] buf = new byte[1024]; int n = 0; while (-1 !=
			 * (n = in.read(buf))) { out.write(buf, 0, n); } out.close(); in.close(); byte[]
			 * response = out.toByteArray(); System.out.print(item.getImage() + "\n");
			 * FileOutputStream fos = new FileOutputStream(wikiDir); fos.write(response);
			 * fos.close();
			 */
			if (!item.getImage().isEmpty()) {
				try (InputStream in = new URL(item.getImage()).openStream()) {
					Files.copy(in, Paths.get(wikiDir.getPath() + "/"
							+ item.getName().toLowerCase().replace('.', '_').replace(':', '_') + ".png"));
				} catch (MalformedURLException e) {
					System.out.println(item.getName() + ": " + item.getImage());
				} catch (FileAlreadyExistsException e) {
					System.out.println(item.getName() + " already exists");
				}
			}
		}
	}

	private static void addItem(String itemName) throws IOException {
		if (itemName.startsWith("Category:") || itemName.startsWith("File:")) {
			return;
		}
		try {
			Item item = scraper.scrapItem(url, itemName);
			if (item.getName() != null) {
				scrapedItems.add(item);
				System.out.println(new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date())
						+ " - Stored " + item.getName() + "'s info in the database");
			}
		} catch (HttpStatusException e) {
			System.out.println(new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date())
					+ " - Unable to scrap " + itemName + ". Skipping...");
			return;
		}
	}
}
