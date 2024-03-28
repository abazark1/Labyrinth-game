/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistence;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author user
 */
public class HighScoreTableModel extends AbstractTableModel {
    private final ArrayList<HighScore> highScores;
    private final String[] colName  = new String[]{"Name", "Difficulty", "Finished levels"};
    
    public HighScoreTableModel(ArrayList<HighScore> highScores){
        this.highScores = highScores;
    }
    
    @Override
    public int getRowCount(){
        return highScores.size();
    }
    
    @Override
    public int getColumnCount(){
        return 3;
    }
    
    @Override
    public Object getValueAt(int r, int c){
        HighScore h = highScores.get(r);
        if (c == 0){
            return h.name;
        } else if (c == 1){
            return h.difficulty;
        }
        return h.steps;
    }
    
    @Override
    public String getColumnName(int i){
        return colName[i];
    }
}
