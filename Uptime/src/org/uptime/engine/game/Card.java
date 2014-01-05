package org.uptime.engine.game;

public class Card {

	private Integer id;
	private String nameToFind;
	private String category;
	private boolean isFound;
	
	
	public Card(Integer id, String nameToFind, String category) {
		super();
		this.id = id;
		this.nameToFind = nameToFind;
		this.category = category;
	}


	public boolean isFound() {
		return isFound;
	}


	public void setFound(boolean isFound) {
		this.isFound = isFound;
	}


	public String getNameToFind() {
		return nameToFind;
	}
	
	
	
}
