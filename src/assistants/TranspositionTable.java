package assistants;

import objects.Move;

import java.util.HashMap;
import java.util.Map;

import static assistants.ZobristHashing.getZobristKeyForPosition;

public class TranspositionTable {

    public static final Map<Integer, Move> transpositionTable = new HashMap<Integer,Move>();

    public static void addMoveToTable(byte [][]state,byte colorToMove, byte depth,Move bestMove){
        transpositionTable.put(getZobristKeyForPosition(state,colorToMove,depth),bestMove);
    }

    public static Move getMoveFromTable(byte [][]state,byte colorToMove, byte depth){
        int zobristKey = getZobristKeyForPosition(state,colorToMove,depth);

        if(transpositionTable.containsKey(zobristKey)){
            return transpositionTable.get(zobristKey);
        };

        return null;

    }


}
