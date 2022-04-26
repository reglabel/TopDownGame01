package com.rbcl.graficos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.rbcl.main.Game;

public class UI {
	public void render(Graphics g) {
		g.setColor(new Color(150, 0, 0));
		g.fillRect(8, 5, 60, 10);
		g.setColor(new Color(0, 150, 0));
		g.fillRect(8, 5, (int)((Game.player.life/Game.player.maxLife)*60), 10);
		g.setColor(Color.WHITE);
		g.setFont(new Font("arial", Font.BOLD, 10));
		g.drawString((int)Game.player.life+"/"+(int)Game.player.maxLife, 9, 13);
	}
}
