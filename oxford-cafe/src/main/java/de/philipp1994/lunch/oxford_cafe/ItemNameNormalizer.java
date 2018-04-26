package de.philipp1994.lunch.oxford_cafe;

import java.util.Arrays;

public final class ItemNameNormalizer {
	
	private static final String[] LOWER_CASE_WORDS = new String[] {
		"ab", "abseits", "an", "auf", "aus", "außer", "außerhalb", "bei", "bis", "diesseits",
		"durch", "entlang", "für", "gegen", "gegenüber", "hinter", "in", "inmitten", "innerhalb",
		"jenseits", "längs", "mit", "nach", "neben", "oberhalb", "unter", "unterhalb", "unweit",
		"von", "vom", "vor", "zu", "zwischen", "zwischen", "über"
	};

	private ItemNameNormalizer() {
		throw new RuntimeException("Utility Class - Do not instantiate");
	}
	
	public static String normalize(String itemName) {
		
		StringBuffer buffer = new StringBuffer(itemName);
		boolean lastCharWasSeperator = true;
		
		for(int i = 0; i < buffer.length(); ++i) {
			if(lastCharWasSeperator) {
				
				int endOfWord = buffer.indexOf(" ", i);
				String word = buffer.substring(i, endOfWord < 0 ? buffer.length() : endOfWord);
				
				boolean isLowerCaseWord = Arrays.stream(LOWER_CASE_WORDS).filter(s -> {
					return s.equalsIgnoreCase(word);
				}).findAny().isPresent();
				
				if(isLowerCaseWord) {
					buffer.setCharAt(i, Character.toLowerCase(buffer.charAt(i)));
				}
				else {
					buffer.setCharAt(i, Character.toUpperCase(buffer.charAt(i)));
				}
			}
			else {
				buffer.setCharAt(i, Character.toLowerCase(buffer.charAt(i)));
			}
			
			lastCharWasSeperator = buffer.charAt(i) == ' ' || buffer.charAt(i) == '-';
		}
		
		return buffer.toString();
	}
	
	
	public static void main(String[] args) {
		System.out.println(normalize("SPAGHETTI MIT BEAT geschwenkt in Zuckerschoten & Speck"));
		System.out.println(normalize("KNUSPER-CAMEMBERT & Tomate-Rauke Salat"));
		System.out.println(normalize("#TeilenKennIchNicht Hausgemachte Lasagne an Salatbouquet"));
		System.out.println(normalize("GLASIERTE HÄHNCHENSPIEßE auf Sommer-Gemüse"));
	}
	
}
