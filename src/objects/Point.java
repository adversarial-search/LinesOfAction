package objects;

public class Point {
    int row;
    int col;

    public Point ( int row, int col ) {
        this.row = row;
        this.col = col;
    }

    public int getRow () {
        return row;
    }
    public int getCol () {
        return col;
    }

    @Override
    public boolean equals ( Object obj ) {
        if(obj == null) return false;
        Point p = (Point) obj;
        return this.row == p.row && this.col == p.col;
    }
}
