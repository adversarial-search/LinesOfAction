package main;

import scenes.AIAgainstAI;
import scenes.Menu;
import scenes.PlayingAgainstAI;
import scenes.PlayingAgainstPerson;

import javax.swing.JFrame;

public class Game extends JFrame implements Runnable {
    private GameScreen gameScreen;
    private Thread gameThread;

    private final double FPS_SET = 30.0;
    private final double UPS_SET = 60.0;

    //Classes
    private Render render;
    private Menu menu;
    private AIAgainstAI aiAgainstAI;
    private PlayingAgainstAI playingAgainstAI;
    private PlayingAgainstPerson playingAgainstPerson;

    public Game () {
        initClasses();

        setDefaultCloseOperation ( EXIT_ON_CLOSE );
        setLocation ( 720, 200 );
        setResizable ( false );

        add ( gameScreen );
        pack (); //place after every component addition

        setVisible ( true );
    }
    private void start(){
        gameThread = new Thread ( this );

        gameThread.start ();
    }
    private void updateGame(){

    }
    private void initClasses () {
        render = new Render ( this );
        gameScreen = new GameScreen ( this );
        menu = new Menu ( this );
        aiAgainstAI = new AIAgainstAI ( this );
        playingAgainstAI = new PlayingAgainstAI ( this );
        playingAgainstPerson = new PlayingAgainstPerson ( this );
    }



    // Getters and setters
    public Render getRender () {
        return render;
    }

    public Menu getMenu () {
        return menu;
    }
    public AIAgainstAI getAiAgainstAI () {
        return aiAgainstAI;
    }
    public PlayingAgainstAI getPlayingAgainstAI () {
        return playingAgainstAI;
    }
    public PlayingAgainstPerson getPlayingAgainstPerson () {
        return playingAgainstPerson;
    }

    public static void main ( String[] args ) {
        Game game = new Game ();
        game.gameScreen.initInputs ();

        game.start();
    }

    @Override
    public void run () {
        //Render
        double timePerFrame = 1_000_000_000.0/ FPS_SET;
        double timePerUpdate = 1_000_000_000.0/ UPS_SET;

        long lastFrame = System.nanoTime ();
        long lastUpdate = System.nanoTime ();

        long lastTimeCheck = System.currentTimeMillis ();

        int frames = 0;
        int updates = 0;

        long now;

        while(true){
            now = System.nanoTime ();
            //Render
            if(now - lastFrame >= timePerFrame){
                repaint();
                lastFrame = now;
                frames++;
            }

            //Update
            if(now - lastUpdate >= timePerUpdate){
                updateGame ();
                lastUpdate = now;
                updates++;
            }

            if(System.currentTimeMillis () - lastTimeCheck >= 1_000){
                System.out.println ( "FPS: " + frames + " | UPS: " + updates );
                frames = 0;
                updates = 0;
                lastTimeCheck = System.currentTimeMillis ();
            }
        }

    }
}
