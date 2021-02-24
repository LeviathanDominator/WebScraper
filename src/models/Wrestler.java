package models;

import org.jsoup.select.Elements;

public class Wrestler {

	private String name;
	private String[] ringNames;
	private String height;
	private String weight;
	private String born;
	private String birthPlace;
	private String debut;
	private String image;

	public Wrestler(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void addData(Elements data, String type) {
		switch (type.toLowerCase()) {
		case "ring names":
			String[] ringNamesArray = data.toString().replaceAll("<[^>]*>", "").split("\n");
			for (int i = 1; i < ringNamesArray.length; i++) {
				if (ringNames == null) {
					ringNames = new String[ringNamesArray.length - 1];
				}
				ringNames[i - 1] = ringNamesArray[i].trim();
			}
			break;
		case "height":
			height = data.text();
			break;
		case "weight":
			weight = data.text();
			break;
		case "born":
			born = data.text();
			break;
		case "birth place":
			birthPlace = data.text();
			break;
		case "debut":
			debut = data.text();
			break;
		}

	}

	public String toString() {
		String SEPARATOR = ";";
		String result = name;
		result = result.concat(SEPARATOR + (height != null ? height : ""));
		result = result.concat(SEPARATOR + (weight != null ? weight : ""));
		result = result.concat(SEPARATOR + (born != null ? born : ""));
		result = result.concat(SEPARATOR + (birthPlace != null ? birthPlace : ""));
		result = result.concat(SEPARATOR + (debut != null ? debut : ""));
		result = result.concat(SEPARATOR + (image != null ? image : ""));
		return result.concat(SEPARATOR);
	}

	private String extractInsidePharentisis(String text) {
		return text;

	}

	public void addImage(String image) {
		this.image= image;
		
	}

}
