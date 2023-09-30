package inputs;

import main.Game;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import static main.GameStates.gameState;

public class MyMouseListener implements MouseListener, MouseMotionListener {
    private final Game game;


    public MyMouseListener(Game game) {
        this.game = game;
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            switch (gameState) {
                case MENU:
                    game.getMenu().mouseClicked(e.getX(), e.getY());
                    break;
                case PLAYING_AGAINST_PERSON:
                    game.getPlayingAgainstPerson().mouseClicked(e.getX(), e.getY());
                    break;
                case PLAYING_AGAINST_AI:
                    game.getPlayingAgainstAI().mouseClicked(e.getX(), e.getY());
                    break;
                case AI_AGAINST_AI:
                    game.getAIAgainstAI().mouseClicked(e.getX(), e.getY());
                    break;
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        switch (gameState) {
            case MENU:
                game.getMenu().mouseMoved(e.getX(), e.getY());
                break;
            case PLAYING_AGAINST_PERSON:
                game.getPlayingAgainstPerson().mouseMoved(e.getX(), e.getY());
                break;
            case PLAYING_AGAINST_AI:
                game.getPlayingAgainstAI().mouseMoved(e.getX(), e.getY());
                break;
            case AI_AGAINST_AI:
                game.getAIAgainstAI().mouseClicked(e.getX(), e.getY());
                break;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        switch (gameState) {
            case MENU:
                game.getMenu().mousePressed(e.getX(), e.getY());
                break;
            case PLAYING_AGAINST_PERSON:
                game.getPlayingAgainstPerson().mousePressed(e.getX(), e.getY());
                break;
            case PLAYING_AGAINST_AI:
                game.getPlayingAgainstAI().mousePressed(e.getX(), e.getY());
                break;
            case AI_AGAINST_AI:
                game.getAIAgainstAI().mouseClicked(e.getX(), e.getY());
                break;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        switch (gameState) {
            case MENU:
                game.getMenu().mouseReleased(e.getX(), e.getY());
                break;
            case PLAYING_AGAINST_PERSON:
                game.getPlayingAgainstPerson().mouseReleased(e.getX(), e.getY());
                break;
            case PLAYING_AGAINST_AI:
                game.getPlayingAgainstAI().mouseReleased(e.getX(), e.getY());
                break;
            case AI_AGAINST_AI:
                game.getAIAgainstAI().mouseClicked(e.getX(), e.getY());
                break;
        }
    }


    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }
}
