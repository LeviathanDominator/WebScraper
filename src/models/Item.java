package models;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.select.Elements;

public class Item {

	private String name;
	private String image;
	private List<String> types;
	private List<String[]> elements;

	public Item(String name) {
		this.name = name;
		types = new ArrayList<>();
		elements = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public String getImage() {
		return image;
	}

	public void addImage(String image) {
		this.image = image;
	}

	public void addData(Elements data, String type) {
		String[] newElements = data.toString().replaceAll("<[^>]*>", "").split("\n");
		String[] elementsArray = new String[newElements.length];
		for (int i = 0; i < newElements.length; i++) {
			elementsArray[i] = newElements[i].trim();
		}
		elements.add(elementsArray);
		types.add(type);
	}

	public String toString() {
		String result = "\n\n" + name + "\n\n";
		if (types.size() == elements.size()) {
			for (int i = 0; i < types.size(); i++) {
				String elements = "";
				String[] items = this.elements.get(i);
				for (int j = 0; j < items.length; j++) {
					elements = elements.concat(String.format("   %s\n", items[j]));
				}
				result = result.concat(String.format("%s: %s\n", types.get(i), elements));
			}
		}
		return result;
	}
	
	public String parseToCsv() {
		String SEPARATOR = ";";
		String result = name;
		if (types.size() == elements.size()) {
			for (int i = 0; i < elements.size(); i++) {
				result = result.concat(SEPARATOR + types.get(i));
				for (int j = 0; j < elements.get(i).length; j++) {
					String element = elements.get(i)[j];
					result = result.concat(SEPARATOR + element);
				}
			}
		}
		result = result.concat(SEPARATOR + (image != null ? image : ""));
		return result.concat(SEPARATOR);
	}

}
