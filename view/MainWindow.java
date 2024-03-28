/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

/**
 *
 * @author user
 */
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import labyrinth.Direction;
import labyrinth.Game;
import labyrinth.GameID;

public class MainWindow extends JFrame{
    private final Game game;
    private Board board;
    private final JLabel gameStatLabel; 
    
    private GameID nextLevelID;
    private int finishedLevels;
    private Timer timer;
    private long startTime;
    private long elapsedTime;
    private long currTime;
    private double elapsedSeconds;
    private String timestring ;
    private final JLabel timerLabel; 
    

    
    public MainWindow() throws IOException, SQLException, ClassNotFoundException{
        game = new Game();
        
        startTime = 0;
        elapsedTime = 0;
        currTime = 0;
        elapsedSeconds = 0;
        timestring = "";
        finishedLevels = 0;
        
        
        setTitle("Labyrinth");
        setSize(600, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        URL url = MainWindow.class.getClassLoader().getResource("res/labyrinth.png");
        setIconImage(Toolkit.getDefaultToolkit().getImage(url));
        
        JMenuBar menuBar = new JMenuBar();
        JMenu menuGame = new JMenu("Menu");
        JMenu menuGameLevel = new JMenu("Levels");

        JMenu menuGameScale = new JMenu("Scale");
        createGameLevelMenuItems(menuGameLevel);
        createScaleMenuItems(menuGameScale, 1.0, 2.0, 0.5);

        JMenuItem menuHighScores = new JMenuItem(new AbstractAction("Score table") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new HighScoreWindow(game.getHighScores(), MainWindow.this);
                    setVisible(true);
                } catch (SQLException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        JMenuItem menuGameExit = new JMenuItem(new AbstractAction("Exit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
        
        JMenuItem menuRestart = new JMenuItem(new AbstractAction("Restart") {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetgame();
            }
        });

        menuGame.add(menuGameLevel);
        menuGame.add(menuGameScale);
        menuGame.addSeparator();
        menuGame.add(menuHighScores);
        menuGame.add(menuGameExit);
        menuBar.add(menuGame);
        setJMenuBar(menuBar);
        menuGame.add(menuRestart);
        
        setLayout(new BorderLayout(0, 10));
        gameStatLabel = new JLabel("label");
        timerLabel = new JLabel("label");
      

        add(gameStatLabel, BorderLayout.NORTH);
        add(timerLabel, BorderLayout.SOUTH);
        try { add(board = new Board(game), BorderLayout.CENTER); } catch (IOException ex) {}
        
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ke) {
                super.keyPressed(ke); 
                if (!game.isLevelLoaded()) return;
                int kk = ke.getKeyCode();
                Direction d = null;
                switch (kk){
                    case KeyEvent.VK_LEFT:  d = Direction.LEFT; break;
                    case KeyEvent.VK_RIGHT: d = Direction.RIGHT; break;
                    case KeyEvent.VK_UP:    d = Direction.UP; break;
                    case KeyEvent.VK_DOWN:  d = Direction.DOWN; break;
                    case KeyEvent.VK_ESCAPE: game.loadGame(game.getGameID());
                }
                
                refreshGameStatLabel();

                board.repaint();
                
                if (d != null && game.step(d )){
                    
                } 
            }

        });
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        game.loadGame(new GameID("EASY", 1));
        board.refresh();
        pack();

       
        Timer midTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                game.getDark().updateVisibility(game.getPlayerPos());
                GameID init = game.getGameID();
                
                if (game.endGame()) {
                    timer.stop();
                    JOptionPane.showMessageDialog(MainWindow.this, "Game Over!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                    if (finishedLevels == 0){
                        JOptionPane.showMessageDialog(MainWindow.this, "You didn't finish any level", "Game over", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        String name = JOptionPane.showInputDialog(MainWindow.this, "You have finished " + finishedLevels + " levels in " + elapsedSeconds + " seconds! Enter your name: ", "Game over", JOptionPane.INFORMATION_MESSAGE);
                        if(name != null) try {
                            game.saveTheScore(name, init.difficulty, finishedLevels);
                            finishedLevels = 0;
                        } catch (SQLException ex) {
                            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                    int option = JOptionPane.showConfirmDialog(MainWindow.this, "Start the game again?" , "Game over", JOptionPane.INFORMATION_MESSAGE);
                    if (option == JOptionPane.OK_OPTION) {
                        resetgame();
                    }
                    else {
                        exitAction();
                    }
                        
                } else if (finishedLevel() && init.level == 5){
                    timer.stop();
                    JOptionPane.showMessageDialog(MainWindow.this, "You finished the last level in current difficulty!", "You win!", JOptionPane.INFORMATION_MESSAGE);
                    int option = JOptionPane.showConfirmDialog(MainWindow.this, "Continue to the next level?", "You win!", JOptionPane.INFORMATION_MESSAGE);
                    if(option == JOptionPane.OK_OPTION){
                        continueNextLevel(init.difficulty);
                    } else {
                        String name = JOptionPane.showInputDialog(MainWindow.this, "Enter your name: ", "You win!", JOptionPane.INFORMATION_MESSAGE);
                        if (name != null) {
                            try {
                                game.saveTheScore(name, init.difficulty, finishedLevels);
                                
                                
                            } catch (SQLException ex) {
                                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                       exitAction();
                    }
                }

                game.moveDragon();
                refreshGameStatLabel();
                board.repaint();
                
                
                if(finishedLevel()){
                    finishedLevels++;
                    try {
                        startNextLevel(init);
                    } catch (SQLException ex) {
                        Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
        });

        midTimer.start();
        refreshGameStatLabel();
        startTimer();
        setVisible(true);
        
        
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent wEvent){
                int option = JOptionPane.showConfirmDialog(MainWindow.this, "Are you sure you want to exit?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }
    
    public boolean finishedLevel() {
        return game.reachedEnd();
    }
    
    public final void startNextLevel(GameID init) throws SQLException {
        nextLevelID = game.getNextLevelID();
        if (nextLevelID != null) {
            game.loadGame(nextLevelID);
            board.refresh();
            refreshGameStatLabel();
            pack();
        }
    }
    
    
    public final void continueNextLevel(String level){
        startTime = System.currentTimeMillis();
        if(level.equals("EASY")){
            game.loadGame(new GameID("MEDIUM", 1));  
        } else if(level.equals("MEDIUM")){
            game.loadGame(new GameID("HARD", 1));
        } else if(level.equals("HARD")){
            game.loadGame(new GameID("EASY", 1));
        }
        refreshGameStatLabel();
        timerLabel.setText(timestring);
        resetTimer();
    }
    
    
    public final void startTimer() {
        currTime = System.currentTimeMillis();
        timer = new Timer(10, (ActionEvent e) -> {
            elapsedTime = System.currentTimeMillis() - currTime;
            elapsedSeconds = (double) elapsedTime / 1000;
            timestring = "Elapsed time:" + elapsedSeconds + " s";
            timerLabel.setText(timestring);
        });
        timer.start();
    }
        
     public void resetTimer() {
        currTime = System.currentTimeMillis();
        timer.restart();
    }
    
    
    private void resetgame(){
        startTime = System.currentTimeMillis();
        game.loadGame(game.getGameID());
        refreshGameStatLabel();
        timerLabel.setText(timestring);
        resetTimer();
    }
    
    
    private void refreshGameStatLabel(){
        String s = "Finished levels: " + finishedLevels;
        gameStatLabel.setText(s);
    }
    
    public void exitAction(){
        AbstractAction exitAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        };

        JMenuItem exitMenuItem = new JMenuItem(exitAction);
        exitAction.putValue(AbstractAction.NAME, "Exit");
        exitMenuItem.doClick();
    }

    private void createGameLevelMenuItems(JMenu menu){
        for (String s : game.getDifficulties()){
            JMenu difficultyMenu = new JMenu(s);
            menu.add(difficultyMenu);
            for (Integer i : game.getLevelsOfDifficulty(s)){
                JMenuItem item = new JMenuItem(new AbstractAction("Level-" + i) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        game.loadGame(new GameID(s, i));
                        board.refresh();
                        pack();
                    }
                });
                difficultyMenu.add(item);
            }
        }
    }
    
    private void createScaleMenuItems(JMenu menu, double from, double to, double by){
        while (from <= to){
            final double scale = from;
            JMenuItem item = new JMenuItem(new AbstractAction(from + "x") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (board.setScale(scale)) pack();
                }
            });
            menu.add(item);
            
            if (from == to) break;
            from += by;
            if (from > to) from = to;
        }
    }
    
    
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        try {
            MainWindow main = new MainWindow();
            main.setVisible(true);
        } catch (IOException ex) {}
    }    
}

