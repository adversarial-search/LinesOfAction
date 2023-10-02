package scenes;

import assistants.LevelBuild;
import main.Game;
import objects.Point;
import objects.StateGenerationDTO;
import ui.MyButton;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import static main.GameStates.MENU;
import static main.GameStates.SetGameState;
// TODO : pull some of the duplicate code up both in AIvAI and PvAI
// TODO : debug the clicking issues
public class AIAgainstAI extends GameScene implements SceneMethods{
    private static final byte MAX_DEPTH_BASIC = 2;
    private static final byte MAX_DEPTH_ALPHA_BETA = 2;
    protected static boolean whiteChosen = false;
    protected static boolean blackChosen = false;
    protected byte whiteAiType;
    protected byte blackAiType;
    private static final Random random = new Random ( );

    private MyButton
            bReset,
            bMenu,
            bChooseNoPruningWhite,
            bChooseAlphaBetaWhite,
            bChooseNoPruningBlack,
            bChooseAlphaBetaBlack,
            bNextMove;


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
        bMenu = new MyButton("Menu", 14, 12, 100, 40);
        bReset = new MyButton("Reset", 128, 12, 100, 40);
        // TODO: Think of a better looking design for the next button
        bNextMove = new MyButton(">>",580,650,50,40);
    }

    private void makeAiMove(byte currentAiId) {
        byte aiType = currentAiId==W?whiteAiType:blackAiType;

        switch (aiType) {
            case CLASSIC_MINMAX -> makeBasicMiniMaxMove(currentAiId);
            case PRUNING_MINMAX -> makeAlphaBetaMiniMaxMove(currentAiId);
        }

        checkWinningConditions();
        changeTurn();
    }
    private void makeAlphaBetaMiniMaxMove(byte aiID){
        ArrayList<byte[][]> bestMoves = new ArrayList<> (  );

        if (aiID == W) {
            short bestScore = Short.MAX_VALUE;
            java.util.List<byte[][]> immediateStates = getAllImmediateStates(piecesPositions, false);

            for (byte[][] state : immediateStates) {
                short moveScore = miniMax(state, MAX_DEPTH_ALPHA_BETA, Short.MIN_VALUE, Short.MAX_VALUE, true, true);
                if (moveScore < bestScore) {
                    bestScore = moveScore;

                    bestMoves = new ArrayList<> (  );
                    bestMoves.add ( state );
                }else if(moveScore == bestScore)
                    bestMoves.add ( state );
            }
        } else {
            short bestScore = Short.MIN_VALUE;
            List<byte[][]> immediateStates = getAllImmediateStates(piecesPositions, true);
            for (byte[][] state : immediateStates) {
                short moveScore = miniMax(state, MAX_DEPTH_ALPHA_BETA, Short.MIN_VALUE, Short.MAX_VALUE, false, false);
                if (moveScore > bestScore) {
                    bestScore = moveScore;

                    bestMoves = new ArrayList<> (  );
                    bestMoves.add ( state );
                }else if(moveScore == bestScore)
                    bestMoves.add ( state );
            }
        }

        System.out.println ( bestMoves.size () );
        piecesPositions = bestMoves.get(random.nextInt ( bestMoves.size () ));
    }
    private short miniMax(byte[][] state, byte depth, short alpha, short beta, boolean isMax, boolean isBlackTurn){
        byte playerPiece = isBlackTurn ? B : W;
        byte opponentPiece = isBlackTurn ? W : B;

        short stateScore = evaluateState ( state, !isBlackTurn );

        if(depth == 0 || (short) (Math.abs(stateScore)) == Short.MAX_VALUE) return stateScore;

        List<objects.Point> points = getAllPiecesOfColor ( state, playerPiece );
        List<Function<StateGenerationDTO, objects.Point>> movementFunctions = getMovementFunctions();
        byte[][] nextState;
        if(isMax) {
            short highestScore = Short.MIN_VALUE;

            for (objects.Point p : points) {
                objects.Point positionToMoveTo;
                StateGenerationDTO currentStateDTO = new StateGenerationDTO(state, p.getRow(), p.getCol(), opponentPiece, playerPiece);
                for (Function<StateGenerationDTO, objects.Point> function : movementFunctions) {
                    positionToMoveTo = function.apply(currentStateDTO);
                    if (positionToMoveTo != null) {
                        nextState = getStateFromMove(state, p, positionToMoveTo);

                        highestScore = (short) Math.max(highestScore, miniMax(nextState, (byte) (depth - 1), alpha, beta, false, !isBlackTurn));

                        alpha = (short) Math.max(alpha, highestScore);
                        if (alpha >= beta) return highestScore;

                    }
                }
            }
            return highestScore;
        }
        else{
            short lowestScore = Short.MAX_VALUE;

            for(objects.Point p: points) {
                objects.Point positionToMoveTo;
                StateGenerationDTO currentStateDTO = new StateGenerationDTO(state, p.getRow(), p.getCol(), opponentPiece, playerPiece);
                for (Function<StateGenerationDTO, Point> function : movementFunctions) {
                    positionToMoveTo = function.apply(currentStateDTO);
                    if (positionToMoveTo != null) {
                        nextState = getStateFromMove(state, p, positionToMoveTo);

                        lowestScore = (short) Math.min(lowestScore, miniMax(nextState, (byte) (depth - 1), alpha, beta, true, !isBlackTurn));

                        beta = (short) Math.min(beta, lowestScore);
                        if (alpha >= beta) return lowestScore;
                    }
                }
            }

            return lowestScore;
        }

    }


    private void makeBasicMiniMaxMove(byte aiID){
        ArrayList<byte[][]> bestMoves = new ArrayList<> (  );

        if (aiID == W) {
            short bestScore = Short.MAX_VALUE;
            List<byte[][]> immediateStates = getAllImmediateStates(piecesPositions, false);

            for (byte[][] state : immediateStates) {
                short moveScore = miniMax(state, MAX_DEPTH_BASIC, true, true);
                if (moveScore < bestScore) {
                    bestScore = moveScore;

                    bestMoves = new ArrayList<> (  );
                    bestMoves.add ( state );
                }else if(moveScore == bestScore){
                    bestMoves.add ( state );
                }
            }
        } else {
            short bestScore = Short.MIN_VALUE;
            List<byte[][]> immediateStates = getAllImmediateStates(piecesPositions, true);
            for (byte[][] state : immediateStates) {
                short moveScore = miniMax(state, MAX_DEPTH_BASIC, false, false);
                if (moveScore > bestScore) {
                    bestScore = moveScore;

                    bestMoves = new ArrayList<> (  );
                    bestMoves.add ( state );
                }else if(moveScore == bestScore){
                    bestMoves.add ( state );
                }
            }
        }
        System.out.println ( bestMoves.size () );
        piecesPositions = bestMoves.get(random.nextInt ( bestMoves.size () ));
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
        //quirky winning case - don't ask
        {
            short specialWin = specialWinningCondition(state, isBlackTurn);
            if (specialWin != 0) return specialWin;
        }

        short score = (short) (
                piecesPositionsScore(state, isBlackTurn)
                        - getArea(state, isBlackTurn)
                        - 15*countEnemyPieces ( state, isBlackTurn ) //?????
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

        return isBlackTurn ? LevelBuild.numPiecesScore[colorCount-1] : (byte)(-1*LevelBuild.numPiecesScore[colorCount-1]);
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
    protected void resetGame() {
        resetValidMovesAndActivePiece();
        piecesPositions = LevelBuild.getInitialPiecesPositions();
        gameWon = false;
        turn = BLACK_TURN;
        blackChosen=false;
        whiteChosen=false;
    }

    @Override
    protected void changeTurn() {
        if (turn == BLACK_TURN) turn = WHITE_TURN;
        else turn = BLACK_TURN;
    }

    @Override
    protected void makeMove(int x, int y) {

    }

    @Override
    public void render(Graphics g) {

        if(whiteChosen&&blackChosen){
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
        } else {
            if(!blackChosen) {
                drawMenuBackground(g);
                drawChooseAiTypeButtonsBlack(g);
            } else{
                drawMenuBackground(g);
                drawChooseAiTypeButtonsWhite(g);
            }
        }
    }
    private void drawButtons(Graphics g) {
        bMenu.draw(g);
        bReset.draw(g);
        bNextMove.draw(g);
    }
    private void drawChooseAiTypeButtonsWhite(Graphics g){
        bChooseAlphaBetaWhite.draw(g);
        bChooseNoPruningWhite.draw(g);
    }
    private void drawChooseAiTypeButtonsBlack(Graphics g){
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
        else if (bChooseNoPruningWhite.getBounds().contains(x, y))
            bChooseNoPruningWhite.setMouseOver(true);
        else if (bChooseAlphaBetaWhite.getBounds().contains(x, y))
            bChooseAlphaBetaWhite.setMouseOver(true);
        else if (bChooseNoPruningBlack.getBounds().contains(x, y))
            bChooseNoPruningBlack.setMouseOver(true);
        else if (bChooseAlphaBetaBlack.getBounds().contains(x, y))
            bChooseAlphaBetaBlack.setMouseOver(true);

    }

    @Override
    public void mouseClicked(int x, int y) {

        if (!blackChosen) {
            chooseBlackAiType(x, y);
            return;
        }else if(!whiteChosen){
            chooseWhiteAiType(x,y);
            return;
        }


        // check if clicked management buttons
        if (bMenu.getBounds().contains(x, y)) {
            resetGame();
            SetGameState(MENU);
            return;
        } else if (bReset.getBounds().contains(x, y)) {
            resetGame();
            return;
        }
        else if(bNextMove.getBounds().contains(x,y)&&!gameWon){
            makeNextMove();
            return;
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
        else if (bChooseNoPruningBlack.getBounds().contains(x, y))
            bChooseNoPruningBlack.setMousePressed(true);
        else if (bChooseAlphaBetaBlack.getBounds().contains(x, y))
            bChooseAlphaBetaBlack.setMousePressed(true);
        else if (bNextMove.getBounds().contains(x, y))
            bNextMove.setMousePressed(true);

    }

    @Override
    public void mouseReleased(int x, int y) {
        resetButtons();
    }
    private void chooseWhiteAiType(int x, int y){
        if(bChooseAlphaBetaWhite.getBounds().contains(x,y)){
            whiteAiType=PRUNING_MINMAX;
            whiteChosen=true;
        }
        else if(bChooseNoPruningWhite.getBounds().contains(x,y)){
            whiteAiType=CLASSIC_MINMAX;
            whiteChosen=true;
        }
    }

    private void chooseBlackAiType(int x, int y){
        if(bChooseAlphaBetaBlack.getBounds().contains(x,y)){
            blackAiType=PRUNING_MINMAX;
            blackChosen=true;
        }
        else if(bChooseNoPruningBlack.getBounds().contains(x,y)){
            blackAiType=CLASSIC_MINMAX;
            blackChosen=true;
        }
    }

    private void makeNextMove(){
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
