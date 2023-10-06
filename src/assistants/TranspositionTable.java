package assistants;

import objects.Move;

import java.util.HashMap;
import java.util.Map;

import static assistants.ZobristHashing.getZobristKeyForPosition;

public class TranspositionTable {

    public static final Map<Long, Move> transpositionTable = new HashMap<>();


    public static void addMoveToTable(byte [][]state,byte colorToMove, byte depth,Move bestMove){
        transpositionTable.put(getZobristKeyForPosition(state,colorToMove,depth),bestMove);
    }

    public static Move getMoveFromTable(byte [][]state,byte colorToMove, byte depth){

        long zobristKey = ZobristHashing.getZobristKeyForPosition(state,colorToMove,depth);

        return transpositionTable.get(zobristKey);
    }



}
