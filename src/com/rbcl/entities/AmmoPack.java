package com.rbcl.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.rbcl.main.Game;
import com.rbcl.world.Camera;

public class AmmoPack extends Entity {
	
	private BufferedImage[] sprites = new BufferedImage[2];

	public AmmoPack(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, null);
		
		sprites[0] = Game.spritesheet.getSprite(96, 16, 16, 16);
		sprites[1] = Game.spritesheet.getSprite(96, 32, 16, 16);
		
		setSeqAnimation(0, 20, 0, 1);
	}
	
	public void tick(){
		animar();
	}
	
	public void render(Graphics g) {
		g.drawImage(sprites[index], this.getX() - Camera.getX(), this.getY() - Camera.getY(), null);
	}

}
