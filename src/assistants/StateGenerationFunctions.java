package assistants;

import objects.Point;

import java.util.ArrayList;
import java.util.List;

import static assistants.LevelBuild.B;
import static assistants.LevelBuild.W;

public class StateGenerationFunctions {

    private static final byte E = 2;

    public static List<byte[][]> getAllImmediateStates(byte[][] state, boolean isBlackMove) {
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

    public static ArrayList<byte[][]> getAllImmediateStatesFor(byte[][] state, byte row, byte col, byte opponentPiece, byte playerPiece) {
        ArrayList<Point> validPositions = ValidMovesFunctions.getValidMoves(state, row, col, opponentPiece, playerPiece);
        ArrayList<byte[][]> immediateStates = new ArrayList<>();

        for (Point p : validPositions) {
            byte[][] nextState = new byte[8][];
            for (byte i = 0; i < 8; i++)
                nextState[i] = state[i].clone();

            nextState[row][col] = 2; // This is equal to E (empty piece)
            nextState[p.getRow()][p.getCol()] = playerPiece;
            immediateStates.add(nextState);
        }
        return immediateStates;
    }

    public static byte[][] getStateFromMove(byte[][] state, Point pieceToMove, Point positionToMoveTo){
        byte[][] stateClone = new byte[8][];
        for(byte x = 0; x < 8; x++) stateClone[x] = state[x].clone ();

        stateClone[positionToMoveTo.getRow ()][positionToMoveTo.getCol ()] = stateClone[pieceToMove.getRow ()][pieceToMove.getCol ()];
        stateClone[pieceToMove.getRow ()][pieceToMove.getCol ()] = E;
        return stateClone;
    }
}
