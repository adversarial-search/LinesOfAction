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
    //TODO check winning conditions again and their implementation
    @Override
    protected void makeMove ( int x, int y ) {
        if( turn == playerID ){
            byte row = getRow ( y );
            byte col = getCol ( x );

            makePlayerMove ( playerID, aiID, row, col );
        }
    }

    private void makeAiMove () {
        ArrayList<byte[][]> immediateStates = getAllImmediateStates();
        int chooseState = random.nextInt (immediateStates.size ());
        piecesPositions = immediateStates.get ( chooseState );
        turn = playerID;
    }
    private ArrayList<byte[][]> getAllImmediateStates () {
        ArrayList<byte[][]> states = new ArrayList<> (  );
        for(byte y = 0; y < 8; y++){
            for(byte x = 0; x < 8; x++){
                if(piecesPositions[y][x] == aiID){
                    states.addAll ( getAllImmediateStatesFor(y, x) );
                }
            }
        }

        return states;
    }
    private ArrayList<byte[][]> getAllImmediateStatesFor ( byte row, byte col ) {
        ArrayList<Point> validPositions = getValidMoves ( row, col, playerID, aiID );
        ArrayList<byte[][]> immediateStates = new ArrayList<> (  );
        for(Point p: validPositions){
            //TODO find a more efficient way or do it with System.arraycopy
            byte[][] state = new byte[8][];
            for(byte i = 0; i < 8; i++)
                state[i] = piecesPositions[i].clone ();

            state[row][col] = E;
            state[p.getRow ()][p.getCol ()] = aiID;
            immediateStates.add ( state );
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
