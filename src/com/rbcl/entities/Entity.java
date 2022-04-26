package com.rbcl.entities;

//import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.List;

import com.rbcl.main.Game;
import com.rbcl.world.Camera;
import com.rbcl.world.Node;
//import com.rbcl.world.Vector2i;
//import com.rbcl.world.World;

public class Entity {
	
	public static BufferedImage LIFEPACK_EN = Game.spritesheet.getSprite(96, 0, 16, 16);
	public static BufferedImage WEAPON_EN = Game.spritesheet.getSprite(128, 0, 16, 16);
	public static BufferedImage AMMOPACK_EN = Game.spritesheet.getSprite(96, 16, 16, 16);
	public static BufferedImage ENEMY_EN = Game.spritesheet.getSprite(112, 16, 16, 16);
	public static BufferedImage ENEMY_FEEDBACK = Game.spritesheet.getSprite(112, 32, 16, 16);

	public static BufferedImage WEAPON_UP = Game.spritesheet.getSprite(144, 32, 16, 16);
	public static BufferedImage WEAPON_DOWN = Game.spritesheet.getSprite(144, 16, 16, 16);
	public static BufferedImage WEAPON_LEFT = Game.spritesheet.getSprite(128, 32, 16, 16);
	public static BufferedImage WEAPON_RIGHT = Game.spritesheet.getSprite(144, 0, 16, 16);
	
	//protected int speed = 1; -> p/ AStar
	protected double speed = 0.3;
	protected double x, y;
	protected int z;
	protected int width, height;
	protected int maskx, masky, mwidth, mheight;
	protected int frames, maxFrames, index, maxIndex;
	
	private BufferedImage sprite;
	
	protected List<Node> path;
	
	public int depth;

	public Entity(int x, int y, int width, int height, BufferedImage sprite) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.sprite = sprite;
		
		this.maskx = 0;
		this.masky = 0;
		this.mwidth = width;
		this.mheight = height;
	}
	
	public static Comparator<Entity> nodeSorter = new Comparator<Entity>() {	
		@Override
		public int compare(Entity n0, Entity n1) {
			if(n1.depth < n0.depth) {
				return +1;
			}
			if(n1.depth > n0.depth) {
				return -1;
			}
			return 0;
		}
	};//quanto maior o depth de um elemento, mais em cima a entidade vai ficar, ser a ultima a ser renderizada
	
	public void setMask(int maskx, int masky, int mwidth, int mheight) {
		this.maskx = maskx;
		this.masky = masky;
		this.mwidth = mwidth;
		this.mheight = mheight;
	}
	
	public void setX(int newX) {
		this.x = newX;
	}
	
	public void setY(int newY) {
		this.y = newY;
	}
	
	public void render(Graphics g) {
		g.drawImage(sprite, this.getX() - Camera.getX(), this.getY() - Camera.getY(), null);
		//g.setColor(Color.BLACK);
		//g.fillRect(this.getX()+maskx - Camera.getX(), this.getY()+masky-Camera.getY(), mwidth, mheight);
	}

	public void tick() {
		
	}
	
	public double calculateDistance(int x1, int y1, int x2, int y2) {
		return Math.sqrt(((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2)));
	}
	
	public boolean isColliding(int xNext, int yNext) {
		Rectangle currentEnemy = new Rectangle(xNext + maskx, yNext + masky, mwidth, mheight);
		for(int i = 0; i < Game.enemies.size(); i++) {
			Enemy en = Game.enemies.get(i);
			if(en == this) {
				continue;
			}
			
			Rectangle targetEnemy = new Rectangle(en.getX() + maskx, en.getY() + masky, mwidth, mheight);
			if(currentEnemy.intersects(targetEnemy)) {
				return true;
			}
		}
		
		return false;
	}
	
	/*
	public void followPath(List<Node> path) {
		if(path != null) {
			if(path.size() > 0 ) {
				Vector2i target = path.get(path.size() - 1).tile;
				//xprev = x;
				//yprev = y;
				if(x < target.x * 16 && !isColliding(this.getX()+speed, this.getY()) && World.isFree(this.getX()+speed, this.getY())) {
					x += speed;
				} else if(x > target.x * 16 && !isColliding(this.getX()-speed, this.getY()) && World.isFree(this.getX()-speed, this.getY())) {
					x -= speed;
				}
				
				if(y < target.y * 16 && !isColliding(this.getX(), this.getY()+speed) && World.isFree(this.getX(), this.getY()+speed)) {
					y += speed;
				} else if(y > target.y * 16 && !isColliding(this.getX(), this.getY()-speed) && World.isFree(this.getX(), this.getY()-speed)) {
					y -= speed;
				}
				
				if(x == target.x * 16 && y == target.y * 16) {
					path.remove(path.size() - 1);
				}
			}
		}
	}
	*/
	
	public void setSeqAnimation(int frames, int maxFrames, int index, int maxIndex) {
		this.frames = frames;
		this.maxFrames = maxFrames;
		this.index = index;
		this.maxIndex = maxIndex;
	}
	
	protected void animar() {
		frames++;
		if(frames == maxFrames) {
			frames = 0;
			index++;
			if(index > maxIndex) {
				index = 0;
			}
		}
	}
	
	public static boolean isColliding(Entity e1, Entity e2) {
		Rectangle e1Mask = new Rectangle(e1.getX()+e1.maskx, e1.getY()+e1.masky, e1.mwidth, e1.mheight);
		Rectangle e2Mask = new Rectangle(e2.getX()+e2.maskx, e2.getY()+e2.masky, e2.mwidth, e2.mheight);

		if(e1Mask.intersects(e2Mask) && e1.z == e2.z) {
			return true;
		}
		return false;
	}
	
	public int getX() {
		return (int)this.x;
	}

	public int getY() {
		return (int)this.y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
}
