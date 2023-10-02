package assistants;

import objects.Point;
import objects.StateGenerationDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ValidMovesFunctions {
    public static final byte E = 2;
    public static final List<Function<StateGenerationDTO,Point>> movementFunctions = getMovementFunctions();
    public static ArrayList<Point> getValidMoves(byte[][] state, byte row, byte col, byte opponentPiece, byte playersPiece) {
        ArrayList<Point> tempValidMoves = new ArrayList<>();
        Point p;

        StateGenerationDTO currentStateDTO = new StateGenerationDTO(state,row,col, opponentPiece, playersPiece);
        for (Function<StateGenerationDTO, Point> function : movementFunctions) {
            p = function.apply(currentStateDTO);
            if (p != null) {
                tempValidMoves.add(p);
            }
        }

        return tempValidMoves;
    }

    private static List<Function<StateGenerationDTO,Point>> getMovementFunctions(){
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
    protected static Point getLeftHorizontalMove(byte[][] state, byte row, byte col, byte opponentPiece, byte playersPiece ){
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
    protected static Point getRightHorizontalMove(byte[][] state, byte row, byte col, byte opponentPiece, byte playersPiece ){
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
    protected static Point getUpVerticalMove(byte[][] state, byte row, byte col, byte opponentPiece, byte playersPiece ){
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
    protected static Point getDownVerticalMove(byte[][] state, byte row, byte col, byte opponentPiece, byte playersPiece ){
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
    protected static Point getUpMainDiagonalMove(byte[][] state, byte row, byte col, byte opponentPiece, byte playersPiece ){
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
    protected static Point getDownMainDiagonalMove(byte[][] state, byte row, byte col, byte opponentPiece, byte playersPiece ){
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
    protected static Point getUpAntiDiagonalMove(byte[][] state, byte row, byte col, byte opponentPiece, byte playersPiece ){
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
    protected static Point getDownAntiDiagonalMove(byte[][] state, byte row, byte col, byte opponentPiece, byte playersPiece ){
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

    private static byte getNumHorizontalPieces(byte[][] state, byte row){
        byte numHorizontalPieces = 0;

        for (byte x = 0; x < 8; x++)
            if (state[row][x] != E)
                numHorizontalPieces++;

        return numHorizontalPieces;
    }
    private static byte getNumVerticalPieces(byte[][] state, byte col){
        byte numVerticalPieces = 0;

        for (byte y = 0; y < 8; y++)
            if (state[y][col] != E)
                numVerticalPieces++;

        return numVerticalPieces;
    }
    private static byte getNumMainDiagPieces(byte[][] state, byte row, byte col){
        byte numMainDiagPieces = 0;

        byte minVertOrHorizDist = (byte) Math.min ( row, col );
        for (byte y = (byte) (row - minVertOrHorizDist), x = (byte) (col - minVertOrHorizDist); y < 8 && x < 8; y++, x++)
            if (state[y][x] != E)
                numMainDiagPieces++;

        return numMainDiagPieces;
    }
    private static byte getNumAntiDiagPieces(byte[][] state, byte row, byte col){
        byte numAntiDiagPieces = 0;

        byte minVertOrHorizDist = (byte) Math.min ( 7 - row, col );
        for (byte y = (byte) (row + minVertOrHorizDist), x = (byte) (col - minVertOrHorizDist); y >= 0 && x < 8; y--, x++)
            if (state[y][x] != E)
                numAntiDiagPieces++;

        return numAntiDiagPieces;
    }

}
