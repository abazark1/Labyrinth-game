/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistence;

import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;



/**
 *
 * @author user
 */
public class Database {
    private Connection connection;

    
    public Database() throws SQLException, ClassNotFoundException {
        Connection c = null;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            c = DriverManager.getConnection("jdbc:mysql://localhost/labyrinth?" + "serverTimezone=UTC&user=root&password=root");
        } catch (SQLException e) {}
        connection = c;
    }
    
    
    public ArrayList<HighScore> getHighScores() throws SQLException{
        ArrayList<HighScore> scores = new ArrayList<>();
        try(Statement statement = connection.createStatement()){
            ResultSet rs = statement.executeQuery("SELECT * FROM HIGHSCORE");
            while(rs.next()){
                String name = rs.getString("PName");
                String diff = rs.getString("Difficulty");
                int steps = rs.getInt("Steps");
                scores.add(new HighScore(name, diff, steps));
            }
            Collections.sort(scores, (HighScore t, HighScore t1) -> t1.getSteps() - t.getSteps());
        } catch (Exception e) {}
        return scores;
    }
    
    
    public void storeHighScore(String name, String difficulty, int steps) throws SQLException {
    try (Statement stmt = connection.createStatement()) {
                String s = "INSERT INTO HIGHSCORE" +
                        " (PName, Difficulty, Steps) " +
                        "VALUES('" + name + "','" + difficulty + "'" +
                        "," + steps +
                        ")";
                stmt.executeUpdate(s);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }    
    }
    
}
