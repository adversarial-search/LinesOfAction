package assistants;


import managers.TileManager;

import java.awt.*;

public class LevelBuild {
    public static final byte[][] positionsScore = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 10, 10, 10, 10, 10, 10, 0},
            {0, 10, 25, 25, 25, 25, 10, 0},
            {0, 10, 25, 25, 25, 25, 10, 0},
            {0, 10, 25, 50, 50, 25, 10, 0},
            {0, 10, 25, 25, 25, 25, 10, 0},
            {0, 10, 10, 10, 10, 10, 10, 0},
            {0, 0, 0, 0, 0, 0, 0, 0}
    };
    private static final byte W = 0;
    private static final byte B = 1;
    private static final byte E = 2;
    private static final TileManager tileManager = new TileManager();

    public static void drawBackground(Graphics g) {
        g.setColor(new Color(138, 219, 181));
        g.fillRect(0, 0, 640, 704);

        g.setColor(new Color(87, 196, 97));
        for (byte y = 0; y < 8; y++)
            for (byte x = 0; x < 8; x++)
                if ((y + x) % 2 == 0)
                    g.fillRect(64 * (y + 1), 64 * (x + 2), 64, 64);

        g.setColor(new Color(45, 161, 84));
        for (byte y = 0; y < 8; y++)
            for (byte x = 0; x < 8; x++)
                if ((y + x) % 2 != 0)
                    g.fillRect(64 * (x + 1), 64 * (y + 2), 64, 64);

        g.drawRect(64, 128, 512, 512);
        g.drawRect(65, 129, 510, 510);
        g.drawRect(66, 130, 508, 508);

        byte[][] numbersAndLetters = LevelBuild.numbersAndLetters();
        for (byte i = 0; i < numbersAndLetters.length; i++)
            for (byte j = 0; j < numbersAndLetters[0].length; j++)
                if (numbersAndLetters[i][j] >= 0)
                    g.drawImage(tileManager.getSprite(numbersAndLetters[i][j]), 64 * j, 64 * i, null);
    }

    public static byte[][] getInitialPiecesPositions() {
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

    public static byte[][] numbersAndLetters() {
        return new byte[][]{
                {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
                {-1, 16, 17, 18, 19, 20, 21, 22, 23, -1},
                {15, -1, -1, -1, -1, -1, -1, -1, -1, 15},
                {14, -1, -1, -1, -1, -1, -1, -1, -1, 14},
                {13, -1, -1, -1, -1, -1, -1, -1, -1, 13},
                {12, -1, -1, -1, -1, -1, -1, -1, -1, 12},
                {11, -1, -1, -1, -1, -1, -1, -1, -1, 11},
                {10, -1, -1, -1, -1, -1, -1, -1, -1, 10},
                {9, -1, -1, -1, -1, -1, -1, -1, -1, 9},
                {8, -1, -1, -1, -1, -1, -1, -1, -1, 8},
                {-1, 16, 17, 18, 19, 20, 21, 22, 23, -1},
        };
    }
}
