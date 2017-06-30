package de.philipp1994.lunch.common;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class LunchMenu implements Serializable {
	private static final long serialVersionUID = 2865237799304727607L;
	
	private final String name;
	private final UUID uuid;
	private final List<LunchMenuItem> lunchItems;
	
	public LunchMenu(String name, UUID uuid) {
		super();
		this.name = name;
		this.uuid = uuid;
		this.lunchItems = new LinkedList<LunchMenuItem>();
	}

	public String getName() {
		return name;
	}

	public UUID getUuid() {
		return uuid;
	}

	public List<LunchMenuItem> getLunchItems() {
		return Collections.unmodifiableList(this.lunchItems);
	}
	
	public void addLunchItem(LunchMenuItem item) {
		this.lunchItems.add(item);
	}
}
