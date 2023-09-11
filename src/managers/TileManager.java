package managers;

import objects.Tile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class TileManager {
    public BufferedImage atlas;
    public ArrayList<Tile> tiles = new ArrayList<> (  );

    public TileManager () {

        loadAtlas();
        createTiles();

    }


    private void createTiles () {
        for(int y=0; y<3; y++)
            for(int x=0; x<8; x++)
                tiles.add ( new Tile(getSprite ( x, y )) );
    }
    private void loadAtlas () {
        InputStream is =  getClass ().getResourceAsStream ( "/spriteAtlas.png" );

        try {
            atlas = ImageIO.read ( is );
        } catch (IOException e) {
            throw new RuntimeException ( e );
        }
    }
    private BufferedImage getSprite(int xCord, int yCord){
        return atlas.getSubimage ( 64*xCord, 64*yCord, 64, 64 );
    }
    public BufferedImage getSprite(int id){
        return tiles.get ( id ).getSprite ();
    }
}
