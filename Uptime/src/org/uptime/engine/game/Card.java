package org.uptime.engine.game;

public class Card {

	private Integer id;
	
	/**
	 * Non active cards will not be used when building card lists for a game.
	 */
	private boolean isActiveInDB;
	
	private String nameToFind;
	private String category;
	private String url;
	private boolean isFound;

	public Card() {
		super();
	}
	
	public Card(Integer id, String nameToFind, String category) {
		super();
		this.id = id;
		this.nameToFind = nameToFind;
		this.category = category;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	public void setNameToFind(String nameToFind) {
		this.nameToFind = nameToFind;
	}

	public void setCategory(String category) {
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

	public Integer getId() {
		return id;
	}

	public String getCategory() {
		return category;
	}

	@Override
	public String toString() {
		return "Card [id=" + id + ", nameToFind=" + nameToFind + ", category=" + category + ", isFound=" + isFound
				+ ", isActiveInDB=" + isActiveInDB + "]";
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isActiveInDB() {
		return isActiveInDB;
	}

	public void setActiveInDB(boolean isActiveInDB) {
		this.isActiveInDB = isActiveInDB;
	}
	
}
