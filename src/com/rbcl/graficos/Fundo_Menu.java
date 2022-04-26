package com.rbcl.graficos;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Fundo_Menu {
	
	public final int frames = 2;
		
	public BufferedImage[] fundo = new BufferedImage[frames];
		
	public Fundo_Menu(String[] path) {
		try {
			for(int i = 0; i < fundo.length; i++) {
				fundo[i] = ImageIO.read(getClass().getResource(path[i]));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
}
