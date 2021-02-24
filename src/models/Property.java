package models;

public class Property {
	
	private String title;
	private String description;
	private String name;
	private String phone;
	
	public Property(String title, String description, String name, String phone) {
		this.title = title;
		this.description = description;
		this.name = name;
		this.phone = phone;
	}
	
	public String toString() {
		return String.format("%s;%s;%s;%s", title, description, name, phone);
	}

}
