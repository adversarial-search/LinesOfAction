package inputs;

import main.Game;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import static main.GameStates.*;

public class MyMouseListener implements MouseListener, MouseMotionListener {
    private Game game;


    public MyMouseListener ( Game game ) {
        this.game = game;
    }


    @Override
    public void mouseClicked ( MouseEvent e ) {
        if(e.getButton () == MouseEvent.BUTTON1){
            switch (gameState){
                case MENU:
                    game.getMenu ().mouseClicked(e.getX (), e.getY ());
                    break;
                case PLAYING_AGAINST_PERSON:
                    game.getPlayingAgainstPerson ().mouseClicked ( e.getX (), e.getY () );
            }
        }
    }
    @Override
    public void mouseMoved ( MouseEvent e ) {
        switch (gameState){
            case MENU:
                game.getMenu ().mouseMoved ( e.getX (), e.getY () );
                break;
        }
    }
    @Override
    public void mousePressed ( MouseEvent e ) {
        switch (gameState){
            case MENU:
                game.getMenu ().mousePressed ( e.getX (), e.getY () );
                break;
        }
    }
    @Override
    public void mouseReleased ( MouseEvent e ) {
        switch (gameState){
            case MENU:
                game.getMenu ().mouseReleased ( e.getX (), e.getY () );
                break;
        }
    }


    @Override
    public void mouseEntered ( MouseEvent e ) {

    }
    @Override
    public void mouseExited ( MouseEvent e ) {

    }
    @Override
    public void mouseDragged ( MouseEvent e ) {

    }
}
