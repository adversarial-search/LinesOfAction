package main;

import java.awt.*;

public class Render {
    private final Game game;

    public Render(Game game) {
        this.game = game;
    }

    public void render(Graphics g) {
        switch (GameStates.gameState) {
            case MENU:
                game.getMenu().render(g);
                break;
            case PLAYING_AGAINST_PERSON:
                game.getPlayingAgainstPerson().render(g);
                break;
            case PLAYING_AGAINST_AI:
                game.getPlayingAgainstAI().render(g);
                break;
            case AI_AGAINST_AI:
                game.getAIAgainstAI().render(g);
                break;
        }
    }

}

