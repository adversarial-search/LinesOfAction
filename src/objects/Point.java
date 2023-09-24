package objects;

public class Point {
    byte row;
    byte col;

    public Point(byte row, byte col) {
        this.row = row;
        this.col = col;
    }

    public byte getRow() {
        return row;
    }

    public byte getCol() {
        return col;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        Point p = (Point) obj;
        return this.row == p.row && this.col == p.col;
    }
}
