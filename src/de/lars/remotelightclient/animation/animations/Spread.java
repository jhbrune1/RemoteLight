package de.lars.remotelightclient.animation.animations;

import java.awt.Color;
import java.util.Random;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.animation.Animation;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.utils.ColorUtil;
import de.lars.remotelightclient.utils.PixelColorUtils;
import de.lars.remotelightclient.utils.RainbowWheel;

public class Spread extends Animation {
	
	private Random random;
	private Color[] strip;
	private int hueStep;

	public Spread() {
		super("Spread");
	}
	
	@Override
	public void onEnable() {
		random = new Random();
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, Main.getLedNum());
		hueStep = 0;
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		float ranPos = (float) random.nextGaussian();
		int pos = (int) Math.round(ranPos * (strip.length / 6) + (strip.length / 2));	// average 'length / 2' and deviation 'length / 6'
		
		int randHue = hueStep + new Random().nextInt(30);
		if(randHue >= RainbowWheel.getRainbow().length) {
			randHue = randHue - RainbowWheel.getRainbow().length;
		}
		
		Color c = RainbowWheel.getRainbow()[randHue];
		if(pos > 0 && pos < strip.length) {
			strip[pos] = ColorUtil.mixColor(strip[pos], c);
		}
		
		if(++hueStep >= RainbowWheel.getRainbow().length) {
			hueStep = 0;
		}
		
		// fade all to black
		strip = ColorUtil.dimColorSimple(strip, 1);
		
		OutputManager.addToOutput(strip);
		super.onLoop();
	}

}
