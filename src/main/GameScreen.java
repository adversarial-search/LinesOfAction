package main;

import inputs.MyMouseListener;

import javax.swing.JPanel;
import java.awt.*;


public class GameScreen extends JPanel {
    private Game game;
    private Dimension size;
    private MyMouseListener myMouseListener;

    public GameScreen ( Game game ) {
        this.game = game;

        setPanelSize();

    }
    public void initInputs(){
        myMouseListener = new MyMouseListener (game);

        addMouseListener ( myMouseListener );
        addMouseMotionListener ( myMouseListener );

        requestFocus ();
    }
    public void paintComponent( Graphics g ){
        super.paintComponent ( g );

        game.getRender ().render ( g );
    }
    private void setPanelSize () {
        size = new Dimension ( 640, 704 );

        setMinimumSize ( size );
        setPreferredSize ( size );
        setMaximumSize ( size );
    }
}
