package scenes;

import assistants.LevelBuild;
import objects.Point;
import main.Game;
import managers.TileManager;
import ui.MyButton;
import java.awt.*;
import java.util.ArrayList;
import static main.GameStates.*;

public class PlayingAgainstPerson extends GameScene implements SceneMethods{
    Game game;
    private MyButton bMenu, bReset,bWinnerDisplay;


    public PlayingAgainstPerson ( Game game) {
        super ( game );
        this.game = game;
        initButtons();
    }
    public static void setUpInitialGameState(){
        piecesPositions = LevelBuild.getInitialPiecesPositions ();
        turn = BLACK_TURN;
        gameWon = false;
        winner = -1;
    }
    @Override
    public void render ( Graphics g ) {
        //draw background
        drawBackground( g );

        //draw piecesPositions
        drawPieces( g );

        //draw active piece
        drawActive( g );

        //draw valid moves
        drawValidMoves( g );

        //draw buttons
        drawButtons( g );

        //display winner
    }
    private void drawValidMoves ( Graphics g ) {
        for(Point p: validMoves)
            g.drawImage ( tileManager.getSprite ( 4 ), 64*(p.getCol ()+1), 64*(p.getRow ()+2), null );
    }
    private void drawActive ( Graphics g ) {
        if(activePiece != null){
            byte row = (byte)(activePiece.getRow ()+2);
            byte col = (byte)(activePiece.getCol ()+1);

            if(turn == W)
                g.drawImage ( tileManager.getSprite ( 1 ), 64 * col, 64 * row, null);
            else if (turn == B)
                g.drawImage ( tileManager.getSprite ( 3 ), 64 * col, 64 * row, null);
        }
    }
    private void drawPieces ( Graphics g ) {
        for(byte i=0; i<piecesPositions.length; i++){
            for(byte j=0; j<piecesPositions[0].length; j++){
                if(piecesPositions[i][j] == W)
                    g.drawImage ( tileManager.getSprite ( piecesPositions[i][j] ), 64*(j+1), 64*(i+2), null );
                else if (piecesPositions[i][j] == B)
                    g.drawImage ( tileManager.getSprite ( piecesPositions[i][j]+1 ), 64*(j+1), 64*(i+2), null );
            }
        }
    }
    private void drawBackground ( Graphics g ) {
        LevelBuild.drawBackground ( g );
    }
    private void drawButtons( Graphics g ){
        bMenu.draw ( g );
        bReset.draw ( g );
        bWinnerDisplay.draw(g);
    }
    private void initButtons () {
        bMenu = new MyButton ( "Menu", 14, 12, 100, 40 );
        bReset = new MyButton ( "Reset", 128, 12, 100, 40 );
        bWinnerDisplay = new MyButton("",0,0,0,0);
    }




    @Override
    protected void changeTurn ( ) {
        if(turn == BLACK_TURN) turn = WHITE_TURN;
        else turn = BLACK_TURN;
        resetValidMovesAndActivePiece ();
    }
    @Override
    protected void resetGame () {
        piecesPositions = LevelBuild.getInitialPiecesPositions ();
        turn = BLACK_TURN;
        gameWon = false;
        winner = -1;
    }



    //TODO if gameWon then the notification should be displayed on the screen, not on the console
    @Override
    public void mouseClicked ( int x, int y ) {
        checkMenuAndResetClicked( x, y );
        if(gameWon) {
            String winnerString = (winner==0?"White":"Black");
            bWinnerDisplay = new MyButton ( winnerString+" wins!", 444, 12, 180, 40 );
            return;
        }
        makeMove( x, y );
    }
    @Override
    protected void makeMove( int x, int y){
        byte row = getRow ( y );
        byte col = getCol ( x );

        if(turn == WHITE_TURN){
            makePlayerMove( W, B, row, col);
        }else if (turn == BLACK_TURN){
            makePlayerMove ( B, W, row, col );
        }
    }


    private void checkMenuAndResetClicked ( int x, int y ) {
        if(bMenu.getBounds ().contains ( x, y )){
            SetGameState(MENU);
        }else if (bReset.getBounds ().contains ( x, y )){
            resetGame();
        }
    }


    @Override
    public void mouseMoved ( int x, int y ) {
        bMenu.setMouseOver ( false );
        bReset.setMouseOver ( false );

        if(bMenu.getBounds ().contains ( x, y ))
            bMenu.setMouseOver ( true );
        else if (bReset.getBounds ().contains ( x, y )) {
            bReset.setMouseOver ( true );
        }
    }
    @Override
    public void mousePressed ( int x, int y ) {
        if(bMenu.getBounds ().contains ( x, y ))
            bMenu.setMousePressed ( true );
        else if (bReset.getBounds ().contains ( x, y )) {
            bReset.setMousePressed ( true );
        }
    }
    @Override
    public void mouseReleased ( int x, int y ) {
        resetButtons();
    }
    private void resetButtons(){
        bMenu.resetBooleans ();
        bReset.resetBooleans ();
    }
}

