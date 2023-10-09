package AITypes;

import assistants.MinMaxFunctions;

public class AlphaBetaMinMaxAi implements AIType{

    @Override
    public void makeMove(byte id, byte depth, byte[][] initialState) {
        MinMaxFunctions.makeAlphaBetaMiniMaxMove( id, depth, initialState,false,true);
    }

    @Override
    public void makeMoveWithTranspositionTable(byte id, byte depth, byte[][] initialState) {
        MinMaxFunctions.makeAlphaBetaMiniMaxMove( id, depth, initialState,true,true);
    }
}
