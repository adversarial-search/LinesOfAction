package assistants;
import objects.Point;

import java.util.ArrayList;
import java.util.List;

import static assistants.LevelBuild.B;
import static assistants.LevelBuild.W;
import scenes.GameScene;

public class Heuristics {
    public static short getScoreFromBoardPositions(byte[][] state, byte color){
        short boardPositionsScore = 0;
        for(byte y = 0; y < 8; y++)
            for(byte x = 0; x < 8; x++)
                if(state[y][x] == color)
                    boardPositionsScore += LevelBuild.positionsScore[y][x];
        return color == B ? boardPositionsScore : (short) (-1*boardPositionsScore);
    }
    public static short getArea(byte[][] state, byte color) {
        byte height = (byte) (findBottomMostY(state, color) - findTopMostY(state, color));
        byte width = (byte) (findRightMostX(state, color) - findLeftMostX(state, color));

        return color == B ? (short) (height * width) : (short) (-1*height * width);
    }
    public static byte getScoreFromNumEnemyPieces(byte[][] state, byte playerColor){
        byte enemyPiecesCount = 0;
        byte enemyColor = playerColor == B ? W : B;

        for(byte y = 0; y < 8; y++)
            for(byte x = 0; x < 8; x++)
                if(state[y][x] == enemyColor)
                    enemyPiecesCount += 1;

        return playerColor == B ? LevelBuild.numPiecesScore[enemyPiecesCount-1] : (byte)(-1*LevelBuild.numPiecesScore[enemyPiecesCount-1]);
    }
    public static short numberOfOpponentsMoves(byte[][] state, byte playerColor){
        byte opponentColor = playerColor == B ? W : B;

        short numberOfPossibleNextPositionsForOpponent = 0;

        List<Point> enemyPieces = GameScene.getAllPiecesOfColor(state, opponentColor);

        for(Point currentPiece:enemyPieces){
            numberOfPossibleNextPositionsForOpponent += ValidMovesFunctions.getValidMoves(
                    state,
                    currentPiece.getRow(),
                    currentPiece.getCol(),
                    opponentColor,
                    playerColor
            ).size();
        }

        return playerColor == B ? numberOfPossibleNextPositionsForOpponent : (short) (-1 * numberOfPossibleNextPositionsForOpponent);
    }
    public static byte getNumConnectedPieces(byte[][] state, byte color){
        byte numConnectedPieces = 0;
        for(byte y = 0; y < 8; y++)
            for(byte x = 0; x < 8; x++)
                if(isConnected(state, y, x, color))
                    numConnectedPieces += 1;
        return color == B ? numConnectedPieces : (byte)(-1*numConnectedPieces);
    }



    private static byte findLeftMostX(byte[][] state, byte color) {

        for (byte col = 0; col < 8; col++) {
            for (byte row = 7; row >= 0; row--) {
                if (state[row][col] == color) {
                    return col;
                }
            }
        }
        return -1;
    }
    private static byte findRightMostX(byte[][] state, byte color) {


        for (byte col = 7; col >= 0; col--) {
            for (byte row = 7; row >= 0; row--) {
                if (state[row][col] == color) {
                    return col;
                }
            }
        }
        return -1;
    }
    private static byte findBottomMostY(byte[][] state, byte color) {

        for (byte row = 7; row >= 0; row--) {
            for (byte col = 7; col >= 0; col--) {
                if (state[row][col] == color) {
                    return row;
                }
            }
        }
        return -1;
    }
    private static byte findTopMostY(byte[][] state, byte color) {

        for (byte row = 0; row < 8; row++) {
            for (byte col = 0; col < 8; col++) {
                if (state[row][col] == color) {
                    return row;
                }
            }
        }
        return -1;
    }
    private static boolean isConnected(byte state[][], byte row, byte col, byte color){
        for(byte y = (byte)Math.max(row - 1, 0); y < Math.min(row + 2, 8); y++)
            for(byte x = (byte)Math.max(col - 1, 0); x < Math.min(col + 2, 8); x++)
                if(state[y][x] == color && y != row && x != col)
                    return true;
        return false;
    }
}
