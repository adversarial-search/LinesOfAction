package scenes;

import assistants.LevelBuild;
import main.Game;
import managers.TileManager;
import objects.Point;
import ui.MyButton;

import java.awt.*;

import static main.GameStates.MENU;
import static main.GameStates.SetGameState;

public class PlayingAgainstAI extends GameScene implements SceneMethods{
    private final TileManager tileManager =  new TileManager ();
    private static final byte W = 0;
    private static final byte B = 1;
    private static final byte E = 2;
    Game game;
    private MyButton bChooseWhite, bChooseBlack, bReset, bMenu;
    private static boolean idIsChosen = false;
    private static byte playerID;
    private static byte aiID;
    private static byte turn = B;
    private Point activePiece = null;



    public PlayingAgainstAI ( Game game) {
        super( game );
        this.game = game;
        initButtons();
    }



    private void initButtons () {
        bChooseBlack = new MyButton ( "Play As Black", 192, 128, 256, 128 );
        bChooseWhite = new MyButton ( "Play As White", 192, 416, 256, 128 );
        bMenu = new MyButton ( "Menu", 14, 12, 100, 40 );
        bReset = new MyButton ( "Reset", 128, 12, 100, 40 );
    }



    @Override
    public void render ( Graphics g ) {
        //draw buttons
        if(idIsChosen) {
            drawBackground ( g );
            drawButtons ( g );
        }
        else drawChoiceButtons( g );
    }
    private void drawBackground ( Graphics g ) {
        LevelBuild.drawBackground ( g );
    }
    private void drawButtons ( Graphics g ) {
        bMenu.draw ( g );
        bReset.draw ( g );
    }
    private void drawChoiceButtons ( Graphics g ) {
        bChooseWhite.draw ( g );
        bChooseBlack.draw ( g );
    }



    private void chooseIDs(int x, int y){
        if (bChooseWhite.getBounds ( ).contains ( x, y )) {
            playerID = W;
            aiID = B;
            idIsChosen = true;
        } else if (bChooseBlack.getBounds ( ).contains ( x, y )) {
            playerID = B;
            aiID = W;
            idIsChosen = true;
        }
    }

    @Override
    public void mouseClicked ( int x, int y ) {
        //choose IDs
        {
            if (!idIsChosen) {
                chooseIDs ( x, y );
                return;
            }
        }

        // check if clicked on Menu or Reset
        {
            if(bMenu.getBounds ().contains ( x, y )) {
                resetGame ();
                SetGameState ( MENU );
                return ;
            }
            else if(bReset.getBounds ().contains ( x, y )) {
                resetGame ( );
                return ;
            }
        }

        //game logic
        if(playerID == turn){

        }else if(aiID == turn){

        }
    }

    @Override
    protected void resetGame () {
        idIsChosen = false;
        playerID = -1;
        aiID = -1;
    }

    @Override
    protected void changeTurn () {

    }

    @Override
    protected void makeMove ( int x, int y ) {

    }


    @Override
    public void mouseMoved ( int x, int y ) {
        bChooseBlack.setMouseOver ( false );
        bChooseWhite.setMouseOver ( false );
        bMenu.setMouseOver ( false );
        bReset.setMouseOver ( false );

        if(bChooseBlack.getBounds ().contains ( x, y ))
            bChooseBlack.setMouseOver ( true );
        else if(bChooseWhite.getBounds ().contains ( x, y ))
            bChooseWhite.setMouseOver ( true );
        else if(bMenu.getBounds ().contains ( x, y ))
            bMenu.setMouseOver ( true );
        else if(bReset.getBounds ().contains ( x, y ))
            bReset.setMouseOver ( true );
    }
    @Override
    public void mousePressed ( int x, int y ) {
        if(bChooseBlack.getBounds ().contains ( x, y ))
            bChooseBlack.setMousePressed ( true );
        else if(bChooseWhite.getBounds ().contains ( x, y ))
            bChooseWhite.setMousePressed ( true );
        else if(bMenu.getBounds ().contains ( x, y ))
            bMenu.setMousePressed ( true );
        else if(bReset.getBounds ().contains ( x, y ))
            bReset.setMousePressed ( true );
    }
    @Override
    public void mouseReleased ( int x, int y ) {
        resetButtons();
    }
    private void resetButtons () {
        bChooseBlack.resetBooleans ();
        bChooseWhite.resetBooleans ();
        bMenu.resetBooleans ();
        bReset.resetBooleans ();
    }
}
