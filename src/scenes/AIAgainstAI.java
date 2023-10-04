package scenes;

import assistants.LevelBuild;
import main.Game;
import ui.MyButton;

import java.awt.*;
import java.util.Random;

import static assistants.MinMaxFunctions.getAlphaBetaMiniMaxState;
import static assistants.MinMaxFunctions.getBasicMiniMaxState;
import static main.GameStates.MENU;
import static main.GameStates.SetGameState;

public class AIAgainstAI extends GameScene implements SceneMethods {
    private static final byte MAX_DEPTH_BASIC = 0;
    private static final byte MAX_DEPTH_ALPHA_BETA = 0;
    private static final byte WHITE_DEPTH_BASIC = 1;
    private static final byte WHITE_DEPTH_AB = 0;
    private static final byte BLACK_DEPTH_BASIC = 1;
    private static final byte BLACK_DEPTH_AB = 0;
    protected static boolean whiteChosen = false;
    protected static boolean blackChosen = false;
    protected byte whiteAiType;
    protected byte blackAiType;
    private static final Random random = new Random();
    private static long startTime = 0L;
    private static long endTime = 0L;

    private MyButton
            bReset,
            bMenu,
            bChooseNoPruningWhite,
            bChooseAlphaBetaWhite,
            bChooseNoPruningBlack,
            bChooseAlphaBetaBlack,
            bNextMove,
            drawTextPickWhiteAiType,
            drawTextPickBlackAiType;


    public AIAgainstAI(Game game) {
        super(game);
        initButtons();
    }

    public static void setUpInitialGameState() {
        piecesPositions = LevelBuild.getInitialPiecesPositions();
        turn = BLACK_TURN;
        gameWon = false;
        winner = -1;
        whiteChosen = false;
        blackChosen = false;
    }

    private void initButtons() {
        bChooseNoPruningWhite = new MyButton("Simple MinMax", 192, 128, 256, 128);
        bChooseAlphaBetaWhite = new MyButton("Alpha Beta MinMax", 192, 416, 256, 128);
        bChooseNoPruningBlack = new MyButton("Simple MinMax", 192, 128, 256, 128);
        bChooseAlphaBetaBlack = new MyButton("Alpha Beta MinMax", 192, 416, 256, 128);
        drawTextPickWhiteAiType = new MyButton("Choose white ai type", 192, 60, 256, 50);
        drawTextPickBlackAiType = new MyButton("Choose black ai type", 192, 60, 256, 50);

        bMenu = new MyButton("Menu", 14, 12, 100, 40);
        bReset = new MyButton("Reset", 128, 12, 100, 40);
        // TODO: Think of a better looking design for the next button
        bNextMove = new MyButton(">>", 580, 650, 50, 40);
    }

    private void makeAiMove(byte currentAiId) {
        byte aiType = currentAiId == W ? whiteAiType : blackAiType;

        if(turn == W){
            switch (aiType) {
                case CLASSIC_MINMAX ->
                        piecesPositions = getBasicMiniMaxState(currentAiId, WHITE_DEPTH_BASIC, piecesPositions);
                case PRUNING_MINMAX ->
                        piecesPositions = getAlphaBetaMiniMaxState(currentAiId, WHITE_DEPTH_AB, piecesPositions);
            }
        }else if(turn == B){
            switch (aiType) {
                case CLASSIC_MINMAX ->
                        piecesPositions = getBasicMiniMaxState(currentAiId, BLACK_DEPTH_BASIC, piecesPositions);
                case PRUNING_MINMAX ->
                        piecesPositions = getAlphaBetaMiniMaxState(currentAiId, BLACK_DEPTH_AB, piecesPositions);
            }
        }


        checkWinningConditions();
        changeTurn();
    }


    @Override
    protected void resetGame() {
        resetValidMovesAndActivePiece();
        piecesPositions = LevelBuild.getInitialPiecesPositions();
        gameWon = false;
        turn = BLACK_TURN;
        blackChosen = false;
        whiteChosen = false;
    }

    @Override
    protected void changeTurn() {
        if (turn == BLACK_TURN) turn = WHITE_TURN;
        else turn = BLACK_TURN;
    }

    @Override
    protected void makeMove(int x, int y) {

    }

    private static int movesPerGameCounter = 0;
    private static int gamesPlayedCounter = 0;
    private static int gamesWonByWhite = 0;
    private static int gamesWonByBlack = 0;
    private static final int gamesToPlay = 49;
    private static int gamesThrashed = 0;
    @Override
    public void render(Graphics g) {

        if (whiteChosen && blackChosen) {
            //draw background
            drawBoardBackground(g);

            //draw piecesPositions
            drawPieces(g);

            //draw active piece
            drawActive(g);

            //draw valid moves
            drawValidMoves(g);

            //draw buttons
            drawButtons(g);

            //display winner
            displayWinner(g);

            if(!gameWon){
                if(movesPerGameCounter <= 100) {
                    makeNextMove();
                    movesPerGameCounter += 1;
                }else{
                    resetValidMovesAndActivePiece();
                    piecesPositions = LevelBuild.getInitialPiecesPositions();
                    gameWon = false;
                    turn = BLACK_TURN;

                    gamesPlayedCounter += 1;
                    gamesThrashed += 1;
                    System.out.println("Thrashed.");
                    movesPerGameCounter = 0;
                }
            }else {
                if(winner == W) gamesWonByWhite += 1;
                else if(winner == B) gamesWonByBlack += 1;

                if(gamesPlayedCounter < gamesToPlay){
                    resetValidMovesAndActivePiece();
                    piecesPositions = LevelBuild.getInitialPiecesPositions();
                    gameWon = false;
                    turn = BLACK_TURN;

                    gamesPlayedCounter += 1;
                    movesPerGameCounter = 0;
                }else{
                    endTime = System.currentTimeMillis();
                    System.out.println("Games played: " + (gamesToPlay+1));
                    System.out.println("Games won by white: " + gamesWonByWhite);
                    System.out.println("Games won by black: " + gamesWonByBlack);
                    System.out.println("Chance of white to win: " + (gamesWonByWhite*100)/(gamesToPlay+1));
                    System.out.println("Chance of black to win: " + (gamesWonByBlack*100)/(gamesToPlay+1));
                    long timeTaken = (endTime-startTime)/1000;
                    System.out.println("Time taken: " + timeTaken + "s");
                    System.out.println("Games thrashed: " + gamesThrashed);
                    System.exit(0);
                }
            }
        } else {
            if (!blackChosen) {
                drawMenuBackground(g);
                drawPickText(g, B);
                drawChooseAiTypeButtonsBlack(g);

            } else {
                drawMenuBackground(g);
                drawPickText(g, W);
                drawChooseAiTypeButtonsWhite(g);
            }
        }
    }

    public void drawPickText(Graphics g, byte color) {
        if (color == W) {
            drawTextPickWhiteAiType.drawTextOnly(g);
        } else {
            drawTextPickBlackAiType.drawTextOnly(g);
        }
    }

    private void drawButtons(Graphics g) {
        bMenu.draw(g);
        bReset.draw(g);
        bNextMove.draw(g);
    }

    private void drawChooseAiTypeButtonsWhite(Graphics g) {
        bChooseAlphaBetaWhite.draw(g);
        bChooseNoPruningWhite.draw(g);
    }

    private void drawChooseAiTypeButtonsBlack(Graphics g) {
        bChooseAlphaBetaBlack.draw(g);
        bChooseNoPruningBlack.draw(g);
    }

    @Override
    public void mouseMoved(int x, int y) {
        bNextMove.setMouseOver(false);
        bMenu.setMouseOver(false);
        bReset.setMouseOver(false);
        bChooseAlphaBetaWhite.setMouseOver(false);
        bChooseNoPruningWhite.setMouseOver(false);
        bChooseAlphaBetaBlack.setMouseOver(false);
        bChooseNoPruningBlack.setMouseOver(false);

        if (bMenu.getBounds().contains(x, y))
            bMenu.setMouseOver(true);
        else if (bReset.getBounds().contains(x, y))
            bReset.setMouseOver(true);
        else if (bChooseNoPruningBlack.getBounds().contains(x, y))
            bChooseNoPruningBlack.setMouseOver(true);
        else if (bChooseAlphaBetaBlack.getBounds().contains(x, y))
            bChooseAlphaBetaBlack.setMouseOver(true);
        else if (bNextMove.getBounds().contains(x, y))
            bNextMove.setMouseOver(true);


        if (bChooseNoPruningWhite.getBounds().contains(x, y))
            bChooseNoPruningWhite.setMouseOver(true);
        else if (bChooseAlphaBetaWhite.getBounds().contains(x, y))
            bChooseAlphaBetaWhite.setMouseOver(true);


    }

    @Override
    public void mouseClicked(int x, int y) {

        if (!blackChosen) {
            chooseBlackAiType(x, y);
            return;
        } else if (!whiteChosen) {
            chooseWhiteAiType(x, y);
            startTime = System.currentTimeMillis();
            return;
        }


        // check if clicked management buttons
        if (bMenu.getBounds().contains(x, y)) {
            resetGame();
            SetGameState(MENU);
        } else if (bReset.getBounds().contains(x, y)) {
            resetGame();
        } else if (bNextMove.getBounds().contains(x, y) && !gameWon) {
            makeNextMove();
        }


    }

    @Override
    public void mousePressed(int x, int y) {
        if (bMenu.getBounds().contains(x, y))
            bMenu.setMousePressed(true);
        else if (bReset.getBounds().contains(x, y))
            bReset.setMousePressed(true);
        else if (bChooseNoPruningWhite.getBounds().contains(x, y))
            bChooseNoPruningWhite.setMousePressed(true);
        else if (bChooseAlphaBetaWhite.getBounds().contains(x, y))
            bChooseAlphaBetaWhite.setMousePressed(true);
        else if (bNextMove.getBounds().contains(x, y))
            bNextMove.setMousePressed(true);

        if (bChooseNoPruningBlack.getBounds().contains(x, y))
            bChooseNoPruningBlack.setMousePressed(true);
        else if (bChooseAlphaBetaBlack.getBounds().contains(x, y))
            bChooseAlphaBetaBlack.setMousePressed(true);

    }

    @Override
    public void mouseReleased(int x, int y) {
        resetButtons();
    }

    private void chooseWhiteAiType(int x, int y) {
        if (bChooseAlphaBetaWhite.getBounds().contains(x, y)) {
            whiteAiType = PRUNING_MINMAX;
            whiteChosen = true;
        } else if (bChooseNoPruningWhite.getBounds().contains(x, y)) {
            whiteAiType = CLASSIC_MINMAX;
            whiteChosen = true;
        }
    }

    private void chooseBlackAiType(int x, int y) {
        if (bChooseAlphaBetaBlack.getBounds().contains(x, y)) {
            blackAiType = PRUNING_MINMAX;
            blackChosen = true;
        } else if (bChooseNoPruningBlack.getBounds().contains(x, y)) {
            blackAiType = CLASSIC_MINMAX;
            blackChosen = true;
        }
    }

    private void makeNextMove() {
        makeAiMove(turn);
    }

    private void resetButtons() {
        bChooseAlphaBetaBlack.resetBooleans();
        bChooseAlphaBetaWhite.resetBooleans();
        bChooseNoPruningBlack.resetBooleans();
        bChooseNoPruningWhite.resetBooleans();
        bMenu.resetBooleans();
        bReset.resetBooleans();
        bNextMove.resetBooleans();
    }
}
