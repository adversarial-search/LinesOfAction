package assistants;


public class LevelBuild {
    private static final byte W = 0;
    private static final byte B = 1;
    private static final byte E = 2;
    public static byte[][] getInitialPiecesPositions(){
        return new byte[][]{
                {E, B, B, B, B, B, B, E},
                {W, E, E, E, E, E, E, W},
                {W, E, E, E, E, E, E, W},
                {W, E, E, E, E, E, E, W},
                {W, E, E, E, E, E, E, W},
                {W, E, E, E, E, E, E, W},
                {W, E, E, E, E, E, E, W},
                {E, B, B, B, B, B, B, E}
        };
    }
    public static byte[][] numbersAndLetters (){
        return new byte[][]{
                {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
                {-1, 16, 17, 18, 19, 20, 21, 22, 23, -1},
                {15, -1, -1, -1, -1, -1, -1, -1, -1, 15},
                {14, -1, -1, -1, -1, -1, -1, -1, -1, 14},
                {13, -1, -1, -1, -1, -1, -1, -1, -1, 13},
                {12, -1, -1, -1, -1, -1, -1, -1, -1, 12},
                {11, -1, -1, -1, -1, -1, -1, -1, -1, 11},
                {10, -1, -1, -1, -1, -1, -1, -1, -1, 10},
                { 9, -1, -1, -1, -1, -1, -1, -1, -1,  9},
                { 8, -1, -1, -1, -1, -1, -1, -1, -1,  8},
                {-1, 16, 17, 18, 19, 20, 21, 22, 23, -1},
        };
    }
}
