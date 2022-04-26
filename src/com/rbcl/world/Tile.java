package com.rbcl.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.rbcl.main.Game;

public class Tile {

	public static BufferedImage TILE_FLOOR = Game.spritesheet.getSprite(0, 0, 16, 16);
	public static BufferedImage TILE_WALL = Game.spritesheet.getSprite(16, 0, 16, 16);

	private BufferedImage sprite;
	private int x, y;
	
	public Tile(BufferedImage sprite, int x, int y) {
		this.sprite = sprite;
		this.x = x;
		this.y = y;
	}

	public void render(Graphics g) {
		g.drawImage(sprite, x - Camera.getX(), y - Camera.getY(), null);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	
}
