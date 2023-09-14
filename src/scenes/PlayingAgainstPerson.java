package scenes;

import assistants.LevelBuild;
import objects.Point;
import main.Game;
import managers.TileManager;
import java.awt.*;
import java.util.ArrayList;


//TODO add winning state check
public class PlayingAgainstPerson extends GameScene implements SceneMethods{
    Game game;
    private static final int WHITE_TURN = 0;
    private static final int BLACK_TURN = 1;
    private int turn = BLACK_TURN;

    private static final int W = 0;
    private static final int B = 1;
    private static final int E = 2;

    private static int[][] piecesPositions;

    private ArrayList<Point> validMoves = new ArrayList<> (  );
    private Point activePiece = null;

    private final TileManager tileManager = new TileManager ();

    public PlayingAgainstPerson ( Game game) {
        super ( game );
        this.game = game;
    }


    public static void resetPiecesPositions(){
        piecesPositions = new int[][]{
                {E, B, B, B, B, B, B, E},
                {W, E, E, E, E, E, E, W},
                {W, E, E, E, E, E, E, W},
                {W, E, E, E, E, E, E, W},
                {W, E, E, E, E, E, E, W},
                {W, E, E, E, E, E, E, W},
                {W, E, E, E, E, E, E, W},
                {E, B, B, B, B, B, B, E}
        };
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
    }
    private void drawValidMoves ( Graphics g ) {
        for(Point p: validMoves)
            g.drawImage ( tileManager.getSprite ( 4 ), 64*(p.getCol ()+1), 64*(p.getRow ()+2), null );
    }
    private void drawActive ( Graphics g ) {
        if(activePiece != null){
            int row = activePiece.getRow ()+2;
            int col = activePiece.getCol ()+1;

            if(turn == W)
                g.drawImage ( tileManager.getSprite ( 1 ), 64 * col, 64 * row, null);
            else if (turn == B)
                g.drawImage ( tileManager.getSprite ( 3 ), 64 * col, 64 * row, null);
        }
    }
    private void drawPieces ( Graphics g ) {
        for(int i=0; i<piecesPositions.length; i++){
            for(int j=0; j<piecesPositions[0].length; j++){
                if(piecesPositions[i][j] == W)
                    g.drawImage ( tileManager.getSprite ( piecesPositions[i][j] ), 64*(j+1), 64*(i+2), null );
                else if (piecesPositions[i][j] == B)
                    g.drawImage ( tileManager.getSprite ( piecesPositions[i][j]+1 ), 64*(j+1), 64*(i+2), null );
            }
        }
    }
    private void drawBackground ( Graphics g ) {
        int[][] emptyFields = LevelBuild.emptyFields ( );
        int[][] numbersAndLetters = LevelBuild.numbersAndLetters ();

        for(int i=0; i<emptyFields.length; i++)
            for(int j=0; j<emptyFields[0].length; j++)
                g.drawImage ( tileManager.getSprite ( emptyFields[i][j] ), 64*j, 64*i, null );

        for(int i=0; i< numbersAndLetters.length; i++)
            for(int j=0; j< numbersAndLetters[0].length; j++)
                if(numbersAndLetters[i][j]>=0)
                    g.drawImage ( tileManager.getSprite ( numbersAndLetters[i][j] ), 64*j, 64*i, null );
    }


    @Override
    public void mouseClicked ( int x, int y ) {
        int row = getRow ( y );
        int col = getCol ( x );

        if(turn == WHITE_TURN){
            if(activePiece == null){
                if(containsWhitePiece ( row, col )){
                    validMoves = getValidMoves ( row, col, B, W );
                    activePiece = new Point (row, col);
                }else {
                    validMoves = new ArrayList<> (  );
                    activePiece = null;
                }
            }else{
                Point selectedPiece = new Point ( row, col );
                if(activePiece.equals ( selectedPiece ) || containsWhitePiece ( row, col )) {
                    validMoves = getValidMoves ( row, col, B, W );
                    activePiece = selectedPiece;
                }else if (movedPieceToValidPosition(selectedPiece, W)){
                    piecesPositions[activePiece.getRow ()][activePiece.getCol ()] = E;
                    piecesPositions[selectedPiece.getRow ()][selectedPiece.getCol ()] = W;

                    turn = BLACK_TURN;
                    activePiece = null;
                    validMoves = new ArrayList<> (  );
                }else {
                    validMoves = new ArrayList<> (  );
                    activePiece = null;
                }
            }
        }else if (turn == BLACK_TURN){
            if( activePiece == null ){
                if (containsBlackPiece ( row, col )){
                    validMoves = getValidMoves ( row, col, W, B );
                    activePiece = new Point ( row, col );
                }else{
                    validMoves = new ArrayList<> (  );
                    activePiece = null;
                }
            }else{
                Point selectedPiece = new Point ( row, col );
                if( activePiece.equals ( selectedPiece ) || containsBlackPiece ( row, col ) ){
                    validMoves = getValidMoves ( row, col, W, B );
                    activePiece = selectedPiece;
                }else if(movedPieceToValidPosition ( selectedPiece, B )){
                    piecesPositions[activePiece.getRow ()][activePiece.getCol ()] = E;
                    piecesPositions[selectedPiece.getRow ()][selectedPiece.getCol ()] = B;

                    turn = WHITE_TURN;
                    activePiece = null;
                    validMoves = new ArrayList<> (  );
                }else{
                    validMoves = new ArrayList<> (  );
                    activePiece = null;
                }
            }
        }
    }

    private boolean movedPieceToValidPosition ( Point selectedPiece, int playersPiece ) {
        if(validMoves.contains ( selectedPiece )) {
            return piecesPositions[selectedPiece.getRow ( )][selectedPiece.getCol ( )] != playersPiece;
        }
        return false;
    }

    private ArrayList<Point> getValidMoves ( int row, int col, int opponentPiece, int playersPiece ) {
        ArrayList<Point> tempValidMoves = new ArrayList<> (  );
        boolean pathContainsOpponentPiece;

        //horizontal movement
        {
            int numHorizontalPieces = 0;
            for(int x = 0; x < 8; x++) if(piecesPositions[row][x] != E) numHorizontalPieces++;

            //right movement
            pathContainsOpponentPiece = false;
            if(col + numHorizontalPieces < 8){
                for(int x = col + 1; x < col + numHorizontalPieces; x++)
                    if (piecesPositions[row][x] == opponentPiece) {
                        pathContainsOpponentPiece = true;
                        break;
                    }
                if(!pathContainsOpponentPiece && piecesPositions[row][col+numHorizontalPieces] != playersPiece)
                    tempValidMoves.add ( new Point ( row, col+numHorizontalPieces ) );
            }

            //left movement
            pathContainsOpponentPiece = false;
            if(col - numHorizontalPieces >= 0){
                for(int x = col - 1; x > col - numHorizontalPieces; x--)
                    if (piecesPositions[row][x] == opponentPiece) {
                        pathContainsOpponentPiece = true;
                        break;
                    }
                if(!pathContainsOpponentPiece && piecesPositions[row][col-numHorizontalPieces] != playersPiece)
                    tempValidMoves.add ( new Point ( row, col-numHorizontalPieces ) );
            }
        }

        //vertical movement
        {
            int numVerticalPieces = 0;
            for(int y=0; y < 8; y++) if(piecesPositions[y][col] != E) numVerticalPieces++;

            //downwards movement
            pathContainsOpponentPiece = false;
            if(row + numVerticalPieces < 8){
                for(int y = row + 1; y < row + numVerticalPieces; y++)
                    if (piecesPositions[y][col] == opponentPiece) {
                        pathContainsOpponentPiece = true;
                        break;
                    }
                if(!pathContainsOpponentPiece && piecesPositions[row+numVerticalPieces][col] != playersPiece)
                    tempValidMoves.add ( new Point ( row+numVerticalPieces, col ) );
            }

            //upwards movement
            pathContainsOpponentPiece = false;
            if(row - numVerticalPieces >= 0){
                for(int y = row - 1; y > row - numVerticalPieces; y--)
                    if (piecesPositions[y][col] == opponentPiece) {
                        pathContainsOpponentPiece = true;
                        break;
                    }
                if(!pathContainsOpponentPiece && piecesPositions[row - numVerticalPieces][col] != playersPiece)
                    tempValidMoves.add ( new Point ( row - numVerticalPieces, col ) );
            }
        }

        //main diagonal movement
        {
            int numMainDiagPieces = 0;
            int minVertOrHorizDist = Math.min(row, col);
            for(int y = row-minVertOrHorizDist, x = col-minVertOrHorizDist; y < 8 && x < 8; y++, x++)
                if(piecesPositions[y][x] != E)
                    numMainDiagPieces++;

            //down diagonal movement
            pathContainsOpponentPiece = false;
            if(row + numMainDiagPieces < 8 && col + numMainDiagPieces < 8){
                for(int y = row + 1, x = col + 1; y < row + numMainDiagPieces && x < col + numMainDiagPieces; y++, x++)
                    if (piecesPositions[y][x] == opponentPiece) {
                        pathContainsOpponentPiece = true;
                        break;
                    }
                if(!pathContainsOpponentPiece && piecesPositions[row + numMainDiagPieces][col + numMainDiagPieces] != playersPiece)
                    tempValidMoves.add ( new Point ( row + numMainDiagPieces, col + numMainDiagPieces ) );
            }

            //up diagonal movement
            pathContainsOpponentPiece = false;
            if(row - numMainDiagPieces >= 0 && col - numMainDiagPieces >= 0){
                for(int y = row - 1, x = col - 1; y > row - numMainDiagPieces && x > col - numMainDiagPieces; y--, x--)
                    if (piecesPositions[y][x] == opponentPiece) {
                        pathContainsOpponentPiece = true;
                        break;
                    }
                if(!pathContainsOpponentPiece && piecesPositions[row - numMainDiagPieces][col - numMainDiagPieces] != playersPiece)
                    tempValidMoves.add ( new Point ( row - numMainDiagPieces, col - numMainDiagPieces ) );
            }
        }

        //anti diagonal movement
        {
            int numAntiDiagPieces = 0;
            int minVertOrHorizDist = Math.min(7-row, col);
            for(int y = row+minVertOrHorizDist, x = col-minVertOrHorizDist; y >= 0 && x < 8; y--, x++)
                if(piecesPositions[y][x] != E)
                    numAntiDiagPieces++;

            //down anti diagonal movement
            pathContainsOpponentPiece = false;
            if(row + numAntiDiagPieces < 8 && col - numAntiDiagPieces >= 0){
                for(int y = row + 1, x = col - 1; y < row + numAntiDiagPieces && x > col - numAntiDiagPieces; y++, x--)
                    if (piecesPositions[y][x] == opponentPiece) {
                        pathContainsOpponentPiece = true;
                        break;
                    }
                if(!pathContainsOpponentPiece && piecesPositions[row + numAntiDiagPieces][col - numAntiDiagPieces] != playersPiece)
                    tempValidMoves.add ( new Point ( row + numAntiDiagPieces, col - numAntiDiagPieces ) );
            }

            //up anti diagonal movement
            pathContainsOpponentPiece = false;
            if(row - numAntiDiagPieces >= 0 && col + numAntiDiagPieces < 8){
                for(int y = row - 1, x = col + 1; y > row - numAntiDiagPieces && x < col + numAntiDiagPieces; y--, x++)
                    if (piecesPositions[y][x] == opponentPiece) {
                        pathContainsOpponentPiece = true;
                        break;
                    }
                if(!pathContainsOpponentPiece && piecesPositions[row - numAntiDiagPieces][col + numAntiDiagPieces] != playersPiece)
                    tempValidMoves.add ( new Point ( row - numAntiDiagPieces, col + numAntiDiagPieces ) );
            }
        }

        return tempValidMoves;
    }

    private boolean containsWhitePiece ( int row, int col ) {
        if(row >= 0 && row < 8 && col >= 0 && col < 8)
            return piecesPositions[row][col] == W;
        return false;
    }
    private boolean containsBlackPiece ( int row, int col ) {
        if(row >= 0 && row < 8 && col >= 0 && col < 8)
            return piecesPositions[row][col] == B;
        return false;
    }
    private boolean containsNoPiece ( int row, int col ){
        if(row >= 0 && row < 8 && col >= 0 && col < 8)
            return piecesPositions[row][col] == E;
        return false;
    }
    private int getCol ( int x ) {
        return x/64-1;
    }
    private int getRow ( int y ) {
        return y/64-2;
    }


    @Override
    public void mouseMoved ( int x, int y ) {

    }
    @Override
    public void mousePressed ( int x, int y ) {

    }
    @Override
    public void mouseReleased ( int x, int y ) {

    }
}
