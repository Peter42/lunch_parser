package de.philipp1994.lunch.oxford_cafe;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class OCR {
	
	private final Font font;
	private static final List<Character> chars = new LinkedList<Character>();
	static {
		for(char c = 'a'; c <= 'z'; ++c){
			chars.add(c);
		}
		
		for(char c = 'A'; c <= 'Z'; ++c){
			chars.add(c);
		}
		
		chars.add('Ä');
		chars.add('ä');
		chars.add('Ö');
		chars.add('ä');
		chars.add('Ü');
		chars.add('ü');

		chars.add('-');
		chars.add(',');
		chars.add('\'');
	}
	
	public OCR(Font font, final double SIZE_FAKTOR) {
		this.font = font.deriveFont((float) (28.0f * SIZE_FAKTOR) ).deriveFont(Font.BOLD);
	}
	
	public List<String> detect(BufferedImage image) {
		
		int state = -1;
		
		List<String> data = new LinkedList<>();
		
		for(int y = 0; y < image.getHeight(); ++y) {
			long sum = 0;
			
			for(int x = 0; x < image.getWidth(); ++x) {
				sum += (255 - new Color(image.getRGB(x, y)).getRed());
			}
			
			if(state == -1) {
				if(sum > 0) {
					state = y;
				}
			}
			else {
				if(sum == 0) {
					data.add(detectRow(image.getSubimage(0, state, image.getWidth(), y - state)));
					state = -1;
					
				}
			}
		}

		return data;
	}

	private String detectRow(BufferedImage image) {
		int state = -1;
		String data = "";
		
		int lastEnd = -1;
		
		for(int x = 0; x < image.getWidth(); ++x) {
			long sum = 0;
			
			for(int y = 0; y < image.getHeight(); ++y) {
				sum += (255 - new Color(image.getRGB(x, y)).getRed());
			}
			
			if(state == -1) {
				if(sum > 0) {
					state = x;
				}
			}
			else {
				if(sum == 0) {
					if(lastEnd > 0 && lastEnd + 5 <= state) {
						data += " ";
					}
					
					data += detectChar( image.getSubimage(state, 0, x - state, image.getHeight())  );
					state = -1;
					
					lastEnd = x;
				}
			}
		}

		data = data.replaceAll(",,", "\"");
		data = data.replaceAll("''", "\"");
		
		return data;
	}

	int detection = 0;
	
	private char detectChar(BufferedImage subimage) {
		BufferedImage cache = new BufferedImage(subimage.getWidth(), subimage.getHeight(), subimage.getType());
		
		detection++;
		
		int best = Integer.MAX_VALUE;
		char match = '?';
		for(char c : chars){
			int misses = Math.min( Math.min(testLetter(subimage, cache, c, -1), testLetter(subimage, cache, c, -2)), testLetter(subimage, cache, c, -3));
			if(misses < best) {
				match = c;
				best = misses;
			}
		}
		
		Graphics2D g = (Graphics2D) subimage.getGraphics();
		g.setColor(new Color(255, 0, 0, 150));
		
		drawLetter(match, g, -1);
		
		return match;
	}
	
	private double drawLetter(char letter, Graphics2D g, int offset) {
		g.setFont(font);
		TextLayout txt = new TextLayout(letter + "", font, g.getFontRenderContext());
		g.drawString(letter + "", offset, (int)(txt.getAscent() - txt.getDescent() + 1 ));
		
		return txt.getBounds().getWidth();
	}
	
	private int testLetter(BufferedImage subimage, BufferedImage cache, char letter, int offset) {
		Graphics2D g = (Graphics2D) cache.getGraphics();
		
		g.setColor(Color.BLACK);
		g.setBackground(Color.WHITE);
		g.clearRect(0, 0, cache.getWidth(), cache.getHeight());

		
		int misscount = (int) Math.abs(drawLetter(letter, g, offset) - subimage.getWidth()) * 3;
		
		for(int x = 0; x < subimage.getWidth(); ++x) {
			for(int y = 0; y < subimage.getHeight(); ++y) {
				if( (new Color(cache.getRGB(x, y)).getGreen() != 0) != (new Color(subimage.getRGB(x, y)).getGreen() != 0) ) {
					++misscount;
					if (new Color(cache.getRGB(x, y)).getRed() != 0) {
						cache.setRGB(x, y, Color.BLUE.getRGB());
					}
					if (new Color(subimage.getRGB(x, y)).getRed() != 0) {
						cache.setRGB(x, y, Color.RED.getRGB());
					}
				}
			}
		}
		
		return misscount;
	}

}
