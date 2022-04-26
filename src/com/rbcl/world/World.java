package com.rbcl.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.rbcl.entities.*;
import com.rbcl.graficos.Spritesheet;
import com.rbcl.main.Game;

public class World {
	
	public static Tile[] tiles;
	public static int WIDTH, HEIGHT;
	public static final int TILE_SIZE = 16;
	public static List<WallTile> walls;
	
	public World(String path) {
		/*Game.player.setX(0*TILE_SIZE);
		Game.player.setY(0*TILE_SIZE);
		
		WIDTH = 100;
		HEIGHT = 100;
		
		tiles = new Tile[(WIDTH)*(HEIGHT)];
		
		
		for(int xx = 0; xx < WIDTH; xx++) {
			for(int yy = 0; yy < HEIGHT; yy++) {
				tiles[xx + yy*WIDTH] = new WallTile(Tile.TILE_WALL, xx*16, yy*16);
			}
			
		}
		
		int dir = 0;
		int xx = 0;
		int yy = 0;
		
		for(int i = 0; i < 1000; i++) {
			tiles[xx + yy*WIDTH] = new FloorTile(Tile.TILE_FLOOR, xx*16, yy*16);
			
			if(dir == 0) {
				//direita
				if(xx < WIDTH) {
					xx++;
				}
			} else if(dir == 1) {
				//esquerda
				if(xx > 0) {
					xx--;
				}
			} else if(dir == 2) {
				//baixo
				if(yy < HEIGHT) {
					yy++;
				}
			} else if(dir == 3) {
				//cima
				if(yy > 0) {
					yy--;
				}
			}
			
			if(Game.rand.nextInt(100) < 30) {
				dir = Game.rand.nextInt(4);
			}
			
		}*/ //mapa randômico
		
		try {
			BufferedImage map = ImageIO.read(getClass().getResource(path));
			int[] pixels = new int[map.getWidth() * map.getHeight()];
			WIDTH = map.getWidth();
			HEIGHT = map.getHeight();
			tiles = new Tile[map.getWidth() * map.getHeight()];
			walls = new ArrayList<WallTile>();
			
			map.getRGB(0, 0, map.getWidth(), map.getHeight(), pixels, 0, map.getWidth());
			
			for(int xx = 0; xx < map.getWidth(); xx++) {
				for(int yy = 0; yy < map.getHeight(); yy++) {
					int pixelAtual = pixels[xx + (yy*map.getWidth())];
					
					//	AZUL (JOGADOR) = 0xFF0026FF
					//	PRETO (CHÃO) = 0xFF000000
					//	BRANCO (PAREDE) = 0xFFFFFFFF
					//	VERMELHO (INIMIGO) = 0xFFFF0000
					//	AMARELO (MUNIÇÃO) = 0xFFFFD800
					//	ROSA (VIDA) = 0xFFFF7F7F
					//	ROXO (ARMA) = 0xFFB200FF
					//
					
					tiles[xx + (yy * WIDTH)] = new FloorTile(Tile.TILE_FLOOR, xx*TILE_SIZE, yy*TILE_SIZE);
					
					if (pixelAtual == 0xFF0026FF) {
						Game.player.setX(xx*TILE_SIZE);
						Game.player.setY(yy*TILE_SIZE);
					} else if (pixelAtual == 0xFFFFFFFF) {
						WallTile wall = new WallTile(Tile.TILE_WALL, xx*TILE_SIZE, yy*TILE_SIZE);
						tiles[xx + (yy * WIDTH)] = wall;
						walls.add(wall);
					} else if (pixelAtual == 0xFFB200FF) {
						Game.entities.add(new Weapon(xx*TILE_SIZE, yy*TILE_SIZE, TILE_SIZE, TILE_SIZE, Entity.WEAPON_EN));
					}else if (pixelAtual == 0xFFFFD800) {
						Game.entities.add(new AmmoPack(xx*TILE_SIZE, yy*TILE_SIZE, TILE_SIZE, TILE_SIZE, Entity.AMMOPACK_EN));
					}else if (pixelAtual == 0xFFFF7F7F) {
						LifePack pack = new LifePack(xx*TILE_SIZE, yy*TILE_SIZE, TILE_SIZE, TILE_SIZE, Entity.LIFEPACK_EN);
						pack.setMask(4,4,8,7);
						Game.entities.add(pack);
					}else if (pixelAtual == 0xFFFF0000) {
						Enemy en = new Enemy(xx*TILE_SIZE, yy*TILE_SIZE, TILE_SIZE, TILE_SIZE, Entity.ENEMY_EN);
						Game.entities.add(en);
						Game.enemies.add(en);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} //mapa manual
	}
	
	public static boolean isFree(int xNext, int yNext) {
		int x1 = xNext/TILE_SIZE;
		int y1 = yNext/TILE_SIZE;
		
		int x2 = (xNext + TILE_SIZE - 1)/TILE_SIZE;
		int y2 = yNext/TILE_SIZE;
		
		int x3 = xNext/TILE_SIZE;
		int y3 = (yNext  + TILE_SIZE - 1)/TILE_SIZE;
		
		int x4 = (xNext + TILE_SIZE - 1)/TILE_SIZE;
		int y4 = (yNext + TILE_SIZE - 1)/TILE_SIZE;
		
		if (!((tiles[x1 + (y1*World.WIDTH)] instanceof WallTile) || 
				(tiles[x2 + (y2*World.WIDTH)] instanceof WallTile) || 
				(tiles[x3 + (y3*World.WIDTH)] instanceof WallTile) || 
				(tiles[x4 + (y4*World.WIDTH)] instanceof WallTile))) {
			return true;
		}
		
		/*
		if(Game.player.z>0) {
			return true;
		}
		*/
		
		return false;
	}
	
	public static void restartGame(String level) {
		Game.entities.clear();
		Game.enemies.clear();
		Game.weaponShoots.clear();
		Game.entities = new ArrayList<Entity>();
		Game.enemies = new ArrayList<Enemy>();
		Game.weaponShoots = new ArrayList<WeaponShoot>();
		Game.spritesheet = new Spritesheet("/spritesheet.png");
		Game.player = new Player(120-16, 80-16, 16, 16, Game.spritesheet.getSprite(32, 0, 16, 16));
		Game.entities.add(Game.player);
		Game.world = new World("/" + level);
		Game.minimapa = new BufferedImage(World.WIDTH, World.HEIGHT, BufferedImage.TYPE_INT_RGB);
		Game.minimapaPixels = ((DataBufferInt)Game.minimapa.getRaster().getDataBuffer()).getData();
		return;
	}
	
	public void render(Graphics g) {
		int xStart = Camera.getX() >> 4;
		int yStart = Camera.getY() >> 4;
		
		int xFinal = xStart + (Game.WIDTH >> 4);
		int yFinal = yStart + (Game.HEIGHT >> 4);
		
		for(int xx = xStart; xx <= xFinal; xx++) {
			for(int yy = yStart; yy <= yFinal; yy++) {
				if(xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT) {
					continue;
				}
				
				Tile tile = tiles[xx + (yy*WIDTH)];
				tile.render(g);
			}
		}
	}
	
	public static void renderMiniMapa() {
		for(int i = 0; i < Game.minimapaPixels.length; i++) {
			Game.minimapaPixels[i] = 0x000000;
		}
		
		for(int xx = 0; xx < WIDTH; xx++) {
			for(int yy = 0; yy < HEIGHT; yy++) {
				if(tiles[xx + (yy*WIDTH)] instanceof WallTile) {
					Game.minimapaPixels[xx + (yy*WIDTH)] = 0x0000ff;
				}
			}
		}
		
		int xPlayer = Game.player.getX()/16;
		int yPlayer = Game.player.getY()/16;
		Game.minimapaPixels[xPlayer + (yPlayer*WIDTH)] = 0x87ceeb;
		
		for(int i = 0; i < Game.enemies.size(); i++) {
			int xEnemie = Game.enemies.get(i).getX()/16;
			int yEnemie = Game.enemies.get(i).getY()/16;
			
			Game.minimapaPixels[xEnemie + (yEnemie*WIDTH)] = 0xff0000;
		}
	}
	
	public Tile[] getTiles() {
		return tiles;
	}

}
