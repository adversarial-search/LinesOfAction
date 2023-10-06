package scenes;

import assistants.LevelBuild;
import main.Game;
import objects.StateInfo;
import ui.MyButton;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

import static main.GameStates.MENU;
import static main.GameStates.SetGameState;

public class PlayingAgainstPerson extends GameScene implements SceneMethods {
    Game game;
    private MyButton bMenu, bReset;
    private static Set<StateInfo> stateHistory = new HashSet<> (  );

    public PlayingAgainstPerson(Game game) {
        super(game);
        this.game = game;
        initButtons();
    }

    public static void setUpInitialGameState() {
        piecesPositions = LevelBuild.getInitialPiecesPositions();
        turn = BLACK_TURN;
        gameWon = false;
        winner = -1;
        stateHistory.add ( new StateInfo ( piecesPositions, true ) );
    }

    private void initButtons() {
        bMenu = new MyButton("Menu", 14, 12, 100, 40);
        bReset = new MyButton("Reset", 128, 12, 100, 40);
    }

    private void checkMenuAndResetClicked(int x, int y) {
        if (bMenu.getBounds().contains(x, y)) {
            SetGameState(MENU);
        } else if (bReset.getBounds().contains(x, y)) {
            resetGame();
        }
    }


    @Override
    public void render(Graphics g) {
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
    }

    private void drawButtons(Graphics g) {
        bMenu.draw(g);
        bReset.draw(g);
    }


    @Override
    protected void resetGame() {
        resetValidMovesAndActivePiece();
        piecesPositions = LevelBuild.getInitialPiecesPositions();
        turn = BLACK_TURN;
        gameWon = false;
        winner = -1;
        stateHistory.add ( new StateInfo ( piecesPositions, true ) );
    }

    @Override
    protected void changeTurn() {
        if (turn == BLACK_TURN) turn = WHITE_TURN;
        else turn = BLACK_TURN;
        resetValidMovesAndActivePiece();

        StateInfo stateInfo = new StateInfo ( piecesPositions, turn == BLACK_TURN );
        if(stateHistory.contains ( stateInfo )){
            System.out.println ( "draw" );
            resetGame ();
        }else stateHistory.add ( stateInfo );
    }

    @Override
    protected void makeMove(int x, int y) {
        byte row = getRow(y);
        byte col = getCol(x);

        if (turn == WHITE_TURN) {
            makePlayerMove(W, B, row, col);
        } else if (turn == BLACK_TURN) {
            makePlayerMove(B, W, row, col);
        }
    }


    @Override
    public void mouseClicked(int x, int y) {
        checkMenuAndResetClicked(x, y);
        if (gameWon) return;
        makeMove(x, y);
    }

    @Override
    public void mouseMoved(int x, int y) {
        bMenu.setMouseOver(false);
        bReset.setMouseOver(false);

        if (bMenu.getBounds().contains(x, y))
            bMenu.setMouseOver(true);
        else if (bReset.getBounds().contains(x, y)) {
            bReset.setMouseOver(true);
        }
    }

    @Override
    public void mousePressed(int x, int y) {
        if (bMenu.getBounds().contains(x, y))
            bMenu.setMousePressed(true);
        else if (bReset.getBounds().contains(x, y)) {
            bReset.setMousePressed(true);
        }
    }

    @Override
    public void mouseReleased(int x, int y) {
        resetButtons();
    }

    private void resetButtons() {
        bMenu.resetBooleans();
        bReset.resetBooleans();
    }
}

