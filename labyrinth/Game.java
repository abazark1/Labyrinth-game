/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package labyrinth;

/**
 *
 * @author user
 */

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;
import java.sql.SQLException;
import persistence.Database;
import res.ResourceLoader;
import view.Dark;
import persistence.HighScore;

public class Game {
    private final HashMap<String, HashMap<Integer, GameLevel>> gameLevels;
    private GameLevel gameLevel = null;
    private GameID currentLevelID = null;
    private Dark dark;
    private final Database database;

    public Game() throws SQLException, ClassNotFoundException {
        gameLevels = new HashMap<>();
        database = new Database();
        readLevels();
    }

    public void loadGame(GameID gameID){
        gameLevel = new GameLevel(gameLevels.get(gameID.difficulty).get(gameID.level));
        currentLevelID = gameID;
        dark = new Dark(this);
    }
    
    public void loadNextLevel(GameID gameID){
        gameLevel = new GameLevel(gameLevels.get(gameID.difficulty).get(gameID.level));
    }
    
    public boolean endGame(){
        return gameLevel.isGameOver();
    }
    
    public boolean reachedEnd(){
        return gameLevel.finishedLevel();
    }
    
    
    public void printGameLevel(){ gameLevel.printLevel(); }
    
    public GameID getNextLevelID(){
        if (currentLevelID == null) return null;

        HashMap<Integer, GameLevel> levels = gameLevels.get(currentLevelID.difficulty);
        if (levels == null) return null;

        int nextLevel = currentLevelID.level + 1;
        if (levels.containsKey(nextLevel)) {
            return new GameID(currentLevelID.difficulty, nextLevel);
        } else {

            return null;
        }
    }
    
    public boolean step(Direction d){
        return gameLevel.movePlayer(d);
    }
    
    
    public Collection<String> getDifficulties(){ return gameLevels.keySet(); }
    
    public Collection<Integer> getLevelsOfDifficulty(String difficulty){
        if (!gameLevels.containsKey(difficulty)) return null;
        return gameLevels.get(difficulty).keySet();
    }
    
    public boolean isLevelLoaded(){ return gameLevel != null; }
    public int getLevelRows(){ return gameLevel.rows; }
    public int getLevelCols(){ return gameLevel.cols; }
    
    public LevelItem getItem(int row, int col){ return gameLevel.level[row][col]; }
    
    public GameID getGameID(){ return (gameLevel != null) ? gameLevel.gameID : null; }
    
    public Dark getDark() { return dark; }
    
    public void moveDragon(){
        gameLevel.moveDragon();
    }
   
    public Position getPlayerPos(){ 
        return new Position(gameLevel.player.x, gameLevel.player.y);
    }
    
    public Position getDragonPos(){
        return new Position(gameLevel.dragon.x, gameLevel.dragon.y);

    }
    
    public int getFinishedLevels(){ return (gameLevel != null) ? gameLevel.finLevels(): 0; }

    public ArrayList<HighScore> getHighScores() throws SQLException {
           
        return database.getHighScores();
    }
    
    public void saveTheScore(String name, String difficulty, int steps) throws SQLException{
        database.storeHighScore(name, difficulty, steps);
    }

    private void readLevels(){
        InputStream is; 
        is = ResourceLoader.loadResource("res/levels.txt");
        
        try (Scanner sc = new Scanner(is)){
            String line = readNextLine(sc);
            ArrayList<String> gameLevelRows = new ArrayList<>();
            
            while (!line.isEmpty()){
                GameID id = readGameID(line);
                if (id == null) return;
                
                System.out.println(id.difficulty + " ");

                gameLevelRows.clear();
                line = readNextLine(sc);
                while (!line.isEmpty() && line.trim().charAt(0) != ';'){
                    gameLevelRows.add(line);                    
                    line = readNextLine(sc);
                }
                addNewGameLevel(new GameLevel(gameLevelRows, id));
            }
        } catch (Exception e){
            System.out.println("Ajaj");
        }
        
    }
    
    private void addNewGameLevel(GameLevel gameLevel){
        HashMap<Integer, GameLevel> levelsOfDifficulty;
        if (gameLevels.containsKey(gameLevel.gameID.difficulty)){
            levelsOfDifficulty = gameLevels.get(gameLevel.gameID.difficulty);
            levelsOfDifficulty.put(gameLevel.gameID.level, gameLevel);
        } else {
            levelsOfDifficulty = new HashMap<>();
            levelsOfDifficulty.put(gameLevel.gameID.level, gameLevel);
            gameLevels.put(gameLevel.gameID.difficulty, levelsOfDifficulty);
        }
    }
  
    private String readNextLine(Scanner sc){
        String line = "";
        while (sc.hasNextLine() && line.trim().isEmpty()){
            line = sc.nextLine();
        }
        return line;
    }
    
    private GameID readGameID(String line){
        line = line.trim();
        if (line.isEmpty() || line.charAt(0) != ';') return null;
        Scanner s = new Scanner(line);
        s.next(); // skip ';'
        if (!s.hasNext()) return null;
        String difficulty = s.next().toUpperCase();
        if (!s.hasNextInt()) return null;
        int id = s.nextInt();
        return new GameID(difficulty, id);
    }    
}
