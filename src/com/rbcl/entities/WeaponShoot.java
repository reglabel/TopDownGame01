package com.rbcl.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.rbcl.main.Game;
import com.rbcl.world.Camera;
import com.rbcl.world.WallTile;
import com.rbcl.world.World;

public class WeaponShoot extends Entity {
	
	private double dx, dy;
	private int life, curLife;
	private double spd;
	
	public WeaponShoot(int x, int y, int width, int height, BufferedImage sprite, double dx, double dy) {
		super(x, y, width, height, sprite);
		this.dx = dx;
		this.dy = dy;
		this.spd = 4;
		this.life = 25;
		this.curLife = 0;
	}

	public void tick() {
		x+=dx*spd;
		y+=dy*spd;
		curLife++;
		
		if(curLife == life) {
			Game.weaponShoots.remove(this);
			return;
		}
		
		checkCollisionWall();
		
	}
	
	private void checkCollisionWall() {
		Rectangle maskTile, maskShoot = new Rectangle((int)(x - Camera.getX()), (int)(y - Camera.getY()), 4,4);
		for(int i = 0; i < World.walls.size(); i++) {
			WallTile t = World.walls.get(i);
			maskTile = new Rectangle(t.getX() - Camera.getX(), t.getY() - Camera.getY(), 16, 16);
			if(maskTile.intersects(maskShoot)) {
				Game.weaponShoots.remove(this);
				return;
			}
		}
	}
	
	
	
	public void render(Graphics g) {
		g.setColor(Color.YELLOW);
		g.fillOval(this.getX() - Camera.getX(), this.getY() - Camera.getY(), width, height);
	}
}
