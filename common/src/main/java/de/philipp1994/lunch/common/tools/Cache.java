package de.philipp1994.lunch.common.tools;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Philipp Stehle
 * @deprecated Use  {@link MaxAgeCache}
 */
@Deprecated
public class Cache<K, V> extends LinkedHashMap<K, V> {
	
	
	public static <K, V> Map<K, V> getSynchronizedCache(int size) {
		return Collections.synchronizedMap(new Cache<K, V>(size));
	}

	private static final long serialVersionUID = 3078147103465778124L;
	private final int size;

	private Cache(int size) {
		this.size = size;
	}

	protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
		return size() > size;
	}

}
