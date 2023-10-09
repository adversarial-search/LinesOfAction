package assistants;

import objects.Move;

import java.util.*;

import static assistants.LevelBuild.W;
import static assistants.ZobristHashing.getZobristKeyForPosition;

public class TranspositionTable {

    public static final Map<Long, Move> transpositionTableForMoves = new HashMap<>();
    public static final Map<Long,Short> evaluationTranspositionTable = new HashMap<>();


    public static void addMoveToTable(byte [][]state,byte colorToMove, byte depth,Move bestMove){
        transpositionTableForMoves.put(getZobristKeyForPosition(state,colorToMove,depth),bestMove);
    }

    public static void addEvaluationToTable(byte [][]state,byte colorToMove,byte depth,short score){
        evaluationTranspositionTable.put(getZobristKeyForPosition(state,colorToMove,depth),score);
    }

    public static Short getEvaluationFromTable(byte [][]state,byte colorToMove,byte depth){
       return evaluationTranspositionTable.get(getZobristKeyForPosition(state,colorToMove,depth));
    }

    public static Move getMoveFromTable(byte [][]state,byte colorToMove, byte depth){

        long zobristKey = ZobristHashing.getZobristKeyForPosition(state,colorToMove,depth);

        return transpositionTableForMoves.get(zobristKey);
    }

    public static void fillTranspositionTable(){
        //This function will fill  the transposition table by evaluating a preset depth from the starting position

        System.out.println("filling started");
        byte[][] initialState = LevelBuild.getInitialPiecesPositions();

        List<byte[][]> immediateStates = StateGenerationFunctions.getAllImmediateStatesByteMatrix(initialState,true);
        for(byte currentDepth = 0; currentDepth<=2;currentDepth++){
                    //here we do the minmax magic ,but we don't update the game state
                for(byte[][] currentState:immediateStates)
                    MinMaxFunctions.makeAlphaBetaMiniMaxMove( W,currentDepth,currentState,true,false);
        }
        System.out.println("filling finished");

    }
    }




