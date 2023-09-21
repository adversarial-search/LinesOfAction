package scenes;

import assistants.LevelBuild;
import main.Game;
import objects.Point;
import ui.MyButton;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;

import static main.GameStates.*;

public class PlayingAgainstAI extends GameScene implements SceneMethods{
    Game game;
    private MyButton bChooseWhite, bChooseBlack, bReset, bMenu;
    private static boolean idIsChosen = false;
    private static byte playerID, aiID;
    private Random random;



    public PlayingAgainstAI ( Game game) {
        super( game );
        this.game = game;
        initButtons();
        random = new Random (  );
    }
    private void initButtons () {
        bChooseBlack = new MyButton ( "Play As Black", 192, 128, 256, 128 );
        bChooseWhite = new MyButton ( "Play As White", 192, 416, 256, 128 );
        bMenu = new MyButton ( "Menu", 14, 12, 100, 40 );
        bReset = new MyButton ( "Reset", 128, 12, 100, 40 );
    }
    public static void setUpInitialGameState(){
        piecesPositions = LevelBuild.getInitialPiecesPositions ();
        turn = BLACK_TURN;
        gameWon = false;
        winner = -1;
        idIsChosen = false;
        playerID = -1;
        aiID = -1;
    }



    @Override
    public void render ( Graphics g ) {
        //draw buttons
        if(idIsChosen) {
            //draw background
            drawBoardBackground( g );

            //draw piecesPositions
            drawPieces( g );

            //draw active piece
            drawActive( g );

            //draw valid moves
            drawValidMoves( g );

            //draw buttons
            drawButtons ( g );

            //display winner
            displayWinner ( g );
            if(turn == aiID) makeAiMove ();
        }
        else{
            drawMenuBackground( g );
            drawChoiceButtons( g );
        }
    }
    private void drawButtons ( Graphics g ) {
        bMenu.draw ( g );
        bReset.draw ( g );
    }
    private void drawChoiceButtons ( Graphics g ) {
        bChooseWhite.draw ( g );
        bChooseBlack.draw ( g );
    }
    private void chooseIDs(int x, int y){
        if (bChooseWhite.getBounds ( ).contains ( x, y )) {
            playerID = W;
            aiID = B;
            idIsChosen = true;
        } else if (bChooseBlack.getBounds ( ).contains ( x, y )) {
            playerID = B;
            aiID = W;
            idIsChosen = true;
        }
    }



    @Override
    protected void resetGame () {
        resetValidMovesAndActivePiece ();
        piecesPositions = LevelBuild.getInitialPiecesPositions ();
        gameWon = false;
        turn = BLACK_TURN;
        idIsChosen = false;
        playerID = -1;
        aiID = -1;
    }
    @Override
    protected void changeTurn () {
        if(turn == BLACK_TURN) turn = WHITE_TURN;
        else turn = BLACK_TURN;
        resetValidMovesAndActivePiece ();
    }



    @Override
    protected void makeMove ( int x, int y ) {
        if( turn == playerID ){
            byte row = getRow ( y );
            byte col = getCol ( x );

            makePlayerMove ( playerID, aiID, row, col );
        }
    }
    //  TODO fix array clone
    private static byte MAX_DEPTH = 3;
    ArrayList<Long> differences = new ArrayList<> (  );
    private int functionCalls;
    private void makeAiMove () {

        long startTime = System.currentTimeMillis ();
        System.out.println ( "Start calculation: " + startTime );
        functionCalls = 0;

        // BLACK == MAX
        // WHITE == MIN

        byte[][] bestState = new byte[8][8];
        ArrayList<byte[][]> immediateStates = getAllImmediateStates ( piecesPositions );


        short bestStateScore = turn == B ? Short.MIN_VALUE : Short.MAX_VALUE;
        for(byte[][] state: immediateStates){
            short stateScore = miniMax(state, MAX_DEPTH, turn == B);
            if( turn == B ? stateScore > bestStateScore : stateScore < bestStateScore ) {
                for (byte k = 0; k < 8; k++)
                    bestState[k] = state[k].clone ( );
                bestStateScore = stateScore;
            }
        }

        piecesPositions = bestState;
        turn = playerID;

        long endTime = System.currentTimeMillis ();
        differences.add ( endTime-startTime );

        System.out.println ( "End calculation: " + endTime );
        System.out.println ( "Time taken: " + (endTime-startTime) );
        System.out.println ( "Function calls: " + functionCalls );
    }
    private short miniMax(byte[][] state, byte depth, boolean isMax){
        short stateScore = evaluateState(state, isMax);

        if( depth == 0 || stateScore == Math.abs(Short.MAX_VALUE) || getAllImmediateStates (state).isEmpty ())
            return stateScore;

        short bestScore = isMax ? Short.MIN_VALUE : Short.MAX_VALUE;
        ArrayList<byte[][]> immediateStates = getAllImmediateStates ( state );
        for(byte[][] immediateState: immediateStates)
            if(isMax)
                bestScore = (short)Math.max ( bestScore, miniMax ( immediateState, (byte)(depth - 1), false ) );
            else
                bestScore = (short)Math.min ( bestScore, miniMax ( immediateState, (byte)(depth - 1), true ) );
        return bestScore;
    }
    private short evaluateState(byte[][] state, boolean isMax){
        functionCalls++;
        byte countPieces = 0;
        for(byte y = 0; y < 8; y++)
            for(byte x = 0; x < 8; x++) {
                if (isMax && state[y][x] == B) countPieces++;
                else if (!isMax && state[y][x] == W) countPieces++;
            }
        return isMax ? (short)(12-countPieces) : (short)(countPieces-12);
    }
    private ArrayList<byte[][]> getAllImmediateStates (byte[][] state) {
        ArrayList<byte[][]> states = new ArrayList<> (  );
        for(byte y = 0; y < 8; y++){
            for(byte x = 0; x < 8; x++){
                if(state[y][x] == aiID){
                    states.addAll ( getAllImmediateStatesFor(state, y, x) );
                }
            }
        }

        return states;
    }
    private ArrayList<byte[][]> getAllImmediateStatesFor ( byte[][] state, byte row, byte col ) {
        ArrayList<Point> validPositions = getValidMoves ( state, row, col, playerID, aiID );
        ArrayList<byte[][]> immediateStates = new ArrayList<> (  );
        for(Point p: validPositions){
            byte[][] nextState = new byte[8][];
            for(byte i = 0; i < 8; i++)
                nextState[i] = piecesPositions[i].clone ();

            nextState[row][col] = E;
            nextState[p.getRow ()][p.getCol ()] = aiID;
            immediateStates.add ( nextState );
        }
        return immediateStates;
    }



    @Override
    public void mouseClicked ( int x, int y ) {
        //choose IDs
        if (!idIsChosen) {
            chooseIDs ( x, y );
            return;
        }


        // check if clicked on Menu or Reset
        if(bMenu.getBounds ().contains ( x, y )) {
            resetGame ();
            SetGameState ( MENU );
            return ;
        }
        else if(bReset.getBounds ().contains ( x, y )) {
            resetGame ( );
            return ;
        }


        //game logic
        if(gameWon) {
            for(Long time: differences)
                System.out.println ( time );
            return ;
        }
        makeMove ( x, y );
    }
    @Override
    public void mouseMoved ( int x, int y ) {
        bChooseBlack.setMouseOver ( false );
        bChooseWhite.setMouseOver ( false );
        bMenu.setMouseOver ( false );
        bReset.setMouseOver ( false );

        if(bChooseBlack.getBounds ().contains ( x, y ))
            bChooseBlack.setMouseOver ( true );
        else if(bChooseWhite.getBounds ().contains ( x, y ))
            bChooseWhite.setMouseOver ( true );
        else if(bMenu.getBounds ().contains ( x, y ))
            bMenu.setMouseOver ( true );
        else if(bReset.getBounds ().contains ( x, y ))
            bReset.setMouseOver ( true );
    }
    @Override
    public void mousePressed ( int x, int y ) {
        if(bChooseBlack.getBounds ().contains ( x, y ))
            bChooseBlack.setMousePressed ( true );
        else if(bChooseWhite.getBounds ().contains ( x, y ))
            bChooseWhite.setMousePressed ( true );
        else if(bMenu.getBounds ().contains ( x, y ))
            bMenu.setMousePressed ( true );
        else if(bReset.getBounds ().contains ( x, y ))
            bReset.setMousePressed ( true );
    }
    @Override
    public void mouseReleased ( int x, int y ) {
        resetButtons();
    }
    private void resetButtons () {
        bChooseBlack.resetBooleans ();
        bChooseWhite.resetBooleans ();
        bMenu.resetBooleans ();
        bReset.resetBooleans ();
    }
}
