package com.rbcl.entities;

//import java.awt.Color;
//import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
//import java.util.Random;

import com.rbcl.main.Game;
import com.rbcl.main.Sound;
//import com.rbcl.world.AStar;
import com.rbcl.world.Camera;
//import com.rbcl.world.Vector2i;
import com.rbcl.world.World;

public class Enemy extends Entity{

	//private double speed;
	private BufferedImage[] sprites = new BufferedImage[2];
	private double life = 10;
	private boolean isDamaged = false;
	private int damagedFrames = 10, currentFrame = 0;
	
	public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, null);
				
		sprites[0] = Game.spritesheet.getSprite(112, 16, 16, 16);
		sprites[1] = Game.spritesheet.getSprite(128, 16, 16, 16);
		
		setMask(3,3,10,10);

		//this.speed = 0.3;
		setSeqAnimation(0,10,0,1);
	}
	
	public void tick() {
		depth = 1;
		/*//Para o AStar
		
		mwidth = 8;
		mheight = 8;*/
		
		//Usando algoritmo normal de achar player e machucar
		int xDoPlayer = Game.player.getX();
		int yDoPlayer = Game.player.getY();
		int xDoEnemy = this.getX();
		int yDoEnemy = this.getY();
		int subir = (int) (y - speed);
		int descer = (int) (y + speed);
		int direita = (int) (x + speed);
		int esquerda = (int) (x - speed);
		
		if(this.calculateDistance(xDoEnemy, yDoEnemy, xDoPlayer, yDoPlayer) < 80) {
			if(!(this.isCollindingWithPlayer())) {
				if(xDoEnemy < xDoPlayer &&
						World.isFree(direita, yDoEnemy) && 
						!(isColliding(direita, yDoEnemy))) {
					x += speed;
				} else if (xDoEnemy > xDoPlayer &&
						World.isFree(esquerda, yDoEnemy) && 
						!(isColliding(esquerda, yDoEnemy))){
					x -= speed;
				}
				
				else if(yDoEnemy < yDoPlayer &&
						World.isFree(xDoEnemy, descer) && 
						!(isColliding(xDoEnemy, descer))) {
					y += speed;
				} else if (yDoEnemy > yDoPlayer &&
						World.isFree(xDoEnemy, subir) && 
						!(isColliding(xDoEnemy, subir))){
					y -= speed;
				}
			} else {
				if(Game.rand.nextInt(100) < 10) {
					Sound.hurtPlayer.play();
					Game.player.life -= Game.rand.nextInt(10);
					Game.player.isDamaged = true;
				}
			}
		}
		
		/*
		//Usando AStar
		if(!isCollindingWithPlayer()){
			if(path == null || path.size() == 0) {
				Vector2i start = new Vector2i((int)(x/16), (int)(y/16));
				Vector2i end = new Vector2i((int)(Game.player.x/16), (int)(Game.player.y/16));
				path = AStar.findPath(Game.world, start, end);
			}
		} else{
			if(new Random().nextInt(100) < 5) {
				Sound.hurtPlayer.play();
				Game.player.life -= Game.rand.nextInt(3);
				Game.player.isDamaged = true;
			}
		}
		if(new Random().nextInt(100) < 100)
			followPath(path);
		*/
		
		animar();
		
		collidingShoot();
		
		if(life <= 0) {
			Sound.deathEnemy.play();
			destroySelf();
			return;
		}
		
		if(isDamaged) {
			currentFrame++;
			if(currentFrame == damagedFrames) {
				currentFrame = 0;
				isDamaged = false;
			}
		}
	}
	
	public void destroySelf() {
		Game.entities.remove(this);
		Game.enemies.remove(this);
	}
	
	public void collidingShoot() {
		for(int i = 0; i < Game.weaponShoots.size(); i++) {
			Entity e =Game.weaponShoots.get(i);
			if(Entity.isColliding(this, e)) {
				Sound.hurtEnemy.play();
				isDamaged = true;
				life = life - 2;
				Game.weaponShoots.remove(e);
				return;
			}
		}
	}
	
	public boolean isCollindingWithPlayer() {
		Rectangle currentEnemy = new Rectangle(this.getX() + maskx, this.getY() + masky, mwidth, mheight);
		Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), 16, 16);
		
		return currentEnemy.intersects(player);
	}
	
	
	public void render(Graphics g) {
		if(!isDamaged) {
			g.drawImage(sprites[index], this.getX() - Camera.getX(), this.getY() - Camera.getY(), null);
		} else {
			g.drawImage(Entity.ENEMY_FEEDBACK, this.getX() - Camera.getX(), this.getY() - Camera.getY(), null);
		}
		
		//debug pra ver a mascara
		//g.setColor(Color.blue);
		//g.fillRect(this.getX() + maskx - Camera.getX(), this.getY() + masky - Camera.getY(), mwidth, mheight);
	}
	
}
