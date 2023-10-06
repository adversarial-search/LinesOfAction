package objects;

public class MoveAndScoreObject {

    private Move move;
    private short score;

    public MoveAndScoreObject(Move move, short score) {
        this.move = move;
        this.score = score;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public short getScore() {
        return score;
    }

    public void setScore(short score) {
        this.score = score;
    }
}
