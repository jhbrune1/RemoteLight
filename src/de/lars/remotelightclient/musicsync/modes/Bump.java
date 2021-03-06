/*******************************************************************************
 * ______                     _       _     _       _     _   
 * | ___ \                   | |     | |   (_)     | |   | |  
 * | |_/ /___ _ __ ___   ___ | |_ ___| |    _  __ _| |__ | |_ 
 * |    // _ \ '_ ` _ \ / _ \| __/ _ \ |   | |/ _` | '_ \| __|
 * | |\ \  __/ | | | | | (_) | ||  __/ |___| | (_| | | | | |_ 
 * \_| \_\___|_| |_| |_|\___/ \__\___\_____/_|\__, |_| |_|\__|
 *                                             __/ |          
 *                                            |___/           
 * 
 * Copyright (C) 2019 Lars O.
 * 
 * This file is part of RemoteLight.
 ******************************************************************************/
package de.lars.remotelightclient.musicsync.modes;

import java.awt.Color;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.musicsync.MusicEffect;
import de.lars.remotelightclient.settings.SettingsManager;
import de.lars.remotelightclient.settings.SettingsManager.SettingCategory;
import de.lars.remotelightclient.settings.types.SettingBoolean;
import de.lars.remotelightclient.settings.types.SettingInt;
import de.lars.remotelightclient.utils.ColorUtil;
import de.lars.remotelightclient.utils.PixelColorUtils;

public class Bump extends MusicEffect {
	
	private Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.CYAN, Color.YELLOW, Color.MAGENTA, Color.WHITE, Color.PINK};
	private int colorIndex = 0;
	private Color color = Color.black;
	private int pDelay; //previous delay
	private SettingsManager s = Main.getInstance().getSettingsManager();

	public Bump() {
		super("Bump");
		
		s.addSetting(new SettingInt("musicsync.bump.speed", "Speed", SettingCategory.MusicEffect, "", 50, 20, 100, 5));
		this.addOption("musicsync.bump.speed");
		s.addSetting(new SettingBoolean("musicsync.bump.background", "Background", SettingCategory.MusicEffect, "", true));
		this.addOption("musicsync.bump.background");
		s.addSetting(new SettingBoolean("musicsync.bump.center", "Center", SettingCategory.MusicEffect, "", false));
		this.addOption("musicsync.bump.center");
	}
	
	@Override
	public void onEnable() {
		pDelay = Main.getInstance().getMusicSyncManager().getDelay();
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		Main.getInstance().getMusicSyncManager().setDelay(pDelay);
		super.onDisable();
	}
	
	@Override
	public void onLoop() {
		Main.getInstance().getMusicSyncManager().setDelay(((SettingInt) s .getSettingFromId("musicsync.bump.speed")).getValue());
		boolean background = ((SettingBoolean) s.getSettingFromId("musicsync.bump.background")).getValue();
		boolean center = ((SettingBoolean) s.getSettingFromId("musicsync.bump.center")).getValue();
		
		if(this.isBump()) {
			if(++colorIndex >= colors.length) {
				colorIndex = 0;
			}
			color = colors[colorIndex];	// bump -> new color
		} else {
			if(background) {
				color = ColorUtil.dimColor(colors[colorIndex], 5);
			} else {
				color = Color.BLACK;
			}
		}
		
		if(!center) {
			PixelColorUtils.shiftRight(3);
			for(int i = 0; i < 3; i++) {
				PixelColorUtils.setPixel(i, color);
			}
		} else {
			PixelColorUtils.shiftCenter(1);
			int ledNum = Main.getLedNum();
			PixelColorUtils.setPixel(ledNum/2, color);
			PixelColorUtils.setPixel(ledNum/2 - 1, color);
		}
		super.onLoop();
	}

}
