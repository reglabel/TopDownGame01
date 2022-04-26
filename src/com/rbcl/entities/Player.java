package com.rbcl.entities;

//import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.rbcl.main.Game;
import com.rbcl.main.Sound;
import com.rbcl.world.Camera;
import com.rbcl.world.World;

public class Player extends Entity {
		
	public boolean right, left, up, down, moved/*, isJumping = false, jump = false*/;
	public boolean isDamaged, hasWeapon, shoot, mouseShoot/*, jumpUp = false, jumpDown = false*/;
	//private double speed;
	
	private BufferedImage[] rightPlayer, leftPlayer, frontPlayer, backPlayer;
	private BufferedImage damagedPlayer, damagedPlayerWithWeapon;
	
	public double life, maxLife;
	public int ammo, damageFrames, z, jumpFrames = 20, jumpCur = 0, jumpSpd = 1;
	public int mx, my;
	private int ultimaOcorrencia = 0;
	
	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		this.right = false;
		this.left = false;
		this.up = false;
		this.down = false;
		this.moved = false;
		this.isDamaged = false;
		this.hasWeapon = false;
		this.shoot = false;
		this.mouseShoot = false;
		
		this.speed = 0.9;
		setSeqAnimation(0,5,0,3);
		
		life = 100;
		maxLife = 100;
		this.ammo = 0;
		this.damageFrames = 0;
		
		this.rightPlayer = new BufferedImage[4];
		this.leftPlayer = new BufferedImage[4];
		this.frontPlayer = new BufferedImage[4];
		this.backPlayer = new BufferedImage[4];
		this.damagedPlayer = Game.spritesheet.getSprite(32, 32, 16, 16);
		this.damagedPlayerWithWeapon = Game.spritesheet.getSprite(48, 32, 16, 16);
		
		for(int i = 0; i < 4; i++) {
			rightPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 0, 16, 16);
			leftPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 16, 16, 16);
			frontPlayer[i] = Game.spritesheet.getSprite(0, 16 + (i*16), 16, 16);
			backPlayer[i] = Game.spritesheet.getSprite(16, 16 + (i*16), 16, 16);
		}
	}
	
	public void render(Graphics g) {
		if(!isDamaged) {
			if(right) {
				ultimaOcorrencia = 1;
			} else if(left) {
				ultimaOcorrencia = 2;
			} else if(up) {
				ultimaOcorrencia = 3;
			} else if(down) {
				ultimaOcorrencia = 4;
			}
			
			
			switch(ultimaOcorrencia) {
				case 1:
					g.drawImage(rightPlayer[index], this.getX() - Camera.getX(), this.getY() - Camera.getY() - z, null);
					if(hasWeapon) {
						g.drawImage(Entity.WEAPON_RIGHT, this.getX() + 1 - Camera.getX(), this.getY() + 1 - Camera.getY()  - z, null);
					}
					break;
					
				case 2:
					g.drawImage(leftPlayer[index], this.getX() - Camera.getX(), this.getY() - Camera.getY()  - z, null);
					if(hasWeapon) {
						g.drawImage(Entity.WEAPON_LEFT, this.getX() - 1 - Camera.getX(), this.getY() + 1 - Camera.getY()  - z, null);
					}					
					break;
				
				case 3:
					g.drawImage(backPlayer[index], this.getX() - Camera.getX(), this.getY() - Camera.getY()  - z, null);
					if(hasWeapon) {
						g.drawImage(Entity.WEAPON_UP, this.getX() + 5 - Camera.getX(), this.getY() + 1 - Camera.getY()  - z, null);
					}	
					break;
				
				case 4:
					g.drawImage(frontPlayer[index], this.getX() - Camera.getX(), this.getY() - Camera.getY()  - z, null);
					if(hasWeapon) {
						g.drawImage(Entity.WEAPON_DOWN, this.getX() + 3 - Camera.getX(), this.getY() + 2 - Camera.getY()  - z, null);
					}
					break;
					
				default:
					g.drawImage(frontPlayer[0], this.getX() - Camera.getX(), this.getY() - Camera.getY()  - z, null);
					if(hasWeapon) {
						g.drawImage(Entity.WEAPON_DOWN, this.getX() + 3 - Camera.getX(), this.getY() + 2 - Camera.getY()  - z, null);
					}
					break;
			}
		} else {
			g.drawImage(damagedPlayer, this.getX() - Camera.getX(), this.getY() - Camera.getY()  - z, null);
			if(hasWeapon) {
				g.drawImage(damagedPlayerWithWeapon, this.getX() - Camera.getX(), this.getY() - Camera.getY()  - z, null);
			}
		}
		
		/*
		if(isJumping) {
			g.setColor(Color.DARK_GRAY);
			g.fillOval(this.getX() - Camera.getX() + 5, this.getY() - Camera.getY() + 13, 5,4);
		}
		*/
		
	}
	
	public void tick() {
		depth = 0;
		moved = false;
		
		/*
		if(jump) {
			if(!isJumping) {
				jump = false;
				isJumping = true;
				jumpUp = true;
			}
		}
		
		if(isJumping) {
			if(jumpUp) {
				jumpCur += jumpSpd;
			} else if (jumpDown) {
				jumpCur -= jumpSpd;
				if(jumpCur <= 0) {
					isJumping = false;
					jumpDown = false;
					jumpUp = false;
				}
			}
			z = jumpCur;
			if(jumpCur >= jumpFrames) {
				jumpUp = false;
				jumpDown = true;
			}
		}
		*/
		
		if(right && World.isFree((int)(x + speed), this.getY())) {
			moved = true;
			x += speed;
		} else if (left && World.isFree((int)(x - speed), this.getY())) {
			moved = true;
			x -= speed;
		} else if (up && World.isFree(this.getX(),(int)(y - speed))) {
			moved = true;
			y -= speed;
		} else if (down && World.isFree(this.getX(),(int)(y + speed))) {
			moved = true;
			y += speed;
		}
		
		if(moved) {
			animar();
		}
		
		checkCollisionLifePack();
		checkCollisionAmmo();
		checkCollisionWeapon();
		
		if(isDamaged) {
			this.damageFrames++;
			if(this.damageFrames == 4) {
				this.damageFrames = 0;
				isDamaged = false;
			}
		}
		
		if(shoot) {
			Sound.weaponShoot.play();
			shoot = false;
			if(hasWeapon && (ammo > 0)) {
				ammo--;
				int dx = 0, dy = 0, posX = 0, posY = 0;
				if(ultimaOcorrencia == 1) {
					dx = 1;
					posY = 7;
					posX = 15;
				} else if (ultimaOcorrencia == 2) {
					dx = -1;
					posY = 7;
					posX = -3;
				} else if(ultimaOcorrencia == 3) {
					dy = -1;
					posX = 11;
					posY = -2;
				} else if (ultimaOcorrencia == 4 || ultimaOcorrencia == 0){
					dy = 1;
					posX = 9;
					posY = 15;
				}
				
				WeaponShoot shoot = new WeaponShoot(this.getX() + posX, this.getY() + posY, 4,4, null, dx, dy);
				Game.weaponShoots.add(shoot);
			}
			
		}
		
		if(mouseShoot) {
			Sound.weaponShoot.play();
			mouseShoot = false;

			if(hasWeapon && (ammo > 0)) {
				ammo--;
				int posX = 0, posY = 0;
				double angle = Math.atan2(my - (this.getY() + 8 - Camera.getY()), mx - (this.getX() + 8 - Camera.getX()));

				double dx = Math.cos(angle);
				double dy = Math.sin(angle);
				
				if(ultimaOcorrencia == 1) {
					posY = 7;
					posX = 15;
					
					if(dx < 0) {
						dx *= -1;
					}
				} else if (ultimaOcorrencia == 2) {
					posY = 7;
					posX = -3;
					
					if(dx >= 0) {
						dx *= -1;
					}
				} else if(ultimaOcorrencia == 3) {
					posX = 11;
					posY = -2;
					
					if(dy >= 0) {
						dy *= -1;
					}
				} else if (ultimaOcorrencia == 4 || ultimaOcorrencia == 0){
					posX = 9;
					posY = 15;
					
					if(dy < 0) {
						dy *= -1;
					}
				}

				WeaponShoot shoot = new WeaponShoot(this.getX() + posX, this.getY() + posY, 4, 4, null, dx, dy);
				Game.weaponShoots.add(shoot);
			}
			
		}
		
		if(this.life <= 0) {
			Sound.deathPlayer.play();
			this.life = 0;
			Game.gameState = "GAME_OVER";
		}
		
		updateCamera();
	}
	
	public void updateCamera() {
		Camera.setX(Camera.clamp(this.getX() - (Game.WIDTH/2), 0, World.WIDTH*16 - Game.WIDTH));
		Camera.setY(Camera.clamp(this.getY() - (Game.HEIGHT/2), 0, World.HEIGHT*16 - Game.HEIGHT));
	}
	
	private void checkCollisionWeapon() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			if(e instanceof Weapon) {
				if(Entity.isColliding(this, e)) {
					Sound.pickUpWeapon.play();
					hasWeapon = true;
					ammo += 6;
					Game.entities.remove(i);
					return;
				}
			}
		}
	}
	
	private void checkCollisionAmmo() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			if(e instanceof AmmoPack) {
				if(Entity.isColliding(this, e)) {
					Sound.pickUpAmmo.play();
					ammo+=9;
					Game.entities.remove(i);
					return;
				}
			}
		}
	}
	
	private void checkCollisionLifePack() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			if(e instanceof LifePack) {
				if(Entity.isColliding(this, e)) {
					Sound.pickUpLife.play();
					life+=10;
					if(life>100) {
						life = 100;
					}
					Game.entities.remove(i);
					return;
				}
			}
		}
	}

}
