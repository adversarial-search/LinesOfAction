package objects;

import java.awt.image.BufferedImage;

public class Tile {
    private final BufferedImage sprite;


    public Tile(BufferedImage sprite) {
        this.sprite = sprite;
    }


    public BufferedImage getSprite() {
        return sprite;
    }
}
