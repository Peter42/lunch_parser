package de.philipp1994.lunch.common;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class LunchMenu implements Serializable {
	private static final long serialVersionUID = 2865237799304727607L;
	
	private final String name;
	private final List<LunchMenuItem> lunchItems;
	
	public LunchMenu(String name) {
		super();
		this.name = name;
		this.lunchItems = new LinkedList<LunchMenuItem>();
	}

	public String getName() {
		return name;
	}

	public List<LunchMenuItem> getLunchItems() {
		return Collections.unmodifiableList(this.lunchItems);
	}
	
	public void addLunchItem(LunchMenuItem item) {
		this.lunchItems.add(item);
	}
}
