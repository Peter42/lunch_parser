package de.philipp1994.lunch.common.tools;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class MaxAgeCache<K, V> {
	
	private class Entry {
		private long expiryTime;
		private V value;
		public boolean isExpired() {
			return this.expiryTime < System.currentTimeMillis();
		}
	}
	
	private final long maxAgeInMillis;
	
	private final Function<? super K, ? extends V> mappingFunction;
	private Entry map(K key) {
		V value = this.mappingFunction.apply(key);
		Entry entry = new Entry();
		entry.expiryTime = System.currentTimeMillis() + maxAgeInMillis;
		entry.value = value;
		return entry;
	}
	
	private final Map<K, Entry> map = Collections.synchronizedMap(new LinkedHashMap<K, Entry>() {
		private static final long serialVersionUID = 4788167531564359227L;

		protected boolean removeEldestEntry(Map.Entry<K,Entry> eldest) {
			return eldest.getValue().isExpired();
		}
	});
	
	public MaxAgeCache(long maxAge, TimeUnit unit, Function<? super K, ? extends V> mappingFunction) {
		if(TimeUnit.MILLISECONDS.ordinal() >= unit.ordinal()) {
			throw new IllegalArgumentException("Use milliseconds or more");
		}
		
		this.maxAgeInMillis = unit.toMillis(maxAge);
		this.mappingFunction = mappingFunction;
	}
	
	public V get(K key) {
		Entry entry = map.computeIfAbsent(key, this::map);
		if(entry.isExpired()) {
			map.remove(key);
			entry = map.computeIfAbsent(key, this::map);
		}
		return entry.value;
	}
}
