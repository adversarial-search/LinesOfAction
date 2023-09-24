package main;

public enum GameStates {
    MENU,
    PLAYING_AGAINST_AI,
    PLAYING_AGAINST_PERSON,
    AI_AGAINST_AI;

    public static GameStates gameState = MENU;

    public static void SetGameState(GameStates state) {
        gameState = state;
    }
}
