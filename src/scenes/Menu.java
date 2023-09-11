package scenes;

import main.Game;
import managers.TileManager;
import ui.MyButton;
import java.awt.*;
import static main.GameStates.*;

public class Menu extends GameScene implements SceneMethods{
    private MyButton bPlayingAgainstAI, bPlayingAgainstPerson, bAIAgainstAI, bQuit;
    TileManager tileManager = new TileManager ();

    public Menu (Game game) {
        super(game);
        initButtons();
    }


    private void initButtons () {
        int w = 246;
        int h = 54;
        int x = 640/2 - w/2;
        int y = 133;
        int yOffset = 128;

        bPlayingAgainstAI = new MyButton ( "Play Against AI", x, y,  w, h );
        bPlayingAgainstPerson = new MyButton ( "Play Against Person", x, y + yOffset, w, h );
        bAIAgainstAI = new MyButton ( "AI Against AI", x, y + 2*yOffset, w, h );
        bQuit = new MyButton ( "Quit", x+64, y + 3*yOffset, w/2, h );
    }

    @Override
    public void render ( Graphics g ) {
        for(int y=0; y<11; y++){
            for(int x=0; x<10; x++)
                if ((x + y) % 2 == 0)
                    g.drawImage ( tileManager.getSprite ( 6 ), 64 * x, 64 * y, null );
                else
                    g.drawImage ( tileManager.getSprite ( 7 ), 64 * x, 64 * y, null );

        }
        drawButtons(g);
    }

    @Override
    public void mouseClicked ( int x, int y ) {
        if(bPlayingAgainstAI.getBounds ().contains ( x, y )){
            SetGameState(PLAYING_AGAINST_AI);
        } else if (bPlayingAgainstPerson.getBounds ().contains ( x, y )) {
            SetGameState ( PLAYING_AGAINST_PERSON );
        } else if (bAIAgainstAI.getBounds ().contains ( x, y )) {
            SetGameState ( AI_AGAINST_AI );
        } else if (bQuit.getBounds ().contains ( x, y )) {
            System.exit ( 0 );
        }
    }
    @Override
    public void mouseMoved ( int x, int y ) {
        bPlayingAgainstAI.setMouseOver ( false );
        bPlayingAgainstPerson.setMouseOver ( false );
        bAIAgainstAI.setMouseOver ( false );
        bQuit.setMouseOver ( false );

        if (bPlayingAgainstAI.getBounds().contains(x, y)) {
            bPlayingAgainstAI.setMouseOver(true);
        } else if (bPlayingAgainstPerson.getBounds().contains(x, y)) {
            bPlayingAgainstPerson.setMouseOver(true);
        } else if (bAIAgainstAI.getBounds ().contains ( x, y )) {
            bAIAgainstAI.setMouseOver ( true );
        } else if (bQuit.getBounds().contains(x, y)) {
            bQuit.setMouseOver(true);
        }
    }
    @Override
    public void mousePressed ( int x, int y ) {
        if (bPlayingAgainstAI.getBounds().contains(x, y)) {
            bPlayingAgainstAI.setMousePressed(true);
        } else if (bPlayingAgainstPerson.getBounds().contains(x, y)) {
            bPlayingAgainstPerson.setMousePressed(true);
        } else if (bAIAgainstAI.getBounds ().contains ( x, y )) {
            bAIAgainstAI.setMousePressed ( true );
        } else if (bQuit.getBounds().contains(x, y)) {
            bQuit.setMousePressed(true);
        }
    }

    @Override
    public void mouseReleased ( int x, int y ) {
        resetButtons();
    }
    private void resetButtons () {
        bPlayingAgainstAI.resetBooleans();
        bPlayingAgainstPerson.resetBooleans();
        bAIAgainstAI.resetBooleans ();
        bQuit.resetBooleans();
    }
    private void drawButtons ( Graphics g ) {
        bPlayingAgainstAI.draw ( g );
        bPlayingAgainstPerson.draw ( g );
        bAIAgainstAI.draw ( g );
        bQuit.draw ( g );
    }
}
