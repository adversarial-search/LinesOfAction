package scenes;

import assistants.LevelBuild;
import objects.Point;
import main.Game;
import managers.TileManager;
import ui.MyButton;
import java.awt.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import static main.GameStates.*;

public class PlayingAgainstPerson extends GameScene implements SceneMethods{
    Game game;
    private static final byte WHITE_TURN = 0;
    private static final byte BLACK_TURN = 1;
    private static byte turn;
    private static boolean gameWon;
    private static byte winner;
    private static final byte W = 0;
    private static final byte B = 1;
    private static final byte E = 2;
    private static byte[][] piecesPositions;
    private boolean[][] positionsVisited;
    private ArrayList<Point> validMoves = new ArrayList<> (  );
    private Point activePiece = null;
    private final TileManager tileManager = new TileManager ();
    private MyButton bMenu, bReset;
    public PlayingAgainstPerson ( Game game) {
        super ( game );
        this.game = game;
        initButtons();
    }



    private void initButtons () {
        bMenu = new MyButton ( "Menu", 14, 12, 100, 40 );
        bReset = new MyButton ( "Reset", 128, 12, 100, 40 );
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
        g.setColor ( new Color ( 138, 219, 181 ) );
        g.fillRect ( 0, 0, 640, 704 );

        g.setColor ( new Color ( 87, 196, 97 ) );
        for(byte y=0; y<8; y++)
            for(byte x=0; x<8; x++)
                if((y+x)%2 == 0)
                    g.fillRect ( 64*(y+1), 64*(x+2), 64, 64 );

        g.setColor ( new Color ( 45, 161, 84 ) );
        for(byte y=0; y<8; y++)
            for(byte x=0; x<8; x++)
                if((y+x)%2 != 0)
                    g.fillRect ( 64*(x+1), 64*(y+2), 64, 64 );

        g.drawRect ( 64, 128, 512, 512 );
        g.drawRect ( 65, 129, 510, 510 );
        g.drawRect ( 66, 130, 508, 508 );

        byte[][] numbersAndLetters = LevelBuild.numbersAndLetters ();
        for(byte i=0; i < numbersAndLetters.length; i++)
            for(byte j=0; j < numbersAndLetters[0].length; j++)
                if(numbersAndLetters[i][j]>=0)
                    g.drawImage ( tileManager.getSprite ( numbersAndLetters[i][j] ), 64*j, 64*i, null );
    }
    private void drawButtons( Graphics g ){
        bMenu.draw ( g );
        bReset.draw ( g );
    }


    private void setValidMovesAndActivePiece ( byte row, byte col, byte opponentPiece, byte playersPiece ) {
        validMoves = getValidMoves ( row, col, opponentPiece, playersPiece );
        activePiece = new Point ( row, col );
    }
    private void resetValidMovesAndActivePiece ( ) {
        validMoves = new ArrayList<> (  );
        activePiece = null;
    }
    private void changeTurn ( ) {
        if(turn == BLACK_TURN) turn = WHITE_TURN;
        else turn = BLACK_TURN;
        resetValidMovesAndActivePiece ();
    }
    private void checkWinningConditions ( ) {
        if(turn == WHITE_TURN){
            wins(W);
            wins(B);
        }else{
            wins(B);
            wins(W);
        }
    }
    private void wins(byte T){
        if(!gameWon)
            if (allPiecesConnected ( T, getFirstPiece ( T ) )){
                gameWon = true;
                winner = T;
            }
    }
    private void resetGame () {
        piecesPositions = LevelBuild.getInitialPiecesPositions ();
        turn = BLACK_TURN;
        gameWon = false;
        winner = -1;
    }



    //TODO if gameWon then the notification should be displayed on the screen, not on the console
    @Override
    public void mouseClicked ( int x, int y ) {
        if(bMenu.getBounds ().contains ( x, y )){
            SetGameState(MENU);
        }else if (bReset.getBounds ().contains ( x, y )){
            resetGame();
        }
        //make this a rectangle that displays the winner
        if(gameWon) {
            if(winner == W)
                System.out.println ( "Game already won by: WHITE" );
            else
                System.out.println ( "Game already won by: BLACK" );
            return;
        }

        byte row = getRow ( y );
        byte col = getCol ( x );

        if(turn == WHITE_TURN){
            if(activePiece == null)
                if(containsWhitePiece ( row, col ))
                    setValidMovesAndActivePiece ( row, col, B, W );
                else
                    resetValidMovesAndActivePiece ();
            else{
                Point selectedPiece = new Point ( row, col );
                if(activePiece.equals ( selectedPiece ) || containsWhitePiece ( row, col ))
                    setValidMovesAndActivePiece ( row, col, B, W );
                else if (movedPieceToValidPosition(selectedPiece, W)){
                    piecesPositions[activePiece.getRow ()][activePiece.getCol ()] = E;
                    piecesPositions[selectedPiece.getRow ()][selectedPiece.getCol ()] = W;

                    checkWinningConditions();
                    changeTurn ();
                }else
                    resetValidMovesAndActivePiece ();
            }
        }else if (turn == BLACK_TURN){
            if( activePiece == null )
                if (containsBlackPiece ( row, col ))
                    setValidMovesAndActivePiece ( row, col, W, B );
                else
                    resetValidMovesAndActivePiece ();
            else{
                Point selectedPiece = new Point ( row, col );
                if( activePiece.equals ( selectedPiece ) || containsBlackPiece ( row, col ) )
                    setValidMovesAndActivePiece ( row, col, W, B );
                else if(movedPieceToValidPosition ( selectedPiece, B )){
                    piecesPositions[activePiece.getRow ()][activePiece.getCol ()] = E;
                    piecesPositions[selectedPiece.getRow ()][selectedPiece.getCol ()] = B;

                    checkWinningConditions ();
                    changeTurn ();
                }else
                    resetValidMovesAndActivePiece ();
            }
        }

    }



    private ArrayList<Point> getValidMoves ( byte row, byte col, byte opponentPiece, byte playersPiece ) {
        ArrayList<Point> tempValidMoves = new ArrayList<> (  );
        boolean pathContainsOpponentPiece;

        //horizontal movement
        {
            byte numHorizontalPieces = 0;
            for(byte x = 0; x < 8; x++)
                if(piecesPositions[row][x] != E)
                    numHorizontalPieces++;

            //right movement
            pathContainsOpponentPiece = false;
            if(col + numHorizontalPieces < 8){
                for(byte x = (byte)(col + 1); x < col + numHorizontalPieces; x++)
                    if (piecesPositions[row][x] == opponentPiece) {
                        pathContainsOpponentPiece = true;
                        break;
                    }
                if(!pathContainsOpponentPiece && piecesPositions[row][col+numHorizontalPieces] != playersPiece)
                    tempValidMoves.add ( new Point ( row, (byte)(col+numHorizontalPieces) ) );
            }

            //left movement
            pathContainsOpponentPiece = false;
            if(col - numHorizontalPieces >= 0){
                for(byte x = (byte)(col - 1); x > col - numHorizontalPieces; x--)
                    if (piecesPositions[row][x] == opponentPiece) {
                        pathContainsOpponentPiece = true;
                        break;
                    }
                if(!pathContainsOpponentPiece && piecesPositions[row][col-numHorizontalPieces] != playersPiece)
                    tempValidMoves.add ( new Point ( row, (byte)(col-numHorizontalPieces) ) );
            }
        }

        //vertical movement
        {
            byte numVerticalPieces = 0;
            for(byte y=0; y < 8; y++)
                if(piecesPositions[y][col] != E)
                    numVerticalPieces++;

            //downwards movement
            pathContainsOpponentPiece = false;
            if(row + numVerticalPieces < 8){
                for(byte y = (byte)(row + 1); y < row + numVerticalPieces; y++)
                    if (piecesPositions[y][col] == opponentPiece) {
                        pathContainsOpponentPiece = true;
                        break;
                    }
                if(!pathContainsOpponentPiece && piecesPositions[row+numVerticalPieces][col] != playersPiece)
                    tempValidMoves.add ( new Point ( (byte)(row+numVerticalPieces), col ) );
            }

            //upwards movement
            pathContainsOpponentPiece = false;
            if(row - numVerticalPieces >= 0){
                for(byte y = (byte)(row - 1); y > row - numVerticalPieces; y--)
                    if (piecesPositions[y][col] == opponentPiece) {
                        pathContainsOpponentPiece = true;
                        break;
                    }
                if(!pathContainsOpponentPiece && piecesPositions[row - numVerticalPieces][col] != playersPiece)
                    tempValidMoves.add ( new Point ( (byte)(row - numVerticalPieces), col ) );
            }
        }

        //main diagonal movement
        {
            byte numMainDiagPieces = 0;
            byte minVertOrHorizDist = (byte)Math.min(row, col);
            for(byte y = (byte)(row-minVertOrHorizDist), x = (byte)(col-minVertOrHorizDist); y < 8 && x < 8; y++, x++)
                if(piecesPositions[y][x] != E)
                    numMainDiagPieces++;

            //down diagonal movement
            pathContainsOpponentPiece = false;
            if(row + numMainDiagPieces < 8 && col + numMainDiagPieces < 8){
                for(byte y = (byte)(row + 1), x = (byte)(col + 1); y < row + numMainDiagPieces && x < col + numMainDiagPieces; y++, x++)
                    if (piecesPositions[y][x] == opponentPiece) {
                        pathContainsOpponentPiece = true;
                        break;
                    }
                if(!pathContainsOpponentPiece && piecesPositions[row + numMainDiagPieces][col + numMainDiagPieces] != playersPiece)
                    tempValidMoves.add ( new Point ( (byte)(row + numMainDiagPieces), (byte)(col + numMainDiagPieces) ) );
            }

            //up diagonal movement
            pathContainsOpponentPiece = false;
            if(row - numMainDiagPieces >= 0 && col - numMainDiagPieces >= 0){
                for(byte y = (byte)(row - 1), x = (byte)(col - 1); y > row - numMainDiagPieces && x > col - numMainDiagPieces; y--, x--)
                    if (piecesPositions[y][x] == opponentPiece) {
                        pathContainsOpponentPiece = true;
                        break;
                    }
                if(!pathContainsOpponentPiece && piecesPositions[row - numMainDiagPieces][col - numMainDiagPieces] != playersPiece)
                    tempValidMoves.add ( new Point ( (byte)(row - numMainDiagPieces), (byte)(col - numMainDiagPieces) ) );
            }
        }

        //anti diagonal movement
        {
            byte numAntiDiagPieces = 0;
            byte minVertOrHorizDist = (byte)Math.min(7-row, col);
            for(byte y = (byte)(row+minVertOrHorizDist), x = (byte)(col-minVertOrHorizDist); y >= 0 && x < 8; y--, x++)
                if(piecesPositions[y][x] != E)
                    numAntiDiagPieces++;

            //down anti diagonal movement
            pathContainsOpponentPiece = false;
            if(row + numAntiDiagPieces < 8 && col - numAntiDiagPieces >= 0){
                for(byte y = (byte)(row + 1), x = (byte)(col - 1); y < row + numAntiDiagPieces && x > col - numAntiDiagPieces; y++, x--)
                    if (piecesPositions[y][x] == opponentPiece) {
                        pathContainsOpponentPiece = true;
                        break;
                    }
                if(!pathContainsOpponentPiece && piecesPositions[row + numAntiDiagPieces][col - numAntiDiagPieces] != playersPiece)
                    tempValidMoves.add ( new Point ( (byte)(row + numAntiDiagPieces), (byte)(col - numAntiDiagPieces) ) );
            }

            //up anti diagonal movement
            pathContainsOpponentPiece = false;
            if(row - numAntiDiagPieces >= 0 && col + numAntiDiagPieces < 8){
                for(byte y = (byte)(row - 1), x = (byte)(col + 1); y > row - numAntiDiagPieces && x < col + numAntiDiagPieces; y--, x++)
                    if (piecesPositions[y][x] == opponentPiece) {
                        pathContainsOpponentPiece = true;
                        break;
                    }
                if(!pathContainsOpponentPiece && piecesPositions[row - numAntiDiagPieces][col + numAntiDiagPieces] != playersPiece)
                    tempValidMoves.add ( new Point ( (byte)(row - numAntiDiagPieces), (byte)(col + numAntiDiagPieces) ) );
            }
        }

        return tempValidMoves;
    }
    private boolean movedPieceToValidPosition ( Point selectedPiece, byte playersPiece ) {
        if(validMoves.contains ( selectedPiece ))
            return piecesPositions[selectedPiece.getRow ( )][selectedPiece.getCol ( )] != playersPiece;
        return false;
    }



    private boolean allPiecesConnected ( byte p, Point startingPiece ) {

        resetPositionsVisited ();

        Queue<Point> queue = new ArrayDeque<> (  );
        queue.add ( startingPiece );

        while( !queue.isEmpty () ){
            Point toEvaluate = queue.remove ();

            if(!positionsVisited[toEvaluate.getRow ()][toEvaluate.getCol ()]) {
                ArrayList<Point> neighbours = getNeighbours ( p, toEvaluate );
                queue.addAll ( neighbours );
                positionsVisited[toEvaluate.getRow ()][toEvaluate.getCol ()] = true;
            }
        }

        for(byte i=0; i<8; i++)
            for(byte j=0; j<8; j++)
                if(piecesPositions[i][j] == p && !positionsVisited[i][j])
                    return false;
        return true;
    }
    private ArrayList<Point> getNeighbours ( byte p, Point toEvaluate ) {
        ArrayList<Point> neighbours = new ArrayList<> (  );
        byte row = toEvaluate.getRow ();
        byte col = toEvaluate.getCol ();

        for(byte i=-1; i<2; i++)
            for(byte j=-1; j<2; j++)
                if(row+i >= 0 && row+i < 8 && col+j >= 0 && col+j < 8)
                    if(piecesPositions[row+i][col+j] == p && !positionsVisited[row + i][col + j])
                        neighbours.add ( new Point ( (byte)(row+i), (byte)(col+j) ) );
        return neighbours;
    }



    private boolean containsWhitePiece ( byte row, byte col ) {
        if(row >= 0 && row < 8 && col >= 0 && col < 8)
            return piecesPositions[row][col] == W;
        return false;
    }
    private boolean containsBlackPiece ( byte row, byte col ) {
        if(row >= 0 && row < 8 && col >= 0 && col < 8)
            return piecesPositions[row][col] == B;
        return false;
    }
    private byte getCol ( int x ) {
        return (byte)(x/64-1);
    }
    private byte getRow ( int y ) {
        return (byte)(y/64-2);
    }
    public static void setUpInitialGameState(){
        piecesPositions = LevelBuild.getInitialPiecesPositions ();
        turn = BLACK_TURN;
        gameWon = false;
        winner = -1;
    }
    public void resetPositionsVisited(){
        positionsVisited = new boolean[][]{
                {false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false},
                {false, false, false, false, false, false, false, false}
        };
    }
    private Point getFirstPiece (byte b) {
        for(byte i=0; i<8; i++)
            for(byte j=0; j<8; j++)
                if(piecesPositions[i][j] == b)
                    return new Point ( i, j );
        return null;
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

