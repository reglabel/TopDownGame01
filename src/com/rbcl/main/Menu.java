package com.rbcl.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.rbcl.world.World;

//import com.rbcl.graficos.Fundo_Menu;

public class Menu {
	
	private String[] options = {"New Game", "Load Game", "Exit"};
	private String[] ocultOptions = {"Continue"};
	private final static String FILE_SAVE_NAME = "save.txt";
	public int currentOption = 0;
	public int maxOption = options.length - 1;
	//private static Fundo_Menu fundoMenu;
	
	public boolean up, down, enter;
	public static boolean pause = false, saveExists = false, saveGame = false;
	
	//private int frames = 0, maxFrames = 80, index = 0, maxIndex;
	
	/*
	public Menu(String[] fundos) {
		fundoMenu = new Fundo_Menu(fundos);
		this.maxIndex = fundoMenu.frames - 1;
	}
	*/
	
	public void tick() {
		File file = new File(FILE_SAVE_NAME);
		if(file.exists()) {
			saveExists = true;
		} else {
			saveExists = false;
		}
		
		if(up) {
			Sound.select.play();
			up = false;
			currentOption--;
			if(currentOption < 0) {
				currentOption = maxOption;
			}
		}
		
		if(down) {
			Sound.select.play();			
			down = false;
			currentOption++;
			if(currentOption > maxOption) {
				currentOption = 0;
			}
		}
		
		if(enter) {
			//Sound.music.loop();
			enter = false;
			
			if(options[currentOption].equals(options[0]) || options[currentOption].equals(ocultOptions[0])) {
				Game.gameState = "NORMAL";
				pause = false;
				file = new File(FILE_SAVE_NAME);
				file.delete();
			} else if (options[currentOption].equals(options[1])) {
				file = new File(FILE_SAVE_NAME);
				if(file.exists()) {
					String saver = loadGame(21);
					applySave(saver);
				}
			} else if (options[currentOption].equals(options[2])) {
				System.exit(1);
			}
			
		}
		/*
		if(!pause) {
			frames++;
			if(frames == maxFrames) {
				frames = 0;
				index++;
				if(index > maxIndex) {
					index = 0;
				}
			}
		}
		*/
		
	}
	
	public static void applySave(String str) {
		String[] spl = str.split("/");
		for(int i = 0; i< spl.length; i++) {
			String[] spl2 = spl[i].split(":");
			
			switch(spl2[0]) {
				case "level":
					World.restartGame("level"+spl2[1]+".png");
					Game.gameState = "NORMAL";
					pause = false;
					break;
				/*	
				case "life":
					Game.player.life = Integer.parseInt(spl2[1]);
					break;
				*/
			}
		}
	}
	
	public static String loadGame(int encode) {
		String line = "";
		File file = new File(FILE_SAVE_NAME);
		
		if(file.exists()) {
			try {
				String singleLine = null;
				BufferedReader reader = new BufferedReader(new FileReader(FILE_SAVE_NAME));
				
				try {
					while((singleLine = reader.readLine()) != null) {
						String[] trans = singleLine.split(":");
						char[] val = trans[1].toCharArray();
						trans[1] = "";
						
						for(int i = 0; i < val.length; i++) {
							val[i] -= encode;
							trans[1] += val[i];
						}
						line+=trans[0];
						line+=":";
						line+=trans[1];
						line+="/";
					}
				}catch(IOException e){}
				
			} catch(FileNotFoundException e) {}
		}
		
		return line;
	}
	
	public static void saveGame(String[] val1, int[] val2, int encode) {
		BufferedWriter write = null;
		try {
			write = new BufferedWriter(new FileWriter(FILE_SAVE_NAME));
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		for(int i = 0; i < val1.length; i++) {
			String current = val1[i];
			current+=":";
			char[] value = Integer.toString(val2[i]).toCharArray();
			
			for(int j = 0; j < value.length; j++) {
				value[j]+=encode;
				current+=value[j];
			}
			
			try{
				write.write(current);
				if(i < val1.length - 1) {
					write.newLine();
				}
			} catch(IOException e) {}
		}
		
		try {
			write.flush();
			write.close();
		} catch(IOException e){}
	}
	
	public void render(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		/*
		if(!pause) {
			g.drawImage(fundoMenu.fundo[index], 0, 0, Game.WIDTH*Game.SCALE, Game.HEIGHT*Game.SCALE, null);
			g2.setColor(new Color(0,0,0,120));
		} else {
			g2.setColor(new Color(0,0,0,200));
		}
		*/
		
		g2.setColor(new Color(0,0,0,120));
		
		if(Game.modeGame.equals("FULLSCREEN")) {
			g2.fillRect(10, 10, Toolkit.getDefaultToolkit().getScreenSize().width - 20, Toolkit.getDefaultToolkit().getScreenSize().height - 20);
			
			g.setColor(Color.YELLOW);
			g.setFont(new Font("arial", Font.BOLD, 90));
			g.drawString("-= yUP =-", (Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - 200, (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - 70);
			
			g.setColor(Color.WHITE);
			
			g.setFont(new Font("arial", Font.BOLD, 36));
			if(!pause) {
				g.drawString(options[0], (int)(0.13*Toolkit.getDefaultToolkit().getScreenSize().width), (int)(0.625*Toolkit.getDefaultToolkit().getScreenSize().height));
			} else {
				g.drawString(ocultOptions[0], (int)(0.13*Toolkit.getDefaultToolkit().getScreenSize().width), (int)(0.625*Toolkit.getDefaultToolkit().getScreenSize().height));
			}
			g.drawString(options[1], (int)(0.13*Toolkit.getDefaultToolkit().getScreenSize().width), (int)(0.73*Toolkit.getDefaultToolkit().getScreenSize().height));
			g.drawString(options[2], (int)(0.13*Toolkit.getDefaultToolkit().getScreenSize().width), (int)(0.833*Toolkit.getDefaultToolkit().getScreenSize().height));
			
			g.setColor(Color.YELLOW);
			if(options[currentOption].equals(options[0])) {
				if(!pause) {
					g.drawString(">", (int)(0.06*Toolkit.getDefaultToolkit().getScreenSize().width), (int)(0.625*Toolkit.getDefaultToolkit().getScreenSize().height));
				} else {
					g.drawString(">", (int)(0.06*Toolkit.getDefaultToolkit().getScreenSize().width), (int)(0.625*Toolkit.getDefaultToolkit().getScreenSize().height));
				}
			} else if (options[currentOption].equals(options[1])) {
				g.drawString(">", (int)(0.06*Toolkit.getDefaultToolkit().getScreenSize().width), (int)(0.73*Toolkit.getDefaultToolkit().getScreenSize().height));
			} else if (options[currentOption].equals(options[2])) {
				g.drawString(">", (int)(0.06*Toolkit.getDefaultToolkit().getScreenSize().width), (int)(0.833*Toolkit.getDefaultToolkit().getScreenSize().height));
			}
		} else if(Game.modeGame.equals("WINDOW")) {
			g2.fillRect(10, 10, Game.WIDTH*Game.SCALE - 20, Game.HEIGHT*Game.SCALE - 20);
			
			g.setColor(Color.YELLOW);
			g.setFont(new Font("arial", Font.BOLD, 90));
			g.drawString("-= yUP =-", (Game.WIDTH*Game.SCALE) / 2 - 200, (Game.HEIGHT*Game.SCALE) / 2 - 70);
			
			g.setColor(Color.WHITE);
			
			g.setFont(new Font("arial", Font.BOLD, 36));
			if(!pause) {
				g.drawString(options[0], 280 - 180, 300);
			} else {
				g.drawString(ocultOptions[0], 230 - 120, 300);
			}
			g.drawString(options[1], 220 - 8 - 120, 350);
			g.drawString(options[2], 283 - 7 - 120, 400);
			
			g.setColor(Color.YELLOW);
			if(options[currentOption].equals(options[0])) {
				if(!pause) {
					g.drawString(">", 280 - 50 - 180, 300);
				} else {
					g.drawString(">", 230 - 50 - 120, 300);
				}
			} else if (options[currentOption].equals(options[1])) {
				g.drawString(">", 220 - 8 - 50 - 120, 350);
			} else if (options[currentOption].equals(options[2])) {
				g.drawString(">", 283 - 7 - 50 - 120, 400);
			}
		}
	}
}
