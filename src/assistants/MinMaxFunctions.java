package assistants;

import objects.*;
import scenes.GameScene;
import scenes.PlayingAgainstAI;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import static assistants.EvaluateStateFunctions.evaluateState;
import static assistants.LevelBuild.W;
import static assistants.StateGenerationFunctions.getAllImmediateStates;
import static assistants.StateGenerationFunctions.getStateFromMove;
import static scenes.GameScene.getAllPiecesOfColor;
import static scenes.GameScene.piecesPositions;

public class MinMaxFunctions {
    private static final Random random = new Random ( );
    private static final byte B = 1;

    public static void makeAlphaBetaMiniMaxMove(byte id,byte depth,byte [][]initialState,boolean usesTranspositionTable){
        if(usesTranspositionTable){
            Move moveFromTable = TranspositionTable.getMoveFromTable(initialState,id,depth);
            if(moveFromTable!=null){
                GameScene.piecesPositions = getStateFromMove(initialState,moveFromTable.getStarPositionOfPiece(),moveFromTable.getTargetPositionOfPiece());
                return;
            }
        }
        ArrayList<MoveAndResultingStateObject> bestMoves = new ArrayList<> (  );

        if (id == W) {
            short bestScore = Short.MAX_VALUE;
            List<MoveAndResultingStateObject> immediateStates = getAllImmediateStates(initialState, false);

            for (MoveAndResultingStateObject moveAndResultingState : immediateStates) {
                short moveScore = miniMax(moveAndResultingState.getResultingState(), depth, Short.MIN_VALUE, Short.MAX_VALUE, true, true);
                if (moveScore < bestScore) {
                    bestScore = moveScore;
                    bestMoves = new ArrayList<> (  );
                    bestMoves.add (moveAndResultingState);
                }else if(moveScore == bestScore)
                    bestMoves.add (moveAndResultingState);
            }
        } else {
            short bestScore = Short.MIN_VALUE;
            List<MoveAndResultingStateObject> immediateStates = getAllImmediateStates(initialState, true);
            for (MoveAndResultingStateObject moveAndResultingState : immediateStates) {
                short moveScore = miniMax(moveAndResultingState.getResultingState(), depth, Short.MIN_VALUE, Short.MAX_VALUE, false, false);
                if (moveScore > bestScore) {
                    bestScore = moveScore;
                    bestMoves = new ArrayList<> (  );
                    bestMoves.add (moveAndResultingState);
                }else if(moveScore == bestScore)
                    bestMoves.add (moveAndResultingState);
            }
        }

        System.out.println ( bestMoves.size () );

        MoveAndResultingStateObject chosenState = bestMoves.get(random.nextInt ( bestMoves.size () ));

        if(usesTranspositionTable)
        TranspositionTable.addMoveToTable(initialState,id,depth,chosenState.getMove());

        GameScene.piecesPositions =chosenState.getResultingState();
    }
    private static short miniMax(byte[][] state, byte depth, short alpha, short beta, boolean isMax, boolean isBlackTurn){
        byte playerPiece = isBlackTurn ? B : W;
        byte opponentPiece = isBlackTurn ? W : B;

        short stateScore = evaluateState ( state, !isBlackTurn );
        GameScene.incrementStatesEvaluated();

        if(depth == 0 || (short) (Math.abs(stateScore)) == Short.MAX_VALUE) return stateScore;

        List<Point> points = getAllPiecesOfColor ( state, playerPiece );
        List<Function<StateGenerationDTO,Point>> movementFunctions = ValidMovesFunctions.movementFunctions;
        byte[][] nextState;
        if(isMax) {
            short highestScore = Short.MIN_VALUE;

            for (Point p : points) {
                Point positionToMoveTo;
                StateGenerationDTO currentStateDTO = new StateGenerationDTO(state, p.getRow(), p.getCol(), opponentPiece, playerPiece);
                for (Function<StateGenerationDTO, Point> function : movementFunctions) {
                    positionToMoveTo = function.apply(currentStateDTO);
                    if (positionToMoveTo != null) {
                        nextState = getStateFromMove(state, p, positionToMoveTo);

                        highestScore = (short) Math.max(highestScore, miniMax(nextState, (byte) (depth - 1), alpha, beta, false, !isBlackTurn));

                        alpha = (short) Math.max(alpha, highestScore);
                        if (alpha >= beta) return highestScore;

                    }
                }
            }
            return highestScore;
        }
        else{
            short lowestScore = Short.MAX_VALUE;

            for(Point p: points) {
                Point positionToMoveTo;
                StateGenerationDTO currentStateDTO = new StateGenerationDTO(state, p.getRow(), p.getCol(), opponentPiece, playerPiece);
                for (Function<StateGenerationDTO, Point> function : movementFunctions) {
                    positionToMoveTo = function.apply(currentStateDTO);
                    if (positionToMoveTo != null) {
                        nextState = getStateFromMove(state, p, positionToMoveTo);

                        lowestScore = (short) Math.min(lowestScore, miniMax(nextState, (byte) (depth - 1), alpha, beta, true, !isBlackTurn));

                        beta = (short) Math.min(beta, lowestScore);
                        if (alpha >= beta) return lowestScore;
                    }
                }
            }

            return lowestScore;
        }

    }


    public static void makeBasicMiniMaxMove(byte id,byte depth,byte [][]initialState,boolean usesTranspositionTable){
        if(usesTranspositionTable){
            Move moveFromTable = TranspositionTable.getMoveFromTable(initialState,id,depth);
            if(moveFromTable!=null){
                GameScene.piecesPositions = getStateFromMove(initialState,moveFromTable.getStarPositionOfPiece(),moveFromTable.getTargetPositionOfPiece());
                return;
            }
        }

        ArrayList<MoveAndResultingStateObject> bestMoves = new ArrayList<> (  );

        if (id == W) {
            short bestScore = Short.MAX_VALUE;
            List<MoveAndResultingStateObject> immediateStates = getAllImmediateStates(initialState, false);

            for (MoveAndResultingStateObject moveAndResultingState : immediateStates) {
                short moveScore = miniMax(moveAndResultingState.getResultingState(), depth, true, true);
                if (moveScore < bestScore) {
                    bestScore = moveScore;

                    bestMoves = new ArrayList<> (  );
                    bestMoves.add ( moveAndResultingState );
                }else if(moveScore == bestScore){
                    bestMoves.add ( moveAndResultingState );
                }
            }
        } else {
            short bestScore = Short.MIN_VALUE;
            List<MoveAndResultingStateObject> immediateStates = getAllImmediateStates(initialState, true);
            for (MoveAndResultingStateObject moveAndResultingState: immediateStates) {
                short moveScore = miniMax(moveAndResultingState.getResultingState(), depth, false, false);
                if (moveScore > bestScore) {
                    bestScore = moveScore;

                    bestMoves = new ArrayList<> (  );
                    bestMoves.add ( moveAndResultingState );
                }else if(moveScore == bestScore){
                    bestMoves.add ( moveAndResultingState );
                }
            }
        }
        System.out.println ( bestMoves.size () );
        MoveAndResultingStateObject chosenState = bestMoves.get(random.nextInt ( bestMoves.size () ));

        if(usesTranspositionTable)
            TranspositionTable.addMoveToTable(initialState,id,depth,chosenState.getMove());

        GameScene.piecesPositions =chosenState.getResultingState();
    }
    private static short miniMax(byte[][] state, byte depth, boolean isMax, boolean isBlackTurn) {
        short stateScore = evaluateState(state, !isBlackTurn);
        GameScene.incrementStatesEvaluated();
        if (depth == 0 || (short) (Math.abs(stateScore)) == Short.MAX_VALUE) return stateScore;

        List<MoveAndResultingStateObject> immediateStates = getAllImmediateStates(state, isBlackTurn);
        if (immediateStates.isEmpty()) return stateScore;


        if (isMax) { // this is a maximising node
            short highestScore = Short.MIN_VALUE;
            for (MoveAndResultingStateObject immediateState : immediateStates)
                highestScore = (short) Math.max(highestScore, miniMax(immediateState.getResultingState(), (byte) (depth - 1), false, !isBlackTurn));
            return highestScore;
        } else {
            short lowestScore = Short.MAX_VALUE;
            for (MoveAndResultingStateObject immediateState: immediateStates)
                lowestScore = (short) Math.min(lowestScore, miniMax(immediateState.getResultingState(), (byte) (depth - 1), true, !isBlackTurn));
            return lowestScore;
        }
    }

    }
