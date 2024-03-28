/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistence;

//import labyrinth.GameID;

import java.util.Objects;


/**
 *
 * @author user
 */
public class HighScore {
    
    public final String name;
    public final String difficulty;
    public final int steps;
    
    public HighScore(String name, String difficulty, int steps) {
        this.name = name;
        this.difficulty = difficulty;
        this.steps = steps;
    }
    
    public String getName() { return name;}
    public String getDiff() { return difficulty; }
    public int getSteps() { return steps;}
    
    
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.difficulty);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HighScore other = (HighScore) obj;
        
        if (!Objects.equals(this.difficulty, other.difficulty)) {
            return false;
        }
        return true;
    }
}

