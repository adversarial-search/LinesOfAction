package objects;

public class MoveAndResultingStateObject {
    Move move;
    byte[][] resultingState;

    public MoveAndResultingStateObject(Move move, byte[][] resultingState) {
        this.move = move;
        this.resultingState = resultingState;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public byte[][] getResultingState() {
        return resultingState;
    }

    public void setResultingState(byte[][] resultingState) {
        this.resultingState = resultingState;
    }
}
