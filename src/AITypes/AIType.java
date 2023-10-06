package AITypes;

public interface AIType {
    public void makeMove(byte id,byte depth,byte [][]initialState);
    public void makeMoveWithTranspositionTable(byte id,byte depth,byte [][]initialState);

}
