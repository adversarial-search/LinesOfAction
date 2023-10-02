package scenes;

import assistants.Heuristics;
import assistants.LevelBuild;
import main.Game;
import objects.Point;
import objects.StateGenerationDTO;
import ui.MyButton;
import java.util.Random;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static main.GameStates.MENU;
import static main.GameStates.SetGameState;

public class PlayingAgainstAI extends GameScene implements SceneMethods {
    //  TODO try make global opponents piece and player piece instead of figuring it out in every function
    private static byte MAX_DEPTH_BASIC = 0;
    private static byte MAX_DEPTH_ALPHA_BETA = 0;
    private static boolean idIsChosen = false;
    private static boolean aiTypeIsChosen = false;
    private static byte playerID, aiID, aiType;
    public static int statesEvaluated = 0, showNumStatesEvaluated;
    private static final Random random = new Random ( );
    Game game;
    private MyButton
            bChooseWhite,
            bChooseBlack,
            bReset,
            bMenu,
            bChooseNoPruning,
            bChooseAlphaBeta,
            bIncreaseDepth,
            bDecreaseDepth;



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
        bChooseNoPruning = new MyButton("Simple MinMax", 192, 128, 256, 128);
        bChooseAlphaBeta = new MyButton("Alpha Beta MinMax", 192, 416, 256, 128);
        bMenu = new MyButton("Menu", 14, 12, 100, 40);
        bReset = new MyButton("Reset", 128, 12, 100, 40);
        bIncreaseDepth = new MyButton ( "++Depth", 242, 37, 100, 25 );
        bDecreaseDepth = new MyButton ( "--Depth", 242, 6, 100, 25 );
    }
    @Override
    public void render(Graphics g) {
        //draw buttons
        if (idIsChosen && aiTypeIsChosen) {
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

            //draw depth management buttons
            drawDepthButtons(g);

            //display current depth
            displayDepthAndNumStates(g);
            if (turn == aiID) makeAiMove();
        } else {
            if(!idIsChosen) {
                drawMenuBackground(g);
                drawChooseSideButtons(g);
            } else {
                drawMenuBackground(g);
                drawChooseAiTypeButtons(g);
            }
        }
    }

    private void displayDepthAndNumStates ( Graphics g ) {
        if(!gameWon) {
            g.setColor ( new Color ( 168, 212, 190 ) );
            g.fillRect ( 356, 12, 40, 40 );
            g.setColor ( new Color ( 88, 69, 47 ) );
            g.drawRect ( 356, 12, 40, 40 );

            String depthStr = aiType == PRUNING_MINMAX ? String.valueOf ( MAX_DEPTH_ALPHA_BETA ) : String.valueOf ( MAX_DEPTH_BASIC );
            g.drawString ( depthStr, 372, 37 );

            g.setColor ( new Color ( 168, 212, 190 ) );
            g.fillRect ( 408, 12, 100, 40 );
            g.setColor ( new Color ( 88, 69, 47 ) );
            g.drawRect ( 408, 12, 100, 40 );
            g.drawString ( "States Evaluated", 412, 28 );
            g.drawString ( String.valueOf ( showNumStatesEvaluated ), 412, 45 );
        }
    }

    private void drawDepthButtons ( Graphics g ) {
        if(!gameWon) {
            bIncreaseDepth.draw ( g );
            bDecreaseDepth.draw ( g );
        }
    }

    private void drawButtons(Graphics g) {
        bMenu.draw(g);
        bReset.draw(g);
    }
    private void drawChooseSideButtons(Graphics g) {
        bChooseWhite.draw(g);
        bChooseBlack.draw(g);
    }
    private void drawChooseAiTypeButtons(Graphics g){
        bChooseAlphaBeta.draw(g);
        bChooseNoPruning.draw(g);
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
    private void chooseAiType(int x, int y){
        if(bChooseAlphaBeta.getBounds().contains(x,y)){
            aiType=PRUNING_MINMAX;
            aiTypeIsChosen=true;
        }
        else if(bChooseNoPruning.getBounds().contains(x,y)){
            aiType=CLASSIC_MINMAX;
            aiTypeIsChosen=true;
        }
    }
    @Override
    protected void resetGame() {
        resetValidMovesAndActivePiece();
        piecesPositions = LevelBuild.getInitialPiecesPositions();
        gameWon = false;
        turn = BLACK_TURN;
        idIsChosen = false;
        aiTypeIsChosen=false;
        playerID = -1;
        aiID = -1;
        MAX_DEPTH_BASIC = 0;
        MAX_DEPTH_ALPHA_BETA = 0;
        statesEvaluated = 0;
        showNumStatesEvaluated = 0;
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
        switch (aiType) {
            case CLASSIC_MINMAX -> makeBasicMiniMaxMove();
            case PRUNING_MINMAX -> makeAlphaBetaMiniMaxMove();
        }

        checkWinningConditions();
        changeTurn();

        showNumStatesEvaluated = statesEvaluated;
        statesEvaluated=0;
    }
    private void makeAlphaBetaMiniMaxMove(){
        ArrayList<byte[][]> bestMoves = new ArrayList<> (  );

        if (aiID == W) {
            short bestScore = Short.MAX_VALUE;
            List<byte[][]> immediateStates = getAllImmediateStates(piecesPositions, false);

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

        List<Point> points = getAllPiecesOfColor ( state, playerPiece );
        List<Function<StateGenerationDTO,Point>> movementFunctions = getMovementFunctions();
        byte[][] nextState;
        if(isMax) {
            short highestScore = Short.MIN_VALUE;

            for (Point p : points) {
                Point positionToMoveTo;
                StateGenerationDTO currentStateDTO = new StateGenerationDTO(state, p.getRow(), p.getCol(), opponentPiece, playerPiece);
                for (Function<StateGenerationDTO, Point> function : movementFunctions) {
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

            for(Point p: points) {
                Point positionToMoveTo;
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


    private void makeBasicMiniMaxMove(){
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
        statesEvaluated+=1;

        byte color = isBlackTurn ? B : W;
        //quirky winning case - don't ask
        {
            short specialWin = specialWinningCondition(state, isBlackTurn);
            if (specialWin != 0) return specialWin;
        }

        return (short) (
                          Heuristics.getScoreFromBoardPositions(state, color)
                        + Heuristics.getNumConnectedPieces(state, color)
                        - Heuristics.getArea(state, color)
                        - Heuristics.getScoreFromNumEnemyPieces ( state, color )
                        - Heuristics.numberOfOpponentsMoves(state, color)
        );
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
        } else if (!aiTypeIsChosen) {
            chooseAiType(x,y);
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
        } else if (bIncreaseDepth.getBounds ().contains ( x, y )){
            increaseDepth();
        } else if (bDecreaseDepth.getBounds ().contains ( x, y )){
            decreaseDepth();
        }


        //game logic
        makeMove(x, y);
    }
    private void increaseDepth () {
        if(aiType == PRUNING_MINMAX)
            MAX_DEPTH_ALPHA_BETA = (byte) Math.min( MAX_DEPTH_ALPHA_BETA+1, 3 );
        else if(aiType == CLASSIC_MINMAX)
            MAX_DEPTH_BASIC = (byte) Math.min ( MAX_DEPTH_BASIC+1, 3 );
    }
    private void decreaseDepth () {
        if(aiType == PRUNING_MINMAX)
            MAX_DEPTH_ALPHA_BETA = (byte) Math.max( MAX_DEPTH_ALPHA_BETA-1, 0 );
        else if(aiType == CLASSIC_MINMAX)
            MAX_DEPTH_BASIC = (byte) Math.max ( MAX_DEPTH_BASIC-1, 0 );
    }



    @Override
    public void mouseMoved(int x, int y) {
        bChooseBlack.setMouseOver(false);
        bChooseWhite.setMouseOver(false);
        bMenu.setMouseOver(false);
        bReset.setMouseOver(false);
        bChooseAlphaBeta.setMouseOver(false);
        bChooseNoPruning.setMouseOver(false);
        bIncreaseDepth.setMouseOver ( false );
        bDecreaseDepth.setMouseOver ( false );

        if (bChooseBlack.getBounds().contains(x, y))
            bChooseBlack.setMouseOver(true);
        else if (bChooseWhite.getBounds().contains(x, y))
            bChooseWhite.setMouseOver(true);
        else if (bMenu.getBounds().contains(x, y))
            bMenu.setMouseOver(true);
        else if (bReset.getBounds().contains(x, y))
            bReset.setMouseOver(true);
        else if (bChooseNoPruning.getBounds().contains(x, y))
            bChooseNoPruning.setMouseOver(true);
        else if (bChooseAlphaBeta.getBounds().contains(x, y))
            bChooseAlphaBeta.setMouseOver(true);
        else if (bIncreaseDepth.getBounds ().contains ( x, y ))
            bIncreaseDepth.setMouseOver ( true );
        else if (bDecreaseDepth.getBounds ().contains ( x, y ))
            bDecreaseDepth.setMouseOver ( true );
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
        else if (bChooseNoPruning.getBounds().contains(x, y))
            bChooseNoPruning.setMousePressed(true);
        else if (bChooseAlphaBeta.getBounds().contains(x, y))
            bChooseAlphaBeta.setMousePressed(true);
        else if (bIncreaseDepth.getBounds ().contains ( x, y ))
            bIncreaseDepth.setMousePressed ( true );
        else if (bDecreaseDepth.getBounds ().contains ( x, y ))
            bDecreaseDepth.setMousePressed ( true );
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
        bChooseNoPruning.resetBooleans();
        bChooseAlphaBeta.resetBooleans();
        bIncreaseDepth.resetBooleans ();
        bDecreaseDepth.resetBooleans ();
    }
}