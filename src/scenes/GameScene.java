package scenes;

import assistants.LevelBuild;
import main.Game;
import managers.TileManager;
import objects.Point;
import objects.StateGenerationDTO;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;

public abstract class GameScene {
    //TODO find a better way to call the 8 functions in PlayingAgainstAI: miniMaxAlphaBeta
    protected static final byte WHITE_TURN = 0, BLACK_TURN = 1, W = 0, B = 1, E = 2,PRUNING_MINMAX = 0, CLASSIC_MINMAX=1;
    protected static byte[][] piecesPositions;
    protected static byte turn = B;
    protected static boolean gameWon;
    protected static byte winner;
    protected final TileManager tileManager = new TileManager();
    protected ArrayList<Point> validMoves = new ArrayList<>();
    protected Point activePiece = null;
    private final Game game;

    public GameScene(Game game) {
        this.game = game;
    }


    protected void setValidMovesAndActivePiece(byte row, byte col, byte opponentPiece, byte playersPiece) {
        validMoves = getValidMoves(piecesPositions, row, col, opponentPiece, playersPiece);
        activePiece = new Point(row, col);
        //validMovesStr ().forEach ( System.out::println );
    }

    protected void resetValidMovesAndActivePiece() {
        validMoves = new ArrayList<>();
        activePiece = null;
    }


    protected void makePlayerMove(byte playerColor, byte opponentColor, byte row, byte col) {
        if (activePiece == null)
            if (containsPiece(row, col, playerColor))
                setValidMovesAndActivePiece(row, col, opponentColor, playerColor);
            else
                resetValidMovesAndActivePiece();
        else {
            Point selectedPiece = new Point(row, col);
            if (activePiece.equals(selectedPiece) || containsPiece(row, col, playerColor))
                setValidMovesAndActivePiece(row, col, opponentColor, playerColor);
            else if (movedPieceToValidPosition(selectedPiece, playerColor)) {
                piecesPositions[activePiece.getRow()][activePiece.getCol()] = E;
                piecesPositions[selectedPiece.getRow()][selectedPiece.getCol()] = playerColor;

                checkWinningConditions();
                changeTurn();
            } else
                resetValidMovesAndActivePiece();
        }
    }

    private byte getNumHorizontalPieces(byte[][] state, byte row){
        byte numHorizontalPieces = 0;

        for (byte x = 0; x < 8; x++)
            if (state[row][x] != E)
                numHorizontalPieces++;

        return numHorizontalPieces;
    }
    private byte getNumVerticalPieces(byte[][] state, byte col){
        byte numVerticalPieces = 0;

        for (byte y = 0; y < 8; y++)
            if (state[y][col] != E)
                numVerticalPieces++;

        return numVerticalPieces;
    }
    private byte getNumMainDiagPieces(byte[][] state, byte row, byte col){
        byte numMainDiagPieces = 0;

        byte minVertOrHorizDist = (byte) Math.min ( row, col );
        for (byte y = (byte) (row - minVertOrHorizDist), x = (byte) (col - minVertOrHorizDist); y < 8 && x < 8; y++, x++)
            if (state[y][x] != E)
                numMainDiagPieces++;

        return numMainDiagPieces;
    }
    private byte getNumAntiDiagPieces(byte[][] state, byte row, byte col){
        byte numAntiDiagPieces = 0;

        byte minVertOrHorizDist = (byte) Math.min ( 7 - row, col );
        for (byte y = (byte) (row + minVertOrHorizDist), x = (byte) (col - minVertOrHorizDist); y >= 0 && x < 8; y--, x++)
            if (state[y][x] != E)
                numAntiDiagPieces++;

        return numAntiDiagPieces;
    }
    protected ArrayList<Point> getValidMoves(byte[][] state, byte row, byte col, byte opponentPiece, byte playersPiece) {
        ArrayList<Point> tempValidMoves = new ArrayList<>();
        Point p;

        //horizontal movement
        {
            p = getRightHorizontalMove ( state, row, col, opponentPiece, playersPiece );
            if(p != null) tempValidMoves.add ( p );

            p = getLeftHorizontalMove ( state, row, col, opponentPiece, playersPiece );
            if(p != null) tempValidMoves.add ( p );
        }

        //vertical movement
        {
            p = getDownVerticalMove ( state, row, col, opponentPiece, playersPiece );
            if(p != null) tempValidMoves.add ( p );

            p = getUpVerticalMove ( state, row, col, opponentPiece, playersPiece );
            if(p != null) tempValidMoves.add ( p );
        }

        //main diagonal movement
        {
            p = getDownMainDiagonalMove ( state, row, col, opponentPiece, playersPiece );
            if(p != null) tempValidMoves.add ( p );

            p = getUpMainDiagonalMove ( state, row, col, opponentPiece, playersPiece );
            if(p != null) tempValidMoves.add ( p );
        }

        //anti diagonal movement
        {
            p = getDownAntiDiagonalMove ( state, row, col, opponentPiece, playersPiece );
            if(p != null) tempValidMoves.add ( p );

            p = getUpAntiDiagonalMove ( state, row, col, opponentPiece, playersPiece );
            if(p != null) tempValidMoves.add ( p );
        }

        return tempValidMoves;
    }
    protected List<Function<StateGenerationDTO,Point>> getMovementFunctions(){
        List<Function<StateGenerationDTO,Point>> functionList = new ArrayList<>();

        functionList.add((s)->getLeftHorizontalMove(s.getState(),s.getRow(),s.getColumn(),s.getOpponentPiece(),s.getPlayerPiece()));
        functionList.add((s)->getRightHorizontalMove(s.getState(),s.getRow(),s.getColumn(),s.getOpponentPiece(),s.getPlayerPiece()));
        functionList.add((s)->getUpVerticalMove(s.getState(),s.getRow(),s.getColumn(),s.getOpponentPiece(),s.getPlayerPiece()));
        functionList.add((s)->getDownVerticalMove(s.getState(),s.getRow(),s.getColumn(),s.getOpponentPiece(),s.getPlayerPiece()));
        functionList.add((s)->getUpMainDiagonalMove(s.getState(),s.getRow(),s.getColumn(),s.getOpponentPiece(),s.getPlayerPiece()));
        functionList.add((s)->getDownMainDiagonalMove(s.getState(),s.getRow(),s.getColumn(),s.getOpponentPiece(),s.getPlayerPiece()));
        functionList.add((s)->getUpAntiDiagonalMove(s.getState(),s.getRow(),s.getColumn(),s.getOpponentPiece(),s.getPlayerPiece()));
        functionList.add((s)->getDownAntiDiagonalMove(s.getState(),s.getRow(),s.getColumn(),s.getOpponentPiece(),s.getPlayerPiece()));

    return functionList;
    }
    protected Point getLeftHorizontalMove(byte[][] state, byte row, byte col, byte opponentPiece, byte playersPiece ){
        byte numHorizontalPieces = getNumHorizontalPieces ( state, row );
        boolean pathContainsOpponentPiece = false;
        if (col - numHorizontalPieces >= 0) {
            for (byte x = (byte) (col - 1); x > col - numHorizontalPieces; x--)
                if (state[row][x] == opponentPiece) {
                    pathContainsOpponentPiece = true;
                    break;
                }
            if (!pathContainsOpponentPiece && state[row][col - numHorizontalPieces] != playersPiece)
                return new Point(row, (byte) (col - numHorizontalPieces));
        }
        return null;
    }
    protected Point getRightHorizontalMove(byte[][] state, byte row, byte col, byte opponentPiece, byte playersPiece ){
        byte numHorizontalPieces = getNumHorizontalPieces ( state, row );
        boolean pathContainsOpponentPiece = false;
        if (col + numHorizontalPieces < 8) {
            for (byte x = (byte) (col + 1); x < col + numHorizontalPieces; x++)
                if (state[row][x] == opponentPiece) {
                    pathContainsOpponentPiece = true;
                    break;
                }
            if (!pathContainsOpponentPiece && state[row][col + numHorizontalPieces] != playersPiece)
                return new Point(row, (byte) (col + numHorizontalPieces));
        }
        return null;
    }
    protected Point getUpVerticalMove(byte[][] state, byte row, byte col, byte opponentPiece, byte playersPiece ){
        byte numVerticalPieces = getNumVerticalPieces ( state, col );
        boolean pathContainsOpponentPiece = false;
        if (row - numVerticalPieces >= 0) {
            for (byte y = (byte) (row - 1); y > row - numVerticalPieces; y--)
                if (state[y][col] == opponentPiece) {
                    pathContainsOpponentPiece = true;
                    break;
                }
            if (!pathContainsOpponentPiece && state[row - numVerticalPieces][col] != playersPiece)
                return new Point((byte) (row - numVerticalPieces), col);
        }
        return null;
    }
    protected Point getDownVerticalMove(byte[][] state, byte row, byte col, byte opponentPiece, byte playersPiece ){
        byte numVerticalPieces = getNumVerticalPieces ( state, col );
        boolean pathContainsOpponentPiece = false;
        if (row + numVerticalPieces < 8) {
            for (byte y = (byte) (row + 1); y < row + numVerticalPieces; y++)
                if (state[y][col] == opponentPiece) {
                    pathContainsOpponentPiece = true;
                    break;
                }
            if (!pathContainsOpponentPiece && state[row + numVerticalPieces][col] != playersPiece)
                return new Point((byte) (row + numVerticalPieces), col);
        }
        return null;
    }
    protected Point getUpMainDiagonalMove(byte[][] state, byte row, byte col, byte opponentPiece, byte playersPiece ){
        byte numMainDiagPieces = getNumMainDiagPieces ( state, row, col );
        boolean pathContainsOpponentPiece = false;
        if (row - numMainDiagPieces >= 0 && col - numMainDiagPieces >= 0) {
            for (byte y = (byte) (row - 1), x = (byte) (col - 1); y > row - numMainDiagPieces && x > col - numMainDiagPieces; y--, x--)
                if (state[y][x] == opponentPiece) {
                    pathContainsOpponentPiece = true;
                    break;
                }
            if (!pathContainsOpponentPiece && state[row - numMainDiagPieces][col - numMainDiagPieces] != playersPiece)
                return new Point((byte) (row - numMainDiagPieces), (byte) (col - numMainDiagPieces));
        }
        return null;
    }
    protected Point getDownMainDiagonalMove(byte[][] state, byte row, byte col, byte opponentPiece, byte playersPiece ){
        byte numMainDiagPieces = getNumMainDiagPieces ( state, row, col );
        boolean pathContainsOpponentPiece = false;
        if (row + numMainDiagPieces < 8 && col + numMainDiagPieces < 8) {
            for (byte y = (byte) (row + 1), x = (byte) (col + 1); y < row + numMainDiagPieces && x < col + numMainDiagPieces; y++, x++)
                if (state[y][x] == opponentPiece) {
                    pathContainsOpponentPiece = true;
                    break;
                }
            if (!pathContainsOpponentPiece && state[row + numMainDiagPieces][col + numMainDiagPieces] != playersPiece)
                return new Point((byte) (row + numMainDiagPieces), (byte) (col + numMainDiagPieces));
        }
        return null;
    }
    protected Point getUpAntiDiagonalMove(byte[][] state, byte row, byte col, byte opponentPiece, byte playersPiece ){
        byte numAntiDiagPieces = getNumAntiDiagPieces ( state, row, col );
        boolean pathContainsOpponentPiece = false;
        if (row - numAntiDiagPieces >= 0 && col + numAntiDiagPieces < 8) {
            for (byte y = (byte) (row - 1), x = (byte) (col + 1); y > row - numAntiDiagPieces && x < col + numAntiDiagPieces; y--, x++)
                if (state[y][x] == opponentPiece) {
                    pathContainsOpponentPiece = true;
                    break;
                }
            if (!pathContainsOpponentPiece && state[row - numAntiDiagPieces][col + numAntiDiagPieces] != playersPiece)
                return new Point((byte) (row - numAntiDiagPieces), (byte) (col + numAntiDiagPieces));
        }
        return null;
    }
    protected Point getDownAntiDiagonalMove(byte[][] state, byte row, byte col, byte opponentPiece, byte playersPiece ){
        byte numAntiDiagPieces = getNumAntiDiagPieces ( state, row, col );
        boolean pathContainsOpponentPiece = false;
        if (row + numAntiDiagPieces < 8 && col - numAntiDiagPieces >= 0) {
            for (byte y = (byte) (row + 1), x = (byte) (col - 1); y < row + numAntiDiagPieces && x > col - numAntiDiagPieces; y++, x--)
                if (state[y][x] == opponentPiece) {
                    pathContainsOpponentPiece = true;
                    break;
                }
            if (!pathContainsOpponentPiece && state[row + numAntiDiagPieces][col - numAntiDiagPieces] != playersPiece)
                return new Point((byte) (row + numAntiDiagPieces), (byte) (col - numAntiDiagPieces));
        }
        return null;
    }
    protected byte[][] getStateFromMove(byte[][] state, Point pieceToMove, Point positionToMoveTo){
        byte[][] stateClone = new byte[8][];
        for(byte x = 0; x < 8; x++) stateClone[x] = state[x].clone ();

        stateClone[positionToMoveTo.getRow ()][positionToMoveTo.getCol ()] = stateClone[pieceToMove.getRow ()][pieceToMove.getCol ()];
        stateClone[pieceToMove.getRow ()][pieceToMove.getCol ()] = E;
        return stateClone;
    }
    protected boolean movedPieceToValidPosition(Point selectedPiece, byte playersPiece) {
        if (validMoves.contains(selectedPiece))
            return piecesPositions[selectedPiece.getRow()][selectedPiece.getCol()] != playersPiece;
        return false;
    }



    protected void checkWinningConditions() {
        if (turn == WHITE_TURN) {
            wins(W);
            wins(B);
        } else {
            wins(B);
            wins(W);
        }
    }

    private void wins(byte color) {
        if (!gameWon)
            if (allPiecesConnected(piecesPositions, color, getFirstPiece(piecesPositions, color))) {
                gameWon = true;
                winner = color;
            }
    }


    protected String positionTranslator(Point pointToTranslate) {
        String returnString = "";
        byte row = pointToTranslate.getRow();
        byte col = pointToTranslate.getCol();
        returnString += (char) (col + 'a');
        returnString += (8 - row);
        return returnString;
    }

    protected ArrayList<String> validMovesStr() {
        ArrayList<String> returnArray = new ArrayList<>();
        validMoves.forEach(point -> {
            String toAdd = positionTranslator(activePiece);
            toAdd += "->";
            toAdd += positionTranslator(point);
            returnArray.add(toAdd);
        });
        return returnArray;
    }


    protected Point getFirstPiece(byte[][] state, byte color) {
        for (byte i = 0; i < 8; i++)
            for (byte j = 0; j < 8; j++)
                if (state[i][j] == color)
                    return new Point(i, j);
        return null;
    }

    protected boolean containsPiece(byte row, byte col, byte color) {
        if (row >= 0 && row < 8 && col >= 0 && col < 8)
            return piecesPositions[row][col] == color;
        return false;
    }


    protected boolean allPiecesConnected(byte[][] state, byte color, Point startingPiece) {

        boolean[][] positionsVisited = new boolean[8][8];

        Queue<Point> queue = new ArrayDeque<>();
        queue.add(startingPiece);

        while (!queue.isEmpty()) {
            Point toEvaluate = queue.remove();

            if (!positionsVisited[toEvaluate.getRow()][toEvaluate.getCol()]) {
                ArrayList<Point> neighbours = getNeighbours(state, positionsVisited, color, toEvaluate);
                queue.addAll(neighbours);
                positionsVisited[toEvaluate.getRow()][toEvaluate.getCol()] = true;
            }
        }

        for (byte i = 0; i < 8; i++)
            for (byte j = 0; j < 8; j++)
                if (state[i][j] == color && !positionsVisited[i][j])
                    return false;
        return true;
    }

    private ArrayList<Point> getNeighbours(byte[][] state, boolean[][] positionsVisited, byte color, Point toEvaluate) {
        ArrayList<Point> neighbours = new ArrayList<>();
        byte row = toEvaluate.getRow();
        byte col = toEvaluate.getCol();

        for (byte i = -1; i < 2; i++)
            for (byte j = -1; j < 2; j++)
                if (row + i >= 0 && row + i < 8 && col + j >= 0 && col + j < 8)
                    if (piecesPositions[row + i][col + j] == color && !positionsVisited[row + i][col + j])
                        neighbours.add(new Point((byte) (row + i), (byte) (col + j)));
        return neighbours;
    }


    protected byte getCol(int x) {
        return (byte) (x / 64 - 1);
    }

    protected byte getRow(int y) {
        return (byte) (y / 64 - 2);
    }


    protected void drawPieces(Graphics g) {
        for (byte i = 0; i < piecesPositions.length; i++) {
            for (byte j = 0; j < piecesPositions[0].length; j++) {
                if (piecesPositions[i][j] == W)
                    g.drawImage(tileManager.getSprite(piecesPositions[i][j]), 64 * (j + 1), 64 * (i + 2), null);
                else if (piecesPositions[i][j] == B)
                    g.drawImage(tileManager.getSprite(piecesPositions[i][j] + 1), 64 * (j + 1), 64 * (i + 2), null);
            }
        }
    }

    protected void drawValidMoves(Graphics g) {
        for (Point p : validMoves)
            g.drawImage(tileManager.getSprite(4), 64 * (p.getCol() + 1), 64 * (p.getRow() + 2), null);
    }

    protected void drawActive(Graphics g) {
        if (activePiece != null) {
            byte row = (byte) (activePiece.getRow() + 2);
            byte col = (byte) (activePiece.getCol() + 1);

            if (turn == W)
                g.drawImage(tileManager.getSprite(1), 64 * col, 64 * row, null);
            else if (turn == B)
                g.drawImage(tileManager.getSprite(3), 64 * col, 64 * row, null);
        }
    }

    protected void drawMenuBackground(Graphics g) {
        g.setColor(new Color(87, 196, 97));
        for (byte y = 0; y < 10; y++)
            for (byte x = 0; x < 11; x++)
                if ((y + x) % 2 == 0)
                    g.fillRect(64 * y, 64 * x, 64, 64);

        g.setColor(new Color(45, 161, 84));
        for (byte y = 0; y < 10; y++)
            for (byte x = 0; x < 11; x++)
                if ((y + x) % 2 != 0)
                    g.fillRect(64 * y, 64 * x, 64, 64);
    }

    protected void drawBoardBackground(Graphics g) {
        LevelBuild.drawBackground(g);
    }

    protected void displayWinner(Graphics g) {
        if (gameWon) {
            g.setColor(new Color(168, 212, 190));
            g.fillRect(340, 12, 100, 40);
            g.setColor(new Color(88, 69, 47));
            g.drawRect(340, 12, 100, 40);
            g.drawRect(341, 13, 98, 38);
            if (winner == W) g.drawString("Winner: White", 356, 38);
            else g.drawString("Winner: Black", 356, 38);
        }
    }


    protected abstract void resetGame();

    protected abstract void changeTurn();

    protected abstract void makeMove(int x, int y);
}
