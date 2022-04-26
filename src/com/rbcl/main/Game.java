package com.rbcl.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
//import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
//import java.io.IOException;
//import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.rbcl.entities.Enemy;
import com.rbcl.entities.Entity;
import com.rbcl.entities.Player;
import com.rbcl.entities.WeaponShoot;
import com.rbcl.graficos.Spritesheet;
import com.rbcl.graficos.UI;
import com.rbcl.world.World;


public class Game extends Canvas implements Runnable, KeyListener, MouseListener, MouseMotionListener {
	
	private static final long serialVersionUID = 1L;
	
	private Thread thread;
	private static JFrame frame;
	
	private BufferedImage image;
	
	public static int WIDTH = 240;
	public static int HEIGHT = 160;
	public static final int SCALE = 3;
	
	public static List<Entity> entities;
	public static List<Enemy> enemies;
	public static List<WeaponShoot> weaponShoots;
	public static Spritesheet spritesheet;
	
	public static World world; 
	public static Player player;
	private UI ui;
	private Menu menu;
	
	public static Random rand;
	
	public static String gameState = "MENU";

	private boolean isRunning, showMessageGameOver = true, restartGame = false;
	private int CUR_LEVEL = 1, MAX_LEVEL = 2;
	
	private int framesGameOver = 0, fps = 0;
	
	public boolean saveGame = false;
	
	public BufferedImage lightmap;
	public int[] lightMapPixels;
	
	public static BufferedImage minimapa;
	public static int[] minimapaPixels;
	
	public int mx, my;
	
	//public InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("pixelfont.ttf");
	//public Font newfont;
	
	//public int[] pixels;
	//public int xx, yy;
	
	public static void main(String[] args) {
		Game game = new Game();
		game.start();
	}
	
	public Game() {
		Sound.music.loop();
		rand = new Random();
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		//this.setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE)); //tamanho janela
		setPreferredSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize())); //tamanho tela cheia
		this.initFrame();
		
		ui = new UI();
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		try {
			lightmap = ImageIO.read(getClass().getResource("/lightmap.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		lightMapPixels = new int[lightmap.getWidth() * lightmap.getHeight()];
		lightmap.getRGB(0, 0, lightmap.getWidth(), lightmap.getHeight(), lightMapPixels, 0, lightmap.getWidth());
		//pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		weaponShoots = new ArrayList<WeaponShoot>();
		
		spritesheet = new Spritesheet("/spritesheet.png");
		player = new Player(120-16, 80-16, 16, 16, spritesheet.getSprite(32, 0, 16, 16));
		entities.add(player);
		world = new World("/level1.png");
		
		minimapa = new BufferedImage(World.WIDTH, World.HEIGHT, BufferedImage.TYPE_INT_RGB);
		minimapaPixels = ((DataBufferInt)minimapa.getRaster().getDataBuffer()).getData();
		
		//String[] fundos = {"/fundo_1.png", "/fundo_2.png"};
		//menu = new Menu(fundos);
		menu = new Menu();
		
		/*
		try {
			newfont = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(70f);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
	}
	
	public void initFrame() {
		frame = new JFrame("will you play along?");
		frame.add(this);
		frame.setUndecorated(true); //tira botoes de maximizar e fechar
		frame.setResizable(false);
		frame.pack();
		//Icone da Janela
		Image imagem = null;
		try {
			imagem = ImageIO.read(getClass().getResource("/icon.png"));
		}catch (IOException e) {
			e.printStackTrace();
		}
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image image = toolkit.getImage(getClass().getResource("/mira.png"));
		Cursor c = toolkit.createCustomCursor(image, new Point(0,0), "img");
		frame.setCursor(c);
		frame.setIconImage(imagem);
		//frame.setAlwaysOnTop(true);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}
	
	public synchronized void stop() {
		isRunning = false;
		
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void tick() {
		if(gameState.equals("NORMAL")) {
			//xx++;
			//yy++;
			if(this.saveGame) {
				this.saveGame = false;
				String[] opt1 = {"level"/*, "life"*/};
				int[] opt2 = {this.CUR_LEVEL/*, (int) player.life*/};
				Menu.saveGame(opt1,opt2,21);
				System.out.println("Jogo salvo...");
			}
			
			frame.setTitle("here we go!");
			this.restartGame = false;
			
			for(int i = 0; i < entities.size(); i++) {
				Entity e = entities.get(i);
				e.tick();
			}
			
			for(int i = 0; i < weaponShoots.size(); i++) {
				weaponShoots.get(i).tick();
			}
			
			if(enemies.size() == 0) {
				CUR_LEVEL++;
				if(CUR_LEVEL > MAX_LEVEL) {
					CUR_LEVEL = 1;
				}
				
				String newWorld = "level" + CUR_LEVEL + ".png";
				World.restartGame(newWorld);
			}
		} else if (gameState.equals("GAME_OVER")){
			frame.setTitle("it makes want to cry, buddy ;-;");

			if(player.life < 0) {
				player.life = 0;
			}
			
			framesGameOver++;
			
			if(framesGameOver == 40) {
				framesGameOver = 0;
				
				if(showMessageGameOver) {
					showMessageGameOver = false;
				} else {
					showMessageGameOver = true;
				}
			}
			
			if(restartGame) {
				this.restartGame = false;
				gameState = "NORMAL";
				CUR_LEVEL = 1;
				String newWorld = "level" + CUR_LEVEL + ".png";
				World.restartGame(newWorld);
			}
		} else if (gameState.equals("MENU")) {
			if(Menu.pause) {
				frame.setTitle("...already tired?!");
			}
			menu.tick();
		}
			
	}
	/*
	public void drawRectangleExample(int xoff, int yoff) {
		for(int xx = 0; xx < 32; xx++) {
			for(int yy = 0; yy < 32; yy++) {
				int xOff = xx + xoff;
				int yOff = yy + yoff;
				
				if(xOff < 0 || yOff < 0 || xOff >= WIDTH || yOff >= HEIGHT) {
					continue;
				}
				
				pixels[xOff + (yOff * WIDTH)] = 0xff0000;
			}
		}
	}
	*/
	
	/*
	public void applyLight() {
		for(int xx=0; xx < Game.WIDTH; xx++) {
			for(int yy=0; yy < Game.HEIGHT; yy++) {
				if(lightMapPixels[xx+(yy*Game.WIDTH)] == 0xffffffff) {
					pixels[xx+(yy*Game.WIDTH)] = 0;
				}
			}
		}
	}
	*/
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g = image.createGraphics();
		g.setColor(new Color(0,0,0));
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		world.render(g);
		
		Collections.sort(entities, Entity.nodeSorter);
		
		for(int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			e.render(g);
		}
		
		for(int i = 0; i < weaponShoots.size(); i++) {
			weaponShoots.get(i).render(g);
		}
		
		//applyLight();
		ui.render(g);
		
		g.dispose(); // para otimização
		g = bs.getDrawGraphics();
		
		//drawRectangleExample(xx, yy);

		//g.drawImage(image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null); //tamanho janela
		g.drawImage(image, 0, 0, Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height, null); //tamanho tela cheia
		g.setFont(new Font("arial", Font.BOLD, 25));
		g.setColor(Color.WHITE);
		g.drawString("Ammo: " + player.ammo, (int)(0.79*Toolkit.getDefaultToolkit().getScreenSize().width), (int)(0.07*Toolkit.getDefaultToolkit().getScreenSize().height));
		
		g.setFont(new Font("arial", Font.BOLD, 18));
		g.setColor(Color.WHITE);
		g.drawString("FPS: " + fps, (int)(0.79*Toolkit.getDefaultToolkit().getScreenSize().width) + 2, (int)(0.07*Toolkit.getDefaultToolkit().getScreenSize().height) + 30);

		if(gameState.equals("GAME_OVER")) {
			Graphics2D g2 = (Graphics2D) g;
			
			g2.setColor(new Color(0,0,0,150));
			g2.fillRect(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height);
			g2.setFont(new Font("arial", Font.BOLD, 35));
			g2.setColor(Color.WHITE);
			g2.drawString("Oh NO! Guess what?", Toolkit.getDefaultToolkit().getScreenSize().width / 2 - 170, Toolkit.getDefaultToolkit().getScreenSize().height / 2 - 40);
			g2.setColor(Color.RED);
			g2.drawString("You DIED!", Toolkit.getDefaultToolkit().getScreenSize().width / 2 - 90, Toolkit.getDefaultToolkit().getScreenSize().height / 2 + 50 - 40);
			g2.setFont(new Font("arial", Font.BOLD, 20));
			g2.setColor(Color.WHITE);
			
			if(showMessageGameOver) {
				g2.drawString("-= press ENTER to try again =-", Toolkit.getDefaultToolkit().getScreenSize().width / 2 - 145, Toolkit.getDefaultToolkit().getScreenSize().height / 2 - 40 + 100);
			}
		} else if(gameState.equals("MENU")) {
			player.updateCamera();
			menu.render(g);
		} //menu tela cheia
		
		/*g.setFont(new Font("arial", Font.BOLD, 25));
		g.setColor(Color.WHITE);
		g.drawString("Ammo: " + player.ammo, 575, 35);
		
		g.setFont(new Font("arial", Font.BOLD, 18));
		g.setColor(Color.WHITE);
		g.drawString("FPS: " + fps, 575 + 2, 35 + 30);

		if(gameState.equals("GAME_OVER")) {
			Graphics2D g2 = (Graphics2D) g;
			
			g2.setColor(new Color(0,0,0,150));
			g2.fillRect(0, 0, WIDTH*SCALE, HEIGHT*SCALE);
			g2.setFont(new Font("arial", Font.BOLD, 35));
			g2.setColor(Color.WHITE);
			g2.drawString("Oh NO! Guess what?", (WIDTH*SCALE) / 2 - 170, (HEIGHT*SCALE) / 2 - 40);
			g2.setColor(Color.RED);
			g2.drawString("You DIED!", (WIDTH*SCALE) / 2 - 90, (HEIGHT*SCALE) / 2 + 50 - 40);
			g2.setFont(new Font("arial", Font.BOLD, 20));
			g2.setColor(Color.WHITE);
			
			if(showMessageGameOver) {
				g2.drawString("-= press ENTER to try again =-", (WIDTH*SCALE) / 2 - 145, (HEIGHT*SCALE) / 2 - 40 + 100);
			}
		} else if(gameState.equals("MENU")) {
			player.updateCamera();
			menu.render(g);
		}*/ //menu com tamanho janela
		
		/*
		g.setFont(newfont);
		g.setColor(Color.RED);
		g.drawString("teste com nova font2", 90,90);
		*/
		
		/*
		Graphics2D g2 = (Graphics2D) g;
		double radianMouse = Math.atan2(my - (200+25), mx - (200+25));
		g2.rotate(radianMouse, 200+25, 200+25);
		g.setColor(Color.RED);
		g.fillRect(200,200,50,50);
		*/
		
		World.renderMiniMapa();
		//g.drawImage(minimapa, Game.WIDTH*SCALE-(World.WIDTH*5)-20, Game.HEIGHT*SCALE-(World.HEIGHT*5)-20, World.WIDTH*5, World.HEIGHT*5, null);
		g.drawImage(minimapa, Toolkit.getDefaultToolkit().getScreenSize().width-(120), Toolkit.getDefaultToolkit().getScreenSize().height-(120), 100, 100, null); //modo tela cheia
		//g.drawImage(minimapa, Game.WIDTH*SCALE-(120), Game.HEIGHT*SCALE-(120), 100, 100, null); //modo janela
		bs.show();
	}

	@Override
	public void run() {
		requestFocus();
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		double timer = System.currentTimeMillis();
		int frames = 0;
		
		while(isRunning) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			
			if(delta >= 1) {
				tick();
				render();
				frames++;
				delta--;
			}
			
			if(System.currentTimeMillis() - timer >= 1000) {
				fps = frames;
				//System.out.println(fps);
				frames = 0;
				timer += 1000;
			}
		}
		
		stop();
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if((e.getKeyCode() == KeyEvent.VK_RIGHT) 
				|| (e.getKeyCode() == KeyEvent.VK_D)) {
			player.right = true;
		} else if((e.getKeyCode() == KeyEvent.VK_LEFT) 
				|| (e.getKeyCode() == KeyEvent.VK_A)) {
			player.left = true;
		}
		
		if((e.getKeyCode() == KeyEvent.VK_UP) 
				|| (e.getKeyCode() == KeyEvent.VK_W)) {
			player.up = true;
			
			if(gameState.equals("MENU")) {
				menu.up = true;
			}
		} else if((e.getKeyCode() == KeyEvent.VK_DOWN) 
				|| (e.getKeyCode() == KeyEvent.VK_S)) {
			player.down = true;
			
			if(gameState.equals("MENU")) {
				menu.down = true;
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			if(player.hasWeapon) {
				player.shoot = true;
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			this.restartGame = true;
			
			if(Game.gameState.equals("MENU")) {
				menu.enter = true;
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_P) {
			gameState = "MENU";
			Menu.pause = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_S) {
			if(gameState.equals("NORMAL")) {
				this.saveGame = true;
			}
		}
		
		/*
		if(e.getKeyCode() == KeyEvent.VK_Z) {
			player.jump = true;
		}
		*/
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if((e.getKeyCode() == KeyEvent.VK_RIGHT) 
				|| (e.getKeyCode() == KeyEvent.VK_D)) {
			player.right = false;
		} else if((e.getKeyCode() == KeyEvent.VK_LEFT) 
				|| (e.getKeyCode() == KeyEvent.VK_A)) {
			player.left = false;
		}
		
		if((e.getKeyCode() == KeyEvent.VK_UP) 
				|| (e.getKeyCode() == KeyEvent.VK_W)) {
			player.up = false;
		} else if((e.getKeyCode() == KeyEvent.VK_DOWN) 
				|| (e.getKeyCode() == KeyEvent.VK_S)) {
			player.down = false;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Game.player.mouseShoot = true;
		player.mx = (e.getX()/3);
		player.my = (e.getY()/3);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		this.mx = e.getX();
		this.my= e.getY();
	}
}

