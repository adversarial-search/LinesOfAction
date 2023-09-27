package scenes;

import assistants.LevelBuild;
import main.Game;
import objects.Point;
import ui.MyButton;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import static main.GameStates.MENU;
import static main.GameStates.SetGameState;

public class PlayingAgainstAI extends GameScene implements SceneMethods {
    //  TODO fix array clone
    //  TODO try make global opponents piece and player piece instead of figuring it out in every function
    private static final byte MAX_DEPTH_BASIC = 3;
    private static final byte MAX_DEPTH_ALPHA_BETA = 3;
    private static boolean idIsChosen = false;
    private static byte playerID, aiID;
    Game game;
    private MyButton bChooseWhite, bChooseBlack, bReset, bMenu;
    public static int statesEvaluated = 0;


    public PlayingAgainstAI(Game game) {
        super(game);
        this.game = game;
        initButtons();
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
        //makeBasicMiniMaxMove ();
        makeAlphaBetaMiniMaxMove ();

        checkWinningConditions();
        changeTurn();
        System.out.println ( "Number of states evaluated: " + statesEvaluated );
        statesEvaluated=0;
    }
    private void makeAlphaBetaMiniMaxMove(){
        byte[][] bestState = new byte[8][];

        for(byte i=0; i<8; i++) bestState[i] = piecesPositions[i].clone ();

        if (aiID == W) {
            short bestScore = Short.MAX_VALUE;
            List<byte[][]> immediateStates = getAllImmediateStates(piecesPositions, false);

            for (byte[][] state : immediateStates) {
                short moveScore = miniMax(state, MAX_DEPTH_ALPHA_BETA, Short.MIN_VALUE, Short.MAX_VALUE, true, true);
                if (moveScore < bestScore) {
                    bestState = state;
                    bestScore = moveScore;
                }
            }
        } else {
            short bestScore = Short.MIN_VALUE;
            List<byte[][]> immediateStates = getAllImmediateStates(piecesPositions, true);
            for (byte[][] state : immediateStates) {
                short moveScore = miniMax(state, MAX_DEPTH_ALPHA_BETA, Short.MIN_VALUE, Short.MAX_VALUE, false, false);
                if (moveScore > bestScore) {
                    bestState = state;
                    bestScore = moveScore;
                }
            }
        }

        piecesPositions = bestState;
    }
    private short miniMax(byte[][] state, byte depth, short alpha, short beta, boolean isMax, boolean isBlackTurn){
        byte playerPiece = isBlackTurn ? B : W;
        byte opponentPiece = isBlackTurn ? W : B;

        short stateScore = evaluateState ( state, !isBlackTurn );

        if(depth == 0 || (short) (Math.abs(stateScore)) == Short.MAX_VALUE) return stateScore;

        List<Point> points = getAllPiecesOfColor ( state, playerPiece );

        byte[][] nextState;
        if(isMax){
            short highestScore = Short.MIN_VALUE;

            for(Point p: points){
                Point positionToMoveTo;

                //horizontal
                {
                    positionToMoveTo = getLeftHorizontalMove ( state, p.getRow ( ), p.getCol ( ), opponentPiece, playerPiece );
                    if (positionToMoveTo != null) {
                        nextState = getStateFromMove ( state, p, positionToMoveTo );

                        highestScore = (short) Math.max ( highestScore, miniMax ( nextState, (byte) (depth - 1), alpha, beta, false, !isBlackTurn ) );

                        alpha = (short) Math.max ( alpha, highestScore );
                        if (alpha >= beta) return highestScore;

                    }

                    positionToMoveTo = getRightHorizontalMove ( state, p.getRow ( ), p.getCol ( ), opponentPiece, playerPiece );
                    if (positionToMoveTo != null) {
                        nextState = getStateFromMove ( state, p, positionToMoveTo );

                        highestScore = (short) Math.max ( highestScore, miniMax ( nextState, (byte) (depth - 1), alpha, beta, false, !isBlackTurn ) );

                        alpha = (short) Math.max ( alpha, highestScore );
                        if (alpha >= beta) return highestScore;

                    }
                }

                //vertical
                {
                    positionToMoveTo = getUpVerticalMove ( state, p.getRow (), p.getCol (), opponentPiece, playerPiece );
                    if(positionToMoveTo != null) {
                        nextState = getStateFromMove ( state, p, positionToMoveTo );

                        highestScore = (short) Math.max(highestScore, miniMax(nextState, (byte)(depth - 1), alpha, beta, false, !isBlackTurn));

                        alpha = (short) Math.max(alpha, highestScore);
                        if(alpha >= beta) return highestScore;

                    }

                    positionToMoveTo = getDownVerticalMove ( state, p.getRow (), p.getCol (), opponentPiece, playerPiece );
                    if(positionToMoveTo != null) {
                        nextState = getStateFromMove ( state, p, positionToMoveTo );

                        highestScore = (short) Math.max(highestScore, miniMax(nextState, (byte)(depth - 1), alpha, beta, false, !isBlackTurn));

                        alpha = (short) Math.max(alpha, highestScore);
                        if(alpha >= beta) return highestScore;

                    }
                }

                //main diagonal
                {
                    positionToMoveTo = getDownMainDiagonalMove ( state, p.getRow (), p.getCol (), opponentPiece, playerPiece );
                    if(positionToMoveTo != null) {
                        nextState = getStateFromMove ( state, p, positionToMoveTo );

                        highestScore = (short) Math.max(highestScore, miniMax(nextState, (byte)(depth - 1), alpha, beta, false, !isBlackTurn));

                        alpha = (short) Math.max(alpha, highestScore);
                        if(alpha >= beta) return highestScore;

                    }
                    positionToMoveTo = getUpMainDiagonalMove ( state, p.getRow (), p.getCol (), opponentPiece, playerPiece );
                    if(positionToMoveTo != null) {
                        nextState = getStateFromMove ( state, p, positionToMoveTo );

                        highestScore = (short) Math.max(highestScore, miniMax(nextState, (byte)(depth - 1), alpha, beta, false, !isBlackTurn));

                        alpha = (short) Math.max(alpha, highestScore);
                        if(alpha >= beta) return highestScore;

                    }
                }

                //anti diagonal
                {
                    positionToMoveTo = getUpAntiDiagonalMove ( state, p.getRow (), p.getCol (), opponentPiece, playerPiece );
                    if(positionToMoveTo != null) {
                        nextState = getStateFromMove ( state, p, positionToMoveTo );

                        highestScore = (short) Math.max(highestScore, miniMax(nextState, (byte)(depth - 1), alpha, beta, false, !isBlackTurn));

                        alpha = (short) Math.max(alpha, highestScore);
                        if(alpha >= beta) return highestScore;

                    }
                    positionToMoveTo = getDownAntiDiagonalMove ( state, p.getRow (), p.getCol (), opponentPiece, playerPiece );
                    if(positionToMoveTo != null) {
                        nextState = getStateFromMove ( state, p, positionToMoveTo );

                        highestScore = (short) Math.max(highestScore, miniMax(nextState, (byte)(depth - 1), alpha, beta, false, !isBlackTurn));

                        alpha = (short) Math.max(alpha, highestScore);
                        if(alpha >= beta) return highestScore;

                    }
                }
            }

            return highestScore;
        }else{
            short lowestScore = Short.MAX_VALUE;

            for(Point p: points){
                Point positionToMoveTo;

                //horizontal
                {
                    positionToMoveTo = getLeftHorizontalMove ( state, p.getRow ( ), p.getCol ( ), opponentPiece, playerPiece );
                    if (positionToMoveTo != null) {
                        nextState = getStateFromMove ( state, p, positionToMoveTo );

                        lowestScore = (short) Math.min ( lowestScore, miniMax ( nextState, (byte) (depth - 1), alpha, beta, true, !isBlackTurn ) );

                        beta = (short) Math.min ( beta, lowestScore );
                        if (alpha >= beta) return lowestScore;
                    }

                    positionToMoveTo = getRightHorizontalMove ( state, p.getRow ( ), p.getCol ( ), opponentPiece, playerPiece );
                    if (positionToMoveTo != null) {
                        nextState = getStateFromMove ( state, p, positionToMoveTo );

                        lowestScore = (short) Math.min ( lowestScore, miniMax ( nextState, (byte) (depth - 1), alpha, beta, true, !isBlackTurn ) );

                        beta = (short) Math.min ( beta, lowestScore );
                        if (alpha >= beta) return lowestScore;
                    }
                }

                //vertical
                {
                    positionToMoveTo = getUpVerticalMove ( state, p.getRow (), p.getCol (), opponentPiece, playerPiece );
                    if(positionToMoveTo != null) {
                        nextState = getStateFromMove ( state, p, positionToMoveTo );

                        lowestScore = (short) Math.min ( lowestScore, miniMax ( nextState, (byte) (depth - 1), alpha, beta, true, !isBlackTurn ) );

                        beta = (short) Math.min ( beta, lowestScore );
                        if (alpha >= beta) return lowestScore;
                    }

                    positionToMoveTo = getDownVerticalMove ( state, p.getRow (), p.getCol (), opponentPiece, playerPiece );
                    if(positionToMoveTo != null) {
                        nextState = getStateFromMove ( state, p, positionToMoveTo );

                        lowestScore = (short) Math.min ( lowestScore, miniMax ( nextState, (byte) (depth - 1), alpha, beta, true, !isBlackTurn ) );

                        beta = (short) Math.min ( beta, lowestScore );
                        if (alpha >= beta) return lowestScore;
                    }
                }

                //main diagonal
                {
                    positionToMoveTo = getDownMainDiagonalMove ( state, p.getRow (), p.getCol (), opponentPiece, playerPiece );
                    if(positionToMoveTo != null) {
                        nextState = getStateFromMove ( state, p, positionToMoveTo );

                        lowestScore = (short) Math.min ( lowestScore, miniMax ( nextState, (byte) (depth - 1), alpha, beta, true, !isBlackTurn ) );

                        beta = (short) Math.min ( beta, lowestScore );
                        if (alpha >= beta) return lowestScore;
                    }
                    positionToMoveTo = getUpMainDiagonalMove ( state, p.getRow (), p.getCol (), opponentPiece, playerPiece );
                    if(positionToMoveTo != null) {
                        nextState = getStateFromMove ( state, p, positionToMoveTo );

                        lowestScore = (short) Math.min ( lowestScore, miniMax ( nextState, (byte) (depth - 1), alpha, beta, true, !isBlackTurn ) );

                        beta = (short) Math.min ( beta, lowestScore );
                        if (alpha >= beta) return lowestScore;
                    }
                }

                //anti diagonal
                {
                    positionToMoveTo = getUpAntiDiagonalMove ( state, p.getRow (), p.getCol (), opponentPiece, playerPiece );
                    if(positionToMoveTo != null) {
                        nextState = getStateFromMove ( state, p, positionToMoveTo );

                        lowestScore = (short) Math.min ( lowestScore, miniMax ( nextState, (byte) (depth - 1), alpha, beta, true, !isBlackTurn ) );

                        beta = (short) Math.min ( beta, lowestScore );
                        if (alpha >= beta) return lowestScore;
                    }
                    positionToMoveTo = getDownAntiDiagonalMove ( state, p.getRow (), p.getCol (), opponentPiece, playerPiece );
                    if(positionToMoveTo != null) {
                        nextState = getStateFromMove ( state, p, positionToMoveTo );

                        lowestScore = (short) Math.min ( lowestScore, miniMax ( nextState, (byte) (depth - 1), alpha, beta, true, !isBlackTurn ) );

                        beta = (short) Math.min ( beta, lowestScore );
                        if (alpha >= beta) return lowestScore;
                    }
                }
            }

            return lowestScore;
        }

    }


    private void makeBasicMiniMaxMove(){
        byte[][] bestState = new byte[8][];

        for (byte i = 0; i < 8; i++) {
            bestState[i] = piecesPositions[i].clone();
        }


        if (aiID == W) {
            short bestScore = Short.MAX_VALUE;
            List<byte[][]> immediateStates = getAllImmediateStates(piecesPositions, false);

            for (byte[][] state : immediateStates) {
                short moveScore = miniMax(state, MAX_DEPTH_BASIC, true, true);
                if (moveScore < bestScore) {
                    bestState = state;
                    bestScore = moveScore;
                }
            }
        } else {
            short bestScore = Short.MIN_VALUE;
            List<byte[][]> immediateStates = getAllImmediateStates(piecesPositions, true);
            for (byte[][] state : immediateStates) {
                short moveScore = miniMax(state, MAX_DEPTH_BASIC, false, false);
                if (moveScore > bestScore) {
                    bestState = state;
                    bestScore = moveScore;
                }
            }
        }


        piecesPositions = bestState;
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



    private short evaluateState(byte[][] state, boolean isBlackTurn) {
        statesEvaluated+=1;
        //quirky winning case - don't ask
        {
            short specialWin = specialWinningCondition(state, isBlackTurn);
            if (specialWin != 0) return specialWin;
        }

        short score = (short) (
                piecesPositionsScore(state, isBlackTurn)
                        - getArea(state, isBlackTurn)
                        + 5*countEnemyPieces ( state, isBlackTurn )
                        - 0.3*numberOfOpponentsMoves(state,isBlackTurn)
        );

        return score;
    }
    private short specialWinningCondition(byte[][] state, boolean isBlackTurn) {
        boolean blackWins = isWinningState(state, B);
        boolean whiteWins = isWinningState(state, W);


        if(blackWins && whiteWins) return isBlackTurn ? Short.MAX_VALUE : Short.MIN_VALUE;


        if(blackWins) return Short.MAX_VALUE;
        if(whiteWins) return Short.MIN_VALUE;


        return 0;
    }
    private boolean isWinningState(byte[][] state, byte color) {
        return allPiecesConnected ( state, color, getFirstPiece ( state, color ) );
    }
    private short piecesPositionsScore(byte[][] state, boolean isBlackTurn) {
        byte color = isBlackTurn ? B : W;
        short score = 0;

        for (byte y = 0; y < 8; y++)
            for (byte x = 0; x < 8; x++)
                if (state[y][x] == color) score += (LevelBuild.positionsScore[y][x]);

        return isBlackTurn ? score : (byte)(-1*score);
    }
    private short numberOfOpponentsMoves(byte[][] state, boolean isBlackTurn){
        byte opponentColor = isBlackTurn?W:B;
        byte playerColor = isBlackTurn?B:W;

       short numberOfPossibleNextPositionsForOpponent = 0;

       List<Point> enemyPieces = getAllPiecesOfColor(state,opponentColor);

        for(Point currentPiece:enemyPieces){
            numberOfPossibleNextPositionsForOpponent += getValidMoves(
                                                                        state,
                                                                        currentPiece.getRow(),
                                                                        currentPiece.getCol(),
                                                                        opponentColor,
                                                                        playerColor
                                                                ).size();
        }

        return isBlackTurn ? numberOfPossibleNextPositionsForOpponent : (short) (-1 * numberOfPossibleNextPositionsForOpponent);
    }
    private List<Point> getAllPiecesOfColor(byte[][] state, byte color){
        List<Point> returnList = new ArrayList<>();

        for(byte row = 0; row<8; row++){
            for(byte column = 0; column<8; column++){
                if(state[row][column]==color){
                    returnList.add(new Point(row,column));
                }
            }
        }

        return returnList;
    }
    private byte getArea(byte[][] state, boolean isBlackTurn) {
        byte color = isBlackTurn ? B : W;

        byte height = (byte) (findBottomMostY(state, color) - findTopMostY(state, color));
        byte width = (byte) (findRightMostX(state, color) - findLeftMostX(state, color));

        return isBlackTurn? (byte) (height * width) : (byte) (-height * width);
    }
    private byte findLeftMostX(byte[][] state, byte color) {

        for (byte col = 0; col < 8; col++) {
            for (byte row = 7; row >= 0; row--) {
                if (state[row][col] == color) {
                    return col;
                }
            }
        }
        return -1;
    }
    private byte findRightMostX(byte[][] state, byte color) {


        for (byte col = 7; col >= 0; col--) {
            for (byte row = 7; row >= 0; row--) {
                if (state[row][col] == color) {
                    return col;
                }
            }
        }
        return -1;
    }
    private byte findBottomMostY(byte[][] state, byte color) {

        for (byte row = 7; row >= 0; row--) {
            for (byte col = 7; col >= 0; col--) {
                if (state[row][col] == color) {
                    return row;
                }
            }
        }
        return -1;
    }
    private byte findTopMostY(byte[][] state, byte color) {

        for (byte row = 0; row < 8; row++) {
            for (byte col = 0; col < 8; col++) {
                if (state[row][col] == color) {
                    return row;
                }
            }
        }
        return -1;
    }
    private byte countEnemyPieces(byte[][] state, boolean isBlackTurn){
        byte colorToCount = isBlackTurn ? W : B;
        byte colorCount = 0;
        for (byte row = 0; row < 8; row++) 
            for (byte col = 0; col < 8; col++)
                if(state[row][col] == colorToCount)
                    colorCount++;

        return isBlackTurn ? colorCount : (byte)(-1*colorCount);
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