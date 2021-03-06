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
package de.lars.remotelightclient.scene;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.tinylog.Logger;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.EffectManager.EffectType;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.scene.scenes.Fire;
import de.lars.remotelightclient.scene.scenes.Jungle;
import de.lars.remotelightclient.scene.scenes.NorthernLights;
import de.lars.remotelightclient.scene.scenes.Ocean;
import de.lars.remotelightclient.scene.scenes.SnowSparkle;
import de.lars.remotelightclient.scene.scenes.Space;
import de.lars.remotelightclient.scene.scenes.Sunset;
import de.lars.remotelightclient.utils.PixelColorUtils;

public class SceneManager {
	
	private Scene activeScene;
	private List<Scene> scenes;
	private boolean active = false;
	
	public SceneManager() {
		scenes = new ArrayList<Scene>();
		this.registerScenes();
	}
	
	public boolean isActive() {
		return (activeScene != null);
	}
	
	public Scene getActiveScene() {
		return activeScene;
	}
	
	public List<Scene> getScenes() {
		return this.scenes;
	}
	
	public void start(Scene scene) {
		Main.getInstance().getEffectManager().stopAllExceptFor(EffectType.Scene);
		if(activeScene != null) {
			activeScene.onDisable();
		}
		if(scene != null) {
			scene.onEnable();
		}
		activeScene = scene;
		this.loop();
	}
	
	public void stop() {
		if(activeScene != null) {
			activeScene.onDisable();
		}
		activeScene = null;
		OutputManager.addToOutput(PixelColorUtils.colorAllPixels(Color.BLACK, Main.getLedNum()));
	}
	
	private void loop() {
		if(!active) {
			active = true;
			Logger.info("Starting Scene Thread.");
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					while(activeScene != null) {
						try {
						activeScene.onLoop();
						
						} catch(Exception e) {
							Logger.error(e, "There was an error executing the scene '" + activeScene.getDisplayname() + "'.");
							// stop on exception
							stop();
							break;
						}
						
						int delay = activeScene.getDelay();
						
						try {
							Thread.sleep(delay);
						} catch (InterruptedException e) {
							Logger.error("Scene Thread could not wait for delay! (delay: " + delay + ")");
							e.printStackTrace();
						}
					}
					active = false;
					Logger.info("Stopped Scene Thread.");
				}
			}).start();
		}
	}
	
	private void registerScenes() {
		scenes.add(new Sunset());
		scenes.add(new Fire());
		scenes.add(new Ocean());
		scenes.add(new Jungle());
		scenes.add(new NorthernLights());
		scenes.add(new Space());
		scenes.add(new SnowSparkle());
		
	}

}
