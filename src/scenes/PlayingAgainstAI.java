package scenes;

import assistants.LevelBuild;
import main.Game;
import objects.Point;
import ui.MyButton;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static main.GameStates.MENU;
import static main.GameStates.SetGameState;

public class PlayingAgainstAI extends GameScene implements SceneMethods {
    //  TODO fix array clone
    private static final byte MAX_DEPTH = 2;
    private static boolean idIsChosen = false;
    private static byte playerID, aiID;
    Game game;
    ArrayList<Long> differences = new ArrayList<>();
    private MyButton bChooseWhite, bChooseBlack, bReset, bMenu;
    private final Random random;
    private int functionCalls;


    public PlayingAgainstAI(Game game) {
        super(game);
        this.game = game;
        initButtons();
        random = new Random();
    }

    public static void setUpInitialGameState() {
        piecesPositions = LevelBuild.getInitialPiecesPositions();
        turn = BLACK_TURN;
        gameWon = false;
        winner = -1;
        idIsChosen = false;
        playerID = -1;
        aiID = -1;
    }

    private void initButtons() {
        bChooseBlack = new MyButton("Play As Black", 192, 128, 256, 128);
        bChooseWhite = new MyButton("Play As White", 192, 416, 256, 128);
        bMenu = new MyButton("Menu", 14, 12, 100, 40);
        bReset = new MyButton("Reset", 128, 12, 100, 40);
    }

    @Override
    public void render(Graphics g) {
        //draw buttons
        if (idIsChosen) {
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
            if (turn == aiID) makeAiMove();
        } else {
            drawMenuBackground(g);
            drawChoiceButtons(g);
        }
    }

    private void drawButtons(Graphics g) {
        bMenu.draw(g);
        bReset.draw(g);
    }

    private void drawChoiceButtons(Graphics g) {
        bChooseWhite.draw(g);
        bChooseBlack.draw(g);
    }

    private void chooseIDs(int x, int y) {
        if (bChooseWhite.getBounds().contains(x, y)) {
            playerID = W;
            aiID = B;
            idIsChosen = true;
        } else if (bChooseBlack.getBounds().contains(x, y)) {
            playerID = B;
            aiID = W;
            idIsChosen = true;
        }
    }

    @Override
    protected void resetGame() {
        resetValidMovesAndActivePiece();
        piecesPositions = LevelBuild.getInitialPiecesPositions();
        gameWon = false;
        turn = BLACK_TURN;
        idIsChosen = false;
        playerID = -1;
        aiID = -1;
    }

    @Override
    protected void changeTurn() {
        if (turn == BLACK_TURN) turn = WHITE_TURN;
        else turn = BLACK_TURN;
        resetValidMovesAndActivePiece();
    }

    @Override
    protected void makeMove(int x, int y) {
        if (turn == playerID) {
            byte row = getRow(y);
            byte col = getCol(x);

            makePlayerMove(playerID, aiID, row, col);
        }
    }

    private void makeAiMove() {

        byte[][] bestState = new byte[8][];

        for (byte i = 0; i < 8; i++) {
            bestState[i] = piecesPositions[i].clone();
        }


        if (aiID == W) {
            short bestScore = Short.MAX_VALUE;
            List<byte[][]> immediateStates = getAllImmediateStates(piecesPositions, false);

            for (byte[][] state : immediateStates) {
                short moveScore = miniMax(state, MAX_DEPTH, true, true);
                if (moveScore < bestScore) {
                    bestState = state;
                    bestScore = moveScore;
                }
            }
        } else {
            short bestScore = Short.MIN_VALUE;
            List<byte[][]> immediateStates = getAllImmediateStates(piecesPositions, true);
            for (byte[][] state : immediateStates) {
                short moveScore = miniMax(state, MAX_DEPTH, false, false);
                if (moveScore > bestScore) {
                    bestState = state;
                    bestScore = moveScore;
                }
            }
        }


        piecesPositions = bestState;
        checkWinningConditions();
        changeTurn();
    }


    private short miniMax(byte[][] state, byte depth, boolean isMax, boolean isBlackTurn) {
        short stateScore = evaluateState(state, !isBlackTurn);
        if (depth == 0 || (short) (Math.abs(stateScore)) == Short.MAX_VALUE) return stateScore;

        List<byte[][]> immediateStates = getAllImmediateStates(state, isBlackTurn);
        if (immediateStates.isEmpty()) return stateScore;


        if (isMax) { // this is a maximising node
            short highestScore = Short.MIN_VALUE;
            for (byte[][] immediateState : immediateStates)
                highestScore = (short) Math.max(highestScore, miniMax(immediateState, (byte) (depth - 1), false, !isBlackTurn));
            return highestScore;
        } else {
            short lowestScore = Short.MAX_VALUE;
            for (byte[][] immediateState : immediateStates)
                lowestScore = (short) Math.min(lowestScore, miniMax(immediateState, (byte) (depth - 1), true, !isBlackTurn));
            return lowestScore;
        }
    }

    // TODO: fix who wins when move connects both white and black
    //       check it by hand someday
    private short evaluateState(byte[][] state, boolean isBlackTurn) {

        short specialWin = specialWinningCondition(state,! isBlackTurn);
        if (specialWin != -1) {
            return specialWin;
        }

        return (short) (piecesPositionsScore(state, isBlackTurn) - getArea(state, isBlackTurn));
    }

    private short specialWinningCondition(byte[][] state, boolean isBlackTurn) {
        short BlackWinningState = winningState(state, B);
        short WhiteWinningState = winningState(state, W);
        if (!isBlackTurn) {
            if (WhiteWinningState == Short.MIN_VALUE && BlackWinningState == Short.MAX_VALUE) {
                return Short.MAX_VALUE;
            }
        } else {
            if (WhiteWinningState == Short.MIN_VALUE && BlackWinningState == Short.MAX_VALUE) {
                return Short.MIN_VALUE;
            }
        }

        if (BlackWinningState == Short.MAX_VALUE) {
            return BlackWinningState;
        }

        if (WhiteWinningState == Short.MIN_VALUE) {
            return WhiteWinningState;
        }

        return -1;
    }

    private short winningState(byte[][] state, byte color) {
        if (color == B && allPiecesConnected(state, color, getFirstPiece(color)))
            return Short.MAX_VALUE;

        if (color == W && allPiecesConnected(state, color, getFirstPiece(color)))
            return Short.MIN_VALUE;

        return -1;
    }

    private short piecesPositionsScore(byte[][] state, boolean isBlackTurn) {
        short score = 0;
        if (isBlackTurn) {
            for (byte y = 0; y < 8; y++) {
                for (byte x = 0; x < 8; x++) {
                    if (state[y][x] == B) score += (LevelBuild.positionsScore[y][x]);
                }
            }
        } else {
            for (byte y = 0; y < 8; y++) {
                for (byte x = 0; x < 8; x++) {
                    if (state[y][x] == W) score -= (LevelBuild.positionsScore[y][x]);
                }
            }
        }

        return score;
    }

    private byte getArea(byte[][] state, boolean isBlackTurn) {
        byte color = isBlackTurn ? B : W;

        byte height = (byte) (findBottomMostY(state, color) - findTopMostY(state, color));
        byte width = (byte) (findRightMostX(state, color) - findLeftMostX(state, color));

        return color == B ? (byte) (height * width) : (byte) (-height * width);
    }

    private byte findLeftMostX(byte[][] state, byte color) {

        for (byte column = 0; column < 8; column++) {
            for (byte row = 7; row >= 0; row--) {
                if (state[row][column] == color) {
                    return column;
                }
            }
        }
        return -1;
    }

    private byte findRightMostX(byte[][] state, byte color) {


        for (byte column = 7; column >= 0; column--) {
            for (byte row = 7; row >= 0; row--) {
                if (state[row][column] == color) {
                    return column;
                }
            }
        }
        return -1;
    }

    private byte findBottomMostY(byte[][] state, byte color) {

        for (byte row = 7; row >= 0; row--) {
            for (byte column = 7; column >= 0; column--) {
                if (state[row][column] == color) {
                    return row;
                }
            }
        }
        return -1;
    }

    private byte findTopMostY(byte[][] state, byte color) {

        for (byte row = 0; row < 8; row++) {
            for (byte column = 0; column < 8; column++) {
                if (state[row][column] == color) {
                    return row;
                }
            }
        }
        return -1;
    }


    private List<byte[][]> getAllImmediateStates(byte[][] state, boolean isBlackMove) {
        ArrayList<byte[][]> states = new ArrayList<>();
        for (byte y = 0; y < 8; y++) {
            for (byte x = 0; x < 8; x++) {
                if (isBlackMove && state[y][x] == B)
                    states.addAll(getAllImmediateStatesFor(state, y, x, W, B));
                else if (!isBlackMove && state[y][x] == W)
                    states.addAll(getAllImmediateStatesFor(state, y, x, B, W));
            }
        }

        return states;
    }

    private ArrayList<byte[][]> getAllImmediateStatesFor(byte[][] state, byte row, byte col, byte opponentPiece, byte playerPiece) {
        ArrayList<Point> validPositions = getValidMoves(state, row, col, opponentPiece, playerPiece);
        ArrayList<byte[][]> immediateStates = new ArrayList<>();

        for (Point p : validPositions) {
            byte[][] nextState = new byte[8][];
            for (byte i = 0; i < 8; i++)
                nextState[i] = state[i].clone();

            nextState[row][col] = E;
            nextState[p.getRow()][p.getCol()] = playerPiece;
            immediateStates.add(nextState);
        }
        return immediateStates;
    }


    @Override
    public void mouseClicked(int x, int y) {
        //choose IDs
        if (!idIsChosen) {
            chooseIDs(x, y);
            return;
        }


        // check if clicked on Menu or Reset
        if (bMenu.getBounds().contains(x, y)) {
            resetGame();
            SetGameState(MENU);
            return;
        } else if (bReset.getBounds().contains(x, y)) {
            resetGame();
            return;
        }


        //game logic
        if (gameWon) {
            for (Long time : differences)
                System.out.println(time);
            return;
        }
        makeMove(x, y);
    }

    @Override
    public void mouseMoved(int x, int y) {
        bChooseBlack.setMouseOver(false);
        bChooseWhite.setMouseOver(false);
        bMenu.setMouseOver(false);
        bReset.setMouseOver(false);

        if (bChooseBlack.getBounds().contains(x, y))
            bChooseBlack.setMouseOver(true);
        else if (bChooseWhite.getBounds().contains(x, y))
            bChooseWhite.setMouseOver(true);
        else if (bMenu.getBounds().contains(x, y))
            bMenu.setMouseOver(true);
        else if (bReset.getBounds().contains(x, y))
            bReset.setMouseOver(true);
    }

    @Override
    public void mousePressed(int x, int y) {
        if (bChooseBlack.getBounds().contains(x, y))
            bChooseBlack.setMousePressed(true);
        else if (bChooseWhite.getBounds().contains(x, y))
            bChooseWhite.setMousePressed(true);
        else if (bMenu.getBounds().contains(x, y))
            bMenu.setMousePressed(true);
        else if (bReset.getBounds().contains(x, y))
            bReset.setMousePressed(true);
    }

    @Override
    public void mouseReleased(int x, int y) {
        resetButtons();
    }

    private void resetButtons() {
        bChooseBlack.resetBooleans();
        bChooseWhite.resetBooleans();
        bMenu.resetBooleans();
        bReset.resetBooleans();
    }
}
