package de.lars.remotelightclient.musicsync.modes;

import java.awt.Color;
import java.util.Random;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.musicsync.MusicEffect;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightclient.settings.SettingsManager.SettingCategory;
import de.lars.remotelightclient.settings.types.SettingColor;
import de.lars.remotelightclient.settings.types.SettingInt;
import de.lars.remotelightclient.settings.types.SettingSelection;
import de.lars.remotelightclient.settings.types.SettingSelection.Model;
import de.lars.remotelightclient.utils.ColorUtil;
import de.lars.remotelightclient.utils.PixelColorUtils;
import de.lars.remotelightclient.utils.RainbowWheel;

public class Bars extends MusicEffect {
	
	private SettingsManager s = Main.getInstance().getSettingsManager();
	private Color[] strip;
	private Color color;
	private int hue;
	private boolean lastPeak;
	private double avgVol;
	private int barWidth = 5;

	public Bars() {
		super("Bars");
		s.addSetting(new SettingInt("musicsync.bars.barwidth", "Bar width", SettingCategory.MusicEffect, null, 5, 1, 20, 1));
		this.addOption("musicsync.bars.barwidth");
		
		String[] modes = new String[] {"Rainbow", "Frequency", "Random", "Static"};
		s.addSetting(new SettingSelection("musicsync.bars.mode", "Color mode", SettingCategory.MusicEffect, null, modes, "Static", Model.ComboBox));
		this.addOption("musicsync.bars.mode");
		
		s.addSetting(new SettingColor("musicsync.bars.color", "Color", SettingCategory.MusicEffect, null, Color.RED));
		this.addOption("musicsync.bars.color");
	}
	
	@Override
	public void onEnable() {
		strip = PixelColorUtils.colorAllPixels(Color.BLACK, Main.getLedNum());
		lastPeak = false;
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		barWidth = ((SettingInt) s.getSettingFromId("musicsync.bars.barwidth")).getValue();	// get bar width from settings
		
		double vol = this.getSpl();
		avgVol = (vol + getMaxSpl() * (0.8 + getSensitivity() / 10)) / 2;	// smooth max volume and calculate average
		
		boolean peak = (vol > avgVol) && !lastPeak;	// peak detection
		lastPeak = peak;
		
		if(peak) {
			if(barWidth >= strip.length) barWidth = strip.length - 1; // prevent errors
			
			setColor();	// set color from chosen mode
			int pos = new Random().nextInt(strip.length - barWidth); // random position
			for(int i = 0; i < barWidth; i++) {
				strip[pos + i] = color;
			}
		}
		
		if(++hue > RainbowWheel.getRainbow().length) {	// increment hue value for rainbow mode
			hue = 0;
		}
		
		OutputManager.addToOutput(strip);
		strip = ColorUtil.dimColorSimple(strip, 10);
		super.onLoop();
	}
	
	
	private void setColor() {
		String mode = ((SettingSelection) s.getSettingFromId("musicsync.bars.mode")).getSelected();
		switch (mode.toLowerCase()) {
			case "static":
				color = ((SettingColor) s.getSettingFromId("musicsync.bars.color")).getValue();
				break;
			case "frequency":
				color = ColorUtil.soundToColor((int) this.getPitch());
				break;
			case "random":
				color = RainbowWheel.getRandomColor();
				break;
			case "rainbow": {
				int ranHue = new Random().nextInt(15) + hue;
				if(ranHue >= RainbowWheel.getRainbow().length) {
					ranHue -= RainbowWheel.getRainbow().length;
				}
				color = RainbowWheel.getRainbow()[ranHue];
				break;
			}
		}
	}

}
