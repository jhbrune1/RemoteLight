package de.lars.remotelightclient.animation.animations;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.lars.remotelightclient.Main;
import de.lars.remotelightclient.animation.Animation;
import de.lars.remotelightclient.out.OutputManager;
import de.lars.remotelightclient.settings.SettingsManager.SettingCategory;
import de.lars.remotelightclient.settings.types.SettingBoolean;
import de.lars.remotelightclient.settings.types.SettingColor;
import de.lars.remotelightclient.settings.types.SettingInt;
import de.lars.remotelightclient.utils.PixelColorUtils;
import de.lars.remotelightclient.utils.RainbowWheel;

public class Snake extends Animation {
	
	private List<Integer> snakePos;
	private List<Color> snakeColor;
	private List<Byte> snakeDirection;
	private Color colorTale, colorHead, colorFruit;
	private byte direction = 1;	// +1 or -1
	private int fruitPos;
	private int rainbowPos = 0;

	public Snake() {
		super("Snake");
		this.addSetting(new SettingBoolean("animation.snake.randomcolor", "Random color", SettingCategory.Intern, null, true));
		this.addSetting(new SettingBoolean("animation.snake.rainbow", "Rainbow color", SettingCategory.Intern, null, false));
		this.addSetting(new SettingColor("animation.snake.colortale", "Tail color", SettingCategory.Intern,	null, Color.CYAN));
		this.addSetting(new SettingColor("animation.snake.colorhead", "Head color", SettingCategory.Intern,	null, Color.GREEN));
		this.addSetting(new SettingColor("animation.snake.colorfruit", "Fruit color", SettingCategory.Intern,	null, Color.RED));
		this.addSetting(new SettingInt("animation.snake.maxlength", "Max length", SettingCategory.Intern, "Maximum tail length", 200, 5, 999, 1));
	}
	
	@Override
	public void onEnable() {
		snakePos = new ArrayList<>();
		snakeColor = new ArrayList<>();
		snakeDirection = new ArrayList<>();
		
		if(!((SettingBoolean) getSetting("animation.snake.randomcolor")).getValue()) {
			colorTale = ((SettingColor) getSetting("animation.snake.colortale")).getValue();
			colorHead = ((SettingColor) getSetting("animation.snake.colorhead")).getValue();
			colorFruit = ((SettingColor) getSetting("animation.snake.colorfruit")).getValue();
		} else {
			colorTale = RainbowWheel.getRandomColor();
			colorHead = RainbowWheel.getRandomColor();
			colorFruit = Color.RED;
		}
		
		int startPoint = new Random().nextInt(Main.getLedNum());
		snakePos.add(startPoint);
		snakeColor.add(colorHead);
		
		fruitPos = new Random().nextInt(Main.getLedNum());
		if(fruitPos > startPoint) {
			direction = 1;
		} else {
			direction = -1;
		}
		snakeDirection.add(direction);
		
		paintSnake();
		super.onEnable();
	}
	
	@Override
	public void onLoop() {
		if(!((SettingBoolean) getSetting("animation.snake.randomcolor")).getValue() && !((SettingBoolean) getSetting("animation.snake.rainbow")).getValue()) {
			colorTale = ((SettingColor) getSetting("animation.snake.colortale")).getValue();
			colorHead = ((SettingColor) getSetting("animation.snake.colorhead")).getValue();
			colorFruit = ((SettingColor) getSetting("animation.snake.colorfruit")).getValue();
		} else if(((SettingBoolean) getSetting("animation.snake.rainbow")).getValue()) {
			colorTale = RainbowWheel.getRainbow()[rainbowPos];
			colorHead = RainbowWheel.getRainbow()[0];
		}
		snakeColor.set(0, colorHead);
		
		int maxLength = ((SettingInt) getSetting("animation.snake.maxlength")).getValue();
		if(snakePos.size() > maxLength) {
			// reset snake
			onEnable();
		}
		
		// move snake
		moveSnake();
		
		if(snakePos.get(0) == fruitPos) {
			
			// set new fruit position
			fruitPos = new Random().nextInt(Main.getLedNum());
			if(fruitPos > snakePos.get(0)) {
				direction = 1;
			} else {
				direction = -1;
			}
			
			// add +1 to the snake
			extendSnake();
			
			// rainbow color
			rainbowPos += 4;
			if(rainbowPos >= RainbowWheel.getRainbow().length) {
				rainbowPos = 0;
			}
		}
		paintSnake();
		super.onLoop();
	}
	
	
	private void paintSnake() {
		Color[] strip = PixelColorUtils.colorAllPixels(Color.BLACK, Main.getLedNum());
		
		// paint fruit
		strip[fruitPos] = colorFruit;
		// paint snake
		for(int i = snakePos.size() - 1; i >= 0; i--) {
			int pos = snakePos.get(i);
			strip[pos] = snakeColor.get(i);
		}
		OutputManager.addToOutput(strip);
	}
	
	
	private void moveSnake() {
		for(int i = 0; i < snakePos.size(); i++) {
			int newPos = snakePos.get(i) + snakeDirection.get(i);
			
			if(newPos >= Main.getLedNum()) {
				newPos = 0;
			}
			if(newPos < 0) {
				newPos = Main.getLedNum() - 1;
			}
			
			snakePos.set(i, newPos);
		}
		
		for(int i = snakeDirection.size() - 1; i > 0; i--) {
			byte prevDir = snakeDirection.get(i - 1);
			snakeDirection.set(i, prevDir);
		}
		
		// set new direction
		snakeDirection.set(0, direction);
	}
	
	
	private void extendSnake() {
		int lastPos = snakePos.get(snakePos.size() - 1);
		byte lastDir = snakeDirection.get(snakeDirection.size() - 1);
		int pos = lastPos - lastDir;
		
		if(pos >= Main.getLedNum()) {
			pos = 0;
		}
		if(pos < 0) {
			pos = Main.getLedNum() - 1;
		}
		
		snakePos.add(pos);
		snakeColor.add(colorTale);
		
		snakeDirection.add(lastDir);
	}

}
