package assistants;

import static assistants.LevelBuild.B;
import static assistants.LevelBuild.W;
import static scenes.GameScene.allPiecesConnected;
import static scenes.GameScene.getFirstPiece;
import static scenes.AIAgainstAI.turn;
public class EvaluateStateFunctions {


    public static short evaluateState(byte[][] state, boolean isBlackTurn) {

        byte color = isBlackTurn ? B : W;
        //quirky winning case - don't ask
        {
            short specialWin = specialWinningCondition(state, isBlackTurn);
            if (specialWin != 0) return specialWin;
        }

        //BLACK PLAYS WITH ALL EVALUATION FUNCTIONS
        if(turn == B)
            return (short) (
                              Heuristics.getScoreFromBoardPositions(state, color)   //h1
                            + Heuristics.getNumConnectedPieces(state, color)        //h5
                            - Heuristics.getDensityScore(state, color)              //h3
                            - Heuristics.getArea(state, color)                      //h2
                            - Heuristics.getScoreFromNumEnemyPieces(state, color)   //h4
                            - Heuristics.numberOfOpponentsMoves(state, color)       //h6
            );

        //WHITE PLAYS WITH A SUBSET OF THE EVALUATION FUNCTIONS
        return (short) (
                          Heuristics.getScoreFromBoardPositions ( state, color )    //h1
                        - Heuristics.getArea(state, color)                          //h2
                        - Heuristics.getDensityScore(state, color)                  //h3
                        - Heuristics.getScoreFromNumEnemyPieces(state, color)       //h4
                        + Heuristics.getNumConnectedPieces(state, color)            //h5
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
