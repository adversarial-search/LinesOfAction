package objects;

public class Move {
    Point starPositionOfPiece;
    Point targetPositionOfPiece;

    public Move(Point starPositionOfPiece, Point targetPositionOfPiece) {
        this.starPositionOfPiece = starPositionOfPiece;
        this.targetPositionOfPiece = targetPositionOfPiece;
    }

    public Point getStarPositionOfPiece() {
        return starPositionOfPiece;
    }

    public Point getTargetPositionOfPiece() {
        return targetPositionOfPiece;
    }
}
