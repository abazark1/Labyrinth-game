/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package labyrinth;

/**
 *
 * @author user
 */
import java.util.ArrayList;
import java.util.Random;

public final class GameLevel {
    public final GameID gameID;
    public final int rows, cols;
    public final LevelItem[][] level;
    public Position player = new Position(0, 0);
    public Position dragon = new Position(0, 0);;
    private boolean gameOver;
    private boolean destReached;
    private int finishedLevls = 0;
    
    
    public GameLevel(ArrayList<String> gameLevelRows, GameID gameID){
        this.gameID = gameID;
        int c = 0;
        for (String s : gameLevelRows) if (s.length() > c) c = s.length();
        rows = gameLevelRows.size();
        cols = c;
        level = new LevelItem[rows][cols];
        gameOver = false;
        destReached = false;
        
        for (int i = 0; i < rows; i++){
            String s = gameLevelRows.get(i);
            for (int j = 0; j < s.length(); j++){
                switch (s.charAt(j)){
                    case '@': player = new Position(j, i);
                              level[i][j] = LevelItem.EMPTY; break;
                    case 'x': level[i][j] = LevelItem.WALL; break;
                    case '*': level[i][j] = LevelItem.DESTINATION; break;
                    case '&': level[i][j] = LevelItem.DRAGON; 
                              dragon = new Position(j, i); break;
                    default:  level[i][j] = LevelItem.EMPTY; break;
                }
            }
            for (int j = s.length(); j < cols; j++){
                level[i][j] = LevelItem.EMPTY;
            }
        }
    }
    
    public GameLevel(GameLevel gl) {
        gameID = gl.gameID;
        rows = gl.rows;
        cols = gl.cols;
        level = new LevelItem[rows][cols];
        gameOver = false;
        destReached = false;
        player = new Position(gl.player.x, gl.player.y);
        dragon = new Position(gl.dragon.x, gl.dragon.y);
        for (int i = 0; i < rows; i++){
            System.arraycopy(gl.level[i], 0, level[i], 0, cols);
        }
    }

    public boolean isValidPosition(Position p){
        return (p.x >= 0 && p.y >= 0 && p.x < cols && p.y < rows);
    }
    
    public boolean isFree(Position p){
        if (!isValidPosition(p)) return false;
        LevelItem li = level[p.y][p.x];
        return (li == LevelItem.EMPTY);
    }
    
    public boolean isDestination(Position p){
        if (!isValidPosition(p)) return false;
        LevelItem li = level[p.y][p.x];
        return (li == LevelItem.DESTINATION);
    }
    
    public boolean isDragon(Position p, Direction d){
        Position c = p.translate(d);
        if(!isValidPosition(c)) return false;
        LevelItem li = level[c.y][c.x];
        return (li == LevelItem.DRAGON);
    }
    
    
    public void moveDragon() {
        Random random = new Random();
        ArrayList<Direction> posDirections = new ArrayList<>();

        if (isValidPosition(dragon.translate(Direction.UP)) && isFree(dragon.translate(Direction.UP))) {
            posDirections.add(Direction.UP);
        }
        if (isValidPosition(dragon.translate(Direction.DOWN)) && isFree(dragon.translate(Direction.DOWN))) {
            posDirections.add(Direction.DOWN);
        }
        if (isValidPosition(dragon.translate(Direction.LEFT)) && isFree(dragon.translate(Direction.LEFT))) {
            posDirections.add(Direction.LEFT);
        }
        if (isValidPosition(dragon.translate(Direction.RIGHT)) && isFree(dragon.translate(Direction.RIGHT))) {
            posDirections.add(Direction.RIGHT);
        }

        if (!posDirections.isEmpty()) {
            int randomIndex = random.nextInt(posDirections.size());
            Direction chosenDirection = posDirections.get(randomIndex);
            Position nextPosition = dragon.translate(chosenDirection);

            level[dragon.y][dragon.x] = LevelItem.EMPTY;
            level[nextPosition.y][nextPosition.x] = LevelItem.DRAGON;
            dragon = nextPosition;

            if (isDragonClose(player)) {
                gameOver = true;
            }
        }
    }

    
    public boolean movePlayer(Direction d){
        Position curr = player;
        Position next = curr.translate(d);
        if (!gameOver) {
            if(isFree(next) || isDestination(next)){
                if(isDestination(next)){
                    player = next;
                    destReached = true;
                    finishedLevls++;
                    
                } else {
                    player = next;
                } 
                return true;
            } else {
                return false;
            }
        } 
        return false;
    }
    
    public boolean isDragonClose(Position player){
        for (Direction dir : Direction.values()) {
            if (isDragon(player, dir)) {
                return true;
            }
        }
        return false;
    }

    
    public void printLevel(){
        int x = player.x, y = player.y;
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j++){
                if (i == y && j == x)
                    System.out.print('@');
                else 
                    System.out.print(level[i][j].representation);
            }
            System.out.println("");
        }
    }

    public boolean isGameOver(){
        return gameOver;
    }
    
    public boolean finishedLevel(){
        return destReached;
    }
    
    public int finLevels(){
        return finishedLevls;
    }
    
}