package de.philipp1994.lunch.common;

@FunctionalInterface
public interface ILunchMenuProvider {
	public LunchMenu getMenu();
}