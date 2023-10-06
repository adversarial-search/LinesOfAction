package AITypes;

import assistants.MinMaxFunctions;
import assistants.TranspositionTable;
import objects.Move;
import scenes.GameScene;

import static assistants.StateGenerationFunctions.getStateFromMove;

public class ClassicMinMaxAi implements AIType {
    @Override
    public void makeMove(byte id, byte depth, byte[][] initialState) {
        MinMaxFunctions.makeBasicMiniMaxMove( id, depth, initialState,false);
    }

    @Override
    public void makeMoveWithTranspositionTable(byte id, byte depth, byte[][] initialState) {
            MinMaxFunctions.makeBasicMiniMaxMove(id,depth,initialState,true);
        }
}

