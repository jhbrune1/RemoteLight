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
import de.lars.remotelightclient.musicsync.MusicSyncUtils;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.utils.PixelColorUtils;

public class Fade extends MusicEffect {
	
	private Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.CYAN, Color.YELLOW, Color.MAGENTA, Color.WHITE, Color.PINK};
	private Color fadeLastColor = colors[0];
	private int color = 0;
	
	public Fade() {
		super("Fade");
	}
	
	@Override
	public void onLoop() {
		if(this.isBump()) {
			if(color < colors.length - 1) {
				color++;
			}
			else color = 0;
			fadeLastColor = colors[color];
		}
		OutputManager.addToOutput(PixelColorUtils.colorAllPixels(fadeLastColor, Main.getLedNum()));
		
		if((fadeLastColor.getRed() != 0) || (fadeLastColor.getGreen() != 0) || (fadeLastColor.getBlue() != 0)) fadeLastColor = MusicSyncUtils.dimColor(fadeLastColor, 2);
		else {
			if(color < colors.length - 1) {
				color++;
			}
			else color = 0;
			fadeLastColor = colors[color];
		}
		super.onLoop();
	}

}
