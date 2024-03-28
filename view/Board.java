/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

/**
 *
 * @author user
 */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;
import javax.swing.JPanel;
import labyrinth.Game;
import labyrinth.LevelItem;
import labyrinth.Position;
import res.ResourceLoader;

public class Board extends JPanel {
    private final Game game;
    private final Image dragon, player, wall, empty, destination;
    private double scale;
    private int scaled_size;
    private final int tile_size = 48;
    
    public Board(Game g) throws IOException{
        game = g;
        scale = 1.0;
        scaled_size = (int)(scale * tile_size);
        player = ResourceLoader.loadImage("res/player.png");
        wall = ResourceLoader.loadImage("res/wall.png");
        empty = ResourceLoader.loadImage("res/empty.png");
        dragon = ResourceLoader.loadImage("res/dragon.png");
        destination = ResourceLoader.loadImage("res/destination.png");
    }
    
    public boolean setScale(double scale){
        this.scale = scale;
        scaled_size = (int)(scale * tile_size);
        return refresh();
    }
    
    public boolean refresh(){
        Dimension dim = new Dimension(game.getLevelCols() * scaled_size, game.getLevelRows() * scaled_size);
        setPreferredSize(dim);
        setMaximumSize(dim);
        setSize(dim);
        repaint();
        return true;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Color semiTransparentBlack = new Color(0, 0, 0);
        if (!game.isLevelLoaded()) return;
        Graphics2D gr = (Graphics2D) g;
        int w = game.getLevelCols();
        int h = game.getLevelRows();
        Position p = game.getPlayerPos();
        Position d = game.getDragonPos();

        boolean[][] visible = game.getDark().getVisible();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Image img = null;

                if (!visible[y][x]) {
                    gr.setColor(semiTransparentBlack);
                    gr.fillRect(x * scaled_size, y * scaled_size, scaled_size, scaled_size);
                } else {
                    LevelItem li = game.getItem(y, x);
                    switch (li) {
                        case DESTINATION: img = destination; break;
                        case WALL: img = wall; break;
                        case EMPTY: img = empty; break;
                    }
                    if (p.x == x && p.y == y) img = player;
                    if (d.x == x && d.y == y) img = dragon;
                }
                if (img == null) continue;
                gr.drawImage(img, x * scaled_size, y * scaled_size, scaled_size, scaled_size, null);
            }
        }
    }
}

