/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package labyrinth;

/**
 *
 * @author user
 */
public enum LevelItem {
    DESTINATION('*'), WALL('x'), DRAGON('&'), EMPTY(' ');
    LevelItem(char rep){ representation = rep; }
    public final char representation;
}
