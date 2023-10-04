package assistants;

import static assistants.LevelBuild.B;
import static assistants.LevelBuild.W;
import static scenes.GameScene.allPiecesConnected;
import static scenes.GameScene.getFirstPiece;

public class EvaluateStateFunctions {


    public static short evaluateState(byte[][] state, boolean isBlackTurn) {

        byte color = isBlackTurn ? B : W;
        //quirky winning case - don't ask
        {
            short specialWin = specialWinningCondition(state, isBlackTurn);
            if (specialWin != 0) return specialWin;
        }

        return (short) (
                Heuristics.getScoreFromBoardPositions(state, color)
                        + Heuristics.getNumConnectedPieces(state, color)
                        - Heuristics.getDensityScore(state, color)
                        - Heuristics.getArea(state, color)
                        - Heuristics.getScoreFromNumEnemyPieces(state, color)
                        - Heuristics.numberOfOpponentsMoves(state, color)
        );
    }

    public static short specialWinningCondition(byte[][] state, boolean isBlackTurn) {
        boolean blackWins = isWinningState(state, B);
        boolean whiteWins = isWinningState(state, W);


        if (blackWins && whiteWins) return isBlackTurn ? Short.MAX_VALUE : Short.MIN_VALUE;


        if (blackWins) return Short.MAX_VALUE;
        if (whiteWins) return Short.MIN_VALUE;


        return 0;
    }

    public static boolean isWinningState(byte[][] state, byte color) {
        return allPiecesConnected(state, color, getFirstPiece(state, color));
    }

}
