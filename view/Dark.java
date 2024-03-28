/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import labyrinth.Game;
import labyrinth.Position;

/**
 *
 * @author user
 */

public class Dark {
    private final Game game;
    private final boolean[][] visible;
    
    public Dark(Game game) {
        this.game = game;
        visible = new boolean[game.getLevelRows()][game.getLevelCols()];
        updateVisibility(game.getPlayerPos());
    }
    
    public final void updateVisibility(Position playerPos) {
        int playerRow = playerPos.y;
        int playerCol = playerPos.x;

        for (int row = 0; row < game.getLevelRows(); row++) {
            for (int col = 0; col < game.getLevelCols(); col++) {
                int distance = Math.abs(playerRow - row) + Math.abs(playerCol - col);
                visible[row][col] = (distance < 3);
            }
        }
    } 
    
    public boolean[][] getVisible(){
        return visible;
    }
    
    public boolean isVisible(int row, int col) {
        return visible[row][col];
    }
}

